package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.SerializedName;
import com.popular.android.mibanco.model.BaseResponse;

import java.io.Serializable;

public class UnicaUrl extends BaseResponse implements Serializable {

    private static final long serialVersionUID = -4629439575689768292L;//Serial Version long value

    private UnicaUrlContent content;

    public String getUrl() {
        return content.url;
    }

    public String getAuthCode() {
        return content.tkn;
    }

    protected static class UnicaUrlContent implements Serializable {

        private static final long serialVersionUID = -8627090580103480899L;//Serial Version long value

        @SerializedName("url")
        private String url;

        @SerializedName("token")
        private String tkn;

    }
}
