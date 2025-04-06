package com.example.netflix_android.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.netflix_android.Adapters.CategoryAdapter;
import com.example.netflix_android.Entities.Movie;
import com.example.netflix_android.Entities.SearchResult;
import com.example.netflix_android.R;
import com.example.netflix_android.Utils.Constants;
import com.example.netflix_android.Utils.SessionManager;
import com.example.netflix_android.Utils.TopMenuManager;
import com.example.netflix_android.ViewModel.MoviesViewModel;
import com.example.netflix_android.ViewModel.MoviesViewModelFactory;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;
    private MoviesViewModel moviesViewModel;
    private VideoView featuredVideo;
    private TextView featuredVideoDescription;

    // Top menu items
    private ImageView searchIcon, netflixLogo;
    private Button exitButton, adminButton;
    private SwitchCompat themeSwitch;
    private Movie featuredMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ✅ Load saved theme preference
        SharedPreferences prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", true);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "🚀 App Started - Fetching Movies...");

        TopMenuManager.setup(this); // ✅ Shared top menu

        // UI Elements
        featuredVideo = findViewById(R.id.featured_video);
        featuredVideoDescription = findViewById(R.id.featured_video_description);
        categoriesRecyclerView = findViewById(R.id.categories_recycler_view);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ViewModel
        moviesViewModel = new ViewModelProvider(this, new MoviesViewModelFactory(this)).get(MoviesViewModel.class);

        loadMovies();
    }

    private void loadMovies() {
        moviesViewModel.getMovies().observe(this, moviesResultsList -> {
            if (moviesResultsList != null && !moviesResultsList.isEmpty()) {
                Map<String, List<Movie>> categorizedMovies = moviesViewModel.getMoviesGroupedByCategory(moviesResultsList);

                for (List<Movie> movieList : categorizedMovies.values()) {
                    if (!movieList.isEmpty()) {
                        featuredMovie = movieList.get(0);
                        break;
                    }
                }

                if (featuredMovie != null) {
                    String videoUrl = featuredMovie.getVideo();
                    if (videoUrl == null || videoUrl.isEmpty()) {
                        Log.e(TAG, "❌ No video URL found for movie: " + featuredMovie.getName());
                    } else {
                        Log.d(TAG, "🎬 Video URL retrieved: " + videoUrl);
                    }

                    featuredVideo.setVideoURI(Uri.parse(Constants.BASE_URL + videoUrl.replace("\\", "/")));
                    featuredVideo.setOnPreparedListener(mp -> {
                        mp.setLooping(true);
                        featuredVideo.start();
                    });
                    final String title;
                    final String imageUrl;
                    final String movieId;
                    final String details;
                    final String description;
                    final String video;
                    final String quality;
                    final String category;
                    final String duration;
                    final String age;
                    final String year;
                    Movie item = featuredMovie;
                    Movie movie = (Movie) item;
                    title = movie.getName();
                    imageUrl = Constants.BASE_URL + movie.getPicture().replace("\\", "/");
                    movieId = movie.getId();
                    description = movie.getDescription();
                    video = Constants.BASE_URL + movie.getVideo().replace("\\", "/");
                    quality = movie.getQuality();
                    duration = movie.getTime();
                    age = movie.getAge() + "+";
                    year = movie.getReleaseDate() != null
                            ? String.valueOf(movie.getReleaseDate().getYear() + 1900)
                            : "N/A";
                    category = (movie.getCategories() != null && !movie.getCategories().isEmpty())
                            ? movie.getCategories().get(0).getName()
                            : "N/A";
                    details = year + " | " + age + " | " + duration;

                    featuredVideo.setOnClickListener(v -> {
                        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
                        intent.putExtra("movie_id", featuredMovie.getId());
                        intent.putExtra("movie_title", featuredMovie.getName());
                        intent.putExtra("movie_image", Constants.BASE_URL + featuredMovie.getPicture().replace("\\", "/"));
                        intent.putExtra("movie_details", "2025  |  " + featuredMovie.getAge() + "+  |  " + featuredMovie.getTime());
                        intent.putExtra("movie_description", featuredMovie.getDescription());
                        intent.putExtra("video_url", Constants.BASE_URL + videoUrl.replace("\\", "/"));
                        intent.putExtra("movie_year", year);
                        intent.putExtra("movie_duration", duration);
                        intent.putExtra("movie_age", age);
                        intent.putExtra("movie_quality", quality);
                        intent.putExtra("chosen_category", category);
                        startActivity(intent);
                    });

                    featuredVideoDescription.setText(featuredMovie.getDescription());
                } else {
                    Log.e(TAG, "⚠️ No featured movie found!");
                }

                categoryAdapter = new CategoryAdapter(this, categorizedMovies);
                categoriesRecyclerView.setAdapter(categoryAdapter);
            } else {
                Log.e(TAG, "⚠️ No movies found.");
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "🔄 onResume called - Refreshing movies list.");
        loadMovies();
    }
}
