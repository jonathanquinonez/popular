//package com.popular.android.mibanco.activity;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.Instrumentation;
//import android.app.TabActivity;
//import android.content.Intent;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewConfiguration;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.TabHost;
//import android.widget.TabHost.OnTabChangeListener;
//import android.widget.TextView;
//
//import com.popular.android.mibanco.App;
//import com.popular.android.mibanco.MiBancoConstants;
//import com.popular.android.mibanco.R;
//import com.popular.android.mibanco.locator.LocationManager;
//import com.popular.android.mibanco.util.BaseActivityHelper;
//import com.popular.android.mibanco.util.FontChanger;
//import com.popular.android.mibanco.util.PermissionsManagerUtils;
//import com.popular.android.mibanco.util.Utils;
//import com.popular.android.mibanco.view.DialogHolo;
//
//import java.util.List;
//
///**
// * Locator tabs Activity.
// */
//@SuppressLint("NewApi")
//public class LocatorTabs extends TabActivity {
//
//    private TabHost tabHost;
//    private LocationManager locationManager;
//    private OnTabChangeListener tabChangedListener;
//    private BaseActivityHelper baseActivityHelper = new BaseActivityHelper();
//
//    private boolean firstTimeView = false;
//
//    //region ACTIVITY LIFECYCLE METHODS
//
//    @Override
//    public void onCreate(final Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        firstTimeView = true;
//        initializeLocationManagerPermission(true);
//
//        requestWindowFeature(Window.FEATURE_ACTION_BAR);
//        android.app.ActionBar actionbar = getActionBar();
//        if(actionbar != null) {
//            actionbar.show();
//            actionbar.setDisplayHomeAsUpEnabled(true);
//        }
//        setContentView(R.layout.locator_tabs);
//        setTabHost();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        tabChangedListener.onTabChanged(tabHost.getCurrentTabTag());
//
//        if(!firstTimeView){
//            initializeLocationManagerPermission(false);
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        FontChanger.changeFonts(getWindow().getDecorView().getRootView());
//    }
//
//    @Override
//    protected void onStop() {
//        if(locationManager != null) {
//            locationManager.disconnect();
//        }
//        super.onStop();
//    }
//
//    @Override
//    protected void onDestroy() {
//        baseActivityHelper.cancelStackedTasks();
//        baseActivityHelper.dismissStackedDialogs();
//        super.onDestroy();
//    }
//
//    //endregion
//
//
//    private void initializeLocationManagerPermission(boolean askForPermission)
//    {
//        List<String> missingPermissions = PermissionsManagerUtils.missingPermissions(this);
//        if(missingPermissions.size() > 0){
//            if(askForPermission) {
//                PermissionsManagerUtils.askForPermission(this, missingPermissions, MiBancoConstants.REQUEST_CODE_ASK_PERMISSIONS);
//            }
//        }else{
//
//            if(locationManager == null) {
//                locationManager = new LocationManager(LocatorTabs.this);
//                App.getApplicationInstance().setLocationManager(locationManager);
//            }
//        }
//    }
//
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case MiBancoConstants.REQUEST_CODE_ASK_PERMISSIONS:
//                boolean permissionsAccepted = true;
//                if(grantResults.length >0){
//                    for(int res: grantResults){
//                        if(res != PackageManager.PERMISSION_GRANTED){
//                            permissionsAccepted = false;
//                            break;
//                        }
//                    }
//                }
//                if (permissionsAccepted) {
//                    locationManager = new LocationManager(LocatorTabs.this);
//                    App.getApplicationInstance().setLocationManager(locationManager);
//
//                }
//                break;
//            default:
//                break;
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case LocationManager.CONNECTION_FAILURE_RESOLUTION_REQUEST:
//                switch (resultCode) {
//                    case Activity.RESULT_OK:
//                        if(locationManager != null) {
//                            locationManager.connect(this, null);
//                        }
//                        break;
//                    default:
//                        break;
//                }
//                break;
//            default:
//                break;
//        }
//    }
//
//
//
//    @Override
//    public boolean onCreateOptionsMenu(final Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        Utils.setupLanguage(this);
//
//        final MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(final MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                break;
//            case R.id.menu_overflow:
//                new Thread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        final Instrumentation instrumentation = new Instrumentation();
//                        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
//                    }
//                }).start();
//                break;
//            case R.id.menu_contact:
//                final Intent iContact = new Intent(this, Contact.class);
//                startActivity(iContact);
//                break;
//            case R.id.menu_settings:
//                final Intent iSettings = new Intent(this, SettingsList.class);
//                startActivity(iSettings);
//                break;
//            case R.id.menu_logout:
//                showLogoutDialog();
//                break;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//        return true;
//    }
//
//    @SuppressLint("NewApi")
//    @Override
//    public boolean onPrepareOptionsMenu(final Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && ViewConfiguration.get(this).hasPermanentMenuKey()) {
//            menu.findItem(R.id.menu_overflow).setVisible(true);
//        }
//
//        menu.findItem(R.id.menu_locator).setVisible(false);
//        menu.findItem(R.id.menu_logout).setVisible(false);
//
//        return true;
//    }
//
//
//
//    public BaseActivityHelper getBaseActivityHelper() {
//        return baseActivityHelper;
//    }
//
//    public void showLogoutDialog() {
//        final DialogHolo alertDialog = new DialogHolo(this);
//        alertDialog.setTitle(getResources().getString(R.string.title_logout_app));
//        alertDialog.setMessage(getResources().getString(R.string.logout_app));
//        alertDialog.setPositiveButton(getResources().getString(R.string.yes), new View.OnClickListener() {
//            @Override
//            public void onClick(final View paramView) {
//                Utils.dismissDialog(alertDialog);
//                App.getApplicationInstance().reLogin(LocatorTabs.this);
//            }
//        });
//        alertDialog.setNegativeButton(getResources().getString(R.string.no), new View.OnClickListener() {
//            @Override
//            public void onClick(final View paramView) {
//                Utils.dismissDialog(alertDialog);
//            }
//        });
//        Utils.showDialog(alertDialog, this);
//    }
//
//    private void setTabHost() {
//        tabHost = getTabHost();
//        tabHost.setup(getLocalActivityManager());
//
//        tabChangedListener = new OnTabChangeListener() {
//            @Override
//            public void onTabChanged(final String tabId) {
//                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//            }
//        };
//
//        final TextView leftTab = (TextView) getLayoutInflater().inflate(R.layout.locator_tab_left, tabHost.getTabWidget(), false);
//        leftTab.setText(getResources().getString(R.string.atm));
//        tabHost.addTab(tabHost.newTabSpec("ATM").setIndicator(leftTab).setContent(new Intent(this, LocatorFacilityList.class)));
//
//        final TextView middleTab = (TextView) getLayoutInflater().inflate(R.layout.locator_tab_middle, tabHost.getTabWidget(), false);
//        middleTab.setText(getString(R.string.branches_tab));
//        Intent branchesIntent = new Intent(this, LocatorFacilityList.class);
//        branchesIntent.putExtra(MiBancoConstants.LOCATOR_BRANCHES_KEY, true);
//        tabHost.addTab(tabHost.newTabSpec("BRANCHES").setIndicator(middleTab).setContent(branchesIntent));
//
//        final TextView rightTab = (TextView) getLayoutInflater().inflate(R.layout.locator_tab_right, tabHost.getTabWidget(), false);
//        rightTab.setText(getString(R.string.map));
//
//        final PackageManager pm = getPackageManager();
//        final List<PackageInfo> pi = pm.getInstalledPackages(0);
//        boolean mapsInstalled = false;
//        for (int k = 0; k < pi.size(); k++) {
//            final String text = pi.get(k).toString();
//            if (text.contains("google.android.apps.maps") || text.contains("google.android.maps")) {
//                mapsInstalled = true;
//                break;
//            }
//        }
//
//        if (mapsInstalled) {
//            Intent intent;
//            try {
//                intent = new Intent().setClass(this, LocatorMap.class);
//                intent.putExtra("from", getIntent().getIntExtra("from", -1));
//                tabHost.addTab(tabHost.newTabSpec("MAP").setIndicator(rightTab).setContent(intent));
//            } catch (final Exception e) {
//                Log.w("LocatorTabs", e);
//            }
//        } else {
//            Log.e("LocatorTabs", "Did not find required map packages. Map tab will not be available.");
//        }
//
//        tabHost.setCurrentTab(0);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        tabHost.setOnTabChangedListener(tabChangedListener);
//    }
//}
