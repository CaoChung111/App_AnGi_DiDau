package com.example.angi_didau.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.angi_didau.R;
import com.example.angi_didau.adapter.RecommendedLocationAdapter;
import com.example.angi_didau.adapter.TrendingFoodAdapter;
import com.example.angi_didau.ui.random.RandomActivity;
import com.example.angi_didau.ui.profile.ProfileActivity;
import com.example.angi_didau.ui.discover.DiscoverActivity;
import com.example.angi_didau.ui.favorites.FavoritesActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

/**
 * Home screen — the main entry point after successful authentication.
 * <p>
 * This Activity is intentionally thin: it only handles view binding and
 * observing LiveData from {@link HomeViewModel}. All data-fetching and
 * business logic lives in the ViewModel and Repository layers.
 */
public class HomeActivity extends AppCompatActivity {

    private HomeViewModel homeViewModel;
    private TrendingFoodAdapter trendingFoodAdapter;
    private RecommendedLocationAdapter recommendedLocationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupBottomNav();
        setupBanners();
        setupRecyclerViews();
        observeViewModel();
    }

    private void setupBanners() {
        View cardFood = findViewById(R.id.cardFood);
        if (cardFood != null) {
            cardFood.setOnClickListener(v -> {
                startActivity(new Intent(this, com.example.angi_didau.ui.food.FoodListActivity.class));
            });
        }
        
        View cardLocation = findViewById(R.id.cardLocation);
        if (cardLocation != null) {
            cardLocation.setOnClickListener(v -> {
                startActivity(new Intent(this, com.example.angi_didau.ui.location.LocationListActivity.class));
            });
        }
    }

    private void setupRecyclerViews() {
        // Trending Foods — horizontal scroll
        RecyclerView rvTrendingFoods = findViewById(R.id.rvTrending);
        trendingFoodAdapter = new TrendingFoodAdapter();
        rvTrendingFoods.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTrendingFoods.setAdapter(trendingFoodAdapter);

        // Recommended Locations — vertical list
        RecyclerView rvRecommendedLocations = findViewById(R.id.rvRecommendations);
        recommendedLocationAdapter = new RecommendedLocationAdapter();
        rvRecommendedLocations.setLayoutManager(new LinearLayoutManager(this));
        rvRecommendedLocations.setAdapter(recommendedLocationAdapter);
    }

    private void setupBottomNav() {
        // Highlight Home Tab
        ((TextView) findViewById(R.id.tvNavHome)).setTextColor(getResources().getColor(R.color.primary_container));

        // Dim others (already default, but ensure Random is inactive)
        ((TextView) findViewById(R.id.tvNavRandom)).setTextColor(getResources().getColor(R.color.secondary));
        
        // Random Click
        findViewById(R.id.navRandom).setOnClickListener(v -> {
            startActivity(new Intent(this, RandomActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
        
        // Discover Click
        findViewById(R.id.navDiscover).setOnClickListener(v -> {
            startActivity(new Intent(this, DiscoverActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
        
        // Favorites Click
        findViewById(R.id.navFavorites).setOnClickListener(v -> {
            startActivity(new Intent(this, FavoritesActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });

        // Profile Click
        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
    }

    /**
     * Observes LiveData from ViewModel and reacts to changes.
     * Activities should ONLY call observe() here — no data manipulation.
     */
    private void observeViewModel() {
        homeViewModel.getTrendingFoods().observe(this, foods -> {
            if (foods != null && !foods.isEmpty()) {
                trendingFoodAdapter.submitList(foods);
            } else {
                // Mock data for UI presentation
                java.util.List<com.example.angi_didau.data.model.Food> dummyFoods = java.util.Arrays.asList(
                    new com.example.angi_didau.data.model.Food("1", "Waffle Chocolate Dâu Tây", "Thơm ngon", 60000, "", 4.8f),
                    new com.example.angi_didau.data.model.Food("2", "Bảo tàng Mỹ thuật", "Khám phá", 30000, "", 4.5f)
                );
                trendingFoodAdapter.submitList(dummyFoods);
            }
        });

        homeViewModel.getRecommendedLocations().observe(this, locations -> {
            if (locations != null && !locations.isEmpty()) {
                recommendedLocationAdapter.submitList(locations);
            } else {
                // Mock data for UI presentation
                java.util.List<com.example.angi_didau.data.model.Location> dummyLocations = java.util.Arrays.asList(
                    new com.example.angi_didau.data.model.Location("1", "Phở Cuốn Hương Mai", "Đặc sản Hà Nội - Ẩm thực", "", 0, 0, 4.9f),
                    new com.example.angi_didau.data.model.Location("2", "Nhà Hàng Ngon", "Không gian sân vườn - Quận 3", "", 0, 0, 4.8f)
                );
                recommendedLocationAdapter.submitList(dummyLocations);
            }
        });
    }
}
