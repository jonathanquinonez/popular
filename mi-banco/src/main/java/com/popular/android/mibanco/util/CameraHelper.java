package com.popular.android.mibanco.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.popular.android.mibanco.MiBancoConstants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraHelper {

    private final static int MIRROR_HORIZONTAL_CODE = 2;

    private final static int MIRROR_HORIZONTAL_ROTATE_270_CODE = 5;

    private final static int MIRROR_HORIZONTAL_ROTATE_90_CODE = 7;

    private final static int MIRROR_VERTICAL_CODE = 4;

    // All rotations are clockwise
    private final static int ROTATE_180 = 180;

    private final static int ROTATE_180_CODE = 3;

    private final static int ROTATE_270 = 270;

    private final static int ROTATE_270_CODE = 8;

    private final static int ROTATE_90 = 90;

    private final static int ROTATE_90_CODE = 6;


    public static File getOutputMediaFile() {

        final File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.w("CameraHelper", "Failed to create directory.");
                return null;
            }
        }
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    public static Uri getOutputMediaFileUri() {
        try {
            return Uri.fromFile(getOutputMediaFile());
        } catch (final Exception e) {
            Log.e("CameraHelper", e.toString());

        }
        return null;
    }

    public static String getOutputMediaFilePath() {
        try {
            return getOutputMediaFileUri().getPath();
        } catch (final Exception e) {
            Log.e("CameraHelper", e.toString());
        }
        return null;
    }

    public static String getCameraUriPathFromPrefs(Context context) {
        final SharedPreferences prefs = Utils.getSecuredSharedPreferences(context);
        return prefs.getString(MiBancoConstants.PREFS_KEY_CAMERA_URI_PATH, null);
    }

    public static void setCameraUriPathFromPrefs(String uriPath, Context context) {
        final SharedPreferences prefs = Utils.getSecuredSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(MiBancoConstants.PREFS_KEY_CAMERA_URI_PATH, uriPath);
        editor.commit();
    }

    public static Bitmap rotateBitmap(Bitmap sourceBitmap, final String imageUri, int orientation) {
        Bitmap rotatedBitmap = null;
        try {
            final ExifInterface exif = new ExifInterface(imageUri);
            if (orientation == -1) {
                final String strOrientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                orientation = Integer.parseInt(strOrientation);
            }

            Matrix matrix;
            Matrix matrixMirrorHorizontal;
            Matrix matrixMirrorVertical;
            final float[] mirrorHorizontal = { -1, 0, 0, 0, 1, 0, 0, 0, 1 };
            final float[] mirrorVertical = { 1, 0, 0, 0, -1, 0, 0, 0, 1 };
            switch (orientation) {
            // Rotate 180
            case ROTATE_180:
                matrix = new Matrix();
                matrix.postRotate(ROTATE_180);
                break;
            // Rotate 90 CW
            case ROTATE_90:
                matrix = new Matrix();
                matrix.postRotate(ROTATE_90);
                break;
            // Rotate 270 CW
            case ROTATE_270:
                matrix = new Matrix();
                matrix.postRotate(ROTATE_270);
                break;
            // Rotate 180
            case ROTATE_180_CODE:
                matrix = new Matrix();
                matrix.postRotate(ROTATE_180);
                break;
            // Rotate 90 CW
            case ROTATE_90_CODE:
                matrix = new Matrix();
                matrix.postRotate(ROTATE_90);
                break;
            // Rotate 270 CW
            case ROTATE_270_CODE:
                matrix = new Matrix();
                matrix.postRotate(ROTATE_270);
                break;
            // Mirror horizontal
            case MIRROR_HORIZONTAL_CODE:
                matrix = new Matrix();
                matrixMirrorHorizontal = new Matrix();
                matrixMirrorHorizontal.setValues(mirrorHorizontal);
                matrix.postConcat(matrixMirrorHorizontal);
                break;
            // Mirror vertical
            case MIRROR_VERTICAL_CODE:
                matrix = new Matrix();
                matrixMirrorVertical = new Matrix();
                matrixMirrorVertical.setValues(mirrorVertical);
                matrix.postConcat(matrixMirrorVertical);
                break;
            // Mirror horizontal and rotate 270 CW
            case MIRROR_HORIZONTAL_ROTATE_270_CODE:
                matrix = new Matrix();
                matrixMirrorHorizontal = new Matrix();
                matrixMirrorHorizontal.setValues(mirrorHorizontal);
                matrix.postConcat(matrixMirrorHorizontal);
                matrix.postRotate(ROTATE_270);
                break;
            // Mirror horizontal and rotate 90 CW
            case MIRROR_HORIZONTAL_ROTATE_90_CODE:
                matrix = new Matrix();
                matrixMirrorHorizontal = new Matrix();
                matrixMirrorHorizontal.setValues(mirrorHorizontal);
                matrix.postConcat(matrixMirrorHorizontal);
                matrix.postRotate(ROTATE_90);
                break;
            default:
                matrix = null;
                break;
            }

            if (matrix == null) {
                rotatedBitmap = sourceBitmap;
            } else {
                rotatedBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
                sourceBitmap.recycle();
                sourceBitmap = null;
            }

        } catch (final Exception e) {

            return sourceBitmap;
        }
        return rotatedBitmap;
    }

    public static File createTempImageFile(Context context) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + ".jpg";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(storageDir, imageFileName);
    }

    public static void deleteTempImage(Context context, Uri uri) {
        if (uri != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                context.getContentResolver().delete(uri, null, null);
            else
                new File(uri.getPath()).delete();
        }
    }
}
