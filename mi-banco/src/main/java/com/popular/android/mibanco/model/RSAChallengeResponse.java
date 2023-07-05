package com.popular.android.mibanco.model;

import com.google.gson.annotations.SerializedName;
import com.popular.android.mibanco.util.Utils;

import java.io.Serializable;

public class RSAChallengeResponse extends BaseResponse implements Serializable {
    private RSAChallengeContentResponse content;
    private RSAChallengeFlagsResponse flags;
    private boolean hasInvalidCode;

    @SerializedName("error_message")
    private String errorMessage;

    private class RSAChallengeFlagsResponse implements Serializable {
        @SerializedName("access_blocked")
        private boolean accessBlocked;
        @SerializedName("iPad_update")
        private boolean ipadUpdate;
        @SerializedName("noSessionError")
        private boolean noSessionError;
    }

    private class RSAChallengeContentResponse implements Serializable {
        @SerializedName("challenge")
        private String challenge;

        @SerializedName("rsablocked")
        private boolean rsaBlocked;

        @SerializedName("question")
        private String question;

        @SerializedName("username")
        private String username;

        @SerializedName("phone")
        private String phone;

        @SerializedName("challengeType")
        private String challengeType;

        @SerializedName("hasAltPhone")
        private boolean hasAltPhone;

        @SerializedName("timeout")
        private boolean timeout;

        @SerializedName("validationError")
        private boolean validationError;

        @SerializedName("code")
        private String codeVoiceCall;

        @SerializedName("codeSent")
        private boolean codeSent;

        @SerializedName("success")
        private boolean success;
    }

    //region Content *********************

    public String getChallenge() {
        return content.challenge;
    }

    public String getQuestion() {
        return content.question;
    }

    public boolean isRSABloked() {
        return content.rsaBlocked;
    }

    public String getUsername() {
        return content.username;
    }

    public String getPhone() {
        return content.phone;
    }

    public String getOOBChanllengeType() {
        return content.challengeType;
    }

    public boolean hasAltPhone() {
        return content.hasAltPhone;
    }

    public boolean isTimeout() {
        return content.timeout;
    }

    public boolean isValidationError() {
        return content.validationError;
    }

    public String getOOBCodeVoiceCall() {
        return content.codeVoiceCall;
    }

    public void setOOBCodeVoiceCall(String oobCodeVoiceCall) {
        this.content.codeVoiceCall = oobCodeVoiceCall;
    }

    public boolean isCodeSent() {
        return content.codeSent;
    }

    public void setIsCodeSent(boolean codeSent){
        this.content.codeSent = codeSent;
    }

    public boolean isHasInvalidCode() {
        return !Utils.isBlankOrNull(errorMessage);
    }

    public void setHasInvalidCode(boolean hasInvalidCode) {
        this.hasInvalidCode = hasInvalidCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }



    //endregion

    //region Flags *********************

    public boolean isAccessBlocked() {
        return this.flags.accessBlocked;
    }

    public boolean isIpadUpdate() {
        return this.flags.ipadUpdate;
    }

    public boolean isNoSessionError() {
        return this.flags.noSessionError;
    }

    //endregion

}
