package com.example.netflix_android.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import com.example.netflix_android.Entities.Category;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Category> categories);
    @Query("SELECT * FROM categories")
    LiveData<List<Category>> getAllCategories();
    //@Query("DELETE FROM categories WHERE id = :categoryId")
    //void deleteCategory(String categoryId);

    @Query("DELETE FROM categories")
    void deleteAll();
}
