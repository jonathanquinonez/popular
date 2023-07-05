package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Class that represents an RDC check
 */
public class RDCCheckItem extends BaseFormResponse implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	RDCCheckItemContent content;
	
	public String getFrontImage() {
		return content.frontImage;
	}
	
	public String getBackImage() {
		return content.backImage;
	}
	
	class RDCCheckItemContent implements Serializable {
		
		private static final long serialVersionUID = 2L;
		
		@SerializedName("frontImage")
		protected String frontImage;
			
		@SerializedName("backImage")
		protected String backImage;
		
		@SerializedName("depositId")
		protected String depositId;
			
		public String getFrontImage() {
			return frontImage;
		}
			
		public String getBackImage() {
			return backImage;
		}
		
		public String getDepositId() {
			return depositId;
		}
	}
}
