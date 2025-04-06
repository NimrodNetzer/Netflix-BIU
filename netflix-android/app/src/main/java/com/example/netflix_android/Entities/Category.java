package com.example.netflix_android.Entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey
    @NonNull
    @SerializedName("_id") // Matches MongoDB's ID field
    private String id;

    private String name;
    private boolean promoted;

    public Category(@NonNull String id, String name, boolean promoted) {
        this.id = id;
        this.name = name;
        this.promoted = promoted;
    }

    @NonNull
    public String getId() { return id; }
    public String getName() { return name; }
    public boolean isPromoted() { return promoted; }

    public void setId(@NonNull String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPromoted(boolean promoted) { this.promoted = promoted; }
}
