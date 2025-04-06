package com.example.netflix_android.ViewModel;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.netflix_android.Entities.SearchResult;
import com.example.netflix_android.Repository.SearchRepository;
import java.util.List;

public class SearchViewModel extends ViewModel {
    private final SearchRepository searchRepository;

    public SearchViewModel(SearchRepository repository) {
        this.searchRepository = repository;
    }

    public LiveData<List<SearchResult>> searchMovies(String query) {
        return searchRepository.searchMovies(query);
    }
}
