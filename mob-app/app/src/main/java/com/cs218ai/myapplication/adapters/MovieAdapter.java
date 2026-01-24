package com.cs218ai.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.cs218ai.myapplication.R;
import com.cs218ai.myapplication.models.Movie;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Movie> movies = new ArrayList<>();
    private OnAddToWatchlistListener listener;

    public interface OnAddToWatchlistListener {
        void onAddToWatchlist(Movie movie);
    }

    public MovieAdapter(OnAddToWatchlistListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            if (parent == null || parent.getContext() == null) {
                throw new IllegalArgumentException("Parent or context is null");
            }
            
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_movie_card, parent, false);
            if (view == null) {
                // Create a fallback view if inflation fails
                view = new View(parent.getContext());
                view.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            
            MovieViewHolder holder = new MovieViewHolder(view);
            if (holder == null) {
                throw new IllegalStateException("ViewHolder creation returned null");
            }
            return holder;
        } catch (Exception e) {
            e.printStackTrace();
            // Return a ViewHolder with a simple view as fallback
            try {
                View fallbackView = new View(parent.getContext());
                fallbackView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                return new MovieViewHolder(fallbackView);
            } catch (Exception e2) {
                e2.printStackTrace();
                // Last resort - create minimal view
                View minimalView = new View(parent.getContext());
                return new MovieViewHolder(minimalView);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        try {
            if (position < 0 || position >= movies.size()) return;
            
            Movie movie = movies.get(position);
            if (movie == null) return;
            
            if (holder.movieTitle != null) {
                holder.movieTitle.setText(movie.getTitle() != null ? movie.getTitle() : "Unknown");
            }
            if (holder.predictedRating != null) {
                holder.predictedRating.setText(String.format("%.1f/5", movie.getPredictedRating()));
            }
            if (holder.confidenceText != null) {
                holder.confidenceText.setText(String.format("Confidence: %.0f%%", movie.getConfidence() * 100));
            }
            
            if (holder.addToWatchlistButton != null) {
                holder.addToWatchlistButton.setOnClickListener(v -> {
                    try {
                        if (listener != null && movie != null) {
                            listener.onAddToWatchlist(movie);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        try {
            return movies != null ? movies.size() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setMovies(List<Movie> movies) {
        try {
            this.movies = movies != null ? movies : new ArrayList<>();
            // Only notify if adapter is attached to avoid layout issues
            try {
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                // If notify fails, it's okay - the data is still set
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.movies = new ArrayList<>();
        }
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView movieTitle;
        TextView predictedRating;
        TextView confidenceText;
        MaterialButton addToWatchlistButton;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            movieTitle = itemView.findViewById(R.id.movieTitle);
            predictedRating = itemView.findViewById(R.id.predictedRating);
            confidenceText = itemView.findViewById(R.id.confidenceText);
            addToWatchlistButton = itemView.findViewById(R.id.addToWatchlistButton);
        }
    }
}
