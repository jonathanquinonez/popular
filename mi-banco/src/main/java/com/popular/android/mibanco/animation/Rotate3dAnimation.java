package com.popular.android.mibanco.animation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * An animation that rotates the view on the Y axis between two specified angles. This animation also adds a translation on the Z axis (depth) to
 * improve the effect.
 */
public class Rotate3dAnimation extends Animation {
    /** Camera object. */
    private Camera mCamera;
    /** Variable for center x value. */
    private final float mCenterX;
    /** Variable for center y value. */
    private final float mCenterY;
    /** Variable for depth z value. */
    private final float mDepthZ;
    /** Variable for from degree value. */
    private final float mFromDegrees;
    /** Variable for reverse value. */
    private final boolean mReverse;
    /** Variable for to degree value. */
    private final float mToDegrees;

    /**
     * Rotate 3D animation constructor.
     * 
     * @param fromDegrees from degrees
     * @param toDegrees to degrees
     * @param centerX center x value
     * @param centerY center y value
     * @param depthZ depth z value
     * @param reverse reverse value
     */
    public Rotate3dAnimation(final float fromDegrees, final float toDegrees, final float centerX, final float centerY, final float depthZ, final boolean reverse) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = centerX;
        mCenterY = centerY;
        mDepthZ = depthZ;
        mReverse = reverse;
    }


    @Override
    protected void applyTransformation(final float interpolatedTime, final Transformation t) {
        final float fromDegrees = mFromDegrees;
        final float degrees = fromDegrees + (mToDegrees - fromDegrees) * interpolatedTime;

        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Camera camera = mCamera;

        final Matrix matrix = t.getMatrix();

        camera.save();
        if (mReverse) {
            camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
        } else {
            camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
        }
        camera.rotateY(degrees);
        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }

    @Override
    public void initialize(final int width, final int height, final int parentWidth, final int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

}
