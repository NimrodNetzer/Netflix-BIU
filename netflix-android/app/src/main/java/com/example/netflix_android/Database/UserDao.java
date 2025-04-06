package com.example.netflix_android.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.netflix_android.Entities.User;

@Dao
public interface UserDao {

    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    LiveData<User> getUserById(int id); // Use LiveData for async updates

    @Query("DELETE FROM users")
    void deleteAllUsers(); // Useful for logging out
}
