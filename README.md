# Tool Mã Hóa Java

**Đồ án giữa kỳ môn:** An toàn bảo mật hệ thống
**Trường:** Đại học Nông Lâm TP.HCM
**Giảng viên:** Phan Đình Long
**Sinh viên:** <!-- Điền tên bạn -->
**MSSV:** <!-- Điền MSSV -->

---

## Mô tả

Ứng dụng Java Swing hỗ trợ mã hóa/giải mã văn bản và file với nhiều thuật toán,
chia thành 3 nhóm: **Mã hóa đối xứng**, **Mã hóa bất đối xứng**, **Hàm băm**.

---

## Giao diện

```
┌─────────────────────────────────────────────────────────┐
│  Tool Ma Hoa Java - v1.0              [Tro Giup]        │
├──────────────┬──────────────────┬───────────────────────┤
│   Ham Bam    │  Ma Hoa Doi Xung │ Ma Hoa Bat Doi Xung   │
├──────────────┴──────────────────┴───────────────────────┤
│  [Noi dung tab tuong ung]                               │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

**Tab Ham Bam:** Chọn thuật toán → Nhập text → Tinh Hash → Copy kết quả hex

**Tab Ma Hoa Doi Xung:** Chọn thuật toán → Chọn tùy chọn → Generate Key → MA HOA / GIAI MA

**Tab Ma Hoa Bat Doi Xung:** Chọn key size → Generate Key Pair → MA HOA / GIAI MA bằng RSA

---

## Danh sách thuật toán

### Mã hóa đối xứng
| Thuật toán | Key Size | Ghi chú |
|---|---|---|
| Caesar Cipher | 1–25 (shift) | Tự cài đặt thủ công |
| Substitution Cipher | 26 ký tự hoán vị | Tự cài đặt thủ công |
| DES | 56-bit | JCA, CBC/PKCS5 |
| 3DES | 112 / 168-bit | JCA, CBC/PKCS5 |
| AES | 128 / 192 / 256-bit | JCA, ECB/CBC/CFB/OFB/CTR |
| Blowfish | 32–448-bit | **BouncyCastle** provider |

### Mã hóa bất đối xứng
| Thuật toán | Key Size |
|---|---|
| RSA | 1024 / 2048 / 3072 / 4096-bit |

### Hàm băm
| Thuật toán | Output |
|---|---|
| MD5 | 128-bit (32 hex) |
| SHA-1 | 160-bit (40 hex) |
| SHA-224 | 224-bit (56 hex) |
| SHA-256 | 256-bit (64 hex) |
| SHA-384 | 384-bit (96 hex) |
| SHA-512 | 512-bit (128 hex) |

---

## Yêu cầu hệ thống

- Java 8+ (JDK 8 trở lên)
- Maven 3.6+

---

## Build và chạy

```bash
# 1. Clone project
git clone https://github.com/tranloiphat/ToolMaHoaJava.git
cd ToolMaHoaJava

# 2. Build file jar (bao gồm BouncyCastle)
mvn clean package

# 3. Chạy ứng dụng
java -jar target/ToolMaHoa.jar
```

> File jar được tạo tại `target/ToolMaHoa.jar` — chứa tất cả dependency, chạy trực tiếp không cần cài thêm gì.

---

## Cấu trúc source code

```
src/main/java/com/cryptotool/
├── Main.java
├── algorithms/
│   ├── symmetric/
│   │   ├── CaesarCipher.java         # Tự cài đặt
│   │   ├── SubstitutionCipher.java   # Tự cài đặt
│   │   ├── DESAlgorithm.java         # JCA
│   │   ├── TripleDESAlgorithm.java   # JCA
│   │   ├── AESAlgorithm.java         # JCA, 5 mode
│   │   └── BlowfishAlgorithm.java    # BouncyCastle
│   ├── asymmetric/
│   │   └── RSAAlgorithm.java         # JCA
│   └── hash/
│       └── HashAlgorithm.java        # JCA, 6 thuật toán
├── ui/
│   ├── MainFrame.java
│   ├── HashPanel.java
│   ├── SymmetricPanel.java
│   └── AsymmetricPanel.java
└── utils/
    ├── FileUtils.java
    └── KeyUtils.java
```

---

## Nộp bài

```bash
# Nén source code thành zip để nộp
# Windows (PowerShell):
Compress-Archive -Path src,pom.xml,README.md,HUONG_DAN_SU_DUNG.md,.gitignore `
  -DestinationPath ToolMaHoaJava_MSSV.zip

# Các file cần nộp:
# 1. ToolMaHoa.jar         (file thực thi)
# 2. ToolMaHoaJava_MSSV.zip (source code)
# 3. HUONG_DAN_SU_DUNG.md  (hướng dẫn sử dụng)
```

---

## Công nghệ

- **Java 8** — ngôn ngữ chính
- **Java Swing** — giao diện đồ họa
- **JCA** (Java Cryptography Architecture) — API mã hóa tích hợp
- **BouncyCastle 1.70** — thư viện mã hóa mở rộng (Blowfish)
- **Maven** — quản lý dependency và build
