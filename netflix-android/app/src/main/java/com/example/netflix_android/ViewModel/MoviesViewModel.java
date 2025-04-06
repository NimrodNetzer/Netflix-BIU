package com.example.netflix_android.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.netflix_android.Entities.MoviesResults;
import com.example.netflix_android.Entities.Movie;
import com.example.netflix_android.Repository.MoviesRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoviesViewModel extends ViewModel {
    private final MoviesRepository moviesRepository;

    public MoviesViewModel(MoviesRepository moviesRepository) {
        this.moviesRepository = moviesRepository;
    }

    public LiveData<List<MoviesResults>> getMovies() {
        return moviesRepository.getMovies();
    }

    // Convert API response into a map of category -> movies list
    public Map<String, List<Movie>> getMoviesGroupedByCategory(List<MoviesResults> moviesResultsList) {
        Map<String, List<Movie>> categorizedMovies = new HashMap<>();
        for (MoviesResults moviesResults : moviesResultsList) {
            categorizedMovies.put(moviesResults.getCategoryName(), moviesResults.getMovies());
        }
        return categorizedMovies;
    }
}
