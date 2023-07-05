package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;

/**
 * PremiaInfo model class.
 */
public class PremiaInfo extends BaseResponse {

    private PremiaContent content; // Premia content.

    @SerializedName("tsysBalanceRewards")
    private String tsysBalanceRewards;

    public PremiaContent getContent() {
        return content;
    }

    public void setContent(PremiaContent content) {
        this.content = content;
    }

    /**
     * Is Premia enabled?
     * @return boolean
     */
    public boolean isPremiaEnabled() {
        return (content != null) ? content.premiaFlag : false;
    }

    /**
     * Premia Balance from profile
     * @return balance
     */
    public String getTsysBalanceRewards() { return tsysBalanceRewards; }
}

class PremiaContent {

    @SerializedName("MBCA1559")
    protected boolean premiaFlag; // MBCA-1559 - Premia ON/OFF flag.

}
