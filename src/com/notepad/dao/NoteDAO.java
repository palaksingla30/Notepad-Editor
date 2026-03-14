package com.notepad.dao;

import com.notepad.db.DatabaseConnection;
import com.notepad.model.Note;
import com.notepad.model.RecentDocument;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDAO {

    // Save a brand new note to the DB, returns the generated Note ID
    public int saveNewNote(int userId, String title, String content) {
        String sql = "INSERT INTO Notes (user_id, title, content) VALUES (?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userId);
            stmt.setString(2, title);
            stmt.setString(3, content);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int noteId = rs.getInt(1);
                        updateRecentDocument(userId, noteId);
                        return noteId;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Update an existing note in the DB
    public boolean updateNote(int noteId, String title, String content) {
        String sql = "UPDATE Notes SET title = ?, content = ? WHERE note_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, content);
            stmt.setInt(3, noteId);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                // To keep track of recent documents reliably, you need the User ID too but 
                // typically recent document last_opened is updated when OPENING, not just saving.
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Fetch a note by its ID
    public Note getNote(int noteId, int userId) {
        String sql = "SELECT * FROM Notes WHERE note_id = ? AND user_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, noteId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Note note = new Note(
                        rs.getInt("note_id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                );
                updateRecentDocument(userId, noteId);
                return note;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Insert or update the Recent Documents table when a document is opened/saved
    public void updateRecentDocument(int userId, int noteId) {
        // Check if entry exists for this user and note
        String checkSql = "SELECT id FROM Recent_Documents WHERE user_id = ? AND note_id = ?";
        String updateSql = "UPDATE Recent_Documents SET last_opened = CURRENT_TIMESTAMP WHERE id = ?";
        String insertSql = "INSERT INTO Recent_Documents (user_id, note_id) VALUES (?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        try {
            int recentId = -1;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, noteId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        recentId = rs.getInt("id");
                    }
                }
            }
            
            if (recentId != -1) {
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, recentId);
                    updateStmt.executeUpdate();
                }
            } else {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, noteId);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get up to 10 recent documents for a user
    public List<RecentDocument> getRecentDocuments(int userId) {
        List<RecentDocument> docs = new ArrayList<>();
        // Join with Notes table to get the title for the UI
        String sql = "SELECT rd.id, rd.user_id, rd.note_id, rd.last_opened, n.title " +
                     "FROM Recent_Documents rd " +
                     "JOIN Notes n ON rd.note_id = n.note_id " +
                     "WHERE rd.user_id = ? " +
                     "ORDER BY rd.last_opened DESC LIMIT 10";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                docs.add(new RecentDocument(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("note_id"),
                        rs.getTimestamp("last_opened"),
                        rs.getString("title")
                ));
            }
        } catch (SQLException e) {
             e.printStackTrace();
        }
        return docs;
    }
}
