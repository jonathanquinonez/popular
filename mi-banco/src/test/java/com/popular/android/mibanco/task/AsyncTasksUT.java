package com.popular.android.mibanco.task;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.model.Customer;
import com.popular.android.mibanco.model.DeviceInfo;
import com.popular.android.mibanco.model.OobChallenge;
import com.popular.android.mibanco.util.CryptoUtils;
import com.popular.android.mibanco.util.PushUtils;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.RSACollectUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.ws.ApiClient;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Any;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import java.util.HashMap;

import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import java.util.HashMap;

@RunWith(PowerMockRunner.class)
@PrepareForTest({App.class, AsyncTasks.class, Context.class, ResponderListener.class, RSACollectUtils.class, Utils.class, Activity.class, CryptoUtils.class, Cipher.class, KeyGenerator.class,
        PushUtils.class,BPAnalytics.class})
public class AsyncTasksUT {

    private App mockApp = mock(App.class);

    private AsyncTasks asyncTasks;

    private  Resources resources = mock(Resources.class);

    private Activity mockContext = mock(Activity.class);

    private ResponderListener mockResponderListener = mock(ResponderListener.class);

    private Context mockContextc = mock(Context.class);

    private ApiClient apiClient = mock(ApiClient.class);

    private OobChallenge oobChallenge = mock(OobChallenge.class);

    private  HashMap<String, Object> response = mock(HashMap.class);

    private OobChallenge.OobFlags oobFlags = mock(OobChallenge.OobFlags.class);

    @Mock
    private CryptoUtils cryptoUtils;


    @Captor
    private ArgumentCaptor<Customer> captorCustomer;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);
        mockStatic(App.class);
        mockStatic(Thread.class);
        mockStatic(RSACollectUtils.class);
        mockStatic(Utils.class);
        mockStatic(PushUtils.class);
        mockStatic(BPAnalytics.class);
        mockStatic(CryptoUtils.class);
        mockStatic(KeyGenerator.class);
        mockStatic(Cipher.class);

        when(App.getApplicationInstance()).thenReturn(mockApp);
        when(RSACollectUtils.collectDeviceInfo(any(Context.class))).thenReturn("[{id:myid,device:fewt24,deviceOS:Android,macAddress:23-434-112-234}]");
        when(Utils.getPrefsStringNotNull(MiBancoConstants.RSA_COOKIE, mockApp)).thenReturn("myCookie");
        when(mockApp.getBaseContext()).thenReturn(mockContext);

        asyncTasks = spy(new AsyncTasks(mockApp));
    }

    @Test
    public void whenCallingLogginMethod_VerifyRSA_Data_Collected() throws Exception {

        when(resources.getString(Mockito.anyInt())).thenReturn("please_wait");
        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);

        asyncTasks.login(mockContext, "pepito007","235235",  mockResponderListener);

        verifyStatic(times(1));
        RSACollectUtils.collectDeviceInfo(mockContext);

        verifyStatic(times(1));
        Utils.getPrefsStringNotNull(MiBancoConstants.RSA_COOKIE, mockContext);

    }

    @Test
    public void when_CallingDoInBackgroundLoginTask_With_Password_and_PhoneNumber() throws Exception {
        HashMap<String, Object> response = new HashMap<String, Object>();
        response.put("responder_name", "password");
        response.put("question", "");
        response.put("access_blocked", false);
        response.put("phoneNumber", "vTKv7VlbdygBOWVtPJuAVA==");
        response.put("strKey", "Ix/x8h8Luv3NofuGq++Ra62S+OBedVdY");

        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);
        when(mockApp.getApiClient()).thenReturn(apiClient);

        when(apiClient.postLogin(anyString(),anyString(),anyString())).thenReturn(response);
        when(oobChallenge.getResponderName()).thenReturn("password");
        Utils.setPrefsString(MiBancoConstants.RSA_COOKIE, "", mockContext);

        Integer result = asyncTasks.new LoginTask(mockContext, "pepito007",
                "326992", "", "PMV6LJvT30FQo2sqdnJU62E3Sb", mockResponderListener ).doInBackground();

        Assert.assertSame(0, result);
    }

    @Test
    public void whenCallingQuestionMethod_VerifyRSA_Data_Collected() {

        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);

        asyncTasks.question(mockContext, "sevilla", true, mockResponderListener);

        verifyStatic(times(1));
        RSACollectUtils.collectDeviceInfo(mockContext);

        verifyStatic(times(1));
        Utils.getPrefsStringNotNull(MiBancoConstants.RSA_COOKIE, mockContext);

    }

    @Test
    public void when_CallingDoInBackgroundQuestionTask_Then_ResultIsSuccess() throws Exception {
        mockStatic(CryptoUtils.class);
        HashMap<String, Object> response = new HashMap<>();
        response.put("responder_name", "password");
        response.put("question", "");
        response.put("access_blocked", false);
        response.put("customerToken", "Test123");
        response.put("phoneNumber", "vTKv7VlbdygBOWVtPJuAVA==");
        response.put("strKey", "Ix/x8h8Luv3NofuGq++Ra62S+OBedVdY");
        response.put(MiBancoConstants.CAN_OPEN_ACCOUNT, false);


        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);
        when(mockApp.getApiClient()).thenReturn(apiClient);

        when(apiClient.postSecurityQuestion(anyString(), anyBoolean(),
                anyString(), anyString())).thenReturn(response);

        Integer result = asyncTasks.new QuestionTask(mockContext, "1",
                false, "", "PMV6LJvT30FQo2sqdnJU62E3Sb", mockResponderListener).doInBackground();

        Assert.assertSame(0, result);
    }

    @Test
    public void whenCallingLoginOOBMethod_VerifyRSA_Data_Collected() {

        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);

        asyncTasks.loginOOB(mockContext, "post", "OOBCode", mockResponderListener);

        verifyStatic(times(1));
        RSACollectUtils.collectDeviceInfo(mockContext);

        verifyStatic(times(1));
        Utils.getPrefsStringNotNull(MiBancoConstants.RSA_COOKIE, mockContext);

    }

    @Test
    public void when_CallingDoInBackgroundOobLoginTask_With_OOB_VALIDATE_SMSCODE_Then_ResultIsSuccess() throws Exception {

        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);
        when(mockApp.getApiClient()).thenReturn(apiClient);
        when(apiClient.trySendingOOBCode(Matchers.<String>any(), Matchers.<String>any(), Matchers.<String>any(),
                Matchers.<String>any())).thenReturn(oobChallenge);
        when(oobChallenge.getResponderName()).thenReturn("password");
        when(oobChallenge.getFlags()).thenReturn(oobFlags);

        Integer result = asyncTasks.new OobLoginTask(mockContext, MiBancoConstants.OOB_VALIDATE_SMSCODE,
                "326992", "", "PMV6LJvT30FQo2sqdnJU62E3Sb", mockResponderListener ).doInBackground();

        Assert.assertSame(1, result);
    }

    @Test
    public void when_CallingDoInBackgroundOobLoginTask_With_OOB_VALIDATE_SMSCODE_Then_ResultIsFailure() throws Exception {

        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);
        when(mockApp.getApiClient()).thenReturn(apiClient);
        when(apiClient.trySendingOOBCode(Matchers.<String>any(), Matchers.<String>any(), Matchers.<String>any(),
                Matchers.<String>any())).thenReturn(oobChallenge);
        when(oobChallenge.getResponderName()).thenReturn("password");

        Integer result = asyncTasks.new OobLoginTask(mockContext, MiBancoConstants.OOB_VALIDATE_SMSCODE,
                "326992", "", "PMV6LJvT30FQo2sqdnJU62E3Sb", mockResponderListener ).doInBackground();

        Assert.assertSame(0, result);
    }

    @Test
    public void when_CallingDoInBackgroundOobLoginTask_With_OOB_SEND_SMSCODE_Then_ResultIsSuccess() throws Exception {

        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);
        when(mockApp.getApiClient()).thenReturn(apiClient);
        when(apiClient.oobResendCode(Matchers.<String>any(), Matchers.<String>any(), Matchers.<String>any(),
                Matchers.<String>any())).thenReturn(oobChallenge);
        when(oobChallenge.getResponderName()).thenReturn("password");
        when(oobChallenge.getFlags()).thenReturn(oobFlags);

        Integer result = asyncTasks.new OobLoginTask(mockContext, MiBancoConstants.OOB_SEND_SMSCODE,
                "326992", "", "PMV6LJvT30FQo2sqdnJU62E3Sb", mockResponderListener ).doInBackground();

        Assert.assertSame(1, result);
    }

    @Test
    public void when_CallingDoInBackgroundOobLoginTask_With_OOB_SEND_SMSCODE_Then_ResultIsFailure() throws Exception {

        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);
        when(mockApp.getApiClient()).thenReturn(apiClient);
        when(apiClient.oobResendCode(Matchers.<String>any(), Matchers.<String>any(), Matchers.<String>any(),
                Matchers.<String>any())).thenReturn(oobChallenge);
        when(oobChallenge.getResponderName()).thenReturn("password");

        Integer result = asyncTasks.new OobLoginTask(mockContext, MiBancoConstants.OOB_SEND_SMSCODE,
                "326992", "", "PMV6LJvT30FQo2sqdnJU62E3Sb", mockResponderListener ).doInBackground();

        Assert.assertSame(0, result);
    }


    @Test
    public void when_CallingDoInBackgroundOobLoginTask_With_OOB_SEND_ALT_PHONE_Then_ResultIsSuccess() throws Exception {

        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);
        when(mockApp.getApiClient()).thenReturn(apiClient);
        when(apiClient.trySendingOOBCodeToAltPhone(Matchers.<String>any(), Matchers.<String>any(), Matchers.<String>any(),
                Matchers.<String>any())).thenReturn(oobChallenge);
        when(oobChallenge.getResponderName()).thenReturn("password");
        when(oobChallenge.getFlags()).thenReturn(oobFlags);

        Integer result = asyncTasks.new OobLoginTask(mockContext, MiBancoConstants.OOB_SEND_ALT_PHONE,
                "326992", "", "PMV6LJvT30FQo2sqdnJU62E3Sb", mockResponderListener ).doInBackground();

        Assert.assertSame(1, result);
    }

    @Test
    public void when_CallingDoInBackgroundOobLoginTask_With_OOB_SEND_ALT_PHONE_Then_ResultIsFailure() throws Exception {

        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);
        when(mockApp.getApiClient()).thenReturn(apiClient);
        when(apiClient.trySendingOOBCodeToAltPhone(Matchers.<String>any(), Matchers.<String>any(), Matchers.<String>any(),
                Matchers.<String>any())).thenReturn(oobChallenge);
        when(oobChallenge.getResponderName()).thenReturn("password");

        Integer result = asyncTasks.new OobLoginTask(mockContext, MiBancoConstants.OOB_SEND_ALT_PHONE,
                "326992", "", "PMV6LJvT30FQo2sqdnJU62E3Sb", mockResponderListener ).doInBackground();

        Assert.assertSame(0, result);
    }

    @Test
    public void when_CallingDoInBackgroundOobLoginTask_With_OOB_VALIDATE_CALLCODE_Then_ResultIsSuccess() throws Exception {

        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);
        when(mockApp.getApiClient()).thenReturn(apiClient);
        when(apiClient.tryValidateCall(Matchers.<String>any(), Matchers.<String>any(),
                Matchers.<String>any())).thenReturn(oobChallenge);
        when(oobChallenge.getResponderName()).thenReturn("password");
        when(oobChallenge.getFlags()).thenReturn(oobFlags);

        Integer result = asyncTasks.new OobLoginTask(mockContext, MiBancoConstants.OOB_VALIDATE_CALLCODE,
                "326992", "", "PMV6LJvT30FQo2sqdnJU62E3Sb", mockResponderListener ).doInBackground();

        Assert.assertSame(1, result);
    }

    @Test
    public void when_CallingDoInBackgroundOobLoginTask_With_OOB_VALIDATE_CALLCODE_Then_ResultIsFailure() throws Exception {

        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);
        when(mockApp.getApiClient()).thenReturn(apiClient);
        when(apiClient.tryValidateCall(Matchers.<String>any(), Matchers.<String>any(),
                Matchers.<String>any())).thenReturn(oobChallenge);
        when(oobChallenge.getResponderName()).thenReturn("password");

        Integer result = asyncTasks.new OobLoginTask(mockContext, MiBancoConstants.OOB_VALIDATE_CALLCODE,
                "326992", "", "PMV6LJvT30FQo2sqdnJU62E3Sb", mockResponderListener ).doInBackground();

        Assert.assertSame(0, result);
    }

    @Test
    public void when_CallingDoInBackgroundOobLoginTask_With_OOB_CALL_PHONE_Then_ResultIsSuccess() throws Exception {

        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);
        when(mockApp.getApiClient()).thenReturn(apiClient);
        when(apiClient.oobMakingCall(Matchers.<String>any(), Matchers.<String>any(), Matchers.<String>any(),
                Matchers.<String>any())).thenReturn(oobChallenge);
        when(oobChallenge.getResponderName()).thenReturn("password");
        when(oobChallenge.getFlags()).thenReturn(oobFlags);

        Integer result = asyncTasks.new OobLoginTask(mockContext, MiBancoConstants.OOB_CALL_PHONE,
                "326992", "", "PMV6LJvT30FQo2sqdnJU62E3Sb", mockResponderListener ).doInBackground();

        Assert.assertSame(1, result);
    }

    @Test
    public void when_CallingDoInBackgroundOobLoginTask_With_OOB_CALL_PHONE_Then_ResultIsFailure() throws Exception {

        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);
        when(mockApp.getApiClient()).thenReturn(apiClient);
        when(apiClient.oobMakingCall(Matchers.<String>any(), Matchers.<String>any(), Matchers.<String>any(),
                Matchers.<String>any())).thenReturn(oobChallenge);
        when(oobChallenge.getResponderName()).thenReturn("password");

        Integer result = asyncTasks.new OobLoginTask(mockContext, MiBancoConstants.OOB_CALL_PHONE,
                "326992", "", "PMV6LJvT30FQo2sqdnJU62E3Sb", mockResponderListener ).doInBackground();

        Assert.assertSame(0, result);
    }

    @Test
    public void when_CallingDoInBackgroundOobLoginTask_With_OOB_CALL_ALT_PHONE_Then_ResultIsSuccess() throws Exception {

        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);
        when(mockApp.getApiClient()).thenReturn(apiClient);
        when(apiClient.oobMakingCallToAlt(Matchers.<String>any(), Matchers.<String>any(),
                Matchers.<String>any())).thenReturn(oobChallenge);
        when(oobChallenge.getResponderName()).thenReturn("password");
        when(oobChallenge.getFlags()).thenReturn(oobFlags);

        Integer result = asyncTasks.new OobLoginTask(mockContext, MiBancoConstants.OOB_CALL_ALT_PHONE,
                "326992", "", "PMV6LJvT30FQo2sqdnJU62E3Sb", mockResponderListener ).doInBackground();

        Assert.assertSame(1, result);
    }

    @Test
    public void when_CallingDoInBackgroundOobLoginTask_With_OOB_CALL_ALT_PHONE_Then_ResultIsFailure() throws Exception {

        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);
        when(mockApp.getApiClient()).thenReturn(apiClient);
        when(apiClient.oobMakingCallToAlt(Matchers.<String>any(), Matchers.<String>any(),
                Matchers.<String>any())).thenReturn(oobChallenge);
        when(oobChallenge.getResponderName()).thenReturn("password");

        Integer result = asyncTasks.new OobLoginTask(mockContext, MiBancoConstants.OOB_CALL_ALT_PHONE,
                "326992", "", "PMV6LJvT30FQo2sqdnJU62E3Sb", mockResponderListener ).doInBackground();

        Assert.assertSame(0, result);
    }

    @Test
    public void when_CallingDoInBackgroundOobLoginTask_With_ANOTHER_Then_ResultIsFailure() throws Exception {

        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);
        when(mockApp.getApiClient()).thenReturn(apiClient);

        Integer result = asyncTasks.new OobLoginTask(mockContext, MiBancoConstants.CAN_OPEN_ACCOUNT,
                "326992", "", "PMV6LJvT30FQo2sqdnJU62E3Sb", mockResponderListener ).doInBackground();

        Assert.assertSame(0, result);
    }

    private HashMap<String, Object> responsePostLogin(String type){
        HashMap<String, Object> response =  new HashMap<String, Object>();

        if(type.equals("loginoob")) {
            response.put("responder_name", "loginoob");
            response.put("phoneNumber", "7844668643");
            response.put("rsa_cookie", "PMV6LJvT30FQo2sqdnJU62E3Sb");
            response.put("access_blocked", false);
            response.put("phone", "784-***-**43");
            response.put("responder_message", "primary_phone");
            response.put("challengeType", "OOBSMS");
            response.put("hasAltPhone", false);
            response.put("username", "username");
        }

        if(type.equals("question")) {
            response.put("responder_name", "question");
            response.put("responder_message", "question_information");
            response.put("rsa_cookie", "PMV6LJvT30FQo2sqdnJU62E3Sb");
            response.put("access_blocked", false);
            response.put("question", "In what city was your mother born? (Enter full name of city only)");
        }

        return response;
    }


    @Test
    public void when_CallingDoInBackgroundLoginTask_TypeQuestion() throws Exception {

        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);
        when(mockApp.getApiClient()).thenReturn(apiClient);

        when(mockApp.getApiClient().postLogin(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(responsePostLogin("question"));

        Integer result = asyncTasks.new LoginTask(mockContext, "username",
                "326992", "", "PMV6LJvT30FQo2sqdnJU62E3Sb", mockResponderListener)
                .doInBackground();

        Assert.assertSame(1, result);
    }

    @Test
    public void when_CallingDoInBackgroundLoginTask_TypeLoginoob() throws Exception {

        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);
        when(mockApp.getApiClient()).thenReturn(apiClient);

        when(mockApp.getApiClient().postLogin(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(responsePostLogin("loginoob"));

        Integer result = asyncTasks.new LoginTask(mockContext, "username",
                "326992", "", "PMV6LJvT30FQo2sqdnJU62E3Sb", mockResponderListener)
                .doInBackground();

        Assert.assertSame(1, result);
    }


    @Test
    public void whenCallPasswordTask_givenInterruptionEnabled_thenReturnDisabledOutReach () throws Exception {
        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);
        when(mockApp.getApiClient()).thenReturn(apiClient);
        when(PushUtils.getDeviceName(any(Context.class))).thenReturn("");
        when(apiClient.postPassword(any(String.class), any(String.class), any(String.class), any(String.class), any(DeviceInfo.class)))
                .thenReturn("{\n" +
                        "    \"responder_name\": \"portal\",\n" +
                        "    \"rsa_cookie\": \"PMV6LZgthHbdnDQkk7pvbqTo58qR1gfBLUa2%2Fsb%2BFRCk21rXFIhFDK4t4XVJbR1wKX6cqumq%2FCowMU3hzP88do2BWiTw%3D%3D\",\n" +
                        "    \"responder_message\": \"account_information\",\n" +
                        "    \"page_title\": \"Portal\",\n" +
                        "    \"error\": \"\",\n" +
                        "    \"goback\": \"\",\n" +
                        "    \"content\": {\n" +
                        "        \"outreach\": \"true\",\n" +
                        "        \"interruptionPage\": \"true\",\n" +
                        "        \"ownership\": \"\",\n" +
                        "        \"customerInfo\": {\n" +
                        "            \"customerName\": \"Jorge\",\n" +
                        "            \"is_today_birthday\": false\n" +
                        "        }\n" +
                        "    }\n" +
                        "}");
        when(apiClient.parseBaseResponse(any(String.class))).thenReturn(new HashMap<String, Object>() {{
            put("responder_name", "responder_name");
            put("access_blocked", false);
            put("content", "");
        }});
        doNothing().when(mockApp).setLoggedInUser(captorCustomer.capture());

        asyncTasks.new PasswordTask(mockContext, "password", mockResponderListener).doInBackground();

        Customer customer = captorCustomer.getValue();
        assertNotNull(customer);
        assertFalse(customer.getOutreach());
        assertTrue(customer.getInterruptionPage());
    }

    @Test
    public void whenCallPasswordTask_givenOutReachEnabled_thenResultEnableOutReach () throws Exception {
        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);
        when(mockApp.getApiClient()).thenReturn(apiClient);
        when(PushUtils.getDeviceName(any(Context.class))).thenReturn("");
        when(apiClient.postPassword(any(String.class), any(String.class), any(String.class), any(String.class), any(DeviceInfo.class)))
                .thenReturn("{\n" +
                        "    \"responder_name\": \"portal\",\n" +
                        "    \"rsa_cookie\": \"PMV6LZgthHbdnDQkk7pvbqTo58qR1gfBLUa2%2Fsb%2BFRCk21rXFIhFDK4t4XVJbR1wKX6cqumq%2FCowMU3hzP88do2BWiTw%3D%3D\",\n" +
                        "    \"responder_message\": \"account_information\",\n" +
                        "    \"page_title\": \"Portal\",\n" +
                        "    \"error\": \"\",\n" +
                        "    \"goback\": \"\",\n" +
                        "    \"content\": {\n" +
                        "        \"outreach\": \"true\",\n" +
                        "        \"interruptionPage\": \"false\",\n" +
                        "        \"ownership\": \"\",\n" +
                        "        \"customerInfo\": {\n" +
                        "            \"customerName\": \"Jorge\",\n" +
                        "            \"is_today_birthday\": false\n" +
                        "        }\n" +
                        "    }\n" +
                        "}");
        when(apiClient.parseBaseResponse(any(String.class))).thenReturn(new HashMap<String, Object>() {{
            put("responder_name", "responder_name");
            put("access_blocked", false);
            put("content", "");
        }});
        doNothing().when(mockApp).setLoggedInUser(captorCustomer.capture());

        asyncTasks.new PasswordTask(mockContext, "password", mockResponderListener).doInBackground();

        Customer customer = captorCustomer.getValue();
        assertNotNull(customer);
        assertTrue(customer.getOutreach());
        assertFalse(customer.getInterruptionPage());
    }

}
