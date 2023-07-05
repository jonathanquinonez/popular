package com.popular.android.mibanco.model;

public enum RedemptionType {
    statementCredit("STATEMENT_CREDIT"), directDeposit("DIRECT_DEPOSIT");

    private final String value;

    RedemptionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
