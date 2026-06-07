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
        // Setup RecyclerView
        androidx.recyclerview.widget.RecyclerView rvUserReviews = findViewById(R.id.rvUserReviews);
        com.example.angi_didau.adapter.ReviewAdapter adapter = new com.example.angi_didau.adapter.ReviewAdapter();
        if (rvUserReviews != null) {
            rvUserReviews.setAdapter(adapter);
        }
        
        View llEmptyState = findViewById(R.id.llEmptyState);

        // Fetch data
        UserReviewsViewModel viewModel = new androidx.lifecycle.ViewModelProvider(this).get(UserReviewsViewModel.class);
        viewModel.getUserReviews().observe(this, reviews -> {
            if (reviews != null && !reviews.isEmpty()) {
                adapter.submitList(reviews);
                if (rvUserReviews != null) rvUserReviews.setVisibility(View.VISIBLE);
                if (llEmptyState != null) llEmptyState.setVisibility(View.GONE);
            } else {
                adapter.submitList(new java.util.ArrayList<>());
                if (rvUserReviews != null) rvUserReviews.setVisibility(View.GONE);
                if (llEmptyState != null) llEmptyState.setVisibility(View.VISIBLE);
            }
        });
    }
}
