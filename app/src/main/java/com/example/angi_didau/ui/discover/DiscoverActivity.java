package com.example.angi_didau.ui.discover;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.angi_didau.R;
import com.example.angi_didau.ui.home.HomeActivity;
import com.example.angi_didau.ui.profile.ProfileActivity;
import com.example.angi_didau.ui.random.RandomActivity;
import com.example.angi_didau.ui.favorites.FavoritesActivity;

import java.util.ArrayList;
import java.util.List;

public class DiscoverActivity extends AppCompatActivity {

    private LinearLayout llForm;
    private LinearLayout llResult;
    private FrameLayout flLoading;
    
    private TextView tvPeopleCount;
    private int peopleCount = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        
        initViews();
        setupBottomNav();
        setupFormInteractions();
    }

    private void initViews() {
        llForm = findViewById(R.id.llForm);
        llResult = findViewById(R.id.llResult);
        flLoading = findViewById(R.id.flLoading);
        tvPeopleCount = findViewById(R.id.tvPeopleCount);
    }

    private void setupFormInteractions() {
        ImageView ivMinus = findViewById(R.id.ivMinus);
        ImageView ivPlus = findViewById(R.id.ivPlus);
        
        ivMinus.setOnClickListener(v -> {
            if (peopleCount > 1) {
                peopleCount--;
                tvPeopleCount.setText(String.valueOf(peopleCount));
            }
        });

        ivPlus.setOnClickListener(v -> {
            if (peopleCount < 20) {
                peopleCount++;
                tvPeopleCount.setText(String.valueOf(peopleCount));
            }
        });

        Button btnSuggest = findViewById(R.id.btnSuggest);
        btnSuggest.setOnClickListener(v -> simulateAIGeneration());
        
        Button btnRegenerate = findViewById(R.id.btnRegenerate);
        if (btnRegenerate != null) {
            btnRegenerate.setOnClickListener(v -> {
                llResult.setVisibility(View.GONE);
                llForm.setVisibility(View.VISIBLE);
            });
        }
        
        Button btnEditPlan = findViewById(R.id.btnEditPlan);
        if (btnEditPlan != null) {
            btnEditPlan.setOnClickListener(v -> {
                llResult.setVisibility(View.GONE);
                llForm.setVisibility(View.VISIBLE);
            });
        }
    }

    private void simulateAIGeneration() {
        // Show loading
        flLoading.setVisibility(View.VISIBLE);
        
        // Simulate network delay for AI
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            flLoading.setVisibility(View.GONE);
            llForm.setVisibility(View.GONE);
            llResult.setVisibility(View.VISIBLE);
            
            setupTimelineData();
        }, 2000); // 2 seconds
    }

    private void setupTimelineData() {
        RecyclerView rvTimeline = findViewById(R.id.rvTimeline);
        if (rvTimeline == null) return;
        
        List<TimelineItem> fakeData = new ArrayList<>();
        fakeData.add(new TimelineItem(
                "18:00 — CAFE",
                "The Workshop Coffee",
                "60.000đ",
                "Không gian yên tĩnh, view ban công cực chill cho cặp đôi.",
                "Quận 1, TP. HCM",
                android.R.drawable.sym_def_app_icon, // Replace with your real drawable or keep a placeholder
                android.R.drawable.ic_menu_camera
        ));
        
        fakeData.add(new TimelineItem(
                "19:30 — DINNER",
                "Bánh Mì Huỳnh Hoa (Shared)",
                "80.000đ",
                "Combo ổ đặc biệt cho 2 người, ăn kèm trà đá lề đường vui vẻ.",
                "Cách Mạng Tháng 8",
                android.R.drawable.sym_def_app_icon,
                android.R.drawable.ic_menu_edit
        ));
        
        fakeData.add(new TimelineItem(
                "21:00 — RELAX",
                "Bến Bạch Đằng Waterbus Walk",
                "60.000đ",
                "Dạo phố đêm và thưởng thức 2 ly trà dâu ngắm tàu chạy.",
                "Bến Bạch Đằng",
                android.R.drawable.sym_def_app_icon,
                android.R.drawable.ic_menu_agenda
        ));

        TimelineAdapter adapter = new TimelineAdapter(fakeData);
        rvTimeline.setAdapter(adapter);
    }

    private void setupBottomNav() {
        // Highlight Discover Tab
        ((TextView) findViewById(R.id.tvNavDiscover)).setTextColor(getResources().getColor(R.color.primary_container));
        ((ImageView) findViewById(R.id.ivNavDiscover)).setColorFilter(getResources().getColor(R.color.primary_container));

        // Dim others
        ((TextView) findViewById(R.id.tvNavHome)).setTextColor(getResources().getColor(R.color.secondary));
        ((ImageView) findViewById(R.id.ivNavHome)).setColorFilter(getResources().getColor(R.color.secondary));
        ((TextView) findViewById(R.id.tvNavProfile)).setTextColor(getResources().getColor(R.color.secondary));
        ((ImageView) findViewById(R.id.ivNavProfile)).setColorFilter(getResources().getColor(R.color.secondary));
        ((TextView) findViewById(R.id.tvNavRandom)).setTextColor(getResources().getColor(R.color.secondary));

        // Clicks
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
        
        findViewById(R.id.navRandom).setOnClickListener(v -> {
            startActivity(new Intent(this, RandomActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
        
        findViewById(R.id.navFavorites).setOnClickListener(v -> {
            startActivity(new Intent(this, FavoritesActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
    }
}
