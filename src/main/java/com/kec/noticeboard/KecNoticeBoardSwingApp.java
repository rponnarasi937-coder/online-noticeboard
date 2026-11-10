package com.kec.noticeboard;

import com.kec.noticeboard.gui.MainFrame;

import javax.swing.*;

public class KecNoticeBoardSwingApp {

    public static void main(String[] args) {
        // Enable high-DPI scaling and smooth subpixel text antialiasing
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // fallback to default
            }

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
            System.out.println("==========================================================");
            System.out.println("   KEC ONLINE NOTICE BOARD - PURE JAVA APPLICATION RUNNING");
            System.out.println("   GUI Frame launched successfully!");
            System.out.println("==========================================================");
        });
    }
}
