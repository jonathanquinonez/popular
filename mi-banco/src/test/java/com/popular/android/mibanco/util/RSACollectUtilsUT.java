package com.popular.android.mibanco.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.GlobalStatus;
import com.popular.android.mibanco.task.RSAChallengeTasks;
import com.rsa.mobilesdk.sdk.MobileAPI;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ProgressDialog.class, RSACollectUtils.class,  R.class, BuildConfig.class, App.class
        , MiBancoConstants.class, MobileAPI.class})
public class RSACollectUtilsUT {

    @Mock
    private App app;//App Instance from Mi Banco

    @Mock
    private GlobalStatus globalStatus;

    @Mock
    private MobileAPI mobileAPI;

    @Mock
    private Context context;//Mi Banco App Context

    @Mock
    private SharedPreferences sharedPreferences;//Mi Banco settings

    @Mock
    private Resources resources;//App resource like progress bar and others

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(MobileAPI.class);

        when(app.getBaseContext()).thenReturn(context);
        when(context.getSharedPreferences(MiBancoConstants.PREFS_KEY, Context.MODE_PRIVATE)).thenReturn(sharedPreferences);
        when(context.getResources()).thenReturn(resources);

    }

    @Test
    public void whenOnCollectDeviceInfo_And_isDeviceInfoSdkInfo_Is_Enabled(){

        when(globalStatus.isDeviceInfoSdkInfoEnabled()).thenReturn(true);
        when(app.getGlobalStatus()).thenReturn(globalStatus);
        when(MobileAPI.getInstance((Context) Mockito.any())).thenReturn(mobileAPI);
        when(App.getApplicationInstance()).thenReturn(app);
        when(mobileAPI.collectInfo()).thenReturn("[{id:myid,device:fewt24,deviceOS:Android,macAddress:23-434-112-234}]");

        String deviceInfo = RSACollectUtils.collectDeviceInfo(context);
        assertTrue(!deviceInfo.equals(""));

    }

    @Test
    public void whenOnCollectDeviceInfo_And_isDeviceInfoSdkInfo_Is_Disabled(){

        when(globalStatus.isDeviceInfoSdkInfoEnabled()).thenReturn(false);
        when(app.getGlobalStatus()).thenReturn(globalStatus);
        when(MobileAPI.getInstance((Context) Mockito.any())).thenReturn(mobileAPI);
        when(App.getApplicationInstance()).thenReturn(app);
        when(mobileAPI.collectInfo()).thenReturn("[{id:myid,device:fewt24,deviceOS:Android,macAddress:23-434-112-234}]");

        String deviceInfo = RSACollectUtils.collectDeviceInfo(context);
        assertTrue(deviceInfo.equals(""));

    }
}
