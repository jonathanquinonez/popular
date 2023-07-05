package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.exception.InvalidPhoneNumberFormatException;
import com.popular.android.mibanco.model.EnrollmentLiteRequest;
import com.popular.android.mibanco.task.LiteEnrollmentTasks;
import com.popular.android.mibanco.util.AlertDialogParameters;
import com.popular.android.mibanco.util.AutoLoginUtils;
import com.popular.android.mibanco.util.ContactsManagementUtils;
import com.popular.android.mibanco.util.EnrollmentLiteStatus;
import com.popular.android.mibanco.util.EnrollmentLiteUtils;
import com.popular.android.mibanco.util.MobileCashUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.ws.response.EnrollmentLiteResponse;

public class EnrollmentLiteWelcomeActivity extends BaseActivity {

    private Context mContext = this;
    private boolean isCustomer = false;
    private String phoneNumber = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment_lite_welcome);
        isCustomer = getIntent().getBooleanExtra(MiBancoConstants.KEY_ENROLL_LITE_IS_CUSTOMER, false);

        Button enrollBtn = (Button)findViewById(R.id.btnEnroll);
        if(enrollBtn != null) {
            enrollBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enrollButton();
                }
            });
        }

        TextView txtTitle = (TextView) findViewById(R.id.txt_enrollment_lite_title);
        TextView txtDetails = (TextView) findViewById(R.id.txt_enrollment_lite_details);
        String description = "";
        if(isCustomer){
            txtTitle.setText(getResources().getString(R.string.enrollment_lite_client_title));
            description = getResources().getString(R.string.enrollment_lite_client_details);
        }else{
            txtTitle.setText(getResources().getString(R.string.enrollment_lite_nonclient_title));
            description = getResources().getString(R.string.enrollment_lite_nonclient_details);
        }
        Linkify.addLinks(txtDetails, Linkify.ALL);
        txtDetails.setMovementMethod(LinkMovementMethod.getInstance());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            txtDetails.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
        } else {
            txtDetails.setText(Html.fromHtml(description));
        }

//        TextView clickableLinkText = (TextView) findViewById(R.id.txt_enrollment_lite_details2);
//        String message = getResources().getString(R.string.enrollment_lite_client_details2);
//        String linkString = "<a href=\""+getResources().getString(R.string.easycash_link_learnmore)+"\">"+getResources().getString(R.string.easycash_linktext_learnmore)+"</a>";
//        clickableLinkText.setText(Html.fromHtml(message.replace("[link]",linkString)));
//        clickableLinkText.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());


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

    private void enrollButton()
    {
        if(isCustomer){
            if(FeatureFlags.CASH_DROP_FINGERPRINT_REQUIRED() ){
               if(AutoLoginUtils.osFingerprintRequirements(this,true)) {//Phone has fingerprint configured
                    if (AutoLoginUtils.getFingerprintPreference(this)) {// Has fingerprint enabled in the app
                        verifyIfPhoneInAlerts();
                    } else {
                        // Display view to enable fingerprint in the app
                        final Intent campaignIntent = new Intent(mContext, CampaignActivity.class);
                        campaignIntent.putExtra(MiBancoConstants.KEY_ENROLL_LITE_FINGERPRINT, true);
                        startActivityForResult(campaignIntent, MiBancoConstants.KEY_ENROLL_LITE_FP);
                    }
                }else{
                   AlertDialogParameters params = new AlertDialogParameters(mContext, R.string.enrollment_lite_fingerprint_required,fingerprintMessage);
                   params.setPositiveButtonText(getResources().getString(R.string.ok).toUpperCase());
                   Utils.showAlertDialog(params);

               }
            }else {
                verifyIfPhoneInAlerts();
            }

        }else {
            EnrollmentLiteUtils.displayEnrollmentProfileInfo(mContext, false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MiBancoConstants.KEY_ENROLL_LITE_FP) {
                verifyIfPhoneInAlerts();
            }
        }
    }

    private void verifyIfPhoneInAlerts(){
        LiteEnrollmentTasks.getHasMobilePhoneInAlerts(mContext, new LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse>() {
            @Override
            public void onLiteEnrollmentApiResponse(EnrollmentLiteResponse result) {
                if(result == null
                        || result.getStatus() == EnrollmentLiteStatus.NO_SESSION_ERROR.getCode()
                        || result.getStatus() == EnrollmentLiteStatus.BACKEND_ERROR.getCode()){
                    MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);
                }else{
                    if(!Utils.isBlankOrNull(result.getAlertsPhoneNumber())){
                        if(result.getStatus() == EnrollmentLiteStatus.CUSTOMER_PHONE_FOUND.getCode()){
                            phoneNumber = result.getAlertsPhoneNumber();
                            displayPhoneAlertsMessage();
                        }else{
                            // Attacks case where PHONE_IN_BLACKLIST, NO_CUSTOMER_PHONE_FOUND
                            EnrollmentLiteUtils.displayEnrollmentProfileInfo(mContext, isCustomer);
                        }
                    }else{
                        EnrollmentLiteUtils.displayEnrollmentProfileInfo(mContext, isCustomer);
                    }
                }
            }
        });
    }

    private void submitInfoAndGoToTermsAndConditions()
    {
        EnrollmentLiteRequest enrollmentLiteRequest = new EnrollmentLiteRequest();
        enrollmentLiteRequest.setPhoneNumber(phoneNumber);
        enrollmentLiteRequest.setDeviceId(App.getApplicationInstance().getDeviceId());
        LiteEnrollmentTasks.postLiteEnrollCustomerInfo(mContext, enrollmentLiteRequest, new LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse>() {
            @Override
            public void onLiteEnrollmentApiResponse(EnrollmentLiteResponse result) {

                if(result != null) {
                    if (result.getStatus() == EnrollmentLiteStatus.DISPLAY_TERMS_AND_CONDITIONS.getCode()) {
                        EnrollmentLiteUtils.displayTermsAndConditionsForm(mContext, isCustomer, false);

                    } else if (result.getStatus() == EnrollmentLiteStatus.MISSING_INFO.getCode()) {
                        MobileCashUtils.informativeMessage(mContext, R.string.enrollment_lite_missing_info);

                    } else {
                        MobileCashUtils.logSystemError(isCustomer);
                        MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);

                    }
                }else {
                    MobileCashUtils.logSystemError(isCustomer);
                    MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);

                }
            }
        });
    }

    private void displayPhoneAlertsMessage()
    {
        try {
            String message = mContext.getString(R.string.enrollment_lite_existing_alerts_phone_message);
            message = message.replace("(?)", ContactsManagementUtils.formatPhoneNumber(this, phoneNumber, false));
            AlertDialogParameters params = new AlertDialogParameters(mContext, message, choosePhoneToUse);
            params.setPositiveButtonText(getResources().getString(R.string.easycash_alerts_phone_yes));
            params.setNegativeButtonText(getResources().getString(R.string.easycash_alerts_phone_no).toUpperCase());
            Utils.showAlertDialog(params);
        } catch (InvalidPhoneNumberFormatException e) {
            Log.e("EnrollmentLiteWelcomeActivity", e.toString());
        }
    }

    DialogInterface.OnClickListener choosePhoneToUse = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    App.getApplicationInstance().setCustomerPhone(phoneNumber);
                    submitInfoAndGoToTermsAndConditions();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    EnrollmentLiteUtils.displayEnrollmentProfileInfo(mContext, isCustomer);
                    break;
                default:
                    break;
            }
            dialog.dismiss();
        }
    };

    DialogInterface.OnClickListener fingerprintMessage = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    verifyIfPhoneInAlerts();
                    break;
                default:
                    break;
            }
            dialog.dismiss();
        }
    };

}
