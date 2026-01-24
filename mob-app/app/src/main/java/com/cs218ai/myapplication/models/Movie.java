package com.cs218ai.myapplication.models;

public class Movie {
    private int movieId;
    private String title;
    private double predictedRating;
    private double confidence;
    private String releaseDate;
    private int rating;

    public Movie() {}

    public Movie(int movieId, String title, double predictedRating, double confidence) {
        this.movieId = movieId;
        this.title = title;
        this.predictedRating = predictedRating;
        this.confidence = confidence;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPredictedRating() {
        return predictedRating;
    }

    public void setPredictedRating(double predictedRating) {
        this.predictedRating = predictedRating;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
