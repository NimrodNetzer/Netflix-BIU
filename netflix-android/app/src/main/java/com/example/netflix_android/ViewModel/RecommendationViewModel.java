package com.example.netflix_android.ViewModel;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.netflix_android.Entities.Movie;
import com.example.netflix_android.Repository.RecommendationRepository;
import java.util.List;

public class RecommendationViewModel extends ViewModel {
    private final RecommendationRepository recommendationRepository;

    public RecommendationViewModel(RecommendationRepository repository) {
        this.recommendationRepository = repository;
    }

    public LiveData<List<Movie>> getRecommendedMovies(String movieId) {
        return recommendationRepository.getRecommendedMovies(movieId);
    }

    public LiveData<Boolean> addRecommendation(String movieId) {
        return recommendationRepository.addRecommendation(movieId);
    }
}
