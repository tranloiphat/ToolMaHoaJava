# Hướng Dẫn Sử Dụng — Tool Mã Hóa Java

## Cách chạy

```bash
# Cách 1: Chạy file jar (sau khi build)
java -jar target/ToolMaHoa.jar

# Cách 2: Chạy qua Maven
mvn compile exec:java
```

---

## Tab 1 — Hàm Băm (Hash Functions)

### Mục đích
Tính giá trị băm (hash/digest) của văn bản hoặc file. Dùng để kiểm tra toàn vẹn dữ liệu.

### Các thuật toán
| Thuật toán | Output | Ghi chú |
|---|---|---|
| MD5 | 32 ký tự hex | Không dùng cho bảo mật cao |
| SHA-1 | 40 ký tự hex | Đang bị loại bỏ dần |
| SHA-224 | 56 ký tự hex | |
| SHA-256 | 64 ký tự hex | Phổ biến nhất |
| SHA-384 | 96 ký tự hex | |
| SHA-512 | 128 ký tự hex | Mạnh nhất |

### Cách dùng — Băm văn bản
1. Chọn thuật toán từ danh sách (vd: SHA-256)
2. Nhập văn bản vào ô **Van ban dau vao**
3. Nhấn nút **Tinh Hash**
4. Kết quả hex hiện ở ô **Ket qua**
5. Nhấn **Copy** để copy kết quả

**Ví dụ:**
```
Input : hello
MD5   : 5d41402abc4b2a76b9719d911017c592
SHA-256: 2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824
```

### Cách dùng — Băm file
1. Nhấn nút **Hash File...**
2. Chọn file bất kỳ (ảnh, PDF, .zip, ...)
3. Kết quả hash của toàn bộ nội dung file hiện ra

---

## Tab 2 — Mã Hóa Đối Xứng (Symmetric Encryption)

### Mục đích
Mã hóa và giải mã văn bản. Người gửi và người nhận dùng **cùng một key**.

### Các thuật toán và tùy chọn

#### Caesar Cipher
- Key: số nguyên từ **1 đến 25** (k=3 là Caesar gốc)
- Ví dụ: `HELLO` + k=3 → `KHOOR`

```
Thuat toan : Caesar
Key        : 3
Van ban ro : HELLO WORLD
Ban ma     : KHOOR ZRUOG
```

#### Substitution Cipher
- Key: chuỗi **26 ký tự** là hoán vị của A-Z (không trùng, không thiếu)
- Ví dụ key: `QWERTYUIOPASDFGHJKLZXCVBNM`

```
Thuat toan : Substitution
Key        : QWERTYUIOPASDFGHJKLZXCVBNM
Van ban ro : HELLO
Ban ma     : ITSSG
```

#### DES
- Key: **56-bit**, dạng Base64 (nhấn Generate để tạo tự động)
- Mode: CBC, Padding: PKCS5

```
Thuat toan : DES
Key (B64)  : 1WhM5bV1xOw=
Van ban ro : Hello DES
Ban ma     : RTXLQHZdG6y2mj...  (Base64)
```

#### 3DES
- Key size: **112-bit** (2-key) hoặc **168-bit** (3-key, an toàn hơn)
- Key dạng Base64

#### AES
- Key size: 128 / 192 / **256-bit** (khuyến nghị 256)
- Mode: **ECB** | **CBC** | **CFB** | **OFB** | **CTR**
- Padding: PKCS5Padding | NoPadding
- Khuyến nghị: CBC + PKCS5Padding

```
Thuat toan : AES
Mode       : CBC
Padding    : PKCS5Padding
Key size   : 256-bit
Key (B64)  : [nhấn Generate]
Van ban ro : An toan bao mat
Ban ma     : l1exNkq0...  (Base64)
```

#### Blowfish
- Key size: 32 đến **448-bit** (thường dùng 128-bit)
- Dùng thư viện BouncyCastle

### Quy trình mã hóa
1. Chọn thuật toán từ **Thuat toan**
2. Chọn tùy chọn phù hợp (Mode, Key size, ...)
3. Nhấn **Generate Key** → key tự điền vào ô Key
4. *(Tùy chọn)* Nhấn **Save Key** để lưu key ra file
5. Nhập văn bản vào ô **Van ban ro**
6. Nhấn **MA HOA** → bản mã Base64 hiện ở ô dưới
7. Nhấn **Copy** để copy bản mã

### Quy trình giải mã
1. Chọn đúng thuật toán và tùy chọn đã dùng khi mã hóa
2. Nhập hoặc **Load Key** đúng key đã dùng
3. Dán bản mã Base64 vào ô **Ban ma**
4. Nhấn **GIAI MA** → văn bản gốc hiện ở ô **Van ban ro**

---

## Tab 3 — Mã Hóa Bất Đối Xứng (RSA)

### Mục đích
Mã hóa dùng **Public Key**, giải mã dùng **Private Key**. Không cần trao đổi bí mật trước.

### Key size
| Key size | Tốc độ sinh | Mức bảo mật |
|---|---|---|
| 1024-bit | Rất nhanh (~35ms) | Không còn khuyến nghị |
| **2048-bit** | Nhanh (~130ms) | **Tiêu chuẩn hiện tại** |
| 3072-bit | Trung bình | Dùng đến 2030+ |
| 4096-bit | Chậm (~1-2s) | Rất an toàn |

### Quy trình mã hóa
1. Chọn key size (khuyến nghị **2048**)
2. Nhấn **Generate Key Pair** → Public Key và Private Key tự điền
3. *(Quan trọng)* Nhấn **Save Public Key** và **Save Private Key** ra file riêng
4. Nhập văn bản vào ô **Van ban ro**
5. Nhấn **MA HOA (Public Key)** → bản mã hiện ở ô **Ket qua**

### Quy trình giải mã
1. Nhấn **Load Private Key** → chọn file private key đã lưu
2. Dán hoặc đảm bảo bản mã đang ở ô **Ket qua**
3. Nhấn **GIAI MA (Private Key)** → văn bản gốc hiện ở ô trên

**Ví dụ:**
```
Key size   : 2048-bit
Van ban ro : Hello RSA! Day la thu nghiem ma hoa bat doi xung.
Ban ma     : TwCG9lRtqCNh5/+5AmbCru0spv/X3M5... (Base64, do dai lon)
Giai ma    : Hello RSA! Day la thu nghiem ma hoa bat doi xung.
```

### Lưu ý bảo mật
> **Private Key phải được giữ bí mật tuyệt đối!**
> Public Key có thể chia sẻ tự do cho bất kỳ ai muốn gửi tin cho bạn.

---

## Lưu ý chung

- Bản mã đầu ra đều ở dạng **Base64** (trừ Caesar và Substitution giữ nguyên dạng text)
- Khi giải mã phải dùng **đúng thuật toán + đúng key + đúng tùy chọn** đã dùng khi mã hóa
- Hàm băm là **1 chiều** — không thể giải mã ngược lại
