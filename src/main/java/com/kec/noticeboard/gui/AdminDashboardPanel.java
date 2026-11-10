package com.kec.noticeboard.gui;

import com.kec.noticeboard.dao.NoticeDAO;
import com.kec.noticeboard.entity.Category;
import com.kec.noticeboard.entity.Notice;
import com.kec.noticeboard.entity.Priority;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AdminDashboardPanel extends JPanel {

    private final NoticeDAO noticeDAO;
    private final MainFrame mainFrame;

    private JLabel statTotal;
    private JLabel statPublished;
    private JLabel statDraft;
    private JLabel statUrgent;

    private JTable adminTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<Notice> allNotices;

    public AdminDashboardPanel(NoticeDAO noticeDAO, MainFrame mainFrame) {
        this.noticeDAO = noticeDAO;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(ModernTheme.BG_LIGHT);

        // Header
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(ModernTheme.PRIMARY_NAVY);
        topHeader.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel("📊 Administrator Control Dashboard");
        title.setFont(ModernTheme.FONT_TITLE);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Full CRUD: Create, Edit, Delete, Toggle Publish Status & Manage Attachments");
        subtitle.setFont(ModernTheme.FONT_SMALL);
        subtitle.setForeground(ModernTheme.ACCENT_CYAN);

        JPanel headerText = new JPanel(new GridLayout(2, 1));
        headerText.setOpaque(false);
        headerText.add(title);
        headerText.add(subtitle);
        topHeader.add(headerText, BorderLayout.WEST);

        JButton postBtn = ModernTheme.createAccentButton("➕ Post New Notice");
        postBtn.addActionListener(e -> openCreateDialog());
        topHeader.add(postBtn, BorderLayout.EAST);

        add(topHeader, BorderLayout.NORTH);

        // Center Area (Metrics + Toolbar + Table)
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(14, 24, 14, 24));

        // Metrics Panel
        JPanel metricsRow = new JPanel(new GridLayout(1, 4, 12, 0));
        metricsRow.setOpaque(false);
        metricsRow.setBorder(new EmptyBorder(0, 0, 14, 0));

        metricsRow.add(createMetricBox("Total Bulletins", statTotal = new JLabel("0"), "📋", ModernTheme.SECONDARY_BLUE));
        metricsRow.add(createMetricBox("Published Live", statPublished = new JLabel("0"), "✅", ModernTheme.COLOR_NORMAL));
        metricsRow.add(createMetricBox("Draft Notices", statDraft = new JLabel("0"), "📝", ModernTheme.COLOR_HIGH));
        metricsRow.add(createMetricBox("Urgent Bulletins", statUrgent = new JLabel("0"), "🚨", ModernTheme.COLOR_URGENT));
        center.add(metricsRow, BorderLayout.NORTH);

        // Action Toolbar
        JPanel toolbarCard = ModernTheme.createCardPanel();
        toolbarCard.setLayout(new BorderLayout());

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actionButtons.setOpaque(false);

        JButton addNoticeBtn = new JButton("➕ Create");
        addNoticeBtn.addActionListener(e -> openCreateDialog());
        actionButtons.add(addNoticeBtn);

        JButton editNoticeBtn = new JButton("✏️ Edit");
        editNoticeBtn.addActionListener(e -> openEditDialog());
        actionButtons.add(editNoticeBtn);

        JButton togglePubBtn = new JButton("⏯️ Toggle Status");
        togglePubBtn.addActionListener(e -> handleTogglePublish());
        actionButtons.add(togglePubBtn);

        JButton deleteBtn = new JButton("🗑️ Delete");
        deleteBtn.setForeground(ModernTheme.COLOR_URGENT);
        deleteBtn.addActionListener(e -> handleDeleteNotice());
        actionButtons.add(deleteBtn);

        JButton refreshBtn = new JButton("↻ Refresh");
        refreshBtn.addActionListener(e -> refreshData());
        actionButtons.add(refreshBtn);

        toolbarCard.add(actionButtons, BorderLayout.WEST);

        // Search in toolbar
        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        searchBox.setOpaque(false);
        searchBox.add(new JLabel("Search:"));
        searchField = new JTextField(14);
        searchField.addActionListener(e -> applyAdminSearch());
        JButton searchBtn = new JButton("Go");
        searchBtn.addActionListener(e -> applyAdminSearch());
        searchBox.add(searchField);
        searchBox.add(searchBtn);
        toolbarCard.add(searchBox, BorderLayout.EAST);

        // Table
        String[] columns = {"#", "Date", "Status", "Priority", "Category", "Title", "Department", "Attachment"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        adminTable = new JTable(tableModel);
        adminTable.setFont(ModernTheme.FONT_REGULAR);
        adminTable.setRowHeight(32);
        adminTable.getTableHeader().setFont(ModernTheme.FONT_BOLD);
        adminTable.getTableHeader().setBackground(ModernTheme.BG_ICE);
        adminTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        adminTable.getColumnModel().getColumn(0).setPreferredWidth(35);
        adminTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        adminTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        adminTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        adminTable.getColumnModel().getColumn(4).setPreferredWidth(125);
        adminTable.getColumnModel().getColumn(5).setPreferredWidth(300);
        adminTable.getColumnModel().getColumn(6).setPreferredWidth(130);
        adminTable.getColumnModel().getColumn(7).setPreferredWidth(85);

        adminTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openEditDialog();
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(adminTable);

        JPanel tableArea = new JPanel(new BorderLayout());
        tableArea.setOpaque(false);
        tableArea.add(toolbarCard, BorderLayout.NORTH);
        tableArea.add(tableScroll, BorderLayout.CENTER);

        center.add(tableArea, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        refreshData();
    }

    private JPanel createMetricBox(String title, JLabel valueLabel, String icon, Color accentColor) {
        JPanel card = ModernTheme.createCardPanel();
        card.setLayout(new BorderLayout());

        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(ModernTheme.PRIMARY_NAVY);
        JLabel lbl = new JLabel(title);
        lbl.setFont(ModernTheme.FONT_SMALL);
        lbl.setForeground(ModernTheme.TEXT_MUTED);

        text.add(valueLabel);
        text.add(lbl);
        card.add(text, BorderLayout.CENTER);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        card.add(iconLabel, BorderLayout.EAST);

        return card;
    }

    private void openCreateDialog() {
        NoticeFormDialog dialog = new NoticeFormDialog(mainFrame, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            Notice newNotice = dialog.getNotice();
            noticeDAO.addNotice(newNotice);
            JOptionPane.showMessageDialog(this, "Notice published successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
            mainFrame.notifyDataChanged();
        }
    }

    private void openEditDialog() {
        int selected = adminTable.getSelectedRow();
        if (selected >= 0 && selected < allNotices.size()) {
            Notice target = allNotices.get(selected);
            NoticeFormDialog dialog = new NoticeFormDialog(mainFrame, target);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                noticeDAO.updateNotice(dialog.getNotice());
                JOptionPane.showMessageDialog(this, "Notice updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
                mainFrame.notifyDataChanged();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a notice row to edit.", "Notice Selection", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleTogglePublish() {
        int selected = adminTable.getSelectedRow();
        if (selected >= 0 && selected < allNotices.size()) {
            Notice target = allNotices.get(selected);
            boolean newStatus = noticeDAO.togglePublish(target.getId());
            JOptionPane.showMessageDialog(this, "Notice status changed to: " + (newStatus ? "Published Live" : "Draft"), "Status Updated", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
            mainFrame.notifyDataChanged();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a notice row to toggle status.", "Notice Selection", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleDeleteNotice() {
        int selected = adminTable.getSelectedRow();
        if (selected >= 0 && selected < allNotices.size()) {
            Notice target = allNotices.get(selected);
            int opt = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to permanently delete:\n\"" + target.getTitle() + "\"?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opt == JOptionPane.YES_OPTION) {
                noticeDAO.deleteNotice(target.getId());
                JOptionPane.showMessageDialog(this, "Notice deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
                mainFrame.notifyDataChanged();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a notice row to delete.", "Notice Selection", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void applyAdminSearch() {
        String keyword = searchField.getText().trim();
        allNotices = noticeDAO.searchNotices(null, null, keyword, false);
        populateTable(allNotices);
    }

    public void refreshData() {
        allNotices = noticeDAO.getAllNotices();

        // Metrics
        int total = allNotices.size();
        int published = (int) allNotices.stream().filter(Notice::isPublished).count();
        int draft = total - published;
        int urgent = (int) allNotices.stream().filter(n -> n.getPriority() == Priority.URGENT).count();

        statTotal.setText(String.valueOf(total));
        statPublished.setText(String.valueOf(published));
        statDraft.setText(String.valueOf(draft));
        statUrgent.setText(String.valueOf(urgent));

        populateTable(allNotices);
    }

    private void populateTable(List<Notice> notices) {
        tableModel.setRowCount(0);
        for (int i = 0; i < notices.size(); i++) {
            Notice n = notices.get(i);
            tableModel.addRow(new Object[]{
                    i + 1,
                    n.getNoticeDate(),
                    n.isPublished() ? "● Live" : "○ Draft",
                    n.getPriority().name(),
                    n.getCategory().getDisplayName(),
                    n.getTitle(),
                    n.getDepartment(),
                    n.getAttachmentFileName() != null ? "📎 Yes" : "-"
            });
        }
    }
}
