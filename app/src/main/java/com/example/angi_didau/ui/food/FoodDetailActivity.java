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
    private com.example.angi_didau.adapter.TrendingFoodAdapter trendingFoodAdapter;
    private String currentFoodName = "";

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
        setupReviews();
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

    }

    private void setupNearbyRestaurants() {
        RecyclerView rvNearby = findViewById(R.id.rvRelatedFoods);
        if (rvNearby != null) {
            rvNearby.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            trendingFoodAdapter = new com.example.angi_didau.adapter.TrendingFoodAdapter();
            trendingFoodAdapter.setOnItemClickListener(food -> {
                android.content.Intent intent = new android.content.Intent(this, FoodDetailActivity.class);
                intent.putExtra(AppConstants.EXTRA_FOOD_ID, food.getId());
                startActivity(intent);
            });
            rvNearby.setAdapter(trendingFoodAdapter);
        }
    }

    private void setupReviews() {
        RecyclerView rvReviews = findViewById(R.id.rvReviews);
        if (rvReviews != null) {
            rvReviews.setLayoutManager(new LinearLayoutManager(this));
            reviewAdapter = new ReviewAdapter();
            rvReviews.setAdapter(reviewAdapter);
        }


        
        View btnAddReview = findViewById(R.id.btnAddReview);
        if (btnAddReview != null) {
            btnAddReview.setOnClickListener(v -> {
                String foodId = getIntent().getStringExtra(AppConstants.EXTRA_FOOD_ID);
                if (foodId != null) {
                    com.example.angi_didau.ui.location.fragment.AddReviewBottomSheet bottomSheet = 
                            com.example.angi_didau.ui.location.fragment.AddReviewBottomSheet.newInstance(foodId, currentFoodName);
                    bottomSheet.show(getSupportFragmentManager(), "AddReviewBottomSheet");
                }
            });
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
            if (tvFoodName != null) {
                tvFoodName.setText(food.getName());
                currentFoodName = food.getName();
            }
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

            View btnFavorite = findViewById(R.id.btnFavorite);
            if (btnFavorite != null) {
                btnFavorite.setOnClickListener(v -> {
                    viewModel.toggleFavorite(food);
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
            if (reviewAdapter != null) {
                reviewAdapter.submitList(reviews);
            }
            int count = reviews != null ? reviews.size() : 0;
            TextView tvTotalReviews = findViewById(R.id.tvTotalReviews);
            if (tvTotalReviews != null) {
                tvTotalReviews.setText(count + " đánh giá");
            }
            
            // Recalculate average rating locally
            if (count > 0) {
                float sum = 0;
                for (com.example.angi_didau.data.model.Review review : reviews) {
                    sum += review.getRating();
                }
                float avg = sum / count;
                
                if (tvRatingValue != null) {
                    tvRatingValue.setText(String.format(java.util.Locale.US, "%.1f", avg));
                }
                
                // Save to DB
                com.example.angi_didau.data.repository.FoodRepository.getInstance().updateAverageRating(foodId, avg);
            }
        });

        viewModel.getIsFavorite(foodId).observe(this, isFavorite -> {
            ImageView btnFavorite = findViewById(R.id.btnFavorite);
            if (btnFavorite != null) {
                if (Boolean.TRUE.equals(isFavorite)) {
                    btnFavorite.setImageResource(R.drawable.ic_favorite);
                    btnFavorite.setColorFilter(androidx.core.content.ContextCompat.getColor(this, R.color.primary));
                } else {
                    btnFavorite.setImageResource(R.drawable.ic_favorite_border);
                    btnFavorite.setColorFilter(androidx.core.content.ContextCompat.getColor(this, R.color.on_surface));
                }
            }
        });

        viewModel.getRelatedFoods().observe(this, foods -> {
            if (trendingFoodAdapter != null && foods != null) {
                trendingFoodAdapter.submitList(foods);
            }
        });
    }
}
