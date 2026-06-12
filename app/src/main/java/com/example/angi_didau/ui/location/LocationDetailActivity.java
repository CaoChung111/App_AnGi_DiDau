package com.example.angi_didau.ui.location;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.angi_didau.R;

import com.example.angi_didau.common.constant.AppConstants;
import com.example.angi_didau.ui.location.fragment.AddReviewBottomSheet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Displays the detail view for a selected location, with tabs for
 * Overview, Photos, and Reviews.
 * <p>
 * Receives the location ID via Intent extra {@link AppConstants#EXTRA_LOCATION_ID}.
 * Shares {@link LocationDetailViewModel} with child Fragments so they don't need
 * their own Firestore calls.
 */
public class LocationDetailActivity extends AppCompatActivity {

    private LocationDetailViewModel viewModel;
    private String currentLocationName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        String locationId = getIntent().getStringExtra(AppConstants.EXTRA_LOCATION_ID);
        if (locationId == null || locationId.isEmpty()) {
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(LocationDetailViewModel.class);

        // MUST observe ViewModel first to initialize the locationId inside the ViewModel.
        // Otherwise, child Fragments (e.g., ReviewsFragment) will call getReviews() and get a Null LiveData.
        observeViewModel(locationId);
        bindViews(locationId);
    }

    private void bindViews(String locationId) {
        // Back button
        ImageView ivBack = findViewById(R.id.btnBack);
        if (ivBack != null) ivBack.setOnClickListener(v -> finish());

        setupReviews();

        FloatingActionButton fabAddReview = findViewById(R.id.fabAddReview);
        if (fabAddReview != null) {
            fabAddReview.setOnClickListener(v -> onAddReviewClicked(locationId));
        }

        setupHeroGallery();
    }

    private void setupHeroGallery() {
        androidx.viewpager2.widget.ViewPager2 vpLocationHero = findViewById(R.id.vpLocationHero);
        if (vpLocationHero != null) {
            vpLocationHero.setAdapter(new com.example.angi_didau.adapter.PhotoGalleryAdapter(new java.util.ArrayList<>(), true));
        }
    }

    private void observeViewModel(String locationId) {
        viewModel.getLocation(locationId).observe(this, location -> {
            viewModel.onDataLoaded();
            if (location == null) return;

            TextView tvName = findViewById(R.id.tvLocationName);
            if (tvName != null) {
                tvName.setText(location.getName());
                currentLocationName = location.getName();
            }

            TextView tvAddress = findViewById(R.id.tvLocationCategory);
            if (tvAddress != null) tvAddress.setText(location.getAddress());

            TextView tvRating = findViewById(R.id.tvLocationRating);
            if (tvRating != null) tvRating.setText(String.valueOf(location.getAverageRating()));

            androidx.viewpager2.widget.ViewPager2 vpLocationHero = findViewById(R.id.vpLocationHero);
            if (vpLocationHero != null) {
                java.util.List<String> realPhotos = location.getImageUrls();
                if (realPhotos == null || realPhotos.isEmpty()) {
                    realPhotos = new java.util.ArrayList<>();
                    if (location.getImageUrl() != null && !location.getImageUrl().isEmpty()) {
                        realPhotos.add(location.getImageUrl());
                    }
                }
                vpLocationHero.setAdapter(new com.example.angi_didau.adapter.PhotoGalleryAdapter(realPhotos, true));
                
                TextView tvLocationPhotoCount = findViewById(R.id.tvLocationPhotoCount);
                if (realPhotos.size() > 1 && tvLocationPhotoCount != null) {
                    tvLocationPhotoCount.setVisibility(android.view.View.VISIBLE);
                    tvLocationPhotoCount.setText("1/" + realPhotos.size() + " Ảnh (Vuốt ➔)");
                    
                    final int totalPhotos = realPhotos.size();
                    vpLocationHero.registerOnPageChangeCallback(new androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            tvLocationPhotoCount.setText((position + 1) + "/" + totalPhotos + " Ảnh (Vuốt ➔)");
                        }
                    });
                }
            }

            TextView tvOverviewAddress = findViewById(R.id.tvOverviewAddress);
            if (tvOverviewAddress != null) tvOverviewAddress.setText(location.getAddress());

            TextView tvOverviewAbout = findViewById(R.id.tvOverviewAbout);
            if (tvOverviewAbout != null) {
                if (location.getDescription() != null && !location.getDescription().isEmpty()) {
                    tvOverviewAbout.setText(location.getDescription());
                } else {
                    tvOverviewAbout.setText("Chưa có mô tả chi tiết cho địa điểm này.");
                }
            }
            
            TextView tvLocationPrice = findViewById(R.id.tvLocationPrice);
            if (tvLocationPrice != null) {
                if (location.getPrice() == 0) {
                    tvLocationPrice.setText("Miễn phí");
                    tvLocationPrice.setTextColor(android.graphics.Color.parseColor("#388E3C")); // Green
                } else {
                    java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
                    tvLocationPrice.setText(format.format(location.getPrice()));
                    tvLocationPrice.setTextColor(getResources().getColor(R.color.on_surface));
                }
            }

            ImageView btnFavorite = findViewById(R.id.btnFavorite);
            if (btnFavorite != null) {
                btnFavorite.setOnClickListener(v -> {
                    viewModel.toggleFavorite(location);
                });
            }

            TextView tvAverageRatingBig = findViewById(R.id.tvAverageRatingBig);
            if (tvAverageRatingBig != null) {
                tvAverageRatingBig.setText(String.valueOf(location.getAverageRating()));
            }

            RatingBar rbAverageRating = findViewById(R.id.rbAverageRating);
            if (rbAverageRating != null) {
                rbAverageRating.setRating((float) location.getAverageRating());
            }

            // Setup Navigation Buttons
            android.view.View.OnClickListener navListener = v -> {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                
                // Fallback ếu Firebase không có tọa độ
                if (lat == 0.0 && lng == 0.0) {
                    lat = 21.05398129592764;
                    lng = 105.7349780492407;
                }
                // Mở Google Maps với tọa độ thực và tên địa điểm
                String uriStr = String.format(java.util.Locale.US, "geo:0,0?q=%f,%f(%s)", lat, lng, location.getName());
                android.net.Uri uri = android.net.Uri.parse(uriStr);
                android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW, uri);
                intent.setPackage("com.google.android.apps.maps");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    // Fallback mở browser tìm kiếm theo tên
                    String mapUrl = "https://www.google.com/maps/search/?api=1&query=" + android.net.Uri.encode(location.getName() + " " + location.getAddress());
                    startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(mapUrl)));
                }
            };



            android.widget.Button btnNavigateTop = findViewById(R.id.btnNavigateTop);
            if (btnNavigateTop != null) {
                btnNavigateTop.setOnClickListener(navListener);
            }
        });

        viewModel.getReviews().observe(this, reviews -> {
            androidx.recyclerview.widget.RecyclerView rvReviews = findViewById(R.id.rvReviews);
            if (rvReviews != null && rvReviews.getAdapter() instanceof com.example.angi_didau.adapter.ReviewAdapter) {
                ((com.example.angi_didau.adapter.ReviewAdapter) rvReviews.getAdapter()).submitList(reviews);
            }

            int count = reviews != null ? reviews.size() : 0;
            TextView tvTotalReviews = findViewById(R.id.tvTotalReviews);
            if (tvTotalReviews != null) {
                tvTotalReviews.setText("Dựa trên " + count + " đánh giá");
            }
            
            // Recalculate average rating locally
            if (count > 0) {
                float sum = 0;
                for (com.example.angi_didau.data.model.Review review : reviews) {
                    sum += review.getRating();
                }
                float avg = sum / count;
                
                TextView tvAverageRatingBig = findViewById(R.id.tvAverageRatingBig);
                if (tvAverageRatingBig != null) {
                    tvAverageRatingBig.setText(String.format(java.util.Locale.US, "%.1f", avg));
                }

                RatingBar rbAverageRating = findViewById(R.id.rbAverageRating);
                if (rbAverageRating != null) {
                    rbAverageRating.setRating(avg);
                }
                
                TextView tvRating = findViewById(R.id.tvLocationRating);
                if (tvRating != null) {
                    tvRating.setText(String.format(java.util.Locale.US, "%.1f", avg));
                }
                
                // Save to DB
                com.example.angi_didau.data.repository.LocationRepository.getInstance().updateAverageRating(locationId, avg);
            }
        });

        viewModel.getIsFavorite(locationId).observe(this, isFavorite -> {
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
    }

    private void setupReviews() {
        androidx.recyclerview.widget.RecyclerView rvReviews = findViewById(R.id.rvReviews);
        if (rvReviews != null) {
            rvReviews.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
            rvReviews.setAdapter(new com.example.angi_didau.adapter.ReviewAdapter());
        }
    }

    private void onAddReviewClicked(String locationId) {
        AddReviewBottomSheet bottomSheet = AddReviewBottomSheet.newInstance(locationId, currentLocationName);
        bottomSheet.show(getSupportFragmentManager(), "AddReviewBottomSheet");
    }
}
