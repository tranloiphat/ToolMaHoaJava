package com.cryptotool;

import com.cryptotool.ui.MainFrame;

import javax.swing.*;

/**
 * Entry point — Giai doan 10: khoi chay giao dien Swing.
 */
public class Main {

    public static void main(String[] args) {
        // Chạy UI trên Event Dispatch Thread (EDT) — quy tắc bắt buộc của Swing
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Dùng Look and Feel của hệ điều hành để UI trông tự nhiên hơn
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    // Nếu không set được thì dùng mặc định của Java cũng không sao
                }

                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            }
        });
    }
}
