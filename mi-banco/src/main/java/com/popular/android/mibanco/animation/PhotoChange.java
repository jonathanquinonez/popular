package com.popular.android.mibanco.animation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * An animation that rotates the view on the Y axis between two specified angles. This animation also adds a translation on the Z axis (depth) to
 * improve the effect.
 */
public class PhotoChange extends Animation {
    /** Variable for represent z translate. **/
    private final static float CAMERA_Z_TRANSLATE = -20f;
    /** Breaking time variable. **/
    private final float breakingTime;
    /** Camera Object. **/
    private Camera camera;
    /** Center X variable for X axis. **/
    private final float centerX;
    /** Center Y variable for Y axis. **/
    private final float centerY;
    /** Delay for transformation. **/
    private final float delay;
    /** Max zoom variable. **/
    private final float maxZoom;

    /**
     * Constructor for photo change Animation.
     * 
     * @param duration time of duration
     * @param mDelay time of delay
     * @param mBreakingTime breaking time
     * @param mMaxZoom max zoom
     * @param mCenterX center X axis
     * @param mCenterY center Y axis
     */
    public PhotoChange(final int duration, final float mDelay, final float mBreakingTime, final int mMaxZoom, final float mCenterX, final float mCenterY) {
        delay = mDelay;
        breakingTime = mBreakingTime;
        maxZoom = mMaxZoom;
        centerX = mCenterX;
        centerY = mCenterY;
        setDuration(duration);
        this.setInterpolator(new AccelerateInterpolator());
    }


    @Override
    protected void applyTransformation(final float interpolatedTime, final Transformation t) {

        final Matrix matrix = t.getMatrix();
        camera.save();
        if (interpolatedTime < delay) {
            camera.translate(0, 0, CAMERA_Z_TRANSLATE);
        } else if (interpolatedTime < breakingTime) {
            camera.translate(0, 0, CAMERA_Z_TRANSLATE - maxZoom * ((interpolatedTime - delay) / (breakingTime - delay)));
        } else {
            camera.translate(0, 0, CAMERA_Z_TRANSLATE + maxZoom * ((interpolatedTime - breakingTime) / (1.0f - breakingTime) - 1));
        }

        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }


    @Override
    public void initialize(final int width, final int height, final int parentWidth, final int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        camera = new Camera();
    }
}
