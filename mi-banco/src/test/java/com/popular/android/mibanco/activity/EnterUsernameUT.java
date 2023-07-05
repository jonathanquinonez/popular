package com.popular.android.mibanco.activity;

import android.Manifest;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.content.res.Resources;
import android.os.Environment;
import android.text.Editable;
import android.view.animation.AnimationUtils;

import android.webkit.CookieSyncManager;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import androidx.fragment.app.FragmentController;
import androidx.lifecycle.ReportFragment;


import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.popular.android.mibanco.R;

import com.popular.android.mibanco.listener.StartListener;
import com.popular.android.mibanco.model.LoginGet;
import com.popular.android.mibanco.model.OobChallenge;
import com.popular.android.mibanco.model.User;
import com.popular.android.mibanco.task.AsyncTasks;
import com.popular.android.mibanco.task.BaseAsyncTask;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.PermissionsManagerUtils;
import com.popular.android.mibanco.util.Utils;

import org.junit.Before;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.mockito.repackaged.cglib.proxy.Callback;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

import java.io.File;
import java.util.Arrays;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Accounts.class, ReportFragment.class,
        MiBancoConstants.class, R.class, CookieSyncManager.class, MiBancoEnviromentConstants.class,
        BuildConfig.class, App.class, Utils.class, FeatureFlags.class, ContextCompat.class,AnimationUtils.class, BPAnalytics.class,
        EnterUsername.class, PermissionsManagerUtils.class, Toast.class, Environment.class, File.class,StartListener.class,
        OobChallenge.class,Context.class})
public class EnterUsernameUT {

    @Mock
    private Context context;

    @Mock
    private FragmentController fragmentController;

    @Mock
    private ListView listView;

    @Mock
    private EditText editText;

    @Mock
    private Editable editable;

    private enum LoginView {
        EnterUsername,
        OtherUsername,
        SelectUsername
    }

    private LoginView loginView;

    @Mock
    private SwitchCompat switchCompat;

    @Mock
    private  StartListener startListener;

    @Mock
    private User user;

    @Mock
    private App app;

    @Mock
    private BaseAsyncTask baseAsyncTaskMock = PowerMockito.mock(BaseAsyncTask.class);

    @Mock
    private AsyncTasks asyncTasks;

    @Mock
    private LoginGet loginGet;

    @Mock
    private File mCustomImage;

    @Mock
    private Object object;

    @Mock
    private Intent intent;

    @Mock
    private Resources resources;

    @InjectMocks
    private EnterUsername enterUsername;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(ReportFragment.class);
        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(CookieSyncManager.class);
        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.mockStatic(Toast.class);
        PowerMockito.mockStatic(Context.class);
        PowerMockito.mockStatic(ContextCompat.class);
        PowerMockito.mockStatic(BPAnalytics.class);
        PowerMockito.mockStatic(Environment.class);
        PowerMockito.mockStatic(File.class);
        PowerMockito.mockStatic(StartListener.class);
        PowerMockito.mockStatic(OobChallenge.class);
        enterUsername = PowerMockito.spy(new EnterUsername());
        app = PowerMockito.spy(new App());

        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);
        PowerMockito.when(app.getBaseContext()).thenReturn(context);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.when(enterUsername.getResources()).thenReturn(resources);

        Whitebox.setInternalState(enterUsername, "savedUsersList", listView);
        Whitebox.setInternalState(enterUsername, "isOtherUsername", true);
        Whitebox.setInternalState(enterUsername, "textOtherUser", editText);
        Whitebox.setInternalState(enterUsername, "textUser", editText);
        Whitebox.setInternalState(enterUsername, "switchRememberOtherUsername", switchCompat);
        Whitebox.setInternalState(enterUsername, "switchRememberUsername", switchCompat);
        Whitebox.setInternalState(enterUsername, "application", app);
        Whitebox.setInternalState(enterUsername, "mCustomImage", mCustomImage);
        Whitebox.setInternalState(enterUsername, "intent", intent);



    }

    @Test
    public void whenCallingOnRequestPermissionsResult_And_PermissionGranted_Is_True() throws Exception{

        final int[] results = {PackageManager.PERMISSION_GRANTED,PackageManager.PERMISSION_GRANTED,PackageManager.PERMISSION_GRANTED};
        final String[] permisions = {"canhack","canstolemyinfo", Manifest.permission.ACCESS_FINE_LOCATION};

        when(Toast.makeText(any(Context.class), any(CharSequence.class), anyInt())).thenReturn(new Toast(context));

        Whitebox.setInternalState(enterUsername, "mFragments", fragmentController);
        enterUsername.onRequestPermissionsResult(MiBancoConstants.REQUEST_CODE_ASK_PERMISSIONS, permisions, results);

        PowerMockito.verifyPrivate("dispatchTakePictureIntent()", times(1));


    }

    @Test
    public void whenCallingOnRequestPermissionsResult_And_PermissionGranted_Is_False() throws Exception{

        final int[] results = {PackageManager.PERMISSION_DENIED,PackageManager.PERMISSION_DENIED,PackageManager.PERMISSION_DENIED};
        final String[] permisions = {"canhack","canstolemyinfo", Manifest.permission.ACCESS_FINE_LOCATION};

        when(Toast.makeText(any(Context.class), any(CharSequence.class), anyInt())).thenReturn(new Toast(context));
        Whitebox.setInternalState(enterUsername, "mFragments", fragmentController);
        enterUsername.onRequestPermissionsResult(MiBancoConstants.REQUEST_CODE_ASK_PERMISSIONS, permisions, results);

        PowerMockito.verifyPrivate("dispatchTakePictureIntent()", times(0));

    }



    @Test
    public void whenPerformLogin() throws Exception{

        when(editText.getText()).thenReturn(editable);

        when(switchCompat.isChecked()).thenReturn(true);

        Whitebox.setInternalState(enterUsername, "savedUsersList", listView);
        Whitebox.setInternalState(enterUsername, "isOtherUsername", true);
        Whitebox.setInternalState(enterUsername, "textOtherUser", editText);
        Whitebox.setInternalState(enterUsername, "textUser", editText);
        Whitebox.setInternalState(enterUsername, "switchRememberOtherUsername", switchCompat);
        Whitebox.setInternalState(enterUsername, "switchRememberUsername", switchCompat);
        Whitebox.setInternalState(enterUsername, "application", app);

        doReturn(user).when(enterUsername, "getUserFromUsername", any(User.class));

        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);

        PowerMockito.when(app.getAsyncTasksManager()).thenReturn(asyncTasks);

        Whitebox.invokeMethod(enterUsername, "performLogin");

        verify(app, times(1)).getAsyncTasksManager();


    }


    @Test
    public void whenPerformLoginNullUser() throws Exception{

        when(editText.getText()).thenReturn(editable);

        when(switchCompat.isChecked()).thenReturn(true);

        Whitebox.setInternalState(enterUsername, "savedUsersList", listView);
        Whitebox.setInternalState(enterUsername, "isOtherUsername", true);
        Whitebox.setInternalState(enterUsername, "textOtherUser", editText);
        Whitebox.setInternalState(enterUsername, "textUser", editText);
        Whitebox.setInternalState(enterUsername, "switchRememberOtherUsername", switchCompat);
        Whitebox.setInternalState(enterUsername, "switchRememberUsername", switchCompat);
        Whitebox.setInternalState(enterUsername, "application", app);

        doReturn(null).when(enterUsername, "getUserFromUsername", any(User.class));

        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);

        PowerMockito.when(app.getAsyncTasksManager()).thenReturn(asyncTasks);

        Whitebox.invokeMethod(enterUsername, "performLogin");

        verify(app, times(1)).getAsyncTasksManager();

    }


    @Test
    public void whenPerformLoginListenerSavedData() throws Exception{

        Whitebox.setInternalState(enterUsername, "mCustomImage", mCustomImage);
        Whitebox.setInternalState(enterUsername, "application", app);

        doReturn(user).when(enterUsername, "getUserFromUsername", any(User.class));

        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);

        PowerMockito.when(app.getAsyncTasksManager()).thenReturn(asyncTasks);

        enterUsername.savedData(loginGet);

    }

    @Test
    public void whenLoginResponserQuestion() throws Exception{

        when(editText.getText()).thenReturn(editable);

        when(switchCompat.isChecked()).thenReturn(true);

        doReturn(context).when(enterUsername, "getApplicationContext");

        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);

        PowerMockito.when(app.getAsyncTasksManager()).thenReturn(asyncTasks);

        enterUsername.responder("question",object);

    }

    @Test
    public void whenLoginResponserLoginoob() throws Exception{

        when(editText.getText()).thenReturn(editable);

        when(switchCompat.isChecked()).thenReturn(true);

        doReturn(context).when(enterUsername, "getApplicationContext");

        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);

        PowerMockito.when(app.getAsyncTasksManager()).thenReturn(asyncTasks);

        OobChallenge oobChallenge =  new OobChallenge();

        enterUsername.responder("loginoob", (Object) oobChallenge);

    }

    @Test
    public void whenLoginResponserPassword() throws Exception{

        when(editText.getText()).thenReturn(editable);

        when(switchCompat.isChecked()).thenReturn(false);

        doReturn(context).when(enterUsername, "getApplicationContext");

        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);

        PowerMockito.when(app.getAsyncTasksManager()).thenReturn(asyncTasks);

        enterUsername.responder("password", object);

    }


    @Test
    public void whenLoginResponserLoginSsdsForced() throws Exception{

        when(editText.getText()).thenReturn(editable);

        when(switchCompat.isChecked()).thenReturn(false);

        String[] urlBlacklist = new String[3];

        doReturn(context).when(enterUsername, "getApplicationContext");

        PowerMockito.when(app.getAsyncTasksManager()).thenReturn(asyncTasks);

        PowerMockito.when(resources.getStringArray(R.array.web_view_url_blacklist)).thenReturn(urlBlacklist);

        enterUsername.responder("loginSsdsForced", object);

    }

    @Test
    public void whenLogin() throws Exception{

        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);

        PowerMockito.when(app.getAsyncTasksManager()).thenReturn(asyncTasks);

        Whitebox.invokeMethod(enterUsername, "login");

        verify(app, times(1)).getAsyncTasksManager();

    }


}