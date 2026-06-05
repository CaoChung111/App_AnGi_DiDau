package com.example.angi_didau.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.angi_didau.fragments.OverviewFragment;
import com.example.angi_didau.fragments.PhotosFragment;
import com.example.angi_didau.fragments.ReviewsFragment;

public class DetailPagerAdapter extends FragmentStateAdapter {

    public DetailPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new OverviewFragment();
            case 1:
                return new PhotosFragment();
            case 2:
                return new ReviewsFragment();
            default:
                return new OverviewFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
