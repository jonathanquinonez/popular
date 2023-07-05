package com.popular.android.mibanco;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import androidx.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryPerformance;
import com.newrelic.agent.android.NewRelic;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.popular.android.mibanco.activity.EasyCashHistoryReceipt;
import com.popular.android.mibanco.listener.SimpleListener;
import com.popular.android.mibanco.locator.LocationManager;
import com.popular.android.mibanco.model.AccountCard;
import com.popular.android.mibanco.model.AccountTransactions;
import com.popular.android.mibanco.model.CardDict;
import com.popular.android.mibanco.model.Customer;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.CustomerEntitlements;
import com.popular.android.mibanco.model.DepositCheckReceipt;
import com.popular.android.mibanco.model.EBills;
import com.popular.android.mibanco.model.EBillsItem;
import com.popular.android.mibanco.model.GlobalStatus;
import com.popular.android.mibanco.model.LoginGet;
import com.popular.android.mibanco.model.Payment;
import com.popular.android.mibanco.model.PremiaInfo;
import com.popular.android.mibanco.model.RDCCheckItem;
import com.popular.android.mibanco.model.RemoteDepositHistory;
import com.popular.android.mibanco.model.Transfer;
import com.popular.android.mibanco.model.User;
import com.popular.android.mibanco.object.BankLocation;
import com.popular.android.mibanco.object.BankLocationDetail;
import com.popular.android.mibanco.task.AsyncTasks;
import com.popular.android.mibanco.task.BaseAsyncTask;
import com.popular.android.mibanco.util.DeviceFingerprint;
import com.popular.android.mibanco.util.FontChanger;
import com.popular.android.mibanco.util.PermissionsManagerUtils;
import com.popular.android.mibanco.util.PushUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.DialogCoverup;
import com.popular.android.mibanco.ws.ApiClient;

import org.apache.commons.lang3.ArrayUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import io.fabric.sdk.android.Fabric;

/**
 * Singleton class that starts the app and stores singleton objects
 */
public class App extends MultiDexApplication {

    private static App applicationInstance;

    private DialogCoverup dialogCoverupValidation;
    private DialogCoverup dialogCoverupUpdateBalances;
    private String dateFormat;
//    private BankOverlayItem lastDisplayedGroup;
    private long lastUserInteractionTime = MiBancoConstants.NO_LAST_USER_INTERACTION_AVAILABLE;
//    private SparseArray<List<BankOverlayItem>> locatorClustersCache = new SparseArray<>();
    private Location userLocation;
    private BaseAsyncTask runningTask;
    private String uid = "";
    private HashMap<String, HashMap<String, CardDict>> accountsCards;
    private List<BankLocation> atms;
    private List<BankLocation> branches;
    private String deviceId;
    private EBills ebills;
    private List<EBillsItem> validEbills;
    private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private GlobalStatus globalStatus;
    private LoginGet loginGet;
    private Customer loggedInUser;
    private PremiaInfo premiaInfo; // Premia Initiative related Info.
    private Payment paymentsInfo;
    private HashMap<String, SparseArray<SparseArray<AccountTransactions>>> transactions;
    private Transfer transfersInfo;
    private boolean globalRdcEntitlementEnabled = true;
    private boolean rdcClientEnrolled = false;
    private boolean rdcClientAcceptedTerms = false;
    private boolean globalPaymentsEntitlementEnabled = true;
    private boolean globalTransfersEntitlementEnabled = true;
    private boolean globalAthmEntitlementEnabled = true;
    private boolean globalMobileCashEntitlementEnabled = false;
    private boolean globalCashdropEntitlementEnabled = false;
    private boolean globalCashdropEntitlementExists = false;
    private RemoteDepositHistory remoteDepositHistory;
    private RDCCheckItem reviewCheck;
    private boolean depositCheckInformationFromSession = false;
    private int depositCheckAmount = 0;
    private int depositCheckCurrentState = 0;
    private CustomerAccount depositCheckSelectedAccount;
    private int depositCheckFlashStatus = 0;
    private byte[] depositCheckFrontImage;
    private byte[] depositCheckBackImage;
    private DepositCheckReceipt depositCheckReceipt;
    private ApiClient apiClient;
    private AsyncTasks asyncTasks;
    private boolean updatingBalances;
    private boolean portalDelayInEffect;
    private boolean refreshPaymentsCardImages;
    private boolean refreshTransfersCardImages;
    private boolean reloadPayments;
    private boolean reloadTransfers;
    private String apiUrl;
    private CustomerEntitlements customerEntitlements;
    private Bundle ebillRequest;
    private DisplayImageOptions defaultOptions;
    private DisplayImageOptions defaultOptionsNoDiskCache;
    private LocationManager locationManager;
    private HashMap<String, CustomerAccount> customerAccountsMap = new HashMap<String, CustomerAccount>();
    private File imageCachePath;
    private String username;
    private User currentUser;
    private Boolean walletCalledAthentication = false;
    private Boolean widgetCalledPayments = false;
    private Boolean widgetCalledTransfers = false;
    private String customerToken;
    private String widgetDeviceId;
    private boolean saveUsername = false;
    private BankLocationDetail bankLocationDetail;
    private List<AccountCard> listAccountsSelect = null;
    private String callingWallet = null;
    private String walletRequest = null;
    private boolean isAutoLogin = false;
    private boolean isSessionNeeded = false;
    private int fingerprintSectionId = -1; // To be fixed (TODO: USE ENUM)
    private EasyCashHistoryReceipt.Callback callback;
    private String customerPhone;
    private boolean updateSidebarMenuOnResume = false;
    private boolean updateEasyCashHistory = false;
    private boolean appParamsInit = false;
    private Context activityContext;
    private Date lastNonCustEasyCashUpdate;
    private Date lastNonCustGlobalStatusUpdate;
    private boolean isPushWarningShowing;
    private boolean isLoginSSDSForced = false;

    public boolean isShowDesktopVersion() {
        return showDesktopVersion;
    }

    public void setShowDesktopVersion(boolean showDesktopVersion) {
        this.showDesktopVersion = showDesktopVersion;
    }

    private boolean showDesktopVersion = false;


    public enum CallingWallets {
        SAMSUNGPAY_WALLET("com.samsung.android.spay.ui.cardreg.RegistrationActivity"),
        ANDROIDPAY_WALLET("com.android.pay.RegistrationActivity");

        private final String text;

        CallingWallets(final String text){
            this.text = text;
        }

        @Override
        public String toString(){
            return text;
        }
    }

    public static App getApplicationInstance() {
        return applicationInstance;
    }

    public App() {
        FontChanger.setApplication(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        applicationInstance = this;
        final Configuration config = getBaseContext().getResources().getConfiguration();
        final String lang = Utils.getSecuredSharedPreferences(this).getString("language", MiBancoConstants.ENGLISH_LANGUAGE_CODE);
        if (!config.locale.getLanguage().equals(lang)) {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;

            getBaseContext().getResources().updateConfiguration(config, this.getResources().getDisplayMetrics());
        }

        setUniversalImageLoader();
        //Create notification channel if it does not exist for Android O
        PushUtils.createNotificationChannel(getApplicationContext());

        // New Relic
        if (getString(R.string.new_relic_active).equals("true")) {
            NewRelic.withApplicationToken(getString(R.string.new_relic_application_token)).start(this.getBaseContext());
        }

        initFlurryAgent(this);
        FlurryAgent.setReportLocation(true);

        //apiUrl = getString(getResources().getIdentifier(getString(R.string.api_url), "string", getPackageName()));
        apiUrl = MiBancoEnviromentConstants.API_URL;
    }

    public boolean isUpdateSidebarMenuOnResume() {
        return updateSidebarMenuOnResume;
    }

    public void setUpdateSidebarMenuOnResume(boolean updateSidebarMenuOnResume) {
        this.updateSidebarMenuOnResume = updateSidebarMenuOnResume;
    }

    public void initDeviceIdParams()
    {
        List<String> neededPermission = new LinkedList<>();
        if (android.os.Build.VERSION.SDK_INT  <= Build.VERSION_CODES.Q) {
            neededPermission.add("android.permission.READ_PHONE_STATE");
        } else {
            neededPermission.add("android.permission.READ_PHONE_NUMBERS");
        }
        List<String> missingPermissions = PermissionsManagerUtils.missingPermissions(getApplicationContext(),neededPermission);
        if(missingPermissions.size() == 0 && deviceId == null) {
            deviceId = getDeviceId();
            apiClient = new ApiClient(apiUrl, deviceId, getLanguage(), getApplicationContext());
            asyncTasks = new AsyncTasks(this);
        }
        appParamsInit = true;

    }

    public Context getActivityContext() {
        return activityContext;
    }

    public void setActivityContext(Context activityContext) {
        this.activityContext = activityContext;
    }

    public boolean isAppParamsInit()
    {
        return appParamsInit;
    }

    public int getFingerprintSectionId() {
        return fingerprintSectionId;
    }

    public void setFingerprintSectionId(int fingerprintSectionId) {
        this.fingerprintSectionId = fingerprintSectionId;
    }

    public String getDeviceId(){
        if(Utils.isBlankOrNull(deviceId)){
            deviceId = new DeviceFingerprint(this).getDeviceId();
        }
        return deviceId;
    }

    public static void submitException(final Exception e) {
        e.printStackTrace();
    }

    private void setUniversalImageLoader() {
        configureUniversalImageLoader();
        defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new FadeInBitmapDisplayer(MiBancoConstants.IMAGE_FADE_MILLIS)).build();
        defaultOptionsNoDiskCache = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(false).imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new FadeInBitmapDisplayer(MiBancoConstants.IMAGE_FADE_MILLIS)).build();
    }

    private void configureUniversalImageLoader() {
        imageCachePath = getImageCachePath();
        final ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(getApplicationContext()).threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 1)
                .denyCacheImageMultipleSizesInMemory()
                .imageDownloader(new BaseImageDownloader(applicationInstance.getApplicationContext(), MiBancoConstants.IMAGE_CONNECT_TIMEOUT_MILLIS, MiBancoConstants.IMAGE_READ_TIMEOUT_MILLIS))
                .tasksProcessingOrder(QueueProcessingType.FIFO).defaultDisplayImageOptions(DisplayImageOptions.createSimple());
        if (imageCachePath != null) {
            config.discCache(new LimitedAgeDiscCache(imageCachePath, new FileNameGenerator() {

                @Override
                public String generate(final String imageUri) {
                    return Utils.sha256(imageUri);
                }
            }, MiBancoConstants.IMAGE_AGE_WEEK_SECONDS));
        }

        if (ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().destroy();
        }
        ImageLoader.getInstance().init(config.build());
    }

    public File getImageCachePath() {
        return getExternalCacheDir() == null ? getCacheDir() : getExternalCacheDir();
    }

    public static DisplayImageOptions getDefaultDisplayImageOptions() {
        if (applicationInstance.getImageCachePath() == null) {
            return applicationInstance.getDefaultOptionsNoDiskCache();
        } else {
            if (applicationInstance.imageCachePath == null) {
                applicationInstance.configureUniversalImageLoader();
            }
            return applicationInstance.defaultOptions;
        }
    }

    public DisplayImageOptions getDefaultOptionsNoDiskCache() {
        return defaultOptionsNoDiskCache;
    }

    public boolean isPortalDelayInEffect() {
        return portalDelayInEffect;
    }

    public void setPortalDelayInEffect(boolean portalDelayInEffect) {
        this.portalDelayInEffect = portalDelayInEffect;
    }

    public DialogCoverup getDialogCoverupUpdateBalances() {
        return dialogCoverupUpdateBalances;
    }

    public void setDialogCoverupUpdateBalances(DialogCoverup dialogCoverupUpdateBalances) {
        this.dialogCoverupUpdateBalances = dialogCoverupUpdateBalances;
    }

    public boolean isUpdatingBalances() {
        return updatingBalances;
    }

    public void setUpdatingBalances(boolean updatingBalances) {
        this.updatingBalances = updatingBalances;
    }

    public AsyncTasks getAsyncTasksManager() {
        return asyncTasks;
    }

    public HashMap<String, HashMap<String, CardDict>> getAccountsCards() {
        return accountsCards;
    }

    public void setAccountsCards(HashMap<String, HashMap<String, CardDict>> accountsCards) {
        this.accountsCards = accountsCards;
    }

    public List<BankLocation> getAtms() {
        return atms;
    }

    public void setAtms(List<BankLocation> atms) {
        this.atms = atms;
    }

    public List<BankLocation> getBranches() {
        return branches;
    }

    public void setBranches(List<BankLocation> branches) {
        this.branches = branches;
    }

    public void setBankLocationDetail(BankLocationDetail bankLocationDetail){this.bankLocationDetail = bankLocationDetail;}
    public BankLocationDetail getBankLocationDetail(){return bankLocationDetail;}

    public void setLoginGet(LoginGet loginGet) {
        this.loginGet = loginGet;
    }

    public Customer getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(Customer loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    /**
     * PremiaInfo getter.
     * @return PremiaInfo
     */
    public PremiaInfo getPremiaInfo() {
        return premiaInfo;
    }

    /**
     * PremiaInfo setter.
     * @param premiaInfo
     */
    public void setPremiaInfo(PremiaInfo premiaInfo) {
        this.premiaInfo = premiaInfo;
    }

    public void setPaymentsInfo(Payment paymentsInfo) {
        this.paymentsInfo = paymentsInfo;
    }

    public void setTransfersInfo(Transfer transfersInfo) {
        this.transfersInfo = transfersInfo;
    }

    public void setRemoteDepositHistory(RemoteDepositHistory remoteDepositHistory) {
        this.remoteDepositHistory = remoteDepositHistory;
    }

    public void setReviewCheck(RDCCheckItem reviewCheck) {
        this.reviewCheck = reviewCheck;
    }

    public void setDepositCheckReceipt(DepositCheckReceipt depositCheckReceipt) {
        this.depositCheckReceipt = depositCheckReceipt;
    }

    public HashMap<String, SparseArray<SparseArray<AccountTransactions>>> getTransactions() {
        return transactions;
    }

    public void setTransactions(HashMap<String, SparseArray<SparseArray<AccountTransactions>>> transactions) {
        this.transactions = transactions;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void setEbills(EBills ebills) {
        this.ebills = ebills;
    }

    public void setGlobalStatus(GlobalStatus globalStatus) {
        this.globalStatus = globalStatus;
        if (globalStatus != null) {
            globalRdcEntitlementEnabled = globalStatus.isRdc();
            globalPaymentsEntitlementEnabled = globalStatus.isBillpay();
            globalTransfersEntitlementEnabled = globalStatus.isTransfers();
            globalAthmEntitlementEnabled = globalStatus.isAthm();
            globalMobileCashEntitlementEnabled = globalStatus.isMobileCash();
            globalCashdropEntitlementEnabled = globalStatus.isCashDrop();
            globalCashdropEntitlementExists = globalStatus.isCashDropExists();
        }
    }

    public boolean getGlobalRdcEntitlementEnabled() {
        return globalRdcEntitlementEnabled;
    }

    public void setRdcClientEnrolled(boolean rdcClientEnrolled) {
        this.rdcClientEnrolled = rdcClientEnrolled;
    }

    public boolean getRdcClientEnrolled() {
        return rdcClientEnrolled;
    }

    public void setRdcClientAcceptedTerms(boolean rdcClientAcceptedTerms) {
        this.rdcClientAcceptedTerms = rdcClientAcceptedTerms;
    }

    public boolean getRdcClientAcceptedTerms() {
        return rdcClientAcceptedTerms;
    }

    public boolean getGlobalPaymentsEntitlementEnabled() {
        return globalPaymentsEntitlementEnabled;
    }

    public boolean getGlobalTransfersEntitlementEnabled() {
        return globalTransfersEntitlementEnabled;
    }

    public Transfer getTransfersInfo() {
        return transfersInfo;
    }

    public RemoteDepositHistory getRemoteDepositHistory() {
        return remoteDepositHistory;
    }

    public RDCCheckItem getReviewCheck() {
        return reviewCheck;
    }

    public DepositCheckReceipt getDepositCheckReceipt() {
        return depositCheckReceipt;
    }

    /**
     * Gets the date format.
     *
     * @return the date format
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Gets the integer amount value for a given amount String.
     *
     * @param amount the amount String
     * @return the integer amount for a String content
     */
    public static int getIntAmount(final String amount) {
        String tempAmount;
        tempAmount = amount.trim();
        tempAmount = tempAmount.replaceAll("[^\\d.,]", "");
        tempAmount = tempAmount.replace(",", "");

        if (TextUtils.isEmpty(tempAmount)) {
            return 0;
        }

        int dotIndex = tempAmount.indexOf('.');
        if (dotIndex == -1) {
            return Integer.parseInt(tempAmount) * 100;
        }

        String integer = tempAmount.substring(0, dotIndex);
        String fraction = tempAmount.substring(dotIndex + 1, tempAmount.length());
        int sum = 0;
        if (!TextUtils.isEmpty(integer)) {
            sum += Integer.parseInt(integer) * 100;
        }
        if (!TextUtils.isEmpty(fraction)) {
            if (fraction.length() == 1) {
                sum += Integer.parseInt(fraction) * 10;
            } else if (fraction.length() == 2) {
                sum += Integer.parseInt(fraction);
            } else {
                sum += Integer.parseInt(fraction.substring(0, 2));
            }
        }

        return sum;
    }

    /**
     * Removes any extra characters from the balance String.
     *
     * @param balance the String containing a balance
     * @return a balance String containing only digits, dots, commas and a currency sign
     */
    public static String cleanBalanceString(String balance) {
        return balance.replaceAll("[^\\d.,$]", "");
    }

    /**
     * Gets the application language.
     *
     * @return the current application language code
     */
    public String getLanguage() {
        final SharedPreferences savedSession = Utils.getSecuredSharedPreferences(this);
        return savedSession.getString("language", null);
    }

//    public BankOverlayItem getLastDisplayedGroup() {
//        return lastDisplayedGroup;
//    }

    /**
     * Gets the running task.
     *
     * @return the running task
     */
    public BaseAsyncTask getLastLaunchedTask() {
        return runningTask;
    }

    /**
     * Gets the last session check time.
     *
     * @return the last session check time
     */
    public long getLastUserInteractionTime() {
        return lastUserInteractionTime;
    }

//    public SparseArray<List<BankOverlayItem>> getLocatorClustersCache() {
//        return locatorClustersCache;
//    }

    public Location getUserLocation() {
        if (userLocation != null && System.currentTimeMillis() - userLocation.getTime() > MiBancoConstants.LOCATION_ERASE_TIMEOUT) {
            userLocation = null;
        }
        return userLocation;
    }

    public void setUserLocation(final Location newUserLocation) {
        if (newUserLocation == null) {
            return;
        }
        this.userLocation = newUserLocation;
        this.userLocation.setTime(System.currentTimeMillis());
    }

    public boolean isRefreshPaymentsCardImages() {
        return refreshPaymentsCardImages;
    }

    public boolean isRefreshTransfersCardImages() {
        return refreshTransfersCardImages;
    }

    /**
     * Sets the date format.
     *
     * @param dateFormat the new date format
     */
    public void setDateFormat(final String dateFormat) {
        this.dateFormat = dateFormat;
    }

    // HELPERS

    /**
     * Sets the language.
     *
     * @param language the new language
     * @return the language code set
     */
    public String setLanguage(final String language, final Context activityContext) {
        // we support only English and Spanish at the moment, default is Spanish
        // since more users is going to use it
        String languageCode = language;
        if (!languageCode.equals(MiBancoConstants.ENGLISH_LANGUAGE_CODE) && !languageCode.equals(MiBancoConstants.SPANISH_LANGUAGE_CODE))
            languageCode = MiBancoConstants.SPANISH_LANGUAGE_CODE;

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        //updateConfiguration is deprecated in Oreo, use context.createConfigurationContext instead
        Configuration configuration = new Configuration(activityContext.getResources().getConfiguration());
        configuration.setLocale(locale);

        if (configuration.fontScale > 1.3f) {
            configuration.fontScale = 1.3f;
        }
        
        activityContext.getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        if (languageCode.equalsIgnoreCase(MiBancoConstants.ENGLISH_LANGUAGE_CODE)) {
            setDateFormat(MiBancoConstants.ENGLISH_DATE_FORMAT);
        } else if (languageCode.equalsIgnoreCase(MiBancoConstants.SPANISH_LANGUAGE_CODE)) {
            setDateFormat(MiBancoConstants.SPANISH_DATE_FORMAT);
        }
        return languageCode;
    }

//    public void setLastDisplayedGroup(final BankOverlayItem lastDisplayedGroup) {
//        this.lastDisplayedGroup = lastDisplayedGroup;
//    }

    /**
     * Sets the last session check time.
     *
     * @param lastUserInteractionTime the new last session check time
     */
    public void setLastUserInteractionTime(final long lastUserInteractionTime) {
        this.lastUserInteractionTime = lastUserInteractionTime;
    }

//    public void setLocatorClustersCache(final SparseArray<List<BankOverlayItem>> locatorClustersCache) {
//        this.locatorClustersCache = locatorClustersCache;
//    }

    public void setRefreshPaymentsCardImages(final boolean refreshCardImages) {
        refreshPaymentsCardImages = refreshCardImages;
    }

    public void setRefreshTransfersCardImages(final boolean refreshTransfersCardImages) {
        this.refreshTransfersCardImages = refreshTransfersCardImages;
    }

    /**
     * Sets the running task.
     *
     * @param runningTask the new running task
     */
    public void setRunningTask(final BaseAsyncTask runningTask) {
        this.runningTask = runningTask;
    }

    /**
     * Sets the UID.
     *
     * @param uid the new UID
     */
    public void setUID(final String uid) {
        this.uid = uid;
    }

    /**
     * Stops the last launched task.
     */
    public void stopLastLaunchedTask() {
        if (runningTask != null) {
            runningTask.cancel(true);
        }
    }

    /**
     * Validates session on Activity resume.
     *
     * @param activityContext the Activity context
     */
    public void validateSessionOnResume(final Context activityContext) {
        if(activityContext != null) {
            ((Activity) activityContext).getWindow().getDecorView().setVisibility(View.INVISIBLE);
            if (getLastUserInteractionTime() == MiBancoConstants.NO_LAST_USER_INTERACTION_AVAILABLE
                    || getLastUserInteractionTime() + MiBancoConstants.USER_INTERACTION_TIMEOUT_MILLIS < System.currentTimeMillis()) {
                reLogin(activityContext);
            } else {
                ((Activity) activityContext).getWindow().getDecorView().setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Re-login.
     *
     * @param activityContext the context of Activity calling the method
     */
    public void reLogin(final Context activityContext) {
        ((Activity) activityContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Utils.dismissDialog(dialogCoverupValidation);
        MiBancoPreferences.setNewNotificationsFlag(false);

        View logOutView = ((Activity) activityContext).getLayoutInflater().inflate(R.layout.log_out_screen, null);
        View rootView = ((Activity) activityContext).getWindow().getDecorView().getRootView();

        if (rootView instanceof ViewGroup) {
            ViewGroup rootViewGroup = (ViewGroup) rootView;
            int childCount = rootViewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View v = rootViewGroup.getChildAt(i);
                if (v != null) {
                    v.setVisibility(View.GONE);
                }
            }
            rootViewGroup.addView(logOutView);
            LayoutParams p = logOutView.getLayoutParams();
            if (p != null) {
                p.height = LayoutParams.MATCH_PARENT;
                p.width = LayoutParams.MATCH_PARENT;
            }
        } else {
            rootView.setBackgroundColor(Color.WHITE);
            rootView.setEnabled(false);
            dialogCoverupValidation = new DialogCoverup(activityContext);
            dialogCoverupValidation.setProgressCaption(R.string.logging_out);
            Utils.showDialog(dialogCoverupValidation, activityContext);
        }

        if(asyncTasks != null) {
            asyncTasks.stopRunningTasks();
        }

        if (asyncTasks != null && loggedInUser != null) {
            asyncTasks.logout(activityContext, true, new SimpleListener() {

                @Override
                public void done() {
                    startOver(activityContext);
                }
            });
        } else {
            startOver(activityContext);
        }
    }

    /**
     * Restarts the application.
     *
     * @param activityContext the current Activity
     */
    private void startOver(final Context activityContext) {
        Utils.dismissDialog(dialogCoverupValidation);
        dialogCoverupValidation = null;

        Utils.dismissDialog(dialogCoverupUpdateBalances);
        dialogCoverupUpdateBalances = null;

        clearSessionData();

        Utils.removePrefsString(MiBancoConstants.CARD_ID_STRING_PREFS_KEY, activityContext);
        Utils.removePrefsString(MiBancoConstants.PREFS_KEY_CAMERA_URI_PATH, activityContext);
        Utils.removePrefsString(MiBancoConstants.ENTERED_USERNAME_PREFS_KEY, activityContext);

        final Intent kIntent = new Intent(MiBancoConstants.KILL_ACTION);
        kIntent.setType(MiBancoConstants.KILL_TYPE);
        activityContext.sendBroadcast(kIntent);

        final Intent iIntro = new Intent();
        iIntro.setClass(activityContext, IntroScreen.class);
        iIntro.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activityContext.startActivity(iIntro);
    }

    /**
     * Nulls out all session-dependent data.
     */
    private void clearSessionData() {
        // Reset all session-dependent data
        setLoggedInUser(null);
        setPaymentsInfo(null);
        setTransfersInfo(null);
        setEbills(null);
        setTransactions(null);
        setAccountsCards(null);
        setGlobalStatus(null);
        setLoginGet(null);
        setValidEbills(null);
        setEbillRequest(null);
        setCustomerAccountsMap(null);

        setUpdatingBalances(false);
        setPortalDelayInEffect(false);
        setRefreshPaymentsCardImages(false);
        setRefreshTransfersCardImages(false);
        setReloadPayments(false);
        setReloadTransfers(false);

        setDepositCheckInformationFromSession(false);
        setDepositCheckSelectedAccount(null);
        setDepositCheckFrontImage(null);
        setDepositCheckBackImage(null);
        setRemoteDepositHistory(null);
        setDepositCheckCurrentState(0);
    }

    /**
     * Parses account cards dictionary.
     *
     * @throws Exception the exception
     */
    private void parseAccountCardsDictionary() throws Exception {
        if (accountsCards == null) {
            final AssetManager am = getApplicationContext().getAssets();
            final InputStream stream = am.open("CardPlastics.plist");
            final List<Node> dict = getDictionaries(getRoot(stream));
            accountsCards = new HashMap<>();
            for (final Node n : dict) {
                final HashMap<String, Node> hm2 = getDictionariesByKeys(n);
                Set<String> keySet = hm2.keySet();
                for (final String key : keySet) {
                    final HashMap<String, Node> hm3 = getDictionariesByKeys(hm2.get(key));
                    final HashMap<String, CardDict> innerHM = new HashMap<String, CardDict>();
                    for (final String key2 : hm3.keySet()) {
                        String img = "";
                        String desc = "";
                        final Element e = (Element) hm3.get(key2);
                        final NodeList nodeElements = e.getChildNodes();
                        int nodeElementsLength = nodeElements.getLength();
                        for (int i = 0; i < nodeElementsLength; i++) {
                            Element eleKey;
                            Element eleString;
                            Node ne = nodeElements.item(i);
                            while (ne != null && ne.getNodeType() != Node.ELEMENT_NODE) {
                                ne = ne.getNextSibling();
                                i++;
                            }
                            if (ne == null || ne.getNodeType() != Node.ELEMENT_NODE) {
                                break;
                            }
                            final Element elem = (Element) ne;
                            if (elem.getNodeName().equals("key")) {
                                eleKey = elem;
                                i++;
                                Node nn = nodeElements.item(i);
                                while (nn != null && nn.getNodeType() != Node.ELEMENT_NODE) {
                                    nn = nn.getNextSibling();
                                    i++;
                                }
                                if (nn == null || nn.getNodeType() != Node.ELEMENT_NODE) {
                                    break;
                                }

                                eleString = (Element) nn;

                                while (eleString != null && !eleString.getNodeName().equalsIgnoreCase("string") && !eleString.getNodeName().equalsIgnoreCase("key")) {
                                    nn = eleString.getNextSibling();
                                    i++;
                                    while (nn != null && nn.getNodeType() != Node.ELEMENT_NODE) {
                                        nn = nn.getNextSibling();
                                        i++;
                                    }
                                    if (nn == null || nn.getNodeType() != Node.ELEMENT_NODE) {
                                        eleString = null;
                                    } else {
                                        eleString = (Element) nn;
                                    }
                                }
                                if (eleString != null) {
                                    if (eleString.getNodeName().equalsIgnoreCase("string")) {
                                        if (getValue(eleKey, "key").equals("image")) {
                                            img = getValue(eleString, "string").toLowerCase().replace('-', '_');
                                        } else if (getValue(eleKey, "key").equals("description")) {
                                            desc = getValue(eleString, "string");
                                        }
                                    } else if (eleString.getNodeName().equalsIgnoreCase("key")) {
                                        i--;
                                    }
                                }
                            }
                        }

                        if (img != null && desc != null) {
                            innerHM.put(key2, new CardDict(img, desc));
                        }
                        img = null;
                        desc = null;
                    }
                    accountsCards.put(key, innerHM);
                }
            }
        }
    }

    /**
     * Gets the root element from the stream.
     *
     * @param stream the stream
     * @return the root element for the stream
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException                 the SAX exception
     * @throws IOException                  Signals that an I/O exception has occurred.
     */
    private Element getRoot(final InputStream stream) throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilder builder = factory.newDocumentBuilder();
        factory.setNamespaceAware(false);
        Document doc;
        doc = builder.parse(new InputSource(stream));
        return doc.getDocumentElement();
    }

    private String getValue(final Element item, final String str) {
        if (str.equals(item.getTagName())) {
            final String value = getElementValue(item);
            if (value != null) {
                return value;
            }
        }
        final NodeList n = item.getElementsByTagName(str);
        return getElementValue(n.item(0));
    }

    /**
     * Sets account card.
     *
     * @param account The account to set a card to.
     */
    private void setAccountCard(final CustomerAccount account) {
        try {
            if (accountsCards == null) {
                return;
            }

            CardDict cardResourceDictionaryEntry = accountsCards.get(account.getSubtype()).get(account.getProductId());
            /**
             * Checks and enables the default value in CardPlastics.plist
             */
            if (FeatureFlags.MBCA_104() && cardResourceDictionaryEntry == null) {
                cardResourceDictionaryEntry = accountsCards.get(account.getSubtype()).get("default");
            }

            if (cardResourceDictionaryEntry != null) {
                final String name = cardResourceDictionaryEntry.getImg();

                account.setResName(name);

                int res = getResources().getIdentifier("account_image_" + name, "drawable", getPackageName());
                if (res != 0) {
                    account.setImgResource(res);
                }

                res = getResources().getIdentifier("carousel_card_" + name, "drawable", getPackageName());
                if (res != 0) {
                    account.setGalleryImgResource(res);
                }

                res = getResources().getIdentifier("transaction_card_" + name, "drawable", getPackageName());
                if (res != 0) {
                    account.setWheelImgResource(res);
                }
            }
        } catch (final Exception e) {
            Log.w("App", e);
        }
    }


    public int getAccountCardResource(String apiAccountKey) {
        CustomerAccount matchingAccount = null;
        if(loggedInUser.getDepositAccounts() != null && loggedInUser.getDepositAccounts().size()>0) {
            for (CustomerAccount account : loggedInUser.getDepositAccounts()) {
                if (account.getApiAccountKey().equals(apiAccountKey)) {
                    matchingAccount = account;
                    break;
                }
            }
        }

        if (accountsCards == null || matchingAccount == null) {
            return R.drawable.account_image_default;
        }

        CardDict cardResourceDictionaryEntry = accountsCards.get(matchingAccount.getSubtype()).get(matchingAccount.getProductId());
        if (cardResourceDictionaryEntry != null) {
            final String name = cardResourceDictionaryEntry.getImg();
            int imageResourceId = getResources().getIdentifier("account_image_" + name, "drawable", getPackageName());
            return imageResourceId == 0 ? R.drawable.account_image_default : imageResourceId;
        }

        return R.drawable.account_image_default;
    }

    public CustomerAccount getAccount(String apiAccountKey) {
        for (CustomerAccount account : loggedInUser.getDepositAccounts()) {
            if (account.getApiAccountKey().equals(apiAccountKey)) {
                return account;
            }
        }
        return null;
    }

    public int getAccountCardResource(CustomerAccount account) {
        if (accountsCards == null || account == null) {
            return R.drawable.account_image_default;
        }

        CardDict cardResourceDictionaryEntry = accountsCards.get(account.getSubtype()).get(account.getProductId());
        if (cardResourceDictionaryEntry != null) {
            final String name = cardResourceDictionaryEntry.getImg();
            return getResources().getIdentifier("account_image_" + name, "drawable", getPackageName());
        }

        return R.drawable.account_image_default;
    }

    public void setCurrentLanguage(final String language) {
        if(apiClient != null) {
            apiClient.setCurrentLanguage(language);
        }
    }

    public HashMap<String, HashMap<String, CardDict>> getAccountCards() throws Exception {
        parseAccountCardsDictionary();
        return accountsCards;
    }

    public LoginGet getLoginGet() {
        return loginGet;
    }

    public String getCurrentWebserviceLanguage() {
        return apiClient.getCurrentLanguage();
    }

    /**
     * Gets dictionaries from a document starting down the given root element.
     *
     * @param root the root element of a document
     * @return dictionaries for the given root element
     */
    private List<Node> getDictionaries(final Element root) {
        final List<Node> dictionaries = new ArrayList<>();
        final NodeList list = root.getChildNodes();
        int listLength = list.getLength();
        for (int i = 0; i < listLength; i++) {
            final Node n = list.item(i);
            if ("dict".equalsIgnoreCase(n.getNodeName())) {
                dictionaries.add(n);
            }
        }
        return dictionaries;
    }

    /**
     * Gets dictionaries by keys.
     *
     * @param root the root node
     * @return dictionaries by keys
     */
    private HashMap<String, Node> getDictionariesByKeys(final Node root) {
        final HashMap<String, Node> dictionaries = new HashMap<String, Node>();
        final NodeList list = root.getChildNodes();
        int listLength = list.getLength();
        for (int i = 0; i < listLength; i++) {
            Node n = list.item(i);
            String key;
            if ("key".equalsIgnoreCase(n.getNodeName())) {
                key = n.getFirstChild().getNodeValue();
                i++;
                n = n.getNextSibling();
                while (n != null && !"dict".equalsIgnoreCase(n.getNodeName())) {
                    i++;
                    n = n.getNextSibling();
                }
                if (n != null) {
                    dictionaries.put(key, n);
                }
            }

        }
        return dictionaries;
    }

    public EBills getEbills() {
        return ebills;
    }

    /**
     * Gets the element value.
     *
     * @param elem the element
     * @return the element value
     */
    private String getElementValue(final Node elem) {
        Node kid;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (kid = elem.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
                    if (kid.getNodeType() == Node.TEXT_NODE) {
                        return kid.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    public GlobalStatus getGlobalStatus() {
        return globalStatus;
    }

    public HashMap<String, SparseArray<SparseArray<AccountTransactions>>> getLoadedTransactions() {
        return transactions;
    }

    public Payment getPaymentsInfo() {
        return paymentsInfo;
    }

    /**
     * Associates user accounts with card images.
     *
     * @throws Exception the exception
     */
    public void loadAccounts() throws Exception {
        HashMap<String, CustomerAccount> customerAccountsMap = new HashMap<String, CustomerAccount>();
        parseAccountCardsDictionary();
        for (final CustomerAccount acc : loggedInUser.getCreditCards()) {
            setAccountCard(acc);
            customerAccountsMap.put(acc.getApiAccountKey() + acc.getAccountNumberSuffix(), acc);
        }
        for (final CustomerAccount acc : loggedInUser.getDepositAccounts()) {
            setAccountCard(acc);
            customerAccountsMap.put(acc.getApiAccountKey() + acc.getAccountNumberSuffix(), acc);
        }
        for (final CustomerAccount acc : loggedInUser.getLoans()) {
            setAccountCard(acc);
            customerAccountsMap.put(acc.getApiAccountKey() + acc.getAccountNumberSuffix(), acc);
        }
        for (final CustomerAccount acc : loggedInUser.getMortgage()) {
            setAccountCard(acc);
            customerAccountsMap.put(acc.getApiAccountKey() + acc.getAccountNumberSuffix(), acc);
        }
        for (final CustomerAccount acc : loggedInUser.getCdsIras()) {
            setAccountCard(acc);
            customerAccountsMap.put(acc.getApiAccountKey() + acc.getAccountNumberSuffix(), acc);
        }
        for (final CustomerAccount acc : loggedInUser.getOtherAccounts()) {
            setAccountCard(acc);
            customerAccountsMap.put(acc.getApiAccountKey() + acc.getAccountNumberSuffix(), acc);
        }
        for (final CustomerAccount acc : loggedInUser.getRewards()) {
            setAccountCard(acc);
            customerAccountsMap.put(acc.getApiAccountKey() + acc.getAccountNumberSuffix(), acc);
        }
        for (final CustomerAccount acc : loggedInUser.getInsuranceAndSecurities()) {
            setAccountCard(acc);
            customerAccountsMap.put(acc.getApiAccountKey() + acc.getAccountNumberSuffix(), acc);
        }
        for (final CustomerAccount acc : loggedInUser.getRetirementPlanAccounts()) {
            setAccountCard(acc);
            customerAccountsMap.put(acc.getApiAccountKey() + acc.getAccountNumberSuffix(), acc);
        }
        setCustomerAccountsMap(customerAccountsMap);
    }


    public void clearSessionDataFromWidget() {
        if(asyncTasks != null) {
            asyncTasks.stopRunningTasks();
        }
        clearSessionData();
    }

    public boolean isReloadTransfers() {
        return reloadTransfers;
    }

    public void setReloadTransfers(boolean reloadTransfers) {
        this.reloadTransfers = reloadTransfers;
    }

    public CustomerEntitlements getCustomerEntitlements() {
        return customerEntitlements;
    }

    public void setCustomerEntitlements(CustomerEntitlements customerEntitlements) {
        this.customerEntitlements = customerEntitlements;
    }

    public boolean isReloadPayments() {
        return reloadPayments;
    }

    public void setReloadPayments(boolean reloadPayments) {
        this.reloadPayments = reloadPayments;
    }

    public List<EBillsItem> getValidEbills() {
        return validEbills;
    }

    public void setValidEbills(List<EBillsItem> validEbills) {
        this.validEbills = validEbills;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Bundle getEbillRequest() {
        return ebillRequest;
    }

    public void setEbillRequest(Bundle ebillRequest) {
        this.ebillRequest = ebillRequest;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public HashMap<String, CustomerAccount> getCustomerAccountsMap() {
        return customerAccountsMap;
    }

    public void setCustomerAccountsMap(HashMap<String, CustomerAccount> customerAccountsMap) {
        this.customerAccountsMap = customerAccountsMap;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
        apiClient = new ApiClient(apiUrl, deviceId, getLanguage(), getApplicationContext());
    }

    public boolean getDepositCheckInformationFromSession() {
        return this.depositCheckInformationFromSession;
    }

    public void setDepositCheckInformationFromSession(boolean depositCheckInformationFromSession) {
        this.depositCheckInformationFromSession = depositCheckInformationFromSession;
    }

    public int getDepositCheckFlashStatus() {
        return depositCheckFlashStatus;
    }

    public void setDepositCheckFlashStatus(int depositCheckFlashStatus) {
        this.depositCheckFlashStatus = depositCheckFlashStatus;
    }

    public byte[] getDepositCheckFrontImage() {
        return depositCheckFrontImage;
    }

    public void setDepositCheckFrontImage(byte[] depositCheckFrontImage) {
        this.depositCheckFrontImage = depositCheckFrontImage;
    }

    public byte[] getDepositCheckBackImage() {
        return depositCheckBackImage;
    }

    public void setDepositCheckBackImage(byte[] depositCheckBackImage) {
        this.depositCheckBackImage = depositCheckBackImage;
    }

    public int getDepositCheckAmount() {
        return this.depositCheckAmount;
    }

    public void setDepositCheckAmount(int depositCheckAmount) {
        this.depositCheckAmount = depositCheckAmount;
    }

    public int getDepositCheckCurrentState() {
        return this.depositCheckCurrentState;
    }

    public void setDepositCheckCurrentState(int depositCheckCurrentState) {
        this.depositCheckCurrentState = depositCheckCurrentState;
    }

    public CustomerAccount getDepositCheckSelectedAccount() {
        return this.depositCheckSelectedAccount;
    }

    public void setDepositCheckSelectedAccount(CustomerAccount depositCheckSelectedAccount) {
        this.depositCheckSelectedAccount = depositCheckSelectedAccount;
    }

    public boolean isSaveUsername() {
        return saveUsername;
    }

    public void setSaveUsername(boolean saveUsername) {
        this.saveUsername = saveUsername;
    }

    public Boolean getWalletCalledAuthentication() {
        return this.walletCalledAthentication;
    }

    public void setWalletCalledAuthentication(Boolean walletCalledAthentication) {
        this.walletCalledAthentication = walletCalledAthentication;
    }

    public String getWalletRequest() {
        return this.walletRequest;
    }

    public void setWalletRequest(String walletRequest) {
        this.walletRequest = walletRequest;
    }

    public String getCallingWallet() {
        return this.callingWallet;
    }

    public void setCallingWallet(String callingWallet) {
        this.callingWallet = callingWallet;
    }



    public Boolean getWidgetCalledPayments() {
        return this.widgetCalledPayments;
    }

    public void setWidgetCalledPayments(Boolean widgetCalledPayments) {
        this.widgetCalledPayments = widgetCalledPayments;
    }

    public Boolean getWidgetCalledTransfers() {
        return this.widgetCalledTransfers;
    }

    public void setWidgetCalledTransfers(Boolean widgetCalledTransfers) {
        this.widgetCalledTransfers = widgetCalledTransfers;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the customerToken
     */
    public String getCustomerToken() {
        return customerToken;
    }

    /**
     * @param customerToken the customerToken to set
     */
    public void setCustomerToken(String customerToken) {
        this.customerToken = customerToken;
    }

    /**
     * @return the widgetDeviceId
     */
    public String getWidgetDeviceId() {
        return widgetDeviceId;
    }

    /**
     * @param widgetDeviceId the widgetDeviceId to set
     */
    public void setWidgetDeviceId(String widgetDeviceId) {
        this.widgetDeviceId = widgetDeviceId;
    }

    /**
     * @return the globalAthmEntitlementEnabled
     */
    public boolean isGlobalAthmEntitlementEnabled() {
        return globalAthmEntitlementEnabled;
    }

    /**
     * @param globalAthmEntitlementEnabled the globalAthmEntitlementEnabled to set
     */
    public void setGlobalAthmEntitlementEnabled(boolean globalAthmEntitlementEnabled) {
        this.globalAthmEntitlementEnabled = globalAthmEntitlementEnabled;
    }

    public boolean isGlobalMobileCashEntitlementEnabled() {
        return globalMobileCashEntitlementEnabled;
    }

    public void setGlobalMobileCashEntitlementEnabled(boolean globalMobileCashEntitlementEnabled) {
        this.globalMobileCashEntitlementEnabled = globalMobileCashEntitlementEnabled;
    }

    public boolean isGlobalCashdropEntitlementEnabled() {
        if(!globalCashdropEntitlementExists){
            globalCashdropEntitlementEnabled = globalMobileCashEntitlementEnabled;
        }
        return globalCashdropEntitlementEnabled;
    }

    public List<AccountCard> getListAccountsSelect() {
        return listAccountsSelect;
    }

    public void setListAccountsSelect(List<AccountCard> listAccountsSelect) {
        this.listAccountsSelect = listAccountsSelect;
    }

    public boolean isAutoLogin() {
        return isAutoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        isAutoLogin = autoLogin;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public boolean isSessionNeeded() {
        return isSessionNeeded;
    }

    public void setSessionNeeded(boolean sessionNeeded) {
        isSessionNeeded = sessionNeeded;
    }

    public EasyCashHistoryReceipt.Callback getCallback() {
        return callback;
    }

    public void setCallback(EasyCashHistoryReceipt.Callback callback) {
        this.callback = callback;
    }

    public String getCustomerPhone(Context context) {
        if(Utils.isBlankOrNull(customerPhone) && currentUser == null){
            customerPhone = Utils.getStringContentFromShared(context,"customerPhone");
        }
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public boolean isUpdateEasyCashHistory() {
        return updateEasyCashHistory;
    }

    public void setUpdateEasyCashHistory(boolean updateEasyCashHistory) {
        this.updateEasyCashHistory = updateEasyCashHistory;
    }



    public boolean needsGlobalStatusNonCustomerRefresh()
    {
        return needsRefresh("GLOBAL");
    }

    public boolean needsTransactionsNonCustomerRefresh()
    {
        return needsRefresh("TRX");
    }

    private boolean needsRefresh(String type)
    {
        Date currentDate = new Date();
        boolean needsRefresh;
        if(type.equalsIgnoreCase("GLOBAL")) {
            if (lastNonCustGlobalStatusUpdate == null) {
                lastNonCustGlobalStatusUpdate = currentDate;
                return true;
            }

            long differenceInSeconds = Utils.dateDifferenceInSeconds(currentDate, lastNonCustGlobalStatusUpdate);
            needsRefresh = differenceInSeconds >= MiBancoConstants.EC_NONCUST_REFRESHRATE_ENT_SECS;

            if (needsRefresh) {
                lastNonCustGlobalStatusUpdate = currentDate;
            }
        }else{

            if (lastNonCustEasyCashUpdate == null) {
                lastNonCustEasyCashUpdate = currentDate;
                return true;
            }

            long differenceInSeconds = Utils.dateDifferenceInSeconds(currentDate, lastNonCustEasyCashUpdate);
            needsRefresh = differenceInSeconds >= MiBancoConstants.EC_NONCUST_REFRESHRATE_SECS;

            if (needsRefresh) {
                lastNonCustEasyCashUpdate = currentDate;
            }

        }
        return needsRefresh;
    }

    public static boolean isSelectedNonTransactioanlAcct(String accountSubtype) {
        return ArrayUtils.contains(MiBancoConstants.allowedNonTransactionalAccounts,  accountSubtype);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // In some cases modifying newConfig leads to unexpected behavior,
        // so it's better to edit new instance.
        Configuration configuration = new Configuration(newConfig);

        getApplicationContext().getResources().updateConfiguration(configuration, getApplicationContext().getResources().getDisplayMetrics());

    }

    /**
     * Initialize the FlurryAgent SDK
     * @param context
     */
    private void initFlurryAgent(Context context) {

        new FlurryAgent.Builder()
                .withDataSaleOptOut(true)
                .withCaptureUncaughtExceptions(false)
                .withIncludeBackgroundSessionsInMetrics(true)
                .withLogLevel(Log.ERROR)
                .withPerformanceMetrics(FlurryPerformance.NONE)
                .build(context, getString(R.string.flurry_api_key)); //Initialize the FlurryAgent SDK

    }



    public boolean isPushWarningShowing() {
        return isPushWarningShowing;
    }

    public void setPushWarningShowing(boolean pushWarningShowing) {
        isPushWarningShowing = pushWarningShowing;
    }

    public boolean isLoginSSDSForced() {
        return isLoginSSDSForced;
    }

    public void setLoginSSDSForced(boolean loginSSDSForced) {
        isLoginSSDSForced = loginSSDSForced;
    }

}
