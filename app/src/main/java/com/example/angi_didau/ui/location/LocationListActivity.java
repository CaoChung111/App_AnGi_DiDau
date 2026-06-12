package com.example.angi_didau.ui.location;

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
 * Displays all available locations in a staggered grid layout.
 * <p>
 * Fetches data from Firestore via {@link LocationListViewModel}.
 * Falls back to mock data if Firestore is empty.
 */
public class LocationListActivity extends AppCompatActivity {

    private LocationListViewModel viewModel;
    private StaggeredGridAdapter  adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_list);

        viewModel = new ViewModelProvider(this).get(LocationListViewModel.class);

        // Set Title
        TextView tvTitle = findViewById(R.id.tvListTitle);
        if (tvTitle != null) tvTitle.setText("Hôm nay Đi Đâu");

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
                filterLocations(s.toString().toLowerCase().trim());
            }
        });
    }

    private List<StaggeredItem> allLocationItems = new ArrayList<>();

    private void filterLocations(String query) {
        if (query.isEmpty()) {
            adapter.submitList(new ArrayList<>(allLocationItems));
            return;
        }
        List<StaggeredItem> filtered = new ArrayList<>();
        for (StaggeredItem item : allLocationItems) {
            if (item.getTitle().toLowerCase().contains(query)) {
                filtered.add(item);
            }
        }
        adapter.submitList(filtered);
    }

    private void setupRecyclerView() {
        RecyclerView rv = findViewById(R.id.rvSharedList);
        adapter = new StaggeredGridAdapter();
        rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        rv.setAdapter(adapter);

        // Click → LocationDetailActivity
        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(this, LocationDetailActivity.class);
            intent.putExtra(AppConstants.EXTRA_LOCATION_ID, item.getId());
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

        viewModel.getLocations().observe(this, locations -> {
            viewModel.onDataLoaded();
            if (locations != null && !locations.isEmpty()) {
                allLocationItems = new ArrayList<>();
                for (com.example.angi_didau.data.model.Location loc : locations) {
                    String priceStr = "Miễn phí";
                    if (loc.getPrice() > 0) {
                        java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
                        priceStr = format.format(loc.getPrice());
                    }
                    allLocationItems.add(new StaggeredItem(
                            loc.getId(),
                            loc.getName(),
                            loc.getAddress(),
                            loc.getAverageRating(),
                            loc.getImageUrl(),
                            priceStr
                    ));
                }
                adapter.submitList(new ArrayList<>(allLocationItems));
            } else {
                adapter.submitList(getMockData());
            }
        });
    }

    private List<StaggeredItem> getMockData() {
        List<StaggeredItem> mockData = new ArrayList<>();
        mockData.add(new StaggeredItem("1", "Dinh Độc Lập", "2.0 km • Quận 1", 4.8f, ""));
        mockData.add(new StaggeredItem("2", "Công viên Tao Đàn", "1.5 km • Quận 1", 4.6f, ""));
        mockData.add(new StaggeredItem("3", "Landmark 81", "4.5 km • Bình Thạnh", 4.9f, ""));
        mockData.add(new StaggeredItem("4", "Hồ Con Rùa", "1.0 km • Quận 3", 4.5f, ""));
        mockData.add(new StaggeredItem("5", "Thảo Cầm Viên", "3.0 km • Quận 1", 4.7f, ""));
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
