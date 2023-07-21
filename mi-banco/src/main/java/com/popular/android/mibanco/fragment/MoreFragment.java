package com.popular.android.mibanco.fragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.activity.Accounts;
import com.popular.android.mibanco.activity.Contact;
import com.popular.android.mibanco.activity.ErrorView;
import com.popular.android.mibanco.activity.LanguageSettings;
import com.popular.android.mibanco.activity.ManageUsers;
import com.popular.android.mibanco.activity.WebViewActivity;
import com.popular.android.mibanco.adapter.SettingsListAdapter;
import com.popular.android.mibanco.fragment.more.MoreInfoFragment;
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

public class MoreFragment extends Fragment {

    private Context mContext;

    private View viewRoot;

    private boolean isSessionActive;

    private boolean isOobEnrolled;

    private int psQuestionItem;

    private SettingsListAdapter adapterBack;

    private Accounts activity;

    private BottomNavigationView bottomNav;

    SettingsItem infoTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewRoot = inflater.inflate(R.layout.settings_list, container, false);
        mContext = getContext();

        activity = (Accounts) getActivity();
        bottomNav = activity.findViewById(R.id.bottom_navigation);

        App.getApplicationInstance().setActivityContext(mContext);

        User user = App.getApplicationInstance().getCurrentUser();
        isSessionActive = user != null;

        if (isSessionActive && App.getApplicationInstance().getLoggedInUser().getAthmSso() &&
                App.getApplicationInstance().getCustomerEntitlements() != null &&
                App.getApplicationInstance().getCustomerEntitlements().hasAthm()) {

            ATHMUtils.getCustomerATHMToken(mContext, false, new AthmTasks.AthmListener<AthmSSOInfo>() {
                @Override
                public void onAthmApiResponse(AthmSSOInfo result) {
                    createSettingsList(isSessionActive, result != null && result.isSsoBound());
                }
            });
        } else {
            createSettingsList(isSessionActive, false);
        }
        return viewRoot;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        BPAnalytics.onStartSession(getContext());
    }
    @Override
    public void onStop() {
        super.onStop();
        BPAnalytics.onEndSession(getContext());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(false);
        menu.findItem(R.id.menu_contact).setVisible(false);
    }

    private void createSettingsList(boolean isSessionActive, boolean isAthmSsoBound) {
        final ListView settingsList = (ListView) viewRoot.findViewById(R.id.list_settings);
        final App app = App.getApplicationInstance();
        if (settingsList != null) {

            final SettingsListAdapter adapter = new SettingsListAdapter(getContext(), isSessionActive);

            //primera sección
            infoTitle = new SettingsItem(0, getString(R.string.title_info), getString(R.string.title_info));
            infoTitle.setTitle(true, false);
            adapter.addItem(infoTitle);

            String urlDesktop = App.getApplicationInstance().getApiUrl();

            adapter.addItem(new SettingsItem(R.drawable.img_localizator,getString(R.string.locator),urlDesktop));
            adapter.addItem(new SettingsItem(R.drawable.img_phone_blue,getString(R.string.contactus), Contact.class));
            adapter.addItem(new SettingsItem(R.drawable.img_info_blue,getString(R.string.more_info),urlDesktop));
            adapter.addItem(new SettingsItem(R.drawable.img_page_blue,getString(R.string.request_documents),urlDesktop));
            //MBSFE-1712

            urlDesktop = urlDesktop + getString(R.string.go_to_desktop_url);
            adapter.addItem(new SettingsItem(R.drawable.img_pc_blue,getString(R.string.go_to_desktop_sidebar), urlDesktop));
            //END MBSFE-1712


            if (isSessionActive && Utils.isOnsenSupported() &&  (PushUtils.isPushEnabled() || FeatureFlags.RSA_ENROLLMENT())) {

                SettingsItem servicesTitle = new SettingsItem(0, getString(R.string.services), getString(R.string.services));
                servicesTitle.setTitle(true, true);
                adapter.addItem(servicesTitle);

                if (PushUtils.isPushEnabled()) {
                    SettingsItem pushSettings = new SettingsItem(R.drawable.img_alert_blue, getString(R.string.push_notifications), getString(R.string.push_notifications));
                    pushSettings.setPushSettings(true);
                    pushSettings.setSubItemTitle(getString(R.string.alerts_tab));
                    pushSettings.setAlertsOnClickListener(alertsOnClickListener());
                    adapter.addItem(pushSettings);
                }
                if (FeatureFlags.RSA_ENROLLMENT() && Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {

                    SettingsItem rsaItem = new SettingsItem(R.drawable.img_2factor_blue,getString(R.string.rsa_enroll_sidebar), WebViewActivity.class);
                    rsaItem.setWebViewOnClickListener(rsaOnClickListener());
                    rsaItem.setPushSettings(false);
                    rsaItem.setIsWebView(true);
                    adapter.addItem(rsaItem);

                    SettingsItem rsaQItem = new SettingsItem(R.drawable.img_card_blue,getString(R.string.rsa_edit_questions_sidebar), WebViewActivity.class);
                    rsaQItem.setWebViewOnClickListener(rsaEditQuestionsOnClickListener());
                    rsaQItem.setPushSettings(false);
                    rsaQItem.setIsWebView(true);
                    adapter.addItem(rsaQItem);

                    psQuestionItem = adapter.getCount() - 1;
                    showRsaQuestions(adapter, rsaQItem);


                }

            }



            if (PermissionsManagerUtils.missingPermissions(mContext).size() == 0) {

                SettingsItem settingsTitle = new SettingsItem(R.drawable.top_image,getString(R.string.title_settings), getString(R.string.title_settings));
                settingsTitle.setTitle(true, true);
                adapter.addItem(settingsTitle);

                //MBSD-4028 - add email menu
                if (isSessionActive && Utils.isOnsenSupported() && app.getLoggedInUser().getIsTransactional()) {
                    SettingsItem emailItem = new SettingsItem(R.drawable.img_email_blue,getString(R.string.email_change), WebViewActivity.class);
                    emailItem.setWebViewOnClickListener(emailChangeClickListener());
                    emailItem.setPushSettings(false);
                    emailItem.setIsWebView(true);
                    adapter.addItem(emailItem);
                }

                if (AutoLoginUtils.osFingerprintRequirements(mContext, false)) {
                    adapter.addItem(new SettingsItem(getString(R.string.settings_fingerprint_auth), true));
                }

                adapter.addItem(new SettingsItem(R.drawable.img_language_blue,getString(R.string.language), LanguageSettings.class));
                adapter.addItem(new SettingsItem(R.drawable.img_protect_blue,getString(R.string.manage_users), ManageUsers.class));


                if (isSessionActive && app.getLoggedInUser().isFlagGDPREnabled()) {
                    SettingsItem cookiesPreference = new SettingsItem(R.drawable.img_protect_blue,getString(R.string.cookies_preference_settings), WebViewActivity.class);
                    cookiesPreference.setWebViewOnClickListener(cookiesPreferenceClickListener());
                    cookiesPreference.setPushSettings(false);
                    cookiesPreference.setIsWebView(true);
                    adapter.addItem(cookiesPreference);
                }
            }



            if (isSessionActive) {
                if (isAthmSsoBound) {
                    infoTitle = new SettingsItem(0,getString(R.string.athm_sidebar), getString(R.string.athm_sidebar));
                    infoTitle.setTitle(true, true);
                    adapter.addItem(infoTitle);

                    SettingsItem athmLogout = new SettingsItem(R.drawable.img_logout_blue,getString(R.string.athm_settings_logout), getString(R.string.athm_settings_logout));
                    athmLogout.setIsAction(true);
                    adapter.addItem(athmLogout);
                }

                boolean userHasCashDrop = app.getCustomerEntitlements() != null
                        && app.getCustomerEntitlements().hasCashDrop() != null
                        && app.getCustomerEntitlements().hasCashDrop();

                if (userHasCashDrop && !Utils.isBlankOrNull(App.getApplicationInstance().getCustomerPhone(mContext))) {
                    infoTitle = new SettingsItem(R.drawable.top_image,getString(R.string.mobilecash_sidebar), getString(R.string.mobilecash_sidebar));
                    infoTitle.setTitle(true, true);
                    adapter.addItem(infoTitle);

                    SettingsItem retiroMovilUnbound = new SettingsItem(R.drawable.top_image,getString(R.string.easycash_unbound), getString(R.string.easycash_unbound));

                    String phoneNumber = App.getApplicationInstance().getCustomerPhone(mContext);
                    try {
                        phoneNumber = ContactsManagementUtils.formatPhoneNumber(mContext, phoneNumber, false);
                    } catch (Exception e) {
                        Log.e("SettingsList", e.toString());
                    }

                    retiroMovilUnbound.setDescription(phoneNumber);
                    adapter.addItem(retiroMovilUnbound);

                    SettingsItem retiroMovilFooter = new SettingsItem(R.drawable.top_image,getString(R.string.easycash_settings_unbound_message), getString(R.string.easycash_settings_unbound_message));
                    retiroMovilFooter.setFooter(true);
                    adapter.addItem(retiroMovilFooter);
                }
            }





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

                        } else if(item.getTitle().equals(getString(R.string.request_documents))) {
                            if(App.getApplicationInstance().getLoggedInUser() != null){
                                openRequestDocumentsWebView();
                            } else {
                                Utils.openExternalUrl(mContext, item.getUrl());
                            }
                        } else if(item.getTitle().equals(getString(R.string.more_info))) {
                            if(App.getApplicationInstance().getLoggedInUser() != null){
                                openMoreInfoFragment();
                            } else {
                                Utils.openExternalUrl(mContext, item.getUrl());
                            }
                        }else {
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
                        List<User> userList = Utils.getUsernames(mContext);
                        if (userList != null && userList.size() > 0) {
                            final Intent iUsers = new Intent(mContext, ManageUsers.class);
                            startActivity(iUsers);
                        } else {
                            Toast.makeText(mContext, getString(R.string.no_more_users), Toast.LENGTH_LONG).show();
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
                                BPAnalytics.logEvent(BPAnalytics.EVENT_ATHM_SSO_LOGGED_OUT);
                                final Intent accounts = new Intent(mContext, Accounts.class);
                                startActivity(accounts);
                            } else {
                                Toast.makeText(getContext(), getString(R.string.athm_settings_logout_error), Toast.LENGTH_LONG).show();
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
        final Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.mobile_alerts_url)));
        intent.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, Utils.urlBlacklist(mContext));
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
        final Intent intentWebView = new Intent(mContext, WebViewActivity.class);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.mobileSwitch)));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, Utils.urlBlacklist(getContext()));
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
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, Utils.urlBlacklist(mContext));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_TOOLBAR_HIDE_KEY, true);
        //BPAnalytics.logEvent(BPAnalytics.EVENT_RSA_ENROLL);
        startActivityForResult(intentWebView, MiBancoConstants.RSA_TO_ENROLL_REQUEST_CODE);
    }

    private void openRsaEditQuestionsWebView() {
        final Intent intentWebView = new Intent(mContext, WebViewActivity.class);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.rsa_edit_questions_url)));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, Utils.urlBlacklist(mContext));
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

    public void openRequestDocumentsWebView(){
        final Intent intentWebView = new Intent(mContext, WebViewActivity.class);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, getString(R.string.url_request_documents));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_BACKACTION_DISABLED_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_REQUEST_DOCUMENTS, true);
        startActivityForResult(intentWebView, 0);
    }

    public void openMoreInfoFragment(){
        activity.setSelectedFragment(new MoreInfoFragment());
        bottomNav.setSelectedItemId(R.id.nav_more_icon);
    }

    @Override
    public void onResume() {

        super.onResume();
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
                enableAlertsTab();
                PushUtils.checkIfPushIsEnabled(mContext,
                        (LinearLayout) viewRoot.findViewById(R.id.warningView),
                        (SwitchCompat) viewRoot.findViewById(R.id.push_switch),
                        (TextView) viewRoot.findViewById(R.id.settings_btn));
            }
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
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
                            SettingsItem rsaQItem = new SettingsItem(R.drawable.top_image,getString(R.string.rsa_edit_questions_sidebar), WebViewActivity.class);
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
                        SettingsItem rsaQItem = new SettingsItem(R.drawable.top_image,getString(R.string.rsa_edit_questions_sidebar), WebViewActivity.class);
                        rsaQItem.setWebViewOnClickListener(rsaEditQuestionsOnClickListener());
                        rsaQItem.setPushSettings(false);
                        rsaQItem.setIsWebView(true);

                        showRsaQuestions(adapterBack, rsaQItem);
                    }
                    break;
                case ErrorView.ERROR_ACCOUNT_BLOCKED:
                    App.getApplicationInstance().reLogin(getActivity());
                    getActivity().finish();
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
                        SettingsItem rsaQItem = new SettingsItem(R.drawable.top_image,getString(R.string.rsa_edit_questions_sidebar), WebViewActivity.class);
                        rsaQItem.setWebViewOnClickListener(rsaEditQuestionsOnClickListener());
                        rsaQItem.setPushSettings(false);
                        rsaQItem.setIsWebView(true);

                        showRsaQuestions(adapterBack, rsaQItem);

                    }
                    break;
                case ErrorView.ERROR_ACCOUNT_BLOCKED:
                    App.getApplicationInstance().reLogin(getActivity());
                    getActivity().finish();
                    break;
                case MiBancoConstants.RSA_EDIT_QUESTIONS_REQUEST_CODE:
                    break;
                default:
                    break;
            }

        } else if (requestCode == ErrorView.ERROR_ACCOUNT_BLOCKED) {
            App.getApplicationInstance().reLogin(getActivity());
            getActivity().finish();
        }

    }

    private void enableAlertsTab() {
        RelativeLayout alertsBtn = viewRoot.findViewById(R.id.alertsView);
        if (alertsBtn != null) {
            alertsBtn.setClickable(true);
        }
    }

    private void showRegainAccess() {
        BPAnalytics.logEvent(BPAnalytics.EVENT_RSA_SESSION_BLOCKED);
        final Intent intent = new Intent(getActivity(), ErrorView.class);
        intent.putExtra("errorCode", ErrorView.ERROR_ACCOUNT_BLOCKED);
        intent.putExtra("errorMessage", getString(R.string.account_blocked_title));
        getActivity().startActivityForResult(intent, ErrorView.ERROR_ACCOUNT_BLOCKED);
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