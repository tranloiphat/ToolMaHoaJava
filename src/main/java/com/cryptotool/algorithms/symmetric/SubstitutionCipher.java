package com.cryptotool.algorithms.symmetric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SUBSTITUTION CIPHER — Mã hóa thay thế (hoán vị bảng chữ cái)
 *
 * Nguyên lý (theo bài giảng):
 *   - Mỗi ký tự trong bảng chữ cái gốc được ánh xạ 1-1 sang một ký tự khác.
 *   - Key là một hoán vị (permutation) của 26 chữ cái — không trùng, không thiếu.
 *   - Ví dụ key = "QWERTYUIOPASDFGHJKLZXCVBNM":
 *       A→Q, B→W, C→E, D→R, E→T, F→Y, G→U, H→I, I→O, J→P, ...
 *
 *   Bảng tra mã hóa:
 *       Bảng gốc (plain):  A B C D E F G H I J K L M N O P Q R S T U V W X Y Z
 *       Bảng mã (cipher):  Q W E R T Y U I O P A S D F G H J K L Z X C B N M V
 *
 *   Mã hóa: tra vị trí của ký tự trong bảng gốc → lấy ký tự tương ứng trong bảng mã
 *   Giải mã: tra vị trí của ký tự trong bảng mã → lấy ký tự tương ứng trong bảng gốc
 *
 * Đặc điểm:
 *   - Không gian khóa: 26! ≈ 4 × 10^26 (rất lớn so với Caesar chỉ có 25 khóa)
 *   - Vẫn dễ bị tấn công bằng phân tích tần suất (frequency analysis)
 *   - Chỉ xử lý chữ cái A-Z, a-z; ký tự khác giữ nguyên
 */
public class SubstitutionCipher {

    // Bảng chữ cái chuẩn — dùng làm bảng tham chiếu
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * Sinh key ngẫu nhiên: hoán vị ngẫu nhiên của 26 chữ cái in hoa.
     * Ví dụ: "QWERTYUIOPASDFGHJKLZXCVBNM"
     *
     * @return chuỗi 26 ký tự, mỗi chữ cái xuất hiện đúng 1 lần
     */
    public static String generateKey() {
        // Tạo danh sách 26 ký tự A-Z rồi xáo trộn ngẫu nhiên
        List<Character> chars = new ArrayList<>();
        for (char c : ALPHABET.toCharArray()) {
            chars.add(c);
        }
        Collections.shuffle(chars);

        StringBuilder key = new StringBuilder();
        for (char c : chars) {
            key.append(c);
        }
        return key.toString();
    }

    /**
     * Mã hóa bản rõ bằng Substitution Cipher.
     *
     * Cách hoạt động:
     *   1. Tìm vị trí của ký tự trong bảng gốc ALPHABET (A=0, B=1, ..., Z=25)
     *   2. Lấy ký tự tại vị trí đó trong bảng mã (key)
     *
     * @param plainText  văn bản gốc
     * @param key        chuỗi 26 ký tự hoán vị (bảng thay thế)
     * @return bản mã
     */
    public static String encrypt(String plainText, String key) {
        validateKey(key);
        String keyUpper = key.toUpperCase();
        StringBuilder result = new StringBuilder();

        for (char c : plainText.toCharArray()) {
            if (Character.isUpperCase(c)) {
                // Tìm vị trí trong ALPHABET → lấy ký tự tương ứng trong key
                int index = ALPHABET.indexOf(c);
                result.append(keyUpper.charAt(index));

            } else if (Character.isLowerCase(c)) {
                // Chữ thường: chuyển lên hoa để tra bảng, kết quả trả về thường
                int index = ALPHABET.indexOf(Character.toUpperCase(c));
                result.append(Character.toLowerCase(keyUpper.charAt(index)));

            } else {
                // Ký tự đặc biệt, số, khoảng trắng: giữ nguyên
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Giải mã bản mã bằng Substitution Cipher.
     *
     * Cách hoạt động (ngược với mã hóa):
     *   1. Tìm vị trí của ký tự trong bảng mã (key)
     *   2. Lấy ký tự tại vị trí đó trong bảng gốc ALPHABET
     *
     * @param cipherText bản mã cần giải mã
     * @param key        chuỗi 26 ký tự hoán vị (phải khớp với khi mã hóa)
     * @return bản rõ gốc
     */
    public static String decrypt(String cipherText, String key) {
        validateKey(key);
        String keyUpper = key.toUpperCase();
        StringBuilder result = new StringBuilder();

        for (char c : cipherText.toCharArray()) {
            if (Character.isUpperCase(c)) {
                // Tìm vị trí của c trong bảng key → lấy ký tự tương ứng trong ALPHABET
                int index = keyUpper.indexOf(c);
                result.append(ALPHABET.charAt(index));

            } else if (Character.isLowerCase(c)) {
                int index = keyUpper.indexOf(Character.toUpperCase(c));
                result.append(Character.toLowerCase(ALPHABET.charAt(index)));

            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Kiểm tra key hợp lệ:
     *   - Phải đúng 26 ký tự
     *   - Phải là hoán vị của A-Z (mỗi chữ xuất hiện đúng 1 lần)
     */
    public static void validateKey(String key) {
        if (key == null || key.length() != 26) {
            throw new IllegalArgumentException(
                "Key khong hop le: phai co dung 26 ky tu, hien tai co " +
                (key == null ? 0 : key.length()) + " ky tu.");
        }
        String keyUpper = key.toUpperCase();
        for (char c : ALPHABET.toCharArray()) {
            if (keyUpper.indexOf(c) == -1) {
                throw new IllegalArgumentException(
                    "Key khong hop le: thieu ky tu '" + c + "'. Key phai la hoan vi cua A-Z.");
            }
        }
    }
}
