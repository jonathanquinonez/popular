package com.popular.android.mibanco.task;

import android.content.Context;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.exception.BankException;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.ws.response.RetirementPlanInfoResponse;

public class RetirementPlanTasks {
    /**
     * Retirement plan async task abstract class template.
     * @param <T>
     */
    private static abstract class RetirementPlanTask<T> extends SessionAsyncTask {

        protected T response;

        RetirementPlanTask(Context context, ResponderListener listener) {
            super(context, listener, false);
        }

        protected abstract T doAsync() throws Exception;

        @Override
        protected Integer doInBackground(Object... params) {
            try {
                response = doAsync();
                return RESULT_SUCCESS;
            } catch (final Exception e) {
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
     * Retirement Plan info .
     */
    private static class RetirementPlanInfoTask extends RetirementPlanTask<RetirementPlanInfoResponse> {

        private static final String RETIREMENT_PLAN_TASK_LOG = "RetirementPlanTask"; // Class TAG used for logging.

        /**
         * Async task constructor.
         * @param context  the context
         * @param listener the listener
         */
        RetirementPlanInfoTask(final Context context,
                           final ResponderListener listener) {
            super(context, listener);

        }

        @Override
        protected RetirementPlanInfoResponse doAsync() throws Exception {
                response = App.getApplicationInstance().getApiClient().getRetirementPlanInfo();
            if (response != null && responderName != null && responderName.equalsIgnoreCase("login")) {
                throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
            }
            return response;
        }
    }

    /**
     * Instantiate Retirement Plan async task.
     * @param context
     * @param listener
     */
    public static void retirementPlanInfo(final Context context, final ResponderListener listener) {
        new RetirementPlanTasks.RetirementPlanInfoTask(context, listener).execute();
    }
}
