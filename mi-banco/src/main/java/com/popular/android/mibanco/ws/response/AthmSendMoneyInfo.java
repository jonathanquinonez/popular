package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.SerializedName;
import com.popular.android.mibanco.model.AccountCard;

import java.io.Serializable;

public class AthmSendMoneyInfo extends AthmResponse implements Serializable {

    private static final long serialVersionUID = 5349666238348628929L;
    private AthmSendMoneyInfoContent content;

    public String getServiceCharge() {
        return content.serviceCharge;
    }

    public void setServiceCharge(String serviceCharge) {
        content.serviceCharge = serviceCharge;
    }

    public AccountCard getFromAccount() {
        return content.fromAccount;
    }

    public void setFromAccount(AccountCard fromAccount) {
        content.fromAccount = fromAccount;
    }

    protected class AthmSendMoneyInfoContent implements Serializable {

        private static final long serialVersionUID = 4757141789359981070L;
        @SerializedName("service_charge")
        private String serviceCharge;

        @SerializedName("from_account")
        private AccountCard fromAccount;
    }
}
