package com.example.angi_didau.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.angi_didau.R;
import com.example.angi_didau.models.Food;
import java.util.List;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.TrendingViewHolder> {

    private Context context;
    private List<Food> foodList;

    public TrendingAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public TrendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trending, parent, false);
        return new TrendingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.tvTitle.setText(food.getName());
        holder.tvAddress.setText(food.getDescription()); // using desc as address for demo
        holder.tvRating.setText(String.valueOf(food.getAverageRating()));
        holder.tvDistance.setText("Cách đây 1.2km"); // Fake distance for now

        Glide.with(context)
                .load(food.getImageUrl())
                .placeholder(android.R.drawable.sym_def_app_icon)
                .into(holder.imgTrending);
    }

    @Override
    public int getItemCount() {
        return foodList != null ? foodList.size() : 0;
    }

    public static class TrendingViewHolder extends RecyclerView.ViewHolder {
        ImageView imgTrending;
        TextView tvTitle, tvAddress, tvRating, tvDistance;

        public TrendingViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTrending = itemView.findViewById(R.id.imgTrending);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvDistance = itemView.findViewById(R.id.tvDistance);
        }
    }
}
