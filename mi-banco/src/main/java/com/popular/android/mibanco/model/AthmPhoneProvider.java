package com.popular.android.mibanco.model;

import java.io.Serializable;

/**
 * Class that represents an ATH Movil phone provider
 */
public class AthmPhoneProvider implements Serializable {

    private static final long serialVersionUID = 1;

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
