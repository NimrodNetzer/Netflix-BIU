package com.example.netflix_android.Database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.netflix_android.Entities.User;
import com.example.netflix_android.Entities.Category;
import com.example.netflix_android.Entities.Movie;

@Database(entities = {User.class, Category.class, Movie.class}, version = 4, exportSchema = false)
@TypeConverters(Converters.class) // Register TypeConverters
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract MovieDao movieDao(); // Add Movie DAO

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "netflix_clone_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
