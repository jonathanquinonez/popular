package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoPreferences;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.PermissionsManagerUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.util.enums.RegainAccessTypeEnum;
import com.popular.android.mibanco.view.DialogHolo;

import java.util.List;

/**
 * Activity used for displaying error messages and allowing user to react for certain types of errors. Pressing back button on this screen moves user
 * back to the login screen.
 */
public class ErrorView extends BaseActivity {

    /** Account blocked error code. */
    public static final int ERROR_ACCOUNT_BLOCKED = 1;

    /** Generic error code. */
    public static final int ERROR_GENERIC = 6;

    /** Maintenance error code. */
    public static final int ERROR_MAINTENANCE = 3;

    /** No connection error code. */
    public static final int ERROR_NO_CONNECTION = 2;

    /** Not available error code. */
    public static final int ERROR_NOT_AVAILABLE = 4;

    /** Update required error code. */
    public static final int ERROR_UPDATE_REQUIRED = 5;

    /** No connection error code thrown in Login Activity. */
    public static final int ERROR_NO_CONNECTION_LOGIN = 7;

    /** Refused connection error code thrown in Login Activity. */
    public static final int ERROR_REFUSED_CONNECTION_LOGIN = 8; //connection refused

    private static int openedErrors = 0;

    private Button confirmButton;

    private LinearLayout confirmButtonLayout;

    /** The error code. */
    private int errorCode;

    private android.view.View.OnClickListener listenerConfirm;

    private android.view.View.OnClickListener listenerNegative;

    private android.view.View.OnClickListener listenerPositive;

    private Button negativeButton;

    private Button positiveButton;

    private LinearLayout positiveNegativeButtonsLayout;

    private boolean activeWindow;
    private Context mContext = this;
    private Intent permissionsCallIntent;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ++openedErrors;
        if (openedErrors > 1) {
            --openedErrors;
            if (openedErrors < 0) {
                openedErrors = 0;
            }

            activeWindow = false;
            finish();
            return;
        }
        activeWindow = true;

        setContentView(R.layout.error_view);

        TextView descriptionTextView = (TextView) findViewById(R.id.description);
        positiveNegativeButtonsLayout = (LinearLayout) findViewById(R.id.positiveNegativeButtons);
        confirmButtonLayout = (LinearLayout) findViewById(R.id.confirmationButton);
        positiveButton = (Button) findViewById(R.id.buttonPositive);
        negativeButton = (Button) findViewById(R.id.buttonNegative);
        confirmButton = (Button) findViewById(R.id.buttonConfirm);

        errorCode = getIntent().getIntExtra("errorCode", ERROR_GENERIC);
        String errorMessage = getIntent().getStringExtra("errorMessage");

        switch (errorCode) {
        case ERROR_ACCOUNT_BLOCKED: {
            if (MiBancoPreferences.getMiBancoFlagMbsfe291()) {
                // MBSFE-291 Modificar proceso de recuperar acceso - Android
                final Intent intentForgotUsername = new Intent(ErrorView.this, WebViewActivity.class);
                intentForgotUsername.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.regain_access_url, RegainAccessTypeEnum.BLOCKED, App.getApplicationInstance().getLanguage())));
                String[] urlBlacklist = getResources().getStringArray(R.array.web_view_url_blacklist);
                for (int i = 0; i < urlBlacklist.length; ++i) {
                    urlBlacklist[i] = Utils.getAbsoluteUrl(urlBlacklist[i]);
                }
                intentForgotUsername.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, urlBlacklist);
                intentForgotUsername.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
                startActivity(intentForgotUsername);
                finish();
            } else {
                final DialogHolo actionDialog = new DialogHolo(ErrorView.this);
                actionDialog.setTitle(getString(R.string.account_blocked_title).toUpperCase());
                final View layout = actionDialog.setCustomContentView(R.layout.error_blocked_action_menu);
                actionDialog.setCancelable(true);
                actionDialog.setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {

                    @Override
                    public void onClick(final View paramView) {
                        Utils.dismissDialog(actionDialog);
                    }
                });
                actionDialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {

                    @Override
                    public void onClick(final View paramView) {
                        if (((RadioButton) layout.findViewById(R.id.radio_url)).isChecked()) {
                            Utils.dismissDialog(actionDialog);
                            final Intent intentForgotUsername = new Intent(ErrorView.this, WebViewActivity.class);
                            //intentForgotUsername.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY,
                              //      Utils.getAbsoluteUrl(getString(R.string.regain_access_url)) + App.getApplicationInstance().getLanguage());
                            intentForgotUsername.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY,
                                    Utils.getAbsoluteUrl(getString(R.string.regain_access_url,
                                            RegainAccessTypeEnum.BLOCKED, App.getApplicationInstance().getLanguage())));
                            String[] urlBlacklist = getResources().getStringArray(R.array.web_view_url_blacklist);
                            for (int i = 0; i < urlBlacklist.length; ++i) {
                                urlBlacklist[i] = Utils.getAbsoluteUrl(urlBlacklist[i]);
                            }
                            intentForgotUsername.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, urlBlacklist);
                            intentForgotUsername.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
                            startActivity(intentForgotUsername);
                        } else if (((RadioButton) layout.findViewById(R.id.radio_call_free)).isChecked()) {
                            Utils.dismissDialog(actionDialog);
                            final Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + getString(R.string.unblock_toll_free_phone_number)));

                            List<String> missingPermissions = PermissionsManagerUtils.missingPermissions(mContext);
                            if(missingPermissions.size() == 0) {
                                ErrorView.this.startActivity(callIntent);
                            }else{
                                permissionsCallIntent = callIntent;
                                PermissionsManagerUtils.askForPermission(mContext,missingPermissions, MiBancoConstants.REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        } else {
                            Utils.dismissDialog(actionDialog);
                            final Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + getString(R.string.unblock_local_phone_number)));
                            List<String> missingPermissions = PermissionsManagerUtils.missingPermissions(mContext);
                            if(missingPermissions.size() == 0) {
                                ErrorView.this.startActivity(callIntent);
                            }else{
                                permissionsCallIntent = callIntent;
                                PermissionsManagerUtils.askForPermission(mContext,missingPermissions, MiBancoConstants.REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        }
                        finish();
                    }
                });

                ((TextView) layout.findViewById(R.id.message)).setText(getString(R.string.account_blocked_actionsheet_title));

                final RadioButton radioButtonUrl = (RadioButton) layout.findViewById(R.id.radio_url);
                radioButtonUrl.setText(getString(R.string.account_blocked_regain_access_action));

                final RadioButton buttonCallLocal = (RadioButton) layout.findViewById(R.id.radio_call_local);
                final RadioButton buttonCallFree = (RadioButton) layout.findViewById(R.id.radio_call_free);
                final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                if (telephonyManager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE) {
                    buttonCallLocal.setText(String.format(getString(R.string.account_blocked_callto_action), getString(R.string.unblock_local_phone_number)));
                    buttonCallFree.setText(String.format(getString(R.string.account_blocked_callto_action), getString(R.string.unblock_toll_free_phone_number)));
                    buttonCallLocal.setVisibility(View.VISIBLE);
                    buttonCallFree.setVisibility(View.VISIBLE);
                }

                descriptionTextView.setText(getString(R.string.account_blocked_description));
                setPositiveNegativeButtonsMode();
                setPositiveButton(getString(R.string.account_blocked_button_action), new OnClickListener() {

                    @Override
                    public void onClick(final View v) {
                        Utils.showDialog(actionDialog, ErrorView.this);
                    }
                });
                setNegativeButton(getString(R.string.cancel), new OnClickListener() {

                    @Override
                    public void onClick(final View v) {
                        onBackPressed();
                    }
                });
            }

            break;
        }
        case ERROR_NO_CONNECTION: {
            if (errorMessage != null) {
                descriptionTextView.setText(errorMessage);
            } else {
                descriptionTextView.setText(getString(R.string.no_connection_description));
            }
            setConfirmationButtonMode();
            setConfirmationButton(getString(R.string.ok).toUpperCase(), new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    onBackPressed();
                }
            });

            break;
        }
        case ERROR_NO_CONNECTION_LOGIN: {
            if (errorMessage != null) {
                descriptionTextView.setText(errorMessage);
            } else {
                descriptionTextView.setText(getString(R.string.no_connection_description));
            }
            setConfirmationButtonMode();
            setConfirmationButton(getString(R.string.ok).toUpperCase(), new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    onBackPressed();
                }
            });

            break;
        }
        case ERROR_REFUSED_CONNECTION_LOGIN: {
            if (errorMessage != null) {
                descriptionTextView.setText(errorMessage);
            } else {
                descriptionTextView.setText(getString(R.string.no_connection_description));
            }
            setConfirmationButtonMode();
            setConfirmationButton(getString(R.string.ok).toUpperCase(), new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    onBackPressed();
                }
            });

            break;
        }
        case ERROR_MAINTENANCE: {
            if (errorMessage != null) {
                descriptionTextView.setText(errorMessage);
            } else {
                descriptionTextView.setText(getString(R.string.maintenance_title));
            }
            setConfirmationButtonMode();
            setConfirmationButton(getString(R.string.title_exit), new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    onBackPressed();
                }
            });

            break;
        }
        case ERROR_NOT_AVAILABLE: {
            descriptionTextView.setText(getString(R.string.not_available_description));
            setPositiveNegativeButtonsMode();
            setPositiveButton(getString(R.string.not_available_open_full_site), new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    Utils.openExternalUrl(ErrorView.this, getString(R.string.not_available_url));
                }
            });
            setNegativeButton(getString(R.string.title_exit), new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    onBackPressed();
                }
            });

            break;
        }
        case ERROR_UPDATE_REQUIRED: {
            descriptionTextView.setText(getString(R.string.update_description));
            setPositiveNegativeButtonsMode();
            setPositiveButton(getString(R.string.update), new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    Utils.openExternalUrl(ErrorView.this, getString(R.string.market_base_url) + ErrorView.this.getClass().getPackage().getName());
                    onBackPressed();
                }
            });
            setNegativeButton(getString(R.string.title_exit), new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    onBackPressed();
                }
            });

            break;
        }
        case ERROR_GENERIC: {
            descriptionTextView.setText(errorMessage != null ? errorMessage : getString(R.string.error_occurred));

            setConfirmationButtonMode();
            setConfirmationButton(getString(R.string.title_exit), new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    onBackPressed();
                }
            });

            break;
        }
        default:
            break;
        }

        setListeners();
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
    public void onBackPressed() {
        --openedErrors;
        if (openedErrors < 0) {
            openedErrors = 0;
        }
        finish();
        if (activeWindow && errorCode != ERROR_NO_CONNECTION_LOGIN && errorCode != ERROR_NO_CONNECTION) {
            activeWindow = false;
            if (errorCode == ERROR_MAINTENANCE || errorCode == ERROR_NOT_AVAILABLE || errorCode == ERROR_UPDATE_REQUIRED) {
                exitApplication();
            } else if (errorCode != ERROR_ACCOUNT_BLOCKED) {
                application.reLogin(ErrorView.this);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (activeWindow) {
            onBackPressed();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MiBancoConstants.REQUEST_CODE_ASK_PERMISSIONS:
                if(PermissionsManagerUtils.isFunctionalityAllowed(this, permissions, grantResults,
                        R.string.permission_mandatory, null) && permissionsCallIntent != null){
                    ErrorView.this.startActivity(permissionsCallIntent);
                }
                break;
            default:
                break;
        }
    }

    public void setConfirmationButton(final String buttonLabel, final View.OnClickListener clickListener) {
        confirmButton.setText(buttonLabel);
        listenerConfirm = clickListener;
        setConfirmationButtonMode();
    }

    private void setConfirmationButtonMode() {
        positiveNegativeButtonsLayout.setVisibility(View.GONE);
        confirmButtonLayout.setVisibility(View.VISIBLE);
    }

    private void setListeners() {
        positiveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                if (listenerPositive != null) {
                    listenerPositive.onClick(positiveButton);
                }
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                if (listenerNegative != null) {
                    listenerNegative.onClick(negativeButton);
                }
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                if (listenerConfirm != null) {
                    listenerConfirm.onClick(confirmButton);
                }
            }
        });
    }

    public void setNegativeButton(final String buttonLabel, final View.OnClickListener clickListener) {
        negativeButton.setText(buttonLabel);
        listenerNegative = clickListener;
        setPositiveNegativeButtonsMode();
    }

    public void setPositiveButton(final String buttonLabel, final View.OnClickListener clickListener) {
        positiveButton.setText(buttonLabel);
        listenerPositive = clickListener;
        setPositiveNegativeButtonsMode();
    }

    private void setPositiveNegativeButtonsMode() {
        positiveNegativeButtonsLayout.setVisibility(View.VISIBLE);
        confirmButtonLayout.setVisibility(View.GONE);
    }

    private void exitApplication() {
        final Intent kIntent = new Intent(MiBancoConstants.KILL_ACTION);
        kIntent.setType(MiBancoConstants.KILL_TYPE);
        sendBroadcast(kIntent);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(false);
        menu.findItem(R.id.menu_contact).setVisible(false);

        return true;
    }
}
