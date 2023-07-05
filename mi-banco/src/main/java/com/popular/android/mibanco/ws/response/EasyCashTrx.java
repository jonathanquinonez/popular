
package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EasyCashTrx implements Serializable
{

    @SerializedName("responder_name")
    @Expose
    private String responder_name;
    @SerializedName("responder_message")
    @Expose
    private String responder_message;
    @SerializedName("content")
    @Expose
    private Content content;
    private final static long serialVersionUID = 8437177209519889949L;

    public String getResponder_name() {
        return responder_name;
    }

    public void setResponder_name(String responder_name) {
        this.responder_name = responder_name;
    }

    public String getResponder_message() {
        return responder_message;
    }

    public void setResponder_message(String responder_message) {
        this.responder_message = responder_message;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

}
