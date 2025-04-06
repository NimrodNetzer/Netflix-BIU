package com.example.netflix_android.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String email;
    private String password;
    private String nickname;
    private String picture;
    private String token; // Store login token for future API calls

    public User(String email, String password, String nickname, String picture, String token) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.picture = picture;
        this.token = token;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getNickname() { return nickname; }
    public String getPicture() { return picture; }
    public String getToken() { return token; }

    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setPicture(String picture) { this.picture = picture; }
    public void setToken(String token) { this.token = token; }
}
