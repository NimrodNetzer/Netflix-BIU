package com.example.netflix_android.ViewModel;

import android.content.Context;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.netflix_android.Repository.MoviesRepository;

public class MoviesViewModelFactory implements ViewModelProvider.Factory {
    private final MoviesRepository moviesRepository;

    public MoviesViewModelFactory(Context context) {
        this.moviesRepository = new MoviesRepository(context);
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MoviesViewModel.class)) {
            return (T) new MoviesViewModel(moviesRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
