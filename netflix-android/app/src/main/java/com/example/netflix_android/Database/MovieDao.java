package com.example.netflix_android.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Delete;
import com.example.netflix_android.Entities.Movie;
import java.util.List;

@Dao
public interface MovieDao {

    // Insert a list of movies (replace existing ones)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovies(List<Movie> movies);

    // Insert a single movie (replace if exists)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie movie);

    // Get all movies from the local database
    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> getAllMovies();

    // Get a movie by its ID
    @Query("SELECT * FROM movies WHERE id = :movieId LIMIT 1")
    LiveData<Movie> getMovieById(String movieId);

    // Delete a specific movie
    @Delete
    void deleteMovie(Movie movie);

    // Delete all movies (useful for refreshing)
    @Query("DELETE FROM movies")
    void deleteAllMovies();
}
