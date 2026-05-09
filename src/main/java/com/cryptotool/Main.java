package com.cryptotool;

import com.cryptotool.algorithms.symmetric.BlowfishAlgorithm;

/**
 * Entry point — Giai doan 8: test Blowfish (BouncyCastle).
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Giai doan 8: Blowfish (BouncyCastle) ===\n");

        testBlowfishDefault();
        testBlowfishKeySizes();
        testImportKey();

        System.out.println("\n=== Blowfish hoat dong chinh xac! ===");
    }

    // Test cơ bản với key 128-bit mặc định
    private static void testBlowfishDefault() {
        System.out.println("[Blowfish] Test key 128-bit (mac dinh):");
        try {
            byte[] key       = BlowfishAlgorithm.generateKey();
            String keyB64    = BlowfishAlgorithm.exportKeyToBase64(key);
            String plain     = "Hello Blowfish - BouncyCastle Provider!";

            String cipher    = BlowfishAlgorithm.encrypt(plain, key);
            String decrypted = BlowfishAlgorithm.decrypt(cipher, key);

            System.out.println("  Provider  : BouncyCastle (BC)");
            System.out.println("  Key B64   : " + keyB64);
            System.out.println("  Key size  : " + key.length * 8 + " bit");
            System.out.println("  Ban ro    : " + plain);
            System.out.println("  Ban ma    : " + cipher);
            System.out.println("  Giai ma   : " + decrypted);
            System.out.println("  Ket qua   : " + (plain.equals(decrypted) ? "PASS" : "FAIL"));
        } catch (Exception e) {
            System.out.println("  LOI: " + e.getMessage());
        }
    }

    // Test nhiều kích thước key: 32, 128, 256, 448 bit
    private static void testBlowfishKeySizes() {
        System.out.println("\n[Blowfish] Test cac key size:");
        int[] keySizes = {32, 128, 256, 448};
        String plain   = "Test Blowfish key sizes";

        for (int keyBits : keySizes) {
            try {
                byte[] key       = BlowfishAlgorithm.generateKey(keyBits);
                String cipher    = BlowfishAlgorithm.encrypt(plain, key);
                String decrypted = BlowfishAlgorithm.decrypt(cipher, key);
                boolean ok       = plain.equals(decrypted);

                System.out.printf("  %3d bit: %s%n", keyBits, ok ? "PASS" : "FAIL");
            } catch (Exception e) {
                System.out.printf("  %3d bit: LOI - %s%n", keyBits, e.getMessage());
            }
        }
    }

    // Test import key từ Base64
    private static void testImportKey() {
        System.out.println("\n[Blowfish] Test import key tu Base64:");
        try {
            byte[] originalKey = BlowfishAlgorithm.generateKey(256);
            String keyB64      = BlowfishAlgorithm.exportKeyToBase64(originalKey);
            byte[] importedKey = BlowfishAlgorithm.importKeyFromBase64(keyB64);

            String plain     = "Import key test";
            String cipher    = BlowfishAlgorithm.encrypt(plain, originalKey);
            String decrypted = BlowfishAlgorithm.decrypt(cipher, importedKey);

            System.out.println("  Key B64   : " + keyB64);
            System.out.println("  Giai ma   : " + (plain.equals(decrypted) ? "PASS" : "FAIL"));
        } catch (Exception e) {
            System.out.println("  LOI: " + e.getMessage());
        }
    }
}
