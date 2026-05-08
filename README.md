# Tool Mã Hóa Java

**Đồ án giữa kỳ môn:** An toàn bảo mật hệ thống  


---

## Mô tả

Ứng dụng Java Swing hỗ trợ mã hóa/giải mã văn bản và file với nhiều thuật toán khác nhau,
chia thành 3 nhóm chính: **Mã hóa đối xứng**, **Mã hóa bất đối xứng** và **Hàm băm**.

---

## Danh sách thuật toán

### 1. Mã hóa đối xứng (Symmetric Encryption)
| Thuật toán | Loại | Key Size | Ghi chú |
|---|---|---|---|
| Caesar Cipher | Cổ điển | 1–25 (shift) | Tự cài đặt thủ công |
| Substitution Cipher | Cổ điển | 26 ký tự hoán vị | Tự cài đặt thủ công |
| DES | Hiện đại | 56-bit | JCA built-in |
| 3DES (Triple DES) | Hiện đại | 112/168-bit | JCA built-in |
| AES | Hiện đại | 128/192/256-bit | JCA, hỗ trợ ECB/CBC/CFB/OFB/CTR |
| Blowfish | Hiện đại | 32–448-bit | BouncyCastle library |

### 2. Mã hóa bất đối xứng (Asymmetric Encryption)
| Thuật toán | Key Size | Ghi chú |
|---|---|---|
| RSA | 1024/2048/3072/4096-bit | JCA built-in |

### 3. Hàm băm (Hash Functions)
| Thuật toán | Output Size |
|---|---|
| MD5 | 128-bit (32 hex chars) |
| SHA-1 | 160-bit (40 hex chars) |
| SHA-224 | 224-bit (56 hex chars) |
| SHA-256 | 256-bit (64 hex chars) |
| SHA-384 | 384-bit (96 hex chars) |
| SHA-512 | 512-bit (128 hex chars) |

---

## Yêu cầu hệ thống

- Java 8 trở lên (JDK 8+)
- Maven 3.6+

---

## Cách build và chạy

```bash
# Clone project
git clone https://github.com/tranloiphat/ToolMaHoaJava.git
cd ToolMaHoaJava

# Biên dịch và chạy nhanh (console mode)
mvn compile exec:java

# Build file jar hoàn chỉnh
mvn clean package

# Chạy file jar
java -jar target/ToolMaHoa.jar
```

---

## Công nghệ sử dụng

- **Java 8** — ngôn ngữ lập trình chính
- **Java Swing** — giao diện đồ họa
- **JCA (Java Cryptography Architecture)** — API mã hóa tích hợp sẵn
- **BouncyCastle 1.70** — thư viện mã hóa mở rộng (cho Blowfish)
- **Maven** — quản lý dependency và build

---

## Cấu trúc thư mục

```
src/main/java/com/cryptotool/
├── Main.java                          # Entry point
├── algorithms/
│   ├── symmetric/
│   │   ├── CaesarCipher.java
│   │   ├── SubstitutionCipher.java
│   │   ├── DESAlgorithm.java
│   │   ├── TripleDESAlgorithm.java
│   │   ├── AESAlgorithm.java
│   │   └── BlowfishAlgorithm.java
│   ├── asymmetric/
│   │   └── RSAAlgorithm.java
│   └── hash/
│       └── HashAlgorithm.java
├── ui/
│   ├── MainFrame.java
│   ├── HashPanel.java
│   ├── SymmetricPanel.java
│   └── AsymmetricPanel.java
└── utils/
    ├── FileUtils.java
    └── KeyUtils.java
```
