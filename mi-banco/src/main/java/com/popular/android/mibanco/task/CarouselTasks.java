package com.popular.android.mibanco.task;

import android.content.Context;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.exception.BankException;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.ws.response.BannerResponse;
public class CarouselTasks {

    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_FAILURE = 2;

    /**
     * Retirement plan async task abstract class template.
     * @param <T>
     */
    public static abstract class CarouselTask<T> extends SessionAsyncTask {

        protected T response;

        CarouselTask(Context context, ResponderListener listener) {
            super(context, listener, false);
        }

        protected abstract T doAsync() throws Exception;

        @Override
        public Integer doInBackground(Object... params) {
            try {
                response = doAsync();
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }

        @Override
        public void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                if(taskListener instanceof ResponderListener) {
                    ResponderListener responderListener = (ResponderListener) taskListener;
                    responderListener.responder(responderName, response);
                }
            }
        }

    }

    /**
     * Retirement Plan info .
     */
    public static class CarouselTasksInfo extends CarouselTask<BannerResponse> {

        private static final String RETIREMENT_PLAN_TASK_LOG = "RetirementPlanTask";
        private String segmentType;
        private String viewLocation;
        private String language;

        /**
         * Async task constructor.
         * @param context  the context
         * @param listener the listener
         * @param segmentType the segment type
         * @param viewLocation the view location
         * @param language the language
         */
        CarouselTasksInfo(final Context context,
                          final ResponderListener listener,
                          String segmentType,
                          String viewLocation,
                          String language) {
            super(context, listener);
            this.segmentType = segmentType;
            this.viewLocation = viewLocation;
            this.language = language;
        }

        @Override
        public BannerResponse doAsync() throws Exception {
            response = App.getApplicationInstance().getApiClient().getBannerCarousel(segmentType, viewLocation, language);
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
    public static void CarouselInfo(final Context context, final ResponderListener listener, String segmentType, String viewLocation, String language) {
        new CarouselTasks.CarouselTasksInfo(context, listener, segmentType, viewLocation, language).execute();
    }
}