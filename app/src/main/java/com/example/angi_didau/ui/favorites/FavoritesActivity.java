package com.example.angi_didau.ui.favorites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.angi_didau.R;
import com.example.angi_didau.adapter.FavoriteAdapter;
import com.example.angi_didau.ui.discover.DiscoverActivity;
import com.example.angi_didau.ui.home.HomeActivity;
import com.example.angi_didau.ui.profile.ProfileActivity;
import com.example.angi_didau.ui.random.RandomActivity;

/**
 * Favorites screen — shows the user's saved items with notes.
 * <p>
 * Uses {@link FavoritesViewModel} to fetch favorites from Firestore.
 * The AddNote button opens {@link AddNoteBottomSheet} for adding new items.
 */
public class FavoritesActivity extends AppCompatActivity {

    private FavoritesViewModel viewModel;

    private FavoriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        viewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);

        setupBottomNav();
        setupRecyclerView();
        observeViewModel();

        com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton fabAddCustomNote = findViewById(R.id.fabAddCustomNote);
        if (fabAddCustomNote != null) {
            fabAddCustomNote.setOnClickListener(v -> {
                CustomNoteBottomSheet.newInstance().show(getSupportFragmentManager(), "CustomNoteBottomSheet");
            });
        }
    }

    private void setupRecyclerView() {
        RecyclerView rvFavorites = findViewById(R.id.rvFavorites);
        adapter = new FavoriteAdapter(new FavoriteAdapter.OnFavoriteClickListener() {
            @Override
            public void onFavoriteClick(java.util.Map<String, Object> favorite) {
                Boolean isCustom = (Boolean) favorite.get("isCustom");
                if (Boolean.TRUE.equals(isCustom)) {
                    Toast.makeText(FavoritesActivity.this, "Đây là ghi chú cá nhân", Toast.LENGTH_SHORT).show();
                    return;
                }

                String type = (String) favorite.get("type");
                String id = (String) favorite.get("entityId");
                if (com.example.angi_didau.common.constant.AppConstants.ENTITY_TYPE_FOOD.equals(type)) {
                    Intent intent = new Intent(FavoritesActivity.this, com.example.angi_didau.ui.food.FoodDetailActivity.class);
                    intent.putExtra(com.example.angi_didau.common.constant.AppConstants.EXTRA_FOOD_ID, id);
                    startActivity(intent);
                } else if (com.example.angi_didau.common.constant.AppConstants.ENTITY_TYPE_LOCATION.equals(type)) {
                    Intent intent = new Intent(FavoritesActivity.this, com.example.angi_didau.ui.location.LocationDetailActivity.class);
                    intent.putExtra(com.example.angi_didau.common.constant.AppConstants.EXTRA_LOCATION_ID, id);
                    startActivity(intent);
                }
            }

            @Override
            public void onRemoveClick(java.util.Map<String, Object> favorite) {
                String id = (String) favorite.get("entityId");
                if (id != null) {
                    String imageUrl = (String) favorite.get("imageUrl");
                    if (imageUrl != null && !imageUrl.startsWith("http") && !imageUrl.isEmpty()) {
                        try {
                            java.io.File file = new java.io.File(imageUrl);
                            if (file.exists()) {
                                file.delete();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    viewModel.removeFavorite(id);
                }
            }
        });
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        rvFavorites.setAdapter(adapter);
    }



    private void observeViewModel() {
        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            // progress bar omitted
        });

        // Observe favorites data
        viewModel.getFavorites().observe(this, favorites -> {
            viewModel.onDataLoaded();
            adapter.submitList(favorites);

            View layoutEmptyState = findViewById(R.id.layoutEmptyState);
            RecyclerView rvFavorites = findViewById(R.id.rvFavorites);
            if (favorites == null || favorites.isEmpty()) {
                if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.VISIBLE);
                if (rvFavorites != null) rvFavorites.setVisibility(View.GONE);
            } else {
                if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
                if (rvFavorites != null) rvFavorites.setVisibility(View.VISIBLE);
            }
        });

        // Observe save note result
        viewModel.getSaveNoteResult().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Đã lưu ghi chú thành công!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNav() {
        // Highlight Favorites Tab
        ((TextView) findViewById(R.id.tvNavFavorites)).setTextColor(getResources().getColor(R.color.primary_container));
        ((ImageView) findViewById(R.id.ivNavFavorites)).setColorFilter(getResources().getColor(R.color.primary_container));

        // Dim others
        ((TextView) findViewById(R.id.tvNavHome)).setTextColor(getResources().getColor(R.color.secondary));
        ((ImageView) findViewById(R.id.ivNavHome)).setColorFilter(getResources().getColor(R.color.secondary));
        ((TextView) findViewById(R.id.tvNavProfile)).setTextColor(getResources().getColor(R.color.secondary));
        ((ImageView) findViewById(R.id.ivNavProfile)).setColorFilter(getResources().getColor(R.color.secondary));
        ((TextView) findViewById(R.id.tvNavDiscover)).setTextColor(getResources().getColor(R.color.secondary));
        ((ImageView) findViewById(R.id.ivNavDiscover)).setColorFilter(getResources().getColor(R.color.secondary));
        ((TextView) findViewById(R.id.tvNavRandom)).setTextColor(getResources().getColor(R.color.secondary));

        // Clicks
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
        findViewById(R.id.navRandom).setOnClickListener(v -> {
            startActivity(new Intent(this, RandomActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
        findViewById(R.id.navDiscover).setOnClickListener(v -> {
            if (com.example.angi_didau.common.util.SessionHelper.checkGuestAndRequireLogin(this)) return;
            startActivity(new Intent(this, com.example.angi_didau.ui.discover.DiscoverActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
    }
}
