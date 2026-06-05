package com.example.angi_didau.data.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.angi_didau.common.constant.AppConstants;
import com.example.angi_didau.data.model.Food;
import com.example.angi_didau.data.remote.FirestoreDataSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for {@link Food} data.
 * <p>
 * Acts as the single source of truth for food-related data. ViewModels observe
 * LiveData from this class — they never interact with Firestore directly.
 */
public class FoodRepository {

    private static final String TAG = "FoodRepository";

    private static FoodRepository instance;
    private final FirebaseFirestore db;

    private FoodRepository() {
        db = FirestoreDataSource.getInstance().getDb();
    }

    public static synchronized FoodRepository getInstance() {
        if (instance == null) {
            instance = new FoodRepository();
        }
        return instance;
    }

    /**
     * Fetches the top trending foods (limited by {@link AppConstants#TRENDING_FOODS_LIMIT}).
     *
     * @return LiveData emitting a list of trending foods. Emits an empty list on error.
     */
    public LiveData<List<Food>> getTrendingFoods() {
        MutableLiveData<List<Food>> liveData = new MutableLiveData<>();

        db.collection(AppConstants.COLLECTION_FOODS)
                .limit(AppConstants.TRENDING_FOODS_LIMIT)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Food> foods = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Food food = doc.toObject(Food.class);
                        if (food != null) {
                            food.setId(doc.getId()); // Firestore ID is not auto-mapped
                            foods.add(food);
                        }
                    }
                    liveData.setValue(foods);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch trending foods", e);
                    liveData.setValue(new ArrayList<>()); // Emit empty list on error
                });

        return liveData;
    }
}
