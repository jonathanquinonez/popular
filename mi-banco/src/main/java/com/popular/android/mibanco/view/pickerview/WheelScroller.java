/*
 *  Android Wheel Control.
 *  https://code.google.com/p/android-wheel/
 *  
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.popular.android.mibanco.view.pickerview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Scroller class handles scrolling events and updates the
 */
public class WheelScroller {
    /**
     * Scrolling listener interface
     */
    public interface ScrollingListener {
        /**
         * Finishing callback called after justifying
         */
        void onFinished();

        /**
         * Justifying callback called to justify a view when scrolling is ended
         */
        void onJustify();

        /**
         * Scrolling callback called when scrolling is performed.
         * 
         * @param distance the distance to scroll
         */
        void onScroll(int distance);

        /**
         * Starting callback called when scrolling is started
         */
        void onStarted();
    }

    /** Minimum delta for scrolling */
    public static final int MIN_DELTA_FOR_SCROLLING = 1;

    /** Scrolling duration */
    private static final int SCROLLING_DURATION = 400;

    // animation handler
    private final Handler animationHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            scroller.computeScrollOffset();
            int currY = scroller.getCurrY();
            final int delta = lastScrollY - currY;
            lastScrollY = currY;
            if (delta != 0) {
                listener.onScroll(delta);
            }

            // scrolling is not finished when it comes to final Y
            // so, finish it manually
            if (Math.abs(currY - scroller.getFinalY()) < MIN_DELTA_FOR_SCROLLING) {
                currY = scroller.getFinalY();
                scroller.forceFinished(true);
            }
            if (!scroller.isFinished()) {
                animationHandler.sendEmptyMessage(msg.what);
            } else if (msg.what == messageScroll) {
                justify();
            } else {
                finishScrolling();
            }
        }
    };

    // Context
    private final Context context;

    // Scrolling
    private final GestureDetector gestureDetector;
    // gesture listener
    private final SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
        @Override
        public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {
            lastScrollY = 0;
            final int maxY = 0x7FFFFFFF;
            final int minY = -maxY;
            scroller.fling(0, lastScrollY, 0, (int) -velocityY, 0, 0, minY, maxY);
            setNextMessage(messageScroll);
            return true;
        }

        @Override
        public boolean onScroll(final MotionEvent e1, final MotionEvent e2, final float distanceX, final float distanceY) {
            // Do scrolling in onTouchEvent() since onScroll() are not call immediately
            // when user touch and move the wheel
            return true;
        }
    };
    private boolean isScrollingPerformed;
    private int lastScrollY;
    private float lastTouchedY;

    // Listener
    private final ScrollingListener listener;

    private final int messageJustify = 1;
    // Messages
    private final int messageScroll = 0;

    private Scroller scroller;

    /**
     * Constructor
     * 
     * @param context the current context
     * @param listener the scrolling listener
     */
    public WheelScroller(final Context context, final ScrollingListener listener) {
        gestureDetector = new GestureDetector(context, gestureListener);
        gestureDetector.setIsLongpressEnabled(false);

        scroller = new Scroller(context);

        this.listener = listener;
        this.context = context;
    }

    /**
     * Clears messages from queue
     */
    private void clearMessages() {
        animationHandler.removeMessages(messageScroll);
        animationHandler.removeMessages(messageJustify);
    }

    /**
     * Finishes scrolling
     */
    void finishScrolling() {
        if (isScrollingPerformed) {
            listener.onFinished();
            isScrollingPerformed = false;
        }
    }

    /**
     * Justifies wheel
     */
    private void justify() {
        listener.onJustify();
        setNextMessage(messageJustify);
    }

    /**
     * Handles Touch event
     * 
     * @param event the motion event
     * @return
     */
    public boolean onTouchEvent(final MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            lastTouchedY = event.getY();
            scroller.forceFinished(true);
            clearMessages();
            break;

        case MotionEvent.ACTION_MOVE:
            // perform scrolling
            final int distanceY = (int) (event.getY() - lastTouchedY);
            if (distanceY != 0) {
                startScrolling();
                listener.onScroll(distanceY);
                lastTouchedY = event.getY();
            }
            break;
        }

        if (!gestureDetector.onTouchEvent(event) && event.getAction() == MotionEvent.ACTION_UP) {
            justify();
        }

        return true;
    }

    /**
     * Scroll the wheel
     * 
     * @param distance the scrolling distance
     * @param time the scrolling duration
     */
    public void scroll(final int distance, final int time) {
        scroller.forceFinished(true);

        lastScrollY = 0;

        scroller.startScroll(0, 0, 0, distance, time != 0 ? time : SCROLLING_DURATION);
        setNextMessage(messageScroll);

        startScrolling();
    }

    /**
     * Set the the specified scrolling interpolator
     * 
     * @param interpolator the interpolator
     */
    public void setInterpolator(final Interpolator interpolator) {
        scroller.forceFinished(true);
        scroller = new Scroller(context, interpolator);
    }

    /**
     * Set next message to queue. Clears queue before.
     * 
     * @param message the message to set
     */
    private void setNextMessage(final int message) {
        clearMessages();
        animationHandler.sendEmptyMessage(message);
    }

    /**
     * Starts scrolling
     */
    private void startScrolling() {
        if (!isScrollingPerformed) {
            isScrollingPerformed = true;
            listener.onStarted();
        }
    }

    /**
     * Stops scrolling
     */
    public void stopScrolling() {
        scroller.forceFinished(true);
    }
}
