package com.popular.android.mibanco;

import android.content.Context;

import com.popular.android.mibanco.util.Utils;

/**
 * Class that manage constants with different values for specifics environments based on selected Build Variant.
 */

public class MiBancoEnviromentConstants {

    /******
     * Public Constant
     */
    public static final boolean TEST_ENV = TEST_ENV();
    public static final boolean QA_ENV = QA_ENV();


    public static final String USER_AGENT_STRING = USER_AGENT_STRING();
    public static final String USER_AGENT_WEBVIEW_STR = USER_AGENT_WEBVIEW_STRING();
    public static final String AGENT_USER_DESKTOP_STR = AGENT_USER_DESKTOP_STRING();

    public static final String API_URL = API_URL();
    public static final String API_SSL_PORT = API_SSL_PORT();
    public static final String COOKIE_STRING = COOKIE_STRING();


    /**
     * Private Constant selector function
     */

    private static boolean TEST_ENV() {
        switch (BuildConfig.BUILD_TYPE) {
            case "debug":
                return true;
            default:
                return false;
        }
    }

    private static boolean QA_ENV() {
        switch (BuildConfig.BUILD_TYPE) {
            case "qa":
                return true;
            default:
                return false;
        }
    }

    private static String USER_AGENT_STRING() {
        switch (BuildConfig.BUILD_TYPE) {
//            case "pilot":
//                return USER_AGENT_STRING_PILOT;
            default:
                return USER_AGENT_STRING_DEFAULT;
        }
    }

    private static String USER_AGENT_WEBVIEW_STRING() {

        return USER_AGENT_STRING_WEBVIEW;
    }

    private static String AGENT_USER_DESKTOP_STRING() {

        return USER_AGENT_STRING_DESKTOP;
    }

    private static String API_URL() {
        switch (BuildConfig.BUILD_TYPE) {
            case "pilot":
                return API_URL_PILOT;
            case "release":
                return API_URL_PRODUCTION;
            default:
                return getSavedUrl();
        }
    }

    private static String getSavedUrl() {
        Context mContext = App.getApplicationInstance().getBaseContext();
        switch (BuildConfig.BUILD_TYPE) {
            case "debug":
                String pref = Utils.getSecuredSharedPreferences(mContext).getString(MiBancoConstants.CUSTOM_URL_KEY, "");

                if (Utils.isBlankOrNull(pref)) {
                    pref = mContext.getString(mContext.getResources().getIdentifier(mContext.getString(R.string.api_url_dev), "string", mContext.getPackageName()));
                }

                Utils.changeHostIp(pref);
                return pref;
            default:

                return mContext.getString(mContext.getResources().getIdentifier(mContext.getString(R.string.api_url_qa), "string", mContext.getPackageName()));
        }
    }

    private static String API_SSL_PORT() {
        switch (BuildConfig.BUILD_TYPE) {
            case "debug":
                return API_SSL_PORT_QA;
            default:
                return API_SSL_PORT_PRODUCTION;
        }
    }

    private static String COOKIE_STRING() {
                return COOKIE_STRING_DEVELOP;
    }

    /**
     * Private Constants
     */

    private static final String USER_AGENT_STRING_PILOT = "iPad App; MiBanco.app; JSON Client; en-us";
    private static final String USER_AGENT_STRING_DEFAULT = "Android App; MiBanco.app; JSON Client; en-us";
    private static final String USER_AGENT_STRING_WEBVIEW = "Android App; MiBanco.app; en-us;";
    private static final String USER_AGENT_STRING_DESKTOP = "Desktop Mobile Web View; MiBanco.app; en-us";
    private static final String API_URL_PRODUCTION = "https://mobile.bancopopular.com/cibp-web/";
    private static final String API_URL_PILOT = "https://piloto.bancopopular.com/cibp-web/";
    private static final String API_SSL_PORT_PRODUCTION = "443";
    private static final String API_SSL_PORT_QA = "443";
    private static final String COOKIE_STRING_DEVELOP = "%s=%s; domain=%s; Path=%s";
    private static final String COOKIE_STRING_DEFAULT = "%s=%s; domain=%s; Path=%s; Secure;";

}
