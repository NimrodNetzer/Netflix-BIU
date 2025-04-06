package com.example.netflix_android.View;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.netflix_android.Adapters.SearchAdapter;
import com.example.netflix_android.Entities.SearchResult;
import com.example.netflix_android.R;
import com.example.netflix_android.ViewModel.SearchViewModel;
import com.example.netflix_android.ViewModel.SearchViewModelFactory;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private SearchViewModel searchViewModel;
    private RecyclerView searchResultsRecyclerView;
    private SearchAdapter searchResultsAdapter;
    private EditText searchInput;
    private ProgressBar loadingIndicator;
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private LiveData<List<SearchResult>> searchResultsLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "🔍 SearchActivity Started");

        // ✅ Initialize UI components
        searchResultsRecyclerView = findViewById(R.id.search_results_list);
        searchInput = findViewById(R.id.search_input);
        loadingIndicator = findViewById(R.id.loading_indicator);

        // Set GridLayoutManager to display multiple movies per row
        searchResultsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        searchResultsAdapter = new SearchAdapter(this, List.of());
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);

        // ✅ Initialize ViewModel using Factory
        SearchViewModelFactory factory = new SearchViewModelFactory(this);
        searchViewModel = new ViewModelProvider(this, factory).get(SearchViewModel.class);

        // ✅ Add TextWatcher with debounce to prevent excessive calls
        searchInput.addTextChangedListener(new TextWatcher() {
            private Runnable searchRunnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> {
                    String query = s.toString().trim();
                    if (!query.isEmpty()) {
                        searchMovies(query);
                    } else {
                        clearResults();
                    }
                };
                searchHandler.postDelayed(searchRunnable, 300); // Debounce: 300ms delay
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // ✅ Fetch search results dynamically
    private void searchMovies(String query) {
        Log.d(TAG, "🔍 Searching for: " + query);
        loadingIndicator.setVisibility(View.VISIBLE); // Show loading

        if (searchResultsLiveData != null) {
            searchResultsLiveData.removeObservers(this);
        }

        searchResultsLiveData = searchViewModel.searchMovies(query);
        searchResultsLiveData.observe(this, this::displaySearchResults);
    }

    // ✅ Display search results efficiently
    private void displaySearchResults(List<SearchResult> results) {
        loadingIndicator.setVisibility(View.GONE); // Hide loading
        if (results != null && !results.isEmpty()) {
            searchResultsAdapter.updateMovies(results);
            Log.d(TAG, "✅ Search results loaded: " + results.size() + " movies found.");
        } else {
            Log.d(TAG, "⚠️ No results found.");
            clearResults();
        }
    }

    // ✅ Clear search results when input is empty
    private void clearResults() {
        searchResultsAdapter.updateMovies(List.of());
    }
}
