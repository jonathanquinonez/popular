package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;
import com.popular.android.mibanco.util.KiuwanUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Class that represents a Payment response
 */
public class Payment extends BaseResponse {

    private PaymentContent content;

    private boolean downtime;

    private PaymentForm form;

    public ArrayList<PaymentAccount> getAccountsFrom() {
        try {
            return form.fields.accountsFrom.values;
        } catch (Error e) {
            return null;
        }
    }

    public ArrayList<PaymentAccount> getAccountsTo() {
        try {
            return form.fields.accountsTo.values;
        } catch (Error e) {
            return null;
        }
    }

    /**
     * getEffectiveDate
     * @return Date
     */
    public Date getEffectiveDate() {
        return KiuwanUtils.getDate(form.fields.effectiveDate.value);
    }

    /**
     * getRealTimeEffectiveDate
     * @return Date
     */
    public Date getRealTimeEffectiveDate() {
        return KiuwanUtils.getDate(form.fields.effectiveDateRealTime != null
                ? form.fields.effectiveDateRealTime.value : StringUtils.EMPTY);
    }

    public String getResponderName() {
        return responderName;
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
}

/**
 * Class that represents payment account properties
 */
class PaymentAccountProperties {

    /**
     * RT_NOTIFICATION Serialized Name
     */
    private static final String RT_NOTIFICATION = "rtNotification"; // rtNotification Serialized Name

    /**
     * RT_HAS_PAYMENT_HISTORY Serialized Name
     */
    private static final String RT_HAS_PAYMENT_HISTORY = "rtHasPaymentHistory"; // rtHasPaymentHistory Serialized Name

    protected String accountBalance;

    protected String accountLast4Num;

    protected String accountName;

    protected String globalPayeeId;

    @SerializedName(RT_NOTIFICATION)
    protected String rtNotification = StringUtils.EMPTY; // real time payee

    @SerializedName(RT_HAS_PAYMENT_HISTORY)
    protected String rtHasPaymentHistory = StringUtils.EMPTY; // payee has payment history
}

/**
 * Class that represents payment accounts
 */
class PaymentAccounts {

    protected ArrayList<PaymentAccount> values;
}

/**
 * Class that represents payment content
 */
class PaymentContent {

    protected String page;

    protected String todayDay;

    protected String todayMonth;

    protected String todayYear;
}

/**
 * Class that represents the payment effective date
 */
class PaymentEffectiveDate {

    protected String value;
}

/**
 * Class that contains the payment form fields
 */
class PaymentForm {

    protected PaymentFormFields fields;
}

/**
 * Class that represents the payment form fields
 */
class PaymentFormFields {

    private final static String EFFECTIVE_DATE_REAL_TIME = "effectiveDateRealTime"; // JSON Key Map

    @SerializedName("quickpayment.accountFrom")
    protected PaymentAccounts accountsFrom;

    @SerializedName("quickpayment.payeeid")
    protected PaymentAccounts accountsTo;

    protected PaymentEffectiveDate effectiveDate;

    @SerializedName(EFFECTIVE_DATE_REAL_TIME)
    protected PaymentEffectiveDate effectiveDateRealTime; // Effective Date For Real Time Payments
}
