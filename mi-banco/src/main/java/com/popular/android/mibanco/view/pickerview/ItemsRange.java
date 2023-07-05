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

/**
 * Range for visible items.
 */
public class ItemsRange {
    // Items count
    private final int count;

    // First item number
    private final int first;

    /**
     * Default constructor. Creates an empty range
     */
    public ItemsRange() {
        this(0, 0);
    }

    /**
     * Constructor
     * 
     * @param first the number of first item
     * @param count the count of items
     */
    public ItemsRange(final int first, final int count) {
        this.first = first;
        this.count = count;
    }

    /**
     * Tests whether item is contained by range
     * 
     * @param index the item number
     * @return true if item is contained
     */
    public boolean contains(final int index) {
        return index >= getFirst() && index <= getLast();
    }

    /**
     * Get items count
     * 
     * @return the count of items
     */
    public int getCount() {
        return count;
    }

    /**
     * Gets number of first item
     * 
     * @return the number of the first item
     */
    public int getFirst() {
        return first;
    }

    /**
     * Gets number of last item
     * 
     * @return the number of last item
     */
    public int getLast() {
        return getFirst() + getCount() - 1;
    }
}
