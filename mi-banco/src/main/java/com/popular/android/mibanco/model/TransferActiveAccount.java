package com.popular.android.mibanco.model;

/**
 * Class that represents an Active transfer account
 */
public class TransferActiveAccount {

    protected String accountNumber;

    protected String accountNumberSuffix;

    protected String nickname;

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountNumberSuffix() {
        return accountNumberSuffix;
    }

    public String getNickname() {
        return nickname;
    }
}
