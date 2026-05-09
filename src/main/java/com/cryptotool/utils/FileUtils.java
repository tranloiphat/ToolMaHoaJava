package com.cryptotool.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Tiện ích đọc/ghi file — dùng chung cho toàn bộ ứng dụng.
 * Hỗ trợ 2 loại:
 *   - File text (.txt): đọc/ghi chuỗi ký tự UTF-8
 *   - File binary (.bin, .enc): đọc/ghi mảng byte (dùng cho ciphertext)
 */
public class FileUtils {

    // -------------------------------------------------------------------------
    // ĐỌC / GHI FILE TEXT
    // -------------------------------------------------------------------------

    /**
     * Đọc toàn bộ nội dung file text, trả về String.
     * Dùng UTF-8 để hỗ trợ tiếng Việt.
     */
    public static String readTextFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } finally {
            reader.close();
        }
        // Bỏ ký tự newline thừa ở cuối nếu có
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Ghi chuỗi String ra file text (UTF-8).
     * Nếu file đã tồn tại sẽ ghi đè.
     */
    public static void writeTextFile(File file, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        try {
            writer.write(content);
        } finally {
            writer.close();
        }
    }

    // -------------------------------------------------------------------------
    // ĐỌC / GHI FILE BINARY
    // -------------------------------------------------------------------------

    /**
     * Đọc toàn bộ file binary, trả về mảng byte[].
     * Dùng để đọc file đã mã hóa (ciphertext dạng byte thô).
     */
    public static byte[] readBinaryFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        try {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            return data;
        } finally {
            fis.close();
        }
    }

    /**
     * Ghi mảng byte[] ra file binary.
     * Dùng để lưu ciphertext sau khi mã hóa.
     */
    public static void writeBinaryFile(File file, byte[] data) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        try {
            fos.write(data);
        } finally {
            fos.close();
        }
    }

    // -------------------------------------------------------------------------
    // TIỆN ÍCH
    // -------------------------------------------------------------------------

    /**
     * Lấy phần mở rộng của file. Ví dụ: "hello.txt" → "txt"
     */
    public static String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot == -1) return "";
        return name.substring(lastDot + 1);
    }
}
