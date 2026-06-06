package com.example.angi_didau.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.angi_didau.R;
import com.example.angi_didau.common.util.SessionManager;
import com.example.angi_didau.ui.auth.LoginActivity;
import com.example.angi_didau.ui.discover.DiscoverActivity;
import com.example.angi_didau.ui.favorites.FavoritesActivity;
import com.example.angi_didau.ui.home.HomeActivity;
import com.example.angi_didau.ui.random.RandomActivity;

/**
 * Profile screen showing current user info and providing logout functionality.
 * <p>
 * Loads Firestore user document via {@link ProfileViewModel}. Falls back
 * gracefully to Firebase Auth data if Firestore document is not found.
 * Logout clears both Firebase Auth session and local SharedPreferences via SessionManager.
 */
public class ProfileActivity extends AppCompatActivity {

    private ProfileViewModel viewModel;
    private SessionManager   sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        viewModel      = new ViewModelProvider(this).get(ProfileViewModel.class);
        sessionManager = new SessionManager(this);

        setupBottomNav();
        setupLogout();
        setupMenuOptions();
        observeViewModel();
    }

    private void observeViewModel() {
        TextView tvName  = findViewById(R.id.tvProfileName);
        TextView tvEmail = findViewById(R.id.tvProfileBio);
        ImageView ivAvatar = findViewById(R.id.civProfileAvatar);
        
        TextView tvStatPlaces = findViewById(R.id.tvStatPlaces);
        TextView tvStatFoods = findViewById(R.id.tvStatFoods);
        TextView tvStatSaved = findViewById(R.id.tvStatSaved);

        viewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                if (tvName  != null) tvName.setText(user.getUsername());
                if (tvEmail != null) tvEmail.setText(user.getEmail());

                if (ivAvatar != null && user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                    Glide.with(this)
                            .load(user.getAvatarUrl())
                            .placeholder(R.drawable.ic_person)
                            .circleCrop()
                            .into(ivAvatar);
                }
            } else {
                // Fallback to Firebase Auth
                com.google.firebase.auth.FirebaseUser fbUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
                if (fbUser != null) {
                    if (tvName != null) {
                        String name = fbUser.getDisplayName();
                        if (name == null || name.isEmpty()) {
                            name = sessionManager.getUserName();
                        }
                        if (name == null || name.isEmpty()) {
                            name = fbUser.getEmail() != null ? fbUser.getEmail().split("@")[0] : "Người dùng";
                        }
                        tvName.setText(name);
                    }
                    if (tvEmail != null) tvEmail.setText(fbUser.getEmail());
                }
            }
        });
        
        viewModel.getPlacesVisitedCount().observe(this, count -> {
            if (tvStatPlaces != null) tvStatPlaces.setText(String.valueOf(count));
        });
        
        viewModel.getFoodsTriedCount().observe(this, count -> {
            if (tvStatFoods != null) tvStatFoods.setText(String.valueOf(count));
        });
        
        viewModel.getSavedPlansCount().observe(this, count -> {
            if (tvStatSaved != null) tvStatSaved.setText(String.valueOf(count));
        });

        viewModel.getLogoutResult().observe(this, shouldLogout -> {
            if (Boolean.TRUE.equals(shouldLogout)) {
                // Clear local session
                sessionManager.clearSession();
                // Navigate to Login, clear entire back stack
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void setupLogout() {
        View btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> confirmLogout());
        }
    }

    private void setupMenuOptions() {
        View llFavorites = findViewById(R.id.llFavorites);
        if (llFavorites != null) {
            llFavorites.setOnClickListener(v -> {
                startActivity(new Intent(this, FavoritesActivity.class));
            });
        }

        View llReviews = findViewById(R.id.llReviews);
        if (llReviews != null) {
            llReviews.setOnClickListener(v -> {
                startActivity(new android.content.Intent(this, UserReviewsActivity.class));
            });
        }

        View llSavedPlans = findViewById(R.id.llSavedPlans);
        if (llSavedPlans != null) {
            llSavedPlans.setOnClickListener(v -> {
                startActivity(new android.content.Intent(this, SavedPlansActivity.class));
            });
        }
    }

    /**
     * Shows a confirmation dialog before logging out.
     * Prevents accidental logouts.
     */
    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> viewModel.logout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void setupBottomNav() {
        // Highlight Profile Tab
        ((TextView) findViewById(R.id.tvNavProfile)).setTextColor(getResources().getColor(R.color.primary_container));
        ((ImageView) findViewById(R.id.ivNavProfile)).setColorFilter(getResources().getColor(R.color.primary_container));

        // Dim others
        ((TextView) findViewById(R.id.tvNavHome)).setTextColor(getResources().getColor(R.color.secondary));
        ((ImageView) findViewById(R.id.ivNavHome)).setColorFilter(getResources().getColor(R.color.secondary));
        ((TextView) findViewById(R.id.tvNavRandom)).setTextColor(getResources().getColor(R.color.secondary));

        // Clicks
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
        findViewById(R.id.navFavorites).setOnClickListener(v -> {
            startActivity(new Intent(this, FavoritesActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
        findViewById(R.id.navRandom).setOnClickListener(v -> {
            startActivity(new Intent(this, RandomActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
    }
}
