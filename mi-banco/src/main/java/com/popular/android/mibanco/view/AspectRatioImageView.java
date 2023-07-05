package com.popular.android.mibanco.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Display;
import android.widget.ImageView;

import com.popular.android.mibanco.R;

public class AspectRatioImageView extends ImageView {

    private int fixedHeight;

    private int minScreenHeight;

    public AspectRatioImageView(final Context context) {
        super(context);
    }

    public AspectRatioImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }

    public AspectRatioImageView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, context);
    }

    private void init(final AttributeSet attrs, final Context context) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView);
        minScreenHeight = (int) typedArray.getDimension(R.styleable.AspectRatioImageView_minScreenHeight, 0);
        fixedHeight = (int) typedArray.getDimension(R.styleable.AspectRatioImageView_fixedHeight, 0);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        final Drawable drawable = getDrawable();
        if (drawable != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            final int diw = drawable.getIntrinsicWidth();
            if (diw > 0) {
                int height;
                if (display.getHeight() < minScreenHeight) {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    height = fixedHeight;
                    width = display.getWidth();
                } else {
                    height = width * drawable.getIntrinsicHeight() / diw;
                }
                setMeasuredDimension(width, height);
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
