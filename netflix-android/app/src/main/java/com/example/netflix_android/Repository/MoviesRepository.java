package com.example.netflix_android.Repository;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.netflix_android.Api.MoviesApi;
import com.example.netflix_android.Database.MovieDao;
import com.example.netflix_android.Database.AppDatabase;
import com.example.netflix_android.Entities.MoviesResponse;
import com.example.netflix_android.Entities.MoviesResults;
import com.example.netflix_android.Network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MoviesRepository {
    private final MoviesApi moviesApi;
    private final MovieDao movieDao;
    private final ExecutorService executorService;
    private static final String TAG = "MoviesRepository";

    public MoviesRepository(Context context) {
        moviesApi = RetrofitClient.getRetrofitInstance(context).create(MoviesApi.class);
        AppDatabase database = AppDatabase.getInstance(context);
        movieDao = database.movieDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    // ✅ Fetch movies grouped by categories from API & store locally
    public LiveData<List<MoviesResults>> getMovies() {
        MutableLiveData<List<MoviesResults>> moviesLiveData = new MutableLiveData<>();

        Log.d(TAG, "Fetching movies from API...");
        moviesApi.getMovies().enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MoviesResults> moviesResultsList = response.body().getMoviesResultsList();
                    Log.d(TAG, "Movies fetched successfully: " + moviesResultsList.size() + " categories found.");

                    // ✅ Store movies in local database
                    executorService.execute(() -> {
                        movieDao.deleteAllMovies(); // Clear old movies before inserting new ones
                        for (MoviesResults moviesResults : moviesResultsList) {
                            movieDao.insertMovies(moviesResults.getMovies());
                        }
                    });

                    moviesLiveData.postValue(moviesResultsList);
                } else {
                    Log.e(TAG, "Failed to fetch movies. Response code: " + response.code());
                    moviesLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                Log.e(TAG, "Network error while fetching movies: " + t.getMessage(), t);
                moviesLiveData.postValue(null);
            }
        });

        return moviesLiveData;
    }
}
