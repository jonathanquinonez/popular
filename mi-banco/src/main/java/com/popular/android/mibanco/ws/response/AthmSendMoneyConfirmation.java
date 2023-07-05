package com.popular.android.mibanco.ws.response;

import java.io.Serializable;

public class AthmSendMoneyConfirmation extends AthmResponse implements Serializable {

    private static final long serialVersionUID = -504143314011914418L;
    private AthmSendMoneyConfirmationContent content;

    public String getAmount() {
        return content.amount;
    }

    public void setAmount(String amount) {
        content.amount = amount;
    }

    public String getPhone() {
        return content.phone;
    }

    public void setPhone(String phone) {
        content.phone = phone;
    }

    protected class AthmSendMoneyConfirmationContent {

        private String amount;
        private String phone;
    }
}
