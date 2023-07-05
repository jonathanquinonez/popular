package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;

/**
 * Class that represents a customer entitlement
 */
public class CustomerEntitlements extends BaseFormResponse {
	
	CustomerEntitlementContent content;
	
	public boolean hasRdc() {
		if(content != null && content.entitlements != null)
			return content.entitlements.rdc;
		else
			return false;
	}
	
	public boolean hasAthm() {
		if(content != null && content.entitlements != null)
			return content.entitlements.athmovil;
		else
			return false;
	}

	public boolean hasMobileCash() {
		if(content != null && content.entitlements != null)
			return content.entitlements.mobilecash;
		else
			return false;
	}

	public Boolean hasCashDrop() {
		if(content != null && content.entitlements != null)
			return content.entitlements.cashdrop;
		else
			return null;
	}

	public Boolean hasRetirementPlan() {
		if(content != null && content.entitlements != null)
			return content.entitlements.retirementPlan;
		else
			return null;
	}
	
}

/**
 * Class that represents customer entitlement content
 */
class CustomerEntitlementContent {
	
    protected CustomerEntitlementsContentEntitlements entitlements;
}

/**
 * Class that represents entitlements definition
 */
class CustomerEntitlementsContentEntitlements {

    @SerializedName("RDC")
    protected boolean rdc = false;
    
    @SerializedName("ATHMOVIL")
	protected boolean athmovil = false;

	@SerializedName("MOBILE_CASH")
	protected boolean mobilecash = false;

	@SerializedName("CASH_DROP")
	protected Boolean cashdrop = null;
    

	@SerializedName("RETIREMENT_PLAN")
	protected boolean retirementPlan = false;

}
