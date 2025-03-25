package com.example.movies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.movies.R;
import com.example.movies.beans.BusquedaPelicula;

import java.util.List;

public class SearchAdapter extends BaseAdapter {

    private final List<BusquedaPelicula> busquedas;
    private final Context context;

    public SearchAdapter(Context context, List<BusquedaPelicula> items) {
        this.context = context;
        this.busquedas = items;
    }

    @Override
    public int getCount() {
        return busquedas.size();
    }

    @Override
    public BusquedaPelicula getItem(int i) {
        return busquedas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (view == null) {
            view = inflater.inflate(R.layout.layout_item_busqueda, viewGroup, false);
        }

        BusquedaPelicula busqueda = busquedas.get(i);
        ((TextView) view.findViewById(R.id.actor_item_name)).setText(String.format("%s", busqueda.getTitulo()));
        ((TextView) view.findViewById(R.id.busqueda_item_year)).setText(String.format("%s", busqueda.getYear()));
        ((TextView) view.findViewById(R.id.busqueda_item_actors)).setText(String.format("%s", busqueda.getActors()));
        ((ImageView) view.findViewById(R.id.busqueda_item_image)).setImageBitmap(busqueda.getImagen());
        return view;
    }
}
