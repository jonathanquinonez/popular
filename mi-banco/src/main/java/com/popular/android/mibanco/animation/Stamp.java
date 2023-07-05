package com.popular.android.mibanco.animation;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;

import com.popular.android.mibanco.R;

/** An animation that make stamping effect with noise. **/
public class Stamp extends ScaleAnimation {
    /** Static value for interpolate time. */
    private final static float INTERPOLATE_TIME = 0.8f;
    /** Static value for scale animation value. */
    private final static int SCALE_ANIMATION_DURATION = 1000;
    /** Global context for activity. */
    private final Context context;
    /** Media player object for playing sounds. */
    private final MediaPlayer m;
    /** Play boolean value. */
    private boolean played;

    /**
     * Constructor for stamp animation class.
     * 
     * @param fromX starts x value
     * @param toX ends x value
     * @param fromY starts y value
     * @param toY ends y value
     * @param pivotXType pivot x type
     * @param pivotXValue pivot x value
     * @param pivotYType pivot y type
     * @param pivotYValue pivot y value
     * @param con context
     */
    public Stamp(final float fromX, final float toX, final float fromY, final float toY, final int pivotXType, final float pivotXValue, final int pivotYType, final float pivotYValue, final Context con) {
        super(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType, pivotYValue);
        setDuration(SCALE_ANIMATION_DURATION);
        context = con;
        m = MediaPlayer.create(context, R.raw.stamp);
        played = false;
    }

    @Override
    protected void applyTransformation(final float interpolatedTime, final Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        if (!played && interpolatedTime > INTERPOLATE_TIME) {
            played = true;
            m.start();
        }
    }

    @Override
    public void initialize(final int width, final int height, final int parentWidth, final int parentHeight) {
        setDuration(SCALE_ANIMATION_DURATION);
        super.initialize(width, height, parentWidth, parentHeight);
    }

}
