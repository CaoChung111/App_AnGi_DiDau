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
import com.example.angi_didau.common.constant.AppConstants;
import com.example.angi_didau.common.util.SessionManager;
import com.example.angi_didau.data.model.Food;
import com.example.angi_didau.data.model.Location;
import com.example.angi_didau.ui.auth.LoginActivity;
import com.example.angi_didau.ui.discover.DiscoverActivity;
import com.example.angi_didau.ui.favorites.FavoritesActivity;
import com.example.angi_didau.ui.food.FoodDetailActivity;
import com.example.angi_didau.ui.food.FoodListActivity;
import com.example.angi_didau.ui.location.LocationDetailActivity;
import com.example.angi_didau.ui.location.LocationListActivity;
import com.example.angi_didau.ui.profile.ProfileActivity;
import com.example.angi_didau.ui.random.RandomActivity;
import com.example.angi_didau.ui.search.SearchActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import java.util.Arrays;

/**
 * Home screen — the main entry point after successful authentication.
 * <p>
 * This Activity is intentionally thin: it only handles view binding and
 * observing LiveData from {@link HomeViewModel}. All data-fetching and
 * business logic lives in the ViewModel and Repository layers.
 * <p>
 * Auth guard: Redirects to {@link LoginActivity} if the user is not logged in
 * (checked via Firebase Auth, not just SessionManager — Firebase is the source of truth).
 */
public class HomeActivity extends AppCompatActivity {

    private HomeViewModel homeViewModel;
    private TrendingFoodAdapter trendingFoodAdapter;
    private RecommendedLocationAdapter recommendedLocationAdapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(this);

        // ── Auth Guard ────────────────────────────────────────────
        // Always check Firebase Auth state, not just SessionManager.
        // Handles cases where Firebase token has expired or user was deleted.
        com.google.firebase.auth.FirebaseUser firebaseUser =
                com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            navigateToLogin();
            return;
        }

        TextView tvUserName = findViewById(R.id.tvUserName);
        if (tvUserName != null) {
            String userName = sessionManager.getUserName();
            if (userName == null || userName.isEmpty()) {
                userName = firebaseUser.getEmail() != null ? firebaseUser.getEmail().split("@")[0] : "Người dùng";
            }
            tvUserName.setText(userName + " 👋");
        }
        // ─────────────────────────────────────────────────────────

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupBottomNav();
        setupBanners();
        setupSearchBar();
        setupRecyclerViews();
        observeViewModel();
//
//        // Tạm thời gọi Seeder để tự động thêm dữ liệu mẫu.
//        // Sau khi Firebase có dữ liệu, bạn có thể xóa dòng này đi.
//        com.example.angi_didau.database.FirebaseSeeder.seedData();
    }

    private void setupSearchBar() {
        View searchBar = findViewById(R.id.cardSearch);
        if (searchBar != null) {
            searchBar.setOnClickListener(v ->
                    startActivity(new Intent(this, SearchActivity.class)));
        }
        
        // Ensure clicking the EditText also triggers the card's click
        View etSearch = findViewById(R.id.etSearch);
        if (etSearch != null) {
            etSearch.setOnClickListener(v -> 
                    startActivity(new Intent(this, SearchActivity.class)));
        }
    }

    private void setupBanners() {
        View cardFood = findViewById(R.id.cardFood);
        if (cardFood != null) {
            cardFood.setOnClickListener(v ->
                    startActivity(new Intent(this, FoodListActivity.class)));
        }

        View cardLocation = findViewById(R.id.cardLocation);
        if (cardLocation != null) {
            cardLocation.setOnClickListener(v ->
                    startActivity(new Intent(this, LocationListActivity.class)));
        }
    }

    private void setupRecyclerViews() {
        // Trending Foods — horizontal scroll
        RecyclerView rvTrendingFoods = findViewById(R.id.rvTrending);
        trendingFoodAdapter = new TrendingFoodAdapter();
        rvTrendingFoods.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTrendingFoods.setAdapter(trendingFoodAdapter);

        // Click → FoodDetailActivity
        trendingFoodAdapter.setOnItemClickListener(food -> {
            Intent intent = new Intent(this, FoodDetailActivity.class);
            intent.putExtra(AppConstants.EXTRA_FOOD_ID, food.getId());
            startActivity(intent);
        });

        // Recommended Locations — vertical list
        RecyclerView rvRecommendedLocations = findViewById(R.id.rvRecommendations);
        recommendedLocationAdapter = new RecommendedLocationAdapter();
        rvRecommendedLocations.setLayoutManager(new LinearLayoutManager(this));
        rvRecommendedLocations.setAdapter(recommendedLocationAdapter);

        // Click → LocationDetailActivity
        recommendedLocationAdapter.setOnItemClickListener(location -> {
            Intent intent = new Intent(this, LocationDetailActivity.class);
            intent.putExtra(AppConstants.EXTRA_LOCATION_ID, location.getId());
            startActivity(intent);
        });
    }

    private void setupBottomNav() {
        // Highlight Home Tab
        ((TextView) findViewById(R.id.tvNavHome)).setTextColor(getResources().getColor(R.color.primary_container));

        // Dim others
        ((TextView) findViewById(R.id.tvNavRandom)).setTextColor(getResources().getColor(R.color.secondary));

        // Navigation clicks
        findViewById(R.id.navRandom).setOnClickListener(v -> {
            startActivity(new Intent(this, RandomActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });

        findViewById(R.id.navDiscover).setOnClickListener(v -> {
            startActivity(new Intent(this, DiscoverActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });

        findViewById(R.id.navFavorites).setOnClickListener(v -> {
            startActivity(new Intent(this, FavoritesActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });

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
            trendingFoodAdapter.submitList(foods);
        });

        homeViewModel.getRecommendedLocations().observe(this, locations -> {
            recommendedLocationAdapter.submitList(locations);
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
