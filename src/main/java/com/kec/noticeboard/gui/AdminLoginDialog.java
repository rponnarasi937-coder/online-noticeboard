package com.kec.noticeboard.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminLoginDialog extends JDialog {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private boolean authenticated = false;

    public AdminLoginDialog(JFrame parent) {
        super(parent, "Admin Authentication - KEC Notice Board", true);
        setSize(420, 360);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(ModernTheme.PRIMARY_NAVY);
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("🔒 Admin Portal Sign In");
        titleLabel.setFont(ModernTheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Restricted to Authorized College Staff");
        subtitleLabel.setFont(ModernTheme.FONT_SMALL);
        subtitleLabel.setForeground(ModernTheme.ACCENT_CYAN);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subtitleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Body Panel
        JPanel bodyPanel = new JPanel();
        bodyPanel.setBackground(ModernTheme.BG_WHITE);
        bodyPanel.setBorder(new EmptyBorder(20, 24, 20, 24));
        bodyPanel.setLayout(new GridLayout(6, 1, 6, 6));

        // Demo credentials tip
        JLabel demoTip = new JLabel("Demo Access: Username: admin | Password: admin123");
        demoTip.setFont(ModernTheme.FONT_SMALL);
        demoTip.setForeground(ModernTheme.SECONDARY_BLUE);
        bodyPanel.add(demoTip);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(ModernTheme.FONT_BOLD);
        bodyPanel.add(userLabel);

        usernameField = new JTextField("admin");
        usernameField.setFont(ModernTheme.FONT_REGULAR);
        bodyPanel.add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(ModernTheme.FONT_BOLD);
        bodyPanel.add(passLabel);

        passwordField = new JPasswordField("admin123");
        passwordField.setFont(ModernTheme.FONT_REGULAR);
        bodyPanel.add(passwordField);

        JButton autoFillBtn = new JButton("⚡ Auto-fill Default Admin");
        autoFillBtn.setFont(ModernTheme.FONT_SMALL);
        autoFillBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        autoFillBtn.addActionListener(e -> {
            usernameField.setText("admin");
            passwordField.setText("admin123");
        });
        bodyPanel.add(autoFillBtn);

        add(bodyPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footerPanel.setBackground(ModernTheme.BG_LIGHT);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(ModernTheme.FONT_REGULAR);
        cancelBtn.addActionListener(e -> dispose());

        JButton loginBtn = ModernTheme.createPrimaryButton("Authenticate ➔");
        loginBtn.addActionListener(e -> {
            String u = usernameField.getText().trim();
            String p = new String(passwordField.getPassword()).trim();

            if (("admin".equals(u) && "admin123".equals(p)) ||
                ("staff".equals(u) && "staff123".equals(p))) {
                authenticated = true;
                JOptionPane.showMessageDialog(this, "Authentication successful! Welcome, " + u + ".", "Access Granted", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials. Please use: admin / admin123", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        footerPanel.add(cancelBtn);
        footerPanel.add(loginBtn);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
