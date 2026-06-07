package com.example.angi_didau.data.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.angi_didau.common.constant.AppConstants;
import com.example.angi_didau.data.model.Food;
import com.example.angi_didau.data.remote.FirestoreDataSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
     * Fetches the top trending foods sorted by rating descending.
     *
     * @return LiveData emitting a list of trending foods. Emits an empty list on error.
     */
    public LiveData<List<Food>> getTrendingFoods() {
        MutableLiveData<List<Food>> liveData = new MutableLiveData<>();

        db.collection(AppConstants.COLLECTION_FOODS)
                .orderBy("averageRating", Query.Direction.DESCENDING)
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

    /**
     * Fetches all foods from Firestore (up to {@link AppConstants#LIST_PAGE_LIMIT}).
     *
     * @return LiveData emitting a list of all foods.
     */
    public LiveData<List<Food>> getAllFoods() {
        MutableLiveData<List<Food>> liveData = new MutableLiveData<>();

        db.collection(AppConstants.COLLECTION_FOODS)
                .limit(AppConstants.LIST_PAGE_LIMIT)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Food> foods = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Food food = doc.toObject(Food.class);
                        if (food != null) {
                            food.setId(doc.getId());
                            foods.add(food);
                        }
                    }
                    liveData.setValue(foods);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch all foods", e);
                    liveData.setValue(new ArrayList<>());
                });

        return liveData;
    }

    /**
     * Fetches a single food document by its Firestore ID.
     *
     * @param foodId Firestore document ID
     * @return LiveData emitting the Food object, or null if not found.
     */
    public LiveData<Food> getFoodById(String foodId) {
        MutableLiveData<Food> liveData = new MutableLiveData<>();

        if (foodId == null || foodId.isEmpty()) {
            liveData.setValue(null);
            return liveData;
        }

        db.collection(AppConstants.COLLECTION_FOODS)
                .document(foodId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Food food = doc.toObject(Food.class);
                        if (food != null) food.setId(doc.getId());
                        liveData.setValue(food);
                    } else {
                        Log.w(TAG, "Food document not found: " + foodId);
                        liveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch food: " + foodId, e);
                    liveData.setValue(null);
                });

        return liveData;
    }

    /**
     * Searches foods by name using Firestore range query (prefix match).
     * <p>
     * NOTE: Firestore does not support native full-text search. This uses a
     * high-Unicode suffix (\uF8FF) to achieve prefix matching. For production
     * apps, consider Algolia or Firebase Extensions for full-text search.
     *
     * @param query Search term (prefix match on "name" field)
     * @return LiveData emitting matching food items.
     */
    public LiveData<List<Food>> searchFoods(String query) {
        MutableLiveData<List<Food>> liveData = new MutableLiveData<>();

        if (query == null || query.trim().isEmpty()) {
            liveData.setValue(new ArrayList<>());
            return liveData;
        }

        String trimmedQuery = query.trim();
        String endQuery = trimmedQuery + "\uF8FF";

        db.collection(AppConstants.COLLECTION_FOODS)
                .orderBy("name")
                .startAt(trimmedQuery)
                .endAt(endQuery)
                .limit(AppConstants.LIST_PAGE_LIMIT)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Food> foods = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Food food = doc.toObject(Food.class);
                        if (food != null) {
                            food.setId(doc.getId());
                            foods.add(food);
                        }
                    }
                    liveData.setValue(foods);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Food search failed for query: " + query, e);
                    liveData.setValue(new ArrayList<>());
                });

        return liveData;
    }

    /**
     * Updates the average rating of a food document.
     * @param foodId the document ID
     * @param newAvg the new average rating
     */
    public void updateAverageRating(String foodId, float newAvg) {
        if (foodId == null || foodId.isEmpty()) return;
        db.collection(AppConstants.COLLECTION_FOODS)
                .document(foodId)
                .update("averageRating", newAvg)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Successfully updated averageRating for " + foodId))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update averageRating", e));
    }
}
