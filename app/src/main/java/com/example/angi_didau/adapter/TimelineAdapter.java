package com.example.angi_didau.adapter;

import com.example.angi_didau.data.model.TimelineItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.angi_didau.R;

import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

    private List<TimelineItem> items;

    public TimelineAdapter(List<TimelineItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimelineItem item = items.get(position);
        
        holder.tvTimeCategory.setText(item.getTimeCategory());
        holder.tvTitle.setText(item.getTitle());
        holder.tvPrice.setText(item.getPrice());
        holder.tvDescription.setText(item.getDescription());
        holder.tvLocation.setText(item.getLocation());
        
        // Load image: use URL if available, else use default icon
        String imageUrl = item.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_discover)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.ic_discover);
            holder.ivImage.setScaleType(ImageView.ScaleType.CENTER);
        }
        
        holder.ivTypeIcon.setImageResource(item.getIconResId());

        holder.itemView.setOnClickListener(v -> {
            if (item.getEntityId() != null && !item.getEntityId().isEmpty()) {
                android.content.Intent intent;
                if ("Food".equalsIgnoreCase(item.getEntityType())) {
                    intent = new android.content.Intent(v.getContext(), com.example.angi_didau.ui.food.FoodDetailActivity.class);
                    intent.putExtra(com.example.angi_didau.common.constant.AppConstants.EXTRA_FOOD_ID, item.getEntityId());
                } else {
                    intent = new android.content.Intent(v.getContext(), com.example.angi_didau.ui.location.LocationDetailActivity.class);
                    intent.putExtra(com.example.angi_didau.common.constant.AppConstants.EXTRA_LOCATION_ID, item.getEntityId());
                }
                v.getContext().startActivity(intent);
            } else {
                android.widget.Toast.makeText(v.getContext(), "Mục này chưa được liên kết với dữ liệu chi tiết", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }
    
    public List<TimelineItem> getItems() {
        return items;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimeCategory, tvTitle, tvPrice, tvDescription, tvLocation;
        ImageView ivImage, ivTypeIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimeCategory = itemView.findViewById(R.id.tvTimeCategory);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivTypeIcon = itemView.findViewById(R.id.ivTypeIcon);
        }
    }
}
