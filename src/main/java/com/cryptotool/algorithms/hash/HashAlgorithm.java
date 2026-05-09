package com.cryptotool.algorithms.hash;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * HASH ALGORITHMS — Các hàm băm mật mã (theo bài 6 bài giảng)
 *
 * Đặc điểm của hàm băm (Hash Function):
 *   1. Input bất kỳ (văn bản, file) → Output cố định (digest)
 *   2. Hàm 1 chiều: không thể tính ngược từ digest ra input gốc
 *   3. Kháng va chạm: khó tìm 2 input khác nhau cho ra cùng digest
 *   4. Thay đổi 1 bit input → digest thay đổi hoàn toàn (avalanche effect)
 *
 * Các thuật toán được hỗ trợ và kích thước output:
 *   - MD5     : 128 bit = 32 ký tự hex  (không còn an toàn, dùng kiểm tra toàn vẹn)
 *   - SHA-1   : 160 bit = 40 ký tự hex  (không còn an toàn cho chữ ký số)
 *   - SHA-224 : 224 bit = 56 ký tự hex  (biến thể rút gọn của SHA-256)
 *   - SHA-256 : 256 bit = 64 ký tự hex  (phổ biến nhất, dùng trong Bitcoin, TLS)
 *   - SHA-384 : 384 bit = 96 ký tự hex  (biến thể rút gọn của SHA-512)
 *   - SHA-512 : 512 bit = 128 ký tự hex (mạnh nhất trong họ SHA-2)
 *
 * Tất cả đều dùng java.security.MessageDigest (JCA built-in, không cần thư viện ngoài).
 */
public class HashAlgorithm {

    // Tên thuật toán chuẩn theo JCA — dùng trực tiếp trong MessageDigest.getInstance()
    public static final String MD5    = "MD5";
    public static final String SHA1   = "SHA-1";
    public static final String SHA224 = "SHA-224";
    public static final String SHA256 = "SHA-256";
    public static final String SHA384 = "SHA-384";
    public static final String SHA512 = "SHA-512";

    // Danh sách thuật toán hợp lệ để validate
    private static final String[] SUPPORTED = {MD5, SHA1, SHA224, SHA256, SHA384, SHA512};

    // -------------------------------------------------------------------------
    // BĂM VĂN BẢN
    // -------------------------------------------------------------------------

    /**
     * Tính hash của một chuỗi văn bản, trả về chuỗi hex.
     *
     * Cách hoạt động:
     *   1. Chuyển String → byte[] (UTF-8)
     *   2. Nạp vào MessageDigest
     *   3. Gọi digest() → nhận mảng byte kết quả
     *   4. Chuyển byte[] → hex string để hiển thị
     *
     * @param text      văn bản cần băm
     * @param algorithm tên thuật toán (dùng hằng số MD5, SHA256, ...)
     * @return chuỗi hex của digest
     */
    public static String hashText(String text, String algorithm)
            throws NoSuchAlgorithmException {
        validateAlgorithm(algorithm);

        // Khởi tạo engine băm với tên thuật toán
        MessageDigest md = MessageDigest.getInstance(algorithm);

        // Nạp dữ liệu vào engine (dùng UTF-8 để nhất quán)
        byte[] inputBytes = text.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        md.update(inputBytes);

        // Thực hiện băm và lấy kết quả
        byte[] digestBytes = md.digest();

        return bytesToHex(digestBytes);
    }

    // -------------------------------------------------------------------------
    // BĂM FILE
    // -------------------------------------------------------------------------

    /**
     * Tính hash của một file (đọc từng chunk 4KB để xử lý file lớn hiệu quả).
     *
     * @param file      file cần băm
     * @param algorithm tên thuật toán
     * @return chuỗi hex của digest
     */
    public static String hashFile(File file, String algorithm)
            throws NoSuchAlgorithmException, IOException {
        validateAlgorithm(algorithm);

        MessageDigest md = MessageDigest.getInstance(algorithm);
        FileInputStream fis = new FileInputStream(file);

        try {
            // Đọc file theo từng chunk 4096 byte để tránh OutOfMemory với file lớn
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                // update() nạp dần dữ liệu, không cần đọc hết file một lúc
                md.update(buffer, 0, bytesRead);
            }
        } finally {
            fis.close();
        }

        byte[] digestBytes = md.digest();
        return bytesToHex(digestBytes);
    }

    // -------------------------------------------------------------------------
    // TIỆN ÍCH
    // -------------------------------------------------------------------------

    /**
     * Chuyển mảng byte[] → chuỗi hex lowercase.
     * Ví dụ: [0x5d, 0x41, 0x40, 0x2a...] → "5d41402a..."
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            // %02x: in hex 2 chữ số, chữ thường, thêm 0 ở đầu nếu cần
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Trả về kích thước output (số bit) của mỗi thuật toán.
     * Dùng để hiển thị thông tin trong UI.
     */
    public static int getOutputBits(String algorithm) {
        switch (algorithm) {
            case MD5:    return 128;
            case SHA1:   return 160;
            case SHA224: return 224;
            case SHA256: return 256;
            case SHA384: return 384;
            case SHA512: return 512;
            default:     return -1;
        }
    }

    /**
     * Kiểm tra tên thuật toán có được hỗ trợ không.
     */
    private static void validateAlgorithm(String algorithm) {
        for (String supported : SUPPORTED) {
            if (supported.equals(algorithm)) return;
        }
        throw new IllegalArgumentException(
            "Thuat toan khong duoc ho tro: " + algorithm +
            ". Chi ho tro: MD5, SHA-1, SHA-224, SHA-256, SHA-384, SHA-512.");
    }
}
