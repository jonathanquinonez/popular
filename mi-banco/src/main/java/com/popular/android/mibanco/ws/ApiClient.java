package com.popular.android.mibanco.ws;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.model.AcceptedTermsInRDC;
import com.popular.android.mibanco.model.AccountTransactions;
import com.popular.android.mibanco.model.Customer;
import com.popular.android.mibanco.model.CustomerEntitlements;
import com.popular.android.mibanco.model.DepositCheckEnrollment;
import com.popular.android.mibanco.model.DepositCheckReceipt;
import com.popular.android.mibanco.model.DeviceInfo;
import com.popular.android.mibanco.model.EBills;
import com.popular.android.mibanco.model.EnrollmentLiteRequest;
import com.popular.android.mibanco.model.LoginGet;
import com.popular.android.mibanco.model.NotificationCenter;
import com.popular.android.mibanco.model.OnOffCardCounter;
import com.popular.android.mibanco.model.OnOffPlastics;
import com.popular.android.mibanco.model.OobChallenge;
import com.popular.android.mibanco.model.OpenAccountUrl;
import com.popular.android.mibanco.model.Payment;
import com.popular.android.mibanco.model.PaymentActive;
import com.popular.android.mibanco.model.PaymentHistory;
import com.popular.android.mibanco.model.PremiaInfo;
import com.popular.android.mibanco.model.PushWelcomeParams;
import com.popular.android.mibanco.model.RDCCheckItem;
import com.popular.android.mibanco.model.RSAChallengeResponse;
import com.popular.android.mibanco.model.RemoteDepositHistory;
import com.popular.android.mibanco.model.RsaModResponse;
import com.popular.android.mibanco.model.SendAcceptTermsInRDC;
import com.popular.android.mibanco.model.Transfer;
import com.popular.android.mibanco.model.TransferActive;
import com.popular.android.mibanco.model.TransferHistory;
import com.popular.android.mibanco.model.TsysLoyaltyRewardsInfo;
import com.popular.android.mibanco.model.TsysLoyaltyRewardsRecurringStatementCreditResponse;
import com.popular.android.mibanco.util.ProductType;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.ws.response.AthmEnrollAccount;
import com.popular.android.mibanco.ws.response.AthmEnrollCard;
import com.popular.android.mibanco.ws.response.AthmEnrollConfirmation;
import com.popular.android.mibanco.ws.response.AthmEnrollInfo;
import com.popular.android.mibanco.ws.response.AthmEnrollPhone;
import com.popular.android.mibanco.ws.response.AthmEnrollPhoneCode;
import com.popular.android.mibanco.ws.response.AthmSSOInfo;
import com.popular.android.mibanco.ws.response.AthmSendMoneyConfirmation;
import com.popular.android.mibanco.ws.response.AthmSendMoneyInfo;
import com.popular.android.mibanco.ws.response.AthmSendMoneyInput;
import com.popular.android.mibanco.ws.response.BannerResponse;
import com.popular.android.mibanco.ws.response.EasyCashTrx;
import com.popular.android.mibanco.ws.response.EnrollmentLiteCompleteResponse;
import com.popular.android.mibanco.ws.response.EnrollmentLiteResponse;
import com.popular.android.mibanco.ws.response.GenerateOtpCode;
import com.popular.android.mibanco.ws.response.MarketPlaceTermsResponse;
import com.popular.android.mibanco.ws.response.MarketplaceDeterminateChallenge;
import com.popular.android.mibanco.ws.response.MobileCashAcctsResponse;
import com.popular.android.mibanco.ws.response.MobileCashTrx;
import com.popular.android.mibanco.ws.response.MobileCashTrxInfo;
import com.popular.android.mibanco.ws.response.PushTokenRequest;
import com.popular.android.mibanco.ws.response.PushTokenResponse;
import com.popular.android.mibanco.ws.response.RetirementPlanInfoResponse;
import com.popular.android.mibanco.ws.response.UnicaUrl;
import com.popular.android.mibanco.ws.response.ValidateOtpCode;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;

/**
 * Provides methods for calling web services. Uses com.popular.android.mibanco.webservice.objects classes for parsing JSON responses and ensuring easy
 * access from Java code.
 */
public class ApiClient {

    private final static String LOCATOR_BASE_URL = "http://bpprlocator.azure-mobile.net/api/locator/"; // LOCATOR_BASE_URL

    /**
     *  LOCATOR_BRANCHES_WEBSERVICE_URL
     */
    public final static String LOCATOR_BRANCHES_WEBSERVICE_URL = LOCATOR_BASE_URL + "branches"; // LOCATOR_BRANCHES_WEBSERVICE_URL

    /**
     *  BANNER URL SERVICE
     */
    public final static String BANNER_URL_SERVICE = "https://apps.popular.com/mobile/banners.php?"; // BANNER URL SERVICE

    /**
     *  LOCATOR_BRANCH_DETAIL_WEBSERVICE_URL
     */
    public final static String LOCATOR_BRANCH_DETAIL_WEBSERVICE_URL = LOCATOR_BASE_URL + "branch/[id]"; // LOCATOR_BRANCH_DETAIL_WEBSERVICE_URL

    /**
     *  LOCATOR_ATMS_WEBSERVICE_URL
     */
    public final static String LOCATOR_ATMS_WEBSERVICE_URL = LOCATOR_BASE_URL + "atms"; // LOCATOR_ATMS_WEBSERVICE_URL

    private final static String SEARCH_PAYMENT_LATEST = "1"; // SEARCH_PAYMENT_LATEST
    private final static String SEARCH_PAYMENT_BY_DATE_ACTION = "2"; // SEARCH_PAYMENT_BY_DATE_ACTION
    private final static String SEARCH_PAYMENT_BY_PAYEE_ACTION = "3"; // SEARCH_PAYMENT_BY_PAYEE_ACTION
    private final static String DELETE_PAYMENT_ACTION = "5"; // DELETE_PAYMENT_ACTION

    private final static String SEARCH_TRANSFER_LATEST = "SEARCH_LAST15"; // SEARCH_TRANSFER_LATEST
    private final static String SEARCH_TRANSFER_BY_DATE_ACTION = "SEARCH_BY_DATE"; // SEARCH_TRANSFER_BY_DATE_ACTION
    private final static String SEARCH_TRANSFER_BY_ACCOUNT_ACTION = "SEARCH_BY_ACCOUNT"; // SEARCH_TRANSFER_BY_ACCOUNT_ACTION
    private final static String DELETE_TRANSFER_ACTION = "CAN_TRANSFER"; // DELETE_TRANSFER_ACTION

    private final SyncRestClient syncRestClient; // Rest Client

    /**
     *  ApiClient constructor
     * @param serverUrl
     * @param deviceID
     * @param language
     * @param context
     */
    public ApiClient(final String serverUrl, final String deviceID, final String language, final Context context) {
        syncRestClient = new SyncRestClient(serverUrl, deviceID, language, context);
    }

    /**
     * getSyncRestClient
     * @return SyncRestClient
     */
    public SyncRestClient getSyncRestClient() {
        return syncRestClient;
    }

    /**
     * globalStatus
     * @return GlobalStatus
     * @throws Exception
     */
    public String globalStatus() throws Exception {
        Call<String> call = syncRestClient.getMiBancoServices().globalStatus();
        return call.execute().body();
    }

    /**
     * fetchLogin
     * @return LoginGet
     * @throws Exception
     */
    public LoginGet fetchLogin() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        Call<String> call = syncRestClient.getMiBancoServices().loginGet();
        String json = call.execute().body();
        return gson.fromJson(json, LoginGet.class);
    }

    /**
     * postLogin
     * @param username
     * @param deviceInfoRsa
     * @param rsaCookie
     * @return HashMap
     * @throws Exception
     */
    public HashMap<String, Object> postLogin(final String username, final String deviceInfoRsa, final String rsaCookie) throws Exception {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("username", username);
        params.put("device_info_rsa", deviceInfoRsa);
        params.put("rsa_cookie", rsaCookie);

        Call<String> call = syncRestClient.getMiBancoServices().login(params);
        String json = call.execute().body();

        return parseBaseResponse(json);
    }

    /**
     *  postSecurityQuestion
     * @param answer
     * @param remember
     * @return HashMap
     * @throws Exception
     */
    public HashMap<String, Object> postSecurityQuestion(final String answer, final boolean remember,
                                                        final String deviceInfoRsa, final String rsaCookie) throws Exception {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("answer", answer);
        params.put("remember", String.valueOf(remember));
        params.put("device_info_rsa", deviceInfoRsa);
        params.put("rsa_cookie", rsaCookie);

        Call<String> call = syncRestClient.getMiBancoServices().question(params);
        String json = call.execute().body();

        return parseBaseResponse(json);
    }

    /**
     * postPushSplashDecision
     * @param decision
     * @throws Exception
     */
    public void postPushSplashDecision(final String decision) throws Exception {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("decision", decision);

        syncRestClient.getMiBancoServices().mobileAlertsSaveSplashDecision(params).execute();
    }

    /**
     *  postPassword
     * @param answer
     * @param deviceIdentifier
     * @param customerToken
     * @param deviceName
     * @param deviceInfo
     * @return String
     * @throws Exception
     */
    public String postPassword(final String answer, final String deviceIdentifier, final String customerToken, final String deviceName, DeviceInfo deviceInfo) throws Exception {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("answer", answer);
        params.put("deviceId", deviceIdentifier);
        params.put("deviceToken", customerToken);//??????
        params.put("deviceNickname", deviceName);
        params.put("appVersion", deviceInfo.getAppVersion());
        params.put("osVersion", deviceInfo.getOsVersion());
        params.put("networkProvider", deviceInfo.getNetworkProvider());
        params.put("deviceModel", deviceInfo.getDeviceModel());

        if (App.getApplicationInstance().isAutoLogin()) {
            params.put("fingerprintId", App.getApplicationInstance().getDeviceId());
        } else {
            params.put("fingerprintId", "");
        }
        Call<String> call = syncRestClient.getMiBancoServices().password(params);
        String json = call.execute().body();
        return json;
    }

    /**
     * postAuthentication
     * @param authenticationJson
     * @param walletType
     * @return String
     * @throws Exception
     */
    public String postAuthentication(final HashMap authenticationJson, final String walletType) throws Exception {

        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("panReferenceID", authenticationJson.get("panReferenceID"));
        params.put("tokenRequestorID", authenticationJson.get("tokenRequestorID"));
        params.put("tokenReferenceID", authenticationJson.get("tokenReferenceID"));
        params.put("panLast4", authenticationJson.get("panLast4"));
        params.put("deviceID", authenticationJson.get("deviceID"));
        params.put("walletAccountID", authenticationJson.get("walletAccountID"));
        params.put("tokenReqName", walletType);
        final Call<String> call = syncRestClient.getMiBancoServices().mobileTokenization(params);
        return call.execute().body();
    }

    /**
     *  logout
     * @param expired
     * @return String
     * @throws Exception
     */
    public String logout(final boolean expired) throws Exception {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("expired", expired);

        Call<String> call = syncRestClient.getMiBancoServices().logout(params);
        String json = call.execute().body();

        return readResponderName(json);
    }

    /**
     * fetchEbills
     * @return EBills
     * @throws Exception
     */
    public EBills fetchEbills() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        Call<String> call = syncRestClient.getMiBancoServices().ebppPortalInbox();
        String json = call.execute().body();
        return gson.fromJson(json, EBills.class);
    }

    /**
     * fetchTransfers
     * @return Transfer
     * @throws Exception
     */
    public Transfer fetchTransfers() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final Call<String> call = syncRestClient.getMiBancoServices().getTransfer();
        final String json = call.execute().body();
        return gson.fromJson(json, Transfer.class);
    }

    /**
     * makeTransfer
     * @param accountFrom
     * @param accountTo
     * @param amount
     * @param effectiveDate
     * @param reccurent
     * @param needsConfirmation
     * @return TransferActive
     * @throws Exception
     */
    public TransferActive makeTransfer(final String accountFrom, final String accountTo, final String amount, final String effectiveDate, final String reccurent, final boolean needsConfirmation)
            throws Exception {
        final HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("transfers[0].accountFrom", accountFrom);
        params.put("transfers[0].accountTo", accountTo);
        params.put("transfers[0].amount", amount);
        params.put("transfers[0].effectiveDate", effectiveDate);
        params.put("transfers[0].recurrent", reccurent);

        if (needsConfirmation) {
            params.put("_target1", "Transfer");
        } else {
            params.put("_finish", "Transfer");
        }

        final CustomGsonParser gson = new CustomGsonParser();
        final Call<String> call = syncRestClient.getMiBancoServices().transfer(params);
        final String json = call.execute().body();
        return gson.fromJson(json, TransferActive.class);
    }

    /**
     * fetchPayments
     * @return Payment
     * @throws Exception
     */
    public Payment fetchPayments() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();

        final Call<String> call = syncRestClient.getMiBancoServices().getQuickPayment();
        final String json = call.execute().body();
        return gson.fromJson(json, Payment.class);
    }

    /**
     *  makePayment
     * @param accountFrom
     * @param amount
     * @param effectiveDate
     * @param payeeId
     * @param needsConfirmation
     * @return PaymentActive
     * @throws Exception
     */
    public PaymentActive makePayment(final String accountFrom, final String amount, final String effectiveDate, final String payeeId, final boolean needsConfirmation) throws Exception {
        final HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("accountFrom", accountFrom);
        params.put("payeeid", payeeId);
        params.put("effectiveDate", effectiveDate);
        params.put("amount", amount);

        if (needsConfirmation) {
            params.put("_target1", "Pay");
        } else {
            params.put("_finish", "Confirm Payment");
        }

        final CustomGsonParser gson = new CustomGsonParser();
        final Call<String> call = syncRestClient.getMiBancoServices().quickPayment(params);
        final String json = call.execute().body();
        return gson.fromJson(json, PaymentActive.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public Customer portal() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        Call<String> call = syncRestClient.getMiBancoServices().portal();
        String json = call.execute().body();

        return gson.fromJson(json, Customer.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public String sessionPing() throws Exception {
        Call<String> call = syncRestClient.getMiBancoServices().sessionPing();
        String json = call.execute().body();
        return readResponderName(json);
    }

    /**
     *
     * @param account
     * @param allPages
     * @param cycle
     * @param pageReq
     * @return
     * @throws Exception
     */
    public AccountTransactions fetchTransactions(final String account, final boolean allPages, final int cycle, final int pageReq) throws Exception {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("account", account);
        params.put("allPages", allPages);
        params.put("cycle", cycle);
        params.put("pageReq", pageReq);

        final CustomGsonParser gson = new CustomGsonParser();
        final Call<String> call = syncRestClient.getMiBancoServices().transaction(params);
        final String json = call.execute().body();
        return gson.fromJson(json, AccountTransactions.class);
    }

    /**
     *
     * @param account
     * @param allPages
     * @param cycle
     * @param pageReq
     * @return
     * @throws Exception
     */
    public AccountTransactions fetchCCATransactions(final String account, final boolean allPages, final int cycle, final int pageReq) throws Exception {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("frontendid", account);
        params.put("allPages", allPages);
        params.put("cycle", cycle);
        params.put("pageReq", pageReq);

        final CustomGsonParser gson = new CustomGsonParser();
        final Call<String> call = syncRestClient.getMiBancoServices().ccaInProcessTransactions(params);
        final String json = call.execute().body();
        return gson.fromJson(json, AccountTransactions.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public PaymentHistory fetchLatestPaymentHistory() throws Exception {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("action", SEARCH_PAYMENT_LATEST);

        final Gson gson = new Gson();
        final Call<String> call = syncRestClient.getMiBancoServices().paymentHistory(params);
        final String json = call.execute().body();
        return gson.fromJson(json, PaymentHistory.class);
    }

    /**
     *
     * @param payeeId
     * @return
     * @throws Exception
     */
    public PaymentHistory fetchPaymentHistoryByPayee(final String payeeId) throws Exception {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("action", SEARCH_PAYMENT_BY_PAYEE_ACTION);
        params.put("payeeId", payeeId);

        final Gson gson = new Gson();
        final Call<String> call = syncRestClient.getMiBancoServices().paymentHistory(params);
        final String json = call.execute().body();
        return gson.fromJson(json, PaymentHistory.class);
    }

    /**
     *
     * @param date
     * @return
     * @throws Exception
     */
    public PaymentHistory fetchPaymentHistoryByDate(final String date) throws Exception {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("action", SEARCH_PAYMENT_BY_DATE_ACTION);
        params.put("date", date);

        final Gson gson = new Gson();
        final Call<String> call = syncRestClient.getMiBancoServices().paymentHistory(params);
        final String json = call.execute().body();
        return gson.fromJson(json, PaymentHistory.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public TransferHistory fetchLatestTransferHistory() throws Exception {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("action", SEARCH_TRANSFER_LATEST);

        final Gson gson = new Gson();
        final Call<String> call = syncRestClient.getMiBancoServices().transferHistory(params);
        final String json = call.execute().body();
        return gson.fromJson(json, TransferHistory.class);
    }

    /**
     *
     * @param accountNumber
     * @return
     * @throws Exception
     */
    public TransferHistory fetchTransferHistoryByAccount(final String accountNumber) throws Exception {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("action", SEARCH_TRANSFER_BY_ACCOUNT_ACTION);
        params.put("accountNumber", accountNumber);

        final Gson gson = new Gson();
        final Call<String> call = syncRestClient.getMiBancoServices().transferHistory(params);
        final String json = call.execute().body();
        return gson.fromJson(json, TransferHistory.class);
    }

    /**
     *
     * @param date
     * @return
     * @throws Exception
     */
    public TransferHistory fetchTransferHistoryByDate(final String date) throws Exception {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("action", SEARCH_TRANSFER_BY_DATE_ACTION);
        params.put("date", date);

        final Gson gson = new Gson();
        final Call<String> call = syncRestClient.getMiBancoServices().transferHistory(params);
        final String json = call.execute().body();
        return gson.fromJson(json, TransferHistory.class);
    }

    /**
     *
     * @param favId
     * @param modPayment
     * @return
     * @throws Exception
     */
    public PaymentHistory deleteInProcessPayment(final String favId, final String modPayment) throws Exception {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("action", DELETE_PAYMENT_ACTION);
        params.put("favId", favId);
        params.put("modPayment", modPayment);

        final Gson gson = new Gson();
        final Call<String> call = syncRestClient.getMiBancoServices().paymentHistory(params);
        final String json = call.execute().body();
        return gson.fromJson(json, PaymentHistory.class);
    }

    /**
     *
     * @param favId
     * @param modTransfer
     * @return
     * @throws Exception
     */
    public TransferHistory deleteInProcessTransfer(final String favId, final String modTransfer) throws Exception {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("action", DELETE_TRANSFER_ACTION);
        params.put("favId", favId);
        params.put("modTransfer", modTransfer);

        final Gson gson = new Gson();
        final Call<String> call = syncRestClient.getMiBancoServices().transferHistory(params);
        final String json = call.execute().body();
        return gson.fromJson(json, TransferHistory.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public String removeMessage() throws Exception {
        final Call<String> call = syncRestClient.getMiBancoServices().removeMessage();
        return call.execute().body();
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public CustomerEntitlements fetchCustomerEntitlements() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        Call<String> call = syncRestClient.getMiBancoServices().customerEntitlements();
        String json = call.execute().body();
        return gson.fromJson(json, CustomerEntitlements.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public RemoteDepositHistory fetchRemoteDepositHistory() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        Call<String> call = syncRestClient.getMiBancoServices().remoteDepositHistory();
        String json = call.execute().body();
        return gson.fromJson(json, RemoteDepositHistory.class);
    }

    /**
     *
     * @param referenceNumber
     * @return
     * @throws Exception
     */
    public RDCCheckItem fetchReviewCheck(String referenceNumber) throws Exception {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("depositId", referenceNumber);

        final CustomGsonParser gson = new CustomGsonParser();
        Call<String> call = syncRestClient.getMiBancoServices().remoteDepositHistoryImages(params);
        final String json = call.execute().body();
        return gson.fromJson(json, RDCCheckItem.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public DepositCheckEnrollment enrollInRDC() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        Call<String> call = syncRestClient.getMiBancoServices().remoteDepositDCenRoll();
        final String json = call.execute().body();
        return gson.fromJson(json, DepositCheckEnrollment.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public AcceptedTermsInRDC acceptedTermsInRDC() throws Exception {
        Call<String> call = syncRestClient.getMiBancoServices().getRemoteDepositEasyAcceptTerms();
        final String json = call.execute().body();
        return new CustomGsonParser().fromJson(json, AcceptedTermsInRDC.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public SendAcceptTermsInRDC sendAcceptTermsInRDC() throws Exception {
        Call<String> call = syncRestClient.getMiBancoServices().remoteDepositEasyAcceptTerms();
        final String json = call.execute().body();
        return new CustomGsonParser().fromJson(json, SendAcceptTermsInRDC.class);
    }

    /**
     *
     * @param frontendid
     * @param amount
     * @param frontImageString
     * @param backImageString
     * @return
     * @throws Exception
     */
    public DepositCheckReceipt depositCheck(String frontendid, String amount, String frontImageString, String backImageString) throws Exception {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("frontImageStr", frontImageString);
        params.put("backImageStr", backImageString);
        params.put("frontEndId", frontendid);
        params.put("amount", amount);
        params.put("deviceType", "Android");
        params.put("osName", getCurrentOSName());
        params.put("osVersion", android.os.Build.VERSION.RELEASE);

        final CustomGsonParser gson = new CustomGsonParser();
        Call<String> call = syncRestClient.getMiBancoServices().remoteDepositSubmitDeposit(params);
        final String json = call.execute().body();
        return gson.fromJson(json, DepositCheckReceipt.class);
    }

    /**
     *
     * @param customerToken
     * @return
     * @throws Exception
     */
    public Customer getBalances(String customerToken) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("deviceToken", customerToken);
        final Call<String> call = syncRestClient.getMiBancoServices().mobileGetBalanceInfo(params);
        final String json = call.execute().body();
        return gson.fromJson(json, Customer.class);
    }

    /**
     *
     * @param enrollmentLiteRequest
     * @return
     * @throws Exception
     */
    public EnrollmentLiteResponse liteNonCustomerInfoSubmit(EnrollmentLiteRequest enrollmentLiteRequest) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("firstName", enrollmentLiteRequest.getFirstName());
        params.put("lastName", enrollmentLiteRequest.getLastName());
        params.put("phoneNumber", enrollmentLiteRequest.getPhoneNumber());
        params.put("phoneProvider", enrollmentLiteRequest.getPhoneProvider());
        params.put("email", enrollmentLiteRequest.getEmail());
        params.put("deviceId", enrollmentLiteRequest.getDeviceId());

        String json = MiBancoConstants.TEST_ENROLL_LITE_NONCUST_SUBMIT_INFO;
        if (!FeatureFlags.TEST_NO_INTERNET()) {
            Call<String> call = syncRestClient.getMiBancoServices().liteEnrollmentVerification(params);
            json = call.execute().body();
        }
        return gson.fromJson(json, EnrollmentLiteResponse.class);
    }

    /**
     *
     * @param enrollmentLiteRequest
     * @return
     * @throws Exception
     */
    public EnrollmentLiteResponse liteCustomerInfoSubmit(EnrollmentLiteRequest enrollmentLiteRequest) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("phoneNumber", enrollmentLiteRequest.getPhoneNumber());

        if (enrollmentLiteRequest.getPhoneProvider() != null) {
            params.put("phoneProvider", enrollmentLiteRequest.getPhoneProvider());
        }
        params.put("deviceId", enrollmentLiteRequest.getDeviceId());

        String json = MiBancoConstants.TEST_ENROLL_LITE_CUST_SUBMIT_INFO;
        if (!FeatureFlags.TEST_NO_INTERNET()) {
            Call<String> call = syncRestClient.getMiBancoServices().liteCustomerEnrollmentInfoSubmit(params);
            json = call.execute().body();
        }
        return gson.fromJson(json, EnrollmentLiteResponse.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public AthmEnrollPhone getPhoneProviders() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        String json = MiBancoConstants.TEST_MOBILE_PHONE_PROVIDERS;
        if (!FeatureFlags.TEST_NO_INTERNET()) {
            Call<String> call = syncRestClient.getMiBancoServices().mobilePhoneProviders();
            json = call.execute().body();
        }
        return gson.fromJson(json, AthmEnrollPhone.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public EnrollmentLiteResponse hasMobilePhoneInAlerts() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        String json = MiBancoConstants.TEST_HAS_MOBILE_PHONE_IN_ALERTS;
        if (!FeatureFlags.TEST_NO_INTERNET()) {
            Call<String> call = syncRestClient.getMiBancoServices().hasMobilePhoneAlerts();
            json = call.execute().body();
        }
        return gson.fromJson(json, EnrollmentLiteResponse.class);
    }

    /**
     *
     * @param accFrontEndId
     * @return
     * @throws Exception
     */
    public OnOffPlastics getAccountPlastics(String accFrontEndId) throws Exception {
        final HashMap<String, Object> params = new HashMap<>();
        params.put("accFrontEndId", accFrontEndId);

        final CustomGsonParser gson = new CustomGsonParser();

        String json = MiBancoConstants.TEST_ON_OFF;
        if (!FeatureFlags.TEST_NO_INTERNET()) {
            Call<String> call = syncRestClient.getMiBancoServices().mobileAthOnOff(params);
            json = call.execute().body();
        }
        return gson.fromJson(json, OnOffPlastics.class);
    }

    /**
     *
     * @param cardFrontEndId
     * @param action
     * @return
     * @throws Exception
     */
    public OnOffPlastics changePlasticStatus(String cardFrontEndId, String action) throws Exception {
        final HashMap<String, Object> params = new HashMap<>();
        params.put("cardFrontEndId", cardFrontEndId);
        params.put("action", action);

        final CustomGsonParser gson = new CustomGsonParser();

        String json = MiBancoConstants.TEST_ON_OFF;
        if (!FeatureFlags.TEST_NO_INTERNET()) {
            Call<String> call = syncRestClient.getMiBancoServices().mobileAthOnOff(params);
            json = call.execute().body();
        }
        return gson.fromJson(json, OnOffPlastics.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public List<OnOffCardCounter> getOnOffPortalCardCount() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();

        final Call<String> call = syncRestClient.getMiBancoServices().getOnOffCardPlastics();
        String json = call.execute().body();

        OnOffCardCounter[] items = gson.fromJson(json, OnOffCardCounter[].class);

        List<OnOffCardCounter> list = new ArrayList<>();
        list.addAll(Arrays.asList(items));

        return list;
    }

    /**
     *
     * @param resendCode
     * @return
     * @throws Exception
     */
    public EnrollmentLiteResponse generateSmsCode(boolean resendCode) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("resendCode", String.valueOf(resendCode));

        String json = "";
        Call<String> call = null;
        if (resendCode) {
            call = syncRestClient.getMiBancoServices().generateSmsCode(params);
            json = call.execute().body();
        } else {
            call = syncRestClient.getMiBancoServices().getGenerateSmsCode();
            json = call.execute().body();
        }

        return gson.fromJson(json, EnrollmentLiteResponse.class);
    }

    /**
     *
     * @param productType
     * @param bind
     * @return
     * @throws Exception
     */
    public EnrollmentLiteResponse bindCustomerDevice(String productType, boolean bind) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("unboundDevice", String.valueOf(!bind));
        params.put("token", App.getApplicationInstance().getDeviceId());
        params.put("productType", productType);
        params.put("phoneNumber", App.getApplicationInstance().getCustomerPhone(App.getApplicationInstance().getApplicationContext()));

        String deviceName = null;

        try {
            BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
            if (myDevice != null) {
                deviceName = myDevice.getName();
            }
        } catch (final Exception ex) {
            deviceName = StringUtils.capitalize(Build.MANUFACTURER.toLowerCase());
        }

        if (deviceName == null || deviceName.isEmpty()) {
            deviceName = StringUtils.capitalize(android.os.Build.BRAND.toLowerCase());
        }

        params.put("nickname", deviceName);
        Call<String> call = syncRestClient.getMiBancoServices().addCustomerDevice(params);
        String json = call.execute().body();

        if (ProductType.CASHDROP.toString().equals(productType) && App.getApplicationInstance().getCurrentUser() != null) {
            App.getApplicationInstance().setCustomerEntitlements(App.getApplicationInstance().getApiClient().fetchCustomerEntitlements());
            App.getApplicationInstance().setUpdateSidebarMenuOnResume(true);
        }

        return gson.fromJson(json, EnrollmentLiteResponse.class);
    }

    /**
     *
     * @param code
     * @return
     * @throws Exception
     */
    public EnrollmentLiteResponse validateSmsCode(String code) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("code", code);

        String json = MiBancoConstants.TEST_VALIDATE_SMS_CODE;
        if (!FeatureFlags.TEST_NO_INTERNET()) {
            Call<String> call = syncRestClient.getMiBancoServices().validateSmsCode(params);
            json = call.execute().body();
        }
        return gson.fromJson(json, EnrollmentLiteResponse.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public EnrollmentLiteCompleteResponse createLiteProfile() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();

        String json = MiBancoConstants.TEST_CREATE_LITE_PROFILE;
        if (!FeatureFlags.TEST_NO_INTERNET()) {
            Call<String> call = syncRestClient.getMiBancoServices().createLiteProfile();
            json = call.execute().body();
        }

        if (App.getApplicationInstance().getCurrentUser() != null) {
            App.getApplicationInstance().setCustomerEntitlements(App.getApplicationInstance().getApiClient().fetchCustomerEntitlements());
            App.getApplicationInstance().setUpdateSidebarMenuOnResume(true);
        }

        return gson.fromJson(json, EnrollmentLiteCompleteResponse.class);
    }

    /**
     *
     * @param token
     * @return
     * @throws Exception
     */
    public EnrollmentLiteResponse isCustomerLiteEnrolled(String token) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("token", token);
        String json = MiBancoConstants.TEST_IS_CUSTOMER_LITE_ENROLLED;
        if (!FeatureFlags.TEST_NO_INTERNET()) {
            Call<String> call = syncRestClient.getMiBancoServices().getCustomerLiteEnrolled(params);
            json = call.execute().body();
        }
        return gson.fromJson(json, EnrollmentLiteResponse.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public RSAChallengeResponse getRSAChallenge(final String sdkRsaJson, final String rsaCookie) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("device_info_rsa", sdkRsaJson);
        params.put("rsa_cookie", rsaCookie);

        Call<String> call = syncRestClient.getMiBancoServices().getMobileCheckRsaChallenge(params);
        String json = call.execute().body();
        return gson.fromJson(json, RSAChallengeResponse.class);
    }

    /**
     *
     * @param answer
     * @return
     * @throws Exception
     */
    public RSAChallengeResponse postRSAAnswerChallenge(String answer, final String sdkRsaJson, final String rsaCookie) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("answer", answer);
        params.put("device_info_rsa", sdkRsaJson);
        params.put("rsa_cookie", rsaCookie);

        Call<String> call = syncRestClient.getMiBancoServices().postMobileCheckRsaChallenge(params);
        String json = call.execute().body();
        return gson.fromJson(json, RSAChallengeResponse.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public RSAChallengeResponse getOOBChallenge(final String sdkRsaJson, final String rsaCookie) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("device_info_rsa", sdkRsaJson);
        params.put("rsa_cookie", rsaCookie);

        Call<String> call = syncRestClient.getMiBancoServices().oobChallenge(params);
        String json = call.execute().body();

        return gson.fromJson(json, RSAChallengeResponse.class);
    }

    /**
     *
     * @param action
     * @param code
     * @param finish
     * @param target
     * @return
     * @throws Exception
     */
    public RSAChallengeResponse postOOBChallenge(String action, String code, boolean finish, String target) throws Exception {
        final HashMap<String, Object> params = new HashMap<>();
        params.put("action", action);
        if (!Utils.isBlankOrNull(target)) {
            params.put("_target" + target, "target" + target);
        }
        if (!Utils.isBlankOrNull(code)) {
            params.put("code", code);
        }
        if (finish) {
            params.put("_finish", "finish");
        }

        final CustomGsonParser gson = new CustomGsonParser();

        Call<String> call = syncRestClient.getMiBancoServices().oobChallenge(params);
        String json = call.execute().body();

        return gson.fromJson(json, RSAChallengeResponse.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public AthmSendMoneyInfo getAthmSendMoneyInfo() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final Call<String> call = syncRestClient.getMiBancoServices().getAthMSendMoney();
        String json = call.execute().body();
        return gson.fromJson(json, AthmSendMoneyInfo.class);
    }

    /**
     *
     * @param amount
     * @param message
     * @param phoneNumber
     * @return
     * @throws Exception
     */
    public AthmSendMoneyInput postAthmSendMoneyInput(String amount, String message, String phoneNumber) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("_target1", "target1");
        params.put("amount", amount);
        params.put("message", message);
        params.put("phoneNumber", phoneNumber);
        final Call<String> call = syncRestClient.getMiBancoServices().athMSendMoney(params);
        final String json = call.execute().body();
        return gson.fromJson(json, AthmSendMoneyInput.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public AthmSendMoneyConfirmation postAthmSendMoneyConfirmation() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("_target2", "target2");
        final Call<String> call = syncRestClient.getMiBancoServices().athMSendMoney(params);
        final String json = call.execute().body();
        return gson.fromJson(json, AthmSendMoneyConfirmation.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public AthmEnrollInfo getAthmEnrollInfo() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final Call<String> call = syncRestClient.getMiBancoServices().getAthMEnroll();
        final String json = call.execute().body();
        return gson.fromJson(json, AthmEnrollInfo.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public AthmEnrollPhone postAthmEnrollPhone() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("_target1", "target1");
        final Call<String> call = syncRestClient.getMiBancoServices().athMEnroll(params);
        final String json = call.execute().body();
        return gson.fromJson(json, AthmEnrollPhone.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public OpenAccountUrl postTokenOpac() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("_target1", "target1");
        final Call<String> call = syncRestClient.getMiBancoServices().tokenOpac(params);
        final String json = call.execute().body();
        return gson.fromJson(json, OpenAccountUrl.class);
    }

    /**
     *
     * @param phoneNumber
     * @param phoneProvider
     * @return
     * @throws Exception
     */
    public AthmEnrollPhoneCode postAthmEnrollPhone(String phoneNumber, String phoneProvider) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("_target2", "target2");
        params.put("phoneNumber", phoneNumber);
        params.put("phoneProvider", phoneProvider);
        final Call<String> call = syncRestClient.getMiBancoServices().athMEnroll(params);
        final String json = call.execute().body();
        return gson.fromJson(json, AthmEnrollPhoneCode.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public AthmEnrollCard postAthmEnrollCard() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("_target3", "target3");
        final Call<String> call = syncRestClient.getMiBancoServices().athMEnroll(params);
        final String json = call.execute().body();
        return gson.fromJson(json, AthmEnrollCard.class);
    }

    /**
     *
     * @param account
     * @param plasticExpDateMonth
     * @param plasticExpDateYear
     * @param plasticNum
     * @return
     * @throws Exception
     */
    public AthmEnrollAccount postAthmEnrollAccount(String account, String plasticExpDateMonth, String plasticExpDateYear, String plasticNum) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("_target4", "target4");
        params.put("account", account);
        params.put("plasticExpDateMonth", plasticExpDateMonth);
        params.put("plasticExpDateYear", plasticExpDateYear);
        params.put("plasticNum", plasticNum);
        final Call<String> call = syncRestClient.getMiBancoServices().athMEnroll(params);
        final String json = call.execute().body();
        return gson.fromJson(json, AthmEnrollAccount.class);
    }

    /**
     *
     * @param usernameLogin
     * @param passwordLogin
     * @return
     * @throws Exception
     */
    public AthmEnrollConfirmation postAthmEnrollLogin(String usernameLogin, String passwordLogin) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("_target5", "target5");
        params.put("usernameLogin", usernameLogin);
        params.put("passwordLogin", passwordLogin);
        final Call<String> call = syncRestClient.getMiBancoServices().athMEnroll(params);
        final String json = call.execute().body();
        return gson.fromJson(json, AthmEnrollConfirmation.class);
    }

    /**
     *
     * @param usernameEnroll
     * @param passwordEnroll
     * @param passwordConfirm
     * @param termsConfirmation
     * @return
     * @throws Exception
     */
    public AthmEnrollConfirmation postAthmEnrollRegistration(String usernameEnroll, String passwordEnroll, String passwordConfirm, String termsConfirmation) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("_target5", "target5");
        params.put("usernameEnroll", usernameEnroll);
        params.put("passwordEnroll", passwordEnroll);
        params.put("passwordConfirm", passwordConfirm);
        params.put("termsConfirmation", termsConfirmation);
        final Call<String> call = syncRestClient.getMiBancoServices().athMEnroll(params);
        final String json = call.execute().body();
        return gson.fromJson(json, AthmEnrollConfirmation.class);
    }

    /**
     *
     * @param appToken
     * @param generateToken
     * @return
     * @throws Exception
     */
    public AthmSSOInfo postAthmTokenInfo(String appToken, boolean generateToken) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("appToken", appToken);
        params.put("tokenGenerationActive", generateToken);
        final Call<String> call = syncRestClient.getMiBancoServices().athMSsoToken(params);
        String json = call.execute().body();
        return gson.fromJson(json, AthmSSOInfo.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public AthmSSOInfo logoutAthmSso() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final Call<String> call = syncRestClient.getMiBancoServices().athMsSoUnbind();
        String json = call.execute().body();
        return gson.fromJson(json, AthmSSOInfo.class);
    }

    /**
     *
     * @param req
     * @param isCheckStatus
     * @return
     * @throws Exception
     */
    public PushTokenResponse postPushTokenInfo(PushTokenRequest req, boolean isCheckStatus) throws Exception {
        final HashMap<String, Object> params = new HashMap<>();
        params.put("pushToken", req.getPushToken());
        params.put("deviceName", req.getDeviceName());
        params.put("deviceModel", req.getDeviceModel());
        params.put("deviceType", req.getDeviceType());
        params.put("hasPermissions", req.HasPermissions());
        params.put("isActive", req.isActive());

        if (isCheckStatus && StringUtils.isNotEmpty(req.getInvalidPushToken())) {
            params.put("invalidPushToken", req.getInvalidPushToken());
        }

        Call<String> call = null;
        if (isCheckStatus)
             call = syncRestClient.getMiBancoServices().mobileCheckPushTokenStatus(params);
        else
             call = syncRestClient.getMiBancoServices().mobileSavePushToken(params);

        String jsonResponse =  call.execute().body();
        return new CustomGsonParser().fromJson(jsonResponse, PushTokenResponse.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public MobileCashTrxInfo getMobileCashPendingTrx() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        Call<String> call = syncRestClient.getMiBancoServices().mobileCashPendingTrxPath();
        String json = call.execute().body();
        return gson.fromJson(json, MobileCashTrxInfo.class);
    }

    /**
     *
     * @param refresh
     * @return
     * @throws Exception
     */
    public EasyCashTrx getEasyCashPendingTrx(boolean refresh) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("refresh", String.valueOf(refresh));
        Call<String> call = syncRestClient.getMiBancoServices().getEasyCashPendingTrx(params);
        String json = call.execute().body();
        return gson.fromJson(json, EasyCashTrx.class);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public MobileCashAcctsResponse getMobileCashAccts() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        Call<String> call = syncRestClient.getMiBancoServices().mobileCashAccountPath();
        String json = call.execute().body();
        return gson.fromJson(json, MobileCashAcctsResponse.class);
    }

    /**
     *
     * @param refresh
     * @return
     * @throws Exception
     */
    public MobileCashAcctsResponse getEasyCashAccts(boolean refresh) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();

        final HashMap<String, Object> params = new HashMap<>();
        params.put("refresh", String.valueOf(refresh));

        Call<String> call = syncRestClient.getMiBancoServices().easyCashAccounts(params);
        String json = call.execute().body();

        MobileCashAcctsResponse resp = gson.fromJson(json, MobileCashAcctsResponse.class);
        return resp;
    }

    /**
     * postMobileCashTrxCode
     * @param atmToken
     * @return MobileCashTrxInfo
     * @throws Exception
     */
    public MobileCashTrxInfo postMobileCashTrxCode(String atmToken) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("cancelTrx", (atmToken == null || atmToken.equals("")) ? 1 : 0);
        params.put("atmToken", atmToken);

        MobileCashTrxInfo trxInfo = new MobileCashTrxInfo();
        try {
            Call<String> call = syncRestClient.getMiBancoServices().mobileCashSubmitTrxCode(params);
            String json = call.execute().body();
            trxInfo = gson.fromJson(json, MobileCashTrxInfo.class);
        } catch (Exception e) {
            Log.e("ApiClient", e.toString());
        }
        return trxInfo;
    }

    /**
     * postEasyCashTrxCode
     * @param atmToken
     * @param pendingTranId
     * @return MobileCashTrxInfo
     * @throws Exception
     */
    public MobileCashTrxInfo postEasyCashTrxCode(String atmToken, String pendingTranId) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("cancelTrx", (atmToken == null || atmToken.equals("")) ? 1 : 0);
        params.put("atmToken", atmToken);
        params.put("pendingTranId", pendingTranId); //TODO, add value

        MobileCashTrxInfo trxInfo = new MobileCashTrxInfo();
        try {
            Call<String> call = syncRestClient.getMiBancoServices().easyCashTrxCode(params);
            String json = call.execute().body();
            trxInfo = gson.fromJson(json, MobileCashTrxInfo.class);
        } catch (Exception e) {
            Log.e("ApiClient", e.toString());
        }
        return trxInfo;
    }

    /**
     * postMobileCashPrestageTrx
     * @param trx
     * @param language
     * @return EasyCashTrx
     * @throws Exception
     */
    public EasyCashTrx postMobileCashPrestageTrx(MobileCashTrx trx, String language) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("accountFrontendId", trx.getAccountFrontEndId());
        params.put("atmLast4Num", trx.getAtmLast4Num()); //plastic
        params.put("atmType", trx.getAtmType());
        params.put("amount", trx.getAmount());
        params.put("language", language);
        params.put("tranType", trx.getTranType());

        if ("DEPOSIT".equals(trx.getTranType())) {
            params.put("receiverPhone", trx.getReceiverPhone());
            params.put("memo", trx.getMemo());
        }
        Call<String> call = syncRestClient.getMiBancoServices().postEasyCashPendingTrx(params);
        String json = call.execute().body();
        EasyCashTrx trx2 = gson.fromJson(json, EasyCashTrx.class);
        return trx2;
    }

    /**
     * fetchLocations
     * @param url
     * @return String
     * @throws Exception
     */
    public String fetchLocations(String url) throws Exception {
        return SyncRestClient.downloadExternalContent(url);
    }

    /**
     * getCurrentOSName
     * @return String
     */
    private String getCurrentOSName() {
        try {
            switch (android.os.Build.VERSION.SDK_INT) {
                case android.os.Build.VERSION_CODES.BASE:
                    return "BASE";
                case android.os.Build.VERSION_CODES.BASE_1_1:
                    return "BASE_1_1";
                case android.os.Build.VERSION_CODES.CUPCAKE:
                    return "CUPCAKE";
                case android.os.Build.VERSION_CODES.DONUT:
                    return "DONUT";
                case android.os.Build.VERSION_CODES.ECLAIR:
                    return "ECLAIR";
                case android.os.Build.VERSION_CODES.FROYO:
                    return "FROYO";
                case android.os.Build.VERSION_CODES.GINGERBREAD:
                    return "GINGERBREAD";
                case android.os.Build.VERSION_CODES.GINGERBREAD_MR1:
                    return "GINGERBREAD_MR1";
                case android.os.Build.VERSION_CODES.HONEYCOMB:
                    return "HONEYCOMB";
                case android.os.Build.VERSION_CODES.HONEYCOMB_MR1:
                    return "HONEYCOMB_MR1";
                case android.os.Build.VERSION_CODES.HONEYCOMB_MR2:
                    return "HONEYCOMB_MR2";
                case android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH:
                    return "ICE_CREAM_SANDWICH";
                case android.os.Build.VERSION_CODES.JELLY_BEAN:
                    return "JELLY_BEAN";
                case android.os.Build.VERSION_CODES.JELLY_BEAN_MR1:
                    return "JELLY_BEAN_MR1";
                case android.os.Build.VERSION_CODES.JELLY_BEAN_MR2:
                    return "JELLY_BEAN_MR2";
                default:
                    return "Not Available";
            }
        } catch (Exception e) {
            return String.valueOf(android.os.Build.VERSION.SDK_INT);
        }
    }

    /**
     * getCurrentLanguage
     * @return string
     */
    public String getCurrentLanguage() {
        return syncRestClient.getLanguage();
    }

    /**
     * setCurrentLanguage
     * @param language
     */
    public void setCurrentLanguage(final String language) {
        syncRestClient.setLanguage(language);
    }

    /**
     * parseBaseResponse
     * @param responseString
     * @return HashMap
     * @throws IOException
     */
    public HashMap<String, Object> parseBaseResponse(final String responseString) throws IOException {
        final HashMap<String, Object> ret = new HashMap<String, Object>();
        ret.put("access_blocked", false);
        final JsonReader reader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(responseString.getBytes()), "UTF-8"));
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("responder_name")) {
                ret.put("responder_name", reader.nextString());
            } else if (name.equals("responder_message")) {
                ret.put("responder_message", reader.nextString());
            } else if (name.equals("customerToken")) {
                ret.put("customerToken", reader.nextString());
            } else if (name.equals("phoneNumber")) {
                ret.put("phoneNumber", reader.nextString());
            } else if (name.equals("strKey")) {
                ret.put("strKey", reader.nextString());
            }
            else if (name.equals("rsa_cookie")) {
                ret.put("rsa_cookie", reader.nextString());
            }
            else if (name.equals("content")) {
                reader.beginObject();
                while (reader.hasNext()) {
                    name = reader.nextName();
                    if (name.equals("question")) {
                        ret.put("question", reader.nextString());
                        break;
                    } else if (name.equals(MiBancoConstants.OOB_USER_NAME)) {
                        ret.put(MiBancoConstants.OOB_USER_NAME, reader.nextString());
                    } else if (name.equals(MiBancoConstants.OOB_PHONE)) {
                        ret.put(MiBancoConstants.OOB_PHONE, reader.nextString());
                    } else if (name.equals(MiBancoConstants.OOB_HAS_ALTPHONE)) {
                        ret.put(MiBancoConstants.OOB_HAS_ALTPHONE, reader.nextBoolean());
                    } else if (name.equals(MiBancoConstants.OOB_CHALLENGE_TYPE)) {
                        ret.put(MiBancoConstants.OOB_CHALLENGE_TYPE, reader.nextString());
                    } else if (name.equals(MiBancoConstants.OOB_CODE_VOICE_CALL)) {
                        ret.put(MiBancoConstants.OOB_CODE_VOICE_CALL, reader.nextString());
                    } else if (name.equals(MiBancoConstants.OOB_RSA_BLOCKED)) {
                        ret.put(MiBancoConstants.OOB_RSA_BLOCKED, reader.nextBoolean());
                    } else if (name.equals(MiBancoConstants.CAN_OPEN_ACCOUNT)) {
                        ret.put(MiBancoConstants.CAN_OPEN_ACCOUNT, reader.nextString());
                    } else if (name.equals(MiBancoConstants.IS_FOREING_CUSTOMER)) {
                        ret.put(MiBancoConstants.IS_FOREING_CUSTOMER, reader.nextString());
                    } else if (name.equals("status")) {
                        ret.put("status", reader.nextString());
                    } else if (name.equals("minutesLeft")) {
                        ret.put("minutesLeft", reader.nextString());
                    } else {
                        reader.skipValue();
                    }
                }
                while (reader.hasNext()) {
                    reader.skipValue();
                }
                reader.endObject();
            } else if (name.equals("flags")) {
                reader.beginObject();

                while (reader.hasNext()) {
                    name = reader.nextName();
                    switch (name) {
                        case "access_blocked":
                            ret.put("access_blocked", reader.nextBoolean());
                            break;
                        case "MBSFE291":
                            ret.put("MBSFE291", reader.nextBoolean());
                            break;
                        case "pendingEnroll":
                            ret.put("pendingEnroll", reader.nextBoolean());
                            break;
                        default:
                            reader.skipValue();
                            break;
                    }
                }

                while (reader.hasNext()) {
                    reader.skipValue();
                }

                reader.endObject();
            } else if (name.equals(MiBancoConstants.CAN_OPEN_ACCOUNT)) {
                ret.put(MiBancoConstants.CAN_OPEN_ACCOUNT, reader.nextString());
            } else if (name.equals(MiBancoConstants.IS_FOREING_CUSTOMER)) {
                ret.put(MiBancoConstants.IS_FOREING_CUSTOMER, reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        reader.close();
        return ret;
    }

    /**
     * readResponderName
     * @param json
     * @return String
     * @throws UnsupportedEncodingException
     */
    public String readResponderName(final String json) throws UnsupportedEncodingException {
        final JsonReader reader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(json.getBytes()), "UTF-8"));
        String ret = null;
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                final String name = reader.nextName();
                if (name.equals("responder_name")) {
                    ret = reader.nextString();
                    break;
                } else {
                    reader.skipValue();
                }
            }
            while (reader.hasNext()) {
                reader.skipValue();
            }
            reader.endObject();
        } catch (final EOFException ex) {

            Log.w(ApiClient.class.getName(), "Empty JSON response.");
        } catch (final Exception ex) {

            Log.w("ApiClient", ex);
        } finally {
            try {
                reader.close();
            } catch (final IOException e) {
                Log.w("ApiClient", e);
            }
        }

        return ret;
    }

    /**
     * Send OOB code typed by user
     *
     * @param action Action to Validate Code
     * @param code   Code typed by user
     * @return The oob code response
     * @throws Exception
     */
    public OobChallenge trySendingOOBCode(String action, String code, final String deviceInfoRsa, final String rsaCookie) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("_finish", "finish");
        params.put("action", action);
        params.put("code", code);
        params.put("device_info_rsa", deviceInfoRsa);
        params.put("rsa_cookie", rsaCookie);

        Call<String> call = syncRestClient.getMiBancoServices().loginObb(params);
        String json = call.execute().body();

        return gson.fromJson(json, OobChallenge.class);
    }

    /**
     * Send OOB code typed by user
     *
     * @param action Action to Validate Code
     * @return
     * @throws Exception
     */
    public OobChallenge tryValidateCall(String action, final String deviceInfoRsa, final String rsaCookie) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("_finish", "finish");
        params.put("action", action);
        params.put("device_info_rsa", deviceInfoRsa);
        params.put("rsa_cookie", rsaCookie);

        Call<String> call = syncRestClient.getMiBancoServices().loginObb(params);
        String json = call.execute().body();
        Log.w(ApiClient.class.getName(), "JSON response." + json);
        return gson.fromJson(json, OobChallenge.class);
    }

    /**
     * Send OOB code typed by user
     *
     * @param action Action to Validate Code
     * @param target Target to resend the OOB code
     * @return
     * @throws Exception
     */
    public OobChallenge oobMakingCall(String action, String target, final String deviceInfoRsa, final String rsaCookie) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("action", action);
        params.put("_target" + target, "target" + target);
        params.put("device_info_rsa", deviceInfoRsa);
        params.put("rsa_cookie", rsaCookie);

        Call<String> call = syncRestClient.getMiBancoServices().loginObb(params);
        String json = call.execute().body();

        return gson.fromJson(json, OobChallenge.class);


    }

    /**
     * Send OOB code typed by user
     *
     * @param action Action to Validate Code
     * @param target Target to resend the OOB code
     * @return
     * @throws Exception
     */
    public OobChallenge oobMakingCallToAlt(String target, final String deviceInfoRsa, final String rsaCookie) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("action", "NO_PHONE");
        params.put("_target" + target, "target" + target);
        params.put("device_info_rsa", deviceInfoRsa);
        params.put("rsa_cookie", rsaCookie);

        Call<String> call = syncRestClient.getMiBancoServices().loginObb(params);
        String json = call.execute().body();
        return gson.fromJson(json, OobChallenge.class);
    }

    /**
     * Send OOB code to Alternate Phone user
     *
     * @param target Target to resend the OOB code
     * @return
     * @throws Exception
     */
    public OobChallenge trySendingOOBCodeToAltPhone(String action, String target, final String deviceInfoRsa, final String rsaCookie) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("_target" + target, "target" + target);
        params.put("action", action);
        params.put("device_info_rsa", deviceInfoRsa);
        params.put("rsa_cookie", rsaCookie);

        Call<String> call = syncRestClient.getMiBancoServices().loginObb(params);
        String json = call.execute().body();
        return gson.fromJson(json, OobChallenge.class);

    }

    /**
     * Send OOB code typed by user
     *
     * @param action Action to Validate Code
     * @param target Target to resend the OOB code
     * @return
     * @throws Exception throws exception
     */
    public OobChallenge oobResendCode(String action, String target, final String deviceInfoRsa, final String rsaCookie) throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("action", action);
        params.put("_target" + target, "target" + target);
        params.put("device_info_rsa", deviceInfoRsa);
        params.put("rsa_cookie", rsaCookie);
        
        Call<String> call = syncRestClient.getMiBancoServices().loginObb(params);
        String json = call.execute().body();
        return gson.fromJson(json, OobChallenge.class);
    }

    public void renewPermId() throws Exception {
       syncRestClient.getMiBancoServices()
                .renewPermID(new HashMap<String, Object>()).execute();

    }

    /**
     * Get Tsys Loyalty Rewards Info
     * @param frontEndId
     * @return TsysLoyaltyRewardsInfo
     * @throws Exception
     */
    public TsysLoyaltyRewardsInfo getTsysLoyaltyRewardsInfo(String frontEndId) throws Exception {

        final String frontEndIdVar = "frontEndId"; // FrontEndId param name
        final String dataResponse = "data"; // response var name
        final String status = "status"; // Status var name
        final String statusCode = "200"; // Status value

        final HashMap<String, Object> params = new HashMap<>();
        params.put(frontEndIdVar, frontEndId);

        Call<String> call = syncRestClient.getMiBancoServices().getTsysLoyaltyRewardsInfo(params);
        String jsonResponse = call.execute().body();

        final CustomGsonParser gson = new CustomGsonParser();

        JSONObject jsonObject = new JSONObject(jsonResponse);

        TsysLoyaltyRewardsInfo tsysLoyaltyRewardsInfo = null;

        if (String.valueOf(jsonObject.get(status)).equals(statusCode)) {
            JSONObject rewardsInfoJson = jsonObject.getJSONArray(dataResponse)
                    .getJSONObject(0);

            tsysLoyaltyRewardsInfo = gson.fromJson(rewardsInfoJson.toString(), TsysLoyaltyRewardsInfo.class);
        }

        return tsysLoyaltyRewardsInfo;
    }

    /**
     * <p>Performs a post request to retrieve redeem and update Tsys Loyalty Rewards Info for Cash Rewards CCA.
     * The call is made to tsysLoyaltyRewardsBalanceInformation in AjaxActionController.
     * </p>
     *
     * @param frontEndId A front end id belonging to a Cash Rewards (VPCBK) CCA.
     * @return TsysLoyaltyRewardsInfo object to insert in CustomerAccount.java or null
     * if error response is received.
     * @throws JSONException goes in catch
     */

    public JSONObject postTsysLoyaltyRewardsRedemption(String frontEndId, HashMap<String, Object> params) throws Exception {

        Call<String> call = syncRestClient.getMiBancoServices().postTsysLoyaltyRewardsRedemption(frontEndId,params);
        String jsonResponse = call.execute().body();



        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);


            String responderName = jsonObject.getString("responder_name");

            if (MiBancoConstants.CASH_REWARDS_REDEMPTION_CONFIGURATION_RESPONDER.equalsIgnoreCase(responderName)
                    || MiBancoConstants.CASH_REWARDS_REDEMPTION_CONFIRMATION_RESPONDER.equals(responderName)
                    || MiBancoConstants.CASH_REWARDS_REDEMPTION_RESULT_RESPONSE.equals(responderName)) {

                JSONObject content = jsonObject.getJSONObject("content");


                return content;
            }

        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return null;
    }

    /**
     * getPushWelcomeParameters
     * @param language
     * @return PushWelcomeParams
     * @throws Exception
     */
    public PushWelcomeParams getPushWelcomeParameters(String language) throws Exception {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        final Gson gson = new Gson();

        Call<String> call = syncRestClient.getMiBancoServices().pushWelcomeParameters(params);
        String json = call.execute().body();
        return gson.fromJson(json, PushWelcomeParams.class);
    }

    /**
     *  getNotificationCenterParameters
     * @return NotificationCenter
     * @throws Exception
     */
    public NotificationCenter getNotificationCenterParameters() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();

        Call<String> call = syncRestClient.getMiBancoServices().notificationCenter();
        String json = call.execute().body();

        return gson.fromJson(json, NotificationCenter.class);
    }

    /**
     * Check OOB status
     *
     * @return
     * @throws Exception
     */
    public RsaModResponse rsaCheckStatus() throws Exception {
        final CustomGsonParser gson = new CustomGsonParser();
        final Call<String> call = syncRestClient.getMiBancoServices().rsaCheckStatus();
        String json = call.execute().body();
        return gson.fromJson(json, RsaModResponse.class);
    }

    /**
     * Get Automatic Redemption info
     * @param frontEndId
     * @param method
     * @return TsysLoyaltyRewardsRecurringStatementCreditResponse object
     * @throws Exception
     */
    public TsysLoyaltyRewardsRecurringStatementCreditResponse getTsysAutomaticRedemptionInfo(String frontEndId, String method) throws Exception {

        final String frontEndIdVar = "frontEndId"; // FrontEndId param name
        final String methodAction = "methodAction"; // Method Action param name
        final String status = "status"; // Status var name
        final String statusCode = "200"; // Status value
        final String logTag = "log_tag"; //Log tag
        final String errorMessage = "Error parsing data "; // Message for error

        final HashMap<String, Object> params = new HashMap<>(2); // Params for tsys call
        params.put(frontEndIdVar, frontEndId);
        params.put(methodAction, method);

        Call<String> call = syncRestClient.getMiBancoServices().getTsysAutomaticRedemption(params); // Tsys Api call response
        String jsonResponse = call.execute().body();

        TsysLoyaltyRewardsRecurringStatementCreditResponse response = null; // Response variable

        try {

            final CustomGsonParser gSon = new CustomGsonParser(); //GSON parser

            final JSONObject content = new JSONObject(jsonResponse); //Json Content

            if (statusCode.equals(String.valueOf(content.get(status)))) {
                final JSONObject data = content.getJSONArray("data")
                        .getJSONObject(0); //Response data

                response =
                        gSon.fromJson(data.toString(),
                                TsysLoyaltyRewardsRecurringStatementCreditResponse.class);
            }

        } catch (
                JSONException e) {
            Log.e(logTag, errorMessage + e.toString());
        }

        return response;
    }

    /**
     * Get Tsys (Premia) Loyalty Rewards Redirect URL with token.
     * @param frontEndId
     * @return Premia Catalog Redirect URL.
     * @throws IOException
     */
    public String getTsysLoyaltyRewardsRedirectURL(String frontEndId) throws IOException {

        final String frontEndIdParamName = "cardNumber"; // FrontEndId param name.

        final String responderNameResponseVarName = "responder_name"; // Responder name var name.
        final String errorMessageResponseVarName = "error_message"; // Error msg var name.
        final String premiaTokenResponseVarName = "premiaToken"; // Response var name.
        final HashMap<String, Object> params = new HashMap<>(); // Parameter map.
        params.put(frontEndIdParamName, frontEndId);
        String premiaCatalogRedirectURL = null; // Premia Catalog Redirect URL.

        try {
            Call<String> call = syncRestClient.getMiBancoServices().getTsysLoyaltyRewardsRedirectUrl(params); // Response string.
            String jsonResponse = call.execute().body();

            JSONObject jsonObject = new JSONObject(jsonResponse); // Json response object.
            if (jsonObject.getString(responderNameResponseVarName).equals("premia") &&
                    jsonObject.getString(errorMessageResponseVarName).isEmpty()) {
                premiaCatalogRedirectURL = jsonObject.getString(premiaTokenResponseVarName);
            }
        } catch (JSONException exception) {
            Log.e("log_tag", "Error parsing data. " + exception.toString());
        }

        return premiaCatalogRedirectURL;
    }

    /**
     * Get Premia Balance info.
     *
     * @return Premia Balance Info.
     * @throws IOException
     */
    public PremiaInfo getPremiaInfo() throws Exception {
        final HashMap<String, Object> params = new HashMap<>(); // Parameter map.
        final String logTag = "PremiaInfo >"; //Log tag

        String jsonResponse= StringUtils.EMPTY;

        final CustomGsonParser gson = new CustomGsonParser();
        try {
            Call<String> call = syncRestClient.getMiBancoServices().getLoyaltyRewardsBalanceUrl(params);
            jsonResponse = call.execute().body();

        } catch (IOException e) {
            Log.e(logTag, e.toString());
        }
        return gson.fromJson(jsonResponse, PremiaInfo.class);
    }

    /**
     * getAcceptMarketplaceTerms
     * @param product
     * @return MarketPlaceTermsResponse object
     */
    public MarketPlaceTermsResponse postAcceptTermsMarketplaceWithProductId(String product) {

        final String productVar = "product"; // FrontEndId param name
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();

        params.put(productVar, product);

        String jsonResponse= StringUtils.EMPTY;

        try {
            Call<String> call = syncRestClient.getMiBancoServices().acceptMarketplaceTerms(params);
            jsonResponse = call.execute().body();
        } catch (IOException e) {
            Log.e(productVar, e.toString());
        }

        return gson.fromJson(jsonResponse, MarketPlaceTermsResponse.class);
    }

    /**
     * getdetermineAuthenticationChallenge
     * @return MarketplaceDeterminateChallenge object
     */
    public MarketplaceDeterminateChallenge postDetermineAuthenticationChallenge() {

        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();
        final String errorDetermineAuthentication = "determineChallenge";

        String jsonResponse= StringUtils.EMPTY;

        try {
            Call<String> call = syncRestClient.getMiBancoServices().determineAuthenticationChallenge(params);
            jsonResponse = call.execute().body();
        } catch (IOException e) {
            Log.e(errorDetermineAuthentication, e.toString());
        }

        return gson.fromJson(jsonResponse, MarketplaceDeterminateChallenge.class);
    }

    /**
     * getGenerateOtpCode
     * @return GenerateOtpCode object
     */
    public GenerateOtpCode postGenerateOtpCode(String resendCode) {

        final String resendCodeVar = "resendCode"; // FrontEndId param name
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();

        params.put(resendCodeVar, resendCode);

        String jsonResponse= StringUtils.EMPTY;

        try {
            Call<String> call = syncRestClient.getMiBancoServices().generateOtpCode(params);
            jsonResponse = call.execute().body();
        } catch (IOException e) {
            Log.e(resendCodeVar, e.toString());
        }

        return gson.fromJson(jsonResponse, GenerateOtpCode.class);
    }

    /**
     * getValidateOtpCode
     * @return ValidateOtpCode object
     */
    public ValidateOtpCode postValidateOtpCode(String code) {

        final String codeVar = "code"; // FrontEndId param name
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();

        params.put(codeVar, code);

        String jsonResponse= StringUtils.EMPTY;

        try {
            Call<String> call = syncRestClient.getMiBancoServices().validateOtpCode(params);
            jsonResponse = call.execute().body();
        } catch (IOException e) {
            Log.e(codeVar, e.toString());
        }

        return gson.fromJson(jsonResponse, ValidateOtpCode.class);
    }

    /**
     * getUnicaUrl
     * @return UnicaUrl object
     */
    public UnicaUrl postUnicaUrl(Boolean isOobAuthenticated) {

        final String codeVar = "isOobAuthenticated";
        final CustomGsonParser gson = new CustomGsonParser();
        final HashMap<String, Object> params = new HashMap<>();

        params.put(codeVar, isOobAuthenticated);

        String jsonResponse= StringUtils.EMPTY;

        try {
            Call<String> call = syncRestClient.getMiBancoServices().getUnicaUrl(params);
            jsonResponse = call.execute().body();
        } catch (IOException e) {
            Log.e(codeVar, e.toString());
        }

        return gson.fromJson(jsonResponse, UnicaUrl.class);
    }

    /**
     * Get RetirementPlan info.
     *
     * @return Retirement Plans.
     * @throws IOException
     */
    public RetirementPlanInfoResponse getRetirementPlanInfo() throws Exception {
        final HashMap<String, Object> params = new HashMap<>(); // Parameter map.
        final String logTag = "RetirementPlanInfo >"; //Log tag

        String jsonResponse= StringUtils.EMPTY;

        final CustomGsonParser gson = new CustomGsonParser();
        try {
            Call<String> call = syncRestClient.getMiBancoServices().getRetirementPlanInfoUrl(params);
            jsonResponse = call.execute().body();


        } catch (IOException e) {
            Log.e(logTag, e.toString());
        }
        return gson.fromJson(jsonResponse, RetirementPlanInfoResponse.class);
    }

    /**
     * Get RetirementPlan info.
     *
     * @return Retirement Plans.
     * @throws IOException
     */
    public BannerResponse getBannerCarousel(String segmentType, String viewLocation, String language) throws Exception {
        final String logTag = "getBannerCarousel >"; //Log tag
        String jsonResponse= StringUtils.EMPTY;

        final CustomGsonParser gson = new CustomGsonParser();
        try {
            String call = SyncRestClient.downloadExternalContent(BANNER_URL_SERVICE + "t=" + segmentType + "&l=" + language + "&s=" + viewLocation);
            jsonResponse = call;

        } catch (IOException e) {
            Log.e(logTag, e.toString());
        }
        return gson.fromJson(jsonResponse, BannerResponse.class);
    }
}


