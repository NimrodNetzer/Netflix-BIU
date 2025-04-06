package com.example.netflix_android.Api;

import com.example.netflix_android.Entities.SearchResult;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SearchApi {

    // Search movies by query
    @GET("/api/movies/search/{query}")
    Call<List<SearchResult>> searchMovies(@Path("query") String query);
}
