package com.popular.android.mibanco;

import android.content.Context;
import android.content.SharedPreferences;

import com.popular.android.mibanco.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by et55498
 * 5/4/2017.
 */

public class FeatureFlags {
    public static boolean CASH_DROP_ENABLED() { return FeatureFlags.getInstance().getValueForFlag("CASH_DROP_ENABLED");}
    public static final boolean CASH_DROP_FINGERPRINT_REQUIRED() {return CASH_DROP_ENABLED() && FeatureFlags.getInstance().getValueForFlag("CASH_DROP_FINGERPRINT_REQUIRED");}

    public static final boolean MBCA_104() { return FeatureFlags.getInstance().getValueForFlag("MBCA_104");}
    public static final boolean MBMT_417() { return FeatureFlags.getInstance().getValueForFlag("MBMT_417");}
    public static final boolean MBMT_511() { return FeatureFlags.getInstance().getValueForFlag("MBMT_511");}

    public static final boolean ADD_PAYEES() { return FeatureFlags.getInstance().getValueForFlag("ADD_PAYEES");}
    public static final boolean MASK_USERNAME(){return FeatureFlags.getInstance().getValueForFlag("MASK_USERNAME");}

    public static final boolean MBSD_1876() { return FeatureFlags.getInstance().getValueForFlag("MBSD_1876");}
    public static final boolean MBSD_1878() { return FeatureFlags.getInstance().getValueForFlag("MBSD_1878");}
    public static final boolean ONOFF() { return FeatureFlags.getInstance().getValueForFlag("ONOFF");}
    public static final boolean TEST_NO_INTERNET() { return FeatureFlags.getInstance().getValueForFlag("TEST_NO_INTERNET");}

    public static final boolean SDG_WIFI() { return FeatureFlags.getInstance().getValueForFlag("SDG_WIFI");}
    public static final boolean MBMT_477() { return FeatureFlags.getInstance().getValueForFlag("MBMT_477");}
    public static final boolean EBILLS_PERMID() { return FeatureFlags.getInstance().getValueForFlag("EBILLS_PERMID");}
    public static final boolean CUSTOM_LOGIN_IMAGE() { return FeatureFlags.getInstance().getValueForFlag("CUSTOM_LOGIN_IMAGE");}

    public static final boolean MBSFE_1735() { return FeatureFlags.getInstance().getValueForFlag("MBSFE_1735_RESET_PASSWORD_FIELD");}

    /** Flag for tsys loyalty rewards features */
    public static final boolean CASH_REWARDS() { return FeatureFlags.getInstance().getValueForFlag("CASH_REWARDS");}

    public static final boolean RSA_ENROLLMENT() { return FeatureFlags.getInstance().getValueForFlag("RSA_ENROLLMENT");}

    public static final boolean PUSH_WELCOME_SPLASH() { return FeatureFlags.getInstance().getValueForFlag("PUSH_WELCOME_SPLASH");}

    /**
     * MBDP-2519-INI
     * @author ehidalgo
     * @return boolean
     **/
    public static final boolean MBDP_MARKETPLACE() { return FeatureFlags.getInstance().getValueForFlag("MBDP_MARKETPLACE");}

    public static final boolean NOTIFICATION_CENTER() {
        boolean result;
        if (Utils.isOnsenSupported()) {
            result = FeatureFlags.getInstance().getValueForFlag("NOTIFICATION_CENTER");
        } else {
            result = false;
        }
        return result;
    }

    private FeatureFlags() {}

    private static boolean overrideSavedFlags = !MiBancoEnviromentConstants.TEST_ENV;
    private static FeatureFlags INSTANCE = null;

    private final Context mContext = App.getApplicationInstance().getBaseContext();
    private SharedPreferences prefs = Utils.getSecuredSharedPreferences(mContext);

    private JSONObject flagsDictionary;

    public static FeatureFlags getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FeatureFlags();

            INSTANCE.flagsDictionary = INSTANCE.getFlagsDictionary();

            if (INSTANCE.flagsDictionary.length() == 0 || overrideSavedFlags) {
                INSTANCE.saveValueForFlag("RSA_ENROLLMENT", true);
                INSTANCE.saveValueForFlag("CASH_DROP_ENABLED", true);
                INSTANCE.saveValueForFlag("CASH_DROP_FINGERPRINT_REQUIRED", true);
                INSTANCE.saveValueForFlag("MBCA_104", true);
                INSTANCE.saveValueForFlag("MBMT_417", true);
                INSTANCE.saveValueForFlag("MBMT_511", true);
                INSTANCE.saveValueForFlag("MBSD_1876", true);
                INSTANCE.saveValueForFlag("MBSD_1878", true);
                INSTANCE.saveValueForFlag("ONOFF", false);
                INSTANCE.saveValueForFlag("TEST_NO_INTERNET", false);
                INSTANCE.saveValueForFlag("ADD_PAYEES", true);
                INSTANCE.saveValueForFlag("SDG_WIFI_HOST", false);
                INSTANCE.saveValueForFlag("MBMT_477", false);
                INSTANCE.saveValueForFlag("MASK_USERNAME",true);
                INSTANCE.saveValueForFlag("EBILLS_PERMID", true);
                INSTANCE.saveValueForFlag("CUSTOM_LOGIN_IMAGE", true);
                INSTANCE.saveValueForFlag("MBSFE_1735_RESET_PASSWORD_FIELD", true);
                INSTANCE.saveValueForFlag("CASH_REWARDS", true);
                INSTANCE.saveValueForFlag("PUSH_WELCOME_SPLASH" , true);
                INSTANCE.saveValueForFlag("NOTIFICATION_CENTER", true);
            }
        }
        return(INSTANCE);
    }

    private void saveFlagsDictionary() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("flagsJsonObject", flagsDictionary.toString());
        editor.commit();
    }
    private JSONObject getFlagsDictionary() {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(prefs.getString("flagsJsonObject", "{}"));
        } catch (JSONException e) {
            jsonObject = new JSONObject();
            e.printStackTrace();
        }
        return jsonObject;
    }

    public ArrayList<String> getFlagsKeys () {
        ArrayList<String> arrayList = new ArrayList<String>();
        for(Iterator<String> iter = flagsDictionary.keys(); iter.hasNext();) {
            String key = iter.next();
            arrayList.add(key);
        }

        return  arrayList;
    }

    public JSONObject getFlagInfo(final String key) {
        JSONObject jsonObject;

        try {
            jsonObject = flagsDictionary.getJSONObject(key);
        } catch (JSONException e) {
            jsonObject = new JSONObject();
        }

        return jsonObject;
    }

    public void saveValueForFlag(final String key, boolean value) {
        saveInfoForFlag(key, value, null);
    }

    public void saveInfoForFlag(final String key, boolean value , String desc) {
        JSONObject flagObject = getFlagInfo(key);

        try {
            flagObject.put( "value", value);
            flagObject.put("desc", desc);

            flagsDictionary.put(key, flagObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        saveFlagsDictionary();
    }

    public boolean getValueForFlag(String key) {
        System.out.println("Feature Flags getValueForFlag " + key);

        return getFlagInfo(key).optBoolean("value", false);
    }


    public String getDescForFlag(String key) {
        return getFlagInfo(key).optString("desc", key);
    }
}
