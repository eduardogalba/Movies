package com.example.movies.tasks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.movies.MainActivity;
import com.example.movies.R;
import com.example.movies.adapters.ActorAdapter;
import com.example.movies.beans.Actor;
import com.example.movies.beans.Pelicula;
import com.example.movies.database.PeliculaDB;
import com.example.movies.utils.BitmapManager;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MovieInflaterTask  {
    private final MainActivity context;
    private final Pelicula pel;
    private final ViewGroup container;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final List<View> prevViews;

    public MovieInflaterTask(MainActivity context, Pelicula pel, ViewGroup container) {
        this.context = context;
        this.pel = pel;
        this.container = container;
        this.prevViews = new LinkedList<>();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void execute() {
        executorService.execute(() -> {
            // Background task: Inflate layout and prepare data
            View newLayout = prepareLayout();

            // Post the result to the main thread
            mainHandler.post(() -> {
                savedBackViews();
                setUpButtons(newLayout);
                container.removeAllViews();
                container.addView(newLayout);
            });
        });
    }

    private View prepareLayout() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View newLayout = inflater.inflate(R.layout.layout_item_pelicula, container, false);

        // Scale the image
        Bitmap scaledImage = Bitmap.createScaledBitmap(pel.getPoster(), 450, 650, true);

        // Set up UI elements
        ((TextView) newLayout.findViewById(R.id.pelicula_item_length)).setText(String.format("%s", (pel.getDuration() == null) ? "" : pel.getDuration()));
        ((TextView) newLayout.findViewById(R.id.pelicula_item_title)).setText(String.format("%s", pel.getName()));
        ((TextView) newLayout.findViewById(R.id.pelicula_item_rating)).setText(String.format("%s", (pel.getRating() == null || pel.getRating().getRate() == null) ? "" : pel.getRating().getRate()));
        ((TextView) newLayout.findViewById(R.id.pelicula_item_year)).setText(String.format("%s", pel.getYear()));
        ((TextView) newLayout.findViewById(R.id.pelicula_item_description)).setMovementMethod(new ScrollingMovementMethod());
        ((TextView) newLayout.findViewById(R.id.pelicula_item_description)).setText(String.format("%s", (pel.getPlot() == null) ? "" : pel.getPlot()));
        ((ImageView) newLayout.findViewById(R.id.pelicula_item_poster)).setImageBitmap(scaledImage);

        ((TextView) newLayout.findViewById(R.id.pelicula_item_genre)).setText(String.format("%s", pel.getGenreString()));

        List<Actor> notFoundList = new LinkedList<>();
        notFoundList.add(new Actor(""));



        ActorAdapter adapter = new ActorAdapter(context, (pel.getActors() == null) ? notFoundList : pel.getActors());
        ((ListView) newLayout.findViewById(R.id.pelicula_item_lv_cast)).setAdapter(adapter);

        adapter = new ActorAdapter(context, (pel.getDirectors() == null) ? notFoundList : pel.getDirectors());
        ((ListView) newLayout.findViewById(R.id.pelicula_item_lv_director)).setAdapter(adapter);


        return newLayout;
    }


    private void setUpButtons(View newLayout) {
        setUpBackButton(newLayout);
        setUpShareMovieButton(newLayout);
        setUpSharePosterButton(newLayout);
        setUpFavoriteButton(newLayout);
    }

    private void setUpBackButton (View layout) {
        layout.findViewById(R.id.pelicula_item_backbtn).setOnClickListener(view -> {
            restoreBackViews();
        });
    }

    private void setUpShareMovieButton (View layout) {
        layout.findViewById(R.id.pelicula_item_movie_shrebtn).setOnClickListener(view -> {
            Intent sendIntent = new Intent();

            String shareMovie = "Title: " + pel.getName() + "\n"
                    + "Length: " + pel.getDuration() + "\n"
                    + "Date: " + pel.getYear() + "\n"
                    + "Rating: " + pel.getRating().getRate() + "\n"
                    + "Plot: " + pel.getPlot() + "\n";

            sendIntent.setAction(Intent.ACTION_SEND)
                    .putExtra(Intent.EXTRA_TITLE, pel.getName())
                    .putExtra(Intent.EXTRA_TEXT, shareMovie)
                    .setType("text/plain");

            context.startActivity(Intent.createChooser(sendIntent, null));
        });
    }

    private void setUpSharePosterButton (View layout) {
        layout.findViewById(R.id.pelicula_item_img_shrebtn).setOnClickListener(view -> {
            // Share image logic can be added here
            Intent sendIntent = new Intent();
            Uri uriToImage = BitmapManager.shareImage(context, pel.getImdbCode(), true);

            sendIntent.setAction(Intent.ACTION_SEND)
                    .putExtra(Intent.EXTRA_TITLE, pel.getName())
                    .putExtra(Intent.EXTRA_STREAM, uriToImage)
                    .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .setType("image/jpeg");

            context.startActivity(Intent.createChooser(sendIntent, null));

        });
    }

    private void setUpFavoriteButton (View layout) {
        ImageButton favBtn = layout.findViewById(R.id.pelicula_item_fav_btn);
        boolean added = PeliculaDB.isFavourite(layout.getContext(), pel.getImdbCode());
        favBtn.setColorFilter(added ? Color.argb(200, 255, 0, 0) :
                Color.argb(255, 0, 0, 0));
        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PeliculaDB.isFavourite(layout.getContext(), pel.getImdbCode())) {
                    executorService.execute(() -> {
                        PeliculaDB.guardarNuevaPelicula(view.getContext(), pel);
                        mainHandler.post(() -> {
                            favBtn.setColorFilter(Color.argb(255, 255, 0, 0));
                        });
                    });


                } else {
                    executorService.execute(() -> {
                        PeliculaDB.eliminarPelicula(view.getContext(), pel.getImdbCode());
                        mainHandler.post(() -> {
                            favBtn.setColorFilter(Color.argb(255, 0, 0, 0));
                        });
                    });
                }
            }
        });
    }


    private void savedBackViews () {
        for (int i = 0; i < container.getChildCount(); i++) {
            prevViews.add(i, container.getChildAt(i));
        }
    }

    public void restoreBackViews () {
        container.removeAllViews();
        for (int i = 0; i < prevViews.size(); i++) {
            container.addView(prevViews.get(i));
        }
    }

}
