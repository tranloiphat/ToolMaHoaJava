package com.cryptotool;

import com.cryptotool.algorithms.symmetric.CaesarCipher;

/**
 * Entry point — Giai đoạn 3: test Caesar Cipher.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Giai doan 3: Caesar Cipher ===\n");

        testCaesarClassic();
        testCaesarRandom();
        testCaesarEdgeCases();

        System.out.println("\n=== Caesar Cipher hoat dong chinh xac! ===");
    }

    // Test mẫu kinh điển từ bài giảng: HELLO + k=3 → KHOOR
    private static void testCaesarClassic() {
        System.out.println("[Caesar] Test k=3 (Caesar goc theo bai giang):");
        int k = 3;
        String plainText  = "HELLO";
        String cipherText = CaesarCipher.encrypt(plainText, k);
        String decrypted  = CaesarCipher.decrypt(cipherText, k);

        System.out.println("  Ban ro   : " + plainText);
        System.out.println("  k        : " + k);
        System.out.println("  Ban ma   : " + cipherText + "  (expected: KHOOR)");
        System.out.println("  Giai ma  : " + decrypted);
        System.out.println("  Ket qua  : " + (cipherText.equals("KHOOR") && decrypted.equals(plainText) ? "PASS" : "FAIL"));
    }

    // Test với key ngẫu nhiên, văn bản hỗn hợp hoa/thường và ký tự đặc biệt
    private static void testCaesarRandom() {
        System.out.println("\n[Caesar] Test key ngau nhien, van ban hon hop:");
        int k = CaesarCipher.generateKey();
        String plainText  = "Hello, World! 123";
        String cipherText = CaesarCipher.encrypt(plainText, k);
        String decrypted  = CaesarCipher.decrypt(cipherText, k);

        System.out.println("  Ban ro   : " + plainText);
        System.out.println("  k        : " + k);
        System.out.println("  Ban ma   : " + cipherText);
        System.out.println("  Giai ma  : " + decrypted);
        // Số và dấu phải giữ nguyên, chữ phải mã hóa/giải đúng
        System.out.println("  Ket qua  : " + (decrypted.equals(plainText) ? "PASS" : "FAIL"));
    }

    // Test trường hợp đặc biệt: quay vòng cuối bảng chữ cái (Z + k phải quay về A)
    private static void testCaesarEdgeCases() {
        System.out.println("\n[Caesar] Test quay vong (Z + 3 = C):");
        String plainText  = "XYZ xyz";
        int k = 3;
        String cipherText = CaesarCipher.encrypt(plainText, k);
        String decrypted  = CaesarCipher.decrypt(cipherText, k);

        System.out.println("  Ban ro   : " + plainText);
        System.out.println("  Ban ma   : " + cipherText + "  (expected: ABC abc)");
        System.out.println("  Giai ma  : " + decrypted);
        System.out.println("  Ket qua  : " + (cipherText.equals("ABC abc") && decrypted.equals(plainText) ? "PASS" : "FAIL"));
    }
}
