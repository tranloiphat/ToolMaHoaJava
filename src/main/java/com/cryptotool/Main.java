package com.cryptotool;

import com.cryptotool.utils.FileUtils;
import com.cryptotool.utils.KeyUtils;

import java.io.File;

/**
 * Entry point — dùng để verify từng giai đoạn.
 * Giai đoạn 2: test FileUtils và KeyUtils.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Giai doan 2: Utility Classes ===\n");

        testFileUtils();
        testKeyUtils();

        System.out.println("\n=== Tat ca utils hoat dong chinh xac! ===");
    }

    // Kiểm tra đọc/ghi file text và binary
    private static void testFileUtils() {
        System.out.println("[FileUtils] Test doc/ghi file text...");
        try {
            File tempFile = File.createTempFile("cryptotool_test_", ".txt");
            tempFile.deleteOnExit();

            String noiDung = "Hello CryptoTool!\nDong thu 2 tieng Viet: xin chao";
            FileUtils.writeTextFile(tempFile, noiDung);

            String docLai = FileUtils.readTextFile(tempFile);
            boolean ok = noiDung.equals(docLai);
            System.out.println("  Ghi roi doc lai: " + (ok ? "PASS" : "FAIL"));

            System.out.println("[FileUtils] Test doc/ghi file binary...");
            File binFile = File.createTempFile("cryptotool_test_", ".bin");
            binFile.deleteOnExit();

            byte[] data = {0x41, 0x42, 0x43, (byte) 0xFF, 0x00, 0x1A};
            FileUtils.writeBinaryFile(binFile, data);

            byte[] docLaiBin = FileUtils.readBinaryFile(binFile);
            boolean binOk = java.util.Arrays.equals(data, docLaiBin);
            System.out.println("  Ghi/doc binary: " + (binOk ? "PASS" : "FAIL"));

            System.out.println("[FileUtils] Extension: " +
                    FileUtils.getFileExtension(new File("hello.txt")));

        } catch (Exception e) {
            System.out.println("  LOI: " + e.getMessage());
        }
    }

    // Kiểm tra encode/decode Base64 và lưu/tải key
    private static void testKeyUtils() {
        System.out.println("\n[KeyUtils] Test encode/decode Base64...");
        try {
            // Giả lập 1 key AES-128 (16 byte ngẫu nhiên)
            byte[] fakeKey = new byte[16];
            new java.util.Random().nextBytes(fakeKey);

            String base64 = KeyUtils.encodeKeyToBase64(fakeKey);
            System.out.println("  Key (Base64): " + base64);

            byte[] decoded = KeyUtils.decodeKeyFromBase64(base64);
            boolean ok = java.util.Arrays.equals(fakeKey, decoded);
            System.out.println("  Encode -> Decode: " + (ok ? "PASS" : "FAIL"));

            System.out.println("[KeyUtils] Test save/load key tu file...");
            File keyFile = File.createTempFile("cryptotool_key_", ".key");
            keyFile.deleteOnExit();

            KeyUtils.saveKeyToFile(fakeKey, keyFile);
            byte[] loadedKey = KeyUtils.loadKeyFromFile(keyFile);
            boolean fileOk = java.util.Arrays.equals(fakeKey, loadedKey);
            System.out.println("  Save/Load key file: " + (fileOk ? "PASS" : "FAIL"));

            System.out.println("[KeyUtils] Test bytesToHex...");
            byte[] sample = {0x3A, 0x0F, (byte) 0xAB, (byte) 0xFF};
            System.out.println("  Hex: " + KeyUtils.bytesToHex(sample) + " (expected: 3a0fabff)");

        } catch (Exception e) {
            System.out.println("  LOI: " + e.getMessage());
        }
    }
}
