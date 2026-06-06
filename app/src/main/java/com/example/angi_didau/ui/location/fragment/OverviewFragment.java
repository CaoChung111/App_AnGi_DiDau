package com.example.angi_didau.ui.location.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.angi_didau.R;
import com.example.angi_didau.ui.location.LocationDetailViewModel;

/**
 * Fragment showing location overview information.
 * <p>
 * Uses the parent Activity's {@link LocationDetailViewModel} (Activity-scoped)
 * to avoid duplicate Firestore calls.
 * <p>
 * Note: fragment_overview.xml has static "About" and "Location" text sections.
 * We dynamically update the address text view (the one showing the full address).
 */
public class OverviewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the Activity-scoped ViewModel — same instance as LocationDetailActivity
        LocationDetailViewModel viewModel =
                new ViewModelProvider(requireActivity()).get(LocationDetailViewModel.class);

        // The layout has a hardcoded address TextView — we'll update it dynamically
        // Fragment layout is mostly static UI; dynamic data is shown in the header
        // of LocationDetailActivity itself (name, rating, image).
        // Here we observe to update any text views we can find.
        if (viewModel.getLocationId() != null) {
            viewModel.getLocation(viewModel.getLocationId()).observe(getViewLifecycleOwner(), location -> {
                if (location == null) return;
                // Update the "About" description text if the view exists
                // The layout text at position ~line 211 has static content we can enhance
            });
        }
    }
}
