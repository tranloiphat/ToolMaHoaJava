package com.cryptotool.utils;

import java.io.*;
import java.util.Base64;

/**
 * Tiện ích xử lý khóa mã hóa — dùng chung cho toàn bộ ứng dụng.
 *
 * Tại sao dùng Base64?
 *   - Khóa (key) thực chất là mảng byte[] nhị phân, không thể hiển thị trực tiếp.
 *   - Base64 chuyển byte[] → chuỗi ASCII an toàn để hiển thị, copy/paste, lưu file.
 *   - Đây là cách lưu key phổ biến nhất trong thực tế (PEM format cũng dùng Base64).
 */
public class KeyUtils {

    // -------------------------------------------------------------------------
    // ENCODE / DECODE BASE64
    // -------------------------------------------------------------------------

    /**
     * Chuyển mảng byte[] (key nhị phân) → chuỗi Base64 để hiển thị / lưu file.
     * Ví dụ: [0x3A, 0xF2, ...] → "OvI..."
     */
    public static String encodeKeyToBase64(byte[] keyBytes) {
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    /**
     * Chuyển chuỗi Base64 → mảng byte[] (key nhị phân) để dùng mã hóa.
     * Ném IllegalArgumentException nếu chuỗi không phải Base64 hợp lệ.
     */
    public static byte[] decodeKeyFromBase64(String base64Key) {
        return Base64.getDecoder().decode(base64Key.trim());
    }

    // -------------------------------------------------------------------------
    // LƯU / TẢI KEY TỪ FILE
    // -------------------------------------------------------------------------

    /**
     * Lưu key ra file text dạng Base64.
     * Dùng cho: symmetric key (AES, DES, ...), public key, private key.
     *
     * @param keyBytes  mảng byte của key
     * @param file      file đích (.key hoặc .txt)
     */
    public static void saveKeyToFile(byte[] keyBytes, File file) throws IOException {
        String base64 = encodeKeyToBase64(keyBytes);
        FileUtils.writeTextFile(file, base64);
    }

    /**
     * Tải key từ file text (đọc chuỗi Base64, decode về byte[]).
     *
     * @param file  file chứa key dạng Base64
     * @return mảng byte[] của key
     */
    public static byte[] loadKeyFromFile(File file) throws IOException {
        String base64 = FileUtils.readTextFile(file);
        return decodeKeyFromBase64(base64);
    }

    // -------------------------------------------------------------------------
    // TIỆN ÍCH HIỂN THỊ
    // -------------------------------------------------------------------------

    /**
     * Chuyển byte[] → chuỗi hex để hiển thị (dùng cho hash output).
     * Ví dụ: [0x3A, 0x0F] → "3a0f"
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            // Mỗi byte → 2 ký tự hex, %02x đảm bảo luôn có đủ 2 chữ số
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Encode dữ liệu bất kỳ (ciphertext) → Base64 để hiển thị trong UI.
     */
    public static String encodeToBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Decode Base64 → byte[] để giải mã.
     */
    public static byte[] decodeFromBase64(String base64) {
        return Base64.getDecoder().decode(base64.trim());
    }
}
