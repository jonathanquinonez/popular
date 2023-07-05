package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class AvailablePeriods implements Serializable {

    private static final long serialVersionUID = -1616463169671752964L;

    @SerializedName("default_label")
    private String defaultLabel;

    @SerializedName("default_value")
    private String defaultValue;

    private ArrayList<Month> months;

    private String action;

    public String getDefaultLabel() {
        return defaultLabel;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public ArrayList<Month> getMonths() {
        return months;
    }

    public String getAction() {
        return action;
    }
}
