package com.popular.android.mibanco.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.activity.AthmEnroll;
import com.popular.android.mibanco.activity.AthmRedirectSplash;
import com.popular.android.mibanco.activity.AthmTransfer;
import com.popular.android.mibanco.activity.AthmWelcomeSplash;
import com.popular.android.mibanco.activity.Downtime;
import com.popular.android.mibanco.task.AthmTasks;
import com.popular.android.mibanco.ws.response.AthmSSOInfo;

public class ATHMUtils {
    private static String getAthmBundleId(Context context) {
        // ATHM APP bundle Id
        // if needed here you could add the (.debug), (.qa) and (.piloto) at the end to test
        // in the different build variants of the project.
        if (MiBancoEnviromentConstants.TEST_ENV || MiBancoEnviromentConstants.QA_ENV) {
            return context.getString(R.string.athm_sso_bundle_id) + ".qa";
        }
        return context.getString(R.string.athm_sso_bundle_id);
    }

    private static boolean isAthmApkInstalledOrUpdated(Context context) {
        int athmVersionCode = 0;

        // Looking for the ATHM app on the device
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(getAthmBundleId(context));
        Log.v("getAthmBundleId(context) ",getAthmBundleId(context)+ "");
        // Getting the ATHM app version code
        try {
            PackageInfo athmInfo = context.getPackageManager().getPackageInfo(getAthmBundleId(context) ,0);
            athmVersionCode = athmInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Validating that the ATHM app was found and the versioncode is valid
        return (intent != null && athmVersionCode > 121);
    }

    private static void verifySplashViewed(Context context, String token, boolean isBounded) {
        if (!Utils.isAthmWelcomeSplashViewed(context)) {
            // Show Athm SSO Welcome Splash Screen
            final Intent intent = new Intent(context, AthmWelcomeSplash.class);
            intent.putExtra(MiBancoConstants.ATHM_SSO_TOKEN_KEY, token);
            intent.putExtra(MiBancoConstants.ATHM_SSO_BOUND_KEY, isBounded);
            context.startActivity(intent);
        } else {
            verifyAthmAppVersion(context, token, isBounded, false);
        }
    }

    private  static void verifyDowntime(final Context context, AthmSSOInfo result) {
        if(result != null) {
            if ( result.isSSODowntime()  || !App.getApplicationInstance().isGlobalAthmEntitlementEnabled()) {
                BPAnalytics.logEvent(BPAnalytics.EVENT_ATHM_SSO_DOWNTIME);
                final Intent downtimeIntent = new Intent(context, Downtime.class);
                downtimeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                downtimeIntent.putExtra("downtimeMessage", context.getResources().getString(R.string.athm_down_error_text));
                context.startActivity(downtimeIntent);
            } else {
                verifySplashViewed(context, result.getToken(), result.isSsoBound());
            }
        }
    }

    private static void redirectToAthmApk(Context context, String token, boolean isBounded) {
        if (isBounded) {
            BPAnalytics.logEvent(BPAnalytics.EVENT_OPEN_ATHM_WITH_VALID_TOKEN);
        } else {
            BPAnalytics.logEvent(BPAnalytics.EVENT_OPEN_ATHM_WITH_NEW_TOKEN);
        }
        /* ****************************IMPORTANT*****************************************
        ***     Here are the keys that the ATHM app will be waiting for in the intent.
        ***     These keys cannot be changed or the ATHM app will not read the values sent.
        ***     String USER_ID_KEY = "userIdKey";
        ***     String OB_ID_KEY = "onlineBankingIdKey";
        ***     String OB_BUNDLE_KEY = "onlineBankingBundleKey";
        /* ****************************IMPORTANT*****************************************/

        Intent intent = context.getPackageManager().getLaunchIntentForPackage(getAthmBundleId(context));

        // Everything is ok launch ATHM app with the info needed.
        intent.putExtra(MiBancoConstants.USER_ID_KEY, token);
        intent.putExtra(MiBancoConstants.OB_ID_KEY, context.getString(R.string.mb_sso_app_id));
        intent.putExtra(MiBancoConstants.OB_BUNDLE_KEY, "com.popular.android.mibanco");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void getCustomerATHMToken(final Context context, boolean generateToken, AthmTasks.AthmListener<AthmSSOInfo> listener) {
        AthmTasks.getAthmTokenInfo(context, context.getString(R.string.mb_sso_app_token), generateToken, listener);
    }

    public static void getCustomerATHMTokenWithMessage(final Context context, boolean generateToken, AthmTasks.AthmListener<AthmSSOInfo> listener, String message, long duration) {
        AthmTasks.getAthmTokenInfo(context, context.getString(R.string.mb_sso_app_token), message,duration, generateToken, listener);
    }


    public static void redirectToStore(Context context) {
        BPAnalytics.logEvent(BPAnalytics.EVENT_OPEN_PLAY_STORE_ATHM);

        // Opening the PlayStore because ether the app does not exist or is not the correct version.
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + context.getString(R.string.athm_sso_bundle_id)));
            context.startActivity(intent);
        } catch (android.content.ActivityNotFoundException anfe) {
            intent.setData(Uri.parse("market://details?id=" + context.getString(R.string.athm_sso_bundle_id)));
            context.startActivity(intent);
        }
    }

    public static void verifyAthmAppVersion(final Context context, final String token, final boolean isBounded, boolean splashWasShown) {
        //show dialog with custom text if user comes from welcome view
        if (splashWasShown && isAthmApkInstalledOrUpdated(context)){
            openATHMApkWithDelay(context, token, isBounded);

        } else if (!splashWasShown && isAthmApkInstalledOrUpdated(context)) {
            redirectToAthmApk(context, token, isBounded);

        } else {
            BPAnalytics.logEvent(BPAnalytics.EVENT_ATHM_APK_NOT_INSTALLED_OR_UPDATED);
            // If ATHM apk is not installed or updated show redirect splash screen
            final Intent intent = new Intent(context, AthmRedirectSplash.class);
            context.startActivity(intent);
        }
    }
    public static void athmVersionViewDeciderAction(final Context context) {
        if (App.getApplicationInstance().getLoggedInUser().getAthmSso()) {
            //show normal loading message if the athm apk is not installed or welcome screen is not viewed
            if (!App.getApplicationInstance().isGlobalAthmEntitlementEnabled() || !isAthmApkInstalledOrUpdated(context) ||
                    !Utils.isAthmWelcomeSplashViewed(context))
                getCustomerATHMToken(context, true, new AthmTasks.AthmListener<AthmSSOInfo>() {
                    @Override
                    public void onAthmApiResponse(AthmSSOInfo result) {
                        verifyDowntime(context, result);
                    }
                });
            //show loading prompt with custom message
            else
                getCustomerATHMTokenWithMessage(context, true, new AthmTasks.AthmListener<AthmSSOInfo>() {
                    @Override
                    public void onAthmApiResponse(AthmSSOInfo result) {
                        verifyDowntime(context, result);
                    }
                }, context.getString(R.string.athm_redirect_disclaimer), MiBancoConstants.ATHM_PROMPT_DURATION);

        } else {
            // ATHM old flow
            if (App.getApplicationInstance().getCustomerEntitlements() != null && App.getApplicationInstance().getCustomerEntitlements().hasAthm()) {
                final Intent intent = new Intent(context, AthmTransfer.class);
                context.startActivity(intent);
            } else {
                final Intent intent = new Intent(context, AthmEnroll.class);
                context.startActivity(intent);
            }
        }
    }
    public static void openATHMApkWithDelay(final Context context, final String token, final boolean isBounded) {
        final ProgressDialog progressDialog = new ProgressDialog(context, AlertDialog.THEME_HOLO_LIGHT);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(context.getResources().getString(R.string.athm_redirect_disclaimer));
        progressDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                redirectToAthmApk(context, token, isBounded);
            }
        }, MiBancoConstants.ATHM_PROMPT_DURATION);
    }
    public static void athmVersionViewDeciderAction(final Context context, FragmentCallback callback) {
        if (App.getApplicationInstance().getLoggedInUser().getAthmSso()) {
            getCustomerATHMToken(context, true, new AthmTasks.AthmListener<AthmSSOInfo>() {
                @Override
                public void onAthmApiResponse(AthmSSOInfo result) {
                    String fragment = verifyDowntimeNew(context, result);
                    callback.onFragmentDetermined(fragment);
                }
            });
        } else {
            // ATHM old flow
            if (App.getApplicationInstance().getCustomerEntitlements() != null && App.getApplicationInstance().getCustomerEntitlements().hasAthm()) {
                callback.onFragmentDetermined("AthmTransferFragment");
            } else {
                callback.onFragmentDetermined("AthmEnrollFragment");
            }
        }
    }
    public interface FragmentCallback {
        void onFragmentDetermined(String fragment);
    }

    public static String verifyDowntimeNew(final Context context, AthmSSOInfo result) {
        if(result != null) {
            if ( result.isSSODowntime()  || !App.getApplicationInstance().isGlobalAthmEntitlementEnabled()) {
                BPAnalytics.logEvent(BPAnalytics.EVENT_ATHM_SSO_DOWNTIME);
                return "DowntimeFragment";
            } else {
                return verifySplashViewedNew(context);
            }
        }
        return null;
    }

    public static String verifySplashViewedNew(Context context) {
        if (!Utils.isAthmWelcomeSplashViewed(context)) {
            return "AthmWelcomeSplashFragment";
        } else {
            return verifyAthmAppVersionNew(context, false);
        }
    }

    public static String verifyAthmAppVersionNew(final Context context, boolean splashWasShown) {
        if (splashWasShown && isAthmApkInstalledOrUpdated(context)){
            return "redirectToAthmApkFragment";
        } else if (!splashWasShown && isAthmApkInstalledOrUpdated(context)) {
            return "redirectToAthmApkFragment";
        } else {
            BPAnalytics.logEvent(BPAnalytics.EVENT_ATHM_APK_NOT_INSTALLED_OR_UPDATED);
            return "AthmRedirectSplashFragment";
        }
    }
}
