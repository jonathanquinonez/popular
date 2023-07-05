package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.SerializedName;
import com.popular.android.mibanco.model.BaseResponse;

import java.io.Serializable;

/**
 * Object that represents an Easy Cash transaction information response
 * Created by ET55498 on 4/15/16.
 */
public class MobileCashTrxInfo extends BaseResponse implements Serializable {

    private static final long serialVersionUID = 4013523101878975151L;
    private MobileCashTrxContent content;

    public String getTransactionStatus(){return content.status;}
    public MobileCashTrx getTransaction(){return content.transaction;}
    public String getTransactionId(){return content.trxReceipt;}
    public String getReceiptId(){return content.trxReceiptId;}

    //To remove

    public void setTransaction(MobileCashTrx trx){content.transaction = trx;}
    public void setTransactionId(String trxId){content.trxReceipt = trxId;}
    public void setReceiptId(String receiptId){ content.trxReceiptId = receiptId;}


    protected class MobileCashTrxContent implements Serializable {

        private static final long serialVersionUID = 2177600273268473107L;
        @SerializedName("status")
        private String status;

        @SerializedName("transaction")
        private MobileCashTrx transaction;

        @SerializedName("trxReceipt")
        private String trxReceipt;

        @SerializedName("trxReceiptId")
        private String trxReceiptId;

    }
}
