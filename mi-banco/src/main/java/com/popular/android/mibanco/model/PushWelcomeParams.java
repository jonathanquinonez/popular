package com.popular.android.mibanco.model;

import java.io.Serializable;

/**
 * Object that represents the Push Welcome Splash Data
 */
public class PushWelcomeParams extends BaseResponse implements Serializable {

    private String pushSplashSmsMessage;

    private String pushSplashNonSmsMessage;

    private String language;


    public void setLanguage(String language) {
        this.language = language;
    }

    public void setSmsMessage(String pushSplashSmsMessage) {
        this.pushSplashSmsMessage = pushSplashSmsMessage;
    }

    public void setNonSmsMessage(String pushSplashNonSmsMessage) {
        this.pushSplashNonSmsMessage = pushSplashNonSmsMessage;
    }


    public String getLanguage() {
        return this.language;
    }

    public String getSmsMessage() {
        return this.pushSplashSmsMessage;
    }

    public String getNonSmsMessage() {
        return this.pushSplashNonSmsMessage;
    }

}
