# Hướng Dẫn Thêm Dữ Liệu Lên Firebase Firestore

Để app hoạt động mượt mà và hiển thị dữ liệu đầy đủ trên các trang như Home, Favorites, và Random, bạn cần thiết lập dữ liệu mẫu trên Firebase Firestore. Các chức năng của ứng dụng (Hiển thị danh sách món ăn, địa điểm) sẽ lấy dữ liệu trực tiếp từ đây.

## Bước 1: Truy cập Firebase Console
1. Truy cập [Firebase Console](https://console.firebase.google.com/).
2. Chọn dự án `AnGi-DiDau` của bạn.
3. Ở menu bên trái, chọn **Firestore Database** dưới mục "Build".
4. Nhấn **Create database** (nếu chưa tạo), và chọn "Start in test mode" để cho phép đọc/ghi dữ liệu dễ dàng trong quá trình phát triển.
5. Sau khi tạo xong, bạn sẽ thấy giao diện Database trống.

---

## Bước 2: Tạo Collection `foods`

Tạo một Collection có tên là `foods` để chứa danh sách các món ăn.

1. Bấm vào **Start collection** hoặc **Add collection**.
2. Collection ID: nhập `foods` và bấm Next.
3. Tạo Document đầu tiên (Để Auto-ID). Sau đó thêm các trường (fields) theo cấu trúc dưới đây:

### Cấu trúc dữ liệu món ăn:
Mỗi document trong collection `foods` cần có các trường (Fields) sau:
- **name** (String): "Bún Chả Hà Nội"
- **description** (String): "Đặc sản chuẩn vị Bắc với thịt nướng than hoa..."
- **priceRange** (String): "45.000đ - 85.000đ"
- **rating** (Number): `4.8`
- **reviewCount** (Number): `1250`
- **imageUrl** (String): "Link ảnh món ăn từ Google/Firebase Storage" (Ví dụ: `https://example.com/buncha.jpg`)
- **tags** (Array): Thêm các chuỗi như `"Truyền thống"`, `"Ăn trưa"`, `"Thịt heo"`
- **restaurantId** (String): ID của quán ăn (sẽ map với location, có thể bỏ qua nếu chưa dùng liên kết sâu)

4. Lặp lại việc "Add document" để thêm vài món ăn khác (Phở bò, Cơm tấm, Bánh mì...).

---

## Bước 3: Tạo Collection `locations`

Tạo một Collection có tên là `locations` để chứa danh sách các địa điểm / quán ăn.

1. Ở màn hình gốc của Firestore, bấm **Add collection**.
2. Collection ID: nhập `locations` và bấm Next.
3. Tạo Document đầu tiên (Để Auto-ID). Thêm các trường sau:

### Cấu trúc dữ liệu địa điểm:
- **name** (String): "An Nam Bistro & Lounge"
- **category** (String): "Modern Vietnamese Cuisine • Bistro"
- **address** (String): "Quận 1, TP. HCM"
- **distance** (Number): `1.5` *(khoảng cách tính bằng km)*
- **rating** (Number): `4.5`
- **reviewCount** (Number): `850`
- **openStatus** (String): "Open until 22:00"
- **imageUrl** (String): "Link ảnh quán ăn" (Ví dụ: `https://example.com/bistro.jpg`)
- **photos** (Array): Thêm vài link ảnh khác của quán.
- **latitude** (Number): Toạ độ latitude (nếu có bản đồ)
- **longitude** (Number): Toạ độ longitude

4. Lặp lại việc "Add document" để thêm vài địa điểm khác (Quán Ốc, Quán Cafe, Nhà hàng Nhật...).

---

## Bước 4: Kiểm tra lại ứng dụng

1. Sau khi đã thêm xong ít nhất 3 món ăn vào `foods` và 3 địa điểm vào `locations`.
2. Mở lại ứng dụng trên Android Studio và Build lại.
3. Đăng nhập thành công vào màn hình Home, kiểm tra xem danh sách món ăn/địa điểm có được load từ Firestore hay không (Nên kiểm tra phần logcat nếu như data chưa hiện để xử lý mapping dữ liệu trong code nếu cần).

*Lưu ý: Đảm bảo Rules của Firestore đang cho phép ứng dụng đọc dữ liệu:*
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if true; // (Chỉ dùng cho dev)
    }
  }
}
```
