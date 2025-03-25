package com.example.movies.fragments;

import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.movies.R;
import com.example.movies.adapters.NotificationAdapter;
import com.example.movies.notifications.NotificationJob;
import com.example.movies.database.NotificationDB;

import java.util.List;


public class NotificationsFragment extends Fragment {
    private List<NotificationJob> notifications;
    private boolean onDelete = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null) {
            NotificationJob notifJob = new NotificationJob(args.getString("notifCode"), args.getString("title"), args.getString("msg"), (args.getInt("imageUri")));
            setArguments(null);

            if (!args.getBoolean("see_later")) {
                return inflateArticle(notifJob, container);
            }

            NotificationDB.guardarNotificacion(requireContext(), notifJob);

        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        actualizarListaNotificaciones();
        setUpButtons();
        setUpListView();

    }

    public void setUpButtons () {
        setUpDeleteBtn();
        setUpClearAllBtn();
    }

    public void setUpClearAllBtn () {
        ImageButton clearAllBtn = requireView().findViewById(R.id.notif_clearAll_button);
        if (clearAllBtn != null) {
            clearAllBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NotificationDB.clear(requireContext());
                    actualizarListaNotificaciones();
                }
            });
        }
    }

    public void setUpDeleteBtn () {
        ImageButton deleteBtn = requireView().findViewById(R.id.notif_delete_button);
        if (deleteBtn != null) {
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDelete = !onDelete;
                    if (onDelete) {
                        deleteBtn.setColorFilter(Color.argb(200, 255, 0, 0));
                    } else {
                        deleteBtn.setColorFilter(Color.argb(200, 0, 0, 0));
                    }
                }
            });
        }
    }

    public void setUpListView () {
        ListView notifLv = requireView().findViewById(R.id.notif_frag_lv);
        if (notifLv != null) {
            notifLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (onDelete) {
                        NotificationDB.eliminarNotificacion(requireContext(), notifications.get(i).getNotifCode());
                        actualizarListaNotificaciones();
                    } else {
                        NotificationDB.seleccionarNotificacion(requireContext(), notifications.get(i).getNotifCode());
                        ViewGroup container = requireView().findViewById(R.id.not_frag_container);
                        View layout = inflateArticle(notifications.get(i), container);
                        container.removeAllViews();
                        container.addView(layout);
                    }
                }
            });
        }
    }

    public void actualizarListaNotificaciones () {
        notifications = NotificationDB.cargarNotificaciones(requireContext());
        ListView notifLv = requireView().findViewById(R.id.notif_frag_lv);
        if (notifLv != null) {
            NotificationAdapter adapter = new NotificationAdapter(requireContext(), notifications);
            notifLv.setAdapter(adapter);
        }
    }

    private View inflateArticle (NotificationJob notifJob, ViewGroup container) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View layout = inflater.inflate(R.layout.layout_notif_received, container, false);
        ((TextView) layout.findViewById(R.id.notif_recv_title)).setText(notifJob.getTitle());
        ((TextView) layout.findViewById(R.id.notif_recv_msg)).setText(notifJob.getMsg());
        ((ImageView) layout.findViewById(R.id.notif_recv_image))
                .setImageBitmap(BitmapFactory.decodeResource(getResources(), notifJob.getImageId()));
        return layout;
    }

}