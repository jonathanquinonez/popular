package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;

/**
 * Class that represents a transaction
 */
public class AccountTransaction extends BaseFormResponse {

    private String amount;

    private String description;

    private boolean isDebit;

    @SerializedName("posted_date")
    private String postedDate;

    private String sign;

    private String type;

    private String traceId;

    private String showDetailEnabled;

    public String getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public String getSign() {
        return sign;
    }

    public String getType() {
        return type;
    }

    public boolean getIsDebit() {
        return isDebit;
    }

    public void SetDebit(final boolean isDebit) {
        this.isDebit = isDebit;
    }

    public void setPostedDate(final String date) {
        postedDate = date;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getShowDetailEnabled() {
        return showDetailEnabled;
    }

    public void setShowDetailEnabled(String showDetailEnabled) {
        this.showDetailEnabled = showDetailEnabled;
    }
}
