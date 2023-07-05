package com.popular.android.mibanco.task;

import android.content.Context;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.exception.BankException;
import com.popular.android.mibanco.listener.TaskListener;
import com.popular.android.mibanco.ws.response.EasyCashTrx;
import com.popular.android.mibanco.ws.response.MobileCashAcctsResponse;
import com.popular.android.mibanco.ws.response.MobileCashTrx;
import com.popular.android.mibanco.ws.response.MobileCashTrxInfo;

/**
 * Class that manages all Easy Cash async tasks
 */
public class MobileCashTasks {

    /**
     * Interface to be implemented and manage the Easy Cash backend response
     * @param <T> The parameter
     */
    public interface MobileCashListener<T> extends TaskListener {
        void onMobileCashApiResponse(T result);
    }

    private static abstract class MobileCashTask<T> extends SessionAsyncTask {

        protected T response;

        public MobileCashTask(Context context, MobileCashListener<T> listener) {
            super(context, listener, true);
        }

        @Override
        protected Integer doInBackground(Object... params) {
            try {
                response = doAsync();
//                if (response instanceof AthmResponse) {
//                    AthmResponse athmResponse = (AthmResponse) response;
//                    if (athmResponse.isDowntime() || athmResponse.isBlocked() || athmResponse.isAlertError()) {
//                        throw new AthmException(athmResponse);
//                    }
//                }
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (taskListener != null) {
                ((MobileCashListener<T>) taskListener).onMobileCashApiResponse(response);
            }
        }

        protected abstract T doAsync() throws Exception;
    }

    private static class MobileCashGetPrestageTask extends MobileCashTask<MobileCashTrxInfo> {

        public MobileCashGetPrestageTask(Context context, MobileCashListener<MobileCashTrxInfo> listener) {
            super(context, listener);
        }

        @Override
        protected MobileCashTrxInfo doAsync() throws Exception {
            response = App.getApplicationInstance().getApiClient().getMobileCashPendingTrx();
            responderName = response.getResponderName();
            if (responderName.equalsIgnoreCase("login")) {
                throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
            }
            return response;
        }
    }

    private static class EasyCashGetPrestageTask extends MobileCashTask<EasyCashTrx> {

        private boolean refresh;
        public EasyCashGetPrestageTask(Context context,boolean refresh, MobileCashListener<EasyCashTrx> listener) {
            super(context, listener);
            this.refresh = refresh;
        }

        @Override
        protected EasyCashTrx doAsync() throws Exception {
            response = App.getApplicationInstance().getApiClient().getEasyCashPendingTrx(refresh);
            responderName = response.getResponder_name();
            if (responderName.equalsIgnoreCase("login")) {
                throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
            }
            return response;
        }
    }

    private static class MobileCashGetAccountsTask extends MobileCashTask<MobileCashAcctsResponse> {

        public MobileCashGetAccountsTask(Context context, MobileCashListener<MobileCashAcctsResponse> listener) {
            super(context, listener);
        }

        @Override
        protected MobileCashAcctsResponse doAsync() throws Exception {
            response = App.getApplicationInstance().getApiClient().getMobileCashAccts();
            responderName = response.getResponderName();
            if (responderName.equalsIgnoreCase("login")) {
                throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
            }
            return response;
        }
    }

    private static class EasyCashGetAccountsTask extends MobileCashTask<MobileCashAcctsResponse> {

        private boolean refresh = false;
        public EasyCashGetAccountsTask(Context context,boolean refresh, MobileCashListener<MobileCashAcctsResponse> listener) {
            super(context, listener);
            this.refresh = refresh;
        }

        @Override
        protected MobileCashAcctsResponse doAsync() throws Exception {
            try {
                response = App.getApplicationInstance().getApiClient().getEasyCashAccts(this.refresh);
                responderName = response.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
                }
                return response;
            } catch (final Exception e) {
                return null;
            }
        }

    }

    private static class MobileCashPostTrxCodeTask extends MobileCashTask<MobileCashTrxInfo> {

        private String atmCode;
        private String pendingTranId;
        public MobileCashPostTrxCodeTask(Context context, String code, String pendingTranId, MobileCashListener<MobileCashTrxInfo> listener) {
            super(context, listener);
            atmCode = code;
            this.pendingTranId = pendingTranId;
        }

        @Override
        protected MobileCashTrxInfo doAsync() throws Exception {
            response = App.getApplicationInstance().getApiClient().postEasyCashTrxCode(atmCode, pendingTranId);
            responderName = response.getResponderName();
            if (responderName.equalsIgnoreCase("login")) {
                throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
            }
            return response;
        }
    }

    private static class MobileCashPostTrxTask extends MobileCashTask<EasyCashTrx> {

        private MobileCashTrx trx;
        public MobileCashPostTrxTask(Context context, MobileCashTrx trx, MobileCashListener<EasyCashTrx> listener) {
            super(context, listener);
            this.trx = trx;
        }

        @Override
        protected EasyCashTrx doAsync() throws Exception {
            response = App.getApplicationInstance().getApiClient().postMobileCashPrestageTrx(trx,App.getApplicationInstance().getLanguage());
            responderName = response.getResponder_name();
            if (responderName.equalsIgnoreCase("login")) {
                throw new BankException(App.getApplicationInstance().getString(R.string.session_has_expired));
            }
            return response;
        }
    }


    public static void getPrestageTrxInfo(Context context, MobileCashListener<MobileCashTrxInfo> listener) {
        new MobileCashGetPrestageTask(context, listener).execute();
    }

    public static void getEasyCashPrestageTrxInfo(Context context,boolean refresh, MobileCashListener<EasyCashTrx> listener) {
        new EasyCashGetPrestageTask(context,refresh, listener).execute();
    }

    public static void getAccounts(Context context, MobileCashListener<MobileCashAcctsResponse> listener) {
        new MobileCashGetAccountsTask(context, listener).execute();
    }

    public static void getEasyCashAccounts(Context context, boolean refresh, MobileCashListener<MobileCashAcctsResponse> listener) {
        new EasyCashGetAccountsTask(context, refresh, listener).execute();
    }

    public static void postTransactionCode(Context context, String transactionCode, String pendingTranId, MobileCashListener<MobileCashTrxInfo> listener) {
        new MobileCashPostTrxCodeTask(context, transactionCode,pendingTranId, listener).execute();
    }

    public static void postTransaction(Context context, MobileCashTrx transaction, MobileCashListener<EasyCashTrx> listener) {
        new MobileCashPostTrxTask(context, transaction, listener).execute();
    }

    public static void cancelTransaction(Context context,String pendingTranId, MobileCashListener<MobileCashTrxInfo> listener) {
        new MobileCashPostTrxCodeTask(context, "",pendingTranId, listener).execute();
    }


}
