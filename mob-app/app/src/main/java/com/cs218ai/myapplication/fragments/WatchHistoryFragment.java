package com.cs218ai.myapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cs218ai.myapplication.R;
import com.cs218ai.myapplication.adapters.MovieAdapter;
import com.cs218ai.myapplication.models.Movie;
import java.util.ArrayList;
import java.util.List;

public class WatchHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private MovieAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_watch_history, container, false);
            
            if (view == null) {
                return createErrorView(inflater);
            }
            
            recyclerView = view.findViewById(R.id.historyRecyclerView);
            
            if (recyclerView == null) {
                return view;
            }
            
            Context context = getContext();
            if (context == null || !isAdded()) {
                return view;
            }
            
            // Initialize adapter
            adapter = new MovieAdapter(null); // No watchlist functionality in history
            
            // Set layout manager first
            try {
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
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
            
            // TODO: Load user history from API
            loadHistory();
            
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

    private void loadHistory() {
        try {
            // TODO: Replace with actual API call
            List<Movie> history = new ArrayList<>();
            // Mock data for now
            if (adapter != null) {
                adapter.setMovies(history);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUserId(int userId) {
        // Reload history when user changes
        loadHistory();
    }
}
