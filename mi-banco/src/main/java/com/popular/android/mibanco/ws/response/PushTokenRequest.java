package com.popular.android.mibanco.ws.response;

import android.content.Context;
import android.text.Html;

import com.popular.android.mibanco.model.BaseResponse;
import com.popular.android.mibanco.util.PushUtils;

import java.io.Serializable;

public class PushTokenRequest extends BaseResponse implements Serializable {

    private static final long serialVersionUID = -4036166704443117592L;
    private String pushToken;
    private String deviceName;
    private String deviceModel;
    private String deviceType;
    private String invalidPushToken;
    private boolean hasPermissions;
    private boolean isActive;

    public PushTokenRequest(Context context) {
        this.deviceName = Html.fromHtml(PushUtils.getDeviceName(context)).toString().trim();
        this.deviceModel = PushUtils.getDeviceModel();
        this.deviceType = PushUtils.ANDROID;
        this.hasPermissions = PushUtils.areNotificationsEnabled(context);
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getDeviceName() {
        return deviceName;
    }


    public String getDeviceModel() {
        return deviceModel;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getInvalidPushToken() {
        return invalidPushToken;
    }

    public void setInvalidPushToken(String invalidPushToken) {
        this.invalidPushToken = invalidPushToken;
    }

    public boolean HasPermissions() {
        return hasPermissions;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
