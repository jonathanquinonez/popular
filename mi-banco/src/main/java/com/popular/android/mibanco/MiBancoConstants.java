package com.popular.android.mibanco;

import android.os.Build;

/**
 * Class that contains all constants definitions
 */
public class MiBancoConstants {

    /**
     * The English language code.
     */
    public final static String ENGLISH_LANGUAGE_CODE = "en";

    /**
     * The Spanish language code.
     */
    public final static String SPANISH_LANGUAGE_CODE = "es";

    /**
     * Kill action name.
     */
    public final static String KILL_ACTION = "killall";

    /**
     * Kill URI.
     */
    public final static String KILL_TYPE = "content://com.popular.android.mibanco";

    /**
     * Max amount string length.
     */
    public static final int AMOUNT_STRING_LEN = 6;

    /**
     * The base DPI value for Android devices.
     **/
    public static final float BASE_DPI = 160f;

    /**
     * English date format string.
     */
    public static final String ENGLISH_DATE_FORMAT = "MM/dd/yyyy";

    /**
     * Indicates that session hasn't been checked yet.
     */
    public final static int NO_LAST_USER_INTERACTION_AVAILABLE = -1;

    /**
     * The main preferences file name.
     */
    public static final String PREFS_KEY = "bppr-shared-data";

    /**
     * Spanish date format string.
     */
    public static final String SPANISH_DATE_FORMAT = "dd/MM/yyyy";

    /**
     * Web services date format string.
     */
    public static final String WEBSERVICE_DATE_FORMAT = "MM/dd/yyyy";

    /**
     * The Constant WEBSERVICE_IN_DESCRIPTION_DATE_FORMAT.
     */
    public static final String WEBSERVICE_IN_DESCRIPTION_DATE_FORMAT = "MM/dd/yy";

    /**
     * The Constant CURRENCY_SYMBOL.
     */
    public static final String CURRENCY_SYMBOL = "$";

    /**
     * The shared preferences key for the entry containing successful logins counter.
     */
    public static final String SUCCESSFUL_LOGINS_PREFS_KEY = "successful-logins";

    public final static String CARD_ID_STRING_PREFS_KEY = "card-id";

    public final static String ENTERED_USERNAME_PREFS_KEY = "entered-username";

    public final static String PREFS_KEY_CAMERA_URI_PATH = "camera-uri";

    /**
     * Max allowed digits number in a global payee ID.
     */
    public static final int GLOBAL_PAYEE_ID_MAX_DIGITS = 6;

    /**
     * "My amounts" preferences file name.
     */
    public static final String MY_AMOUNTS_KEY = "my-amounts";

    /**
     * "My amounts" amounts list preferences key.
     */
    public static final String MY_AMOUNTS_LIST = "my-amounts-list";

    public static final String BASE_URL = "https://mobile.bancopopular.com";

    public final static String EBILL_DATE_FORMAT = "yyyyMMdd";

    /* Magic numbers */
    public static final int IMAGE_FADE_MILLIS = 500;
    public static final int IMAGE_AGE_WEEK_SECONDS = 7 * 24 * 60 * 60;
    public static final int IMAGE_CONNECT_TIMEOUT_MILLIS = 5000;
    public static final int IMAGE_READ_TIMEOUT_MILLIS = 20000;
    public static final int IMAGE_COMPRESSION_LEVEL = 100;
    public static final int LOCATION_ERASE_TIMEOUT = 1000 * 60 * 10;
    public static final int WEEK_MILLIS = 7 * 24 * 60 * 60 * 1000;
    public static final long USER_INTERACTION_TIMEOUT_MILLIS = 14 * 60 * 1000;
    public static final int ATHM_MAX_RECENT_NUMBERS = 6;

    /* Misc */
    public static final String KEY_DEVICE_ID = "app-device-id";
    public static final String WELCOME_IMAGE_FILENAME = "welcome_image.png";
    public static final String LOGIN_IMAGE_FILENAME = "custom_login_image.png";
    public static final String TRANSACTION_STATUS_CODE_OK = "OK";
    public static final String TRANSACTION_STATUS_CODE_IN_PROCESS = "IN_PROCESS";
    public static final String MARKETPLACE_CC_PR = "marketplace_credit_card_pr.png";
    public static final String MARKETPLACE_CC_VI = "marketplace_credit_card_usvi.png";
    public static final String MARKETPLACE_EA_PR = "marketplace_eaccount_pr.png";
    public static final String MARKETPLACE_EA_VI = "marketplace_eaccount_usvi.png";

    /** Status Notificated Real Time Payments */
    public static final String TRANSACTION_STATUS_CODE_IA = "IA"; // Status Notificated RT Payments
    public static final String INTRO_REQUEST_KEY = "intro_request";
    public static final String LOCATOR_BRANCHES_KEY = "branches";
    public static final String SPINNER_MONTH_YEAR_FORMAT = "MMMM yyyy";
    public static final String RSA_ENROLL = "loginRsaEnroll";
    public static final String SSDS_FORCED_LOGIN = "loginSsdsForced";

    public static final String SAMSUNGPAY_WALLET = "com.samsung.android.spay";
    public static final String ANDROIDPAY_WALLET = "com.google.android.gms";
    public static final String MASK_UNICODE = "\u2022";
    public static final int MASK_PATTERN_LENGTH = 5;

    /** Otp Validation */
    public static final String MASK_UNICODE_ASTERISK = "\u002A";

    /* Request codes */
    public static final int ENROLLMENT_REQUEST_CODE = 12369;
    public static final int PASSWORD_RECOVERY_REQUEST_CODE = 36915;
    public static final int ATHM_SELECT_CARD_REQUEST_CODE = 1337;
    public static final int AMOUNT_PICKER_REQUEST_CODE = 1338;
    public static final int CONTACT_CHOOSER_REQUEST_CODE = 1339;
    public static final int ATHM_TRANSFER_SENT_REQUEST_CODE = 1340;
    public static final int ATHM_TERMS_AND_CONDITIONS_REQUEST_CODE = 1341;

    public static final int MC_TRANSFER_SENT_REQUEST_CODE = 1201;
    public static final int OUTREACH_REQUEST_CODE = 33399;
    public static final int INTERRUPTION_REQUEST_CODE = 11;
    public static final int RSA_ENROLL_REQUEST_CODE = 33400;
    public static final int SSDS_FORCED_LOGIN_REQUEST_CODE = 33500;
    public static final int OPAC_REQUEST_CODE = 56452;

    public static final int ADD_PAYEES_REQUEST_CODE = 61024;
    public static final int EDIT_PAYEES_REQUEST_CODE = 61025;
    public static final int RSA_TO_ENROLL_REQUEST_CODE = 61026;
	public static final int RSA_EDIT_QUESTIONS_REQUEST_CODE = 61027;
    public static final int MODIFY_ALERTS_REQUEST_CODE = 1342;

    public static final int ACCOUNT_INFO_SEC_REQUEST_CODE = 32001; //MBFIS-223
    public static final int FORGOT_PASSWORD_SEC_REQUEST_CODE = 32002; //MBFIS-155
    public static final int SECURITY_INFORMATION_SEC_REQUEST_CODE = 32003; //MBFIS-521

    public static final int NOTIFICATION_CENTER_REQUEST_CODE = 32003;
    public static final int EMAIL_CHANGE_REQUEST_CODE = 32006; //MBSD-4028

    public static final int MARKETPLACE_WEBVIEW_REQUEST_CODE = 32007;

    public static final int COOKIE_PREFERENCE_VIEW_CODE = 32007; //mbsd 2388
    public static final int DESKTOP_WEBVIEW_REQUEST_CODE = 32005;



    /* Keys */
    public static final String DISMISS_PERSONALIZATION_NOTICE_PREFS_KEY = "dismiss-notice";
    public static final String WEB_VIEW_URL_KEY = "web_view_url";
    public static final String WEB_VIEW_START_OVER_KEY = "web_view_start_over";
    public static final String WEB_VIEW_HIDE_NAVIGATION_KEY = "web_view_hide_navigation";
    public static final String WEB_VIEW_PROTECT_URL_LIST_KEY = "web_view_protect_url_list";
    public static final String WEB_VIEW_PROTECT_URL_PATTERN_LIST_KEY = "web_view_protect_url_pattern_list";
    public static final String WEB_VIEW_URL_BLACKLIST_KEY = "web_view_url_blacklist";
    public static final String WEB_VIEW_URL_EXTERNAL_KEY = "web_view_url_external";
    public static final String WEB_VIEW_URL_MBDP = "web_view_url_mbdp";
    public static final String WEB_VIEW_SYNC_COOKIES_KEY = "web_view_sync_cookies";
    public static final String WEB_VIEW_ENROLLMENT_REQUEST_KEY = "web_view_enrollment_request";
    public static final String WEB_VIEW_TOOLBAR_HIDE_KEY = "web_view_toolbar_hide";
    public static final String WEB_VIEW_BACKACTION_DISABLED_KEY = "web_view_back_action";
    public static final String WEB_VIEW_CLOSEACTION_KEY = "web_view_close_action";
    public static final String WEB_VIEW_PROGRESSBAR_HIDE_KEY = "web_view_progressbar_hide";
    public static final String WEB_VIEW_ONSEN_ALERTS = "web_view_onsen_alerts";
    public static final String WEB_VIEW_CAN_BACK = "web_view_can_back"; //Allow WebView Back inside shown page
    public static final String WEB_VIEW_MARKETPLACE = "web_view_marketplace"; //To identify if WebView is for Marketplace
    public static final String WEB_VIEW_REQUEST_DOCUMENTS = "web_view_request_documents"; //To identify if WebView is for Marketplace
    public static final String WEB_VIEW_MARKETPLACE_CCA = "web_view_marketplace_cca"; //To identify Marketplace has Credit Cards
    public static final String WEB_VIEW_MARKETPLACE_EACCOUNT = "web_view_marketplace_eaccount"; //To identify Marketplace has EAccount
    public static final String WEB_VIEW_HIDE_RIGHT_MENU = "web_view_hide_right_menu"; //Hide menu
    public static final String CUSTOMER_ACCOUNT_KEY = "customer_account";
    public static final String ERROR_MESSAGE_KEY = "error_message";
    public static final String ERROR_MESSAGE_VALUE_KEY = "error_message_value";
    public static final String ATH_CARD_KEY = "athm_card";
    public static final String ATHM_CARD_EXPIRATION_DATE_MONTH_KEY = "athm_card_expiration_date_month";
    public static final String ATHM_CARD_EXPIRATION_DATE_YEAR_KEY = "athm_card_expiration_date_year";
    public static final String AMOUNT_PICKER_AMOUNT_KEY = "amount";
    public static final String ATHM_CONTACT_KEY = "athm_contact";
    public static final String ATHM_TRANSFER_AMOUNT_KEY = "athm_transfer_amount";
    public static final String ATHM_TRANSFER_PHONE_KEY = "athm_transfer_phone";
    public static final String ATHM_PHONE_CODE_KEY = "athm_phone_code";
    public static final String ATHM_PHONE_NUMBER_RESPONSE_KEY = "athm_phone_number_response";
    public static final String ATHM_ENROLL_CARD_RESPONSE_KEY = "athm_enroll_card_response";
    public static final String ATHM_SSO_TOKEN_KEY = "athm_sso_token";
    public static final String ATHM_SSO_BOUND_KEY = "athm_sso_bound_key";
    public static final String USER_ID_KEY = "userIdKey";
    public static final String OB_ID_KEY = "onlineBankingIdKey";
    public static final String OB_BUNDLE_KEY = "onlineBankingBundleKey";

    public static final String REGEX_ALL_DIGITS = "[0-9]+";

    public static final String MOBILE_CASH_SUCCESS = "TRX_FOUND";
    public static final String MOBILE_CASH_DELETE_SUCCESS = "SUCCESS";
    public static final String MOBILE_CASH_CODE_SUCCESS = "SUCCESS";
    public static final String MOBILE_CASH_CODE_PREFIX = "242762";
    public static final String KEY_MOBILE_CASH_TRX = "MC_TRX";
    public static final String MOBILE_CASH_TRX_INFO_KEY = "MOBILE_CASH_TRX_INFO_KEY";
    public static final String MOBILE_CASH_WS_DATE_FORMAT = "MM/dd/yyyy HH:mm";
    public static final String MOBILE_CASH_TIME_FORMAT = "h:mm a";
    public static final int MC_SELECT_ACCOUNT_REQUEST_CODE = 1330;
    public static final int MC_SELECT_CARD_REQUEST_CODE = 1331;
    public static final String SELECT_CARD_WARNING = "1332";
    public static final String MC_REDEEM_SUCCESS_TRX = "1333";
    public static final String SELECT_CARD_TITLE = "1334";
    public static final int MC_MIN_BALANCE_AMOUNT = 20;
    public static final int MC_MAX_BALANCE_AMOUNT = 300;
    public static final String FINGERPRINT_STORAGE_DATE_FORMAT = "MM/dd/yyyy HH:mm";

    public static final long EASYCASH_FINGERPRINT_WAITHOURS = 72;

    public static final String MC_BLACKLIST_SENDER = "SENDERPHONE_IN_BLACKLIST";
    public static final String MC_BLACKLIST_RECEIVER = "RECEIVERPHONE_IN_BLACKLIST";
    public static final String MC_BLACKLIST_PHONE = "PHONE_IN_BLACKLIST";

    public static final String INFORMATIVE_TITLE_ID = "title";
    public static final String INFORMATIVE_TEXT_ID = "text";
    public static final String INFORMATIVE_BUTTON_ID = "button";
    public static final String INFORMATIVE_IMAGE_ID = "image";

    public static final int DATE_COMPARE_YESTERDAY = 0;
    public static final int DATE_COMPARE_TODAY = 1;
    public static final int DATE_COMPARE_TOMOROW=2;
    public static final int DATE_COMPARE_MORE= 3;

    /* OOB AUTHENTICATION   */
    public static final String OOB_ACTION_NAME = "loginoob";
    public static final String OOB_USER_NAME = "username";
    public static final String OOB_HAS_ALTPHONE = "hasAltPhone";
    public static final String OOB_CHALLENGE_TYPE = "challengeType";
    public static final String OOB_PAGE_NAME = "responder_message";
    public static final String OOB_PHONE = "phone";
    public static final String OOB_DATA = "oobData";
    public static final String OOB_RSA_BLOCKED = "rsablocked";
    public static final String OOB_VALIDATE_SMSCODE = "VALIDATE_SMSCODE";
    public static final String OOB_SEND_SMSCODE = "SEND_SMSCODE";
    public static final String OOB_SEND_ALT_PHONE = "SEND_ALTPHONE";
    public static final String OOB_VALIDATE_CALLCODE = "VALIDATE_CALLCODE";
    public static final String OOB_CODE_VOICE_CALL = "code";
    public static final String OOB_CALL_TYPE_CHALLENGE = "OOBPHONE";
    public static final String OOB_CALL_PHONE = "CALL_PHONE";
    public static final String OOB_CALL_ALT_PHONE = "CALL_ALTPHONE";
    public static final String OOB_NO_PHONE = "NO_PHONE";

    /* OPAC Autentication */
    public static final String CAN_OPEN_ACCOUNT = "canOpenAccount";
    public static final String IS_FOREING_CUSTOMER = "isForeignCustomer";
    public static final String MBDP_PDF_VIEWER_ONLINE = "https://docs.google.com/viewer?url=";

    /* My Plan Access Platform */
    public static final String ACCS_PLATFRM_URL = "http://popular.com/401k"; //Untranslatable URL for My Plain Access Platform

    
    // PERMISSIONS
    public static final int MAX_NO_RUNTIME_PERMISSION_VERSION = Build.VERSION_CODES.M;
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public static final int REQUEST_CODE_ASK_PERMISSIONS_LOCATOR = 124;

    public static final String[] INTRO_PERMISSIONS = {
            android.os.Build.VERSION.SDK_INT  <= Build.VERSION_CODES.Q ?
                    android.Manifest.permission.READ_PHONE_STATE :
                    android.Manifest.permission.READ_PHONE_NUMBERS,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION};

    public static final String[] LOCATOR_PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION};

    public static final String[] WEBSERVICE_PERMISSIONS = {
            android.os.Build.VERSION.SDK_INT  <= Build.VERSION_CODES.Q ?
                    android.Manifest.permission.READ_PHONE_STATE :
                    android.Manifest.permission.READ_PHONE_NUMBERS
    };

    public static final String[] CONTACTS_PERMISSIONS = {
            android.Manifest.permission.READ_CONTACTS
    };

    public static final String[] CAMERA_PERMISSIONS = {
            android.Manifest.permission.CAMERA
    };

    public static final String[] EASYCASH_REDEEM_PERMISSIONS = {
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.CAMERA};

    public static final String PASSWORD_KEY = "&)&#&&&@^$";
    public static final String FINGERPRINT_PREFERENCE_KEY = "fingerprint-preference";
    public static final String FINGERPRINT_DATE_KEY = "fingerprint-date";
    public static final String FINGERPRINT_CAMPAIGN_KEY = "fingerprint-campaign";
    public static final String CUSTOM_IMAGE_CAMPAIGN_KEY = "custom-image-key";
    public static final String REMEMBER_USERNAME_DEFAULT = "remember-username-default";
    public static final String ATHM_SSO_WELCOME_SPLASH_KEY = "athm-sso-welcome-splash";
    public static final String CUSTOM_URL_KEY = "custom_url";
    public static final String PUSH_TOKEN_KEY = "push-token-key";
    public static final String PUSH_TOGGLE_STATE = "push-toggle-state";
    public static final String PUSH_TOKEN_INITIAL_SAVED = "push-inital-saved";
    public static final String SH_KEY = "key-stored-sh";

    public static final long ATHM_PROMPT_DURATION = 2000;

    public static final int WRONG_PASSWORD = 3214;
    public static final String EXTERNAL_URL = "open_external_browser";

    public static final String NEW_LINE = "\n";

    public static final String RECENT_CONTACTS_KEY = "recents-type";
    public static final String RECENT_CONTACTS_KEY_ATHM = "recents-athm-key";
    public static final String RECENT_CONTACTS_KEY_EASYCASH = "recents-easycash-key";

    public static final long EC_NONCUST_REFRESHRATE_SECS = 30;
    public static final long EC_NONCUST_REFRESHRATE_ENT_SECS = 30;
    public static final String ONOFF_RELOAD_INDICATOR = "onoff-reload-indicator";
	
	public static final String RSA_OOB_ENROLLED = "oobEnrolled";
    public static final String RSA_QUESTIONS = "questionrsa";
    public static final String RSA_BLOCKED = "RSA_BLOCKED";

    //refresh balance
    public static final long DIALOG_DURATION = 5000; //time showed
    public static final String MAKE_PAYMENT = "makePayment"; //url contain make payment
    public static final String MAKE_TRANSFER = "makeTransfer"; //url contain make transfer


    public static final String SHARED_WIDGET_CUSTOMER_TOKEN = "widget_customer_token";
    public static final String SHARED_WIDGET_DEVICE = "widget_device_identifier";
    public static final String SHARED_WIDGET_BALANCE_FAIL = "maintenance_last_balance_fail";
    public static final String SHARED_WIDGET_BALANCE = "widget_balances";
    public static final String SHARED_WIDGET_LAST_UPDATED = "widget_lastupdatedon";

    public static final String WIDGET_CONTENT_DIVIDER = "><";
    public static final String WIDGET_LAST_UPDATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
    public static final String WIDGET_EN_DATE_FORMAT = "MMMM d, yyyy h:mm a";
    public static final String WIDGET_ES_DATE_FORMAT = "d 'de' MMMM 'de' yyyy h:mm a";

    public static final String WIDGET_CONTENT_SPLIT = ";";
    public static final String WIDGET_RED_BALANCE = "R";
    public static final String WIDGET_RED_BALANCE_WITH_DELIMITER = "R;";
    public static final String WIDGET_BLUE_BALANCE_WITH_DELIMITER = "B;";

    public static final String MAINTENANCE_TYPE = "MAINTENANCE_TYPE";
    public static final String MAINTENANCE = "maintenance";
    public static final String HIGH_VOLUME = "high_volume";

    public static final String SHARED_WIDGET_USERNAME = "widget_username";
    public static final long WIDGET_MAX_UPDATE_TIME = 600000;


    //Notification Center
    public static final String NEW_NOTIFICATIONS = "newNotifications";
    public static final String DISMISS_NEW_NOTIFICATION_KEY = "dismiss_new_notification";

    /**
     * Non Transactional Accounts
     */
    public static final String allowedNonTransactionalAccounts[] = {"MLA"};

    /**
     * Class that defines constants related to displayed dialogs
     */
    public static class MiBancoDialogId {

        public static final int ERROR = 0;
        public static final int ERROR_FINISH = 1;
        public static final int CONFIRMATION = 2;
        public static final int CONFIRMATION_BACK = 3;
        public static final int INVALID_PHONE_NUMBER = 4;
        public static final int ATHM_DOWNTIME = 5;
        public static final int ATHM_BLOCKED = 6;
        public static final int ATHM_ALERT_ERROR = 7;
        public static final int ATHM_TRANSFER_CONFIRMATION = 8;
        public static final int ATHM_TRANSFER_ERROR = 9;

        private MiBancoDialogId() {
        }
    }

    private MiBancoConstants() {
    }


    public static final String KEY_SHARED_LITE_ENROLLMENT_TOKEN = "lite-enrollment-token";
    public static final String KEY_ENROLL_LITE_IS_CUSTOMER = "lite-enrollment-customer";
    public static final String KEY_ENROLL_LITE_NEXT_STEP_SMS = "lite-enrollment-next-step-sms";

    public static final String KEY_ENROLL_LITE_FINGERPRINT = "lite-enrollment-fingerprint";
    public static final int KEY_ENROLL_LITE_FP = 3323;

    public static final String TEST_ENROLL_LITE_NONCUST_SUBMIT_INFO =
            " {\n" +
                    "    \"responder_name\": \"liteEnrollmentVerification\",\n" +
                    "    \"responder_message\": \"lite_enrollment_verification\",\n" +
                    "\t\n" +
                    "    \"content\": {\n" +
                    "\t\t\"error\":\"false\",\n" +
                    "\t\t\"status\":\"203\"\n" +
                    "\t}\n" +
                    " }";

    public static final String TEST_MOBILE_PHONE_PROVIDERS =
            "{\n" +
                    "    \"responder_name\": \"mobilePhoneProviders\",\n" +
                    "    \"responder_message\": \"mobile_phone_providers\",\n" +
                    "    \"content\": {\n" +
                    "\"providers\":[\n" +
                    "{\"name\":\"AT&T\",\"value\":\"US3\"},\n" +
                    "{\"name\":\"T-Mobile\",\"value\":\"US4\"},\n" +
                    "{\"name\":\"Claro\",\"value\":\"PR7\"},\n" +
                    "{\"name\":\"Sprint\",\"value\":\"US4\"},\n" +
                    "{\"name\":\"Open Mobile\",\"value\":\"PR1\"}]\n" +
                    "\t}\n" +
                    " }";

    public static final String TEST_IS_CUSTOMER_LITE_ENROLLED =
            " {\n" +
                    "    \"responder_name\": \"isCustomerLiteEnrolled\",\n" +
                    "    \"responder_message\": \"is_customer_lite_enrolled\",\n" +
                    "\t\n" +
                    "    \"content\": {\n" +
                    "\t\t\"status\":\"208\"\n" +
                    "\t}\n" +
                    " }";

    public static final String TEST_ENROLL_LITE_CUST_SUBMIT_INFO =
            " {\n" +
                    "    \"responder_name\": \"liteCustomerEnrollmentInfoSubmit\",\n" +
                    "    \"responder_message\": \"lite_customer_enrollment_info_submit\",\n" +
                    "\t\n" +
                    "    \"content\": {\n" +
                    "\t\t\"error\":\"false\",\n" +
                    "\t\t\"status\":\"203\"\n" +
                    "\t}\n" +
                    " }";

    public static final String TEST_HAS_MOBILE_PHONE_IN_ALERTS =
            " {\n" +
                    "    \"responder_name\": \"hasMobilePhoneAlerts\",\n" +
                    "    \"responder_message\": \"has_mobile_phone_alerts\",\n" +
                    "\t\n" +
                    "    \"content\": {\n" +
                    "\t\t\"alertsPhoneNumber\":\"9392085201\"\n" +
                    "\t}\n" +
                    " }";

    public static final String TEST_GENERATE_SMS_CODE =
            "{\n" +
                    "    \"responder_name\": \"generateSmscode\",\n" +
                    "    \"responder_message\": \"generate_sms_code\",\n" +
                    "\t\"content\": {\n" +
                    "\t\t\"error\": \"false\",\n" +
                    "\t\t\"status\": \"204\"\n" +
                    "\t}\n" +
                    " }";

    public static final String TEST_RESEND_SMS_CODE =
            "{\n" +
                    "    \"responder_name\": \"generateSmscode\",\n" +
                    "    \"responder_message\": \"generate_sms_code\",\n" +
                    "\t\"content\": {\n" +
                    "\t\t\"error\": \"false\",\n" +
                    "\t\t\"status\": \"204\"\n" +
                    "\t}\n" +
                    " }";

    public static final String TEST_VALIDATE_SMS_CODE =
            " {\n" +
                    "    \"responder_name\": \"validateSmsCode\",\n" +
                    "    \"responder_message\": \"validate_sms_code\",\n" +
                    "\t\n" +
                    "    \"content\": {\n" +
                    "\t\t\"error\":\"false\",\n" +
                    "\t\t\"status\":\"207\"\n" +
                    "\t}\n" +
                    " }";

    public static final String TEST_CREATE_LITE_PROFILE =
            " {\n" +
                    "    \"responder_name\": \"createLiteProfile\",\n" +
                    "    \"responder_message\": \"create_lite_profile\",\n" +
                    "\t\n" +
                    "    \"content\": {\n" +
                    "\t\t\"error\":\"false\",\n" +
                    "\t\t\"status\":\"200\",\n" +
                    "\t\t\"token\":\"12345\"\n" +
                    "\t}\n" +
                    " }";

    public static final String TEST_ON_OFF =
            " {\n" +
                    "    \"responder_name\": \"mobileathonoff\",\n" +
                    "    \"responder_message\": \"mobile_ath_onoff\",\n" +
                    "\t\n" +
                    "    \"content\": {\n" +
                    "\t\t\"plastics\": [\n" +
                    "\t\t{\n" +
                    "\t\t\t\"plasticNumber\":\"x0347\",\n" +
                    "\t\t\t\"plasticEmbossedName\":\"BRUCE E MADAGOSKY\",\n" +
                    "\t\t\t\"plasticIsOff\":\"false\"\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"plasticNumber\":\"x0347\",\n" +
                    "\t\t\t\"plasticEmbossedName\":\"BRUCE E MADAGOSKY\",\n" +
                    "\t\t\t\"plasticIsOff\":\"false\"\n" +
                    "\t\t}\n" +
                    "\t\t]\n" +
                    "\t}\n" +
                    " }";

    /* Argument key for full text footer */
    public static final String FOOTER_FULL_TEXT_KEY = "footerFullText";//serves as a key for fragments using parameters
    public static final String CASH_REWARDS_REDEMPTION_CONFIGURATION_RESPONDER= "cash_rewards_redemption_configuration";
    public static final String CASH_REWARDS_REDEMPTION_CONFIRMATION_RESPONDER= "cash_rewards_redemption_confirmation";
    public static final String CASH_REWARDS_REDEMPTION_RESULT_RESPONSE ="cash_rewards_redemption_result";
    public static final String CASH_REWARDS_REDEMPTION_MODEL="CASH_REWARDS_REDEMPTION_MODEL";
    public static final String ACCOUNT_DEPOSIT_INFO = "ACCOUNT_DEPOSIT_INFO";
    public static final String DESKTOP_USER_AGENT = "DESKTOP_USER_AGENT";
    public static final String ACCOUNT_CREDIT_CARD_TYPE = "CCA";

    /*MBCC Products*/
    public static final String POPULAR_MARKETPLACE_IMAGES_API = "https://apps.popular.com/mobile/api/mi-banco/products-marketplace.json";
    
    public static final String COOKIE_PREFERENCE_VIEW = "cookiePreference"; //
    
    public static final String RSA_COOKIE = "RSA_COOKIE";

    public static final String SHOW_BOTTOM_NAVIGATION_BAR = "SHOW_BOTTOM_NAVIGATION_BAR";

}
