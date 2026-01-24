package com.cs218ai.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cs218ai.myapplication.R;
import com.cs218ai.myapplication.models.Movie;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.WatchlistViewHolder> {
    private List<Movie> watchlist = new ArrayList<>();
    private OnRemoveListener listener;

    public interface OnRemoveListener {
        void onRemove(Movie movie, int position);
    }

    public WatchlistAdapter(OnRemoveListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public WatchlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            if (parent == null || parent.getContext() == null) {
                throw new IllegalArgumentException("Parent or context is null");
            }
            
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_watchlist_card, parent, false);
            if (view == null) {
                // Create a fallback view if inflation fails
                view = new View(parent.getContext());
                view.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            
            WatchlistViewHolder holder = new WatchlistViewHolder(view);
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
                return new WatchlistViewHolder(fallbackView);
            } catch (Exception e2) {
                e2.printStackTrace();
                // Last resort - create minimal view
                View minimalView = new View(parent.getContext());
                return new WatchlistViewHolder(minimalView);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull WatchlistViewHolder holder, int position) {
        try {
            if (position < 0 || position >= watchlist.size()) return;
            
            Movie movie = watchlist.get(position);
            if (movie == null) return;
            
            if (holder.movieTitle != null) {
                holder.movieTitle.setText(movie.getTitle() != null ? movie.getTitle() : "Unknown");
            }
            if (holder.rating != null) {
                holder.rating.setText(String.format("%.1f", movie.getPredictedRating()));
            }
            
            if (holder.removeButton != null) {
                holder.removeButton.setOnClickListener(v -> {
                    try {
                        if (listener != null && movie != null) {
                            listener.onRemove(movie, position);
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
            return watchlist != null ? watchlist.size() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setWatchlist(List<Movie> watchlist) {
        try {
            this.watchlist = watchlist != null ? watchlist : new ArrayList<>();
            // Only notify if adapter is attached to avoid layout issues
            try {
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                // If notify fails, it's okay - the data is still set
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.watchlist = new ArrayList<>();
        }
    }

    public void removeItem(int position) {
        if (position >= 0 && position < watchlist.size()) {
            watchlist.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, watchlist.size());
        }
    }

    static class WatchlistViewHolder extends RecyclerView.ViewHolder {
        TextView movieTitle;
        TextView rating;
        MaterialButton removeButton;

        WatchlistViewHolder(@NonNull View itemView) {
            super(itemView);
            movieTitle = itemView.findViewById(R.id.watchlistMovieTitle);
            rating = itemView.findViewById(R.id.watchlistRating);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}
