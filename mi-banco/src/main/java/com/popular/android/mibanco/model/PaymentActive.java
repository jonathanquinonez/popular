package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;

/**
 * Class that represents a payment active
 */
public class PaymentActive extends BaseFormResponse {

    private PaymentActiveContent content;

    private boolean downtime;

    private PaymentActiveFlags flags;

    public String getAmount() {
        return content.amount;
    }

    public PaymentActiveContent getContent() {
        return content;
    }

    public String getEffectiveDate() {
        return content.effectiveDate;
    }

    public String getEffectiveDateFormat() {
        return content.effectiveDateFormat;
    }

    @Override
    public String getError() {
        return error;
    }

    public PaymentActiveFlags getFlags() {
        return flags;
    }

    public String getPayeeBillingAccount() {
        return content.payeeBillingAccount;
    }

    public String getPayeeNickname() {
        return content.payeeNickname;
    }

    public String getPaymentMessage() {
        return content.paymentMessage;
    }

    public String getReferenceNumber() {
        return content.referenceNumber;
    }

    public String getSourceAccountNickname() {
        return content.sourceAccountNickname;
    }

    public String getSourceAccountNumber() {
        return content.sourceAccountNumber;
    }

    public String getTodayDay() {
        return content.todayDay;
    }

    public String getTodayMonth() {
        return content.todayMonth;
    }

    public String getTodayYear() {
        return content.todayYear;
    }

    public boolean isDowntime() {
        return downtime;
    }

    public boolean isPaymentSent() {
        return flags.paymentSent;
    }

    public boolean isPaymentTotalGreaterThanBalance() {
        return flags.paymentTotalGreaterThanBalance;
    }

    public boolean isShowAlertTotal() {
        return flags.showAlertTotal;
    }
}

/**
 * Class that represents payment active content
 */
class PaymentActiveContent {

    @SerializedName("payment.amount")
    protected String amount;

    @SerializedName("payment.effectiveDate")
    protected String effectiveDate;

    @SerializedName("payment.effectiveDate.format")
    protected String effectiveDateFormat;

    protected String error;

    @SerializedName("payment.payeeBillingAccount")
    protected String payeeBillingAccount;

    @SerializedName("payment.payee.nickname")
    protected String payeeNickname;

    @SerializedName("payment.paymentMessage")
    protected String paymentMessage;

    @SerializedName("payment.referenceNumber")
    protected String referenceNumber;

    @SerializedName("payment.source.account.nickname")
    protected String sourceAccountNickname;

    @SerializedName("payment.source.account.accountNumber")
    protected String sourceAccountNumber;

    protected String todayDay;

    protected String todayMonth;

    protected String todayYear;
}

/**
 * Class that represents the payment active flags response
 */
class PaymentActiveFlags {

    @SerializedName("payment_sent")
    boolean paymentSent;

    @SerializedName("payment_total_greater_than_balance")
    boolean paymentTotalGreaterThanBalance;

    @SerializedName("show_alert_total")
    boolean showAlertTotal;
}
