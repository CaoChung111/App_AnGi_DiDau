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
import android.widget.ImageView;
import android.widget.TextView;

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
        setupRecyclerViews();
        observeViewModel();
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
            if (foods != null) {
                trendingFoodAdapter.submitList(foods);
            }
        });

        homeViewModel.getRecommendedLocations().observe(this, locations -> {
            if (locations != null) {
                recommendedLocationAdapter.submitList(locations);
            }
        });
    }
}
