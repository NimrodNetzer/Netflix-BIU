package com.example.netflix_android.Repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.example.netflix_android.Api.CategoryApi;
import com.example.netflix_android.Database.AppDatabase;
import com.example.netflix_android.Database.CategoryDao;
import com.example.netflix_android.Entities.Category;
import com.example.netflix_android.Network.RetrofitClient;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {
    private static final String TAG = "CategoryRepository";
    private final CategoryDao categoryDao;
    private final CategoryApi categoryApi;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Context context;

    public CategoryRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.context = context;
        this.categoryDao = db.categoryDao();
        this.categoryApi = RetrofitClient.getRetrofitInstance(context).create(CategoryApi.class);
    }

    public LiveData<List<Category>> getCategories() {
        fetchCategoriesFromApi();
        return categoryDao.getAllCategories();
    }

    private void fetchCategoriesFromApi() {
        categoryApi.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executorService.execute(() -> {
                        categoryDao.deleteAll();
                        categoryDao.insertAll(response.body());
                    });
                    Log.d(TAG, "✅ Categories fetched successfully");
                } else {
                    logErrorResponse("fetch categories", response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                Log.e(TAG, "❌ Network error while fetching categories", t);
            }
        });
    }

    public void addCategory(Category category) {
        executorService.execute(() -> categoryDao.insertAll(Collections.singletonList(category)));

        categoryApi.createCategory(category).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    handler.post(() -> Toast.makeText(context, "✅ Category added successfully", Toast.LENGTH_SHORT).show());
                    fetchCategoriesFromApi(); // reload updated data from API
                } else {
                    logErrorResponse("add category", response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "❌ Network error while adding category", t);
                handler.post(() -> Toast.makeText(context, "❌ Failed to add category", Toast.LENGTH_SHORT).show());
            }
        });
    }


    public void updateCategory(Category category) {
        categoryApi.updateCategory(category.getId(), category).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    handler.post(() -> Toast.makeText(context, "✅ Category updated successfully", Toast.LENGTH_SHORT).show());
                    fetchCategoriesFromApi(); // Optional reload
                } else {
                    logErrorResponse("update category", response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "❌ Network error while updating category", t);
            }
        });
    }

    public void deleteCategory(String categoryId) {
        categoryApi.deleteCategory(categoryId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    handler.post(() -> Toast.makeText(context, "✅ Category deleted", Toast.LENGTH_SHORT).show());
                    fetchCategoriesFromApi(); // Trigger UI update
                } else {
                    logErrorResponse("delete category", response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "❌ Network error while deleting category", t);
                handler.post(() -> Toast.makeText(context, "❌ Failed to delete category", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void logErrorResponse(String action, Response<?> response) {
        Log.e(TAG, "❌ Failed to " + action + ". HTTP Code: " + response.code());
        try {
            if (response.errorBody() != null) {
                String error = response.errorBody().string();
                Log.e(TAG, "Response error: " + error);
                handler.post(() -> Toast.makeText(context, "❌ " + error, Toast.LENGTH_LONG).show());
            } else {
                Log.e(TAG, "❌ No error body returned.");
                handler.post(() -> Toast.makeText(context, "❌ Failed to " + action + ": No error details", Toast.LENGTH_SHORT).show());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading error body", e);
        }
    }
}
