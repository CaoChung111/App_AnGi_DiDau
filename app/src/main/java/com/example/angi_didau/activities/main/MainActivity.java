package com.example.angi_didau.activities.main;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.angi_didau.R;
import com.example.angi_didau.adapters.RecommendationAdapter;
import com.example.angi_didau.adapters.TrendingAdapter;
import com.example.angi_didau.models.Food;
import com.example.angi_didau.models.Location;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvTrending, rvRecommendations;
    private TrendingAdapter trendingAdapter;
    private RecommendationAdapter recommendationAdapter;
    private List<Food> trendingList;
    private List<Location> recommendationList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Map Views
        rvTrending = findViewById(R.id.rvTrending);
        rvRecommendations = findViewById(R.id.rvRecommendations);

        // Setup Layout Managers
        rvTrending.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvRecommendations.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Initialize Lists and Adapters
        trendingList = new ArrayList<>();
        trendingAdapter = new TrendingAdapter(this, trendingList);
        rvTrending.setAdapter(trendingAdapter);

        recommendationList = new ArrayList<>();
        recommendationAdapter = new RecommendationAdapter(this, recommendationList);
        rvRecommendations.setAdapter(recommendationAdapter);

        // Load Data from Firebase
        loadTrendingFoods();
        loadRecommendations();
    }

    private void loadTrendingFoods() {
        db.collection("Foods")
                // .orderBy("averageRating", Query.Direction.DESCENDING) // Uncomment if you want to sort
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    trendingList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Food food = doc.toObject(Food.class);
                        if (food != null) {
                            trendingList.add(food);
                        }
                    }
                    trendingAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("MainActivity", "Error loading trending foods", e);
                    Toast.makeText(this, "Không thể tải dữ liệu món ăn", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadRecommendations() {
        db.collection("Locations")
                // .orderBy("averageRating", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    recommendationList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Location location = doc.toObject(Location.class);
                        if (location != null) {
                            recommendationList.add(location);
                        }
                    }
                    recommendationAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("MainActivity", "Error loading recommendations", e);
                    Toast.makeText(this, "Không thể tải dữ liệu địa điểm", Toast.LENGTH_SHORT).show();
                });
    }
}
