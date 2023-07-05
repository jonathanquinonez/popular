package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.SerializedName;
import com.popular.android.mibanco.model.BaseResponse;

import java.io.Serializable;

/**
 * Class that represents GenerateOtpCode response
 */
public class GenerateOtpCode extends BaseResponse implements Serializable {

    private static final long serialVersionUID = -4629439575689768292L;//Serial Version long value

    private GenerateOtpCodeContent content;

    public String getStatus() {
        return content.status;
    }

    public String getError() {
        return content.error;
    }

    protected static class GenerateOtpCodeContent implements Serializable {

        private static final long serialVersionUID = -8627090580103480899L;//Serial Version long value

        @SerializedName("status")
        private String status;

        @SerializedName("error")
        private String error;

    }
}