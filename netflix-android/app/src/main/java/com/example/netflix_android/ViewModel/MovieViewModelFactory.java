package com.example.netflix_android.ViewModel;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.netflix_android.Repository.MovieRepository;

public class MovieViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public MovieViewModelFactory(Context context) {
        this.context = context.getApplicationContext(); // Prevent memory leaks
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MovieViewModel.class)) {
            MovieRepository repository = new MovieRepository(context);
            return (T) new MovieViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
