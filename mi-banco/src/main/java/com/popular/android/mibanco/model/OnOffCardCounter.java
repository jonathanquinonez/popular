package com.popular.android.mibanco.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OnOffCardCounter implements Serializable {

    private static final long serialVersionUID = 1;

    @SerializedName("frontEndId")
    @Expose
    private String frontEndId;

    @SerializedName("onOffCount")
    @Expose
    private int onOffCount;

    public String getFrontEndId() {
        return frontEndId;
    }

    public void setPlasticFrontEndId(String frontEndId) {
        this.frontEndId = frontEndId;
    }

    public int getOnOffCount() {
        return onOffCount;
    }

    public void setOnOffCount(int onOffCount) {
        this.onOffCount = onOffCount;
    }
}
