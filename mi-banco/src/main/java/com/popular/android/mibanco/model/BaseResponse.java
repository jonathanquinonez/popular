package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Class that represents a Base response
 */
public class BaseResponse implements Serializable {
    private static final long serialVersionUID = 1;

    @SerializedName("responder_name")
    protected String responderName;

    @SerializedName("responder_message")
    protected String responderMessage;

    protected String error;

    @SerializedName("status_message")
    private String statusMessage;

    @SerializedName("rsa_cookie")
    private String rsaCookie;

    private String responseBody;

    public String getError() {
        return error;
    }

    public String getResponderMessage() {
        return responderMessage;
    }


    public String getResponderName() {
        return responderName;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getRsaCookie() {
        return rsaCookie;
    }

    public void setRsaCookie(String rsaCookie) {
        this.rsaCookie = rsaCookie;
    }
}
