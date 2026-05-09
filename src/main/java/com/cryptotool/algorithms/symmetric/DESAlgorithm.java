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
 * DES — Data Encryption Standard (theo bài 3 bài giảng)
 *
 * Nguyên lý:
 *   - Thuật toán mã hóa khối (block cipher) theo cấu trúc Feistel.
 *   - Kích thước khối (block size): 64 bit (8 byte).
 *   - Kích thước khóa: 56 bit hiệu dụng (Java dùng 64-bit key, 8 bit dùng làm parity).
 *   - Số vòng lặp: 16 vòng Feistel.
 *   - Mode: CBC (Cipher Block Chaining) — mỗi khối được XOR với khối trước khi mã hóa.
 *   - Padding: PKCS5Padding — tự động thêm byte để đủ bội số 8 byte.
 *
 * Cấu trúc Feistel:
 *   1. Chia khối 64-bit thành 2 nửa L và R (mỗi nửa 32-bit)
 *   2. Mỗi vòng: L(i) = R(i-1), R(i) = L(i-1) XOR f(R(i-1), K(i))
 *   3. Sau 16 vòng: kết hợp L và R thành bản mã
 *
 * CBC Mode + IV:
 *   - IV (Initial Vector) 8 byte được sinh ngẫu nhiên mỗi lần mã hóa.
 *   - IV được ghép vào đầu ciphertext để decrypt có thể tách ra dùng.
 *   - Format lưu trữ: [IV (8 byte)] + [CipherText]
 *
 * Lưu ý: DES hiện nay KHÔNG còn an toàn (key 56-bit quá ngắn, có thể brute-force).
 * Chỉ dùng cho mục đích học tập.
 */
public class DESAlgorithm {

    private static final String ALGORITHM     = "DES";
    private static final String TRANSFORMATION = "DES/CBC/PKCS5Padding";
    private static final int    IV_SIZE        = 8;  // DES block size = 64 bit = 8 byte

    // -------------------------------------------------------------------------
    // SINH KHÓA
    // -------------------------------------------------------------------------

    /**
     * Sinh khóa DES ngẫu nhiên 56-bit (Java biểu diễn là 64-bit).
     *
     * @return byte[] chứa key DES
     */
    public static byte[] generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(56, new SecureRandom()); // 56-bit key
        SecretKey secretKey = keyGen.generateKey();
        return secretKey.getEncoded();
    }

    /**
     * Import key từ chuỗi Base64 (dán key có sẵn vào).
     *
     * @param base64Key chuỗi Base64 của key
     * @return byte[] chứa key
     */
    public static byte[] importKeyFromBase64(String base64Key) {
        return Base64.getDecoder().decode(base64Key.trim());
    }

    /**
     * Xuất key ra chuỗi Base64 để hiển thị / lưu file.
     */
    public static String exportKeyToBase64(byte[] keyBytes) {
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    // -------------------------------------------------------------------------
    // MÃ HÓA
    // -------------------------------------------------------------------------

    /**
     * Mã hóa văn bản bằng DES/CBC/PKCS5Padding.
     *
     * Các bước:
     *   1. Tạo SecretKey từ keyBytes
     *   2. Sinh IV ngẫu nhiên 8 byte
     *   3. Khởi tạo Cipher ở chế độ ENCRYPT với key + IV
     *   4. Mã hóa plaintext → ciphertext bytes
     *   5. Ghép [IV][CipherText] → encode Base64 để trả về
     *
     * @param plainText văn bản gốc (UTF-8)
     * @param keyBytes  key DES dạng byte[]
     * @return chuỗi Base64 của [IV + CipherText]
     */
    public static String encrypt(String plainText, byte[] keyBytes) throws Exception {
        SecretKey secretKey = new SecretKeySpec(keyBytes, ALGORITHM);

        // Sinh IV ngẫu nhiên
        byte[] ivBytes = new byte[IV_SIZE];
        new SecureRandom().nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        // Khởi tạo Cipher mã hóa
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        byte[] cipherBytes = cipher.doFinal(plainText.getBytes("UTF-8"));

        // Ghép IV vào đầu ciphertext để decrypt tách ra sau
        byte[] combined = new byte[IV_SIZE + cipherBytes.length];
        System.arraycopy(ivBytes,    0, combined, 0,       IV_SIZE);
        System.arraycopy(cipherBytes, 0, combined, IV_SIZE, cipherBytes.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    // -------------------------------------------------------------------------
    // GIẢI MÃ
    // -------------------------------------------------------------------------

    /**
     * Giải mã chuỗi Base64 (đã mã hóa bởi encrypt() ở trên).
     *
     * Các bước:
     *   1. Decode Base64 → byte[] combined
     *   2. Tách IV (8 byte đầu) và CipherText (phần còn lại)
     *   3. Khởi tạo Cipher ở chế độ DECRYPT với key + IV
     *   4. Giải mã → plaintext bytes → String UTF-8
     *
     * @param cipherBase64 chuỗi Base64 đã mã hóa
     * @param keyBytes     key DES dạng byte[] (phải khớp với khi mã hóa)
     * @return văn bản gốc
     */
    public static String decrypt(String cipherBase64, byte[] keyBytes) throws Exception {
        byte[] combined = Base64.getDecoder().decode(cipherBase64.trim());

        // Tách IV và ciphertext
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
