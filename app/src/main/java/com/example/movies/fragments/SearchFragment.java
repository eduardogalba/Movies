package com.example.movies.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.movies.MainActivity;
import com.example.movies.R;
import com.example.movies.beans.BusquedaPelicula;
import com.example.movies.threads.DescargaBusqueda;
import com.example.movies.threads.DescargaPelicula;
import com.example.movies.adapters.SearchAdapter;

import java.util.List;

public class SearchFragment extends Fragment {

    private List<BusquedaPelicula> busquedas;
    private MainActivity main;


    public SearchFragment (MainActivity m) {
        this.main = m;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        ImageButton btnSearch = requireView().findViewById(R.id.frag_sear_img_btn);
        SearchFragment me = this;
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String busqueda = ((EditText) requireView().findViewById(R.id.frag_sear_txt)).getText().toString();
                Thread searHilo = new Thread(new DescargaBusqueda(main, me, busqueda));
                searHilo.start();

                try {
                    searHilo.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        ListView searchList = requireView().findViewById(R.id.frag_sear_lv);
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Thread desHilo = new Thread(new DescargaPelicula(main,busquedas.get(i)));
                desHilo.start();
                try {
                    desHilo.join();
                } catch (InterruptedException e) {
                    throw  new RuntimeException(e);
                }

            }
        });
    }

    public void startSearch () {
        Toast.makeText(requireContext(), "Buscando..", Toast.LENGTH_SHORT).show();
        ProgressBar pb = requireView().findViewById(R.id.frag_sear_pb);
        pb.setVisibility(View.VISIBLE);
        ImageButton btnSearch = requireView().findViewById(R.id.frag_sear_img_btn);
        btnSearch.setEnabled(false);
    }

    public void finishSearch (List<BusquedaPelicula> busquedas) {
        this.busquedas = busquedas;
        ListView listView = requireView().findViewById(R.id.frag_sear_lv);
        SearchAdapter searchAdapter = new SearchAdapter(requireContext(), busquedas);
        listView.setAdapter(searchAdapter);
        ProgressBar pb = requireView().findViewById(R.id.frag_sear_pb);
        pb.setVisibility(View.INVISIBLE);

        Toast.makeText(requireContext(), "Busqueda terminada..", Toast.LENGTH_SHORT).show();
    }




}
