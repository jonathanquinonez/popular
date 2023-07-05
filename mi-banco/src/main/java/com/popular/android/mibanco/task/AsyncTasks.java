package com.popular.android.mibanco.task;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;

import com.google.gson.stream.JsonReader;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoPreferences;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.activity.Payments;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.exception.AccessBlockedException;
import com.popular.android.mibanco.exception.BankException;
import com.popular.android.mibanco.exception.MaintenanceMobileException;
import com.popular.android.mibanco.exception.MaintenancePersonalException;
import com.popular.android.mibanco.exception.NotAvailableException;
import com.popular.android.mibanco.exception.UpdateRequiredException;
import com.popular.android.mibanco.listener.LocationsListener;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.listener.SimpleListener;
import com.popular.android.mibanco.listener.StartListener;
import com.popular.android.mibanco.listener.TaskListener;
import com.popular.android.mibanco.listener.TransactionsListener;
import com.popular.android.mibanco.model.AccountTransactions;
import com.popular.android.mibanco.model.BaseFormResponse;
import com.popular.android.mibanco.model.Customer;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.CustomerEntitlements;
import com.popular.android.mibanco.model.DepositCheckEnrollment;
import com.popular.android.mibanco.model.DepositCheckReceipt;
import com.popular.android.mibanco.model.DeviceInfo;
import com.popular.android.mibanco.model.EBills;
import com.popular.android.mibanco.model.EBillsItem;
import com.popular.android.mibanco.model.GlobalStatus;
import com.popular.android.mibanco.model.LoginGet;
import com.popular.android.mibanco.model.NotificationCenter;
import com.popular.android.mibanco.model.OnOffPlastics;
import com.popular.android.mibanco.model.OobChallenge;
import com.popular.android.mibanco.model.OpenAccountUrl;
import com.popular.android.mibanco.model.Payment;
import com.popular.android.mibanco.model.PaymentAccount;
import com.popular.android.mibanco.model.PaymentActive;
import com.popular.android.mibanco.model.PaymentHistory;
import com.popular.android.mibanco.model.PremiaInfo;
import com.popular.android.mibanco.model.RDCCheckItem;
import com.popular.android.mibanco.model.RemoteDepositHistory;
import com.popular.android.mibanco.model.Transfer;
import com.popular.android.mibanco.model.TransferAccount;
import com.popular.android.mibanco.model.TransferActive;
import com.popular.android.mibanco.model.TransferHistory;
import com.popular.android.mibanco.model.TsysLoyaltyRewardsInfo;
import com.popular.android.mibanco.model.TsysLoyaltyRewardsRecurringStatementCreditResponse;
import com.popular.android.mibanco.model.User;
import com.popular.android.mibanco.object.BankLocation;
import com.popular.android.mibanco.object.BankLocationDetail;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.CryptoUtils;
import com.popular.android.mibanco.util.DFMUtils;
import com.popular.android.mibanco.util.FontChanger;
import com.popular.android.mibanco.util.KiuwanUtils;
import com.popular.android.mibanco.util.PushUtils;
import com.popular.android.mibanco.util.RSACollectUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.pickerview.ArrayBankWheelItem;
import com.popular.android.mibanco.widget.BalanceWidgetProvider;
import com.popular.android.mibanco.ws.ApiClient;
import com.popular.android.mibanco.ws.CustomGsonParser;
import com.popular.android.mibanco.ws.response.PushTokenRequest;
import com.popular.android.mibanco.ws.response.PushTokenResponse;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Gathers AsyncTasks classes and methods.
 */
@SuppressWarnings({"unchecked", "StaticFieldLeak"})
public class AsyncTasks {

    /**
     * The application instance.
     */
    private App app;

    /**
     * The fetch payments task.
     */
    private FetchPaymentsTask fetchPaymentsTask;

    /**
     * Is payments task currently in progress?
     */
    private boolean paymentsTaskRunning;

    /**
     * The e-Bills progress dialog.
     */
    private ProgressDialog paymentsDialog;

    /**
     * The last running task.
     */
    private FetchTransfersTask fetchTransfersTask;

    /**
     * The transfers progress dialog.
     */
    private ProgressDialog transfersDialog;

    /**
     * The global status task progress dialog.
     */
    private ProgressDialog globalStatusDialog;

    /**
     * Notification Center
     */
    private NotificationCenterTask notificationCenterTask;

    /**
     * The global status task.
     */
    private StartTask globalStatusTask;

    /**
     * Is global status task currently in progress?
     */
    private boolean globalStatusTaskRunning;

    /**
     * Is transfers task currently in progress?
     */
    private boolean transfersTaskRunning;

    /**
     * Instantiates new AsyncTasks object.
     *
     * @param application the Application instance
     */
    public AsyncTasks(App application) {
        this.app = application;
    }

    // PAYMENTS

    /**
     * Fetches payments from the web service.
     */
    protected class FetchPaymentsTask extends SessionAsyncTask {

        /**
         * The Constant PORTAL_UPDATE_SLEEP_MILLIS.
         */
        private final static long PORTAL_UPDATE_SLEEP_MILLIS = 800;

        /**
         * Instantiates a new FetchPaymentsTask.
         *
         * @param context      the context
         * @param listener     the listener
         * @param showProgress show progress indicator?
         */
        public FetchPaymentsTask(final Context context, final ResponderListener listener, final boolean showProgress, final String progressMessage) {
            super(context, listener, showProgress, progressMessage);
            app.setPaymentsInfo(null);
        }

        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("FetchPaymentsTask");
            try {
                while (app.isPortalDelayInEffect()) {
                    try {
                        Thread.sleep(PORTAL_UPDATE_SLEEP_MILLIS);
                    } catch (Exception e) {
                        Log.w("AsyncTasks", e);
                    }
                }

                if (app.isUpdatingBalances()) {
                    Customer customer = app.getApiClient().portal();
                    app.setLoggedInUser(customer);
                    app.loadAccounts();

                    responderName = customer.getResponderName();
                    if (responderName.equalsIgnoreCase("login")) {
                        taskException = new BankException(app.getString(R.string.session_has_expired));
                        return RESULT_FAILURE;
                    }
                }

                app.setPaymentsInfo(app.getApiClient().fetchPayments());

                responderName = app.getPaymentsInfo().getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    taskException = new BankException(app.getString(R.string.session_has_expired));
                    return RESULT_FAILURE;
                }

                try {
                    if (app.getGlobalStatus().isEbills()) {
                        app.setEbills(app.getApiClient().fetchEbills());
                        filterEbills(app.getEbills());
                    }
                } catch (Exception e) {
                    Log.w("FetchPaymentsTask", e);
                }

                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }

        private void filterEbills(EBills ebills) throws ParseException {

            // get payees 'global payee id' and 'last 4 account numbers', one payee may have few account numbers assigned
            App.getApplicationInstance().setValidEbills(new ArrayList<EBillsItem>());
            final Payment payment = App.getApplicationInstance().getPaymentsInfo();
            final SparseArray<Set<String>> payeeIds = new SparseArray<>();
            for (final PaymentAccount account : payment.getAccountsTo()) {
                if (account.getGlobalPayeeId() != 0) {
                    Set<String> last4nums = payeeIds.get(account.getGlobalPayeeId());
                    if (last4nums != null) {
                        last4nums.add(account.getAccountLast4Num());
                    } else {
                        last4nums = new HashSet<>();
                        last4nums.add(account.getAccountLast4Num());
                        payeeIds.put(account.getGlobalPayeeId(), last4nums);
                    }
                }
            }

            if (ebills == null || ebills.getEbppInbox() == null) {
                return;
            }

            for (final EBillsItem ebill : ebills.getEbppInbox()) {
                boolean matchedEbill = false;
                boolean dueDateInFuture = false;
                boolean hasNonZeroAmount = false;

                final Date dueDate = ebill.getDueDate();
                if (dueDate != null) {
                    if (dueDate.after(new Date())) {
                        dueDateInFuture = true;
                    }
                }

                final BigDecimal amountDueDecimal = ebill.getAmountDueDecimal();
                if (amountDueDecimal.compareTo(new BigDecimal(0.0)) > 0) {
                    hasNonZeroAmount = true;
                }

                final int ebillGlobalPayeeId = ebill.getPayeeNumber();
                final Set<String> last4nums = payeeIds.get(ebillGlobalPayeeId);
                if (last4nums != null && last4nums.contains(ebill.getLast4AcctNumber())) {
                    matchedEbill = true;
                }

                if (dueDateInFuture && hasNonZeroAmount && matchedEbill) {
                    App.getApplicationInstance().getValidEbills().add(ebill);
                }
            }
        }

        @Override
        protected void onCancelled() {
            paymentsTaskRunning = false;
            super.onCancelled();
        }


        @Override
        protected void onPostExecute(final Integer result) {
            paymentsTaskRunning = false;
            Utils.dismissDialog(paymentsDialog);
            paymentsDialog = null;

            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(responderName, app.getPaymentsInfo());
            }
        }
    }

    /**
     * Fetches transfers from the web service.
     */
    protected class FetchTransfersTask extends SessionAsyncTask {

        /**
         * The Constant PORTAL_UPDATE_SLEEP_MILLIS.
         */
        private final static long PORTAL_UPDATE_SLEEP_MILLIS = 500;

        /**
         * Instantiates a new FetchTransfersTask.
         *
         * @param context      the context
         * @param listener     the listener
         * @param showProgress show progress indicator?
         */
        public FetchTransfersTask(final Context context, final ResponderListener listener, final boolean showProgress, final String progressMessage) {
            super(context, listener, showProgress, progressMessage);
            app.setTransfersInfo(null);
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("FetchTransfersTask");
            try {
                while (app.isPortalDelayInEffect()) {
                    try {
                        Thread.sleep(PORTAL_UPDATE_SLEEP_MILLIS);
                    } catch (Exception e) {
                        Log.w("AsyncTasks", e);
                    }
                }

                if (app.isUpdatingBalances()) {
                    Customer customer = app.getApiClient().portal();
                    app.setLoggedInUser(customer);
                    app.loadAccounts();

                    responderName = customer.getResponderName();
                    if (responderName.equalsIgnoreCase("login")) {
                        taskException = new BankException(app.getString(R.string.session_has_expired));
                        return RESULT_FAILURE;
                    }
                }

                app.setTransfersInfo(app.getApiClient().fetchTransfers());

                responderName = app.getTransfersInfo().getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    taskException = new BankException(app.getString(R.string.session_has_expired));
                    return RESULT_FAILURE;
                }
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }

        @Override
        protected void onCancelled() {
            transfersTaskRunning = false;
            super.onCancelled();
        }


        @Override
        protected void onPostExecute(final Integer result) {
            transfersTaskRunning = false;
            Utils.dismissDialog(transfersDialog);
            transfersDialog = null;

            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(responderName, app.getTransfersInfo());
            }
        }
    }

    // LOCATOR

    /**
     * Gets locations of ATMs and branches from the web.
     */
    protected class GetLocationsTask extends BaseAsyncTask {

        /**
         * Instantiates a new GetLocationsTask.
         *
         * @param context  the context
         * @param listener the listener
         */
        public GetLocationsTask(final Context context, final LocationsListener listener) {
            super(context, listener, true);
            taskListener = listener;
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("GetLocationsTask");

            JsonReader reader = null;
            try {

                String atmsJson = app.getApiClient().fetchLocations(ApiClient.LOCATOR_ATMS_WEBSERVICE_URL);
                if (atmsJson == null || atmsJson.equals("")) {
                    return RESULT_FAILURE;
                } else {
                    final InputStream stream = new ByteArrayInputStream(atmsJson.getBytes());
                    reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
                    app.setAtms(Utils.readLocationsArray(reader));
                }

                String branchesJson = app.getApiClient().fetchLocations(ApiClient.LOCATOR_BRANCHES_WEBSERVICE_URL);
                if (branchesJson == null || branchesJson.equals("")) {
                    return RESULT_FAILURE;
                } else {
                    final InputStream stream2 = new ByteArrayInputStream(branchesJson.getBytes());
                    reader = new JsonReader(new InputStreamReader(stream2, "UTF-8"));
                    app.setBranches(Utils.readLocationsArray(reader));
                }

            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            } finally {
                try {
                    if (reader != null)
                        reader.close();
                } catch (final IOException e) {
                    Log.w("AsyncTasks", e);
                }
            }

            return RESULT_SUCCESS;
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((LocationsListener) taskListener).updateLocations(app.getAtms(), app.getBranches());
                ((LocationsListener) taskListener).updateBranches(app.getBranches());
                ((LocationsListener) taskListener).updateATMs(app.getAtms());
            }
        }
    }

    /**
     * Gets locations of ATMs and branches from the web.
     */
    protected class GetLocationDetailsTask extends BaseAsyncTask {

        private String locationDetailId;

        public GetLocationDetailsTask(final Context context, String locationDetailId, final LocationsListener listener) {
            super(context, listener, true);
            taskListener = listener;
            this.locationDetailId = locationDetailId;
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("GetLocationDetailsTask");

            JsonReader reader = null;
            try {
                String branchUrl = ApiClient.LOCATOR_BRANCH_DETAIL_WEBSERVICE_URL.replace("[id]", locationDetailId);
                String branchesJson = app.getApiClient().fetchLocations(branchUrl);
                if (branchesJson == null || branchesJson.equals("")) {
                    return RESULT_FAILURE;
                } else {
                    final InputStream stream2 = new ByteArrayInputStream(branchesJson.getBytes());
                    reader = new JsonReader(new InputStreamReader(stream2, "UTF-8"));
                    app.setBankLocationDetail(Utils.readLocationDetailsArray(reader));
                }

            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (final IOException e) {
                    Log.w("AsyncTasks", e);
                }
            }

            return RESULT_SUCCESS;
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((LocationsListener) taskListener).updateBranchDetail(app.getBankLocationDetail());
            }
        }
    }


    // ACCOUNTS

    /**
     * Gets account transactions from the web service.
     */
    protected class GetTransactionsTask extends SessionAsyncTask {

        /**
         * The account number.
         */
        private final String accountNr;

        /**
         * The cycle number.
         */
        private final int cycle;

        /**
         * The page number.
         */
        private final int pageNr;

        /**
         * The transactions JSON response object.
         */
        private AccountTransactions transactionsResponse;

        /**
         * Instantiates a new GetTransactionsTask.
         *
         * @param context  the context
         * @param account  the account
         * @param aCycle   the cycle
         * @param aPageNr  the page nr
         * @param listener the listener
         */
        public GetTransactionsTask(final Context context, final String account, final int aCycle, final int aPageNr, final TransactionsListener listener) {
            super(context, listener, true);
            accountNr = account;
            cycle = aCycle;
            pageNr = aPageNr;
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("GetTransactionsTask");
            try {
                transactionsResponse = app.getApiClient().fetchTransactions(accountNr, (pageNr == -1), cycle, pageNr == -1 ? 1 : pageNr);
                if (transactionsResponse.getResponderName().equalsIgnoreCase("login")) {
                    taskException = new BankException(app.getString(R.string.session_has_expired));
                    return RESULT_FAILURE;
                }
                if (app.getTransactions() == null) {
                    app.setTransactions(new HashMap<String, SparseArray<SparseArray<AccountTransactions>>>());
                }
                SparseArray<SparseArray<AccountTransactions>> transactionsCycles;
                SparseArray<AccountTransactions> cycleTransactions;
                if (!app.getTransactions().containsKey(accountNr)) {
                    cycleTransactions = new SparseArray<>();
                    transactionsCycles = new SparseArray<>();
                    transactionsCycles.put(cycle, cycleTransactions);
                    app.getTransactions().put(accountNr, transactionsCycles);
                } else {
                    transactionsCycles = app.getTransactions().get(accountNr);
                    if (transactionsCycles.get(cycle) != null) {
                        cycleTransactions = transactionsCycles.get(cycle);
                    } else {
                        cycleTransactions = new SparseArray<>();
                        transactionsCycles.put(cycle, cycleTransactions);
                    }
                }
                cycleTransactions.put(pageNr, transactionsResponse);
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS) {
                ((TransactionsListener) taskListener).onTransactionsUpdated(transactionsResponse);
            }
        }
    }

    // LOGIN

    /**
     * Logs user in using supplied username. This task returns security question and account blocked flag. Returned responder name may directly point
     * to Password screen during the login process if the device has been remembered by the bank back-end.
     */
    protected class LoginTask extends BaseAsyncTask {

        /**
         * The username.
         */
        private final String mUsername;

        /**
         * The deviceInfoRsa.
         */
        private final String mDeviceInfoRsa;

        /**
         * The rsaCookie.
         */
        private final String rsaCookie;

        /**
         * The security question.
         */
        private String question;

        /**
         * OOB data
         */
        private OobChallenge oobData;

        /**
         * The device identifier
         */
        private String mDeviceIdentifier;

        /**
         * Instantiates a new LoginTask.
         *
         * @param context  the context
         * @param username the username
         * @param listener the listener
         */
        LoginTask(final Context context, final String username, final String deviceIdentifier,
                         final String deviceInfoRsa, String rsaCookie,
                         final ResponderListener listener) {
            super(context, listener, true);
            mUsername = username;
            app.setUID(username);
            mDeviceIdentifier = deviceIdentifier;
            mDeviceInfoRsa = deviceInfoRsa;
            this.rsaCookie = rsaCookie;
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("LoginTask");
            try {
                if (mUsername == null || mUsername.length() <= 0) {
                    taskException = new BankException(app.getString(R.string.enter_username));
                    return RESULT_FAILURE;
                }

                final HashMap<String, Object> response = app.getApiClient().postLogin(mUsername, mDeviceInfoRsa, rsaCookie);


                responderName = (String) response.get("responder_name");
                question = (String) response.get("question");
                boolean accountBlocked = (Boolean) response.get("access_blocked");

                Utils.setPrefsString(MiBancoConstants.RSA_COOKIE, "", context);
                final String cookieValue = KiuwanUtils.checkBeforeCast(String.class, response.get("rsa_cookie"));
                if (cookieValue != null && !cookieValue.equals("")) {
                    Utils.setPrefsString(MiBancoConstants.RSA_COOKIE, cookieValue, context);
                }

                App.getApplicationInstance().setCustomerPhone(null);
                //ADD NEW CONDITION SET PHONE NUMBER
                if (responderName.contains("password") || responderName.contains("loginoob")) {
                    String phoneNumber = (String) response.get("phoneNumber");
                    App.getApplicationInstance().setCustomerPhone(phoneNumber);
                }

                if (responderName.contains("password") ) {
                    CryptoUtils crypto = new CryptoUtils();
                    String keyDecrypt = (response.get("strKey") instanceof String ? (String)response.get("strKey") : null);
                    String decryptPhNumber = "";
                    if(keyDecrypt != null && !keyDecrypt.isEmpty()){
                        decryptPhNumber = crypto.decrypt(keyDecrypt, keyDecrypt, response.get("phoneNumber").toString());
                    }
                    App.getApplicationInstance().setCustomerPhone(decryptPhNumber);
                    SharedPreferences sharedPreferences = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("isSecuritiesCustomer", (String) response.get("isSecuritiesCustomer")).apply();
                }

                if (response.containsKey("pendingEnroll") && Boolean.TRUE.equals(response.get("pendingEnroll"))) {
                    taskException = new BankException(app.getString(R.string.account_pending_enroll), app.getString(R.string.account_pending_enroll_title));
                    return RESULT_FAILURE;
                }

                if (response.containsKey("MBSFE291")) {
                    MiBancoPreferences.setMiBancoFlagMbsfe291(true);
                } else {
                    MiBancoPreferences.setMiBancoFlagMbsfe291(false);
                }

                String customerToken = (String) response.get("customerToken");
                app.setCustomerToken(customerToken);

                if (responderName == null || responderName.equalsIgnoreCase("login")) {
                    if (accountBlocked) {
                        taskException = new AccessBlockedException(app.getString(R.string.account_blocked_title));
                        BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_USERNAME_BLOCKED);
                    } else {
                        taskException = new BankException(app.getString(R.string.enter_valid_username));
                        BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_USERNAME_INVALID);
                    }
                    return RESULT_FAILURE;
                }

                if (responderName.equalsIgnoreCase("notAvailable")) {
                    taskException = new NotAvailableException(app.getString(R.string.not_available_description));
                    return RESULT_FAILURE;
                }
                if (responderName.equalsIgnoreCase(MiBancoConstants.OOB_ACTION_NAME)) {
                    String phone = (String) response.get(MiBancoConstants.OOB_PHONE);
                    String usern = (String) response.get(MiBancoConstants.OOB_USER_NAME);
                    String challengetype = (String) response.get(MiBancoConstants.OOB_CHALLENGE_TYPE);
                    String code = (String) response.get(MiBancoConstants.OOB_CODE_VOICE_CALL);
                    boolean rsaBlocked = false;
                    if (response.containsKey(MiBancoConstants.OOB_RSA_BLOCKED)) {
                        rsaBlocked = (boolean) response.get(MiBancoConstants.OOB_RSA_BLOCKED);
                    }
                    //check for RSA blocked
                    if (rsaBlocked) {
                        taskException = new AccessBlockedException(app.getString(R.string.account_blocked_title));
                        BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_USERNAME_BLOCKED);
                        return RESULT_FAILURE;
                    }
                    //if is voice call
                    if ((challengetype.equals(MiBancoConstants.OOB_CALL_TYPE_CHALLENGE) &&
                            phone != null && usern != null && !phone.isEmpty() && !usern.isEmpty() &&
                            code != null && !code.isEmpty())

                            //if is sms
                            || (phone != null && usern != null && !phone.isEmpty() && !usern.isEmpty())) {
                        oobData = setOOBChallengeData(response);
                    } else {
                        taskException = new BankException(app.getString(R.string.new_code_error));
                        return RESULT_FAILURE;
                    }
                }
                String opacFlag = (String) response.get(MiBancoConstants.CAN_OPEN_ACCOUNT);
                final HashMap<String, String> opac = new HashMap<>();
                if (responderName.contains("password") && opacFlag != null) {
                    opac.put(MiBancoConstants.CAN_OPEN_ACCOUNT, (String) response.get(MiBancoConstants.CAN_OPEN_ACCOUNT));
                    opac.put(MiBancoConstants.IS_FOREING_CUSTOMER, (String) response.get(MiBancoConstants.IS_FOREING_CUSTOMER));
                    MiBancoPreferences.setOpac(opac);
                } else if (opacFlag == null) {
                    opac.put(MiBancoConstants.CAN_OPEN_ACCOUNT, "false");
                    opac.put(MiBancoConstants.IS_FOREING_CUSTOMER, "false");
                    MiBancoPreferences.setOpac(opac);
                }
                return RESULT_SUCCESS;

            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS) {
                if (responderName.equalsIgnoreCase(MiBancoConstants.OOB_ACTION_NAME)) {
                    ((ResponderListener) taskListener).responder(responderName, oobData);
                } else {
                    ((ResponderListener) taskListener).responder(responderName, question);
                }
            } else {
                App.getApplicationInstance().setCurrentUser(null);
            }
        }

        /*
         * Set Data for OOB Challenge
         */
        protected OobChallenge setOOBChallengeData(final HashMap<String, Object> response) {
            OobChallenge oobChallenge = new OobChallenge();
            oobChallenge.getContent().setChallengeType((String) response.get(MiBancoConstants.OOB_CHALLENGE_TYPE));
            oobChallenge.getContent().setPhone((String) response.get(MiBancoConstants.OOB_PHONE));
            oobChallenge.getContent().setUsername((String) response.get(MiBancoConstants.OOB_USER_NAME));
            oobChallenge.setResponderMessage((String) response.get(MiBancoConstants.OOB_PAGE_NAME));
            oobChallenge.getContent().setHasAltPhone((Boolean) response.get(MiBancoConstants.OOB_HAS_ALTPHONE));
            oobChallenge.getContent().setCodeVoiceCall((String) response.get(MiBancoConstants.OOB_CODE_VOICE_CALL));

            return oobChallenge;
        }
    }

    // LOGOUT

    /**
     * Logout task.
     */
    protected class LogoutTask extends BaseAsyncTask {

        /**
         * Web service flag.
         */
        private final boolean expired;

        /**
         * Instantiates a new LogoutTask.
         *
         * @param context  the context
         * @param aExpired the a expired
         * @param listener the listener
         */
        public LogoutTask(final Context context, final boolean aExpired, final SimpleListener listener) {
            super(context, listener, false);
            expired = aExpired;
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("LogoutTask");
            try {
                responderName = app.getApiClient().logout(expired);
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                Log.w("AsyncTasks", e);
                return RESULT_FAILURE;
            }
        }


        @Override
        protected void onPostExecute(final Integer result) {
            // ignore exceptions
            cleanUp();
            if (taskListener != null) {
                ((SimpleListener) taskListener).done();
            }
        }
    }

    /**
     * Submits password entered by the user during the login process.
     */
    protected class PasswordTask extends BaseAsyncTask {

        /**
         * The password string.
         */
        private final String mPassword;

        /**
         * The question String.
         */
        private String question;

        /**
         * Instantiates a new PasswordTask.
         *
         * @param context  the context
         * @param password the password
         * @param listener the listener
         */
        public PasswordTask(final Context context, final String password, final ResponderListener listener) {
            super(context, listener, true);
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("PasswordTask");
            try {
                if (mPassword == null || mPassword.length() <= 0) {
                    taskException = new BankException(app.getString(R.string.enter_password));
                    return RESULT_FAILURE;
                }

                //TODO: LALR REVISE WIDGET TOKEN LOGIC
                // To avoid unnecesary check to the customer_devices table
                String widgetDeviceId = "";
                //String customerToken = "";
                List<User> usernames = Utils.getUsernames(context);
                if (usernames != null && usernames.size() > 0) {

                    if (app.getUid().equals(usernames.get(0).getUsername())) {
                        widgetDeviceId = Utils.getStringContentFromShared(context, "widget_device_identifier");
                        if (widgetDeviceId == null || widgetDeviceId.equals("")) {
                            widgetDeviceId = UUID.randomUUID().toString();
                        }
                        //customerToken = app.getCustomerToken();
                        app.setWidgetDeviceId(widgetDeviceId);
                    }
                }

//                //SAVE UNIQUE DEVICE ID
//                if(Utils.isBlankOrNull(deviceId)){
//                    deviceId = Utils.getDeviceId(context);
//                }
                String customerToken = app.getCustomerToken();
                String deviceId = App.getApplicationInstance().getDeviceId();
                //app.setWidgetDeviceId(deviceId);

                TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                DeviceInfo deviceInfo = new DeviceInfo();
                if (manager != null) {
                    deviceInfo.setNetworkProvider(manager.getNetworkOperatorName());
                } else {
                    deviceInfo.setNetworkProvider(null);
                }

                deviceInfo.setDeviceModel(PushUtils.getDeviceModel());
                deviceInfo.setAppVersion(BuildConfig.VERSION_NAME + "  Build: " + BuildConfig.VERSION_CODE);
                deviceInfo.setOsVersion(Utils.getOsInfo());

                String deviceName = PushUtils.getDeviceName(context);

                String responseString = app.getApiClient().postPassword(mPassword, deviceId, (customerToken + widgetDeviceId/*deviceId*/), deviceName, deviceInfo);
                final HashMap<String, Object> response = app.getApiClient().parseBaseResponse(responseString);
                responderName = (String) response.get("responder_name");
                boolean accountBlocked = (Boolean) response.get("access_blocked");
                final HashMap<String, String> opac = new HashMap<>();
                final boolean hasOpacPreferences = MiBancoPreferences.getOpac().containsKey(MiBancoConstants.IS_FOREING_CUSTOMER) && MiBancoPreferences.getOpac().containsKey(MiBancoConstants.CAN_OPEN_ACCOUNT);
                String opacCanOpenAccount = null;

                if (response.get(MiBancoConstants.CAN_OPEN_ACCOUNT) instanceof String) {
                    opacCanOpenAccount = (String) response.get(MiBancoConstants.CAN_OPEN_ACCOUNT);
                }

                Utils.setPrefsString(MiBancoConstants.RSA_COOKIE, "", context);
                if (response.containsKey("rsa_cookie")) {
                    String rsaCookie = String.class.cast(response.get("rsa_cookie"));
                    Utils.setPrefsString(MiBancoConstants.RSA_COOKIE, rsaCookie, context);
                }

                if (response.containsKey("MBSFE291")) {
                    MiBancoPreferences.setMiBancoFlagMbsfe291(true);
                } else {
                    MiBancoPreferences.setMiBancoFlagMbsfe291(false);
                }

                if (responderName == null || responderName.equalsIgnoreCase("login")) {
                    if (accountBlocked) {
                        taskException = new AccessBlockedException(app.getString(R.string.account_blocked_title));
                        BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_USERNAME_BLOCKED);
                    } else {
                        taskException = new BankException(app.getString(R.string.session_has_expired));
                        BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_BACK_TO_LOGIN_AFTER_PASSWORD);
                    }
                    return RESULT_FAILURE;
                } else if (responderName.equalsIgnoreCase("notAvailable")) {
                    taskException = new NotAvailableException(app.getString(R.string.not_available_description));
                    return RESULT_FAILURE;
                } else if (responderName.equalsIgnoreCase("question")) {
                    BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_CHALLENGED_AFTER_PASSWORD);
                    question = (String) response.get("question");
                    return RESULT_SUCCESS;
                } else if (responderName.equalsIgnoreCase("password")) {
                    taskException = new BankException(app.getString(R.string.enter_valid_password));
                    BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_INCORRECT_PASSWORD);
                    return MiBancoConstants.WRONG_PASSWORD;
                } else if (responderName.equalsIgnoreCase(MiBancoConstants.RSA_ENROLL)) {
                    BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_USERNAME_BLOCKED);
                    return RESULT_SUCCESS;
                } else if (responderName.equalsIgnoreCase(MiBancoConstants.SSDS_FORCED_LOGIN)) {
                    BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_CHANGE_USERNAME);
                    return RESULT_SUCCESS;
                } else if (responderName.equalsIgnoreCase("accountInformationSec")) { //MBFIS-223
                    //BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_CHALLENGED_AFTER_PASSWORD);
                    return RESULT_SUCCESS;
                } else if (responderName.equalsIgnoreCase("resetForgotPasswordSec")) { //MBFIS-155
                    //BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_CHALLENGED_AFTER_PASSWORD);
                    return RESULT_SUCCESS;
                } else if (responderName.equalsIgnoreCase("securityInformationSec")) { //MBFIS-521
                    //BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_CHALLENGED_AFTER_PASSWORD);
                    return RESULT_SUCCESS;
                } else if (responderName.contains("portal") && opacCanOpenAccount != null) {
                    opac.put(MiBancoConstants.CAN_OPEN_ACCOUNT, opacCanOpenAccount);

                    if (response.get(MiBancoConstants.IS_FOREING_CUSTOMER) instanceof String) {
                        opac.put(MiBancoConstants.IS_FOREING_CUSTOMER, (String) response.get(MiBancoConstants.IS_FOREING_CUSTOMER));
                    }
                    MiBancoPreferences.setOpac(opac);
                } else if (opacCanOpenAccount == null && !hasOpacPreferences) {
                    opac.put(MiBancoConstants.CAN_OPEN_ACCOUNT, "false");
                    opac.put(MiBancoConstants.IS_FOREING_CUSTOMER, "false");
                    MiBancoPreferences.setOpac(opac);
                }

                final CustomGsonParser gson = new CustomGsonParser();
                Customer customer = gson.fromJson(responseString, Customer.class);
                if (customer.getInterruptionPage()){
                    customer.setOutreach(false);
                }
                app.setLoggedInUser(customer);
                app.setLastUserInteractionTime(System.currentTimeMillis());



                if (app.getLoggedInUser() == null || app.getLoggedInUser().getContent() == null) {
                    taskException = new BankException(context.getString(R.string.error_occurred));
                    return RESULT_FAILURE;
                }

                // Parse Premia Initiative Information.
                app.setPremiaInfo(gson.fromJson(responseString, PremiaInfo.class));

                app.loadAccounts();

                BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_PASSWORD_ACCEPTED);
                if (DFMUtils.isLimitsPerSegmentEnabled() && app.getLoggedInUser().getIsComercialCustomer()) {
                    BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_COMMERCIAL_PASSWORD_ACCEPTED);
                }

                // Obtain the customer entitlements
                try {
                    app.setCustomerEntitlements(app.getApiClient().fetchCustomerEntitlements());
                } catch (Exception e) {
                    // If we cannot get the client services, set them to default
                    CustomerEntitlements customerEntitlements = new CustomerEntitlements();
                    app.setCustomerEntitlements(customerEntitlements);
                }

                // count successful logins
                final SharedPreferences sharedPreferences = Utils.getSecuredSharedPreferences(app);
                int successfulLogins = sharedPreferences.getInt(MiBancoConstants.SUCCESSFUL_LOGINS_PREFS_KEY, 0);
                ++successfulLogins;

                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(MiBancoConstants.SUCCESSFUL_LOGINS_PREFS_KEY, successfulLogins);
                editor.commit();

                // Save username
                if (app.isSaveUsername() && Utils.saveUser(context, app.getCurrentUser())) {
                    BPAnalytics.logEvent(BPAnalytics.EVENT_ADDED_SAVED_USERNAME);
                }

                updateWidgetBalances(context, editor);

                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }

        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS) {
                ((ResponderListener) taskListener).responder(responderName, question);
            } else if (result == MiBancoConstants.WRONG_PASSWORD) {
                ((ResponderListener) taskListener).responder(Integer.toString(MiBancoConstants.WRONG_PASSWORD), "");
            }
        }
    }

    /**
     * Submits Authentication requested by wallet.
     */
    protected class AuthenticationTask extends BaseAsyncTask {

        /**
         * The Authentication string passed by the wallet.
         */
        private final String mAuthentication;
        /**
         * the wallet type.
         */
        private String walletType;

        /**
         * the response status.
         */
        private String responseStatus;
        /**
         * The error String.
         */
        private String error;

        /**
         * Instantiates a new AuthenticationTask.
         *
         * @param context            the context
         * @param authenticationJson the json from wallet
         * @param listener           the listener
         */
        public AuthenticationTask(final Context context, final String authenticationJson, final String wallet, final ResponderListener listener) {
            super(context, listener, true, app.getString(R.string.wallet_authentication_msg));
            mAuthentication = authenticationJson;
            walletType = wallet;
        }

        private HashMap<String, Object> parseBaseResponse(final String responseString) throws IOException {
            final HashMap<String, Object> ret = new HashMap<>();
            ret.put("access_blocked", false);
            final JsonReader reader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(responseString.getBytes()), "UTF-8"));

            if (reader != null) {
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    switch (name) {
                        case "panReferenceID":
                            ret.put("panReferenceID", reader.nextString());
                            break;
                        case "tokenRequestorID":
                            ret.put("tokenRequestorID", reader.nextString());
                            break;

                        case "tokenReferenceID":
                            ret.put("tokenReferenceID", reader.nextString());
                            break;
                        case "panLast4":
                            ret.put("panLast4", reader.nextString());
                            break;
                        case "deviceID":
                            ret.put("deviceID", reader.nextString());
                            break;
                        case "walletAccountID":
                            ret.put("walletAccountID", reader.nextString());
                            break;
                        default:
                            break;
                    }

                }
                reader.endObject();
                reader.close();
            }
            return ret;
        }

        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("AuthenticationTask");
            try {
                if (mAuthentication == null || mAuthentication.length() <= 0) {
                    taskException = new BankException(app.getString(R.string.wallet_authentication));
                    return RESULT_FAILURE;
                }

                Log.v("###***", "###*** :" + mAuthentication); // Only for debug

                final HashMap<String, Object> authenticationJson = parseBaseResponse(mAuthentication);
                String responseString = app.getApiClient().postAuthentication(authenticationJson, walletType);

                responseStatus = (String) app.getApiClient().parseBaseResponse(responseString).get("status");

                if (responseStatus != null && responseStatus.equalsIgnoreCase("SUCCESS")) {
                    return RESULT_SUCCESS;
                }

                if (responseStatus != null && responseStatus.equalsIgnoreCase("FINGERPRINT_DELAY_FAILED")) {
                    error = (String) app.getApiClient().parseBaseResponse(responseString).get("minutesLeft");
                } else {
                    error = responseStatus;
                }
                return RESULT_FAILURE;

            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }

        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (responseStatus != null) {
                if (result == RESULT_FAILURE) {
                    ((ResponderListener) taskListener).responder(responseStatus, error);
                } else {
                    ((ResponderListener) taskListener).responder(responseStatus, null);
                }
            }
        }
    }

    /**
     * Fetches customer information.
     */
    protected class PortalTask extends SessionAsyncTask {

        /**
         * The customer JSON response.
         */
        private Customer customer;

        /**
         * Instantiates a new PortalTask.
         *
         * @param context      the context
         * @param listener     the listener
         * @param showProgress show progress indicator?
         */
        public PortalTask(final Context context, final ResponderListener listener, final boolean showProgress) {
            super(context, listener, showProgress);
        }

        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("PortalTask");
            try {
                customer = app.getApiClient().portal();
                app.setLoggedInUser(customer);
                app.loadAccounts();

                final SharedPreferences sharedPreferences = Utils.getSecuredSharedPreferences(app);
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                updateWidgetBalances(context, editor);

                responderName = customer.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    taskException = new BankException(app.getString(R.string.session_has_expired));
                    return RESULT_FAILURE;
                }
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }

        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(responderName, customer);
            }
        }
    }

    /**
     * Submits answer to the security question.
     */
    protected class QuestionTask extends BaseAsyncTask {

        /**
         * The answer string.
         */
        private final String mAnswer;

        /**
         * Remember the device?.
         */
        private final boolean mRemember;

        /**
         * The question String.
         */
        private String question;

        /**
         * The deviceInfoRsa.
         */
        private final String mDeviceInfoRsa;

        /**
         * The rsaCookie.
         */
        private final String mRsaCookie;

        /**
         * Instantiates a new QuestionTask.
         *
         * @param context  the context
         * @param answer   the answer
         * @param remember remember the device?
         * @param listener the listener
         */
        QuestionTask(final Context context, final String answer, final boolean remember, final String deviceInfoRsa, final String rsaCookie,
                            final ResponderListener listener) {
            super(context, listener, true);
            mAnswer = answer;
            mRemember = remember;
            this.mDeviceInfoRsa = deviceInfoRsa;
            this.mRsaCookie = rsaCookie;
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("QuestionTask");
            try {
                if (mAnswer == null || mAnswer.length() <= 0) {
                    taskException = new BankException(app.getString(R.string.enter_answer));
                    return RESULT_FAILURE;
                }

                final HashMap<String, Object> response = app.getApiClient().postSecurityQuestion(mAnswer, mRemember, mDeviceInfoRsa, mRsaCookie);

                Utils.setPrefsString(MiBancoConstants.RSA_COOKIE, "", context);
                if (response.containsKey("rsa_cookie")) {
                    String rsaCookie = String.class.cast(response.get("rsa_cookie"));
                    Utils.setPrefsString(MiBancoConstants.RSA_COOKIE, rsaCookie, context);
                }
                responderName = (String) response.get("responder_name");
                boolean accountBlocked = (Boolean) response.get("access_blocked");

                if (responderName == null || responderName.equalsIgnoreCase("login")) {
                    if (accountBlocked) {
                        taskException = new AccessBlockedException(app.getString(R.string.account_blocked_title));
                        BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_USERNAME_BLOCKED);
                    } else {
                        taskException = new BankException(app.getString(R.string.session_has_expired));
                        BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_BACK_TO_USERNAME_AFTER_CHALLENGE);
                    }
                    return RESULT_FAILURE;
                }

                if (responderName.equalsIgnoreCase("notAvailable")) {
                    taskException = new NotAvailableException(app.getString(R.string.not_available_description));
                    return RESULT_FAILURE;
                }

                if (responderName.equalsIgnoreCase("question")) {
                    taskException = new BankException(app.getString(R.string.enter_valid_answer));
                    question = (String) response.get("question");
                    BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_INCORRECT_ANSWER_RETRY_CHALLENGE);
                    return RESULT_SUCCESS;
                }

                if (responderName.contains("password")) {
                    CryptoUtils crypto = new CryptoUtils();
                    String keyDecrypt = (response.get("strKey") instanceof String ? (String)response.get("strKey") : null);
                    String decryptPhNumber = "";
                    if(keyDecrypt != null && !keyDecrypt.isEmpty()){
                        decryptPhNumber = crypto.decrypt(keyDecrypt, keyDecrypt, response.get("phoneNumber").toString());
                    }
                    Object customerTokenObj = response.get("customerToken");
                    String customerToken = customerTokenObj != null && customerTokenObj instanceof String ? customerTokenObj.toString() : null;
                    App.getApplicationInstance().setCustomerPhone(decryptPhNumber);
                    App.getApplicationInstance().setCustomerToken(customerToken);
                }

                String opacFlag = (String) response.get(MiBancoConstants.CAN_OPEN_ACCOUNT);
                if (responderName.contains("password") && opacFlag != null && opacFlag.equals("true")) {
                    final HashMap<String, String> opac = new HashMap<>();
                    opac.put(MiBancoConstants.CAN_OPEN_ACCOUNT, (String) response.get(MiBancoConstants.CAN_OPEN_ACCOUNT));
                    opac.put(MiBancoConstants.IS_FOREING_CUSTOMER, (String) response.get(MiBancoConstants.IS_FOREING_CUSTOMER));
                    MiBancoPreferences.setOpac(opac);
                }
                BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_CHALLENGE_SUCCESSFUL_ASKED_FOR_PASSWORD);
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS) {
                ((ResponderListener) taskListener).responder(responderName, question);
            }
        }
    }

    /**
     * Submits a payment.
     */
    protected class QuickPaymentTask extends SessionAsyncTask {

        /**
         * The account from.
         */
        private final String accountFrom;

        /**
         * The amount.
         */
        private final String amount;

        /**
         * The effective date.
         */
        private final String effectiveDate;

        /**
         * Needs confirmation?.
         */
        private final boolean needsConfirmation;

        /**
         * The payee id.
         */
        private final String payeeId;

        /**
         * The payment object.
         */
        private PaymentActive payment;

        /**
         * Instantiates a new quick payment task.
         *
         * @param context            the context
         * @param aAccountFrom       the account from
         * @param aAmount            the amount
         * @param aEffectiveDate     the effective date
         * @param aPayeeId           the payee id
         * @param aNeedsConfirmation needs confirmation?
         * @param listener           the listener
         */
        public QuickPaymentTask(final Context context, final String aAccountFrom, final String aAmount, final String aEffectiveDate, final String aPayeeId, final boolean aNeedsConfirmation,
                                final ResponderListener listener) {
            super(context, listener, true, context.getString(R.string.processing_transaction));
            accountFrom = aAccountFrom;
            amount = aAmount;
            effectiveDate = aEffectiveDate;
            payeeId = aPayeeId;
            needsConfirmation = aNeedsConfirmation;
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("QuickPaymentTask");
            try {
                payment = app.getApiClient().makePayment(accountFrom, amount, effectiveDate, payeeId, needsConfirmation);

                responderName = payment.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    taskException = new BankException(app.getString(R.string.session_has_expired));
                    return RESULT_FAILURE;
                }

                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(null, payment);
            }
        }
    }

    // SESSION

    /**
     * Performs a session ping to keep the session alive.
     */
    protected class SessionPingTask extends BaseAsyncTask {

        /**
         * Instantiates a new SessionPingTask.
         *
         * @param context  the context
         * @param listener the listener
         */
        public SessionPingTask(final Context context, final ResponderListener listener) {
            super(context, listener, false);
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("SessionPingTask");
            try {
                responderName = app.getApiClient().sessionPing();
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                if (responderName != null) {
                    ((ResponderListener) taskListener).sessionHasExpired();
                } else {
                    ((ResponderListener) taskListener).responder(responderName, null);
                }
            }
        }
    }

    // APPLICATION START

    /**
     * Fetches background image URL and global banking status.
     */
    protected class StartTask extends BaseAsyncTask {

        /**
         * The login data.
         */
        private LoginGet loginData;

        /**
         * Instantiates a new start task.
         *
         * @param context      the context
         * @param listener     the listener
         * @param showProgress show progress indicator?
         */
        public StartTask(final Context context, final StartListener listener, boolean showProgress) {
            super(context, listener, showProgress);
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("StartTask");
            try {
                final CustomGsonParser gson = new CustomGsonParser();
                String response = app.getApiClient().globalStatus();
                if (response.isEmpty()) {
                    return -1;
                }

                app.setGlobalStatus(gson.fromJson(response, GlobalStatus.class));
                App.getApplicationInstance().setGlobalStatus(app.getGlobalStatus());

                if (Utils.isUpdateRequired(app.getGlobalStatus().getMinimumVersion())) {
                    taskException = new UpdateRequiredException(app.getString(R.string.update_description));
                    return RESULT_FAILURE;
                }

                if (!app.getGlobalStatus().isBanking()) {
                    taskException = new MaintenancePersonalException(app.getString(R.string.maintenance_personal_banking));
                    return RESULT_FAILURE;
                }

                if (!app.getGlobalStatus().isMobileBanking() || !app.getGlobalStatus().isSignon()) {
                    taskException = new MaintenanceMobileException(app.getString(R.string.maintenance_mobile_banking));
                    return RESULT_FAILURE;
                }
                if (App.getApplicationInstance().isSessionNeeded()) {
                    loginData = app.getApiClient().fetchLogin();
                }

                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }

        @Override
        protected void onCancelled() {
            globalStatusTaskRunning = false;
            super.onCancelled();
        }


        @Override
        protected void onPostExecute(final Integer result) {
            globalStatusTaskRunning = false;
            Utils.dismissDialog(globalStatusDialog);
            globalStatusDialog = null;

            super.onPostExecute(result);
            if (result == RESULT_SUCCESS) {
                ((StartListener) taskListener).savedData(loginData);
            }
        }
    }

    /**
     * Submits a transfer.
     */
    protected class TransferTask extends SessionAsyncTask {

        /**
         * The account from.
         */
        private final String accountFrom;

        /**
         * The account to.
         */
        private final String accountTo;

        /**
         * The amount.
         */
        private final String amount;

        /**
         * The effective date.
         */
        private final String effectiveDate;

        /**
         * Needs confirmation?.
         */
        private final boolean needsConfirmation;

        /**
         * Is recurrent?.
         */
        private final String recurrent;

        /**
         * The current transfer.
         */
        private TransferActive transfer;

        /**
         * Instantiates a new TransferTask.
         *
         * @param context            the context
         * @param aAccountFrom       the account from
         * @param aAccountTo         the account to
         * @param aAmount            the amount
         * @param aEffectiveDate     the effective date
         * @param aReccurent         is reccurent?
         * @param aNeedsConfirmation needs confirmation?
         * @param listener           the listener
         */
        public TransferTask(final Context context, final String aAccountFrom, final String aAccountTo, final String aAmount, final String aEffectiveDate, final String aReccurent,
                            final boolean aNeedsConfirmation, final ResponderListener listener) {
            super(context, listener, true, context.getString(R.string.processing_transaction));
            accountFrom = aAccountFrom;
            accountTo = aAccountTo;
            amount = aAmount;
            effectiveDate = aEffectiveDate;
            recurrent = aReccurent;
            needsConfirmation = aNeedsConfirmation;
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("TransferTask");
            try {
                transfer = app.getApiClient().makeTransfer(accountFrom, accountTo, amount, effectiveDate, recurrent, needsConfirmation);

                responderName = transfer.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    taskException = new BankException(app.getString(R.string.session_has_expired));
                    return RESULT_FAILURE;
                }

                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(null, transfer);
            }
        }
    }

    /**
     * Executes PasswordTask.
     *
     * @param context  the context
     * @param password the password
     * @param listener the listener
     */
    public void password(final Context context, final String password, final ResponderListener listener) {
        new PasswordTask(context, password, listener).execute();
    }

    /**
     * Executes AuthenticationTask.
     *
     * @param context  the context
     * @param json     the json from wallet
     * @param listener the listener
     */
    public void walletAuthentication(final Context context, final String json, final String walletType, final ResponderListener listener) {
        new AuthenticationTask(context, json, walletType, listener).execute();
    }

    /**
     * Executes QuestionTask.
     *
     * @param context  the context
     * @param answer   the answer
     * @param remember remember device?
     * @param listener the listener
     */
    public void question(final Context context, final String answer, final boolean remember, final ResponderListener listener) {
        final String deviceInfoJson = RSACollectUtils.collectDeviceInfo(context);
        final String rsaCookie = Utils.getPrefsStringNotNull(MiBancoConstants.RSA_COOKIE, context);

        QuestionTask task = new QuestionTask(context, answer, remember, deviceInfoJson, rsaCookie, listener);
        addTaskToCancelOnDestroy(context, task);
        task.execute();
    }

    /**
     * Executes QuickPaymentTask.
     *
     * @param context           the context
     * @param accountFrom       the account from
     * @param amount            the amount
     * @param effectiveDate     the effective date
     * @param payeeId           the payee id
     * @param needsConfirmation needs confirmation?
     * @param listener          the listener
     */
    public void quickPayment(final Context context, final String accountFrom, final String amount, final String effectiveDate, final String payeeId, final boolean needsConfirmation,
                             final ResponderListener listener) {
        final QuickPaymentTask task = new QuickPaymentTask(context, accountFrom, amount, effectiveDate, payeeId, needsConfirmation, listener);
        task.execute();
    }

    /**
     * Executes SessionPingTask.
     *
     * @param context  the context
     * @param listener the listener
     */
    public void sessionPing(final Context context, final ResponderListener listener) {
        new SessionPingTask(context, listener).execute();
    }

    /**
     * Executes StartTask.
     *
     * @param context      the context
     * @param listener     the listener
     * @param showProgress show progress indicator?
     * @param forceReload  force reload, even if already loaded?
     */
    public void startApp(final Context context, final StartListener listener, final boolean showProgress, final boolean forceReload) {
        if (app.getLoginGet() == null || forceReload) {
            if (globalStatusTaskRunning) {
                globalStatusTask.setContext(context);
                globalStatusTask.setListener(listener);
                if (showProgress) {
                    globalStatusDialog = new ProgressDialog(context, AlertDialog.THEME_HOLO_LIGHT);
                    globalStatusDialog.setCancelable(false);
                    globalStatusDialog.setMessage(context.getString(R.string.please_wait));
                    Utils.showDialog(globalStatusDialog, context);
                }
                styleProgressDialog(globalStatusDialog);
            } else {
                globalStatusTask = new StartTask(context, listener, showProgress);
                globalStatusTaskRunning = true;
                globalStatusTask.execute();
            }
        } else {
            listener.savedData(app.getLoginGet());
        }
    }

    /**
     * Styles a progress dialog.
     *
     * @param dialog the dialog to style
     */
    private void styleProgressDialog(final ProgressDialog dialog) {
        if (dialog != null) {
            FontChanger.changeFonts(dialog.findViewById(android.R.id.content));
        }
    }

    // OTHER

    /**
     * Fetches payments information for the logged in user.
     *
     * @param context      the context
     * @param listener     the listener
     * @param showProgress show progress indicator?
     * @param forceReload  force reload, even if already loaded?
     */
    public void fetchPayments(final Context context, final ResponderListener listener, final boolean showProgress, final boolean forceReload) {
        if (app.getPaymentsInfo() == null || forceReload) {
            if (paymentsTaskRunning) {
                fetchPaymentsTask.setContext(context);
                fetchPaymentsTask.setListener(listener);
                if (showProgress) {
                    paymentsDialog = new ProgressDialog(context, AlertDialog.THEME_HOLO_LIGHT);
                    paymentsDialog.setCancelable(false);
                    paymentsDialog.setMessage(context.getString(R.string.loading_payments));
                    Utils.showDialog(paymentsDialog, context);
                }
                styleProgressDialog(paymentsDialog);
            } else {
                fetchPaymentsTask = new FetchPaymentsTask(context, listener, showProgress, context.getString(R.string.loading_payments));
                paymentsTaskRunning = true;
                fetchPaymentsTask.execute();
            }
        } else {
            listener.responder(app.getPaymentsInfo().getResponderName(), app.getPaymentsInfo());
        }
    }

    /**
     * Fetches transfers information for the logged in user.
     *
     * @param context      the context
     * @param listener     the listener
     * @param showProgress show progress indicator?
     * @param forceReload  force reload, even if already loaded?
     */
    public void fetchTransfers(final Context context, final ResponderListener listener, final boolean showProgress, final boolean forceReload) {
        if (app.getTransfersInfo() == null || forceReload) {
            if (transfersTaskRunning) {
                fetchTransfersTask.setContext(context);
                fetchTransfersTask.setListener(listener);
                if (showProgress) {
                    transfersDialog = new ProgressDialog(context, AlertDialog.THEME_HOLO_LIGHT);
                    transfersDialog.setCancelable(false);
                    transfersDialog.setMessage(context.getString(R.string.loading_transfers));
                    Utils.showDialog(transfersDialog, context);
                    styleProgressDialog(transfersDialog);
                }
            } else {
                fetchTransfersTask = new FetchTransfersTask(context, listener, showProgress, context.getString(R.string.loading_transfers));
                transfersTaskRunning = true;
                fetchTransfersTask.execute();
            }
        } else {
            listener.responder(app.getTransfersInfo().getResponderName(), app.getTransfersInfo());
        }
    }

    /**
     * Executes LoginTask.
     *
     * @param context  the context
     * @param username the username
     * @param listener the listener
     */
    public void login(final Context context, final String username, final String deviceIdentifier, final ResponderListener listener) {
        final String deviceInfoJson = RSACollectUtils.collectDeviceInfo(context);
        final String rsaCookie = Utils.getPrefsStringNotNull(MiBancoConstants.RSA_COOKIE, context);

        new LoginTask(context, username, deviceIdentifier, deviceInfoJson, rsaCookie, listener).execute();
    }

    /**
     * Executes LogoutTask.
     *
     * @param context  the context
     * @param expired  the expired flag
     * @param listener the listener
     */
    public void logout(final Context context, final boolean expired, final SimpleListener listener) {
        new LogoutTask(context, expired, listener).execute();
    }

    /**
     * Submits a transfer.
     *
     * @param context           the context
     * @param accountFrom       the account from
     * @param accountTo         the account to
     * @param amount            the amount
     * @param effectiveDate     the effective date
     * @param recurrent         is recurrent?
     * @param needsConfirmation needs confirmation?
     * @param listener          the listener
     */
    public void makeTransfer(final Context context, final String accountFrom, final String accountTo, final String amount, final String effectiveDate, final String recurrent,
                             final boolean needsConfirmation, final ResponderListener listener) {
        new TransferTask(context, accountFrom, accountTo, amount, effectiveDate, recurrent, needsConfirmation, listener).execute();
    }

    /**
     * Gets and updates locations of ATMs and branches.
     *
     * @param context  the context
     * @param listener the listener
     */
    public void getLocations(final Context context, final LocationsListener listener) {
        if (app.getAtms() == null || app.getBranches() == null) {
            new GetLocationsTask(context, new LocationsListener() {

                @Override
                public void updateATMs(final List<BankLocation> aAtms) {
                    listener.updateATMs(aAtms);
                }

                @Override
                public void updateBranches(final List<BankLocation> aBranches) {
                    listener.updateBranches(aBranches);
                }

                @Override
                public void updateLocations(final List<BankLocation> aAtms, final List<BankLocation> aBranches) {
                    listener.updateLocations(aAtms, aBranches);
                }

                @Override
                public void updateBranchDetail(BankLocationDetail bankLocationDetail) {
                    listener.updateBranchDetail(bankLocationDetail);
                }
            }).execute();
        } else {
            listener.updateLocations(app.getAtms(), app.getBranches());
            listener.updateBranches(app.getBranches());
            listener.updateATMs(app.getAtms());
        }
    }

    public void getLocationDetails(final Context context, String locationId, final LocationsListener listener) {
        new GetLocationDetailsTask(context, locationId, new LocationsListener() {

            @Override
            public void updateATMs(final List<BankLocation> aAtms) {
                listener.updateATMs(aAtms);
            }

            @Override
            public void updateBranches(final List<BankLocation> aBranches) {
                listener.updateBranches(aBranches);
            }

            @Override
            public void updateLocations(final List<BankLocation> aAtms, final List<BankLocation> aBranches) {
                listener.updateLocations(aAtms, aBranches);
            }

            @Override
            public void updateBranchDetail(BankLocationDetail bankLocationDetail) {
                listener.updateBranchDetail(bankLocationDetail);
            }
        }).execute();
    }

    /**
     * Gets the transactions.
     *
     * @param context  the context
     * @param account  the account
     * @param cycle    the cycle
     * @param pageNr   the page nr
     * @param listener the listener
     */
    public void getTransactions(final Context context, final String account, final int cycle, final int pageNr, final TransactionsListener listener) {
        if (app.getTransactions() != null && app.getTransactions().containsKey(account) && app.getTransactions().get(account).get(cycle) != null
                && app.getTransactions().get(account).get(cycle).get(pageNr) != null) {
            listener.onTransactionsUpdated(app.getTransactions().get(account).get(cycle).get(pageNr));
        } else {
            new GetTransactionsTask(context, account, cycle, pageNr, listener).execute();
        }
    }

    /**
     * Gets the account available transaction cycles.
     *
     * @param context  the context
     * @param account  the account
     * @param listener the listener
     */
    public void getAccountAvailableCycles(final Context context, final String account, final TransactionsListener listener) {
        getTransactions(context, account, 1, 1, listener);
    }

    /**
     * Null out all background tasks resources and flags to make sure we don't use any stale data after new login.
     */
    public void stopRunningTasks() {
        if (fetchPaymentsTask != null) {
            fetchPaymentsTask.cancel(true);
            fetchPaymentsTask = null;
        }
        paymentsTaskRunning = false;
        app.setPaymentsInfo(null);
        app.setEbills(null);
        Utils.dismissDialog(paymentsDialog);
        paymentsDialog = null;

        if (fetchTransfersTask != null) {
            fetchTransfersTask.cancel(true);
            fetchTransfersTask = null;
        }
        transfersTaskRunning = false;
        app.setTransfersInfo(null);
        Utils.dismissDialog(transfersDialog);
        transfersDialog = null;

        if (globalStatusTask != null) {
            globalStatusTask.cancel(true);
            globalStatusTask = null;
        }
        globalStatusTaskRunning = false;
        app.setGlobalStatus(null);
        Utils.dismissDialog(globalStatusDialog);
        globalStatusDialog = null;

        Utils.dismissDialog(app.getDialogCoverupUpdateBalances());
        app.setDialogCoverupUpdateBalances(null);
        app.setUpdatingBalances(false);
        app.setPortalDelayInEffect(false);
        app.setDepositCheckInformationFromSession(false);
        app.setRdcClientEnrolled(false);
        app.setRdcClientAcceptedTerms(false);
    }

    /**
     * Loads payment accounts details from JSON wrapper object to WheelView.
     */
    protected class LoadPaymentsCardsTask extends BaseAsyncTask {

        /**
         * The quick payment JSON response object.
         */
        private final Payment quickPayment;

        /**
         * Items to be loaded to the left side of a WheelView.
         */
        private final List<ArrayBankWheelItem> itemsFrom;

        /**
         * Items to be loaded to the right side of a WheelView.
         */
        private final List<ArrayBankWheelItem> itemsTo;

        /**
         * Instantiates a new LoadPaymentsCardsTask.
         *
         * @param context       the context
         * @param listener      the listener
         * @param aQuickPayment the quick payment JSON response object
         * @param outItemsFrom  items to be loaded to the left side of a WheelView (out argument)
         * @param outItemsTo    items to be loaded to the right side of a WheelView (out argument)
         * @param showProgress  show progress indicator?
         */
        public LoadPaymentsCardsTask(final Context context, final SimpleListener listener, final Payment aQuickPayment, final List<ArrayBankWheelItem> outItemsFrom,
                                     final List<ArrayBankWheelItem> outItemsTo, final boolean showProgress) {
            super(context, listener, showProgress, app.getString(R.string.loading_payments));
            this.quickPayment = aQuickPayment;
            this.itemsFrom = outItemsFrom;
            this.itemsTo = outItemsTo;
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("PaymentsTask");
            try {
                final Customer loggedInCustomer = app.getLoggedInUser();

                itemsFrom.clear();
                for (final PaymentAccount account : quickPayment.getAccountsFrom()) {
                    if (!account.getAccountId().equals("none")) {
                        final ArrayBankWheelItem item = new ArrayBankWheelItem(account.getAccountId());

                        final CustomerAccount foundAccount = setPaymentWheelItemImage(item, account.getAccountId(), loggedInCustomer);
                        if (foundAccount != null) {
                            item.setBalanceRed(foundAccount.isBalanceColorRed());
                        }
                        item.setName(account.getAccountName());
                        item.setCode(account.getAccountLast4Num());
                        item.setAmount(account.getAccountBalance());
                        item.setTitle(account.getAccountTitle());
                        itemsFrom.add(item);
                    }
                }

                itemsTo.clear();
                for (final PaymentAccount account : quickPayment.getAccountsTo()) {
                    if (!account.getAccountId().equals("none")) {
                        final ArrayBankWheelItem item = new ArrayBankWheelItem(account.getAccountId());
                        item.setName(account.getAccountName());
                        item.setCode(account.getAccountLast4Num());
                        item.setTitle(account.getAccountTitle());
                        item.setImgResource(Utils.getPayeeDrawableResource(account.getGlobalPayeeId()));
                        item.setPayeeId(account.getGlobalPayeeId());
                        item.setRtNotificatiion(account.getRtNotification());
                        item.setRtHasPaymentHistory(account.getRtHasPaymentHistory());
                        itemsTo.add(item);
                    }
                }

                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }

        /**
         * Sets the payment wheel item image.
         *
         * @param item             the wheel item
         * @param accountId        the account id
         * @param loggedInCustomer the logged in customer
         * @return the customer account with matching id or null if account was not found
         */
        private CustomerAccount setPaymentWheelItemImage(final ArrayBankWheelItem item, final String accountId, final Customer loggedInCustomer) {
            CustomerAccount foundAccount = null;
            for (final CustomerAccount customerAccount : loggedInCustomer.getDepositAccounts()) {
                if (customerAccount.getFrontEndId().equals(accountId)) {
                    setImages(item, customerAccount);
                    foundAccount = customerAccount;
                    break;
                }
            }
            if (item.getImgResource() == 0) {
                for (final CustomerAccount customerAccount : loggedInCustomer.getCreditCards()) {
                    if (customerAccount.getFrontEndId().equals(accountId)) {
                        setImages(item, customerAccount);
                        foundAccount = customerAccount;
                        break;
                    }
                }
            }

            // set default image
            if (item.getImgResource() == 0) {
                item.setImgResource(R.drawable.merchant_image_default);
            }

            return foundAccount;
        }

        /**
         * Sets wheel item's image.
         *
         * @param item            the wheel item
         * @param customerAccount the customer account
         */
        private void setImages(final ArrayBankWheelItem item, final CustomerAccount customerAccount) {
            final String path = Utils.getAccountImagePath(customerAccount, context);
            item.setImgResource(customerAccount.getWheelImgResource());
            item.setImgPath(path);
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && !Payments.isPaymentsDestroyed()) {
                if (taskListener != null) {
                    ((SimpleListener) taskListener).done();
                }
            }
        }
    }

    /**
     * Loads payment accounts details from JSON wrapper object to WheelView.
     *
     * @param context       the context
     * @param listener      the listener
     * @param aQuickPayment the quick payment JSON response object
     * @param outItemsFrom  items to be loaded to the left side of a WheelView (out argument)
     * @param outItemsTo    items to be loaded to the right side of a WheelView (out argument)
     * @param showProgress  show progress indicator?
     */
    public void loadPaymentsCards(final Context context, final SimpleListener listener, final Payment aQuickPayment, final List<ArrayBankWheelItem> outItemsFrom,
                                  final List<ArrayBankWheelItem> outItemsTo, final boolean showProgress) {
        LoadPaymentsCardsTask task = new LoadPaymentsCardsTask(context, listener, aQuickPayment, outItemsFrom, outItemsTo, showProgress);
        addTaskToCancelOnDestroy(context, task);
        task.execute();
    }

    /**
     * Loads transfers accounts details from JSON wrapper object to WheelView.
     */
    protected class LoadTransfersCardsTask extends BaseAsyncTask {

        /**
         * The Constant MAX_ACCOUNT_NAME_LENGTH.
         */
        private final static int MAX_ACCOUNT_NAME_LENGTH = 15;

        /**
         * The Constant TRUNCATED_ACCOUNT_NAME_LENGTH.
         */
        private final static int TRUNCATED_ACCOUNT_NAME_LENGTH = 12;

        /**
         * The transfer JSON response object.
         */
        private final Transfer transfer;

        /**
         * Items to be loaded to the left side of a WheelView.
         */
        private final List<ArrayBankWheelItem> itemsFrom;

        /**
         * Items to be loaded to the right side of a WheelView.
         */
        private final List<ArrayBankWheelItem> itemsTo;

        /**
         * Instantiates a new transfers task.
         *
         * @param context      the context
         * @param listener     the listener
         * @param aTransfer    the transfer JSON response object
         * @param outItemsFrom items to be loaded to the left side of a WheelView (out argument)
         * @param outItemsTo   items to be loaded to the right side of a WheelView (out argument)
         * @param showProgress show progress indicator?
         */
        public LoadTransfersCardsTask(final Context context, final SimpleListener listener, final Transfer aTransfer, final List<ArrayBankWheelItem> outItemsFrom,
                                      final List<ArrayBankWheelItem> outItemsTo, final boolean showProgress) {
            super(context, listener, showProgress, app.getString(R.string.loading_transfers));
            transfer = aTransfer;
            this.itemsFrom = outItemsFrom;
            this.itemsTo = outItemsTo;
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("TransfersTask");
            try {
                itemsFrom.clear();
                for (final TransferAccount account : transfer.getAccountsFrom()) {
                    if (!account.getAccountId().equals("none")) {
                        final ArrayBankWheelItem item = new ArrayBankWheelItem(account.getAccountId());

                        final CustomerAccount foundAccount = setTransferWheelItemImage(item, account.getAccountTitle());
                        if (foundAccount != null) {
                            item.setBalanceRed(foundAccount.isBalanceColorRed());
                        }
                        item.setName(account.getAccountName());
                        item.setCode(account.getAccountLast4Num());
                        item.setTitle(account.getAccountTitle());
                        item.setAmount(account.getAccountBalance());
                        itemsFrom.add(item);
                    }
                }

                itemsTo.clear();
                for (final TransferAccount account : transfer.getAccountsTo()) {
                    if (!account.getAccountId().equals("none")) {
                        final ArrayBankWheelItem item = new ArrayBankWheelItem(account.getAccountId());

                        final CustomerAccount foundAccount = setTransferWheelItemImage(item, account.getAccountTitle());
                        if (foundAccount != null) {
                            item.setBalanceRed(foundAccount.isBalanceColorRed());
                        }
                        item.setName(account.getAccountName());
                        item.setCode(account.getAccountLast4Num());
                        item.setTitle(account.getAccountTitle());
                        item.setAmount(account.getAccountBalance());
                        itemsTo.add(item);
                    }
                }

                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }

        /**
         * Sets the transfer wheel item image.
         *
         * @param item         the wheel item to set values on it
         * @param accountTitle the account title
         * @return the matching customer account
         */
        private CustomerAccount setTransferWheelItemImage(final ArrayBankWheelItem item, final String accountTitle) {
            CustomerAccount foundAccount = null;
            /*
             * We use account nickname and perform some quirky String operations, because account entries fetched from web service don't contain
             * account suffix field in 'properties'. Some accounts may have the same name and code, so the only way to differentiate them is to take
             * account suffix into consideration.
             */
            for (final CustomerAccount customerAccount : app.getLoggedInUser().getDepositAccounts()) {
                String accountName = customerAccount.getNickname();
                if (accountName.length() > MAX_ACCOUNT_NAME_LENGTH) {
                    accountName = accountName.substring(0, TRUNCATED_ACCOUNT_NAME_LENGTH) + "...";
                }

                final String accountIdentifier = accountName + " " + customerAccount.getAccountLast4Num() + " " + App.cleanBalanceString(customerAccount.getPortalBalance()) + "  " + customerAccount.getAccountNumberSuffix();

                if (cutOutBalance(accountIdentifier.trim()).equals(cutOutBalance(accountTitle.trim()))) {
                    setImages(item, customerAccount);
                    foundAccount = customerAccount;
                    break;
                }
            }
            if (item.getImgResource() == 0) {
                for (final CustomerAccount customerAccount : app.getLoggedInUser().getCreditCards()) {
                    String accountName = customerAccount.getNickname();
                    if (accountName.length() > MAX_ACCOUNT_NAME_LENGTH) {
                        accountName = accountName.substring(0, TRUNCATED_ACCOUNT_NAME_LENGTH) + "...";
                    }

                    final String accountIdentifier = accountName + " " + customerAccount.getAccountLast4Num() + " " + App.cleanBalanceString(customerAccount.getPortalBalance()) + "  " + customerAccount.getAccountNumberSuffix();

                    if (cutOutBalance(accountIdentifier.trim()).equals(cutOutBalance(accountTitle.trim()))) {
                        setImages(item, customerAccount);
                        foundAccount = customerAccount;
                        break;
                    }
                }
            }

            // set default image
            if (item.getImgResource() == 0) {
                item.setImgResource(R.drawable.merchant_image_default);
            }

            return foundAccount;
        }

        /**
         * Sets wheel item's image.
         *
         * @param item            the wheel item
         * @param customerAccount the customer account
         */
        private void setImages(final ArrayBankWheelItem item, final CustomerAccount customerAccount) {
            final String path = Utils.getAccountImagePath(customerAccount, context);
            item.setImgResource(customerAccount.getWheelImgResource());
            item.setImgPath(path);
        }

        /**
         * Cut out the balance.
         *
         * @param input the input amount string
         * @return the balance string without the currency sign
         */
        private String cutOutBalance(final String input) {
            final int dollarIndex = input.indexOf(MiBancoConstants.CURRENCY_SYMBOL);
            if (dollarIndex != -1) {
                final int spaceAfterDollarIndex = input.indexOf(' ', dollarIndex);
                if (spaceAfterDollarIndex != -1) {
                    return input.substring(0, dollarIndex) + input.substring(spaceAfterDollarIndex + 1);
                } else {
                    return input.substring(0, dollarIndex);
                }
            }
            return input;
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && !Payments.isTransfersDestroyed()) {
                if (taskListener != null) {
                    ((SimpleListener) taskListener).done();
                }
            }
        }
    }

    /**
     * Loads transfer accounts details from JSON wrapper object to WheelView.
     *
     * @param context      the context
     * @param listener     the listener
     * @param aTransfer    the transfer JSON response object
     * @param outItemsFrom items to be loaded to the left side of a WheelView (out argument)
     * @param outItemsTo   items to be loaded to the right side of a WheelView (out argument)
     * @param showProgress show progress indicator?
     */
    public void loadTransfersCards(final Context context, final SimpleListener listener, final Transfer aTransfer, final List<ArrayBankWheelItem> outItemsFrom,
                                   final List<ArrayBankWheelItem> outItemsTo, final boolean showProgress) {
        LoadTransfersCardsTask task = new LoadTransfersCardsTask(context, listener, aTransfer, outItemsFrom, outItemsTo, showProgress);
        addTaskToCancelOnDestroy(context, task);
        task.execute();
    }

    /**
     * Reloads account information after 7 seconds of transaction submission.
     */
    protected class PortalTimerTask extends SessionAsyncTask {

        /**
         * The Constant PORTAL_DELAY_MILIS.
         */
        public final static int PORTAL_DELAY_MILIS = 7000;

        /**
         * Instantiates a new TransferTask.
         *
         * @param context the context
         */
        public PortalTimerTask(final Context context) {
            super(context, null, false);
            app.setUpdatingBalances(true);
            app.setPortalDelayInEffect(true);
        }

        @Override
        protected void onCancelled() {
            app.setUpdatingBalances(false);
            app.setPortalDelayInEffect(false);
            super.onCancelled();
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("PortalTimerTask");
            try {
                new Timer("PortalDelayTimer", true).schedule(new TimerTask() {

                    @Override
                    public void run() {
                        app.setPortalDelayInEffect(false);
                    }
                }, PORTAL_DELAY_MILIS);

                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
        }
    }

    /**
     * Reloads account information after 7 seconds of transaction submission.
     *
     * @param context the context
     */
    public void updateBalances(final Context context) {
        new PortalTimerTask(context).execute();
    }

    /**
     * Adds a task to the context's list of task to cancel on destroy.
     *
     * @param context the context containing the list of tasks
     * @param task    the task to add to the list
     */
    private void addTaskToCancelOnDestroy(Context context, BaseAsyncTask task) {
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).getBaseActivityHelper().addTaskToCancelOnDestroy(task);
        }
    }

    // PAYMENT/TRANSFER HISTORY

    /**
     * Fetches payment history from the web service.
     */
    private class FetchPaymentHistoryTask extends SessionAsyncTask {

        /**
         * Payment history object.
         */
        private PaymentHistory paymentHistory;

        /**
         * Payee ID String to filter results by or null if no filtering should be performed.
         */
        private String payeeId;

        /**
         * Date String to filter results by or null if no filtering should be performed.
         */
        private String date;


        /**
         * Instantiates a new FetchPaymentHistoryTask with filtering parameters (one must be null, otherwise payeeId is taken).
         *
         * @param context      the context
         * @param listener     the listener
         * @param showProgress show progress indicator?
         * @param payeeId      payee ID String to filter results by or null if no filtering should be performed.
         * @param date         date String to filter results by or null if no filtering should be performed
         */
        public FetchPaymentHistoryTask(final Context context, final ResponderListener listener, final boolean showProgress, final String payeeId, final String date) {
            super(context, listener, showProgress);
            this.payeeId = payeeId;
            this.date = date;
        }

        public int validateResponse(String responderName){
            if (responderName.equalsIgnoreCase("login")) {
                taskException = new BankException(app.getString(R.string.session_has_expired));
                return RESULT_FAILURE;
            } else if (!responderName.equalsIgnoreCase("payments")) {
                taskException = new BankException(context.getString(R.string.error_occurred), true);
                return RESULT_FAILURE;
            }

            return RESULT_SUCCESS;
        }



        @Override
        protected Integer doInBackground(final Object... params) {
            try {
                if (payeeId == null && date == null) {
                    paymentHistory = app.getApiClient().fetchLatestPaymentHistory();
                } else if (payeeId == null) {
                    paymentHistory = app.getApiClient().fetchPaymentHistoryByDate(date);
                } else {
                    paymentHistory = app.getApiClient().fetchPaymentHistoryByPayee(payeeId);
                }

                if(paymentHistory.getError() != null &&
                        paymentHistory.getHistory().size() == 0 && payeeId != null){
                    paymentHistory = app.getApiClient().fetchPaymentHistoryByPayee(payeeId);
                }


                responderName = paymentHistory.getResponderName();


                return  validateResponse(responderName);
            } catch (final Exception e) {
                return RESULT_FAILURE;
            }
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(responderName, paymentHistory);
            } else if (result == RESULT_FAILURE && taskListener != null) {
                ((ResponderListener) taskListener).responder("", null);
            }
        }
    }

    /**
     * Fetches a payment history list for the logged in user. Supports filtering by one field.
     *
     * @param context      the context
     * @param listener     the listener
     * @param showProgress show progress indicator?
     * @param payeeId      payee ID String to filter results by
     * @param date         date String to filter results by
     */
    public void fetchPaymentHistory(final Context context, final ResponderListener listener, final boolean showProgress, final String payeeId, final String date) {
        new FetchPaymentHistoryTask(context, listener, showProgress, payeeId, date).execute();
    }

    /**
     * Fetches transfer history from the web service.
     */
    private class FetchTransferHistoryTask extends SessionAsyncTask {

        /**
         * Transfer history object.
         */
        private TransferHistory transferHistory;

        /**
         * Recipient account number String to filter results by or null if no filtering should be performed.
         */
        private String accountTo;

        /**
         * Date String to filter results by or null if no filtering should be performed.
         */
        private String date;

        /**
         * Instantiates a new FetchTransferHistoryTask.
         *
         * @param context      the context
         * @param listener     the listener
         * @param showProgress show progress indicator?
         */
        public FetchTransferHistoryTask(final Context context, final ResponderListener listener, final boolean showProgress) {
            super(context, listener, showProgress);
            this.accountTo = null;
            this.date = null;
        }

        /**
         * Instantiates a new FetchTransferHistoryTask with filtering parameters (one must be null, otherwise payeeId is taken).
         *
         * @param context      the context
         * @param listener     the listener
         * @param showProgress show progress indicator?
         * @param accountTo    recipient account number String to filter results by or null if no filtering should be performed.
         * @param date         date String to filter results by or null if no filtering should be performed
         */
        public FetchTransferHistoryTask(final Context context, final ResponderListener listener, final boolean showProgress, final String accountTo, final String date) {
            super(context, listener, showProgress);
            this.accountTo = accountTo;
            this.date = date;
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            try {
                if (accountTo == null && date == null) {
                    transferHistory = app.getApiClient().fetchLatestTransferHistory();
                } else if (accountTo == null) {
                    transferHistory = app.getApiClient().fetchTransferHistoryByDate(date);
                } else {
                    transferHistory = app.getApiClient().fetchTransferHistoryByAccount(accountTo);
                }

                responderName = transferHistory.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    taskException = new BankException(app.getString(R.string.session_has_expired));
                    return RESULT_FAILURE;
                } else if (!responderName.equalsIgnoreCase("transfers")) {
                    taskException = new BankException(context.getString(R.string.error_occurred), true);
                    return RESULT_FAILURE;
                }
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                return RESULT_FAILURE;
            }
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(responderName, transferHistory);
            } else if (result == RESULT_FAILURE && taskListener != null) {
                ((ResponderListener) taskListener).responder("", null);
            }
        }
    }

    /**
     * Fetches a transfers history list for the logged in user. Supports filtering by one field.
     *
     * @param context      the context
     * @param listener     the listener
     * @param showProgress show progress indicator?
     * @param accountTo    recipient account number String to filter results by
     * @param date         date String to filter results by
     */
    public void fetchTransferHistory(final Context context, final ResponderListener listener, final boolean showProgress, final String accountTo, final String date) {
        new FetchTransferHistoryTask(context, listener, showProgress, accountTo, date).execute();
    }

    /**
     * Deletes an in process payment.
     */
    private class DeleteInProcessPaymentTask extends SessionAsyncTask {

        /**
         * The favId of the item to delete on the list counted from 0 (favId).
         */
        private String favId;

        /**
         * The ID of transaction (modPayment).
         */
        private String paymentId;

        private PaymentHistory paymentHistory;

        /**
         * Instantiates a new DeleteInProcessPaymentTask.
         *
         * @param context      the context
         * @param listener     the listener
         * @param showProgress show progress indicator?
         * @param favId        the favId of the item to delete on the list (favId)
         * @param paymentId    the ID of transaction (modPayment)
         */
        public DeleteInProcessPaymentTask(final Context context, final ResponderListener listener, final boolean showProgress, final String favId, final String paymentId) {
            super(context, listener, showProgress, context.getString(R.string.deleting));
            this.favId = favId;
            this.paymentId = paymentId;
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            try {
                paymentHistory = app.getApiClient().deleteInProcessPayment(favId, paymentId);
                //MBIM-356 INCIDENT 3815327
                if (app.isUpdatingBalances()) {
                    Customer customer = app.getApiClient().portal();
                    app.setLoggedInUser(customer);
                    app.loadAccounts();
                    responderName = customer.getResponderName();
                    if (responderName.equalsIgnoreCase("login")) {
                        taskException = new BankException(app.getString(R.string.session_has_expired));
                        return RESULT_FAILURE;
                    }
                }
                //ENDS MBIM-356
                responderName = paymentHistory.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    taskException = new BankException(app.getString(R.string.session_has_expired));
                    return RESULT_FAILURE;
                } else if (!responderName.equalsIgnoreCase("payments")) {
                    taskException = new BankException(context.getString(R.string.delete_error_description), true);
                    return RESULT_FAILURE;
                }
                app.getApiClient().removeMessage();
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                //MBIM-356 INCIDENT 3815327
                try {
                    app.setPaymentsInfo(app.getApiClient().fetchPayments());
                } catch (Exception e) {
                    Log.e("AsyncTasks", e.toString());
                }
                //ENDS MBIM-356
                ((ResponderListener) taskListener).responder(responderName, paymentHistory);
            }
        }
    }

    /**
     * Deletes an in process payment.
     *
     * @param context      the context
     * @param listener     the listener
     * @param showProgress show progress indicator?
     * @param favId        the favId of the item to delete on the list (favId)
     * @param paymentId    the ID of transaction (modPayment)
     */
    public void deleteInProcessPayment(final Context context, final ResponderListener listener, final boolean showProgress, final String favId, final String paymentId) {
        new DeleteInProcessPaymentTask(context, listener, showProgress, favId, paymentId).execute();
    }

    /**
     * Deletes an in process transfer.
     */
    private class DeleteInProcessTransferTask extends SessionAsyncTask {

        /**
         * The favId of the item to delete on the list counted from 0 (favId).
         */
        private String favId;

        /**
         * The ID of transaction (modTransfer).
         */
        private String transferId;

        private TransferHistory transferHistory;

        /**
         * Instantiates a new FetchTransferHistoryTask.
         *
         * @param context      the context
         * @param listener     the listener
         * @param showProgress show progress indicator?
         * @param favId        the favId of the item to delete on the list (favId)
         * @param transferId   the ID of transaction (modTransfer)
         */
        public DeleteInProcessTransferTask(final Context context, final ResponderListener listener, final boolean showProgress, final String favId, final String transferId) {
            super(context, listener, showProgress, context.getString(R.string.deleting));
            this.favId = favId;
            this.transferId = transferId;
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            try {
                transferHistory = app.getApiClient().deleteInProcessTransfer(favId, transferId);

                responderName = transferHistory.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    taskException = new BankException(app.getString(R.string.session_has_expired));
                    return RESULT_FAILURE;
                } else if (!responderName.equalsIgnoreCase("transfers")) {
                    taskException = new BankException(context.getString(R.string.delete_error_description), true);
                    return RESULT_FAILURE;
                }
                app.getApiClient().removeMessage();
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(responderName, transferHistory);
            }
        }
    }

    /**
     * Deletes an in process transfer.
     *
     * @param context      the context
     * @param listener     the listener
     * @param showProgress show progress indicator?
     * @param favId        the favId of the item to delete on the list (favId)
     * @param transferId   the ID of transaction (modTransfer)
     */
    public void deleteInProcessTransfer(final Context context, final ResponderListener listener, final boolean showProgress, final String favId, final String transferId) {
        new DeleteInProcessTransferTask(context, listener, showProgress, favId, transferId).execute();
    }

    public class SaveImageTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            try {
                if (params == null || params[0] == null || params[1] == null) {
                    return null;
                }
                final String filePath = (String) params[1];
                final File bgImageFile = new File(filePath);
                final FileOutputStream out = new FileOutputStream(bgImageFile);
                if (out != null) {
                    ((Bitmap) params[0]).compress(Bitmap.CompressFormat.PNG, MiBancoConstants.IMAGE_COMPRESSION_LEVEL, out);
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
                Log.e("AsyncTasks", e.toString());
            }
            return null;
        }
    }

    /**
     * Fetches RDC check deposit history from the web service.
     */
    private class FetchRemoteDepositHistoryTask extends SessionAsyncTask {

        private RemoteDepositHistory remoteDepositHistory;

        /**
         * Instantiates a new FetchRemoteDepositHistoryTask.
         *
         * @param context  the context
         * @param listener the listener
         */
        public FetchRemoteDepositHistoryTask(final Context context, final ResponderListener listener) {
            super(context, listener);
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("RemoteDepositHistory");
            try {
                remoteDepositHistory = app.getApiClient().fetchRemoteDepositHistory();
                app.setRemoteDepositHistory(remoteDepositHistory);
                responderName = remoteDepositHistory.getResponderName();

                if (responderName.equalsIgnoreCase("login")) {
                    taskException = new BankException(app.getString(R.string.session_has_expired));
                    return RESULT_FAILURE;
                } else if (!responderName.equalsIgnoreCase("remotedeposithistory")) {
                    taskException = new BankException(context.getString(R.string.error_occurred), true);
                    return RESULT_FAILURE;
                }
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(responderName, remoteDepositHistory);
            }
        }
    }

    /**
     * Fetches the list of RDC checks in the client's history.
     *
     * @param context  the context
     * @param listener the listener
     */
    public void fetchRemoteDepositHistory(final Context context, final ResponderListener listener) {
        new FetchRemoteDepositHistoryTask(context, listener).execute();
    }

    /**
     * Fetches a specific RDC check from the web service.
     */
    private class FetchRDCHistoryImagesTask extends SessionAsyncTask {

        private RDCCheckItem rdcHistoryCheckItem;

        private String referenceNumber;

        /**
         * Instantiates a new FetchReviewCheckTask.
         *
         * @param context  the context
         * @param listener the listener
         */
        public FetchRDCHistoryImagesTask(final Context context, final ResponderListener listener) {
            super(context, listener);
            this.referenceNumber = "";
        }

        /**
         * Instantiates a new FetchReviewCheckTask.
         *
         * @param context         the context
         * @param referenceNumber the reference number (deposit ID) to look up
         * @param listener        the listener
         */
        public FetchRDCHistoryImagesTask(final Context context, final String referenceNumber, final ResponderListener listener) {
            super(context, listener);
            this.referenceNumber = referenceNumber;
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("ReviewCheck");
            try {
                rdcHistoryCheckItem = app.getApiClient().fetchReviewCheck(referenceNumber);
                app.setReviewCheck(rdcHistoryCheckItem);
                responderName = rdcHistoryCheckItem.getResponderName();

                if (responderName.equalsIgnoreCase("login")) {
                    taskException = new BankException(app.getString(R.string.session_has_expired));
                    return RESULT_FAILURE;
                } else if (!responderName.equalsIgnoreCase("remotedeposithistoryimages")) {
                    taskException = new BankException(context.getString(R.string.error_occurred), true);
                    return RESULT_FAILURE;
                }
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                // If we receive an error here, ignore it and simply send back two null images
                app.setReviewCheck(null);
                return RESULT_SUCCESS;
            }
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(responderName, rdcHistoryCheckItem);
            }
        }
    }

    /**
     * Fetches the list of RDC checks in the client's history.
     *
     * @param context  the context
     * @param listener the listener
     */
    public void FetchRDCHistoryImagesTask(final Context context, final String referenceNumber, final ResponderListener listener) {
        new FetchRDCHistoryImagesTask(context, referenceNumber, listener).execute();
    }

    /**
     * Enrolls customer in RDC.
     */
    private class EnrollInRDCTask extends SessionAsyncTask {

        private DepositCheckEnrollment depositCheckEnrollment;

        /**
         * Instantiates a new EnrollInRDCTask.
         *
         * @param context  the context
         * @param listener the listener
         */
        public EnrollInRDCTask(final Context context, final ResponderListener listener) {
            super(context, listener);
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("EnrollInRDC");
            try {
                depositCheckEnrollment = app.getApiClient().enrollInRDC();
                responderName = depositCheckEnrollment.getResponderName();

                if (responderName.equalsIgnoreCase("login")) {
                    taskException = new BankException(app.getString(R.string.session_has_expired));
                    return RESULT_FAILURE;
                } else if (!responderName.equalsIgnoreCase("remotedepositrdcenroll")) {
                    taskException = new BankException(context.getString(R.string.error_occurred), true);
                    return RESULT_FAILURE;
                }
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(responderName, depositCheckEnrollment);
            }
        }
    }

    public void enrollInRDC(final Context context, final ResponderListener listener) {
        new EnrollInRDCTask(context, listener).execute();
    }

    private abstract class GenericTask extends SessionAsyncTask {

        private BaseFormResponse response;

        GenericTask(Context context, TaskListener listener) {
            super(context, listener);
        }

        @Override
        protected Integer doInBackground(Object... objects) {
            Thread.currentThread().setName(getApiClientServiceName());
            try {
                response = getApiClientService();
                responderName = response.getResponderName();

                if (responderName.equalsIgnoreCase("login")) {
                    taskException = new BankException(app.getString(R.string.session_has_expired));
                    return RESULT_FAILURE;
                } else if (!responderName.equalsIgnoreCase(getApiClientServiceName())) {
                    taskException = new BankException(context.getString(R.string.error_occurred), true);
                    return RESULT_FAILURE;
                }
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }

        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(responderName, response);
            }
        }

        abstract BaseFormResponse getApiClientService() throws Exception;

        abstract String getApiClientServiceName();

    }

    /**
     * Verify Accept Terms in RDC.
     */
    private class AcceptedTermsInRDCTask extends GenericTask {
        AcceptedTermsInRDCTask(Context context, TaskListener listener) {
            super(context, listener);
        }

        @Override
        BaseFormResponse getApiClientService() throws Exception {
            return app.getApiClient().acceptedTermsInRDC();
        }

        @Override
        String getApiClientServiceName() {
            return "getRemoteDepositEasyAcceptTerms";
        }
    }

    public void acceptedTermsInRDC(final Context context, final ResponderListener listener) {
        new AcceptedTermsInRDCTask(context, listener).execute();
    }

    /**
     * Accept Terms in RDC.
     */
    private class SendAcceptTermsInRDCTask extends GenericTask {
        SendAcceptTermsInRDCTask(Context context, TaskListener listener) {
            super(context, listener);
        }

        @Override
        BaseFormResponse getApiClientService() throws Exception {
            return app.getApiClient().sendAcceptTermsInRDC();
        }

        @Override
        String getApiClientServiceName() {
            return "remoteDepositEasyAcceptTerms";
        }
    }

    public void sendAcceptTermsInRDC(final Context context, final ResponderListener listener) {
        new SendAcceptTermsInRDCTask(context, listener).execute();
    }

    /**
     * Deposits a check.
     */
    private class DepositCheckTask extends SessionAsyncTask {

        private String frontendid;
        private String amount;
        private byte[] frontImage;
        private byte[] backImage;
        private DepositCheckReceipt depositCheckReceipt;

        /**
         * Instantiates a new DepositCheckTask.
         *
         * @param context  the context
         * @param listener the listener
         */
        public DepositCheckTask(final Context context, final ResponderListener listener) {
            super(context, listener);
            this.frontendid = null;
            this.amount = null;
            this.frontImage = null;
            this.backImage = null;
        }

        /**
         * Instantiates a new DepositCheckTask.
         *
         * @param context    the context
         * @param amount     the amount of the check
         * @param frontImage the byte array corresponding to the front image
         * @param backImage  the byte array corresponding to the back image
         * @param listener   the listener
         * @param frontendid the frontendid of the account to deposit to
         */
        public DepositCheckTask(final Context context, final String frontendid, final String amount, byte[] frontImage, byte[] backImage, final ResponderListener listener) {
            super(context, listener);
            this.frontendid = frontendid;
            this.amount = amount;
            this.frontImage = frontImage;
            this.backImage = backImage;
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("DepositCheck");
            try {
                // Convert the images to strings
                String frontImageString = "";
                String backImageString = "";

                if (frontImage != null && frontImage.length > 0) {
                    frontImageString = new String(Base64.encode(frontImage, Base64.DEFAULT));
                    frontImageString = URLEncoder.encode(frontImageString, "utf-8");
                }

                if (backImage != null && backImage.length > 0) {
                    backImageString = new String(Base64.encode(backImage, Base64.DEFAULT));
                    backImageString = URLEncoder.encode(backImageString, "utf-8");
                }

                depositCheckReceipt = app.getApiClient().depositCheck(frontendid, amount, frontImageString, backImageString);
                app.setDepositCheckReceipt(depositCheckReceipt);
                responderName = depositCheckReceipt.getResponderName();

                if (responderName.equalsIgnoreCase("login")) {
                    taskException = new BankException(app.getString(R.string.session_has_expired));
                    return RESULT_FAILURE;
                } else if (!responderName.equalsIgnoreCase("remotedepositsubmitdeposit")) {
                    taskException = new BankException(context.getString(R.string.error_occurred), true);
                    return RESULT_FAILURE;
                }
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(responderName, depositCheckReceipt);
            }
        }
    }

    /**
     * Deposits a check.
     *
     * @param context    the context
     * @param amount     the amount of the check
     * @param frontImage the byte array corresponding to the front image
     * @param backImage  the byte array corresponding to the back image
     * @param listener   the listener
     * @param frontendid the frontendid of the account to deposit to
     */
    public void depositCheck(final Context context, final String frontendid, String amount, byte[] frontImage, byte[] backImage, final ResponderListener listener) {
        new DepositCheckTask(context, frontendid, amount, frontImage, backImage, listener).execute();
    }

    /**
     * Fetches customer balances.
     */
    protected class GetBalancesTask extends SessionAsyncTask {

        /**
         * The customer JSON response.
         */
        private Customer customer;

        /**
         * The customer token
         */
        private String mCustomerToken;

        /**
         * Instantiates a new PortalTask.
         *
         * @param context  the context
         * @param listener the listener
         */
        public GetBalancesTask(final Context context, final String customerToken, final ResponderListener listener) {
            super(context, listener, false);
            mCustomerToken = customerToken;
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("GetBalancesTask");
            try {
                customer = app.getApiClient().getBalances(mCustomerToken);
                responderName = customer.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    return RESULT_FAILURE;
                } else if (!responderName.equals("mobilegetbalance")) {
                    return RESULT_FAILURE;
                }

                return RESULT_SUCCESS;

            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }

        @Override
        protected void onPostExecute(final Integer result) {
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(responderName, customer);
            } else {
                ((ResponderListener) taskListener).responder("error", context.getString(R.string.error_occurred));
            }
        }
    }

    /**
     * Executes GetBalancesTask.
     *
     * @param context       the context
     * @param customerToken the id of the user
     * @param listener      the listener
     */
    public void getBalances(final Context context, final String customerToken, final ResponderListener listener) {
        new GetBalancesTask(context, customerToken, listener).execute();
    }

    public void updateWidgetBalances(Context context, SharedPreferences.Editor editor) {
        // Set up the widget items

        List<User> usersList = Utils.getUsernames(context);
        if (usersList.size() > 0
                && app.getUid() != null
                && app.getLoggedInUser() != null
                && app.getCustomerToken() != null
                && Utils.getUsernames(context) != null
                && ((app.getLoggedInUser().getCreditCards() != null && app.getLoggedInUser().getCreditCards().size() > 0)
                || (app.getLoggedInUser().getDepositAccounts() != null && app.getLoggedInUser().getDepositAccounts().size() > 0))) {

            // Get first username
            if (app.getUid().equals(usersList.get(0).getUsername())) {

                List<CustomerAccount> accounts = new LinkedList<>();

                if (app.getLoggedInUser().getDepositAccounts() != null && app.getLoggedInUser().getDepositAccounts().size() > 0)
                    accounts.addAll(app.getLoggedInUser().getDepositAccounts());

                if (app.getLoggedInUser().getCreditCards() != null && app.getLoggedInUser().getCreditCards().size() > 0)
                    accounts.addAll(app.getLoggedInUser().getCreditCards());

                String accountsAndBalances = "";
                for (CustomerAccount account : accounts) {
                    accountsAndBalances += account.getNickname() + "><";
                    accountsAndBalances += account.getAccountLast4Num() + (!account.getAccountNumberSuffix().equals("") ? " " + account.getAccountNumberSuffix() : "") + "><";
                    accountsAndBalances += account.getPortalBalance() + "><";
                    accountsAndBalances += account.isBalanceColorRed() ? "R;" : "B;";
                }

                // Write these to the shared preferences
                editor.putString("widget_balances", accountsAndBalances);
                editor.putString("widget_lastupdatedon", Calendar.getInstance().getTime().toString());
                editor.putString("widget_username", app.getUid());
                editor.putString("widget_get_balance", "false");
                editor.putString("widget_customer_token", app.getCustomerToken());
                editor.putString("widget_device_identifier", app.getWidgetDeviceId());
                editor.commit();

                // Send the update intent to the widget
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, BalanceWidgetProvider.class));
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listView);
            }
        }
    }


    /**
     * Executes OobLoginTask.
     *
     * @param context  the context
     * @param action   the OOB action to execute.
     * @param param    Target or code to process
     * @param listener the listener for handle response
     */
    public void loginOOB(final Context context, final String action, final String param, final ResponderListener listener) {
        final String deviceInfoJson = RSACollectUtils.collectDeviceInfo(context);
        final String rsaCookie = Utils.getPrefsStringNotNull(MiBancoConstants.RSA_COOKIE, context);

        new OobLoginTask(context, action, param, deviceInfoJson, rsaCookie, listener).execute();
    }


    protected class OobLoginTask extends BaseAsyncTask {


        /**
         * The action to need question.
         */
        private String action;

        /**
         * The param need to loginoob
         */

        private String param;

        /**
         * OOB data
         */
        private OobChallenge oobData;

        /**
         * The deviceInfoRsa.
         */
        private final String mDeviceInfoRsa;

        /**
         * The rsaCookie.
         */
        private final String mRsaCookie;

        /**
         * Instantiates a new OobLoginTask.
         *
         * @param context  the context
         * @param action   the OOB action to execute.
         * @param param    Target or code to process
         * @param listener the listener for handle response
         */
        OobLoginTask(final Context context, final String action, final String param, final String deviceInfoRsa, final String rsaCookie,
                            final ResponderListener listener) {
            super(context, listener, true);
            this.action = action;
            this.param = param;
            this.mDeviceInfoRsa = deviceInfoRsa;
            this.mRsaCookie = rsaCookie;
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("OobLoginTask");
            try {

                Log.d("OobLoginTask", " doInBackground() Executing: " + action);
                boolean accountBlocked = false;
                if (action.equalsIgnoreCase(MiBancoConstants.OOB_VALIDATE_SMSCODE)) {
                    oobData = app.getApiClient().trySendingOOBCode(action, param, mDeviceInfoRsa, mRsaCookie);
                } else if (action.equalsIgnoreCase(MiBancoConstants.OOB_SEND_SMSCODE)) {
                    oobData = app.getApiClient().oobResendCode(action, param, mDeviceInfoRsa, mRsaCookie);
                } else if (action.equalsIgnoreCase(MiBancoConstants.OOB_SEND_ALT_PHONE)) {
                    oobData = app.getApiClient().trySendingOOBCodeToAltPhone(MiBancoConstants.OOB_NO_PHONE, param, mDeviceInfoRsa, mRsaCookie);
                } else if (action.equalsIgnoreCase(MiBancoConstants.OOB_VALIDATE_CALLCODE)) {
                    oobData = app.getApiClient().tryValidateCall(action, mDeviceInfoRsa, mRsaCookie);
                } else if (action.equalsIgnoreCase(MiBancoConstants.OOB_CALL_PHONE)) {
                    oobData = app.getApiClient().oobMakingCall(action, param, mDeviceInfoRsa, mRsaCookie);
                } else if (action.equalsIgnoreCase(MiBancoConstants.OOB_CALL_ALT_PHONE)) {
                    oobData = app.getApiClient().oobMakingCallToAlt(param, mDeviceInfoRsa, mRsaCookie);
                } else {
                    taskException = new BankException(app.getString(R.string.invalid_action));
                    return RESULT_FAILURE;

                }
                app.setCustomerToken(oobData.getCustomerToken());
                responderName = oobData.getResponderName();
                if (oobData.getFlags().getAccessBlocked() != null) {
                    accountBlocked = oobData.getFlags().getAccessBlocked();
                }
                Utils.setPrefsString(MiBancoConstants.RSA_COOKIE, "", context);
                if (oobData.getRsaCookie() != null && !oobData.getRsaCookie().equals("")) {
                    Utils.setPrefsString(MiBancoConstants.RSA_COOKIE, oobData.getRsaCookie(), context);
                }

                if (responderName == null || responderName.equalsIgnoreCase("login")) {
                    if (accountBlocked) {
                        oobData.getContent().setRsaBlocked(true);
                        BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_USERNAME_BLOCKED);
                    } else {
                        taskException = new BankException(app.getString(R.string.session_has_expired));
                        BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_TIMED_OUT_CHALLENGE);
                    }
                    return RESULT_FAILURE;
                }

                if (responderName.equalsIgnoreCase("notAvailable")) {
                    taskException = new NotAvailableException(app.getString(R.string.not_available_description));
                    return RESULT_FAILURE;
                }
                //add validation for empty json response.
                if (responderName.equalsIgnoreCase(MiBancoConstants.OOB_ACTION_NAME)) {
                    if (oobData.getContent().getRsaBlocked()) {
                        BPAnalytics.logEvent(BPAnalytics.EVENT_AUTH_PROCESS_USERNAME_BLOCKED);
                        return RESULT_FAILURE;
                    } else if (oobData.getContent().isTimeout()) {
                        taskException = new BankException(app.getString(R.string.oob_timeout_code));
                        BPAnalytics.logEvent(BPAnalytics.EVENT_OOB_PROCESS_CHALLENGED_CODE);
                        return RESULT_FAILURE;
                    } else if (oobData.getContent().isValidationError()) {
                        taskException = new BankException(app.getString(R.string.oob_validation_error));
                        BPAnalytics.logEvent(BPAnalytics.EVENT_OOB_PROCESS_CHALLENGED_CODE);
                        return RESULT_FAILURE;
                    } else if (action.equalsIgnoreCase(MiBancoConstants.OOB_VALIDATE_SMSCODE)) {
                        taskException = new BankException(app.getString(R.string.invalid_code));
                        BPAnalytics.logEvent(BPAnalytics.EVENT_OOB_PROCESS_INCORRECT_CODE);
                        return RESULT_FAILURE;
                    } else if (action.equalsIgnoreCase(MiBancoConstants.OOB_SEND_SMSCODE) && !oobData.getContent().isCodeSent()) {
                        taskException = new BankException(app.getString(R.string.new_code_error));
                        return RESULT_FAILURE;
                    } else if (action.equalsIgnoreCase(MiBancoConstants.OOB_VALIDATE_CALLCODE)) {
                        taskException = new BankException(app.getString(R.string.invalid_code_call));
                        BPAnalytics.logEvent(BPAnalytics.EVENT_OOB_PROCESS_INCORRECT_CODE);
                        return RESULT_FAILURE;
                    }
                }
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS) {
                ((ResponderListener) taskListener).responder(responderName, oobData);
            } else if (result == RESULT_FAILURE && oobData.getContent().getRsaBlocked()) {
                ((ResponderListener) taskListener).responder(responderName, oobData);
            }
        }
    }


    /**
     * Fetches a payment history list for the logged in user. Supports filtering by one field.
     *
     * @param context      the context
     * @param listener     the listener
     * @param showProgress show progress indicator?
     */
    public void getOpacUrl(final Context context, final ResponderListener listener, final boolean showProgress) {
        new OpenAccountTask(context, listener, showProgress).execute();
    }

    /**
     * Fetches transfer history from the web service.
     */
    private class OpenAccountTask extends SessionAsyncTask {

        /**
         * Transfer history object.
         */
        private OpenAccountUrl openAccountUrl;

        /**
         * Instantiates a new FetchTransferHistoryTask.
         *
         * @param context      the context
         * @param listener     the listener
         * @param showProgress show progress indicator?
         */
        public OpenAccountTask(final Context context, final ResponderListener listener, final boolean showProgress) {
            super(context, listener, showProgress);
        }

        /**
         * Instantiates a new FetchTransferHistoryTask with filtering parameters (one must be null, otherwise payeeId is taken).
         *
         * @param context      the context
         * @param listener     the listener
         * @param showProgress show progress indicator?
         * @param accountTo    recipient account number String to filter results by or null if no filtering should be performed.
         * @param date         date String to filter results by or null if no filtering should be performed
         */
        public OpenAccountTask(final Context context, final ResponderListener listener, final boolean showProgress, final String accountTo, final String date) {
            super(context, listener, showProgress);
        }


        @Override
        protected Integer doInBackground(final Object... params) {
            try {
                openAccountUrl = app.getApiClient().postTokenOpac();

                responderName = openAccountUrl.getResponderName();
                if (responderName.equalsIgnoreCase("login")) {
                    taskException = new BankException(app.getString(R.string.session_has_expired));
                    return RESULT_FAILURE;
                } else if (!responderName.equalsIgnoreCase("tokenOpac")) {
                    taskException = new BankException(context.getString(R.string.error_occurred), true);
                    return RESULT_FAILURE;
                }
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                return RESULT_FAILURE;
            }
        }


        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(responderName, openAccountUrl);
            } else if (result == RESULT_FAILURE && taskListener != null) {
                ((ResponderListener) taskListener).responder("", null);
            }
        }
    }

    public void getMobileAthPlastics(final Context context, final String frontEndId, final ResponderListener listener) {
        new MobileAthPlasticsTask(context, frontEndId, listener).execute();
    }

    private class MobileAthPlasticsTask extends SessionAsyncTask {
        /**
         * Frontendid of the account for the plastics.
         */
        private String accFrontEndId;
        private OnOffPlastics plastics;

        /**
         * Instantiates a new OnOffPlastics.
         *
         * @param context       the context
         * @param accFrontEndId the accFrontEndId of the Account
         * @param listener      the listener for handle response
         */
        public MobileAthPlasticsTask(final Context context, final String accFrontEndId, final ResponderListener listener) {
            super(context, listener, true);
            this.accFrontEndId = accFrontEndId;
        }

        @Override
        protected Integer doInBackground(final Object... params) {
            try {
                plastics = app.getApiClient().getAccountPlastics(accFrontEndId);
                responderName = plastics.getResponderName();
                if (!responderName.equals("mobileathonoff")) {
                    return RESULT_FAILURE;
                }
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }

        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(responderName, plastics);
            } else if (result == RESULT_FAILURE && taskListener != null) {
                ((ResponderListener) taskListener).responder("", null);
            }
        }
    }

    public void postMobilePlasticStatusTask(final Context context, final String cardFrontEndId, final String action, final ResponderListener listener) {
        new MobilePlasticStatusTask(context, cardFrontEndId, action, listener).execute();
    }

    private class MobilePlasticStatusTask extends SessionAsyncTask {
        private String cardFrontEndId;
        private String action;
        private OnOffPlastics plastics;
        private List<CustomerAccount> tmpList;

        /**
         * Instantiates a new OnOffPlastics.
         *
         * @param context        the context
         * @param cardFrontEndId the FrontEndId of the Card Plastic
         * @param action         the action
         * @param listener       the listener for handle response
         */
        public MobilePlasticStatusTask(final Context context, final String cardFrontEndId, final String action, final ResponderListener listener) {
            super(context, listener, true);
            this.cardFrontEndId = cardFrontEndId;
            this.action = action;
        }

        @Override
        protected Integer doInBackground(final Object... params) {
            try {
                plastics = app.getApiClient().changePlasticStatus(cardFrontEndId, action);
                responderName = plastics.getResponderName();
                if (!responderName.equals("mobileathonoff") || plastics == null || plastics.getCardPlastic() == null) {
                    return RESULT_FAILURE;
                }

                List<CustomerAccount> accounts = new ArrayList<>();
                accounts.addAll(app.getLoggedInUser().getDepositAccounts());
                accounts.addAll(app.getLoggedInUser().getCreditCards());
                tmpList = new LinkedList<>();

                for (final CustomerAccount acc : accounts) {
                    if (acc.getApiAccountKey().equals(plastics.getCardPlastic().getApiAccountKey())) {
                        tmpList.add(acc);
                    }
                }

                plastics.setTmpList(tmpList);
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }

        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(responderName, plastics);
            } else if (result == RESULT_FAILURE && taskListener != null) {
                ((ResponderListener) taskListener).responder("", null);
            }
        }
    }

    /**
     * Executes Ebills Task.
     *
     * @param context  the context
     * @param listener the listener
     */
    public void ebills(final Context context, final ResponderListener listener) {
        new EbillsTasks(context, listener).execute();
    }


    public class EbillsTasks extends SessionAsyncTask {

        public EbillsTasks(final Context context, final ResponderListener listener) {
            super(context, listener, false);
        }

        @Override
        protected Integer doInBackground(final Object... params) {
            try {
                //EB-1726
                app.getApiClient().renewPermId();
                return RESULT_SUCCESS;
            } catch (final Exception e) {
                taskException = e;
                return RESULT_FAILURE;
            }
        }

        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
        }
    }

    public void PushTask(final Context context, PushTokenRequest request, boolean isLogin, final ResponderListener listener) {
        new PushTasks(context, request, isLogin, listener).execute();
    }

    public class PushTasks extends SessionAsyncTask {

        private PushTokenRequest pushTokenRequest;
        private PushTokenResponse pushTokenResponse;
        private boolean isLogin;

        PushTasks(final Context context, PushTokenRequest pushTokenRequest, boolean isLogin, final ResponderListener listener) {
            super(context, listener, !isLogin);
            this.isLogin = isLogin;
            this.pushTokenRequest = pushTokenRequest;
        }

        @Override
        protected Integer doInBackground(Object... objects) {
            try {
                pushTokenResponse = app.getApiClient().postPushTokenInfo(pushTokenRequest, isLogin);
                responderName = pushTokenResponse.getResponderName();

                if (pushTokenResponse == null || !StringUtils.equals(responderName, PushTokenResponse.RESPONDER_NAME)) {
                    return RESULT_FAILURE;
                }
                return RESULT_SUCCESS;

            } catch (final Exception e) {
                return RESULT_FAILURE;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(responderName, pushTokenResponse);
            } else if (result == RESULT_FAILURE && taskListener != null) {
                ((ResponderListener) taskListener).responder("", null);
            }
        }
    }

    /**
     * Initiates an async call to acquire the Tsys Loyalty Rewards Info for the provided frontEndId.
     *
     * @param context    The context from where its called
     * @param frontEndId A front end Id belonging to a Cash Rewards (VPCBK) CCA.
     * @param listener   The code to call when request is complete.
     */
    public void getTsysLoyaltyRewardsInfoTask(final Context context, String frontEndId, final ResponderListener listener) {
        new GetTsysLoyaltyRewardsInfoTask(context, frontEndId, listener).execute();
    }

    /**
     * AsyncTask to request Tsys Loyalty Rewards Info. This is called from Accounts.java and
     * AccountDetails.java to load the available balance for Cash Rewards (VPCBK) CCA.
     */
    public class GetTsysLoyaltyRewardsInfoTask extends SessionAsyncTask {

        private String frontEndId;
        private TsysLoyaltyRewardsInfo tsysLoyaltyRewardsInfo;

        GetTsysLoyaltyRewardsInfoTask(final Context context, String frontEndId, final ResponderListener listener) {
            super(context, listener, false);
            expectedDuration = 20000;
            this.frontEndId = frontEndId;
        }

        @Override
        protected Integer doInBackground(Object... objects) {
            Thread.currentThread().setName("GetTsysLoyaltyRewardsInfoTask");
            try {
                tsysLoyaltyRewardsInfo = app.getApiClient().getTsysLoyaltyRewardsInfo(frontEndId);

                if (tsysLoyaltyRewardsInfo == null) {
                    return RESULT_FAILURE;
                }
                return RESULT_SUCCESS;

            } catch (final Exception e) {
                return RESULT_FAILURE;
            }
        }

        /**
        * Establish pos execute the result based in validations.
                *
                * @param result  the result
         */
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(responderName, tsysLoyaltyRewardsInfo);
            } else if (result == RESULT_FAILURE && taskListener != null) {
                ((ResponderListener) taskListener).responder("", null);
            }
        }
    }

    /**
     * @param context    The context from where its called
     * @param frontEndId A front end Id belonging to a Cash Rewards (VPCBK) CCA.
     * @param listener   The code to call when request is complete.
     */
    public void postTsysLoyaltyRewardsInfoTask(final Context context, String frontEndId, HashMap<String, Object> params, final ResponderListener listener) {
        new PostTsysLoyaltyRewardsInfoTask(context, frontEndId, params, listener).execute();
    }

    /**
     * AsyncTask to request Tsys Loyalty Rewards Info. This is called from Accounts.java and
     * AccountDetails.java to load the available balance for Cash Rewards (VPCBK) CCA.
     */
    public class PostTsysLoyaltyRewardsInfoTask extends SessionAsyncTask {

        private String frontEndId;
        private JSONObject response;
        private HashMap<String, Object> params;

        PostTsysLoyaltyRewardsInfoTask(final Context context, String frontEndId, HashMap<String, Object> params, final ResponderListener listener) {
            super(context, listener, true);
            expectedDuration = 20000;
            this.frontEndId = frontEndId;
            this.params = params;
        }

        @Override
        protected Integer doInBackground(Object... objects) {
            Thread.currentThread().setName("PostTsysLoyaltyRewardsInfoTask");
            try {
                response = app.getApiClient().postTsysLoyaltyRewardsRedemption(frontEndId, params);

                if (response == null || frontEndId == null) {
                    return RESULT_FAILURE;
                }
                return RESULT_SUCCESS;

            } catch (final Exception e) {
                return RESULT_FAILURE;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(responderName, response);
            } else if (result == RESULT_FAILURE && taskListener != null) {
                ((ResponderListener) taskListener).responder("", null);
            }
        }
    }

    /**
     * Fetches Notification Center data.
     */
    public class NotificationCenterTask extends SessionAsyncTask {

        NotificationCenter data;

        public NotificationCenterTask(final Context context, final ResponderListener listener, final Boolean showProgress) {
            super(context, listener, showProgress);
        }

        @Override
        protected Integer doInBackground(final Object... params) {
            Thread.currentThread().setName("NotificationCenterTask");
            int finalResult;
            try {
                data = app.getApiClient().getNotificationCenterParameters();
                finalResult = (data == null) ? RESULT_FAILURE : RESULT_SUCCESS;
            } catch (final Exception e) {
                finalResult = RESULT_FAILURE;
            }
            return finalResult;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                ((ResponderListener) taskListener).responder(responderName, data);
            } else if (result == RESULT_FAILURE && taskListener != null) {
                ((ResponderListener) taskListener).responder("", null);
            }
        }
    }

    public void notificationCenterTask(final Context context, final ResponderListener listener, final boolean showProgress) {
        notificationCenterTask = new NotificationCenterTask(context, listener, showProgress);
        notificationCenterTask.execute();
    }

    /**
     * AsyncTask to request Tsys Loyalty Rewards Automatic Redemption Info.
     * This is called from {@link com.popular.android.mibanco.activity.AutomaticRedemptionActivity}
     * @author leandro.baleriani
     * @version 1.0
     * @since 1.0
     * @see TsysLoyaltyRewardsRecurringStatementCreditResponse
     */
    private class GetTsysLoyaltyRewardsRecurringStatementCreditInfoTask extends SessionAsyncTask {

        private String frontEndId; //FrontEndId value
        private String method; //MethodAction value
        private TsysLoyaltyRewardsRecurringStatementCreditResponse response; //Tsys Api response

        /**
         * Constructor for GetTLRAutomaticRedemptionInfoTask class
         * @param context
         * @param frontEndId
         * @param method
         * @param listener
         */
        GetTsysLoyaltyRewardsRecurringStatementCreditInfoTask(final Context context, String frontEndId, String method, final ResponderListener listener) {
            super(context, listener, true);
            expectedDuration = 20000;
            this.frontEndId = frontEndId;
            this.method = method;
        }

        /**
         *
         * @param objects
         * @return integer
         */
        @Override
        protected Integer doInBackground(Object... objects) {
            final String threadName = "GetTsysLoyaltyRewardsAutomaticRedemptionInfoTask"; //Thread name

            Thread.currentThread().setName(threadName);
            try {
                response = app.getApiClient().getTsysAutomaticRedemptionInfo(frontEndId, method);

                if (response == null || frontEndId == null) {
                    return RESULT_FAILURE;
                }
                return RESULT_SUCCESS;

            } catch (final Exception e) {
                return RESULT_FAILURE;
            }
        }

        /**
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Integer result) {
            final String responderEmpty = ""; //Empty response

            super.onPostExecute(result);
            if (result == RESULT_SUCCESS && taskListener != null) {
                if (taskListener instanceof ResponderListener) {
                    ((ResponderListener) taskListener).responder(responderName, response);
                }
            } else if (result == RESULT_FAILURE && taskListener != null) {
                if (taskListener instanceof ResponderListener) {
                    ((ResponderListener) taskListener).responder(responderEmpty, null);
                }
            }
        }
    }

    /**
     * @param context    The context from where its called
     * @param frontEndId A front end Id belonging to a Cash Rewards (VPCBK) CCA.
     * @param method The action to perfom on api call
     * @param listener   The code to call when request is complete.
     */
    public void getTsysLoyaltyRewardsRecurringStatementCreditInfoTask(final Context context, String frontEndId, String method, final ResponderListener listener) {
        new GetTsysLoyaltyRewardsRecurringStatementCreditInfoTask(context, frontEndId, method, listener).execute();
    }

}
