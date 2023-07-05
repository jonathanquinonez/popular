package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Class that represents a deposit check enrollment response
 */
public class SendAcceptTermsInRDC extends BaseFormResponse implements Serializable {

	public SendAcceptTermsInRDC() {
		this.content = null;
	}

	public SendAcceptTermsContent content;

    public String getStatus() {
        return content.status;
    }

    public String getError() {
        return content.error;
    }
		
	class SendAcceptTermsContent {
		
		@SerializedName("status")
		public String status;

		@SerializedName("error")
		public String error;

		public String getStatus() {
			return status;
		}

		public String getError() {
			return error;
		}
	}
}