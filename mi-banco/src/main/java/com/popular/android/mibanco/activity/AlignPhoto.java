package com.popular.android.mibanco.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.util.CameraHelper;
import com.popular.android.mibanco.view.ZoomableImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Activity displays a confirmation to align a taken photo to a card.
 */
@SuppressLint("InlinedApi")
public class AlignPhoto extends BaseSessionActivity {

    /**
     * The Constant DEFAULT_PHOTO_HEIGHT.
     */
    private final static int DEFAULT_PHOTO_HEIGHT = 360;

    /**
     * The Constant DEFAULT_PHOTO_WIDTH.
     */
    private final static int DEFAULT_PHOTO_WIDTH = 480;

    /**
     * The Constant JPEG_QUALITY.
     */
    private final static int JPEG_QUALITY = 100;

    /**
     * The overlay's alpha level.
     */
    private final static int OVERLAY_ALPHA = 128;

    /**
     * The background bitmap.
     */
    private Bitmap backgroundBitmap;

    /**
     * The card height.
     */
    private int cardHeight;

    /**
     * The card's image unique identifier.
     */
    private String cardId;

    /**
     * The card width.
     */
    private int cardWidth;

    /**
     * The container height.
     */
    private int containerHeight;

    /**
     * The container width.
     */
    private int containerWidth;

    /**
     * The frame height.
     */
    private int frameHeight;

    /**
     * The frame width.
     */
    private int frameWidth;

    /**
     * The frame start X coordinate.
     */
    private int frameX;

    /**
     * The frame start Y coordinate.
     */
    private int frameY;

    /**
     * The overlay layout.
     */
    private RelativeLayout overlay;

    /**
     * The saved card image file path.
     */
    private String savedFilePath;

    /**
     * The image viewer.
     */
    private ZoomableImageView viewer;

    /**
     * OnGlobalLayoutListener instance.
     */
    private OnGlobalLayoutListener layoutListener;

    /**
     * Decodes a file and returns a corresponding bitmap. Scales the bitmap down if the size is too big.
     *
     * @param file the file to decode
     * @return the decoded bitmap
     */
    private Bitmap decodeFile(final File file) {
        Bitmap outBitmap = null;
        try {
            // Decode image size
            final BitmapFactory.Options bitmapOptionsTemp = new BitmapFactory.Options();
            bitmapOptionsTemp.inJustDecodeBounds = true;
            FileInputStream fileInputStream = new FileInputStream(file);
            BitmapFactory.decodeStream(fileInputStream, null, bitmapOptionsTemp);
            fileInputStream.close();

            int scaleFactor = 1;
            int maxBitmapPixels = 1000 * 1000 * 2;
            while (bitmapOptionsTemp.outHeight / scaleFactor * (bitmapOptionsTemp.outWidth / scaleFactor) > maxBitmapPixels) {
                scaleFactor *= 2;
            }

            // Decode and scale bitmap
            final BitmapFactory.Options bitmapOptionsOut = new BitmapFactory.Options();
            bitmapOptionsOut.inSampleSize = scaleFactor;
            bitmapOptionsOut.inPurgeable = true;
            fileInputStream = new FileInputStream(file);
            outBitmap = BitmapFactory.decodeStream(fileInputStream, null, bitmapOptionsOut);
            fileInputStream.close();
        } catch (final IOException e) {
            Log.w("AlignPhoto", e);
        }
        return outBitmap;
    }

    /**
     * Gets the background bitmap for the overlay.
     *
     * @return the background bitmap
     */
    private Bitmap getBackgroudBitmap() {
        final int centerX = containerWidth / 2;
        final int centerY = containerHeight / 2;

        final Bitmap bitmap = Bitmap.createBitmap(containerWidth, containerHeight, Config.ARGB_8888);
        final Canvas c = new Canvas(bitmap);
        final Paint screenPaint = new Paint();
        screenPaint.setColor(Color.WHITE);
        screenPaint.setAlpha(OVERLAY_ALPHA);

        final int upDownGap = (containerHeight - frameHeight) / 2;
        final int leftRightGap = (containerWidth - frameWidth) / 2;

        c.drawRect(0, 0, leftRightGap, containerHeight, screenPaint);
        c.drawRect(containerWidth - leftRightGap, 0, containerWidth, containerHeight, screenPaint);

        c.drawRect(leftRightGap, 0, containerWidth - leftRightGap, upDownGap, screenPaint);
        c.drawRect(leftRightGap, containerHeight - upDownGap, containerWidth - leftRightGap, containerHeight, screenPaint);

        final Paint framePaint = new Paint();
        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setColor(ContextCompat.getColor(this, R.color.grey));
        framePaint.setStrokeWidth(2.0f);

        frameX = centerX - frameWidth / 2;
        frameY = centerY - frameHeight / 2;

        c.drawRect(frameX, frameY, centerX + frameWidth / 2, centerY + frameHeight / 2, framePaint);

        return bitmap;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coverflow_align_photo);

        getSupportActionBar().hide();

        final Intent data = getIntent();
        cardWidth = data.getIntExtra("width", DEFAULT_PHOTO_WIDTH);
        cardHeight = data.getIntExtra("height", DEFAULT_PHOTO_HEIGHT);
        cardId = data.getStringExtra("cardId");
        final int orientation = data.getIntExtra("orientation", -1);

        ((TextView) findViewById(R.id.alertTitle)).setText(getString(R.string.coverflow_align_photo).toUpperCase());

        overlay = (RelativeLayout) findViewById(R.id.overlay);

        final Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        final int rotation = display.getRotation();
        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }

        savedFilePath = data.getStringExtra("path");
        Bitmap newBitmap = decodeFile(new File(savedFilePath));

        /* Image file deleted or unavailable (for example SD card has been unmounted). */
        if (newBitmap == null) {
            finish();
            return;
        }

        final Bitmap rotatedBitmap = CameraHelper.rotateBitmap(newBitmap, savedFilePath, orientation);
        viewer = (ZoomableImageView) findViewById(R.id.viewer);
        viewer.setBitmap(rotatedBitmap);
        viewer.setDefaultScale(ZoomableImageView.DEFAULT_SCALE_FIT_HEIGHT);

        setListeners();
    }

    /**
     * Sets listeners.
     */
    private void setListeners() {
        findViewById(R.id.buttonPositive).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View arg0) {
                Bitmap outBitmap = Bitmap.createBitmap(viewer.getVisibleBitmap(), frameX, frameY, frameWidth, frameHeight);
                final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                outBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, bytes);
                final String filePath = CameraHelper.getOutputMediaFilePath();
                if (filePath != null) {
                    savedFilePath = filePath;
                    final File file = new File(savedFilePath);
                    try {
                        file.createNewFile();
                        final FileOutputStream fileOutpusStream = new FileOutputStream(file);
                        fileOutpusStream.write(bytes.toByteArray());
                        fileOutpusStream.flush();
                        fileOutpusStream.close();

                        final SharedPreferences settings = getSharedPreferences(cardId, Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = settings.edit();
                        editor.putString("path", savedFilePath);
                        editor.commit();

                        ((App) getApplication()).setRefreshPaymentsCardImages(true);
                        ((App) getApplication()).setRefreshTransfersCardImages(true);
                    } catch (final Exception e) {
                        Log.e("AlignPhoto", e.toString());
                    }
                }
                outBitmap.recycle();
                outBitmap = null;

                setResult(RESULT_OK);
                finish();
            }
        });

        findViewById(R.id.buttonNegative).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View arg0) {
                finish();
            }
        });

        layoutListener = new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (overlay != null && overlay.getMeasuredWidth() > 0 && overlay.getMeasuredHeight() > 0) {
                    containerWidth = overlay.getMeasuredWidth();
                    containerHeight = overlay.getMeasuredHeight();
                    frameWidth = cardWidth;
                    frameHeight = cardHeight;

                    backgroundBitmap = getBackgroudBitmap();

                    overlay.setBackgroundDrawable(new BitmapDrawable(getResources(), backgroundBitmap));
                }
            }
        };
    }


    @Override
    protected void onDestroy() {
        if (backgroundBitmap != null) {
            backgroundBitmap.recycle();
            backgroundBitmap = null;
        }
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (overlay != null && overlay.getViewTreeObserver().isAlive()) {
            overlay.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
    }

    @Override
    protected void onStop() {
        if (overlay != null && overlay.getViewTreeObserver().isAlive()) {
            overlay.getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
        }

        super.onStop();
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(false);
        menu.findItem(R.id.menu_contact).setVisible(false);

        return true;
    }
}
