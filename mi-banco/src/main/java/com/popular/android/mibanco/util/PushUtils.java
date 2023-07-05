package com.popular.android.mibanco.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.ws.response.PushTokenRequest;
import com.popular.android.mibanco.ws.response.PushTokenResponse;

import org.apache.commons.lang3.StringUtils;

/**
 * PushUtils class for Push Notifications related logic
 *
 */

public class PushUtils {

    /**
     * Android OS base name
     */
    public static String ANDROID = "Android";
    public static String FCM_CHANNEL_ID = "fcm_fallback_notification_channel";

    public static boolean isPushEnabled() {
        return App.getApplicationInstance().getLoggedInUser().isPushEnabled();
    }

    /**
     * Open notifications settings on user device
     */
    private static void goToNotificationsSettings(Context context) {
        Intent i = new Intent();
        i.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        i.putExtra("app_package", context.getPackageName());
        i.putExtra("app_uid", context.getApplicationInfo().uid);
        i.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    /**
     * Provide a click listener to open notifications settings
     */
    public static View.OnClickListener getNotificationSettingsListener(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNotificationsSettings(context);
            }
        };
    }

    /**
     * Returns if Push Notifications are enabled in device settings
     */
    public static boolean areNotificationsEnabled(Context context) {
        boolean areNotificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled();
        if (areNotificationsEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                NotificationChannel channel = manager.getNotificationChannel(FCM_CHANNEL_ID);
                return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
            }
            return true;
        } else {
            return areNotificationsEnabled;
        }
    }

    /**
     * Save push token to shared preferences
     */
    public static void savePushToken(final Context context, String token) {
        final SharedPreferences.Editor editor = Utils.getSecuredSharedPreferences(context).edit();
        editor.putString(MiBancoConstants.PUSH_TOKEN_KEY, token);
        editor.apply();
    }

    /**
     * Retrieve push token from shared preferences
     */
    public static String getSavedPushToken(final Context context) {
        final SharedPreferences preferences = Utils.getSecuredSharedPreferences(context);
        return preferences.getString(MiBancoConstants.PUSH_TOKEN_KEY, StringUtils.EMPTY);
    }

    /**
     * Save push toggle state to shared preferences
     */
    public static void setPushToggleChecked(final Context context, boolean isChecked, String username) {
        final SharedPreferences.Editor editor = Utils.getSecuredSharedPreferences(context).edit();
        editor.putBoolean(MiBancoConstants.PUSH_TOGGLE_STATE + username, isChecked);
        editor.apply();
    }

    /**
     * Retrieve push toggle state from shared preferences
     */
    public static boolean isPushToggleChecked(final Context context, String username) {
        final SharedPreferences preferences = Utils.getSecuredSharedPreferences(context);
        return preferences.getBoolean(MiBancoConstants.PUSH_TOGGLE_STATE + username, false);
    }

    public static void setInitialPushTokenSaved(final Context context, boolean isSaved, String username) {
        //If Initial is already saved, avoid the set again
        if (isInitialPushTokenSaved(context, username))
            return;

        final SharedPreferences.Editor editor = Utils.getSecuredSharedPreferences(context).edit();
        editor.putBoolean(MiBancoConstants.PUSH_TOKEN_INITIAL_SAVED + username, isSaved);
        editor.apply();
    }

    public static boolean isInitialPushTokenSaved(final Context context, String username) {
        final SharedPreferences preferences = Utils.getSecuredSharedPreferences(context);
        return preferences.getBoolean(MiBancoConstants.PUSH_TOKEN_INITIAL_SAVED + username, false);
    }

    /**
     * Returns the current Push token
     */
    public static String getPushToken(Context context) {
        String token = FirebaseInstanceId.getInstance().getToken();
        return StringUtils.isNotEmpty(token) ? token : PushUtils.getSavedPushToken(context);
    }

    /**
     * Returns the device model (e.g. LGE Nexus 5)
     */
    public static String getDeviceModel() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (StringUtils.startsWithIgnoreCase(model, manufacturer)) {
            return StringUtils.capitalize(model);
        } else {
            return StringUtils.capitalize(manufacturer) + StringUtils.SPACE + model;
        }
    }

    /**
     * Returns the device name (e.g. Armando's Nexus 5) or model if fail (e.g. LGE Nexus 5)
     */
    public static String getDeviceName(Context context) {
        String deviceName = null;
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            try {
                BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
                if (myDevice != null) {
                    deviceName = myDevice.getName();
                }
            } catch (final Exception ex) {
                deviceName = getDeviceModel();
            }
        } else {
            deviceName = getDeviceModel();
        }
        return deviceName;
    }

    /**
     * Updates push token status and toggle
     */
    private static void updatePushTokenOnPermissionChange(final Context context, final SwitchCompat swt) {
        PushTokenRequest request = new PushTokenRequest(context);
        request.setPushToken(PushUtils.getPushToken(context));
        request.setActive(true);
        final String username = App.getApplicationInstance().getCurrentUser().getUsername();

        App.getApplicationInstance().getAsyncTasksManager().PushTask(context, request, false, new ResponderListener() {
            @Override
            public void responder(String responderName, Object data) {
                PushTokenResponse response = (PushTokenResponse) data;
                if (response != null) {
                    if (StringUtils.equals(response.getStatus(), PushTokenResponse.SUCCESS)) {
                        PushUtils.setPushToggleChecked(context, true, username);
                        swt.setChecked(true);
                    } else {
                        swt.setChecked(false);
                        PushUtils.setPushToggleChecked(context, false, username);
                        MobileCashUtils.informativeMessage(context, R.string.mc_service_error_message);
                    }
                } else {
                    swt.setChecked(false);
                    PushUtils.setPushToggleChecked(context, false, username);
                }

            }

            @Override
            public void sessionHasExpired() {
            }
        });
    }

    /**
     * Check if push notifications are enabled after the app comes from background
     */
    public static void checkIfPushIsEnabled(final Context context, LinearLayout warning, SwitchCompat swt, TextView settingsBtn) {

        boolean isSessionActive = App.getApplicationInstance().getCurrentUser() != null;
        if (isSessionActive) {
            boolean isPushToggleChecked = PushUtils.isPushToggleChecked(context, App.getApplicationInstance().getCurrentUser().getUsername());
            if (isPushToggleChecked) {
                if (warning != null) {
                    boolean isPushWarningShowing = App.getApplicationInstance().isPushWarningShowing();
                    if (PushUtils.areNotificationsEnabled(context)) {
                        if (isPushWarningShowing) {
                            warning.setVisibility(View.GONE);
                            App.getApplicationInstance().setPushWarningShowing(false);
                            PushUtils.updatePushTokenOnPermissionChange(context, swt);
                        }
                    } else {
                        if (!isPushWarningShowing) {
                            warning.setVisibility(View.VISIBLE);
                            App.getApplicationInstance().setPushWarningShowing(true);
                            settingsBtn.setClickable(true);
                            settingsBtn.setOnClickListener(PushUtils.getNotificationSettingsListener(context));
                            PushUtils.updatePushTokenOnPermissionChange(context, swt);
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns JS code for handling device back button inside Alerts Section
     */
    public static String getAlertsBackButtonJS() {
        return "var nav = document.querySelector('#navigator');\n" +
                "\t\t\n" +
                "\t\tif (nav.topPage.id == 'alerts-home') {\n" +
                "\t\t\tj('ons-back-button').click();\n" +
                "\t\t} else {\n" +
                "\t\t\tvar confirmDialog = document.querySelector('#confirmation-dialog');\n" +
                "\t\t\tvar dialog = document.querySelector('#alert-dialog');\n" +
                "\t\t\tif (confirmDialog != null) {\n" +
                "\t\t\t\tif (confirmDialog.style.display === 'block') {\n" +
                "\t\t\t\t\tconfirmDialog.hide();\n" +
                "\t\t\t\t}\n" +
                "\t\t\t} else if (dialog != null) {\n" +
                "\t\t\t\tif (dialog.style.display === 'block') {\n" +
                "\t\t\t\t\tdialog.hide();\n" +
                "\t\t\t\t}\n" +
                "\t\t\t} else {\n" +
                "\t\t\t\tnav.popPage();\n" +
                "\t\t\t};\t\n" +
                "\t\t};";
    }

    /**
     * Create Notification Channel for Push
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nManager != null) {
                NotificationChannel currentChannel = nManager.getNotificationChannel(FCM_CHANNEL_ID);
                if (currentChannel == null) {
                    String channelName = context.getString(R.string.fcm_fallback_notification_channel_label);
                    NotificationChannel channel = new NotificationChannel(FCM_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
                    channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                    channel.enableLights(true);
                    channel.setLightColor(Color.RED);
                    channel.enableVibration(true);
                    channel.setShowBadge(false);
                    nManager.createNotificationChannel(channel);
                }
            }
        }
    }
}
