package com.popular.android.mibanco.util;

public enum EnumStatusResponses {

    NEW_STATUS_OFF_RESPONSE_FAILED(417),
    NEW_STATUS_ON_RESPONSE_FAILED(418),
    INQUIRY_RESPONSE_FAILED(419),
    NEW_STATUS_RESPONSE_FAILED(420),
    NEW_STATUS_OFF_RESPONSE_SUCCESS(213),
    NEW_STATUS_ON_RESPONSE_SUCCESS(214),
    INQUIRY_RESPONSE_SUCCESS(215),
    NEW_STATUS_RESPONSE_SUCCESS(216);

    private int code;
    private int stringId;

    EnumStatusResponses(int code){
        this.code = code;
    }
    EnumStatusResponses(int code, int stringId){
        this.code = code;
        this.stringId = stringId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getStringId() {
        return stringId;
    }

    public void setStringId(int stringId) {
        this.stringId = stringId;
    }

}
