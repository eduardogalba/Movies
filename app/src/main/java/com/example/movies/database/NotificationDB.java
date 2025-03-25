package com.example.movies.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.movies.notifications.NotificationJob;

import java.util.LinkedList;
import java.util.List;

public class NotificationDB {
    private static BDNotificationHelper miDBHelper = null;

    // Patron Singleton
    private static void initHelper (Context ctx) {
        if (miDBHelper == null) {
            miDBHelper = new BDNotificationHelper(ctx);
        }
    }

    public static void guardarNotificacion (Context ctx, NotificationJob not) {

        initHelper(ctx);

        SQLiteDatabase conn = miDBHelper.getWritableDatabase();

        conn.execSQL("INSERT OR REPLACE INTO notifications (not_code, title, msg, imageUri, selected" +
                ") VALUES (?,?,?,?,?);",
                new Object[]{not.getNotifCode(), not.getTitle(), not.getMsg(), not.getImageId(), 0});
        conn.close();
    }

    public static List<NotificationJob> cargarNotificaciones (Context ctx) {

        initHelper(ctx);

        SQLiteDatabase db = miDBHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM notifications;", null);
        List<NotificationJob> notificaciones = new LinkedList<>();

        while (c.moveToNext()) {
            NotificationJob notif = new NotificationJob(c.getString(0), c.getString(1), c.getString(2), c.getInt(3));
            notif.setSelected(c.getInt(4) == 0);
            notificaciones.add(notif);
        }

        db.close();

        return notificaciones;
    }

    public static void seleccionarNotificacion (Context ctx, String notifCode) {

        initHelper(ctx);

        SQLiteDatabase conn = miDBHelper.getWritableDatabase();

        conn.execSQL("UPDATE notifications SET selected = ? WHERE not_code = ?;", new Object[]{1, notifCode});

        conn.close();
    }

    public static void eliminarNotificacion (Context ctx, String notifCode) {

        initHelper(ctx);

        SQLiteDatabase conn = miDBHelper.getWritableDatabase();

        conn.execSQL("DELETE FROM notifications WHERE not_code = ?;", new String[]{notifCode});
        conn.close();
    }


    public static void clear (Context ctx) {
        ctx.deleteDatabase("notificaciones_actuales");
    }

}
