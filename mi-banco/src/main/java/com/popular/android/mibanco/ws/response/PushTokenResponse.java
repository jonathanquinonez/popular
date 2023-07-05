package com.popular.android.mibanco.ws.response;

import com.popular.android.mibanco.model.BaseResponse;

import java.io.Serializable;

public class PushTokenResponse extends BaseResponse implements Serializable {

    private static final long serialVersionUID = -3337438786369987371L;
    public static final String DISABLE = "DISABLE";
    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";
    public static final String RESPONDER_NAME = "pushTokenResponse";

    private PushTokenResponseContent content;


    public String getStatus(){return content.status;}

    protected class PushTokenResponseContent implements Serializable {

        private static final long serialVersionUID = 3534818052767912769L;
        private String status;
    }
}
