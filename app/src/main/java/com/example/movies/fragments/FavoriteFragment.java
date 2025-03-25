package com.example.movies.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.movies.MainActivity;
import com.example.movies.R;
import com.example.movies.beans.BusquedaPelicula;
import com.example.movies.beans.Pelicula;
import com.example.movies.database.PeliculaDB;
import com.example.movies.tasks.MovieInflaterTask;
import com.example.movies.adapters.SearchAdapter;

import java.util.List;

public class FavoriteFragment extends Fragment {
    private List<BusquedaPelicula> busquedas;
    private final MainActivity main;
    private boolean onDelete = false;

    public FavoriteFragment (MainActivity m) {
        this.main = m;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        actualizarPeliculasEnLista();
        ImageButton deleteButton = requireView().findViewById(R.id.fav_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDelete = !onDelete;
                if (onDelete) {
                    deleteButton.setColorFilter(Color.argb(200, 255, 0, 0));
                } else {
                    deleteButton.setColorFilter(Color.argb(200, 0, 0, 0));
                }
            }
        });

        ListView lv = requireView().findViewById(R.id.frag_fav_lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Quiero cargar la pelicula de mi base de datos
                if (onDelete) {
                    PeliculaDB.eliminarPelicula(getContext(), busquedas.get(i).getImdb_code());
                    actualizarPeliculasEnLista();
                } else {
                    Pelicula selected = PeliculaDB.cargarPelicula(getContext(), busquedas.get(i).getImdb_code());
                    MovieInflaterTask task = new MovieInflaterTask(main, selected, main.findViewById(R.id.fav_frag_layout));
                    task.execute();
                }

            }
        });

    }

    public void actualizarPeliculasEnLista () {
        busquedas = PeliculaDB.cargarBusquedas(getContext());
        ListView lv = requireView().findViewById(R.id.frag_fav_lv);
        SearchAdapter searchAdapter = new SearchAdapter(requireContext(), busquedas);
        lv.setAdapter(searchAdapter);
    }
}