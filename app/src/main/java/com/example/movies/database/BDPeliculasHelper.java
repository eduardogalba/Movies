package com.example.movies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDPeliculasHelper extends SQLiteOpenHelper {

    public BDPeliculasHelper (Context context) {
        super(context, "peliculas_favoritas", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // SQL para crear la BBDD
        sqLiteDatabase.execSQL("CREATE TABLE searchs (" +
                "   search_code VARCHAR(15) PRIMARY KEY, " +
                "   title VARCHAR(255) NOT NULL, " +
                "   year TEXT," +
                "   actors TEXT," +
                "   miniature_uri TEXT);");

        sqLiteDatabase.execSQL("CREATE TABLE favourites (" +
                "    movie_code VARCHAR(15) PRIMARY KEY," +
                "    title VARCHAR(255) NOT NULL," +
                "    duration VARCHAR(8)," +
                "    year VARCHAR(12)," +
                "    rating DECIMAL(3, 1)," +
                "    description TEXT, " +
                "    poster_uri TEXT, " +
                "    miniature_uri TEXT, " +
                "    genre TEXT," +
                "    actors TEXT," +
                "    directors TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int versionActual, int nuevaVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS searchs;");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS favourites;");

        onCreate(sqLiteDatabase);
    }

}
