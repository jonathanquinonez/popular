package com.popular.android.mibanco.ws;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.AcceptedTermsInRDC;
import com.popular.android.mibanco.model.Customer;
import com.popular.android.mibanco.model.CustomerEntitlements;
import com.popular.android.mibanco.model.DepositCheckEnrollment;
import com.popular.android.mibanco.model.DepositCheckReceipt;
import com.popular.android.mibanco.model.DeviceInfo;
import com.popular.android.mibanco.model.EnrollmentLiteRequest;
import com.popular.android.mibanco.model.AccountTransactions;
import com.popular.android.mibanco.model.EBills;
import com.popular.android.mibanco.model.GlobalStatus;
import com.popular.android.mibanco.model.LoginGet;
import com.popular.android.mibanco.model.NotificationCenter;
import com.popular.android.mibanco.model.OnOffCardCounter;
import com.popular.android.mibanco.model.OnOffPlastics;
import com.popular.android.mibanco.model.OpenAccountUrl;
import com.popular.android.mibanco.model.Payment;
import com.popular.android.mibanco.model.PaymentActive;
import com.popular.android.mibanco.model.PaymentHistory;
import com.popular.android.mibanco.model.PushWelcomeParams;
import com.popular.android.mibanco.model.RDCCheckItem;
import com.popular.android.mibanco.model.RemoteDepositHistory;
import com.popular.android.mibanco.model.RsaModResponse;
import com.popular.android.mibanco.model.SendAcceptTermsInRDC;
import com.popular.android.mibanco.model.Transfer;
import com.popular.android.mibanco.model.TransferActive;
import com.popular.android.mibanco.model.TransferHistory;
import com.popular.android.mibanco.model.TsysLoyaltyRewardsInfo;
import com.popular.android.mibanco.model.TsysLoyaltyRewardsRecurringStatementCreditResponse;
import com.popular.android.mibanco.util.FontChanger;
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
import com.popular.android.mibanco.ws.response.EasyCashTrx;
import com.popular.android.mibanco.ws.response.EnrollmentLiteCompleteResponse;
import com.popular.android.mibanco.ws.response.EnrollmentLiteResponse;
import com.popular.android.mibanco.ws.response.GenerateOtpCode;
import com.popular.android.mibanco.ws.response.MarketPlaceTermsResponse;
import com.popular.android.mibanco.ws.response.MarketplaceDeterminateChallenge;
import com.popular.android.mibanco.ws.response.MobileCashAcctsResponse;
import com.popular.android.mibanco.ws.response.MobileCashTrx;
import com.popular.android.mibanco.ws.response.PushTokenRequest;
import com.popular.android.mibanco.ws.response.PushTokenResponse;
import com.popular.android.mibanco.ws.response.RetirementPlanInfoResponse;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.model.OobChallenge;
import com.popular.android.mibanco.model.PremiaInfo;
import com.popular.android.mibanco.model.RSAChallengeResponse;
import com.popular.android.mibanco.ws.response.MobileCashTrxInfo;
import com.popular.android.mibanco.ws.response.UnicaUrl;
import com.popular.android.mibanco.ws.response.ValidateOtpCode;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import retrofit2.Call;
import retrofit2.Response;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApiClient.class, SyncRestClient.class, App.class, Context.class,
        MiBancoServices.class,Response.class,BluetoothAdapter.class,
        R.class, MiBancoConstants.class, MiBancoEnviromentConstants.class,
        App.class, BuildConfig.class, FeatureFlags.class, FontChanger.class,TextUtils.class,Utils.class,
        Html.class,StringUtils.class})
public class ApiClientUT {

    private ApiClient apiClient;

    private App mockApp = mock(App.class);

    private Context mockContext = mock(Context.class);

    private SyncRestClient mockSyncRestClient = mock(SyncRestClient.class);

    private DeviceInfo deviceInfo = mock(DeviceInfo.class);

    MiBancoServices mockMiBancoService = mock(MiBancoServices.class);

    Call<String> call = mock(Call.class);

    Response<String> response = mock(Response.class);

    SharedPreferences sharedPreferences = mock(SharedPreferences.class);

    Resources resources = mock(Resources.class);

    @Before
    public void setup() throws Exception {

        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(FontChanger.class);
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(App.getApplicationInstance()).thenReturn(mockApp);
        PowerMockito.when(mockApp.getBaseContext()).thenReturn(mockContext);
        PowerMockito.when(Utils.getSecuredSharedPreferences(mockContext)).thenReturn(sharedPreferences);
        PowerMockito.when(mockContext.getResources()).thenReturn(resources);

        PowerMockito.mockStatic(MiBancoEnviromentConstants.class);
        PowerMockito.mockStatic(FeatureFlags.class);


        mockStatic(App.class);

        when(App.getApplicationInstance()).thenReturn(mockApp);
        when(mockApp.getBaseContext()).thenReturn(mockContext);


        Context context = new App();
        whenNew(SyncRestClient.class).withArguments("", "", "", context).thenReturn(mockSyncRestClient);
        apiClient = spy(new ApiClient("", "", "", context));

    }

    @Test
    public void whenCallingGetTsysLoyaltyRewardsRedirectURL_GivenAFrontendId_ThenCatalogURL() throws IOException {

        final String dummyFrontendId = "1234567"; // Dummy frontEndId.
        final HashMap<String, Object> dummyParams = new HashMap<>(); // Parameter map.
        dummyParams.put("cardNumber", dummyFrontendId);
        final String dummyJsonResponse = dummyJsonResponseString(); // Dummy Json response string.

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getTsysLoyaltyRewardsRedirectUrl(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(dummyJsonResponse);

        String url = apiClient.getTsysLoyaltyRewardsRedirectURL(dummyFrontendId); // Catalog URL.

        assertNotNull(url);

    }

    @Test
    public void whenCallingGetTsysLoyaltyRewardsRedirectURL_GivenAnError_ThenCatalogURLIsNull() throws IOException {

        final String dummyFrontendId = "1234567"; // Dummy frontEndId.
        final HashMap<String, Object> dummyParams = new HashMap<>(); // Parameter map.
        dummyParams.put("cardNumber", dummyFrontendId);
        final String dummyJsonResponse = dummyJsonResponseStringWithError(); // Dummy Json response string.

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getTsysLoyaltyRewardsRedirectUrl(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(dummyJsonResponse);

        String url = apiClient.getTsysLoyaltyRewardsRedirectURL(dummyFrontendId); // Catalog URL.

        assertNull(url);

    }

    @Test
    public void whenCallingGetPremiaInfo_ThenPremiaBalanceInfo() throws Exception {


        final String dummyJsonResponse = dummyPremiaJsonResponseString(); // Dummy Json response string.

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getLoyaltyRewardsBalanceUrl(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(dummyPremiaJsonResponseString());

        PremiaInfo premiaInfo = apiClient.getPremiaInfo(); // Catalog URL.

        assertNotNull(premiaInfo);
        assertTrue(!premiaInfo.getTsysBalanceRewards().isEmpty());

    }

    @Test
    public void whenCallingGetPremiaInfo_ThenReturnErrorBalance() throws Exception {


        final String dummyJsonResponse = dummyPremiaJsonResponseStringWithError(); // Dummy Json response string.

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getLoyaltyRewardsBalanceUrl(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(dummyJsonResponse);

        PremiaInfo premiaInfo = apiClient.getPremiaInfo(); // Catalog URL.

        assertNotNull(premiaInfo);
        assertTrue(!premiaInfo.getError().isEmpty());

    }

    @Test
    public void whenCallingGetRetirementPlanInfo_ThenGetRetirementPlans() throws Exception {

        final String dummyFrontendId = "1234567"; // Dummy frontEndId.
        final HashMap<String, Object> dummyParams = new HashMap<>(); // Parameter map.
        dummyParams.put("cardNumber", dummyFrontendId);
        final String dummyJsonResponse = dummyJsonRetirementPlanResponseString(); // Dummy Json response string.

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getRetirementPlanInfoUrl(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(dummyJsonResponse);

        RetirementPlanInfoResponse retirementPlanInfo = apiClient.getRetirementPlanInfo(); // Catalog URL.

        assertNotNull(retirementPlanInfo);
        assertTrue(retirementPlanInfo.getHasRetPlan());
        assertTrue(retirementPlanInfo.getRetirementPlans().size() > 0);

    }

    @Test
    public void whenCalling_PostRSAAnswerChallenge_ThenVerify_Rsa_Params() throws Exception {

        final String rsaChallengeAnswer = "sevilla";
        final String sdkRsaJson = "[{id:myid,device:fewt24,deviceOS:Android,macAddress:23-434-112-234}]";
        final String rsaCookies = "myCookie";
        final HashMap<String, Object> params = new HashMap<>();
        params.put("answer", rsaChallengeAnswer);
        params.put("device_info_rsa", sdkRsaJson);
        params.put("rsa_cookie", rsaCookies);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.postMobileCheckRsaChallenge(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(dummyRSAJsonResponseStrinng());

        RSAChallengeResponse rsaChallengeResponse = apiClient.postRSAAnswerChallenge(rsaChallengeAnswer, sdkRsaJson, rsaCookies); // Catalog URL.
        assertTrue(rsaChallengeResponse.getRsaCookie().equals("myCookie"));

    }

    @Test
    public void whenCalling_PostLogin_ThenVerify_Rsa_Params() throws Exception {

        final String username = "carlos05";
        final String sdkRsaJson = "[{id:myid,device:fewt24,deviceOS:Android,macAddress:23-434-112-234}]";
        final String rsaCookies = "myCookie";
        final HashMap<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("device_info_rsa", sdkRsaJson);
        params.put("rsa_cookie", rsaCookies);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.login(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(dummyRSAJsonResponseStrinng());

        HashMap<String, Object> rsaChallengeResponse = apiClient.postLogin(username, sdkRsaJson, rsaCookies); // Catalog URL.
        assertTrue(rsaChallengeResponse.containsKey("rsa_cookie"));

    }

    @Test
    public void whenCalling_PostSecurityQuestion_ThenVerify_Rsa_Params() throws Exception {

        final String answer = "sevilla";
        final String sdkRsaJson = "[{id:myid,device:fewt24,deviceOS:Android,macAddress:23-434-112-234}]";
        final String rsaCookies = "myCookie";
        final boolean remember = true;
        final HashMap<String, Object> params = new HashMap<>();
        params.put("answer", answer);
        params.put("remember", String.valueOf(remember));
        params.put("device_info_rsa", sdkRsaJson);
        params.put("rsa_cookie", rsaCookies);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.question(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(dummyRSAJsonResponseStrinng());

        HashMap<String, Object> rsaChallengeResponse = apiClient.postSecurityQuestion(answer, remember, sdkRsaJson, rsaCookies); // Catalog URL.
        assertTrue(rsaChallengeResponse.containsValue("myCookie"));

    }

    @Test
    public void whenCalling_Portal_GivenDefaultConditions_ThenSuccessResponse() throws Exception {

        final String bodyResponse = "{ \n" +
                "  \"responder_name\": \"portal\",\n" +
                "  \"rsa_cookie\": \"myCookie\",\n" +
                "  \"responder_message\": \"account_information\"}";

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.portal()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(bodyResponse);

        Customer customerResponse = apiClient.portal();
        assertNotNull(customerResponse);

    }

    @Test
    public void whenCalling_Logout_GivenDefaultConditions_ThenSuccessResponse() throws Exception {

        final HashMap<String, Object> params = new HashMap<>();
        final String bodyResponse = "{ \n" +
                "  \"responder_name\": \"logout\",\n" +
                "  \"rsa_cookie\": \"myCookie\",\n" +
                "  \"responder_message\": \"account_information\"}";
        params.put("expired",true);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.logout(params)).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(bodyResponse);

        String logoutResponse = apiClient.logout(true);
        assertNotNull(logoutResponse);

    }

    @Test
    public void whenCalling_GetOOBChallenge_ThenVerify_Rsa_Params() throws Exception {

        final String sdkRsaJson = "[{id:myid,device:fewt24,deviceOS:Android,macAddress:23-434-112-234}]";
        final String rsaCookies = "myCookie";
        final HashMap<String, Object> params = new HashMap<>();
        params.put("device_info_rsa", sdkRsaJson);
        params.put("rsa_cookie", rsaCookies);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.oobChallenge(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(dummyRSAJsonResponseStrinng());

        RSAChallengeResponse rsaChallengeResponse = apiClient.getOOBChallenge(sdkRsaJson, rsaCookies);
        assertTrue(rsaChallengeResponse.getRsaCookie().equals(rsaCookies));

    }

    @Test
    public void whenCalling_trySendingOOBCode_ThenVerify_Rsa_Params() throws Exception {

        final String sdkRsaJson = "[{id:myid,device:fewt24,deviceOS:Android,macAddress:23-434-112-234}]";
        final String rsaCookies = "myCookie";
        final String action = "get";
        final String code = "bppr";
        final HashMap<String, Object> params = new HashMap<>();
        params.put("_finish", "finish");
        params.put("action", action);
        params.put("code", code);
        params.put("device_info_rsa", sdkRsaJson);
        params.put("rsa_cookie", rsaCookies);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.loginObb(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(dummyRSAJsonResponseStrinng());

        OobChallenge rsaChallengeResponse = apiClient.trySendingOOBCode(action, code, sdkRsaJson, rsaCookies);
        assertTrue(rsaChallengeResponse.getRsaCookie().equals(rsaCookies));

    }

    @Test
    public void whenCalling_tryValidateCall_ThenVerify_Rsa_Params() throws Exception {

        final String sdkRsaJson = "[{id:myid,device:fewt24,deviceOS:Android,macAddress:23-434-112-234}]";
        final String rsaCookies = "myCookie";
        final String action = "get";
        final String code = "1234";
        final HashMap<String, Object> params = new HashMap<>();
        params.put("_finish", "finish");
        params.put("action", action);
        params.put("device_info_rsa", sdkRsaJson);
        params.put("rsa_cookie", rsaCookies);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.loginObb(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(dummyRSAJsonResponseStrinng());

        OobChallenge rsaChallengeResponse = apiClient.tryValidateCall(action, sdkRsaJson, rsaCookies);
        assertTrue(rsaChallengeResponse.getRsaCookie().equals(rsaCookies));

    }

    @Test
    public void whenCalling_oobMakingCall_ThenVerify_Rsa_Params() throws Exception {

        final String sdkRsaJson = "[{id:myid,device:fewt24,deviceOS:Android,macAddress:23-434-112-234}]";
        final String rsaCookies = "myCookie";
        final String action = "get";
        final String target = "bppr";
        final HashMap<String, Object> params = new HashMap<>();
        params.put("action", action);
        params.put("_target" + target, "target" + target);
        params.put("device_info_rsa", sdkRsaJson);
        params.put("rsa_cookie", rsaCookies);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.loginObb(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(dummyRSAJsonResponseStrinng());

        OobChallenge rsaChallengeResponse = apiClient.oobMakingCall(action, target, sdkRsaJson, rsaCookies);
        assertTrue(rsaChallengeResponse.getRsaCookie().equals(rsaCookies));

    }

    @Test
    public void whenCalling_oobMakingCallToAlt_ThenVerify_Rsa_Params() throws Exception {

        final String sdkRsaJson = "[{id:myid,device:fewt24,deviceOS:Android,macAddress:23-434-112-234}]";
        final String rsaCookies = "myCookie";
        final String target = "bppr";
        final HashMap<String, Object> params = new HashMap<>();
        params.put("action", "NO_PHONE");
        params.put("_target" + target, "target" + target);
        params.put("device_info_rsa", sdkRsaJson);
        params.put("rsa_cookie", rsaCookies);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.loginObb(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(dummyRSAJsonResponseStrinng());

        OobChallenge rsaChallengeResponse = apiClient.oobMakingCallToAlt(target, sdkRsaJson, rsaCookies);
        assertTrue(rsaChallengeResponse.getRsaCookie().equals(rsaCookies));

    }

    @Test
    public void whenCalling_trySendingOOBCodeToAltPhone_ThenVerify_Rsa_Params() throws Exception {

        final String sdkRsaJson = "[{id:myid,device:fewt24,deviceOS:Android,macAddress:23-434-112-234}]";
        final String rsaCookies = "myCookie";
        final String target = "bppr";
        final String action = "get";
        final HashMap<String, Object> params = new HashMap<>();
        params.put("_target" + target, "target" + target);
        params.put("action", action);
        params.put("device_info_rsa", sdkRsaJson);
        params.put("rsa_cookie", rsaCookies);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.loginObb(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(dummyRSAJsonResponseStrinng());

        OobChallenge rsaChallengeResponse = apiClient.trySendingOOBCodeToAltPhone(action, target, sdkRsaJson, rsaCookies);
        assertTrue(rsaChallengeResponse.getRsaCookie().equals(rsaCookies));

    }

    @Test
    public void whenCalling_oobResendCode_ThenVerify_Rsa_Params() throws Exception {

        final String sdkRsaJson = "[{id:myid,device:fewt24,deviceOS:Android,macAddress:23-434-112-234}]";
        final String rsaCookies = "myCookie";
        final String target = "bppr";
        final String action = "get";
        final HashMap<String, Object> params = new HashMap<>();
        params.put("action", action);
        params.put("_target" + target, "target" + target);
        params.put("device_info_rsa", sdkRsaJson);
        params.put("rsa_cookie", rsaCookies);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.loginObb(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(dummyRSAJsonResponseStrinng());

        OobChallenge rsaChallengeResponse = apiClient.oobResendCode(action, target, sdkRsaJson, rsaCookies);
        assertTrue(rsaChallengeResponse.getRsaCookie().equals(rsaCookies));

    }

    @Test
    public void whenPostMobileCashTrxCode_givenRequestIsGood_thenReturnJson() throws Exception {

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(new MobileCashTrxInfo());

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.mobileCashSubmitTrxCode(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(jsonResponse);

        MobileCashTrxInfo response = apiClient.postMobileCashTrxCode("token");
        assertNotNull(response);
    }

    @Test
    public void whenCall_PostEasyCashTrxCode_Fail() throws Exception {
        final HashMap<String, Object> params = new HashMap<>();
        params.put("cancelTrx", 1);
        params.put("atmToken", "atmToken");
        params.put("pendingTranId", "pendingTranId");

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.mobileCashSubmitTrxCode(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenThrow(Exception.class);

        apiClient.postEasyCashTrxCode("token", "id");

    }

    @Test
    public void whenPostEasyCashTrxCode_givenRequestIsGood_thenReturnObject() throws Exception {

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(new MobileCashTrxInfo());

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.easyCashTrxCode(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(jsonResponse);

        apiClient.postEasyCashTrxCode("token", "id");

    }

    @Test
    public void whenCalling_PostPassword_GivenValidParams_ThenReturnResponse() throws Exception {

        final String answer = "";
        final String deviceId = "";
        final String deviceToken = "";
        final String deviceName = "";
        final HashMap<String, Object> params = new HashMap<>();

        when(deviceInfo.getAppVersion()).thenReturn("1234");
        when(deviceInfo.getOsVersion()).thenReturn("1.0");
        when(deviceInfo.getNetworkProvider()).thenReturn("");
        when(deviceInfo.getDeviceModel()).thenReturn("");

        params.put("answer", answer);
        params.put("deviceId", deviceId);
        params.put("deviceToken", deviceToken);
        params.put("deviceNickname", deviceName);
        params.put("appVersion", "1234");
        params.put("osVersion", "1.0");
        params.put("networkProvider", "");
        params.put("deviceModel", "");
        params.put("fingerprintId", "");

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.password(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(dummyPasswordJsonResponseString());

        String passwordResponse = apiClient.postPassword(
                answer, deviceId, deviceToken, deviceName, deviceInfo);

        HashMap<String, Object> parseBaseResponse = apiClient.parseBaseResponse(passwordResponse);

        assertEquals("portal", parseBaseResponse.get("responder_name"));
    }

    @Test
    public void whenCalling_PostPassword_GivenValidParamsAndFingerprintID_ThenReturnResponse() throws Exception {

        final String answer = "";
        final String deviceId = "123";
        final String deviceToken = "";
        final String deviceName = "";
        final HashMap<String, Object> params = new HashMap<>();

        when(deviceInfo.getAppVersion()).thenReturn("1234");
        when(deviceInfo.getOsVersion()).thenReturn("1.0");
        when(deviceInfo.getNetworkProvider()).thenReturn("");
        when(deviceInfo.getDeviceModel()).thenReturn("");

        params.put("answer", answer);
        params.put("deviceId", deviceId);
        params.put("deviceToken", deviceToken);
        params.put("deviceNickname", deviceName);
        params.put("appVersion", "1234");
        params.put("osVersion", "1.0");
        params.put("networkProvider", "");
        params.put("deviceModel", "");
        params.put("fingerprintId", deviceId);
        when(mockApp.isAutoLogin()).thenReturn(true);
        when(mockApp.getDeviceId()).thenReturn(deviceId);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.password(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(dummyPasswordJsonResponseString());

        String passwordResponse = apiClient.postPassword(
                answer, deviceId, deviceToken, deviceName, deviceInfo);

        HashMap<String, Object> parseBaseResponse = apiClient.parseBaseResponse(passwordResponse);

        assertEquals("portal", parseBaseResponse.get("responder_name"));
    }

    /**
     * Dummy Json response string.
     *
     * @return String
     */
    private String dummyJsonResponseString() {
        StringBuffer sb = new StringBuffer(); // StringBuffer instance.
        sb.append("{\"responder_name\": \"premia\", ");
        sb.append("\"responder_message\": \"premia_token\", ");
        sb.append("\"error_message\": \"\", ");
        sb.append("\"premiaToken\": \"http://development.premia.com\"}");
        return sb.toString();
    }

    /**
     * Dummy Json response string with error message.
     *
     * @return String
     */
    private String dummyJsonResponseStringWithError() {
        StringBuffer sb = new StringBuffer(); // StringBuffer instance.
        sb.append("{\"responder_name\": \"premia\", ");
        sb.append("\"responder_message\": \"premia_token\", ");
        sb.append("\"error_message\": \"THERE WAS AN ERROR!\", ");
        sb.append("\"premiaToken\": \"\"}");
        return sb.toString();
    }

    /**
     * Dummy Json response string with premia Balance.
     *
     * @return String
     */
    private String dummyPremiaJsonResponseString() {
        StringBuffer sb = new StringBuffer(); // StringBuffer instance.
        sb.append("{\"error\": \"\", ");
        sb.append("\"tsysBalanceRewards\": \"10\"}");
        return sb.toString();
    }

    /**
     * Dummy Json response string with error message.
     *
     * @return String
     */
    private String dummyPremiaJsonResponseStringWithError() {
        StringBuffer sb = new StringBuffer(); // StringBuffer instance.
        sb.append("{\"error\": \"N/A\", ");
        sb.append("\"tsysBalanceRewards\": \"\"}");
        return sb.toString();
    }

    /**
     * Dummy Json response string with error message.
     *
     * @return String
     */
    private String dummyJsonRetirementPlanResponseString() {
        StringBuffer sb = new StringBuffer(); // StringBuffer instance.
        sb.append("{\"hasRetPlan\":true,\"retplan\":{ ");
        sb.append("\"plans\":[{\"accountLast4Num\":\"x8017\",\"planid\":\"930098017\", ");
        sb.append("\"plannam\":\"HGS Falcon Health Solutions PR, LLC Savings and Ret Plan\",\"plantypecd\":\"4\", ");
        sb.append("\"discrmtchcd\":\"N\",\"deferentnum\":\"0\",\"erid\":\"327\", ");
        sb.append("\"matchDescription\":\"Match 0.50% of employee deferral up to 6.00% and limit annual match to 6.00% of participants compensation.\", ");
        sb.append("\"planTotalBalance\":18690.84}, ");
        sb.append(" {\"accountLast4Num\":\"x3011\",\"planid\":\"930303011\",\"plannam\":\"Medical Card System Savings Plan\",\"plantypecd\":\"4\", ");
        sb.append("\"discrmtchcd\":\"N\",\"deferentnum\":\"0\",\"erid\":\"763\",\"matchDescription\":\".\",\"planTotalBalance\":1167.18}]}, ");
        sb.append("\"retirementPlanAccountNewBadge\":false,\"retplanDowntimeMessage\":false}");
        return sb.toString();
    }

    /**
     * Dummy Json response string with error message.
     *
     * @return String
     */
    private String dummyRSAJsonResponseStrinng() {

        StringBuffer sb = new StringBuffer(); // StringBuffer instance.

        sb.append("{\"error_message\": \"RSA\", ");
        sb.append("\"device_info_rsa\": \"[{id:myid,device:fewt24,deviceOS:Android,macAddress:23-434-112-234}]\", ");
        sb.append("\"rsa_cookie\": \"myCookie\", ");
        sb.append("\"answer\": \"\"}");

        return sb.toString();

    }

    /**
     * Dummy Password Json response string.
     *
     * @return String
     */
    private String dummyPasswordJsonResponseString() {

        StringBuffer sb = new StringBuffer();

        sb.append("{ ");
        sb.append("  \"responder_name\": \"portal\",");
        sb.append("  \"rsa_cookie\": \"PMV6LEcjkumL5LTsZYuRGP41aHplravWp%2Be2co2Zv78JedjcXLndQtjS9F%2Berdg%2BCf3w5QxKlIlrBBEV96I%2FAdtaRG7w%3D%3D\",");
        sb.append("  \"responder_message\": \"account_information\",");
        sb.append("	\"page_title\": \"Portal\",");
        sb.append("	\"error\": \"\",");
        sb.append("	\"goback\": \"\",");
        sb.append("	\"flags\": {");
        sb.append("			\"deletedUser\":  false ,");
        sb.append("			\"fullyEnrolledCustomer\":  true  ,");
        sb.append("			\"accounts\":  true  ,");
        sb.append("			\"guardianRepPayeeAccts\":  false ,");
        sb.append("			\"secins\":  false ,");
        sb.append("			\"programs\":  false 	},");
        sb.append("	\"content\": {");
        sb.append("		\"athmSso\": \"true\",");
        sb.append("		\"pushNotifications\": \"true\",");
        sb.append("		\"limitsPerSegment\": \"true\",");
        sb.append("		\"ebills\" : \"\",");
        sb.append("		\"outreach\" : \"\",");
        sb.append("		\"showAlertsSplash\" :  \"\" ,");
        sb.append("		\"isComercialCustomer\" : \"false\",");
        sb.append("		\"MBCA1559\":\"true\",");
        sb.append("		\"showMarketplace\":\"true\",");
        sb.append("		\"customerEmail\":\"RetailOnlineBanking@evertecinc.com\",");
        sb.append("		\"canOpenAccount\": \"true\",");
        sb.append("		\"isForeignCustomer\": \"false\",");
        sb.append("		\"MBSD3806\":\"true\",");
        sb.append("							\"isPremiumBanking\" : \"false\",");
        sb.append("				\"isWealth\" : \"F\",");
        sb.append("					\"rpDowntimeMessage\" : \"true\",");
        sb.append("				        \"showPrivateBankerButtonApp\" : \"\",");
        sb.append("		\"flagMBFM169\" : \"\",");
        sb.append("		\"flagMBSE2513\" : \"true\",");
        sb.append("        				\"isTransactional\" : true,");
        sb.append("							\"isOOBEnrolled\" : false,");
        sb.append("			    		\"flagMBFIS581\" : true,");
        sb.append("				\"ownership\" : \"\",");
        sb.append("		\"customerInfo\": {");
        sb.append("			\"customerName\": \"Mayra\",");
        sb.append("			\"is_today_birthday\":	 false 		},");
        sb.append("		\"labels\": {");
        sb.append("			\"deletedUserString\": \"We’re sad to see you go! It was great having you as a customer. Your online username and password has been deleted.\",	");
        sb.append("			\"portal.welcome\": \"Hi\",");
        sb.append("			\"portal.label.birthday\": \"Happy Birthday!\",");
        sb.append("			\"portal.popularaccount.label\": \"Popular Accounts\",");
        sb.append("			\"portal.account.name.label\": \"Account Name\",");
        sb.append("			\"portal.account.number.label\": \"Account #\",");
        sb.append("			\"portal.account.balance.label\": \"Balance\",");
        sb.append("			\"portal.account.secutity.inversion.label\": \"Insurance and Securities\",");
        sb.append("			\"detail.sec.ins.information\": \"Insurance and Investment Products are: not deposits - not FDIC Insured. Not bank guaranteed - may lose value. Insurance products also not insured by other government agencies.\",");
        sb.append("			\"delinquency.promise.text\": \"Remember to make your payment by the date and the amount agreed.\",");
        sb.append("			\"delinquency.promise.amount.text\": \"Agreed amount\",");
        sb.append("			\"delinquency.date.text\": \"Date:\",");
        sb.append("			\"delinquency.pastdue.text\": \"Amount and days past due:\",");
        sb.append("			\"delinquency.pastdue1.text\": \"This account is past due. Please make your payment.\",");
        sb.append("			\"delinquency.pastdue2.text\": \"This account is past due. It is important that you make your payment.\",");
        sb.append("			\"delinquency.pastdue.days.text\": \"days\",");
        sb.append("			\"delinquency.pastduecall.text\": \"Please call us at 787-522-1512 for assistance with your payment.\",");
        sb.append("			\"delinquency.paynow.link\": \"Pay\",");
        sb.append("			\"delinquency.skip.text\": \"<strong>We need to update important information on your accounts.</strong><br/><i>Please call us at 787-522-1512.</i>\",");
        sb.append("			\"delinquency.overlimit.text\": \"Your credit card is over the limit.\",");
        sb.append("			\"delinquency.overlimitcall.text\": \"It is important that you call one of the representatives at 787-522-1512.\",");
        sb.append("			\"delinquency.overlimit.amount\": \"Overlimit amount:\",");
        sb.append("			\"delinquency.chargeoff.text\": \"<strong>Our records indicate that you have one or more accounts in charge off.</strong><br/><i>Please call us at 787-522-1535.</i>\"");
        sb.append("		},");
        sb.append("		\"accounts\": [");
        sb.append("							{");
        sb.append("				\"frontEndId\": \"22797936487045100\",");
        sb.append("				\"nickname\": \"Cuenta Popular\",");
        sb.append("				\"apiAccountKey\" : \"ad4631461dc961da01ba834e58096da94c8f165c\",");
        sb.append("				\"accountLast4Num\": \"x1234\",");
        sb.append("				\"accountNumberSuffix\": \"\",");
        sb.append("				\"accountSection\": \"C\",");
        sb.append("				\"subtype\": \"IDA\",");
        sb.append("				\"productId\": \"015\",");
        sb.append("				\"portalBalance\": \"$1,234.56\",");
        sb.append("				\"balanceColorRed\":  false ,");
        sb.append("				\"features\": {");
        sb.append("					\"showStatement\":  true 				},");
        sb.append("				\"newAccount\":  false ,");
        sb.append("				\"showFullAccount\":  false ,");
        sb.append("				");
        sb.append("								");
        sb.append("								");
        sb.append("								");
        sb.append("												");
        sb.append("									\"href\": \"transaction?account=22797936487045100\"");
        sb.append("							}");
        sb.append("					 , 			{");
        sb.append("				\"frontEndId\": \"22797936504132570\",");
        sb.append("				\"nickname\": \"U Save\",");
        sb.append("				\"apiAccountKey\" : \"e9f7d83522c08cad6bc975d9c7a1f79a3cb59145\",");
        sb.append("				\"accountLast4Num\": \"x4321\",");
        sb.append("				\"accountNumberSuffix\": \"\",");
        sb.append("				\"accountSection\": \"S\",");
        sb.append("				\"subtype\": \"IDA\",");
        sb.append("				\"productId\": \"090\",");
        sb.append("				\"portalBalance\": \"123.45\",");
        sb.append("				\"balanceColorRed\":  false ,");
        sb.append("				\"features\": {");
        sb.append("					\"showStatement\":  true 				},");
        sb.append("				\"newAccount\":  false ,");
        sb.append("				\"showFullAccount\":  false ,");
        sb.append("				");
        sb.append("								");
        sb.append("								");
        sb.append("								");
        sb.append("												");
        sb.append("									\"href\": \"transaction?account=22797936504132570\"");
        sb.append("							}");
        sb.append("			");
        sb.append("	");
        sb.append("		],");
        sb.append("		\"rdcAccounts\": [");
        sb.append("						{");
        sb.append("				\"frontEndId\": \"22797936487045100\",");
        sb.append("				\"nickname\": \"Cuenta Popular\",");
        sb.append("				\"apiAccountKey\" : \"ad4631461dc961da01ba834e58096da94c8f165c\",");
        sb.append("				\"accountLast4Num\": \"x1234\",");
        sb.append("				\"accountNumberSuffix\": \"\",");
        sb.append("				\"accountSection\": \"C\",");
        sb.append("				\"subtype\": \"IDA\",");
        sb.append("				\"depositLimit\": \"\"");
        sb.append("		}");
        sb.append("				],");
        sb.append(" ");
        sb.append("		\"secinsLabel\": \"Insurance and Securities\",");
        sb.append("		\"secinsInformation\": \"Insurance and Investment Products are: not deposits - not FDIC Insured. Not bank guaranteed - may lose value. Insurance products also not insured by other government agencies.\"");
        sb.append("	}");
        sb.append("}");

        return sb.toString();
    }

    @Test
    public void whenCallingGetTsysLoyaltyRewardsRedirectURL_GivenAnError_ThenWriterLogException() throws IOException {

        final String dummyFrontendId = "1234567"; // Dummy frontEndId.
        final HashMap<String, Object> dummyParams = new HashMap<>(); // Parameter map.
        dummyParams.put("cardNumber", dummyFrontendId);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getTsysLoyaltyRewardsRedirectUrl(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenThrow(JSONException.class);

        String result = apiClient.getTsysLoyaltyRewardsRedirectURL(dummyFrontendId); // Catalog URL.
        assertNull(result);
    }

    @Test
    public void whenGetTsysAutomaticRedemptionInfo_givenRequestIsGood_thenReturnJson() throws Exception {

        String jsonResponse = "{ \"status\" : \"200\", \"data\" : [{ \"itemCode\" : \"112\", \"recurringCashBackType\" : \"type\" }]}";

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getTsysAutomaticRedemption(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(jsonResponse);

        TsysLoyaltyRewardsRecurringStatementCreditResponse result = apiClient.getTsysAutomaticRedemptionInfo("frontEndId", "method"); // Catalog URL.
        assertNotNull(result);
    }

    @Test
    public void whenGetTsysAutomaticRedemptionInfo_givenFail_thenReturnNull() throws Exception {

        //json success is with arrayJson but this json for this test is wrong
        String jsonResponse = "{ \"status\" : \"200\", \"data\" : { \"itemCode\" : \"112\", \"recurringCashBackType\" : \"type\" }}";

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getTsysAutomaticRedemption(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(jsonResponse);

        TsysLoyaltyRewardsRecurringStatementCreditResponse result = apiClient.getTsysAutomaticRedemptionInfo("frontEndId", "method"); // Catalog URL.
        assertNull(result);
    }

    @Test
    public void whenPostTsysLoyaltyRewardsRedemption_givenRequestIsGood_thenResponseJson() throws Exception {

        String jsonResponse = "{\"data\" : \"data\" , \"responder_name\" : \"cash_rewards_redemption_configuration\",\"content\" : { \"name\" : \"content\"}}";

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.postTsysLoyaltyRewardsRedemption(any(), any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(jsonResponse);

        JSONObject result = apiClient.postTsysLoyaltyRewardsRedemption("frontEndId", new HashMap<>()); // Catalog URL.
        assertNotNull(result);
    }

    @Test
    public void whenPostTsysLoyaltyRewardsRedemption_givenFailed_thenWriteInLog() throws Exception {

        String jsonResponse = "\"data\" : \"data\" , \"responder_name\" : \"cash_rewards_redemption_configuration\",\"content\" : { \"name\" : \"content\"}}";

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.postTsysLoyaltyRewardsRedemption(any(), any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(jsonResponse);

        JSONObject result = apiClient.postTsysLoyaltyRewardsRedemption("frontEndId", new HashMap<>()); // Catalog URL.
        assertNull(result);
    }

    @Test
    public void whenGetTsysLoyaltyRewardsInfo_givenRequestIsGood_thenReturnObject()
            throws Exception {

        String jsonResponse = "{ \"status\" : \"200\", \"data\" : [{ \"canRedeemRewards\" : \"redeemRewards\" , \"rewardsAccountStatus\" : \"2000\",\"availableRewardsBalance\" : \"2000\" , \"minimumRewardsBalance\": \"100\"}]}";

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getTsysLoyaltyRewardsInfo(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(jsonResponse);

        TsysLoyaltyRewardsInfo result = apiClient.getTsysLoyaltyRewardsInfo("frontEndId"); // Catalog URL.
        assertNotNull(result);
    }

    @Test
    public void whenPostMobileCashPrestageTrx_givenRequestIsGood_thenReturnObject() throws Exception {

        String jsonResponse = "{ \"responder_name\" : \"name\",  \"responder_message\" : \"msg\"}";

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.postEasyCashPendingTrx(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(jsonResponse);

        EasyCashTrx result = apiClient.postMobileCashPrestageTrx(new MobileCashTrx(), "frontEndId"); // Catalog URL.
        assertNotNull(result);
    }

    @Test
    public void whenPostMobileCashPrestageTrx_givenDepositContent_thenReturnObject() throws Exception {

        String jsonResponse = "{ \"responder_name\" : \"name\",  \"responder_message\" : \"msg\"}";

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.postEasyCashPendingTrx(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(jsonResponse);

        MobileCashTrx request = new MobileCashTrx();
        request.setTranType("DEPOSIT");

        EasyCashTrx result = apiClient.postMobileCashPrestageTrx(request, "frontEndId"); // Catalog URL.
        assertNotNull(result);
    }

    @Test
    public void whenGetEasyCashAccts_givenRequestIsGood_thenReturnObject() throws Exception {

        Gson gson = new Gson();

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.easyCashAccounts(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new MobileCashAcctsResponse()));

        MobileCashAcctsResponse result = apiClient.getEasyCashAccts(true); // Catalog URL.
        assertNotNull(result);
    }


    @Test
    public void whenGetMobileCashAccts_givenRequestIsGood_thenReturnObject() throws Exception {

        Gson gson = new Gson();

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.mobileCashAccountPath()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new MobileCashAcctsResponse()));

        MobileCashAcctsResponse result = apiClient.getMobileCashAccts(); // Catalog URL.
        assertNotNull(result);
    }

    @Test
    public void whenGetEasyCashPendingTrx_givenRequestIsGood_thenReturnObject() throws Exception {

        Gson gson = new Gson();

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getEasyCashPendingTrx(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new EasyCashTrx()));

        EasyCashTrx result = apiClient.getEasyCashPendingTrx(true); // Catalog URL.
        assertNotNull(result);
    }

    @Test
    public void whenGetMobileCashPendingTrx_givenRequestIsGood_thenReturnObject() throws Exception {

        Gson gson = new Gson();

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.mobileCashPendingTrxPath()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new MobileCashTrxInfo()));

        MobileCashTrxInfo result = apiClient.getMobileCashPendingTrx(); // Catalog URL.
        assertNotNull(result);
    }

    @Test
    public void whenGetRetirementPlanInfo_givenFailed_returnWriteInLog() throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getRetirementPlanInfoUrl(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenThrow(IOException.class);

        apiClient.getRetirementPlanInfo();
    }

    @Test
    public void whenGetPremiaInfo_givenFailed_returnWriteInLog() throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getLoyaltyRewardsBalanceUrl(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenThrow(IOException.class);

        apiClient.getPremiaInfo();
    }

    @Test
    public void whenFetchCustomerEntitlements_givenRequestIsGood_thenReturnCustomerEntitlementsObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.customerEntitlements()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new CustomerEntitlements()));

        CustomerEntitlements customerEntitlements = apiClient.fetchCustomerEntitlements();
        assertNotNull(customerEntitlements);
    }

    @Test
    public void whenBindCustomerDevice_givenRequestIsGood_thenReturnObject()
            throws Exception {

        BluetoothAdapter bluetoothAdapter = mock(BluetoothAdapter.class);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.addCustomerDevice(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new EnrollmentLiteResponse()));
        PowerMockito.mockStatic(BluetoothAdapter.class);
        BDDMockito.given(BluetoothAdapter.getDefaultAdapter()).willReturn(bluetoothAdapter);
        when(bluetoothAdapter.getName()).thenReturn("PopularDevice");

        EnrollmentLiteResponse enrollmentLiteResponse = apiClient.bindCustomerDevice("CASHDROP", true);
        assertNotNull(enrollmentLiteResponse);
    }

    @Test
    public void whenLiteNonCustomerInfoSubmit_givenRequestIsGood_thenReturnObject()
            throws Exception {

        EnrollmentLiteRequest enrollmentLiteRequest = new EnrollmentLiteRequest();
        enrollmentLiteRequest.setCountry("Puerto rico");
        enrollmentLiteRequest.setDeviceId("id");
        enrollmentLiteRequest.setEmail("test@popular.com");
        enrollmentLiteRequest.setFirstName("test");
        enrollmentLiteRequest.setLastName("popular");
        enrollmentLiteRequest.setPhoneNumber("82998771122");

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.liteEnrollmentVerification(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new EnrollmentLiteResponse()));
        when(FeatureFlags.TEST_NO_INTERNET()).thenReturn(false);

        EnrollmentLiteResponse enrollmentLiteResponse = apiClient.liteNonCustomerInfoSubmit(enrollmentLiteRequest);
        assertNotNull(enrollmentLiteResponse);

    }

    @Test
    public void whenIsCustomerLiteEnrolled_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getCustomerLiteEnrolled(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new EnrollmentLiteResponse()));
        when(FeatureFlags.TEST_NO_INTERNET()).thenReturn(false);

        EnrollmentLiteResponse enrollmentLiteResponse = apiClient.isCustomerLiteEnrolled("token");
        assertNotNull(enrollmentLiteResponse);
    }

    @Test
    public void whenLiteCustomerInfoSubmit_givenRequestIsGood_thenReturnObject()
            throws Exception {

        EnrollmentLiteRequest enrollmentLiteRequest = new EnrollmentLiteRequest();
        enrollmentLiteRequest.setCountry("Puerto rico");
        enrollmentLiteRequest.setDeviceId("id");
        enrollmentLiteRequest.setEmail("test@popular.com");
        enrollmentLiteRequest.setFirstName("test");
        enrollmentLiteRequest.setLastName("popular");
        enrollmentLiteRequest.setPhoneNumber("82998771122");
        enrollmentLiteRequest.setPhoneProvider("8092122233");

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.liteCustomerEnrollmentInfoSubmit(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new EnrollmentLiteResponse()));
        when(FeatureFlags.TEST_NO_INTERNET()).thenReturn(false);

        EnrollmentLiteResponse enrollmentLiteResponse = apiClient.liteCustomerInfoSubmit(enrollmentLiteRequest);
        assertNotNull(enrollmentLiteResponse);
    }

    @Test
    public void whenGenerateSmsCode_givenParamsExist_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.generateSmsCode(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new EnrollmentLiteResponse()));

        EnrollmentLiteResponse enrollmentLiteResponse = apiClient.generateSmsCode(true);
        assertNotNull(enrollmentLiteResponse);
    }

    @Test
    public void whenGenerateSmsCode_givenParamsNotExist_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getGenerateSmsCode()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new EnrollmentLiteResponse()));

        EnrollmentLiteResponse enrollmentLiteResponse = apiClient.generateSmsCode(false);
        assertNotNull(enrollmentLiteResponse);
    }

    @Test
    public void whenValidateSmsCode_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.validateSmsCode(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new EnrollmentLiteResponse()));
        when(FeatureFlags.TEST_NO_INTERNET()).thenReturn(false);

        EnrollmentLiteResponse enrollmentLiteResponse = apiClient.validateSmsCode("123");
        assertNotNull(enrollmentLiteResponse);
    }

    @Test
    public void whenCreateLiteProfile_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.createLiteProfile()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new EnrollmentLiteCompleteResponse()));
        when(FeatureFlags.TEST_NO_INTERNET()).thenReturn(false);

        EnrollmentLiteCompleteResponse enrollmentLiteResponse = apiClient.createLiteProfile();
        assertNotNull(enrollmentLiteResponse);
    }

    @Test
    public void whenHasMobilePhoneInAlerts_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.hasMobilePhoneAlerts()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new EnrollmentLiteResponse()));
        when(FeatureFlags.TEST_NO_INTERNET()).thenReturn(false);

        EnrollmentLiteResponse enrollmentLiteResponse = apiClient.hasMobilePhoneInAlerts();
        assertNotNull(enrollmentLiteResponse);
    }

    @Test
    public void whenMobilePhoneProviders_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.mobilePhoneProviders()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new AthmEnrollPhone()));
        when(FeatureFlags.TEST_NO_INTERNET()).thenReturn(false);

        AthmEnrollPhone athmEnrollPhone = apiClient.getPhoneProviders();
        assertNotNull(athmEnrollPhone);
    }

    public void whenFetchEbills_ThenReturnEbills() throws Exception {
        Gson gson = new Gson();
        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.ebppPortalInbox()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new EBills()));

        assertNotNull(apiClient.fetchEbills());
    }

    @Test
    public void whenFetchTransfers_ThenReturnTransfers() throws Exception {
        Gson gson = new Gson();
        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getTransfer()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new Transfer()));

        assertNotNull(apiClient.fetchTransfers());
    }

    @Test
    public void whenMakeTransfer_GivenTransferContentAndNeedsConfirmationTrue_ThenReturnTransferActive() throws Exception {
        Gson gson = new Gson();
        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.transfer(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new TransferActive()));

        assertNotNull(apiClient.makeTransfer("accountFrom", "accountTo",
                "amount", "effectiveDate", "reccurent", true));
    }

    @Test
    public void whenMakeTransfer_GivenTransferContentAndNeedsConfirmationFalse_ThenReturnTransferActive() throws Exception {
        Gson gson = new Gson();
        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.transfer(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new TransferActive()));

        assertNotNull(apiClient.makeTransfer("accountFrom", "accountTo",
                "amount", "effectiveDate", "reccurent", false));
    }

    @Test
    public void whenFetchPayments_ThenReturnPayment() throws Exception {
        Gson gson = new Gson();
        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getQuickPayment()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new Payment()));

        assertNotNull(apiClient.fetchPayments());
    }

    @Test
    public void whenMakePayment_GivenPaymentContentAndNeedsConfirmationTrue_ThenReturnPaymentActive() throws Exception {
        Gson gson = new Gson();
        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.quickPayment(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new PaymentActive()));

        assertNotNull(apiClient.makePayment("accountFrom", "payeeid",
                "effectiveDate", "amount", true));
    }

    @Test
    public void whenMakePayment_GivenPaymentContentAndNeedsConfirmationFalse_ThenReturnPaymentActive() throws Exception {
        Gson gson = new Gson();
        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.quickPayment(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new PaymentActive()));

        assertNotNull(apiClient.makePayment("accountFrom", "payeeid",
                "effectiveDate", "amount", false));
    }

    @Test
    public void whenFetchTransactions_GivenTransactionContent_ThenReturnAccountTransactions() throws Exception {
        Gson gson = new Gson();
        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.transaction(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new AccountTransactions()));

        assertNotNull(apiClient.fetchTransactions("account", true, 1, 1));
    }

    @Test
    public void whenFetchCCATransactions_GivenCCATransactionsContent_ThenReturnAccountTransactions() throws Exception {
        Gson gson = new Gson();
        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.ccaInProcessTransactions(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new AccountTransactions()));

        assertNotNull(apiClient.fetchCCATransactions("account", true, 1, 1));
    }

    @Test
    public void whenFetchLatestPaymentHistory_ThenReturnPaymentHistory() throws Exception {
        Gson gson = new Gson();
        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.paymentHistory(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new PaymentHistory()));

        assertNotNull(apiClient.fetchLatestPaymentHistory());
    }

    @Test
    public void whenFetchPaymentHistoryByPayee_GivenPayeeId_ThenReturnPaymentHistory() throws Exception {
        Gson gson = new Gson();
        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.paymentHistory(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new PaymentHistory()));

        assertNotNull(apiClient.fetchPaymentHistoryByPayee("payeeId"));
    }

    @Test
    public void whenFetchPaymentHistoryByDate_GivenDate_ThenReturnPaymentHistory() throws Exception {
        Gson gson = new Gson();
        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.paymentHistory(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new PaymentHistory()));

        assertNotNull(apiClient.fetchPaymentHistoryByDate("date"));
    }

    @Test
    public void whenFetchLatestTransferHistory_ThenReturnTransferHistory() throws Exception {
        Gson gson = new Gson();
        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.transferHistory(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new TransferHistory()));

        assertNotNull(apiClient.fetchLatestTransferHistory());
    }

    @Test
    public void whenFetchTransferHistoryByAccount_GivenAccountNumber_ThenReturnTransferHistory() throws Exception {
        Gson gson = new Gson();
        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.transferHistory(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new TransferHistory()));

        assertNotNull(apiClient.fetchTransferHistoryByAccount("accountNumber"));
    }

    @Test
    public void whenFetchTransferHistoryByDate_GivenAccountNumber_ThenReturnTransferHistory() throws Exception {
        Gson gson = new Gson();
        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.transferHistory(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new TransferHistory()));

        assertNotNull(apiClient.fetchTransferHistoryByDate("date"));
    }

    @Test
    public void whenDeleteInProcessPayment_GivenPaymentContent_ThenReturnPaymentHistory() throws Exception {
        Gson gson = new Gson();
        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.paymentHistory(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new PaymentHistory()));

        assertNotNull(apiClient.deleteInProcessPayment("favId", "modPayment"));
    }

    @Test
    public void whenDeleteInProcessTransfer_GivenTransferContent_thenReturnTransferHistory() throws Exception {
        Gson gson = new Gson();
        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.transferHistory(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(gson.toJson(new TransferHistory()));

        assertNotNull(apiClient.deleteInProcessTransfer("favId", "modTransfer"));
    }

    @Test
    public void whenGlobalStatus_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.globalStatus()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new GlobalStatus()));

        String response = apiClient.globalStatus();
        assertNotNull(response);
    }

    @Test
    public void whenSessionPing_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.sessionPing()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn("{\"responder_name\":\"name\"}");

        String result = apiClient.sessionPing();
        assertNotNull(result);

    }

    @Test
    public void whenRemoveMessage_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.removeMessage()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn("{\"responder_name\":\"name\"}");

        String result = apiClient.removeMessage();
        assertNotNull(result);

    }

    @Test
    public void whenGetBalance_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.mobileGetBalanceInfo(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new Customer()));

        Customer result = apiClient.getBalances("token");
        assertNotNull(result);

    }

    @Test
    public void whenPostAuthentication_givenRequestIsGood_thenReturnObject()
            throws Exception {

        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("panReferenceID", "1212");
        params.put("tokenRequestorID", "2121");
        params.put("tokenReferenceID", "241");
        params.put("panLast4", "1234");
        params.put("deviceID", "android");
        params.put("walletAccountID", "wallet");

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.mobileTokenization(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn("result");

        String result = apiClient.postAuthentication(params, "type");
        assertNotNull(result);

    }

    @Test
    public void whenGetNotificationCenterParameters_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.notificationCenter()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new NotificationCenter()));

        NotificationCenter result = apiClient.getNotificationCenterParameters();
        assertNotNull(result);

    }

    @Test
    public void whenPostPushSplashDecision_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.mobileAlertsSaveSplashDecision(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new NotificationCenter()));

        apiClient.postPushSplashDecision("decision");

    }

    @Test
    public void whenTokenOpac_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.tokenOpac(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new OpenAccountUrl()));

        OpenAccountUrl openAccountUrl = apiClient.postTokenOpac();
        assertNotNull(openAccountUrl);

    }

    @Test
    public void whenRenewPermId_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.renewPermID(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new OpenAccountUrl()));

        apiClient.renewPermId();

    }

    @Test
    public void whenPostPushTokenInfo_givenCallMobileCheckPushTokenStatus_thenReturnObject()
            throws Exception {

        Context context = mock(Context.class);
        PackageManager packageManager = mock(PackageManager.class);
        BluetoothAdapter bluetoothAdapter = mock(BluetoothAdapter.class);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.mobileCheckPushTokenStatus(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new PushTokenResponse()));
        when(context.getPackageManager()).thenReturn(packageManager);
        when(packageManager.hasSystemFeature(any())).thenReturn(true);
        PowerMockito.mockStatic(BluetoothAdapter.class);
        BDDMockito.given(BluetoothAdapter.getDefaultAdapter()).willReturn(bluetoothAdapter);
        when(bluetoothAdapter.getName()).thenReturn("PopularDevice");
        PowerMockito.mockStatic(Html.class);
        BDDMockito.given(Html.fromHtml(any())).willReturn(mock(Spanned.class));

        PushTokenRequest pushTokenRequest = new PushTokenRequest(context);

        PushTokenResponse pushTokenResponse = apiClient.postPushTokenInfo(pushTokenRequest, true);
        assertNotNull(pushTokenResponse);

    }


    @Test
    public void whenPostPushTokenInfo_givenCallMobileSavePushToken_thenReturnObject()
            throws Exception {

        Context context = mock(Context.class);
        PackageManager packageManager = mock(PackageManager.class);
        BluetoothAdapter bluetoothAdapter = mock(BluetoothAdapter.class);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.mobileSavePushToken(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new PushTokenResponse()));
        when(context.getPackageManager()).thenReturn(packageManager);
        when(packageManager.hasSystemFeature(any())).thenReturn(true);
        PowerMockito.mockStatic(BluetoothAdapter.class);
        BDDMockito.given(BluetoothAdapter.getDefaultAdapter()).willReturn(bluetoothAdapter);
        when(bluetoothAdapter.getName()).thenReturn("PopularDevice");
        PowerMockito.mockStatic(Html.class);
        BDDMockito.given(Html.fromHtml(any())).willReturn(mock(Spanned.class));

        PushTokenRequest pushTokenRequest = new PushTokenRequest(context);

        PushTokenResponse pushTokenResponse = apiClient.postPushTokenInfo(pushTokenRequest, false);
        assertNotNull(pushTokenResponse);

    }


    @Test
    public void whenGetAccountPlastics_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.mobileAthOnOff(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new OnOffPlastics()));
        when(FeatureFlags.TEST_NO_INTERNET()).thenReturn(false);

        OnOffPlastics onOffPlastics = apiClient.getAccountPlastics("id");
        assertNotNull(onOffPlastics);

    }

    @Test
    public void whenChangePlasticStatus_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.mobileAthOnOff(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new OnOffPlastics()));
        when(FeatureFlags.TEST_NO_INTERNET()).thenReturn(false);

        OnOffPlastics onOffPlastics = apiClient.changePlasticStatus("id", "action");
        assertNotNull(onOffPlastics);

    }

    @Test
    public void whenGetOnOffCardPlastics_givenRequestIsGood_thenReturnObject()
            throws Exception {

        List<OnOffCardCounter> list = new ArrayList<>();
        OnOffCardCounter onOffCardCounter = new OnOffCardCounter();
        onOffCardCounter.setOnOffCount(1);
        onOffCardCounter.setPlasticFrontEndId("end");
        list.add(onOffCardCounter);
        list.add(onOffCardCounter);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getOnOffCardPlastics()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(list));

        List<OnOffCardCounter> onOffPlastics = apiClient.getOnOffPortalCardCount();
        assertFalse(onOffPlastics.isEmpty());

    }

    @Test
    public void whenGetAthMSendMoneyInfo_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getAthMSendMoney()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new AthmSendMoneyInfo()));

        AthmSendMoneyInfo athmSendMoneyInfo = apiClient.getAthmSendMoneyInfo();
        assertNotNull(athmSendMoneyInfo);

    }

    @Test
    public void whenPostAthMSendMoneyInput_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.athMSendMoney(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new AthmSendMoneyInfo()));

        AthmSendMoneyInput athmSendMoneyInfo = apiClient.postAthmSendMoneyInput("200", "test", "8092123412");
        assertNotNull(athmSendMoneyInfo);

    }

    @Test
    public void whenPostAthMSendMoneyConfirmation_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.athMSendMoney(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new AthmSendMoneyInfo()));

        AthmSendMoneyConfirmation athmSendMoneyInfo = apiClient.postAthmSendMoneyConfirmation();
        assertNotNull(athmSendMoneyInfo);

    }

    @Test
    public void whenGetAthmEnrollInfo_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getAthMEnroll()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new AthmEnrollInfo()));

        AthmEnrollInfo athmEnrollInfo = apiClient.getAthmEnrollInfo();
        assertNotNull(athmEnrollInfo);

    }

    @Test
    public void whenPostAthmEnrollPhone_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.athMEnroll(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new AthmEnrollPhone()));

        AthmEnrollPhone athmEnrollPhone = apiClient.postAthmEnrollPhone();
        assertNotNull(athmEnrollPhone);

    }

    @Test
    public void whenPostAthmEnrollPhone_givenRequestIsGoodWithPhone_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.athMEnroll(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new AthmEnrollPhoneCode()));

        AthmEnrollPhoneCode athmEnrollPhone = apiClient.postAthmEnrollPhone("80921232122", "8292123232");
        assertNotNull(athmEnrollPhone);

    }

    @Test
    public void whenPostAthmEnrollCard_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.athMEnroll(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new AthmEnrollCard()));

        AthmEnrollCard athmEnrollCard = apiClient.postAthmEnrollCard();
        assertNotNull(athmEnrollCard);

    }

    @Test
    public void whenPostAthmEnrollAccountPostAthmEnrollCard_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.athMEnroll(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new AthmEnrollAccount()));

        AthmEnrollAccount athmEnrollCard = apiClient.postAthmEnrollAccount("123123312", "04", "2027", "12341234123412334");
        assertNotNull(athmEnrollCard);

    }

    @Test
    public void whenPostAthmEnrollLogin_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.athMEnroll(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new AthmEnrollConfirmation()));

        AthmEnrollConfirmation athmEnrollCard = apiClient.postAthmEnrollLogin("test", "123412");
        assertNotNull(athmEnrollCard);

    }

    @Test
    public void whenPostAthmEnrollRegistration_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.athMEnroll(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new AthmEnrollConfirmation()));

        AthmEnrollConfirmation athmEnrollCard = apiClient.postAthmEnrollRegistration("test", "123412", "123412", "TERMS");
        assertNotNull(athmEnrollCard);

    }

    @Test
    public void whenPostAthmTokenInfo_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.athMSsoToken(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new AthmSSOInfo()));

        AthmSSOInfo athmSSOInfo = apiClient.postAthmTokenInfo("token", true);
        assertNotNull(athmSSOInfo);

    }

    @Test
    public void whenLogoutAthmSso_givenRequestIsGood_thenReturnObject()
            throws Exception {

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.athMsSoUnbind()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(new AthmSSOInfo()));

        AthmSSOInfo athmSSOInfo = apiClient.logoutAthmSso();
        assertNotNull(athmSSOInfo);

    }
    public void whenCallingFetchRemoteDepositHistory_GivenValidParams_ThenReturnResponse() throws Exception {

        final String bodyResponse = "{ \n" +
                "  \"responder_name\": \"portal\",\n" +
                "  \"rsa_cookie\": \"PMV6LqHxfNMOy\",\n" +
                "  \"responder_message\": \"account_information\"}";

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.remoteDepositHistory()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(bodyResponse);

        RemoteDepositHistory remoteDepositHistoryResponse = apiClient.fetchRemoteDepositHistory();

        assertNotNull(remoteDepositHistoryResponse);
    }

    @Test
    public void whenCallingFetchReviewCheck_GivenValidParams_ThenReturnResponse() throws Exception {

        final HashMap<String, Object> params = new HashMap<>();

        final String bodyResponse = "{ \n" +
                "  \"responder_name\": \"portal\",\n" +
                "  \"rsa_cookie\": \"PMV6LqHxfNMOy\",\n" +
                "  \"responder_message\": \"account_information\"}";
        params.put("depositId", "1");

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.remoteDepositHistoryImages(params)).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(bodyResponse);

        RDCCheckItem remoteDepositHistoryResponse = apiClient.fetchReviewCheck("1");

        assertNotNull(remoteDepositHistoryResponse);
    }

    @Test
    public void whenCallingRemoteDepositDCenRoll_GivenValidParams_ThenReturnResponse() throws Exception {

        final String bodyResponse = "{ \n" +
                "  \"responder_name\": \"portal\",\n" +
                "  \"rsa_cookie\": \"PMV6LqHxfNMOy\",\n" +
                "  \"responder_message\": \"account_information\"}";

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.remoteDepositDCenRoll()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(bodyResponse);

        DepositCheckEnrollment depositCheckEnrollmentResponse = apiClient.enrollInRDC();

        assertNotNull(depositCheckEnrollmentResponse);
    }

    @Test
    public void whenCallingAcceptedTermsInRDC_GivenValidParams_ThenReturnResponse() throws Exception {

        final String bodyResponse = "{ \n" +
                "  \"responder_name\": \"portal\",\n" +
                "  \"rsa_cookie\": \"PMV6LqHxfNMOy\",\n" +
                "  \"responder_message\": \"account_information\"}";

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getRemoteDepositEasyAcceptTerms()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(bodyResponse);

        AcceptedTermsInRDC acceptTermsInRDCtResponse = apiClient.acceptedTermsInRDC();

        assertNotNull(acceptTermsInRDCtResponse);
    }

    @Test
    public void whenCallingSendAcceptTermsInRDC_GivenValidParams_ThenReturnResponse() throws Exception {

        final String bodyResponse = "{ \n" +
                "  \"responder_name\": \"portal\",\n" +
                "  \"rsa_cookie\": \"PMV6LqHxfNMOy\",\n" +
                "  \"responder_message\": \"account_information\"}";

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.remoteDepositEasyAcceptTerms()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(bodyResponse);

        SendAcceptTermsInRDC sendTermsInRDCResponse = apiClient.sendAcceptTermsInRDC();

        assertNotNull(sendTermsInRDCResponse);
    }

    @Test
    public void whenCallingDepositCheck_GivenValidParams_ThenReturnResponse() throws Exception {

        final HashMap<String, Object> params = new HashMap<String, Object>();
        String frontImage = "frontImage";
        String backImageString = "backImage";
        String frontendId = "1";
        String amount ="10.0";

        params.put("frontImageStr", frontImage);
        params.put("backImageStr", backImageString);
        params.put("frontEndId", frontendId);
        params.put("amount", amount);
        params.put("deviceType", "Android");
        params.put("osName", "Not Available");
        params.put("osVersion", null);

        final String bodyResponse = "{ \n" +
                "  \"responder_name\": \"deposits\",\n" +
                "  \"rsa_cookie\": \"PMV6LqHxfNMOy\",\n" +
                "  \"responder_message\": \"account_information\"}";

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.remoteDepositSubmitDeposit(params)).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(bodyResponse);

        DepositCheckReceipt depositCheckReceiptResponse = apiClient.depositCheck(frontendId,amount,frontImage,backImageString);

        assertNotNull(depositCheckReceiptResponse);
    }

    @Test
    public void whenCalling_RsaCheckStatus_GivenValidParams_ThenReturnResponse() throws Exception {

        final String bodyResponse = "{ \n" +
                "  \"responder_name\": \"rsaCheckStatus\",\n" +
                "  \"rsa_cookie\": \"PMV6LqHxfNMOy\",\n" +
                "  \"responder_message\": \"account_information\"}";

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.rsaCheckStatus()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(bodyResponse);

        RsaModResponse rsaModResponse = apiClient.rsaCheckStatus();

        assertNotNull(rsaModResponse);
    }

    @Test
    public void whenCalling_PostGenerateOtpCode_GivenValidParams_ThenReturnResponse() throws Exception {

        final String bodyResponse = "{}";
        final HashMap<String, Object> params = new HashMap<String, Object>();
        final String resendCode = "resendCode";

        params.put("resendCode", resendCode);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.generateOtpCode(params)).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(bodyResponse);

        GenerateOtpCode generateOtpCodeResponse = apiClient.postGenerateOtpCode(resendCode);

        assertNotNull(generateOtpCodeResponse);
    }

    @Test
    public void whenCalling_PostGenerateOtpCode_GivenInvalidParams_ThenReturnIOExceptionResponse() throws IOException {

        final String bodyResponse = "{}";
        final HashMap<String, Object> params = new HashMap<String, Object>();
        final String resendCode = "resendCode";

        params.put("resendCode", resendCode);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.generateOtpCode(params)).thenThrow(IOException.class);

        GenerateOtpCode generateOtpCodeResponse = apiClient.postGenerateOtpCode(resendCode);

        assertNull(generateOtpCodeResponse);
    }

    @Test
    public void whenCalling_PostValidateOtpCode_GivenValidParams_ThenReturnResponse() throws IOException {

        final String bodyResponse = "{}";
        final HashMap<String, Object> params = new HashMap<String, Object>();
        final String code = "code";

        params.put("code", code);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.validateOtpCode(params)).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(bodyResponse);

        ValidateOtpCode validateOtpCodeResponse = apiClient.postValidateOtpCode(code);

        assertNotNull(validateOtpCodeResponse);
    }

    @Test
    public void whenCalling_PostValidateOtpCode_GivenInvalidParams_ThenReturnIOExceptionResponse() throws IOException {

        final String bodyResponse = "{}";
        final HashMap<String, Object> params = new HashMap<String, Object>();
        final String code = "code";

        params.put("code", code);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.validateOtpCode(params)).thenThrow(IOException.class);

        ValidateOtpCode validateOtpCodeResponse = apiClient.postValidateOtpCode(code);

        assertNull(validateOtpCodeResponse);
    }

    @Test
    public void whenCalling_PostAcceptTermsMarketplace_GivenValidParams_ThenReturnResponse() throws IOException {

        final String bodyResponse = "{}";
        final HashMap<String, Object> params = new HashMap<String, Object>();
        final String product = "product";

        params.put("product", product);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.acceptMarketplaceTerms(params)).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(bodyResponse);

        MarketPlaceTermsResponse marketPlaceTermsResponseResponse = apiClient.postAcceptTermsMarketplaceWithProductId(product);

        assertNotNull(marketPlaceTermsResponseResponse);
    }

    @Test
    public void whenCalling_PostAcceptTermsMarketplace_GivenInvalidParams_ThenReturnIOExceptionResponse() throws IOException {

        final String bodyResponse = "{}";
        final HashMap<String, Object> params = new HashMap<String, Object>();
        final String product = "product";

        params.put("product", product);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.acceptMarketplaceTerms(params)).thenThrow(IOException.class);

        MarketPlaceTermsResponse marketPlaceTermsResponseResponse = apiClient.postAcceptTermsMarketplaceWithProductId(product);

        assertNull(marketPlaceTermsResponseResponse);
    }

    @Test
    public void whenCalling_PostDetermineAuthenticationChallenge_GivenInvalidParams_ThenReturnIOExceptionResponse() throws IOException {

        final String bodyResponse = "{}";
        final HashMap<String, Object> params = new HashMap<String, Object>();

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.determineAuthenticationChallenge(params)).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(bodyResponse);

        MarketplaceDeterminateChallenge marketplaceDeterminateChallengeResponse = apiClient.postDetermineAuthenticationChallenge();

        assertNotNull(marketplaceDeterminateChallengeResponse);
    }

    @Test
    public void whenCalling_PostDetermineAuthenticationChallenge_GivenValidParams_ThenReturnResponse() throws IOException {

        final String bodyResponse = "{}";
        final HashMap<String, Object> params = new HashMap<String, Object>();

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.determineAuthenticationChallenge(params)).thenThrow(IOException.class);

        MarketplaceDeterminateChallenge marketplaceDeterminateChallengeResponse = apiClient.postDetermineAuthenticationChallenge();
        assertNull(marketplaceDeterminateChallengeResponse);
    }

    @Test
    public void whenCalling_postUnicaUrl_GivenValidParams_ThenReturnResponse() throws IOException {

        final String bodyResponse = "{}";
        final HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("isOobAuthenticated", true);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getUnicaUrl(params)).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(bodyResponse);

        UnicaUrl unicaUrlResponse = apiClient.postUnicaUrl(true);

        assertNotNull(unicaUrlResponse);
    }

    @Test
    public void whenCalling_postUnicaUrl_GivenInvalidParams_ThenReturnIOExceptionResponse() throws IOException {

        final String bodyResponse = "{}";
        final HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("isOobAuthenticated", true);

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getUnicaUrl(params)).thenThrow(IOException.class);

        UnicaUrl unicaUrlResponse = apiClient.postUnicaUrl(true);

        assertNull(unicaUrlResponse);
    }

    @Test
    public void whenCalling_FetchLogin_GivenValidParams_ThenReturnResponse() throws Exception {

        final String bodyResponse = "{}";

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.loginGet()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(bodyResponse);

        LoginGet loginGetResponse = apiClient.fetchLogin();

        assertNotNull(loginGetResponse);
    }

    @Test
    public void whenCalling_FetchRemoteDepositHistory_GivenValidParams_ThenReturnResponse() throws Exception {

        final String bodyResponse = "{}";

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.remoteDepositHistory()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(bodyResponse);

        RemoteDepositHistory remoteDepositHistoryResponse = apiClient.fetchRemoteDepositHistory();

        assertNotNull(remoteDepositHistoryResponse);
    }

    @Test
    public void whenCalling_GetPushWelcomeParameters_GivenValidParams_ThenReturnResponse() throws Exception {

        final String bodyResponse = "{}";
        final HashMap<String, Object> params = new HashMap<String, Object>();

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.pushWelcomeParameters(params)).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(bodyResponse);

        PushWelcomeParams pushWelcomeParamsResponse = apiClient.getPushWelcomeParameters("param");

        assertNotNull(pushWelcomeParamsResponse);
    }

    @Test
    public void whenCalling_PostOOBChallenge_GivenValidParams_ThenReturnResponse() throws Exception {

        final String bodyResponse = "{}";
        final HashMap<String, Object> params = new HashMap<String, Object>();
        String action = "action";
        String target = "target";
        String code = "code";

        params.put("action", action);
        params.put("_target" + target, "target" + target);
        params.put("code", code);
        params.put("_finish", "finish");

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.oobChallenge(params)).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(bodyResponse);

        RSAChallengeResponse RSAChallengeResponse = apiClient.postOOBChallenge(action, code, true, target);

        assertNotNull(RSAChallengeResponse);
    }

    @Test
    public void whenCalling_GetRSAChallenge_GivenValidParams_ThenReturnResponse() throws Exception {

        final String bodyResponse = "{}";
        final HashMap<String, Object> params = new HashMap<String, Object>();
        String sdkRsaJson = "action";
        String rsaCookie = "target";

        params.put("device_info_rsa", sdkRsaJson);
        params.put("rsa_cookie", rsaCookie);


        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.getMobileCheckRsaChallenge(params)).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(bodyResponse);

        RSAChallengeResponse RSAChallengeResponse = apiClient.getRSAChallenge(sdkRsaJson, rsaCookie);

        assertNotNull(RSAChallengeResponse);
    }

    @Test
    public void whenCalling_FetchEbills_GivenValidParams_ThenReturnResponse() throws Exception {

        final String bodyResponse = "{}";


        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.ebppPortalInbox()).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(bodyResponse);

        EBills eBillsResponse = apiClient.fetchEbills();

        assertNotNull(eBillsResponse);
    }

}


