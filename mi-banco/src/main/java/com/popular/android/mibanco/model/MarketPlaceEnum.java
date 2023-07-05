package com.popular.android.mibanco.model;

import android.app.Application;

public class MarketPlaceEnum extends Application {

    private static boolean isResendButtonVisible;
    private static long timerSeconds;
    public static final long RESEND_SECONDS = 60; //60

    public enum Products {
        marketplace_credit_card_pr ("marketplace_credit_card_pr"),
        marketplace_credit_card_usvi ("marketplace_credit_card_usvi"),
        marketplace_eaccount_pr("marketplace_eaccount_pr"),
        marketplace_eaccount_usvi ("marketplace_eaccount_usvi");

        private final String name;

        Products(String s) {
            name = s;
        }
    }

    public enum MarketeplaceAction {
        ERROR ("ERROR"),
        NO_CHALLENGE_PRESENTED("NO_CHALLENGE_PRESENTED"),
        OOB_CHALLENGE("OOB_CHALLENGE"),
        EMAIL_CHALLENGE("EMAIL_CHALLENGE");

        private final String name;

        MarketeplaceAction(String s) {
            name = s;
        }
    }

    public enum OtpChallengeStatus {
        OTP_SERVICE_SUCCESS ("OTP_SERVICE_SUCCESS"),
        OTP_SERVICE_FAILED ("OTP_SERVICE_FAILED"),
        RESEND_LIMIT_REACHED ("RESEND_LIMIT_REACHED"),
        RESEND_LIMIT_EXCEEDED ("RESEND_LIMIT_EXCEEDED"),
        VALIDATION_SUCCESS ("VALIDATION_SUCCESS"),
        VALIDATION_FAILED ("VALIDATION_FAILED"),
        VALIDATION_LIMIT ("VALIDATION_LIMIT"),
        CODE_EXPIRED ("CODE_EXPIRED"),
        NO_SESSION_ERROR ("NO_SESSION_ERROR"),
        MISSING_INFO ("MISSING_INFO");

        private final String nameStatus;

        OtpChallengeStatus(String s) {
            nameStatus = s;
        }
    }

    public enum resendCode {

        RESEND_CODE_TRUE("true"),
        RESEND_CODE_FALSE("false");

        private final String value;
        resendCode(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }

    public static boolean getIsResendButtonVisible() {
        return isResendButtonVisible;
    }

    public static void setIsResendButtonVisible(boolean someVariable) {
        isResendButtonVisible = someVariable;
    }

    public static long getTimerSeconds() {
        return timerSeconds;
    }

    public static void setTimerSeconds(long millis) {
        timerSeconds = millis;
    }

}
