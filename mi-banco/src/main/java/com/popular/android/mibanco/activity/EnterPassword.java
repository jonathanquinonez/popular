package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.model.User;
import com.popular.android.mibanco.util.AutoLoginUtils;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.PushUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.util.enums.RegainAccessTypeEnum;
import com.popular.android.mibanco.ws.response.PushTokenRequest;
import com.popular.android.mibanco.ws.response.PushTokenResponse;

import org.apache.commons.lang3.StringUtils;

import static com.popular.android.mibanco.util.PushUtils.isInitialPushTokenSaved;

/**
 * Activity that manages the password submit process
 */
public class EnterPassword extends BaseActivity {

    private EditText textPwd;
    private TextInputLayout passwordLayout;
    private Context context;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (application == null || application.getAsyncTasksManager() == null) {
            errorReload();
        } else {
            context = this;
            setContentView(R.layout.enter_password);

            textPwd = (EditText) findViewById(R.id.editPassword);
            passwordLayout = findViewById(R.id.passwordLayout);

            textPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        sendPassword(textPwd.getText().toString());
                        return true;
                    }
                    return false;
                }
            });

            final Button btnSubmit = (Button) findViewById(R.id.btnSubmitPassword);
            if (btnSubmit != null) {
                btnSubmit.setEnabled(false);

                btnSubmit.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(final View v) {
                        sendPassword(textPwd.getText().toString());
                    }
                });
            }

            textPwd.addTextChangedListener(new TextWatcher() {

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

            findViewById(R.id.textViewForgotPassword).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
                    String isSecuritiesCustomer = sharedPreferences.getString("isSecuritiesCustomer", null);
                    final Intent intentForgotUsername = new Intent(EnterPassword.this, WebViewActivity.class);
                    intentForgotUsername.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.regain_access_url, RegainAccessTypeEnum.PASSWORD,  App.getApplicationInstance().getLanguage())));
                    //MBFIS-155
                    if (isSecuritiesCustomer != null && isSecuritiesCustomer.equalsIgnoreCase("true")) {
                        intentForgotUsername.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY,Utils.getAbsoluteUrl(getString(R.string.forgot_password_sec_url)));
                        intentForgotUsername.putExtra(MiBancoConstants.WEB_VIEW_TOOLBAR_HIDE_KEY, true);
                    }

                    String[] urlBlacklist = getResources().getStringArray(R.array.web_view_url_blacklist);
                    for (int i = 0; i < urlBlacklist.length; ++i) {
                        urlBlacklist[i] = Utils.getAbsoluteUrl(urlBlacklist[i]);
                    }
                    intentForgotUsername.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, urlBlacklist);
                    intentForgotUsername.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
                    startActivityForResult(intentForgotUsername, MiBancoConstants.PASSWORD_RECOVERY_REQUEST_CODE);
                }
            });


            if (AutoLoginUtils.isFingerprintAbsolutelyReady(context, App.getApplicationInstance().getCurrentUser(), true)
                    && App.getApplicationInstance().isAutoLogin()) {
                btnSubmit.setEnabled(false);
                String password = Utils.getPasswordDecrypted(App.getApplicationInstance().getCurrentUser());
                try {
                    textPwd.setText(password);
                    textPwd.setEnabled(false);
                    sendPassword(password);
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        BPAnalytics.onStartSession(this);
        BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_ASKED_FOR_PASSWORD);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MiBancoConstants.PASSWORD_RECOVERY_REQUEST_CODE:
                    final Intent iLogin = new Intent(this, EnterUsername.class);
                    iLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(iLogin);
                    finish();
                    break;
                case MiBancoConstants.OUTREACH_REQUEST_CODE:
                case MiBancoConstants.INTERRUPTION_REQUEST_CODE:
                    this.nextActivityOnSuccess();
                    finish();
                    break;
                case MiBancoConstants.RSA_ENROLL_REQUEST_CODE:
                case MiBancoConstants.SSDS_FORCED_LOGIN_REQUEST_CODE:
                    if (textPwd.getText().toString().trim().length() > 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sendPassword(textPwd.getText().toString());
                            }
                        }, 1000);
                    }
                    break;
                default:
                    finish();
                    break;
            }
        }
    }


    private void nextActivityOnSuccess() {
        if (application.getWidgetCalledPayments() && application.getLoggedInUser().getIsTransactional()) {
            application.setWidgetCalledPayments(false);
            final Intent i = new Intent(getApplicationContext(), Payments.class);
            i.putExtra("transfers", false);
            startActivity(i);
        } else if (application.getWidgetCalledTransfers() && application.getLoggedInUser().getIsTransactional()) {
            application.setWidgetCalledTransfers(false);
            final Intent i = new Intent(getApplicationContext(), Payments.class);
            i.putExtra("transfers", true);
            startActivity(i);
        } else if (application.getWalletCalledAuthentication()) {
            String authenticationJson = application.getWalletRequest();

            String walletType = "";
            if (application.getCallingWallet().equals(MiBancoConstants.SAMSUNGPAY_WALLET)) {
                walletType = getResources().getString(R.string.wallet_name_samsung);
            } else {//if (application.getCallingWallet().equals(MiBancoConstants.ANDROIDPAY_WALLET)) {
                walletType = getResources().getString(R.string.wallet_name_android);
            }

            application.getAsyncTasksManager().walletAuthentication(EnterPassword.this, authenticationJson, walletType, new ResponderListener() {
                @Override
                public void responder(String responderName, Object data) {
                    application.setWalletCalledAuthentication(false);

                    if (responderName != null && responderName.equalsIgnoreCase("SUCCESS")) {
                        final Intent i = new Intent(getApplicationContext(), WalletAuthenticationSuccessful.class);
                        startActivity(i);
                    } else if (responderName != null && responderName.equalsIgnoreCase("FINGERPRINT_DELAY_FAILED")) {
                        long minutesLeft = Long.parseLong((String) data);

                        final Intent i = new Intent(getApplicationContext(), WalletAuthenticationError.class);
                        i.putExtra(MiBancoConstants.ERROR_MESSAGE_KEY, responderName);
                        i.putExtra(MiBancoConstants.ERROR_MESSAGE_VALUE_KEY, minutesLeft);
                        startActivity(i);
                    } else {
                        final Intent i = new Intent(getApplicationContext(), WalletAuthenticationError.class);
                        i.putExtra(MiBancoConstants.ERROR_MESSAGE_KEY, (String) data);
                        startActivity(i);
                    }
                    finish();

                }

                @Override
                public void sessionHasExpired() {

                }
            });
            //return;
        } else {
            boolean minimumFingerprintRequirements = AutoLoginUtils.minimumFingerPrintDisplayReqs(context);
            boolean isAutoLogin = App.getApplicationInstance().isAutoLogin();
            User user = App.getApplicationInstance().getCurrentUser();
            if (minimumFingerprintRequirements) {
                if (user != null && user.isSavedUsername() && !isAutoLogin) {
                    Utils.savePasswordWithEncryption(context, user, textPwd.getText().toString());
                } else if (isAutoLogin) {
                    BPAnalytics.logEvent(BPAnalytics.EVENT_FP_LOGIN_SUCCESS);
                }
            }

            //Update FCM Token info if Push Notifications toggle is enabled
            if (user != null && Utils.isOnsenSupported() && PushUtils.isPushEnabled()) {
                updatePushTokenInfo();
            }

            ////EB-1726 Renew PermID
            if (FeatureFlags.EBILLS_PERMID()) {
                renewPermID();
            }

            final Intent i = new Intent(getApplicationContext(), Accounts.class);
            startActivity(i);
        }
    }

    private void sendPassword(String password) {
        application.getAsyncTasksManager().password(EnterPassword.this, password, new ResponderListener() {
            @Override
            public void responder(final String responderName, final Object data) {

                if (!responderName.equalsIgnoreCase(Integer.toString(MiBancoConstants.WRONG_PASSWORD))) {
                    if (responderName.equalsIgnoreCase("question")) {
                        if (data == null) {
                            BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_UNKNOWN_ERROR_PASSWORD);
                            application.reLogin(EnterPassword.this);
                        }
                        final Intent i = new Intent(getApplicationContext(), SecurityQuestion.class);
                        i.putExtra("question", (String) data);
                        startActivity(i);
                        finish();

                        return;
                    } else if (responderName.equalsIgnoreCase(MiBancoConstants.RSA_ENROLL)) {
                        final Intent loginRSAEnrollIntent = new Intent();
                        loginRSAEnrollIntent.setClass(EnterPassword.this, WebViewActivity.class);
                        loginRSAEnrollIntent.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.loginRsaEnroll_url)));
                        String[] urlBlacklist = getResources().getStringArray(R.array.web_view_url_blacklist);
                        for (int i = 0; i < urlBlacklist.length; ++i) {
                            urlBlacklist[i] = Utils.getAbsoluteUrl(urlBlacklist[i]);
                        }
                        loginRSAEnrollIntent.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, urlBlacklist);
                        loginRSAEnrollIntent.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
                        loginRSAEnrollIntent.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
                        startActivityForResult(loginRSAEnrollIntent, MiBancoConstants.RSA_ENROLL_REQUEST_CODE);
                    } else if (responderName.equalsIgnoreCase(MiBancoConstants.SSDS_FORCED_LOGIN)) {
                        final Intent loginSsdsEnrollIntent = new Intent();
                        loginSsdsEnrollIntent.setClass(EnterPassword.this, WebViewActivity.class);
                        loginSsdsEnrollIntent.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.loginSsdsForced_url)));
                        String[] urlBlacklist = getResources().getStringArray(R.array.web_view_url_blacklist);
                        for (int i = 0; i < urlBlacklist.length; ++i) {
                            urlBlacklist[i] = Utils.getAbsoluteUrl(urlBlacklist[i]);
                        }
                        loginSsdsEnrollIntent.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, urlBlacklist);
                        loginSsdsEnrollIntent.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
                        loginSsdsEnrollIntent.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
                        App.getApplicationInstance().setLoginSSDSForced(true);
                        startActivityForResult(loginSsdsEnrollIntent, MiBancoConstants.SSDS_FORCED_LOGIN_REQUEST_CODE);
                    } else if(application.getLoggedInUser().getInterruptionPage()) {
                        openInterruptionPage();
                    } else if(application.getLoggedInUser().getOutreach()) {
                        
                        final Intent outreachInterruption = new Intent();
                        outreachInterruption.setClass(EnterPassword.this, WebViewActivity.class);
                        outreachInterruption.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.outreach_url)) + App.getApplicationInstance().getLanguage());
                        String[] urlBlacklist = getResources().getStringArray(R.array.web_view_url_blacklist);
                        for (int i = 0; i < urlBlacklist.length; ++i) {
                            urlBlacklist[i] = Utils.getAbsoluteUrl(urlBlacklist[i]);
                        }
                        outreachInterruption.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, urlBlacklist);
                        outreachInterruption.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
                        outreachInterruption.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
                        outreachInterruption.putExtra(MiBancoConstants.WEB_VIEW_TOOLBAR_HIDE_KEY, true);
                        outreachInterruption.putExtra(MiBancoConstants.WEB_VIEW_BACKACTION_DISABLED_KEY, true);
                        outreachInterruption.putExtra(MiBancoConstants.WEB_VIEW_CLOSEACTION_KEY, true);
                        outreachInterruption.putExtra(MiBancoConstants.WEB_VIEW_URL_EXTERNAL_KEY, getResources().getStringArray(R.array.web_view_url_external));
                        startActivityForResult(outreachInterruption, MiBancoConstants.OUTREACH_REQUEST_CODE);
                    } else {
                        nextActivityOnSuccess();
                        if (!application.getWalletCalledAuthentication()) {
                            finish();
                        }
                    }

                } else {

                    //MBSFE-1735 Reset Password Field
                    if (FeatureFlags.MBSFE_1735()) {
                        textPwd.getText().clear();
                        textPwd.setTransformationMethod(new PasswordTransformationMethod());
                        passwordLayout.setPasswordVisibilityToggleEnabled(false);
                        passwordLayout.setPasswordVisibilityToggleEnabled(true);
                    }

                    boolean minimumFingerprintRequirements = AutoLoginUtils.minimumFingerPrintDisplayReqs(context);
                    boolean isAutoLogin = App.getApplicationInstance().isAutoLogin();
                    if (minimumFingerprintRequirements
                            && isAutoLogin && responderName.equalsIgnoreCase(Integer.toString(MiBancoConstants.WRONG_PASSWORD))) {
                        App.getApplicationInstance().setAutoLogin(false);
                        textPwd.setEnabled(true);
                    }
                }
            }

            @Override
            public void sessionHasExpired() {
                application.reLogin(EnterPassword.this);
            }
        });
    }

    private void renewPermID() {
        application.getAsyncTasksManager().ebills(this, new ResponderListener() {
            @Override
            public void responder(String responderName, Object data) {
            }

            @Override
            public void sessionHasExpired() {
            }
        });
    }

    private void updatePushTokenInfo() {

        final String username = application.getCurrentUser().getUsername();
        final String token = PushUtils.getPushToken(context);

        //User does not have permission and has no valid initial token on backend to update
        if (StringUtils.isEmpty(token) || (!isInitialPushTokenSaved(context, username)
                && !PushUtils.areNotificationsEnabled(context))) {
            return;
        }

        updateToken(username, token);


    }

    private void updateToken(final String username, String token) {
        PushTokenRequest request = new PushTokenRequest(context);
        request.setPushToken(token);
        request.setActive(PushUtils.isPushToggleChecked(context, username));
        String savedToken = PushUtils.getSavedPushToken(context);
        if (!StringUtils.equals(token, savedToken)) {
            request.setInvalidPushToken(savedToken);
            PushUtils.savePushToken(context, token);
        }

        application.getAsyncTasksManager().PushTask(this, request, true, new ResponderListener() {
            @Override
            public void responder(String responderName, Object data) {
                PushTokenResponse response = (PushTokenResponse) data;

                if (response != null) {

                    if (StringUtils.equals(PushTokenResponse.SUCCESS, response.getStatus())) {
                        PushUtils.setInitialPushTokenSaved(context, true, username);
                    } else if (StringUtils.equals(PushTokenResponse.DISABLE, response.getStatus())) {
                        PushUtils.setPushToggleChecked(context, false, username);
                    }
                }
            }

            @Override
            public void sessionHasExpired() {}
        });
    }

    protected void openInterruptionPage () {
        final Intent interruptionIntent = new Intent(EnterPassword.this, WebViewActivity.class);
        interruptionIntent.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.interruption_url)) + App.getApplicationInstance().getLanguage());
        String[] urlBlacklist = getResources().getStringArray(R.array.web_view_url_blacklist);
        for (int i = 0; i < urlBlacklist.length; ++i) {
            urlBlacklist[i] = Utils.getAbsoluteUrl(urlBlacklist[i]);
        }
        interruptionIntent.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, urlBlacklist);
        interruptionIntent.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
        interruptionIntent.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
        interruptionIntent.putExtra(MiBancoConstants.WEB_VIEW_TOOLBAR_HIDE_KEY, true);
        interruptionIntent.putExtra(MiBancoConstants.WEB_VIEW_BACKACTION_DISABLED_KEY, true);
        interruptionIntent.putExtra(MiBancoConstants.WEB_VIEW_CLOSEACTION_KEY, true);
        interruptionIntent.putExtra(MiBancoConstants.WEB_VIEW_URL_EXTERNAL_KEY, getResources().getStringArray(R.array.web_view_url_external));
        startActivityForResult(interruptionIntent, MiBancoConstants.INTERRUPTION_REQUEST_CODE);
    }
}
