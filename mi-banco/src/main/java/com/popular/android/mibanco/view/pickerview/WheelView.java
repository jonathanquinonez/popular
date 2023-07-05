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
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.popular.android.mibanco.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Numeric wheel view.
 * 
 * @author Yuri Kanivets
 */
public class WheelView extends View {

    /** Default count of visible items */
    private static final int DEF_VISIBLE_ITEMS = 2;

    /** Top and bottom items offset (to hide that) */
    private static final int ITEM_OFFSET_PERCENT = 0;

    /** Left and right padding value */
    private static final int PADDING = 0;

    /** Top and bottom shadows colors */
    private static final int[] SHADOWS_COLORS = new int[] { 0xFF777777, 0x00AAAAAA, 0x00AAAAAA };

    private GradientDrawable bottomShadow;
    // Center Line
    private Drawable centerDrawable;

    // Listeners
    private final List<OnWheelChangedListener> changingListeners = new LinkedList<OnWheelChangedListener>();

    private final List<OnWheelClickedListener> clickingListeners = new LinkedList<OnWheelClickedListener>();

    // Wheel Values
    private int currentItem;

    // Adapter listener
    private final DataSetObserver dataObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            invalidateWheel(false);
        }

        @Override
        public void onInvalidated() {
            invalidateWheel(true);
        }
    };

    // The number of first item in layout
    private int firstItem;

    // Cyclic
    boolean isCyclic;
    private boolean isScrollingPerformed;

    // Item height
    private int itemHeight;
    // Items layout
    private LinearLayout itemsLayout;
    int pos;

    // Recycle
    private final WheelRecycle recycle = new WheelRecycle(this);
    private int scrollBy = -1;
    // Scrolling
    private WheelScroller scroller;

    /** Scroll Listener */
    private WheelScrollListener wheelScrollListener; // Scroll Listener

    // Scrolling listener
    WheelScroller.ScrollingListener scrollingListener = new WheelScroller.ScrollingListener() {
        @Override
        public void onFinished() {
            if (isScrollingPerformed) {
                notifyScrollingListenersAboutEnd();
                isScrollingPerformed = false;
            }

            if (wheelScrollListener != null) {
                wheelScrollListener.stopScroll();
            }

            scrollingOffset = 0;
            invalidate();
        }

        @Override
        public void onJustify() {
            if (Math.abs(scrollingOffset) > WheelScroller.MIN_DELTA_FOR_SCROLLING) {
                scroller.scroll(scrollingOffset, 0);
            }
        }

        @Override
        public void onScroll(final int distance) {
            doScroll(distance);

            final int height = getHeight();
            if (scrollingOffset > height) {
                scrollingOffset = height;
                scroller.stopScrolling();
            } else if (scrollingOffset < -height) {
                scrollingOffset = -height;
                scroller.stopScrolling();
            }
        }

        @Override
        public void onStarted() {
            isScrollingPerformed = true;
            notifyScrollingListenersAboutStart();
        }
    };

    private final List<OnWheelScrollListener> scrollingListeners = new LinkedList<OnWheelScrollListener>();

    private int scrollingOffset;

    private Integer shaddowHeight;

    // Shadows drawables
    private GradientDrawable topShadow;
    // View adapter
    private WheelViewAdapter viewAdapter;

    // Count of visible items
    private int visibleItems = DEF_VISIBLE_ITEMS;

    private boolean withCD = true;

    /**
     * Constructor
     */
    public WheelView(final Context context) {
        super(context);
        initData(context);
        initAttributes(context, null);
    }

    /**
     * Constructor
     */
    public WheelView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initData(context);
        initAttributes(context, attrs);
    }

    /**
     * Constructor
     */
    public WheelView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        initData(context);
        initAttributes(context, attrs);
    }

    /**
     * Adds wheel changing listener
     * 
     * @param listener the listener
     */
    public void addChangingListener(final OnWheelChangedListener listener) {
        changingListeners.add(listener);
    }

    /**
     * Adds wheel event listener
     * @param wheelScrollListener the listener
     */
    public void setScrollEventListener(WheelScrollListener wheelScrollListener) {
        this.wheelScrollListener = wheelScrollListener;
    }

    /**
     * Adds wheel clicking listener
     * 
     * @param listener the listener
     */
    public void addClickingListener(final OnWheelClickedListener listener) {
        clickingListeners.add(listener);
    }

    /**
     * Adds wheel scrolling listener
     * 
     * @param listener the listener
     */
    public void addScrollingListener(final OnWheelScrollListener listener) {
        scrollingListeners.add(listener);
    }

    /**
     * Adds view for item to items layout
     * 
     * @param index the item index
     * @param first the flag indicates if view should be first
     * @return true if corresponding item exists and is added
     */
    private boolean addViewItem(final int index, final boolean first) {
        final View view = getItemView(index);
        if (view != null) {
            if (first) {
                itemsLayout.addView(view, 0);
            } else {
                itemsLayout.addView(view);
            }

            return true;
        }

        return false;
    }

    /**
     * Builds view for measuring
     */
    private void buildViewForMeasuring() {
        // clear all items
        if (itemsLayout != null) {
            recycle.recycleItems(itemsLayout, firstItem, new ItemsRange());
        } else {
            createItemsLayout();
        }

        // add views
        final int addItems = visibleItems / 2;
        for (int i = currentItem + addItems; i >= currentItem - addItems; i--) {
            if (addViewItem(i, true)) {
                firstItem = i;
            }
        }
    }

    /**
     * Calculates control width and creates text layouts
     * 
     * @param widthSize the input layout width
     * @param mode the layout mode
     * @return the calculated control width
     */
    private int calculateLayoutWidth(final int widthSize, final int mode) {
        initResourcesIfNecessary();

        // TODO: make it static
        itemsLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        itemsLayout.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int width = itemsLayout.getMeasuredWidth();

        if (mode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width += 2 * PADDING;

            // Check against our minimum width
            width = Math.max(width, getSuggestedMinimumWidth());

            if (mode == MeasureSpec.AT_MOST && widthSize < width) {
                width = widthSize;
            }
        }

        itemsLayout.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        return width;
    }

    /**
     * Creates item layouts if necessary
     */
    private void createItemsLayout() {
        if (itemsLayout == null) {
            itemsLayout = new LinearLayout(getContext());
            itemsLayout.setOrientation(LinearLayout.VERTICAL);
        }
    }

    /**
     * Scrolls the wheel
     * 
     * @param delta the scrolling value
     */
    private void doScroll(final int delta) {
        scrollingOffset += delta;

        final int itemHeight = getItemHeight();
        int count = scrollingOffset / itemHeight;

        int pos = currentItem - count;
        final int itemCount = viewAdapter.getItemsCount();

        int fixPos = scrollingOffset % itemHeight;
        if (Math.abs(fixPos) <= itemHeight / 2) {
            fixPos = 0;
        }
        if (isCyclic && itemCount > 0) {
            if (fixPos > 0) {
                pos--;
                count++;
            } else if (fixPos < 0) {
                pos++;
                count--;
            }
            // fix position by rotating
            while (pos < 0) {
                pos += itemCount;
            }
            pos %= itemCount;
        } else {
            //
            if (pos < 0) {
                count = currentItem;
                pos = 0;
            } else if (pos >= itemCount) {
                count = currentItem - itemCount + 1;
                pos = itemCount - 1;
            } else if (pos > 0 && fixPos > 0) {
                pos--;
                count++;
            } else if (pos < itemCount - 1 && fixPos < 0) {
                pos++;
                count--;
            }
        }

        final int offset = scrollingOffset;
        if (pos != currentItem) {
            setCurrentItem(pos, false);
        } else {
            invalidate();
        }

        // update offset
        scrollingOffset = offset - count * itemHeight;
        if (scrollingOffset > getHeight()) {
            scrollingOffset = scrollingOffset % getHeight() + getHeight();
        }
    }

    /**
     * Draws rect for current value
     * 
     * @param canvas the canvas for drawing
     */
    private void drawCenterRect(final Canvas canvas) {
        if (withCD) {
            final int center = getHeight() / 2;
            final int offset = (int) (getItemHeight() / 2 * 1.2);
            centerDrawable.setBounds(0, center - offset, getWidth(), center + offset);
            centerDrawable.draw(canvas);
        }
    }

    /**
     * Draws items
     * 
     * @param canvas the canvas for drawing
     */
    private void drawItems(final Canvas canvas) {
        canvas.save();

        // int top = (currentItem - firstItem) * getItemHeight() +
        // (getItemHeight() - getHeight()) / 2;
        final int top = (currentItem - firstItem) * getItemHeight();
        canvas.translate(PADDING, -top + scrollingOffset);

        itemsLayout.draw(canvas);

        canvas.restore();
    }

    /**
     * Draws shadows on top and bottom of control
     * 
     * @param canvas the canvas for drawing
     */
    private void drawShadows(final Canvas canvas) {
        final int height = shaddowHeight == null ? (int) (1.5 * getItemHeight()) : shaddowHeight;
        topShadow.setBounds(0, 0, getWidth(), height);
        topShadow.draw(canvas);

        bottomShadow.setBounds(0, getHeight() - height, getWidth(), getHeight());
        bottomShadow.draw(canvas);
    }

    /**
     * Gets current value
     * 
     * @return the current value
     */
    public int getCurrentItem() {
        return currentItem;
    }

    /**
     * Calculates desired height for layout
     * 
     * @param layout the source layout
     * @return the desired layout height
     */
    private int getDesiredHeight(final LinearLayout layout) {
        if (layout != null && layout.getChildAt(0) != null) {
            itemHeight = layout.getChildAt(0).getMeasuredHeight();
        }

        final int desired = itemHeight * visibleItems - itemHeight * ITEM_OFFSET_PERCENT / 50;

        return Math.max(desired, getSuggestedMinimumHeight());
    }

    /**
     * Returns height of wheel item
     * 
     * @return the item height
     */
    private int getItemHeight() {
        if (itemHeight != 0) {
            return itemHeight;
        }

        if (itemsLayout != null && itemsLayout.getChildAt(0) != null) {
            itemHeight = itemsLayout.getChildAt(0).getHeight();
            return itemHeight;
        }

        return getHeight() / visibleItems;
    }

    /**
     * Calculates range for wheel items
     * 
     * @return the items range
     */
    private ItemsRange getItemsRange() {
        if (getItemHeight() == 0) {
            return null;
        }

        int first = currentItem;
        int count = 1;

        while (count * getItemHeight() < getHeight()) {
            first--;
            // top + bottom items
            count += 2;
        }

        if (scrollingOffset != 0) {
            if (scrollingOffset > 0) {
                first--;
            }
            count++;

            // process empty items above the first or below the second
            final int emptyItems = scrollingOffset / getItemHeight();
            first -= emptyItems;
            count += Math.asin(emptyItems);
        }
        return new ItemsRange(first, count);
    }

    /**
     * Returns view for specified item
     * 
     * @param index the item index
     * @return item view or empty view if index is out of bounds
     */
    private View getItemView(int index) {
        if (viewAdapter == null || viewAdapter.getItemsCount() == 0) {
            return null;
        }
        final int count = viewAdapter.getItemsCount();
        if (!isValidItemIndex(index)) {
            return viewAdapter.getEmptyItem(recycle.getEmptyItem(), itemsLayout);
        } else {
            while (index < 0) {
                index = count + index;
            }
        }

        index %= count;
        return viewAdapter.getItem(index, recycle.getItem(), itemsLayout);
    }

    /**
     * Gets view adapter
     * 
     * @return the view adapter
     */
    public WheelViewAdapter getViewAdapter() {
        return viewAdapter;
    }

    /**
     * Gets count of visible items
     * 
     * @return the count of visible items
     */
    public int getVisibleItems() {
        return visibleItems;
    }

    private void initAttributes(final Context context, final AttributeSet attrs) {
        if (attrs != null) {
            final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
            shaddowHeight = typedArray.getInteger(R.styleable.WheelView_shaddowHeight, 0);
            typedArray.recycle();
        }
    }

    /**
     * Initializes class data
     * 
     * @param context the context
     */
    private void initData(final Context context) {
        scroller = new WheelScroller(getContext(), scrollingListener);
        if (scrollBy != -1) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    next();
                }
            }, scrollBy, scrollBy);
        }
    }

    /**
     * Initializes resources
     */
    private void initResourcesIfNecessary() {
        if (centerDrawable == null && withCD) {
            centerDrawable = ContextCompat.getDrawable(getContext(),R.drawable.wheel_val_bank);
        }

        if (topShadow == null) {
            topShadow = new GradientDrawable(Orientation.TOP_BOTTOM, SHADOWS_COLORS);
        }

        if (bottomShadow == null) {
            bottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP, SHADOWS_COLORS);
        }

        setBackgroundResource(R.drawable.wheel_bg_bank);
    }

    /**
     * Invalidates wheel
     * 
     * @param clearCaches if true then cached views will be clear
     */
    public void invalidateWheel(final boolean clearCaches) {
        if (clearCaches) {
            recycle.clearAll();
            if (itemsLayout != null) {
                itemsLayout.removeAllViews();
            }
            scrollingOffset = 0;
        } else if (itemsLayout != null) {
            // cache all items
            recycle.recycleItems(itemsLayout, firstItem, new ItemsRange());
        }

        invalidate();
    }

    /**
     * Tests if wheel is cyclic. That means before the 1st item there is shown the last one
     * 
     * @return true if wheel is cyclic
     */
    public boolean isCyclic() {
        return isCyclic;
    }

    /**
     * Checks whether intem index is valid
     * 
     * @param index the item index
     * @return true if item index is not out of bounds or the wheel is cyclic
     */
    private boolean isValidItemIndex(final int index) {
        return viewAdapter != null && viewAdapter.getItemsCount() > 0 && (isCyclic || index >= 0 && index < viewAdapter.getItemsCount());
    }

    /**
     * Sets layouts width and height
     * 
     * @param width the layout width
     * @param height the layout height
     */
    private void layout(final int width, final int height) {
        itemsLayout.layout(0, 0, width, height);
    }

    public void next() {
        setCurrentItem(currentItem + 1, true);
    }

    /**
     * Notifies changing listeners
     * 
     * @param oldValue the old wheel value
     * @param newValue the new wheel value
     */
    protected void notifyChangingListeners(final int oldValue, final int newValue) {
        for (final OnWheelChangedListener listener : changingListeners) {
            listener.onChanged(this, oldValue, newValue);
        }
    }

    /**
     * Notifies listeners about clicking
     */
    protected void notifyClickListenersAboutClick(final int item) {
        for (final OnWheelClickedListener listener : clickingListeners) {
            listener.onItemClicked(this, item);
        }
    }

    /**
     * Notifies listeners about ending scrolling
     */
    protected void notifyScrollingListenersAboutEnd() {
        for (final OnWheelScrollListener listener : scrollingListeners) {
            listener.onScrollingFinished(this);
        }
    }

    /**
     * Notifies listeners about starting scrolling
     */
    protected void notifyScrollingListenersAboutStart() {
        for (final OnWheelScrollListener listener : scrollingListeners) {
            listener.onScrollingStarted(this);
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        if (viewAdapter != null && viewAdapter.getItemsCount() > 0) {
            updateView();

            drawItems(canvas);
            drawCenterRect(canvas);
        }

        drawShadows(canvas);
    }

    @Override
    protected void onLayout(final boolean changed, final int l, final int t, final int r, final int b) {
        layout(r - l, b - t);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        buildViewForMeasuring();

        final int width = calculateLayoutWidth(widthSize, widthMode);

        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getDesiredHeight(itemsLayout);

            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }

        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {

        if (!isEnabled() || getViewAdapter() == null) {
            return true;
        }

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            pos = (int) event.getY();
            break;
        case MotionEvent.ACTION_MOVE:
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            break;

        case MotionEvent.ACTION_UP:
            if (Math.abs((int) event.getY() - pos) < 25 && isValidItemIndex(currentItem)) {
                notifyClickListenersAboutClick(currentItem);
            }
            break;
        }

        return scroller.onTouchEvent(event);
    }

    /**
     * Rebuilds wheel items if necessary. Caches all unused items.
     * 
     * @return true if items are rebuilt
     */
    private boolean rebuildItems() {
        boolean updated = false;
        final ItemsRange range = getItemsRange();
        if (itemsLayout != null) {
            final int first = recycle.recycleItems(itemsLayout, firstItem, range);
            updated = firstItem != first;
            firstItem = first;
        } else {
            createItemsLayout();
            updated = true;
        }

        if (!updated) {
            updated = firstItem != range.getFirst() || itemsLayout.getChildCount() != range.getCount();
        }

        if (firstItem > range.getFirst() && firstItem <= range.getLast()) {
            for (int i = firstItem - 1; i >= range.getFirst(); i--) {
                if (!addViewItem(i, true)) {
                    break;
                }
                firstItem = i;
            }
        } else {
            firstItem = range.getFirst();
        }

        int first = firstItem;
        for (int i = itemsLayout.getChildCount(); i < range.getCount(); i++) {
            if (!addViewItem(firstItem + i, false) && itemsLayout.getChildCount() == 0) {
                first++;
            }
        }
        firstItem = first;

        return updated;
    }

    /**
     * Removes wheel changing listener
     * 
     * @param listener the listener
     */
    public void removeChangingListener(final OnWheelChangedListener listener) {
        changingListeners.remove(listener);
    }

    /**
     * Removes wheel clicking listener
     * 
     * @param listener the listener
     */
    public void removeClickingListener(final OnWheelClickedListener listener) {
        clickingListeners.remove(listener);
    }

    /**
     * Removes wheel scrolling listener
     * 
     * @param listener the listener
     */
    public void removeScrollingListener(final OnWheelScrollListener listener) {
        scrollingListeners.remove(listener);
    }

    /**
     * Scroll the wheel
     * 
     * @param itemsToScroll items to scroll
     * @param time scrolling duration
     */
    public void scroll(final int itemsToScroll, final int time) {
        final int distance = itemsToScroll * getItemHeight() - scrollingOffset;
        scroller.scroll(distance, time);
    }

    public void setCD(final boolean cd) {
        withCD = cd;
    }

    /**
     * Sets the current item w/o animation. Does nothing when index is wrong.
     * 
     * @param index the item index
     */
    public void setCurrentItem(final int index) {
        setCurrentItem(index, false);
    }

    /**
     * Sets the current item. Does nothing when index is wrong.
     * 
     * @param index the item index
     * @param animated the animation flag
     */
    public void setCurrentItem(int index, final boolean animated) {
        if (viewAdapter == null || viewAdapter.getItemsCount() == 0) {
            // throw?
            return;
        }

        final int itemCount = viewAdapter.getItemsCount();
        if (index < 0 || index >= itemCount) {
            if (isCyclic) {
                while (index < 0) {
                    index += itemCount;
                }
                index %= itemCount;
            } else {
                // throw?
                return;
            }
        }
        if (index != currentItem) {
            if (animated) {
                int itemsToScroll = index - currentItem;
                if (isCyclic) {
                    final int scroll = itemCount + Math.min(index, currentItem) - Math.max(index, currentItem);
                    if (scroll < Math.abs(itemsToScroll)) {
                        itemsToScroll = itemsToScroll < 0 ? scroll : -scroll;
                    }
                }
                scroll(itemsToScroll, 0);
            } else {
                scrollingOffset = 0;

                final int old = currentItem;
                currentItem = index;

                notifyChangingListeners(old, currentItem);

                postInvalidate();
            }
        }
    }

    /**
     * Set wheel cyclic flag
     * 
     * @param isCyclic the flag to set
     */
    public void setCyclic(final boolean isCyclic) {
        this.isCyclic = isCyclic;
        invalidateWheel(false);
    }

    /**
     * Set the the specified scrolling interpolator
     * 
     * @param interpolator the interpolator
     */
    public void setInterpolator(final Interpolator interpolator) {
        scroller.setInterpolator(interpolator);
    }

    public void setScrillong(final int timeInMillis) {
        scrollBy = timeInMillis;
    }

    public void setShaddowHeight(final int height) {
        shaddowHeight = height;
    }

    /**
     * Sets view adapter. Usually new adapters contain different views, so it needs to rebuild view by calling measure().
     * 
     * @param viewAdapter the view adapter
     */
    public void setViewAdapter(final WheelViewAdapter viewAdapter) {
        if (this.viewAdapter != null) {
            this.viewAdapter.unregisterDataSetObserver(dataObserver);
        }
        this.viewAdapter = viewAdapter;
        if (this.viewAdapter != null) {
            this.viewAdapter.registerDataSetObserver(dataObserver);
        }

        invalidateWheel(true);
    }

    /**
     * Sets the desired count of visible items. Actual amount of visible items depends on wheel layout parameters. To apply changes and rebuild view
     * call measure().
     * 
     * @param count the desired count for visible items
     */
    public void setVisibleItems(final int count) {
        visibleItems = count;
    }

    /**
     * Stops scrolling
     */
    public void stopScrolling() {
        scroller.stopScrolling();
    }

    /**
     * Updates view. Rebuilds items and label if necessary, recalculate items sizes.
     */
    private void updateView() {
        if (rebuildItems()) {
            calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
            layout(getWidth(), getHeight());
        }
    }
}
