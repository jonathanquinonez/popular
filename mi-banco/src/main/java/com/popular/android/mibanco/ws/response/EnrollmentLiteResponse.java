package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.SerializedName;
import com.popular.android.mibanco.model.BaseResponse;

import java.io.Serializable;

/**
 * Object that represents a lite enrollment services response
 * Created by ET55498 on 3/9/17.
 */
public class EnrollmentLiteResponse extends BaseResponse implements Serializable {

    private static final long serialVersionUID = 2837523101836975151L;
    private EnrollmentLiteContent content;

    public int getStatus(){return content != null ? content.status : -1;}
    public boolean isError(){return content.error;}
    public String getAlertsPhoneNumber(){return content.alertsPhoneNumber;}

    protected class EnrollmentLiteContent implements Serializable {
        private static final long serialVersionUID = 1287600227668473107L;
        @SerializedName("status")
        private int status;

        @SerializedName("error")
        private boolean error;

        private String alertsPhoneNumber;

    }
}
