package com.example.angi_didau.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import com.example.angi_didau.data.model.Food;
import com.example.angi_didau.data.model.Location;
import com.example.angi_didau.data.repository.FoodRepository;
import com.example.angi_didau.data.repository.LocationRepository;
import java.util.List;

/**
 * ViewModel for {@link HomeActivity}.
 * <p>
 * Survives configuration changes (rotation). Fetches and holds UI data from
 * Repositories, exposing it via LiveData. HomeActivity observes these and
 * only handles view binding — zero business/data logic in the Activity.
 */
public class HomeViewModel extends ViewModel {

    private final FoodRepository foodRepository;
    private final LocationRepository locationRepository;

    // Backing fields — lazily initialized
    private LiveData<List<Food>> trendingFoods;
    private LiveData<List<Location>> recommendedLocations;

    public HomeViewModel() {
        foodRepository = FoodRepository.getInstance();
        locationRepository = LocationRepository.getInstance();
    }

    /**
     * Returns LiveData for trending foods. Fetches from Firestore on first call.
     * Subsequent calls return the same LiveData instance (no duplicate requests).
     */
    public LiveData<List<Food>> getTrendingFoods() {
        if (trendingFoods == null) {
            trendingFoods = foodRepository.getTrendingFoods();
        }
        return trendingFoods;
    }

    /**
     * Returns LiveData for recommended locations. Fetches from Firestore on first call.
     */
    public LiveData<List<Location>> getRecommendedLocations() {
        if (recommendedLocations == null) {
            recommendedLocations = locationRepository.getRecommendedLocations();
        }
        return recommendedLocations;
    }

    /**
     * Forces a refresh of both trending foods and recommended locations.
     * Can be triggered by pull-to-refresh gestures.
     */
    public void refresh() {
        trendingFoods = foodRepository.getTrendingFoods();
        recommendedLocations = locationRepository.getRecommendedLocations();
    }
}
