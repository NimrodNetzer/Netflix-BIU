package com.example.netflix_android.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.netflix_android.Entities.Category;
import com.example.netflix_android.Repository.CategoryRepository;
import java.util.List;

public class CategoryViewModel extends ViewModel {
    private final CategoryRepository categoryRepository;
    private final LiveData<List<Category>> categories;

    public CategoryViewModel(CategoryRepository repository) {
        this.categoryRepository = repository;
        this.categories = repository.getCategories();
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public void addCategory(Category category) {
        categoryRepository.addCategory(category);
    }

    public void updateCategory(Category category) {
        categoryRepository.updateCategory(category);
    }

    public void deleteCategory(String categoryId) {
        categoryRepository.deleteCategory(categoryId);
    }
}
