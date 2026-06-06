package com.example.angi_didau.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.angi_didau.data.model.User;
import com.example.angi_didau.data.repository.AuthRepository;
import com.example.angi_didau.data.repository.UserRepository;

/**
 * ViewModel for {@link ProfileActivity}.
 * <p>
 * Fetches the current user's Firestore profile and handles logout.
 */
public class ProfileViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final com.example.angi_didau.data.repository.ReviewRepository reviewRepository;
    private final com.example.angi_didau.data.repository.FavoritesRepository favoritesRepository;

    private LiveData<User> currentUser;
    private final MutableLiveData<Boolean> logoutResult = new MutableLiveData<>();
    
    // Stats LiveData
    private LiveData<Integer> placesVisitedCount;
    private LiveData<Integer> foodsTriedCount;
    private LiveData<Integer> savedPlansCount;

    public ProfileViewModel() {
        userRepository = UserRepository.getInstance();
        authRepository = AuthRepository.getInstance();
        reviewRepository = com.example.angi_didau.data.repository.ReviewRepository.getInstance();
        favoritesRepository = com.example.angi_didau.data.repository.FavoritesRepository.getInstance();
    }

    // ──────────────────────────────────────────
    //  Exposed LiveData
    // ──────────────────────────────────────────

    /** Emits true when logout is complete and Activity should navigate to Login. */
    public LiveData<Boolean> getLogoutResult() { return logoutResult; }

    /**
     * Returns the current user's profile data from Firestore.
     * Lazy — only queries Firestore once per ViewModel lifetime.
     */
    public LiveData<User> getCurrentUser() {
        if (currentUser == null) {
            currentUser = userRepository.getCurrentUserData();
        }
        return currentUser;
    }

    public LiveData<Integer> getPlacesVisitedCount() {
        if (placesVisitedCount == null) {
            placesVisitedCount = loadPlacesVisitedCount();
        }
        return placesVisitedCount;
    }

    public LiveData<Integer> getFoodsTriedCount() {
        if (foodsTriedCount == null) {
            foodsTriedCount = loadFoodsTriedCount();
        }
        return foodsTriedCount;
    }

    public LiveData<Integer> getSavedPlansCount() {
        if (savedPlansCount == null) {
            savedPlansCount = loadSavedPlansCount();
        }
        return savedPlansCount;
    }

    private LiveData<Integer> loadPlacesVisitedCount() {
        MutableLiveData<Integer> countData = new MutableLiveData<>(0);
        com.google.firebase.auth.FirebaseUser fbUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {
            favoritesRepository.getFavorites(fbUser.getUid()).observeForever(favorites -> {
                long count = favorites.stream().filter(f -> "location".equals(f.get("type"))).count();
                countData.setValue((int) count);
            });
        }
        return countData;
    }

    private LiveData<Integer> loadFoodsTriedCount() {
        MutableLiveData<Integer> countData = new MutableLiveData<>(0);
        com.google.firebase.auth.FirebaseUser fbUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {
            favoritesRepository.getFavorites(fbUser.getUid()).observeForever(favorites -> {
                long count = favorites.stream().filter(f -> "food".equals(f.get("type"))).count();
                countData.setValue((int) count);
            });
        }
        return countData;
    }

    private LiveData<Integer> loadSavedPlansCount() {
        MutableLiveData<Integer> countData = new MutableLiveData<>(0);
        com.google.firebase.auth.FirebaseUser fbUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("Users")
                .document(fbUser.getUid())
                .collection("SavedPlans")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        countData.setValue(value.size());
                    }
                });
        }
        return countData;
    }

    // ──────────────────────────────────────────
    //  Actions
    // ──────────────────────────────────────────

    /**
     * Signs out from Firebase Auth and signals the Activity to navigate to Login.
     * Clears the Firebase Auth token — SessionManager.clearSession() is called by the Activity.
     */
    public void logout() {
        authRepository.logout();
        logoutResult.setValue(true);
    }
}
