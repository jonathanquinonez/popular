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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.popular.android.mibanco.R;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The simple Array wheel adapter
 * 
 * @param <T> the element type
 */
public class ArrayBankWheelAdapter<T> extends AbstractWheelAdapter {

    protected Context context;

    private int currentItem;

    private boolean hasFiller;

    private final Map<String, WeakReference<Bitmap>> imagesCache = new HashMap<String, WeakReference<Bitmap>>();

    private LayoutInflater inflater;

    private List<ArrayBankWheelItem> items;

    private int layout;

    private int layoutHeight;

    private int layoutWidth;

    /**
     * Constructor
     * 
     * @param context the current context
     * @param mItems the items
     */
    public ArrayBankWheelAdapter(final Context context, final List<ArrayBankWheelItem> mItems) {
        this(context, R.layout.wheel_item, mItems);
    }

    public ArrayBankWheelAdapter(final Context context, final int layout, final List<ArrayBankWheelItem> mItems) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.layout = layout;
        this.items = mItems;
    }

    private Bitmap getBitmap(final String path) {
        if (imagesCache.get(path) != null) {
            if (imagesCache.get(path).get() != null) {
                return imagesCache.get(path).get();
            }
        }

        final Bitmap temp = loadBitmap(path);
        imagesCache.put(path, new WeakReference<Bitmap>(temp));
        return temp;
    }

    public int getCurrentItem() {
        return currentItem;
    }

    public boolean getFiller() {
        return hasFiller;
    }

    @Override
    public View getItem(final int index, final View convertView, final ViewGroup parent) {
        View myConvertView = convertView;
        if (index >= 0 && index < getItemsCount()) {
            if (myConvertView == null) {
                myConvertView = inflater.inflate(layout, parent, false);
            }

            final ImageView accountImage = (ImageView) myConvertView.findViewById(R.id.account_image);
            // image views have fixed dimensions
            if (accountImage.getMeasuredHeight() > 0 && accountImage.getMeasuredWidth() > 0) {
                layoutWidth = accountImage.getMeasuredWidth();
                layoutHeight = accountImage.getMeasuredHeight();
            }
            if (accountImage != null) {
                if (!TextUtils.isEmpty(items.get(index).getImgPath())) {
                    Bitmap imageBitmap = getBitmap(items.get(index).getImgPath());
                    if (imageBitmap == null) {
                        accountImage.setImageResource(items.get(index).getImgResource());
                    } else {
                        accountImage.setImageBitmap(imageBitmap);
                    }
                } else {
                    accountImage.setImageResource(items.get(index).getImgResource());
                }
            }

            final TextView title = (TextView) myConvertView.findViewById(R.id.item_name);
            if (title != null) {
                String text = items.get(index).getName();
                if (text == null) {
                    text = "";
                }
                title.setText(text);
            }

            final TextView balance = (TextView) myConvertView.findViewById(R.id.item_balance);
            if (balance != null) {
                String text = items.get(index).getAmount();
                if (text == null) {
                    text = "";
                }
                balance.setText(text);
                if (items.get(index).isBalanceRed()) {
                    balance.setTextColor(ContextCompat.getColor(context, R.color.accounts_credit_balance));
                }
            }

            final TextView last4num = (TextView) myConvertView.findViewById(R.id.item_last4num);
            if (last4num != null) {
                String text = items.get(index).getCode();
                if (text == null) {
                    text = "";
                }
                last4num.setText(text);
            }

            if (index == 0 && hasFiller) {
                myConvertView.findViewById(R.id.filler).setVisibility(View.INVISIBLE);
            } else if (!hasFiller) {
                myConvertView.findViewById(R.id.filler).setVisibility(View.GONE);
            }

            if (index == currentItem) {
                myConvertView.findViewById(R.id.opacity_layer).setVisibility(View.INVISIBLE);
            } else {
                myConvertView.findViewById(R.id.opacity_layer).setVisibility(View.VISIBLE);
            }

            return myConvertView;
        }

        return null;
    }

    public ArrayBankWheelItem getItemAt(final int index) {
        if (index >= 0 && index < getItemsCount()) {
            return items.get(index);
        }
        return null;
    }

    public String getItemId(final int index) {
        return items.get(index).getId();
    }

    @Override
    public int getItemsCount() {
        return items.size();
    }

    private Bitmap loadBitmap(final String path) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.outHeight = layoutWidth;
        options.outWidth = layoutHeight;
        return BitmapFactory.decodeFile(path, options);
    }

    public void setCurrentItem(final int currentItem) {
        this.currentItem = currentItem;
    }

    public void setFiller(final boolean filler) {
        this.hasFiller = filler;
    }

}
