package com.cs218ai.myapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cs218ai.myapplication.R;
import com.cs218ai.myapplication.adapters.MovieAdapter;
import com.cs218ai.myapplication.models.Movie;
import com.cs218ai.myapplication.utils.NetworkUtils;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TopPicksFragment extends Fragment {
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private MaterialButton generateButton;
    private TextView curatedForUser;
    private int currentUserId = 1;
    private List<Movie> recommendations = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_top_picks, container, false);
            
            if (view == null) {
                return createErrorView(inflater);
            }
            
            recyclerView = view.findViewById(R.id.recommendationsRecyclerView);
            generateButton = view.findViewById(R.id.generateButton);
            curatedForUser = view.findViewById(R.id.curatedForUser);
            
            if (recyclerView == null || generateButton == null || curatedForUser == null) {
                return view; // Return view even if some components are missing
            }
            
            Context context = getContext();
            if (context == null) {
                return view;
            }
            
            // Initialize adapter with empty list first to prevent null ViewHolder issues
            // Initialize adapter
            adapter = new MovieAdapter(movie -> {
                try {
                    // Add to watchlist - this will be handled by MainActivity
                    if (isAdded() && getActivity() != null && getActivity() instanceof OnWatchlistUpdateListener) {
                        ((OnWatchlistUpdateListener) getActivity()).onAddToWatchlist(movie);
                        Context ctx = getContext();
                        if (ctx != null) {
                            Toast.makeText(ctx, "Added to watchlist!", Toast.LENGTH_SHORT).show();
                        }
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
            
            generateButton.setOnClickListener(v -> {
                try {
                    generateRecommendations();
                } catch (Exception e) {
                    e.printStackTrace();
                    Context ctx = getContext();
                    if (ctx != null && isAdded()) {
                        Toast.makeText(ctx, "Error generating recommendations", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            
            updateCuratedText();
            
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

    private void generateRecommendations() {
        try {
            Context context = getContext();
            if (context == null || !isAdded()) {
                return;
            }
            
            // Check network connectivity
            if (!NetworkUtils.isNetworkAvailable(context)) {
                NetworkUtils.showNetworkError(context);
                // Still show mock data for offline mode
            }
            
            // TODO: Replace with actual API call
            // For now, generate mock recommendations
            recommendations.clear();
            Random random = new Random();
            String[] movieTitles = {
                "Toy Story", "GoldenEye", "Four Rooms", "Get Shorty", "Copycat",
                "Shanghai Triad", "Twelve Monkeys", "Babe", "Dead Man Walking", "Usual Suspects"
            };
            
            for (int i = 0; i < 12; i++) {
                Movie movie = new Movie();
                movie.setMovieId(i + 1);
                movie.setTitle(movieTitles[i % movieTitles.length] + " " + (i + 1));
                movie.setPredictedRating(3.5 + random.nextDouble() * 1.5);
                movie.setConfidence(0.7 + random.nextDouble() * 0.3);
                recommendations.add(movie);
            }
            
            if (adapter != null) {
                adapter.setMovies(recommendations);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Context context = getContext();
            if (context != null && isAdded()) {
                Toast.makeText(context, "Error loading recommendations", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setUserId(int userId) {
        this.currentUserId = userId;
        updateCuratedText();
    }

    private void updateCuratedText() {
        try {
            if (curatedForUser != null && isAdded() && getContext() != null) {
                curatedForUser.setText(getString(R.string.curated_for_user, currentUserId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnWatchlistUpdateListener {
        void onAddToWatchlist(Movie movie);
    }
}
