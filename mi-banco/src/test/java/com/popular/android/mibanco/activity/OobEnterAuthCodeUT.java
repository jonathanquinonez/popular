package com.popular.android.mibanco.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.webkit.CookieSyncManager;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ReportFragment;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.popular.android.mibanco.MiBancoPreferences;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.OobChallenge;
import com.popular.android.mibanco.task.AsyncTasks;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.ws.ApiClient;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.Map;


import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OobEnterAuthCode.class, ReportFragment.class,
        MiBancoConstants.class, R.class, CookieSyncManager.class, MiBancoEnviromentConstants.class,
        BuildConfig.class, App.class, Utils.class, ContextCompat.class})
public class OobEnterAuthCodeUT {
    @Mock
    private AppCompatDelegate appDelegate;

    @Mock
    private App app;

    @Mock
    private Context context;

    @Mock
    private SharedPreferences sharedPreferences;

    @Mock
    private Resources resources;

    @Mock
    private AsyncTasks asyncTasks;

    @Mock
    private CustomerAccount account;

    @Mock
    private Intent intent;

    @Mock
    ApiClient apiClient;

    @Mock
    private OobEnterAuthCode oobEnterAuthCode;

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
        PowerMockito.mockStatic(ContextCompat.class);
        //Need this to mock static MiBancoEnvironmentConstants
        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);
        PowerMockito.when(app.getAsyncTasksManager()).thenReturn(asyncTasks);
        PowerMockito.when(app.getBaseContext()).thenReturn(context);
        PowerMockito.when(Utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.mockStatic(MiBancoEnviromentConstants.class);

        PowerMockito.doReturn(appDelegate).when(oobEnterAuthCode).getDelegate();

        when(oobEnterAuthCode.getIntent()).thenReturn(intent);

    }

    @Test
    public void whenUserCanOpenNewAccountShouldUpdateOpacOptions() throws Exception{
        OobChallenge data = new OobChallenge();
        data.setCanOpenAccount(Boolean.TRUE.toString());
        data.setIsForeignCustomer(Boolean.FALSE.toString());

        Whitebox.invokeMethod(oobEnterAuthCode, "setOpacOptions", data);
        HashMap<String, String> opac = MiBancoPreferences.getOpac();

        Assert.assertTrue(opac.get(MiBancoConstants.CAN_OPEN_ACCOUNT).equals(Boolean.TRUE.toString()));
        Assert.assertTrue(opac.get(MiBancoConstants.IS_FOREING_CUSTOMER).equals(Boolean.FALSE.toString()));
    }

    @Test
    public void whenUserCanNotOpenNewAccountShouldNoChangeOpacOptions() throws Exception{
        OobChallenge data = new OobChallenge();
        data.setCanOpenAccount(Boolean.FALSE.toString());
        data.setIsForeignCustomer(Boolean.FALSE.toString());

        final Map<String, String> previousValue = MiBancoPreferences.getOpac();
        Whitebox.invokeMethod(oobEnterAuthCode, "setOpacOptions", data);

        final Map<String, String> opac = MiBancoPreferences.getOpac();
        Assert.assertTrue(previousValue.equals(opac));
    }

    @Test
    public void whenOpacDataIsNullShouldNoChangeOpacOptions() throws Exception{
        OobChallenge data = new OobChallenge();
        data.setCanOpenAccount(null);
        data.setIsForeignCustomer(null);

        final Map<String, String> previousValue = MiBancoPreferences.getOpac();
        Whitebox.invokeMethod(oobEnterAuthCode, "setOpacOptions", data);

        final Map<String, String> opac = MiBancoPreferences.getOpac();
        Assert.assertTrue(previousValue.equals(opac));
    }
}
