package com.popular.android.mibanco.base;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.IntroScreen;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoPreferences;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.activity.Accounts;
import com.popular.android.mibanco.activity.AthmActivity;
import com.popular.android.mibanco.activity.AthmTransfer;
import com.popular.android.mibanco.activity.Contact;
import com.popular.android.mibanco.activity.DepositCheck;
import com.popular.android.mibanco.activity.Downtime;
import com.popular.android.mibanco.activity.EasyCashFaqs;
import com.popular.android.mibanco.activity.EasyCashHistoryActivity;
import com.popular.android.mibanco.activity.EasyCashLocator;
import com.popular.android.mibanco.activity.EasyCashStaging;
import com.popular.android.mibanco.activity.EnrollmentLiteWelcomeActivity;
import com.popular.android.mibanco.activity.MarketplaceActivity;
import com.popular.android.mibanco.activity.Payments;
import com.popular.android.mibanco.activity.RDCHistory;
import com.popular.android.mibanco.activity.Receipts;
import com.popular.android.mibanco.activity.Receipts.HistoryType;
import com.popular.android.mibanco.activity.SettingsList;
import com.popular.android.mibanco.activity.WebViewActivity;
import com.popular.android.mibanco.adapter.MenuAdapter;
import com.popular.android.mibanco.fragment.PaymentsTransfersFragment;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.CustomerEntitlements;
import com.popular.android.mibanco.object.SidebarItem;
import com.popular.android.mibanco.util.ATHMUtils;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.DFMUtils;
import com.popular.android.mibanco.util.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

//import com.popular.android.mibanco.activity.LocatorTabs;

/**
 * The base class for Activities shown to logged in user.
 */
public abstract class BaseSessionActivity extends BaseActivity implements OnClickListener {

    private static final long SLIDING_MENU_TOGGLE_DELAY_MILLIS = 100;
    private static final String SIDEBAR_MARKETPLACE = "ic_sidebar_marketplace"; //Marketplace icon

    private SlidingMenu slidingMenu;
    private List<SidebarItem> menuItems;

    private int athmSidebarItemIndex = -1;
    private int depositeCheckItemIndex = -1;
    private int easyCashSidebarItemIndex = -1;
    private int locatorSidebarItemIndex = -1;
    //MBSFE-1712
    private int goToDesktopSidebarItemIndex = -1;
    //END MBSFE-1712
    private int showMarketplaceItemIndex = -1; //Identify when Marketplace is pressed on SideMenu

    private int showRequestDocuments = -1;

    private Fragment selectedFragment;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(application != null && application.getAsyncTasksManager() != null) {
            if (this instanceof Accounts
                    || this instanceof Payments
                    || this instanceof Receipts
                    || this instanceof RDCHistory
                    || this instanceof DepositCheck
                    || this instanceof AthmActivity
                    || this instanceof EasyCashStaging
                    || this instanceof EasyCashHistoryActivity) {
                setupDrawerToolbar();
//                setupDrawer();
            }
        }
    }

    protected void setupDrawerToolbar() {
        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            LayoutInflater inflater = getLayoutInflater();

            View customView = inflater.inflate(R.layout.sidebar_open, null);
            actionBar.setCustomView(customView);
        }
    }


    protected void setupDrawer() {

        if(application != null && application.getAsyncTasksManager()!= null) {
            setUpMenuItems();
            slidingMenu = new SlidingMenu(this);

            slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
            slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
            slidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
            slidingMenu.setBehindScrollScale(0.25f);
            slidingMenu.setFadeDegree(0.25f);

            slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);

            slidingMenu.setMenu(R.layout.menu_sidebar);
            ListView menuListView = (ListView) slidingMenu.findViewById(R.id.menulistView);
            menuListView.setAdapter(new MenuAdapter(this, menuItems));
            menuListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    launchOption(position);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        if(App.getApplicationInstance().isSessionNeeded()) {
            if (application != null && application.getAsyncTasksManager() != null) {
                application.validateSessionOnResume(this);
            } else {
                errorReload();
            }
        }
        super.onResume();
    }

    public void errorReload()
    {
        final Intent iIntro = new Intent(this, IntroScreen.class);
        iIntro.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(iIntro);
        finish();
    }

    @Override
    public void onUserInteraction() {
        if (application.getLastUserInteractionTime() == MiBancoConstants.NO_LAST_USER_INTERACTION_AVAILABLE
                || application.getLastUserInteractionTime() + MiBancoConstants.USER_INTERACTION_TIMEOUT_MILLIS < System.currentTimeMillis()) {

            if(App.getApplicationInstance().isSessionNeeded()) {
                application.reLogin(this);
            }
        } else {
            application.setLastUserInteractionTime(System.currentTimeMillis());
        }

        super.onUserInteraction();
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        final MenuItem menuContact = menu.findItem(R.id.menu_contact);
        final MenuItem menuLocator = menu.findItem(R.id.menu_locator);
        final MenuItem menuSettings = menu.findItem(R.id.menu_settings);
        final MenuItem menuLogout = menu.findItem(R.id.menu_logout);

        if(App.getApplicationInstance().isSessionNeeded()) {
            menuLogout.setVisible(true);
        }else{
            menuLogout.setVisible(false);
        }

        menuContact.setVisible(false);
        menuLocator.setVisible(false);
        menuSettings.setVisible(false);

        menuContact.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menuLocator.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        ImageView bell = findViewById(R.id.bell_toolbar);
        bell.setVisibility(View.VISIBLE);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        if (selectedFragment instanceof PaymentsTransfersFragment) {
            return ((PaymentsTransfersFragment) selectedFragment).onOptionsItemSelected(item);
        }
        if (launchOption(item.getItemId())) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onClick(View v) {

        launchOption(v.getId());
    }

    private boolean launchOption(int id) {
        switch (id) {
            case R.id.btn_sidebar_open:
                slidingMenu.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (slidingMenu != null) {
                            slidingMenu.toggle();
                        }
                    }
                }, SLIDING_MENU_TOGGLE_DELAY_MILLIS);
                break;
            case R.id.menu_logout:
                application.reLogin(this);
                break;

            case R.id.notification_center_active:
            case R.id.notification_center_normal:
            case R.id.notification_center_ribbon:
                notificationCenterWebView();
                recallOnPrepareMenu();
                break;

            default:
                if (id == athmSidebarItemIndex) {
                    if (!(this instanceof  AthmTransfer))
                        ATHMUtils.athmVersionViewDeciderAction(this);
                    break;
                }
                else if (id == easyCashSidebarItemIndex && this instanceof  EasyCashStaging) {
                    EasyCashStaging easycash = (EasyCashStaging) this;
                    easycash.resetToInitialValues();
                } else if (id == locatorSidebarItemIndex) {
                    Utils.openExternalUrl(this, getString(R.string.locator_url));
                //MBSFE-1712
                } else if (id == goToDesktopSidebarItemIndex) {
                    desktopVersionWebView();
                }
                //END MBSFE-1712
                else if (id == depositeCheckItemIndex
                        && DFMUtils.isLimitsPerSegmentEnabled()
                        && App.getApplicationInstance().getLoggedInUser().getIsComercialCustomer()
                        && !(this instanceof DepositCheck)) {
                    BPAnalytics.logEvent(BPAnalytics.EVENT_COMMERCIAL_REMOTE_DEPOSIT);
                    DFMUtils.verifyAcceptedTermsAction(this);
                    break;
                } else if (id == showMarketplaceItemIndex) {
                    marketplaceActivity();
                    BPAnalytics.logEvent(BPAnalytics.EVENT_MARKETPLACE_SECTION);
                } else if (id == showRequestDocuments){
                    startActivity(openRequestDocumentsWebView());
                }

                SidebarItem selectedOption = menuItems.get(id);
                if(selectedOption.getIntent() != null)
                    startActivity(selectedOption.getIntent());
                break;
        }

        if (slidingMenu != null) {
            slidingMenu.showContent(false);
        }

        return true;
    }

    private void setUpMenuItems() {
        menuItems = new LinkedList<>();
        if(App.getApplicationInstance().isSessionNeeded()){
            setupCustomerMenu();

        }else{
            setupNonCustomerMenu();
        }
    }

    private Intent addDefaultMenu(Class<?> cls, int menuString, String drawableName) {
        Intent intent;
        if (cls == null) {
            intent = null;
        }else{
            intent = new Intent(this, cls);
        }

        String menu = Utils.getLocaleStringResource(new Locale(application.getLanguage()), menuString, getBaseContext());

        SidebarItem sidebarItem = new SidebarItem(menu,(drawableName==null)?0:
                getResources().getIdentifier(drawableName, "drawable", getPackageName()),
                intent);
        menuItems.add(sidebarItem);
        return intent;
    }

    private void setupNonCustomerMenu()
    {
        addDefaultMenu(EasyCashFaqs.class, R.string.faq, "ic_sidebar_payments");
        addDefaultMenu(EasyCashLocator.class, R.string.locator, "ic_sidebar_locator");
    }

    private void setupCustomerMenu()
    {
        addDefaultMenu(Accounts.class, R.string.accounts_sidebar, "ic_sidebar_accounts");

        if (hasMarketplaceProducts()) {
            showMarketplaceItemIndex = menuItems.size();
            addDefaultMenu(null, R.string.marketplace_sidebar, SIDEBAR_MARKETPLACE);
        }

        // MobileCash option ***********************************

        CustomerEntitlements customerEntitlements = App.getApplicationInstance().getCustomerEntitlements();

        boolean globalCashDropEntitlement = application.isGlobalCashdropEntitlementEnabled();
        boolean userHasCashDrop = application.getCustomerEntitlements() != null
                && application.getCustomerEntitlements().hasCashDrop() != null
                && application.getCustomerEntitlements().hasCashDrop();

        easyCashSidebarItemIndex = menuItems.size();
        if (!globalCashDropEntitlement && (userHasCashDrop ||
                (application.getCustomerEntitlements() != null && application.getCustomerEntitlements().hasCashDrop() == null))) {
            addDefaultMenu(Downtime.class, R.string.mobilecash_sidebar, "ic_sidebar_retiromovil")
                    .putExtra("downtimeMessage", getResources().getString(R.string.maintenance_mobilecash));

        } else if (globalCashDropEntitlement && userHasCashDrop) {
            addDefaultMenu(EasyCashStaging.class, R.string.mobilecash_sidebar, "ic_sidebar_retiromovil");
            addDefaultMenu(EasyCashHistoryActivity.class, R.string.easycash_pendingtrx_history, null);

        } else if (globalCashDropEntitlement &&
                (application.getCustomerEntitlements() != null && application.getCustomerEntitlements().hasCashDrop() == null)) {
            addDefaultMenu(EnrollmentLiteWelcomeActivity.class, R.string.mobilecash_sidebar, "ic_sidebar_retiromovil").putExtra(MiBancoConstants.KEY_ENROLL_LITE_IS_CUSTOMER, true);
        }

        if(application.getLoggedInUser().getIsTransactional())
        {
            // Payments options **************************************
            if (application.getGlobalTransfersEntitlementEnabled()) {
                addDefaultMenu(Payments.class, R.string.payments_sidebar, "ic_sidebar_payments");

            addDefaultMenu(Receipts.class, R.string.payment_receipts_sidebar, null)
                    .putExtra(Receipts.RECEIPT_TYPE, HistoryType.PAYMENT);
            } else {
                addDefaultMenu(Downtime.class, R.string.payments_sidebar, "ic_sidebar_transfers")
                        .putExtra("downtimeMessage", getResources().getString(R.string.maintenance_payments));

                addDefaultMenu(Downtime.class, R.string.payment_receipts_sidebar, null)
                        .putExtra("downtimeMessage", getResources().getString(R.string.maintenance_payments));
            }

            // Transfers options **************************************
            if (application.getGlobalTransfersEntitlementEnabled()) {
                addDefaultMenu(Payments.class, R.string.transfers_sidebar, "ic_sidebar_transfers")
                        .putExtra("transfers", true);

                addDefaultMenu(Receipts.class, R.string.transfer_receipts_sidebar, null)
                        .putExtra(Receipts.RECEIPT_TYPE, HistoryType.TRANSFER);
            } else {
                addDefaultMenu(Downtime.class, R.string.transfers_sidebar, "ic_sidebar_transfers")
                        .putExtra("downtimeMessage", getResources().getString(R.string.maintenance_transfers));

                addDefaultMenu(Downtime.class, R.string.transfer_receipts_sidebar, null)
                        .putExtra("downtimeMessage", getResources().getString(R.string.maintenance_transfers));
            }

            // RDC Options **************************************
            if (application.getCustomerEntitlements() != null && application.getCustomerEntitlements().hasRdc()) {
                PackageManager pm = getPackageManager();

                List<CustomerAccount> rdcAccounts = application.getLoggedInUser().getRDCAccounts();
                boolean disabledCheckDeposit = ((!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT))
                        || (rdcAccounts == null || (rdcAccounts!= null && rdcAccounts.size() == 0)));

                if (!disabledCheckDeposit) {
                    if (!application.getGlobalRdcEntitlementEnabled()) {
                        if (FeatureFlags.MBMT_417()) {
                            addDefaultMenu(Downtime.class, R.string.deposit_check_sidebar_MBMT_417, "ic_sidebar_check")
                                    .putExtra("downtimeMessage", getResources().getString(R.string.maintenance_rdc));
                        }else {
                            addDefaultMenu(Downtime.class, R.string.deposit_check_sidebar, "ic_sidebar_check")
                                    .putExtra("downtimeMessage", getResources().getString(R.string.maintenance_rdc));
                        }
                    }else{
                        depositeCheckItemIndex = menuItems.size();
                        if (FeatureFlags.MBMT_417()) {
                            addDefaultMenu(DepositCheck.class, R.string.deposit_check_sidebar_MBMT_417, "ic_sidebar_check");
                        }else {
                            addDefaultMenu(DepositCheck.class, R.string.deposit_check_sidebar, "ic_sidebar_check");
                        }
                    }
                }

                if (!application.getGlobalRdcEntitlementEnabled()) {
                    addDefaultMenu(Downtime.class, R.string.review_checks_sidebar, null)
                            .putExtra("downtimeMessage", getResources().getString(R.string.maintenance_rdc));
                }else{
                    addDefaultMenu(RDCHistory.class, R.string.review_checks_sidebar, null);
                }
            }

            // ATHM Options **************************************
            athmSidebarItemIndex = menuItems.size();
            addDefaultMenu(null, R.string.athm_sidebar, "ic_sidebar_athm");
        }

        // Request Documents **************************************
        showRequestDocuments = menuItems.size();
        addDefaultMenu(null, R.string.request_documents, "ic_sidebar_req_documents");

        locatorSidebarItemIndex = menuItems.size();
        addDefaultMenu(null, R.string.locator_sidebar, "ic_sidebar_locator");

        addDefaultMenu(Contact.class, R.string.contactus_sidebar, "ic_sidebar_contactus");
        addDefaultMenu(SettingsList.class,R.string.settings_sidebar,"ic_sidebar_settings");

        //MBSFE-1712
        goToDesktopSidebarItemIndex = menuItems.size();
        addDefaultMenu(null,R.string.go_to_desktop_sidebar,"ic_sidebar_go_to_desktop");
        //END MBSFE-1712
    }

    public SlidingMenu getSlidingMenu() {
        return slidingMenu;
    }

    //Method to execute notification center web view

    private void notificationCenterWebView (){
        final Intent intentWebView = new Intent(BaseSessionActivity.this, WebViewActivity.class);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.notification_center_url)));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, Utils.urlBlacklist(BaseSessionActivity.this));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_TOOLBAR_HIDE_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_PROGRESSBAR_HIDE_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_EXTERNAL_KEY, getResources().getStringArray(R.array.web_view_url_external));
        BPAnalytics.logEvent(BPAnalytics.EVENT_NOTIFICATION_CENTER);
        MiBancoPreferences.setNewNotificationsFlag(false);
        if (findViewById(R.id.notification_center_notice).getVisibility() == View.VISIBLE) {
            findViewById(R.id.notification_center_notice).setVisibility(View.GONE);
        }
        startActivityForResult(intentWebView, MiBancoConstants.NOTIFICATION_CENTER_REQUEST_CODE);
    }

    /**
     * Start new Intent (MarketplaceActivity)
     */
    public void marketplaceActivity() {

        final Intent intentMarketplace = new Intent(BaseSessionActivity.this, MarketplaceActivity.class); //Setup new Intent for WebViewActivity

        intentMarketplace.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, application.getLoggedInUser().getHasVIAccount() ?
                getString(R.string.marketplace_vi_url) : getString(R.string.marketplace_pr_url));

        intentMarketplace.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, Utils.urlBlacklist(BaseSessionActivity.this));
        intentMarketplace.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
        intentMarketplace.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
        intentMarketplace.putExtra(MiBancoConstants.WEB_VIEW_CAN_BACK, true);
        intentMarketplace.putExtra(MiBancoConstants.WEB_VIEW_MARKETPLACE, true);
        intentMarketplace.putExtra(MiBancoConstants.WEB_VIEW_MARKETPLACE_CCA, hasMarketplaceCCA());
        intentMarketplace.putExtra(MiBancoConstants.WEB_VIEW_MARKETPLACE_EACCOUNT, hasEAccount());
        startActivityForResult(intentMarketplace, MiBancoConstants.MARKETPLACE_WEBVIEW_REQUEST_CODE);
    }

    /**
     * @return True if User has Marketplace Products available
     */
    public boolean hasMarketplaceProducts(){
        boolean isMarketplaceAvailable = false;

        if (hasMarketplaceCCA() || hasEAccount()) {
            isMarketplaceAvailable = true;
        }

        return isMarketplaceAvailable;
    }

    /**
     * @return True if User can open an eAccount
     */
    private boolean hasEAccount() {
        return MiBancoPreferences.getOpac() != null && MiBancoPreferences.getOpac().size() > 0
                && "true".equals(MiBancoPreferences.getOpac().get(MiBancoConstants.CAN_OPEN_ACCOUNT))
                && application.getLoggedInUser().getIsTransactional();
    }

    /**
     * @return True if User can apply for Credit Cards
     */
    private boolean hasMarketplaceCCA() {
        return application.getLoggedInUser().getShowMarketplaceCCA();
    }
    private void desktopVersionWebView (){
        final Intent intentWebView = new Intent(BaseSessionActivity.this, WebViewActivity.class);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.mobileSwitch)));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, Utils.urlBlacklist(BaseSessionActivity.this));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
        intentWebView.putExtra(MiBancoConstants.DESKTOP_USER_AGENT, true);
        intentWebView.putExtra(MiBancoConstants.SHOW_BOTTOM_NAVIGATION_BAR, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_BACKACTION_DISABLED_KEY, true);
        startActivityForResult(intentWebView, MiBancoConstants.DESKTOP_WEBVIEW_REQUEST_CODE);
    }

    public Intent openRequestDocumentsWebView(){
        final Intent intentWebView = new Intent(BaseSessionActivity.this, WebViewActivity.class);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, getString(R.string.url_request_documents));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_BACKACTION_DISABLED_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_REQUEST_DOCUMENTS, true);
        return intentWebView;
    }

    public void recallOnPrepareMenu() {
        if(application != null && application.getAsyncTasksManager() != null) {
            invalidateOptionsMenu();
        }
    }

    public void setSelectedSubFragment(Fragment fragment) {
        selectedFragment = fragment;
    }

}
