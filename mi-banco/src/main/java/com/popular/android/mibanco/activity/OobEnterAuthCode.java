package com.popular.android.mibanco.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoPreferences;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.listener.SimpleListener;
import com.popular.android.mibanco.model.OobChallenge;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by S681718 on 6/27/2016.
 * Implementation for OOB validation.
 */
public class OobEnterAuthCode extends BaseActivity {

    private EditText textCode;
    private TextView textInstructionsOOB;
    private TextView textVoiceCode;
    private Button btnSubmitCode;

    private OobChallenge oobData;
    private final String PRIMAY_PHONE = "primary_phone";
    private final String ALTERNATE_PHONE = "alternate_phone";
    private final String RECOVERY_CODE = "recovery_code";
    private final String NOT_HAVE_ANY_CODE = "no_code";
    private final String RESEND_CODE = "resend_code";
    private final String OOB_SMS_TYPE_CHALLENGE = "OOBSMS";
    private final String LOGIN_RESPONSE = "login";
    private final String QUESTION_RESPONSE = "question";
    private final String PASSWORD_RESPONSE = "password";
    private final HashMap<String, OobOption> options = new HashMap<String, OobOption> () {
        {
            put(RECOVERY_CODE,new OobOption(RECOVERY_CODE,R.string.oob_use_rec_code, OobUseMyRecoveryCode.class, "3"));
            put(NOT_HAVE_ANY_CODE, new OobOption(NOT_HAVE_ANY_CODE,R.string.oob_none_codes, OobNoneOfTheCodes.class, "4"));
            put(ALTERNATE_PHONE, new OobOption(ALTERNATE_PHONE,R.string.oob_use_alt_phone, OobEnterAuthCode.class, "2"));
            put(PRIMAY_PHONE, new OobOption(PRIMAY_PHONE,R.string.oob_use_primary_phone, OobEnterAuthCode.class, "0"));
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        BPAnalytics.onStartSession(this);
        BPAnalytics.logEvent(BPAnalytics.EVENT_OOB_PROCESS_CHALLENGED_CODE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BPAnalytics.onEndSession(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.menu_settings).setVisible(true);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(true);
        menu.findItem(R.id.menu_contact).setVisible(true);

        return true;
    }

    @Override
    public void onBackPressed() {
         super.onBackPressed();
        application.reLogin(OobEnterAuthCode.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ErrorView.ERROR_ACCOUNT_BLOCKED) {
            application.reLogin(OobEnterAuthCode.this);
            finish();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        oobData = (OobChallenge) getIntent().getSerializableExtra(MiBancoConstants.OOB_DATA);

        setContentView(R.layout.oob_enter_auth_code);

        textInstructionsOOB = (TextView) findViewById(R.id.textOOBCodeInstructions);

        setOOBScreenOptions(oobData);
       // final Button
         btnSubmitCode = (Button) findViewById(R.id.btnSubmitCode);

        if(oobData.getContent().getChallengeType().equalsIgnoreCase(OOB_SMS_TYPE_CHALLENGE)) {
            textCode.setOnEditorActionListener(new TextView.OnEditorActionListener(){
                @Override
                public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        if(oobData.getContent().getChallengeType().equalsIgnoreCase(OOB_SMS_TYPE_CHALLENGE)) {
                            sendActionOOB(MiBancoConstants.OOB_VALIDATE_SMSCODE,textCode.getText().toString());
                        }else{
                            sendActionOOB(MiBancoConstants.OOB_VALIDATE_CALLCODE,"");
                        }
                        return true;
                    }
                    return false;
                }
            });

            btnSubmitCode.setEnabled(false);
            btnSubmitCode.setText(R.string.continue_phrase);
        }

        findViewById(R.id.btnNoPhoneAvailable).setOnClickListener(noPhoneAvailableOnClick);
    }



    /**
     * Method to perform call to OOB webService.
     * @param action  actions to perform a OOB Challenge like VALIDATE_SMSCODE, SEND_SMSCODE or SEND_ALTPHONE
     * @param param   Target or code to process
     */
    private void sendActionOOB(final String action, final String param) {

        application.getAsyncTasksManager().loginOOB(OobEnterAuthCode.this,action , param, new ResponderListener() {
            @Override
            public void responder(final String responderName, final Object oobData) {
                OobChallenge data = (OobChallenge) oobData;

                if (responderName != null && !responderName.equalsIgnoreCase(LOGIN_RESPONSE)) {
                    Class<?> cls = null;
                    if (responderName.equalsIgnoreCase(QUESTION_RESPONSE)) {
                        cls = SecurityQuestion.class;
                    } else if (responderName.equalsIgnoreCase(MiBancoConstants.OOB_ACTION_NAME)) {
                        if(data.getContent().getRsaBlocked()) {
                            showRegainAccess();
                        } else if(action.equalsIgnoreCase(MiBancoConstants.OOB_SEND_SMSCODE)) {
                             Utils.showAlert(OobEnterAuthCode.this, getString(R.string.new_code_sent), new SimpleListener() {
                                 @Override
                                 public void done() {
                                     textCode.setText("");
                                 }
                             });
                        } else if(action.equalsIgnoreCase(MiBancoConstants.OOB_SEND_ALT_PHONE)) {
                                cls = options.get(ALTERNATE_PHONE).getCls();
                        } else if(action.equalsIgnoreCase(MiBancoConstants.OOB_CALL_PHONE)) {
                            Utils.showAlert(OobEnterAuthCode.this, getString(R.string.new_call_made), new SimpleListener() {
                                @Override
                                public void done() {
                                    textCode.setText("");
                                }
                            });
                            textVoiceCode = (TextView) findViewById(R.id.textOOBVoiceCode);
                            textVoiceCode.setText(data.getContent().getCodeVoiceCall());
                        }

                    } else if (responderName.equalsIgnoreCase(PASSWORD_RESPONSE)) {
                        setOpacOptions(data);
                        cls = EnterPassword.class;
                    }
                    if (cls != null) {

                        final Intent i = new Intent(getApplicationContext(), cls);
                        if (cls.equals(OobEnterAuthCode.class)) {
                            i.putExtra(MiBancoConstants.OOB_DATA, data);
                        }
                        startActivity(i);
                        finish();
                    }
                } else if (responderName.equalsIgnoreCase(LOGIN_RESPONSE)) {
                    if(data.getContent().getRsaBlocked() || data.getFlags().getAccessBlocked()) {
                        showRegainAccess();
                    }
                }
            }

            @Override
            public void sessionHasExpired() {
                application.reLogin(OobEnterAuthCode.this);
            }
        });
    }

    private void showRegainAccess() {
        final Intent intent = new Intent(OobEnterAuthCode.this, ErrorView.class);
        intent.putExtra("errorCode", ErrorView.ERROR_ACCOUNT_BLOCKED);
        intent.putExtra("errorMessage", getString(R.string.account_blocked_title));
        OobEnterAuthCode.this.startActivityForResult(intent,ErrorView.ERROR_ACCOUNT_BLOCKED);
    }
    /**
     *  Set all values on the screen.
     * @param oobData data Required for set the screen.
     */
    public void setOOBScreenOptions(OobChallenge oobData) {
        TextView title = (TextView) findViewById(R.id.textOOBVerificationCode);
        title.setText(getString(R.string.verification_code).toUpperCase());
        textCode = (EditText) findViewById(R.id.editAuthCode);

        textVoiceCode = (TextView) findViewById(R.id.textOOBVoiceCode);
        TextView btnResendCode = (TextView) findViewById(R.id.btnResendCode);


        if(oobData.getResponderMessage().equalsIgnoreCase(PRIMAY_PHONE)){
            if(oobData.getContent().getChallengeType().equalsIgnoreCase(OOB_SMS_TYPE_CHALLENGE)) {
                textCode.setVisibility(View.VISIBLE);
                textCode.requestFocus();
                textInstructionsOOB.setText(R.string.oob_sms_primary_instructions);
                btnResendCode.setOnClickListener(resendCodeOnClick);
                btnResendCode.setText(R.string.resend_code);
                findViewById(R.id.btnSubmitCode).setOnClickListener(submitCodeOnClick);
                textVoiceCode.setVisibility(View.GONE);
                setEventChangeButton();
                if(oobData.isHasInvalidCode()) {
                    textCode.setError(getString(R.string.invalid_code));
                }
                //Display de soft-kayboard
                this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            } else {
                textCode.setVisibility(View.GONE);
                textInstructionsOOB.setText(R.string.oob_call_primary_instructions);
                btnResendCode.setOnClickListener(makeCallOnClick);
                btnResendCode.setText(R.string.oob_make_new_call);
                textVoiceCode.setText(oobData.getContent().getCodeVoiceCall());
                textVoiceCode.setVisibility(View.VISIBLE);
                findViewById(R.id.btnSubmitCode).setOnClickListener(submitCallOnClick);
            }

            textInstructionsOOB.append(" ");
            textInstructionsOOB.append(oobData.getContent().getPhone().replaceAll("-", "‑"));
        //secondary phone
        } else {
            if(oobData.getContent().getChallengeType().equalsIgnoreCase(OOB_SMS_TYPE_CHALLENGE)) {
                textCode.setVisibility(View.VISIBLE);
                textCode.requestFocus();
                textInstructionsOOB.setText(R.string.oob_sms_alternate_instructions);
                findViewById(R.id.btnSubmitCode).setOnClickListener(submitCodeOnClick);
                btnResendCode.setOnClickListener(resendCodeOnClick);
                btnResendCode.setText(R.string.resend_code);

                textVoiceCode.setVisibility(View.GONE);
                setEventChangeButton();
                if(oobData.isHasInvalidCode()) {
                    textCode.setError(getString(R.string.invalid_code));
                }
            } else {
                textCode.setVisibility(View.GONE);
                textInstructionsOOB.setText(R.string.oob_call_alternate_instructions);
                btnResendCode.setOnClickListener(makeCallOnClick);
                btnResendCode.setText(R.string.oob_make_new_call);
                textVoiceCode.setText(oobData.getContent().getCodeVoiceCall());
                textVoiceCode.setVisibility(View.VISIBLE);
                findViewById(R.id.btnSubmitCode).setOnClickListener(submitCallOnClick);
                options.get(oobData.getResponderMessage()).setIdText(R.string.oob_use_alt_phone_call);
            }

            textInstructionsOOB.append(" ");
            textInstructionsOOB.append(oobData.getContent().getPhone().replaceAll("-", "‑"));
        }


    }

    /**
     * Event OnClick listener for button "No phone Available On Click", display a dialog with 2 or 3 options.
     *
     */
    View.OnClickListener noPhoneAvailableOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final List<String> menuItems = new ArrayList<String>();
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(OobEnterAuthCode.this);

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    OobEnterAuthCode.this,
                    android.R.layout.simple_list_item_1);

            if(oobData.getContent().isHasAltPhone()){
                arrayAdapter.add(getString(options.get(ALTERNATE_PHONE).getIdText()));
                menuItems.add(ALTERNATE_PHONE);
            }
            arrayAdapter.add(getString(options.get(RECOVERY_CODE).getIdText()));
            menuItems.add(RECOVERY_CODE);
            arrayAdapter.add(getString(options.get(NOT_HAVE_ANY_CODE).getIdText()));
            menuItems.add(NOT_HAVE_ANY_CODE);

            builderSingle.setAdapter(
                    arrayAdapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Intent i = new Intent(getApplicationContext(), options.get(menuItems.get(which)).getCls());
                            if(menuItems.get(which).equalsIgnoreCase(ALTERNATE_PHONE)) {
                                sendActionOOB(MiBancoConstants.OOB_SEND_ALT_PHONE,options.get(ALTERNATE_PHONE).getTarget());
                            } else {
                                startActivity(i);
                                finish();
                            }

                        }
                    });
            builderSingle.show();
        }
    };

    /**
     * Event OnClick listener for the button "Resend Code"
     */
    View.OnClickListener resendCodeOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendActionOOB(MiBancoConstants.OOB_SEND_SMSCODE, options.get(oobData.getResponderMessage()).getTarget());
        }
    };

    /**
     * Event OnClick listener for the button "Send Code to Validate"
     */
    View.OnClickListener submitCodeOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendActionOOB(MiBancoConstants.OOB_VALIDATE_SMSCODE, textCode.getText().toString());
        }
    };

    /**
     * Event OnClick listener for the button "Call voice code"
     */
    View.OnClickListener submitCallOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendActionOOB(MiBancoConstants.OOB_VALIDATE_CALLCODE, "");
        }
    };

    /**
     * Event OnClick listener for the button "Resend Code"
     */
    View.OnClickListener makeCallOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendActionOOB(MiBancoConstants.OOB_CALL_PHONE , options.get(oobData.getResponderMessage()).getTarget());
        }
    };

    private void setEventChangeButton(){
        textCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                btnSubmitCode.setEnabled(s.length() > 0);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btnSubmitCode.setEnabled(s.length() > 0);
            }
        });

    }
    /**
     *
     */
    protected class OobOption {

        private String option;
        private int idText;
        private Class<?> cls;
        private String target;

        /**
         *
         * @param option
         * @param idText
         * @param cls
         * @param target
         */
        OobOption(String option, int idText, Class<?> cls, String target){
            this.option = option;
            this.idText = idText;
            this.cls = cls;
            this.target = target;
        }

        public int getIdText() {
            return idText;
        }

        public void setIdText(int idText) {
            this.idText = idText;
        }

        public Class<?> getCls() {
            return cls;
        }

        public void setCls(Class<?> cls) {
            this.cls = cls;
        }

        public String getOption() {
            return option;
        }

        public void setOption(String option) {
            this.option = option;
        }

        public String getTarget() { return target;}
        public void setTarget(String target) { this.target = target; }
    }

    private void setOpacOptions(OobChallenge oobData){
        if (Boolean.TRUE.toString().equalsIgnoreCase(oobData.getCanOpenAccount())) {
            final HashMap<String, String> opac = new HashMap<>();
            opac.put(MiBancoConstants.CAN_OPEN_ACCOUNT, oobData.getCanOpenAccount());
            opac.put(MiBancoConstants.IS_FOREING_CUSTOMER, oobData.getIsForeignCustomer());
            MiBancoPreferences.setOpac(opac);
        }
    }
}
