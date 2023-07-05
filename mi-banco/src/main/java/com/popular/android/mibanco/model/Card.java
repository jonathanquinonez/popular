package com.popular.android.mibanco.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Card {

    @SerializedName("lastOffInfo")
    @Expose
    private LastOffInfo lastOffInfo;
    @SerializedName("apiAccountKey")
    @Expose
    private String apiAccountKey;
    @SerializedName("status")
    @Expose
    private String status;

    public LastOffInfo getLastOffInfo() {return lastOffInfo;}

    public String getApiAccountKey(){return apiAccountKey;}

    public String getStatus(){return apiAccountKey;}
}