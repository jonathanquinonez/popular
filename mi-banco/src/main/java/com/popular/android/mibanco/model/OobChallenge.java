package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Class that represents an OOB challenge
 * Created by S681718 on 6/30/2016.
 */
public class OobChallenge extends BaseResponse {

    @SerializedName("content")
    private OobContent content;
    @SerializedName("flags")
    private OobFlags flags;
    @SerializedName("customerToken")
    private String customerToken;

    @SerializedName("canOpenAccount")
    private String canOpenAccount;

    @SerializedName("isForeignCustomer")
    private String isForeignCustomer;


    public void setResponderMessage(String responderMessage) { this.responderMessage = responderMessage;}

    private boolean hasInvalidCode;

    public OobChallenge() {
        content = new OobContent();
        flags = new OobFlags();
    }

    public OobFlags getFlags() { return flags;}
    public void setFlags(OobFlags flags) { this.flags = flags;   }

    public OobContent getContent() {
        return content;
    }
    public void setContent(OobContent content) {
        this.content = content;
    }

    public String getCustomerToken() {
        return customerToken;
    }

    public void setCustomerToken(String customerToken) {
        this.customerToken = customerToken;
    }

    /**
     * Internal object that represents an OOB Flag
     */
    public class OobFlags implements Serializable {

        private static final long serialVersionUID = 1;
        @SerializedName("access_blocked")
        private Boolean accessBlocked;
        @SerializedName("iPad_update")
        private Boolean ipadUpdate;
        @SerializedName("noSessionError")
        private Boolean noSessionError;

        public Boolean getAccessBlocked() {
            return accessBlocked;
        }

        public void setAccessBlocked(Boolean accessBlocked) {
            this.accessBlocked = accessBlocked;
        }

        public Boolean getIpadUpdate() {
            return ipadUpdate;
        }

        public void setIpadUpdate(Boolean ipadUpdate) {
            this.ipadUpdate = ipadUpdate;
        }

        public Boolean getNoSessionError() {
            return noSessionError;
        }

        public void setNoSessionError(Boolean noSessionError) {
            this.noSessionError = noSessionError;
        }
    }

    /**
     * Internal class that represents OOB Content
     */
    public class OobContent implements Serializable{
        private static final long serialVersionUID = 1;

        @SerializedName("phone")
        private String phone;
        @SerializedName("challengeType")
        private String challengeType;

        @SerializedName("username")
        private String username;

        @SerializedName("rsablocked")
        private boolean rsaBlocked;
        @SerializedName("hasAltPhone")
        private boolean hasAltPhone;

        @SerializedName("code")
        private String codeVoiceCall;

        @SerializedName("timeout")
        private boolean timeout;
        @SerializedName("validationError")
        private boolean validationError;
        @SerializedName("codeSent")
        private boolean codeSent;


        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getChallengeType() {
            return challengeType;
        }

        public void setChallengeType(String challengeType) {
            this.challengeType = challengeType;
        }



        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }



        public boolean getRsaBlocked() {
            return rsaBlocked;
        }

        public void setRsaBlocked(boolean rsaBlocked) {
            this.rsaBlocked = rsaBlocked;
        }

        public boolean isHasAltPhone() {
            return hasAltPhone;
        }

        public void setHasAltPhone(boolean hasAltPhone) {
            this.hasAltPhone = hasAltPhone;
        }

        public String getCodeVoiceCall() {
            return codeVoiceCall;
        }

        public void setCodeVoiceCall(String codeVoiceCall) {
            this.codeVoiceCall = codeVoiceCall;
        }

        public boolean isTimeout() { return timeout; }

        public void setTimeout(boolean timeout) { this.timeout = timeout; }

        public boolean isValidationError() { return validationError; }

        public void setValidationError(boolean validationError) { this.validationError = validationError; }

        public boolean isCodeSent() { return codeSent;}

        public void setCodeSent(boolean codeSent) { this.codeSent = codeSent; }

    }

    public boolean isHasInvalidCode() {
        return hasInvalidCode;
    }

    public void setHasInvalidCode(boolean hasInvalidCode) {
        this.hasInvalidCode = hasInvalidCode;
    }

    public String getCanOpenAccount() {
        return canOpenAccount;
    }
    public void setCanOpenAccount(String canOpenAccount) {
        this.canOpenAccount = canOpenAccount;
    }

    public String getIsForeignCustomer() {
        return isForeignCustomer;
    }
    public void setIsForeignCustomer(String isForeignCustomer) {
        this.isForeignCustomer = isForeignCustomer;
    }

}
