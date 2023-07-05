package com.popular.android.mibanco.object;

import com.popular.android.mibanco.MiBancoConstants;

import java.io.Serializable;

public class ListItemEBills implements Serializable {

    private static final long serialVersionUID = -3815376933361277207L;

    private String amount;
    private String dueDate;
    private String ebillDateFormat;
    private boolean hasMinDueAmount;
    private int id;
    private int imgResource;
    private String invoiceDate;
    private String minAmount;
    private int payeeId;
    private String payeeLast4Num;
    private String title;

    public ListItemEBills(final int id, final int imgResource, final String title, final String billDate, final String dueDate, final String amount, final String minAmount, final int payeeId,
            final String payeeLast4Num, final boolean hasMinDueAmount, final String ebillDateFormat) {
        this.id = id;
        this.imgResource = imgResource;
        this.title = title;
        invoiceDate = billDate;
        this.dueDate = dueDate;
        this.amount = amount;
        this.minAmount = minAmount;
        this.payeeId = payeeId;
        this.payeeLast4Num = payeeLast4Num;
        this.hasMinDueAmount = hasMinDueAmount;
        this.ebillDateFormat = ebillDateFormat;
    }

    public String getAmount() {
        return amount;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getEbillDateFormat() {
        return ebillDateFormat;
    }

    public String getFormattedAmount() {
        return MiBancoConstants.CURRENCY_SYMBOL + amount;
    }

    public int getId() {
        return id;
    }

    public int getImgResource() {
        return imgResource;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public String getMinAmount() {
        return minAmount;
    }

    public int getPayeeId() {
        return payeeId;
    }

    public String getPayeeLast4Num() {
        return payeeLast4Num;
    }

    public String getTitle() {
        return title != null ? title.trim() : title;
    }

    public boolean hasMinDueAmount() {
        return hasMinDueAmount;
    }

    public void setAmount(final String amount) {
        this.amount = amount;
    }

    public void setDueDate(final String dueDate) {
        this.dueDate = dueDate;
    }

    public void setDueDateFormat(final String dueDateFormat) {
        this.ebillDateFormat = dueDateFormat;
    }

    public void setHasMinDueAmount(final boolean hasMinDueAmount) {
        this.hasMinDueAmount = hasMinDueAmount;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public void setImgResource(final int imgResource) {
        this.imgResource = imgResource;
    }

    public void setInvoiceDate(final String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public void setMinAmount(final String minAmount) {
        this.minAmount = minAmount;
    }

    public void setPayeeId(final int payeeId) {
        this.payeeId = payeeId;
    }

    public void setPayeeLast4Num(final String payeeLast4Num) {
        this.payeeLast4Num = payeeLast4Num;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

}
