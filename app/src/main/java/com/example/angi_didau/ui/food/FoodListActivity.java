package com.example.angi_didau.ui.food;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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

public class FoodListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_list);

        // Set Title
        TextView tvTitle = findViewById(R.id.tvListTitle);
        if (tvTitle != null) {
            tvTitle.setText("Hôm nay Ăn Gì");
        }

        // Setup RecyclerView
        RecyclerView rv = findViewById(R.id.rvSharedList);
        StaggeredGridAdapter adapter = new StaggeredGridAdapter();
        rv.setAdapter(adapter);

        // Mock Data to match screenshot
        List<StaggeredItem> mockData = Arrays.asList(
                new StaggeredItem("1", "The Morning...", "1.2 km • Thảo Điền", 4.8f, ""),
                new StaggeredItem("2", "The Prime Cut", "3.1 km • Quận 7", 4.5f, ""),
                new StaggeredItem("3", "Bánh Mì 36", "0.8 km • Quận 1", 4.9f, ""),
                new StaggeredItem("4", "Urban Grill", "1.5 km • Bình Thạnh", 4.6f, ""),
                new StaggeredItem("5", "HIVE District 2", "2.5 km • Quận 2", 4.7f, ""),
                new StaggeredItem("6", "Midnight...", "4.2 km • Quận 1", 5.0f, "")
        );
        adapter.submitList(mockData);

        setupBottomNav();
    }

    private void setupBottomNav() {
        // Since this is a sub-activity, we don't highlight any specific tab 
        // to show that the user is currently deep inside a list, or we could highlight Home.
        // For now, let's keep them all unselected or highlight Home.
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
