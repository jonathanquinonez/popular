package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Carousel implements Serializable {
    @SerializedName("name")
    private String name;
    @SerializedName("action_url")
    private String action_url;
    @SerializedName("image_url")
    private String image_url;

    // Constructor
    public Carousel(String name, String action_url, String image_url) {
        this.name = name;
        this.action_url = action_url;
        this.image_url = image_url;
    }

    // Getters y Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAction_url() {
        return action_url;
    }

    public void setAction_url(String action_url) {
        this.action_url = action_url;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
