package com.cs218ai.myapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cs218ai.myapplication.R;
import com.cs218ai.myapplication.adapters.WatchlistAdapter;
import com.cs218ai.myapplication.models.Movie;
import java.util.ArrayList;
import java.util.List;

public class WatchlistFragment extends Fragment {
    private RecyclerView recyclerView;
    private WatchlistAdapter adapter;
    private TextView emptyWatchlistText;
    private List<Movie> watchlist = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_watchlist, container, false);
            
            if (view == null) {
                return createErrorView(inflater);
            }
            
            recyclerView = view.findViewById(R.id.watchlistRecyclerView);
            emptyWatchlistText = view.findViewById(R.id.emptyWatchlistText);
            
            if (recyclerView == null || emptyWatchlistText == null) {
                return view;
            }
            
            Context context = getContext();
            if (context == null || !isAdded()) {
                return view;
            }
            
            // Initialize adapter
            adapter = new WatchlistAdapter((movie, position) -> {
                try {
                    if (position >= 0 && position < watchlist.size()) {
                        watchlist.remove(position);
                        if (adapter != null) {
                            adapter.removeItem(position);
                        }
                        updateEmptyState();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            
            // Set layout manager first
            try {
                GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
                recyclerView.setLayoutManager(layoutManager);
                
                // Disable animations to prevent layout issues
                recyclerView.setItemAnimator(null);
                recyclerView.setHasFixedSize(false); // Allow RecyclerView to size itself
                recyclerView.setNestedScrollingEnabled(false); // Since it's in NestedScrollView
                
                // Set adapter - it already has empty list initialized
                recyclerView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            updateEmptyState();
            
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorView(inflater);
        }
    }
    
    private View createErrorView(LayoutInflater inflater) {
        try {
            return new View(inflater.getContext());
        } catch (Exception e) {
            return null;
        }
    }

    public void addToWatchlist(Movie movie) {
        try {
            if (movie == null) return;
            
            // Check if already in watchlist
            for (Movie m : watchlist) {
                if (m != null && m.getMovieId() == movie.getMovieId()) {
                    return; // Already in watchlist
                }
            }
            watchlist.add(movie);
            if (adapter != null) {
                adapter.setWatchlist(watchlist);
            }
            updateEmptyState();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateEmptyState() {
        try {
            if (emptyWatchlistText == null || recyclerView == null) return;
            
            if (watchlist.isEmpty()) {
                emptyWatchlistText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyWatchlistText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
