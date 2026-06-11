package com.example.angi_didau.ui.favorites;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.angi_didau.data.repository.FavoritesRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ViewModel for {@link FavoritesActivity}.
 * <p>
 * Manages the user's favorites list and note-adding functionality.
 * Requires the user to be logged in — guards against null user.
 */
public class FavoritesViewModel extends ViewModel {

    private final FavoritesRepository favoritesRepository;

    private LiveData<List<Map<String, Object>>> favorites;
    private final MutableLiveData<Boolean>      saveNoteResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean>      removeResult   = new MutableLiveData<>();
    private final MutableLiveData<Boolean>      isLoading      = new MutableLiveData<>(true);

    public FavoritesViewModel() {
        favoritesRepository = FavoritesRepository.getInstance();
    }

    // ──────────────────────────────────────────
    //  Exposed LiveData
    // ──────────────────────────────────────────

    public LiveData<Boolean> getSaveNoteResult() { return saveNoteResult; }
    public LiveData<Boolean> getRemoveResult()   { return removeResult; }
    public LiveData<Boolean> getIsLoading()      { return isLoading; }

    /**
     * Returns the list of favorites for the current user.
     * Returns an empty list LiveData if not logged in.
     */
    public LiveData<List<Map<String, Object>>> getFavorites() {
        if (favorites == null) {
            String userId = getCurrentUserId();
            if (userId != null) {
                favorites = favoritesRepository.getFavorites(userId);
            } else {
                MutableLiveData<List<Map<String, Object>>> emptyData = new MutableLiveData<>();
                emptyData.setValue(new ArrayList<>());
                favorites = emptyData;
            }
        }
        return favorites;
    }

    // ──────────────────────────────────────────
    //  Actions
    // ──────────────────────────────────────────

    /**
     * Adds a new note/favorite entry.
     *
     * @param entityId Firestore ID of the food or location
     * @param type     {@code "food"} or {@code "location"}
     * @param name     Display name of the item
     * @param imageUrl URL of the image
     * @param note     User's personal note
     */
    public void addFavoriteWithNote(String entityId, String type, String name, String imageUrl, String note) {
        String userId = getCurrentUserId();
        if (userId == null) {
            saveNoteResult.setValue(false);
            return;
        }

        favoritesRepository.addFavorite(userId, entityId, type, name, imageUrl, note, false)
                .observeForever(success -> saveNoteResult.setValue(success));
    }

    /**
     * Adds a custom personal note (not tied to an existing database entity).
     */
    public void addCustomNote(String type, String name, String note, String imageUrl) {
        String userId = getCurrentUserId();
        if (userId == null) {
            saveNoteResult.setValue(false);
            return;
        }

        String customId = java.util.UUID.randomUUID().toString();
        if (imageUrl == null) {
            imageUrl = "";
        }

        favoritesRepository.addFavorite(userId, customId, type, name, imageUrl, note, true)
                .observeForever(success -> saveNoteResult.setValue(success));
    }

    /**
     * Removes a favorite by its entityId.
     */
    public void removeFavorite(String entityId) {
        String userId = getCurrentUserId();
        if (userId == null) {
            removeResult.setValue(false);
            return;
        }
        favoritesRepository.removeFavorite(userId, entityId)
                .observeForever(success -> removeResult.setValue(success));
    }

    public void onDataLoaded() {
        isLoading.setValue(false);
    }

    // ──────────────────────────────────────────
    //  Helpers
    // ──────────────────────────────────────────

    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }
}
