package com.popular.android.mibanco.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import com.popular.android.mibanco.MiBancoConstants;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Device print generator.
 */
public class DeviceFingerprint {

    private final static int MILIS_IN_SECOND = 1000;

    private final static int MINUTES_IN_HOUR = 60;

    private final static int SECONDS_IN_MINUTE = 60;

    private final static int UNIQUE_ID_SHIFT = 32;

    private final Context context;

    public DeviceFingerprint(final Context context) {
        this.context = context;
    }

    private String getBrowser(final Context context) {
        return System.getProperty("http.agent");
    }

    private String getCookie() {
        return "1";
    }

    /**
     * Creates device's unique ID using devices's fingerprint, TelephonyManager ID and android.provider.Settings.Secure.ANDROID_ID.
     * 
     * @return device's unique ID String
     */
    public String getDeviceId() {

        String deviceId = Utils.getStringContentFromShared(context,MiBancoConstants.KEY_DEVICE_ID);

        if(Utils.isBlankOrNull(deviceId)) {
            try {
                //Generate unsaved device ID and save
                List<String> missingPermissions = PermissionsManagerUtils.missingPermissions(context, Arrays.asList(MiBancoConstants.WEBSERVICE_PERMISSIONS));
                if (missingPermissions.size() == 0) {
                    final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    @SuppressLint("MissingPermission")
                    final String tmDevice = "" + tm.getDeviceId();
                    final String androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                    deviceId = new UUID(getDevicePrint().hashCode(), (long) tmDevice.hashCode() << UNIQUE_ID_SHIFT | androidId.hashCode()).toString();
                }

                //Second validation in case de previous process still returns blank or null for any reason
                if (missingPermissions.size() > 0 || Utils.isBlankOrNull(deviceId)) {
                    deviceId = UUID.randomUUID().toString(); //Generated code
                }
                Utils.saveStringContentToShared(context, MiBancoConstants.KEY_DEVICE_ID, deviceId);

            }catch(Exception e){ // In case something wrong from TelephonyManager process
                deviceId = UUID.randomUUID().toString(); //Generated code
                Utils.saveStringContentToShared(context, MiBancoConstants.KEY_DEVICE_ID, deviceId);
            }
        }
        return deviceId;
    }

    public String getDevicePrint() {
        return String.format("version=%s&pm_fpua=%s&pm_fpsc=%s&pm_fpsw=%s&pm_fptz=%s&pm_fpln=%s&fpm_fpjv=%s&pm_fpco=%s", getVersion(), getBrowser(context), getDisplay(context),
                getSoftware(), getTimezone(), getLanguage(), getJava(), getCookie());
    }

    private String getDisplay(final Context context) {
        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();

        return String.format("%dx%d %s %s", display.getWidth(), display.getHeight(), Build.MANUFACTURER, Build.MODEL);
    }

    private String getJava() {
        return "0";
    }

    private String getLanguage() {
        return "lang=" + Locale.getDefault().getLanguage() + "_US";
    }

    private String getSoftware() {
        return String.format("Android %s|%s %s|%s", Build.VERSION.RELEASE, Build.MANUFACTURER, Build.MODEL, Build.PRODUCT);
    }

    private String getTimezone() {
        final TimeZone timeZone = TimeZone.getDefault();
        final Date now = new Date();
        final int offsetFromUtc = timeZone.getOffset(now.getTime());

        return String.format("%d", offsetFromUtc / MILIS_IN_SECOND / SECONDS_IN_MINUTE / MINUTES_IN_HOUR);
    }

    private String getVersion() {
        return "1";
    }
}
