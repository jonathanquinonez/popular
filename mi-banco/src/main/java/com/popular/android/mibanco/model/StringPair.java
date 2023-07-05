package com.popular.android.mibanco.model;

import java.io.Serializable;

/**
 * Class that contains key value pairs
 */
public class StringPair implements Serializable {

    private static final long serialVersionUID = -6869776303469984001L;
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name;
    }
}
