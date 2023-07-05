package com.popular.android.mibanco.util;

/**
 * Created by ET55498 on 3/8/17.
 */

public enum EnrollmentLiteStatus {

    ENROLL_COMPLETE(200),
    CUSTOMER_EMAIL_FOUND(201),
    CUSTOMER_PHONE_FOUND(202),
    DISPLAY_TERMS_AND_CONDITIONS(203),
    SMS_SERVICE_REQUESTED(204),
    NO_CUSTOMER_PHONE_FOUND(205),
    SMS_RESEND_LIMIT(206),
    VALIDATION_SUCCESS(207),
    CUSTOMER_LITE_FOUND(208),
    FINGERPRINT_VALIDATION_SUCCESS(209),
    DEVICE_REGISTRATION_SUCCESS(210),
    DEVICE_UPDATE_SUCCESS(211),
    MISSING_INFO(400),
    INVALID_SMS_CODE(401),
    SMS_SERVICE_FAILED(402),
    BACKEND_ERROR(403),
    NO_SESSION_ERROR (404),
    SMS_ERROR_LIMIT_REACHED (405),
    VALIDATION_FAILED(406),
    CUSTOMER_PHONE_NOT_FOUND(407),
    CUSTOMER_DEVICE_NOT_FOUND(408),
    COUNTRY_MATCH(409),
    NAME_MATCH(410),
    PHONE_IN_BLACKLIST(411),
    FINGERPRINT_DELAY_FAILED(412),
    FINGERPRINT_NOT_REGISTERED(413),
    FINGERPRINT_DISABLED(414),
    DEVICE_REGISTRATION_FAILED(415),
    DEVICE_UPDATE_FAILED(416);

    private int code;
    private int stringId;

    EnrollmentLiteStatus(int code){
        this.code = code;
    }
    EnrollmentLiteStatus(int code, int stringId){
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
