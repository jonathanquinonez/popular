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

import java.util.ArrayList;

/**
 * The simple Array wheel adapter.
 * 
 * @param <T> the element type
 */
public class ArrayImgWheelAdapter<T> extends AbstractWheelImgAdapter {

    // items
    private final ArrayList<ArrayImgWheelItem> items;

    /**
     * Constructor
     * 
     * @param context the current context
     * @param mItems the items
     */
    public ArrayImgWheelAdapter(final Context context, final ArrayList<ArrayImgWheelItem> mItems) {
        super(context);
        this.items = mItems;
    }

    public ArrayImgWheelAdapter(final Context context, final int layout, final int resource, final ArrayList<ArrayImgWheelItem> mItems) {
        super(context, layout, resource);
        this.items = mItems;
    }

    public String getItemId(final int index) {
        return items.get(index).getId();
    }

    @Override
    public Drawable getItemImg(final int index) {
        return items.get(index).getDrawable();
    }

    @Override
    public int getItemsCount() {
        return items.size();
    }
}
