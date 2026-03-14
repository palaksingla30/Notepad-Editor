package com.notepad.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FontChooserDialog extends JDialog implements ActionListener {

    private JComboBox<String> fontNameCombo;
    private JComboBox<String> fontStyleCombo;
    private JComboBox<Integer> fontSizeCombo;
    private JButton okButton;
    private JButton cancelButton;
    private Font selectedFont;

    private static final String[] STYLES = {"Plain", "Bold", "Italic", "Bold Italic"};
    private static final int[] STYLE_VALUES = {Font.PLAIN, Font.BOLD, Font.ITALIC, Font.BOLD | Font.ITALIC};
    private static final Integer[] SIZES = {8, 10, 12, 14, 16, 18, 20, 24, 28, 32, 36, 48, 72};

    public FontChooserDialog(JFrame parent, Font initialFont) {
        super(parent, "Select Font", true);
        selectedFont = initialFont;

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Font Names
        panel.add(new JLabel("Font:"));
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();
        fontNameCombo = new JComboBox<>(fontNames);
        fontNameCombo.setSelectedItem(initialFont.getName());
        panel.add(fontNameCombo);

        // Font Styles
        panel.add(new JLabel("Style:"));
        fontStyleCombo = new JComboBox<>(STYLES);
        int styleIndex = 0;
        for (int i = 0; i < STYLE_VALUES.length; i++) {
            if (initialFont.getStyle() == STYLE_VALUES[i]) {
                styleIndex = i;
                break;
            }
        }
        fontStyleCombo.setSelectedIndex(styleIndex);
        panel.add(fontStyleCombo);

        // Font Sizes
        panel.add(new JLabel("Size:"));
        fontSizeCombo = new JComboBox<>(SIZES);
        fontSizeCombo.setSelectedItem(initialFont.getSize());
        panel.add(fontSizeCombo);

        JPanel btnPanel = new JPanel();
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        btnPanel.add(okButton);
        btnPanel.add(cancelButton);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(btnPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            String name = (String) fontNameCombo.getSelectedItem();
            int style = STYLE_VALUES[fontStyleCombo.getSelectedIndex()];
            int size = (Integer) fontSizeCombo.getSelectedItem();
            selectedFont = new Font(name, style, size);
            dispose();
        } else if (e.getSource() == cancelButton) {
            dispose();
        }
    }

    public Font getSelectedFont() {
        return selectedFont;
    }
}
