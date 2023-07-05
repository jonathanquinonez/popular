package com.popular.android.mibanco.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.IntroScreen;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.activity.Downtime;
import com.popular.android.mibanco.activity.EasyCashHistoryActivity;
import com.popular.android.mibanco.activity.EasyCashHistoryReceipt;
import com.popular.android.mibanco.activity.EasyCashNonCustHistoryActivity;
import com.popular.android.mibanco.activity.EasyCashRedeem;
import com.popular.android.mibanco.activity.EasyCashStaging;
import com.popular.android.mibanco.activity.EnrollmentLiteWelcomeActivity;
import com.popular.android.mibanco.activity.InformativeActivity;
import com.popular.android.mibanco.listener.StartListener;
import com.popular.android.mibanco.model.LoginGet;
import com.popular.android.mibanco.task.LiteEnrollmentTasks;
import com.popular.android.mibanco.task.MobileCashTasks;
import com.popular.android.mibanco.ws.response.EnrollmentLiteResponse;
import com.popular.android.mibanco.ws.response.MobileCashTrx;
import com.popular.android.mibanco.ws.response.MobileCashTrxInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MobileCashUtils {


    static DialogInterface.OnClickListener informativeMessageOnClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    public static void informativeMessage(Context mContext, int title, int message)
    {
        AlertDialogParameters params = new AlertDialogParameters(mContext,message,informativeMessageOnClick);
        params.setTitle(mContext.getResources().getString(title));
        params.setPositiveButtonText(mContext.getResources().getString(R.string.ok));
        Utils.showAlertDialog(params);
    }

    public static void informativeMessage(Context mContext, String message)
    {
        AlertDialogParameters params = new AlertDialogParameters(mContext,message,informativeMessageOnClick);
        params.setPositiveButtonText(mContext.getResources().getString(R.string.ok));
        Utils.showAlertDialog(params);
    }

    public static void informativeMessage(Context mContext, int errorMessageStr)
    {
        AlertDialogParameters params = new AlertDialogParameters(mContext,errorMessageStr,informativeMessageOnClick);
        params.setPositiveButtonText(mContext.getResources().getString(R.string.ok));
        Utils.showAlertDialog(params);
    }

    public static void informativeMessageWithoutTitle(Context mContext, int errorMessageStr)
    {
        AlertDialogParameters params = new AlertDialogParameters(mContext,errorMessageStr,informativeMessageOnClick);
        params.setPositiveButtonText(mContext.getResources().getString(R.string.ok));
        Utils.showAlertDialog(params);
    }

    private static void verifyGlobalStatus(Context context) {
        final boolean globalCashDropEntitlementEnabled = App.getApplicationInstance().isGlobalCashdropEntitlementEnabled();

        if(globalCashDropEntitlementEnabled) {
            final String token = Utils.getStringContentFromShared(context, MiBancoConstants.KEY_SHARED_LITE_ENROLLMENT_TOKEN);
            if (Utils.isBlankOrNull(token)) {
                enrollmentLiteWelcomeScreen(context, false);
            } else {
                cashDropTokenVerification(context, token);
            }
        }else{
            final Intent downtimeIntent = new Intent(context, Downtime.class);
            downtimeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            downtimeIntent.putExtra("downtimeMessage", context.getResources().getString(R.string.maintenance_mobilecash));
            context.startActivity(downtimeIntent);
        }

    }

    public static void nonCustomerViewDeciderAction(final Context context){
        App app = App.getApplicationInstance();
        app.setSessionNeeded(false);

        if (!app.needsGlobalStatusNonCustomerRefresh() && app.getGlobalStatus() != null) {
            verifyGlobalStatus(context);
        }else {
            app.getAsyncTasksManager().startApp(context, new StartListener() {
                @Override
                public void savedData(final LoginGet loginData) {
                    verifyGlobalStatus(context);
                }
            }, true, false);
        }
    }

    public static void cashDropTokenVerification(final Context mContext, String token)
    {
        LiteEnrollmentTasks.postIsCustomerLiteEnrolled(mContext, token, new LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse>() {
            @Override
            public void onLiteEnrollmentApiResponse(EnrollmentLiteResponse result) {
                if(result == null){
                    MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);
                }else{
                    if(result.getStatus() == EnrollmentLiteStatus.CUSTOMER_LITE_FOUND.getCode()){
                        easyCashNonCustomerHistoryScreen(mContext, false);

                    }else{
                        resetNonCustomerToken(mContext);
                    }
                }
            }
        });
    }

    public static void resetNonCustomerToken(Context mContext)
    {
        //Start over with the initial enrollment process
        Utils.saveStringContentToShared(mContext, MiBancoConstants.KEY_SHARED_LITE_ENROLLMENT_TOKEN, "");
        final Intent iIntro = new Intent(mContext, IntroScreen.class);
        iIntro.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(iIntro);
        ((Activity) mContext).finish();
    }

    public static void easyCashHistoryScreen(final Context mContext)
    {
        final Intent intentEasyCash = new Intent(mContext, EasyCashHistoryActivity.class);
        intentEasyCash.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intentEasyCash);
    }

    public static void easyCashRedeemReceiptNextScreen(final Context mContext, boolean isCustomer)
    {
        if(isCustomer) {
            final Intent intentEasyCash = new Intent(mContext, EasyCashStaging.class);
            intentEasyCash.putExtra("isCustomer", isCustomer);
            intentEasyCash.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intentEasyCash);
        }else{
            easyCashNonCustomerHistoryScreen(mContext, isCustomer);
        }
    }

    public static void easyCashNonCustomerHistoryScreen(final Context mContext, boolean isCustomer)
    {
        final Intent intentEasyCash = new Intent(mContext, EasyCashNonCustHistoryActivity.class);
        intentEasyCash.putExtra("isCustomer", isCustomer);
        intentEasyCash.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intentEasyCash);
    }

    public static void enrollmentLiteWelcomeScreen(final Context mContext, boolean isCustomer)
    {
        final Intent intentEnrollmentLiteWelcome = new Intent(mContext, EnrollmentLiteWelcomeActivity.class);
        intentEnrollmentLiteWelcome.putExtra(MiBancoConstants.KEY_ENROLL_LITE_IS_CUSTOMER, isCustomer);
        intentEnrollmentLiteWelcome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intentEnrollmentLiteWelcome);
    }

    public static void passcodeRequiredScreen(final Context mContext, String activityType)
    {
        final Intent intentPasscode = new Intent(mContext, InformativeActivity.class);
        intentPasscode.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentPasscode.putExtra(MiBancoConstants.INFORMATIVE_TITLE_ID, R.string.easycash_passcode_title);
        intentPasscode.putExtra(MiBancoConstants.INFORMATIVE_TEXT_ID, R.string.easycash_passcode_message);
        intentPasscode.putExtra(MiBancoConstants.INFORMATIVE_BUTTON_ID, R.string.ok);
        intentPasscode.putExtra(MiBancoConstants.INFORMATIVE_IMAGE_ID, R.drawable.passcode);
        intentPasscode.putExtra("Activity",activityType);

        mContext.startActivity(intentPasscode);

        if(!(mContext instanceof IntroScreen)) {
            ((Activity) mContext).finish();
        }
    }

    public static void goToMobileCashFromMeToMeRedeem(final Context context, MobileCashTrx transaction, boolean isCustomer)
    {
        Intent intent = new Intent(context, EasyCashRedeem.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MiBancoConstants.KEY_MOBILE_CASH_TRX, transaction);
        intent.putExtras(bundle);
        intent.putExtra(MiBancoConstants.KEY_ENROLL_LITE_IS_CUSTOMER, isCustomer);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void goToMobileTrxReceipt(final Context mContext, boolean hasDeleteAction,boolean activityWithResult, MobileCashTrx transaction)
    {
        Intent intent = new Intent(mContext, EasyCashHistoryReceipt.class);
        intent.putExtra(MiBancoConstants.MC_REDEEM_SUCCESS_TRX,transaction);
        intent.putExtra("deleteAction",hasDeleteAction);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getApplicationInstance().setCallback((EasyCashHistoryReceipt.Callback)mContext);
        if(activityWithResult){
            ((Activity)mContext).startActivityForResult(intent,1);
        }else {
            mContext.startActivity(intent);
        }
    }

    public static void deleteTransaction(final Context mContext, String receiptId, final boolean isForMe)
    {
        MobileCashTasks.cancelTransaction(mContext,receiptId, new MobileCashTasks.MobileCashListener<MobileCashTrxInfo>() {
            @Override
            public void onMobileCashApiResponse(MobileCashTrxInfo result) {
                if(result != null && result.getTransactionStatus() != null
                        && result.getTransactionStatus().equals(MiBancoConstants.MOBILE_CASH_DELETE_SUCCESS)) {

                    if (isForMe) {
                        BPAnalytics.logEvent(BPAnalytics.EVENT_MC_CANCEL_PRESTAGE_SUCCESSFULL);
                    } else {
                        BPAnalytics.logEvent(BPAnalytics.EVENT_MC_CANCEL_PRESTAGE_FOR_OTHER_SUCCESSFULL);
                    }

                    Intent intent = new Intent(mContext, EasyCashStaging.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    App.getApplicationInstance().setUpdateEasyCashHistory(true);
                    ((Activity)mContext).finish();
                }else{
                    if (isForMe) {
                        BPAnalytics.logEvent(BPAnalytics.EVENT_MC_CANCEL_PRESTAGE_FAILED);
                    } else {
                        BPAnalytics.logEvent(BPAnalytics.EVENT_MC_CANCEL_PRESTAGE_FOR_OTHER_FAILED);
                    }

                    MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);
                }
            }
        });
    }

    public static void deleteTransactionnsAndFinish(final Context mContext,String receiptId)
    {
        MobileCashTasks.cancelTransaction(mContext,receiptId, new MobileCashTasks.MobileCashListener<MobileCashTrxInfo>() {
            @Override
            public void onMobileCashApiResponse(MobileCashTrxInfo result) {
                if(result != null && result.getTransactionStatus() != null
                        && result.getTransactionStatus().equals(MiBancoConstants.MOBILE_CASH_DELETE_SUCCESS)) {

                    BPAnalytics.logEvent(BPAnalytics.EVENT_MC_CANCEL_PRESTAGE_FOR_OTHER_SUCCESSFULL);
                    App.getApplicationInstance().getCallback().onDelete();
                    ((Activity)mContext).finish();
                }else{
                    BPAnalytics.logEvent(BPAnalytics.EVENT_MC_CANCEL_PRESTAGE_FOR_OTHER_FAILED);
                    MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);
                }
            }
        });
    }

    public static String getFormattedExpDate(String strDate, Context context)
    {
        try {
            DateFormat format = new SimpleDateFormat(MiBancoConstants.MOBILE_CASH_WS_DATE_FORMAT, Locale.ENGLISH);
            Date date = format.parse(strDate);

            switch (Utils.getDateComparison(date)) {
                case MiBancoConstants.DATE_COMPARE_TODAY:
                    return context.getResources().getString(R.string.mc_date_today, getDisplayTime(date));

                case MiBancoConstants.DATE_COMPARE_TOMOROW:
                    return context.getResources().getString(R.string.mc_date_tomorow,getDisplayTime(date));

                case MiBancoConstants.DATE_COMPARE_MORE:
                    return context.getResources().getString(R.string.mc_date_more, getDisplayDate(date, context),getDisplayTime(date));

                case MiBancoConstants.DATE_COMPARE_YESTERDAY:
                    return context.getResources().getString(R.string.mc_date_yesterday, getDisplayTime(date));

                default:
                    break;
            }
        }catch (Exception e){
            Log.e("MobileCashUtils", e.toString());
        }
        return "";
    }

    private static String getDisplayTime(Date date)
    {
        SimpleDateFormat timeFormat = new SimpleDateFormat(MiBancoConstants.MOBILE_CASH_TIME_FORMAT);
        return timeFormat.format(date);
    }

    private static String getDisplayDate(Date date, Context context)
    {
        String defDateFormat = context.getResources().getString(R.string.mc_date_format);
        SimpleDateFormat dateFormat = new SimpleDateFormat(defDateFormat);
        return dateFormat.format(date);
    }

    public static List<MobileCashTrx> filterReceivedTransactionList(List<MobileCashTrx> completeHistory)
    {
        List<MobileCashTrx> filteredList = new LinkedList<>();
        for(MobileCashTrx trx: completeHistory){
            if("true".equals(trx.getReceived())){
                filteredList.add(trx);
            }
        }
        return filteredList;
    }

    public static List<MobileCashTrx> filterSentTransactionList(List<MobileCashTrx> completeHistory)
    {
        List<MobileCashTrx> filteredList = new LinkedList<>();
        for(MobileCashTrx trx: completeHistory){
            if("false".equals(trx.getReceived()) && !Utils.isBlankOrNull(trx.getReceiverPhone())){
                filteredList.add(trx);
            }
        }
        return filteredList;
    }

    public static List<MobileCashTrx> filterForMeTransactionList(List<MobileCashTrx> completeHistory)
    {
        List<MobileCashTrx> filteredList = new LinkedList<>();
        for(MobileCashTrx trx: completeHistory){
            if(Utils.isBlankOrNull(trx.getReceiverPhone())){
                filteredList.add(trx);
            }
        }
        return filteredList;
    }

    public static void logSystemError(boolean isCustomer) {
        if (isCustomer) {
            BPAnalytics.logEvent(BPAnalytics.EVENT_MC_ENROLL_SYSTEM_FAILED);
        } else {
            BPAnalytics.logEvent(BPAnalytics.EVENT_MC_ENROLL_NON_CUSTOMER_SYSTEM_FAILED);
        }
    }


}
