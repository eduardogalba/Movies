package com.example.movies.utils;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitmapManager extends FileProvider {

    public static Uri saveBitmap2Dir (Context context, Bitmap image, String fileName, boolean poster) {
        File targetFile;
        FileOutputStream targetStream;

        targetFile = findBitmapFile(context, fileName, poster);

        try {
            targetStream = new FileOutputStream(targetFile);
            image.compress(Bitmap.CompressFormat.JPEG, 100, targetStream);
            targetStream.flush();
            targetStream.close();
            return FileProvider.getUriForFile(context, BitmapManager.class.getName(), targetFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static File findBitmapFile (Context ctx, String fileName, boolean poster) {
        File directory;
        File baseDir;

        baseDir = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                ? ctx.getExternalFilesDir(null)
                : ctx.getFilesDir();

        directory = new File(baseDir, poster ? "MoviePosters" : "MiniaturePosters");

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new RuntimeException("Cannot create directory on saving Bitmap file");
            }
        }

        return new File(directory, fileName);
    }

    public static Bitmap getBitmap (Context ctx, String fileName, boolean poster) {
        File found = findBitmapFile(ctx, fileName, poster);

        if (found.exists()) {
            return BitmapFactory.decodeFile(found.getAbsolutePath());
        }

        return null;
    }

    public static Uri shareImage (Context ctx, String filename, boolean poster) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, filename);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        Uri uri = ctx.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        OutputStream targetStream;
        Bitmap image = getBitmap(ctx, filename, poster);

        try {
            assert(uri != null);
            targetStream = ctx.getContentResolver().openOutputStream(uri);
            assert (targetStream != null);
            assert (image != null);
            image.compress(Bitmap.CompressFormat.JPEG, 100, targetStream);
            targetStream.flush();
            targetStream.close();
            return uri;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
