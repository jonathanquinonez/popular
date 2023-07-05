package com.popular.android.mibanco.util;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.MarketPlaceEnum;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class OtpUtils{


    public interface ContinueButtonCallback {
        void continueButtonAction(String code);
    }

    public interface ResendCodeButtonCallback {
        void resendCodeButtonAction();
    }

    public interface CancelButtonCallback {
        void cancelButtonAction();
    }

    private static final int CONTINUE_BUTTON = -1;
    private static final int RESEND_CODE_BUTTON = -2;
    private static CountDownTimer countTimer = null;

    public static void challengeOtpStatus(final Context c, String titleDialog, String textDialog, Boolean isHiddenButtonResend, Boolean isHiddenButtonContinue) {
        prepOtpChallenge(c,titleDialog,textDialog,isHiddenButtonResend,isHiddenButtonContinue);
    }

    /**
     *  Set all values for the message.
     */
    private static void prepOtpChallenge(Context context, String titleDialog, String textDialog, Boolean isHiddenButtonResend, Boolean isHiddenButtonContinue) {
        AlertDialogParameters params = new AlertDialogParameters(context);
        params.setTitle(titleDialog);
        params.setStrMessage(textDialog);

        params.setIsHtmlFormat(true);

        if (!isHiddenButtonContinue) {
            params.setPositiveButtonText(context.getString(R.string.continue_phrase));
        }

        if (!isHiddenButtonResend) {
            params.setNegativeButtonText(context.getString(R.string.otp_resend_code));
        }

        params.setNeutralButtonText(context.getString(R.string.cancel));

        askOtpCodeDialog(context, params ,isHiddenButtonContinue);
    }


    private static void askOtpCodeDialog(final Context mContext, final AlertDialogParameters params, Boolean isHiddenButtonContinue) {

        // Set up the input
        final EditText input = new EditText(mContext);
        final int maxLength = 6;
        InputFilter[] FilterArray = new InputFilter[1];

        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        input.setFilters(FilterArray);
        input.setHint(mContext.getString(R.string.otp_code_limit));

        LinearLayout layout = new LinearLayout(mContext);

        if (!isHiddenButtonContinue) {

            final int topPadding = Math.round(Utils.convertDpToPixel(5, mContext));
            final int leftPadding = Math.round(Utils.convertDpToPixel(17, mContext));
            final int rightPadding = Math.round(Utils.convertDpToPixel(20, mContext));

            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setPadding(leftPadding,topPadding,rightPadding,0);

            layout.addView(input, layoutParams);
        }

        //Add focus listener to show keyboard automatically
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, boolean hasFocus) {
                input.post(new Runnable() {
                    @Override
                    public void run() {
                        if (v.getContext().getSystemService(INPUT_METHOD_SERVICE) instanceof InputMethodManager) {
                            InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(INPUT_METHOD_SERVICE);
                            inputMethodManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }
                });
            }
        });
        input.requestFocus();

        //Add TextChangeListener to enable buttons
        input.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() == maxLength) {
                    changeButtonState(params.getPositiveButton(),true);
                } else {
                    changeButtonState(params.getPositiveButton(),false);
                }
            }
        });

        params.setCustomView(layout);

        params.setOnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                switch (which){
                    case CONTINUE_BUTTON:
                        resetTimer();
                        dissmisKeyboard(mContext, input.getWindowToken());
                        dialog.dismiss();
                        final String code = input.getText().toString();
                        castContinueButtonCallback(mContext).continueButtonAction(code);
                        break;
                    case RESEND_CODE_BUTTON:
                        resetTimer();
                        dissmisKeyboard(mContext, input.getWindowToken());
                        dialog.dismiss();
                        castResendCodeButtonCallback(mContext).resendCodeButtonAction();
                        break;
                    default:
                        resetTimer();
                        dissmisKeyboard(mContext, input.getWindowToken());
                        dialog.dismiss();
                        castCancelButtonCallback(mContext).cancelButtonAction();
                        break;
                }
            }
        });

        Utils.showAlertDialog(params);
        //After Utils sets up buttons disable them
        changeButtonState(params.getPositiveButton(),false);

        if (!MarketPlaceEnum.getIsResendButtonVisible()) {
            changeButtonState(params.getNegativeButton(),false);
            resendCountDown(params.getNegativeButton());
        }

        if (MarketPlaceEnum.getTimerSeconds() > 1) {
            changeButtonState(params.getNegativeButton(),false);
        }
    }

    private static void resetTimer() {
        if (countTimer != null) {
            countTimer.onFinish();
            countTimer.cancel();
            countTimer = null;
        }
    }

    private static void dissmisKeyboard(Context context, IBinder inputToken) {

        if (context.getSystemService(Context.INPUT_METHOD_SERVICE) instanceof InputMethodManager) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(inputToken, 0);
        }
    }

    public static ContinueButtonCallback castContinueButtonCallback(Context context) {
        ContinueButtonCallback continueButtonCallback = null;
        if (context instanceof ContinueButtonCallback) {
            continueButtonCallback = (ContinueButtonCallback) context;
        }

        return continueButtonCallback;
    }

    public static ResendCodeButtonCallback castResendCodeButtonCallback(Context context) {
        ResendCodeButtonCallback resendCodeButtonPush = null;
        if (context instanceof ResendCodeButtonCallback) {
            resendCodeButtonPush = (ResendCodeButtonCallback) context;
        }

        return resendCodeButtonPush;
    }

    public static CancelButtonCallback castCancelButtonCallback(Context context) {
        CancelButtonCallback cancelButtonCallback = null;
        if (context instanceof CancelButtonCallback) {
            cancelButtonCallback = (CancelButtonCallback) context;
        }

        return cancelButtonCallback;
    }

    private static void changeButtonState(Button button, boolean isEnabled){
        if (button != null){
            button.setEnabled(isEnabled);
            button.setAlpha(.5f);
            button.setClickable(isEnabled);

            if(!isEnabled) {
                button.setAlpha(.5f);
            } else {
                button.setAlpha(1f);
            }
        }
    }

    private static void resendCountDown(final Button button)
    {

        changeButtonState(button,false);

        countTimer = new CountDownTimer(MarketPlaceEnum.getTimerSeconds() * 1000, 1000) {
            String timer;
            @Override
            public void onTick(long millisUntilFinished) {
                //this will be done every 1000 milliseconds ( 1 seconds )
                @SuppressLint("DefaultLocale")
                String v = String.format("%02d", millisUntilFinished/60000);
                long va = (millisUntilFinished%60000)/1000;
                MarketPlaceEnum.setTimerSeconds(va);

                timer = " ("+v+":"+String.format("%02d",va)+")";
                if (button != null) {
                    String btnText = button.getContext().getResources().getString(R.string.otp_resend_code) + timer;
                    button.setText(btnText);
                }
            }

            @Override
            public void onFinish() {
                changeButtonState(button, true);
                if (button != null && (MarketPlaceEnum.getTimerSeconds() <= 1)) {
                    String btnText = button.getContext().getResources().getString(R.string.otp_resend_code);
                    button.setText(btnText);
                }
            }

        }.start();
    }
}

