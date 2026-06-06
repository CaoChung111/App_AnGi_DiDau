package com.example.angi_didau.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.angi_didau.R;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.angi_didau.data.model.SavedPlan;
import com.example.angi_didau.adapter.SavedPlansAdapter;
import java.util.ArrayList;
import java.util.List;

public class SavedPlansActivity extends AppCompatActivity {

    private RecyclerView rvSavedPlans;
    private View llEmptyState;
    private SavedPlansAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_plans);

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        
        rvSavedPlans = findViewById(R.id.rvSavedPlans);
        llEmptyState = findViewById(R.id.llEmptyState);
        
        rvSavedPlans.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SavedPlansAdapter(new ArrayList<>(), plan -> {
            // Can pass the JSON representation of items to DiscoverActivity if we want to show it there,
            // or we can handle it later. For now, just show a toast.
            android.widget.Toast.makeText(this, "Xem chi tiết: " + plan.getTitle(), android.widget.Toast.LENGTH_SHORT).show();
        });
        rvSavedPlans.setAdapter(adapter);

        loadSavedPlans();
    }
    
    private void loadSavedPlans() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;
        
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid)
                .collection("SavedPlans")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        android.util.Log.e("SavedPlans", "Error loading plans", error);
                        return;
                    }
                    
                    if (value != null) {
                        List<SavedPlan> plans = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            SavedPlan plan = doc.toObject(SavedPlan.class);
                            plan.setId(doc.getId());
                            plans.add(plan);
                        }
                        
                        adapter.updateData(plans);
                        if (plans.isEmpty()) {
                            rvSavedPlans.setVisibility(View.GONE);
                            llEmptyState.setVisibility(View.VISIBLE);
                        } else {
                            rvSavedPlans.setVisibility(View.VISIBLE);
                            llEmptyState.setVisibility(View.GONE);
                        }
                    }
                });
    }
}
