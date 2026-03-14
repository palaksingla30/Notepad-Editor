package com.notepad.model;

import java.sql.Timestamp;

public class RecentDocument {
    private int id;
    private int userId;
    private int noteId;
    private Timestamp lastOpened;

    // Optional Note reference to show the title in UI directly
    private String noteTitle;

    public RecentDocument(int id, int userId, int noteId, Timestamp lastOpened, String noteTitle) {
        this.id = id;
        this.userId = userId;
        this.noteId = noteId;
        this.lastOpened = lastOpened;
        this.noteTitle = noteTitle;
    }

    public RecentDocument() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getNoteId() { return noteId; }
    public void setNoteId(int noteId) { this.noteId = noteId; }

    public Timestamp getLastOpened() { return lastOpened; }
    public void setLastOpened(Timestamp lastOpened) { this.lastOpened = lastOpened; }

    public String getNoteTitle() { return noteTitle; }
    public void setNoteTitle(String noteTitle) { this.noteTitle = noteTitle; }

    @Override
    public String toString() {
        return (noteTitle != null ? noteTitle : "Note ID " + noteId) + " - Last opened: " + lastOpened;
    }
}
