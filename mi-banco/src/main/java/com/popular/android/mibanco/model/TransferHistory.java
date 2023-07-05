package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Object that represents the Transfer History
 */
public class TransferHistory extends BaseResponse implements Serializable {

    private static final long serialVersionUID = -4842888622413739138L;

    private TransferHistoryContent content;

    protected class TransferHistoryContent implements Serializable {

        private static final long serialVersionUID = -6731136053171823711L;

        private String totalInProcess;

        @SerializedName("in_process")
        private ArrayList<TransferHistoryEntry> inProcess;

        private ArrayList<TransferHistoryEntry> history;

        @SerializedName("available_periods")
        private AvailablePeriods availablePeriods;

        @SerializedName("available_accounts")
        private AvailableAccounts availableAccounts;

    }

    /**
     * Internal class that represents available accounts
     */
    public class AvailableAccounts implements Serializable {

        private static final long serialVersionUID = 6169594879672319681L;

        @SerializedName("default_label")
        private String defaultLabel;

        @SerializedName("default_value")
        private String defaultValue;

        private String action;

        private ArrayList<TransferHistoryEntry> accounts;

        public String getDefaultLabel() {
            return defaultLabel;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public String getAction() {
            return action;
        }

        public ArrayList<TransferHistoryEntry> getAccounts() {
            return accounts;
        }
    }

    public String getTotalInProcess() {
        return content.totalInProcess;
    }

    public ArrayList<TransferHistoryEntry> getInProcess() {
        return content.inProcess;
    }

    public ArrayList<TransferHistoryEntry> getHistory() {
        return content.history;
    }

    public AvailablePeriods getAvailablePeriods() {
        return content.availablePeriods;
    }

    public AvailableAccounts getAvailableAccounts() {
        return content.availableAccounts;
    }
}
