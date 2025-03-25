package com.example.movies.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.movies.R;
import com.example.movies.notifications.NotificationJob;

import java.util.List;

public class NotificationAdapter extends BaseAdapter {
    private final List<NotificationJob> notifications;
    private final Context context;

    public NotificationAdapter (Context ctx, List<NotificationJob> items) {
        this.notifications = items;
        this.context = ctx;
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int i) {
        return notifications.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (view == null) {
            view = inflater.inflate(R.layout.layout_item_notificacion, viewGroup, false);
        }

        NotificationJob notif = notifications.get(i);
        ((TextView) view.findViewById(R.id.notif_item_title)).setText(notif.getTitle());

        ImageView badge = view.findViewById(R.id.notif_item_badge);

        if (notif.isSelected()) {
            badge.setColorFilter(Color.argb(200, 66, 197, 242));
            badge.setVisibility(View.VISIBLE);
        } else {
            badge.setVisibility(View.INVISIBLE);
        }

        return view;
    }
}
