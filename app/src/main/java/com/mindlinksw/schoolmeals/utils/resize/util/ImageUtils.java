package com.mindlinksw.schoolmeals.utils.resize.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.mindlinksw.schoolmeals.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by K.K. Ho on 3/9/2017.
 */

public class ImageUtils {

    public static final String TAG = ImageUtils.class.getName();

    public static File getScaledImage(int targetLength, int quality, Bitmap.CompressFormat compressFormat,
                                      String outputDirPath, String outputFilename, File sourceImage) throws IOException {

        File directory = new File(outputDirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Prepare the new file name and path
        String outputFilePath = FileUtils.getOutputFilePath(compressFormat, outputDirPath, outputFilename, sourceImage);

        // Write the resized image to the new file
        Bitmap scaledBitmap = getScaledBitmap(targetLength, sourceImage);

        FileUtils.writeBitmapToFile(scaledBitmap, compressFormat, quality, outputFilePath);
        return new File(outputFilePath);
    }

    public static Bitmap getScaledBitmap(int targetLength, File sourceImage) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(sourceImage.getAbsolutePath(), options);

        // Get the dimensions of the original bitmap
        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;
        float aspectRatio = (float) originalWidth / originalHeight;

        // Calculate the target dimensions
        int targetWidth, targetHeight;

        if (originalWidth > originalHeight) {
            targetWidth = targetLength;
            targetHeight = Math.round(targetWidth / aspectRatio);
        } else {
            aspectRatio = 1 / aspectRatio;
            targetHeight = targetLength;
            targetWidth = Math.round(targetHeight / aspectRatio);
        }

        // create bitmap
        Matrix m = new Matrix();
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        if (width != targetWidth || height != targetHeight) {
            final float sx = targetWidth / (float) width;
            final float sy = targetHeight / (float) height;
            m.setScale(sx, sy);
        }

        int degree = getRotateDegree(sourceImage.getPath());
        m.postRotate(degree);

        Bitmap b = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);
        return b;

    }

    /**
     * Rotate Degree
     */
    public static int getRotateDegree(String path) {

        int degree = 0;

        try {

            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return degree;
    }

    /**
     * Image Size
     *
     * @param path
     */
    public static HashMap<String, Integer> getImageSize(String path) {

        HashMap<String, Integer> map = new HashMap<>();
        map.put("width", 0);
        map.put("height", 0);

        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int imageWidth = options.outWidth;
            int imageHeight = options.outHeight;

            map.put("width", imageWidth);
            map.put("height", imageHeight);

            return map;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
}
