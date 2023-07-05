package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;

public class MarketplaceImages {

    @SerializedName("identifier")
    private String identifier;

    @SerializedName("image_url")
    private String image_url;

    @SerializedName("action_url")
    private String action_url;

    @SerializedName("desc")
    private String desc;

    public MarketplaceImages(){
    }

    public String getId() {
        return identifier;
    }

    public void setId(String id) {
        this.identifier = id;
    }

    public String getUrlImage() {
        return image_url;
    }

    public void setUrlImage(String image_url) {
        this.image_url = image_url;
    }

    public String getAction() {
        return action_url;
    }

    public void setAction(String action_url) {
        this.action_url = action_url;
    }

    public String getDescription() {
        return desc;
    }

    public void setDescription(String desc) {
        this.desc = desc;
    }
}