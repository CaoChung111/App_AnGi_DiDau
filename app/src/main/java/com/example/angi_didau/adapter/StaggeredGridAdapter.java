package com.example.angi_didau.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.angi_didau.R;
import com.example.angi_didau.ui.model.StaggeredItem;

import java.util.ArrayList;
import java.util.List;

public class StaggeredGridAdapter extends RecyclerView.Adapter<StaggeredGridAdapter.ViewHolder> {

    private List<StaggeredItem> items = new ArrayList<>();

    public void submitList(List<StaggeredItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
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
        StaggeredItem item = items.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvSubtitle.setText(item.getSubtitle());
        holder.tvRating.setText(String.valueOf(item.getRating()));
        
        // Randomize image height for staggered effect
        ViewGroup.LayoutParams layoutParams = holder.imgItem.getLayoutParams();
        int[] heights = {400, 500, 600, 450};
        layoutParams.height = heights[position % heights.length];
        holder.imgItem.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem;
        TextView tvTitle, tvSubtitle, tvRating;

        ViewHolder(View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.imgItem);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvSubtitle = itemView.findViewById(R.id.tvItemSubtitle);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }
}
