package com.example.angi_didau.activities.location;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.angi_didau.R;
import com.example.angi_didau.adapters.DetailPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class LocationDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        // Bind Views
        ImageView btnBack = findViewById(R.id.btnBack);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        FloatingActionButton fabAddReview = findViewById(R.id.fabAddReview);

        // Setup Listeners
        btnBack.setOnClickListener(v -> finish());
        
        // Setup ViewPager2
        DetailPagerAdapter pagerAdapter = new DetailPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        
        // Setup TabLayoutMediator
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Overview");
                            break;
                        case 1:
                            tab.setText("Photos");
                            break;
                        case 2:
                            tab.setText("Reviews");
                            break;
                    }
                }
        ).attach();

        fabAddReview.setOnClickListener(v -> {
            // Handle Add Review
        });
    }
}
