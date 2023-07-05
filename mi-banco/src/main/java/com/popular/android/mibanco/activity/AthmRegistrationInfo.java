package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.task.AthmTasks;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.view.AlertDialogFragment;
import com.popular.android.mibanco.ws.response.AthmEnrollConfirmation;

/**
 * Activity that manages the registration information in the enrollment process
 */
public class AthmRegistrationInfo extends AthmActivity implements TextWatcher {

    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnContinue;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.athm_registration_info);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);

        etUsername.addTextChangedListener(this);
        etPassword.addTextChangedListener(this);
        etConfirmPassword.addTextChangedListener(this);

        btnContinue = (Button) findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(this);

        findViewById(R.id.btnCancel).setOnClickListener(this);

        validate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MiBancoConstants.ATHM_TERMS_AND_CONDITIONS_REQUEST_CODE && resultCode == RESULT_OK) {
            AthmTasks.postAthmEnrollRegistration(this, etUsername.getText().toString(), etPassword.getText().toString(), etConfirmPassword.getText().toString(),"Y", new AthmTasks.AthmListener<AthmEnrollConfirmation>() {
                @Override
                public void onAthmApiResponse(AthmEnrollConfirmation result) {
                    if (result != null && !result.isAlertError() && !result.isBlocked() && !result.isDowntime()) {
                        Intent athmRegistrationIntent = new Intent();
                        athmRegistrationIntent.setClass(AthmRegistrationInfo.this, AthmRegistrationCompleted.class);
                        startActivity(athmRegistrationIntent);
                        BPAnalytics.logEvent(BPAnalytics.EVENT_ATHM_ENROLL_SUCCESSFUL);
                        finish();
                    }
                }
            });
        }
    }

    private void validate() {
        if (TextUtils.isEmpty(etUsername.getText()) || TextUtils.isEmpty(etPassword.getText()) || TextUtils.isEmpty(etConfirmPassword.getText())) {
            btnContinue.setEnabled(false);
        } else {
            btnContinue.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnContinue:
                if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
                    AlertDialogFragment.showAlertDialog(this, null, R.string.athm_password_confirmation_match_error, R.string.ok, null, MiBancoConstants.MiBancoDialogId.ERROR, null, false);
                } else {
                    startActivityForResult(new Intent(this, AthmTermsAndConditions.class), MiBancoConstants.ATHM_TERMS_AND_CONDITIONS_REQUEST_CODE);
                }
                break;
            case R.id.btnCancel:
                finish();
                break;
            default:
                super.onClick(v);
                break;
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        validate();
    }
}
