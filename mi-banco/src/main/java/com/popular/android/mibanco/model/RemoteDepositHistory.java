package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class that represents the Remote Deposit History
 */
public class RemoteDepositHistory extends BaseFormResponse implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	RemoteDepositHistoryContent content;
	
	public ArrayList<RemoteDepositCheckItem> getInProcessRemoteDepositHistoryChecks() {
		return content.in_process;
	}
	
	public ArrayList<RemoteDepositCheckItem> getProcessedRemoteDepositHistoryChecks() {
		return content.history;
	}
	
	protected class RemoteDepositHistoryContent implements Serializable {

		private static final long serialVersionUID = 2L;
		
		protected ArrayList<RemoteDepositCheckItem> in_process;
		protected ArrayList<RemoteDepositCheckItem> history;
	}

	/**
	 * Internal class that represents a Remote Deposit check item
	 */
	public class RemoteDepositCheckItem implements Serializable {
		
		private static final long serialVersionUID = 3L;

		@SerializedName("targetNickname")
		protected String targetNickname;
		
		@SerializedName("targetAccountLast4Num")
		protected String targetAccountLast4Num;
		
		@SerializedName("frontEndId")
	    protected String frontendid;
		
		@SerializedName("referenceNumber")
	    protected String referenceNumber;
		
		@SerializedName("amount")
	    protected String amount;
		
		@SerializedName("submittedDate")
	    protected String submittedDate;
		
		@SerializedName("status")
	    protected String status;

		public String getTargetNickname() {
			return targetNickname;
		}

		public String getTargetAccountLast4Num() {
			return targetAccountLast4Num;
		}

		public String getFrontendid() {
			return frontendid;
		}

		public String getReferenceNumber() {
			return referenceNumber;
		}

		public String getAmount() {
			return amount;
		}

		public String getSubmittedDate() {
			return submittedDate;
		}
		
		public String getStatus() {
			return status;
		}
	}
}
