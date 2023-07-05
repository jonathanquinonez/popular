package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.SerializedName;
import com.popular.android.mibanco.model.BaseResponse;

import java.io.Serializable;

/**
 * Class that represents Marketplace Terms response
 */
public class MarketPlaceTermsResponse extends BaseResponse implements Serializable {

    private static final long serialVersionUID = -4629439575689768292L;//Serial Version long value

    private MarketPlaceTermsContent content;

    public boolean getSuccess() {
        return content.success;
    }

    protected static class MarketPlaceTermsContent implements Serializable {

        private static final long serialVersionUID = -8627090580103480899L;//Serial Version long value
        @SerializedName("success")
        private boolean success;//Log AcceptTerms successful

    }
}