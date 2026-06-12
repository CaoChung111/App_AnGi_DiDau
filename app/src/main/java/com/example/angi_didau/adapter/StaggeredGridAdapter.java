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
import com.example.angi_didau.ui.model.StaggeredItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the staggered grid layout used in FoodListActivity and LocationListActivity.
 * <p>
 * Updated to use {@link ListAdapter} + {@link DiffUtil} for proper change detection.
 * Added an {@link OnItemClickListener} interface for navigation callbacks.
 * Randomizes image heights to create a Pinterest-style staggered visual effect.
 */
public class StaggeredGridAdapter extends ListAdapter<StaggeredItem, StaggeredGridAdapter.ViewHolder> {

    /** Callback interface for item click events. */
    public interface OnItemClickListener {
        void onItemClick(StaggeredItem item);
    }

    private OnItemClickListener onItemClickListener;

    public StaggeredGridAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<StaggeredItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<StaggeredItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull StaggeredItem oldItem, @NonNull StaggeredItem newItem) {
                    return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull StaggeredItem oldItem, @NonNull StaggeredItem newItem) {
                    return oldItem.getTitle().equals(newItem.getTitle())
                            && oldItem.getRating() == newItem.getRating();
                }
            };

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_staggered, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StaggeredItem item = getItem(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvSubtitle.setText(item.getSubtitle());
        holder.tvRating.setText(String.valueOf(item.getRating()));

        // Load image with Glide if URL present, else show placeholder
        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.ic_restaurant_menu)
                .centerCrop()
                .into(holder.imgItem);

        // Randomize image height for staggered effect
        int[] heights = {400, 520, 600, 460, 540};
        ViewGroup.LayoutParams layoutParams = holder.imgItem.getLayoutParams();
        layoutParams.height = heights[position % heights.length];
        holder.imgItem.setLayoutParams(layoutParams);

        // Click handling
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(item));
        }
        
        if (holder.tvItemPrice != null) {
            if (item.getPriceLabel() != null && !item.getPriceLabel().isEmpty()) {
                holder.tvItemPrice.setText(item.getPriceLabel());
                holder.tvItemPrice.setVisibility(View.VISIBLE);
                if (item.getPriceLabel().equals("Miễn phí")) {
                    holder.tvItemPrice.setTextColor(android.graphics.Color.parseColor("#388E3C")); // Green
                } else {
                    holder.tvItemPrice.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.primary));
                }
            } else {
                holder.tvItemPrice.setVisibility(View.GONE);
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem;
        TextView  tvTitle, tvSubtitle, tvRating, tvItemPrice;

        ViewHolder(View itemView) {
            super(itemView);
            imgItem     = itemView.findViewById(R.id.imgItem);
            tvTitle     = itemView.findViewById(R.id.tvItemTitle);
            tvSubtitle  = itemView.findViewById(R.id.tvItemSubtitle);
            tvRating    = itemView.findViewById(R.id.tvRating);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
        }
    }
}
