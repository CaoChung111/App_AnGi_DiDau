package com.example.angi_didau.ui.food;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.angi_didau.data.model.Food;
import com.example.angi_didau.data.model.Location;
import com.example.angi_didau.data.model.Review;
import com.example.angi_didau.data.repository.FoodRepository;
import com.example.angi_didau.data.repository.LocationRepository;
import com.example.angi_didau.data.repository.ReviewRepository;
import java.util.List;

/**
 * ViewModel for {@link FoodDetailActivity}.
 * <p>
 * Fetches food details and its reviews from separate repositories.
 * Survives configuration changes (screen rotation) — no duplicate Firestore reads.
 */
public class FoodDetailViewModel extends ViewModel {

    private final FoodRepository foodRepository;
    private final ReviewRepository reviewRepository;
    private final LocationRepository locationRepository;

    private LiveData<Food> food;
    private LiveData<List<Review>> reviews;
    private LiveData<List<Location>> nearbyLocations;

    /** Tracks loading state so the Activity can show/hide a progress indicator. */
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    /** Emits a one-time error message on fetch failure. */
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public FoodDetailViewModel() {
        foodRepository     = FoodRepository.getInstance();
        reviewRepository   = ReviewRepository.getInstance();
        locationRepository = LocationRepository.getInstance();
    }

    // ──────────────────────────────────────────
    //  Exposed LiveData
    // ──────────────────────────────────────────

    public LiveData<Boolean> getIsLoading()    { return isLoading; }
    public LiveData<String>  getErrorMessage() { return errorMessage; }

    /**
     * Lazily initializes and returns the LiveData for a specific food.
     * Firestore is only queried once per ViewModel lifecycle.
     *
     * @param foodId Firestore document ID for the food
     */
    public LiveData<Food> getFood(String foodId) {
        if (food == null) {
            isLoading.setValue(true);
            food = foodRepository.getFoodById(foodId);
        }
        return food;
    }

    /**
     * Lazily fetches reviews for this food.
     *
     * @param foodId Firestore document ID for the food
     */
    public LiveData<List<Review>> getReviews(String foodId) {
        if (reviews == null) {
            reviews = reviewRepository.getReviewsByEntityId(foodId);
        }
        return reviews;
    }

    /**
     * Fetch nearby locations serving this food.
     * Currently using recommended locations as a proxy since we lack geolocation.
     */
    public LiveData<List<Location>> getNearbyLocations() {
        if (nearbyLocations == null) {
            nearbyLocations = locationRepository.getRecommendedLocations();
        }
        return nearbyLocations;
    }

    /** Called by the Activity once food data has been received to stop showing spinner. */
    public void onDataLoaded() {
        isLoading.setValue(false);
    }
}
