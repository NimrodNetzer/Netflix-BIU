package com.example.netflix_android.Repository;

import android.util.Log;
import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.netflix_android.Api.SearchApi;
import com.example.netflix_android.Entities.SearchResult;
import com.example.netflix_android.Network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchRepository {
    private final SearchApi searchApi;
    private static final String TAG = "SearchDebug";

    public SearchRepository(Context context) {
        searchApi = RetrofitClient.getRetrofitInstance(context).create(SearchApi.class);
    }

    // Search movies by query
    public LiveData<List<SearchResult>> searchMovies(String query) {
        MutableLiveData<List<SearchResult>> searchResults = new MutableLiveData<>();

        Log.d(TAG, "Starting search for query: " + query); // Log when search starts

        searchApi.searchMovies(query).enqueue(new Callback<List<SearchResult>>() {
            @Override
            public void onResponse(Call<List<SearchResult>> call, Response<List<SearchResult>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && !response.body().isEmpty()) {
                        Log.d(TAG, "Search successful, received " + response.body().size() + " movies.");
                        searchResults.postValue(response.body());
                    } else {
                        Log.d(TAG, "Search returned empty response.");
                        searchResults.postValue(null);
                    }
                } else {
                    Log.e(TAG, "Search request failed. Response code: " + response.code());
                    Log.e(TAG, "Error message: " + response.message());
                    searchResults.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<SearchResult>> call, Throwable t) {
                Log.e(TAG, "Search request failed due to network error: " + t.getMessage(), t);
                searchResults.postValue(null);
            }
        });

        return searchResults;
    }
}
