package com.example.netflix_android.ViewModel;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.netflix_android.Repository.CategoryRepository;

public class CategoryViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public CategoryViewModelFactory(Context context) {
        this.context = context.getApplicationContext(); // Prevent memory leaks
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CategoryViewModel.class)) {
            CategoryRepository repository = new CategoryRepository(context);
            return (T) new CategoryViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
