package com.popular.android.mibanco.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.activity.EnrollmentLiteProfileInformation;
import com.popular.android.mibanco.activity.EnrollmentLiteRegistrationComplete;
import com.popular.android.mibanco.activity.EnrollmentLiteSmsCode;
import com.popular.android.mibanco.activity.EnrollmentLiteTermsAndConditions;
import com.popular.android.mibanco.task.LiteEnrollmentTasks;
import com.popular.android.mibanco.ws.response.EnrollmentLiteCompleteResponse;

/**
 * Created by et55498 on 3/14/17.
 */

public class EnrollmentLiteUtils {

    public static void displayTermsAndConditionsForm(Context mContext, boolean isCustomer, boolean isNextStepSms)
    {
        final Intent intentCashDrop = new Intent(mContext, EnrollmentLiteTermsAndConditions.class);
        intentCashDrop.putExtra(MiBancoConstants.KEY_ENROLL_LITE_IS_CUSTOMER, isCustomer);
        intentCashDrop.putExtra(MiBancoConstants.KEY_ENROLL_LITE_NEXT_STEP_SMS, isNextStepSms);
        mContext.startActivity(intentCashDrop);
        ((Activity)mContext).finish();
    }

    public static void displaySmsCode(Context mContext, boolean isCustomer)
    {
        final Intent intentCashDrop = new Intent(mContext, EnrollmentLiteSmsCode.class);
        intentCashDrop.putExtra(MiBancoConstants.KEY_ENROLL_LITE_IS_CUSTOMER, isCustomer);
        mContext.startActivity(intentCashDrop);
        ((Activity)mContext).finish();
    }

    public static void displayEnrollmentComplete(Context mContext, boolean isCustomer)
    {
        final Intent intentCashDrop = new Intent(mContext, EnrollmentLiteRegistrationComplete.class);
        intentCashDrop.putExtra(MiBancoConstants.KEY_ENROLL_LITE_IS_CUSTOMER, isCustomer);
        mContext.startActivity(intentCashDrop);
        ((Activity)mContext).finish();
    }

    public static void displayEnrollmentProfileInfo(Context mContext, boolean isCustomer)
    {
        final Intent enrollmentLite = new Intent(mContext, EnrollmentLiteProfileInformation.class);
        enrollmentLite.putExtra(MiBancoConstants.KEY_ENROLL_LITE_IS_CUSTOMER, isCustomer);
        mContext.startActivity(enrollmentLite);
        ((Activity)mContext).finish();
    }

    public static boolean isUiElementNull(Object[] elementsList)
    {
        for(int i = 0; i< elementsList.length; i++){
            if(elementsList[i] == null){
                return true;
            }
        }
        return false;
    }


    public static void executeLiteEnrollment(final Context mContext, final boolean isCustomer)
    {
        LiteEnrollmentTasks.getCreateLiteProfile(mContext, new LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteCompleteResponse>() {
            @Override
            public void onLiteEnrollmentApiResponse(EnrollmentLiteCompleteResponse result) {
                if(result == null) {
                    MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);

                }else {
                    if(result.getStatus() == EnrollmentLiteStatus.ENROLL_COMPLETE.getCode()) {
                        boolean error = false;
                        if(!isCustomer){
                            if(Utils.isBlankOrNull(result.getToken())){
                                MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);
                                error = true;
                            }else{
                                Utils.saveStringContentToShared(mContext, MiBancoConstants.KEY_SHARED_LITE_ENROLLMENT_TOKEN,result.getToken());
                            }
                        }
                        if(!error) {
                            EnrollmentLiteUtils.displayEnrollmentComplete(mContext,isCustomer);
                        } else {
                            MobileCashUtils.logSystemError(isCustomer);
                        }
                    }else{
                        MobileCashUtils.logSystemError(isCustomer);
                        MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);
                    }
                }
            }
        });
    }
}
