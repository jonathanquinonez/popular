package com.popular.android.mibanco.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.popular.android.mibanco.R;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.HashMap;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ReportFragment;
import java.util.Locale;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class,  R.class, BuildConfig.class, App.class, MiBancoEnviromentConstants.class, FeatureFlags.class
        ,MiBancoConstants.class, ReportFragment.class, ContextCompat.class, BPAnalytics.class, Build.VERSION.class, Configuration.class})
public class UtilsUT {
    @Mock
    private App app;//App Instance from Mi Banco

    @Mock
    private Context context;//Mi Banco App Context

    @Mock
    private SharedPreferences sharedPreferences;//Mi Banco settings

    @Mock
    private Resources resources;//App resource like progress bar and others

    @Mock
    private Configuration configuration;

    @InjectMocks
    private Utils utils;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(App.class);

        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.mockStatic(ReportFragment.class);
        mockStatic(ContextCompat.class);
        mockStatic(BPAnalytics.class);


        //Need this to mock static MiBancoEnvironmentConstants
        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);
        PowerMockito.when(app.getBaseContext()).thenReturn(context);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.when(Utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);

    }

    @Test
    public void whenGetSharedsSecuredPreferences_apilvl_moreM(){
        when(utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);
    }

    @Test
    public void whenGetSharedsSecuredPreferences_apilvl_belowM() throws Exception {
        Utils mock = mock(Utils.class);
        when(mock.isDeviceSOSecure()).thenReturn(false);
        when(utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);
    }


    public void whenGetMyAmounts_Then_ReturnHashMap(){
        Utils utils = new Utils();
         HashMap<String, Integer> has  = utils.getMyAmounts(context);
         assertNotNull(has);
    }

    @Ignore
    @Test
    public void whenGetSharedsSecuredPreferences_error() {
       doThrow(new Exception("error")).when(utils).getSecuredSharedPreferences(context);
    }

    @Test
    public void whenCallGetLocaleStringResource_GivenLocaleIntAndContextNotNull_ThenReturnNull() throws Exception
    {
        Locale locale = new Locale("es");
        when(resources.getConfiguration()).thenReturn(configuration);
        String reply =  Utils.getLocaleStringResource(locale, 0,context);
        assertNull(reply);

    }

    @Test
    public void whenCallGetLocaleStringResource_GivenLocaleIntAndContextNotNull_ThenReturnStringNotNull() throws Exception
    {
        Locale locale = new Locale("es");
        when(Utils.getLocaleStringResource(locale,0, context)).thenCallRealMethod();
        Build.VERSION buildVersion = new Build.VERSION();
        Whitebox.setInternalState(buildVersion, "SDK_INT", 20);
        PowerMockito.whenNew(Configuration.class).withArguments(context.getResources().getConfiguration()).thenReturn(configuration);

        when(context.createConfigurationContext(configuration)).thenReturn(context);
        when(context.getText(any(int.class))).thenReturn("prueba");

        String reply =  Utils.getLocaleStringResource(locale, 0,context);
        assertEquals("prueba", reply);

    }


}
