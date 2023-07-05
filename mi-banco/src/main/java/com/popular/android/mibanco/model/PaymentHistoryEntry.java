package com.popular.android.mibanco.model;

import android.text.TextUtils;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;

import java.io.Serializable;

/**
 * Class that represents a Payment History entry
 */
public class PaymentHistoryEntry implements Serializable {

    public PaymentHistoryEntry(String payeeNickname, String frontEndId) {
        this.payeeNickname = payeeNickname;
        this.frontEndId = frontEndId;
    }

    private static final long serialVersionUID = -2413471920979354285L;

    private String payeeNickname;
    private String payeeAccountLast4Num;
    private String globalPayeeId;

    private String sourceApiAccountKey;
    private String sourceNickname;
    private String sourceAccountLast4Num;
    private String sourceAccountNumberSuffix;
    private String sourceAccountSection;
    private String sourceSubtype;

    private String effectiveDate;
    private String estimateDate;
    private String amount;
    private String frequency;
    private String frontEndId;
    private String referenceNumber;
    private String status;
    private String statusCode;

    private String favId;

    private Payee payee;

    /**
     * Class that represents a Payee
     */
    public class Payee implements Serializable {

        private static final long serialVersionUID = 1L;

        public String getGlobalId() {
            return getGlobalPayeeId();
        }

        public String getNickname() {
            return getPayeeNickname();
        }

        public String getFrontEndID() {
            return getFrontEndId();
        }

        public String getAccountLast4Num() {
            return getPayeeAccountLast4Num();
        }
    }

    public String getPayeeNickname() {
        return payeeNickname;
    }

    public String getPayeeAccountLast4Num() {
        return payeeAccountLast4Num;
    }

    public String getGlobalPayeeId() {
        return globalPayeeId;
    }

    public String getSourceApiAccountKey() {
        return sourceApiAccountKey;
    }

    public String getSourceNickname() {
        return sourceNickname;
    }

    public String getSourceAccountLast4Num() {
        return sourceAccountLast4Num;
    }

    public String getSourceAccountNumberSuffix() {
        return sourceAccountNumberSuffix;
    }

    public String getSourceAccountSection() {
        return sourceAccountSection;
    }

    public String getSourceSubtype() {
        return sourceSubtype;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public String getEstimateDate() {
        return estimateDate;
    }

    public String getAmount() {
        return amount;
    }

    public String getFrontEndId() {
        return frontEndId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getFavId() {
        return favId;
    }

    public Payee getPayee() {
        if (payee == null) {
            payee = new Payee();
        }
        return payee;
    }

    public String getStatus() {
        return TextUtils.isEmpty(status) ? App.getApplicationInstance().getString(R.string.status_processing) : status;
    }

    public String getStatusCode() {
        return TextUtils.isEmpty(statusCode) ? MiBancoConstants.TRANSACTION_STATUS_CODE_IN_PROCESS : statusCode;
    }
}
