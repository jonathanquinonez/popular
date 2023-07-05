
package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Content implements Serializable
{

    @SerializedName("blackListStatus")
    @Expose
    private String blackListStatus;
    @SerializedName("easyOfacStatus")
    @Expose
    private String easyOfacStatus;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("transactions")
    @Expose
    private List<MobileCashTrx> transactions = null;
    @SerializedName("history")
    @Expose
    private List<MobileCashTrx> history = null;
    private final static long serialVersionUID = -2131204098522845570L;

    public String getBlackListStatus() {
        return blackListStatus;
    }

    public void setBlackListStatus(String blackListStatus) {
        this.blackListStatus = blackListStatus;
    }

    public String getEasyOfacStatus() {
        return easyOfacStatus;
    }

    public void setEasyOfacStatus(String easyOfacStatus) {
        this.easyOfacStatus = easyOfacStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<MobileCashTrx> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<MobileCashTrx> transactions) {
        this.transactions = transactions;
    }

    public List<MobileCashTrx> getHistory() {
        return history;
    }

    public void setHistory(List<MobileCashTrx> history) {
        this.history = history;
    }

}
