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
import java.util.Map;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private final List<Map<String, Object>> favoriteList = new ArrayList<>();
    private final OnFavoriteClickListener listener;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Map<String, Object> favorite);
        void onRemoveClick(Map<String, Object> favorite);
    }

    public FavoriteAdapter(OnFavoriteClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Map<String, Object>> list) {
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
        Map<String, Object> favorite = favoriteList.get(position);
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

        public void bind(Map<String, Object> favorite) {
            String name = (String) favorite.get("name");
            String note = (String) favorite.get("note");
            String type = (String) favorite.get("type");
            String imageUrl = (String) favorite.get("imageUrl");
            Boolean isCustom = (Boolean) favorite.get("isCustom");

            tvName.setText(name != null ? name : "Chưa có tên");
            
            if (note != null && !note.trim().isEmpty()) {
                tvNote.setText(note);
                tvNote.setVisibility(View.VISIBLE);
            } else {
                tvNote.setVisibility(View.GONE);
            }

            if (Boolean.TRUE.equals(isCustom)) {
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
