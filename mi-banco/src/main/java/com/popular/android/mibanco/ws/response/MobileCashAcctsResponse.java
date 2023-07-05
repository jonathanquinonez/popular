package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.SerializedName;
import com.popular.android.mibanco.model.AccountCard;
import com.popular.android.mibanco.model.BaseResponse;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Object that represents a Easy Cash accounts response
 * Created by ET55498 on 4/15/16.
 */
public class MobileCashAcctsResponse extends BaseResponse implements Serializable {

    private static final long serialVersionUID = 1106498973157239555L;
    private MobileCashAcctsContent content;

    public String getMaxAmount(){return content.maxAmount;}
    public String getMaxAmountCashDrop(){return content.maxAmountCashDrop;}
    public int getStatus(){return content.status;}
    public int getPendingCount(){return content.pendingCount;}
    public MobileCashTrx getPendingFromMeTransaction(){return content.pendingTransaction;}
    public ArrayList<AccountCard> getMobileCashAccts(){return content.accounts;}


    protected class MobileCashAcctsContent implements Serializable {

        private static final long serialVersionUID = 4404436733495329761L;
        @SerializedName("maxAmount")
        private String maxAmount;

        @SerializedName("maxAmountCashDrop")
        private String maxAmountCashDrop;

        @SerializedName("status")
        private int status;

        @SerializedName("pendingCount")
        private int pendingCount;

        @SerializedName("pendingForMe")
        private MobileCashTrx pendingTransaction;

        @SerializedName("accounts")
        private ArrayList<AccountCard> accounts;


    }
}

