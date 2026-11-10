package com.kec.noticeboard.gui;

import com.kec.noticeboard.dao.NoticeDAO;
import com.kec.noticeboard.entity.Category;
import com.kec.noticeboard.entity.Notice;
import com.kec.noticeboard.entity.Priority;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class StudentPanel extends JPanel {

    private final NoticeDAO noticeDAO;
    private final MainFrame mainFrame;

    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> priorityCombo;
    private JTable noticeTable;
    private DefaultTableModel tableModel;
    private JLabel countLabel;

    private List<Notice> currentNotices;

    public StudentPanel(NoticeDAO noticeDAO, MainFrame mainFrame) {
        this.noticeDAO = noticeDAO;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(ModernTheme.BG_LIGHT);

        // Header Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(ModernTheme.PRIMARY_NAVY);
        titlePanel.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel("🎓 Student Notice Board");
        title.setFont(ModernTheme.FONT_TITLE);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Search, filter and download official academic and campus circulars");
        subtitle.setFont(ModernTheme.FONT_SMALL);
        subtitle.setForeground(ModernTheme.ACCENT_CYAN);

        JPanel textWrap = new JPanel(new GridLayout(2, 1));
        textWrap.setOpaque(false);
        textWrap.add(title);
        textWrap.add(subtitle);
        titlePanel.add(textWrap, BorderLayout.WEST);

        countLabel = new JLabel("0 Notices");
        countLabel.setFont(ModernTheme.FONT_BOLD);
        countLabel.setForeground(Color.WHITE);
        titlePanel.add(countLabel, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);

        // Filter Bar Panel
        JPanel filterCard = ModernTheme.createCardPanel();
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));

        filterCard.add(new JLabel("🔍 Search:"));
        searchField = new JTextField(18);
        searchField.setFont(ModernTheme.FONT_REGULAR);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });
        filterCard.add(searchField);

        filterCard.add(new JLabel("Category:"));
        categoryCombo = new JComboBox<>();
        categoryCombo.addItem("All Categories");
        for (Category c : Category.values()) {
            categoryCombo.addItem(c.name());
        }
        categoryCombo.addActionListener(e -> applyFilters());
        filterCard.add(categoryCombo);

        filterCard.add(new JLabel("Priority:"));
        priorityCombo = new JComboBox<>(new String[]{"All Priorities", "URGENT", "HIGH", "NORMAL"});
        priorityCombo.addActionListener(e -> applyFilters());
        filterCard.add(priorityCombo);

        JButton resetBtn = new JButton("Reset ↻");
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            categoryCombo.setSelectedIndex(0);
            priorityCombo.setSelectedIndex(0);
            applyFilters();
        });
        filterCard.add(resetBtn);

        // Table
        String[] columns = {"#", "Date", "Priority", "Category", "Notice Title", "Department", "Attachment"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        noticeTable = new JTable(tableModel);
        noticeTable.setFont(ModernTheme.FONT_REGULAR);
        noticeTable.setRowHeight(32);
        noticeTable.getTableHeader().setFont(ModernTheme.FONT_BOLD);
        noticeTable.getTableHeader().setBackground(ModernTheme.BG_ICE);
        noticeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set column widths
        noticeTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        noticeTable.getColumnModel().getColumn(1).setPreferredWidth(95);
        noticeTable.getColumnModel().getColumn(2).setPreferredWidth(85);
        noticeTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        noticeTable.getColumnModel().getColumn(4).setPreferredWidth(320);
        noticeTable.getColumnModel().getColumn(5).setPreferredWidth(140);
        noticeTable.getColumnModel().getColumn(6).setPreferredWidth(90);

        noticeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedNotice();
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(noticeTable);
        tableScroll.setBorder(new EmptyBorder(12, 24, 12, 24));

        // Center Content wrapper
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(filterCard, BorderLayout.NORTH);
        centerPanel.add(tableScroll, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Footer Actions
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        footer.setBackground(ModernTheme.BG_LIGHT);

        JButton viewBtn = ModernTheme.createPrimaryButton("View Full Circular Details ➔");
        viewBtn.addActionListener(e -> openSelectedNotice());
        footer.add(viewBtn);

        add(footer, BorderLayout.SOUTH);

        refreshData();
    }

    public void setCategoryFilter(Category cat) {
        if (cat != null) {
            categoryCombo.setSelectedItem(cat.name());
        } else {
            categoryCombo.setSelectedIndex(0);
        }
        applyFilters();
    }

    private void openSelectedNotice() {
        int selectedRow = noticeTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < currentNotices.size()) {
            Notice n = currentNotices.get(selectedRow);
            new NoticeDetailDialog(mainFrame, n).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a notice to view details.", "Notice Selection", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void refreshData() {
        applyFilters();
    }

    private void applyFilters() {
        String search = searchField.getText().trim();
        Category cat = null;
        if (categoryCombo.getSelectedIndex() > 0) {
            cat = Category.valueOf((String) categoryCombo.getSelectedItem());
        }

        Priority pri = null;
        if (priorityCombo.getSelectedIndex() > 0) {
            pri = Priority.valueOf((String) priorityCombo.getSelectedItem());
        }

        currentNotices = noticeDAO.searchNotices(cat, pri, search, true);

        tableModel.setRowCount(0);
        for (int i = 0; i < currentNotices.size(); i++) {
            Notice n = currentNotices.get(i);
            tableModel.addRow(new Object[]{
                    i + 1,
                    n.getNoticeDate(),
                    n.getPriority().name(),
                    n.getCategory().getDisplayName(),
                    n.getTitle(),
                    n.getDepartment(),
                    n.getAttachmentFileName() != null ? "📎 Yes" : "-"
            });
        }

        countLabel.setText("Showing " + currentNotices.size() + " Notices");
    }
}
