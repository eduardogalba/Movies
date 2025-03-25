package com.example.movies.beans;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

public class BusquedaPelicula {
    @SerializedName("#TITLE")
    private String titulo;

    @SerializedName("#YEAR")
    private String year;

    @SerializedName("#ACTORS")
    private String actors;

    @SerializedName("#IMG_POSTER")
    private String urlPoster;

    @SerializedName("#IMDB_ID")
    private String imdb_code;

    private Bitmap imagen;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getUrlPoster() {
        return urlPoster;
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }

    public String getImdb_code() {
        return imdb_code;
    }

    public void setImdb_code(String imdb_code) {
        this.imdb_code = imdb_code;
    }
}
