package com.popular.android.mibanco.model;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.FeatureFlags;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class that represents an account card
 */
public class AccountCard implements Serializable {

    private static final long serialVersionUID = 1;

    private String nickname;
    private String balance;
    private String accountLast4Num;
    private String section;
    private String accountSection;
    private String apiAccountKey;
    private String cardResourceUri;
    private String frontEndId;
    private String atmLast4Num;
    private String accountFrontEndId;
    private String atmType;
    private ArrayList<AccountCard> atmCards;
    private AccountCard selectedCardFromAccount;


    public AccountCard(){}
    public ArrayList<AccountCard> getAtmCards() {return atmCards; }

    public void setAtmCards(ArrayList<AccountCard> atmCards) { this.atmCards = atmCards; }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String name) {
        this.nickname = name;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getAccountLast4Num() {
        return accountLast4Num;
    }

    public void setAccountLast4Num(String accountLast4Num) {
        this.accountLast4Num = accountLast4Num;
    }

    public String getCardImageUri() {
        if(atmType != null && atmType.equalsIgnoreCase("INT")){
            return "drawable://" + App.getApplicationInstance().getResources().getIdentifier("account_image_international", "drawable", App.getApplicationInstance().getPackageName());
        }
        else if(atmType != null && atmType.equalsIgnoreCase("REG")){
            if (FeatureFlags.MBMT_417()) {
                return "drawable://" + App.getApplicationInstance().getResources().getIdentifier("account_image_regular", "drawable", App.getApplicationInstance().getPackageName());
            }else {
                return "drawable://" + App.getApplicationInstance().getResources().getIdentifier("account_image_default", "drawable", App.getApplicationInstance().getPackageName());
            }
        }
        else if (cardResourceUri == null) {
            return "drawable://" + App.getApplicationInstance().getAccountCardResource(getApiAccountKey());
        }
        return "drawable://" + App.getApplicationInstance().getResources().getIdentifier("account_image_regular", "drawable", App.getApplicationInstance().getPackageName());
        //return cardResourceUri;
    }

    public void setCardResourceUri(String cardResourceUri) {
        this.cardResourceUri = cardResourceUri;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getApiAccountKey() {
        return apiAccountKey;
    }

    public void setApiAccountKey(String apiAccountKey) {
        this.apiAccountKey = apiAccountKey;
    }

    public String getFrontEndId() {
        return frontEndId;
    }

    public void setFrontEndId(String frontEndId) {
        this.frontEndId = frontEndId;
    }

    public String getAccountSection() {return accountSection;}

    public void setAccountSection(String accountSection) {this.accountSection = accountSection;}

    public String getAccountFrontEndId() {
        return accountFrontEndId;
    }

    public void setAccountFrontEndId(String accountFrontEndId) {
        this.accountFrontEndId = accountFrontEndId;
    }

    public String getAtmLast4Num() {
        return atmLast4Num;
    }

    public void setAtmLast4Num(String atmLast4Num) {
        this.atmLast4Num = atmLast4Num;
    }

    public String getAtmType() { return atmType; }

    public void setAtmType(String atmType) {this.atmType = atmType; }

    public AccountCard getSelectedCardFromAccount() {
        return selectedCardFromAccount;
    }

    public void setSelectedCardFromAccount(AccountCard selectedCardFromAccount) {
        this.selectedCardFromAccount = selectedCardFromAccount;
    }
}
