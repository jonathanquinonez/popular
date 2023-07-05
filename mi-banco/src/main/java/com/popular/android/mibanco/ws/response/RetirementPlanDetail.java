package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RetirementPlanDetail implements Serializable{

    @SerializedName("accountLast4Num")
    private String planLast4Num;

    @SerializedName("plannam")
    private String plannam;


    @SerializedName("planTotalBalance")
    protected String planTotalBalance;

    public String getPlanLast4Num() {
        return planLast4Num;
    }

    public void setPlanLast4Num(String planLast4Num) {
        this.planLast4Num = planLast4Num;
    }

    public String getPlannam() {
        return plannam;
    }

    public void setPlannam(String plannam) {
        this.plannam = plannam;
    }

    public String getPlanTotalBalance() {
        return planTotalBalance;
    }

    public void setPlanTotalBalance(String planTotalBalance) {
        this.planTotalBalance = planTotalBalance;
    }

}
