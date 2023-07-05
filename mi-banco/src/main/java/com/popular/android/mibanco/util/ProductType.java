package com.popular.android.mibanco.util;

/**
 * Created by et55498 on 5/17/2017.
 */

import java.io.Serializable;

public enum ProductType implements Serializable {
    FINGERPRINT, WIDGET, CASHDROP;

    public static Object fromString(String string) {
        return ProductType.valueOf(string);
    }

    public String toString(Object o) {
        return o.toString();
    }
}