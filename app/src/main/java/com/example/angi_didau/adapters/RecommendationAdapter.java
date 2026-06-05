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
import com.example.angi_didau.models.Location;
import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder> {

    private Context context;
    private List<Location> locationList;

    public RecommendationAdapter(Context context, List<Location> locationList) {
        this.context = context;
        this.locationList = locationList;
    }

    @NonNull
    @Override
    public RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recommendation, parent, false);
        return new RecommendationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationViewHolder holder, int position) {
        Location location = locationList.get(position);
        holder.tvRecTitle.setText(location.getName());
        holder.tvRecSubtitle.setText(location.getAddress());
        holder.tvRecRating.setText(String.valueOf(location.getAverageRating()));
        holder.tvRecPrice.setText("$$$"); // Fake price for now
        holder.tvRecDistance.setText("1.5km"); // Fake distance for now

        Glide.with(context)
                .load(location.getImageUrl())
                .placeholder(android.R.drawable.sym_def_app_icon)
                .into(holder.imgRec);
    }

    @Override
    public int getItemCount() {
        return locationList != null ? locationList.size() : 0;
    }

    public static class RecommendationViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRec;
        TextView tvRecTitle, tvRecSubtitle, tvRecRating, tvRecPrice, tvRecDistance;

        public RecommendationViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRec = itemView.findViewById(R.id.imgRec);
            tvRecTitle = itemView.findViewById(R.id.tvRecTitle);
            tvRecSubtitle = itemView.findViewById(R.id.tvRecSubtitle);
            tvRecRating = itemView.findViewById(R.id.tvRecRating);
            tvRecPrice = itemView.findViewById(R.id.tvRecPrice);
            tvRecDistance = itemView.findViewById(R.id.tvRecDistance);
        }
    }
}
