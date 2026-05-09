package com.cryptotool.ui;

import com.cryptotool.algorithms.symmetric.*;
import com.cryptotool.utils.FileUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Tab Mã Hóa Đối Xứng — hỗ trợ 6 thuật toán:
 * Caesar, Substitution, DES, 3DES, AES, Blowfish.
 *
 * UI tự thích nghi theo thuật toán được chọn:
 *   - AES       : hiện thêm Mode, Padding, Key size
 *   - 3DES      : hiện Key size (112/168-bit)
 *   - Blowfish  : hiện Key size (32–448-bit)
 *   - Caesar    : Key là số nguyên 1–25
 *   - Substitution : Key là chuỗi 26 ký tự hoán vị
 *   - DES       : Key size cố định 56-bit
 */
public class SymmetricPanel extends JPanel {

    // Danh sách thuật toán
    private static final String ALGO_CAESAR = "Caesar";
    private static final String ALGO_SUBST  = "Substitution";
    private static final String ALGO_DES    = "DES";
    private static final String ALGO_3DES   = "3DES";
    private static final String ALGO_AES    = "AES";
    private static final String ALGO_BLOW   = "Blowfish";

    private static final String[] ALGORITHMS = {
        ALGO_CAESAR, ALGO_SUBST, ALGO_DES, ALGO_3DES, ALGO_AES, ALGO_BLOW
    };

    // ── Components chọn thuật toán ──
    private JComboBox<String> algoCombo;
    private CardLayout        cardLayout;
    private JPanel            cardPanel;

    // ── Options cho AES ──
    private JComboBox<String> aesModeCombo;
    private JComboBox<String> aesPaddingCombo;
    private JComboBox<Integer> aesKeySizeCombo;

    // ── Options cho 3DES ──
    private JComboBox<String> triDesKeySizeCombo;

    // ── Options cho Blowfish ──
    private JComboBox<Integer> blowKeySizeCombo;

    // ── Vùng key ──
    private JTextField keyField;

    // ── Vùng văn bản ──
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JLabel    statusLabel;

    public SymmetricPanel() {
        setLayout(new BorderLayout(6, 6));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(buildNorthPanel(),  BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildStatusBar(),   BorderLayout.SOUTH);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // NORTH — Chọn thuật toán + tùy chọn + key
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildNorthPanel() {
        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(buildAlgoSelectorRow());
        north.add(Box.createVerticalStrut(4));
        north.add(buildOptionsCardPanel());
        north.add(Box.createVerticalStrut(4));
        north.add(buildKeyPanel());
        return north;
    }

    // Dòng chọn thuật toán
    private JPanel buildAlgoSelectorRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        JLabel lbl = new JLabel("Thuat toan:");
        lbl.setFont(new Font("Tahoma", Font.BOLD, 14));

        algoCombo = new JComboBox<>(ALGORITHMS);
        algoCombo.setFont(new Font("Tahoma", Font.PLAIN, 14));
        algoCombo.setPreferredSize(new Dimension(160, 30));
        algoCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onAlgorithmChanged();
            }
        });
        row.add(lbl);
        row.add(algoCombo);
        return row;
    }

    // Panel tùy chọn dùng CardLayout — thay đổi theo thuật toán
    private JPanel buildOptionsCardPanel() {
        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Tuy chon",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Tahoma", Font.BOLD, 12)));

        cardPanel.add(buildCaesarCard(),     ALGO_CAESAR);
        cardPanel.add(buildSubstCard(),      ALGO_SUBST);
        cardPanel.add(buildDesCard(),        ALGO_DES);
        cardPanel.add(build3DesCard(),       ALGO_3DES);
        cardPanel.add(buildAesCard(),        ALGO_AES);
        cardPanel.add(buildBlowfishCard(),   ALGO_BLOW);

        return cardPanel;
    }

    private JPanel buildCaesarCard() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        p.add(new JLabel("Key la so nguyen tu 1 den 25. K=3 la Caesar goc (A→D, B→E...)."));
        return p;
    }

    private JPanel buildSubstCard() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        p.add(new JLabel("Key la hoan vi 26 chu cai (A-Z), moi chu xuat hien dung 1 lan."));
        return p;
    }

    private JPanel buildDesCard() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        p.add(new JLabel("Key size: 56-bit (co dinh). Mode: CBC. Padding: PKCS5."));
        return p;
    }

    private JPanel build3DesCard() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        JLabel lbl = new JLabel("Key size:");
        lbl.setFont(new Font("Tahoma", Font.PLAIN, 13));
        triDesKeySizeCombo = new JComboBox<>(new String[]{"112-bit (2-key)", "168-bit (3-key)"});
        triDesKeySizeCombo.setFont(new Font("Tahoma", Font.PLAIN, 13));
        triDesKeySizeCombo.setSelectedIndex(1); // mặc định 168-bit
        p.add(lbl);
        p.add(triDesKeySizeCombo);
        p.add(new JLabel("  Mode: CBC | Padding: PKCS5"));
        return p;
    }

    private JPanel buildAesCard() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));

        // Mode
        p.add(makeLabel("Mode:"));
        aesModeCombo = new JComboBox<>(new String[]{
            AESAlgorithm.MODE_ECB, AESAlgorithm.MODE_CBC,
            AESAlgorithm.MODE_CFB, AESAlgorithm.MODE_OFB, AESAlgorithm.MODE_CTR
        });
        aesModeCombo.setSelectedItem(AESAlgorithm.MODE_CBC); // mặc định CBC
        aesModeCombo.setFont(new Font("Tahoma", Font.PLAIN, 13));
        aesModeCombo.setPreferredSize(new Dimension(80, 26));
        p.add(aesModeCombo);

        // Padding
        p.add(makeLabel("  Padding:"));
        aesPaddingCombo = new JComboBox<>(new String[]{
            AESAlgorithm.PADDING_PKCS5, AESAlgorithm.PADDING_NONE
        });
        aesPaddingCombo.setFont(new Font("Tahoma", Font.PLAIN, 13));
        aesPaddingCombo.setPreferredSize(new Dimension(130, 26));
        p.add(aesPaddingCombo);

        // Key size
        p.add(makeLabel("  Key size:"));
        aesKeySizeCombo = new JComboBox<>(new Integer[]{128, 192, 256});
        aesKeySizeCombo.setSelectedItem(256);
        aesKeySizeCombo.setFont(new Font("Tahoma", Font.PLAIN, 13));
        aesKeySizeCombo.setPreferredSize(new Dimension(70, 26));
        p.add(aesKeySizeCombo);
        p.add(makeLabel("bit"));
        return p;
    }

    private JPanel buildBlowfishCard() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        p.add(makeLabel("Key size:"));
        blowKeySizeCombo = new JComboBox<>(new Integer[]{32, 64, 128, 192, 256, 448});
        blowKeySizeCombo.setSelectedItem(128);
        blowKeySizeCombo.setFont(new Font("Tahoma", Font.PLAIN, 13));
        blowKeySizeCombo.setPreferredSize(new Dimension(70, 26));
        p.add(blowKeySizeCombo);
        p.add(makeLabel("bit  (32-448 bit, BouncyCastle provider)"));
        return p;
    }

    // Panel quản lý key
    private JPanel buildKeyPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 4));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Khoa (Key)",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Tahoma", Font.BOLD, 12)));

        keyField = new JTextField();
        keyField.setFont(new Font("Courier New", Font.PLAIN, 13));
        keyField.setToolTipText("Nhap key hoac nhan Generate");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        JButton genBtn  = makeButton("Generate Key");
        JButton saveBtn = makeButton("Save Key");
        JButton loadBtn = makeButton("Load Key");

        genBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { doGenerateKey(); }
        });
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { doSaveKey(); }
        });
        loadBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { doLoadKey(); }
        });

        btnPanel.add(genBtn);
        btnPanel.add(saveBtn);
        btnPanel.add(loadBtn);

        panel.add(keyField,  BorderLayout.CENTER);
        panel.add(btnPanel,  BorderLayout.EAST);
        return panel;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CENTER — Vùng nhập/xuất văn bản
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.insets  = new Insets(3, 2, 3, 2);

        // Label + Load File button cho vùng input
        JPanel inputHeader = new JPanel(new BorderLayout());
        inputHeader.add(makeBoldLabel("Van ban ro (Plaintext):"), BorderLayout.WEST);
        JButton loadFileBtn = makeButton("Load File...");
        loadFileBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { doLoadFile(); }
        });
        inputHeader.add(loadFileBtn, BorderLayout.EAST);
        gbc.gridy = 0; gbc.weighty = 0;
        panel.add(inputHeader, gbc);

        // Input textarea
        inputArea = new JTextArea(5, 40);
        inputArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        JScrollPane inputScroll = new JScrollPane(inputArea);
        gbc.gridy = 1; gbc.weighty = 0.45;
        panel.add(inputScroll, gbc);

        // Nút Encrypt / Decrypt
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buildActionButtons(), gbc);
        gbc.fill = GridBagConstraints.BOTH;

        // Label output
        gbc.gridy = 3; gbc.weighty = 0;
        panel.add(makeBoldLabel("Ban ma (Ciphertext - Base64):"), gbc);

        // Output textarea
        outputArea = new JTextArea(5, 40);
        outputArea.setFont(new Font("Courier New", Font.PLAIN, 13));
        outputArea.setLineWrap(true);
        outputArea.setBackground(new Color(245, 245, 245));
        JScrollPane outputScroll = new JScrollPane(outputArea);
        gbc.gridy = 4; gbc.weighty = 0.45;
        panel.add(outputScroll, gbc);

        // Nút Copy / Clear
        gbc.gridy = 5; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(buildCopyPanel(), gbc);

        return panel;
    }

    private JPanel buildActionButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 4));

        JButton encBtn = new JButton("  MA HOA (Encrypt)  ");
        encBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        encBtn.setBackground(new Color(34, 139, 34));
        encBtn.setForeground(Color.WHITE);
        encBtn.setFocusPainted(false);
        encBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { doEncrypt(); }
        });

        JButton decBtn = new JButton("  GIAI MA (Decrypt)  ");
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
        JButton copyBtn  = makeButton("Copy");
        JButton clearBtn = makeButton("Clear All");

        copyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String txt = outputArea.getText().trim();
                if (!txt.isEmpty()) {
                    Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new StringSelection(txt), null);
                    setStatus("Da copy ban ma vao clipboard.");
                }
            }
        });
        clearBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputArea.setText("");
                outputArea.setText("");
                keyField.setText("");
                setStatus("Da xoa.");
            }
        });
        p.add(copyBtn);
        p.add(clearBtn);
        return p;
    }

    private JLabel buildStatusBar() {
        statusLabel = new JLabel("  San sang.");
        statusLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        return statusLabel;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LOGIC XỬ LÝ
    // ═══════════════════════════════════════════════════════════════════════

    // Đổi thuật toán → chuyển card + xóa key cũ
    private void onAlgorithmChanged() {
        String algo = getSelectedAlgo();
        cardLayout.show(cardPanel, algo);
        keyField.setText("");
        outputArea.setText("");
        setStatus("Da chon: " + algo);
    }

    // Generate key theo thuật toán hiện tại
    private void doGenerateKey() {
        String algo = getSelectedAlgo();
        try {
            switch (algo) {
                case ALGO_CAESAR:
                    keyField.setText(String.valueOf(CaesarCipher.generateKey()));
                    break;
                case ALGO_SUBST:
                    keyField.setText(SubstitutionCipher.generateKey());
                    break;
                case ALGO_DES:
                    keyField.setText(DESAlgorithm.exportKeyToBase64(DESAlgorithm.generateKey()));
                    break;
                case ALGO_3DES:
                    int triSize = get3DesKeySize();
                    keyField.setText(TripleDESAlgorithm.exportKeyToBase64(
                        TripleDESAlgorithm.generateKey(triSize)));
                    break;
                case ALGO_AES:
                    int aesSize = (Integer) aesKeySizeCombo.getSelectedItem();
                    keyField.setText(AESAlgorithm.exportKeyToBase64(
                        AESAlgorithm.generateKey(aesSize)));
                    break;
                case ALGO_BLOW:
                    int blowSize = (Integer) blowKeySizeCombo.getSelectedItem();
                    keyField.setText(BlowfishAlgorithm.exportKeyToBase64(
                        BlowfishAlgorithm.generateKey(blowSize)));
                    break;
            }
            setStatus("Da tao key moi cho " + algo + ".");
        } catch (Exception ex) {
            showError("Loi tao key: " + ex.getMessage());
        }
    }

    // Lưu key ra file
    private void doSaveKey() {
        String keyText = keyField.getText().trim();
        if (keyText.isEmpty()) {
            showError("Chua co key de luu. Hay Generate hoac nhap key truoc.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Luu key ra file");
        fc.setSelectedFile(new File(getSelectedAlgo().toLowerCase() + "_key.txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                FileUtils.writeTextFile(fc.getSelectedFile(), keyText);
                setStatus("Da luu key vao: " + fc.getSelectedFile().getName());
            } catch (Exception ex) {
                showError("Loi luu key: " + ex.getMessage());
            }
        }
    }

    // Tải key từ file
    private void doLoadKey() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Tai key tu file");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String keyText = FileUtils.readTextFile(fc.getSelectedFile());
                keyField.setText(keyText.trim());
                setStatus("Da tai key tu: " + fc.getSelectedFile().getName());
            } catch (Exception ex) {
                showError("Loi tai key: " + ex.getMessage());
            }
        }
    }

    // Tải văn bản từ file
    private void doLoadFile() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Chon file van ban");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                inputArea.setText(FileUtils.readTextFile(fc.getSelectedFile()));
                setStatus("Da tai file: " + fc.getSelectedFile().getName());
            } catch (Exception ex) {
                showError("Loi doc file: " + ex.getMessage());
            }
        }
    }

    // Mã hóa
    private void doEncrypt() {
        String plain = inputArea.getText();
        String key   = keyField.getText().trim();
        if (plain.isEmpty()) { showError("Vui long nhap van ban can ma hoa."); return; }
        if (key.isEmpty())   { showError("Vui long nhap hoac Generate key truoc."); return; }

        String algo = getSelectedAlgo();
        try {
            String result;
            switch (algo) {
                case ALGO_CAESAR:
                    result = CaesarCipher.encrypt(plain, Integer.parseInt(key));
                    break;
                case ALGO_SUBST:
                    result = SubstitutionCipher.encrypt(plain, key);
                    break;
                case ALGO_DES:
                    result = DESAlgorithm.encrypt(plain, DESAlgorithm.importKeyFromBase64(key));
                    break;
                case ALGO_3DES:
                    result = TripleDESAlgorithm.encrypt(plain,
                        TripleDESAlgorithm.importKeyFromBase64(key));
                    break;
                case ALGO_AES:
                    result = AESAlgorithm.encrypt(plain,
                        AESAlgorithm.importKeyFromBase64(key),
                        (String)  aesModeCombo.getSelectedItem(),
                        (String)  aesPaddingCombo.getSelectedItem());
                    break;
                case ALGO_BLOW:
                    result = BlowfishAlgorithm.encrypt(plain,
                        BlowfishAlgorithm.importKeyFromBase64(key));
                    break;
                default:
                    return;
            }
            outputArea.setText(result);
            setStatus("Ma hoa thanh cong voi " + algo + ".");
        } catch (NumberFormatException ex) {
            showError("Key cua Caesar phai la so nguyen (1-25). Loi: " + ex.getMessage());
        } catch (Exception ex) {
            showError("Loi ma hoa (" + algo + "): " + ex.getMessage());
        }
    }

    // Giải mã
    private void doDecrypt() {
        String cipher = outputArea.getText().trim();
        String key    = keyField.getText().trim();
        if (cipher.isEmpty()) { showError("Chua co ban ma. Hay ma hoa truoc hoac dan ban ma vao o Ciphertext."); return; }
        if (key.isEmpty())    { showError("Vui long nhap hoac Load key truoc."); return; }

        // Cho phép giải mã từ ô output (ciphertext hiện tại)
        // hoặc nếu user muốn giải mã từ ô input thì swap
        String algo = getSelectedAlgo();
        try {
            String result;
            switch (algo) {
                case ALGO_CAESAR:
                    result = CaesarCipher.decrypt(cipher, Integer.parseInt(key));
                    break;
                case ALGO_SUBST:
                    result = SubstitutionCipher.decrypt(cipher, key);
                    break;
                case ALGO_DES:
                    result = DESAlgorithm.decrypt(cipher,
                        DESAlgorithm.importKeyFromBase64(key));
                    break;
                case ALGO_3DES:
                    result = TripleDESAlgorithm.decrypt(cipher,
                        TripleDESAlgorithm.importKeyFromBase64(key));
                    break;
                case ALGO_AES:
                    result = AESAlgorithm.decrypt(cipher,
                        AESAlgorithm.importKeyFromBase64(key),
                        (String) aesModeCombo.getSelectedItem(),
                        (String) aesPaddingCombo.getSelectedItem());
                    break;
                case ALGO_BLOW:
                    result = BlowfishAlgorithm.decrypt(cipher,
                        BlowfishAlgorithm.importKeyFromBase64(key));
                    break;
                default:
                    return;
            }
            inputArea.setText(result);
            setStatus("Giai ma thanh cong voi " + algo + ".");
        } catch (NumberFormatException ex) {
            showError("Key cua Caesar phai la so nguyen (1-25).");
        } catch (Exception ex) {
            showError("Loi giai ma (" + algo + "): " + ex.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    private String getSelectedAlgo() {
        return (String) algoCombo.getSelectedItem();
    }

    private int get3DesKeySize() {
        String sel = (String) triDesKeySizeCombo.getSelectedItem();
        return sel.startsWith("112") ? 112 : 168;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Loi", JOptionPane.ERROR_MESSAGE);
        setStatus("Loi: " + msg);
    }

    private void setStatus(String msg) {
        statusLabel.setText("  " + msg);
    }

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
