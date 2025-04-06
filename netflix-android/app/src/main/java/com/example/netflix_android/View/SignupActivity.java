package com.example.netflix_android.View;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import com.example.netflix_android.R;
import com.example.netflix_android.ViewModel.AuthViewModel;
import com.example.netflix_android.ViewModel.AuthViewModelFactory;

public class SignupActivity extends AppCompatActivity {
    private AuthViewModel authViewModel;
    private EditText emailInput, passwordInput, nicknameInput;
    private Button signupButton;
    private TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ✅ Force dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        authViewModel = new ViewModelProvider(this, new AuthViewModelFactory(this)).get(AuthViewModel.class);

        emailInput = findViewById(R.id.signup_email);
        passwordInput = findViewById(R.id.signup_password);
        nicknameInput = findViewById(R.id.signup_nickname);
        signupButton = findViewById(R.id.signup_button);
        loginText = findViewById(R.id.text_login);

        signupButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String nickname = nicknameInput.getText().toString().trim();

            authViewModel.signup(email, password, nickname, "").observe(this, success -> {
                if (success) {
                    Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignupActivity.this, "Signup failed! Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        loginText.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
