package com.example.netflix_android.Api;

import com.example.netflix_android.Entities.MoviesResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface MoviesApi {

    // Fetch all movies grouped by categories
    @GET("/api/movies")
    Call<MoviesResponse> getMovies();
}
