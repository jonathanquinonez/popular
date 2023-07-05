package com.popular.android.mibanco.model;

import java.io.Serializable;

/**
 * Class that represents a Month
 */
public class Month implements Serializable {

    private static final long serialVersionUID = -7339256501527338133L;

    private String month;
    private String year;
    private String value;

    public Month(String month, String year, String value) {
        this.month = month;
        this.year = year;
        this.value = value;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public String getValue() {
        return value;
    }
}
