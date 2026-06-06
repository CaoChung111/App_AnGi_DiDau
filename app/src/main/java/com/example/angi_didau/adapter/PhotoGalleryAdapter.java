package com.example.angi_didau.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.angi_didau.R;

import java.util.List;

public class PhotoGalleryAdapter extends RecyclerView.Adapter<PhotoGalleryAdapter.PhotoViewHolder> {

    private final List<String> imageUrls;

    public PhotoGalleryAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gallery_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        String url = imageUrls.get(position);
        Glide.with(holder.itemView.getContext())
                .load(url)
                .placeholder(R.drawable.ic_restaurant_menu)
                .centerCrop()
                .into(holder.ivGalleryPhoto);
    }

    @Override
    public int getItemCount() {
        return imageUrls != null ? imageUrls.size() : 0;
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGalleryPhoto;

        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGalleryPhoto = itemView.findViewById(R.id.ivGalleryPhoto);
        }
    }
}
