package com.popular.android.mibanco.model;

/**
 * Class that represents a card dictionary
 */
public class CardDict {

    private final String desc;

    private final String img;

    public CardDict(final String image, final String description) {
        img = image;
        desc = description;
    }

    public String getDesc() {
        return desc;
    }

    public String getImg() {
        return img;
    }

}
