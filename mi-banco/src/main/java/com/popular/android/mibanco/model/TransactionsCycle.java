package com.popular.android.mibanco.model;

import android.util.Log;

import com.popular.android.mibanco.object.ListItemSelectable;

/**
 * Class that represents a transaction cycle
 */
public class TransactionsCycle extends ListItemSelectable {

    private String cycle;

    private String description;

    private String endBalance;

    private String endDate;

    private String frontEndId;

    private String premia;

    private String startDate;

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public String getContent() {
        return null;
    }

    public String getCycle() {
        return cycle;
    }

    public String getDescription() {
        return description;
    }

    public String getEndBalance() {
        return endBalance;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getFrontEndId() {
        return frontEndId;
    }

    @Override
    public int getId() {
        try {
            return Integer.parseInt(cycle);
        } catch (final Exception ex) {
            Log.w("TransactionCycle", ex);
            return 1;
        }
    }

    public String getPremia() {
        return premia;
    }

    public String getStartDate() {
        return startDate;
    }

    @Override
    public String getTitle() {
        return getDescription();
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }

}
