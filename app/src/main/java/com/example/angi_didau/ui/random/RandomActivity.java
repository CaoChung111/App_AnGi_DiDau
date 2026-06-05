package com.example.angi_didau.ui.random;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.angi_didau.R;
import com.example.angi_didau.ui.home.HomeActivity;
import com.example.angi_didau.ui.profile.ProfileActivity;
import com.example.angi_didau.ui.discover.DiscoverActivity;
import com.example.angi_didau.ui.favorites.FavoritesActivity;

import java.util.Arrays;
import java.util.List;

public class RandomActivity extends AppCompatActivity {

    private SpinWheelView spinWheelView;
    private TextView tabEat, tabGo, tvResult, tvNearbyCount;
    private TextView chipBudget, chipDistance, chipAllFood;
    private Button btnSpinNow;
    private FrameLayout btnCenterSpin;

    private List<String> foodItems = Arrays.asList("Mì Cay", "Lẩu Kichi", "Phở", "Bún Đậu", "Pizza", "Sushi", "Cơm Tấm", "Bánh Mì");
    private List<String> locationItems = Arrays.asList("Hồ Tây", "Phố Cổ", "Quán Cafe", "Rạp Phim", "Công Viên", "Trà Đá", "TTTM", "Nhà Sách");

    private boolean isFoodTab = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        initViews();
        setupBottomNav();
        setupTabs();
        setupSpinWheel();

        // Initialize with Food Tab
        updateTabState(true);
    }

    private void initViews() {
        spinWheelView = findViewById(R.id.spinWheelView);
        tabEat = findViewById(R.id.tabEat);
        tabGo = findViewById(R.id.tabGo);
        tvResult = findViewById(R.id.tvResult);
        tvNearbyCount = findViewById(R.id.tvNearbyCount);
        
        chipBudget = findViewById(R.id.chipBudget);
        chipDistance = findViewById(R.id.chipDistance);
        chipAllFood = findViewById(R.id.chipAllFood);

        btnSpinNow = findViewById(R.id.btnSpinNow);
        btnCenterSpin = findViewById(R.id.btnCenterSpin);
    }

    private void setupBottomNav() {
        // Highlight Random Tab
        ((TextView) findViewById(R.id.tvNavRandom)).setTextColor(getResources().getColor(R.color.primary_container));

        // Dim others
        ((TextView) findViewById(R.id.tvNavHome)).setTextColor(getResources().getColor(R.color.secondary));
        ((ImageView) findViewById(R.id.ivNavHome)).setColorFilter(getResources().getColor(R.color.secondary));

        // Clicks
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });

        // Discover Click
        findViewById(R.id.navDiscover).setOnClickListener(v -> {
            startActivity(new Intent(this, DiscoverActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
        
        // Favorites Click
        findViewById(R.id.navFavorites).setOnClickListener(v -> {
            startActivity(new Intent(this, FavoritesActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });

        // Profile Click
        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
    }

    private void setupTabs() {
        tabEat.setOnClickListener(v -> {
            if (!isFoodTab) {
                updateTabState(true);
            }
        });

        tabGo.setOnClickListener(v -> {
            if (isFoodTab) {
                updateTabState(false);
            }
        });
    }

    private void updateTabState(boolean isFood) {
        this.isFoodTab = isFood;

        if (isFood) {
            tabEat.setBackgroundResource(R.drawable.bg_tab_selected);
            tabEat.setTextColor(getResources().getColor(R.color.white));
            
            tabGo.setBackgroundResource(android.R.color.transparent);
            tabGo.setTextColor(getResources().getColor(R.color.secondary));

            tvNearbyCount.setText("32 Quán Gần Đây");
            chipBudget.setText("🍽 Dưới 100k");
            chipAllFood.setText("🍴 Tất cả món");
            spinWheelView.setItems(foodItems);
            
            resetResult("Nhấn nút bên dưới để bắt đầu xoay (Món Ăn)!");
        } else {
            tabGo.setBackgroundResource(R.drawable.bg_tab_selected);
            tabGo.setTextColor(getResources().getColor(R.color.white));

            tabEat.setBackgroundResource(android.R.color.transparent);
            tabEat.setTextColor(getResources().getColor(R.color.secondary));

            tvNearbyCount.setText("15 Địa Điểm Gần Đây");
            chipBudget.setText("🎟 Miễn phí/Rẻ");
            chipAllFood.setText("🎯 Tất cả loại hình");
            spinWheelView.setItems(locationItems);
            
            resetResult("Nhấn nút bên dưới để bắt đầu xoay (Địa Điểm)!");
        }
    }
    
    private void resetResult(String text) {
        tvResult.setText(text);
        tvResult.setTextColor(getResources().getColor(R.color.secondary));
        tvResult.setTextSize(14f);
        tvResult.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    private void setupSpinWheel() {
        spinWheelView.setOnSpinListener(result -> {
            tvResult.setText("Kết quả: " + result + "!");
            tvResult.setTextColor(getResources().getColor(R.color.primary_container));
            tvResult.setTextSize(18f);
            tvResult.setTypeface(null, android.graphics.Typeface.BOLD);
            
            Toast.makeText(this, "Hôm nay " + (isFoodTab ? "ăn" : "đi") + " " + result + " nhé!", Toast.LENGTH_LONG).show();
        });

        btnSpinNow.setOnClickListener(v -> {
            resetResult("Đang xoay...");
            spinWheelView.spin();
        });
        
        btnCenterSpin.setOnClickListener(v -> {
            resetResult("Đang xoay...");
            spinWheelView.spin();
        });
    }
}
