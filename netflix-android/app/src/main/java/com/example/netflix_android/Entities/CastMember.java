package com.example.netflix_android.Entities;

import com.google.gson.annotations.SerializedName;

public class CastMember {

    @SerializedName("name")
    private String name;

    @SerializedName("role")
    private String role;

    public CastMember(String name, String role) {
        this.name = name;
        this.role = role;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
