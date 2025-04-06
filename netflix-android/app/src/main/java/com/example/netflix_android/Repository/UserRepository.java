package com.example.netflix_android.Repository;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.netflix_android.Api.AuthApi;
import com.example.netflix_android.Entities.User;
import com.example.netflix_android.Network.RetrofitClient;
import com.example.netflix_android.Utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Map;

public class UserRepository {
    private AuthApi authApi;
    private SessionManager sessionManager;

    public UserRepository(Context context) {
        authApi = RetrofitClient.getRetrofitInstance(context).create(AuthApi.class);
        sessionManager = new SessionManager(context);
    }

    public LiveData<Boolean> login(String email, String password) {
        MutableLiveData<Boolean> success = new MutableLiveData<>();
        User loginUser = new User(email, password, null, null, null);

        Log.d("LoginDebug", "Attempting login with email: " + email);

        authApi.login(loginUser).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().containsKey("token")) {
                    String token = response.body().get("token");
                    Log.d("LoginDebug", "Login successful. Token received: " + token);

                    // Store token in SharedPreferences
                    sessionManager.saveToken(token);

                    success.postValue(true);
                } else {
                    Log.e("LoginDebug", "Login failed: " + response.code() + " - " + response.message());
                    success.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.e("LoginDebug", "Login request failed", t);
                success.postValue(false);
            }
        });
        return success;
    }

    public LiveData<Boolean> signup(String email, String password, String nickname, String picture) {
        MutableLiveData<Boolean> success = new MutableLiveData<>();
        User newUser = new User(email, password, nickname, picture, null);

        Log.d("SignupDebug", "Attempting signup with email: " + email);

        authApi.signup(newUser).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("SignupDebug", "Signup successful");
                    success.postValue(true);
                } else {
                    Log.e("SignupDebug", "Signup failed: " + response.code() + " - " + response.message());
                    success.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("SignupDebug", "Signup request failed", t);
                success.postValue(false);
            }
        });
        return success;
    }

    public void logout() {
        sessionManager.clearSession(); // Clear stored token
    }

    public String getToken() {
        return sessionManager.getToken();
    }
}
