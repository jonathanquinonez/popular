package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.SerializedName;
import com.popular.android.mibanco.model.BaseResponse;

import java.io.Serializable;

/**
 * Class that represents MarketplaceDeterminateChallenge response
 */
public class MarketplaceDeterminateChallenge extends BaseResponse implements Serializable {

    private static final long serialVersionUID = -4629439575689768292L;//Serial Version long value

    private MarketplaceDeterminateChallengeContent content;

    public String getAction() {
        return content.action;
    }

    protected static class MarketplaceDeterminateChallengeContent implements Serializable {

        private static final long serialVersionUID = -8627090580103480899L;//Serial Version long value

        @SerializedName("action")
        private String action;

    }
}