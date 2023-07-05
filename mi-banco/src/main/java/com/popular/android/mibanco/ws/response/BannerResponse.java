package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BannerResponse implements Serializable {

    private static final long serialVersionUID = -4629439575689768292L;//Serial Version long value
    @SerializedName("image_retina_url")
    private String image_retina_url;
    @SerializedName("action_url")
    private String action_url;
    @SerializedName("image_url")
    private String image_url;
    private List<Carousel> carousel = null;

    // Constructor
    public void Response(String image_retina_url, String action_url, String image_url, List<Carousel> carousel) {
        this.image_retina_url = image_retina_url;
        this.action_url = action_url;
        this.image_url = image_url;
        this.carousel = carousel;
    }

    // Getters y Setters
    public String getImage_retina_url() {
        return image_retina_url;
    }

    public void setImage_retina_url(String image_retina_url) {
        this.image_retina_url = image_retina_url;
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

    public List<Carousel> getCarousel() {
        return carousel;
    }

    public void setCarousel(List<Carousel> carousel) {
        this.carousel = carousel;
    }

}
