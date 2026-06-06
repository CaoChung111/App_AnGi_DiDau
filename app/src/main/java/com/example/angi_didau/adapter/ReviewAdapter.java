package com.example.angi_didau.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.angi_didau.R;
import com.example.angi_didau.data.model.Review;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Adapter for displaying user reviews in a RecyclerView.
 * <p>
 * Uses {@link ListAdapter} + {@link DiffUtil} for efficient updates.
 * Timestamps are formatted to a readable Vietnamese locale date string.
 */
public class ReviewAdapter extends ListAdapter<Review, ReviewAdapter.ReviewViewHolder> {

    public ReviewAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Review> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Review>() {
                @Override
                public boolean areItemsTheSame(@NonNull Review oldItem, @NonNull Review newItem) {
                    return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Review oldItem, @NonNull Review newItem) {
                    return oldItem.getContent().equals(newItem.getContent())
                            && oldItem.getRating() == newItem.getRating();
                }
            };

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final TextView   tvUserName;
        private final TextView   tvContent;
        private final TextView   tvDate;
        private final TextView   tvRating;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvReviewerName);
            tvContent  = itemView.findViewById(R.id.tvReviewContent);
            tvDate     = itemView.findViewById(R.id.tvReviewDate);
            tvRating   = itemView.findViewById(R.id.tvReviewRating);
        }

        public void bind(Review review) {
            // Use userId as placeholder — in production, resolve to display name via UserRepository
            tvUserName.setText(review.getUserId() != null ? "Người dùng" : "Ẩn danh");
            tvContent.setText(review.getContent());
            if (tvRating != null) tvRating.setText(String.valueOf(review.getRating()));

            // Format epoch milliseconds to readable date
            if (review.getTimestamp() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                tvDate.setText(sdf.format(new Date(review.getTimestamp())));
            } else {
                tvDate.setText("");
            }
        }
    }
}
