package com.example.movies.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.movies.MainActivity;
import com.example.movies.R;
import com.example.movies.beans.Actor;

import java.util.List;

public class ActorAdapter extends BaseAdapter {

    private List<Actor> actores;
    private MainActivity context;

    public ActorAdapter (MainActivity context, List<Actor> items) {
        this.context = context;
        this.actores = items;
    }

    @Override
    public int getCount() {
        return actores.size();
    }

    @Override
    public Object getItem(int i) {
        return actores.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (view == null) {
            view = inflater.inflate(R.layout.layout_item_actor, viewGroup, false);
        }

        String actor = actores.get(i).getName();

        ((TextView) view.findViewById(R.id.actor_item_name)).setText(actor);

        return view;
    }
}
