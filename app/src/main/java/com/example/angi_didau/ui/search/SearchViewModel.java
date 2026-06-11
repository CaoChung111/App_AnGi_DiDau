package com.example.angi_didau.ui.search;

import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.angi_didau.common.constant.AppConstants;
import com.example.angi_didau.data.model.Food;
import com.example.angi_didau.data.model.Location;
import com.example.angi_didau.data.repository.FoodRepository;
import com.example.angi_didau.data.repository.LocationRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for {@link SearchActivity}.
 * <p>
 * Combines food and location search results into unified LiveData.
 * Implements debouncing via {@link Handler} to avoid firing a Firestore query
 * on every keystroke — waits {@link AppConstants#SEARCH_DEBOUNCE_MS} ms after
 * the user stops typing before triggering the query.
 * <p>
 * Design decision: Rather than creating a unified "SearchResult" model, foods and
 * locations are exposed separately so the adapter can differentiate item types
 * for navigation (food → FoodDetailActivity, location → LocationDetailActivity).
 */
public class SearchViewModel extends ViewModel {

    private final FoodRepository     foodRepository;
    private final LocationRepository locationRepository;
    private final Handler            debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable                 pendingSearch;

    private final MutableLiveData<List<Food>>     foodResults     = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Location>> locationResults = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean>        isSearching     = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean>        isEmpty         = new MutableLiveData<>(false);

    private List<Food> cachedFoods = new ArrayList<>();
    private List<Location> cachedLocations = new ArrayList<>();
    private String currentQuery = "";

    public SearchViewModel() {
        foodRepository     = FoodRepository.getInstance();
        locationRepository = LocationRepository.getInstance();
        
        foodRepository.getAllFoods().observeForever(foods -> {
            if (foods != null) {
                cachedFoods = foods;
                if (!currentQuery.isEmpty()) executeSearch(currentQuery);
            }
        });
        
        locationRepository.getAllLocations().observeForever(locations -> {
            if (locations != null) {
                cachedLocations = locations;
                if (!currentQuery.isEmpty()) executeSearch(currentQuery);
            }
        });
    }

    // ──────────────────────────────────────────
    //  Exposed LiveData
    // ──────────────────────────────────────────

    public LiveData<List<Food>>     getFoodResults()     { return foodResults; }
    public LiveData<List<Location>> getLocationResults() { return locationResults; }
    public LiveData<Boolean>        getIsSearching()     { return isSearching; }
    public LiveData<Boolean>        getIsEmpty()         { return isEmpty; }

    // ──────────────────────────────────────────
    //  Search
    // ──────────────────────────────────────────

    /**
     * Triggers a debounced search. Cancels any pending search before scheduling a new one.
     * This prevents excessive Firestore reads while the user is still typing.
     *
     * @param query The search text from the EditText
     */
    public void search(String query) {
        // Cancel any pending search
        if (pendingSearch != null) {
            debounceHandler.removeCallbacks(pendingSearch);
        }

        if (query == null || query.trim().isEmpty()) {
            foodResults.setValue(new ArrayList<>());
            locationResults.setValue(new ArrayList<>());
            isSearching.setValue(false);
            isEmpty.setValue(false);
            return;
        }

        isSearching.setValue(true);

        pendingSearch = () -> executeSearch(query.trim());
        debounceHandler.postDelayed(pendingSearch, AppConstants.SEARCH_DEBOUNCE_MS);
    }

    private void executeSearch(String query) {
        currentQuery = query;
        String lowerQuery = removeAccents(query);

        List<Food> filteredFoods = new ArrayList<>();
        for (Food f : cachedFoods) {
            if (f.getName() != null && removeAccents(f.getName()).contains(lowerQuery)) {
                filteredFoods.add(f);
            }
        }
        foodResults.setValue(filteredFoods);

        List<Location> filteredLocations = new ArrayList<>();
        for (Location l : cachedLocations) {
            if (l.getName() != null && removeAccents(l.getName()).contains(lowerQuery)) {
                filteredLocations.add(l);
            }
        }
        locationResults.setValue(filteredLocations);

        isSearching.setValue(false);
        checkIfEmpty();
    }

    private String removeAccents(String text) {
        if (text == null) return "";
        String normalized = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String unaccented = pattern.matcher(normalized).replaceAll("").toLowerCase();
        return unaccented.replaceAll("đ", "d").replaceAll("Đ", "d");
    }

    private void checkIfEmpty() {
        List<Food>     foods     = foodResults.getValue();
        List<Location> locations = locationResults.getValue();
        boolean noResults = (foods == null || foods.isEmpty())
                && (locations == null || locations.isEmpty());
        isEmpty.setValue(noResults);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up handler to prevent memory leaks when ViewModel is destroyed
        if (pendingSearch != null) {
            debounceHandler.removeCallbacks(pendingSearch);
        }
    }
}
