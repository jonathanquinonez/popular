package com.popular.android.mibanco.util;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.IBinder;
import android.text.InputFilter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.exception.BankException;
import com.popular.android.mibanco.listener.AsyncTaskListener;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.model.RSAChallengeResponse;
import com.popular.android.mibanco.model.RsaModResponse;
import com.popular.android.mibanco.task.RSAChallengeTasks;

import java.util.ArrayList;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class RSAUtils {

    private static RSAChallengeResponse oobData;

    private static final String PRIMARY_PHONE = "primary_phone";
    private static final String ALTERNATE_PHONE = "alternate_phone";
    private static final String RECOVERY_CODE = "recovery_code";
    private static final String NOT_HAVE_ANY_CODE = "no_code";
    private static final String OOB_SMS_TYPE_CHALLENGE = "OOBSMS";
    private static final int CONTINUE_BUTTON = -1;
    private static final int RESEND_CODE_BUTTON = -2;
    private static final int OOB_NO_PHONE_BUTTON = -3;

    public static void challengeRSAStatus(final Context context, final AsyncTaskListener listener) {
        final String sdkRsaJson = RSACollectUtils.collectDeviceInfo(context);
        final String rsaCookie = Utils.getPrefsStringNotNull(MiBancoConstants.RSA_COOKIE, context);

        RSAChallengeTasks.getRSAChallenge(context, sdkRsaJson, rsaCookie, new ResponderListener() {
            @Override
            public void responder(String responderName, Object data) {
                if (data != null) {
                    RSAChallengeResponse response = (RSAChallengeResponse) data;
                    if (response.getChallenge().equalsIgnoreCase("QUESTION")) {
                        BPAnalytics.logEvent(BPAnalytics.EVENT_RSA_SESSION_SHOWN);
                        askQuestionDialog(context, R.string.security_question,response.getQuestion(), listener);
                    } else if (response.getChallenge().equalsIgnoreCase("OOB")) {
                        BPAnalytics.logEvent(BPAnalytics.EVENT_RSA_SESSION_SHOWN);
                        challengeOOB(context, sdkRsaJson, rsaCookie, listener);
                    } else {
                        listener.onSuccess("Some");
                    }
                } else {
                    listener.onError(new BankException("Some"));

                }
            }

            @Override
            public void sessionHasExpired() {
                listener.onError(new BankException("Some"));
            }
        });
    }

    //region RSA QUESTION *********************

    private static void askQuestionDialog(final Context mContext, final int title, final String message, final AsyncTaskListener listener) {
        final EditText input = new EditText(mContext);

        AlertDialogParameters params = new AlertDialogParameters(mContext,message,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        final String answer = input.getText().toString();
                        dialog.dismiss();
                        if (!Utils.isBlankOrNull(answer)) {
                            final String deviceInfoJson = RSACollectUtils.collectDeviceInfo(mContext);
                            final String rsaCookie = Utils.getPrefsStringNotNull(MiBancoConstants.RSA_COOKIE, mContext);

                            sendRSAAnswer(mContext, answer, deviceInfoJson, rsaCookie, listener);
                        }else {
                            askQuestionDialog(mContext, title, message, listener);
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        listener.onCancelled();
                        break;
                    default:
                        dialog.dismiss();
                        listener.onError(new BankException("Some"));
                        break;
                }
            }
        });

        params.setInputEditText(input);
        params.setTitle(mContext.getResources().getString(title));
        params.setPositiveButtonText(mContext.getResources().getString(R.string.ok));
        params.setNegativeButtonText(mContext.getResources().getString(R.string.cancel).toUpperCase());
        Utils.showAlertDialog(params);
    }

    private static void sendRSAAnswer(final Context context, final String answer, final String sdkMobileData, final String rsaCookie,
                                      final AsyncTaskListener listener) {
        RSAChallengeTasks.postRSAChallengeAnswer(context, answer, sdkMobileData, rsaCookie, new ResponderListener() {
            @Override
            public void responder(String responderName, Object data) {
                if (data != null) {
                    RSAChallengeResponse response = (RSAChallengeResponse) data;
                    if (!Utils.isBlankOrNull(response.getQuestion())) {
                        askQuestionDialog(context, R.string.enter_answer,response.getQuestion(), listener);
                    }else {
                        BPAnalytics.logEvent(BPAnalytics.EVENT_RSA_SESSION_SUCCESS);
                        listener.onSuccess("some");
                    }
                    listener.onError(new BankException("Some"));
                }
            }

            @Override
            public void sessionHasExpired() {
                listener.onError(new BankException("Some"));
            }
        });
    }

    //endregion

    //region RSA OOB *********************

    //region OOB DIALOGS *********************

    /**
     *  Set all values for the message.
     */
    private static void prepOOBChallenge(Context context, final AsyncTaskListener listener) {
        String message;

        //show resend code btn
        String target;

        AlertDialogParameters params = new AlertDialogParameters(context);
        params.setTitle(context.getString(R.string.verification_code).toUpperCase());
        params.setPositiveButtonText(context.getString(R.string.continue_phrase));
        params.setNeutralButtonText(context.getString(R.string.oob_no_phone));


        if (oobData.isCodeSent()) {
            oobData.setIsCodeSent(false);
            if(oobData.getOOBChanllengeType().equalsIgnoreCase(OOB_SMS_TYPE_CHALLENGE)) {
                params.setTitle(context.getString(R.string.new_code_sent));
            } else {
                params.setTitle(context.getString(R.string.new_call_made));
            }
        }

        if(oobData.getResponderMessage().equalsIgnoreCase(PRIMARY_PHONE)) {
            target = "0";
        } else {
            target = "2";
        }

        if(!Utils.isBlankOrNull(oobData.getErrorMessage())) {
            params.setTitle(android.text.Html.fromHtml(oobData.getErrorMessage()).toString());
        }

        if(oobData.getOOBChanllengeType().equalsIgnoreCase(OOB_SMS_TYPE_CHALLENGE)) {
            if(oobData.getResponderMessage().equalsIgnoreCase(PRIMARY_PHONE)) {
                message = context.getString(R.string.oob_sms_primary_instructions);
            } else {
                message = context.getString(R.string.oob_sms_alternate_instructions);
            }
            params.setNegativeButtonText(context.getString(R.string.resend_code));

            params.setStrMessage(message + " " + oobData.getPhone().replaceAll("-", "‑"));

            askOOBCodeDialog(context, params, target, listener);
        } else {
            if(oobData.getResponderMessage().equalsIgnoreCase(PRIMARY_PHONE)) {
                message = context.getString(R.string.oob_call_primary_instructions);
            } else {
                message = context.getString(R.string.oob_call_alternate_instructions);
            }
            params.setNegativeButtonText(context.getString(R.string.oob_make_new_call));

            String voiceCodeCall = oobData.getOOBCodeVoiceCall();

            params.setStrMessage(message + " " + oobData.getPhone().replaceAll("-", "‑"));


            askOOBCallDialog(context, params, voiceCodeCall, target, listener);
        }
    }

    private static void askOOBCodeDialog(final Context mContext,final AlertDialogParameters params, final String target, final AsyncTaskListener listener) {
        // Set up the input
        final EditText input = new EditText(mContext);
        int maxLength = 6;
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        input.setFilters(FilterArray);
        params.setInputEditText(input);

        params.setOnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case CONTINUE_BUTTON:
                        final String code = input.getText().toString();
                        dissmisKeyboard(mContext, input.getWindowToken());
                        dialog.dismiss();
                            sendOOBCode(mContext, code, MiBancoConstants.OOB_VALIDATE_SMSCODE , listener);
                        break;
                    case RESEND_CODE_BUTTON:
                        //Resend Code
                        dialog.dismiss();
                        //if primary = 0 , else = 2
                        reSendOOBCode(mContext, target, MiBancoConstants.OOB_SEND_SMSCODE , listener);
                        break;
                    case OOB_NO_PHONE_BUTTON:
                        //Dont have phone
                        dialog.dismiss();
                        showOOBOptionsDialog(mContext,listener);
                        break;
                    default:
                        dialog.dismiss();
                        listener.onError(new BankException("Some"));
                        break;
                }
            }
        });

        params.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                listener.onCancelled();
            }
        });

        Utils.showAlertDialog(params);
    }

    private static void dissmisKeyboard(Context context, IBinder inputToken) {

        if (context.getSystemService(INPUT_METHOD_SERVICE) instanceof InputMethodManager) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(inputToken, 0);
        }
    }

    private static void askOOBCallDialog(final Context mContext, final AlertDialogParameters params,final String voiceCodeCall, final String target, final AsyncTaskListener listener) {

        params.setOnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case CONTINUE_BUTTON:
                        sendOOBCallConfirmation(mContext, MiBancoConstants.OOB_VALIDATE_CALLCODE , listener);
                        break;
                    case RESEND_CODE_BUTTON:
                        //Resend Code
                        dialog.dismiss();
                        //if primary = 0 , else = 2
                        oobCallAgain(mContext, target, MiBancoConstants.OOB_CALL_PHONE , listener);
                        break;
                    case OOB_NO_PHONE_BUTTON:
                        //Dont have phone
                        dialog.dismiss();
                        showOOBOptionsDialog(mContext,listener);
                        break;
                    default:
                        dialog.dismiss();
                        listener.onError(new BankException("Some"));
                        break;
                }
            }
        });

        params.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                listener.onCancelled();
            }
        });

        final TextView textView = new TextView(mContext,null, R.style.TextOOBCodeVoice);
        textView.setText(voiceCodeCall);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,42);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTypeface(Typeface.DEFAULT_BOLD);

        params.setTextView(textView);

        Utils.showAlertDialog(params);
    }
    private static void showOOBOptionsDialog(final Context mContext, final AsyncTaskListener listener) {

        final ArrayList<String> menuItems = new ArrayList<>();

        final ArrayList<String> str = new ArrayList<>();
        if (oobData.hasAltPhone()) {
            menuItems.add(ALTERNATE_PHONE);
            if(oobData.getOOBChanllengeType().equalsIgnoreCase(OOB_SMS_TYPE_CHALLENGE)) {
                str.add(mContext.getString(R.string.oob_use_alt_phone));
            }else {
                str.add(mContext.getString(R.string.oob_use_alt_phone_call));
            }
        }

        str.add(mContext.getString(R.string.oob_use_rec_code));
        menuItems.add(RECOVERY_CODE);
        str.add(mContext.getString(R.string.oob_none_codes));
        menuItems.add(NOT_HAVE_ANY_CODE);

        AlertDialogParameters params = new AlertDialogParameters(mContext ,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (menuItems.get(which)){
                    case ALTERNATE_PHONE:
                        dialog.dismiss();
                        sendToAltPhone(mContext,MiBancoConstants.OOB_SEND_ALT_PHONE,listener);
                        break;
                    case RECOVERY_CODE:
                        dialog.dismiss();
                        recoveryCodeDialog(mContext, listener);
                        break;
                    case NOT_HAVE_ANY_CODE:
                        dialog.dismiss();
                        noneOfTheCodeDialog(mContext, listener);
                        break;
                    default:
                        dialog.dismiss();
                        listener.onError(new BankException("Some"));
                        break;
                }
            }
        });

        params.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                listener.onCancelled();
            }
        });

        String[] items = str.toArray(new String[str.size()]);
        params.setItems(items);

        Utils.showAlertDialog(params);
    }

    private static void recoveryCodeDialog(final Context mContext, final AsyncTaskListener listener) {

        String message = mContext.getString(R.string.text_recovery_code);

        AlertDialogParameters params = new AlertDialogParameters(mContext, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    listener.onCancelled();
                    dialog.dismiss();
                    App.getApplicationInstance().setShowDesktopVersion(true);
                    App.getApplicationInstance().reLogin(mContext);

                } else {
                    dialog.dismiss();
                    listener.onError(new BankException("Some"));
                }
            }
        });

        params.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                listener.onCancelled();
            }
        });

        params.setTitle(mContext.getString(R.string.title_use_my_recovery_code));

        params.setPositiveButtonText(mContext.getString(R.string.submit_go_mi_banco));

        params.setNegativeButtonText(mContext.getString(R.string.cancel));

        Utils.showAlertDialog(params);
    }

    private static void noneOfTheCodeDialog(final Context mContext, final AsyncTaskListener listener) {

        String message = (mContext.getString(R.string.text_nearest_branch) + "\n\n\n" + mContext.getString(R.string.text_contact_call));

        AlertDialogParameters params = new AlertDialogParameters(mContext,message,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.dismiss();
                        listener.onCancelled();
                        break;
                    default:
                        dialog.dismiss();
                        listener.onError(new BankException("Some"));
                        break;
                }
            }
        });

        params.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                listener.onCancelled();
            }
        });

        params.setTitle(mContext.getString(R.string.title_none_of_the_codes));

        params.setPositiveButtonText(mContext.getString(R.string.ok));

        Utils.showAlertDialog(params);
    }

    //endregion

    //region *********************

    private static void challengeOOB(final Context mContext, final String sdkRsaJson, final String rsaCookie, final AsyncTaskListener listener) {

        RSAChallengeTasks.getOOBChallenge(mContext, sdkRsaJson, rsaCookie, new ResponderListener() {
            @Override
            public void responder(String responderName, Object data) {
                if (data != null) {
                    oobData = (RSAChallengeResponse) data;
                    prepOOBChallenge(mContext, listener);
                } else {
                    listener.onError(new BankException("Some"));
                }
            }

            @Override
            public void sessionHasExpired() {
                listener.onError(new BankException("Some"));
            }
        });
    }

    private static void sendOOBCode(final Context context, final String code , final String actionType, final AsyncTaskListener listener) {
        RSAChallengeTasks.postOOBCodeAnswer(context, code, null, actionType , new ResponderListener() {
            @Override
            public void responder(String responderName, Object data) {
                if (data != null) {
                    RSAChallengeResponse response = (RSAChallengeResponse) data;
                    if (!Utils.isBlankOrNull(response.getErrorMessage())) {
                        oobData = response;
                        prepOOBChallenge(context, listener);
                    }else {
                        BPAnalytics.logEvent(BPAnalytics.EVENT_RSA_SESSION_SUCCESS);
                        listener.onSuccess("some");
                    }
                } else {
                    listener.onError(new BankException("Some"));
                }
            }

            @Override
            public void sessionHasExpired() {
                listener.onError(new BankException("Some"));
            }
        });
    }

    private static void sendOOBCallConfirmation(final Context context,  final String actionType, final AsyncTaskListener listener) {
        RSAChallengeTasks.postOOBCodeAnswer(context, null, null, actionType , new ResponderListener() {
            @Override
            public void responder(String responderName, Object data) {
                if (data != null) {
                    RSAChallengeResponse response = (RSAChallengeResponse) data;
                    if (!Utils.isBlankOrNull(response.getErrorMessage())) {
                        oobData = response;
                        prepOOBChallenge(context, listener);
                    }else {
                        BPAnalytics.logEvent(BPAnalytics.EVENT_RSA_SESSION_SUCCESS);
                        listener.onSuccess("some");
                    }
                } else {
                    listener.onError(new BankException("Some"));
                }
            }

            @Override
            public void sessionHasExpired() {
                listener.onError(new BankException("Some"));
            }
        });
    }

    private static void sendToAltPhone(final Context context, final String actionType, final AsyncTaskListener listener) {
        RSAChallengeTasks.postOOBCodeAnswer(context, null, "2", actionType , new ResponderListener() {
            @Override
            public void responder(String responderName, Object data) {
                if (data != null){
                    RSAChallengeResponse response = (RSAChallengeResponse) data;
                    if (!Utils.isBlankOrNull(response.getResponderMessage()) && response.getResponderMessage().equalsIgnoreCase(ALTERNATE_PHONE)) {
                        oobData = response;
                        prepOOBChallenge(context, listener);
                    }else {
                        BPAnalytics.logEvent(BPAnalytics.EVENT_RSA_SESSION_SUCCESS);
                        listener.onSuccess("some");
                    }
                }
                listener.onError(new BankException("Some"));
            }

            @Override
            public void sessionHasExpired() {
                listener.onError(new BankException("Some"));
            }
        });
    }

    private static void reSendOOBCode(final Context context, final String target, final String actionType, final AsyncTaskListener listener) {
        RSAChallengeTasks.postOOBCodeAnswer(context, null, target, actionType , new ResponderListener() {
            @Override
            public void responder(String responderName, Object data) {
                if (data != null && ((RSAChallengeResponse) data).isCodeSent()) {
                    oobData.setIsCodeSent(true);
                    oobData.setErrorMessage(((RSAChallengeResponse) data).getErrorMessage());
                    prepOOBChallenge(context, listener);
                    return;
                }
                listener.onError(new BankException("Some"));
            }

            @Override
            public void sessionHasExpired() {
                listener.onError(new BankException("Some"));
            }
        });
    }

    private static void oobCallAgain(final Context context, final String target, final String actionType, final AsyncTaskListener listener) {
        RSAChallengeTasks.postOOBCodeAnswer(context, null, target, actionType , new ResponderListener() {
            @Override
            public void responder(String responderName, Object data) {
                if (data != null) {
                    RSAChallengeResponse response = (RSAChallengeResponse) data;
                    if (!Utils.isBlankOrNull(response.getOOBCodeVoiceCall())) {
                        oobData.setIsCodeSent(true);
                        oobData.setOOBCodeVoiceCall(response.getOOBCodeVoiceCall());
                        oobData.setErrorMessage(response.getErrorMessage());
                        prepOOBChallenge(context, listener);
                        return;
                    }
                }
                listener.onError(new BankException("Some"));
            }

            @Override
            public void sessionHasExpired() {
                listener.onError(new BankException("Some"));
            }
        });
    }


    public static void rsaCheckStatus(final Context context, final AsyncTaskListener listener) {
        RSAChallengeTasks.getRSAStatus(context, new ResponderListener() {
            @Override
            public void responder(String responderName, Object data) {
                if (data != null) {
                    RsaModResponse response = (RsaModResponse) data;
                    listener.onSuccess(response.isOobEnrolled());

                } else {
                    listener.onError(new BankException("Rsa status not available this moment - rsaCheckStatus"));

                }
            }

            @Override
            public void sessionHasExpired() {
                listener.onError(new BankException("Session is not alive - rsaCheckStatus"));
            }
        });
    }
}
