# ARCHITECTURE.md — Kiến trúc Hệ thống AnGi DiDau

## 1. Tổng quan

**AnGi DiDau** là ứng dụng Android gợi ý ăn uống và địa điểm vui chơi. App sử dụng kiến trúc **MVVM (Model-View-ViewModel)** kết hợp với **Repository Pattern** và Firebase làm backend.

---

## 2. Sơ đồ Luồng Dữ liệu (Data Flow)

```
┌─────────────────────────────────────────────────────────────────┐
│                          UI LAYER                               │
│  Activity / Fragment  ◄─── observe() ─── ViewModel             │
│       │                                      │                  │
│       └──── user action ──────────────►  call method()          │
└─────────────────────────────────────────────────────────────────┘
                                │
                         LiveData<T>
                                │
┌─────────────────────────────────────────────────────────────────┐
│                       VIEWMODEL LAYER                           │
│  HomeViewModel / FoodDetailViewModel / SearchViewModel / ...    │
│       │                                                         │
│       └──── repository.getSomething() ──────────────────────►   │
└─────────────────────────────────────────────────────────────────┘
                                │
                         LiveData<T>
                                │
┌─────────────────────────────────────────────────────────────────┐
│                      REPOSITORY LAYER                           │
│  FoodRepository / LocationRepository / ReviewRepository /       │
│  UserRepository / FavoritesRepository / AuthRepository          │
│       │                                                         │
│       └──── db.collection().get() ──► Firestore callback        │
└─────────────────────────────────────────────────────────────────┘
                                │
┌─────────────────────────────────────────────────────────────────┐
│                       DATA SOURCE                               │
│  FirestoreDataSource (singleton) — Firebase Auth                │
└─────────────────────────────────────────────────────────────────┘
                                │
┌─────────────────────────────────────────────────────────────────┐
│                        FIREBASE                                 │
│  Firebase Authentication  |  Firestore Database                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 3. Package Structure

```
com.example.angi_didau/
├── AnGiDiDauApplication.java          # App entry point, Firebase init
│
├── common/
│   ├── constant/
│   │   └── AppConstants.java          # Centralized constants
│   └── util/
│       └── SessionManager.java        # SharedPreferences login state
│
├── data/
│   ├── model/                         # POJO models (Firestore-compatible)
│   │   ├── Food.java
│   │   ├── Location.java
│   │   ├── Review.java
│   │   └── User.java
│   ├── remote/
│   │   └── FirestoreDataSource.java   # Singleton Firestore instance
│   └── repository/                    # Single source of truth per domain
│       ├── AuthRepository.java
│       ├── FoodRepository.java
│       ├── LocationRepository.java
│       ├── ReviewRepository.java
│       ├── UserRepository.java
│       └── FavoritesRepository.java
│
├── adapter/                           # RecyclerView Adapters
│   ├── TrendingFoodAdapter.java
│   ├── RecommendedLocationAdapter.java
│   ├── StaggeredGridAdapter.java
│   ├── LocationDetailPagerAdapter.java
│   ├── ReviewAdapter.java
│   └── SearchResultAdapter.java
│
└── ui/
    ├── auth/                          # Login, Register + AuthViewModel
    ├── home/                          # Home + HomeViewModel
    ├── discover/                      # Discover (AI simulation)
    ├── food/                          # FoodList + FoodDetail + ViewModels
    ├── location/                      # LocationList + LocationDetail + Fragments + ViewModels
    ├── search/                        # SearchActivity + SearchViewModel
    ├── random/                        # RandomActivity + SpinWheelView
    ├── favorites/                     # FavoritesActivity + ViewModel + BottomSheet
    ├── profile/                       # ProfileActivity + ProfileViewModel
    └── model/                         # UI-layer models (StaggeredItem)
```

---

## 4. Màn hình và Luồng Điều hướng

```
LoginActivity  ──(success)──►  HomeActivity
     │                              │
     │                    ┌─────────┼──────────────────┐
     │                    │         │                   │
     │               FoodListAct  LocationListAct   BottomNav
     │                    │         │            ┌───┴───┐───────┐
     │                    ▼         ▼            │       │       │
     │             FoodDetailAct  LocationDetailAct  Discover  Random
     │                                │              │       │
     │                         ┌──────┤          FavoritesAct ProfileAct
     │                    OverviewFrag│
     │                    PhotosFrag  │
     │                    ReviewsFrag ─── (AddReviewBottomSheet)
     │
RegisterActivity ──(success)──► HomeActivity
```

### Luồng Authentication:
1. App khởi động → `LoginActivity` (LAUNCHER)
2. Kiểm tra `FirebaseAuth.getCurrentUser()`
3. Nếu đã đăng nhập → chuyển thẳng `HomeActivity`
4. `HomeActivity.onCreate()` cũng guard: nếu Firebase user null → back về `LoginActivity`
5. Đăng xuất: `ProfileActivity` → `confirmLogout()` → `AuthRepository.logout()` → `LoginActivity`

---

## 5. Cơ sở Dữ liệu Firestore

### Collections:
```
Foods/
  {foodId}/
    name: String
    description: String
    price: double
    imageUrl: String
    averageRating: float

Locations/
  {locationId}/
    name: String
    address: String
    imageUrl: String
    latitude: double
    longitude: double
    averageRating: float

Reviews/
  {reviewId}/
    userId: String
    entityId: String        # foodId hoặc locationId
    content: String
    rating: float
    timestamp: long

Users/
  {uid}/
    id: String
    username: String
    email: String
    avatarUrl: String
    createdAt: long

Favorites/
  {userId}/
    items/
      {entityId}/
        entityId: String
        type: String        # "food" hoặc "location"
        name: String
        note: String
        savedAt: long
```

---

## 6. Xử lý Bất đồng bộ (Async)

Tất cả các Firestore operation đều **async** và được xử lý qua:

- **Firebase callback** (`addOnSuccessListener` / `addOnFailureListener`) — chạy trên main thread
- **LiveData** — ViewModel expose `LiveData<T>`, Activity/Fragment `.observe()`
- **Không block Main Thread** — Firebase SDK tự chạy network trên background thread và callback về main thread
- **Debouncing** trong `SearchViewModel` dùng `Handler(Looper.getMainLooper()).postDelayed()` — an toàn, không cần coroutine/RxJava

---

## 7. Lifecycle Management

- **ViewModel** survive configuration changes (rotation) — Firestore chỉ được query một lần nhờ lazy initialization (`if (data == null) { data = repo.get... }`)
- **Fragment** dùng `getViewLifecycleOwner()` thay vì `this` khi observe — tránh memory leak sau `onDestroyView()`
- **Adapter** lấy Context từ `parent.getContext()` trong `onCreateViewHolder`, không lưu trữ Activity context
- **Handler** trong `SearchViewModel.onCleared()` được cleanup để tránh leak
