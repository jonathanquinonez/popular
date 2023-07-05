package com.popular.android.mibanco.util;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.flurry.android.FlurryAgent;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a wrapper for Flurry and Google Analytics method calls.
 */
public final class BPAnalytics {
    private static FirebaseAnalytics mFirebaseAnalytics = null;
    public static final String EVENT_ACCOUNT_STATEMENT_REQUEST = "kAccountStatementRequest";
    public static final String EVENT_CONTACTUS_CALL_INITIATED = "kContactUsCallInitiated";
    public static final String EVENT_CUSTOM_LOGIN_IMAGE_SUCCESS = "kAddPersonalizeLogInImageSuccess";
    public static final String EVENT_REMEMBER_AMOUNT_BUTTON_TOUCHED = "kRememberAmountButtonTouched";
    public static final String EVENT_REMEMBER_AMOUNT_ENTERED = "kRememberAmountEntered";
    public static final String EVENT_AUTH_PROCESS_ASKED_FOR_PASSWORD = "kAuthProcessAskedForPassword";
    public static final String EVENT_AUTH_PROCESS_UNKNOWN_ERROR_PASSWORD = "kAuthProcessUnknownErrorPassword";
    public static final String EVENT_ADDED_SAVED_USERNAME = "kAddedSavedUsername";
    public static final String EVENT_LOCATOR_ASKED_FOR_DIRECTIONS = "kLocatorAskedForDirections";
    public static final String EVENT_PAYMENT_SUCCESSFUL = "kPaymentSuccessful";
    public static final String EVENT_PAYMENT_OVERPAY_PROMPT_PRESENTED = "kPaymentOverpayPromptPresented";
    public static final String EVENT_TRANSFER_SUCCESSFUL = "kTransferSuccessful";
    public static final String EVENT_AUTH_PROCESS_CHALLENGED = "kAuthProcessChallenged";
    public static final String EVENT_AUTH_PROCESS_UNKNOWN_ERROR_CHALLENGE = "kAuthProcessUnknownErrorChallenge";
    public static final String EVENT_EDITED_SAVED_USERNAMES = "kEditedSavedUsernames";
    public static final String EVENT_AUTH_PROCESS_USERNAME_BLOCKED = "kAuthProcessUsernameBlocked";
    public static final String EVENT_AUTH_PROCESS_USERNAME_INVALID = "kAuthProcessUsernameInvalid";
    public static final String EVENT_AUTH_PROCESS_BACK_TO_LOGIN_AFTER_PASSWORD = "kAuthProcessBackToLoginAfterPassword";
    public static final String EVENT_AUTH_PROCESS_CHALLENGED_AFTER_PASSWORD = "kAuthProcessChallengedAfterPassword";
    public static final String EVENT_AUTH_PROCESS_INCORRECT_PASSWORD = "kAuthProcessIncorrectPassword";
    public static final String EVENT_AUTH_PROCESS_PASSWORD_ACCEPTED = "kAuthProcessPasswordAccepted";
    public static final String EVENT_AUTH_PROCESS_BACK_TO_USERNAME_AFTER_CHALLENGE = "kAuthProcessBackToUsernameAfterChallenge";
    public static final String EVENT_AUTH_PROCESS_INCORRECT_ANSWER_RETRY_CHALLENGE = "kAuthProcessIncorrectAnswerRetryChallenge";
    public static final String EVENT_AUTH_PROCESS_CHALLENGE_SUCCESSFUL_ASKED_FOR_PASSWORD = "kAuthProcessChallengeSuccessfulAskedForPassword";
    public static final String EVENT_AUTH_PROCESS_TIMED_OUT_USERNAME = "kAuthProcessTimedOutUsername";
    public static final String EVENT_AUTH_PROCESS_TIMED_OUT_CHALLENGE = "kAuthProcessTimedOutChallenge";
    public static final String EVENT_AUTH_PROCESS_TIMED_OUT_PASSWORD = "kAuthProcessTimedOutPassword";
    public static final String EVENT_AUTH_PROCESS_CONNECTIVITY_ERROR_USERNAME = "kAuthProcessConnectivityErrorUsername";
    public static final String EVENT_AUTH_PROCESS_CONNECTIVITY_ERROR_CHALLENGE = "kAuthProcessConnectivityErrorChallenge";
    public static final String EVENT_AUTH_PROCESS_CONNECTIVITY_ERROR_PASSWORD = "kAuthProcessConnectivityErrorPassword";
    public static final String EVENT_AUTH_PROCESS_JSON_ERROR_PASSWORD = "kAuthProcessJSONErrorPassword";
    public static final String EVENT_AUTH_PROCESS_UNKNOWN_ERROR_USERNAME = "kAuthProcessUnknownErrorUsername";
    public static final String EVENT_AUTH_PROCESS_CHANGE_USERNAME = "kAuthProcessChangeUsername";
    public static final String EVENT_ACCOUNT_STATEMENT_SELECTED_CYCLE = "kAccountStatementSelectedCycle";
    public static final String EVENT_LOGON_INITIATED_WITH_SAVED_USERNAME = "kLogonInitiatedWithSavedUsername";
    public static final String EVENT_TRANSACTION_FAILED = "kTransactionFailed";
    public static final String EVENT_LAUNCHED_EXTERNAL_URL = "kLaunchedExternalURL";
    public static final String EVENT_MAINTENANCE_BALANCE_REQUESTED = "kLoginBalancesButtonPressed";

    public static final String EVENT_TRANSFER_HISTORY_VIEWED = "kTransferHistoryViewed";
    public static final String EVENT_PAYMENT_HISTORY_VIEWED = "kPaymentHistoryViewed";
    public static final String EVENT_TRANSFER_HISTORY_FILTERED_BY_DATE = "kTransferHistoryFilteredByDate";
    public static final String EVENT_TRANSFER_HISTORY_FILTERED_BY_ACCOUNT = "kTransferHistoryFilteredByAccount";
    public static final String EVENT_PAYMENT_HISTORY_FILTERED_BY_DATE = "kPaymentHistoryFilteredByDate";
    public static final String EVENT_PAYMENT_HISTORY_FILTERED_BY_PAYEE = "kPaymentHistoryFilteredByPayee";
    public static final String EVENT_PAYMENT_DELETED = "kPaymentDeleted";
    public static final String EVENT_TRANSFER_DELETED = "kTransferDeleted";
    public static final String EVENT_ENROLLMENT_INITIATED = "kEnrollmentInitiated";
    public static final String EVENT_ADD_PAYEES_SECTION = "kAddPayeeButtonTouched";
    public static final String EVENT_EDIT_PAYEES_SECTION = "kEditPayeeButtonTouched";
    public static final String EVENT_RSA_SESSION_SHOWN = "kRsaSessionChallengeShown";
    public static final String EVENT_RSA_SESSION_SUCCESS = "kRsaSessionChallengeCompleted";
    public static final String EVENT_RSA_SESSION_BLOCKED = "kRsaSessionChallengeBlocked";
    public static final String EVENT_AUTH_PROCESS_COMMERCIAL_PASSWORD_ACCEPTED = "kAuthProcessPAsswordAcceptedCommercial";
    public static final String EVENT_COMMERCIAL_REMOTE_DEPOSIT = "kRemoteDepositSuccessCommercial";

    public static final String EVENT_ALERTS_SECTION = "kAlertsWebViewOpened";

    public static final String EVENT_REMOTE_DEPOSIT_SUCCESSFUL = "kRemoteDepositSuccessful";
    public static final String EVENT_REMOTE_DEPOSIT_FAILED = "kRemoteDepositFailed";

    public static final String EVENT_ATHM_ENROLL_SUCCESSFUL = "kAthmEnrollmentSuccessful";
    public static final String EVENT_ATHM_LOGIN_SUCCESSFUL = "kAthmLoginSuccessful";
    public static final String EVENT_ATHM_LOGIN_FAILED = "kAthmLoginFailed";
    public static final String EVENT_ATHM_SENDMONEY_SUCCESSFUL = "kAthmSendMoneySuccessful";
    public static final String EVENT_ATHM_SENDMONEY_FAILED = "kAthmSendMoneyFailed";

    public static final String EVENT_ATHM_SSO_DOWNTIME = "kATHMDowntimeScreen";
    public static final String EVENT_ATHM_SSO_WELCOME_SPLASH = "kATHMWelcomeSplashScreen";
    public static final String EVENT_ATHM_APK_NOT_INSTALLED_OR_UPDATED = "kATHMDownloadSplashScreen";
    public static final String EVENT_OPEN_ATHM_WITH_VALID_TOKEN = "kATHMOpenedWithValidToken";
    public static final String EVENT_OPEN_ATHM_WITH_NEW_TOKEN = "kATHMOpenedWithNewToken";
    public static final String EVENT_OPEN_PLAY_STORE_ATHM = "kATHMPlayStoreOpened";
    public static final String EVENT_ATHM_SSO_LOGGED_OUT = "kATHMLoggedOut";

    public static final String EVENT_MC_VIEW_SCREEN = "kMobileCashViewScreen";
    public static final String EVENT_MC_PRESTAGE_SUCCESSFULL = "kMobileCashPreStageSuccessfull";
    public static final String EVENT_MC_PRESTAGE_FAILED = "kMobileCashPreStageFailed";
    public static final String EVENT_MC_PRESTAGE_FOR_OTHER_SUCCESSFULL = "kMobileCashPreStageForOtherSuccessfull";
    public static final String EVENT_MC_PRESTAGE_FOR_OTHER_FAILED = "kMobileCashPreStageForOtherFailed";
    public static final String EVENT_MC_PRESTAGE_FOR_OTHER_FAILED_SENDER_BLACKLIST = "kMobileCashPreStageForOtherFailedSenderBlacklist";
    public static final String EVENT_MC_PRESTAGE_FOR_OTHER_FAILED_RECEIVER_BLACKLIST = "kMobileCashPreStageForOtherFailedReceiverBlacklist";
    public static final String EVENT_MC_CANCEL_PRESTAGE_SUCCESSFULL = "kMobileCashCancelPreStageSuccessfull";
    public static final String EVENT_MC_CANCEL_PRESTAGE_FAILED = "kMobileCashCancelPreStageFailed";
    public static final String EVENT_MC_CANCEL_PRESTAGE_FOR_OTHER_SUCCESSFULL = "kMobileCashCancelPreStageForOtherSuccessfull";
    public static final String EVENT_MC_CANCEL_PRESTAGE_FOR_OTHER_FAILED = "kMobileCashCancelPreStageForOtherFailed";
    public static final String EVENT_MC_SEND_SMS_INSTRUCTIONS = "kMobileCashSendSMSInstructions";
    public static final String EVENT_MC_VIEW_HISTORY_CUSTOMER = "kMobileCashViewHistoryCustomer";
    public static final String EVENT_MC_SCAN_SUCCESSFULL = "kMobileCashScanSuccessfull";
    public static final String EVENT_MC_SCAN_FAILED = "kMobileCashScanFailed";
    public static final String EVENT_MC_SCAN_BLACKLIST_FAILED = "kMobileCashScanBlacklistFailed";
    public static final String EVENT_MC_SCAN_FROM_OTHER_SUCCESSFULL = "kMobileCashScanFromOtherSuccessfull";
    public static final String EVENT_MC_SCAN_FROM_OTHER_FAILED = "kMobileCashScanFromOtherFailed";
    public static final String EVENT_MC_UNSUBSCRIBE_CUSTOMER_SUCCESSFUL = "kMobileCashUnsubscribeCustomerSuccessful";
    public static final String EVENT_MC_UNSUBSCRIBE_CUSTOMER_FAILED = "kMobileCashUnsubscribeCustomerFailed";
    public static final String EVENT_MC_ENROLL_CUSTOMER_SUCCESS = "kMobileCashEnrollCustomerSuccess";
    public static final String EVENT_MC_ENROLL_SYSTEM_FAILED = "kMobileCashEnrollSystemFailed";
    public static final String EVENT_MC_ENROLL_CUSTOMER_BLACKLIST_FAILED = "kMobileCashEnrollCustomerBlacklistFailed";
    public static final String EVENT_MC_VIEW_HISTORY_NON_CUSTOMER = "kMobileCashViewHistoryNonCustomer";
    public static final String EVENT_MC_SCAN_FROM_OTHER_NON_CUSTOMER_SUCCESSFUL = "kMobileCashScanFromOtherNonCustomerSuccessful";
    public static final String EVENT_MC_SCAN_FROM_OTHER_NON_CUSTOMER_FAILED = "kMobileCashScanFromOtherNonCustomerFailed";
    public static final String EVENT_MC_SCAN_NON_CUSTOMER_BLACKLIST_FAILED = "kMobileCashScanNonCustomerBlacklistFailed";
    public static final String EVENT_MC_SCAN_NON_CUSTOMER_OFAC_FAILED = "kMobileCashScanNonCustomerOFACFailed";
    public static final String EVENT_MC_ENROLL_NON_CUSTOMER_SUCCESS = "kMobileCashEnrollNonCustomerSuccess";
    public static final String EVENT_MC_ENROLL_NON_CUSTOMER_SYSTEM_FAILED = "kMobileCashEnrollNonCustomerSystemFailed";
    public static final String EVENT_MC_ENROLL_NON_CUSTOMER_BLACKLIST_FAILED = "kMobileCashEnrollNonCustomerBlacklistFailed";
    public static final String EVENT_MC_ENROLL_NON_CUSTOMER_OFAC_FAILED = "kMobileCashEnrollNonCustomerOFACFailed";
    public static final String EVENT_MC_UNSUBSCRIBE_NON_CUSTOMER_SUCCESSFUL = "kMobileCashUnsubscribeNonCustomerSuccessful";
    public static final String EVENT_MC_UNSUBSCRIBE_NON_CUSTOMER_FAILED = "kMobileCashUnsubscribeNonCustomerFailed";

    public static final String EVENT_OOB_PROCESS_CHALLENGED_CODE = "kOobProcessCode";
    public static final String EVENT_OOB_PROCESS_INCORRECT_CODE = "kOobProcessIncorrectCode";

    public static final String EVENT_FP_SWITCH_ON= "kFingerprintSwitchOn";
    public static final String EVENT_FP_SWITCH_OFF= "kFingerprintSwitchOff";
    public static final String EVENT_PUSH_SWITCH_ON = "kPushNotificationsSwitchOn";
    public static final String EVENT_PUSH_SWITCH_OFF = "kPushNotificationsSwitchOff";
    public static final String EVENT_FP_LOGIN_SUCCESS= "kFingerprintLoginSuccess";
    public static final String EVENT_OPEN_ACCOUNT = "kOpenAccount";

    public static final String EVENT_ONOFF_PLASTIC_INQUIRY_SUCCESS = "kOnOffPlasticInquirySuccess";
    public static final String EVENT_ONOFF_PLASTIC_INQUIRY_FAIL = "kOnOffPlasticInquiryFail";
    public static final String EVENT_ONOFF_PLASTICUPDATE_ERROR = "kOnOffPlasticUpdateBackendError";
    public static final String EVENT_ONOFF_PLASTICUPDATE_OFF_SUCCESS = "kOnOffPlasticUpdateOffSuccess";
    public static final String EVENT_ONOFF_PLASTICUPDATE_ON_SUCCESS = "kOnOffPlasticUpdateOnSuccess";
    public static final String EVENT_ONOFF_PLASTICUPDATE_OFF_FAIL = "kOnOffPlasticUpdateOffFail";
    public static final String EVENT_ONOFF_PLASTICUPDATE_ON_FAIL = "kOnOffPlasticUpdateOnFail";

    public static final String EVENT_PUSH_WELCOME_SPLASH = "kSmsPushSplashScreen";
    public static final String EVENT_PUSH_WELCOME_SPLASH_ACCEPT = "kSmsPushSplashAccept";
    public static final String EVENT_PUSH_WELCOME_SPLASH_DECLINE = "kSmsPushSplashDecline";
    public static final String EVENT_PUSH_WELCOME_SPLASH_SKIP = "kSmsPushSplashSkip";
    public static final String EVENT_PUSH_WELCOME_SPLASH_NON_SMS = "kNonSmsPushSplashScreen";
    public static final String EVENT_PUSH_WELCOME_SPLASH_ACCEPT_NON_SMS = "kNonSmsPushSplashAccept";
    public static final String EVENT_PUSH_WELCOME_SPLASH_DECLINE_NON_SMS = "kSmsPushSplashDecline";
    public static final String EVENT_PUSH_WELCOME_SPLASH_SKIP_NON_SMS = "kSmsPushSplashSkip";
    public static final String EVENT_NOTIFICATION_CENTER = "kNotificationCenterButtonTouched";

    public static final String EVENT_CASH_REWARDS_REDEMPTION_STAT_CRED = "kCashRewards_StatCrdt";
    public static final String EVENT_CASH_REWARDS_REDEMPTION_ACCT_DEPOSIT = "kCashRewards_AcctDeposit";
    public static final String EVENT_CASH_REWARDS_REDEMPTION_ERROR = "kCashRewardsRedemptionError";
    
    public static final String EVENT_RET_PLAN_TOUCHED = "kRetirementAccountTouchedSuccess"; //Event to execute when the user taps Retirement Plan Account

//    kNonSmsPushSplashScreen
//            kNonSmsPushSplashAccept
//    kNonSmsPushSplashDecline
//            kNonSmsPushSplashSkip
//    kSmsPushSplashScreen
//            kSmsPushSplashAccept
//    kSmsPushSplashDecline
//            kSmsPushSplashSkip


    public static final String EVENT_WALLET_AUTHENTICATION_SUCCESS = "kWalletAuthenticationSuccessful";
    public static final String EVENT_MARKETPLACE_SECTION = "kMarketplaceSection"; //Event tag when user selects Marketplace banner
    public static final String EVENT_CREDIT_CARD_SECTION = "kCreditCardSection";
    public static final String EVENT_MARKETPLACE_BANNER = "kMarketplaceBanner";

    public static void logEvent(final String eventId) {
        FlurryAgent.logEvent(eventId);

        Log.v("BPAnalytics.logEvent()", "Event:[" + eventId + "]");

        if(mFirebaseAnalytics != null) {
            mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

            Bundle params = new Bundle();
            params.putString("Page", eventId);
            params.putString("Action", eventId);
            mFirebaseAnalytics.logEvent("action", params);
        }

        //CrashLytics events
        Answers.getInstance().logCustom(new CustomEvent(eventId));
    }

    public static void logEvent(final String eventId, final String... keysValues) {
        CustomEvent answersEvent = new CustomEvent(eventId);
        final Map<String, String> parameters = new HashMap<String, String>();
        Bundle bundle = new Bundle();

        for (int i = 0; i <= keysValues.length; i += 2) {
            if (i + 1 >= keysValues.length) {
                break;
            }
            parameters.put(keysValues[i], keysValues[i + 1]);
            bundle.putString(keysValues[i], keysValues[i + 1]);
            answersEvent.putCustomAttribute(keysValues[i], keysValues[i + 1]);
        }

        FlurryAgent.logEvent(eventId, parameters);

        if(mFirebaseAnalytics != null) {
            mFirebaseAnalytics.logEvent(eventId, bundle);
        }

        //CrashLytics events
        Answers.getInstance().logCustom(answersEvent);
    }

    public static void onEndSession(final Context context) {
        FlurryAgent.onEndSession(context);
    }

    public static void onStartSession(final Context context) {
        FlurryAgent.onStartSession(context);
        if(mFirebaseAnalytics == null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }
    }
}
