package com.popular.android.mibanco.model;

/**
 * Class that represents an active transfer
 */
public class TransferActiveTransfer {

    protected String amount;

    protected String effectiveDate;

    protected String error;

    protected TransferActiveAccount fromAccount;

    protected String referenceNumber;

    protected TransferActiveAccount toAccount;

    public String getAmount() {
        return amount;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public String getError() {
        return error;
    }

    public TransferActiveAccount getFromAccount() {
        return fromAccount;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public TransferActiveAccount getToAccount() {
        return toAccount;
    }
}
