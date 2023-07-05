package com.popular.android.mibanco.view.pickerview;

import android.graphics.drawable.Drawable;

public class ArrayImgWheelItem {

    private String id;
    private Drawable img;

    public ArrayImgWheelItem(final String mId, final Drawable mImg) {
        id = mId;
        img = mImg;
    }

    public Drawable getDrawable() {
        return img;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setName(final Drawable img) {
        this.img = img;
    }

}
