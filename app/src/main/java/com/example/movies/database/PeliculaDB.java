package com.example.movies.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.movies.beans.Actor;
import com.example.movies.beans.BusquedaPelicula;
import com.example.movies.beans.Pelicula;
import com.example.movies.beans.Rating;
import com.example.movies.utils.BitmapManager;
import com.example.movies.utils.SearchFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PeliculaDB {
    private static BDPeliculasHelper miDBHelper = null;

    // Patron SIngleton
    private static void initHelper (Context ctx) {
        if (miDBHelper == null) {
            miDBHelper = new BDPeliculasHelper(ctx);
        }
    }

    public static void guardarNuevaPelicula (Context ctx, Pelicula pel) {
        Uri miniature;
        Uri poster;
        initHelper(ctx);

        guardarNuevaBusqueda(ctx, SearchFactory.createSearch(pel));

        SQLiteDatabase conn = miDBHelper.getWritableDatabase();
        poster = BitmapManager.saveBitmap2Dir(ctx, pel.getPoster(), pel.getImdbCode(), true);
        miniature = BitmapManager.saveBitmap2Dir(ctx, pel.getMiniature(), pel.getImdbCode(), false);

        conn.execSQL("INSERT OR REPLACE INTO favourites(movie_code, title, duration, year," +
                        "rating, description, poster_uri, miniature_uri, genre, actors, directors) VALUES (?,?,?,?,?,?,?,?,?,?,?);",
                new Object[]{pel.getImdbCode(), pel.getName(), pel.getDuration(), pel.getYear(),
                        pel.getRating().getRate(), pel.getPlot(), poster.toString(), miniature.toString(), pel.getGenreString(), pel.getActorString(), pel.getDirectorString()});
        conn.close();
    }

    private static void guardarNuevaBusqueda (Context ctx, BusquedaPelicula bus) {
        Uri miniature;
        initHelper(ctx);
        SQLiteDatabase conn = miDBHelper.getWritableDatabase();
        miniature = BitmapManager.saveBitmap2Dir(ctx, bus.getImagen(), bus.getImdb_code(), false);

        conn.execSQL("INSERT OR REPLACE INTO searchs(search_code, title, year," +
                        "actors, miniature_uri) VALUES (?,?,?,?,?);",
                new Object[]{bus.getImdb_code(), bus.getTitulo(), bus.getYear(), bus.getActors(),
                        miniature.toString()});
        conn.close();
    }

    public static void eliminarPelicula (Context ctx, String movie_code) {
        initHelper(ctx);
        SQLiteDatabase conn = miDBHelper.getWritableDatabase();

        conn.execSQL("DELETE FROM favourites WHERE movie_code = ?;", new String[]{movie_code});
        conn.execSQL("DELETE FROM searchs WHERE search_code = ?;", new String[]{movie_code});
        conn.close();
    }

    public static List<BusquedaPelicula> cargarBusquedas (Context ctx) {
        initHelper(ctx);
        SQLiteDatabase db = miDBHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM searchs ORDER BY year DESC;", null);
        Uri miniature;
        List<BusquedaPelicula> busquedas = new LinkedList<>();

        while (c.moveToNext()) {
            BusquedaPelicula bus = new BusquedaPelicula();
            bus.setImdb_code(c.getString(0));
            bus.setTitulo(c.getString(1));
            bus.setYear(c.getString(2));
            bus.setActors(c.getString(3));
            miniature = Uri.parse(c.getString(4));
            bus.setImagen(BitmapManager.getBitmap(ctx, miniature.getLastPathSegment(), false));
            busquedas.add(bus);
        }

        c.close();

        return busquedas;
    }

    public static Pelicula cargarPelicula (Context ctx, String movie_code) {
        initHelper(ctx);
        SQLiteDatabase db = miDBHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM favourites WHERE movie_code = ?;", new String[]{movie_code});
        Pelicula pel = new Pelicula();
        Uri poster;
        Uri miniature;

        if (c.moveToNext()) {
            pel.setImdbCode(c.getString(0));
            pel.setName(c.getString(1));
            pel.setDuration(c.getString(2));
            pel.setYear(c.getString(3));
            pel.setRating(new Rating(c.getString(4)));
            pel.setPlot(c.getString(5));
            poster = Uri.parse(c.getString(6));
            pel.setPoster(BitmapManager.getBitmap(ctx, poster.getLastPathSegment(), true));
            miniature = Uri.parse(c.getString(7));
            pel.setMiniature(BitmapManager.getBitmap(ctx, miniature.getLastPathSegment(), false));

            List<String> genreList = Arrays.stream(c.getString(8).split(" ")).toList();
            pel.setGenre(genreList);

            String [] nombreActores = c.getString(9).split("\t");
            List<Actor> actores = new LinkedList<>();
            for (String nombre : nombreActores) {
                actores.add(new Actor(nombre));
            }
            pel.setActors(actores);

            String [] nombreDirectores = c.getString(10).split("\t");
            List<Actor> directores = new LinkedList<>();
            for (String nombre : nombreDirectores) {
                directores.add(new Actor(nombre));
            }
            pel.setDirectors(directores);

        }

        c.close();

        return pel;
    }

    public static boolean isFavourite (Context ctx, String movie_code) {
        initHelper(ctx);
        boolean found;
        SQLiteDatabase db = miDBHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM favourites WHERE movie_code = ?;", new String[]{movie_code});
        found = c.moveToNext();
        c.close();
        return found;
    }
}
