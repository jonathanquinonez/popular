package com.popular.android.mibanco.util;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.ArrayList;

/**
 * Provides implementation of swipe gesture detection. SwipeDetector intercepts touch events of added views.
 */
public class SwipeDetector {

    private final static int MIN_SWIPE_DISTANCE = 100;

    private float downX;

    private float downY;

    private OnSwipeListener listener;

    private OnTouchListener touchListener;

    private float upX;

    private float upY;

    private final ArrayList<View> views = new ArrayList<View>();

    public SwipeDetector(final OnSwipeListener swipeListener) {
        listener = swipeListener;
        touchListener = new OnTouchListener() {

            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    downX = event.getX();
                    downY = event.getY();
                    return false;
                }
                case MotionEvent.ACTION_UP: {
                    upX = event.getX();
                    upY = event.getY();

                    final float deltaX = downX - upX;
                    final float deltaY = downY - upY;

                    if (Math.abs(deltaX) > MIN_SWIPE_DISTANCE) {
                        if (deltaX < 0) {
                            listener.onLeftToRightSwipe();
                            return true;
                        }
                        if (deltaX > 0) {
                            listener.onRightToLeftSwipe();
                            return true;
                        }
                    } else {
                        return false;
                    }

                    if (Math.abs(deltaY) > MIN_SWIPE_DISTANCE) {
                        if (deltaY < 0) {
                            listener.onTopToBottomSwipe();
                            return true;
                        }
                        if (deltaY > 0) {
                            listener.onBottomToTopSwipe();
                            return true;
                        }
                    } else {
                        return false;
                    }

                    return false;
                }
                default:
                    break;
                }

                return false;
            }
        };
    }

    public void addView(final View view) {
        views.add(view);
        view.setOnTouchListener(touchListener);
    }

    public void destroy() {
        if (views != null) {
            for (View view : views) {
                view.setOnTouchListener(null);
                view = null;
            }
            views.clear();
        }
    }

    public boolean onTouchEvent(final MotionEvent event) {
        return touchListener.onTouch(null, event);
    }

    public void removeView(final View view) {
        views.remove(view);
        view.setOnTouchListener(null);
    }
}
