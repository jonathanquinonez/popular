package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;
import com.popular.android.mibanco.FeatureFlags;

import java.util.LinkedList;
import java.util.List;

/**
 * Class that represents a Customer
 */
public class Customer extends BaseFormResponse {

    private List<CustomerAccount> accounts;

    private List<CustomerAccount> ccards;

    private List<CustomerAccount> cdIra;

    /**
     * Contains account information for the particular customer
     */
    CustomerContent content; //content, changed accessibility to allow ut

    private List<CustomerAccount> loans;

    private List<CustomerAccount> mortgage;

    private List<CustomerAccount> other;

    private List<CustomerAccount> rewards;

    private List<CustomerAccount> securities;

    private List<CustomerAccount> rdcAccounts;

    private List<CustomerAccount>  retirementPlanAccounts;//MBFIS-459 list for the retirement plan

    private boolean retPlanCallback = false;

    public CustomerContent getContent() {
        return content;
    }

    public boolean getAthmSso() {
        return content.athmSso;
    }

    public boolean isPushEnabled() {
        if (content != null) {
            return content.isPushEnabled;
        } else {
            return false;
        }
    }

    public boolean getOutreach() { return content.outreach; }

    public void setOutreach(boolean outreach) { content.outreach = outreach; }

    public boolean getInterruptionPage() { return content.interruptionPage; }

    /**
     * //MBFIS-459
     * @return flag boolean
     */
    public boolean isRetPlanEnabled() {
        if (content != null) {
            return content.retPlanFlag;
        } else {
            return false;
        }
    }

    /**
     * Gets a flag for Marketplace Credit Card visibility
     *
     * @return true if Marketplace Credit Card  is supposed to be shown, false otherwise
     */
    public boolean getShowMarketplaceCCA() {
        return content.showMarketplaceCCA;
    }

    public String getCustomerEmail() {
        return content.customerEmail;
    }

    /**
     * Gets a flag for Virgin Island Account visibility
     *
     * @return true if Customer has Virgin Island Account, false otherwise
     */
    public boolean getHasVIAccount() {
        return content.hasVIAccount;
    }

    /**
     * Determine if should appear badge or not
     * @return bool
     */
    public boolean hasRpBadge() {
        return content.rpBadge;
    }

    public void setRpBadge(boolean rpBadge) {
        this.content.rpBadge = rpBadge;
    }

    public boolean getIsTransactional() { return content.isTransactional == null || content.isTransactional.equals("true"); }

    /**
     * Get GDPR Flag ENHA.20191018.popup.cookie.track.message
     * @return bool
     */
    public boolean isFlagGDPREnabled() {
        return content.flagGDPR;
    }

    /**
     * //MBFIS-810
     * @return flag boolean
     */
    public boolean customerHasRetPlan() {

       return  (content != null) ? content.hasRetPlan : false;
    }

    public void setHasRetPlan(boolean hasRetPlan) {
        this.content.hasRetPlan = hasRetPlan;
    }


    public boolean getIsComercialCustomer() { return content.isComercialCustomer == null || content.isComercialCustomer.equals("true"); }

    public boolean getIsWealth() { return content.isWealth == null || content.isWealth.equals("T"); }

    public boolean getIsPremiumBanking() {
        return content.isPremiumBanking != null && content.isPremiumBanking.equals("true");
    }

    public List<CustomerAccount> getDepositAccounts() {
        if (accounts == null) {
            sortAcc();
        }
        return accounts;
    }

    public List<CustomerAccount> getCdsIras() {
        if (cdIra == null) {
            sortAcc();
        }
        return cdIra;
    }

    public List<CustomerAccount> getCreditCards() {
        if (ccards == null) {
            sortAcc();
        }
        return ccards;
    }

    public List<CustomerAccount> getInsuranceAndSecurities() {
        if (securities == null) {
            sortAcc();
        }
        return new LinkedList<>(securities);
    }

    /**
     * //mbfis 459
     * @return retirementPlanAccounts account list
     */
    public List<CustomerAccount>  getRetirementPlanAccounts() {
        return retirementPlanAccounts;
    }

    public void setRetirementPlanAccounts(List<CustomerAccount> retirementPlanAccounts) {
        this.retirementPlanAccounts = retirementPlanAccounts;
    }

    public List<CustomerAccount> getLoans() {
        if (loans == null) {
            sortAcc();
        }
        return new LinkedList<>(loans);
    }

    public List<CustomerAccount> getMortgage() {
        if (mortgage == null) {
            sortAcc();
        }
        return mortgage;
    }

    public List<CustomerAccount> getOtherAccounts() {
        if (other == null) {
            sortAcc();
        }
        return other;
    }

    public List<CustomerAccount> getRewards() {
        if (rewards == null) {
            sortAcc();
        }
        return rewards;
    }

    public List<CustomerAccount> getRDCAccounts() {
        if (rdcAccounts == null) {
            rdcAccounts = new LinkedList<>();
            if (content.rdcAccounts != null && content.rdcAccounts.size() > 0) {
                if (accounts != null && accounts.size() > 0) {
                    for (CustomerAccount customerAccount : accounts) {
                        for (CustomerAccount rdcAccount : content.rdcAccounts) {
                            if (customerAccount.getApiAccountKey().equals(rdcAccount.getApiAccountKey())
                                    && customerAccount.getAccountNumberSuffix().equalsIgnoreCase(rdcAccount.getAccountNumberSuffix())
                                    && customerAccount.getAccountSection().equalsIgnoreCase(rdcAccount.getAccountSection())) {
                                customerAccount.setDepositLimit(rdcAccount.getDepositLimit());
                               // customerAccount.setFrontEndId(rdcAccount.getFrontEndId());
                                rdcAccounts.add(customerAccount);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return rdcAccounts;
    }
    
    private void sortAcc() {
        ccards = new LinkedList<>();
        accounts = new LinkedList<>();
        loans = new LinkedList<>();
        mortgage = new LinkedList<>();
        cdIra = new LinkedList<>();
        other = new LinkedList<>();
        rewards = new LinkedList<>();
        retirementPlanAccounts = new LinkedList<>();
        securities = new LinkedList<>();

        if (content == null) {
            return;
        }

        if (content.accounts != null) {
            for (final CustomerAccount acc : content.accounts) {
                if (acc.getSubtype().equalsIgnoreCase("CCA")) {
                    ccards.add(acc);
                } else if (acc.getSubtype().equalsIgnoreCase("IDA")) {
                    accounts.add(acc);
                } else if (acc.getSubtype().equalsIgnoreCase("ILA") || acc.getSubtype().equalsIgnoreCase("LEA")) {
                    acc.setShowStatement(false);
                    loans.add(acc);
                } else if (acc.getSubtype().equalsIgnoreCase("MLA")) {
                    if(content.hideMLATransactions) {
                        acc.setShowStatement(false);
                    } else {
                        if (FeatureFlags.MBCA_104()) {
                            acc.setShowStatement(true);
                        } else {
                            acc.setShowStatement(false);
                        }
                    }
                    mortgage.add(acc);
                } else if (acc.getSubtype().equalsIgnoreCase("CDA")) {
                    acc.setShowStatement(false);
                    cdIra.add(acc);
                } else {
                    other.add(acc);
                }
            }
        }

        if (content.programs != null) {
            for (final CustomerAccount acc : content.programs) {
                acc.setShowStatement(false);
                acc.setBalanceColorRed(false);
                rewards.add(acc);
            }
        }

        if (content.secins != null) {
            for (final CustomerAccount acc : content.secins) {
                acc.setShowStatement(false);
                acc.setBalanceColorRed(false);
                securities.add(acc);
            }
        }

    }

    public boolean isLimitsPerSegmentEnabled() {
        if (content != null) {
            return content.isLimitsPerSegmentEnabled;
        } else {
            return false;
        }
    }

    public boolean getRetPlanCallback() {
        return retPlanCallback;
    }

    public void setRetPlanCallback(boolean retPlanCallback) {
        this.retPlanCallback = retPlanCallback;
    }
}

class CustomerContent {

    private static final String CUSTOMER_EMAIL = "customerEmail"; //Marketplace flag from back-end

    private static final String SHOW_MARKETPLACE = "showMarketplace"; //Marketplace flag from back-end

    private static final String HAS_VI_ACCOUNT = "hasVIAccount"; //Marketplace flag from back-end

    final private String badge = "rpBadge"; // @SerializeName literal

    //MBFIS-810
    final private String hasRetPlanLiteral = "hasRetPlan"; //@SerializeName literal

    //GDPR II
    private static final String MBSD3806 = "MBSD3806"; //@SerializeName literal

    protected LinkedList<CustomerAccount> accounts;

    protected CustomerInfo customerInfo;

    protected LinkedList<CustomerAccount> programs;

    protected LinkedList<CustomerAccount> secins;
    
    protected LinkedList<CustomerAccount> rdcAccounts;

    protected LinkedList<CustomerAccount> retplan; //MBFIS-459 Retirement Plans

    private static final String HIDE_MLA_TRANSACTIONS = "flagMBSE2513"; //string flag mla hide transactions

    @SerializedName("outreach")
    protected boolean outreach;

    @SerializedName("interruptionPage")
    protected boolean interruptionPage;

    @SerializedName("athmSso")
    protected boolean athmSso;

    @SerializedName("isTransactional")
    protected String isTransactional;

    @SerializedName("pushNotifications")
    protected boolean isPushEnabled;

    @SerializedName("isComercialCustomer")
    protected String isComercialCustomer;

    @SerializedName("isPremiumBanking")
    protected String isPremiumBanking;

    @SerializedName("flagMBFIS581")
    protected boolean retPlanFlag;//mbfis 459 flagMBFIS581

    @SerializedName("isWealth")
    protected String isWealth;

    @SerializedName(badge)
    protected boolean rpBadge; //MBFIS-640

    @SerializedName("limitsPerSegment")
    protected boolean isLimitsPerSegmentEnabled;

    @SerializedName(SHOW_MARKETPLACE)
    protected boolean showMarketplaceCCA; //Martkeplace visibility flag

    @SerializedName(CUSTOMER_EMAIL)
    protected String customerEmail; //customerEmail

    @SerializedName(HAS_VI_ACCOUNT)
    protected boolean hasVIAccount; //Martkeplace visibility flag

    @SerializedName(hasRetPlanLiteral)
    protected  boolean hasRetPlan; //MBFIS-810

    @SerializedName(HIDE_MLA_TRANSACTIONS)
    protected boolean hideMLATransactions; //flag hide mla transactions

    @SerializedName(MBSD3806)
    protected boolean flagGDPR; //Flag ENHA.20191018.popup.cookie.track.message
}

/**
 * Class that represents customer information
 */
class CustomerInfo {

    protected String customerName;

    @SerializedName("is_today_birthday")
    protected String isTodayBirthday;
}
