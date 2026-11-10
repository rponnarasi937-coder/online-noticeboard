package com.kec.noticeboard.gui;

import com.kec.noticeboard.entity.Notice;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.print.PrinterException;
import java.io.File;

public class NoticeDetailDialog extends JDialog {

    public NoticeDetailDialog(JFrame parent, Notice notice) {
        super(parent, "Notice Details - " + notice.getTitle(), true);
        setSize(650, 520);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(ModernTheme.PRIMARY_NAVY);
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("<html><body style='width: 480px;'><b>" + notice.getTitle() + "</b></body></html>");
        titleLabel.setFont(ModernTheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Priority Badge
        JLabel priorityBadge = new JLabel(" " + notice.getPriority().name() + " ");
        priorityBadge.setFont(ModernTheme.FONT_BOLD);
        priorityBadge.setOpaque(true);
        if (notice.getPriority() == com.kec.noticeboard.entity.Priority.URGENT) {
            priorityBadge.setBackground(ModernTheme.COLOR_URGENT);
            priorityBadge.setForeground(Color.WHITE);
        } else if (notice.getPriority() == com.kec.noticeboard.entity.Priority.HIGH) {
            priorityBadge.setBackground(ModernTheme.COLOR_HIGH);
            priorityBadge.setForeground(Color.WHITE);
        } else {
            priorityBadge.setBackground(ModernTheme.COLOR_NORMAL);
            priorityBadge.setForeground(Color.WHITE);
        }
        headerPanel.add(priorityBadge, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Center Content
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(ModernTheme.BG_WHITE);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Meta info strip
        JPanel metaPanel = new JPanel(new GridLayout(2, 2, 10, 6));
        metaPanel.setBackground(ModernTheme.BG_ICE);
        metaPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.SECONDARY_BLUE, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        metaPanel.add(new JLabel("Category: " + notice.getCategory().getDisplayName()));
        metaPanel.add(new JLabel("Notice Date: " + notice.getNoticeDate()));
        metaPanel.add(new JLabel("Department: " + notice.getDepartment()));
        metaPanel.add(new JLabel("Issued By: " + notice.getPostedBy()));

        contentPanel.add(metaPanel);
        contentPanel.add(Box.createVerticalStrut(14));

        JLabel descLabel = new JLabel("Full Circular Details:");
        descLabel.setFont(ModernTheme.FONT_BOLD);
        contentPanel.add(descLabel);
        contentPanel.add(Box.createVerticalStrut(6));

        JTextArea descArea = new JTextArea(notice.getDescription());
        descArea.setFont(ModernTheme.FONT_REGULAR);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBackground(ModernTheme.BG_LIGHT);
        descArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JScrollPane scrollPane = new JScrollPane(descArea);
        scrollPane.setPreferredSize(new Dimension(580, 180));
        contentPanel.add(scrollPane);

        // Attachment Box if available
        if (notice.getAttachmentFileName() != null && !notice.getAttachmentFileName().isBlank()) {
            contentPanel.add(Box.createVerticalStrut(12));
            JPanel attachPanel = new JPanel(new BorderLayout());
            attachPanel.setBackground(ModernTheme.BG_ICE);
            attachPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1),
                    new EmptyBorder(6, 12, 6, 12)
            ));

            JLabel attachName = new JLabel("📎 Circular Attachment: " + notice.getAttachmentFileName());
            attachName.setFont(ModernTheme.FONT_BOLD);
            attachPanel.add(attachName, BorderLayout.CENTER);

            JButton openAttachBtn = new JButton("Open / View File ↗");
            openAttachBtn.setFont(ModernTheme.FONT_SMALL);
            openAttachBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            openAttachBtn.addActionListener(e -> {
                try {
                    File file = new File(notice.getAttachmentUrl());
                    if (file.exists() && Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(file);
                    } else {
                        JOptionPane.showMessageDialog(this, "Attachment file is stored at: " + notice.getAttachmentUrl(), "Attachment Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Could not launch viewer: " + ex.getMessage(), "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            attachPanel.add(openAttachBtn, BorderLayout.EAST);
            contentPanel.add(attachPanel);
        }

        add(contentPanel, BorderLayout.CENTER);

        // Footer Buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(ModernTheme.BG_LIGHT);

        JButton printBtn = new JButton("Print Notice 🖨️");
        printBtn.setFont(ModernTheme.FONT_REGULAR);
        printBtn.addActionListener(e -> {
            try {
                descArea.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Print error: " + ex.getMessage());
            }
        });

        JButton closeBtn = ModernTheme.createPrimaryButton("Close");
        closeBtn.addActionListener(e -> dispose());

        footer.add(printBtn);
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);
    }
}
