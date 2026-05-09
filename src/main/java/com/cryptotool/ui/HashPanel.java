package com.cryptotool.ui;

import com.cryptotool.algorithms.hash.HashAlgorithm;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Tab Hàm Băm — giao diện để tính hash cho văn bản và file.
 */
public class HashPanel extends JPanel {

    // Các thuật toán hash được hỗ trợ
    private static final String[] ALGORITHMS = {
        HashAlgorithm.MD5,
        HashAlgorithm.SHA1,
        HashAlgorithm.SHA224,
        HashAlgorithm.SHA256,
        HashAlgorithm.SHA384,
        HashAlgorithm.SHA512
    };

    // Components
    private JComboBox<String> algoCombo;
    private JLabel            algoInfoLabel;
    private JTextArea         inputArea;
    private JTextArea         outputArea;
    private JLabel            statusLabel;

    public HashPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildTopPanel(),    BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildStatusBar(),   BorderLayout.SOUTH);
    }

    // ── Panel chọn thuật toán ──────────────────────────────────────────────
    private JPanel buildTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Thuat toan",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Tahoma", Font.BOLD, 13)));

        JLabel algoLabel = new JLabel("Chon thuat toan:");
        algoLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));

        algoCombo = new JComboBox<>(ALGORITHMS);
        algoCombo.setFont(new Font("Tahoma", Font.PLAIN, 14));
        algoCombo.setPreferredSize(new Dimension(160, 30));

        algoInfoLabel = new JLabel();
        algoInfoLabel.setFont(new Font("Tahoma", Font.ITALIC, 13));
        algoInfoLabel.setForeground(new Color(0, 100, 0));

        // Cập nhật info khi đổi thuật toán
        algoCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateAlgoInfo();
            }
        });
        updateAlgoInfo(); // hiển thị info cho thuật toán mặc định

        panel.add(algoLabel);
        panel.add(algoCombo);
        panel.add(algoInfoLabel);
        return panel;
    }

    // ── Panel trung tâm: input + output ───────────────────────────────────
    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(4, 4, 4, 4);
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // ── Vùng nhập liệu ──
        JLabel inputLabel = new JLabel("Van ban dau vao:");
        inputLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        gbc.gridy = 0; gbc.weighty = 0;
        panel.add(inputLabel, gbc);

        inputArea = new JTextArea(6, 40);
        inputArea.setFont(new Font("Tahoma", Font.PLAIN, 14));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        gbc.gridy = 1; gbc.weighty = 0.4;
        panel.add(inputScroll, gbc);

        // ── Nút Hash ──
        gbc.gridy = 2; gbc.weighty = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buildActionButtons(), gbc);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;

        // ── Vùng kết quả ──
        JLabel outputLabel = new JLabel("Ket qua (Hex):");
        outputLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        gbc.gridy = 3; gbc.weighty = 0;
        panel.add(outputLabel, gbc);

        outputArea = new JTextArea(4, 40);
        outputArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(245, 245, 245));
        outputArea.setLineWrap(true);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        gbc.gridy = 4; gbc.weighty = 0.6;
        panel.add(outputScroll, gbc);

        // ── Nút Copy/Clear ──
        gbc.gridy = 5; gbc.weighty = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(buildOutputButtons(), gbc);

        return panel;
    }

    // Nút "Tinh Hash" và "Hash File..."
    private JPanel buildActionButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));

        JButton hashBtn = new JButton("  Tinh Hash  ");
        hashBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        hashBtn.setBackground(new Color(59, 130, 246));
        hashBtn.setForeground(Color.WHITE);
        hashBtn.setFocusPainted(false);
        hashBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performHash();
            }
        });

        JButton fileBtn = new JButton("  Hash File...  ");
        fileBtn.setFont(new Font("Tahoma", Font.PLAIN, 13));
        fileBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performHashFile();
            }
        });

        panel.add(hashBtn);
        panel.add(fileBtn);
        return panel;
    }

    // Nút "Copy" và "Clear"
    private JPanel buildOutputButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));

        JButton copyBtn = new JButton("Copy");
        copyBtn.setFont(new Font("Tahoma", Font.PLAIN, 13));
        copyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = outputArea.getText().trim();
                if (!text.isEmpty()) {
                    Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new StringSelection(text), null);
                    setStatus("Da copy ket qua vao clipboard!");
                }
            }
        });

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Tahoma", Font.PLAIN, 13));
        clearBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputArea.setText("");
                outputArea.setText("");
                setStatus("Da xoa.");
            }
        });

        panel.add(copyBtn);
        panel.add(clearBtn);
        return panel;
    }

    private JLabel buildStatusBar() {
        statusLabel = new JLabel("  San sang.");
        statusLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        return statusLabel;
    }

    // ── Logic xử lý ────────────────────────────────────────────────────────

    // Tính hash của văn bản trong inputArea
    private void performHash() {
        String input = inputArea.getText();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui long nhap van ban can tinh hash.",
                "Canh bao", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String algo = (String) algoCombo.getSelectedItem();
        try {
            String result = HashAlgorithm.hashText(input, algo);
            outputArea.setText(result);
            setStatus("Hash thanh cong! " + algo + " | " +
                HashAlgorithm.getOutputBits(algo) + " bit | " +
                result.length() + " ky tu hex");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Loi khi tinh hash: " + ex.getMessage(),
                "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Chọn file và tính hash của file đó
    private void performHashFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chon file can tinh hash");
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File selectedFile = chooser.getSelectedFile();
        String algo = (String) algoCombo.getSelectedItem();
        try {
            setStatus("Dang tinh hash file: " + selectedFile.getName() + "...");
            String hashResult = HashAlgorithm.hashFile(selectedFile, algo);
            outputArea.setText(hashResult);
            inputArea.setText("[File] " + selectedFile.getAbsolutePath());
            setStatus("Hash file thanh cong! " + selectedFile.getName() +
                " | " + algo + " | " + hashResult.length() + " ky tu hex");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Loi khi hash file: " + ex.getMessage(),
                "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Cập nhật label thông tin thuật toán (số bit output)
    private void updateAlgoInfo() {
        String algo = (String) algoCombo.getSelectedItem();
        int bits = HashAlgorithm.getOutputBits(algo);
        algoInfoLabel.setText("→ Output: " + bits + " bit  (" + (bits / 4) + " ky tu hex)");
    }

    private void setStatus(String message) {
        statusLabel.setText("  " + message);
    }
}
