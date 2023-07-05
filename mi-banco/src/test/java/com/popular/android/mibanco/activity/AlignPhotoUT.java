package com.popular.android.mibanco.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.util.PremiaUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AlignPhoto.class,  R.class, BuildConfig.class, App.class
        , MiBancoConstants.class})
public class AlignPhotoUT {
    @Mock
    private App app;//App Instance from Mi Banco

    @Mock
    private Context context;//Mi Banco App Context

    @Mock
    private SharedPreferences sharedPreferences;//Mi Banco settings

    @Mock
    private Resources resources;//App resource like progress bar and others

    @Mock
    private AlignPhoto alignPhoto;

    @Mock
    private LayoutInflater inflater;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(App.class);


        when(App.getApplicationInstance()).thenReturn(app);
        when(app.getBaseContext()).thenReturn(context);
        when(context.getSharedPreferences(MiBancoConstants.PREFS_KEY, Context.MODE_PRIVATE)).thenReturn(sharedPreferences);
        when(context.getResources()).thenReturn(resources);
        when(alignPhoto.getLayoutInflater()).thenReturn(inflater);

    }

    @Ignore
    @Test
    public void whenGetSharedsSecuredPreferences_error() throws Exception {
       when(alignPhoto, "setListeners").thenCallRealMethod();

    }
}
