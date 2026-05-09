package com.cryptotool.algorithms.symmetric;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * 3DES — Triple DES / Triple Data Encryption Standard (theo bài 3 bài giảng)
 *
 * Nguyên lý:
 *   - 3DES áp dụng DES 3 lần liên tiếp để tăng độ bảo mật.
 *   - Quy trình: Mã hóa(K1) → Giải mã(K2) → Mã hóa(K3)  [gọi là EDE: Encrypt-Decrypt-Encrypt]
 *   - Dùng EDE thay vì EEE để đảm bảo tương thích ngược với DES khi K1=K2=K3.
 *
 * Kích thước khóa (Java "DESede"):
 *   - 2-key 3DES: K1 ≠ K2, K3 = K1 → key 112-bit hiệu dụng (Java dùng 16 byte = 128 bit)
 *   - 3-key 3DES: K1 ≠ K2 ≠ K3    → key 168-bit hiệu dụng (Java dùng 24 byte = 192 bit)
 *
 * Kích thước khối: vẫn là 64-bit (8 byte) như DES.
 * Mode: CBC, Padding: PKCS5Padding, IV: 8 byte.
 *
 * Lưu ý: 3DES vẫn chậm hơn AES và đang bị loại bỏ dần. Dùng AES cho hệ thống mới.
 *
 * Format lưu trữ: [IV (8 byte)] + [CipherText] → encode Base64
 */
public class TripleDESAlgorithm {

    private static final String ALGORITHM      = "DESede";
    private static final String TRANSFORMATION = "DESede/CBC/PKCS5Padding";
    private static final int    IV_SIZE        = 8; // block size DES/3DES = 64 bit = 8 byte

    // -------------------------------------------------------------------------
    // SINH KHÓA
    // -------------------------------------------------------------------------

    /**
     * Sinh khóa 3DES ngẫu nhiên.
     *
     * @param keySize 112 (2-key) hoặc 168 (3-key)
     * @return byte[] chứa key (16 byte cho 112-bit, 24 byte cho 168-bit)
     */
    public static byte[] generateKey(int keySize) throws Exception {
        if (keySize != 112 && keySize != 168) {
            throw new IllegalArgumentException(
                "Key size khong hop le: " + keySize + ". Chi ho tro 112 hoac 168 bit.");
        }
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(keySize, new SecureRandom());
        SecretKey secretKey = keyGen.generateKey();
        return secretKey.getEncoded();
    }

    /**
     * Sinh khóa 3DES mặc định 168-bit (an toàn nhất).
     */
    public static byte[] generateKey() throws Exception {
        return generateKey(168);
    }

    /**
     * Import key từ chuỗi Base64.
     */
    public static byte[] importKeyFromBase64(String base64Key) {
        return Base64.getDecoder().decode(base64Key.trim());
    }

    /**
     * Xuất key ra chuỗi Base64.
     */
    public static String exportKeyToBase64(byte[] keyBytes) {
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    // -------------------------------------------------------------------------
    // MÃ HÓA
    // -------------------------------------------------------------------------

    /**
     * Mã hóa văn bản bằng 3DES/CBC/PKCS5Padding.
     * Cơ chế giống DES nhưng dùng key 112 hoặc 168-bit.
     *
     * @param plainText văn bản gốc
     * @param keyBytes  key 3DES (16 hoặc 24 byte)
     * @return chuỗi Base64 của [IV + CipherText]
     */
    public static String encrypt(String plainText, byte[] keyBytes) throws Exception {
        SecretKey secretKey = new SecretKeySpec(keyBytes, ALGORITHM);

        byte[] ivBytes = new byte[IV_SIZE];
        new SecureRandom().nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        byte[] cipherBytes = cipher.doFinal(plainText.getBytes("UTF-8"));

        // Ghép [IV][CipherText]
        byte[] combined = new byte[IV_SIZE + cipherBytes.length];
        System.arraycopy(ivBytes,     0, combined, 0,       IV_SIZE);
        System.arraycopy(cipherBytes, 0, combined, IV_SIZE, cipherBytes.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    // -------------------------------------------------------------------------
    // GIẢI MÃ
    // -------------------------------------------------------------------------

    /**
     * Giải mã chuỗi Base64 bằng 3DES.
     *
     * @param cipherBase64 chuỗi Base64 đã mã hóa
     * @param keyBytes     key 3DES (phải khớp khi mã hóa)
     * @return văn bản gốc
     */
    public static String decrypt(String cipherBase64, byte[] keyBytes) throws Exception {
        byte[] combined = Base64.getDecoder().decode(cipherBase64.trim());

        // Tách IV (8 byte đầu) và phần còn lại là ciphertext
        byte[] ivBytes     = Arrays.copyOfRange(combined, 0, IV_SIZE);
        byte[] cipherBytes = Arrays.copyOfRange(combined, IV_SIZE, combined.length);

        SecretKey secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
        IvParameterSpec iv  = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        byte[] plainBytes = cipher.doFinal(cipherBytes);
        return new String(plainBytes, "UTF-8");
    }
}
