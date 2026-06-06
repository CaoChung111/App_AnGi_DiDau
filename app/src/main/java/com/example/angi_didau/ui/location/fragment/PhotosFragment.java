package com.example.angi_didau.ui.location.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.angi_didau.R;
import com.example.angi_didau.adapter.PhotoGalleryAdapter;
import com.example.angi_didau.ui.location.LocationDetailViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

public class PhotosFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvPhotos = view.findViewById(R.id.rvPhotos);
        if (rvPhotos != null) {
            rvPhotos.setLayoutManager(new GridLayoutManager(getContext(), 3)); // 3 columns grid
            
            LocationDetailViewModel viewModel = new ViewModelProvider(requireActivity()).get(LocationDetailViewModel.class);
            viewModel.getLocation(requireActivity().getIntent().getStringExtra(com.example.angi_didau.common.constant.AppConstants.EXTRA_LOCATION_ID)).observe(getViewLifecycleOwner(), location -> {
                if (location != null) {
                    List<String> photos = location.getImageUrls();
                    if (photos == null || photos.isEmpty()) {
                        photos = new ArrayList<>();
                        if (location.getImageUrl() != null && !location.getImageUrl().isEmpty()) {
                            photos.add(location.getImageUrl());
                        }
                    }
                    PhotoGalleryAdapter adapter = new PhotoGalleryAdapter(photos);
                    rvPhotos.setAdapter(adapter);
                }
            });
        }
    }
}
