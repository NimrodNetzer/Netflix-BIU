package com.example.netflix_android.Repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.netflix_android.Api.MovieApi;
import com.example.netflix_android.Database.AppDatabase;
import com.example.netflix_android.Database.MovieDao;
import com.example.netflix_android.Entities.Movie;
import com.example.netflix_android.Network.RetrofitClient;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class MovieRepository {
    private final MovieDao movieDao;
    private final MovieApi movieApi;
    private final MovieUploadService movieUploadService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public MovieRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        movieDao = db.movieDao();
        movieApi = RetrofitClient.getRetrofitInstance(context).create(MovieApi.class);
        movieUploadService = new MovieUploadService(context);
    }

    // Fetch a movie by ID (from API and cache locally)
    public LiveData<Movie> getMovieById(String movieId) {
        MutableLiveData<Movie> movieLiveData = new MutableLiveData<>();

        movieApi.getMovieById(movieId).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Movie movie = response.body();

                    // Save in Room DB for caching
                    executorService.execute(() -> movieDao.insertMovie(movie));

                    movieLiveData.postValue(movie);
                } else {
                    movieLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                movieLiveData.postValue(null);
            }
        });

        return movieLiveData;
    }

    // Create a new movie
    public LiveData<Boolean> createMovie(Movie movie, File imageFile, File videoFile, String token) {
        return movieUploadService.uploadMovie(movie, imageFile, videoFile, token);
    }

    // Update an existing movie
    public LiveData<Boolean> updateMovie(String movieId, Movie movie, File imageFile, File videoFile, String token) {
        return movieUploadService.updateMovie(movieId, movie, imageFile, videoFile, token);
    }

    // Delete a movie
    public LiveData<Boolean> deleteMovie(String movieId) {
        MutableLiveData<Boolean> successLiveData = new MutableLiveData<>();

        movieApi.deleteMovie(movieId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    executorService.execute(() -> movieDao.deleteMovie(new Movie(movieId, "", "", "", "", 0, "", null, "", null, null, null, "")));
                    successLiveData.postValue(true);
                } else {
                    successLiveData.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                successLiveData.postValue(false);
            }
        });

        return successLiveData;
    }
}