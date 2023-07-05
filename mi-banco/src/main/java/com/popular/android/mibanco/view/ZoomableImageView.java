/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 */

/* Modified https://github.com/a85/WebComicViewer/blob/master/src/com/rickreation/ui/ZoomableImageView.java */
package com.popular.android.mibanco.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class ZoomableImageView extends View {

    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(final MotionEvent event) {
            if (isAnimating) {
                return true;
            }

            scaleChange = 1;
            isAnimating = true;
            targetScaleX = event.getX();
            targetScaleY = event.getY();

            if (Math.abs(currentScale - maxScale) > 0.1) {
                targetScale = maxScale;
            } else {
                targetScale = minScale;
            }
            targetRatio = targetScale / currentScale;
            mHandler.removeCallbacks(mUpdateImageScale);
            mHandler.post(mUpdateImageScale);
            return true;
        }

        @Override
        public boolean onDown(final MotionEvent e) {
            return false;
        }

        @Override
        public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    public static final int DEFAULT_SCALE_FIT_INSIDE = 0;

    public static final int DEFAULT_SCALE_ORIGINAL = 1;

    public static final int DEFAULT_SCALE_FIT_HEIGHT = 2;

    public static final float MAX_ZOOM_IN = 5.0f;

    public static final float MAX_ZOOM_OUT = 2.5f;

    // We can be in one of these 3 states
    static final int NONE = 0;

    static final int DRAG = 1;

    static final int ZOOM = 2;

    Paint background;

    private int containerHeight;

    private int containerWidth;

    float currentScale;

    float curX;

    float curY;

    private int defaultScale;

    float easing = 0.2f;

    private final GestureDetector gestureDetector;

    private Bitmap imgBitmap;

    boolean isAnimating;

    // Matrices will be used to move and zoom image
    Matrix matrix = new Matrix();

    float maxScale = 2.0f;

    private final Handler mHandler = new Handler();

    PointF mid = new PointF();

    float minScale;

    int mode = NONE;

    private final Runnable mUpdateImagePositionTask = new Runnable() {
        @Override
        public void run() {
            float[] mvals;

            if (Math.abs(targetX - curX) < 5 && Math.abs(targetY - curY) < 5) {
                isAnimating = false;
                mHandler.removeCallbacks(mUpdateImagePositionTask);

                mvals = new float[9];
                matrix.getValues(mvals);

                currentScale = mvals[0];
                curX = mvals[2];
                curY = mvals[5];

                // Set the image parameters and invalidate display
                final float diffX = targetX - curX;
                final float diffY = targetY - curY;

                matrix.postTranslate(diffX, diffY);
            } else {
                isAnimating = true;
                mvals = new float[9];
                matrix.getValues(mvals);

                currentScale = mvals[0];
                curX = mvals[2];
                curY = mvals[5];

                // Set the image parameters and invalidate display
                final float diffX = (targetX - curX) * 0.3f;
                final float diffY = (targetY - curY) * 0.3f;

                matrix.postTranslate(diffX, diffY);
                mHandler.postDelayed(this, 25);
            }

            invalidate();
        }
    };

    private final Runnable mUpdateImageScale = new Runnable() {
        @Override
        public void run() {
            final float transitionalRatio = targetScale / currentScale;
            float dx;
            if (Math.abs(transitionalRatio - 1) > 0.05) {
                isAnimating = true;
                if (targetScale > currentScale) {
                    dx = transitionalRatio - 1;
                    scaleChange = 1 + dx * 0.2f;

                    currentScale *= scaleChange;

                    if (currentScale > targetScale) {
                        currentScale = currentScale / scaleChange;
                        scaleChange = 1;
                    }
                } else {
                    dx = 1 - transitionalRatio;
                    scaleChange = 1 - dx * 0.5f;
                    currentScale *= scaleChange;

                    if (currentScale < targetScale) {
                        currentScale = currentScale / scaleChange;
                        scaleChange = 1;
                    }
                }

                if (scaleChange != 1) {
                    matrix.postScale(scaleChange, scaleChange, targetScaleX, targetScaleY);
                    mHandler.postDelayed(mUpdateImageScale, 15);
                    invalidate();
                } else {
                    isAnimating = false;
                    scaleChange = 1;
                    matrix.postScale(targetScale / currentScale, targetScale / currentScale, targetScaleX, targetScaleY);
                    currentScale = targetScale;
                    mHandler.removeCallbacks(mUpdateImageScale);
                    invalidate();
                    checkImageConstraints();
                }
            } else {
                isAnimating = false;
                scaleChange = 1;
                matrix.postScale(targetScale / currentScale, targetScale / currentScale, targetScaleX, targetScaleY);
                currentScale = targetScale;
                mHandler.removeCallbacks(mUpdateImageScale);
                invalidate();
                checkImageConstraints();
            }
        }
    };
    // For pinch and zoom
    float oldDist = 1f;

    Matrix savedMatrix = new Matrix();
    float scaleChange;

    float scaleDampingFactor = 0.5f;
    float screenDensity;

    PointF start = new PointF();

    float targetRatio;

    float targetScale;

    float targetScaleX;

    float targetScaleY;
    // For animating stuff
    float targetX;

    float targetY;

    float transitionalRatio;

    float wpInnerRadius = 20.0f;

    float wpRadius = 25.0f;

    public ZoomableImageView(final Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);

        screenDensity = context.getResources().getDisplayMetrics().density;

        initPaints();
        gestureDetector = new GestureDetector(new MyGestureDetector());
    }

    public ZoomableImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        screenDensity = context.getResources().getDisplayMetrics().density;
        initPaints();
        gestureDetector = new GestureDetector(new MyGestureDetector());

        defaultScale = ZoomableImageView.DEFAULT_SCALE_FIT_INSIDE;
    }

    // Checks and sets the target image x and y co-ordinates if out of bounds
    private void checkImageConstraints() {
        if (imgBitmap == null) {
            return;
        }

        final float[] mvals = new float[9];
        matrix.getValues(mvals);

        currentScale = mvals[0];

        if (currentScale < minScale) {
            final float deltaScale = minScale / currentScale;
            final float px = containerWidth / 2;
            final float py = containerHeight / 2;
            matrix.postScale(deltaScale, deltaScale, px, py);
            invalidate();
        }

        matrix.getValues(mvals);
        currentScale = mvals[0];
        curX = mvals[2];
        curY = mvals[5];

        final int rangeLimitX = containerWidth - (int) (imgBitmap.getWidth() * currentScale);
        final int rangeLimitY = containerHeight - (int) (imgBitmap.getHeight() * currentScale);

        boolean toMoveX = false;
        boolean toMoveY = false;

        if (rangeLimitX < 0) {
            if (curX > 0 && curX > containerWidth / 2) {
                targetX = 0;
                toMoveX = true;
            } else if (curX < rangeLimitX - containerWidth / 2) {
                targetX = rangeLimitX;
                toMoveX = true;
            }
        } else {
            if (curX > containerWidth / 2 || curX + imgBitmap.getWidth() * currentScale < containerWidth / 2) {
                targetX = rangeLimitX / 2;
                toMoveX = true;
            }
        }

        if (rangeLimitY < 0) {
            if (curY > 0 && curY > containerHeight / 2) {
                targetY = 0;
                toMoveY = true;
            } else if (curY < rangeLimitY - containerHeight / 2) {
                targetY = rangeLimitY;
                toMoveY = true;
            }
        } else {
            if (curY > containerHeight / 2 || curY + imgBitmap.getHeight() * currentScale < containerHeight / 2) {
                targetY = rangeLimitY / 2;
                toMoveY = true;
            }
        }

        if (toMoveX || toMoveY) {
            if (!toMoveY) {
                targetY = curY;
            }
            if (!toMoveX) {
                targetX = curX;
            }

            // Disable touch event actions
            isAnimating = true;
            // Initialize timer
            mHandler.removeCallbacks(mUpdateImagePositionTask);
            mHandler.postDelayed(mUpdateImagePositionTask, 100);
        }
    }

    public int getDefaultScale() {
        return defaultScale;
    }

    public Bitmap getPhotoBitmap() {
        return imgBitmap;
    }

    public Bitmap getVisibleBitmap() {
        final Bitmap temp = Bitmap.createBitmap(containerWidth, containerHeight, Config.ARGB_8888);
        final Canvas canvas = new Canvas(temp);
        canvas.drawBitmap(imgBitmap, matrix, background);

        return temp;
    }

    private void initPaints() {
        background = new Paint();
        background.setColor(Color.WHITE);
    }

    private void midPoint(final PointF point, final MotionEvent event) {
        final float x = event.getX(0) + event.getX(1);
        final float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (imgBitmap != null && canvas != null) {
            canvas.drawBitmap(imgBitmap, matrix, background);
        }
    }

    @Override
    protected void onSizeChanged(final int width, final int height, final int oldWidth, final int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        // Reset the width and height. Will draw bitmap and change
        containerWidth = width;
        containerHeight = height;

        if (imgBitmap != null) {
            final int imgHeight = imgBitmap.getHeight();
            final int imgWidth = imgBitmap.getWidth();

            float scale;
            int initX = 0;
            int initY = 0;

            if (defaultScale == ZoomableImageView.DEFAULT_SCALE_FIT_INSIDE) {
                if (imgWidth > containerWidth) {
                    scale = (float) containerWidth / imgWidth;
                    final float newHeight = imgHeight * scale;
                    initY = (containerHeight - (int) newHeight) / 2;

                    matrix.setScale(scale, scale);
                    matrix.postTranslate(0, initY);
                } else {
                    scale = (float) containerHeight / imgHeight;
                    final float newWidth = imgWidth * scale;
                    initX = (containerWidth - (int) newWidth) / 2;

                    matrix.setScale(scale, scale);
                    matrix.postTranslate(initX, 0);
                }

                curX = initX;
                curY = initY;

                currentScale = scale;
                minScale = scale;
            } else if (defaultScale == ZoomableImageView.DEFAULT_SCALE_FIT_HEIGHT) {
                scale = (float) containerHeight / imgHeight;
                final float newWidth = imgWidth * scale;
                initX = (containerWidth - (int) newWidth) / 2;

                matrix.setScale(scale, scale);
                matrix.postTranslate(initX, 0);

                curX = initX;
                curY = initY;

                currentScale = scale;
                minScale = scale / MAX_ZOOM_OUT;
                maxScale = scale * MAX_ZOOM_IN;
            } else {
                if (imgWidth > containerWidth) {
                    initY = (containerHeight - imgHeight) / 2;
                    matrix.postTranslate(0, initY);
                } else {
                    initX = (containerWidth - imgWidth) / 2;
                    matrix.postTranslate(initX, 0);
                }

                curX = initX;
                curY = initY;

                currentScale = 1.0f;
                minScale = 1.0f;
            }

            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }

        if (isAnimating) {
            return true;
        }

        // Handle touch events here
        final float[] mvals = new float[9];
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            if (!isAnimating) {
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
            }
            break;

        case MotionEvent.ACTION_POINTER_DOWN:
            oldDist = spacing(event);
            if (oldDist > 10f) {
                savedMatrix.set(matrix);
                midPoint(mid, event);
                mode = ZOOM;
            }
            break;

        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
            mode = NONE;

            matrix.getValues(mvals);
            curX = mvals[2];
            curY = mvals[5];
            currentScale = mvals[0];

            if (!isAnimating) {
                checkImageConstraints();
            }
            break;

        case MotionEvent.ACTION_MOVE:
            if (mode == DRAG && !isAnimating) {
                matrix.set(savedMatrix);
                final float diffX = event.getX() - start.x;
                final float diffY = event.getY() - start.y;

                matrix.postTranslate(diffX, diffY);

                matrix.getValues(mvals);
                curX = mvals[2];
                curY = mvals[5];
                currentScale = mvals[0];
            } else if (mode == ZOOM && !isAnimating) {
                final float newDist = spacing(event);
                if (newDist > 10f) {
                    matrix.set(savedMatrix);
                    final float scale = newDist / oldDist;
                    matrix.getValues(mvals);
                    currentScale = mvals[0];

                    if (currentScale * scale <= minScale) {
                        matrix.postScale(minScale / currentScale, minScale / currentScale, mid.x, mid.y);
                    } else if (currentScale * scale >= maxScale) {
                        matrix.postScale(maxScale / currentScale, maxScale / currentScale, mid.x, mid.y);
                    } else {
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }

                    matrix.getValues(mvals);
                    curX = mvals[2];
                    curY = mvals[5];
                    currentScale = mvals[0];
                }
            }

            break;
        }

        // Calculate the transformations and then invalidate
        invalidate();
        return true;
    }

    public void setBitmap(final Bitmap b) {
        if (b != null) {
            imgBitmap = b;

            containerWidth = getWidth();
            containerHeight = getHeight();

            final int imgHeight = imgBitmap.getHeight();
            final int imgWidth = imgBitmap.getWidth();

            float scale;
            int initX = 0;
            int initY = 0;

            matrix.reset();

            if (defaultScale == ZoomableImageView.DEFAULT_SCALE_FIT_INSIDE) {
                if (imgWidth > containerWidth) {
                    scale = (float) containerWidth / imgWidth;
                    final float newHeight = imgHeight * scale;
                    initY = (containerHeight - (int) newHeight) / 2;

                    matrix.setScale(scale, scale);
                    matrix.postTranslate(0, initY);
                } else {
                    scale = (float) containerHeight / imgHeight;
                    final float newWidth = imgWidth * scale;
                    initX = (containerWidth - (int) newWidth) / 2;

                    matrix.setScale(scale, scale);
                    matrix.postTranslate(initX, 0);
                }

                curX = initX;
                curY = initY;

                currentScale = scale;
                minScale = scale;
            } else {
                if (imgWidth > containerWidth) {
                    initX = 0;
                    if (imgHeight > containerHeight) {
                        initY = 0;
                    } else {
                        initY = (containerHeight - imgHeight) / 2;
                    }

                    matrix.postTranslate(0, initY);
                } else {
                    initX = (containerWidth - imgWidth) / 2;
                    if (imgHeight > containerHeight) {
                        initY = 0;
                    } else {
                        initY = (containerHeight - imgHeight) / 2;
                    }
                    matrix.postTranslate(initX, 0);
                }

                curX = initX;
                curY = initY;

                currentScale = 1.0f;
                minScale = 1.0f;
            }

            invalidate();
        }
    }

    public void setDefaultScale(final int defaultScale) {
        this.defaultScale = defaultScale;
    }

    private float spacing(final MotionEvent event) {
        final float x = event.getX(0) - event.getX(1);
        final float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }
}
