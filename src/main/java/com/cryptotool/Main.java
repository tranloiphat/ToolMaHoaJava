package com.cryptotool;

import com.cryptotool.algorithms.symmetric.DESAlgorithm;
import com.cryptotool.algorithms.symmetric.TripleDESAlgorithm;

/**
 * Entry point — Giai doan 6: test DES va 3DES.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Giai doan 6: DES & 3DES ===\n");

        testDES();
        testTripleDES112();
        testTripleDES168();
        testImportKey();

        System.out.println("\n=== DES & 3DES hoat dong chinh xac! ===");
    }

    // Test DES cơ bản: generate key → encrypt → decrypt
    private static void testDES() {
        System.out.println("[DES] Test co ban (CBC/PKCS5Padding):");
        try {
            byte[] key      = DESAlgorithm.generateKey();
            String keyB64   = DESAlgorithm.exportKeyToBase64(key);
            String plain    = "Hello DES World!";

            String cipher   = DESAlgorithm.encrypt(plain, key);
            String decrypted = DESAlgorithm.decrypt(cipher, key);

            System.out.println("  Key (Base64) : " + keyB64);
            System.out.println("  Key size     : " + key.length + " bytes (" + (key.length * 8) + " bit)");
            System.out.println("  Ban ro       : " + plain);
            System.out.println("  Ban ma (B64) : " + cipher);
            System.out.println("  Giai ma      : " + decrypted);
            System.out.println("  Ket qua      : " + (plain.equals(decrypted) ? "PASS" : "FAIL"));
        } catch (Exception e) {
            System.out.println("  LOI: " + e.getMessage());
        }
    }

    // Test 3DES với key 112-bit (2-key)
    private static void testTripleDES112() {
        System.out.println("\n[3DES] Test key 112-bit (2-key EDE):");
        try {
            byte[] key       = TripleDESAlgorithm.generateKey(112);
            String plain     = "Hello Triple DES!";

            String cipher    = TripleDESAlgorithm.encrypt(plain, key);
            String decrypted = TripleDESAlgorithm.decrypt(cipher, key);

            System.out.println("  Key size : " + key.length + " bytes (" + (key.length * 8) + " bit)");
            System.out.println("  Ban ro   : " + plain);
            System.out.println("  Ban ma   : " + cipher);
            System.out.println("  Giai ma  : " + decrypted);
            System.out.println("  Ket qua  : " + (plain.equals(decrypted) ? "PASS" : "FAIL"));
        } catch (Exception e) {
            System.out.println("  LOI: " + e.getMessage());
        }
    }

    // Test 3DES với key 168-bit (3-key) — mặc định
    private static void testTripleDES168() {
        System.out.println("\n[3DES] Test key 168-bit (3-key EDE):");
        try {
            byte[] key       = TripleDESAlgorithm.generateKey(168);
            String plain     = "Bao mat he thong - DH Nong Lam TPHCM";

            String cipher    = TripleDESAlgorithm.encrypt(plain, key);
            String decrypted = TripleDESAlgorithm.decrypt(cipher, key);

            System.out.println("  Key size : " + key.length + " bytes (" + (key.length * 8) + " bit)");
            System.out.println("  Ban ro   : " + plain);
            System.out.println("  Ban ma   : " + cipher);
            System.out.println("  Giai ma  : " + decrypted);
            System.out.println("  Ket qua  : " + (plain.equals(decrypted) ? "PASS" : "FAIL"));
        } catch (Exception e) {
            System.out.println("  LOI: " + e.getMessage());
        }
    }

    // Test import key từ Base64 (giả lập user dán key vào)
    private static void testImportKey() {
        System.out.println("\n[DES] Test import key tu Base64:");
        try {
            // Sinh key gốc
            byte[] originalKey = DESAlgorithm.generateKey();
            String keyB64      = DESAlgorithm.exportKeyToBase64(originalKey);

            // Giả lập import lại từ chuỗi Base64
            byte[] importedKey  = DESAlgorithm.importKeyFromBase64(keyB64);
            String plain        = "Test import key";

            String cipher       = DESAlgorithm.encrypt(plain, originalKey);
            // Giải mã bằng key đã import — phải ra đúng bản gốc
            String decrypted    = DESAlgorithm.decrypt(cipher, importedKey);

            System.out.println("  Key B64 goc   : " + keyB64);
            System.out.println("  Key da import : " + DESAlgorithm.exportKeyToBase64(importedKey));
            System.out.println("  Giai ma dung  : " + (plain.equals(decrypted) ? "PASS" : "FAIL"));
        } catch (Exception e) {
            System.out.println("  LOI: " + e.getMessage());
        }
    }
}
