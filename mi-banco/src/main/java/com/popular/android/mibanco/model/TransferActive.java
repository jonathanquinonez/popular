package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Class that represents a transfer active
 */
public class TransferActive extends BaseFormResponse {

    private TransferActiveContent content;

    private boolean downtime;

    private boolean errors;

    private TransferActiveFlags flags;

    @SerializedName("goback")
    private String goBack;

    @SerializedName("page_title")
    private String pageTitle;

    @SerializedName("unknown_page_error")
    private boolean unknownPageError;

    public ArrayList<TransferActiveTransfer> getConfirmedTransfers() {
        return content.confirmedTransfers;
    }

    public TransferActiveContent getContent() {
        return content;
    }

    public String getCurrentTransfer() {
        return content.currentTransfer;
    }

    public String getDateFormat() {
        return content.dateFormat;
    }

    public ArrayList<TransferActiveTransfer> getFailTransfers() {
        return content.failTransfers;
    }

    public TransferActiveFlags getFlags() {
        return flags;
    }

    public String getGoBack() {
        return goBack;
    }

    public String getNextYear() {
        return content.nextYear;
    }

    public String getPage() {
        return content.page;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getThisYear() {
        return content.thisYear;
    }

    public String getTodayDay() {
        return content.todayDay;
    }

    public String getTodayMonth() {
        return content.todayMonth;
    }

    public String getTodayYear() {
        return content.todayYear;
    }

    public String getTotalFail() {
        return content.totalFail;
    }

    public ArrayList<TransferActiveTransfer> getTransfers() {
        return content.transfers;
    }

    public boolean isDowntime() {
        return downtime;
    }

    public boolean isErrors() {
        return errors;
    }

    public boolean isFailTransfers() {
        return flags.failTransfers;
    }

    public boolean isUnknownPageError() {
        return unknownPageError;
    }
}

/**
 * Class that represents the content of a transfer active
 */
class TransferActiveContent {

    protected ArrayList<TransferActiveTransfer> confirmedTransfers;

    protected String currentTransfer;

    protected String dateFormat;

    protected ArrayList<TransferActiveTransfer> failTransfers;

    protected String nextYear;

    protected String page;

    protected String thisYear;

    protected String todayDay;

    protected String todayMonth;

    protected String todayYear;

    protected String totalFail;

    protected ArrayList<TransferActiveTransfer> transfers;
}

/**
 * Class that represents the flags of an active transfer
 */
class TransferActiveFlags {

    boolean failTransfers;
}
