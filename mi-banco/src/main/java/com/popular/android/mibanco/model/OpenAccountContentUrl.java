package com.popular.android.mibanco.model;


import java.io.Serializable;

public class OpenAccountContentUrl extends BaseResponse implements Serializable {
    private String token;
    private String opacURL;
    private String URL;
    private String flagOpac;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOpacURL() {
        return opacURL;
    }

    public void setOpacURL(String opacURL) {
        this.opacURL = opacURL;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getFlagOpac() {
        return flagOpac;
    }

    public void setFlagOpac(String flagOpac) {
        this.flagOpac = flagOpac;
    }

}
