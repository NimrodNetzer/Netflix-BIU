package com.example.netflix_android.Database;

import androidx.room.TypeConverter;
import com.example.netflix_android.Entities.CastMember;
import com.example.netflix_android.Entities.Category;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Converters {

    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromCategoryList(List<Category> categories) {
        return gson.toJson(categories);
    }

    @TypeConverter
    public static List<Category> toCategoryList(String categoriesJson) {
        Type listType = new TypeToken<List<Category>>() {}.getType();
        return gson.fromJson(categoriesJson, listType);
    }

    @TypeConverter
    public static String fromCastList(List<CastMember> cast) {
        return gson.toJson(cast);
    }

    @TypeConverter
    public static List<CastMember> toCastList(String castJson) {
        Type listType = new TypeToken<List<CastMember>>() {}.getType();
        return gson.fromJson(castJson, listType);
    }

    @TypeConverter
    public static String fromPropertiesMap(Map<String, String> properties) {
        return gson.toJson(properties);
    }

    @TypeConverter
    public static Map<String, String> toPropertiesMap(String propertiesJson) {
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        return gson.fromJson(propertiesJson, mapType);
    }

    @TypeConverter
    public static Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }
}
