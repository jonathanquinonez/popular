package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.util.BPAnalytics;

/**
 * Activity prompting for answering the security question.
 */
public class SecurityQuestion extends BaseActivity {

    /**
     * The switch widget reference.
     */
    private SwitchCompat rememberDeviceSwitch;

    /**
     * The text question answer.
     */
    private EditText textQuestionAnswer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.security_question);
        if(application == null || application.getAsyncTasksManager() == null){
            errorReload();
        }else {

            textQuestionAnswer = (EditText) findViewById(R.id.editSecurityQuestion);
            rememberDeviceSwitch = (SwitchCompat) findViewById(R.id.switchRememberDevice);

            textQuestionAnswer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        sendQuestionAnswer();
                        return true;
                    }
                    return false;
                }
            });

            ((TextView) findViewById(R.id.textSecurityQuestion)).setText(Html.fromHtml(Html.fromHtml(getIntent().getStringExtra("question")).toString()));

            final Button btnSubmit = (Button) findViewById(R.id.btnSubmitAnswer);
            btnSubmit.setEnabled(false);

            btnSubmit.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    sendQuestionAnswer();
                }
            });

            textQuestionAnswer.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    btnSubmit.setEnabled(s.length() > 0);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        BPAnalytics.onStartSession(this);
        BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_CHALLENGED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BPAnalytics.onEndSession(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(false);
        menu.findItem(R.id.menu_contact).setVisible(false);

        return true;
    }
    
    private void sendQuestionAnswer() {
        application.getAsyncTasksManager().question(SecurityQuestion.this, textQuestionAnswer.getText().toString(), rememberDeviceSwitch.isChecked(), new ResponderListener() {

            @Override
            public void responder(final String responderName, final Object data) {
                if (responderName != null && (responderName.equalsIgnoreCase("password") || responderName.equalsIgnoreCase("question") && data != null)) {
                    if (responderName.equalsIgnoreCase("password")) {
                        final Intent i = new Intent(getApplicationContext(), EnterPassword.class);
                        startActivity(i);
                        finish();
                    } else {
                        ((TextView) findViewById(R.id.textSecurityQuestion)).setText((String) data);
                        textQuestionAnswer.setText("");
                    }
                } else {
                    BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_UNKNOWN_ERROR_CHALLENGE);
                    application.reLogin(SecurityQuestion.this);
                }
            }

            @Override
            public void sessionHasExpired() {
                application.reLogin(SecurityQuestion.this);
            }
        });
    }
}
