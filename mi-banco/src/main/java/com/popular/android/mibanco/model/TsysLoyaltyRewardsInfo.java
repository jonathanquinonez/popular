package com.popular.android.mibanco.model;

import java.io.Serializable;

public class TsysLoyaltyRewardsInfo implements Serializable {

	private static final long serialVersionUID = 754520442719334905L;
	
	private Boolean canRedeemRewards;
	private String rewardsAccountStatus;
	private String availableRewardsBalance;
	private float minimumRewardsBalance = 10.0f;
	/** A double version of availableRewardsBalance for number comparisons */
	private double availableBalanceDouble;
	
	public Boolean getCanRedeemRewards() {
		return canRedeemRewards;
	}
	
	public void setCanRedeemRewards(Boolean canRedeemRewards) {
		this.canRedeemRewards = canRedeemRewards;
	}
	
	public String getRewardsAccountStatus() {
		return rewardsAccountStatus;
	}
	
	public void setRewardsAccountStatus(String rewardsAccountStatus) {
		this.rewardsAccountStatus = rewardsAccountStatus;
	}
	
	public String getAvailableRewardsBalance() {
		return availableRewardsBalance;
	}
	
	public void setAvailableRewardsBalance(String availableRewardsBalance) {
		this.availableRewardsBalance = availableRewardsBalance;
	}

	public float getMinimumRewardsBalance() {
		return minimumRewardsBalance;
	}

	public void setMinimumRewardsBalance(float minimumRewardsBalance) {
		this.minimumRewardsBalance = minimumRewardsBalance;
	}

	public double getAvailableBalanceDouble() {
		return availableBalanceDouble;
	}

	public void setAvailableBalanceDouble(double availableBalanceDouble) {
		this.availableBalanceDouble = availableBalanceDouble;
	}

	@Override
	public String toString() {

		return "{\n" +
			"\t\"canRedeemRewards\" : " + (canRedeemRewards == null ? false : canRedeemRewards) + ",\n" +
			"\t\"rewardsAccountStatus\" : " + (rewardsAccountStatus == null ? "null" : "\"" + rewardsAccountStatus + "\"") + ",\n" +
			"\t\"availableRewardsBalance\" : " + (availableRewardsBalance == null ? "null" : "\"" + availableRewardsBalance + "\"") + ",\n" +
			"\t\"minimumRewardsBalance\" : " + minimumRewardsBalance + ",\n" +
			"\t\"availableBalanceDouble\" : " + availableBalanceDouble + "\n" +
		"}";
	}
}
