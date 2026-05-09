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
 * AES — Advanced Encryption Standard (theo bài 4 bài giảng)
 *
 * Nguyên lý:
 *   - Thuật toán mã hóa khối (block cipher) thay thế DES từ năm 2001.
 *   - Kích thước khối cố định: 128 bit (16 byte).
 *   - Kích thước khóa: 128 / 192 / 256 bit.
 *   - Số vòng lặp: 10 (128-bit), 12 (192-bit), 14 (256-bit).
 *   - Cấu trúc: SubBytes → ShiftRows → MixColumns → AddRoundKey (mỗi vòng).
 *
 * Các Mode hoạt động được hỗ trợ (theo bài 4):
 *
 *   ECB (Electronic Codebook):
 *     - Mỗi khối được mã hóa độc lập với cùng key.
 *     - Không dùng IV. Đơn giản nhưng KHÔNG AN TOÀN: khối giống nhau → bản mã giống nhau.
 *
 *   CBC (Cipher Block Chaining):
 *     - Mỗi khối được XOR với bản mã khối trước rồi mới mã hóa.
 *     - C(i) = Encrypt(P(i) XOR C(i-1)), C(0) = IV.
 *     - An toàn hơn ECB, phổ biến nhất. Cần IV ngẫu nhiên.
 *
 *   CFB (Cipher Feedback):
 *     - Biến block cipher thành stream cipher.
 *     - C(i) = P(i) XOR Encrypt(C(i-1)), C(0) = IV.
 *     - Phù hợp mã hóa dữ liệu không đủ 1 khối.
 *
 *   OFB (Output Feedback):
 *     - Giống CFB nhưng XOR với output của Encrypt thay vì ciphertext.
 *     - Keystream không phụ thuộc plaintext → có thể tính trước.
 *     - O(i) = Encrypt(O(i-1)), C(i) = P(i) XOR O(i), O(0) = IV.
 *
 *   CTR (Counter):
 *     - Mã hóa một bộ đếm (counter) tăng dần, XOR với plaintext.
 *     - C(i) = P(i) XOR Encrypt(Nonce || Counter(i)).
 *     - Cho phép mã hóa song song (parallelizable), tốc độ nhanh.
 *
 * Format lưu trữ:
 *   - ECB: Base64([CipherText])
 *   - Các mode khác: Base64([IV (16 byte)] + [CipherText])
 */
public class AESAlgorithm {

    private static final String ALGORITHM = "AES";
    private static final int    IV_SIZE   = 16; // AES block size = 128 bit = 16 byte

    // Hằng số mode — dùng trong UI và khi gọi encrypt/decrypt
    public static final String MODE_ECB = "ECB";
    public static final String MODE_CBC = "CBC";
    public static final String MODE_CFB = "CFB";
    public static final String MODE_OFB = "OFB";
    public static final String MODE_CTR = "CTR";

    // Hằng số padding
    public static final String PADDING_PKCS5 = "PKCS5Padding";
    public static final String PADDING_NONE  = "NoPadding";

    // -------------------------------------------------------------------------
    // SINH KHÓA
    // -------------------------------------------------------------------------

    /**
     * Sinh khóa AES ngẫu nhiên.
     *
     * @param keySize 128, 192, hoặc 256 (bit)
     * @return byte[] chứa key AES
     */
    public static byte[] generateKey(int keySize) throws Exception {
        if (keySize != 128 && keySize != 192 && keySize != 256) {
            throw new IllegalArgumentException(
                "Key size khong hop le: " + keySize + ". Chi ho tro 128, 192, 256 bit.");
        }
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(keySize, new SecureRandom());
        return keyGen.generateKey().getEncoded();
    }

    /**
     * Sinh khóa AES mặc định 256-bit.
     */
    public static byte[] generateKey() throws Exception {
        return generateKey(256);
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
     * Mã hóa văn bản bằng AES với mode và padding tùy chọn.
     *
     * @param plainText văn bản gốc
     * @param keyBytes  key AES (16/24/32 byte tương ứng 128/192/256-bit)
     * @param mode      MODE_ECB / MODE_CBC / MODE_CFB / MODE_OFB / MODE_CTR
     * @param padding   PADDING_PKCS5 / PADDING_NONE
     * @return chuỗi Base64 của [IV + CipherText] (hoặc chỉ CipherText nếu ECB)
     */
    public static String encrypt(String plainText, byte[] keyBytes,
                                 String mode, String padding) throws Exception {
        String transformation = ALGORITHM + "/" + mode + "/" + padding;
        SecretKey secretKey   = new SecretKeySpec(keyBytes, ALGORITHM);
        Cipher cipher         = Cipher.getInstance(transformation);

        byte[] cipherBytes;

        if (MODE_ECB.equals(mode)) {
            // ECB không dùng IV
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            cipherBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(cipherBytes);

        } else {
            // CBC, CFB, OFB, CTR — cần IV 16 byte
            byte[] ivBytes = new byte[IV_SIZE];
            new SecureRandom().nextBytes(ivBytes);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            cipherBytes = cipher.doFinal(plainText.getBytes("UTF-8"));

            // Ghép [IV (16 byte)][CipherText]
            byte[] combined = new byte[IV_SIZE + cipherBytes.length];
            System.arraycopy(ivBytes,     0, combined, 0,       IV_SIZE);
            System.arraycopy(cipherBytes, 0, combined, IV_SIZE, cipherBytes.length);

            return Base64.getEncoder().encodeToString(combined);
        }
    }

    // -------------------------------------------------------------------------
    // GIẢI MÃ
    // -------------------------------------------------------------------------

    /**
     * Giải mã chuỗi Base64 bằng AES.
     *
     * @param cipherBase64 chuỗi Base64 đã mã hóa
     * @param keyBytes     key AES (phải khớp khi mã hóa)
     * @param mode         mode dùng khi mã hóa
     * @param padding      padding dùng khi mã hóa
     * @return văn bản gốc
     */
    public static String decrypt(String cipherBase64, byte[] keyBytes,
                                 String mode, String padding) throws Exception {
        String transformation = ALGORITHM + "/" + mode + "/" + padding;
        SecretKey secretKey   = new SecretKeySpec(keyBytes, ALGORITHM);
        Cipher cipher         = Cipher.getInstance(transformation);

        byte[] combined = Base64.getDecoder().decode(cipherBase64.trim());

        if (MODE_ECB.equals(mode)) {
            // ECB: toàn bộ combined là ciphertext
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] plainBytes = cipher.doFinal(combined);
            return new String(plainBytes, "UTF-8");

        } else {
            // Tách IV (16 byte đầu) và ciphertext (phần còn lại)
            byte[] ivBytes     = Arrays.copyOfRange(combined, 0, IV_SIZE);
            byte[] cipherBytes = Arrays.copyOfRange(combined, IV_SIZE, combined.length);

            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            byte[] plainBytes = cipher.doFinal(cipherBytes);
            return new String(plainBytes, "UTF-8");
        }
    }
}
