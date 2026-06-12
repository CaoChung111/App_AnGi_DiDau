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
import com.example.angi_didau.data.model.Location;

/**
 * Adapter for the vertical "Recommended Locations" RecyclerView on the home screen.
 * <p>
 * Uses {@link ListAdapter} + {@link DiffUtil} for smooth, efficient list updates.
 * Context is obtained from {@code parent.getContext()} — no memory leaks.
 */
public class RecommendedLocationAdapter
        extends ListAdapter<Location, RecommendedLocationAdapter.RecommendedLocationViewHolder> {

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onLocationClick(Location location);
    }

    public RecommendedLocationAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Location> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Location>() {
                @Override
                public boolean areItemsTheSame(@NonNull Location oldItem, @NonNull Location newItem) {
                    return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Location oldItem, @NonNull Location newItem) {
                    return oldItem.getName().equals(newItem.getName())
                            && oldItem.getAverageRating() == newItem.getAverageRating()
                            && oldItem.getAddress().equals(newItem.getAddress());
                }
            };

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public RecommendedLocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommendation, parent, false);
        return new RecommendedLocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendedLocationViewHolder holder, int position) {
        holder.bind(getItem(position), onItemClickListener);
    }

    public static class RecommendedLocationViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivLocationImage;
        private final TextView tvLocationName;
        private final TextView tvLocationAddress;
        private final TextView tvLocationRating;
        private final TextView tvLocationPrice;
        private final TextView tvLocationDistance;

        public RecommendedLocationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLocationImage    = itemView.findViewById(R.id.imgRec);
            tvLocationName     = itemView.findViewById(R.id.tvRecTitle);
            tvLocationAddress  = itemView.findViewById(R.id.tvRecSubtitle);
            tvLocationRating   = itemView.findViewById(R.id.tvRecRating);
            tvLocationPrice    = itemView.findViewById(R.id.tvRecPrice);
            tvLocationDistance = itemView.findViewById(R.id.tvRecDistance);
        }

        public void bind(Location location, OnItemClickListener listener) {
            tvLocationName.setText(location.getName());
            tvLocationAddress.setText(location.getAddress());
            tvLocationRating.setText(String.valueOf(location.getAverageRating()));
            // Show price based on location.getPrice()
            if (location.getPrice() > 0) {
                java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
                tvLocationPrice.setText(format.format(location.getPrice()));
            } else {
                tvLocationPrice.setText("Miễn phí");
            }
            tvLocationPrice.setVisibility(android.view.View.VISIBLE);
            
            // Distance is still hidden as we don't have GPS data yet
            tvLocationDistance.setVisibility(android.view.View.GONE);

            Glide.with(itemView.getContext())
                    .load(location.getImageUrl())
                    .placeholder(R.drawable.ic_location_on)
                    .centerCrop()
                    .into(ivLocationImage);

            if (listener != null) {
                itemView.setOnClickListener(v -> listener.onLocationClick(location));
            }
        }
    }
}
