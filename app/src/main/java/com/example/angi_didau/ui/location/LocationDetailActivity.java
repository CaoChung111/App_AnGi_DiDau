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
import com.example.angi_didau.adapter.LocationDetailPagerAdapter;
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

        bindViews(locationId);
        observeViewModel(locationId);
    }

    private void bindViews(String locationId) {
        // Back button
        ImageView ivBack = findViewById(R.id.btnBack);
        if (ivBack != null) ivBack.setOnClickListener(v -> finish());

        TabLayout  tabLayout  = findViewById(R.id.tabLayout);
        ViewPager2 viewPager  = findViewById(R.id.viewPager);

        setupTabViewPager(tabLayout, viewPager);

        FloatingActionButton fabAddReview = findViewById(R.id.fabAddReview);
        if (fabAddReview != null) {
            fabAddReview.setOnClickListener(v -> onAddReviewClicked(locationId));
        }

        setupHeroGallery();
    }

    private void setupHeroGallery() {
        androidx.viewpager2.widget.ViewPager2 vpLocationHero = findViewById(R.id.vpLocationHero);
        if (vpLocationHero != null) {
            vpLocationHero.setAdapter(new com.example.angi_didau.adapter.PhotoGalleryAdapter(new java.util.ArrayList<>()));
        }
    }

    private void observeViewModel(String locationId) {
        viewModel.getLocation(locationId).observe(this, location -> {
            viewModel.onDataLoaded();
            if (location == null) return;

            TextView tvName = findViewById(R.id.tvLocationName);
            if (tvName != null) tvName.setText(location.getName());

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
                vpLocationHero.setAdapter(new com.example.angi_didau.adapter.PhotoGalleryAdapter(realPhotos));
            }
        });
    }

    private void setupTabViewPager(TabLayout tabLayout, ViewPager2 viewPager) {
        LocationDetailPagerAdapter pagerAdapter = new LocationDetailPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText(R.string.tab_overview); break;
                case 1: tab.setText(R.string.tab_photos);   break;
                case 2: tab.setText(R.string.tab_reviews);  break;
            }
        }).attach();
    }

    private void onAddReviewClicked(String locationId) {
        AddReviewBottomSheet bottomSheet = AddReviewBottomSheet.newInstance(locationId);
        bottomSheet.show(getSupportFragmentManager(), "AddReviewBottomSheet");
    }
}
