package com.popular.android.mibanco.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Class that represents an account card
 */
public class OnOffCard implements Serializable {

    private static final long serialVersionUID = 1;

    @SerializedName("plasticNumber")
    @Expose
    private String plasticNumber;

    @SerializedName("plasticEmbossedName")
    @Expose
    private String plasticEmbossedName;

    @SerializedName("plasticFrontEndId")
    @Expose
    private String plasticFrontEndId;

    @SerializedName("plasticIsOff")
    @Expose
    private boolean plasticIsOff;

    @SerializedName("plasticType")
    @Expose
    private String plasticType;

    @SerializedName("plasticLastTurnedOff")
    @Expose
    private String plasticLastTurnedOff;

    @SerializedName("plasticLastTransactionDate")
    @Expose
    private String plasticLastTransactionDate;

    public String getPlasticNumber() {
        return plasticNumber;
    }

    public void setPlasticNumber(String plasticNumber) {
        this.plasticNumber = plasticNumber;
    }

    public String getPlasticEmbossedName() {
        return plasticEmbossedName;
    }

    public void setPlasticEmbossedName(String plasticEmbossedName) {
        this.plasticEmbossedName = plasticEmbossedName;
    }

    public String getPlasticFrontEndId() {
        return plasticFrontEndId;
    }

    public void setPlasticFrontEndId(String plasticFrontEndId) {
        this.plasticFrontEndId = plasticFrontEndId;
    }

    public boolean isPlasticIsOff() {
        return plasticIsOff;
    }

    public void setPlasticIsOff(boolean plasticIsOff) {
        this.plasticIsOff = plasticIsOff;
    }

    public String getPlasticType() {
        return plasticType;
    }

    public void setPlasticType(String plasticType) {
        this.plasticType = plasticType;
    }

    public String getPlasticLastTurnedOff() {
        return plasticLastTurnedOff;
    }

    public void setPlasticLastTurnedOff(String plasticLastTurnedOff) {
        this.plasticLastTurnedOff = plasticLastTurnedOff; }

    public String getPlasticLastTransactionDate() {
        return plasticLastTransactionDate;
    }

    public void setPlasticLastTransactionDate(String plasticLastTransactionDate) {
        this.plasticLastTransactionDate = plasticLastTransactionDate;
    }
}
