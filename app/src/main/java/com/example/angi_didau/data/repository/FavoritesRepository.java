package com.example.angi_didau.data.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.angi_didau.common.constant.AppConstants;
import com.example.angi_didau.data.remote.FirestoreDataSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository for managing user favorites stored in Firestore.
 * <p>
 * Favorites are stored in /Favorites/{userId}/items/{entityId} sub-collection.
 * This design allows per-user queries without cross-user data leaks and
 * leverages Firestore's automatic document ID for deduplication.
 */
public class FavoritesRepository {

    private static final String TAG = "FavoritesRepository";
    private static final String SUB_COLLECTION_ITEMS = "items";

    private static FavoritesRepository instance;
    private final FirebaseFirestore db;

    private FavoritesRepository() {
        db = FirestoreDataSource.getInstance().getDb();
    }

    public static synchronized FavoritesRepository getInstance() {
        if (instance == null) {
            instance = new FavoritesRepository();
        }
        return instance;
    }

    /**
     * Fetches all favorite item IDs and their types for a given user.
     *
     * @param userId Firebase Auth UID of the current user
     * @return LiveData emitting a list of favorite maps (each map has "entityId", "type", "name", "note")
     */
    public LiveData<List<Map<String, Object>>> getFavorites(String userId) {
        MutableLiveData<List<Map<String, Object>>> liveData = new MutableLiveData<>();

        if (userId == null || userId.isEmpty()) {
            liveData.setValue(new ArrayList<>());
            return liveData;
        }

        db.collection(AppConstants.COLLECTION_FAVORITES)
                .document(userId)
                .collection(SUB_COLLECTION_ITEMS)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Failed to listen for favorites for userId: " + userId, error);
                        liveData.setValue(new ArrayList<>());
                        return;
                    }
                    if (querySnapshot != null) {
                        List<Map<String, Object>> items = new ArrayList<>();
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            Map<String, Object> item = doc.getData();
                            if (item != null) {
                                item.put("docId", doc.getId());
                                items.add(item);
                            }
                        }
                        liveData.setValue(items);
                    }
                });

        return liveData;
    }

    /**
     * Adds an entity to the user's favorites in Firestore.
     * Uses the entityId as the document ID to prevent duplicates naturally.
     *
     * @param userId   Firebase Auth UID
     * @param entityId Firestore document ID of the food or location
     * @param type     {@link AppConstants#ENTITY_TYPE_FOOD} or {@link AppConstants#ENTITY_TYPE_LOCATION}
     * @param name     Display name of the item
     * @param imageUrl Thumbnail image url to display on favorites screen
     * @param note     Optional user note about this favorite
     * @return LiveData emitting true on success, false on failure
     */
    public LiveData<Boolean> addFavorite(String userId, String entityId, String type, String name, String imageUrl, String note, boolean isCustom) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        Map<String, Object> favoriteData = new HashMap<>();
        favoriteData.put("entityId",   entityId);
        favoriteData.put("type",       type);
        favoriteData.put("name",       name);
        favoriteData.put("imageUrl",   imageUrl != null ? imageUrl : "");
        favoriteData.put("note",       note != null ? note : "");
        favoriteData.put("savedAt",    System.currentTimeMillis());
        favoriteData.put("isCustom",   isCustom);

        db.collection(AppConstants.COLLECTION_FAVORITES)
                .document(userId)
                .collection(SUB_COLLECTION_ITEMS)
                .document(entityId)   // Use entityId as doc ID → auto-deduplication
                .set(favoriteData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Favorite added: " + entityId);
                    result.setValue(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add favorite: " + entityId, e);
                    result.setValue(false);
                });

        return result;
    }

    /**
     * Removes an entity from the user's favorites.
     *
     * @param userId   Firebase Auth UID
     * @param entityId Firestore document ID to remove
     * @return LiveData emitting true on success, false on failure
     */
    public LiveData<Boolean> removeFavorite(String userId, String entityId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        db.collection(AppConstants.COLLECTION_FAVORITES)
                .document(userId)
                .collection(SUB_COLLECTION_ITEMS)
                .document(entityId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Favorite removed: " + entityId);
                    result.setValue(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to remove favorite: " + entityId, e);
                    result.setValue(false);
                });

        return result;
    }

    /**
     * Checks if a specific entity is already in the user's favorites.
     *
     * @param userId   Firebase Auth UID
     * @param entityId Firestore document ID to check
     * @return LiveData emitting true if favorited, false otherwise
     */
    public LiveData<Boolean> isFavorite(String userId, String entityId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        db.collection(AppConstants.COLLECTION_FAVORITES)
                .document(userId)
                .collection(SUB_COLLECTION_ITEMS)
                .document(entityId)
                .addSnapshotListener((doc, error) -> {
                    if (error != null) {
                        result.setValue(false);
                        return;
                    }
                    result.setValue(doc != null && doc.exists());
                });

        return result;
    }

    /**
     * Updates only the note field of an existing favorite.
     */
    public LiveData<Boolean> updateFavoriteNote(String userId, String entityId, String newNote) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        Map<String, Object> updates = new HashMap<>();
        updates.put("note", newNote);

        db.collection(AppConstants.COLLECTION_FAVORITES)
                .document(userId)
                .collection(SUB_COLLECTION_ITEMS)
                .document(entityId)
                .update(updates)
                .addOnSuccessListener(aVoid -> result.setValue(true))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update note for: " + entityId, e);
                    result.setValue(false);
                });

        return result;
    }
}
