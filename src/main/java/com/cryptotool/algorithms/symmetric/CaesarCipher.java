package com.cryptotool.algorithms.symmetric;

import java.util.Random;

/**
 * CAESAR CIPHER — Mã hóa Caesar (mã hóa dịch chuyển)
 *
 * Nguyên lý (theo bài giảng):
 *   - Mỗi ký tự trong bản rõ được dịch chuyển đi k vị trí trong bảng chữ cái.
 *   - Caesar gốc dùng k = 3: A→D, B→E, ..., X→A, Y→B, Z→C
 *   - Công thức mã hóa:  C = (P + k) mod 26
 *   - Công thức giải mã: P = (C - k + 26) mod 26
 *
 * Ví dụ với k = 3:
 *   Bản rõ:    H  E  L  L  O
 *   Vị trí:    7  4  11 11 14
 *   +3 mod 26: 10 7  14 14 17
 *   Bản mã:    K  H  O  O  R  → "KHOOR"
 *
 * Đặc điểm:
 *   - Chỉ xử lý ký tự chữ cái (A-Z, a-z), ký tự khác (số, dấu) giữ nguyên.
 *   - Phân biệt hoa/thường: 'a' và 'A' được xử lý riêng.
 *   - Key là số nguyên k trong khoảng [1, 25].
 */
public class CaesarCipher {

    /**
     * Sinh key ngẫu nhiên: số nguyên k trong khoảng [1, 25].
     * k = 0 hoặc k = 26 không có nghĩa (bản rõ = bản mã).
     *
     * @return giá trị k ngẫu nhiên từ 1 đến 25
     */
    public static int generateKey() {
        // nextInt(25) trả về [0, 24], cộng 1 → [1, 25]
        return new Random().nextInt(25) + 1;
    }

    /**
     * Mã hóa bản rõ bằng Caesar Cipher.
     *
     * @param plainText  văn bản gốc (bản rõ)
     * @param k          khóa dịch chuyển (1–25)
     * @return bản mã sau khi dịch chuyển k vị trí
     */
    public static String encrypt(String plainText, int k) {
        validateKey(k);
        StringBuilder result = new StringBuilder();

        for (char c : plainText.toCharArray()) {
            if (Character.isUpperCase(c)) {
                // Chữ hoa: A=65, tính vị trí 0-25, dịch chuyển, đưa về ký tự hoa
                int pos = c - 'A';                    // vị trí trong bảng chữ cái (0-25)
                int shifted = (pos + k) % 26;         // dịch chuyển, mod 26 để quay vòng
                result.append((char) ('A' + shifted));

            } else if (Character.isLowerCase(c)) {
                // Chữ thường: a=97, xử lý tương tự
                int pos = c - 'a';
                int shifted = (pos + k) % 26;
                result.append((char) ('a' + shifted));

            } else {
                // Ký tự không phải chữ cái (số, dấu, khoảng trắng): giữ nguyên
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Giải mã bản mã bằng Caesar Cipher.
     * Giải mã = dịch ngược lại k vị trí (hoặc dịch thuận 26-k vị trí).
     *
     * @param cipherText bản mã cần giải mã
     * @param k          khóa dịch chuyển (phải khớp với khi mã hóa)
     * @return bản rõ gốc
     */
    public static String decrypt(String cipherText, int k) {
        validateKey(k);
        StringBuilder result = new StringBuilder();

        for (char c : cipherText.toCharArray()) {
            if (Character.isUpperCase(c)) {
                int pos = c - 'A';
                // Cộng 26 trước khi trừ k để tránh kết quả âm
                int shifted = (pos - k + 26) % 26;
                result.append((char) ('A' + shifted));

            } else if (Character.isLowerCase(c)) {
                int pos = c - 'a';
                int shifted = (pos - k + 26) % 26;
                result.append((char) ('a' + shifted));

            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Kiểm tra key hợp lệ: k phải trong khoảng [1, 25].
     */
    private static void validateKey(int k) {
        if (k < 1 || k > 25) {
            throw new IllegalArgumentException(
                "Key khong hop le: k = " + k + ". Key phai trong khoang [1, 25].");
        }
    }
}
