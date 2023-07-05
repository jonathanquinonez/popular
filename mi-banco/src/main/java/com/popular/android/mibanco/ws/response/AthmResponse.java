package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.SerializedName;
import com.popular.android.mibanco.model.BaseResponse;

import java.io.Serializable;

public class AthmResponse extends BaseResponse implements Serializable {

    private static final long serialVersionUID = 4546551717714064788L;
    private boolean downtime;
    private boolean blocked;

    @SerializedName("alert_error")
    private boolean alertError;

    @SerializedName("alert_message")
    private String alertMessage;

    public boolean isDowntime() {
        return downtime;
    }

    public void setDowntime(boolean downtime) {
        this.downtime = downtime;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isAlertError() {
        return alertError;
    }

    public void setAlertError(boolean alertError) {
        this.alertError = alertError;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }
}
