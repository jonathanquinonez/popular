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

import java.util.ArrayList;

/**
 * The simple Array wheel adapter
 * 
 * @param <T> the element type
 */
public class ArrayWheelAdapter<T> extends AbstractWheelTextAdapter {

    // items
    private final ArrayList<ArrayWheelItem> items;

    /**
     * Constructor
     * 
     * @param context the current context
     * @param mItems the items
     */
    public ArrayWheelAdapter(final Context context, final ArrayList<ArrayWheelItem> mItems) {
        super(context);
        this.items = mItems;
    }

    public int getItemId(final int index) {
        return items.get(index).getId();
    }

    @Override
    public int getItemsCount() {
        return items.size();
    }

    @Override
    public CharSequence getItemText(final int index) {
        return items.get(index).getName();
    }
}
