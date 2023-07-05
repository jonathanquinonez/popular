package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;

/**
 * Class that represents a check deposit receipt response
 */
public class DepositCheckReceipt extends BaseFormResponse {
	
	public DepositCheckReceipt() {
		this.content = null;
	}
	
	DepositCheckContent content;
	
	public String getStatus() {
		return content.status;
	}
	
	public String getDepositId() {
		return content.depositId;
	}
	
	public String getError() {
		return content.error;
	}
		
	class DepositCheckContent {
		
		@SerializedName("status")
		protected String status;
			
		@SerializedName("depositId")
		protected String depositId;
		
		@SerializedName("error")
		protected String error;
	}
}