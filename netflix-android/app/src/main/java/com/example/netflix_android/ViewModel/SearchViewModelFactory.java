package com.example.netflix_android.ViewModel;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.netflix_android.Repository.SearchRepository;

public class SearchViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public SearchViewModelFactory(Context context) {
        this.context = context.getApplicationContext(); // Prevent memory leaks
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            SearchRepository repository = new SearchRepository(context);
            return (T) new SearchViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
