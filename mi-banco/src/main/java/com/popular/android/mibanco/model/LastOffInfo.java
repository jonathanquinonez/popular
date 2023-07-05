package com.popular.android.mibanco.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LastOffInfo implements Serializable {

    private static final long serialVersionUID = -509279203602864779L;
    @SerializedName("applicationName")
    @Expose
    private String applicationName;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("userFullName")
    @Expose
    private String userFullName;

    @SerializedName("userId")
    @Expose
    private String userId;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
