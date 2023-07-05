package com.popular.android.mibanco.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.IntroScreen;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.activity.Accounts;
import com.popular.android.mibanco.activity.Contact;
import com.popular.android.mibanco.activity.EasyCashStaging;
import com.popular.android.mibanco.activity.EnterPassword;
import com.popular.android.mibanco.activity.EnterUsername;
import com.popular.android.mibanco.activity.ErrorView;
import com.popular.android.mibanco.activity.Payments;
import com.popular.android.mibanco.activity.SecurityQuestion;
import com.popular.android.mibanco.activity.SettingsList;
import com.popular.android.mibanco.util.BaseActivityHelper;
import com.popular.android.mibanco.util.FontChanger;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.AlertDialogFragment;
import com.popular.android.mibanco.view.DialogHolo;

//import com.popular.android.mibanco.activity.LocatorTabs;

/**
 * The base class for all regular FragmentActivities. Provides extended back button press functionality, re-login and restart actions. Constitutes the
 * main point for global font change and image resources release.
 */
@SuppressLint("NewApi")
public abstract class BaseActivity extends AppCompatActivity implements AlertDialogFragment.AlertDialogListener {

    public final static String KILL_ACTION = "killall";
    public final static String KILL_TYPE = "content://com.popular.android.mibanco";

    protected App application;
    private KillReceiver mKillReceiver;
    private BaseActivityHelper baseActivityHelper = new BaseActivityHelper();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mKillReceiver = new KillReceiver();
        registerReceiver(mKillReceiver, IntentFilter.create(KILL_ACTION, KILL_TYPE));

        application = App.getApplicationInstance();
        Utils.setupLanguage(this);
    }


    private final class KillReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            finish();
        }
    }




    public BaseActivityHelper getBaseActivityHelper() {
        return baseActivityHelper;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        final App application = (App) getApplication();
        Activity parent = getParent();
        Class<?> parentClass = null;
        if (parent != null) {
            parentClass = parent.getClass();
        }
        Class<?> thisClass = getClass();

        if (this instanceof Accounts || this instanceof Payments ) {
            showLogoutDialog();
        } else if (this instanceof EasyCashStaging) {
            // Reset values
            this.finish();
        } else if (thisClass.equals(SecurityQuestion.class) || thisClass.equals(EnterPassword.class)) {
            application.reLogin(this);
        } else if (parent != null && parentClass.equals(ErrorView.class) || thisClass.equals(ErrorView.class)) {
            application.reLogin(this);
        } else {
            finish();
        }
    }

    public void errorReload()
    {
        final Intent iIntro = new Intent(this, IntroScreen.class);
        iIntro.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(iIntro);
        finish();
    }

    public void showLogoutDialog() {
        final DialogHolo alertDialog = new DialogHolo(this);
        alertDialog.setTitle(getResources().getString(R.string.title_logout_app));
        alertDialog.setMessage(getResources().getString(R.string.logout_app));
        alertDialog.setPositiveButton(getResources().getString(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(final View paramView) {
                Utils.dismissDialog(alertDialog);
                application.reLogin(BaseActivity.this);
            }
        });
        alertDialog.setNegativeButton(getResources().getString(R.string.no), new View.OnClickListener() {
            @Override
            public void onClick(final View paramView) {
                Utils.dismissDialog(alertDialog);
            }
        });
        Utils.showDialog(alertDialog, this);
    }



    public void setContentView(int layoutResID, boolean toolbar) {
        if (!toolbar) {
            super.setContentView(layoutResID);
            return;
        }

        ViewGroup container = (ViewGroup) getLayoutInflater().inflate(R.layout.toolbar_container, null, false);
        setupToolbar(container);

        ViewGroup contentView = (ViewGroup) getLayoutInflater().inflate(layoutResID, container, false);
        container.addView(contentView);

        super.setContentView(container);
    }

    @Override
    public void setContentView(int layoutResID) {
        if (this instanceof ErrorView) {
            super.setContentView(layoutResID);
            return;
        }

        ViewGroup container = (ViewGroup) getLayoutInflater().inflate(R.layout.toolbar_container, null, false);
        setupToolbar(container);

        ViewGroup contentView = (ViewGroup) getLayoutInflater().inflate(layoutResID, container, false);
        container.addView(contentView);

        super.setContentView(container);
    }

    public void setupToolbar(ViewGroup container) {
        Toolbar toolbar = (Toolbar) container.findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        }
        View customView = LayoutInflater.from(this).inflate(R.layout.toolbar_navigation, null);

        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setCustomView(customView);

    }

    public void setContentViewNoNavigation(int layoutResID, boolean toolbar) {
        if (!toolbar) {
            super.setContentView(layoutResID);
            return;
        }

        ViewGroup container = (ViewGroup) getLayoutInflater().inflate(R.layout.toolbar_container, null, false);
        setupToolbarNoNavigation(container);

        ViewGroup contentView = (ViewGroup) getLayoutInflater().inflate(layoutResID, container, false);
        container.addView(contentView);

        super.setContentView(container);
    }

    public void setupToolbarNoNavigation(ViewGroup container) {
        Toolbar toolbar = (Toolbar) container.findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        }
        View customView = LayoutInflater.from(this).inflate(R.layout.toolbar_no_back, null);
        getSupportActionBar().setCustomView(customView);

    }


    @Override
    protected void onDestroy() {
        baseActivityHelper.cancelStackedTasks();

        try {
            unregisterReceiver(mKillReceiver);
        } catch (final Exception ex) {
            Log.i("UnregisterReceiver", "Error unregistering receiver.", ex);
        }

        // release image resources
        Utils.unbindDrawables(getWindow().getDecorView().getRootView());
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FontChanger.changeFonts(getWindow().getDecorView().getRootView());
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        Utils.setupLanguage(this);

        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        if (this instanceof EnterUsername) {
            onPrepareOptionsMenu(menu);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.menu_logout).setVisible(false);
        if (App.getApplicationInstance().isLoginSSDSForced()) {
            menu.findItem(R.id.menu_settings).setVisible(false);
            menu.findItem(R.id.menu_locator).setVisible(false);
            menu.findItem(R.id.menu_contact).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_overflow:
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        final Instrumentation instrumentation = new Instrumentation();
                        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
                    }
                }).start();
                break;
            case R.id.menu_locator:
                Utils.openExternalUrl(this, getString(R.string.locator_url));
                break;
            case R.id.menu_contact:
                final Intent iContact = new Intent(this, Contact.class);
                startActivity(iContact);
                break;
            case R.id.menu_settings:
                final Intent iSettings = new Intent(this, SettingsList.class);
                startActivity(iSettings);
                break;
            case R.id.menu_logout:
                showLogoutDialog();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onPositiveClick(final DialogFragment dialog, final int dialogId, final Bundle data) {
        switch (dialogId) {
            case MiBancoConstants.MiBancoDialogId.CONFIRMATION:
            case MiBancoConstants.MiBancoDialogId.ERROR:
                dialog.dismiss();
                break;
            case MiBancoConstants.MiBancoDialogId.CONFIRMATION_BACK:
                dialog.dismiss();
                onBackPressed();
                break;
            case MiBancoConstants.MiBancoDialogId.ERROR_FINISH:
                dialog.dismiss();
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onNegativeClick(final DialogFragment dialog, final int dialogId, final Bundle data) {
        switch (dialogId) {
            default:
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void onDialogCancel(final DialogFragment dialog, final int dialogId, final Bundle dataBundle) {
        if (dialogId == MiBancoConstants.MiBancoDialogId.ERROR_FINISH) {
            dialog.dismiss();
            onBackPressed();
        }
    }

    public void cashRewardsConfirmationToolBar() {
        findViewById(R.id.popular_logo).setVisibility(View.GONE);
        findViewById(R.id.edit_cash_rewards).setVisibility(View.VISIBLE);
        findViewById(R.id.redemption_result_title).setVisibility(View.VISIBLE);

    }
    public void cashRewardsResultToolBar() {
        findViewById(R.id.popular_logo).setVisibility(View.VISIBLE);
        findViewById(R.id.up).setVisibility(View.GONE);
        findViewById(R.id.redemption_result_title).setVisibility(View.GONE);


    }

    public void cashRewardsAutomaticRedemptionToolBar() {
        findViewById(R.id.popular_logo).setVisibility(View.GONE);
        findViewById(R.id.close_cash_rewards).setVisibility(View.VISIBLE);
    }


}
