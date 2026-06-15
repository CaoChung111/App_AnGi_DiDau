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
import com.example.angi_didau.common.constant.AppConstants;

import java.util.ArrayList;
import java.util.List;

import com.example.angi_didau.data.model.Favorite;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private final List<Favorite> favoriteList = new ArrayList<>();
    private final OnFavoriteClickListener listener;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Favorite favorite);
        void onRemoveClick(Favorite favorite);
    }

    public FavoriteAdapter(OnFavoriteClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Favorite> list) {
        favoriteList.clear();
        if (list != null) {
            favoriteList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Favorite favorite = favoriteList.get(position);
        holder.bind(favorite);
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivImage;
        private final TextView tvName;
        private final ImageView ivType;
        private final TextView tvNote;
        private final TextView tvCustomTag;
        private final ImageView btnRemoveFavorite;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivFavoriteImage);
            tvName = itemView.findViewById(R.id.tvFavoriteName);
            ivType = itemView.findViewById(R.id.ivFavoriteType);
            tvNote = itemView.findViewById(R.id.tvFavoriteNote);
            tvCustomTag = itemView.findViewById(R.id.tvCustomTag);
            btnRemoveFavorite = itemView.findViewById(R.id.btnRemoveFavorite);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onFavoriteClick(favoriteList.get(position));
                }
            });

            if (btnRemoveFavorite != null) {
                btnRemoveFavorite.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onRemoveClick(favoriteList.get(position));
                    }
                });
            }
        }

        public void bind(Favorite favorite) {
            String name = favorite.getName();
            String note = favorite.getNote();
            String type = favorite.getType();
            String imageUrl = favorite.getImageUrl();
            boolean isCustom = favorite.isCustom();

            tvName.setText(name != null ? name : "Chưa có tên");
            
            if (note != null && !note.trim().isEmpty()) {
                tvNote.setText(note);
                tvNote.setVisibility(View.VISIBLE);
            } else {
                tvNote.setVisibility(View.GONE);
            }

            if (isCustom) {
                if (tvCustomTag != null) tvCustomTag.setVisibility(View.VISIBLE);
            } else {
                if (tvCustomTag != null) tvCustomTag.setVisibility(View.GONE);
            }

            if (AppConstants.ENTITY_TYPE_FOOD.equals(type)) {
                ivType.setImageResource(R.drawable.ic_restaurant_menu);
            } else {
                ivType.setImageResource(R.drawable.ic_location_on);
            }

            int placeholderIcon = AppConstants.ENTITY_TYPE_FOOD.equals(type) ? R.drawable.ic_restaurant_menu : R.drawable.ic_location_on;

            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(placeholderIcon)
                        .centerCrop()
                        .into(ivImage);
            } else {
                ivImage.setImageResource(placeholderIcon);
            }
        }
    }
}
