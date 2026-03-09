package com.example.netflix_android.View;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.netflix_android.R;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Button backButton = findViewById(R.id.info_back_button);
        backButton.setOnClickListener(v -> finish());
    }
}
