package com.example.netflix_android.Api;

import com.example.netflix_android.Entities.Movie;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.Header;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public interface MovieApi {


    // Fetch a movie by its ID
    @GET("/api/movies/{id}")
    Call<Movie> getMovieById(@Path("id") String movieId);

    // Update an existing movie


    // Delete a movie
    @DELETE("/api/movies/{id}")
    Call<Void> deleteMovie(@Path("id") String movieId);

    @Multipart
    @POST("/api/movies")
    Call<Void> createMovie(
            @Part("data") RequestBody movieData, // JSON Movie Data
            @Part MultipartBody.Part image,     // Image File
            @Part MultipartBody.Part video,     // Video File
            @Header("Authorization") String token // Bearer Token for Auth
    );

    @Multipart
    @PUT("/api/movies/{id}")
    Call<Void> updateMovie(
            @Path("id") String movieId, // Movie ID in the URL
            @Part("data") RequestBody movieData, // JSON Movie Data
            @Part MultipartBody.Part image,     // Image File
            @Part MultipartBody.Part video,     // Video File
            @Header("Authorization") String token // Bearer Token for Auth
    );

}