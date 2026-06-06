# FEATURES_LOG.md — Nhật ký Chức năng

## Tổng quan

File này mô tả chi tiết từng chức năng của ứng dụng **AnGi DiDau**, cách thức hoạt động, các lỗi có thể xảy ra và cách phòng tránh.

---

## 1. Xác thực Người dùng (Authentication)

### 1.1 Đăng nhập (Login)

**File**: `LoginActivity.java`, `AuthViewModel.java`, `AuthRepository.java`

**Cách hoạt động**:
1. User nhập email và password
2. `AuthViewModel.login()` kiểm tra validation (email hợp lệ, password ≥ 6 ký tự)
3. Nếu hợp lệ → gọi `AuthRepository.login()` → Firebase `signInWithEmailAndPassword()`
4. Thành công → lưu session vào `SessionManager` → navigate `HomeActivity`
5. Thất bại → Toast thông báo lỗi

**Validation**:
- Email rỗng → `"Email không được để trống"`
- Email sai format → `"Email không hợp lệ"`
- Password rỗng → `"Mật khẩu không được để trống"`
- Password < 6 ký tự → `"Mật khẩu phải có ít nhất 6 ký tự"`

**Error Cases**:
| Lỗi | Nguyên nhân | Xử lý |
|-----|------------|-------|
| Validation fail | Input không hợp lệ | Hiển thị lỗi inline trên TextInputLayout |
| Firebase auth fail | Sai password / email không tồn tại | Toast `error_login_failed` |
| Network error | Không có internet | Firebase callback `onFailureListener` → Toast |

**Auto-login**:
- Nếu Firebase `getCurrentUser() != null` khi mở `LoginActivity` → **tự động chuyển** sang `HomeActivity` (không cần nhập lại)

---

### 1.2 Đăng ký (Register)

**File**: `RegisterActivity.java`, `AuthViewModel.java`, `UserRepository.java`

**Cách hoạt động**:
1. User nhập email, password, confirm password
2. Validate confirm password khớp với password
3. Firebase `createUserWithEmailAndPassword()`
4. Thành công → tạo document `/Users/{uid}` trong Firestore
5. Lưu session → navigate `HomeActivity`

**Error Cases**:
| Lỗi | Xử lý |
|-----|-------|
| Email đã tồn tại | Firebase trả về lỗi → Toast thông báo |
| Password quá yếu | Firebase reject → Toast |
| Confirm không khớp | Validate local → `"Mật khẩu xác nhận không khớp"` |

---

### 1.3 Đăng xuất (Logout)

**File**: `ProfileActivity.java`, `ProfileViewModel.java`, `AuthRepository.java`

**Cách hoạt động**:
1. User nhấn "Đăng xuất" → hiện `AlertDialog` xác nhận
2. Xác nhận → `AuthRepository.logout()` → Firebase `signOut()`
3. `SessionManager.clearSession()` xóa SharedPreferences
4. Navigate `LoginActivity` với `FLAG_ACTIVITY_CLEAR_TASK` (clear back stack)

---

## 2. Màn hình Chính (Home)

**File**: `HomeActivity.java`, `HomeViewModel.java`

**Cách hoạt động**:
- **Auth Guard**: `onCreate()` kiểm tra `FirebaseAuth.getCurrentUser()` — nếu null → redirect Login
- **Trending Foods**: Fetch từ Firestore collection `Foods`, sort theo `averageRating` DESC, limit 5 → RecyclerView horizontal
- **Recommended Locations**: Fetch từ `Locations`, sort theo `averageRating` DESC, limit 5 → RecyclerView vertical
- **Mock data**: Nếu Firestore trả về rỗng → hiển thị data mẫu (cho demo/dev)
- **Search bar click**: Mở `SearchActivity`
- **CardFood click**: Mở `FoodListActivity`
- **CardLocation click**: Mở `LocationListActivity`
- **Adapter item click**: Mở `FoodDetailActivity` / `LocationDetailActivity` với ID

**Error Cases**:
| Lỗi | Xử lý |
|-----|-------|
| Firestore query fail | Emit empty list → hiển thị mock data |
| User not logged in | Redirect sang Login |
| Item click với null ID | Truyền empty string → Detail Activity finish() ngay |

---

## 3. Danh sách Món ăn (Food List)

**File**: `FoodListActivity.java`, `FoodListViewModel.java`, `FoodRepository.java`

**Cách hoạt động**:
- Layout: `activity_shared_list.xml` với `RecyclerView` dùng `StaggeredGridLayoutManager(2, VERTICAL)`
- Fetch all foods từ Firestore (limit 20)
- Convert `Food` model sang `StaggeredItem` để dùng với `StaggeredGridAdapter`
- Click item → `FoodDetailActivity` với `EXTRA_FOOD_ID`

**Lỗi đã fix**:
- ⚠️ `StaggeredGridLayoutManager` chưa được set → RecyclerView không hiển thị gì → **ĐÃ FIX**

---

## 4. Chi tiết Món ăn (Food Detail)

**File**: `FoodDetailActivity.java`, `FoodDetailViewModel.java`

**Cách hoạt động**:
1. Nhận `foodId` từ Intent extra
2. `FoodDetailViewModel.getFood(foodId)` → Firestore `Foods/{foodId}`
3. Bind: tên, mô tả, giá (format VND), rating
4. `FoodDetailViewModel.getReviews(foodId)` → fetch `Reviews` where `entityId == foodId`
5. Hiển thị reviews trong `RecyclerView` với `ReviewAdapter`

**Error Cases**:
| Lỗi | Xử lý |
|-----|-------|
| foodId null/empty | `finish()` ngay trong `onCreate()` |
| Firestore document không tồn tại | `liveData.setValue(null)` → observer không bind |
| Không có reviews | RecyclerView hiển thị rỗng (không crash) |

---

## 5. Danh sách Địa điểm (Location List)

**File**: `LocationListActivity.java`, `LocationListViewModel.java`

**Cách hoạt động**: Tương tự Food List nhưng data từ `Locations` collection.

---

## 6. Chi tiết Địa điểm (Location Detail)

**File**: `LocationDetailActivity.java`, `LocationDetailViewModel.java`
**Fragments**: `OverviewFragment`, `PhotosFragment`, `ReviewsFragment`
**Adapter**: `LocationDetailPagerAdapter` (ViewPager2)

**Cách hoạt động**:
1. Nhận `locationId` từ Intent
2. Fetch Location document từ Firestore
3. Bind tên, địa chỉ, rating lên header
4. ViewPager2 với 3 tabs:
   - **Tổng quan**: Address + description + rating (từ shared ViewModel)
   - **Ảnh**: Placeholder (chưa implement)
   - **Đánh giá**: List reviews từ Firestore

**Shared ViewModel Pattern**:
- `LocationDetailActivity` và cả 3 Fragment đều dùng cùng 1 `LocationDetailViewModel` instance (scoped to Activity)
- Tránh duplicate Firestore calls — data chỉ được fetch 1 lần

**FAB "Thêm Đánh giá"**:
- Mở `AddReviewBottomSheet` với `locationId`
- Validate: nội dung không rỗng + user phải đăng nhập
- Lưu vào `Reviews/{auto-id}` trong Firestore

**Error Cases**:
| Lỗi | Xử lý |
|-----|-------|
| locationId null | `finish()` ngay |
| User chưa đăng nhập khi thêm review | Toast "Bạn cần đăng nhập" |
| Review rỗng | Toast "Nhập nội dung đánh giá" |

---

## 7. Tìm kiếm (Search)

**File**: `SearchActivity.java`, `SearchViewModel.java`
**Adapters**: `SearchResultAdapter`

**Cách hoạt động**:
1. `TextWatcher.onTextChanged()` → `SearchViewModel.search(query)`
2. ViewModel debounce 500ms (cancel và reschedule) → tránh Firestore spam
3. Sau 500ms → `FoodRepository.searchFoods()` + `LocationRepository.searchLocations()` song song
4. Firestore range query: `.orderBy("name").startAt(query).endAt(query + "\uF8FF")`
5. Results combine trong `SearchResultAdapter` với 2 ViewType (food / location)
6. Click → navigate tới FoodDetail hoặc LocationDetail

**Giới hạn**:
- Chỉ prefix-match (không full-text search)
- Case-sensitive với dữ liệu Firestore
- **Giải pháp production**: Dùng Algolia hoặc Firebase Extension cho full-text search

**Error Cases**:
| Lỗi | Xử lý |
|-----|-------|
| Query rỗng | Clear results, không query Firestore |
| Firestore query fail | `liveData.setValue(new ArrayList<>())` |
| Không có kết quả | `isEmpty.setValue(true)` → hiển thị empty state |

---

## 8. Quay Số Ngẫu nhiên (Random)

**File**: `RandomActivity.java`, `SpinWheelView.java`

**Cách hoạt động**:
- Tab "Ăn gì": 8 món ăn mẫu trên bánh xe
- Tab "Đi đâu": 8 địa điểm mẫu trên bánh xe
- Nhấn "QUAY NGAY" → `SpinWheelView.spin()`:
  1. Random `targetIndex` trong danh sách
  2. Tính góc dừng: `5+ vòng × 360° + góc của targetIndex`
  3. `ObjectAnimator` tạo animation quay với `DecelerateInterpolator`
  4. Callback `onSpinEnd(result)` → hiển thị kết quả + Toast

**SpinWheelView**: Custom `View` override `onDraw()` — vẽ bánh xe bằng `Canvas.drawArc()`

**Error Cases**:
| Lỗi | Xử lý |
|-----|-------|
| Spin khi đang quay | `isSpinning` flag ngăn double-spin |
| Items list rỗng | `if (isSpinning || items.isEmpty()) return` |

---

## 9. Khám phá — AI Suggestion (Discover)

**File**: `DiscoverActivity.java`, `TimelineAdapter.java`

**Cách hoạt động**:
1. User chọn số người, loại ăn uống (dropdown)
2. Nhấn "Gợi ý cho tôi" → hiện loading spinner 2 giây (mô phỏng AI)
3. Sau 2 giây → ẩn form, hiện kết quả timeline (fake data)
4. Timeline: 3 items với thời gian, địa điểm, giá, mô tả

**Lưu ý**: Đây là **simulation** — chưa kết nối AI thực. Data là hardcoded. Để production cần tích hợp Gemini API hoặc logic recommendation từ Firestore.

---

## 10. Yêu thích (Favorites)

**File**: `FavoritesActivity.java`, `FavoritesViewModel.java`, `FavoritesRepository.java`
**BottomSheet**: `AddNoteBottomSheet.java`

**Cách hoạt động**:
- Fetch danh sách yêu thích từ `/Favorites/{userId}/items/`
- Nút "Thêm ghi chú" → `AddNoteBottomSheet`:
  1. Nhập tên + ghi chú
  2. Validate tên không rỗng + user đăng nhập
  3. Save vào Firestore: `/Favorites/{userId}/items/{entityId}`

**Thiết kế lưu trữ Favorites**:
- Sub-collection `/Favorites/{userId}/items/` → bảo mật per-user với Firestore Rules
- Document ID = `entityId` → **deduplication tự động** (thêm lại cùng item = overwrite)

**Error Cases**:
| Lỗi | Xử lý |
|-----|-------|
| User không đăng nhập | Toast + không lưu |
| Tên rỗng | Toast "Nhập tên địa điểm/món ăn" |
| Firestore fail | Toast "Lưu thất bại" |

---

## 11. Hồ sơ (Profile)

**File**: `ProfileActivity.java`, `ProfileViewModel.java`, `UserRepository.java`

**Cách hoạt động**:
1. Fetch `/Users/{uid}` từ Firestore
2. Nếu document không tồn tại (đăng ký cũ chưa có document) → **tự động tạo** từ Auth data
3. Hiển thị: username, email, avatar (Glide + circleCrop)
4. Nút Logout → `AlertDialog` xác nhận → `AuthRepository.logout()` + clear SessionManager

**Error Cases**:
| Lỗi | Xử lý |
|-----|-------|
| Firestore fail | Fallback user từ Firebase Auth data |
| Avatar URL rỗng | Glide placeholder `ic_person` |
| Không đăng nhập | `ProfileViewModel.getCurrentUser()` trả null |

---

## Checklist Hoàn thiện

| Chức năng | Trạng thái | Ghi chú |
|-----------|-----------|---------|
| Login | ✅ Hoàn thiện | |
| Register | ✅ Hoàn thiện | Lưu Firestore |
| Logout | ✅ Hoàn thiện | Confirm dialog |
| Home | ✅ Hoàn thiện | Auth guard + click nav |
| Food List | ✅ Hoàn thiện | StaggeredGrid fix |
| Food Detail | ✅ Hoàn thiện | Load từ Firestore |
| Location List | ✅ Hoàn thiện | StaggeredGrid fix |
| Location Detail | ✅ Hoàn thiện | Shared ViewModel |
| Search | ✅ Hoàn thiện | Debounce + 2 types |
| Random | ✅ Đã có sẵn | Spin wheel hoạt động |
| Discover | ✅ Đã có sẵn | Simulation |
| Favorites | ✅ Hoàn thiện | Lưu Firestore |
| Profile | ✅ Hoàn thiện | Hiển thị user data |
| AddReview | ✅ Hoàn thiện | Lưu Firestore |
| Photos Tab | 🟡 Placeholder | Cần Firebase Storage |
