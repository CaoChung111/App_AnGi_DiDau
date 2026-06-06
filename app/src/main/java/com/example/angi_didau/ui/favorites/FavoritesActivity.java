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
        setupAddNoteButton();
        setupRecyclerView();
        observeViewModel();
    }

    private void setupRecyclerView() {
        RecyclerView rvFavorites = findViewById(R.id.rvFavorites);
        adapter = new FavoriteAdapter(favorite -> {
            // Future feature: View favorite detail
        });
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        rvFavorites.setAdapter(adapter);
    }

    private void setupAddNoteButton() {
        View btnAddNote = findViewById(R.id.btnAddNote);
        if (btnAddNote != null) {
            btnAddNote.setOnClickListener(v -> {
                AddNoteBottomSheet bottomSheet = AddNoteBottomSheet.newInstance();
                bottomSheet.show(getSupportFragmentManager(), "AddNoteBottomSheet");
            });
        }
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
            startActivity(new Intent(this, DiscoverActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
    }
}
