package com.example.angi_didau.database;

import android.util.Log;

import com.example.angi_didau.data.model.Food;
import com.example.angi_didau.data.model.Location;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class FirebaseSeeder {

    private static final String TAG = "FirebaseSeeder";

    public static void seedData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference foodsRef = db.collection(com.example.angi_didau.common.constant.AppConstants.COLLECTION_FOODS);
        CollectionReference locationsRef = db.collection(com.example.angi_didau.common.constant.AppConstants.COLLECTION_LOCATIONS);

        // --- Seed Foods ---
        List<String> foodGallery = Arrays.asList(
                "https://picsum.photos/seed/food_gal1/600/400",
                "https://picsum.photos/seed/food_gal2/600/400"
        );
        List<Food> foods = Arrays.asList(
                new Food("f1", "Bún Chả Hà Nội", "Thịt nướng than hoa thơm lừng với nước mắm chua ngọt đặc trưng.", 45000.0, "https://picsum.photos/seed/food1/600/400", foodGallery, 4.8f),
                new Food("f2", "Phở Bò Nam Định", "Nước dùng ngọt thanh, bánh phở dai mềm, thịt bò thái mỏng.", 55000.0, "https://picsum.photos/seed/food2/600/400", foodGallery, 4.7f),
                new Food("f3", "Cơm Tấm Sài Gòn", "Sườn nướng mỡ hành thơm phức, chả cua trứng béo ngậy.", 50000.0, "https://picsum.photos/seed/food3/600/400", foodGallery, 4.9f),
                new Food("f4", "Bánh Mì Huynh Hoa", "Ổ bánh mì ngập ngụa pate, chả lụa và dăm bông.", 65000.0, "https://picsum.photos/seed/food4/600/400", foodGallery, 4.5f),
                new Food("f5", "Bún Đậu Mắm Tôm", "Bún ép chặt, đậu rán giòn, chả cốm dẻo thơm.", 40000.0, "https://picsum.photos/seed/food5/600/400", foodGallery, 4.6f)
        );

        for (Food food : foods) {
            foodsRef.document(food.getId()).set(food)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Added food: " + food.getName()))
                    .addOnFailureListener(e -> Log.e(TAG, "Error adding food", e));
        }

        // --- Seed Locations ---
        List<String> locGallery = Arrays.asList(
                "https://picsum.photos/seed/loc_gal1/600/400",
                "https://picsum.photos/seed/loc_gal2/600/400",
                "https://picsum.photos/seed/loc_gal3/600/400"
        );
        List<Location> locations = Arrays.asList(
                new Location("loc1", "An Nam Bistro & Lounge", "Quận 1, TP. HCM", "Một không gian nhà hàng sang trọng mang đậm chất Việt Nam hiện đại.", 250000.0, "https://picsum.photos/seed/loc1/600/400", locGallery, 10.7769, 106.7009, 4.8f),
                new Location("loc2", "Cà Phê Trứng Giảng", "Hoàn Kiếm, Hà Nội", "Cà phê trứng lâu đời nhất Hà Nội, mang hương vị truyền thống đặc biệt.", 35000.0, "https://picsum.photos/seed/loc2/600/400", locGallery, 21.0285, 105.8542, 4.7f),
                new Location("loc3", "Bún Chả Hương Liên", "Hai Bà Trưng, Hà Nội", "Quán bún chả nổi tiếng từng đón tiếp cựu Tổng thống Mỹ Barack Obama.", 50000.0, "https://picsum.photos/seed/loc3/600/400", locGallery, 21.0163, 105.8524, 4.6f),
                new Location("loc4", "Quán Ốc Như", "Quận 10, TP. HCM", "Quán ốc bình dân nhưng luôn tấp nập khách với hàng chục món ốc tươi ngon.", 100000.0, "https://picsum.photos/seed/loc4/600/400", locGallery, 10.7715, 106.6698, 4.5f)
        );

        for (Location loc : locations) {
            locationsRef.document(loc.getId()).set(loc)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Added location: " + loc.getName()))
                    .addOnFailureListener(e -> Log.e(TAG, "Error adding location", e));
        }
    }
}
