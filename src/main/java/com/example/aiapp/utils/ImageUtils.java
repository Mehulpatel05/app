package com.example.aiapp.utils;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageUtils {

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] imageData = stream.toByteArray();
        return Base64.encodeToString(imageData, Base64.DEFAULT);
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        return stream.toByteArray();
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        if (bitmap.getWidth() <= maxWidth && bitmap.getHeight() <= maxHeight) {
            return bitmap;
        }

        float scale = Math.min((float) maxWidth / bitmap.getWidth(),
                (float) maxHeight / bitmap.getHeight());
        int newWidth = Math.round(bitmap.getWidth() * scale);
        int newHeight = Math.round(bitmap.getHeight() * scale);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}
