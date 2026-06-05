package com.example.angi_didau.ui.favorites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.angi_didau.R;
import com.example.angi_didau.ui.discover.DiscoverActivity;
import com.example.angi_didau.ui.home.HomeActivity;
import com.example.angi_didau.ui.profile.ProfileActivity;
import com.example.angi_didau.ui.random.RandomActivity;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        setupBottomNav();
        
        findViewById(R.id.btnAddNote).setOnClickListener(v -> {
            AddNoteBottomSheet bottomSheet = AddNoteBottomSheet.newInstance();
            bottomSheet.show(getSupportFragmentManager(), "AddNoteBottomSheet");
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
