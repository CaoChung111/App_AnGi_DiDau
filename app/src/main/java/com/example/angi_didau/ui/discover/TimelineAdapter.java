package com.example.angi_didau.ui.discover;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        
        // Load fake images directly since it's hardcoded for this UI
        holder.ivImage.setImageResource(item.getImageResId());
        holder.ivTypeIcon.setImageResource(item.getIconResId());

        // Hide the top line for the first item
        if (position == 0) {
            // We'll just let it draw, or we could modify constraints if we wanted to be pixel-perfect.
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
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
