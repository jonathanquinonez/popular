package com.popular.android.mibanco.model;

import java.util.HashMap;

/**
 * Class that represents form data
 */
public class FormData {

    protected String action;

    protected HashMap<String, FormField> fields;

    protected String method;

    protected String name;

    public String getAction() {
        return action;
    }

    public HashMap<String, FormField> getFields() {
        return fields;
    }

    public String getMethod() {
        return method;
    }

    public String getName() {
        return name;
    }
}
