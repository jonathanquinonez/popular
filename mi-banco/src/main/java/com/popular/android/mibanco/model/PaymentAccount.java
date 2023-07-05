package com.popular.android.mibanco.model;

import com.popular.android.mibanco.util.Utils;

/**
 * Class that represents a Payment Account
 */
public class PaymentAccount {

    protected PaymentAccountProperties properties;

    protected String text;

    protected String value;

    /**
     * getAccountBalance
     * @return String
     */
    public String getAccountBalance() {
        if (properties.accountBalance == null) {
            return "";
        }
        return properties.accountBalance;
    }

    /**
     * getAccountId
     * @return String
     */
    public String getAccountId() {
        return value;
    }

    /**
     * getAccountLast4Num
     * @return String
     */
    public String getAccountLast4Num() {
        return properties.accountLast4Num;
    }

    /**
     * getAccountName
     * @return String
     */
    public String getAccountName() {
        return properties.accountName;
    }

    /**
     * getAccountTitle
     * @return String
     */
    public String getAccountTitle() {
        return text;
    }

    /**
     * getGlobalPayeeId
     * @return int
     */
    public int getGlobalPayeeId() {
        return Utils.getValidGlobalPayeeId(properties.globalPayeeId);
    }

    /**
     * getRtNotification
     * @return String
     */
    public String getRtNotification() {
        return properties.rtNotification;
    }

    /**
     * getRtHasPaymentHistory
     * @return String
     */
    public String getRtHasPaymentHistory() {
        return properties.rtHasPaymentHistory;
    }
}
