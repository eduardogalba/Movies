package com.example.movies.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchApiResponse {
    @SerializedName("ok")
    private boolean ok;

    @SerializedName("description")
    private List<BusquedaPelicula> description;


    public boolean isOk() {
        return ok;
    }

    public List<BusquedaPelicula> getDescription() {
        return description;
    }

}
