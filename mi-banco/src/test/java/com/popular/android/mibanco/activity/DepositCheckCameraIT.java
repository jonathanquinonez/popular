package com.popular.android.mibanco.activity;

import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;

import android.content.Context;
import android.content.res.Resources;
import android.view.SurfaceHolder;


import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.MiBancoConstants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({BuildConfig.class, App.class, MiBancoConstants.class,Context.class})
public class DepositCheckCameraIT {
    @Mock
    private App app;//App Instance from Mi Banco

    @Mock
    private Context context;//Mi Banco App Context

    @Mock
    private Resources resources;//App resource like progress bar and others

    @Mock
    private SurfaceHolder holder;

    @InjectMocks
    private DepositCheckCamera depositCheckCamera;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(Context.class);

        depositCheckCamera = PowerMockito.spy(new DepositCheckCamera());
        when(App.getApplicationInstance()).thenReturn(app);
        when(app.getBaseContext()).thenReturn(context);
        when(context.getResources()).thenReturn(resources);

    }

    @Test
    public void whenInitPreview() throws Exception {

        depositCheckCamera.surfaceChanged(holder,1,1,1);

        verifyPrivate(depositCheckCamera, times(1)).invoke("initPreview", 1,1);

    }


    @Test
    public void whenStartPreview() throws Exception {

        depositCheckCamera.surfaceChanged(holder,1,1,1);

        verifyPrivate(depositCheckCamera, times(1)).invoke("startPreview");

    }
}
