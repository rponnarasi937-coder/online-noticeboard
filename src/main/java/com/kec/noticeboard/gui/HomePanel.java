package com.kec.noticeboard.gui;

import com.kec.noticeboard.dao.NoticeDAO;
import com.kec.noticeboard.entity.Category;
import com.kec.noticeboard.entity.Notice;
import com.kec.noticeboard.entity.Priority;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

public class HomePanel extends JPanel {

    private final NoticeDAO noticeDAO;
    private final MainFrame mainFrame;

    private JLabel totalNoticesLabel;
    private JLabel examNoticesLabel;
    private JLabel placementNoticesLabel;
    private JLabel eventsNoticesLabel;
    private JLabel alertTickerLabel;
    private JPanel latestNoticesContainer;

    public HomePanel(NoticeDAO noticeDAO, MainFrame mainFrame) {
        this.noticeDAO = noticeDAO;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(ModernTheme.BG_LIGHT);

        // Header Hero Banner
        JPanel heroPanel = createHeroBanner();
        add(heroPanel, BorderLayout.NORTH);

        // Scrollable Center Content
        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setBackground(ModernTheme.BG_LIGHT);
        centerContent.setBorder(new EmptyBorder(16, 24, 24, 24));

        // Stats Cards
        centerContent.add(createStatsRow());
        centerContent.add(Box.createVerticalStrut(20));

        // Category Quick Links
        centerContent.add(createCategoriesRow());
        centerContent.add(Box.createVerticalStrut(20));

        // Latest Notices Section
        centerContent.add(createLatestNoticesSection());

        JScrollPane scrollPane = new JScrollPane(centerContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        refreshData();
    }

    private JPanel createHeroBanner() {
        JPanel hero = new JPanel(new BorderLayout());
        hero.setBackground(ModernTheme.PRIMARY_NAVY);
        hero.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel collegeName = new JLabel("KONGU ENGINEERING COLLEGE (Autonomous)");
        collegeName.setFont(ModernTheme.FONT_SMALL);
        collegeName.setForeground(ModernTheme.ACCENT_CYAN);

        JLabel mainTitle = new JLabel("Official Online Notice Board");
        mainTitle.setFont(ModernTheme.FONT_LARGE_TITLE);
        mainTitle.setForeground(Color.WHITE);

        JLabel desc = new JLabel("Real-time academic circulars, exam schedules, placement drives and college bulletins");
        desc.setFont(ModernTheme.FONT_REGULAR);
        desc.setForeground(new Color(203, 213, 225));

        textPanel.add(collegeName);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(mainTitle);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(desc);

        hero.add(textPanel, BorderLayout.CENTER);

        // Button to browse notices
        JButton browseBtn = ModernTheme.createAccentButton("Open Student Portal ➔");
        browseBtn.addActionListener(e -> mainFrame.showView("STUDENT"));
        hero.add(browseBtn, BorderLayout.EAST);

        // Ticker Alert Strip
        JPanel tickerStrip = new JPanel(new BorderLayout());
        tickerStrip.setBackground(ModernTheme.PRIMARY_NAVY_DARK);
        tickerStrip.setBorder(new EmptyBorder(6, 24, 6, 24));

        JLabel alertBadge = new JLabel(" ⚡ URGENT ALERT: ");
        alertBadge.setFont(ModernTheme.FONT_BOLD);
        alertBadge.setForeground(Color.WHITE);
        alertBadge.setBackground(ModernTheme.COLOR_URGENT);
        alertBadge.setOpaque(true);

        alertTickerLabel = new JLabel("Loading announcements...");
        alertTickerLabel.setFont(ModernTheme.FONT_BOLD);
        alertTickerLabel.setForeground(ModernTheme.BG_ICE);

        tickerStrip.add(alertBadge, BorderLayout.WEST);
        tickerStrip.add(alertTickerLabel, BorderLayout.CENTER);

        JPanel bannerWrapper = new JPanel(new BorderLayout());
        bannerWrapper.add(hero, BorderLayout.CENTER);
        bannerWrapper.add(tickerStrip, BorderLayout.SOUTH);
        return bannerWrapper;
    }

    private JPanel createStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setOpaque(false);

        row.add(createStatCard("Total Notices", totalNoticesLabel = new JLabel("0"), "📋", ModernTheme.SECONDARY_BLUE));
        row.add(createStatCard("Exams & Tests", examNoticesLabel = new JLabel("0"), "📝", new Color(139, 92, 246)));
        row.add(createStatCard("Placements", placementNoticesLabel = new JLabel("0"), "💼", ModernTheme.COLOR_NORMAL));
        row.add(createStatCard("Events & Symposia", eventsNoticesLabel = new JLabel("0"), "🎯", new Color(236, 72, 153)));

        return row;
    }

    private JPanel createStatCard(String label, JLabel valueLabel, String icon, Color color) {
        JPanel card = ModernTheme.createCardPanel();
        card.setLayout(new BorderLayout());

        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(ModernTheme.PRIMARY_NAVY);
        JLabel title = new JLabel(label);
        title.setFont(ModernTheme.FONT_SMALL);
        title.setForeground(ModernTheme.TEXT_MUTED);

        text.add(valueLabel);
        text.add(title);
        card.add(text, BorderLayout.CENTER);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        card.add(iconLabel, BorderLayout.EAST);

        return card;
    }

    private JPanel createCategoriesRow() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Notice Categories (Click to filter):");
        title.setFont(ModernTheme.FONT_HEADER);
        title.setForeground(ModernTheme.PRIMARY_NAVY);
        panel.add(title, BorderLayout.NORTH);

        JPanel catRow = new JPanel(new GridLayout(1, 6, 10, 0));
        catRow.setOpaque(false);
        catRow.setBorder(new EmptyBorder(10, 0, 0, 0));

        for (Category cat : Category.values()) {
            JButton btn = new JButton(cat.getDisplayName());
            btn.setFont(ModernTheme.FONT_SMALL);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> mainFrame.showStudentWithCategory(cat));
            catRow.add(btn);
        }

        panel.add(catRow, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLatestNoticesSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Latest Campus Bulletins:");
        title.setFont(ModernTheme.FONT_HEADER);
        title.setForeground(ModernTheme.PRIMARY_NAVY);
        panel.add(title, BorderLayout.NORTH);

        latestNoticesContainer = new JPanel();
        latestNoticesContainer.setLayout(new BoxLayout(latestNoticesContainer, BoxLayout.Y_AXIS));
        latestNoticesContainer.setOpaque(false);
        latestNoticesContainer.setBorder(new EmptyBorder(10, 0, 0, 0));

        panel.add(latestNoticesContainer, BorderLayout.CENTER);
        return panel;
    }

    public void refreshData() {
        List<Notice> published = noticeDAO.getPublishedNotices();

        totalNoticesLabel.setText(String.valueOf(published.size()));
        examNoticesLabel.setText(String.valueOf(published.stream().filter(n -> n.getCategory() == Category.EXAMINATION).count()));
        placementNoticesLabel.setText(String.valueOf(published.stream().filter(n -> n.getCategory() == Category.PLACEMENTS).count()));
        eventsNoticesLabel.setText(String.valueOf(published.stream().filter(n -> n.getCategory() == Category.EVENTS).count()));

        // Update alert ticker
        Notice urgent = published.stream().filter(n -> n.getPriority() == Priority.URGENT).findFirst().orElse(null);
        if (urgent != null) {
            alertTickerLabel.setText(" " + urgent.getTitle() + " (" + urgent.getNoticeDate() + ")");
        } else if (!published.isEmpty()) {
            alertTickerLabel.setText(" " + published.get(0).getTitle());
        } else {
            alertTickerLabel.setText(" No urgent bulletins at this time.");
        }

        // Render latest 5 notice cards
        latestNoticesContainer.removeAll();
        List<Notice> top5 = published.stream().limit(5).collect(Collectors.toList());

        for (Notice n : top5) {
            JPanel card = ModernTheme.createCardPanel();
            card.setLayout(new BorderLayout());
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            top.setOpaque(false);

            JLabel priBadge = new JLabel(" " + n.getPriority().name() + " ");
            priBadge.setFont(ModernTheme.FONT_SMALL);
            priBadge.setOpaque(true);
            if (n.getPriority() == Priority.URGENT) {
                priBadge.setBackground(ModernTheme.COLOR_URGENT);
                priBadge.setForeground(Color.WHITE);
            } else if (n.getPriority() == Priority.HIGH) {
                priBadge.setBackground(ModernTheme.COLOR_HIGH);
                priBadge.setForeground(Color.WHITE);
            } else {
                priBadge.setBackground(ModernTheme.COLOR_NORMAL);
                priBadge.setForeground(Color.WHITE);
            }

            JLabel catBadge = new JLabel("[" + n.getCategory().getDisplayName() + "]");
            catBadge.setFont(ModernTheme.FONT_SMALL);
            catBadge.setForeground(ModernTheme.SECONDARY_BLUE);

            JLabel dateLabel = new JLabel("📅 " + n.getNoticeDate());
            dateLabel.setFont(ModernTheme.FONT_SMALL);
            dateLabel.setForeground(ModernTheme.TEXT_MUTED);

            top.add(priBadge);
            top.add(catBadge);
            top.add(dateLabel);
            card.add(top, BorderLayout.NORTH);

            JLabel titleLabel = new JLabel("<html><b>" + n.getTitle() + "</b></html>");
            titleLabel.setFont(ModernTheme.FONT_BOLD);
            titleLabel.setForeground(ModernTheme.PRIMARY_NAVY);
            titleLabel.setBorder(new EmptyBorder(6, 0, 4, 0));
            card.add(titleLabel, BorderLayout.CENTER);

            JLabel bottom = new JLabel("Target: " + n.getDepartment() + " | By: " + n.getPostedBy() + (n.getAttachmentFileName() != null ? " | 📎 Has Attachment" : ""));
            bottom.setFont(ModernTheme.FONT_SMALL);
            bottom.setForeground(ModernTheme.TEXT_MUTED);
            card.add(bottom, BorderLayout.SOUTH);

            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    new NoticeDetailDialog(mainFrame, n).setVisible(true);
                }
            });

            latestNoticesContainer.add(card);
            latestNoticesContainer.add(Box.createVerticalStrut(8));
        }

        latestNoticesContainer.revalidate();
        latestNoticesContainer.repaint();
    }
}
