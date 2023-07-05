package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class that represents PaymentHistory response
 */
public class PaymentHistory extends BaseResponse implements Serializable {

	private static final long serialVersionUID = -7126204221653262020L;
	
	private PaymentHistoryContent content;

	/**
	 * Class that represents payment history content (as received from the backend)
	 */
	protected class PaymentHistoryContent implements Serializable {

		private static final long serialVersionUID = 7884827382362938824L;

		private String totalInProcess;

		@SerializedName("in_process")
		private ArrayList<PaymentHistoryEntry> inProcess;

		private ArrayList<PaymentHistoryEntry> history;

		@SerializedName("available_periods")
		private AvailablePeriods availablePeriods;

		@SerializedName("available_payees")
		private AvailablePayees availablePayees;
	}

	/**
	 * Internal class that represents available payees
	 */
	public class AvailablePayees implements Serializable {

		private static final long serialVersionUID = -3476525151324085158L;

		@SerializedName("default_label")
		private String defaultLabel;

		@SerializedName("default_value")
		private String defaultValue;

		private String action;

		private ArrayList<PaymentHistoryEntry> payees;

		public String getDefaultLabel() {
			return defaultLabel;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public String getAction() {
			return action;
		}

		public ArrayList<PaymentHistoryEntry> getPayees() {
			return payees;
		}
	}
	
	public String getTotalInProcess() {
		return content.totalInProcess;
	}

	public ArrayList<PaymentHistoryEntry> getInProcess() {
		return content.inProcess;
	}

	public ArrayList<PaymentHistoryEntry> getHistory() {
		return content.history;
	}

	public AvailablePeriods getAvailablePeriods() {
		return content.availablePeriods;
	}

	public AvailablePayees getAvailablePayees() {
		return content.availablePayees;
	}
}
