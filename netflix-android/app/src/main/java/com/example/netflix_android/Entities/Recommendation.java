package com.example.netflix_android.Entities;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Recommendation {

    @SerializedName("recommendedMovies")
    private List<Integer> recommendedMovieIds; // List of recommended movie IDs

    public Recommendation(List<Integer> recommendedMovieIds) {
        this.recommendedMovieIds = recommendedMovieIds;
    }

    public List<Integer> getRecommendedMovieIds() {
        return recommendedMovieIds;
    }

    public void setRecommendedMovieIds(List<Integer> recommendedMovieIds) {
        this.recommendedMovieIds = recommendedMovieIds;
    }
}
