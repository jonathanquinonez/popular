package com.popular.android.mibanco;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.popular.android.mibanco.activity.CampaignActivity;
import com.popular.android.mibanco.activity.Contact;
import com.popular.android.mibanco.activity.EnterUsername;
import com.popular.android.mibanco.activity.SettingsList;
import com.popular.android.mibanco.activity.WebViewActivity;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.util.AutoLoginUtils;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.MobileCashUtils;
import com.popular.android.mibanco.util.PermissionsManagerUtils;
import com.popular.android.mibanco.util.Utils;

import java.util.List;

//import com.popular.android.mibanco.activity.LocatorTabs;

@SuppressLint("NewApi")
public class IntroScreen extends BaseActivity {

    private Context mContext = this;
    private boolean easycashRefresh = false;

    private final int PERMISSION_INFO_ACITIVITY = 1;
    private String PREFERENCE_PERMISSION_INFO = "permission_infp_preference";
    private List<String> missingPermissions;

    //region ACTIVITY LIFECYCLE METHODS ***

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }

        //enable TLS 1.2 on 4.4 or less
        enableTLSIfNeeded();

        App.getApplicationInstance().setCurrentUser(null); //reset current user
        application.setWalletCalledAuthentication(false);

        missingPermissions = PermissionsManagerUtils.missingPermissions(this);
        missingPermissions.remove(Manifest.permission.ACCESS_FINE_LOCATION);
        if (missingPermissions.size() == 0) {
            if (application != null) {
                application.initDeviceIdParams();
            }

            final String token = Utils.getStringContentFromShared(getApplicationContext(), MiBancoConstants.KEY_SHARED_LITE_ENROLLMENT_TOKEN);
            if (!Utils.isBlankOrNull(token)) {
                MobileCashUtils.nonCustomerViewDeciderAction(mContext);
                introScreenInitialization();
                easycashRefresh = true;
            } else {
                walletInitialization();
                if (!isRedirectToLogin()) {
                    introScreenInitialization();
                }

                if (application.isShowDesktopVersion()) {
                    introScreenInitialization();
                }
            }

        } else {
            introScreenInitialization();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        List<String> missingPermissions = PermissionsManagerUtils.missingPermissions(this);
        if (missingPermissions.size() == 0 && application != null && application.getDeviceId() == null) {
            application.initDeviceIdParams();
        }

        final String token = Utils.getStringContentFromShared(getApplicationContext(), MiBancoConstants.KEY_SHARED_LITE_ENROLLMENT_TOKEN);
        if (!Utils.isBlankOrNull(token) && !easycashRefresh) {
            MobileCashUtils.nonCustomerViewDeciderAction(mContext);
            introScreenInitialization();
        }
        easycashRefresh = false;
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

    //endregion

    //region INITIALIZATION METHODS ***

    private void walletInitialization() {

        String walletRequest = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        if (walletRequest != null && this.getReferrer() != null && application != null) {
            String callingWallet = this.getReferrer().getHost();

            if (callingWallet.equals("com.popular.android.mibanco")) {
                application.setCallingWallet(null);
                application.setWalletCalledAuthentication(false);
                application.setWalletRequest(null);
                return;
            }

            final byte[] walletRequestByte = Base64.decode(walletRequest, Base64.DEFAULT);
            final String walletRequestDecoded = new String(walletRequestByte);

            application.setCallingWallet(callingWallet);
            application.setWalletCalledAuthentication(true);
            application.setWalletRequest(walletRequestDecoded);

        }
    }

    private void introScreenInitialization() {

        setContentView(R.layout.intro_screen);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        Button btnInfo = (Button) findViewById(R.id.button_info);
        Button btnLogIn = (Button) findViewById(R.id.button_log_in);
        Button btnLocator = (Button) findViewById(R.id.button_locator);
        Button btnContactUs = (Button) findViewById(R.id.button_contactus);
        Button btnEnrollment = (Button) findViewById(R.id.button_enrollment);
        Button btnEasyCash = (Button) findViewById(R.id.btnEasyCash);

        TextView textViewPrivacyPolicy = TextView.class.cast(findViewById(R.id.privacy_policy_textview_intro));

        if (btnInfo != null && btnLogIn != null && btnLocator != null && btnContactUs != null && btnEnrollment != null && btnEasyCash != null) {
            btnInfo.setOnClickListener(infoOnClick);
            btnLogIn.setOnClickListener(loginOnClick);
            btnLocator.setOnClickListener(locatorOnClick);
            btnContactUs.setOnClickListener(contactUsOnClick);
            btnEnrollment.setOnClickListener(enrollmentOnClick);
            btnEasyCash.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobileCashUtils.nonCustomerViewDeciderAction(mContext);
                }
            });
            textViewPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        }

        ImageView backgroundImage = (ImageView) findViewById(R.id.image_background);
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder().cloneFrom(App.getDefaultDisplayImageOptions()).showImageOnFail(R.drawable.intro_bg).build();
        ImageLoader.getInstance().displayImage(getString(R.string.intro_image_url), backgroundImage, displayImageOptions);

        //The user requested to see the desktop version to use the recovery code,
        //but since the app logs out, it starts the activity while the app is in background
        //and bring it to front, that's why we need for this to start and then open the browser
        if (application.isShowDesktopVersion()) {
            application.setShowDesktopVersion(false);
            showDesktopVersion();
        }

        if (showPermissionInfo(this)) {
            final Intent intent = new Intent(mContext, PermissionInfoActivity.class);
            startActivityForResult(intent, PERMISSION_INFO_ACITIVITY);
        }
    }

    private boolean isFingerprintCampaignValidation() {
        if (!Utils.isFingerprintCampaignViewed(mContext)
                && AutoLoginUtils.osFingerprintRequirements(mContext, false)) {
            final Intent campaignIntent = new Intent(mContext, CampaignActivity.class);
            startActivity(campaignIntent);
            return true;
        }
        return false;
    }

    //endregion

    //region ACTION LISTENERS ***


    OnClickListener locatorOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Utils.openExternalUrl(mContext, getString(R.string.locator_url));
        }
    };

    OnClickListener contactUsOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final Intent iContact = new Intent(mContext, Contact.class);
            startActivity(iContact);
        }
    };

    OnClickListener infoOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final Intent iSettings = new Intent(mContext, SettingsList.class);
            startActivity(iSettings);
        }
    };

    OnClickListener loginOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            List<String> permission = PermissionsManagerUtils.missingPermissions(mContext);
            permission.remove(Manifest.permission.ACCESS_FINE_LOCATION);
            if (permission.size() == 0) {
                //Verify if fingerprint
                if (!isFingerprintCampaignValidation()) {
                    final Intent iLogin = new Intent(mContext, EnterUsername.class);
                    startActivity(iLogin);
                }
            } else {
                PermissionsManagerUtils.displayRequiredPermissionsDialog(mContext, R.string.permission_mandatory,
                        Utils.openPermissionSettings(IntroScreen.this));
            }
        }
    };

    OnClickListener enrollmentOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final Intent intentEnroll = new Intent(mContext, WebViewActivity.class);
            intentEnroll.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.enrollment_url)) + App.getApplicationInstance().getLanguage());
            String[] urlBlacklist = getResources().getStringArray(R.array.web_view_url_blacklist);
            for (int i = 0; i < urlBlacklist.length; ++i) {
                urlBlacklist[i] = Utils.getAbsoluteUrl(urlBlacklist[i]);
            }
            intentEnroll.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, urlBlacklist);
            intentEnroll.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
            startActivityForResult(intentEnroll, MiBancoConstants.ENROLLMENT_REQUEST_CODE);
            BPAnalytics.logEvent(BPAnalytics.EVENT_ENROLLMENT_INITIATED);
        }
    };

    //endregion

    //region ACTIVITY RESULT MANAGEMENT ***
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MiBancoConstants.REQUEST_CODE_ASK_PERMISSIONS:
                if (PermissionsManagerUtils.isFunctionalityAllowed(this, permissions, grantResults, R.string.permission_mandatory,
                        Utils.openPermissionSettings(this))) {
                    application.initDeviceIdParams();
                    walletInitialization();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MiBancoConstants.ENROLLMENT_REQUEST_CODE) {
                final Intent iLogin = new Intent(mContext, EnterUsername.class);
                iLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(iLogin);
                finish();
            } else if (requestCode == PERMISSION_INFO_ACITIVITY) {
                savePermissionInfo(this);
                PermissionsManagerUtils.askForPermission(this,
                        PermissionsManagerUtils.missingPermissions(this),
                        MiBancoConstants.REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
    }

    //endregion

    private boolean isRedirectToLogin() {
        if (!isFingerprintCampaignValidation()) {
            if (!getIntent().hasExtra(MiBancoConstants.INTRO_REQUEST_KEY) && Utils.getSuccessfulLoginsCount() > 0) {
                final Intent iLogin = new Intent(this, EnterUsername.class);
                startActivity(iLogin);
                finish();
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    private void enableTLSIfNeeded() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            try {
                ProviderInstaller.installIfNeeded(this);
            } catch (GooglePlayServicesRepairableException e) {
                GoogleApiAvailability.getInstance().showErrorNotification(IntroScreen.this, e.getConnectionStatusCode());
                Log.e("SecurityException", "Google Play Services need to be upgraded.");
            } catch (GooglePlayServicesNotAvailableException e) {
                Log.e("SecurityException", "Google Play Services is not available.");
            }
        }
    }

    private void showDesktopVersion() {
        this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.getAbsoluteUrl(mContext.getString(R.string.switch_desktop_url)))));
    }

    public void savePermissionInfo(final Context context) {
        final SharedPreferences.Editor editor = context.getSharedPreferences(MiBancoConstants.PREFS_KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(PREFERENCE_PERMISSION_INFO, false);
        editor.apply();
    }

    public boolean showPermissionInfo(final Context context) {
        final SharedPreferences preferences = context.getSharedPreferences(MiBancoConstants.PREFS_KEY, Context.MODE_PRIVATE);
        return preferences.getBoolean(PREFERENCE_PERMISSION_INFO, true);
    }

}
