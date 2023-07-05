package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AthmEnrollPhoneCode extends AthmResponse implements Serializable {

    private static final long serialVersionUID = -5240741140280840452L;
    private AthmEnrollPhoneCodeContent content;

    public String getConfirmationCode() {
        return content.confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        content.confirmationCode = confirmationCode;
    }

    protected class AthmEnrollPhoneCodeContent implements Serializable {

        private static final long serialVersionUID = 6967740844449267394L;
        @SerializedName("confirmation_code")
        private String confirmationCode;
    }
}
