package com.cs218ai.myapplication.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.cs218ai.myapplication.fragments.InsightsFragment;
import com.cs218ai.myapplication.fragments.TopPicksFragment;
import com.cs218ai.myapplication.fragments.WatchHistoryFragment;
import com.cs218ai.myapplication.fragments.WatchlistFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private TopPicksFragment topPicksFragment;
    private WatchHistoryFragment watchHistoryFragment;
    private InsightsFragment insightsFragment;
    private WatchlistFragment watchlistFragment;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        try {
            switch (position) {
                case 0:
                    if (topPicksFragment == null) {
                        topPicksFragment = new TopPicksFragment();
                    }
                    return topPicksFragment;
                case 1:
                    if (watchHistoryFragment == null) {
                        watchHistoryFragment = new WatchHistoryFragment();
                    }
                    return watchHistoryFragment;
                case 2:
                    if (insightsFragment == null) {
                        insightsFragment = new InsightsFragment();
                    }
                    return insightsFragment;
                case 3:
                    if (watchlistFragment == null) {
                        watchlistFragment = new WatchlistFragment();
                    }
                    return watchlistFragment;
                default:
                    if (topPicksFragment == null) {
                        topPicksFragment = new TopPicksFragment();
                    }
                    return topPicksFragment;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Return a default fragment if creation fails
            return new TopPicksFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public TopPicksFragment getTopPicksFragment() {
        try {
            return topPicksFragment;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public WatchHistoryFragment getWatchHistoryFragment() {
        try {
            return watchHistoryFragment;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public InsightsFragment getInsightsFragment() {
        try {
            return insightsFragment;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public WatchlistFragment getWatchlistFragment() {
        try {
            return watchlistFragment;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
