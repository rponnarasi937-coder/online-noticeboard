package com.kec.noticeboard.gui;

import com.kec.noticeboard.dao.NoticeDAO;
import com.kec.noticeboard.entity.Category;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private final NoticeDAO noticeDAO;
    private final CardLayout cardLayout;
    private final JPanel contentCards;

    private final HomePanel homePanel;
    private final StudentPanel studentPanel;
    private final AdminDashboardPanel adminPanel;

    private boolean isAdminAuthenticated = false;
    private JButton adminNavBtn;
    private JButton authActionBtn;

    public MainFrame() {
        super("KEC Online Notice Board - Kongu Engineering College");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null);

        this.noticeDAO = new NoticeDAO();

        setLayout(new BorderLayout());

        // Left Navigation Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Center Card Panels
        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);

        homePanel = new HomePanel(noticeDAO, this);
        studentPanel = new StudentPanel(noticeDAO, this);
        adminPanel = new AdminDashboardPanel(noticeDAO, this);

        contentCards.add(homePanel, "HOME");
        contentCards.add(studentPanel, "STUDENT");
        contentCards.add(adminPanel, "ADMIN");

        add(contentCards, BorderLayout.CENTER);

        showView("HOME");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(ModernTheme.PRIMARY_NAVY_DARK);
        sidebar.setBorder(new EmptyBorder(16, 12, 16, 12));

        // Top Brand
        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("  🏛️  KEC");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(Color.WHITE);

        JLabel sub = new JLabel("  Online Notice Board");
        sub.setFont(ModernTheme.FONT_SMALL);
        sub.setForeground(ModernTheme.ACCENT_CYAN);

        brand.add(logo);
        brand.add(Box.createVerticalStrut(4));
        brand.add(sub);
        brand.add(Box.createVerticalStrut(20));

        sidebar.add(brand, BorderLayout.NORTH);

        // Navigation Items
        JPanel menu = new JPanel(new GridLayout(6, 1, 0, 8));
        menu.setOpaque(false);

        JButton homeBtn = createSidebarNavButton("🏠  Home Overview");
        homeBtn.addActionListener(e -> showView("HOME"));
        menu.add(homeBtn);

        JButton studentBtn = createSidebarNavButton("🎓  Student Board");
        studentBtn.addActionListener(e -> showView("STUDENT"));
        menu.add(studentBtn);

        adminNavBtn = createSidebarNavButton("📊  Admin Dashboard");
        adminNavBtn.addActionListener(e -> {
            if (!isAdminAuthenticated) {
                AdminLoginDialog dialog = new AdminLoginDialog(this);
                dialog.setVisible(true);
                if (dialog.isAuthenticated()) {
                    setAdminAuthenticated(true);
                    showView("ADMIN");
                }
            } else {
                showView("ADMIN");
            }
        });
        menu.add(adminNavBtn);

        sidebar.add(menu, BorderLayout.CENTER);

        // Sidebar Footer (Auth / Exit)
        JPanel footer = new JPanel(new GridLayout(2, 1, 0, 6));
        footer.setOpaque(false);

        authActionBtn = new JButton("🔒 Admin Login");
        authActionBtn.setFont(ModernTheme.FONT_SMALL);
        authActionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        authActionBtn.addActionListener(e -> {
            if (isAdminAuthenticated) {
                setAdminAuthenticated(false);
                JOptionPane.showMessageDialog(this, "Logged out successfully.", "Signed Out", JOptionPane.INFORMATION_MESSAGE);
                showView("HOME");
            } else {
                AdminLoginDialog dialog = new AdminLoginDialog(this);
                dialog.setVisible(true);
                if (dialog.isAuthenticated()) {
                    setAdminAuthenticated(true);
                    showView("ADMIN");
                }
            }
        });
        footer.add(authActionBtn);

        JLabel copyright = new JLabel("© 2026 KEC Mini Project", SwingConstants.CENTER);
        copyright.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        copyright.setForeground(new Color(148, 163, 184));
        footer.add(copyright);

        sidebar.add(footer, BorderLayout.SOUTH);
        return sidebar;
    }

    private JButton createSidebarNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(ModernTheme.FONT_BOLD);
        btn.setForeground(new Color(226, 232, 240));
        btn.setBackground(new Color(15, 30, 55));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 14, 10, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void setAdminAuthenticated(boolean auth) {
        this.isAdminAuthenticated = auth;
        if (auth) {
            authActionBtn.setText("🚪 Sign Out (Admin)");
            authActionBtn.setForeground(ModernTheme.COLOR_URGENT);
        } else {
            authActionBtn.setText("🔒 Admin Login");
            authActionBtn.setForeground(Color.BLACK);
        }
    }

    public void showView(String viewName) {
        cardLayout.show(contentCards, viewName);
        if ("HOME".equals(viewName)) homePanel.refreshData();
        if ("STUDENT".equals(viewName)) studentPanel.refreshData();
        if ("ADMIN".equals(viewName)) adminPanel.refreshData();
    }

    public void showStudentWithCategory(Category cat) {
        studentPanel.setCategoryFilter(cat);
        cardLayout.show(contentCards, "STUDENT");
    }

    public void notifyDataChanged() {
        homePanel.refreshData();
        studentPanel.refreshData();
        adminPanel.refreshData();
    }
}
