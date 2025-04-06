package com.example.netflix_android.Repository;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.netflix_android.Api.MovieApi;
import com.example.netflix_android.Entities.Movie;
import com.example.netflix_android.Network.RetrofitClient;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieUploadService {
    private static final String TAG = "MovieUploadService";
    private final MovieApi movieApi;

    public MovieUploadService(Context context) {
        movieApi = RetrofitClient.getRetrofitInstance(context).create(MovieApi.class);
    }

    public LiveData<Boolean> uploadMovie(Movie movie, File imageFile, File videoFile, String token) {
        MutableLiveData<Boolean> uploadStatus = new MutableLiveData<>();
        sendMultipartRequest(movieApi.createMovie(prepareRequestBody(movie), prepareImagePart(imageFile), prepareVideoPart(videoFile), "Bearer " + token), uploadStatus);
        return uploadStatus;
    }

    public LiveData<Boolean> updateMovie(String movieId, Movie movie, File imageFile, File videoFile, String token) {
        MutableLiveData<Boolean> uploadStatus = new MutableLiveData<>();
        sendMultipartRequest(movieApi.updateMovie(movieId, prepareRequestBody(movie), prepareImagePart(imageFile), prepareVideoPart(videoFile), "Bearer " + token), uploadStatus);
        return uploadStatus;
    }

    private RequestBody prepareRequestBody(Movie movie) {
        String movieJson = new com.google.gson.Gson().toJson(movie);
        return RequestBody.create(MediaType.parse("application/json"), movieJson);
    }

    private MultipartBody.Part prepareImagePart(File imageFile) {
        RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/png"), imageFile);
        return MultipartBody.Part.createFormData("image", imageFile.getName(), imageRequestBody);
    }

    private MultipartBody.Part prepareVideoPart(File videoFile) {
        RequestBody videoRequestBody = RequestBody.create(MediaType.parse("video/mp4"), videoFile);
        return MultipartBody.Part.createFormData("video", videoFile.getName(), videoRequestBody);
    }


    private void sendMultipartRequest(Call<Void> call, MutableLiveData<Boolean> uploadStatus) {
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    uploadStatus.postValue(true);
                    Log.d(TAG, "✅ Movie uploaded successfully");
                } else {
                    logErrorResponse("upload/update movie", response);
                    uploadStatus.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "❌ Network error while uploading/updating movie", t);
                uploadStatus.postValue(false);
            }
        });
    }

    private void logErrorResponse(String action, Response<?> response) {
        Log.e(TAG, "❌ Failed to " + action + ". HTTP Code: " + response.code());
        try {
            Log.e(TAG, "Response error: " + response.errorBody().string());
        } catch (Exception e) {
            Log.e(TAG, "Error reading response body", e);
        }
    }
}