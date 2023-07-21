package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.adapter.SettingsListAdapter;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.listener.AsyncTaskListener;
import com.popular.android.mibanco.model.User;
import com.popular.android.mibanco.object.SettingsItem;
import com.popular.android.mibanco.task.AthmTasks;
import com.popular.android.mibanco.util.ATHMUtils;
import com.popular.android.mibanco.util.AlertDialogParameters;
import com.popular.android.mibanco.util.AutoLoginUtils;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.ContactsManagementUtils;
import com.popular.android.mibanco.util.PermissionsManagerUtils;
import com.popular.android.mibanco.util.ProductType;
import com.popular.android.mibanco.util.PushUtils;
import com.popular.android.mibanco.util.RSAUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.ws.response.AthmSSOInfo;

import java.util.List;

/**
 * Settings list Activity.
 */
public class SettingsList extends BaseActivity {
    private Context mContext = this;
    private boolean isSessionActive;
    private boolean isOobEnrolled;
    private int psQuestionItem;
    private SettingsListAdapter adapterBack;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_list);

        App.getApplicationInstance().setActivityContext(mContext);

        User user = App.getApplicationInstance().getCurrentUser();
        isSessionActive = user != null;

        if (isSessionActive && application.getLoggedInUser().getAthmSso() &&
                application.getCustomerEntitlements() != null &&
                application.getCustomerEntitlements().hasAthm()) {

            ATHMUtils.getCustomerATHMToken(mContext, false, new AthmTasks.AthmListener<AthmSSOInfo>() {
                @Override
                public void onAthmApiResponse(AthmSSOInfo result) {
                    createSettingsList(isSessionActive, result != null && result.isSsoBound());
                }
            });
        } else {
            createSettingsList(isSessionActive, false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        BPAnalytics.onStartSession(this);
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

    private void createSettingsList(boolean isSessionActive, boolean isAthmSsoBound) {
        final ListView settingsList = (ListView) findViewById(R.id.list_settings);
        final App app = App.getApplicationInstance();//get the instance of one aplication
        if (settingsList != null) {

            final SettingsListAdapter adapter = new SettingsListAdapter(getApplicationContext(), isSessionActive);

            if (PermissionsManagerUtils.missingPermissions(mContext).size() == 0) {

                SettingsItem settingsTitle = new SettingsItem(getString(R.string.title_settings));
                settingsTitle.setTitle(true, true);
                adapter.addItem(settingsTitle);

                //MBSD-4028 - add email menu
                if (isSessionActive && Utils.isOnsenSupported() && app.getLoggedInUser().getIsTransactional()) {
                    SettingsItem emailItem = new SettingsItem(0,getString(R.string.email_change), WebViewActivity.class);
                    emailItem.setWebViewOnClickListener(emailChangeClickListener());
                    emailItem.setPushSettings(false);
                    emailItem.setIsWebView(true);
                    adapter.addItem(emailItem);
                }

                adapter.addItem(new SettingsItem(0,getString(R.string.manage_users), ManageUsers.class));
                adapter.addItem(new SettingsItem(0,getString(R.string.language), LanguageSettings.class));

                if (AutoLoginUtils.osFingerprintRequirements(mContext, false)) {
                    adapter.addItem(new SettingsItem(getString(R.string.settings_fingerprint_auth), true));
                }
                //Cookies preference settings
                if (isSessionActive && app.getLoggedInUser().isFlagGDPREnabled()) {
                    SettingsItem cookiesPreference = new SettingsItem(0,getString(R.string.cookies_preference_settings), WebViewActivity.class);
                    cookiesPreference.setWebViewOnClickListener(cookiesPreferenceClickListener());
                    cookiesPreference.setPushSettings(false);
                    cookiesPreference.setIsWebView(true);
                    adapter.addItem(cookiesPreference);
                }
            }

            //Show WebViews with Onsen Support
            if (isSessionActive && Utils.isOnsenSupported() &&
            (PushUtils.isPushEnabled() || FeatureFlags.RSA_ENROLLMENT())) {

                SettingsItem servicesTitle = new SettingsItem(getString(R.string.services));
                servicesTitle.setTitle(true, true);
                adapter.addItem(servicesTitle);

                //Alerts
                if (PushUtils.isPushEnabled()) {
                    SettingsItem pushSettings = new SettingsItem(getString(R.string.push_notifications));
                    pushSettings.setPushSettings(true);
                    pushSettings.setSubItemTitle(getString(R.string.alerts_tab));
                    pushSettings.setAlertsOnClickListener(alertsOnClickListener());
                    adapter.addItem(pushSettings);
                }

                //RSA Enroll
                if (FeatureFlags.RSA_ENROLLMENT() && Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    SettingsItem rsaItem = new SettingsItem(0,getString(R.string.rsa_enroll_sidebar), WebViewActivity.class);
                    SettingsItem rsaQItem = new SettingsItem(0,getString(R.string.rsa_edit_questions_sidebar), WebViewActivity.class);
                    rsaQItem.setWebViewOnClickListener(rsaEditQuestionsOnClickListener());
                    rsaQItem.setPushSettings(false);
                    rsaQItem.setIsWebView(true);

                    rsaItem.setWebViewOnClickListener(rsaOnClickListener());
                    rsaItem.setPushSettings(false);
                    rsaItem.setIsWebView(true);
                    adapter.addItem(rsaQItem);
                    psQuestionItem = adapter.getCount() - 1;
                    showRsaQuestions(adapter, rsaQItem);

                    adapter.addItem(rsaItem);
                }

            }

            if (isSessionActive) {
                if (isAthmSsoBound) {
                    SettingsItem infoTitle = new SettingsItem(getString(R.string.athm_sidebar));
                    infoTitle.setTitle(true, true);
                    adapter.addItem(infoTitle);

                    SettingsItem athmLogout = new SettingsItem(getString(R.string.athm_settings_logout));
                    athmLogout.setIsAction(true);
                    adapter.addItem(athmLogout);
                }

                boolean userHasCashDrop = app.getCustomerEntitlements() != null
                        && app.getCustomerEntitlements().hasCashDrop() != null
                        && app.getCustomerEntitlements().hasCashDrop();

                if (userHasCashDrop && !Utils.isBlankOrNull(App.getApplicationInstance().getCustomerPhone(mContext))) {
                    SettingsItem infoTitle = new SettingsItem(getString(R.string.mobilecash_sidebar));
                    infoTitle.setTitle(true, true);
                    adapter.addItem(infoTitle);

                    SettingsItem retiroMovilUnbound = new SettingsItem(getString(R.string.easycash_unbound));

                    String phoneNumber = App.getApplicationInstance().getCustomerPhone(mContext);
                    try {
                        phoneNumber = ContactsManagementUtils.formatPhoneNumber(mContext, phoneNumber, false);
                    } catch (Exception e) {
                        Log.e("SettingsList", e.toString());
                    }

                    retiroMovilUnbound.setDescription(phoneNumber);
                    adapter.addItem(retiroMovilUnbound);

                    SettingsItem retiroMovilFooter = new SettingsItem(getString(R.string.easycash_settings_unbound_message));
                    retiroMovilFooter.setFooter(true);
                    adapter.addItem(retiroMovilFooter);
                }
            }

            SettingsItem infoTitle = new SettingsItem(getString(R.string.title_info));
            infoTitle.setTitle(true, true);
            adapter.addItem(infoTitle);

            adapter.addItem(new SettingsItem(0,getString(R.string.about), About.class));
            adapter.addItem(new SettingsItem(0,getString(R.string.faq), Faq.class));
            adapter.addItem(new SettingsItem(0,getString(R.string.faq) + " " + getString(R.string.mobilecash_sidebar), EasyCashFaqs.class));
            adapter.addItem(new SettingsItem(0,getString(R.string.fraud_prevention), FraudPrevention.class));
            adapter.addItem(new SettingsItem(0,getString(R.string.policy), getString(R.string.privacy_policy_url)));
            adapter.addItem(new SettingsItem(0,getString(R.string.terms), getString(R.string.terms_and_conditions_url)));

            //MBSFE-1712
            String urlDesktop = App.getApplicationInstance().getApiUrl();
            urlDesktop = urlDesktop + getString(R.string.go_to_desktop_url);
            adapter.addItem(new SettingsItem(0,getString(R.string.go_to_desktop_sidebar), urlDesktop));
            //END MBSFE-1712

            settingsList.setAdapter(adapter);
            adapterBack = adapter;

            settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(final AdapterView<?> parent, final View v, final int position, final long id) {
                    final SettingsListAdapter adapter = (SettingsListAdapter) ((ListView) parent).getAdapter();
                    final SettingsItem item = (SettingsItem) adapter.getItem(position);
                    final Class<?> intentClass = item.getIntentClass();
                    if (item.getIntentClass() == null && item.getUrl() == null && !item.isAction() && !item.isDescription()) {
                        return;
                    }
                    if (item.getIntentClass() == null && item.getUrl() != null) {
                        if(item.getTitle().equals(getString(R.string.go_to_desktop_sidebar))){

                            if(App.getApplicationInstance().getLoggedInUser() != null){
                                desktopVersionWebView();
                            } else {
                                Utils.openExternalUrl(mContext, item.getUrl());
                            }

                        } else {
                            Utils.openExternalUrl(mContext, item.getUrl());
                        }

                    } else if (item.isAction()) {
                        AlertDialogParameters params = new AlertDialogParameters(mContext, R.string.athm_settings_logout_dialog_title, logoutATHMovil);

                        params.setPositiveButtonText(getResources().getString(R.string.yes));
                        params.setNegativeButtonText(getResources().getString(R.string.cancel));
                        Utils.showAlertDialog(params);

                    } else if (item.isDescription()) {
                        AlertDialogParameters params = new AlertDialogParameters(mContext, R.string.easycash_unbound_message, unboundUserOnClick);

                        if (!Utils.isBlankOrNull(item.getDescription())) {
                            try {
                                String title = String.format(getResources().getString(R.string.easycash_unbound_title), item.getDescription());
                                params.setTitle(title);
                            } catch (Exception e) {
                                Log.e("SettingsList", e.toString());
                            }
                        }

                        params.setPositiveButtonText(getResources().getString(R.string.yes).toUpperCase());
                        params.setNegativeButtonText(getResources().getString(R.string.no).toUpperCase());
                        Utils.showAlertDialog(params);

                    }  else if (intentClass.equals(ManageUsers.class)) {
                        List<User> userList = Utils.getUsernames(getApplicationContext());
                        if (userList != null && userList.size() > 0) {
                            final Intent iUsers = new Intent(mContext, ManageUsers.class);
                            startActivity(iUsers);
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.no_more_users), Toast.LENGTH_LONG).show();
                        }
                    } else if (item.isWebViewItem()) {
                        item.getWebViewOnClickListener().onClick(v);
                    } else {
                        final Intent iIntent = new Intent(mContext, intentClass);
                        startActivity(iIntent);
                    }
                }
            });
        }
    }

    DialogInterface.OnClickListener logoutATHMovil = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    AthmTasks.logoutAthmSso(mContext, new AthmTasks.AthmListener<AthmSSOInfo>() {
                        @Override
                        public void onAthmApiResponse(AthmSSOInfo result) {
                            if(result != null && result.isUnBoundSuccess()) {
                                // go to accounts
                                BPAnalytics.logEvent(BPAnalytics.EVENT_ATHM_SSO_LOGGED_OUT);
                                final Intent accounts = new Intent(mContext, Accounts.class);
                                startActivity(accounts);
                            } else {
                                // show error
                                Toast.makeText(getApplicationContext(), getString(R.string.athm_settings_logout_error), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
            dialog.dismiss();
        }
    };

    DialogInterface.OnClickListener unboundUserOnClick = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    AutoLoginUtils.registerDevice(mContext, ProductType.CASHDROP.toString(),false, true);
                    break;
                default:
                    break;
            }
            dialog.dismiss();
        }
    };

    private void openAlertsWebView() {

        final Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.mobile_alerts_url)));
        intent.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, Utils.urlBlacklist(this));
        intent.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
        intent.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
        intent.putExtra(MiBancoConstants.WEB_VIEW_TOOLBAR_HIDE_KEY, true);
        intent.putExtra(MiBancoConstants.WEB_VIEW_CLOSEACTION_KEY, true);
        intent.putExtra(MiBancoConstants.WEB_VIEW_ONSEN_ALERTS, true);
        intent.putExtra(MiBancoConstants.WEB_VIEW_BACKACTION_DISABLED_KEY, true);
        intent.putExtra(MiBancoConstants.WEB_VIEW_URL_EXTERNAL_KEY, getResources().getStringArray(R.array.web_view_url_external));
        BPAnalytics.logEvent(BPAnalytics.EVENT_ALERTS_SECTION);
        startActivityForResult(intent, MiBancoConstants.MODIFY_ALERTS_REQUEST_CODE);
    }

    private void desktopVersionWebView (){
        final Intent intentWebView = new Intent(this, WebViewActivity.class);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.mobileSwitch)));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, Utils.urlBlacklist(this));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
        intentWebView.putExtra(MiBancoConstants.DESKTOP_USER_AGENT, true);
        intentWebView.putExtra(MiBancoConstants.SHOW_BOTTOM_NAVIGATION_BAR, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_BACKACTION_DISABLED_KEY, true);
        startActivityForResult(intentWebView, MiBancoConstants.DESKTOP_WEBVIEW_REQUEST_CODE);
    }

    private void openRsaEnrollWebView() {
        final Intent intentWebView = new Intent(mContext, WebViewActivity.class);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.rsa_enroll_url)));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, Utils.urlBlacklist(this));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_TOOLBAR_HIDE_KEY, true);
        //BPAnalytics.logEvent(BPAnalytics.EVENT_RSA_ENROLL);
        startActivityForResult(intentWebView, MiBancoConstants.RSA_TO_ENROLL_REQUEST_CODE);
    }

    private void openRsaEditQuestionsWebView() {

        final Intent intentWebView = new Intent(mContext, WebViewActivity.class);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.rsa_edit_questions_url)));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, Utils.urlBlacklist(this));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_TOOLBAR_HIDE_KEY, true);
        //BPAnalytics.logEvent(BPAnalytics.EVENT_EDIT_RSA_QUESTIONS);
        startActivityForResult(intentWebView, MiBancoConstants.RSA_EDIT_QUESTIONS_REQUEST_CODE);
    }
   
      /* MBSE-2388 Cookies Privacy Setting
     */
    private void openCookiesPrivacySettingWebView() {

        final Intent intentWebView = new Intent(mContext, WebViewActivity.class);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.cookies_privacy_setting_url)));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, Utils.urlBlacklist(mContext));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_HIDE_RIGHT_MENU, true);
        startActivityForResult(intentWebView, MiBancoConstants.COOKIE_PREFERENCE_VIEW_CODE);
    }

     /* MBSD-4028
     */
    private void openEmailChangeWebView() {

        //MBSD-4028
        final Intent intentWebView = new Intent(mContext, WebViewActivity.class);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.email_change_url)));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, Utils.urlBlacklist(mContext));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_TOOLBAR_HIDE_KEY, true);
        startActivityForResult(intentWebView, MiBancoConstants.EMAIL_CHANGE_REQUEST_CODE);
    }

    @Override
    protected void onResume() {

        super.onResume();
        //Checks if device have push disabled in device settings
        checkIfPushIsEnabled();
    }

    private View.OnClickListener rsaOnClickListener() {

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRsaEnrollWebView();
            }
        };
    }

    /**
     * MSDD-4028
     *
     * @return
     */
    private View.OnClickListener emailChangeClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmailChangeWebView();
            }
        };
    }

    private View.OnClickListener rsaEditQuestionsOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRsaEditQuestionsWebView();
            }
        };
    }

    private View.OnClickListener alertsOnClickListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                RSAUtils.challengeRSAStatus(mContext, new AsyncTaskListener() {
                    @Override
                    public void onSuccess(Object data) {openAlertsWebView();}
                    @Override
                    public boolean onError(Throwable error) {
                        enableAlertsTab();
                        return true;
                    }
                    @Override
                    public void onCancelled() {enableAlertsTab();}
                });
            }
        };
    }

    private void checkIfPushIsEnabled() {
        if (isSessionActive && Utils.isOnsenSupported()) {
            if (PushUtils.isPushEnabled()) {
                //Verify if alert tab is disabled if exist
                enableAlertsTab();
                //check if the device have push disabled in settings
                PushUtils.checkIfPushIsEnabled(mContext,
                        (LinearLayout) findViewById(R.id.warningView),
                        (SwitchCompat) findViewById(R.id.push_switch),
                        (TextView) findViewById(R.id.settings_btn));
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MiBancoConstants.RSA_TO_ENROLL_REQUEST_CODE:
                    if (data != null && data.hasExtra(MiBancoConstants.RSA_BLOCKED)) {
                        Boolean rsaBlocked = data.getBooleanExtra(MiBancoConstants.RSA_BLOCKED, false);
                        if (rsaBlocked) {
                            showRegainAccess();
                        }
                    } else if (data != null && data.hasExtra(MiBancoConstants.RSA_OOB_ENROLLED)) {
                        boolean enrolled = data.getBooleanExtra(MiBancoConstants.RSA_OOB_ENROLLED, false);
                        if (!enrolled) {
                            SettingsItem rsaQItem = new SettingsItem(0,getString(R.string.rsa_edit_questions_sidebar), WebViewActivity.class);
                            rsaQItem.setWebViewOnClickListener(rsaEditQuestionsOnClickListener());
                            rsaQItem.setPushSettings(false);
                            rsaQItem.setIsWebView(true);
                            if (adapterBack.containsItemByTitle(rsaQItem.getTitle()) < 0) {
                                adapterBack.addItemAt(psQuestionItem, rsaQItem);
                                adapterBack.notifyDataSetChanged();
                                psQuestionItem = adapterBack.getCount() - 1;
                            }
                        } else {
                            adapterBack.removeItemAt(psQuestionItem);
                            adapterBack.notifyDataSetChanged();
                        }
                    } else {
                        SettingsItem rsaQItem = new SettingsItem(0,getString(R.string.rsa_edit_questions_sidebar), WebViewActivity.class);
                        rsaQItem.setWebViewOnClickListener(rsaEditQuestionsOnClickListener());
                        rsaQItem.setPushSettings(false);
                        rsaQItem.setIsWebView(true);

                        showRsaQuestions(adapterBack, rsaQItem);
                    }
                    break;
                case ErrorView.ERROR_ACCOUNT_BLOCKED:
                    application.reLogin(SettingsList.this);
                    finish();
                    break;
                case MiBancoConstants.RSA_EDIT_QUESTIONS_REQUEST_CODE:
                default:
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            switch (requestCode) {
                case MiBancoConstants.RSA_TO_ENROLL_REQUEST_CODE:
                    if (data != null && data.hasExtra(MiBancoConstants.RSA_BLOCKED)) {
                        Boolean rsaBlocked = data.getBooleanExtra(MiBancoConstants.RSA_BLOCKED, false);
                        if (rsaBlocked) {
                            showRegainAccess();
                        }
                    } else {
                        SettingsItem rsaQItem = new SettingsItem(0,getString(R.string.rsa_edit_questions_sidebar), WebViewActivity.class);
                        rsaQItem.setWebViewOnClickListener(rsaEditQuestionsOnClickListener());
                        rsaQItem.setPushSettings(false);
                        rsaQItem.setIsWebView(true);

                        showRsaQuestions(adapterBack, rsaQItem);

                    }
                    break;
                case ErrorView.ERROR_ACCOUNT_BLOCKED:
                    application.reLogin(SettingsList.this);
                    finish();
                    break;
                case MiBancoConstants.RSA_EDIT_QUESTIONS_REQUEST_CODE:
                    break;
                default:
                    break;
            }

        } else if (requestCode == ErrorView.ERROR_ACCOUNT_BLOCKED) {
            application.reLogin(SettingsList.this);
            finish();
        }

    }

    private void enableAlertsTab() {
        RelativeLayout alertsBtn = findViewById(R.id.alertsView);
        if (alertsBtn != null) {
            alertsBtn.setClickable(true);
        }
    }

    private void showRegainAccess() {
        BPAnalytics.logEvent(BPAnalytics.EVENT_RSA_SESSION_BLOCKED);
        final Intent intent = new Intent(SettingsList.this, ErrorView.class);
        intent.putExtra("errorCode", ErrorView.ERROR_ACCOUNT_BLOCKED);
        intent.putExtra("errorMessage", getString(R.string.account_blocked_title));
        SettingsList.this.startActivityForResult(intent, ErrorView.ERROR_ACCOUNT_BLOCKED);
    }

    private void showRsaQuestions(final SettingsListAdapter adapter, final SettingsItem itemQ) {
        RSAUtils.rsaCheckStatus(mContext, new AsyncTaskListener() {


            @Override
            public void onSuccess(Object data) {

                //MBSD-4028
                isOobEnrolled = data instanceof Boolean && (boolean) data;

                if (isOobEnrolled) {
                    int itemIndex = adapter.containsItemByTitle(itemQ.getTitle());
                    if (itemIndex > 0) {
                        adapter.removeItemAt(itemIndex);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    if (adapter.containsItemByTitle(itemQ.getTitle()) < 0) {
                        adapter.addItemAt(psQuestionItem, itemQ);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public boolean onError(Throwable error) {
                return false;
            }

            @Override
            public void onCancelled() {
                isOobEnrolled = false;
            }
        });
    }

    /**
     * MBSE-2388
     *
     * @return
     */
    private View.OnClickListener cookiesPreferenceClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCookiesPrivacySettingWebView();
            }
        };
    }
}
