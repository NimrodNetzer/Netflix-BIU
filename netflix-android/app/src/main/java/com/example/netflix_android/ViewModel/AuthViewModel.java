package com.example.netflix_android.ViewModel;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.netflix_android.Repository.UserRepository;

public class AuthViewModel extends ViewModel {
    private final UserRepository userRepository;

    public AuthViewModel(Context context) {
        userRepository = new UserRepository(context);
    }

    public LiveData<Boolean> login(String email, String password) {
        return userRepository.login(email, password);
    }
    public LiveData<Boolean> signup(String email, String password, String nickname, String picture) {
        return userRepository.signup(email, password, nickname, picture);
    }
    public String getToken() {
        return userRepository.getToken();
    }

    public void logout() {
        userRepository.logout();
    }
}
