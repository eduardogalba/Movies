package com.example.movies.beans;

import com.google.gson.annotations.SerializedName;

public class Actor {
    @SerializedName("name")
    private String name;

    public Actor (String nm) {
        this.name = nm;
    }

    public String getName() {
        return name;
    }
}
