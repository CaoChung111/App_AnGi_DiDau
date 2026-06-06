# DEPENDENCIES.md — Thư viện Sử dụng

## Tổng quan

File này liệt kê tất cả các thư viện (dependencies) được sử dụng trong dự án `AnGi DiDau`, lý do lựa chọn và các lưu ý quan trọng khi cấu hình.

---

## 1. Core AndroidX

| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| `androidx.appcompat:appcompat` | catalog alias | AppCompatActivity, theme support |
| `com.google.android.material:material` | catalog alias | MaterialButton, TextInputLayout, BottomSheetDialogFragment, TabLayout, FloatingActionButton |
| `androidx.activity:activity` | catalog alias | `ComponentActivity`, `enableEdgeToEdge()` |
| `androidx.constraintlayout:constraintlayout` | catalog alias | Layout positioning engine |

**Lý do chọn**: Đây là bộ chuẩn Android Jetpack, bắt buộc cho mọi ứng dụng Android hiện đại. Material 3 cung cấp các component UI phong phú mà không cần viết lại từ đầu.

---

## 2. MVVM — ViewModel + LiveData

| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| `androidx.lifecycle:lifecycle-viewmodel` | catalog alias | `ViewModel` — survive configuration changes |
| `androidx.lifecycle:lifecycle-livedata` | catalog alias | `LiveData`, `MutableLiveData` — observable data holder |

**Lý do chọn**:
- **ViewModel** giữ data xuyên suốt rotation, không bị destroy khi Activity recreate
- **LiveData** là lifecycle-aware — tự động unsubscribe khi Activity/Fragment bị destroy → **zero memory leak**
- Không cần RxJava hay Kotlin Coroutines — Firebase callbacks kết hợp LiveData là đủ cho project này

**Lưu ý**:
- ViewModel **không được** giữ reference tới Context/View — sẽ gây leak
- Dùng `getViewLifecycleOwner()` trong Fragment thay vì `this` khi observe LiveData

---

## 3. Firebase

| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| `com.google.firebase:firebase-bom` | BOM (Bill of Materials) | Quản lý version tất cả Firebase libs |
| `com.google.firebase:firebase-firestore` | via BOM | Cloud database — lưu Foods, Locations, Reviews, Users, Favorites |
| `com.google.firebase:firebase-auth` | via BOM | Authentication — đăng nhập/đăng ký email/password |

**Lý do chọn Firebase**:
- **Firestore**: NoSQL, real-time, không cần backend server riêng, SDK tích hợp sẵn Android
- **Firebase Auth**: Quản lý credential an toàn, không cần tự hash password, hỗ trợ nhiều provider (Email, Google, Facebook)
- **BOM pattern**: Đảm bảo tất cả Firebase libs dùng cùng version tương thích — tránh conflict

**Cấu hình quan trọng**:
```
1. Tạo project trên Firebase Console
2. Download google-services.json → đặt vào app/
3. Thêm plugin: alias(libs.plugins.google.services) trong app/build.gradle.kts
4. Bật Authentication → Email/Password provider
5. Tạo Firestore Database (Production mode → cần rules)
```

**Firestore Security Rules (khuyến nghị cho production)**:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /Users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    match /Favorites/{userId}/items/{itemId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    match /Foods/{foodId} {
      allow read: if request.auth != null;
      allow write: if false; // Admin only
    }
    match /Locations/{locationId} {
      allow read: if request.auth != null;
      allow write: if false;
    }
    match /Reviews/{reviewId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null && request.resource.data.userId == request.auth.uid;
    }
  }
}
```

---

## 4. Image Loading

| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| `com.github.bumptech.glide:glide` | catalog alias | Load ảnh từ URL, caching, placeholder |
| `de.hdodenhof:circleimageview` | catalog alias | Avatar hình tròn (ProfileActivity) |

**Lý do chọn Glide**:
- Tích hợp lifecycle-aware: tự dừng request khi Activity/Fragment destroy
- Caching 2 level (memory + disk): ảnh đã load không cần tải lại
- API đơn giản, hỗ trợ `placeholder()`, `error()`, `centerCrop()`, `circleCrop()`
- Thay thế cho Picasso — Glide nhẹ hơn và tốt hơn với large image sets

**Lưu ý**:
- Không giữ `Glide.with(context)` reference qua lifecycle — luôn dùng `with(itemView.getContext())` trong ViewHolder
- Cần `<uses-permission android:name="android.permission.INTERNET" />` trong Manifest

---

## 5. Testing

| Thư viện | Mục đích |
|----------|----------|
| `junit:junit` | Unit tests (JVM) |
| `androidx.test.ext:junit` | Instrumented tests |
| `androidx.test.espresso:espresso-core` | UI tests |

> **Hiện tại chưa có test case nào được viết** — đây là phase tiếp theo sau khi hoàn thiện features.

---

## 6. Cấu hình Build

```kotlin
// app/build.gradle.kts
android {
    compileSdk 36
    defaultConfig {
        minSdk 24       // Android 7.0 — cover ~95% thiết bị
        targetSdk 36
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
```

**Lý do chọn `minSdk 24`**:
- Android 7.0 (2016) — đủ để dùng tất cả API cần thiết
- Cover hơn 95% thiết bị Android đang hoạt động
- Firebase SDK yêu cầu tối thiểu API 21

---

## 7. Thư viện Chưa Được Thêm (Cân nhắc cho Tương lai)

| Thư viện | Lý do cần | Ghi chú |
|----------|-----------|---------|
| Google Maps SDK | Hiển thị map cho Location lat/lng | Cần billing account |
| Algolia Search | Full-text search tốt hơn Firestore range query | Có free tier |
| Firebase Storage | Upload ảnh user (Photos tab) | Cần thêm dependency |
| Hilt (DI) | Dependency Injection để loại bỏ singleton manual | Nên migrate khi scale |
| WorkManager | Background tasks (sync data) | Khi cần offline mode |
