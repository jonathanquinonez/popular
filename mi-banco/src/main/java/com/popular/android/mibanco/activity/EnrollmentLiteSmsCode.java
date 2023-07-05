package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.task.LiteEnrollmentTasks;
import com.popular.android.mibanco.util.AlertDialogParameters;
import com.popular.android.mibanco.util.AutoLoginUtils;
import com.popular.android.mibanco.util.EnrollmentLiteStatus;
import com.popular.android.mibanco.util.EnrollmentLiteUtils;
import com.popular.android.mibanco.util.MobileCashUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.ws.response.EnrollmentLiteResponse;

public class EnrollmentLiteSmsCode extends BaseActivity {

    private Context mContext = this;
    private boolean isCustomer = false;
    private static final String TAG = "EnrollmentLiteSmsCode";
    private static final int RESEND_MINUTES = ((2*60)*1000);

    private EditText passcodeEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment_lite_sms_code);
        passcodeEditText = (EditText)findViewById(R.id.editTextPasscode);
        Button btnConfirmCode = (Button) findViewById(R.id.btnConfirmCode);
        Button resendBtn = (Button) findViewById(R.id.btnResendCode);

        isCustomer = getIntent().getBooleanExtra(MiBancoConstants.KEY_ENROLL_LITE_IS_CUSTOMER, false);
        Object[] elementsList = {passcodeEditText, btnConfirmCode, resendBtn};

        if(!EnrollmentLiteUtils.isUiElementNull(elementsList)) {
            btnConfirmCode.setEnabled(true);
            btnConfirmCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    codeConfirmation(passcodeEditText.getText().toString());
                }
            });

            resendCountDown();
            resendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resendCode();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isCustomer) {
            if (!AutoLoginUtils.isDeviceSecured(mContext)) {
                MobileCashUtils.passcodeRequiredScreen(mContext, "passcode-enrollment");
            }
        }
    }

    private void resendCode()
    {
            LiteEnrollmentTasks.getGenerateSmsCode(mContext, true, new LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse>() {
                @Override
                public void onLiteEnrollmentApiResponse(EnrollmentLiteResponse result) {
                    if (result != null) {
                        if (result.getStatus() == EnrollmentLiteStatus.MISSING_INFO.getCode()) {
                            MobileCashUtils.informativeMessage(mContext, R.string.enrollment_lite_missing_info);

                        } else if (result.getStatus() == EnrollmentLiteStatus.SMS_RESEND_LIMIT.getCode()) {
                            AlertDialogParameters params = new AlertDialogParameters(mContext, R.string.enrollment_lite_sms_resend_reached, goToBeginningOnClick);
                            params.setPositiveButtonText(getResources().getString(R.string.ok));
                            Utils.showAlertDialog(params);

                        } else if (result.getStatus() == EnrollmentLiteStatus.SMS_SERVICE_REQUESTED.getCode()) {
                            resendCountDown();
                            MobileCashUtils.informativeMessage(mContext, R.string.mc_sms_resent);

                        } else {

                            MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);

                        }
                    } else {
                        MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);
                    }
                }
            });
    }

    private void resendCountDown()
    {
        final Button resendBtn = (Button) findViewById(R.id.btnResendCode);
        resendBtn.setEnabled(false);
        resendBtn.setTextColor(Color.LTGRAY);
        new CountDownTimer(RESEND_MINUTES, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //this will be done every 1000 milliseconds ( 1 seconds )
                String v = String.format("%02d", millisUntilFinished/60000);
                int va = (int)( (millisUntilFinished%60000)/1000);
                String btnText = getResources().getString(R.string.enrollment_lite_resend_code)+" ("+v+":"+String.format("%02d",va)+")";
                resendBtn.setText(btnText);
            }

            @Override
            public void onFinish() {
                resendBtn.setEnabled(true);
                resendBtn.setText(getResources().getString(R.string.enrollment_lite_resend_code));
                resendBtn.setTextColor(Color.WHITE);
            }

        }.start();
    }

    private void codeConfirmation(String code)
    {
        LiteEnrollmentTasks.getValidateSmsCode(mContext, code, new LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse>() {
            @Override
            public void onLiteEnrollmentApiResponse(EnrollmentLiteResponse result) {
                if(result == null){
                    MobileCashUtils.logSystemError(isCustomer);
                    MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);
                }else{
                    if(result.getStatus() == EnrollmentLiteStatus.VALIDATION_SUCCESS.getCode()){
                        EnrollmentLiteUtils.executeLiteEnrollment(mContext, isCustomer);

                    }else if(result.getStatus() == EnrollmentLiteStatus.VALIDATION_FAILED.getCode()){
                        MobileCashUtils.informativeMessage(mContext, R.string.enrollment_lite_wrong_sms);
                        passcodeEditText.setText("");

                    }else if(result.getStatus() == EnrollmentLiteStatus.MISSING_INFO.getCode()){
                        MobileCashUtils.informativeMessage(mContext, R.string.enrollment_lite_missing_info);

                    }else if(result.getStatus() == EnrollmentLiteStatus.SMS_ERROR_LIMIT_REACHED.getCode()){
                        AlertDialogParameters params = new AlertDialogParameters(mContext, R.string.enrollment_lite_sms_fail_reached,goToBeginningOnClick);
                        params.setPositiveButtonText(getResources().getString(R.string.ok));
                        Utils.showAlertDialog(params);
                    }else{
                        MobileCashUtils.logSystemError(isCustomer);
                        MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);
                    }
                }
            }
        });
    }


    DialogInterface.OnClickListener goToBeginningOnClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    EnrollmentLiteUtils.displayEnrollmentProfileInfo(mContext, isCustomer);
                    break;
                default:
                    break;
            }
            dialog.dismiss();
            finish();
        }
    };
}
