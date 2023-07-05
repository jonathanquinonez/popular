package com.popular.android.mibanco.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.listener.TaskListener;
import com.popular.android.mibanco.util.FontChanger;
import com.popular.android.mibanco.util.Utils;

/**
 * Provides extended AsyncTask functionality. It's the base class for all the task which should run in the background and don't block the main thread.
 * All web service operations make use of CustomAsyncTask.
 */
public abstract class BaseAsyncTask extends AsyncTask<Object, String, Integer> implements OnCancelListener {

    /** Indicates failed task. */
    protected final static int RESULT_FAILURE = 0;

    /** Indicates successfully finished task. */
    protected final static int RESULT_SUCCESS = 1;

    /** The Activity context. */
    protected Context context;

    /** The progress dialog for the task. */
    protected Dialog progressDialog;

    /** The listener to notify about task finished. */
    protected TaskListener taskListener;

    /**
     * The exception that may occur during the task execution and terminates it.
     */
    protected Exception taskException;

    /** The responder name used for determining response status. */
    protected String responderName;

    /** Should we show a progress dialog during the task execution?. */
    protected boolean showProgress;

    /** Duration if there's a delay. */
    protected long expectedDuration;

    /** Should we add a delay to progress dialog?. */
    protected boolean addDelay;

    /** Takes the task start time to be deducted from the duration */
    protected long startTime;

    /**
     * Instantiates a new CustomAsyncTask with a progress dialog and the standard message.
     * 
     * @param context the context
     * @param listener the listener
     */
    public BaseAsyncTask(final Context context, final TaskListener listener) {
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("An Activity context is required.");
        }
        this.context = context;
        ((App) ((Activity) context).getApplication()).setRunningTask(this);
        taskListener = listener;
        showProgress = true;
        try {
            progressDialog = setupProgressDialog();
        } catch (final Exception ex) {

            Log.w("CustomAsyncTask", ex);
        }
    }

    /**
     * Instantiates a new CustomAsyncTask with the standard progress message.
     * 
     * @param context the context
     * @param listener the listener
     * @param showProgress show progress indicator?
     */
    public BaseAsyncTask(final Context context, final TaskListener listener, final boolean showProgress) {
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("An Activity context is required.");
        }
        this.context = context;
        ((App) ((Activity) context).getApplication()).setRunningTask(this);
        taskListener = listener;
        this.showProgress = showProgress;
        if (showProgress) {
            progressDialog = setupProgressDialog();
        }
    }

    /**
     * Instantiates a new CustomAsyncTask with a custom progress message.
     * 
     * @param context the context
     * @param listener the listener
     * @param showProgress show progress indicator?
     * @param progressMessage message to display in progress dialog
     */
    public BaseAsyncTask(final Context context, final TaskListener listener, final boolean showProgress, final String progressMessage) {
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("An Activity context is required.");
        }
        this.context = context;
        ((App) ((Activity) context).getApplication()).setRunningTask(this);
        taskListener = listener;
        this.showProgress = showProgress;
        if (showProgress) {
            progressDialog = setupProgressDialog();
            ((ProgressDialog) progressDialog).setMessage(progressMessage);
        }
    }

    /**
     * Instantiates a new CustomAsyncTask with the standard progress message.
     *
     * @param context the context
     * @param listener the listener
     * @param showProgress show progress indicator?
     * @param progressMessage show progress message
     * @param duration add duration to the delay
     */
    public BaseAsyncTask(final Context context, final TaskListener listener, final boolean showProgress, final String progressMessage, final long duration) {
        this(context, listener, showProgress, progressMessage);
        this.addDelay = true;
        this.expectedDuration = duration;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setListener(TaskListener listener) {
        taskListener = listener;
    }


    @Override
    public void onCancel(final DialogInterface dialog) {
        cancel(true);
    }


    @Override
    protected void onCancelled() {
        cleanUp();
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(final Integer result) {
        if (taskException != null) {
            TaskExceptionHandler.handleTaskException(this, context, taskException, taskListener);
        }
        //if there's a delay, the dialog dismiss will be managed where you manage the delay
        if(!addDelay)
            cleanUp();
        super.onPostExecute(result);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (showProgress) {
            if (Utils.showDialog(progressDialog, context)) {
                FontChanger.changeFonts(progressDialog.findViewById(android.R.id.content));
            }
        }
    }

    /**
     * Makes sure progress dialog gets dismissed and context cleared when task finishes.
     */
    protected void cleanUp() {
        if (progressDialog != null) {
            try {
                progressDialog.dismiss();
            } catch (final Exception e) {
                Log.w("CustomAsyncTask", e);
            }
        }
        progressDialog = null;
        context = null;
    }

    /**
     * Setups progress dialog.
     * 
     * @return the progress dialog
     */
    private ProgressDialog setupProgressDialog() {
        Utils.setupLanguage(context);
        final ProgressDialog progressDialog = new ProgressDialog(context,  AlertDialog.THEME_HOLO_LIGHT);
        progressDialog.setCancelable(false);
        progressDialog.setOnCancelListener(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(context.getResources().getString(R.string.please_wait));

        return progressDialog;
    }
}
