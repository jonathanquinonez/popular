package com.popular.android.mibanco.object;

public class TransactionItem {

    private String name;
    private int value;

    public TransactionItem(final String name, final int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setValue(final int value) {
        this.value = value;
    }

}
