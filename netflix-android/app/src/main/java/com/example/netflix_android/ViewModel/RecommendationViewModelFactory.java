package com.example.netflix_android.ViewModel;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.netflix_android.Repository.RecommendationRepository;

public class RecommendationViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public RecommendationViewModelFactory(Context context) {
        this.context = context.getApplicationContext(); // Prevent memory leaks
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RecommendationViewModel.class)) {
            RecommendationRepository repository = new RecommendationRepository(context);
            return (T) new RecommendationViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
