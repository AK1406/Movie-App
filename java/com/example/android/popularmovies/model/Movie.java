package com.example.android.popularmovies.model;

public class Movie {
    private String movieTitle, moviePoster, releaseDate, movieRating, movieOverview;
    private int movieId;

    public Movie(){
    }
/*
    public Movie(String title, String poster, String release, String rate, String overview, int id){
        movieTitle = title;
        moviePoster = poster;
        releaseDate = release;
        movieRating = rate;
        movieOverview = overview;
        movieId = id;
    } */

    public String getTitle() {
        return movieTitle;
    }

    public void setTitle(String title) {
        movieTitle = title;
    }

    public String getPoster() {
        return moviePoster;
    }

    public void setPoster(String poster) { moviePoster = poster;
    }

    public String getRelease() {
        return releaseDate;
    }

    public void setRelease(String release) {
        releaseDate = release;
    }

    public String getRate() {
        return movieRating;
    }

    public void setRate(String rate) { movieRating = rate;
    }

    public String getOverview() {
        return movieOverview;
    }

    public void setOverview(String overview) { movieOverview= overview;
    }

    public void setId(int id) {
        movieId = id;
    }

    public int getId() {
        return movieId;
    }
}
