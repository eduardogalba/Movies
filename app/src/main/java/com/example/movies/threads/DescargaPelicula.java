package com.example.movies.threads;

import android.widget.Toast;

import com.example.movies.MainActivity;
import com.example.movies.R;
import com.example.movies.beans.BusquedaPelicula;
import com.example.movies.beans.MovieApiResponse;
import com.example.movies.beans.Pelicula;
import com.example.movies.tasks.MovieInflaterTask;
import com.example.movies.utils.NetUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DescargaPelicula implements Runnable {
    private MainActivity main;
    private BusquedaPelicula chosen;

    public DescargaPelicula (MainActivity m, BusquedaPelicula selected) {
        this.main = m;
        this.chosen = selected;
    }



    @Override
    public void run() {
        main.runOnUiThread(() -> main.startDownload());

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("dd/MM/yyyy hh:mm a");
        Gson gson = gsonBuilder.create();
        String response = null;
        try {
            response = NetUtils.getURLText("https://imdb.iamidiotareyoutoo.com/search?tt=" + chosen.getImdb_code());
        } catch (Exception e) {
            e.printStackTrace();
        }
        MovieApiResponse apiResponse = gson.fromJson(response, MovieApiResponse.class);

        if (apiResponse != null && apiResponse.isOk()) {
            Pelicula pel = apiResponse.getPelicula();
            pel.setImdbCode(chosen.getImdb_code());
            pel.setMiniature(chosen.getImagen());
            try {
                pel.setPoster(NetUtils.getURLImage(pel.getUrlPoster()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            main.runOnUiThread(() -> {
                MovieInflaterTask task = new MovieInflaterTask(main, pel, main.findViewById(R.id.dynamic_container));
                task.execute();
            });
        } else {
            Toast.makeText(main, "Ooops server is not responding.", Toast.LENGTH_SHORT).show();
        }
    }
}
