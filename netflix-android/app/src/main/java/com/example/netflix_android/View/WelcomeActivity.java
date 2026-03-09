package com.example.netflix_android.View;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.netflix_android.R;

public class WelcomeActivity extends AppCompatActivity {
    private Button signInButton, getStartedButton, infoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        signInButton = findViewById(R.id.button_sign_in);
        getStartedButton = findViewById(R.id.button_get_started);
        infoButton = findViewById(R.id.button_info_welcome);

        signInButton.setOnClickListener(v ->
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class)));

        getStartedButton.setOnClickListener(v ->
            startActivity(new Intent(WelcomeActivity.this, SignupActivity.class)));

        infoButton.setOnClickListener(v ->
            startActivity(new Intent(WelcomeActivity.this, InfoActivity.class)));
    }
}
