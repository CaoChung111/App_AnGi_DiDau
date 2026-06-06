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

    public void onDataLoaded() {
        isLoading.setValue(false);
    }
}
