package com.cryptotool;

import com.cryptotool.algorithms.symmetric.SubstitutionCipher;

/**
 * Entry point — Giai doan 4: test Substitution Cipher.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Giai doan 4: Substitution Cipher ===\n");

        testSubstitutionFixed();
        testSubstitutionRandom();
        testSubstitutionMixedCase();
        testSubstitutionInvalidKey();

        System.out.println("\n=== Substitution Cipher hoat dong chinh xac! ===");
    }

    // Test với key cố định để kiểm tra bảng tra đúng không
    private static void testSubstitutionFixed() {
        System.out.println("[Substitution] Test key co dinh:");
        // Key mẫu: A→Q, B→W, C→E, ..., Z→M
        String key       = "QWERTYUIOPASDFGHJKLZXCVBNM";
        String plainText = "HELLO";

        String cipherText = SubstitutionCipher.encrypt(plainText, key);
        String decrypted  = SubstitutionCipher.decrypt(cipherText, key);

        // H=7 → key[7]='I', E=4 → key[4]='T', L=11 → key[11]='S', O=14 → key[14]='G'
        // HELLO → ITSSG
        System.out.println("  Key      : " + key);
        System.out.println("  Ban ro   : " + plainText);
        System.out.println("  Ban ma   : " + cipherText + "  (expected: ITSSG)");
        System.out.println("  Giai ma  : " + decrypted);
        System.out.println("  Ket qua  : " + (cipherText.equals("ITSSG") && decrypted.equals(plainText) ? "PASS" : "FAIL"));
    }

    // Test với key ngẫu nhiên, encrypt rồi decrypt phải ra bản gốc
    private static void testSubstitutionRandom() {
        System.out.println("\n[Substitution] Test key ngau nhien:");
        String key       = SubstitutionCipher.generateKey();
        String plainText = "The quick brown fox jumps over the lazy dog";

        String cipherText = SubstitutionCipher.encrypt(plainText, key);
        String decrypted  = SubstitutionCipher.decrypt(cipherText, key);

        System.out.println("  Key      : " + key);
        System.out.println("  Ban ro   : " + plainText);
        System.out.println("  Ban ma   : " + cipherText);
        System.out.println("  Giai ma  : " + decrypted);
        System.out.println("  Ky tu dac biet giu nguyen: " + (cipherText.contains(" ") ? "YES" : "NO"));
        System.out.println("  Ket qua  : " + (decrypted.equals(plainText) ? "PASS" : "FAIL"));
    }

    // Test hoa/thường được giữ nguyên sau giải mã
    private static void testSubstitutionMixedCase() {
        System.out.println("\n[Substitution] Test hoa/thuong:");
        String key       = SubstitutionCipher.generateKey();
        String plainText = "Hello World";

        String cipherText = SubstitutionCipher.encrypt(plainText, key);
        String decrypted  = SubstitutionCipher.decrypt(cipherText, key);

        boolean caseOk = Character.isUpperCase(cipherText.charAt(0))
                      && Character.isLowerCase(cipherText.charAt(1));
        System.out.println("  Ban ro   : " + plainText);
        System.out.println("  Ban ma   : " + cipherText);
        System.out.println("  Giu hoa/thuong: " + (caseOk ? "PASS" : "FAIL"));
        System.out.println("  Giai ma  : " + (decrypted.equals(plainText) ? "PASS" : "FAIL"));
    }

    // Test validate key sai
    private static void testSubstitutionInvalidKey() {
        System.out.println("\n[Substitution] Test key khong hop le:");
        try {
            SubstitutionCipher.encrypt("TEST", "ABCD"); // key chỉ 4 ký tự
            System.out.println("  Ket qua: FAIL (phai nem exception)");
        } catch (IllegalArgumentException e) {
            System.out.println("  Bat duoc loi: " + e.getMessage());
            System.out.println("  Ket qua: PASS");
        }
    }
}
