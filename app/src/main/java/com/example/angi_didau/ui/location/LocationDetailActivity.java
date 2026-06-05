package com.example.angi_didau.ui.location;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.angi_didau.R;
import com.example.angi_didau.adapter.LocationDetailPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Displays the detail view for a selected location, with tabs for
 * Overview, Photos, and Reviews.
 * <p>
 * Receives the location ID via {@link Intent} extra key
 * {@link com.example.angi_didau.common.constant.AppConstants#EXTRA_LOCATION_ID}.
 */
public class LocationDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        bindViews();
    }

    private void bindViews() {
        // Back button — correctly typed as ImageView
        ImageView ivBack = findViewById(R.id.btnBack);
        ivBack.setOnClickListener(v -> finish());

        TabLayout tabLayout  = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        FloatingActionButton fabAddReview = findViewById(R.id.fabAddReview);

        setupTabViewPager(tabLayout, viewPager);

        // TODO: Implement Add Review bottom sheet dialog
        fabAddReview.setOnClickListener(v -> onAddReviewClicked());
    }

    private void setupTabViewPager(TabLayout tabLayout, ViewPager2 viewPager) {
        LocationDetailPagerAdapter pagerAdapter = new LocationDetailPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Tab labels from string resources — no hardcoded strings
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText(R.string.tab_overview); break;
                case 1: tab.setText(R.string.tab_photos);   break;
                case 2: tab.setText(R.string.tab_reviews);  break;
            }
        }).attach();
    }

    private void onAddReviewClicked() {
        // TODO: Open AddReviewBottomSheetFragment
    }
}
