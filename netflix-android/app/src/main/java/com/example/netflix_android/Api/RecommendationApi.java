package com.example.netflix_android.Api;

import com.example.netflix_android.Entities.Recommendation;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RecommendationApi {

    // Fetch recommended movies for a user
    @GET("/api/movies/{id}/recommend")
    Call<Recommendation> getRecommendations(@Path("id") String movieId);

    // Add a watched movie for recommendation
    @POST("/api/movies/{id}/recommend")
    Call<Void> addRecommendation(@Path("id") String movieId);
}
