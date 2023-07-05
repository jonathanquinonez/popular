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
import com.popular.android.mibanco.ws.response.AthmEnrollConfirmation;

/**
 * Activity that manages the login information in the ATHM enrollment process
 */
public class AthmLoginInfo extends AthmActivity implements View.OnClickListener, TextWatcher {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnContinue;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.athm_login_info);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);

        etUsername.addTextChangedListener(this);
        etPassword.addTextChangedListener(this);

        btnContinue = (Button) findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(this);

        findViewById(R.id.btnForgot).setOnClickListener(this);
        findViewById(R.id.btnCancel).setOnClickListener(this);

        validate();
    }

    private void validate() {
        if (TextUtils.isEmpty(etUsername.getText()) || TextUtils.isEmpty(etPassword.getText())) {
            btnContinue.setEnabled(false);
        } else {
            btnContinue.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnContinue:
                AthmTasks.postAthmEnrollLogin(this, etUsername.getText().toString(), etPassword.getText().toString(), new AthmTasks.AthmListener<AthmEnrollConfirmation>() {
                    @Override
                    public void onAthmApiResponse(AthmEnrollConfirmation result) {
                        if(result != null && !result.isAlertError() && !result.isBlocked() && !result.isDowntime()) {
                            Intent athmLoginIntent = new Intent();
                            athmLoginIntent.setClass(AthmLoginInfo.this, AthmRegistrationCompleted.class);
                            startActivity(athmLoginIntent);
                            BPAnalytics.logEvent(BPAnalytics.EVENT_ATHM_LOGIN_SUCCESSFUL);
                            finish();
                        }else{
                            BPAnalytics.logEvent(BPAnalytics.EVENT_ATHM_LOGIN_FAILED);
                        }
                    }
                });
                break;
            case R.id.btnForgot:
                Intent webViewIntent = new Intent(this, WebViewActivity.class);
                webViewIntent.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, getString(R.string.athm_forgot_password_url));
                webViewIntent.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
                startActivity(webViewIntent);
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
