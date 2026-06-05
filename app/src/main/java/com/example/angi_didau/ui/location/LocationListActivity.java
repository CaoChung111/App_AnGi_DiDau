package com.example.angi_didau.ui.location;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.angi_didau.R;
import com.example.angi_didau.adapter.StaggeredGridAdapter;
import com.example.angi_didau.ui.model.StaggeredItem;

import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.widget.ImageView;
import com.example.angi_didau.ui.discover.DiscoverActivity;
import com.example.angi_didau.ui.favorites.FavoritesActivity;
import com.example.angi_didau.ui.home.HomeActivity;
import com.example.angi_didau.ui.profile.ProfileActivity;
import com.example.angi_didau.ui.random.RandomActivity;

public class LocationListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_list);

        // Set Title
        TextView tvTitle = findViewById(R.id.tvListTitle);
        if (tvTitle != null) {
            tvTitle.setText("Hôm nay Đi Đâu");
        }

        // Setup RecyclerView
        RecyclerView rv = findViewById(R.id.rvSharedList);
        StaggeredGridAdapter adapter = new StaggeredGridAdapter();
        rv.setAdapter(adapter);

        // Mock Data for Locations
        List<StaggeredItem> mockData = Arrays.asList(
                new StaggeredItem("1", "Dinh Độc Lập", "2.0 km • Quận 1", 4.8f, ""),
                new StaggeredItem("2", "Công viên Tao Đàn", "1.5 km • Quận 1", 4.6f, ""),
                new StaggeredItem("3", "Landmark 81", "4.5 km • Bình Thạnh", 4.9f, ""),
                new StaggeredItem("4", "Hồ Con Rùa", "1.0 km • Quận 3", 4.5f, ""),
                new StaggeredItem("5", "Thảo Cầm Viên", "3.0 km • Quận 1", 4.7f, "")
        );
        adapter.submitList(mockData);

        setupBottomNav();
    }

    private void setupBottomNav() {
        // Highlight Home tab as we navigated from Home
        ((TextView) findViewById(R.id.tvNavHome)).setTextColor(getResources().getColor(R.color.primary_container));
        ((ImageView) findViewById(R.id.ivNavHome)).setColorFilter(getResources().getColor(R.color.primary_container));

        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
        
        findViewById(R.id.navDiscover).setOnClickListener(v -> {
            startActivity(new Intent(this, DiscoverActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });

        findViewById(R.id.navRandom).setOnClickListener(v -> {
            startActivity(new Intent(this, RandomActivity.class));
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
}
