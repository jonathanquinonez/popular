package com.popular.android.mibanco.model;

import com.popular.android.mibanco.R;

import java.io.Serializable;

/**
 * Class that represents customer account
 */
public class CustomerAccount implements Serializable, Cloneable {

    private static final long serialVersionUID = -7363263993149741246L;

    class AccountFeatures implements Serializable {

        private static final long serialVersionUID = -8359903723059581542L;

        protected boolean showStatement;
    }

    private String frontEndId;
    private String nickname;
    private String apiAccountKey;
    private String accountLast4Num;
    private String accountNumberSuffix;
    private String accountSection;
    private String subtype;
    private String productId;
    private String portalBalance;
    private boolean balanceColorRed;
    private AccountFeatures features;
    private boolean newAccount;
    private boolean showFullAccount;

    private String href;
    private transient Integer footerPreviewTextId;//It is the resource id of the text(or preview if longer) to show below the account info
    private transient Integer footerFullTextId;//It is the resource id of the full text to be shown in the modal fragment
    private int imgCoverFlowRes;
    private int imgRes;
    private String resName;
    private int wheelRes;
    private String depositLimit;

    private int onOffCount;
    private boolean onOffElegible;

    /**
     * True if the cash rewards balance can be shown and redeemed for this account,
     * false if the productId is not VPCBK or user is not the primary owner.
     * Only primary Cash Rewards (VPCBK) CCA Customers may view and redeem the balance.
     */
    private boolean isCashRewardsEligible;

    /**
     * Contains Tsys Loyalty Rewards info. This should only exist for Cash Rewards (VPCBK) CCA.
     * On other accounts this should be null.
     */
    private TsysLoyaltyRewardsInfo tsysLoyaltyRewardsInfo;

    /**
     * Contains Premia Rewards info. This should only exist for Premia Accounts CCA.
     * On other accounts this should be null
     */
    private String premiaBalance;

    public CustomerAccount() {
    }

    public CustomerAccount(String accountLast4Num, String accountNumberSuffix, String nickname, String apiAccountKey, String subtype, String accountSection) {
        this.accountLast4Num = accountLast4Num;
        this.accountNumberSuffix = accountNumberSuffix;
        this.nickname = nickname;
        this.apiAccountKey = apiAccountKey;
        this.subtype = subtype;
        this.accountSection = accountSection;
    }

    /**
     * @return resource id for footerPreviewText
     */
    public Integer getFooterPreviewTextId () {
        return footerPreviewTextId;
    }

    /**
     * @param footerPreviewTextId the Integer resource ID for footerPreviewText
     */
    public void setFooterPreviewTextId (Integer footerPreviewTextId) {
        this.footerPreviewTextId = footerPreviewTextId;
    }

    /**
     * @return resource id for footerFullText
     */
    public Integer getFooterFullTextId () {
        return footerFullTextId;
    }

    /**
     * @param footerFullTextId the Integer resource ID for footerFullText
     */
    public void setFooterFullTextId (Integer footerFullTextId) {
        this.footerFullTextId = footerFullTextId;
    }

    public String getAccountLast4Num() {
        return accountLast4Num;
    }

    public String getAccountNumberSuffix() {
        return accountNumberSuffix;
    }

    public String getAccountSection() {
        return accountSection;
    }

    public String getApiAccountKey() {
        return apiAccountKey;
    }

    public String getFrontEndId() {
        return frontEndId;
    }

    public int getOnOffCount() {
        return onOffCount;
    }

    public void setOnOffCount(final int onOffCount) {
        this.onOffCount = onOffCount;
    }

    public boolean isOnOffElegible() {
        return onOffElegible;
    }

    public void setOnOffElegible(boolean onOffElegible) {
        this.onOffElegible = onOffElegible;
    }


    public int getGalleryImgResource() {
        if (imgCoverFlowRes == 0) {
            imgCoverFlowRes = R.drawable.carousel_card_default;
        }
        return imgCoverFlowRes;
    }

    public String getHref() {
        return href;
    }

    public int getImgResource() {
        if (imgRes == 0) {
            imgRes = R.drawable.account_image_default;
        }
        return imgRes;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPortalBalance() {
        if (portalBalance == null) {
            return "";
        }
        return portalBalance;
    }

    public String getProductId() {
        return productId;
    }

    public String getResName() {
        return resName;
    }

    public String getSubtype() {
        return subtype;
    }

    public int getWheelImgResource() {
        if (wheelRes == 0) {
            wheelRes = R.drawable.transaction_card_default;
        }
        return wheelRes;
    }

    public boolean isBalanceColorRed() {
        return balanceColorRed;
    }

    public boolean isNewAccount() {
        return newAccount;
    }

    public boolean isShowFullAccount() {
        return showFullAccount;
    }
    
    public String getDepositLimit() {
    	return this.depositLimit;
    }

    public void setAccountLast4Num(final String accountLast4Num) {
        this.accountLast4Num = accountLast4Num;
    }

    public void setAccountNumberSuffix(final String accountNumberSuffix) {
        this.accountNumberSuffix = accountNumberSuffix;
    }

    public void setAccountSection(final String accountSection) {
        this.accountSection = accountSection;
    }

    public void setApiAccountKey(final String apiAccountKey) {
        this.apiAccountKey = apiAccountKey;
    }

    public void setBalanceColorRed(final boolean balanceColorRed) {
        this.balanceColorRed = balanceColorRed;
    }

    public void setFrontEndId(final String frontEndId) {
        this.frontEndId = frontEndId;
    }

    public void setGalleryImgResource(final int res) {
        imgCoverFlowRes = res;
    }

    public void setHref(final String href) {
        this.href = href;
    }

    public void setImgCoverFlowRes(final Integer imgCoverFlowRes) {
        this.imgCoverFlowRes = imgCoverFlowRes;
    }

    public void setImgResource(final int res) {
        imgRes = res;
    }

    public void setNewAccount(final boolean newAccount) {
        this.newAccount = newAccount;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }

    public void setPortalBalance(final String portalBalance) {
        this.portalBalance = portalBalance;
    }

    public void setProductId(final String productId) {
        this.productId = productId;
    }

    public void setResName(final String name) {
        resName = name;
    }

    public void setShowFullAccount(final boolean showFullAccount) {
        this.showFullAccount = showFullAccount;
    }

    public void setShowStatement(final boolean showStatement) {
        if (features == null) {
            features = new AccountFeatures();
        }
        features.showStatement = showStatement;
    }

    public void setSubtype(final String subtype) {
        this.subtype = subtype;
    }

    public void setWheelImgResource(final int res) {
        wheelRes = res;
    }

    public void setWheelRes(final Integer wheelRes) {
        this.wheelRes = wheelRes;
    }

    public boolean showStatement() {
        return features != null && features.showStatement;
    }
    
    public void setDepositLimit (String depositLimit) {
    	this.depositLimit = depositLimit;
    }

    public boolean isCashRewardsEligible() {
        return isCashRewardsEligible;
    }

    public void setCashRewardsEligible(boolean cashRewardsEligible) {
        isCashRewardsEligible = cashRewardsEligible;
    }

    public TsysLoyaltyRewardsInfo getTsysLoyaltyRewardsInfo() {
        return tsysLoyaltyRewardsInfo;
    }

    public void setTsysLoyaltyRewardsInfo(TsysLoyaltyRewardsInfo tsysLoyaltyRewardsInfo) {
        this.tsysLoyaltyRewardsInfo = tsysLoyaltyRewardsInfo;
    }

    public String getPremiaBalance() {
        return premiaBalance;
    }

    public void setPremiaBalance(String premiaBalance) {
        this.premiaBalance = premiaBalance;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "CustomerAccount{" +
                "frontEndId='" + frontEndId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", apiAccountKey='" + apiAccountKey + '\'' +
                ", accountLast4Num='" + accountLast4Num + '\'' +
                ", accountNumberSuffix='" + accountNumberSuffix + '\'' +
                ", accountSection='" + accountSection + '\'' +
                ", subtype='" + subtype + '\'' +
                ", productId='" + productId + '\'' +
                ", portalBalance='" + portalBalance + '\'' +
                ", balanceColorRed=" + balanceColorRed +
                ", features=" + features +
                ", newAccount=" + newAccount +
                ", showFullAccount=" + showFullAccount +
                ", href='" + href + '\'' +
                ", imgCoverFlowRes=" + imgCoverFlowRes +
                ", imgRes=" + imgRes +
                ", resName='" + resName + '\'' +
                ", wheelRes=" + wheelRes +
                ", depositLimit='" + depositLimit + '\'' +
                ", onOffCount=" + onOffCount +
                ", onOffElegible=" + onOffElegible +
                ", isCashRewardsEligible=" + isCashRewardsEligible +
                ", tsysLoyaltyRewardsInfo=" + tsysLoyaltyRewardsInfo +
                ", premiaInfo=" + premiaBalance +
        '}';
    }
}
