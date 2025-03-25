package com.example.movies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.movies.fragments.FavoriteFragment;
import com.example.movies.fragments.NotificationsFragment;
import com.example.movies.fragments.SearchFragment;
import com.example.movies.utils.Scheduler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity {

    private final SearchFragment searchFragment = new SearchFragment(this);
    private final NotificationsFragment notificationsFragment = new NotificationsFragment();
    private final FavoriteFragment favoriteFragment = new FavoriteFragment(this);
    private boolean granted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        askForPermissionsGrant();


        BottomNavigationView bottomNavView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, favoriteFragment).commit();

        bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.search) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, searchFragment)
                            .addToBackStack(null)
                            .commit();
                    return true;
                } else if (item.getItemId() == R.id.favorites) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, favoriteFragment)
                            .addToBackStack(null)
                            .commit();
                    return true;
                } else if (item.getItemId() == R.id.notifications) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, notificationsFragment)
                            .addToBackStack(null)
                            .commit();
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (granted) {
            Scheduler.scheduleJob(this, 0, false);
            Scheduler.scheduleWork(this, 99);
        }
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("notifCode")) {
            sendNotificationToFragment(intent);
        }
    }

    private void askForPermissionsGrant(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 35) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        101
                );
            }
        } else {
            granted = true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            granted = true;
        }
    }


    @Override
    public void onResume () {
        super.onResume();

        // Comunicacion asincrona: Evitando que Android me suspenda la actividad por trabajar
        // en background
        SharedPreferences prefs = getSharedPreferences("Notifications", Context.MODE_PRIVATE);

        try {
            //Obtener las notificaciones atrasadas del fichero JSON
            JSONArray notifArray = new JSONArray(prefs.getString("pending_notifs", "[]"));
            Intent seeLaterIntent = new Intent("SEE_LATER");
            for (int i = 0; i < notifArray.length(); i++) {
                JSONObject notifJSON = notifArray.getJSONObject(i);

                // Preparar el intent con la notificacion atrasada
                seeLaterIntent.putExtra("notifCode", notifJSON.getString("notifCode"));
                seeLaterIntent.putExtra("title", notifJSON.getString("title"));
                seeLaterIntent.putExtra("msg", notifJSON.getString("msg"));
                seeLaterIntent.putExtra("imageUri", notifJSON.getInt("imageUri"));
                seeLaterIntent.putExtra("see_later", true);

                //Enviarlas al Fragmento de Notificaciones
                sendNotificationToFragment(seeLaterIntent);
            }

            // Eliminar notificaciones atrasadas
            prefs.edit().remove("pending_notifs").apply();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void startDownload () {
        Toast.makeText(this, "comienza descarga..", Toast.LENGTH_SHORT).show();
    }


    public void sendNotificationToFragment (Intent intent) {

            Bundle packet = new Bundle();
            packet.putString("notifCode", intent.getStringExtra("notifCode"));
            packet.putString("title", intent.getStringExtra("title"));
            packet.putString("msg", intent.getStringExtra("msg"));
            packet.putInt("imageUri", intent.getIntExtra("imageUri", 0));
            packet.putBoolean("see_later", false);

            if (("SEE_LATER").equals(intent.getAction())) {
                packet.putBoolean("see_later", true);
            }

            notificationsFragment.setArguments(packet);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, notificationsFragment)
                    .addToBackStack(null)
                    .commit();
        }

}