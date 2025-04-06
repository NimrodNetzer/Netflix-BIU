package com.example.netflix_android.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.auth0.android.jwt.JWT;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ADMIN = "admin";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // ✅ Save token and extract admin status
    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);

        // Decode the token and extract "admin" value
        try {
            JWT jwt = new JWT(token);
            boolean isAdmin = jwt.getClaim("admin").asBoolean() != null && jwt.getClaim("admin").asBoolean();
            editor.putBoolean(KEY_ADMIN, isAdmin);
        } catch (Exception e) {
            e.printStackTrace();
            editor.putBoolean(KEY_ADMIN, false); // Default to false if decoding fails
        }

        editor.apply();
    }

    // ✅ Retrieve token
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    // ✅ Retrieve admin status
    public boolean isAdmin() {
        return sharedPreferences.getBoolean(KEY_ADMIN, false);
    }

    // ✅ Clear session (useful for logout)
    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
