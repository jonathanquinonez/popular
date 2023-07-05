package com.popular.android.mibanco.model;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by ET55498 on 3/8/17.
 */

public class Country implements Serializable {


    private static final long serialVersionUID = -3835109286891934122L;
    private Locale locale;

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getCountryName(){
        return locale.getDisplayCountry();
    }

    public String getCountryCode(){
        return locale.getCountry();
    }

    @Override
    public String toString() {
        return locale.getDisplayCountry();
    }



}
