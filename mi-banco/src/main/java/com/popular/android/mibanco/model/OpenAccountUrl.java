package com.popular.android.mibanco.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OpenAccountUrl extends BaseResponse implements Serializable {


    private String downtime;
    private String pageTitle;
    private String goback;
    private OpenAccountContentUrl content;


    public class OpenAccountContentUrl implements Serializable {
        private String token;
        private String opacURL;
        @SerializedName("URL")
        private String url;
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

        public String getUrl() {
            return url;
        }

        public void setUrl(String URL) {
            this.url = URL;
        }

        public String getFlagOpac() {
            return flagOpac;
        }

        public void setFlagOpac(String flagOpac) {
            this.flagOpac = flagOpac;
        }
    }
    public OpenAccountContentUrl getContent() {
        return content;
    }

    public void setContent(OpenAccountContentUrl content) {
        this.content = content;
    }
    public String getDowntime() {
        return downtime;
    }

    public void setDowntime(String downtime) {
        this.downtime = downtime;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getGoback() {
        return goback;
    }

    public void setGoback(String goback) {
        this.goback = goback;
    }
}
