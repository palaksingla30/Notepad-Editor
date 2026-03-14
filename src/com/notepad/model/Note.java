package com.notepad.model;

import java.sql.Timestamp;

public class Note {
    private int noteId;
    private int userId;
    private String title;
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Note(int noteId, int userId, String title, String content, Timestamp createdAt, Timestamp updatedAt) {
        this.noteId = noteId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Note() {} // Empty constructor for new notes

    public int getNoteId() { return noteId; }
    public void setNoteId(int noteId) { this.noteId = noteId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return title + " (Last updated: " + (updatedAt != null ? updatedAt : "never") + ")";
    }
}
