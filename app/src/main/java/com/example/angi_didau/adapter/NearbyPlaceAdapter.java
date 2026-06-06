package com.example.angi_didau.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.angi_didau.R;
import com.example.angi_didau.data.model.Location;

import java.util.List;

public class NearbyPlaceAdapter extends RecyclerView.Adapter<NearbyPlaceAdapter.PlaceViewHolder> {

    private final List<Location> places;

    public NearbyPlaceAdapter(List<Location> places) {
        this.places = places != null ? new java.util.ArrayList<>(places) : new java.util.ArrayList<>();
    }

    public void setLocations(List<Location> newPlaces) {
        this.places.clear();
        if (newPlaces != null) {
            this.places.addAll(newPlaces);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nearby_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Location place = places.get(position);
        holder.tvNearbyName.setText(place.getName());
        holder.tvNearbyRating.setText(String.valueOf(place.getAverageRating()));
        
        // Dummy distance for now
        holder.tvNearbyDistance.setText("1.2 km away");

        Glide.with(holder.itemView.getContext())
                .load(place.getImageUrl())
                .placeholder(R.drawable.ic_location_on)
                .centerCrop()
                .into(holder.ivNearbyPlace);
    }

    @Override
    public int getItemCount() {
        return places != null ? places.size() : 0;
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder {
        ImageView ivNearbyPlace;
        TextView tvNearbyRating;
        TextView tvNearbyName;
        TextView tvNearbyDistance;

        PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivNearbyPlace = itemView.findViewById(R.id.ivNearbyPlace);
            tvNearbyRating = itemView.findViewById(R.id.tvNearbyRating);
            tvNearbyName = itemView.findViewById(R.id.tvNearbyName);
            tvNearbyDistance = itemView.findViewById(R.id.tvNearbyDistance);
        }
    }
}
