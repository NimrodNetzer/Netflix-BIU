package com.example.netflix_android.Api;

import com.example.netflix_android.Entities.Category;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.DELETE;

public interface CategoryApi {
    @GET("/api/categories") // Matches Node.js route: GET /
    Call<List<Category>> getCategories();

    @GET("/api/categories/{id}") // Matches Node.js route: GET /:id
    Call<Category> getCategory(@Path("id") String id);

    @POST("/api/categories") // Matches Node.js route: POST /
    Call<Void> createCategory(@Body Category category);

    @PATCH("/api/categories/{id}") // Matches Node.js route: PATCH /:id
    Call<Void> updateCategory(@Path("id") String id, @Body Category category);

    @DELETE("/api/categories/{id}") // Matches Node.js route: DELETE /:id
    Call<Void> deleteCategory(@Path("id") String id);
}
