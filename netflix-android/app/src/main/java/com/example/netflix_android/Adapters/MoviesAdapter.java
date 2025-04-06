package com.example.netflix_android.Adapters;

import android.content.Intent;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.netflix_android.Entities.Movie;
import com.example.netflix_android.Entities.SearchResult;
import com.example.netflix_android.R;
import com.example.netflix_android.Utils.Constants;
import com.example.netflix_android.View.MovieDetailActivity;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private final Context context;
    private final List<?> movieList;  // Supports both Movie and SearchResult types

    public MoviesAdapter(Context context, List<?> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_card, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Object item = movieList.get(position);

        // Declare final variables for lambda scope
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

        if (item instanceof Movie) {
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
        } else if (item instanceof SearchResult) {
            SearchResult searchResult = (SearchResult) item;
            title = searchResult.getName();
            imageUrl = Constants.BASE_URL + searchResult.getPicture().replace("\\", "/");
            movieId = searchResult.getId();
            description = searchResult.getDescription();
            video = Constants.BASE_URL + searchResult.getVideo().replace("\\", "/");
            quality = "N/A";
            category = "N/A";
            duration = searchResult.getTime();
            age = searchResult.getAge() + "+";
            year = "2025"; // fallback
            details = year + " | " + age + " | " + duration;
        } else {
            return; // Prevent unexpected errors
        }

        // Set movie title
        holder.movieTitle.setText(title);

        // Load movie poster using Glide
        Glide.with(context)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.moviePoster);

        // Handle Click Event - Open Movie Details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("movie_id", movieId);
            intent.putExtra("movie_title", title);
            intent.putExtra("movie_image", imageUrl);
            intent.putExtra("movie_description", description);
            intent.putExtra("video_url", video);

            // ✅ NEW: Pass additional movie details
            intent.putExtra("movie_year", year);
            intent.putExtra("movie_duration", duration);
            intent.putExtra("movie_age", age);
            intent.putExtra("movie_quality", quality);
            intent.putExtra("chosen_category", category);

            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView moviePoster;
        TextView movieTitle;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            moviePoster = itemView.findViewById(R.id.movie_thumbnail);
            movieTitle = itemView.findViewById(R.id.movie_title);
        }
    }
}
