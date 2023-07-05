package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.SerializedName;
import com.popular.android.mibanco.model.BaseResponse;

import java.io.Serializable;

/**
 * Class that represents ValidateOtpCode response
 */
public class ValidateOtpCode extends BaseResponse implements Serializable {

    private static final long serialVersionUID = -4629439575689768292L;//Serial Version long value

    private ValidateOtpCodeContent content;

    public String getStatus() {
        return content.status;
    }

    public String getError() {
        return content.error;
    }

    protected static class ValidateOtpCodeContent implements Serializable {

        private static final long serialVersionUID = -8627090580103480899L;//Serial Version long value

        @SerializedName("status")
        private String status;

        @SerializedName("error")
        private String error;

    }
}