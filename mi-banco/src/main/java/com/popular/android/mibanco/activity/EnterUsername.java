package com.popular.android.mibanco.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.FileProvider;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.IntroScreen;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.listener.SimpleListener;
import com.popular.android.mibanco.listener.StartListener;
import com.popular.android.mibanco.model.LoginGet;
import com.popular.android.mibanco.model.OobChallenge;
import com.popular.android.mibanco.model.User;
import com.popular.android.mibanco.util.AlertDialogParameters;
import com.popular.android.mibanco.util.AutoLoginUtils;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.CameraHelper;
import com.popular.android.mibanco.util.FingerprintModule;
import com.popular.android.mibanco.util.PermissionsManagerUtils;
import com.popular.android.mibanco.util.ShakeDetector;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.util.enums.RegainAccessTypeEnum;
import com.popular.android.mibanco.view.DialogHolo;
import com.splunk.mint.Mint;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Implements the first login screen where user can enter username and save it or select one from the list of saved username.
 */
@SuppressLint("NewApi")
public class EnterUsername extends BaseActivity implements StartListener,ResponderListener {

    private final static float WELCOME_IMAGE_HEIGHT_RATIO = 0.95f;
    private final static float WELCOME_IMAGE_WIDTH_RATIO = 0.95f;
    private final static int WELCOME_IMAGE_X_OFFSET = 2;
    private final static int WELCOME_IMAGE_Y_OFFSET = 4;
    private final static int MASK_LENGTH = MiBancoConstants.MASK_PATTERN_LENGTH;
    private final static int CUSTOM_IMAGE_HEIGHT = 500;
    private final static int CUSTOM_IMAGE_WIDTH = 1000;
    private final static int CUSTOM_IMAGE_ASPECT_X = 16;
    private final static int CUSTOM_IMAGE_ASPECT_Y = 8;
    private ObjectAnimator shakeAnim;
    private int animCounter;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    private String welcomeImageFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + File.separator
            + Utils.sha256(MiBancoConstants.WELCOME_IMAGE_FILENAME);


    private enum LoginView {
        EnterUsername,
        OtherUsername,
        SelectUsername
    }

    private LoginView loginView;
    private Button buttonLogin;
    private Button buttonSubmit;
    private TextView textViewPrivacyPolicyNewUsername;
    private TextView textViewPrivacyPolicyOtherUsername;
    private TextView textViewPrivacyPolicySavedUsername;
    private LinearLayout linearEnterUsername;
    private LinearLayout linearOtherUsername;
    private RelativeLayout linearWelcome;
    private RelativeLayout linearSelectUsername;

    private int savedUserNamesCount;
    private ListView savedUsersList;
    private SwitchCompat switchRememberUsername;
    private SwitchCompat switchRememberOtherUsername;
    private EditText textUser;
    private EditText textOtherUser;

    private String walletRequest;
    private ImageView welcomeImage;
    private ImageView customizeImageBtn;
    private ImageButton fingerprintButton;

    private boolean isOtherUsername;
    private boolean fingerprintFragmentShowing = false;
    private Context mContext = this;
    private Intent intent;

    private Uri mCurrentPhotoPath;
    final static int REQUEST_TAKE_PHOTO = 1;
    final static int RESULT_LOAD_IMG = 2;
    private File mCustomImage;

    private List<User> usersList;
    private boolean wasCustomImageBtnClicked;

    //region ACTIVITY LIFECYCLE METHODS *********************************

    @SuppressLint("NewApi")
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_username);
        App.getApplicationInstance().setLoginSSDSForced(false);

        if (application == null || application.getAsyncTasksManager() == null) {
            errorReload();
        } else {
            application.setSessionNeeded(true);
            application.setAutoLogin(false);
            if (getString(R.string.bugsense_active).equals("true")) {
                Mint.initAndStartSession(this, getString(R.string.bugsense_key));
            }

            boolean rememberUsernameActive = getIntent().getBooleanExtra(MiBancoConstants.REMEMBER_USERNAME_DEFAULT, false);
            textUser = (EditText) findViewById(R.id.editUsername);

            textUser.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public void onDestroyActionMode(ActionMode mode) {
                }

                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }
            });

            textOtherUser = (EditText) findViewById(R.id.editOtherUsername);
            textOtherUser.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public void onDestroyActionMode(ActionMode mode) {
                }

                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }
            });
//            //  **** OJO SOLO PARA SAMSUNG PAY
//            walletRequest = getIntent().getStringExtra(Intent.EXTRA_TEXT);
//            //  **** FIN SOLO PARA SAMSUNG PAY

            App.getApplicationInstance().setFingerprintSectionId(0);
            TextView textHeaderUsername = ((TextView) findViewById(R.id.textHeaderUsername));
            TextView textHeaderOtherUsername = ((TextView) findViewById(R.id.textHeaderOtherUsername));
            TextView textHeaderSelectUsername = ((TextView) findViewById(R.id.textHeaderSelectUsername));

            if (textHeaderUsername != null && textHeaderOtherUsername != null && textHeaderSelectUsername != null) {
                textHeaderUsername.setText(getString(R.string.username).toUpperCase());
                textHeaderOtherUsername.setText(getString(R.string.other_username_header).toUpperCase());
                textHeaderSelectUsername.setText(getString(R.string.select_username).toUpperCase());
            }
            welcomeImage = (ImageView) findViewById(R.id.welcomeImage);

            //show camera button
            if (FeatureFlags.CUSTOM_LOGIN_IMAGE()) {
                wasCustomImageBtnClicked = Utils.wasCustomImageBtnClicked(this);
                customizeImageBtn = (ImageView) findViewById(R.id.camera_welcome_btn);
                if (!wasCustomImageBtnClicked)
                    customizeImageBtn.setImageResource(R.drawable.camera_notification);
                customizeImageBtn.setOnClickListener(customImageBtnListener());
                customizeImageBtn.setVisibility(View.VISIBLE);
            }
            linearWelcome = (RelativeLayout) findViewById(R.id.linearWelcome);
            linearSelectUsername = (RelativeLayout) findViewById(R.id.linearSelectUser);
            linearEnterUsername = (LinearLayout) findViewById(R.id.linearEnterUsername);
            linearOtherUsername = (LinearLayout) findViewById(R.id.linearOtherUsername);

            buttonLogin = (Button) findViewById(R.id.btnLogin);
            buttonSubmit = (Button) findViewById(R.id.btnSubmit);

            textViewPrivacyPolicyNewUsername = TextView.class.cast(findViewById(R.id.privacy_policy_textview_new_username));
            textViewPrivacyPolicyOtherUsername = TextView.class.cast(findViewById(R.id.privacy_policy_textview_other_username));
            textViewPrivacyPolicySavedUsername = TextView.class.cast(findViewById(R.id.privacy_policy_textview_saved_username));

            savedUsersList = (ListView) findViewById(R.id.users_list);
            switchRememberUsername = (SwitchCompat) findViewById(R.id.switchRememberUsername);
            switchRememberOtherUsername = (SwitchCompat) findViewById(R.id.switchRememberOtherUsername);

            mCustomImage = new File(getFilesDir(), MiBancoConstants.LOGIN_IMAGE_FILENAME);

            buttonSubmit.setEnabled(false);
            buttonLogin.setEnabled(false);

            setListeners();

            if (FeatureFlags.CUSTOM_LOGIN_IMAGE() && mCustomImage.exists()) {
                displayCustomImage(Uri.fromFile(mCustomImage).toString());
            } else {
                if (mCustomImage.exists())
                    mCustomImage.delete(); //delete image if flag is false but user still has a custom image saved
                fetchDefaultImage();
            }

            /* Fix for deposit check in session */
            if (application.getDepositCheckInformationFromSession())
                application.setDepositCheckInformationFromSession(false);

            usersList = Utils.getUsernames(this);
            updateList(usersList);

            if (usersList.size() > 0) {
                setActiveView(LoginView.SelectUsername);
            } else {
                setActiveView(LoginView.EnterUsername);
                if (switchRememberUsername != null) {
                    switchRememberUsername.setChecked(rememberUsernameActive);
                }
            }

            fingerprintInit();

            if (MiBancoEnviromentConstants.TEST_ENV) {

                TextView textEviromentPort = ((TextView) findViewById(R.id.enviroment_port));
                textEviromentPort.setVisibility(View.VISIBLE);
                textEviromentPort.setText(Utils.getParsedPort());
            }
            if (MiBancoEnviromentConstants.TEST_ENV || MiBancoEnviromentConstants.QA_ENV) {

                shakeDetectorInit();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        App.getApplicationInstance().setCurrentUser(null); //reset current user
        usersList = Utils.getUsernames(this);
        updateList(usersList);

        if (fingerprintButton != null) {
            if ((AutoLoginUtils.osFingerprintRequirements(mContext, true)
                    && AutoLoginUtils.getFingerprintPreference(mContext)) && !fingerprintFragmentShowing) {
                fingerprintButton.setVisibility(View.VISIBLE);
            } else {
                fingerprintButton.setVisibility(View.INVISIBLE);
            }
        }

        if (MiBancoEnviromentConstants.TEST_ENV || MiBancoEnviromentConstants.QA_ENV) {
            // Add the following line to register the Session Manager Listener onResume
            mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

            TextView textEviromentPort = ((TextView) findViewById(R.id.enviroment_port));
            textEviromentPort.setText(Utils.getParsedPort());
        }

        if (savedUserNamesCount > 0) {
            setActiveView(LoginView.SelectUsername);
        } else {
            setActiveView(LoginView.EnterUsername);
        }
        if (FeatureFlags.CUSTOM_LOGIN_IMAGE() && !wasCustomImageBtnClicked && animCounter == 0)
            shakeIcon();

    }

    @Override
    public void onStart() {
        super.onStart();
        BPAnalytics.onStartSession(this);
    }

    @Override
    public void onStop() {
        if (mSensorManager != null) {
            // Add the following line to unregister the Sensor Manager onPause
            mSensorManager.unregisterListener(mShakeDetector);
        }

        super.onStop();
        //application.setWalletCalledAuthentication(false);
        BPAnalytics.onEndSession(this);
    }

    @Override
    public void onBackPressed() {
        if (isOtherUsername && savedUserNamesCount > 0) {
            setActiveView(LoginView.SelectUsername);
            return;
        }

        final Intent iIntro = new Intent(this, IntroScreen.class);
        iIntro.putExtra(MiBancoConstants.INTRO_REQUEST_KEY, true);
        iIntro.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(iIntro);
        finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);

        final MenuItem menuContact = menu.findItem(R.id.menu_contact);
        final MenuItem menuLocator = menu.findItem(R.id.menu_locator);

        menu.findItem(R.id.menu_logout).setVisible(false);
        menuContact.setVisible(true);
        menuLocator.setVisible(true);
        menuContact.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuLocator.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    //endregion

    //region SHAKEDETECTOR METHODS *******************


    /**
     * ShakeDetector initialization
     */
    private void shakeDetectorInit() {
        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                /*
                 * The following method, "handleShakeEvent(count):" is a stub //
                 * method you would use to setup whatever you want done once the
                 * device has been shook.
                 */
                handleShakeEvent(count);
            }
        });
    }

    private void handleShakeEvent(int count) {
        if (MiBancoEnviromentConstants.TEST_ENV) {
            // Add the following line to unregister the Sensor Manager onPause
            mSensorManager.unregisterListener(mShakeDetector);

            Intent intent = new Intent(mContext, DeveloperActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } else if (MiBancoEnviromentConstants.QA_ENV) {
            Toast.makeText(getApplicationContext(), Utils.getParsedPort(), Toast.LENGTH_LONG).show();
        }
    }

    //endregion

    //region FINGERPRINT METHODS *******************

    /**
     * Fingerprint initialization
     */
    private void fingerprintInit() {
        fingerprintButton = (ImageButton) findViewById(R.id.img_fingerprint_button);
        if (fingerprintButton != null) {

            fingerprintButton.setVisibility(View.INVISIBLE);
            fingerprintButton.setOnClickListener(fingerprintButtonOnClick);
            if (AutoLoginUtils.minimumFingerPrintDisplayReqs(this) &&
                    !(application.getWalletCalledAuthentication() && !AutoLoginUtils.osFingerprintRequirements(mContext, true))) {
                fingerprintButton.setVisibility(View.VISIBLE);
                boolean linearSelectUsernameVisible = linearSelectUsername.getVisibility() == View.VISIBLE;

                // Pop up if there is only one saved user
                if (linearSelectUsernameVisible && usersList.size() == 1) {
                    User currentUser = usersList.get(0);
                    //App.getApplicationInstance().setCurrentUser(currentUser); //TODO: Borrar?
                    if (!Utils.isBlankOrNull(currentUser.getEncryptedPassword())) {
                        fingerprintButton.setVisibility(View.INVISIBLE);
                        fingerprintFragmentShowing = true;
                        new FingerprintModule(this, getFragmentManager());
                    }
                } else if (application != null && application.getWalletCalledAuthentication()) {
                    if (linearSelectUsernameVisible && usersList.size() > 1) {
                        fingerprintButton.setVisibility(View.INVISIBLE);
                        fingerprintFragmentShowing = true;
                        new FingerprintModule(this, getFragmentManager());
                    } else {
                        Utils.showAlert(mContext, getString(R.string.wallet_authentication_error_no_user_saved), new SimpleListener() {
                            @Override
                            public void done() {
                                setResult(Activity.RESULT_CANCELED);
                                application.setWalletCalledAuthentication(false);
                                finishAfterTransition();
                            }
                        });
                    }
                }
            } else if (application != null && application.getWalletCalledAuthentication() && !AutoLoginUtils.osFingerprintRequirements(mContext, true)) {
                Utils.showAlert(mContext, getString(R.string.wallet_authentication_error_no_minimun_fingerprint_req), new SimpleListener() {
                    @Override
                    public void done() {
                        setResult(Activity.RESULT_CANCELED);
                        application.setWalletCalledAuthentication(false);
                        finishAfterTransition();
                    }
                });
            } else if (application != null && application.getWalletCalledAuthentication() && !AutoLoginUtils.getFingerprintPreference(mContext)) {
                Utils.showAlert(mContext, getString(R.string.wallet_authentication_error_no_fingerprint_in_app), new SimpleListener() {
                    @Override
                    public void done() {
                        setResult(Activity.RESULT_CANCELED);
                        application.setWalletCalledAuthentication(false);
                        finishAfterTransition();
                    }
                });
            }
        }
    }


    OnClickListener fingerprintButtonOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (linearSelectUsername.getVisibility() == View.VISIBLE) {
                if (userHasStoredPassword()) {
                    fingerprintFragmentShowing = true;
                    new FingerprintModule(mContext, getFragmentManager());
                } else {
                    AlertDialogParameters params = new AlertDialogParameters(mContext, R.string.fp_password_setup, null);
                    params.setPositiveButtonText(mContext.getResources().getString(R.string.ok).toUpperCase());
                    Utils.showAlertDialog(params);
                }
            } else {
                AlertDialogParameters params = new AlertDialogParameters(mContext, R.string.fp_mibanco_setup, null);
                params.setPositiveButtonText(mContext.getResources().getString(R.string.ok).toUpperCase());
                Utils.showAlertDialog(params);
            }
        }
    };

    public void onFingerprintAuthSuccess(boolean success) {
        if (success) {
            fingerprintButton.setVisibility(View.INVISIBLE);
            App.getApplicationInstance().setAutoLogin(true);
            if (usersList != null && usersList.size() == 1) {
                savedUsersList.performItemClick(
                        savedUsersList.getAdapter().getView(0, null, null), 0, 0);
            }

        }
    }

    public void onFingerprintAuthCanceled() {
        if (application != null && application.getWalletCalledAuthentication()) {
            Utils.showAlert(mContext, getString(R.string.wallet_authentication_error_fingerprint_canceled), new SimpleListener() {
                @Override
                public void done() {
                    setResult(Activity.RESULT_CANCELED);
                    application.setWalletCalledAuthentication(false);
                    finishAfterTransition();
                }
            });
        } else {
            fingerprintFragmentShowing = false;
            if (fingerprintButton != null) {
                fingerprintButton.setVisibility(View.VISIBLE);
            }
        }
    }

    //endregion

    //region VIEW HELPER METHODS *********************

    private void fetchWelcomeImageAsync(String imageUrl) {
        if (imageUrl == null || welcomeImage == null) {
            return;
        }

        final File imageFile = new File(welcomeImageFilePath);
        final Date currDate = Calendar.getInstance().getTime();
        if (!imageFile.exists() || imageFile.lastModified() < currDate.getTime() - MiBancoConstants.WEEK_MILLIS) {
            ImageLoader.getInstance().loadImage(imageUrl, App.getApplicationInstance().getDefaultOptionsNoDiskCache(), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    int welcomeImgWidth = loadedImage.getWidth();
                    int welcomeImgHeight = loadedImage.getHeight();
                    if (welcomeImgWidth > 0 && welcomeImgHeight > 0) {
                        Bitmap croppedWelcomeImageBitmap = Bitmap.createBitmap(loadedImage, WELCOME_IMAGE_X_OFFSET, WELCOME_IMAGE_Y_OFFSET, (int) (welcomeImgWidth * WELCOME_IMAGE_WIDTH_RATIO),
                                (int) (welcomeImgHeight * WELCOME_IMAGE_HEIGHT_RATIO));
                        welcomeImage.setImageBitmap(croppedWelcomeImageBitmap);
                        App.getApplicationInstance().getAsyncTasksManager().new SaveImageTask().execute(croppedWelcomeImageBitmap, welcomeImageFilePath);
                    }
                }
            });
        }
    }

    /**
     * Sets the active view.
     *
     * @param viewToSet the new active view
     */
    private void setActiveView(final LoginView viewToSet) {
        loginView = viewToSet;
        switch (viewToSet) {
            case EnterUsername:
                linearWelcome.setVisibility(View.VISIBLE);
                linearEnterUsername.setVisibility(View.VISIBLE);
                linearSelectUsername.setVisibility(View.GONE);
                linearOtherUsername.setVisibility(View.GONE);
                isOtherUsername = false;
                break;
            case SelectUsername:
                linearWelcome.setVisibility(View.VISIBLE);
                linearEnterUsername.setVisibility(View.GONE);
                linearSelectUsername.setVisibility(View.VISIBLE);
                linearOtherUsername.setVisibility(View.GONE);
                isOtherUsername = false;
                break;
            case OtherUsername:
                linearWelcome.setVisibility(View.GONE);
                linearEnterUsername.setVisibility(View.GONE);
                linearSelectUsername.setVisibility(View.GONE);
                linearOtherUsername.setVisibility(View.VISIBLE);
                isOtherUsername = true;
                break;
            default:
                break;
        }
    }

    /**
     * Updates list of saved usernames.
     *
     * @param usernames the saved usernames
     */
    private void updateList(final List<User> usernames) {
        savedUserNamesCount = usernames.size();
        final ArrayList<String> values = new ArrayList<>();

        for (User user : usernames) {
            values.add(FeatureFlags.MASK_USERNAME() ? Utils.maskUsername(user.getUsername(), MASK_LENGTH) : user.getUsername());
        }

        final String[] listValues = values.toArray(new String[values.size() + 1]);
        listValues[values.size()] = getString(R.string.other_username);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_saved_users, listValues);
        savedUsersList.setAdapter(adapter);

        savedUsersList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                if (position == adapter.getCount() - 1) {
                    if (application != null && application.getWalletCalledAuthentication()) {
                        Utils.showAlert(mContext, getString(R.string.wallet_authentication_error_no_user_saved), new SimpleListener() {
                            @Override
                            public void done() {
                                setResult(Activity.RESULT_CANCELED);
                                application.setWalletCalledAuthentication(false);
                                finishAfterTransition();
                            }
                        });
                    } else {
                        setActiveView(LoginView.OtherUsername);
                    }
                } else {
                    textUser.setText(FeatureFlags.MASK_USERNAME() ? usernames.get(position).getUsername() : parent.getItemAtPosition(position).toString());
                    BPAnalytics.logEvent(BPAnalytics.EVENT_LOGON_INITIATED_WITH_SAVED_USERNAME, "usernameCount", Integer.toString(adapter.getCount()));
                    performLogin();
                }
            }
        });
    }

    //endregion

    //region LOGIN ACTIONS *************************

    /**
     * Checks if StartTask has been completed and performs login.
     */
    private void performLogin() {
        savedUsersList.setEnabled(false);
        String username = isOtherUsername ? textOtherUser.getText().toString().toLowerCase() : textUser.getText().toString().toLowerCase();
        User currentUser = getUserFromUsername(username);

        if (currentUser == null) {
            currentUser = new User();
            currentUser.setUsername(username);
        }

        if (loginView == LoginView.SelectUsername
                || (isOtherUsername ? switchRememberOtherUsername.isChecked() : switchRememberUsername.isChecked())) {
            currentUser.setSavedUsername(true);
        }

        App.getApplicationInstance().setCurrentUser(currentUser);
        Utils.setPrefsString(MiBancoConstants.ENTERED_USERNAME_PREFS_KEY, username, getApplicationContext());

        application.getAsyncTasksManager().startApp(EnterUsername.this, this, true, false);
    }

    @Override
    public void savedData(LoginGet loginData) {
        if (!mCustomImage.exists())
            fetchWelcomeImageAsync(Utils.stripUrlQueryParameters(MiBancoConstants.BASE_URL + loginData.getBackgroundImgUrl()));
        login();
    }

    @Override
    public void responder(String responderName, Object question) {

        if (responderName != null && !responderName.equalsIgnoreCase("login")) {
            Class<?> cls = null;
            Context context = getApplicationContext();
            if (responderName.equalsIgnoreCase("question")) {
                cls = SecurityQuestion.class;
            } else if (responderName.equalsIgnoreCase(MiBancoConstants.OOB_ACTION_NAME)) {
                cls = OobEnterAuthCode.class;
            } else if (responderName.equalsIgnoreCase("password")) {
                cls = EnterPassword.class;
            } else if (responderName.equalsIgnoreCase(MiBancoConstants.SSDS_FORCED_LOGIN)) {
                cls = WebViewActivity.class;
                context = EnterUsername.this;
            }
            if (cls != null) {
                if (isOtherUsername ? switchRememberOtherUsername.isChecked() : switchRememberUsername.isChecked()) {
                    application.setSaveUsername(true);
                    application.setUsername(isOtherUsername ? textOtherUser.getText().toString() : textUser.getText().toString());
                } else
                    application.setSaveUsername(false);

                intent = new Intent(context, cls);
                if (cls.equals(SecurityQuestion.class)) {
                    intent.putExtra("question",String.valueOf(question));
                } else if (cls.equals(OobEnterAuthCode.class)) {
                    OobChallenge oobChallenge = (OobChallenge) question;
                    intent.putExtra(MiBancoConstants.OOB_DATA, oobChallenge);
                } else if (cls.equals(WebViewActivity.class)) {
                    intent.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.loginSsdsForced_url)));
                    System.out.println(getResources());
                    System.out.println(getResources().getStringArray(R.array.web_view_url_blacklist));
                    String[] urlBlacklist = getResources().getStringArray(R.array.web_view_url_blacklist);
                    for (int x = 0; x < urlBlacklist.length; ++x) {
                        urlBlacklist[x] = Utils.getAbsoluteUrl(urlBlacklist[x]);
                    }
                    intent.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, urlBlacklist);
                    intent.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
                    intent.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
                    startActivityForResult(intent, MiBancoConstants.SSDS_FORCED_LOGIN_REQUEST_CODE);
                }
//                        if(application.getWalletCalledAuthentication()){
//                            if(walletRequest != null) {
//                                i.putExtra(Intent.EXTRA_TEXT, walletRequest);
//                            }
//                        }
                if (!cls.equals(WebViewActivity.class)) {
                    startActivity(intent);
                    finish();
                }
            }
        }
        savedUsersList.setEnabled(true);
    }

    @Override
    public void sessionHasExpired() {
        application.reLogin(EnterUsername.this);
    }

    /**
     * Performs login.
     */
    private void login() {
        application.getAsyncTasksManager().login(EnterUsername.this, Utils.getPrefsString(MiBancoConstants.ENTERED_USERNAME_PREFS_KEY, getApplicationContext()), "", this);
    }

    //endregion

    private boolean userHasStoredPassword() {
        for (User user : usersList) {
            if (!Utils.isBlankOrNull(user.getEncryptedPassword()))
                return true;
        }
        return false;
    }

    /**
     * Sets listeners.
     */
    private void setListeners() {
        textUser.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    performLogin();
                    return true;
                }
                return false;
            }
        });

        textUser.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonLogin.setEnabled(s.length() > 0);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        textOtherUser.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonSubmit.setEnabled(s.length() > 0);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final OnClickListener buttonClickListener = new OnClickListener() {

            @Override
            public void onClick(final View v) {
                performLogin();
            }
        };

        buttonSubmit.setOnClickListener(buttonClickListener);
        buttonLogin.setOnClickListener(buttonClickListener);
        textViewPrivacyPolicyNewUsername.setMovementMethod(LinkMovementMethod.getInstance());
        textViewPrivacyPolicyOtherUsername.setMovementMethod(LinkMovementMethod.getInstance());
        textViewPrivacyPolicySavedUsername.setMovementMethod(LinkMovementMethod.getInstance());

        TextView textForgotUsername = (TextView) findViewById(R.id.textForgotUsername);
        if (textForgotUsername != null) {
            textForgotUsername.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    final Intent intentForgotUsername = new Intent(EnterUsername.this, WebViewActivity.class);
                    final String regainAccessUrl = Utils.getAbsoluteUrl(getString(R.string.regain_access_url, RegainAccessTypeEnum.USERNAME, App.getApplicationInstance().getLanguage()));
                    intentForgotUsername.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, regainAccessUrl);
                    String[] urlBlacklist = getResources().getStringArray(R.array.web_view_url_blacklist);
                    for (int i = 0; i < urlBlacklist.length; ++i) {
                        urlBlacklist[i] = Utils.getAbsoluteUrl(urlBlacklist[i]);
                    }
                    intentForgotUsername.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, urlBlacklist);
                    intentForgotUsername.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
                    startActivity(intentForgotUsername);
                }
            });
        }
    }


    private User getUserFromUsername(String username) {
        for (User user : usersList) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && (requestCode == REQUEST_TAKE_PHOTO || requestCode == RESULT_LOAD_IMG)) {

            Uri imageUri = (requestCode == RESULT_LOAD_IMG) ? data.getData() : mCurrentPhotoPath;
            if (imageUri != null) {
                UCrop.Options options = new UCrop.Options();
                options.setCompressionFormat(Bitmap.CompressFormat.PNG);
                options.setFreeStyleCropEnabled(false);
                options.setToolbarTitle(getString(R.string.edit_your_image));
                options.setToolbarColor(getResources().getColor(R.color.title_bar_background));
                options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                options.setAllowedGestures(UCropActivity.ALL, UCropActivity.ALL, UCropActivity.ALL);
                options.setHideBottomControls(true);
                UCrop.of(imageUri, Uri.fromFile(mCustomImage))
                        .withAspectRatio(CUSTOM_IMAGE_ASPECT_X, CUSTOM_IMAGE_ASPECT_Y)
                        .withMaxResultSize(CUSTOM_IMAGE_WIDTH, CUSTOM_IMAGE_HEIGHT)
                        .withOptions(options)
                        .start(this);
            }
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            final Uri resultUri = UCrop.getOutput(data);
            deleteTempImage();
            if (resultUri != null) {
                displayCustomImage(resultUri.toString());
                BPAnalytics.logEvent(BPAnalytics.EVENT_CUSTOM_LOGIN_IMAGE_SUCCESS);
            }
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_CANCELED) {
            deleteTempImage();
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
            deleteTempImage();
            final Throwable cropError = UCrop.getError(data);
            if (cropError != null)
                cropError.printStackTrace();
        } else if (resultCode == RESULT_CANCELED && requestCode == REQUEST_TAKE_PHOTO) {
            deleteTempImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MiBancoConstants.REQUEST_CODE_ASK_PERMISSIONS) {
            boolean permissionGranted = PermissionsManagerUtils.isFunctionalityAllowed(this, permissions, grantResults,
                    R.string.permission_custom_image_mandatory, Utils.openPermissionSettings(this));
            if (permissionGranted)
                dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = CameraHelper.createTempImageFile(this);
            Uri photoURI;
            //use fileProvider if Android Version > Nougat
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
            else
                photoURI = Uri.fromFile(photoFile);
            mCurrentPhotoPath = photoURI;
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

        } else //no app was found to open the camera
            Toast.makeText(this, getString(R.string.no_application_found), Toast.LENGTH_LONG).show();
    }

    private OnClickListener customImageBtnListener() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!wasCustomImageBtnClicked) {
                    Utils.setCustomImageBtnClicked(mContext, true);
                    wasCustomImageBtnClicked = true;
                    customizeImageBtn.setImageResource(R.drawable.camera_icon);
                    if (shakeAnim != null) {
                        shakeAnim.removeAllListeners();
                        shakeAnim.end();
                    }
                }
                final DialogHolo dialog = new DialogHolo(mContext, true);
                final View customView = dialog.setCustomContentView(R.layout.login_dialog_personalize);
                dialog.setTitle(getString(R.string.custom_image_personalize));
                dialog.setCancelable(true);

                final Button btnCancel = (Button) customView.findViewById(R.id.login_cancel);
                final Button btnCamera = (Button) customView.findViewById(R.id.login_button_take_photo);
                PackageManager packageManager = getPackageManager();
                if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) && !packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT))
                    btnCamera.setVisibility(View.GONE);

                final Button btnMyPhotos = (Button) customView.findViewById(R.id.login_button_my_photos);
                final Button btnUseDefault = (Button) customView.findViewById(R.id.login_use_default);
                final LinearLayout defaultSeparator = (LinearLayout) customView.findViewById(R.id.login_use_default_separator);
                if (mCustomImage.exists()) {
                    btnUseDefault.setVisibility(View.VISIBLE);
                    defaultSeparator.setVisibility(View.VISIBLE);
                    btnUseDefault.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.dismissDialog(dialog);
                            defaultSeparator.setVisibility(View.GONE);
                            fetchDefaultImage();
                            if (mCustomImage.delete())
                                Toast.makeText(mContext, getString(R.string.remove_image_toast), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                Utils.showDialog(dialog, mContext);
                //set Camera option button
                btnCamera.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.dismissDialog(dialog);
                        List<String> missingPermissions = PermissionsManagerUtils.missingPermissions(mContext);
                        if (missingPermissions.isEmpty())
                            dispatchTakePictureIntent();
                        else
                            PermissionsManagerUtils.askForPermission(mContext, missingPermissions, MiBancoConstants.REQUEST_CODE_ASK_PERMISSIONS);
                    }
                });
                //set Gallery button
                btnMyPhotos.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.dismissDialog(dialog);
                        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        photoPickerIntent.setType("image/*");
                        if (photoPickerIntent.resolveActivity(getPackageManager()) != null)
                            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
                        else
                            Toast.makeText(mContext, getString(R.string.no_application_found), Toast.LENGTH_LONG).show();
                    }
                });
                //set Cancel button
                btnCancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.dismissDialog(dialog);
                    }
                });
            }
        };
    }

    private void fetchDefaultImage() {
        final File imageFile = new File(welcomeImageFilePath);
        if (imageFile.exists()) {
            DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder().cloneFrom(App.getDefaultDisplayImageOptions())
                    .showImageOnFail(R.drawable.welcome_default).build();
            ImageLoader.getInstance().displayImage(Uri.fromFile(imageFile).toString(), welcomeImage, displayImageOptions);
        } else {
            welcomeImage.setImageResource(R.drawable.welcome_default);
        }
    }

    private void displayCustomImage(String uri) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.welcome_default).build();
        ImageLoader.getInstance().displayImage(uri, welcomeImage, options);
    }

    private void deleteTempImage() {
        CameraHelper.deleteTempImage(this, mCurrentPhotoPath);
        mCurrentPhotoPath = null;
    }


    private void shakeIcon() {
        shakeAnim = ObjectAnimator.ofFloat(customizeImageBtn, "rotation", 0f, 20f, 0f, -20f, 0f);
        shakeAnim.setRepeatCount(2); // repeat the loop a number of times
        shakeAnim.setDuration(425); // animation play time in ms
        shakeAnim.setStartDelay(700); //start after ms delay
        shakeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (animCounter == 0) {
                    shakeAnim.start();
                    animCounter++;
                }
            }
        });
        shakeAnim.start();
    }
}
