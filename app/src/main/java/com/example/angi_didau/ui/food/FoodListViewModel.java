package com.example.angi_didau.ui.food;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.angi_didau.data.model.Food;
import com.example.angi_didau.data.repository.FoodRepository;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for {@link FoodListActivity}.
 * <p>
 * Fetches food list from Firestore using infinite scroll pagination.
 */
public class FoodListViewModel extends ViewModel {

    private final FoodRepository foodRepository;

    private final MutableLiveData<List<Food>> foods = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private DocumentSnapshot lastVisible = null;
    private boolean isLastPage = false;
    private boolean isFetching = false;
    private static final int PAGE_SIZE = 50;

    public FoodListViewModel() {
        foodRepository = FoodRepository.getInstance();
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public LiveData<List<Food>> getFoods() {
        if (foods.getValue() == null || foods.getValue().isEmpty()) {
            loadNextPage();
        }
        return foods;
    }

    public boolean isFetching() { return isFetching; }
    public boolean isLastPage() { return isLastPage; }

    public void loadNextPage() {
        if (isLastPage || isFetching) return;
        isFetching = true;
        isLoading.setValue(true);

        foodRepository.getFoodsPage(lastVisible, PAGE_SIZE, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Food> newFoods = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult()) {
                    Food food = doc.toObject(Food.class);
                    if (food != null) {
                        food.setId(doc.getId());
                        newFoods.add(food);
                    }
                }

                List<Food> current = foods.getValue();
                if (current == null) current = new ArrayList<>();
                current.addAll(newFoods);
                foods.setValue(current);

                if (task.getResult().size() < PAGE_SIZE) {
                    isLastPage = true;
                } else {
                    int lastIndex = task.getResult().size() - 1;
                    lastVisible = task.getResult().getDocuments().get(lastIndex);
                }
            }
            isFetching = false;
            isLoading.setValue(false);
        });
    }

    public void refresh() {
        lastVisible = null;
        isLastPage = false;
        isFetching = false;
        foods.setValue(new ArrayList<>());
        loadNextPage();
    }

    public void onDataLoaded() {
        // No-op or keep for compatibility
    }
}
