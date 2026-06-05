package com.example.angi_didau.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.angi_didau.ui.location.fragment.OverviewFragment;
import com.example.angi_didau.ui.location.fragment.PhotosFragment;
import com.example.angi_didau.ui.location.fragment.ReviewsFragment;

/**
 * Pager adapter for the Location Detail screen tabs (Overview / Photos / Reviews).
 * <p>
 * Renamed from {@code DetailPagerAdapter} to clarify its scope. The number of tabs
 * is defined by {@link #TOTAL_TABS} — add new tabs by extending this constant and
 * the switch statement in {@link #createFragment(int)}.
 */
public class LocationDetailPagerAdapter extends FragmentStateAdapter {

    private static final int TOTAL_TABS = 3;

    public LocationDetailPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1: return new PhotosFragment();
            case 2: return new ReviewsFragment();
            case 0:
            default: return new OverviewFragment();
        }
    }

    @Override
    public int getItemCount() {
        return TOTAL_TABS;
    }
}
