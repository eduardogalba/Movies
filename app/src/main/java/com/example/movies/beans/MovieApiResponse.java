package com.example.movies.beans;

import com.google.gson.annotations.SerializedName;

public class MovieApiResponse {
    @SerializedName("ok")
    private boolean ok;

    @SerializedName("short")
    private Pelicula pelicula;

    public boolean isOk() {
        return ok;
    }


    public Pelicula getPelicula() {
        return pelicula;
    }

}
