package com.cs218ai.myapplication.models;

public class UserStats {
    private int moviesRated;
    private double avgRating;
    private String favGenre;

    public UserStats() {}

    public UserStats(int moviesRated, double avgRating, String favGenre) {
        this.moviesRated = moviesRated;
        this.avgRating = avgRating;
        this.favGenre = favGenre;
    }

    public int getMoviesRated() {
        return moviesRated;
    }

    public void setMoviesRated(int moviesRated) {
        this.moviesRated = moviesRated;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public String getFavGenre() {
        return favGenre;
    }

    public void setFavGenre(String favGenre) {
        this.favGenre = favGenre;
    }
}
