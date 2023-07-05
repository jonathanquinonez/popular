
package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Transaction implements Serializable
{

    @SerializedName("accountLast4Num")
    @Expose
    private String accountLast4Num;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("accountSection")
    @Expose
    private String accountSection;
    @SerializedName("trxExpDate")
    @Expose
    private String trxExpDate;
    @SerializedName("trxReceiptId")
    @Expose
    private String trxReceiptId;
    @SerializedName("atmLast4Num")
    @Expose
    private String atmLast4Num;
    @SerializedName("senderPhone")
    @Expose
    private String senderPhone;
    @SerializedName("receiverPhone")
    @Expose
    private String receiverPhone;
    @SerializedName("memo")
    @Expose
    private String memo;
    @SerializedName("received")
    @Expose
    private String received;
    @SerializedName("nickname")
    @Expose
    private String nickname;
    @SerializedName("atmType")
    @Expose
    private String atmType;
    private final static long serialVersionUID = 2952730134197864976L;

    public String getAccountLast4Num() {
        return accountLast4Num;
    }

    public void setAccountLast4Num(String accountLast4Num) {
        this.accountLast4Num = accountLast4Num;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAccountSection() {
        return accountSection;
    }

    public void setAccountSection(String accountSection) {
        this.accountSection = accountSection;
    }

    public String getTrxExpDate() {
        return trxExpDate;
    }

    public void setTrxExpDate(String trxExpDate) {
        this.trxExpDate = trxExpDate;
    }

    public String getTrxReceiptId() {
        return trxReceiptId;
    }

    public void setTrxReceiptId(String trxReceiptId) {
        this.trxReceiptId = trxReceiptId;
    }

    public String getAtmLast4Num() {
        return atmLast4Num;
    }

    public void setAtmLast4Num(String atmLast4Num) {
        this.atmLast4Num = atmLast4Num;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAtmType() {
        return atmType;
    }

    public void setAtmType(String atmType) {
        this.atmType = atmType;
    }

}
