package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Class that represents a deposit check enrollment response
 */
public class DepositCheckEnrollment extends BaseFormResponse implements Serializable {
	
	public DepositCheckEnrollment() {
		this.content = null;
	}
	
	DepositCheckContent content;
	
	public String getStatus() {
		return content.status;
	}
	
	public String getError() {
		return content.error;
	}
		
	class DepositCheckContent {
		
		@SerializedName("status")
		protected String status;
		
		@SerializedName("error")
		protected String error;
	}
}