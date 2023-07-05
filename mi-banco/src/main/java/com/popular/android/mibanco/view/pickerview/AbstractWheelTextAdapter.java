/*
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
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Abstract wheel adapter provides common functionality for adapters.
 */
public abstract class AbstractWheelTextAdapter extends AbstractWheelAdapter {

    /** Default text color */
    public static final int DEFAULT_TEXT_COLOR = 0xFF101010;

    /** Default text size */
    public static final int DEFAULT_TEXT_SIZE = 24;

    /** Default text color */
    public static final int LABEL_COLOR = 0xFF700070;

    /** No resource constant. */
    protected static final int NO_RESOURCE = 0;

    /** Text view resource. Used as a default view for adapter. */
    public static final int TEXT_VIEW_ITEM_RESOURCE = -1;

    // Current context
    protected Context context;
    // Empty items resources
    protected int emptyItemResourceId;

    // Layout inflater
    protected LayoutInflater inflater;
    // Items resources
    protected int itemResourceId;

    protected int itemTextResourceId;

    // Text settings
    private int textColor = DEFAULT_TEXT_COLOR;
    private int textSize = DEFAULT_TEXT_SIZE;

    /**
     * Constructor
     * 
     * @param context the current context
     */
    protected AbstractWheelTextAdapter(final Context context) {
        this(context, TEXT_VIEW_ITEM_RESOURCE);
    }

    /**
     * Constructor
     * 
     * @param context the current context
     * @param itemResource the resource ID for a layout file containing a TextView to use when instantiating items views
     */
    protected AbstractWheelTextAdapter(final Context context, final int itemResource) {
        this(context, itemResource, NO_RESOURCE);
    }

    /**
     * Constructor
     * 
     * @param context the current context
     * @param itemResource the resource ID for a layout file containing a TextView to use when instantiating items views
     * @param itemTextResource the resource ID for a text view in the item layout
     */
    protected AbstractWheelTextAdapter(final Context context, final int itemResource, final int itemTextResource) {
        this.context = context;
        itemResourceId = itemResource;
        itemTextResourceId = itemTextResource;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Configures text view. Is called for the TEXT_VIEW_ITEM_RESOURCE views.
     * 
     * @param view the text view to be configured
     */
    protected void configureTextView(final TextView view) {
        view.setTextColor(textColor);
        view.setGravity(Gravity.CENTER);
        view.setTextSize(textSize);
        view.setLines(1);
        view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
    }

    @Override
    public View getEmptyItem(final View convertView, final ViewGroup parent) {
        View myConvertView = convertView;
        if (myConvertView == null) {
            myConvertView = getView(emptyItemResourceId, parent);
        }
        if (emptyItemResourceId == TEXT_VIEW_ITEM_RESOURCE && myConvertView instanceof TextView) {
            configureTextView((TextView) myConvertView);
        }

        return myConvertView;
    }

    /**
     * Gets resource Id for empty items views
     * 
     * @return the empty item resource Id
     */
    public int getEmptyItemResource() {
        return emptyItemResourceId;
    }

    @Override
    public View getItem(final int index, final View convertView, final ViewGroup parent) {
        View myConvertView = convertView;
        if (index >= 0 && index < getItemsCount()) {
            if (myConvertView == null) {
                myConvertView = getView(itemResourceId, parent);
            }
            final TextView textView = getTextView(myConvertView, itemTextResourceId);
            if (textView != null) {
                CharSequence text = getItemText(index);
                if (text == null) {
                    text = "";
                }
                textView.setText(text);

                if (itemResourceId == TEXT_VIEW_ITEM_RESOURCE) {
                    configureTextView(textView);
                }
            }
            return myConvertView;
        }
        return null;
    }

    /**
     * Gets resource Id for items views
     * 
     * @return the item resource Id
     */
    public int getItemResource() {
        return itemResourceId;
    }

    /**
     * Returns text for specified item
     * 
     * @param index the item index
     * @return the text of specified items
     */
    protected abstract CharSequence getItemText(int index);

    /**
     * Gets resource Id for text view in item layout
     * 
     * @return the item text resource Id
     */
    public int getItemTextResource() {
        return itemTextResourceId;
    }

    /**
     * Gets text color
     * 
     * @return the text color
     */
    public int getTextColor() {
        return textColor;
    }

    /**
     * Gets text size
     * 
     * @return the text size
     */
    public int getTextSize() {
        return textSize;
    }

    /**
     * Loads a text view from view
     * 
     * @param view the text view or layout containing it
     * @param textResource the text resource Id in layout
     * @return the loaded text view
     */
    private TextView getTextView(final View view, final int textResource) {
        TextView text = null;
        try {
            if (textResource == NO_RESOURCE && view instanceof TextView) {
                text = (TextView) view;
            } else if (textResource != NO_RESOURCE) {
                text = (TextView) view.findViewById(textResource);
            }
        } catch (final ClassCastException e) {
            Log.e("AbstractWheelAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException("AbstractWheelAdapter requires the resource ID to be a TextView", e);
        }

        return text;
    }

    /**
     * Loads view from resources
     * 
     * @param resource the resource Id
     * @return the loaded view or null if resource is not set
     */
    private View getView(final int resource, final ViewGroup parent) {
        switch (resource) {
        case NO_RESOURCE:
            return null;
        case TEXT_VIEW_ITEM_RESOURCE:
            return new TextView(context);
        default:
            return inflater.inflate(resource, parent, false);
        }
    }

    /**
     * Sets resource Id for empty items views
     * 
     * @param emptyItemResourceId the empty item resource Id to set
     */
    public void setEmptyItemResource(final int emptyItemResourceId) {
        this.emptyItemResourceId = emptyItemResourceId;
    }

    /**
     * Sets resource Id for items views
     * 
     * @param itemResourceId the resource Id to set
     */
    public void setItemResource(final int itemResourceId) {
        this.itemResourceId = itemResourceId;
    }

    /**
     * Sets resource Id for text view in item layout
     * 
     * @param itemTextResourceId the item text resource Id to set
     */
    public void setItemTextResource(final int itemTextResourceId) {
        this.itemTextResourceId = itemTextResourceId;
    }

    /**
     * Sets text color
     * 
     * @param textColor the text color to set
     */
    public void setTextColor(final int textColor) {
        this.textColor = textColor;
    }

    /**
     * Sets text size
     * 
     * @param textSize the text size to set
     */
    public void setTextSize(final int textSize) {
        this.textSize = textSize;
    }
}
