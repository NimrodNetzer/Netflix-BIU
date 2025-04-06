package com.example.netflix_android.View;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_android.Adapters.AdminCategoryAdapter;
import com.example.netflix_android.Adapters.CategoryAdapter;
import com.example.netflix_android.Entities.Category;
import com.example.netflix_android.Entities.Movie;
import com.example.netflix_android.R;
import com.example.netflix_android.Repository.CategoryRepository;
import com.example.netflix_android.Utils.TopMenuManager;
import com.example.netflix_android.ViewModel.CategoryViewModel;
import com.example.netflix_android.ViewModel.CategoryViewModelFactory;
import com.example.netflix_android.ViewModel.MoviesViewModel;
import com.example.netflix_android.ViewModel.MoviesViewModelFactory;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.List;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {
    private static final String TAG = "AdminActivity";

    private RecyclerView itemsRecyclerView;
    private CategoryAdapter categoryAdapter;
    private AdminCategoryAdapter adminCategoryAdapter;

    private MoviesViewModel moviesViewModel;
    private CategoryViewModel categoryViewModel;
    private CategoryRepository categoryRepository;
    private LiveData<List<Category>> categoryLiveData;
    private boolean isViewingMovies = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", true);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_settings);
        Log.d(TAG, "⚙️ Admin Panel Opened");

        TopMenuManager.setup(this);

        itemsRecyclerView = findViewById(R.id.admin_recycler_view);

        Button addMovieButton = findViewById(R.id.button_add_movie);
        Button addCategoryButton = findViewById(R.id.button_add_category);
        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.toggle_group);
        MaterialButton toggleMovies = findViewById(R.id.toggle_movies);
        MaterialButton toggleCategories = findViewById(R.id.toggle_categories);

        moviesViewModel = new ViewModelProvider(this, new MoviesViewModelFactory(this)).get(MoviesViewModel.class);
        categoryViewModel = new ViewModelProvider(this, new CategoryViewModelFactory(this)).get(CategoryViewModel.class);
        categoryRepository = new CategoryRepository(this);

        loadMovies();

        addMovieButton.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, MovieFormActivity.class));
        });

        addCategoryButton.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, AddCategoryActivity.class));
        });

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.toggle_movies) {
                    isViewingMovies = true;
                    toggleMovies.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.button_red));
                    toggleCategories.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.button_gray));
                    loadMovies();
                } else if (checkedId == R.id.toggle_categories) {
                    isViewingMovies = false;
                    toggleCategories.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.button_red));
                    toggleMovies.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.button_gray));
                    loadCategories();
                }
            }
        });
    }

    private void loadMovies() {
        moviesViewModel.getMovies().observe(this, moviesResultsList -> {
            if (moviesResultsList != null && !moviesResultsList.isEmpty()) {
                Map<String, List<Movie>> categorizedMovies = moviesViewModel.getMoviesGroupedByCategory(moviesResultsList);
                itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                categoryAdapter = new CategoryAdapter(this, categorizedMovies);
                itemsRecyclerView.setAdapter(categoryAdapter);
            }
            else {
                // Clear adapter if movie list is empty
                itemsRecyclerView.setAdapter(null);
            }
        });
    }

    public void loadCategories() {
        itemsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        categoryLiveData = categoryRepository.getCategories();
        categoryLiveData.observe(this, categories -> {
            if (categories != null && !categories.isEmpty()) {
                adminCategoryAdapter = new AdminCategoryAdapter(this, categories, categoryViewModel);
                itemsRecyclerView.setAdapter(adminCategoryAdapter);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isViewingMovies) {
            loadMovies();
        } else {
            loadCategories();
        }
    }

    @SuppressWarnings("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Log.d(TAG, "🔙 Back pressed - Navigating to MainActivity");
        Intent intent = new Intent(AdminActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
