package com.example.angi_didau.adapters;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        // Bind logic
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
