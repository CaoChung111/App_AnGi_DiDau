package com.example.angi_didau.data.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.angi_didau.common.constant.AppConstants;
import com.example.angi_didau.data.model.User;
import com.example.angi_didau.data.remote.FirestoreDataSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository for {@link User} profile data stored in Firestore.
 * <p>
 * Separates Firebase Auth (credentials) from Firestore (profile data).
 * Auth operations stay in {@link AuthRepository}; this class only handles
 * the /Users/{uid} document.
 */
public class UserRepository {

    private static final String TAG = "UserRepository";

    private static UserRepository instance;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    private UserRepository() {
        db   = FirestoreDataSource.getInstance().getDb();
        auth = FirebaseAuth.getInstance();
    }

    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    /**
     * Fetches the current user's Firestore profile document.
     *
     * @return LiveData emitting the User object, or null if not found / not logged in.
     */
    public LiveData<User> getCurrentUserData() {
        MutableLiveData<User> liveData = new MutableLiveData<>();

        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            liveData.setValue(null);
            return liveData;
        }

        db.collection(AppConstants.COLLECTION_USERS)
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            user.setId(documentSnapshot.getId());
                        }
                        liveData.setValue(user);
                    } else {
                        // Document doesn't exist — create a basic one from Auth data
                        User newUser = new User(
                                firebaseUser.getUid(),
                                firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "Người dùng",
                                firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "",
                                firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : "",
                                System.currentTimeMillis()
                        );
                        saveUserToFirestore(newUser);
                        liveData.setValue(newUser);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch user data", e);
                    // Fallback: build user from Auth info without Firestore
                    User fallbackUser = new User(
                            firebaseUser.getUid(),
                            firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "Người dùng",
                            firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "",
                            "",
                            0
                    );
                    liveData.setValue(fallbackUser);
                });

        return liveData;
    }

    /**
     * Persists a newly registered user's profile to Firestore.
     * Called automatically after Firebase Auth creates the account.
     *
     * @param user The user model to save (id must equal Firebase Auth UID)
     */
    public void saveUserToFirestore(User user) {
        if (user == null || user.getId() == null) return;

        db.collection(AppConstants.COLLECTION_USERS)
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User profile saved: " + user.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to save user profile", e));
    }

    /**
     * Updates the current user's display name in both Firestore and Auth profile.
     *
     * @param newName New display name to set
     * @return LiveData emitting true on success, false on failure
     */
    public LiveData<Boolean> updateDisplayName(String newName) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            result.setValue(false);
            return result;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("username", newName);

        db.collection(AppConstants.COLLECTION_USERS)
                .document(firebaseUser.getUid())
                .update(updates)
                .addOnSuccessListener(aVoid -> result.setValue(true))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update display name", e);
                    result.setValue(false);
                });

        return result;
    }
}
