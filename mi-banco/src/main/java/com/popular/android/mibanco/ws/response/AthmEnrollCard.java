package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.SerializedName;
import com.popular.android.mibanco.model.AccountCard;
import com.popular.android.mibanco.model.StringPair;

import java.io.Serializable;
import java.util.ArrayList;

public class AthmEnrollCard extends AthmResponse implements Serializable {

    private static final long serialVersionUID = 4170931411505594665L;
    private AthmEnrollCardContent content;

    public ArrayList<AccountCard> getAvailableAccounts() {
        return content.availableAccounts;
    }

    public void setAvailableAccounts(ArrayList<AccountCard> availableAccounts) {
        content.availableAccounts = availableAccounts;
    }

    public ArrayList<StringPair> getExpirationMonths() {
        return content.expirationMonths;
    }

    public void setExpirationMonths(ArrayList<StringPair> expirationMonths) {
        content.expirationMonths = expirationMonths;
    }

    public ArrayList<StringPair> getExpirationYears() {
        return content.expirationYears;
    }

    public void setExpirationYears(ArrayList<StringPair> expirationYears) {
        content.expirationYears = expirationYears;
    }

    protected class AthmEnrollCardContent implements Serializable {

        private static final long serialVersionUID = 3546711724781673475L;
        @SerializedName("available_accounts")
        private ArrayList<AccountCard> availableAccounts;

        @SerializedName("expiration_months")
        private ArrayList<StringPair> expirationMonths;

        @SerializedName("expiration_years")
        private ArrayList<StringPair> expirationYears;
    }
}
