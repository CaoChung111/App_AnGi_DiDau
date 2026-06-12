package com.example.angi_didau.ui.food;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.angi_didau.R;
import com.example.angi_didau.adapter.StaggeredGridAdapter;
import com.example.angi_didau.common.constant.AppConstants;
import com.example.angi_didau.ui.discover.DiscoverActivity;
import com.example.angi_didau.ui.favorites.FavoritesActivity;
import com.example.angi_didau.ui.home.HomeActivity;
import com.example.angi_didau.ui.model.StaggeredItem;
import com.example.angi_didau.ui.profile.ProfileActivity;
import com.example.angi_didau.ui.random.RandomActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays all available foods in a staggered grid layout.
 * <p>
 * Fetches data from Firestore via {@link FoodListViewModel}.
 * Falls back to mock data if Firestore is empty (for demo/development).
 */
public class FoodListActivity extends AppCompatActivity {

    private FoodListViewModel viewModel;
    private StaggeredGridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_list);

        viewModel = new ViewModelProvider(this).get(FoodListViewModel.class);

        // Set Title
        TextView tvTitle = findViewById(R.id.tvListTitle);
        if (tvTitle != null) tvTitle.setText("Hôm nay Ăn Gì");

        setupRecyclerView();
        observeViewModel();
        setupBottomNav();
        setupSearch();
    }

    private void setupSearch() {
        android.widget.EditText etSearch = findViewById(R.id.etListSearch);
        if (etSearch == null) return;
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFoods(s.toString().toLowerCase().trim());
            }
        });
    }

    private List<StaggeredItem> allFoodItems = new ArrayList<>();

    private void filterFoods(String query) {
        if (query.isEmpty()) {
            adapter.submitList(new ArrayList<>(allFoodItems));
            return;
        }
        List<StaggeredItem> filtered = new ArrayList<>();
        for (StaggeredItem item : allFoodItems) {
            if (item.getTitle().toLowerCase().contains(query)) {
                filtered.add(item);
            }
        }
        adapter.submitList(filtered);
    }

    private void setupRecyclerView() {
        RecyclerView rv = findViewById(R.id.rvSharedList);
        adapter = new StaggeredGridAdapter();

        // StaggeredGridLayoutManager: 2 columns, vertical
        rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        rv.setAdapter(adapter);

        // Click → FoodDetailActivity
        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(this, FoodDetailActivity.class);
            intent.putExtra(AppConstants.EXTRA_FOOD_ID, item.getId());
            startActivity(intent);
        });

        // Infinite Scroll via NestedScrollView
        androidx.core.widget.NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);
        if (nestedScrollView != null) {
            nestedScrollView.setOnScrollChangeListener((androidx.core.widget.NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                // Check if we scrolled to the bottom
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    if (!viewModel.isFetching() && !viewModel.isLastPage()) {
                        viewModel.loadNextPage();
                    }
                }
            });
        }
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            View progressBar = findViewById(R.id.pbLoading);
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getFoods().observe(this, foods -> {
            viewModel.onDataLoaded();
            if (foods != null && !foods.isEmpty()) {
                allFoodItems = new ArrayList<>();
                for (com.example.angi_didau.data.model.Food food : foods) {
                    String priceStr = "Miễn phí";
                    if (food.getPrice() > 0) {
                        java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
                        priceStr = format.format(food.getPrice());
                    }
                    allFoodItems.add(new StaggeredItem(
                            food.getId(),
                            food.getName(),
                            String.format("%.1f ⭐", food.getAverageRating()),
                            food.getAverageRating(),
                            food.getImageUrl(),
                            priceStr
                    ));
                }
                adapter.submitList(new ArrayList<>(allFoodItems));
            } else {
                // Mock data for demo
                adapter.submitList(getMockData());
            }
        });
    }

    private List<StaggeredItem> getMockData() {
        List<StaggeredItem> mockData = new ArrayList<>();
        mockData.add(new StaggeredItem("1", "The Morning...", "1.2 km • Thảo Điền", 4.8f, ""));
        mockData.add(new StaggeredItem("2", "The Prime Cut", "3.1 km • Quận 7", 4.5f, ""));
        mockData.add(new StaggeredItem("3", "Bánh Mì 36", "0.8 km • Quận 1", 4.9f, ""));
        mockData.add(new StaggeredItem("4", "Urban Grill", "1.5 km • Bình Thạnh", 4.6f, ""));
        mockData.add(new StaggeredItem("5", "HIVE District 2", "2.5 km • Quận 2", 4.7f, ""));
        mockData.add(new StaggeredItem("6", "Midnight Kitchen", "4.2 km • Quận 1", 5.0f, ""));
        return mockData;
    }

    private void setupBottomNav() {
        ((TextView) findViewById(R.id.tvNavHome)).setTextColor(getResources().getColor(R.color.primary_container));
        ((ImageView) findViewById(R.id.ivNavHome)).setColorFilter(getResources().getColor(R.color.primary_container));

        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
        findViewById(R.id.navDiscover).setOnClickListener(v -> {
            if (com.example.angi_didau.common.util.SessionHelper.checkGuestAndRequireLogin(this)) return;
            startActivity(new Intent(this, com.example.angi_didau.ui.discover.DiscoverActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
        findViewById(R.id.navRandom).setOnClickListener(v -> {
            startActivity(new Intent(this, RandomActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
        findViewById(R.id.navFavorites).setOnClickListener(v -> {
            if (com.example.angi_didau.common.util.SessionHelper.checkGuestAndRequireLogin(this)) return;
            startActivity(new Intent(this, com.example.angi_didau.ui.favorites.FavoritesActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
    }
}
