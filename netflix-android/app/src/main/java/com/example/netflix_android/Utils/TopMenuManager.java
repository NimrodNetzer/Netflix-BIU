package com.example.netflix_android.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.example.netflix_android.R;
import com.example.netflix_android.View.AdminActivity;
import com.example.netflix_android.View.MainActivity;
import com.example.netflix_android.View.SearchActivity;
import com.example.netflix_android.View.WelcomeActivity;

public class TopMenuManager {

    public static void setup(Activity activity) {   
        ImageView searchIcon = activity.findViewById(R.id.icon_search);
        ImageView netflixLogo = activity.findViewById(R.id.netflix_logo);
        Button exitButton = activity.findViewById(R.id.button_exit);
        ImageView adminButton = activity.findViewById(R.id.button_admin);
        SwitchCompat themeSwitch = activity.findViewById(R.id.theme_switch);

        if (searchIcon == null || netflixLogo == null || exitButton == null || adminButton == null || themeSwitch == null) {
            Log.e("TopMenuManager", "❌ Missing top menu views in layout.");
            return;
        }

        // 🌙 Theme toggle setup
        SharedPreferences prefs = activity.getSharedPreferences("theme_prefs", Activity.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", true);
        themeSwitch.setChecked(isDarkMode);
        themeSwitch.setText(isDarkMode ? "🌙" : "🌞");

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
            themeSwitch.setText(isChecked ? "🌙" : "🌞");
            activity.recreate(); // Refresh UI
        });

        // 👑 Admin button behavior
        SessionManager sessionManager = new SessionManager(activity);
        boolean isAdmin = sessionManager.isAdmin();
        if (isAdmin) {
            adminButton.setVisibility(View.VISIBLE);
            adminButton.setOnClickListener(v -> {
                if (!(activity instanceof AdminActivity)) {
                    Log.d("TopMenuManager", "👑 Switching to AdminActivity");
                    Intent intent = new Intent(activity, AdminActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                    activity.finish();
                } else {
                    Log.d("TopMenuManager", "⚠️ Already in AdminActivity, skipping restart");
                }
            });
        } else {
            adminButton.setVisibility(View.GONE);
        }

        // 🔄 Netflix logo behavior
        netflixLogo.setOnClickListener(v -> {
            if (!(activity instanceof MainActivity)) {
                Log.d("TopMenuManager", "🔁 Going to MainActivity");
                Intent intent = new Intent(activity, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.finish();
            } else {
                Log.d("TopMenuManager", "🔁 Refreshing MainActivity");
                activity.recreate();
            }
        });

        // 🔍 Search icon
        searchIcon.setOnClickListener(v -> {
            Log.d("TopMenuManager", "🔍 Going to SearchActivity");
            activity.startActivity(new Intent(activity, SearchActivity.class));
        });

        // 🚪 Exit button
        exitButton.setOnClickListener(v -> {
            Log.d("TopMenuManager", "🚪 Logging out and going to WelcomeActivity");
            Intent intent = new Intent(activity, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
        });
    }
}
