package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.SerializedName;
import com.popular.android.mibanco.model.BaseResponse;

import java.io.Serializable;

/**
 * Object that represents a lite enrollment services response
 * Created by ET55498 on 3/9/17.
 */
public class MobilePhoneInAlertsResponse extends BaseResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private MobilePhoneInAlertsContent content;

    public String getAlertsPhoneNumber(){return content.alertsPhoneNumber;}

    protected class MobilePhoneInAlertsContent implements Serializable {

        private static final long serialVersionUID = 1L;
        @SerializedName("alertsPhoneNumber")
        private String alertsPhoneNumber;

    }
}
