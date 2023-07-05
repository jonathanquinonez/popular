package com.popular.android.mibanco.model;

import android.text.TextUtils;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;

import java.io.Serializable;

/**
 * Class that represents transfer history entry
 */
public class TransferHistoryEntry implements Serializable {

    private static final long serialVersionUID = 9082933554780230567L;

    public TransferHistoryEntry(String targetNickname, String targetApiAccountKey, String targetAccountNumberSuffix, String targetAccountSection) {
        this.targetNickname = targetNickname;
        this.targetApiAccountKey = targetApiAccountKey;
        this.targetAccountNumberSuffix = targetAccountNumberSuffix;
        this.targetAccountSection = targetAccountSection;
    }

    private String sourceApiAccountKey;
    private String sourceNickname;
    private String sourceAccountLast4Num;
    private String sourceAccountNumberSuffix;
    private String sourceAccountSection;
    private String sourceSubtype;

    private String targetApiAccountKey;
    private String targetNickname;
    private String targetAccountLast4Num;
    private String targetAccountNumberSuffix;
    private String targetAccountSection;
    private String targetSubtype;

    private String effectiveDate;
    private String amount;
    private String frequency;
    private String frontEndId;
    private String referenceNumber;
    private String status;
    private String statusCode;

    private String favId;
    private String value;

    private Account account;

    /**
     * Internal class that represents an account
     */
    public class Account implements Serializable {

        private static final long serialVersionUID = 1L;

        public String getApiAccountKey() {
            return TransferHistoryEntry.this.getTargetApiAccountKey();
        }

        public String getNickname() {
            return TransferHistoryEntry.this.getTargetNickname();
        }

        public String getAccountLast4Num() {
            return TransferHistoryEntry.this.getTargetAccountLast4Num();
        }

        public String getAccountNumberSuffix() {
            return TransferHistoryEntry.this.getTargetAccountNumberSuffix();
        }

        public String getAccountSection() {
            return TransferHistoryEntry.this.getTargetAccountSection();
        }

        public String getValue() {
            return TransferHistoryEntry.this.getValue();
        }
    }


    public String getSourceNickname() {
        return sourceNickname;
    }

    public String getSourceAccountLast4Num() {
        return sourceAccountLast4Num;
    }

    public String getTargetApiAccountKey() {
        return targetApiAccountKey;
    }

    public String getTargetNickname() {
        return targetNickname;
    }

    public String getTargetAccountLast4Num() {
        return targetAccountLast4Num;
    }

    public String getTargetAccountNumberSuffix() {
        return targetAccountNumberSuffix;
    }

    public String getTargetAccountSection() {
        return targetAccountSection;
    }

    public String getEffectiveDate() {
        return effectiveDate;
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

    public String getValue() {
        return value;
    }

    public Account getAccount() {
        if (account == null) {
            account = new Account();
        }

        return account;
    }

    public String getStatus() {
        return TextUtils.isEmpty(status) ? App.getApplicationInstance().getString(R.string.status_processing) : status;
    }

    public String getStatusCode() {
        return TextUtils.isEmpty(statusCode) ? MiBancoConstants.TRANSACTION_STATUS_CODE_IN_PROCESS : statusCode;
    }
}
