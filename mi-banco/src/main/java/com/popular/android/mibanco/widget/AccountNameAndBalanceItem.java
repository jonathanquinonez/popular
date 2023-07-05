package com.popular.android.mibanco.widget;

public class AccountNameAndBalanceItem {
	
	private String accountName;
	private String accountSuffix;
	private String balance;
	private boolean redBalance = false;
	
	/**
	 * Default constructor
	 */
	public AccountNameAndBalanceItem() {
	}

	/**
	 * @return the accountName
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * @param accountName the accountName to set
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	/**
	 * @return the accountSuffix
	 */
	public String getAccountSuffix() {
		return accountSuffix;
	}

	/**
	 * @param accountSuffix the accountSuffix to set
	 */
	public void setAccountSuffix(String accountSuffix) {
		this.accountSuffix = accountSuffix;
	}

	/**
	 * @return the balance
	 */
	public String getBalance() {
		return balance;
	}

	/**
	 * @param balance the balance to set
	 */
	public void setBalance(String balance) {
		this.balance = balance;
	}


	/**
	 * @return the redBalance
	 */
	public boolean isRedBalance() {
		return redBalance;
	}


	/**
	 * @param redBalance the redBalance to set
	 */
	public void setRedBalance(boolean redBalance) {
		this.redBalance = redBalance;
	}
	
	
}
