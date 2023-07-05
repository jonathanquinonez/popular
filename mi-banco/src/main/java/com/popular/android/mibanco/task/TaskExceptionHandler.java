package com.popular.android.mibanco.task;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.activity.EnterUsername;
import com.popular.android.mibanco.activity.ErrorView;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.exception.AccessBlockedException;
import com.popular.android.mibanco.exception.AthmException;
import com.popular.android.mibanco.exception.BankException;
import com.popular.android.mibanco.exception.MaintenanceMobileException;
import com.popular.android.mibanco.exception.MaintenancePersonalException;
import com.popular.android.mibanco.exception.NotAvailableException;
import com.popular.android.mibanco.exception.UpdateRequiredException;
import com.popular.android.mibanco.listener.TaskListener;
import com.popular.android.mibanco.task.AsyncTasks.PasswordTask;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.AlertDialogFragment;

//import com.popular.android.mibanco.activity.LocatorFacilityDetails;

public class TaskExceptionHandler {

    private TaskExceptionHandler() {
    }

    public static void handleTaskException(final BaseAsyncTask task, final Context context, final Exception taskException, final TaskListener taskListener) {
        // Log with Flurry Analytics
        if (taskException.getClass().equals(java.net.SocketTimeoutException.class)) {
            // time-out
            if (task instanceof AsyncTasks.LoginTask) {
                BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_TIMED_OUT_USERNAME);
            } else if (task instanceof AsyncTasks.QuestionTask) {
                BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_TIMED_OUT_CHALLENGE);
            } else if (task instanceof AsyncTasks.PasswordTask) {
                BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_TIMED_OUT_PASSWORD);
            } else if (task instanceof AsyncTasks.FetchPaymentsTask) {
                BPAnalytics.logEvent("kAuthProcessTimedOutFetchPayments");
                return;
            } else if (task instanceof AsyncTasks.FetchTransfersTask) {
                BPAnalytics.logEvent("kAuthProcessTimedOutFetchTransfers");
                return;
            }
        } else if (taskException.getClass().getCanonicalName().contains("java.net.") || taskException.getClass().getCanonicalName().contains("javax.net.")
                || taskException.getClass().getCanonicalName().contains("org.apache.http.conn") || taskException.getClass().equals(java.io.IOException.class)
                && taskException.getMessage().contains("SSL")) {
            // connectivity error
            if (task instanceof AsyncTasks.LoginTask) {
                BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_CONNECTIVITY_ERROR_USERNAME);
            } else if (task instanceof AsyncTasks.QuestionTask) {
                BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_CONNECTIVITY_ERROR_CHALLENGE);
            } else if (task instanceof AsyncTasks.PasswordTask) {
                BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_CONNECTIVITY_ERROR_PASSWORD);
            }
        } else if (taskException.getClass().getCanonicalName().contains("com.google.gson.") && task instanceof AsyncTasks.PasswordTask) {
            // JSON exception on Password screen
            BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_JSON_ERROR_PASSWORD);
        } else if (!(taskException instanceof BankException)) {
            // unknown error
            if (task instanceof AsyncTasks.LoginTask) {
                BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_UNKNOWN_ERROR_USERNAME);
            } else if (task instanceof AsyncTasks.QuestionTask) {
                BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_UNKNOWN_ERROR_CHALLENGE);
            } else if (task instanceof PasswordTask) {
                BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_UNKNOWN_ERROR_PASSWORD);
            }
        }

        // Connectivity exceptions first
        if (taskException.getClass().getCanonicalName().contains("java.net.") || taskException.getClass().getCanonicalName().contains("javax.net.")
                || taskException.getClass().getCanonicalName().contains("org.apache.http.conn")) {
            final ConnectivityManager connectionManager = (ConnectivityManager) App.getApplicationInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo activeNetwork = connectionManager.getActiveNetworkInfo();

            if (activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED) {
                if (context instanceof EnterUsername || context instanceof ErrorView) {
                    Utils.showMaintenanceWithBalances(MiBancoConstants.HIGH_VOLUME, context);
                } else {
                    Utils.showNoConnectionError(context, null);
                }
            } else {
                if (context instanceof EnterUsername || context instanceof ErrorView) {
                    Utils.showNoConnectionDuringLoginError(context, context.getString(R.string.check_connection));
                } else {
                    Utils.showNoConnectionError(context, context.getString(R.string.check_connection));
                }
            }
        } else if (taskException.getClass().equals(java.io.IOException.class) && taskException.getMessage().contains("SSL")) {
            Utils.showNoConnectionError(context, context.getString(R.string.no_connection_ssl));
        } else if (taskException.getClass().equals(UpdateRequiredException.class)) {
            Utils.showUpdateRequiredError(context, null);
        } else if (taskException.getClass().equals(MaintenanceMobileException.class)) {
            Utils.showMaintenanceWithBalances(MiBancoConstants.MAINTENANCE, context);
        } else if (taskException.getClass().equals(MaintenancePersonalException.class)) {
            Utils.showMaintenanceWithBalances(MiBancoConstants.MAINTENANCE, context);
        } else if (taskException.getClass().equals(AccessBlockedException.class)) {
            if (((AccessBlockedException) taskException).isExitToLogin()) {
                Utils.showAccountBlockedErrorAndExit(context, taskException.getMessage());
            }else {
                Utils.showAccountBlockedError(context, null);
            }
        } else if (taskException.getClass().equals(NotAvailableException.class)) {
            Utils.showNotAvailableError(context, context.getString(R.string.not_available_description));
        } else if (taskException.getClass().getCanonicalName().contains("com.google.gson.")) {
            Utils.showMaintenanceWithBalances(MiBancoConstants.MAINTENANCE, context);
        } else if (taskException.getClass().equals(BankException.class)) {
            Utils.showAlertType(context, taskException, taskListener);
        } else if (taskException instanceof AthmException) {
            AthmException athmException = (AthmException) taskException;
            if (athmException.getResponse().isDowntime()) {
                AlertDialogFragment.showAlertDialog((BaseActivity) context, null, R.string.athm_down_error_text, R.string.ok, null, MiBancoConstants.MiBancoDialogId.ATHM_DOWNTIME, null, false);
            } else if (athmException.getResponse().isBlocked()) {
                AlertDialogFragment.showAlertDialog((BaseActivity) context, R.string.athm_blocked_error_title, R.string.athm_blocked_error_text, R.string.ok, null, MiBancoConstants.MiBancoDialogId.ATHM_BLOCKED, null, false);
            }
        } else {
            Utils.showGenericError(context, null);
        }
    }
}
