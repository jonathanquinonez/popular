package com.popular.android.mibanco.model;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.util.Utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class that represents the response of an ebills item
 */
public class EBillsItem {

    private final static SimpleDateFormat dateFormatApp = new SimpleDateFormat(App.getApplicationInstance().getDateFormat());
    private final static SimpleDateFormat dateFormatEbill = new SimpleDateFormat(MiBancoConstants.EBILL_DATE_FORMAT);

    private String href;
    private String accountNickname;
    private String last4AcctNumber;
    private String invoiceDate;
    private String payeeNumber;
    private String dueDate;
    private boolean hasDueDate;
    private String amountDue;
    private String minAmount;
    private boolean hasMinAmount;

    public String getAccountNickname() {
        return accountNickname;
    }

    public String getAmountDue() {
        return amountDue;
    }

    public BigDecimal getAmountDueDecimal() {
        try {
            final String currencyToBigDecimalFormat = Utils.currencyToBigDecimalFormat(amountDue);
            return new BigDecimal(currencyToBigDecimalFormat);
        } catch (final Exception e) {
            Log.e("EBillsItem", "Invalid or missing amount due.", e);
            return new BigDecimal(0.0f);
        }
    }

    public Date getDueDate() {
        if (TextUtils.isEmpty(dueDate)) {
            return null;
        }

        Date parsedDate = null;
        synchronized(dateFormatEbill) {
            try {
                parsedDate = dateFormatEbill.parse(dueDate);
            } catch (final ParseException e) {
                Log.e("EBillsItem", "Due date cannot be parsed.");
                return null;
            }
            return parsedDate;
        }
    }

    public String getDueDateString() {
        return dueDate;
    }

    public String getHref() {
        return href;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public String getInvoiceDateString(final Activity context) {
        Date parsedDate = null;
        synchronized(dateFormatEbill) {
            synchronized(dateFormatEbill) {
            try {
                parsedDate = dateFormatEbill.parse(invoiceDate);
                return dateFormatApp.format(parsedDate);
            } catch (final ParseException e) {
                Log.e("EBillsItem", "Invoice date cannot be parsed.");
                return invoiceDate;
              }
            }
        }
    }

    public String getLast4AcctNumber() {
        return last4AcctNumber;
    }

    public String getMinAmount() {
        return minAmount;
    }

    public BigDecimal getMinAmountDueDecimal() {
        try {
            return new BigDecimal(Utils.currencyToBigDecimalFormat(minAmount));
        } catch (final Exception e) {
            Log.w("EBillsItem", "Invalid or missing min amount due.");
            return new BigDecimal(0.0f);
        }
    }

    public int getPayeeNumber() {
        return Utils.getValidGlobalPayeeId(payeeNumber);
    }

    public boolean hasAmountDue() {
        return (getAmountDueDecimal().compareTo(new BigDecimal(0.0)) > 0);
    }

    public boolean hasDueDate() {
        return hasDueDate;
    }

    public boolean hasMinAmount() {
        return hasMinAmount;
    }

    public void setDueDate(String dueDate) { this.dueDate=dueDate; };
}
