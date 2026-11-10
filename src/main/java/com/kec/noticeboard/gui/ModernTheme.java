package com.kec.noticeboard.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ModernTheme {

    public static final Color PRIMARY_NAVY = new Color(0, 33, 71);
    public static final Color PRIMARY_NAVY_DARK = new Color(0, 21, 46);
    public static final Color SECONDARY_BLUE = new Color(2, 132, 199);
    public static final Color ACCENT_CYAN = new Color(56, 189, 248);
    public static final Color BG_ICE = new Color(240, 247, 255);
    public static final Color BG_LIGHT = new Color(248, 250, 252);
    public static final Color BG_WHITE = Color.WHITE;
    public static final Color BORDER_COLOR = new Color(226, 232, 240);
    public static final Color TEXT_DARK = new Color(15, 23, 42);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);

    public static final Color COLOR_URGENT = new Color(220, 38, 38);
    public static final Color COLOR_HIGH = new Color(217, 119, 6);
    public static final Color COLOR_NORMAL = new Color(16, 185, 129);

    public static final Font FONT_LARGE_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(PRIMARY_NAVY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(PRIMARY_NAVY);
        btn.setBackground(BG_ICE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(SECONDARY_BLUE, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        return btn;
    }

    public static JButton createAccentButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(SECONDARY_BLUE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));
        return panel;
    }
}
