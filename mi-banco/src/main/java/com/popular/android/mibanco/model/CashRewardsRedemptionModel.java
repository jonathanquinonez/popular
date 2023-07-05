package com.popular.android.mibanco.model;

import com.popular.android.mibanco.util.Utils;

import java.io.Serializable;
import java.util.List;

/**
 * Mi Banco  - Mi Banco Credit Acquisition-MBCA.
 *
 * @author Stephanie Diaz <Stephanie.Diaz@evertecinc.com>
 * @version 1.0
 */
public class CashRewardsRedemptionModel implements Serializable {


    private  CustomerAccount cashRewardsAccount;
    private  RedemptionType redemptionType = RedemptionType.statementCredit;
    private  int redemptionAmount;
    private  String cardName;
    private  boolean acceptedTerms;
    private  List<CustomerAccount> directDepositAccounts;
    private  RedemptionStep redemptionStep = RedemptionStep.notStarted;
    private  CustomerAccount accountSelected;

    public CustomerAccount getCashRewardsAccount() {
        return cashRewardsAccount;
    }

    public void setCashRewardsAccount(CustomerAccount cashRewardsAccount) {
        this.cashRewardsAccount = cashRewardsAccount;
    }

    public RedemptionType getRedemptionType() {
        return redemptionType;
    }

    public void setRedemptionType(RedemptionType redemptionType) {
        this.redemptionType = redemptionType;
    }

    public int getRedemptionAmount() {
        return redemptionAmount;
    }

    public void setRedemptionAmount(int redemptionAmount) {
        this.redemptionAmount = redemptionAmount;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public boolean isAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

    public List<CustomerAccount> getDirectDepositAccounts() {
        return directDepositAccounts;
    }

    public void setDirectDepositAccounts(List<CustomerAccount> directDepositAccounts) {
        this.directDepositAccounts = directDepositAccounts;
    }

    public CustomerAccount getAccountSelected() {
        return accountSelected;
    }

    public void setAccountSelected(CustomerAccount accountSelected) {
        this.accountSelected = accountSelected;
    }

    public RedemptionStep getRedemptionStep() {
        return redemptionStep;
    }

    public void setRedemptionStep(RedemptionStep redemptionStep) {
        this.redemptionStep = redemptionStep;
    }

    public boolean isRedemptionInfoValid() {
        return (redemptionType == RedemptionType.statementCredit
                || redemptionType == RedemptionType.directDeposit) && isRedemptionAmountInRange()
                && acceptedTerms;
    }

    private boolean isRedemptionAmountInRange() {
        double amount = Double.parseDouble(Utils.formatAmountForWsWithoutCommas(redemptionAmount));
        return amount >= cashRewardsAccount.getTsysLoyaltyRewardsInfo()
                .getMinimumRewardsBalance() && amount <= cashRewardsAccount
                .getTsysLoyaltyRewardsInfo().getAvailableBalanceDouble();
    }

}
