package com.popular.android.mibanco;

import com.google.gson.reflect.TypeToken;
import com.popular.android.mibanco.util.GsonUtils;
import com.popular.android.mibanco.util.PreferencesUtils;
import com.popular.android.mibanco.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * Class that manages user preferences
 */
public class MiBancoPreferences {

    public static final String ATHM_RECENTS_LIST_PREFS_KEY_PREFIX = "athm_recents_list";
    public static final String EASYCASH_RECENTS_LIST_PREFS_KEY_PREFIX = "easyCash_recents_list";

    private static boolean MI_BANCO_FLAG_MBSFE_291 = false;
    private static boolean NEW_NOTIFICATIONS_FLAG = false;

    private static HashMap<String, String> opac = new HashMap<>();

    public static ArrayList<String> getAthmRecentContacts(String username) {
        if(Utils.isBlankOrNull(username)){
            return new ArrayList<>();
        }
        return GsonUtils.getGsonInstance().fromJson(PreferencesUtils.getPrefsString(Utils.sha256(ATHM_RECENTS_LIST_PREFS_KEY_PREFIX + username), "[]"), new TypeToken<ArrayList<String>>() {
        }.getType());
    }

    public static void setAthmRecentContacts(String username, ArrayList<String> recentsList) {
        PreferencesUtils.putPrefsStringAsync(Utils.sha256(ATHM_RECENTS_LIST_PREFS_KEY_PREFIX + username), GsonUtils.getGsonInstance().toJson(recentsList));
    }

    public static ArrayList<String> getEasyCashRecentContacts(String username) {
        return GsonUtils.getGsonInstance().fromJson(PreferencesUtils.getPrefsString(Utils.sha256(EASYCASH_RECENTS_LIST_PREFS_KEY_PREFIX + username), "[]"), new TypeToken<ArrayList<String>>() {
        }.getType());
    }

    public static void setEasyCashRecentContacts(String username, ArrayList<String> recentsList) {
        PreferencesUtils.putPrefsStringAsync(Utils.sha256(EASYCASH_RECENTS_LIST_PREFS_KEY_PREFIX + username), GsonUtils.getGsonInstance().toJson(recentsList));
    }


    public static void addAthmRecentPhoneNumber(String username, String rawPhoneNumber, String recentsType) {

        if(Utils.isBlankOrNull(username) || Utils.isBlankOrNull(rawPhoneNumber) || Utils.isBlankOrNull(recentsType)){
            return;
        }

        ArrayList<String> recentPhoneNumbers = new ArrayList<>();
        String preferencesKey = "";
        if(MiBancoConstants.RECENT_CONTACTS_KEY_ATHM.equalsIgnoreCase(recentsType)){
            recentPhoneNumbers = getAthmRecentContacts(username);
            preferencesKey = ATHM_RECENTS_LIST_PREFS_KEY_PREFIX;

        }else if(MiBancoConstants.RECENT_CONTACTS_KEY_EASYCASH.equalsIgnoreCase(recentsType)){
            recentPhoneNumbers = getEasyCashRecentContacts(username);
            preferencesKey = EASYCASH_RECENTS_LIST_PREFS_KEY_PREFIX;

        }

        LinkedHashSet<String> recentPhoneNumbersSet = new LinkedHashSet<>(recentPhoneNumbers);
        if (recentPhoneNumbersSet.contains(rawPhoneNumber)) {
            recentPhoneNumbers.remove(rawPhoneNumber);
        }
        recentPhoneNumbers.add(0, rawPhoneNumber);

        ArrayList<String> updatedRecentsList = new ArrayList<>();
        int recentsCount = 0;
        for (String recentPhoneNumber : recentPhoneNumbers) {
            if (recentsCount++ < MiBancoConstants.ATHM_MAX_RECENT_NUMBERS) {
                updatedRecentsList.add(recentPhoneNumber);
            } else {
                break;
            }
        }
        PreferencesUtils.putPrefsStringAsync(Utils.sha256(preferencesKey + username), GsonUtils.getGsonInstance().toJson(updatedRecentsList));
    }

    public static String getLoggedInUsername() {
        if( App.getApplicationInstance().getCurrentUser() != null) {
            return App.getApplicationInstance().getCurrentUser().getUsername();
        }else{
            return "";
        }
    }

    public static final boolean getMiBancoFlagMbsfe291() {
        return MI_BANCO_FLAG_MBSFE_291;
    }

    public static final void setMiBancoFlagMbsfe291(boolean miBancoFlagMbsfe291) {
        MI_BANCO_FLAG_MBSFE_291 = miBancoFlagMbsfe291;
    }

    public static HashMap<String, String> getOpac() {
        return opac;
    }

    public static void setOpac(HashMap<String, String> opac) {
        MiBancoPreferences.opac = opac;
    }

    public static boolean isNewNotificationsFlag() {
        return NEW_NOTIFICATIONS_FLAG;
    }

    public static void setNewNotificationsFlag(boolean newNotificationsFlag) {
        NEW_NOTIFICATIONS_FLAG = newNotificationsFlag;
    }
}