package com.cryptotool.algorithms.asymmetric;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA — Rivest–Shamir–Adleman (theo bài 5 bài giảng)
 *
 * Nguyên lý toán học:
 *   Bước 1: Chọn 2 số nguyên tố lớn p và q (giữ bí mật)
 *   Bước 2: Tính n = p × q  (modulus, công khai)
 *   Bước 3: Tính φ(n) = (p-1) × (q-1)  (Euler's totient, bí mật)
 *   Bước 4: Chọn e sao cho: 1 < e < φ(n) và gcd(e, φ(n)) = 1  (thường dùng e = 65537)
 *   Bước 5: Tính d sao cho: e × d ≡ 1 (mod φ(n))  (d là nghịch đảo modular của e)
 *
 *   → Public key  (n, e): dùng để MÃ HÓA và chia sẻ công khai
 *   → Private key (n, d): dùng để GIẢI MÃ và giữ bí mật tuyệt đối
 *
 * Công thức:
 *   Mã hóa:  C = M^e mod n
 *   Giải mã: M = C^d mod n
 *
 * Bảo mật dựa trên bài toán phân tích thừa số nguyên tố:
 *   - Biết n nhưng không biết p, q → rất khó tính φ(n) → không tính được d
 *   - Kích thước key càng lớn → càng khó phá (1024 không còn an toàn, dùng 2048+)
 *
 * Kích thước key được hỗ trợ: 1024 / 2048 / 3072 / 4096 bit
 *
 * Giới hạn kích thước input:
 *   RSA chỉ mã hóa được dữ liệu nhỏ hơn kích thước khóa.
 *   Với PKCS1Padding: max input = keySize/8 - 11 byte
 *     1024-bit → tối đa 117 byte   (~117 ký tự ASCII)
 *     2048-bit → tối đa 245 byte
 *     4096-bit → tối đa 501 byte
 *   Trong thực tế: RSA mã hóa symmetric key (AES), AES mã hóa dữ liệu thật.
 *   Class này tự chia nhỏ (chunk) để xử lý văn bản dài hơn giới hạn.
 *
 * Format lưu key:
 *   - Public key : X.509 encoded → Base64
 *   - Private key: PKCS8 encoded → Base64
 */
public class RSAAlgorithm {

    private static final String ALGORITHM      = "RSA";
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    // -------------------------------------------------------------------------
    // SINH CẶP KHÓA
    // -------------------------------------------------------------------------

    /**
     * Sinh cặp khóa RSA (Public Key + Private Key).
     *
     * @param keySize 1024, 2048, 3072, hoặc 4096 bit
     * @return KeyPair chứa publicKey và privateKey
     */
    public static KeyPair generateKeyPair(int keySize) throws Exception {
        if (keySize != 1024 && keySize != 2048 && keySize != 3072 && keySize != 4096) {
            throw new IllegalArgumentException(
                "Key size khong hop le: " + keySize +
                ". Chi ho tro 1024, 2048, 3072, 4096 bit.");
        }
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGen.initialize(keySize, new SecureRandom());
        return keyPairGen.generateKeyPair();
    }

    /**
     * Sinh cặp khóa RSA mặc định 2048-bit.
     */
    public static KeyPair generateKeyPair() throws Exception {
        return generateKeyPair(2048);
    }

    // -------------------------------------------------------------------------
    // XUẤT / NHẬP KEY (BASE64)
    // -------------------------------------------------------------------------

    /**
     * Xuất Public Key ra chuỗi Base64 (định dạng X.509).
     */
    public static String exportPublicKey(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * Xuất Private Key ra chuỗi Base64 (định dạng PKCS8).
     */
    public static String exportPrivateKey(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * Nhập Public Key từ chuỗi Base64.
     * Dùng X509EncodedKeySpec — đây là định dạng chuẩn của Public Key trong Java.
     */
    public static PublicKey importPublicKey(String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key.trim());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory   = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(spec);
    }

    /**
     * Nhập Private Key từ chuỗi Base64.
     * Dùng PKCS8EncodedKeySpec — định dạng chuẩn của Private Key trong Java.
     */
    public static PrivateKey importPrivateKey(String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key.trim());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory    = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(spec);
    }

    // -------------------------------------------------------------------------
    // MÃ HÓA (dùng Public Key)
    // -------------------------------------------------------------------------

    /**
     * Mã hóa văn bản bằng RSA Public Key.
     *
     * Vì RSA giới hạn kích thước input, method này tự chia văn bản thành
     * các chunk nhỏ, mã hóa từng chunk, ghép kết quả bằng dấu "|".
     *
     * @param plainText văn bản gốc
     * @param publicKey Public Key để mã hóa
     * @return chuỗi Base64 của ciphertext (các chunk phân tách bằng "|")
     */
    public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        // Tính kích thước key để biết giới hạn mỗi chunk
        // PKCS1Padding chiếm 11 byte → maxChunk = keySize/8 - 11
        int keySize  = ((java.security.interfaces.RSAPublicKey) publicKey).getModulus().bitLength();
        int maxChunk = keySize / 8 - 11;

        byte[] inputBytes = plainText.getBytes("UTF-8");
        StringBuilder result = new StringBuilder();

        // Chia thành từng chunk và mã hóa riêng
        for (int offset = 0; offset < inputBytes.length; offset += maxChunk) {
            int chunkLen   = Math.min(maxChunk, inputBytes.length - offset);
            byte[] chunk   = new byte[chunkLen];
            System.arraycopy(inputBytes, offset, chunk, 0, chunkLen);

            byte[] encrypted = cipher.doFinal(chunk);

            if (result.length() > 0) result.append("|");
            result.append(Base64.getEncoder().encodeToString(encrypted));
        }
        return result.toString();
    }

    // -------------------------------------------------------------------------
    // GIẢI MÃ (dùng Private Key)
    // -------------------------------------------------------------------------

    /**
     * Giải mã chuỗi đã mã hóa bằng RSA Private Key.
     *
     * @param cipherText chuỗi đã mã hóa (các chunk Base64 phân tách bằng "|")
     * @param privateKey Private Key để giải mã
     * @return văn bản gốc
     */
    public static String decrypt(String cipherText, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        // Tách từng chunk và giải mã riêng
        String[] chunks = cipherText.split("\\|");
        StringBuilder result = new StringBuilder();

        for (String chunk : chunks) {
            byte[] encryptedChunk = Base64.getDecoder().decode(chunk.trim());
            byte[] decryptedChunk = cipher.doFinal(encryptedChunk);
            result.append(new String(decryptedChunk, "UTF-8"));
        }
        return result.toString();
    }
}
