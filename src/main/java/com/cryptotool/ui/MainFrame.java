package com.cryptotool.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Cửa sổ chính của ứng dụng Tool Mã Hóa Java.
 * Chứa JTabbedPane với 3 tab tương ứng 3 nhóm chức năng.
 */
public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;

    public MainFrame() {
        initComponents();
        setupMenuBar();
        applySettings();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 14));

        // Tab 1: Hàm Băm (đơn giản nhất, làm trước)
        tabbedPane.addTab("  Ham Bam  ", new HashPanel());

        // Tab 2: Mã hóa đối xứng
        tabbedPane.addTab("  Ma Hoa Doi Xung  ", new SymmetricPanel());

        // Tab 3: placeholder — sẽ được thay bằng panel thật ở giai đoạn 12
        tabbedPane.addTab("  Ma Hoa Bat Doi Xung  ", buildPlaceholder("AsymmetricPanel - Giai doan 12"));

        add(tabbedPane, BorderLayout.CENTER);
    }

    // Panel tạm thời cho tab chưa làm
    private JPanel buildPlaceholder(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Tahoma", Font.ITALIC, 16));
        label.setForeground(Color.GRAY);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu helpMenu = new JMenu("Tro Giup");
        helpMenu.setFont(new Font("Tahoma", Font.PLAIN, 13));

        JMenuItem aboutItem = new JMenuItem("Gioi Thieu");
        aboutItem.setFont(new Font("Tahoma", Font.PLAIN, 13));
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });

        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void showAboutDialog() {
        String message =
            "Tool Ma Hoa Java - v1.0\n\n" +
            "Do an giua ky mon: An toan bao mat he thong\n" +
            "Truong: DH Nong Lam TP.HCM\n" +
            "Giang vien: Phan Dinh Long\n\n" +
            "Cac thuat toan ho tro:\n" +
            "  Ma hoa doi xung : Caesar, Substitution, DES, 3DES, AES, Blowfish\n" +
            "  Ma hoa bat doi xung: RSA (1024/2048/3072/4096-bit)\n" +
            "  Ham bam: MD5, SHA-1, SHA-224, SHA-256, SHA-384, SHA-512";

        JOptionPane.showMessageDialog(
            this, message, "Gioi Thieu", JOptionPane.INFORMATION_MESSAGE);
    }

    private void applySettings() {
        setTitle("Tool Ma Hoa Java - v1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 640);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null); // căn giữa màn hình
        setLayout(new BorderLayout());

        // Status bar ở dưới cùng
        JLabel statusBar = new JLabel("  San sang / Ready");
        statusBar.setFont(new Font("Tahoma", Font.PLAIN, 12));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        add(statusBar, BorderLayout.SOUTH);
    }
}
