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
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Abstract wheel adapter provides common functionality for adapters.
 */
public abstract class AbstractWheelBankAdapter extends AbstractWheelAdapter {

    /** Text view resource. Used as a default view for adapter. */
    private static final int IMG_VIEW_ITEM_RESOURCE = -1;

    /** No resource constant. */
    private static final int NO_RESOURCE = 0;

    // Current context
    private Context context;
    // Empty items resources
    private int emptyItemResourceId;

    // Layout inflater
    private LayoutInflater inflater;
    private int itemImgResourceId;

    // Items resources
    private int itemResourceId;

    /**
     * Constructor
     * 
     * @param context the current context
     */
    protected AbstractWheelBankAdapter(final Context context) {
        this(context, IMG_VIEW_ITEM_RESOURCE);
    }

    /**
     * Constructor
     * 
     * @param context the current context
     * @param itemResource the resource ID for a layout file containing a TextView to use when instantiating items views
     */
    private AbstractWheelBankAdapter(final Context context, final int itemResource) {
        this(context, itemResource, NO_RESOURCE);
    }

    /**
     * Constructor
     * 
     * @param context the current context
     * @param itemResource the resource ID for a layout file containing a TextView to use when instantiating items views
     * @param itemImgResource the resource ID for a text view in the item layout
     */
    private AbstractWheelBankAdapter(final Context context, final int itemResource, final int itemImgResource) {
        this.context = context;
        itemResourceId = itemResource;
        itemImgResourceId = itemImgResource;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getEmptyItem(final View convertView, final ViewGroup parent) {
        View myConvertView = convertView;
        if (myConvertView == null) {
            myConvertView = getView(emptyItemResourceId, parent);
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

    /**
     * Loads a text view from view
     * 
     * @param view the text view or layout containing it
     * @param imgResource the text resource Id in layout
     * @return the loaded text view
     */
    private ImageView getImgView(final View view, final int imgResource) {
        ImageView img = null;
        try {
            if (imgResource == NO_RESOURCE && view instanceof ImageView) {
                img = (ImageView) view;
            } else if (imgResource != NO_RESOURCE) {
                img = (ImageView) view.findViewById(imgResource);
            }
        } catch (final ClassCastException e) {
            Log.e("AbstractWheelAdapter", "You must supply a resource ID for a ImageView");
            throw new IllegalStateException("AbstractWheelAdapter requires the resource ID to be a ImageView", e);
        }

        return img;
    }

    @Override
    public View getItem(final int index, final View convertView, final ViewGroup parent) {
        View myConvertView = convertView;
        if (index >= 0 && index < getItemsCount()) {
            if (myConvertView == null) {
                myConvertView = getView(itemResourceId, parent);
            }
            final ImageView imgView = getImgView(myConvertView, itemImgResourceId);
            if (imgView != null) {
                imgView.setImageDrawable(getItemImg(index));
            }
            return myConvertView;
        }
        return null;
    }

    /**
     * Returns images for specified item
     * 
     * @param index the item index
     * @return the images of specified items
     */
    protected abstract Drawable getItemImg(int index);

    /**
     * Gets resource Id for image view in item layout
     * 
     * @return the item text resource Id
     */
    public int getItemImgResource() {
        return itemImgResourceId;
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
     * Loads view from resources
     * 
     * @param resource the resource Id
     * @return the loaded view or null if resource is not set
     */
    private View getView(final int resource, final ViewGroup parent) {
        switch (resource) {
        case NO_RESOURCE:
            return null;
        case IMG_VIEW_ITEM_RESOURCE:
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
     * Sets resource Id for image view in item layout
     * 
     * @param itemImgResourceId the item text resource Id to set
     */
    public void setItemImgResource(final int itemImgResourceId) {
        this.itemImgResourceId = itemImgResourceId;
    }

    /**
     * Sets resource Id for items views
     * 
     * @param itemResourceId the resource Id to set
     */
    public void setItemResource(final int itemResourceId) {
        this.itemResourceId = itemResourceId;
    }
}
