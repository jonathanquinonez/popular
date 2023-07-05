package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.model.AthmPhoneProvider;
import com.popular.android.mibanco.model.EnrollmentLiteRequest;
import com.popular.android.mibanco.task.LiteEnrollmentTasks;
import com.popular.android.mibanco.util.AlertDialogParameters;
import com.popular.android.mibanco.util.AutoLoginUtils;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.EnrollmentLiteStatus;
import com.popular.android.mibanco.util.EnrollmentLiteUtils;
import com.popular.android.mibanco.util.MobileCashUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.ws.response.AthmEnrollPhone;
import com.popular.android.mibanco.ws.response.EnrollmentLiteResponse;
import com.qburst.android.widget.spinnerextended.SpinnerExtended;

import java.util.ArrayList;
import java.util.Collections;

public class EnrollmentLiteProfileInformation extends BaseActivity implements AdapterView.OnItemSelectedListener, TextWatcher {

    private Context mContext = this;
    private Button btnContinue;
    private EditText editTextName;
    private EditText editTextLastName;
    private EditText editTextEmail;
    private EditText editTextPhoneNumber;
    private SpinnerExtended spinnerProvider;

    private EnrollmentLiteRequest enrollmentLiteRequest;
    private boolean isCustomer = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment_lite_profile_information);
        isCustomer = getIntent().getBooleanExtra(MiBancoConstants.KEY_ENROLL_LITE_IS_CUSTOMER, false);

        editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        spinnerProvider = (SpinnerExtended)findViewById(R.id.spinnerProvider);
        btnContinue = (Button) findViewById(R.id.btnContinue);
        TextView headerPersonalInfoTv = (TextView)findViewById(R.id.header_personal_info);
        TextView headerAccessInfoTv = (TextView)findViewById(R.id.header_access_info);
        LinearLayout lineSeparatorLl = (LinearLayout)findViewById(R.id.line_separator_pi);
        LinearLayout lineSeparatorL2 = (LinearLayout)findViewById(R.id.line_separator_ai);

        Object[] elementList = {editTextPhoneNumber, editTextName, editTextEmail, editTextLastName,spinnerProvider, headerPersonalInfoTv, lineSeparatorLl, btnContinue};

        if(!EnrollmentLiteUtils.isUiElementNull(elementList)) {

            editTextPhoneNumber.addTextChangedListener(this);
            spinnerProvider.setOnItemSelectedListener(this);

            initPhoneProviders();

            if (!isCustomer) {
                TextView title = (TextView)findViewById(R.id.txtEnrollmentWelcome);
                title.setText(getResources().getString(R.string.cashdrop_welcome_nonclient_instr));
                editTextName.addTextChangedListener(this);
                editTextLastName.addTextChangedListener(this);
                editTextEmail.addTextChangedListener(this);



                headerPersonalInfoTv.setVisibility(View.VISIBLE);
                headerAccessInfoTv.setVisibility(View.VISIBLE);
                lineSeparatorLl.setVisibility(View.VISIBLE);
                lineSeparatorL2.setVisibility(View.VISIBLE);
                editTextName.setVisibility(View.VISIBLE);
                editTextLastName.setVisibility(View.VISIBLE);
                editTextEmail.setVisibility(View.VISIBLE);


            } else {
                headerPersonalInfoTv.setVisibility(View.GONE);
                headerAccessInfoTv.setVisibility(View.GONE);
                lineSeparatorLl.setVisibility(View.GONE);
                lineSeparatorL2.setVisibility(View.GONE);
                editTextName.setVisibility(View.GONE);
                editTextLastName.setVisibility(View.GONE);
                editTextEmail.setVisibility(View.GONE);

            }
            btnContinue.setEnabled(false);
            btnContinue.setBackgroundColor(getResources().getColor(R.color.grey_light));
            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    continueButton();
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

    private void initPhoneProviders()
    {
        LiteEnrollmentTasks.getPhoneProviders(this, new LiteEnrollmentTasks.LiteEnrollmentListener<AthmEnrollPhone>() {
            @Override
            public void onLiteEnrollmentApiResponse(AthmEnrollPhone result) {
                if(result != null) {
                    ArrayList<AthmPhoneProvider> phoneProviders = result.getProviders();
                    Collections.sort(phoneProviders, Utils.providerTitleComparator);
                    ArrayAdapter<AthmPhoneProvider> adapter = new ArrayAdapter<>(
                            EnrollmentLiteProfileInformation.this,
                            R.layout.spinner_extended_selected_item,
                            android.R.id.text1,
                            phoneProviders);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerProvider.setAdapter(adapter);
                }
            }
        });
    }

    private void continueButton(){
        if(enrollmentLiteRequest != null) {
            App.getApplicationInstance().setCustomerPhone(enrollmentLiteRequest.getPhoneNumber());
            if(isCustomer){
                LiteEnrollmentTasks.postLiteEnrollCustomerInfo(mContext, enrollmentLiteRequest, new LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse>() {
                    @Override
                    public void onLiteEnrollmentApiResponse(EnrollmentLiteResponse result) {
                        responseValidator(result);
                    }
                });

            }else{
                LiteEnrollmentTasks.postLiteEnrollNonCustomerInfo(mContext, enrollmentLiteRequest, new LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse>() {
                    @Override
                    public void onLiteEnrollmentApiResponse(EnrollmentLiteResponse result) {
                        responseValidator(result);
                    }
                });
            }
        }
    }


    private void responseValidator(EnrollmentLiteResponse result){
        if (result == null) {
            MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);

        } else {
            if (result.getStatus() == EnrollmentLiteStatus.PHONE_IN_BLACKLIST.getCode()) {
                if (isCustomer) {
                    BPAnalytics.logEvent(BPAnalytics.EVENT_MC_ENROLL_CUSTOMER_BLACKLIST_FAILED);
                } else {
                    BPAnalytics.logEvent(BPAnalytics.EVENT_MC_ENROLL_NON_CUSTOMER_BLACKLIST_FAILED);
                }
                AlertDialogParameters params = new AlertDialogParameters(mContext, R.string.enrollment_lite_phone_blacklist,blackListAction);
                params.setPositiveButtonText(getResources().getString(R.string.ok));
                Utils.showAlertDialog(params);
            }else if(result.getStatus() == EnrollmentLiteStatus.DISPLAY_TERMS_AND_CONDITIONS.getCode()) {
                EnrollmentLiteUtils.displayTermsAndConditionsForm(mContext, isCustomer, true);

            } else if (!isCustomer && result.getStatus() == EnrollmentLiteStatus.CUSTOMER_EMAIL_FOUND.getCode()) {
                // Display message login or continue message
                AlertDialogParameters params = new AlertDialogParameters(mContext, R.string.enrollment_lite_existing_email_msg,chooseToLoginOnClick);
                params.setPositiveButtonText(getResources().getString(R.string.btn_positive_existing_email));
                params.setNegativeButtonText(getResources().getString(R.string.btn_negative_existing_email).toUpperCase());
                Utils.showAlertDialog(params);

            } else if(result.getStatus() == EnrollmentLiteStatus.NAME_MATCH.getCode()) { //OFAC VALIDATION
                BPAnalytics.logEvent(BPAnalytics.EVENT_MC_ENROLL_NON_CUSTOMER_OFAC_FAILED);
                AlertDialogParameters params = new AlertDialogParameters(mContext, R.string.easycash_ofac_hit,blackListAction);
                params.setPositiveButtonText(getResources().getString(R.string.ok));
                Utils.showAlertDialog(params);

            }else if (result.getStatus() == EnrollmentLiteStatus.MISSING_INFO.getCode()) {
                MobileCashUtils.informativeMessage(mContext, R.string.enrollment_lite_missing_info);

            } else {
                MobileCashUtils.logSystemError(isCustomer);
                MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);

            }
        }

    }

    DialogInterface.OnClickListener chooseToLoginOnClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    final Intent iLogin = new Intent(mContext, EnterUsername.class);
                    startActivity(iLogin);
                    finish();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    EnrollmentLiteUtils.displayTermsAndConditionsForm(mContext,isCustomer,true);
                    break;
                default:
                    break;
            }
            dialog.dismiss();
        }
    };

    DialogInterface.OnClickListener blackListAction = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
            dialog.dismiss();
        }
    };

    public static boolean isValidEmail(String target) {
        return (target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private EnrollmentLiteRequest areFieldsComplete()
    {
        if((!isCustomer && (Utils.isBlankOrNull(editTextName.getText().toString())
                || Utils.isBlankOrNull(editTextLastName.getText().toString())
                || Utils.isBlankOrNull(editTextEmail.getText().toString())
                || Utils.isBlankOrNull(editTextPhoneNumber.getText().toString())
                || spinnerProvider.getSelectedItemPosition() == SpinnerExtended.NOTHING_SELECTED_POSITION
                || !isValidEmail(editTextEmail.getText().toString())
                || editTextPhoneNumber.getText().length() <10))
                || (isCustomer && (Utils.isBlankOrNull(editTextPhoneNumber.getText().toString())
                || editTextPhoneNumber.getText().length() <10
                || spinnerProvider.getSelectedItemPosition() == SpinnerExtended.NOTHING_SELECTED_POSITION))
                ) {

            enrollmentLiteRequest = null;
        }else{

            enrollmentLiteRequest = new EnrollmentLiteRequest();
            enrollmentLiteRequest.setPhoneNumber(editTextPhoneNumber.getText().toString());
            enrollmentLiteRequest.setPhoneProvider(((AthmPhoneProvider) spinnerProvider.getSelectedItem()).getValue());
            enrollmentLiteRequest.setDeviceId(App.getApplicationInstance().getDeviceId());

            if(!isCustomer) {
                enrollmentLiteRequest.setFirstName(editTextName.getText().toString());
                enrollmentLiteRequest.setLastName(editTextLastName.getText().toString());
                enrollmentLiteRequest.setEmail(editTextEmail.getText().toString());

            }
        }
        btnContinue.setEnabled((enrollmentLiteRequest != null));
        
        validateBtnContinueColor();
        
        return enrollmentLiteRequest;
    }
    
    public void validateBtnContinueColor(){
        btnContinue.setBackgroundColor(getResources().getColor(btnContinue.isEnabled() ?
                R.color.btn_blue : R.color.grey_light));

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        areFieldsComplete();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        areFieldsComplete();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        areFieldsComplete();
    }

}
