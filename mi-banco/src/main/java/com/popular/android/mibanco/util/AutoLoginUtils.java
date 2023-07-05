package com.popular.android.mibanco.util;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.IntroScreen;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.model.User;
import com.popular.android.mibanco.task.LiteEnrollmentTasks;
import com.popular.android.mibanco.ws.response.EnrollmentLiteResponse;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ET55498 on 8/18/16.
 */
public class AutoLoginUtils {

    /**
     * Method that verifies if the phone is absolutely ready for fingerprint
     * @param context The context
     * @param user The user
     * @param storedPasswordValidation Password validation
     * @return
     */
    public static boolean isFingerprintAbsolutelyReady(final Context context, User user, boolean storedPasswordValidation)
    {
        boolean operatingSystemRequirements = osFingerprintRequirements(context,true);
        boolean fingerprintPreference = getFingerprintPreference(context);

        boolean passwordSaved = true;
        if(storedPasswordValidation) {
            passwordSaved = (user!= null && !Utils.isBlankOrNull(user.getEncryptedPassword()));
        }

        return (operatingSystemRequirements && fingerprintPreference && passwordSaved);

    }

    /**
     * Method that verifies the phone complies with Operating System requirements
     * @param context The context
     * @param verifyIfEnrolledFingerPrints Parameter for the method to verify if the user has enrolled fingerprints
     * @return True if all conditions to use fingerprint are met, false otherwise
     */
    public static boolean osFingerprintRequirements(final Context context, boolean verifyIfEnrolledFingerPrints)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ActivityCompat.checkSelfPermission(context, android.Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED) {
                FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
                if (fingerprintManager != null && fingerprintManager.isHardwareDetected()) {
                    if(verifyIfEnrolledFingerPrints){
                        return fingerprintManager.hasEnrolledFingerprints();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    // Updates the user's fingerprint preference
    public static boolean saveFingerprintPreference(final Context context, final boolean newPreference) {
        if(newPreference) {
            BPAnalytics.logEvent(BPAnalytics.EVENT_FP_SWITCH_ON);
            saveFingerprintDate(context);
        }else{
            BPAnalytics.logEvent(BPAnalytics.EVENT_FP_SWITCH_OFF);
        }
        final SharedPreferences.Editor editor = Utils.getSecuredSharedPreferences(context).edit();
        editor.putBoolean(MiBancoConstants.FINGERPRINT_PREFERENCE_KEY, newPreference);
        return editor.commit();
    }

    public static void saveFingerprintDate(final Context context)
    {
        if("".equals(getFingerprintDate(context)) && getFingerprintPreference(context)) {
            final SharedPreferences.Editor editor = Utils.getSecuredSharedPreferences(context).edit();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyy-mm-dd");
            java.util.Date today = new java.util.Date();
            editor.putString(MiBancoConstants.FINGERPRINT_DATE_KEY, dateFormat.format(today));
            editor.apply();
        }
    }

    // Returns the user's fingerprint preference
    public static String getFingerprintDate(final Context context) {
        final SharedPreferences savedSession = Utils.getSecuredSharedPreferences(context);
        return savedSession.getString(MiBancoConstants.FINGERPRINT_DATE_KEY, "");
    }


    // Returns the user's fingerprint preference
    public static boolean getFingerprintPreference(final Context context) {
        final SharedPreferences savedSession = Utils.getSecuredSharedPreferences(context);
        return savedSession.getBoolean(MiBancoConstants.FINGERPRINT_PREFERENCE_KEY, false);
    }

    public static boolean minimumFingerPrintDisplayReqs(Context mContext){
        return (osFingerprintRequirements(mContext,false)
            && getFingerprintPreference(mContext));
    }

    public static void registerDevice(final Context mContext,final String productType, final boolean bind, final boolean isCustomer) {
        LiteEnrollmentTasks.bindCustomerDevice(mContext,productType,bind, new LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse>() {
            @Override
            public void onLiteEnrollmentApiResponse(EnrollmentLiteResponse result) {

                // hack for fingerprint hours countdown,easy cash
                if(productType.equals(ProductType.FINGERPRINT.toString())){
                    //TODO: Check response status before these conditions
                   setUpCountdownFingerprint(mContext,bind);
                }

                if(!bind && productType.equals(ProductType.CASHDROP.toString())){
                    if (result.getError() != null || !(result.getStatus() == EnrollmentLiteStatus.DEVICE_UPDATE_SUCCESS.getCode()
                            || result.getStatus() == EnrollmentLiteStatus.FINGERPRINT_DISABLED.getCode())) {
                        if (isCustomer) {
                            BPAnalytics.logEvent(BPAnalytics.EVENT_MC_UNSUBSCRIBE_CUSTOMER_FAILED);
                        } else {
                            BPAnalytics.logEvent(BPAnalytics.EVENT_MC_UNSUBSCRIBE_NON_CUSTOMER_FAILED);
                        }
                    } else {
                        App.getApplicationInstance().setCustomerPhone(null);
                        if (isCustomer) {
                            BPAnalytics.logEvent(BPAnalytics.EVENT_MC_UNSUBSCRIBE_CUSTOMER_SUCCESSFUL);
                        } else {
                            BPAnalytics.logEvent(BPAnalytics.EVENT_MC_UNSUBSCRIBE_NON_CUSTOMER_SUCCESSFUL);
                        }
                    }

                    if(!isCustomer){
                        Utils.saveStringContentToShared(mContext, MiBancoConstants.KEY_SHARED_LITE_ENROLLMENT_TOKEN,"");

                        final Intent iIntro = new Intent(mContext, IntroScreen.class);
                        iIntro.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mContext.startActivity(iIntro);
                    }

                    ((Activity)mContext).finish();
                }
            }
        });
    }

    public static void setUpCountdownFingerprint(Context mContext, boolean bind)
    {
        User currentUser = App.getApplicationInstance().getCurrentUser();
        if(bind){
            if(currentUser != null && Utils.isBlankOrNull(currentUser.getFingerprintBindDate())){
                SimpleDateFormat format = new SimpleDateFormat(MiBancoConstants.FINGERPRINT_STORAGE_DATE_FORMAT);
                String formattedDate = format.format(new Date());
                Utils.saveFingerprintDate(mContext, App.getApplicationInstance().getCurrentUser(),formattedDate);
            }
        }else{
            Utils.saveFingerprintDate(mContext, currentUser,"");
        }
    }

    public static boolean isDeviceSecured(Context context)
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            KeyguardManager manager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            return manager.isDeviceSecure();
        }else{
            String LOCKSCREEN_UTILS = "com.android.internal.widget.LockPatternUtils";

            try {
                Class<?> lockUtilsClass = Class.forName(LOCKSCREEN_UTILS);
                // "this" is a Context, in my case an Activity
                Object lockUtils = lockUtilsClass.getConstructor(Context.class).newInstance(context);
                Method method = lockUtilsClass.getMethod("isLockScreenDisabled");
                boolean isDisabled = Boolean.valueOf(String.valueOf(method.invoke(lockUtils)));
                return (!isDisabled);
            }
            catch (Exception e) {
                Log.e("reflectInternalUtils", "ex:"+e);
            }
            return false;
        }
    }
}
