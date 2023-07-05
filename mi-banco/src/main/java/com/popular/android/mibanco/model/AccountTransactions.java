package com.popular.android.mibanco.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Class that represents accounts transactions
 */
public class AccountTransactions extends BaseFormResponse {

    private TransactionsContent content;

    private ArrayList<AccountTransaction> currentTransactions;

    private ArrayList<AccountTransaction> inProcessTransactions;

    public ArrayList<AccountTransaction> getAllCurrentTransactions() {
        return currentTransactions;
    }

    public ArrayList<AccountTransaction> getAllInProcessTransactions() {
        return inProcessTransactions;
    }

    public ArrayList<TransactionsCycle> getAvailibleCycles() {
        if (content == null) {
            return null;
        }
        return content.availableCycles;
    }

    public int getCurrentCycle() {
        try {
            return Integer.parseInt(content.cycle);
        } catch (final Exception ex) {
            Log.w("AccountTransactions", ex);
            return 1;
        }
    }

    public int getCurrentPage() {
        try {
            return Integer.parseInt(content.currentPage);
        } catch (final Exception ex) {
            Log.w("AccountTransactions", ex);
            return 1;
        }
    }

    public TransactionsCycle getCycle(final int cycleNr) {
        return getCycle(Integer.toString(cycleNr));
    }

    public TransactionsCycle getCycle(final String cycleNr) {
        TransactionsCycle cycle = null;
        if (content != null && content.availableCycles != null) {
            int i = 0;
            while (cycle == null && i < content.availableCycles.size()) {
                if (content.availableCycles.get(i).getCycle().equals(cycleNr)) {
                    cycle = content.availableCycles.get(i);
                }
                i++;
            }
        }
        return cycle;
    }

    public String getDateFormat() {
        if(content == null || content.dateFormat == null){
            return "MM/dd/yyyy";
        }
        return content.dateFormat;
    }

    public int getTotalPages() {
        try {
            return Integer.parseInt(content.totalPages);
        } catch (final Exception ex) {
            Log.w("AccountTransactions", ex);
            return 1;
        }
    }
}

/**
 * Class that represents transactions contents
 */
class TransactionsContent {

    @SerializedName("available_cycles")
    protected ArrayList<TransactionsCycle> availableCycles;

    protected String currentPage;

    protected String cycle;

    protected String cycleDescription;

    @SerializedName("date_format")
    protected String dateFormat;
    @SerializedName("in_process_type")
    protected String inProcessType;
    @SerializedName("total_in_process")
    protected String totalInProcess;
    protected String totalPages;
}
