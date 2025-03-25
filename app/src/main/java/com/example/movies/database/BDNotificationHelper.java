package com.example.movies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDNotificationHelper  extends SQLiteOpenHelper {

    public BDNotificationHelper (Context context) {
        super(context, "notificaciones_actuales", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // SQL para crear la BBDD
        sqLiteDatabase.execSQL("CREATE TABLE notifications (" +
                "   not_code VARCHAR(15) PRIMARY KEY, " +
                "   title TEXT," +
                "   msg TEXT," +
                "   imageUri DECIMAL(3, 1)," +
                "   selected DECIMAL(3, 1));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }


}
