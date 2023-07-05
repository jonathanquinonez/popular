package com.popular.android.mibanco.model;

/**
 * Class that represents a transfer account
 */
public class TransferAccount {

    class TransferAccountProperties {

        protected String accountBalance;

        protected String accountLast4Num;

        protected String accountName;
    }

    protected TransferAccountProperties properties;

    protected String text;

    protected String value;

    public String getAccountBalance() {
        if (properties.accountBalance == null) {
            return "";
        }
        return properties.accountBalance;
    }

    public String getAccountId() {
        return value;
    }

    public String getAccountLast4Num() {
        return properties.accountLast4Num;
    }

    public String getAccountName() {
        return properties.accountName;
    }

    public String getAccountTitle() {
        return text;
    }
}
