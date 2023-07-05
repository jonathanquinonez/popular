package com.popular.android.mibanco.view.coverflow;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.SpinnerAdapter;

import com.popular.android.mibanco.R;

public class CoverFlow extends Gallery {

    private static final int DEFAULT_IMAGE_HEIGHT = 360;

    private static final int DEFAULT_IMAGE_WIDTH = 480;

    private static final int DEFAULT_MAX_ROTATION_ANGLE = 30;

    private static final int DEFAULT_MAX_ZOOM = -20;

    private final static float ZOOM_RATIO = 1.5f;

    private static int getCenterOfView(final View view) {
        return view.getLeft() + view.getWidth() / 2;
    }

    private int imageHeight;

    private int imageWidth;

    private final Camera mCamera = new Camera();

    private int mCoveflowCenter;

    private int mMaxRotationAngle = DEFAULT_MAX_ROTATION_ANGLE;

    private int mMaxZoom = DEFAULT_MAX_ZOOM;

    private boolean scrollingEnabled = true;

    public CoverFlow(final Context context) {
        super(context);
        setStaticTransformationsEnabled(true);
    }

    public CoverFlow(final Context context, final AttributeSet attrs) {
        this(context, attrs, android.R.attr.galleryStyle);
        setStaticTransformationsEnabled(true);
    }

    public CoverFlow(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        parseAttributes(context, attrs);
        setStaticTransformationsEnabled(true);
    }

    private int getCenterOfCoverflow() {
        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    }

    @Override
    protected boolean getChildStaticTransformation(final View child, final Transformation t) {

        final int childCenter = getCenterOfView(child);
        final int childWidth = child.getWidth();
        int rotationAngle = 0;

        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX);

        if (childCenter == mCoveflowCenter) {
            transformImageBitmap(child, t, 0);
        } else {
            rotationAngle = (int) ((float) (mCoveflowCenter - childCenter) / childWidth * mMaxRotationAngle);
            if (Math.abs(rotationAngle) > mMaxRotationAngle) {
                rotationAngle = rotationAngle < 0 ? -mMaxRotationAngle : mMaxRotationAngle;
            }
            transformImageBitmap(child, t, rotationAngle);
        }

        return true;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getMaxRotationAngle() {
        return mMaxRotationAngle;
    }

    public int getMaxZoom() {
        return mMaxZoom;
    }

    public boolean getScrollingEnabled() {
        return scrollingEnabled;
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_LEFT:
        case KeyEvent.KEYCODE_DPAD_RIGHT:
            if (!scrollingEnabled) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        mCoveflowCenter = getCenterOfCoverflow();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTrackballEvent(final MotionEvent event) {
        if (scrollingEnabled) {
            return super.onTrackballEvent(event);
        }
        return true;
    }

    private void parseAttributes(final Context context, final AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CoverFlow);
        try {
            imageWidth = (int) a.getDimension(R.styleable.CoverFlow_imageWidth, DEFAULT_IMAGE_WIDTH);
            imageHeight = (int) a.getDimension(R.styleable.CoverFlow_imageHeight, DEFAULT_IMAGE_HEIGHT);
        } finally {
            a.recycle();
        }
    }

    @Override
    public void setAdapter(final SpinnerAdapter adapter) {
        if (!(adapter instanceof CoverflowImageAdapter)) {
            throw new IllegalArgumentException("The adapter should derive from " + CoverflowImageAdapter.class.getName());
        }
        final CoverflowImageAdapter coverAdapter = (CoverflowImageAdapter) adapter;
        coverAdapter.setWidth(imageWidth);
        coverAdapter.setHeight(imageHeight);
        super.setAdapter(coverAdapter);
    }

    public void setImageHeight(final int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public void setImageWidth(final int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public void setMaxRotationAngle(final int maxRotationAngle) {
        mMaxRotationAngle = maxRotationAngle;
    }

    public void setMaxZoom(final int maxZoom) {
        mMaxZoom = maxZoom;
    }

    public void setScrollingEnabled(final boolean enabled) {
        scrollingEnabled = enabled;
    }

    private void transformImageBitmap(final View child, final Transformation t, final int rotationAngle) {
        mCamera.save();
        final Matrix imageMatrix = t.getMatrix();
        final int height = child.getLayoutParams().height;
        final int width = child.getLayoutParams().width;
        final int rotation = Math.abs(rotationAngle);

        // As the angle of the view gets less, zoom in
        if (rotation < mMaxRotationAngle) {
            final float zoomAmount = mMaxZoom + rotation * ZOOM_RATIO;
            mCamera.translate(0.0f, 0.0f, zoomAmount);
        }

        mCamera.rotateY(rotationAngle);
        mCamera.getMatrix(imageMatrix);
        imageMatrix.preTranslate(-(width / 2.0f), -(height / 2.0f));
        imageMatrix.postTranslate(width / 2.0f, height / 2.0f);
        mCamera.restore();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return super.onFling(e1, e2, velocityX / 2, velocityY);
    }
}
