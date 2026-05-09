package com.cryptotool;

import com.cryptotool.algorithms.symmetric.AESAlgorithm;

/**
 * Entry point — Giai doan 7: test AES voi nhieu mode va key size.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Giai doan 7: AES ===\n");

        // Test tất cả 5 mode với các key size khác nhau
        testAES("ECB", AESAlgorithm.PADDING_PKCS5, 128);
        testAES("CBC", AESAlgorithm.PADDING_PKCS5, 256);
        testAES("CFB", AESAlgorithm.PADDING_NONE,  192);
        testAES("OFB", AESAlgorithm.PADDING_NONE,  128);
        testAES("CTR", AESAlgorithm.PADDING_NONE,  256);

        testSamePlaintextECBvsCBC();

        System.out.println("\n=== AES hoat dong chinh xac! ===");
    }

    // Chạy 1 test case: mode + padding + keySize
    private static void testAES(String mode, String padding, int keySize) {
        System.out.println("[AES/" + mode + "/" + padding + "] Key=" + keySize + "bit:");
        try {
            byte[] key       = AESAlgorithm.generateKey(keySize);
            String plain     = "An toan bao mat he thong - NLU";

            String cipher    = AESAlgorithm.encrypt(plain, key, mode, padding);
            String decrypted = AESAlgorithm.decrypt(cipher, key, mode, padding);

            System.out.println("  Ban ro   : " + plain);
            System.out.println("  Ban ma   : " + cipher);
            System.out.println("  Giai ma  : " + decrypted);
            System.out.println("  Ket qua  : " + (plain.equals(decrypted) ? "PASS" : "FAIL"));
        } catch (Exception e) {
            System.out.println("  LOI: " + e.getMessage());
        }
        System.out.println();
    }

    // Chứng minh ECB không an toàn: cùng plaintext → cùng ciphertext
    // CBC thì khác mỗi lần (do IV ngẫu nhiên)
    private static void testSamePlaintextECBvsCBC() {
        System.out.println("[AES] Demo ECB vs CBC - cung plaintext, ma hoa 2 lan:");
        try {
            byte[] key   = AESAlgorithm.generateKey(128);
            String plain = "AAAAAAAAAAAAAAAA"; // 16 byte toàn 'A'

            // ECB: mã hóa 2 lần cùng plain → bản mã GIỐNG NHAU (nguy hiểm!)
            String ecb1 = AESAlgorithm.encrypt(plain, key, AESAlgorithm.MODE_ECB, AESAlgorithm.PADDING_PKCS5);
            String ecb2 = AESAlgorithm.encrypt(plain, key, AESAlgorithm.MODE_ECB, AESAlgorithm.PADDING_PKCS5);

            // CBC: mã hóa 2 lần cùng plain → bản mã KHÁC NHAU (do IV random)
            String cbc1 = AESAlgorithm.encrypt(plain, key, AESAlgorithm.MODE_CBC, AESAlgorithm.PADDING_PKCS5);
            String cbc2 = AESAlgorithm.encrypt(plain, key, AESAlgorithm.MODE_CBC, AESAlgorithm.PADDING_PKCS5);

            System.out.println("  ECB lan 1: " + ecb1);
            System.out.println("  ECB lan 2: " + ecb2);
            System.out.println("  ECB giong nhau (khong an toan): " + (ecb1.equals(ecb2) ? "YES (BAD)" : "NO"));
            System.out.println();
            System.out.println("  CBC lan 1: " + cbc1);
            System.out.println("  CBC lan 2: " + cbc2);
            System.out.println("  CBC khac nhau (an toan): " + (!cbc1.equals(cbc2) ? "YES (GOOD)" : "NO"));
        } catch (Exception e) {
            System.out.println("  LOI: " + e.getMessage());
        }
    }
}
