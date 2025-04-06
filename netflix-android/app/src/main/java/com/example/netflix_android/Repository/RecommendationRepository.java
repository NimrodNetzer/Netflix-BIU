package com.example.netflix_android.Repository;

import android.util.Log;
import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.netflix_android.Api.MovieApi;
import com.example.netflix_android.Api.RecommendationApi;
import com.example.netflix_android.Entities.Movie;
import com.example.netflix_android.Entities.Recommendation;
import com.example.netflix_android.Network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendationRepository {
    private final RecommendationApi recommendationApi;
    private final MovieApi movieApi;
    private static final String TAG = "RecommendationDebug";

    public RecommendationRepository(Context context) {
        recommendationApi = RetrofitClient.getRetrofitInstance(context).create(RecommendationApi.class);
        movieApi = RetrofitClient.getRetrofitInstance(context).create(MovieApi.class);
    }

    // ✅ Fetch full movie details for recommended movies
    public LiveData<List<Movie>> getRecommendedMovies(String movieId) {
        MutableLiveData<List<Movie>> recommendedMoviesLiveData = new MutableLiveData<>();

        Log.d(TAG, "Fetching recommended movie IDs for user: " );

        recommendationApi.getRecommendations(movieId).enqueue(new Callback<Recommendation>() {
            @Override
            public void onResponse(Call<Recommendation> call, Response<Recommendation> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Integer> movieIds = response.body().getRecommendedMovieIds();
                    Log.d(TAG, "Received recommended movie IDs: " + movieIds);

                    if (movieIds.isEmpty()) {
                        recommendedMoviesLiveData.postValue(new ArrayList<>());
                        return;
                    }

                    fetchMoviesByIds(movieIds, recommendedMoviesLiveData);
                } else {
                    Log.e(TAG, "Failed to fetch recommendations. Response code: " + response.code());
                    recommendedMoviesLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Recommendation> call, Throwable t) {
                Log.e(TAG, "Network error fetching recommendations: " + t.getMessage(), t);
                recommendedMoviesLiveData.postValue(null);
            }
        });

        return recommendedMoviesLiveData;
    }

    // ✅ Fetch full movie details for a list of IDs
    private void fetchMoviesByIds(List<Integer> movieIds, MutableLiveData<List<Movie>> recommendedMoviesLiveData) {
        List<Movie> movieList = new ArrayList<>();
        for (Integer movieId : movieIds) {
            movieApi.getMovieById(String.valueOf(movieId)).enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(Call<Movie> call, Response<Movie> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        movieList.add(response.body());
                        Log.d(TAG, "Fetched movie details: " + response.body().getName());

                        // If all movies are fetched, update LiveData
                        if (movieList.size() == movieIds.size()) {
                            recommendedMoviesLiveData.postValue(movieList);
                        }
                    } else {
                        Log.e(TAG, "Failed to fetch movie ID: " + movieId);
                    }
                }

                @Override
                public void onFailure(Call<Movie> call, Throwable t) {
                    Log.e(TAG, "Network error fetching movie ID: " + movieId + ", Error: " + t.getMessage(), t);
                }
            });
        }
    }

    // ✅ Add a movie to recommendations
    public LiveData<Boolean> addRecommendation(String movieId) {
        MutableLiveData<Boolean> successLiveData = new MutableLiveData<>();

        Log.d(TAG, "Adding recommendation for movie: " + movieId);

        recommendationApi.addRecommendation(movieId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Successfully added recommendation for movie ID: " + movieId);
                    successLiveData.postValue(true);
                } else {
                    Log.e(TAG, "Failed to add recommendation. Response code: " + response.code());
                    successLiveData.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network error adding recommendation: " + t.getMessage(), t);
                successLiveData.postValue(false);
            }
        });

        return successLiveData;
    }
}
