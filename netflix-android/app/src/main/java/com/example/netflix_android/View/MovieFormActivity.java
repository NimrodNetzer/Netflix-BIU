package com.example.netflix_android.View;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.netflix_android.Entities.CastMember;
import com.example.netflix_android.Entities.Category;
import com.example.netflix_android.Entities.Movie;
import com.example.netflix_android.R;
import com.example.netflix_android.Utils.SessionManager;
import com.example.netflix_android.ViewModel.CategoryViewModel;
import com.example.netflix_android.ViewModel.CategoryViewModelFactory;
import com.example.netflix_android.ViewModel.MovieViewModel;
import com.example.netflix_android.ViewModel.MovieViewModelFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MovieFormActivity extends AppCompatActivity {
    private static final String TAG = "MovieFormActivity";

    private EditText editMovieName, editMovieDescription, editMovieAge, editMovieHours, editMovieMinutes, editMovieReleaseDate;
    private Spinner spinnerQuality, spinnerLanguage;
    private LinearLayout categoryCheckboxContainer, castContainer;
    private Button buttonAddCast, buttonUploadPicture, buttonUploadVideo, buttonSubmitMovie;
    private ImageView previewImage;
    private VideoView previewVideo;

    private MovieViewModel movieViewModel;
    private Uri selectedPictureUri, selectedVideoUri;
    private boolean isUpdate = false;
    private String movieId;
    private List<String> selectedCategories = new ArrayList<>();
    private List<CastMember> castList = new ArrayList<>();
    private View progressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_form);
        previewImage = findViewById(R.id.preview_image);
        previewVideo = findViewById(R.id.preview_video);
        progressLoading = findViewById(R.id.progress_loading);
        // Initialize UI elements
        editMovieName = findViewById(R.id.edit_movie_name);
        editMovieDescription = findViewById(R.id.edit_movie_description);
        editMovieAge = findViewById(R.id.edit_movie_age);
        editMovieHours = findViewById(R.id.edit_movie_hours);
        editMovieMinutes = findViewById(R.id.edit_movie_minutes);
        editMovieReleaseDate = findViewById(R.id.edit_movie_release_date);
        spinnerQuality = findViewById(R.id.spinner_quality);
        spinnerLanguage = findViewById(R.id.spinner_language);
        categoryCheckboxContainer = findViewById(R.id.category_checkbox_container);
        castContainer = findViewById(R.id.cast_container);
        buttonAddCast = findViewById(R.id.add_cast_button);
        buttonUploadPicture = findViewById(R.id.button_upload_picture);
        buttonUploadVideo = findViewById(R.id.button_upload_video);
        buttonSubmitMovie = findViewById(R.id.button_submit_movie);
        //previewImage = findViewById(R.id.preview_image);
        //previewVideo = findViewById(R.id.preview_video);

        movieViewModel = new ViewModelProvider(this, new MovieViewModelFactory(this)).get(MovieViewModel.class);

        // Check if updating existing movie
        Intent intent = getIntent();
        if (intent.hasExtra("movie_id")) {
            isUpdate = true;
            movieId = intent.getStringExtra("movie_id");
            TextView textTitle = findViewById(R.id.text_title);
            textTitle.setText("Update Movie"); // Set title to Update Movie
            loadMovieDetails(movieId); // Load movie details
        } else {
            // Optional: set title explicitly in create mode if needed
            TextView textTitle = findViewById(R.id.text_title);
            textTitle.setText("Create Movie");
        }

        setupDropdowns();
        loadCategories();
        setupDatePicker();
        setupButtons();
    }

    private void setupDropdowns() {
        ArrayAdapter<CharSequence> qualityAdapter = ArrayAdapter.createFromResource(this,
                R.array.quality_options, android.R.layout.simple_spinner_item);
        qualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuality.setAdapter(qualityAdapter);

        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this,
                R.array.language_options, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(languageAdapter);
    }

    private void setupDatePicker() {
        editMovieReleaseDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(MovieFormActivity.this, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                editMovieReleaseDate.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void loadCategories() {
        CategoryViewModel categoryViewModel = new ViewModelProvider(this, new CategoryViewModelFactory(this)).get(CategoryViewModel.class);

        categoryViewModel.getCategories().observe(this, categories -> {
            if (categories != null && !categories.isEmpty()) {
                categoryCheckboxContainer.removeAllViews(); // Clear previous checkboxes

                for (Category category : categories) {
                    CheckBox checkBox = new CheckBox(this);
                    checkBox.setText(category.getName());
                    checkBox.setTextColor(getResources().getColor(android.R.color.white));
                    checkBox.setTag(category.getId()); // Store category ID

                    // Pre-select checkboxes for existing movie categories
                    if (selectedCategories.contains(category.getId())) {
                        checkBox.setChecked(true);
                    }

                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        String categoryId = (String) buttonView.getTag();
                        if (isChecked) selectedCategories.add(categoryId);
                        else selectedCategories.remove(categoryId);
                    });

                    categoryCheckboxContainer.addView(checkBox);
                }
            } else {
                Log.e(TAG, "❌ No categories found.");
            }
        });
    }

    private void setupButtons() {
        buttonAddCast.setOnClickListener(v -> addCastMember());
        buttonUploadPicture.setOnClickListener(v -> selectFile(true));
        buttonUploadVideo.setOnClickListener(v -> selectFile(false));
        buttonSubmitMovie.setOnClickListener(v -> submitMovie());
    }

    private void addCastMember() {
        View castView = getLayoutInflater().inflate(R.layout.item_cast_member, castContainer, false);
        EditText editCastName = castView.findViewById(R.id.edit_cast_name);
        EditText editCastRole = castView.findViewById(R.id.edit_cast_role);
        Button removeCastButton = castView.findViewById(R.id.button_remove_cast);

        removeCastButton.setOnClickListener(v -> castContainer.removeView(castView));
        castContainer.addView(castView);
    }

    private void selectFile(boolean isPicture) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(isPicture ? "image/*" : "video/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), isPicture ? 1 : 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            try {
                if (requestCode == 1) {
                    selectedPictureUri = fileUri;
                    previewImage.setImageURI(fileUri);
                    previewImage.setVisibility(View.VISIBLE); // Show image preview
                } else if (requestCode == 2) {
                    selectedVideoUri = fileUri;
                    previewVideo.setVideoURI(fileUri);
                    previewVideo.setVisibility(View.VISIBLE); // Show video preview

                    previewVideo.setOnPreparedListener(mp -> {
                        mp.setVolume(0f, 0f); // Optional: mute it just in case
                        mp.seekTo(1);         // Seek a bit to ensure preview shows
                        previewVideo.pause(); // Pause so it doesn't play
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ Error processing file: " + e.getMessage());
            }
        }
    }


    private void loadMovieDetails(String movieId) {
        movieViewModel.getMovieById(movieId).observe(this, movie -> {
            if (movie != null) {
                editMovieName.setText(movie.getName());
                editMovieDescription.setText(movie.getDescription());
                editMovieAge.setText(String.valueOf(movie.getAge()));

                // Parse duration
                String[] timeParts = movie.getTime().split("h |m");
                if (timeParts.length > 1) {
                    editMovieHours.setText(timeParts[0]);
                    editMovieMinutes.setText(timeParts[1]);
                }

                spinnerQuality.setSelection(
                        ((ArrayAdapter<String>) spinnerQuality.getAdapter())
                                .getPosition(movie.getQuality())
                );

                // ✅ Safe access to language
                Map<String, String> props = movie.getProperties();
                if (props != null && props.get("language") != null) {
                    spinnerLanguage.setSelection(
                            ((ArrayAdapter<String>) spinnerLanguage.getAdapter())
                                    .getPosition(props.get("language"))
                    );
                } else {
                    Log.w("MovieFormActivity", "⚠️ Language property is missing or null");
                    // Optionally set a default value:
                    spinnerLanguage.setSelection(0); // or leave it unselected
                }
            }
        });
    }


    private File getFileFromUri(Uri uri, String type) throws IOException {
        String extension = type.equals("image") ? ".png" : ".mp4";
        File file = new File(getCacheDir(), "temp_file_" + UUID.randomUUID() + extension);
        InputStream inputStream = getContentResolver().openInputStream(uri);
        OutputStream outputStream = new FileOutputStream(file);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        inputStream.close();
        outputStream.close();
        return file;
    }

    private void submitMovie() {
        String name = editMovieName.getText().toString().trim();
        String description = editMovieDescription.getText().toString().trim();
        String ageString = editMovieAge.getText().toString().trim();
        String hoursString = editMovieHours.getText().toString().trim();
        String minutesString = editMovieMinutes.getText().toString().trim();
        String time = hoursString + "h " + minutesString + "m";
        String releaseDate = editMovieReleaseDate.getText().toString().trim();
        String quality = spinnerQuality.getSelectedItem().toString();
        String language = spinnerLanguage.getSelectedItem().toString();
        String author = "a"; // Update as needed
        if (releaseDate.isEmpty()) {
            Toast.makeText(this, "Please fill Release Date.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Validate required text fields
        if (name.isEmpty() || description.isEmpty() || ageString.isEmpty() ||
                hoursString.isEmpty() || minutesString.isEmpty() || releaseDate.isEmpty() || quality.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate that a picture and video have been selected
        if (selectedPictureUri == null || selectedVideoUri == null) {
            Toast.makeText(this, "Please select both a picture and a video.", Toast.LENGTH_SHORT).show();
            return;
        }

        // **✅ Convert selected categories into Category objects**
        List<Category> categories = new ArrayList<>();
        for (String categoryId : selectedCategories) {
            categories.add(new Category(categoryId, "", false)); // Only passing ID
        }

        // **✅ Prepare cast list**
        List<CastMember> castList = new ArrayList<>();
        for (int i = 0; i < castContainer.getChildCount(); i++) {
            View castView = castContainer.getChildAt(i);
            EditText editCastName = castView.findViewById(R.id.edit_cast_name);
            EditText editCastRole = castView.findViewById(R.id.edit_cast_role);

            String castName = editCastName.getText().toString().trim();
            String castRole = editCastRole.getText().toString().trim();

            if (!castName.isEmpty() && !castRole.isEmpty()) {
                castList.add(new CastMember(castName, castRole));
            }
        }

        // **✅ Validate required fields**
        if (name.isEmpty() || description.isEmpty() || time.isEmpty() || releaseDate.isEmpty() || quality.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // **✅ Convert selected picture and video**
        File imageFile = null;
        File videoFile = null;
        try {
            if (selectedPictureUri != null) {
                imageFile = getFileFromUri(selectedPictureUri, "image");
            }
            if (selectedVideoUri != null) {
                videoFile = getFileFromUri(selectedVideoUri, "video");
            }
        } catch (IOException e) {
            Log.e(TAG, "❌ Error processing file: " + e.getMessage());
            Toast.makeText(this, "File processing error.", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid age rating.", Toast.LENGTH_SHORT).show();
            return;
        }

        // **✅ Create movie object with selected categories**
        Movie movie = new Movie("", name, "", "", description, age, time, new Date(), quality, categories, castList, null, author);
        progressLoading.setVisibility(View.VISIBLE);
        buttonSubmitMovie.setEnabled(false);

        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getToken();

        LiveData<Boolean> resultLiveData;
        if (isUpdate) {
            resultLiveData = movieViewModel.updateMovie(movieId, movie, imageFile, videoFile, token);
        } else {
            resultLiveData = movieViewModel.createMovie(movie, imageFile, videoFile, token);
        }
        resultLiveData.observe(this, success -> {
            // Always hide the loading indicator and re-enable the button
            progressLoading.setVisibility(View.GONE);
            buttonSubmitMovie.setEnabled(true);

            if (success) {
                String message = isUpdate ? "Movie updated successfully!" : "Movie created successfully!";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("movie_id", movieId);
                setResult(RESULT_OK, resultIntent);
                finish(); // Return to previous window
            } else {
                String message = isUpdate ? "Failed to update movie." : "Failed to create movie.";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
