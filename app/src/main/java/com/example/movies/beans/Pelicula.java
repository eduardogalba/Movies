package com.example.movies.beans;

import android.graphics.Bitmap;
import java.util.Scanner;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pelicula {
    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String plot;

    @SerializedName("image")
    private String urlPoster;

    @SerializedName("datePublished")
    private String year;

    @SerializedName("duration")
    private String duration;

    @SerializedName("aggregateRating")
    private Rating rating;

    @SerializedName("genre")
    private List<String> genre;

    private Bitmap poster;
    private Bitmap miniature;

    private String imdbCode;

    @SerializedName("actor")
    private List<Actor> actors;

    @SerializedName("director")
    private List<Actor> directors;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getUrlPoster() {
        return urlPoster;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDuration() {
        if (duration == null) {
            return duration;
        }

        Pattern pattern = Pattern.compile("\\d*\\smin");
        Matcher matcher = pattern.matcher(duration);

        if (matcher.find()) {
            return duration;
        }

        Scanner scanner = new Scanner(duration).useDelimiter("[^0-9]+");
        int hours = scanner.nextInt();
        int minutes = scanner.nextInt();
        minutes = hours*60 + minutes;
        return minutes + " min";
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public Bitmap getPoster() {
        return poster;
    }

    public void setPoster(Bitmap poster) {
        this.poster = poster;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public String getImdbCode() {
        return imdbCode;
    }

    public void setImdbCode(String imdbCode) {
        this.imdbCode = imdbCode;
    }

    public Bitmap getMiniature() {
        return miniature;
    }

    public void setMiniature(Bitmap miniature) {
        this.miniature = miniature;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public String getActorString () {
        StringBuilder actorString = new StringBuilder();
        for (int i = 0; actors != null && i < actors.size(); i++) {
            actorString.append(actors.get(i).getName());
            actorString.append("\t");
        }

        return actorString.toString();
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public List<Actor> getDirectors() {
        return directors;
    }

    public String getDirectorString () {
        StringBuilder directorString = new StringBuilder();
        for (int i = 0; directors != null && i < directors.size(); i++) {
            directorString.append(directors.get(i).getName());
            directorString.append("\t");
        }

        return directorString.toString();
    }

    public String getGenreString () {
        StringBuilder genreString = new StringBuilder();
        for (int i = 0; genre != null && i < genre.size(); i++) {
            genreString.append(genre.get(i));
            genreString.append("\n");
        }

        return genreString.toString();
    }

    public void setDirectors(List<Actor> directors) {
        this.directors = directors;
    }
}
