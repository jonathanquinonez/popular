// package com.popular.android.mibanco.task;

// import android.app.Activity;
// import android.content.Context;
// import android.content.SharedPreferences;
// import android.content.res.Resources;

// import com.popular.android.mibanco.App;
// import com.popular.android.mibanco.MiBancoConstants;
// import com.popular.android.mibanco.listener.ResponderListener;
// import com.popular.android.mibanco.task.AsyncTasks.LoginTask;
// import com.popular.android.mibanco.util.BPAnalytics;
// import com.popular.android.mibanco.util.DeviceFingerprint;
// import com.popular.android.mibanco.util.Utils;
//import com.popular.android.mibanco.ws.ApiClient;
// import com.popular.android.mibanco.ws.SyncRestClient;

// import org.junit.Before;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.mockito.Mockito;
// import org.mockito.MockitoAnnotations;
// import org.powermock.api.mockito.PowerMockito;
// import org.powermock.core.classloader.annotations.PrepareForTest;
// import org.powermock.modules.junit4.PowerMockRunner;

// import java.util.HashMap;

// import static org.powermock.api.mockito.PowerMockito.mock;
// import static org.powermock.api.mockito.PowerMockito.mockStatic;
// import static org.powermock.api.mockito.PowerMockito.spy;
// import static org.powermock.api.mockito.PowerMockito.when;
// import static org.powermock.api.mockito.PowerMockito.whenNew;

// @RunWith(PowerMockRunner.class)
// @PrepareForTest({App.class, Context.class, ResponderListener.class, LoginTask.class,ApiClient.class, BPAnalytics.class})
// public class LoginTasksUT {

//     private static final String JSON_STRING = "{\"responder_name\":\"login\",\"responder_message\":\"login_information\",\"page_title\":\"Login\",\"rsa_cookie\":\"\",\"error\":\"\",\"form\":{\"name\":\"login\",\"action\":\"login\",\"method\":\"post\",\"fields\":{\"pm_fp\":{\"type\":\"hidden\",\"value\":\"\"},\"username\":{\"type\":\"text\",\"value\":\"\"}},\"submit\":\"login.button.login\"},\"labels\":{\"login.button.login\":\"Log in\",\"goback\":\"Go Back\",\"noSessionError\":\"No Session Error\",\"login.username.label\":\"Username:\",\"login.call.alert\":\"Alert:\",\"password.answer.blocked\":\"Access blocked\",\"password.answer.blocked.callInfo\":\"Please call us at 787-724-3655 or 1-888-724-3655 to unblock your account.\"},\"content\":{\"call_info_a\": \"787-724-3655\",\"call_info_b\":\"1-888-724-3655\",\"backgroundImageUrl\":\"/cibp-web/img/cm/imgLoginPR.jpg\"},\"flags\":{\"pendingEnroll\":true,\"noSessionError\":false}}";
//     private static final String JSON_STRING2 = "{\"responder_name\":\"login\",\"responder_message\":\"login_information\",\"page_title\":\"Login\",\"rsa_cookie\":\"\",\"error\":\"\",\"form\":{\"name\":\"login\",\"action\":\"login\",\"method\":\"post\",\"fields\":{\"pm_fp\":{\"type\":\"hidden\",\"value\":\"\"},\"username\":{\"type\":\"text\",\"value\":\"\"}},\"submit\":\"login.button.login\"},\"labels\":{\"login.button.login\":\"Log in\",\"goback\":\"Go Back\",\"noSessionError\":\"No Session Error\",\"login.username.label\":\"Username:\",\"login.call.alert\":\"Alert:\",\"password.answer.blocked\":\"Access blocked\",\"password.answer.blocked.callInfo\":\"Please call us at 787-724-3655 or 1-888-724-3655 to unblock your account.\"},\"content\":{\"call_info_a\": \"787-724-3655\",\"call_info_b\":\"1-888-724-3655\",\"backgroundImageUrl\":\"/cibp-web/img/cm/imgLoginPR.jpg\"},\"flags\":{\"pendingEnroll\":false,\"noSessionError\":false}}";

//     private App mockApp = mock(App.class);

//     private Activity mockContext = mock(Activity.class);

//     private ResponderListener mockResponderListener = mock(ResponderListener.class);

//     private LoginTask loginTask;

//     private Resources mockResources = mock(Resources.class);

//     private ApiClient mockApiClient;

//     private SyncRestClient mockSyncRestClient = mock(SyncRestClient.class);
//     private DeviceFingerprint mockDeviceFingerprint = mock(DeviceFingerprint.class);

//     private SharedPreferences mockSharedPreferences = mock(SharedPreferences.class);

//     private SharedPreferences.Editor mockEditor = mock(SharedPreferences.Editor.class);

    // @Before
    // public void setup() {

    //     MockitoAnnotations.initMocks(this);

    //     mockStatic(App.class);
    //     mockStatic(LoginTask.class);
    //     mockStatic(Thread.class);
    //     mockStatic(BPAnalytics.class);

    //     try {
    //         whenNew(SyncRestClient.class).withAnyArguments().thenReturn(mockSyncRestClient);
    //         whenNew(DeviceFingerprint.class).withAnyArguments().thenReturn(mockDeviceFingerprint);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     when(App.getApplicationInstance()).thenReturn(mockApp);
    //     when(mockApp.getBaseContext()).thenReturn(mockContext);
    //     when(mockContext.getApplication()).thenReturn(mockApp);
    //     when(Utils.getSecuredSharedPreferences(mockContext)).thenReturn(mockSharedPreferences);
    //     when(mockSharedPreferences.edit()).thenReturn(mockEditor);
    //     when(mockApp.getLanguage()).thenReturn(MiBancoConstants.SPANISH_LANGUAGE_CODE);
    //     when(mockContext.getResources()).thenReturn(mockResources);
    //     when(mockResources.getString(Mockito.anyInt())).thenReturn("a string","anotherString");

    //     mockApiClient = spy(new ApiClient("http://localhost:8080/","android","es",mockContext));
    //     when(mockDeviceFingerprint.getDevicePrint()).thenReturn("android");

    //     when(mockApp.getApiClient()).thenReturn(mockApiClient);
    //     String username = "trevor";
    //     String deviceIdentifier = "android";
    //     String deviceInfoRsa = "deviceInfoRsa";
    //     String rsaCookie = "rsaCookie";

    //     loginTask = PowerMockito.spy(new AsyncTasks(mockApp).new LoginTask(mockContext,username,deviceIdentifier, deviceInfoRsa, rsaCookie,mockResponderListener ));

    // }

    // private void prePrepareTest1() {
    //     try {
    //         when(mockSyncRestClient.doPost(Mockito.anyString(),Mockito.any(HashMap.class))).thenReturn(JSON_STRING);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    // private void prePrepareTest2() {
    //     try {
    //         when(mockSyncRestClient.doPost(Mockito.anyString(),Mockito.any(HashMap.class))).thenReturn(JSON_STRING2);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    // @Test
    // public void whenDoInBackground_withUserPendingEnrollToShowAlert_thenTrue() {
    //     prePrepareTest1();
    //     Integer result = loginTask.doInBackground(mockContext, mockResponderListener);
    //     assert result.equals(BaseAsyncTask.RESULT_FAILURE);
    // }

    // @Test
    // public void whenDoInBackground_withInvalidUsername_thenTrue() {
    //     prePrepareTest2();
    //     Integer result = loginTask.doInBackground(mockContext, mockResponderListener);
    //     assert result.equals(BaseAsyncTask.RESULT_FAILURE);
    // }
// }