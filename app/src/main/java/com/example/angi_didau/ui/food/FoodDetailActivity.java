package com.example.angi_didau.ui.food;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.angi_didau.R;
import com.example.angi_didau.common.constant.AppConstants;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Displays detailed information about a specific food item.
 * <p>
 * Receives the food's Firestore document ID via:
 * {@code getIntent().getStringExtra(AppConstants.EXTRA_FOOD_ID)}
 * <p>
 * TODO: Implement full food detail UI using activity_food_detail.xml layout.
 */
public class FoodDetailActivity extends AppCompatActivity {

    private String foodId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        // Retrieve the food ID passed from the previous screen
        foodId = getIntent().getStringExtra(AppConstants.EXTRA_FOOD_ID);

        bindViews();
    }

    private void bindViews() {
        ImageView ivBack = findViewById(R.id.btnBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        // TODO: Load food data using foodId, bind to views
        // TODO: Connect FoodDetailViewModel here
    }
}
