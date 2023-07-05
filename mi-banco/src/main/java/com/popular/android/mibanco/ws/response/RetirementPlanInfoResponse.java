package com.popular.android.mibanco.ws.response;

import com.google.gson.annotations.SerializedName;
import com.popular.android.mibanco.model.BaseResponse;

import java.io.Serializable;
import java.util.LinkedList;

public class RetirementPlanInfoResponse extends BaseResponse implements Serializable {

    private RetirementPlanRetData retplan;

    @SerializedName("hasRetPlan")
    private boolean hasRetPlan;

    @SerializedName("retirementPlanAccountNewBadge")
    private boolean retirementPlanAccountNewBadge;

    @SerializedName("retplanDowntimeMessage")
    private boolean retplanDowntimeMessage;

    private static class RetirementPlanRetData implements Serializable {

        @SerializedName("plans")
        private LinkedList<RetirementPlanDetail> plans;
    }

    public boolean getHasRetPlan () { return hasRetPlan; }

    public boolean getRetplanDowntimeMessage () {return retplanDowntimeMessage; }

    public LinkedList getRetirementPlans () { return retplan.plans; }

    public boolean getRetirementPlanAccountNewBadge() {
        return retirementPlanAccountNewBadge;
    }

}
