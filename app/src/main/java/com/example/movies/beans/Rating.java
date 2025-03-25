package com.example.movies.beans;

import com.google.gson.annotations.SerializedName;

public class Rating {

    @SerializedName("ratingValue")
    private final String rate;

    public Rating (String meanRate) {
        this.rate = meanRate;
    }


    public String getRate() {
        return rate;
    }

}
