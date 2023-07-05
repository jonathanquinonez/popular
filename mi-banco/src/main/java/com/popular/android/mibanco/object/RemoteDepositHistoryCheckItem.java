package com.popular.android.mibanco.object;

public class RemoteDepositHistoryCheckItem {

	private String targetNickname;
	private String targetAccountLast4Num;
	private String frontendId;
	private String referenceNumber;
	private String amount;
	private String submittedDate;
	private String depositStatus;
	
	public String getTargetNickname() {
		return targetNickname;
	}
	public void setTargetNickname(String targetNickname) {
		this.targetNickname = targetNickname;
	}
	public String getTargetAccountLast4Num() {
		return targetAccountLast4Num;
	}
	public void setTargetAccountLast4Num(String targetAccountLast4Num) {
		this.targetAccountLast4Num = targetAccountLast4Num;
	}
	public String getFrontendId() {
		return frontendId;
	}
	public void setFrontendId(String frontendId) {
		this.frontendId = frontendId;
	}
	public String getReferenceNumber() {
		return referenceNumber;
	}
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getSubmittedDate() {
		return submittedDate;
	}
	public void setSubmittedDate(String submittedDate) {
		this.submittedDate = submittedDate;
	}
	public String getDepositStatus() {
		return depositStatus;
	}
	public void setDepositStatus(String depositStatus) {
		this.depositStatus = depositStatus;
	}
}
