package com.example.angi_didau.ui.food;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.angi_didau.data.model.Food;
import com.example.angi_didau.data.repository.FoodRepository;
import java.util.List;

/**
 * ViewModel for {@link FoodListActivity}.
 * <p>
 * Fetches the complete food list from Firestore. Survives rotation — only one
 * Firestore read is triggered per ViewModel lifecycle.
 */
public class FoodListViewModel extends ViewModel {

    private final FoodRepository foodRepository;

    private LiveData<List<Food>> foods;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);

    public FoodListViewModel() {
        foodRepository = FoodRepository.getInstance();
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }

    /**
     * Returns LiveData for all foods. Firestore is queried only on the first call.
     */
    public LiveData<List<Food>> getFoods() {
        if (foods == null) {
            foods = foodRepository.getAllFoods();
        }
        return foods;
    }

    /**
     * Forces a fresh fetch from Firestore. Call on pull-to-refresh.
     */
    public void refresh() {
        isLoading.setValue(true);
        foods = foodRepository.getAllFoods();
    }

    public void onDataLoaded() {
        isLoading.setValue(false);
    }
}
