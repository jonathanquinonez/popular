package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;
import com.popular.android.mibanco.util.Utils;

import java.util.ArrayList;

/**
 * Class that represents a global status response
 */
public class GlobalStatus extends BaseFormResponse {

    GlobalStatusContent content;

    boolean downtime;

    public String getLatestVersion() {
        return content.latestVersion;
    }

    public ArrayList<String> getLocatorActiveLayers() {
        return content.locatorActiveLayers;
    }

    public String getMinimumVersion() {
        return content.minimumVersion;
    }

    public boolean isAccountOpening() {
        return content.entitlements.accountOpening;
    }

    public boolean isAggregation() {
        return content.entitlements.aggregation;
    }

    public boolean isAlerts() {
        return content.entitlements.alerts;
    }

    public boolean isBanking() {
        return content.entitlements.banking;
    }
    
    public boolean isBillpay() {
        return content.entitlements.billpay;
    }

    public boolean isEbills() {
        return content.entitlements.ebills;
    }

    public boolean isEnglish() {
        return content.isEnglish;
    }

    public boolean isEnrollment() {
        return content.entitlements.enrollment;
    }

    public boolean isEzstmt() {
        return content.entitlements.ezstmt;
    }

    public boolean isMobileBanking() {
        return content.entitlements.mobileBanking;
    }

    public boolean isOao() {
        return content.entitlements.oao;
    }

    public boolean isSignon() {
        return content.entitlements.signon;
    }

    public boolean isTransfers() {
        return content.entitlements.transfers;
    }
    
    public boolean isRdc() {
    	return content.entitlements.rdc;
    }
    
    public boolean isAthm() {
    	return content.entitlements.athmovil;
    }

    public boolean isMobileCash(){return content.entitlements.mobilecash;}

    public boolean isMobileCashForOthers(){return content.entitlements.mobilecashForOthers;}

    public boolean isDeviceInfoSdkInfoEnabled(){return content.entitlements != null && content.entitlements.deviceInfoSdkInfo;}

    public boolean isCashDrop() {
        String cashdrop = content.entitlements.cashdrop;
        return ("true".equalsIgnoreCase(cashdrop));
    }

    public boolean isCashDropExists(){
        String cashdrop = content.entitlements.cashdrop;
        return (!Utils.isBlankOrNull(cashdrop));
    }

    /**
     * Check if retirementPlan globalEntitlement is enabled
     * @return
     */
    public boolean isReterimentPlanEnabled() {
        return content.entitlements.retirementPlan; // Call retirement value of content
    }
}

/**
 * Class that represents global status content
 */
class GlobalStatusContent {

    protected GlobalStatusEntitlements entitlements;

    @SerializedName("is_english")
    protected boolean isEnglish;

    @SerializedName("latest_version")
    protected String latestVersion;

    protected ArrayList<String> locatorActiveLayers;

    @SerializedName("minimum_version")
    protected String minimumVersion;
}

/**
 * Class that represents the global status entitlements
 */
class GlobalStatusEntitlements {

    /**
     * Literal value to retirementPlan serialiazed, used to parse json.
     */
    final private String retPlan = "RETIREMENT_PLAN"; // globalStatus literal value

    @SerializedName("ACCOUNT_OPENING")
    protected boolean accountOpening;

    @SerializedName("AGGREGATION")
    protected boolean aggregation;

    @SerializedName("ALERTS")
    protected boolean alerts;

    @SerializedName("BANKING")
    protected boolean banking;
    
    @SerializedName("BILLPAY")
    protected boolean billpay;

    @SerializedName("EBILLS")
    protected boolean ebills;

    @SerializedName("ENROLLMENT")
    protected boolean enrollment;

    @SerializedName("EZSTMT")
    protected boolean ezstmt;

    @SerializedName("MOBILE_BANKING")
    protected boolean mobileBanking;

    @SerializedName("OAO")
    protected boolean oao;

    @SerializedName("SIGNON")
    protected boolean signon;

    @SerializedName("TRANSFERS")
    protected boolean transfers;
    
    @SerializedName("RDC")
    protected boolean rdc;
    
    @SerializedName("ATHMOVIL")
    protected boolean athmovil;

    @SerializedName("MOBILE_CASH")
    protected boolean mobilecash = true;

    @SerializedName("MOBILE_CASH_FOR_OTHERS")
    protected boolean mobilecashForOthers;

    @SerializedName("CASH_DROP")
    protected String cashdrop = null;

    /**
     * RetirmentPlan global entitlement status parse
     * bool retirmentPlan
     */
    @SerializedName(retPlan)
    protected boolean retirementPlan; // Content glabalStatus parse to retirementPlan global entitlement

    @SerializedName("MOBILE_SDK_RSA")
    protected boolean deviceInfoSdkInfo = false;

}
