package com.example.angi_didau.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.angi_didau.R;

public class UserReviewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reviews);

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        
        // TODO: Initialize RecyclerView and Adapter when we have a database of User Reviews
        View rvUserReviews = findViewById(R.id.rvUserReviews);
        if (rvUserReviews != null) {
            rvUserReviews.setVisibility(View.GONE); // Hide list since we show empty state by default
        }
    }
}
