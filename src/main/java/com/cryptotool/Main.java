package com.cryptotool;

import com.cryptotool.algorithms.hash.HashAlgorithm;

/**
 * Entry point — Giai doan 5: test 6 ham bam.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Giai doan 5: Hash Algorithms ===\n");

        testAllAlgorithms();
        testAvalancheEffect();
        testEmptyString();

        System.out.println("\n=== Hash Algorithms hoat dong chinh xac! ===");
    }

    // Chạy cả 6 thuật toán trên cùng 1 input, kiểm tra độ dài output đúng không
    private static void testAllAlgorithms() {
        System.out.println("[Hash] Test 6 thuat toan voi input \"hello\":");
        System.out.println("  (Gia tri chuan lay tu cong cu truc tuyen de doi chieu)\n");

        String input = "hello";
        // Giá trị chuẩn của "hello" với từng thuật toán (để đối chiếu)
        String[] algorithms = {
            HashAlgorithm.MD5, HashAlgorithm.SHA1,
            HashAlgorithm.SHA224, HashAlgorithm.SHA256,
            HashAlgorithm.SHA384, HashAlgorithm.SHA512
        };

        try {
            for (String algo : algorithms) {
                String hash = HashAlgorithm.hashText(input, algo);
                int expectedBits = HashAlgorithm.getOutputBits(algo);
                int actualHexLen = hash.length();
                int actualBits   = actualHexLen * 4; // 1 hex char = 4 bit

                boolean sizeOk = actualBits == expectedBits;
                System.out.printf("  %-8s | %d bit | len=%d | %s | %s%n",
                    algo, expectedBits, actualHexLen,
                    sizeOk ? "SIZE OK" : "SIZE FAIL",
                    hash);
            }
        } catch (Exception e) {
            System.out.println("  LOI: " + e.getMessage());
        }
    }

    // Kiểm tra avalanche effect: thay 1 ký tự → hash thay đổi hoàn toàn
    private static void testAvalancheEffect() {
        System.out.println("\n[Hash] Test Avalanche Effect (SHA-256):");
        System.out.println("  (Thay doi 1 ky tu → hash thay doi hoan toan)\n");
        try {
            String text1 = "Hello World";
            String text2 = "Hello world"; // chỉ đổi W → w

            String hash1 = HashAlgorithm.hashText(text1, HashAlgorithm.SHA256);
            String hash2 = HashAlgorithm.hashText(text2, HashAlgorithm.SHA256);

            System.out.println("  \"Hello World\" : " + hash1);
            System.out.println("  \"Hello world\" : " + hash2);
            System.out.println("  Khac nhau     : " + (!hash1.equals(hash2) ? "PASS" : "FAIL"));
        } catch (Exception e) {
            System.out.println("  LOI: " + e.getMessage());
        }
    }

    // Băm chuỗi rỗng — vẫn cho ra digest hợp lệ
    private static void testEmptyString() {
        System.out.println("\n[Hash] Test chuoi rong (MD5):");
        try {
            String hash = HashAlgorithm.hashText("", HashAlgorithm.MD5);
            // MD5("") = d41d8cd98f00b204e9800998ecf8427e (gia tri chuan)
            System.out.println("  MD5(\"\") = " + hash);
            System.out.println("  Ket qua : " +
                (hash.equals("d41d8cd98f00b204e9800998ecf8427e") ? "PASS" : "FAIL"));
        } catch (Exception e) {
            System.out.println("  LOI: " + e.getMessage());
        }
    }
}
