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
import com.example.angi_didau.data.model.Food;
import com.example.angi_didau.data.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying mixed search results (foods + locations) in a single RecyclerView.
 * <p>
 * Uses a flat list of {@link SearchResultItem} wrappers, with two view types:
 * {@link #VIEW_TYPE_FOOD} and {@link #VIEW_TYPE_LOCATION}.
 * <p>
 * Click handling is delegated to the Activity via separate callbacks for each type,
 * so navigation logic stays out of the adapter.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchViewHolder> {

    public static final int VIEW_TYPE_FOOD     = 0;
    public static final int VIEW_TYPE_LOCATION = 1;

    // ──────────────────────────────────────────
    //  Interfaces
    // ──────────────────────────────────────────

    public interface OnFoodClickListener     { void onFoodClick(Food food); }
    public interface OnLocationClickListener { void onLocationClick(Location location); }

    // ──────────────────────────────────────────
    //  Fields
    // ──────────────────────────────────────────

    private final List<SearchResultItem> items = new ArrayList<>();
    private OnFoodClickListener     foodClickListener;
    private OnLocationClickListener locationClickListener;

    public void setFoodClickListener(OnFoodClickListener l)         { this.foodClickListener = l; }
    public void setLocationClickListener(OnLocationClickListener l) { this.locationClickListener = l; }

    // ──────────────────────────────────────────
    //  Data Methods
    // ──────────────────────────────────────────

    /**
     * Replaces the current list with a new mixed set of foods and locations.
     * Sections are shown in order: foods first, then locations.
     *
     * @param foods     List of food results (can be null/empty)
     * @param locations List of location results (can be null/empty)
     */
    public void submitResults(List<Food> foods, List<Location> locations) {
        items.clear();
        if (foods != null) {
            for (Food f : foods) {
                items.add(new SearchResultItem(f));
            }
        }
        if (locations != null) {
            for (Location loc : locations) {
                items.add(new SearchResultItem(loc));
            }
        }
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    // ──────────────────────────────────────────
    //  RecyclerView.Adapter overrides
    // ──────────────────────────────────────────

    @Override
    public int getItemViewType(int position) {
        return items.get(position).isFood ? VIEW_TYPE_FOOD : VIEW_TYPE_LOCATION;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_place_card, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        SearchResultItem item = items.get(position);
        if (item.isFood && item.food != null) {
            holder.bind(item.food, foodClickListener);
        } else if (!item.isFood && item.location != null) {
            holder.bind(item.location, locationClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ──────────────────────────────────────────
    //  ViewHolder
    // ──────────────────────────────────────────

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivSearchItem;
        private final TextView  tvSearchTitle;
        private final TextView  tvSearchSubtitle;
        private final TextView  tvSearchRating;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSearchItem     = itemView.findViewById(R.id.ivPlaceImage);
            tvSearchTitle    = itemView.findViewById(R.id.tvPlaceName);
            tvSearchSubtitle = itemView.findViewById(R.id.tvPlaceLocation);
            tvSearchRating   = itemView.findViewById(R.id.tvRating);
        }

        public void bind(Food food, OnFoodClickListener listener) {
            tvSearchTitle.setText(food.getName());
            tvSearchSubtitle.setText(food.getDescription());
            tvSearchRating.setText(String.valueOf(food.getAverageRating()));

            Glide.with(itemView.getContext())
                    .load(food.getImageUrl())
                    .placeholder(R.drawable.ic_restaurant_menu)
                    .centerCrop()
                    .into(ivSearchItem);

            if (listener != null) {
                itemView.setOnClickListener(v -> listener.onFoodClick(food));
            }
        }

        public void bind(Location location, OnLocationClickListener listener) {
            tvSearchTitle.setText(location.getName());
            tvSearchSubtitle.setText(location.getAddress());
            tvSearchRating.setText(String.valueOf(location.getAverageRating()));

            Glide.with(itemView.getContext())
                    .load(location.getImageUrl())
                    .placeholder(R.drawable.ic_location_on)
                    .centerCrop()
                    .into(ivSearchItem);

            if (listener != null) {
                itemView.setOnClickListener(v -> listener.onLocationClick(location));
            }
        }
    }

    // ──────────────────────────────────────────
    //  Wrapper model
    // ──────────────────────────────────────────

    private static class SearchResultItem {
        final boolean  isFood;
        final Food     food;
        final Location location;

        SearchResultItem(Food food) {
            this.isFood   = true;
            this.food     = food;
            this.location = null;
        }

        SearchResultItem(Location location) {
            this.isFood   = false;
            this.food     = null;
            this.location = location;
        }
    }
}
