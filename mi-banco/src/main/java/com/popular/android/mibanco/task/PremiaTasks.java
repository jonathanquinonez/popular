package com.popular.android.mibanco.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.model.PremiaInfo;
import com.popular.android.mibanco.util.PremiaUtils;

import java.io.IOException;

/**
 * Premia related async tasks.
 */
public class PremiaTasks {

    private static final String PREMIA_REDIRECT_TASK_TAG = "PremiaRedirectTask"; // Class TAG used for logging.

    /**
     * Premia async task abstract class template.
     * @param <T>
     */
    private static abstract class PremiaTask<T> extends SessionAsyncTask {

        protected T response;

        PremiaTask(Context context, ResponderListener listener) {
            super(context, listener, true);
        }

        PremiaTask(Context context, ResponderListener listener, boolean progress) {
            super(context, listener, progress);
        }
        protected abstract T doAsync() throws Exception;

        @Override
        protected Integer doInBackground(Object... params) {
            try {
                response = doAsync();
                return RESULT_SUCCESS;
            } catch (final IOException e) {
                taskException = e;
                return RESULT_FAILURE;
            } catch (Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                if(taskListener instanceof ResponderListener) {
                    ResponderListener responderListener = (ResponderListener) taskListener; // ResponderListener.
                    responderListener.responder(responderName, response);
                }
            }
        }

    }

    /**
     * Premia catalog redirect async task.
     */
    private static class PremiaRedirectTask extends PremiaTask<String> {

        private ProgressDialog progressDialog;
        private String frontEndId;

        /**
         * Async task constructor.
         * @param context  the context
         * @param frontEndId the frontEndId
         * @param listener the listener
         */
        PremiaRedirectTask(final Context context,
                                  final String frontEndId,
                                  final ResponderListener listener) {
            super(context, listener);
            this.frontEndId = frontEndId;
            this.progressDialog = PremiaUtils.openPremiaLoadingDialog(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog.show();
        }

        @Override
        protected String doAsync() throws IOException {

            Thread.currentThread().setName("PremiaRedirectTask");

            try {
                // Pause it for 4 seconds in order to allow the end user to read the redirect message.
                Thread.sleep(4000);
                response = App.getApplicationInstance().getApiClient().getTsysLoyaltyRewardsRedirectURL(frontEndId);
            } catch (InterruptedException e) {
                Log.e(PREMIA_REDIRECT_TASK_TAG, "Error while pausing async task before calling ApiClient.");
            }

            return response;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }
        }
    }

    /**
     * Premia catalog redirect async task.
     */
    private static class PremiaBalanceTask extends PremiaTask<String> {

        private static final String PREMIA_BALANCE_TASK_TAG = "PremiaBalanceTask"; // Class TAG used for logging.


        /**
         * Async task constructor.
         * @param context  the context
         * @param listener the listener
         */
        PremiaBalanceTask(final Context context,
                           final ResponderListener listener) {
            super(context, listener, false);
        }

        @Override
        protected String doAsync()  {
            PremiaInfo responseInfo = new PremiaInfo();
            Thread.currentThread().setName("PremiaBalanceTask");
            try {
                responseInfo = App.getApplicationInstance().getApiClient().getPremiaInfo();
                Thread.sleep(4000);
            } catch (Exception e) {
                Log.e(PremiaBalanceTask.PREMIA_BALANCE_TASK_TAG, "Error while pausing async task before calling ApiClient.");
                Log.e(PremiaBalanceTask.PREMIA_BALANCE_TASK_TAG, e.getMessage());
            }

            if(responseInfo.getTsysBalanceRewards() != null && !responseInfo.getTsysBalanceRewards().isEmpty()) {
                response = responseInfo.getTsysBalanceRewards();
            } else if(responseInfo.getError() != null && !responseInfo.getError().isEmpty()) {
                response = responseInfo.getError();
            } else {
                response = "N/A";
            }
            return response;
        }

    }

    /**
     * Instantiate Premia Catalog async task.
     * @param context
     * @param frontEndId
     * @param listener
     */
    public static void premiaCatalogRedirect(final Context context, final String frontEndId, final ResponderListener listener) {
        new PremiaRedirectTask(context, frontEndId, listener).execute();
    }

    /**
     * Instantiate Premia Catalog async task.
     * @param context
     * @param listener
     */
    public static void premiaAccountBalance(final Context context, final ResponderListener listener) {
        new PremiaBalanceTask(context, listener).execute();
    }

}

