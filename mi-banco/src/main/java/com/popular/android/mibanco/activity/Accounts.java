package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoPreferences;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.fragment.AccountsFragment;
import com.popular.android.mibanco.fragment.AthFragment;
import com.popular.android.mibanco.fragment.AthmRedirectSplashFragment;
import com.popular.android.mibanco.fragment.DowntimeFragment;
import com.popular.android.mibanco.fragment.MoreFragment;
import com.popular.android.mibanco.fragment.PaymentsTransfersFragment;
import com.popular.android.mibanco.fragment.more.MoreInfoFragment;
import com.popular.android.mibanco.model.Customer;
import com.popular.android.mibanco.util.ATHMUtils;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.viewModel.SharedViewModel;
import com.popular.android.mibanco.viewModel.SharedViewModelFactory;

/**
 * Accounts Activity class.
 */
public class Accounts extends BaseSessionActivity {

    private Context mContext = this;
    private SharedViewModel viewModel;

    public Fragment selectedFragment;

    private final Accounts currentFragment = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounts);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new AccountsFragment()).commit();
        String requestedUrl = getRdcInstructionsUrl(application.getLoggedInUser());
        String accountHome = getResources().getString(R.string.account_home);
        SharedViewModelFactory factory = new SharedViewModelFactory(this, application, requestedUrl, accountHome);
        viewModel = new ViewModelProvider(this, factory).get(SharedViewModel.class);
            Accounts accounts = (Accounts) this;
            accounts.setSelectedFragment(selectedFragment);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Accounts accounts = currentFragment;
//                    selectedFragment = null;
                    Bundle bundle = new Bundle();
                    switch (item.getItemId()) {
                        case R.id.nav_accounts_icons:
                            selectedFragment = new AccountsFragment();
                            break;
                        case R.id.nav_payments_icons:
                            selectedFragment = new PaymentsTransfersFragment();
                            accounts.setSelectedFragment(selectedFragment);
                            bundle.putBoolean("transfers", false);
                            selectedFragment.setArguments(bundle);
                            break;
                        case R.id.nav_transfer_icon:
                            selectedFragment = new PaymentsTransfersFragment();
                            accounts.setSelectedFragment(selectedFragment);
                            bundle.putBoolean("transfers", true);
                            selectedFragment.setArguments(bundle);
                            break;
                        case R.id.nav_ath_icon:
                            boolean isFragmentStored = getSharedPreferences("MyPrefs", MODE_PRIVATE).getBoolean("isFragmentStored", false);
                            if (isFragmentStored && selectedFragment != null) {
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                            } else {
                               ATHMUtils.athmVersionViewDeciderAction(mContext, new ATHMUtils.FragmentCallback() {
                                    @Override
                                    public void onFragmentDetermined(String fragment) {
                                        switch (fragment) {
                                            case "DowntimeFragment":
                                                selectedFragment = new DowntimeFragment();
                                                break;
                                            case "AthmWelcomeSplashFragment":
                                                selectedFragment = new AthFragment();
                                                break;
                                            case "redirectToAthmApkFragment":
                                            case "AthmRedirectSplashFragment":
                                                selectedFragment = new AthmRedirectSplashFragment();
                                                break;
                                            default:
                                                break;
                                        }
                                        getSharedPreferences("MyPrefs", MODE_PRIVATE).edit().putBoolean("isFragmentStored", true).apply();
                                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                                    }
                                });
                            }
                            break;
                        case R.id.nav_more_icon:

                            if (selectedFragment instanceof MoreInfoFragment) {
                                Log.v("openMoreInfoFragment", "MoreInfoFragment" + selectedFragment);
                                selectedFragment = new MoreInfoFragment();
                                break;
                            }
                            Log.v("openMoreInfoFragment", "MoreFragment" + selectedFragment);
                            selectedFragment = new MoreFragment();
                            break;
                        default:
                            break;
                    }
                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                        selectedFragment = null;
                    }
                    return true;
                }
            };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        boolean isTransfer = intent.getBooleanExtra("transfers", false);
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof PaymentsTransfersFragment) {
            PaymentsTransfersFragment paymentsFragment = (PaymentsTransfersFragment) currentFragment;
            paymentsFragment.handleNewIntent(isTransfer);
        }
    }

    public String getRdcInstructionsUrl(Customer customer) {
        String segmentType;
        if (customer.getIsComercialCustomer()){
            segmentType = "comercial";
        } else if (customer.getIsPremiumBanking()){
            segmentType = "pbs";
        } else if (customer.getIsWealth()) {
            segmentType = "wealth";
        } else if (!customer.getIsTransactional()){
            segmentType = "nonretail";
        } else {
            segmentType = "retail";
        }
        return segmentType;
    }

    public void onNotificationCenterClicked() {
        notificationCenterWebView();
        recallOnPrepareMenu();
    }

    private void notificationCenterWebView (){
        final Intent intentWebView = new Intent(Accounts.this, WebViewActivity.class);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.notification_center_url)));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, Utils.urlBlacklist(Accounts.this));
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

    public void recallOnPrepareMenu() {
        if(application != null && application.getAsyncTasksManager() != null) {
            invalidateOptionsMenu();
        }
    }

    public void setSelectedFragment(Fragment fragment) {
        selectedFragment = fragment;
    }
}
