package com.popular.android.mibanco.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.popular.android.mibanco.util.Utils.getSecuredSharedPreferences;
import static org.junit.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ProgressDialog.class, PremiaUtils.class,  R.class, BuildConfig.class, App.class
    , MiBancoConstants.class})
public class PremiaUtilsUT {

    @Mock
    private App app;//App Instance from Mi Banco

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
        PowerMockito.mockStatic(PremiaUtils.class);

        when(App.getApplicationInstance()).thenReturn(app);
        when(app.getBaseContext()).thenReturn(context);
        when(getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);
        when(context.getResources()).thenReturn(resources);

    }

    @Test
    public void whenOnPreparePremiaDialog_GivenProgressDialog_ThenReturnObject(){

        when(PremiaUtils.openPremiaLoadingDialog(context)).thenCallRealMethod();

        ProgressDialog dialog  = PremiaUtils.openPremiaLoadingDialog(context);
        PowerMockito.verifyStatic();
        assertNotNull(dialog);
    }
}
