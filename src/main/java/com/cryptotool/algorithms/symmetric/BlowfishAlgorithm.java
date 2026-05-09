package com.cryptotool.algorithms.symmetric;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;

/**
 * BLOWFISH — Thuật toán mã hóa dùng thư viện BouncyCastle (theo yêu cầu đề bài)
 *
 * Tại sao dùng BouncyCastle?
 *   - JDK tích hợp sẵn không hỗ trợ Blowfish đầy đủ.
 *   - BouncyCastle là thư viện mã hóa mã nguồn mở phổ biến nhất cho Java,
 *     được dùng rộng rãi trong thực tế (Android, Spring Security, ...).
 *   - Đây là 1 trong 2 thuật toán dùng "thư viện hỗ trợ" theo yêu cầu đề bài.
 *
 * Nguyên lý Blowfish (theo bài giảng):
 *   - Thuật toán mã hóa khối (block cipher) theo cấu trúc Feistel.
 *   - Kích thước khối: 64 bit (8 byte) — giống DES.
 *   - Kích thước khóa: 32 đến 448 bit (4 đến 56 byte) — rất linh hoạt.
 *   - Số vòng Feistel: 16 vòng.
 *   - Thiết kế bởi Bruce Schneier năm 1993, là lựa chọn thay thế nhanh cho DES.
 *   - Đặc điểm nổi bật: key schedule phức tạp (tốn 4KB bộ nhớ), tốc độ cao
 *     sau khi khởi tạo nhưng chậm khi thay đổi key thường xuyên.
 *
 * Cấu trúc Feistel của Blowfish:
 *   1. Chia khối 64-bit thành L và R (mỗi 32-bit)
 *   2. 16 vòng: L = L XOR P[i], R = R XOR F(L), rồi hoán đổi L↔R
 *   3. F function: chia L thành 4 byte a,b,c,d → (S[0][a] + S[1][b] XOR S[2][c]) + S[3][d]
 *   4. P-array (18 subkey 32-bit) + 4 S-box (256×32-bit) được khởi tạo từ key
 *
 * Mode: CBC, Padding: PKCS5Padding, IV: 8 byte.
 * Format: Base64([IV (8 byte)] + [CipherText])
 */
public class BlowfishAlgorithm {

    private static final String ALGORITHM      = "Blowfish";
    private static final String TRANSFORMATION = "Blowfish/CBC/PKCS5Padding";
    private static final int    IV_SIZE        = 8; // Blowfish block size = 64 bit = 8 byte

    // Đăng ký BouncyCastle provider khi class được load lần đầu.
    // Nếu chưa có thì thêm vào, nếu đã có rồi thì bỏ qua (tránh duplicate).
    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    // -------------------------------------------------------------------------
    // SINH KHÓA
    // -------------------------------------------------------------------------

    /**
     * Sinh khóa Blowfish ngẫu nhiên.
     * Blowfish cho phép key 32–448 bit, các giá trị phổ biến: 128, 192, 256.
     *
     * @param keyBits kích thước key theo bit (phải là bội số của 8, trong [32, 448])
     * @return byte[] chứa key
     */
    public static byte[] generateKey(int keyBits) {
        if (keyBits < 32 || keyBits > 448 || keyBits % 8 != 0) {
            throw new IllegalArgumentException(
                "Key size khong hop le: " + keyBits +
                " bit. Blowfish ho tro 32-448 bit, boi so cua 8.");
        }
        byte[] keyBytes = new byte[keyBits / 8];
        new SecureRandom().nextBytes(keyBytes);
        return keyBytes;
    }

    /**
     * Sinh khóa Blowfish mặc định 128-bit.
     */
    public static byte[] generateKey() {
        return generateKey(128);
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
     * Mã hóa văn bản bằng Blowfish/CBC/PKCS5Padding qua BouncyCastle.
     *
     * Lưu ý kỹ thuật:
     *   - Dùng SecretKeySpec với tên "Blowfish" để JCA tạo đúng loại key.
     *   - Provider "BC" = BouncyCastle — được đăng ký trong static block ở trên.
     *   - Cipher.getInstance(transformation, "BC") chỉ định rõ dùng BouncyCastle.
     *
     * @param plainText văn bản gốc
     * @param keyBytes  key Blowfish (4–56 byte)
     * @return chuỗi Base64 của [IV + CipherText]
     */
    public static String encrypt(String plainText, byte[] keyBytes) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);

        byte[] ivBytes = new byte[IV_SIZE];
        new SecureRandom().nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        // Chỉ định rõ provider "BC" = BouncyCastle
        Cipher cipher = Cipher.getInstance(TRANSFORMATION, "BC");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        byte[] cipherBytes = cipher.doFinal(plainText.getBytes("UTF-8"));

        // Ghép [IV (8 byte)][CipherText]
        byte[] combined = new byte[IV_SIZE + cipherBytes.length];
        System.arraycopy(ivBytes,     0, combined, 0,       IV_SIZE);
        System.arraycopy(cipherBytes, 0, combined, IV_SIZE, cipherBytes.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    // -------------------------------------------------------------------------
    // GIẢI MÃ
    // -------------------------------------------------------------------------

    /**
     * Giải mã chuỗi Base64 bằng Blowfish qua BouncyCastle.
     *
     * @param cipherBase64 chuỗi Base64 đã mã hóa
     * @param keyBytes     key Blowfish (phải khớp khi mã hóa)
     * @return văn bản gốc
     */
    public static String decrypt(String cipherBase64, byte[] keyBytes) throws Exception {
        byte[] combined = Base64.getDecoder().decode(cipherBase64.trim());

        // Tách IV (8 byte đầu) và ciphertext
        byte[] ivBytes     = Arrays.copyOfRange(combined, 0, IV_SIZE);
        byte[] cipherBytes = Arrays.copyOfRange(combined, IV_SIZE, combined.length);

        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
        IvParameterSpec iv      = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION, "BC");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        byte[] plainBytes = cipher.doFinal(cipherBytes);
        return new String(plainBytes, "UTF-8");
    }
}
