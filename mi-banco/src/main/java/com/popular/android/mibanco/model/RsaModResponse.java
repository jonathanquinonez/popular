package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RsaModResponse extends BaseResponse implements Serializable {

    @SerializedName("result")
    private String result;

    @SerializedName("message")
    private String message;

    @SerializedName("messageError")
    private String messageError;

    @SerializedName("oobEnrolled")
    private boolean oobEnrolled;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageError() {
        return messageError;
    }

    public void setMessageError(String messageError) {
        this.messageError = messageError;
    }

    public boolean isOobEnrolled() {
        return oobEnrolled;
    }

    public void setOobEnrolled(boolean oobEnrolled) {
        this.oobEnrolled = oobEnrolled;
    }
}
