package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Class that represents a deposit check enrollment response
 */
public class AcceptedTermsInRDC extends BaseFormResponse implements Serializable {

	public AcceptedTermsInRDC() {
		this.content = null;
	}

	public AcceptedTermsContent content;

	public String getAcceptTerms() {
		return content.acceptTerms;
	}

	public String getAmountChargeTerms() {
		return content.amountChargeTerms;
	}
		
	class AcceptedTermsContent {
		
		@SerializedName("acceptTerms")
		public String acceptTerms;

		@SerializedName("amountChargeTerms")
		public String amountChargeTerms;

	}
}