package com.notepad.ui;

import com.notepad.dao.NoteDAO;
import com.notepad.model.Note;
import com.notepad.model.RecentDocument;
import com.notepad.model.User;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;

public class MainEditorFrame extends JFrame implements ActionListener {
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JLabel statusBar;
    private User loggedInUser;
    private NoteDAO noteDAO;
    private Note currentNote;
    private String currentFilePath;
    private boolean wordWrapEnabled = false;
    private Timer autoSaveTimer;

    // Menus
    private JMenuItem newFileMenu, openFileMenu, saveFileMenu, saveAsMenu, exitMenu;
    private JMenuItem cutMenu, copyMenu, pasteMenu;
    private JMenuItem fontMenu, wordWrapMenu;
    private JMenu recentDocsMenu;
    private JMenuItem newCloudNoteMenu, saveCloudNoteMenu, syncCloudNoteMenu;

    public MainEditorFrame(User user) {
        this.loggedInUser = user;
        this.noteDAO = new NoteDAO();

        setTitle("Notepad - " + (user != null ? user.getUsername() : "Guest"));
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createUI();
        createMenuBar();
        updateStatusBar();

        if (user != null) {
            setupAutoSave();
            refreshRecentDocumentsMenu();
        }

        setLocationRelativeTo(null);
    }

    private void createUI() {
        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Listen to cursor movement to update status bar
        textArea.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                updateStatusBar();
            }
        });

        scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);

        statusBar = new JLabel("L: 1, C: 1 | Not Saved");
        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        add(statusBar, BorderLayout.SOUTH);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        newFileMenu = new JMenuItem("New");
        openFileMenu = new JMenuItem("Open");
        saveFileMenu = new JMenuItem("Save");
        saveAsMenu = new JMenuItem("Save As");
        exitMenu = new JMenuItem("Exit");

        newFileMenu.addActionListener(this);
        openFileMenu.addActionListener(this);
        saveFileMenu.addActionListener(this);
        saveAsMenu.addActionListener(this);
        exitMenu.addActionListener(this);

        fileMenu.add(newFileMenu);
        fileMenu.add(openFileMenu);
        fileMenu.add(saveFileMenu);
        fileMenu.add(saveAsMenu);
        fileMenu.addSeparator();
        fileMenu.add(exitMenu);

        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        cutMenu = new JMenuItem("Cut");
        copyMenu = new JMenuItem("Copy");
        pasteMenu = new JMenuItem("Paste");

        cutMenu.addActionListener(e -> textArea.cut());
        copyMenu.addActionListener(e -> textArea.copy());
        pasteMenu.addActionListener(e -> textArea.paste());

        editMenu.add(cutMenu);
        editMenu.add(copyMenu);
        editMenu.add(pasteMenu);

        // Format Menu
        JMenu formatMenu = new JMenu("Format");
        wordWrapMenu = new JCheckBoxMenuItem("Word Wrap");
        fontMenu = new JMenuItem("Font...");

        wordWrapMenu.addActionListener(e -> toggleWordWrap());
        fontMenu.addActionListener(e -> changeFont());

        formatMenu.add(wordWrapMenu);
        formatMenu.add(fontMenu);

        // Database Menu
        JMenu dbMenu = new JMenu("Database");
        newCloudNoteMenu = new JMenuItem("New Cloud Note");
        saveCloudNoteMenu = new JMenuItem("Save to Cloud");
        syncCloudNoteMenu = new JMenuItem("Sync from Cloud");
        recentDocsMenu = new JMenu("Recent Cloud Notes");

        newCloudNoteMenu.addActionListener(this);
        saveCloudNoteMenu.addActionListener(this);
        syncCloudNoteMenu.addActionListener(this);

        if (loggedInUser == null) {
            dbMenu.setEnabled(false); // Disable DB features for guest users
        }

        dbMenu.add(newCloudNoteMenu);
        dbMenu.add(saveCloudNoteMenu);
        dbMenu.add(syncCloudNoteMenu);
        dbMenu.addSeparator();
        dbMenu.add(recentDocsMenu);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);
        menuBar.add(dbMenu);

        setJMenuBar(menuBar);
    }

    private void updateStatusBar() {
        int line = 1;
        int column = 1;
        try {
            int caretpos = textArea.getCaretPosition();
            line = textArea.getLineOfOffset(caretpos);
            column = caretpos - textArea.getLineStartOffset(line);
            line += 1; // 1-based line number
            column += 1; // 1-based column number
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        String saveState = "Not Saved";
        if (currentNote != null) saveState = "Cloud Note: " + currentNote.getTitle();
        else if (currentFilePath != null) saveState = "Local File";
        
        statusBar.setText("Ln: " + line + ", Col: " + column + " | " + saveState);
    }

    private void toggleWordWrap() {
        wordWrapEnabled = !wordWrapEnabled;
        textArea.setLineWrap(wordWrapEnabled);
        textArea.setWrapStyleWord(wordWrapEnabled);
        if (wordWrapEnabled) {
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        } else {
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }
    }

    private void changeFont() {
        FontChooserDialog fontDialog = new FontChooserDialog(this, textArea.getFont());
        fontDialog.setVisible(true);
        if (fontDialog.getSelectedFont() != null) {
            textArea.setFont(fontDialog.getSelectedFont());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newFileMenu) {
            textArea.setText("");
            currentFilePath = null;
            currentNote = null;
            updateStatusBar();
        } else if (e.getSource() == openFileMenu) {
            openLocalFile();
        } else if (e.getSource() == saveFileMenu) {
            saveLocalFile(false);
        } else if (e.getSource() == saveAsMenu) {
            saveLocalFile(true);
        } else if (e.getSource() == exitMenu) {
            System.exit(0);
        } else if (e.getSource() == newCloudNoteMenu) {
            textArea.setText("");
            currentFilePath = null;
            currentNote = null;
            String title = JOptionPane.showInputDialog(this, "Enter Cloud Note Title:");
            if (title != null && !title.trim().isEmpty()) {
                currentNote = new Note();
                currentNote.setTitle(title);
                saveCloudNote();
            }
        } else if (e.getSource() == saveCloudNoteMenu) {
            saveCloudNote();
        } else if (e.getSource() == syncCloudNoteMenu) {
            if (currentNote != null) {
                Note freshNote = noteDAO.getNote(currentNote.getNoteId(), loggedInUser.getUserId());
                if (freshNote != null) {
                    textArea.setText(freshNote.getContent());
                    JOptionPane.showMessageDialog(this, "Synced successfully!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Not currently editing a cloud note.");
            }
        }
    }

    private void openLocalFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            currentFilePath = file.getAbsolutePath();
            currentNote = null; // Unlink from cloud
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                textArea.read(reader, null);
                updateStatusBar();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveLocalFile(boolean saveAs) {
        if (currentFilePath == null || saveAs) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                currentFilePath = file.getAbsolutePath();
            } else {
                return; // User cancelled
            }
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFilePath))) {
            textArea.write(writer);
            updateStatusBar();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveCloudNote() {
        if (loggedInUser == null) return;
        
        String content = textArea.getText();
        
        if (currentNote == null || currentNote.getNoteId() == 0) {
            // New note
            String title = currentNote != null ? currentNote.getTitle() : JOptionPane.showInputDialog(this, "Enter note title:");
            if (title == null || title.trim().isEmpty()) return;
            
            int newId = noteDAO.saveNewNote(loggedInUser.getUserId(), title, content);
            if (newId != -1) {
                currentNote = noteDAO.getNote(newId, loggedInUser.getUserId());
                updateStatusBar();
                refreshRecentDocumentsMenu();
                JOptionPane.showMessageDialog(this, "Note saved to cloud.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save note.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Update existing note
            boolean success = noteDAO.updateNote(currentNote.getNoteId(), currentNote.getTitle(), content);
            if (success) {
                updateStatusBar();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update cloud note.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshRecentDocumentsMenu() {
        if (loggedInUser == null) return;
        
        recentDocsMenu.removeAll();
        List<RecentDocument> recentDocs = noteDAO.getRecentDocuments(loggedInUser.getUserId());
        
        if (recentDocs.isEmpty()) {
            JMenuItem emptyItem = new JMenuItem("No recent documents");
            emptyItem.setEnabled(false);
            recentDocsMenu.add(emptyItem);
            return;
        }

        for (RecentDocument doc : recentDocs) {
            JMenuItem item = new JMenuItem(doc.getNoteTitle() + " (" + doc.getLastOpened().toString().substring(0, 16) + ")");
            item.addActionListener(e -> loadCloudNote(doc.getNoteId()));
            recentDocsMenu.add(item);
        }
    }

    private void loadCloudNote(int noteId) {
        Note note = noteDAO.getNote(noteId, loggedInUser.getUserId());
        if (note != null) {
            currentNote = note;
            currentFilePath = null; // Unlink from local
            textArea.setText(note.getContent());
            updateStatusBar();
            refreshRecentDocumentsMenu();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to load note.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupAutoSave() {
        // Auto-save every 30 seconds (30000 ms)
        autoSaveTimer = new Timer(30000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentNote != null && currentNote.getNoteId() != 0) {
                    noteDAO.updateNote(currentNote.getNoteId(), currentNote.getTitle(), textArea.getText());
                    System.out.println("Auto-saved note ID: " + currentNote.getNoteId());
                }
            }
        });
        autoSaveTimer.start();
    }
}
