package com.kec.noticeboard.gui;

import com.kec.noticeboard.entity.Category;
import com.kec.noticeboard.entity.Notice;
import com.kec.noticeboard.entity.Priority;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;

public class NoticeFormDialog extends JDialog {

    private final JTextField titleField;
    private final JComboBox<Category> categoryBox;
    private final JComboBox<Priority> priorityBox;
    private final JTextField deptField;
    private final JTextField dateField;
    private final JTextField postedByField;
    private final JTextArea descArea;
    private final JCheckBox publishedCheck;
    private final JLabel attachmentLabel;

    private String attachmentUrl = null;
    private String attachmentFileName = null;
    private String attachmentFileType = null;

    private Notice notice;
    private boolean saved = false;

    public NoticeFormDialog(JFrame parent, Notice existingNotice) {
        super(parent, existingNotice == null ? "Create New College Notice" : "Edit College Notice", true);
        this.notice = existingNotice;

        setSize(600, 680);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        header.setBackground(ModernTheme.PRIMARY_NAVY);
        JLabel title = new JLabel(existingNotice == null ? "➕ Post New College Notice" : "✏️ Edit Notice");
        title.setFont(ModernTheme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Body Form
        JPanel form = new JPanel();
        form.setBackground(ModernTheme.BG_WHITE);
        form.setBorder(new EmptyBorder(16, 20, 16, 20));
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        titleField = addFormField(form, "Notice Title *", existingNotice != null ? existingNotice.getTitle() : "");

        // Category & Priority row
        JPanel row1 = new JPanel(new GridLayout(1, 2, 12, 0));
        row1.setBackground(ModernTheme.BG_WHITE);

        JPanel catPanel = new JPanel(new BorderLayout(0, 4));
        catPanel.setBackground(ModernTheme.BG_WHITE);
        catPanel.add(new JLabel("Category *"), BorderLayout.NORTH);
        categoryBox = new JComboBox<>(Category.values());
        if (existingNotice != null) categoryBox.setSelectedItem(existingNotice.getCategory());
        catPanel.add(categoryBox, BorderLayout.CENTER);
        row1.add(catPanel);

        JPanel priPanel = new JPanel(new BorderLayout(0, 4));
        priPanel.setBackground(ModernTheme.BG_WHITE);
        priPanel.add(new JLabel("Priority Level *"), BorderLayout.NORTH);
        priorityBox = new JComboBox<>(Priority.values());
        if (existingNotice != null) priorityBox.setSelectedItem(existingNotice.getPriority());
        priPanel.add(priorityBox, BorderLayout.CENTER);
        row1.add(priPanel);

        form.add(row1);
        form.add(Box.createVerticalStrut(10));

        // Dept & Date row
        JPanel row2 = new JPanel(new GridLayout(1, 2, 12, 0));
        row2.setBackground(ModernTheme.BG_WHITE);

        JPanel deptPanel = new JPanel(new BorderLayout(0, 4));
        deptPanel.setBackground(ModernTheme.BG_WHITE);
        deptPanel.add(new JLabel("Target Department"), BorderLayout.NORTH);
        deptField = new JTextField(existingNotice != null ? existingNotice.getDepartment() : "All Departments");
        deptPanel.add(deptField, BorderLayout.CENTER);
        row2.add(deptPanel);

        JPanel datePanel = new JPanel(new BorderLayout(0, 4));
        datePanel.setBackground(ModernTheme.BG_WHITE);
        datePanel.add(new JLabel("Notice Date (YYYY-MM-DD) *"), BorderLayout.NORTH);
        dateField = new JTextField(existingNotice != null ? existingNotice.getNoticeDate().toString() : LocalDate.now().toString());
        datePanel.add(dateField, BorderLayout.CENTER);
        row2.add(datePanel);

        form.add(row2);
        form.add(Box.createVerticalStrut(10));

        postedByField = addFormField(form, "Issuing Authority / Cell", existingNotice != null ? existingNotice.getPostedBy() : "Principal Office");

        // Description
        JLabel descLabel = new JLabel("Full Notice Circular Text *");
        descLabel.setFont(ModernTheme.FONT_BOLD);
        form.add(descLabel);
        form.add(Box.createVerticalStrut(4));

        descArea = new JTextArea(existingNotice != null ? existingNotice.getDescription() : "", 6, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        form.add(descScroll);
        form.add(Box.createVerticalStrut(10));

        // Attachment chooser
        JPanel attachPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        attachPanel.setBackground(ModernTheme.BG_WHITE);
        JButton chooseFileBtn = new JButton("📎 Choose Attachment...");
        attachmentLabel = new JLabel(existingNotice != null && existingNotice.getAttachmentFileName() != null ? existingNotice.getAttachmentFileName() : "No file attached");
        attachmentLabel.setForeground(ModernTheme.TEXT_MUTED);

        if (existingNotice != null) {
            attachmentUrl = existingNotice.getAttachmentUrl();
            attachmentFileName = existingNotice.getAttachmentFileName();
            attachmentFileType = existingNotice.getAttachmentFileType();
        }

        chooseFileBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int res = chooser.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                File selected = chooser.getSelectedFile();
                attachmentUrl = selected.getAbsolutePath();
                attachmentFileName = selected.getName();
                attachmentFileType = selected.getName().endsWith(".pdf") ? "application/pdf" : "application/octet-stream";
                attachmentLabel.setText(selected.getName());
            }
        });

        attachPanel.add(chooseFileBtn);
        attachPanel.add(attachmentLabel);
        form.add(attachPanel);
        form.add(Box.createVerticalStrut(10));

        // Published checkbox
        publishedCheck = new JCheckBox("Publish Live Immediately (visible on Student Board)", existingNotice == null || existingNotice.isPublished());
        publishedCheck.setBackground(ModernTheme.BG_WHITE);
        form.add(publishedCheck);

        add(new JScrollPane(form), BorderLayout.CENTER);

        // Footer Buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(ModernTheme.BG_LIGHT);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = ModernTheme.createPrimaryButton("Save & Publish ➔");
        saveBtn.addActionListener(e -> handleSave());

        footer.add(cancelBtn);
        footer.add(saveBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private JTextField addFormField(JPanel parent, String labelText, String initialVal) {
        JLabel label = new JLabel(labelText);
        label.setFont(ModernTheme.FONT_BOLD);
        parent.add(label);
        parent.add(Box.createVerticalStrut(4));
        JTextField field = new JTextField(initialVal);
        field.setFont(ModernTheme.FONT_REGULAR);
        parent.add(field);
        parent.add(Box.createVerticalStrut(10));
        return field;
    }

    private void handleSave() {
        String title = titleField.getText().trim();
        String desc = descArea.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a notice title", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter notice description", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateField.getText().trim());
        } catch (Exception ex) {
            date = LocalDate.now();
        }

        if (notice == null) {
            notice = new Notice();
        }

        notice.setTitle(title);
        notice.setDescription(desc);
        notice.setCategory((Category) categoryBox.getSelectedItem());
        notice.setPriority((Priority) priorityBox.getSelectedItem());
        notice.setDepartment(deptField.getText().trim());
        notice.setNoticeDate(date);
        notice.setPostedBy(postedByField.getText().trim());
        notice.setPublished(publishedCheck.isSelected());
        notice.setAttachmentUrl(attachmentUrl);
        notice.setAttachmentFileName(attachmentFileName);
        notice.setAttachmentFileType(attachmentFileType);

        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public Notice getNotice() {
        return notice;
    }
}
