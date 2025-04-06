package com.example.netflix_android.Entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.example.netflix_android.Database.Converters;

@Entity(tableName = "movies")
@TypeConverters({Converters.class}) // Required for storing lists and maps
public class Movie {

    @PrimaryKey
    @NonNull
    @SerializedName("_id") // Match MongoDB field
    private String id;

    private String name;
    private String picture;
    private String video;
    private String description;
    private int age;
    private String time;

    @SerializedName("releaseDate")
    private Date releaseDate;

    private String quality;

    @SerializedName("categories")
    private List<Category> categories; // Store full category objects

    @SerializedName("cast")
    private List<CastMember> cast;

    @SerializedName("properties")
    private Map<String, String> properties; // Store extra movie properties

    private String author;

    public Movie(@NonNull String id, String name, String picture, String video, String description, int age,
                 String time, Date releaseDate, String quality, List<Category> categories, List<CastMember> cast,
                 Map<String, String> properties, String author) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.video = video;
        this.description = description;
        this.age = age;
        this.time = time;
        this.releaseDate = releaseDate;
        this.quality = quality;
        this.categories = categories;
        this.cast = cast;
        this.properties = properties;
        this.author = author;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }

    public String getVideo() { return video; }
    public void setVideo(String video) { this.video = video; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public Date getReleaseDate() { return releaseDate; }
    public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }

    public String getQuality() { return quality; }
    public void setQuality(String quality) { this.quality = quality; }

    public List<Category> getCategories() { return categories; }
    public void setCategories(List<Category> categories) { this.categories = categories; }

    public List<CastMember> getCast() { return cast; }
    public void setCast(List<CastMember> cast) { this.cast = cast; }

    public Map<String, String> getProperties() { return properties; }
    public void setProperties(Map<String, String> properties) { this.properties = properties; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
}
