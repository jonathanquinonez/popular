package com.popular.android.mibanco.view.pickerview;

public class ArrayWheelItem {

    private int id;
    private String name;

    public ArrayWheelItem(final int mId, final String mName) {
        id = mId;
        name = mName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

}
