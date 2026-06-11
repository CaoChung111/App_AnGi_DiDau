package com.example.angi_didau.ui.random;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.angi_didau.R;
import com.example.angi_didau.common.constant.AppConstants;
import com.example.angi_didau.ui.home.HomeActivity;
import com.example.angi_didau.ui.profile.ProfileActivity;
import com.example.angi_didau.ui.discover.DiscoverActivity;
import com.example.angi_didau.ui.favorites.FavoritesActivity;
import com.example.angi_didau.ui.food.FoodDetailActivity;
import com.example.angi_didau.ui.location.LocationDetailActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomActivity extends AppCompatActivity {

    private SpinWheelView spinWheelView;
    private TextView tabEat, tabGo, tvResult, tvNearbyCount, btnViewDetail;
    private Button btnSpinNow;
    private FrameLayout btnCenterSpin;

    private boolean isFoodTab = true;

    private List<QueryDocumentSnapshot> allFoods = new ArrayList<>();
    private List<QueryDocumentSnapshot> allLocations = new ArrayList<>();

    private List<QueryDocumentSnapshot> currentSpinData = new ArrayList<>();


    private String selectedEntityId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        initViews();
        setupBottomNav();
        setupTabs();

        setupSpinWheel();

        // Load data from Firebase
        fetchData();
    }

    private void initViews() {
        spinWheelView = findViewById(R.id.spinWheelView);
        tabEat = findViewById(R.id.tabEat);
        tabGo = findViewById(R.id.tabGo);
        tvResult = findViewById(R.id.tvResult);
        tvNearbyCount = findViewById(R.id.tvNearbyCount);
        btnViewDetail = findViewById(R.id.btnViewDetail);
        

        btnSpinNow = findViewById(R.id.btnSpinNow);
        btnCenterSpin = findViewById(R.id.btnCenterSpin);

        btnViewDetail.setOnClickListener(v -> {
            if (selectedEntityId != null) {
                Intent intent;
                if (isFoodTab) {
                    intent = new Intent(this, FoodDetailActivity.class);
                    intent.putExtra(AppConstants.EXTRA_FOOD_ID, selectedEntityId);
                } else {
                    intent = new Intent(this, LocationDetailActivity.class);
                    intent.putExtra(AppConstants.EXTRA_LOCATION_ID, selectedEntityId);
                }
                startActivity(intent);
            }
        });
    }

    private void fetchData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        db.collection(AppConstants.COLLECTION_FOODS).get().addOnSuccessListener(queryDocumentSnapshots -> {
            allFoods.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                allFoods.add(doc);
            }
            if (isFoodTab) updateTabState(true);
        });

        db.collection(AppConstants.COLLECTION_LOCATIONS).get().addOnSuccessListener(queryDocumentSnapshots -> {
            allLocations.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                allLocations.add(doc);
            }
            if (!isFoodTab) updateTabState(false);
        });
    }

    private void setupBottomNav() {
        ((TextView) findViewById(R.id.tvNavRandom)).setTextColor(getResources().getColor(R.color.primary_container));
        ((TextView) findViewById(R.id.tvNavHome)).setTextColor(getResources().getColor(R.color.secondary));
        ((ImageView) findViewById(R.id.ivNavHome)).setColorFilter(getResources().getColor(R.color.secondary));

        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });

        findViewById(R.id.navDiscover).setOnClickListener(v -> {
            startActivity(new Intent(this, DiscoverActivity.class));
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

    private void setupTabs() {
        tabEat.setOnClickListener(v -> {
            if (!isFoodTab) updateTabState(true);
        });

        tabGo.setOnClickListener(v -> {
            if (isFoodTab) updateTabState(false);
        });
    }



    private void updateTabState(boolean isFood) {
        this.isFoodTab = isFood;


        if (isFood) {
            tabEat.setBackgroundResource(R.drawable.bg_tab_selected);
            tabEat.setTextColor(getResources().getColor(R.color.white));
            tabGo.setBackgroundResource(android.R.color.transparent);
            tabGo.setTextColor(getResources().getColor(R.color.secondary));
        } else {
            tabGo.setBackgroundResource(R.drawable.bg_tab_selected);
            tabGo.setTextColor(getResources().getColor(R.color.white));
            tabEat.setBackgroundResource(android.R.color.transparent);
            tabEat.setTextColor(getResources().getColor(R.color.secondary));
        }

        applyFilters();
    }

    private void applyFilters() {
        List<QueryDocumentSnapshot> source = isFoodTab ? allFoods : allLocations;
        List<QueryDocumentSnapshot> filtered = new ArrayList<>();

        for (QueryDocumentSnapshot doc : source) {
            filtered.add(doc);
        }

        tvNearbyCount.setText(filtered.size() + (isFoodTab ? " Quán" : " Địa Điểm"));

        Collections.shuffle(filtered);
        int pickCount = Math.min(50, filtered.size());
        currentSpinData = new ArrayList<>(filtered.subList(0, pickCount));

        List<String> displayNames = new ArrayList<>();
        for (QueryDocumentSnapshot doc : currentSpinData) {
            String name = doc.getString("name");
            if (name == null) name = "Unknown";
            displayNames.add(name);
        }

        if (displayNames.isEmpty()) {
            displayNames.add("Không có dữ liệu");
        }

        spinWheelView.setItems(displayNames);
        resetResult("Nhấn nút bên dưới để bắt đầu xoay!");
    }
    
    private void resetResult(String text) {
        tvResult.setText(text);
        tvResult.setTextColor(getResources().getColor(R.color.secondary));
        tvResult.setTextSize(14f);
        tvResult.setTypeface(null, android.graphics.Typeface.NORMAL);
        btnViewDetail.setVisibility(View.GONE);
        selectedEntityId = null;
    }

    private void setupSpinWheel() {
        spinWheelView.setOnSpinListener((index, result) -> {
            tvResult.setText("Kết quả: " + result + "!");
            tvResult.setTextColor(getResources().getColor(R.color.primary_container));
            tvResult.setTextSize(18f);
            tvResult.setTypeface(null, android.graphics.Typeface.BOLD);
            
            if (!currentSpinData.isEmpty() && !result.equals("Không có dữ liệu")) {
                selectedEntityId = currentSpinData.get(index).getId();
                btnViewDetail.setVisibility(View.VISIBLE);
            }
        });

        btnSpinNow.setOnClickListener(v -> {
            if (currentSpinData.isEmpty()) {
                Toast.makeText(this, "Không có quán nào thỏa mãn bộ lọc!", Toast.LENGTH_SHORT).show();
                return;
            }
            resetResult("Đang xoay...");
            spinWheelView.spin();
        });
        
        btnCenterSpin.setOnClickListener(v -> {
            if (currentSpinData.isEmpty()) {
                Toast.makeText(this, "Không có quán nào thỏa mãn bộ lọc!", Toast.LENGTH_SHORT).show();
                return;
            }
            resetResult("Đang xoay...");
            spinWheelView.spin();
        });
    }
}
