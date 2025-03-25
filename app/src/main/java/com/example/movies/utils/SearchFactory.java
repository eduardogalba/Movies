package com.example.movies.utils;

import com.example.movies.beans.BusquedaPelicula;
import com.example.movies.beans.Pelicula;

public class SearchFactory {
    public static BusquedaPelicula createSearch (Pelicula pel) {
        BusquedaPelicula bus = new BusquedaPelicula();
        bus.setTitulo(pel.getName());
        bus.setImagen(pel.getMiniature());
        StringBuilder busActors = new StringBuilder();
        String[] actors = pel.getActorString().split("\t");
        for (int i = 0; i < 1; i++) {
            busActors.append(actors[i]);
            busActors.append(", ");
        }
        bus.setActors(busActors.toString());
        bus.setYear(pel.getYear());
        bus.setImdb_code(pel.getImdbCode());

        return bus;
    }
}
