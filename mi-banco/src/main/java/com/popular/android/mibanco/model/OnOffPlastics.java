package com.popular.android.mibanco.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Class that represents an account card
 */
public class OnOffPlastics extends BaseResponse implements Serializable {

    @SerializedName("responseStatus")
    @Expose
    private String responseStatus;
    private MobileOnOffContent content;
    private List<CustomerAccount> tmpList;

    public Card getCardPlastic(){return content.cardPlastic;}
    public List<OnOffCard> getPlastics(){return content.plastics;}
    public String getResponseStatus(){return responseStatus;}
    public void setTmpList(List<CustomerAccount> list){this.tmpList = list;}
    public List<CustomerAccount> getTmpList(){return this.tmpList;}

    protected class MobileOnOffContent implements Serializable {

        private static final long serialVersionUID = 1287600227668473144L;
        @SerializedName("plastics")
        @Expose
        private List<OnOffCard> plastics;

        @SerializedName("card")
        @Expose
        private Card cardPlastic;

    }

}
