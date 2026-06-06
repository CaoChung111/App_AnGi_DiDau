package com.example.angi_didau.ui.location.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.angi_didau.R;
import com.example.angi_didau.adapter.ReviewAdapter;
import com.example.angi_didau.data.model.Review;
import com.example.angi_didau.ui.location.LocationDetailViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

public class ReviewsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvReviews = view.findViewById(R.id.rvReviews);
        if (rvReviews != null) {
            rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));
            LocationDetailViewModel viewModel = new ViewModelProvider(requireActivity()).get(LocationDetailViewModel.class);
            ReviewAdapter adapter = new ReviewAdapter();
            rvReviews.setAdapter(adapter);

            viewModel.getReviews().observe(getViewLifecycleOwner(), reviews -> {
                if (reviews != null) {
                    adapter.submitList(reviews);
                }
            });
        }
    }
}
