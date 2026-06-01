package com.example.angi_didau.adapters;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        // Bind logic
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
