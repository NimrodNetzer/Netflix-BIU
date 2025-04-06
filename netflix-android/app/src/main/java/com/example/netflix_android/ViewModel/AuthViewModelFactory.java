package com.example.netflix_android.ViewModel;

import android.content.Context;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AuthViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public AuthViewModelFactory(Context context) {
        this.context = context.getApplicationContext(); // Prevent memory leaks
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AuthViewModel.class)) {
            return (T) new AuthViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
