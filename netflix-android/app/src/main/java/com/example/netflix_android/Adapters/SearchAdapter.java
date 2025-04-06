package com.example.netflix_android.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.netflix_android.Entities.SearchResult;
import com.example.netflix_android.R;
import com.example.netflix_android.Utils.Constants;
import com.example.netflix_android.View.MovieDetailActivity;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private Context context;
    private List<SearchResult> searchResults;

    public SearchAdapter(Context context, List<SearchResult> searchResults) {
        this.context = context;
        this.searchResults = searchResults;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_card, parent, false);
        return new SearchViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        SearchResult movie = searchResults.get(position);
        holder.movieTitle.setText(movie.getName());

        String imageUrl = Constants.BASE_URL + movie.getPicture().replace("\\", "/");

        Glide.with(context)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.moviePoster);

        String video = Constants.BASE_URL + movie.getVideo().replace("\\", "/");

        // Extract extra details for consistency
        String title = movie.getName();
        String movieId = movie.getId();
        String description = movie.getDescription();
        String duration = movie.getTime();
        String age = movie.getAge() + "+";
        String year = "2025"; // fallback; ideally from movie.getReleaseDate()
        String quality = "N/A";
        String category = "N/A";

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("movie_id", movieId);
            intent.putExtra("movie_title", title);
            intent.putExtra("movie_image", imageUrl);
            intent.putExtra("movie_description", description);
            intent.putExtra("video_url", video);

            // ✅ Add the additional extras for compatibility
            intent.putExtra("movie_year", year);
            intent.putExtra("movie_duration", duration);
            intent.putExtra("movie_age", age);
            intent.putExtra("movie_quality", quality);
            intent.putExtra("chosen_category", category);

            context.startActivity(intent);
        });
    }


    public void updateMovies(List<SearchResult> newMovies) {
        searchResults = newMovies;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView moviePoster;
        TextView movieTitle;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            moviePoster = itemView.findViewById(R.id.movie_thumbnail);
            movieTitle = itemView.findViewById(R.id.movie_title);
        }
    }
}
