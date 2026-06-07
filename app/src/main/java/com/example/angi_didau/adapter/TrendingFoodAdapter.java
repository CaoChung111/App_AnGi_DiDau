package com.example.angi_didau.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.angi_didau.R;
import com.example.angi_didau.data.model.Food;

/**
 * Adapter for the horizontal "Trending Foods" RecyclerView on the home screen.
 * <p>
 * Uses {@link ListAdapter} with {@link DiffUtil} for efficient, animated list updates
 * instead of the naive {@code notifyDataSetChanged()} approach.
 * <p>
 * Memory leak fix: Context is obtained from {@code parent.getContext()} in
 * {@link #onCreateViewHolder} rather than stored as a field.
 */
public class TrendingFoodAdapter extends ListAdapter<Food, TrendingFoodAdapter.TrendingFoodViewHolder> {

    private OnItemClickListener onItemClickListener;

    /** Callback interface for item click events. */
    public interface OnItemClickListener {
        void onFoodClick(Food food);
    }

    public TrendingFoodAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Food> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Food>() {
                @Override
                public boolean areItemsTheSame(@NonNull Food oldItem, @NonNull Food newItem) {
                    // Items are the same if they share the same Firestore document ID
                    return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Food oldItem, @NonNull Food newItem) {
                    return oldItem.getName().equals(newItem.getName())
                            && oldItem.getAverageRating() == newItem.getAverageRating();
                }
            };

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public TrendingFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trending, parent, false);
        return new TrendingFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingFoodViewHolder holder, int position) {
        Food food = getItem(position);
        holder.bind(food, onItemClickListener);
    }

    /** ViewHolder uses an inner {@code bind()} method to keep onBindViewHolder clean. */
    public static class TrendingFoodViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivTrendingFood;
        private final TextView tvFoodName;
        private final TextView tvFoodAddress;
        private final TextView tvFoodRating;
        private final TextView tvFoodDistance;

        public TrendingFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTrendingFood  = itemView.findViewById(R.id.imgTrending);
            tvFoodName      = itemView.findViewById(R.id.tvTitle);
            tvFoodAddress   = itemView.findViewById(R.id.tvAddress);
            tvFoodRating    = itemView.findViewById(R.id.tvRating);
            tvFoodDistance  = itemView.findViewById(R.id.tvDistance);
        }

        public void bind(Food food, OnItemClickListener listener) {
            tvFoodName.setText(food.getName());
            tvFoodAddress.setText(food.getDescription());
            tvFoodRating.setText(String.valueOf(food.getAverageRating()));
            // Since we don't have real distance data yet, hide this element
            tvFoodDistance.setVisibility(android.view.View.GONE);

            Glide.with(itemView.getContext())
                    .load(food.getImageUrl())
                    .placeholder(R.drawable.ic_restaurant_menu)
                    .centerCrop()
                    .into(ivTrendingFood);

            if (listener != null) {
                itemView.setOnClickListener(v -> listener.onFoodClick(food));
            }
        }
    }
}
