package com.popular.android.mibanco.ws.response;

import com.popular.android.mibanco.model.BaseResponse;

import java.io.Serializable;

/**
 * Created by ET55498 on 4/15/16.
 */
public class MobileCashTrx extends BaseResponse implements Serializable {

    private static final long serialVersionUID = 2992185557388266376L;
    private int id; //local field (not from json)
    private String amount;
    private String nickname;
    private String trxReceipt;
    private String trxExpDate;
    private String atmLast4Num;
    private String atmType;
    private String accountSection;
    private String accountLast4Num;
    private String accountFrontEndId;
    private String trxDateTime;
    private String trxReceiptId;
    private String tranType;
    private String receiverName;
    private String receiverPhone;
    private String memo;
    private String received;
    private String senderPhone;
    private String status;
    private String atmLocation;
    private String trxDate;


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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTrxReceipt() {
        return trxReceipt;
    }

    public void setTrxReceipt(String trxReceipt) {
        this.trxReceipt = trxReceipt;
    }

    public String getTrxExpDate() {
        return trxExpDate;
    }

    public void setTrxExpDate(String trxExpDate) {
        this.trxExpDate = trxExpDate;
    }

    public String getAtmLast4Num() {
        return atmLast4Num;
    }

    public void setAtmLast4Num(String atmLast4Num) {
        this.atmLast4Num = atmLast4Num;
    }

    public String getAccountSection() {
        return accountSection;
    }

    public void setAccountSection(String accountSection) {
        this.accountSection = accountSection;
    }

    public String getAccountFrontEndId() {
        return accountFrontEndId;
    }

    public void setAccountFrontEndId(String accountFrontEndId) {
        this.accountFrontEndId = accountFrontEndId;
    }

    public String getTrxDateTime() {
        return trxDateTime;
    }

    public void setTrxDateTime(String trxDateTime) {
        this.trxDateTime = trxDateTime;
    }

    public String getTrxReceiptId() {
        return trxReceiptId;
    }

    public void setTrxReceiptId(String trxReceiptId) {
        this.trxReceiptId = trxReceiptId;
    }

    public String getAtmType() {
        return atmType;
    }

    public void setAtmType(String atmType) {
        this.atmType = atmType;
    }

    public String getTranType() {
        return tranType;
    }

    public void setTranType(String tranType) {
        this.tranType = tranType;
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

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAtmLocation() { return atmLocation; }

    public void setAtmLocation(String atmLocation) { this.atmLocation = atmLocation; }

    public String getTrxDate() { return trxDate; }

    public void settrxDate(String trxDate) {
        this.trxDate = trxDate;
    }

}
