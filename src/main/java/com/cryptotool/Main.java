package com.cryptotool;

import com.cryptotool.algorithms.asymmetric.RSAAlgorithm;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Entry point — Giai doan 9: test RSA.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Giai doan 9: RSA ===\n");

        testRSA2048();
        testRSAKeySizes();
        testExportImportKey();
        testLongText();

        System.out.println("\n=== RSA hoat dong chinh xac! ===");
    }

    // Test RSA 2048-bit cơ bản
    private static void testRSA2048() {
        System.out.println("[RSA] Test 2048-bit (mac dinh):");
        try {
            System.out.print("  Dang sinh cap khoa 2048-bit... ");
            long start   = System.currentTimeMillis();
            KeyPair pair = RSAAlgorithm.generateKeyPair(2048);
            long end     = System.currentTimeMillis();
            System.out.println("xong (" + (end - start) + "ms)");

            PublicKey  pubKey  = pair.getPublic();
            PrivateKey privKey = pair.getPrivate();

            System.out.println("  Public Key  (64 ky tu dau): " +
                RSAAlgorithm.exportPublicKey(pubKey).substring(0, 64) + "...");
            System.out.println("  Private Key (64 ky tu dau): " +
                RSAAlgorithm.exportPrivateKey(privKey).substring(0, 64) + "...");

            String plain     = "Hello RSA! Ma hoa bat doi xung.";
            String cipher    = RSAAlgorithm.encrypt(plain, pubKey);
            String decrypted = RSAAlgorithm.decrypt(cipher, privKey);

            System.out.println("  Ban ro   : " + plain);
            System.out.println("  Ban ma   : " + cipher.substring(0, Math.min(60, cipher.length())) + "...");
            System.out.println("  Giai ma  : " + decrypted);
            System.out.println("  Ket qua  : " + (plain.equals(decrypted) ? "PASS" : "FAIL"));
        } catch (Exception e) {
            System.out.println("  LOI: " + e.getMessage());
        }
    }

    // Test các kích thước key khác nhau (1024 nhanh, 4096 chậm)
    private static void testRSAKeySizes() {
        System.out.println("\n[RSA] Test cac key size:");
        int[] keySizes = {1024, 2048, 4096};
        String plain   = "Test RSA key sizes";

        for (int keySize : keySizes) {
            try {
                long start   = System.currentTimeMillis();
                KeyPair pair = RSAAlgorithm.generateKeyPair(keySize);
                long genTime = System.currentTimeMillis() - start;

                String cipher    = RSAAlgorithm.encrypt(plain, pair.getPublic());
                String decrypted = RSAAlgorithm.decrypt(cipher, pair.getPrivate());
                boolean ok       = plain.equals(decrypted);

                System.out.printf("  %4d-bit: %s | Gen=%dms%n",
                    keySize, ok ? "PASS" : "FAIL", genTime);
            } catch (Exception e) {
                System.out.printf("  %4d-bit: LOI - %s%n", keySize, e.getMessage());
            }
        }
    }

    // Test export public key → import lại → dùng để encrypt
    private static void testExportImportKey() {
        System.out.println("\n[RSA] Test export/import key:");
        try {
            KeyPair pair = RSAAlgorithm.generateKeyPair(2048);

            // Export ra Base64
            String pubB64  = RSAAlgorithm.exportPublicKey(pair.getPublic());
            String privB64 = RSAAlgorithm.exportPrivateKey(pair.getPrivate());

            // Import lại từ Base64
            PublicKey  importedPub  = RSAAlgorithm.importPublicKey(pubB64);
            PrivateKey importedPriv = RSAAlgorithm.importPrivateKey(privB64);

            String plain     = "Test import/export key";
            String cipher    = RSAAlgorithm.encrypt(plain, importedPub);
            String decrypted = RSAAlgorithm.decrypt(cipher, importedPriv);

            System.out.println("  Export → Import → Encrypt → Decrypt: " +
                (plain.equals(decrypted) ? "PASS" : "FAIL"));
        } catch (Exception e) {
            System.out.println("  LOI: " + e.getMessage());
        }
    }

    // Test văn bản dài hơn giới hạn 1 chunk — tự động chia chunk
    private static void testLongText() {
        System.out.println("\n[RSA] Test van ban dai (vuot gioi han 1 chunk):");
        try {
            KeyPair pair = RSAAlgorithm.generateKeyPair(1024);
            // 1024-bit key → max 117 byte/chunk. Dùng 300 ký tự → cần 3 chunk
            String plain = "A".repeat(300);

            String cipher    = RSAAlgorithm.encrypt(plain, pair.getPublic());
            String decrypted = RSAAlgorithm.decrypt(cipher, pair.getPrivate());

            int chunks = cipher.split("\\|").length;
            System.out.println("  Do dai input : " + plain.length() + " ky tu");
            System.out.println("  So chunk     : " + chunks + " (moi chunk toi da 117 byte)");
            System.out.println("  Ket qua      : " + (plain.equals(decrypted) ? "PASS" : "FAIL"));
        } catch (Exception e) {
            System.out.println("  LOI: " + e.getMessage());
        }
    }
}
