package com.example.netflix_android.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.netflix_android.Entities.Movie;
import com.example.netflix_android.R;
import java.util.List;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context context;
    private Map<String, List<Movie>> categorizedMovies;

    public CategoryAdapter(Context context, Map<String, List<Movie>> categorizedMovies) {
        this.context = context;
        this.categorizedMovies = categorizedMovies;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_row, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String categoryName = (String) categorizedMovies.keySet().toArray()[position];
        List<Movie> movies = categorizedMovies.get(categoryName);

        holder.categoryTitle.setText(categoryName);

        // ✅ Ensure `moviesRecyclerView` is initialized before setting LayoutManager
        if (holder.moviesRecyclerView != null) {
            holder.moviesRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.moviesRecyclerView.setAdapter(new MoviesAdapter(context, movies));
        }
    }

    @Override
    public int getItemCount() {
        return categorizedMovies.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;
        RecyclerView moviesRecyclerView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.category_title);
            moviesRecyclerView = itemView.findViewById(R.id.movies_recycler_view);
        }
    }
}
