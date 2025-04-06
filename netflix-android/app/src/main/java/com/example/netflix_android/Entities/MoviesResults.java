package com.example.netflix_android.Entities;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MoviesResults {

    @SerializedName("category")
    private String categoryName;

    @SerializedName("category_id")
    private String categoryId;

    @SerializedName("promoted")
    private boolean promoted;

    @SerializedName("movies")
    private List<Movie> movies;

    public MoviesResults(String categoryName, String categoryId, boolean promoted, List<Movie> movies) {
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        this.promoted = promoted;
        this.movies = movies;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public boolean isPromoted() {
        return promoted;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setPromoted(boolean promoted) {
        this.promoted = promoted;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
}
