package com.example.netflix_android.Entities;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MoviesResponse {

    @SerializedName("movies")
    private List<MoviesResults> moviesResultsList;

    public MoviesResponse(List<MoviesResults> moviesResultsList) {
        this.moviesResultsList = moviesResultsList;
    }

    public List<MoviesResults> getMoviesResultsList() {
        return moviesResultsList;
    }

    public void setMoviesResultsList(List<MoviesResults> moviesResultsList) {
        this.moviesResultsList = moviesResultsList;
    }
}
