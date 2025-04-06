package com.example.netflix_android.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.netflix_android.Entities.Category;
import com.example.netflix_android.R;
import com.example.netflix_android.ViewModel.CategoryViewModel;
import com.example.netflix_android.ViewModel.CategoryViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.UUID;

public class AddCategoryActivity extends AppCompatActivity {
    private EditText categoryNameInput;
    private Button buttonSubmitCategory, buttonCancel, buttonPromoted;
    private FloatingActionButton buttonDelete; // ✅ Floating delete button
    private boolean isPromoted = false;
    private CategoryViewModel categoryViewModel;
    private TextView titleText;
    private String existingCategoryId = null; // Holds ID for update mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        // Initialize UI components
        categoryNameInput = findViewById(R.id.category_name_input);
        buttonSubmitCategory = findViewById(R.id.button_submit_category);
        buttonCancel = findViewById(R.id.button_cancel);
        buttonPromoted = findViewById(R.id.button_promoted);
        buttonDelete = findViewById(R.id.button_delete); // ✅ Floating delete button
        titleText = findViewById(R.id.category_title);

        // ✅ Use ViewModelFactory
        CategoryViewModelFactory factory = new CategoryViewModelFactory(getApplicationContext());
        categoryViewModel = new ViewModelProvider(this, factory).get(CategoryViewModel.class);

        // ✅ Check if an existing category is being edited
        Intent intent = getIntent();
        if (intent.hasExtra("category_id")) {
            existingCategoryId = intent.getStringExtra("category_id"); // Retrieve ID
            categoryNameInput.setText(intent.getStringExtra("category_name"));
            isPromoted = intent.getBooleanExtra("category_promoted", false);
            buttonPromoted.setText(isPromoted ? "Promoted" : "Not Promoted");

            // ✅ Update UI for Edit Mode
            titleText.setText("Update Category");
            buttonSubmitCategory.setText("Update Category");
            buttonDelete.setVisibility(View.VISIBLE); // Show floating delete button
        } else {
            // ✅ Create Mode (Default)
            buttonSubmitCategory.setText("Add Category");
            buttonDelete.setVisibility(View.GONE); // Hide delete button
        }

        // ✅ Handle Promoted Toggle
        buttonPromoted.setOnClickListener(v -> {
            isPromoted = !isPromoted;
            buttonPromoted.setText(isPromoted ? "Promoted" : "Not Promoted");
        });

        // ✅ Handle Submit (Create/Update)
        buttonSubmitCategory.setOnClickListener(v -> {
            String categoryName = categoryNameInput.getText().toString().trim();
            if (categoryName.isEmpty()) {
                Toast.makeText(this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (existingCategoryId != null) {
                // ✅ Update existing category
                Category updatedCategory = new Category(existingCategoryId, categoryName, isPromoted);
                categoryViewModel.updateCategory(updatedCategory);
                Toast.makeText(this, "Category updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                // ✅ Create new category
                String tempId = UUID.randomUUID().toString();
                Category newCategory = new Category(tempId, categoryName, isPromoted);
                categoryViewModel.addCategory(newCategory);
                Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        });

        // ✅ Handle Floating Delete (Only in Update Mode)
        buttonDelete.setOnClickListener(v -> {
            if (existingCategoryId != null) {
                categoryViewModel.deleteCategory(existingCategoryId);
                Toast.makeText(this, "Category deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // ✅ Handle Cancel
        buttonCancel.setOnClickListener(v -> finish());
    }
}
