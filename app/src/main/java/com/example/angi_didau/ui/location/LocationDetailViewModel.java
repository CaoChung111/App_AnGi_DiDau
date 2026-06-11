package com.example.angi_didau.ui.location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.angi_didau.data.model.Location;
import com.example.angi_didau.data.model.Review;
import com.example.angi_didau.data.repository.LocationRepository;
import com.example.angi_didau.data.repository.ReviewRepository;
import java.util.List;

/**
 * ViewModel for {@link LocationDetailActivity}.
 * <p>
 * Fetches location details and its reviews. Also exposes the locationId so that
 * child Fragments (Overview, Reviews) can share the same ViewModel instead of
 * receiving IDs through Fragment arguments.
 */
public class LocationDetailViewModel extends ViewModel {

    private final LocationRepository locationRepository;
    private final ReviewRepository   reviewRepository;

    private LiveData<Location>      location;
    private LiveData<List<Review>>  reviews;
    private String                  locationId;

    private final MutableLiveData<Boolean> isLoading    = new MutableLiveData<>(false);
    private final MutableLiveData<String>  errorMessage = new MutableLiveData<>();

    public LocationDetailViewModel() {
        locationRepository = LocationRepository.getInstance();
        reviewRepository   = ReviewRepository.getInstance();
    }

    // ──────────────────────────────────────────
    //  Exposed LiveData
    // ──────────────────────────────────────────

    public LiveData<Boolean> getIsLoading()    { return isLoading; }
    public LiveData<String>  getErrorMessage() { return errorMessage; }
    public String            getLocationId()   { return locationId; }

    /**
     * Initializes the ViewModel with a locationId and starts data fetching.
     * Safe to call multiple times — only fetches once.
     *
     * @param id Firestore document ID for the location
     */
    public LiveData<Location> getLocation(String id) {
        if (location == null) {
            this.locationId = id;
            isLoading.setValue(true);
            location = locationRepository.getLocationById(id);
        }
        return location;
    }

    /**
     * Lazily fetches reviews for the current location.
     */
    public LiveData<List<Review>> getReviews() {
        if (reviews == null && locationId != null) {
            reviews = reviewRepository.getReviewsByEntityId(locationId);
        }
        return reviews;
    }

    private LiveData<Boolean> isFavorite;

    public LiveData<Boolean> getIsFavorite(String locationId) {
        if (isFavorite == null) {
            com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && locationId != null) {
                isFavorite = com.example.angi_didau.data.repository.FavoritesRepository.getInstance().isFavorite(user.getUid(), locationId);
            } else {
                MutableLiveData<Boolean> empty = new MutableLiveData<>(false);
                isFavorite = empty;
            }
        }
        return isFavorite;
    }

    public void toggleFavorite(Location location) {
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && location != null) {
            Boolean currentStatus = isFavorite != null ? isFavorite.getValue() : false;
            if (Boolean.TRUE.equals(currentStatus)) {
                com.example.angi_didau.data.repository.FavoritesRepository.getInstance().removeFavorite(user.getUid(), location.getId());
            } else {
                com.example.angi_didau.data.repository.FavoritesRepository.getInstance().addFavorite(
                        user.getUid(), location.getId(), "location", location.getName(), location.getImageUrl(), "", false);
            }
        }
    }

    public void onDataLoaded() {
        isLoading.setValue(false);
    }
}
