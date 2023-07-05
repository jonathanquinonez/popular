package com.popular.android.mibanco.model;

/**
 * Class that represents an Account
 */
public class Account {

    private String payeeNickname;
    private String payeeAccountLast4Num;
    private String payeeId;
    private String globalPayeeId;

    public String getPayeeNickname() {
        return payeeNickname;
    }

    public String getPayeeAccountLast4Num() {
        return payeeAccountLast4Num;
    }

    public String getPayeeId() {
        return payeeId;
    }

    public String getGlobalPayeeId() {
        return globalPayeeId;
    }
}
