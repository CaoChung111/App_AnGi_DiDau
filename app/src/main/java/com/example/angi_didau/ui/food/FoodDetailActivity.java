package com.example.angi_didau.ui.food;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.angi_didau.R;
import com.example.angi_didau.adapter.ReviewAdapter;
import com.example.angi_didau.common.constant.AppConstants;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Displays detailed information about a specific food item.
 * <p>
 * Receives the food's Firestore document ID via:
 * {@code getIntent().getStringExtra(AppConstants.EXTRA_FOOD_ID)}
 * <p>
 * Uses {@link FoodDetailViewModel} for data fetching. Handles null foodId gracefully
 * by finishing early rather than crashing.
 */
public class FoodDetailActivity extends AppCompatActivity {

    private FoodDetailViewModel viewModel;
    private ReviewAdapter reviewAdapter;
    private com.example.angi_didau.adapter.NearbyPlaceAdapter nearbyPlaceAdapter;

    // Views
    private ImageView ivFoodImage;
    private TextView  tvFoodName;
    private TextView  tvFoodDescription;
    private TextView  tvFoodPrice;
    private TextView  tvRatingValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        String foodId = getIntent().getStringExtra(AppConstants.EXTRA_FOOD_ID);
        if (foodId == null || foodId.isEmpty()) {
            finish(); // Can't display detail without an ID — bail out safely
            return;
        }

        viewModel = new ViewModelProvider(this).get(FoodDetailViewModel.class);

        bindViews();
        setupNearbyRestaurants();
        observeViewModel(foodId);
    }

    private void bindViews() {
        ivFoodImage       = findViewById(R.id.ivHero);
        tvFoodName        = findViewById(R.id.tvFoodDetailName);
        tvFoodDescription = findViewById(R.id.tvFoodDetailDescription);
        tvFoodPrice       = findViewById(R.id.tvFoodDetailPrice);
        tvRatingValue     = findViewById(R.id.tvFoodDetailRating);

        // Back button
        ImageView ivBack = findViewById(R.id.btnBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        // Action Buttons
        View btnFavorite = findViewById(R.id.btnFavorite);
        if (btnFavorite != null) {
            btnFavorite.setOnClickListener(v -> 
                android.widget.Toast.makeText(this, "Đã thêm vào yêu thích", android.widget.Toast.LENGTH_SHORT).show());
        }

        View llSaveAction = findViewById(R.id.llSaveAction);
        if (llSaveAction != null) {
            llSaveAction.setOnClickListener(v -> 
                android.widget.Toast.makeText(this, "Đã lưu", android.widget.Toast.LENGTH_SHORT).show());
        }

        View tvSeeAllNearby = findViewById(R.id.tvSeeAllNearby);
        if (tvSeeAllNearby != null) {
            tvSeeAllNearby.setOnClickListener(v -> 
                android.widget.Toast.makeText(this, "Đang tải thêm danh sách", android.widget.Toast.LENGTH_SHORT).show());
        }

    }

    private void setupNearbyRestaurants() {
        RecyclerView rvNearby = findViewById(R.id.rvNearbyRestaurants);
        if (rvNearby != null) {
            rvNearby.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            nearbyPlaceAdapter = new com.example.angi_didau.adapter.NearbyPlaceAdapter(new java.util.ArrayList<>());
            rvNearby.setAdapter(nearbyPlaceAdapter);
        }
    }

    private void observeViewModel(String foodId) {
        // Show loading indicator
        viewModel.getIsLoading().observe(this, isLoading -> {
            // progressBarFood is omitted in this layout
        });

        viewModel.getFood(foodId).observe(this, food -> {
            viewModel.onDataLoaded();

            if (food == null) return;

            // Bind food data to views
            if (tvFoodName != null) tvFoodName.setText(food.getName());
            if (tvFoodDescription != null) tvFoodDescription.setText(food.getDescription());
            if (tvFoodPrice != null) tvFoodPrice.setText(
                    String.format("%,d đ", (long) food.getPrice()));
            if (tvRatingValue != null) tvRatingValue.setText(String.valueOf(food.getAverageRating()));

            Glide.with(this)
                    .load(food.getImageUrl())
                    .placeholder(R.drawable.ic_restaurant_menu)
                    .centerCrop()
                    .into(ivFoodImage);

            // Intent Actions
            View btnShare = findViewById(R.id.btnShare);
            if (btnShare != null) {
                btnShare.setOnClickListener(v -> {
                    android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Món ngon bạn nên thử");
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Cùng đi ăn món " + food.getName() + " nhé!");
                    startActivity(android.content.Intent.createChooser(shareIntent, "Chia sẻ qua"));
                });
            }

            View llFindNearbyAction = findViewById(R.id.llFindNearbyAction);
            if (llFindNearbyAction != null) {
                llFindNearbyAction.setOnClickListener(v -> {
                    android.net.Uri gmmIntentUri = android.net.Uri.parse("geo:0,0?q=quán " + food.getName());
                    android.content.Intent mapIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    } else {
                        // Fallback to browser if Maps app is not installed
                        startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, 
                                android.net.Uri.parse("https://www.google.com/maps/search/?api=1&query=" + android.net.Uri.encode("quán " + food.getName()))));
                    }
                });
            }

            View btnDirections = findViewById(R.id.btnDirections);
            if (btnDirections != null) {
                btnDirections.setOnClickListener(v -> {
                    android.net.Uri gmmIntentUri = android.net.Uri.parse("geo:0,0?q=quán " + food.getName());
                    android.content.Intent mapIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    } else {
                        startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, 
                                android.net.Uri.parse("https://www.google.com/maps/search/?api=1&query=" + android.net.Uri.encode("quán " + food.getName()))));
                    }
                });
            }
        });

        viewModel.getReviews(foodId).observe(this, reviews -> {
            // Reviews RecyclerView is omitted in this layout
        });

        viewModel.getNearbyLocations().observe(this, locations -> {
            if (nearbyPlaceAdapter != null && locations != null) {
                // Submit list to nearby adapter (NearbyPlaceAdapter doesn't have submitList, we may need to recreate or add a method. Wait, does it have submitList?)
                // Actually NearbyPlaceAdapter currently takes List in constructor. Let's create a setLocations method.
                nearbyPlaceAdapter.setLocations(locations);
                nearbyPlaceAdapter.notifyDataSetChanged();
            }
        });
    }
}
