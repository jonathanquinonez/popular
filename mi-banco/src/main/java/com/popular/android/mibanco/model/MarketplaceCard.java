package com.popular.android.mibanco.model;

import android.graphics.Bitmap;

public class MarketplaceCard {

    String type;
    String title;
    String subtitle;
    String buttonText;
    String urlImage;
    Bitmap image;

    public MarketplaceCard(String type, String title, String subtitle, String buttonText, String urlImage, Bitmap image) {
        this.type = type;
        this.title = title;
        this.subtitle = subtitle;
        this.buttonText = buttonText;
        this.urlImage = urlImage;
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subtitle;
    }

    public String getButtonText() {
        return buttonText;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public Bitmap getImageBitmap() {
        return image;
    }

    public void setImageBitmap(Bitmap image) {
        this.image = image;
    }
}