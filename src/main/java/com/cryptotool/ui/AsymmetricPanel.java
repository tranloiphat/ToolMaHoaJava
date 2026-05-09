package com.cryptotool.ui;

import com.cryptotool.algorithms.asymmetric.RSAAlgorithm;
import com.cryptotool.utils.FileUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Tab Mã Hóa Bất Đối Xứng — RSA.
 *
 * Luồng sử dụng:
 *   1. Chọn key size → Generate Key Pair → Public + Private Key tự điền
 *   2. (Tùy chọn) Save/Load từng key ra file riêng
 *   3. Nhập văn bản → MA HOA bằng Public Key → ra Base64
 *   4. Dán Base64 → GIAI MA bằng Private Key → ra văn bản gốc
 *
 * Lưu ý bảo mật:
 *   - Public Key: chia sẻ công khai — dùng để MÃ HÓA
 *   - Private Key: GIỮ BÍ MẬT tuyệt đối — dùng để GIẢI MÃ
 */
public class AsymmetricPanel extends JPanel {

    private static final Integer[] KEY_SIZES = {1024, 2048, 3072, 4096};

    // Key management components
    private JComboBox<Integer> keySizeCombo;
    private JTextArea          publicKeyArea;
    private JTextArea          privateKeyArea;

    // IO components
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JLabel    statusLabel;

    public AsymmetricPanel() {
        setLayout(new BorderLayout(6, 6));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(buildNorthPanel(),  BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildStatusBar(),   BorderLayout.SOUTH);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // NORTH — Quản lý key
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildNorthPanel() {
        JPanel north = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.insets  = new Insets(3, 2, 3, 2);

        // Dòng chọn key size + Generate
        gbc.gridy = 0; gbc.weighty = 0;
        north.add(buildGenerateRow(), gbc);

        // Public Key
        gbc.gridy = 1;
        north.add(buildPublicKeyPanel(), gbc);

        // Private Key
        gbc.gridy = 2;
        north.add(buildPrivateKeyPanel(), gbc);

        return north;
    }

    private JPanel buildGenerateRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        row.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Sinh cap khoa RSA",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Tahoma", Font.BOLD, 12)));

        row.add(makeLabel("Key size:"));

        keySizeCombo = new JComboBox<>(KEY_SIZES);
        keySizeCombo.setSelectedItem(2048);
        keySizeCombo.setFont(new Font("Tahoma", Font.PLAIN, 14));
        keySizeCombo.setPreferredSize(new Dimension(90, 30));
        row.add(keySizeCombo);
        row.add(makeLabel("bit"));

        JButton genBtn = new JButton("  Generate Key Pair  ");
        genBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        genBtn.setBackground(new Color(59, 130, 246));
        genBtn.setForeground(Color.WHITE);
        genBtn.setFocusPainted(false);
        genBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { doGenerateKeyPair(); }
        });
        row.add(genBtn);

        JLabel noteLabel = new JLabel("  (Key lon hon se mat nhieu thoi gian hon)");
        noteLabel.setFont(new Font("Tahoma", Font.ITALIC, 12));
        noteLabel.setForeground(Color.GRAY);
        row.add(noteLabel);

        return row;
    }

    private JPanel buildPublicKeyPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 2));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Public Key (dung de MA HOA — co the chia se cong khai)",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Tahoma", Font.BOLD, 12)));
        panel.getBorder();

        publicKeyArea = buildKeyArea();
        panel.add(new JScrollPane(publicKeyArea), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        JButton saveBtn = makeButton("Save Public Key");
        JButton loadBtn = makeButton("Load Public Key");
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveKeyToFile(publicKeyArea.getText().trim(), "public_key.txt", "Public Key");
            }
        });
        loadBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadKeyFromFile(publicKeyArea, "Public Key");
            }
        });
        btnPanel.add(saveBtn);
        btnPanel.add(loadBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildPrivateKeyPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 2));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Private Key (dung de GIAI MA — GIU BI MAT!)",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Tahoma", Font.BOLD, 12)));

        privateKeyArea = buildKeyArea();
        privateKeyArea.setBackground(new Color(255, 250, 235)); // nền vàng nhạt = cảnh báo bí mật
        panel.add(new JScrollPane(privateKeyArea), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        JButton saveBtn = makeButton("Save Private Key");
        JButton loadBtn = makeButton("Load Private Key");
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveKeyToFile(privateKeyArea.getText().trim(), "private_key.txt", "Private Key");
            }
        });
        loadBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadKeyFromFile(privateKeyArea, "Private Key");
            }
        });
        btnPanel.add(saveBtn);
        btnPanel.add(loadBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CENTER — Vùng nhập/xuất văn bản
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.insets  = new Insets(3, 2, 2, 2);

        // Input label
        gbc.gridy = 0; gbc.weighty = 0;
        panel.add(makeBoldLabel("Van ban ro (Plaintext) / Ban ma (Ciphertext):"), gbc);

        // Input area
        inputArea = new JTextArea(4, 40);
        inputArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        gbc.gridy = 1; gbc.weighty = 0.4;
        panel.add(new JScrollPane(inputArea), gbc);

        // Action buttons
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buildActionButtons(), gbc);
        gbc.fill = GridBagConstraints.BOTH;

        // Output label
        gbc.gridy = 3; gbc.weighty = 0;
        panel.add(makeBoldLabel("Ket qua:"), gbc);

        // Output area
        outputArea = new JTextArea(4, 40);
        outputArea.setFont(new Font("Courier New", Font.PLAIN, 13));
        outputArea.setLineWrap(true);
        outputArea.setBackground(new Color(245, 245, 245));
        gbc.gridy = 4; gbc.weighty = 0.6;
        panel.add(new JScrollPane(outputArea), gbc);

        // Copy/Clear
        gbc.gridy = 5; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(buildCopyPanel(), gbc);

        return panel;
    }

    private JPanel buildActionButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 4));

        JButton encBtn = new JButton("  MA HOA (Public Key)  ");
        encBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        encBtn.setBackground(new Color(34, 139, 34));
        encBtn.setForeground(Color.WHITE);
        encBtn.setFocusPainted(false);
        encBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { doEncrypt(); }
        });

        JButton decBtn = new JButton("  GIAI MA (Private Key)  ");
        decBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        decBtn.setBackground(new Color(180, 90, 0));
        decBtn.setForeground(Color.WHITE);
        decBtn.setFocusPainted(false);
        decBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { doDecrypt(); }
        });

        p.add(encBtn);
        p.add(decBtn);
        return p;
    }

    private JPanel buildCopyPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));

        JButton copyBtn  = makeButton("Copy Ket Qua");
        JButton clearBtn = makeButton("Clear All");

        copyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String txt = outputArea.getText().trim();
                if (!txt.isEmpty()) {
                    Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new StringSelection(txt), null);
                    setStatus("Da copy ket qua vao clipboard.");
                }
            }
        });
        clearBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputArea.setText("");
                outputArea.setText("");
                publicKeyArea.setText("");
                privateKeyArea.setText("");
                setStatus("Da xoa tat ca.");
            }
        });
        p.add(copyBtn);
        p.add(clearBtn);
        return p;
    }

    private JLabel buildStatusBar() {
        statusLabel = new JLabel("  San sang. Hay Generate Key Pair truoc.");
        statusLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        return statusLabel;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LOGIC XỬ LÝ
    // ═══════════════════════════════════════════════════════════════════════

    private void doGenerateKeyPair() {
        int keySize = (Integer) keySizeCombo.getSelectedItem();
        setStatus("Dang sinh cap khoa RSA " + keySize + "-bit, vui long cho...");

        // Chạy trên background thread để không đơ UI (4096-bit có thể mất vài giây)
        SwingWorker<KeyPair, Void> worker = new SwingWorker<KeyPair, Void>() {
            protected KeyPair doInBackground() throws Exception {
                return RSAAlgorithm.generateKeyPair(keySize);
            }
            protected void done() {
                try {
                    KeyPair pair = get();
                    publicKeyArea.setText(RSAAlgorithm.exportPublicKey(pair.getPublic()));
                    privateKeyArea.setText(RSAAlgorithm.exportPrivateKey(pair.getPrivate()));
                    setStatus("Da sinh cap khoa RSA " + keySize + "-bit thanh cong!");
                } catch (Exception ex) {
                    showError("Loi sinh key: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void doEncrypt() {
        String plain  = inputArea.getText().trim();
        String pubKey = publicKeyArea.getText().trim();

        if (plain.isEmpty())  { showError("Vui long nhap van ban can ma hoa."); return; }
        if (pubKey.isEmpty()) { showError("Chua co Public Key. Hay Generate hoac Load Public Key."); return; }

        try {
            PublicKey key    = RSAAlgorithm.importPublicKey(pubKey);
            String    result = RSAAlgorithm.encrypt(plain, key);
            outputArea.setText(result);
            setStatus("Ma hoa RSA thanh cong! Do dai ban ma: " + result.length() + " ky tu.");
        } catch (Exception ex) {
            showError("Loi ma hoa RSA: " + ex.getMessage());
        }
    }

    private void doDecrypt() {
        String cipher   = outputArea.getText().trim();
        String privKey  = privateKeyArea.getText().trim();

        // Nếu ô output rỗng, thử lấy từ ô input (user có thể paste ciphertext vào input)
        if (cipher.isEmpty()) cipher = inputArea.getText().trim();

        if (cipher.isEmpty())  { showError("Chua co ban ma. Hay ma hoa truoc hoac dan ban ma vao o Ket qua."); return; }
        if (privKey.isEmpty()) { showError("Chua co Private Key. Hay Generate hoac Load Private Key."); return; }

        try {
            PrivateKey key    = RSAAlgorithm.importPrivateKey(privKey);
            String     result = RSAAlgorithm.decrypt(cipher, key);
            inputArea.setText(result);
            setStatus("Giai ma RSA thanh cong!");
        } catch (Exception ex) {
            showError("Loi giai ma RSA: " + ex.getMessage());
        }
    }

    private void saveKeyToFile(String keyText, String defaultName, String keyType) {
        if (keyText.isEmpty()) {
            showError("Chua co " + keyType + ". Hay Generate truoc.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Luu " + keyType);
        fc.setSelectedFile(new File(defaultName));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                FileUtils.writeTextFile(fc.getSelectedFile(), keyText);
                setStatus("Da luu " + keyType + " vao: " + fc.getSelectedFile().getName());
            } catch (Exception ex) {
                showError("Loi luu file: " + ex.getMessage());
            }
        }
    }

    private void loadKeyFromFile(JTextArea targetArea, String keyType) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Tai " + keyType + " tu file");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String content = FileUtils.readTextFile(fc.getSelectedFile());
                targetArea.setText(content.trim());
                setStatus("Da tai " + keyType + " tu: " + fc.getSelectedFile().getName());
            } catch (Exception ex) {
                showError("Loi tai file: " + ex.getMessage());
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    private JTextArea buildKeyArea() {
        JTextArea area = new JTextArea(3, 40);
        area.setFont(new Font("Courier New", Font.PLAIN, 11));
        area.setLineWrap(true);
        area.setWrapStyleWord(false);
        return area;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Loi", JOptionPane.ERROR_MESSAGE);
        setStatus("Loi: " + msg);
    }

    private void setStatus(String msg) { statusLabel.setText("  " + msg); }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Tahoma", Font.PLAIN, 13));
        return l;
    }

    private JLabel makeBoldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Tahoma", Font.BOLD, 13));
        return l;
    }

    private JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Tahoma", Font.PLAIN, 13));
        return b;
    }
}
