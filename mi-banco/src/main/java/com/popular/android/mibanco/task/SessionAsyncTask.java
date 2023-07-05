package com.popular.android.mibanco.task;

import android.content.Context;
import android.content.DialogInterface;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.exception.BankException;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.listener.TaskListener;

/**
 * Base class for tasks requiring active session for a successful execution.
 */
public abstract class SessionAsyncTask extends BaseAsyncTask {

    public SessionAsyncTask(final Context context, final TaskListener listener) {
        super(context, listener);
    }

    public SessionAsyncTask(final Context context, final TaskListener listener, final boolean showProgress) {
        super(context, listener, showProgress);
    }

    public SessionAsyncTask(final Context context, final TaskListener listener, final boolean showProgress, final String progressMessage) {
        super(context, listener, showProgress, progressMessage);
    }

    public SessionAsyncTask(final Context context, final TaskListener listener, final boolean showProgress, final String message, final long duration) {
        super(context, listener, showProgress, message, duration);
    }

    @Override
    public void onCancel(final DialogInterface dialog) {
        cancel(true);
        super.onCancelled();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if ((App.getApplicationInstance()!= null
                && App.getApplicationInstance().getAsyncTasksManager() != null
                && (App.getApplicationInstance().getLastUserInteractionTime() == MiBancoConstants.NO_LAST_USER_INTERACTION_AVAILABLE
                || App.getApplicationInstance().getLastUserInteractionTime() + MiBancoConstants.USER_INTERACTION_TIMEOUT_MILLIS < System.currentTimeMillis()))
                && App.getApplicationInstance().isSessionNeeded()) {

            App.getApplicationInstance().getAsyncTasksManager().sessionPing(context, new ResponderListener() {

                @Override
                public void responder(final String responderName, final Object data) {
                }

                @Override
                public void sessionHasExpired() {
                    taskException = new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
                }
            });
        }
    }
}
