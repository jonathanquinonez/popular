package com.popular.android.mibanco.view.coverflow;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;

import androidx.core.content.ContextCompat;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.task.BaseAsyncTask;
import com.popular.android.mibanco.util.BitmapUtils;
import com.popular.android.mibanco.util.Utils;

import java.util.ArrayList;

public class ManagedBitmapBuffer {

    private class LoadCardImageTask extends BaseAsyncTask {

        private int position;

        public LoadCardImageTask(Context context, int position) {
            super(context, null, false);
            this.position = position;
        }

        @Override
        protected Integer doInBackground(Object... params) {
            Thread.currentThread().setName("LoadCardImageTask");
            if (position < 0 || position > accounts.size() - 1) {
                return RESULT_SUCCESS;
            }

            if (bitmapBuffer.get(position) != null) {
                return RESULT_SUCCESS;
            }

            setMemoryBitmapCache(position, getCoverBitmap(position));

            return RESULT_SUCCESS;
        }
    }

    private static final int PIXEL_BYTE_SIZE = 4;

    private static final int LOAD_BITMAP_SYNC_COUNT = 5;

    private static final int MEGABYTE = 1024 * 1024;

    private static final int MAX_WINDOW_SIZE = 15;

    private final static float CARD_NICKNAME_TEXT_SIZE_TO_HEIGHT_RATIO = 0.11f;

    private final static float CARD_NICKNAME_Y_OFFSET_RATIO = 0.83f;

    private final static float CARD_NUMBER_TEXT_SIZE_TO_HEIGHT_RATIO = 0.135f;

    private final static float CARD_NUMBER_Y_OFFSET_RATIO = 0.7f;

    private final static float CARD_TEXT_X_OFFSET_RATIO = 0.08f;

    private int currentItemPosition = 0;

    private ArrayList<Bitmap> bitmapBuffer;

    private Context context;

    private ArrayList<CustomerAccount> accounts;

    private int scaledWidth;

    private int scaledHeight;

    private int windowStartIndex;

    private int windowEndIndex;

    private int windowSize;

    private int windowHalfSize;

    private boolean needsShifting;

    public ManagedBitmapBuffer(Context context, ArrayList<CustomerAccount> accounts, int scaledWidth, int scaledHeight, int position) {
        this.context = context;
        this.accounts = accounts;
        this.scaledWidth = scaledWidth;
        this.scaledHeight = scaledHeight;

        // Get available memory heap size for Bitmap buffer
        final int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        final int cacheSize = MEGABYTE * memClass / 2;

        int bitmapSize = scaledWidth * scaledHeight * PIXEL_BYTE_SIZE;
        int maxBitmaps = cacheSize / bitmapSize;
        if (maxBitmaps % 2 == 0) {
            maxBitmaps -= 1;
        }
        windowHalfSize = (maxBitmaps - 1) / 2;

        windowSize = accounts.size() < windowHalfSize * 2 + 1 ? accounts.size() : windowHalfSize * 2 + 1;
        windowSize = windowSize > MAX_WINDOW_SIZE ? MAX_WINDOW_SIZE : windowSize;
        windowHalfSize = (windowSize - 1) / 2;

        bitmapBuffer = new ArrayList<Bitmap>(accounts.size());
        for (int i = 0; i < accounts.size(); ++i) {
            bitmapBuffer.add(null);
        }
        needsShifting = (windowSize != accounts.size());

        int loadingPosition = 0;
        if (needsShifting) {
            if (position - windowHalfSize < 0) {
                loadingPosition = 0;
            } else if (position + windowHalfSize > accounts.size() - 1) {
                loadingPosition = accounts.size() - windowSize;
            } else {
                loadingPosition = position - windowHalfSize;
            }
        }

        int loadedBitmaps = 0;
        for (int i = loadingPosition; i < loadingPosition + windowSize; ++i) {
            if (loadedBitmaps <= LOAD_BITMAP_SYNC_COUNT) {
                setMemoryBitmapCache(i, getCoverBitmap(i));
            } else {
                new LoadCardImageTask(context, i).execute();
            }
            ++loadedBitmaps;
        }
        windowStartIndex = loadingPosition;
        windowEndIndex = loadingPosition + windowSize - 1;
    }

    public void setCurrentPosition(int position) {
        if (!needsShifting) {
            return;
        }

        if (position == 0 || position == accounts.size() - 1) {
            return;
        }

        boolean shiftLeft = false;
        if (position < currentItemPosition) {
            shiftLeft = true;
        }
        currentItemPosition = position;
        if (shiftLeft) {
            shiftLeft();
        } else {
            shiftRight();
        }
    }

    private void shiftLeft() {
        if (currentItemPosition > windowHalfSize) {
            setMemoryBitmapCache(windowEndIndex, null);
            windowStartIndex -= 1;
            windowEndIndex -= 1;
            new LoadCardImageTask(context, windowStartIndex).execute();
        } else {
            windowStartIndex = 0;
            windowEndIndex = windowSize - 1;
        }
    }

    private void shiftRight() {
        if (currentItemPosition > windowHalfSize) {
            setMemoryBitmapCache(windowStartIndex, null);
            windowStartIndex += 1;
            windowEndIndex += 1;
            new LoadCardImageTask(context, windowEndIndex).execute();
        } else {
            windowStartIndex = 0;
            windowEndIndex = windowSize - 1;
        }
    }

    private void setMemoryBitmapCache(int position, Bitmap object) {
        if (position < 0 || position > accounts.size() - 1) {
            return;
        }
        Bitmap bitmap = bitmapBuffer.get(position);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        bitmapBuffer.set(position, object);
    }

    public void close() {
        for (int i = 0; i < bitmapBuffer.size(); ++i) {
            Bitmap bitmap = bitmapBuffer.get(i);
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmapBuffer.set(i, null);
            }
        }
    }

    public Bitmap getBitmap(final int position, final boolean recreate) {
        if (position < 0 || position > accounts.size() - 1) {
            return null;
        }

        if (!recreate) {
            if (bitmapBuffer.get(position) != null) {
                return bitmapBuffer.get(position);
            }
        }

        return getCoverBitmap(position);
    }

    /**
     * Gets the cover bitmap for the specified position.
     * 
     * @param position the position of cover flow card
     * @return the cover bitmap for the position
     */
    private Bitmap getCoverBitmap(final int position) {
        if (accounts == null || position < 0 || position > accounts.size() - 1) {
            return null;
        }

        final String path = Utils.getAccountImagePath(accounts.get(position), context);
        Bitmap sourceBitmap = null;
        Bitmap tempBitmap = null;
        Canvas canvas;

        if (path != null) {
            tempBitmap = BitmapUtils.decodeSampledBitmapFromFile(path, scaledWidth, scaledHeight);
        }
        if (tempBitmap == null) {
            tempBitmap = Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context,accounts.get(position).getGalleryImgResource())).getBitmap(), scaledWidth, scaledHeight, true);
        }

        if (tempBitmap.isMutable()) {
            sourceBitmap = tempBitmap;
            canvas = new Canvas(sourceBitmap);
        } else {
            sourceBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Config.ARGB_8888);
            canvas = new Canvas(sourceBitmap);
            canvas.drawBitmap(tempBitmap, 0, 0, null);
            tempBitmap.recycle();
            tempBitmap = null;
        }

        final Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/kredit.ttf"));
        p.setFakeBoldText(false);
        p.setShadowLayer(1, 0, 0, Color.BLACK);
        p.setTextSize((int) (scaledHeight * CARD_NUMBER_TEXT_SIZE_TO_HEIGHT_RATIO));

        String accountLast4Num = accounts.get(position).getAccountLast4Num() == null || accounts.get(position).getAccountLast4Num().isEmpty()? "" : accounts.get(position).getAccountLast4Num().substring(1);
        canvas.drawText(context.getString(R.string.coverflow_hidden_card_numbers) + accountLast4Num, (int) (scaledWidth * CARD_TEXT_X_OFFSET_RATIO),
                (int) (scaledHeight * CARD_NUMBER_Y_OFFSET_RATIO), p);

        p.setTextSize((int) (scaledHeight * CARD_NICKNAME_TEXT_SIZE_TO_HEIGHT_RATIO));
        canvas.drawText(accounts.get(position).getNickname(), (int) (scaledWidth * CARD_TEXT_X_OFFSET_RATIO), (int) (scaledHeight * CARD_NICKNAME_Y_OFFSET_RATIO), p);

        return sourceBitmap;
    }

    public void refreshCacheAt(int position) {
        setMemoryBitmapCache(position, getCoverBitmap(position));
    }
}
