package com.popular.android.mibanco.object;

public class ProductItem {

    private Integer image;
    private String name;
    private int value;

    public ProductItem(final Integer image, final String name, final int value) {
        this.image = image;
        this.name = name;
        this.value = value;
    }

    public Integer getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public void setImage(final Integer image) {
        this.image = image;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setValue(final int value) {
        this.value = value;
    }

}
