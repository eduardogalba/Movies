package com.example.movies.threads;

import static java.lang.Thread.sleep;

import android.graphics.Bitmap;
import android.widget.Toast;

import com.example.movies.MainActivity;
import com.example.movies.beans.SearchApiResponse;
import com.example.movies.beans.BusquedaPelicula;
import com.example.movies.fragments.SearchFragment;
import com.example.movies.utils.NetUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

public class DescargaBusqueda implements Runnable {
    private MainActivity main;
    private String patron;
    private SearchFragment frag;

    public DescargaBusqueda (MainActivity m, SearchFragment frag, String busqueda) {
        this.main = m;
        this.patron = busqueda;
        this.frag = frag;
    }



    @Override
    public void run() {
        main.runOnUiThread(() -> frag.startSearch());

        try {
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("dd/MM/yyyy hh:mm a");
        Gson gson = gsonBuilder.create();
        String response = null;
        try {
            response = NetUtils.getURLText("https://imdb.iamidiotareyoutoo.com/search?q=" + patron);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SearchApiResponse apiResponse = gson.fromJson(response, SearchApiResponse.class);

        if (apiResponse.isOk()) {
            List<BusquedaPelicula> pls= apiResponse.getDescription();
            for (int i = 0; i < pls.size(); i++) {
                try {
                    if (pls.get(i).getUrlPoster() != null) {
                        Bitmap imagenBusqueda = NetUtils.getURLImage(pls.get(i).getUrlPoster());
                        pls.get(i).setImagen(imagenBusqueda);
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            main.runOnUiThread(() -> frag.finishSearch(pls));
        } else {
            Toast.makeText(main, "Ooops server is not responding.", Toast.LENGTH_SHORT).show();
        }
    }
}
