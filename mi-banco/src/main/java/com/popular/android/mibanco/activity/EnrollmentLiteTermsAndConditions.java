package com.popular.android.mibanco.activity;

import static com.popular.android.mibanco.MiBancoConstants.KEY_ENROLL_LITE_IS_CUSTOMER;
import static com.popular.android.mibanco.MiBancoConstants.KEY_ENROLL_LITE_NEXT_STEP_SMS;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.task.LiteEnrollmentTasks;
import com.popular.android.mibanco.util.AutoLoginUtils;
import com.popular.android.mibanco.util.EnrollmentLiteStatus;
import com.popular.android.mibanco.util.EnrollmentLiteUtils;
import com.popular.android.mibanco.util.MobileCashUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.ws.response.EnrollmentLiteResponse;

import java.util.Locale;

public class EnrollmentLiteTermsAndConditions extends BaseActivity {

    private Context mContext = this;
    private boolean willGenerateCode = true;
    private boolean isCustomer = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_enrollment_lite_terms_and_conditions);

        willGenerateCode = getIntent().getBooleanExtra(KEY_ENROLL_LITE_NEXT_STEP_SMS, true);
        isCustomer = getIntent().getBooleanExtra(KEY_ENROLL_LITE_IS_CUSTOMER, false);

        WebView webViewTerms = (WebView) findViewById(R.id.webViewTerms);
        webViewTerms.getSettings().setJavaScriptEnabled(true);
        Button btnAgree = (Button) findViewById(R.id.btnAgree);
        Button btnDisagree = (Button) findViewById(R.id.btnDisagree);

        Object[] elementsList = {webViewTerms,btnAgree};
        if(!EnrollmentLiteUtils.isUiElementNull(elementsList)) {
            String requestedUrl = Utils.getLocaleStringResource(new Locale(application.getLanguage()), R.string.enrollment_lite_terms_and_conditions_url, mContext);
            if(!requestedUrl.contains("javascript") && Utils.isValidUrl(requestedUrl, getApplicationContext())) {
                webViewTerms.loadUrl(requestedUrl);
            }else{
                webViewTerms.goBack();
            }
            //webViewTerms.loadUrl(getString(R.string.enrollment_lite_terms_and_conditions_url));
            btnAgree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (willGenerateCode) {
                        generateSmsCode();
                    } else {
                        EnrollmentLiteUtils.executeLiteEnrollment(mContext, isCustomer);
                    }
                }
            });

            btnDisagree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MobileCashUtils.enrollmentLiteWelcomeScreen(mContext, isCustomer);
                    finish();
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

    private void generateSmsCode()
    {
        LiteEnrollmentTasks.getGenerateSmsCode(mContext, false, new LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse>() {
            @Override
            public void onLiteEnrollmentApiResponse(EnrollmentLiteResponse result) {

                if(result != null) {
                    if (result.getStatus() == EnrollmentLiteStatus.MISSING_INFO.getCode()) {
                        MobileCashUtils.informativeMessage(mContext, R.string.enrollment_lite_missing_info);

                    } else if (result.getStatus() == EnrollmentLiteStatus.SMS_SERVICE_REQUESTED.getCode()) {

                        EnrollmentLiteUtils.displaySmsCode(mContext, isCustomer);

                    } else {
                        MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);
                    }
                }else{
                    MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);
                }
            }
        });
    }

}
