package com.popular.android.mibanco.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class that represents a Transfer
 */
public class Transfer extends BaseResponse {

    private class TransferAccounts {

        protected ArrayList<TransferAccount> values;
    }

    private class TransferContent {

        protected String currentTransfer;

        protected String dateFormat;

        protected String nextYear;

        protected String page;

        protected String thisYear;

        protected String todayDay;

        protected String todayMonth;

        protected String todayYear;
    }

    private class TransferEffectiveDate {

        protected String value;
    }

    private class TransferForm {

        protected TransferFormFields fields;
    }

    private class TransferFormFields {

        @SerializedName("transfers[0].accountFrom")
        protected TransferAccounts accountsFrom;

        @SerializedName("transfers[0].accountTo")
        protected TransferAccounts accountsTo;

        @SerializedName("transfers[0].effectiveDate")
        protected TransferEffectiveDate effectiveDate;
    }

    private TransferContent content;

    private boolean downtime;

    private boolean errors;

    private TransferForm form;

    @SerializedName("unknown_page_error")
    private boolean unknownPageError;

    public ArrayList<TransferAccount> getAccountsFrom() {
        try {
            return form.fields.accountsFrom.values;
        } catch (Error e) {
            return null;
        }
    }

    public ArrayList<TransferAccount> getAccountsTo() {
        try {
            return form.fields.accountsTo.values;
        } catch (Error e) {
            return null;
        }
    }

    public String getCurrentTransfer() {
        return content.currentTransfer;
    }

    public String getDateFormat() {
        return content.dateFormat;
    }

    public Date getEfectiveDate() {
        final SimpleDateFormat formatter = new SimpleDateFormat(content.dateFormat);
        Date date = null;
        try {
            date = formatter.parse(form.fields.effectiveDate.value);
        } catch (final ParseException e) {
            Log.e("Payment", "Error while parsing effective date string.");
        }
        return date;
    }

    public String getResponderName() {
        return responderName;
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

    public boolean isDowntime() {
        return downtime;
    }

    public boolean isErrors() {
        return errors;
    }

    public boolean isUnknownPageError() {
        return unknownPageError;
    }
}
