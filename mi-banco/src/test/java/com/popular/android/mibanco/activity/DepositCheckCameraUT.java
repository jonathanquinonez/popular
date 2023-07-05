package com.popular.android.mibanco.activity;

import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.when;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Surface;
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
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BuildConfig.class, App.class, MiBancoConstants.class,Context.class})
public class DepositCheckCameraUT {
    @Mock
    private App app;//App Instance from Mi Banco

    @Mock
    private Context context;//Mi Banco App Context

    @Mock
    private Resources resources;//App resource like progress bar and others

    @Mock
    private Camera camera;

    @Mock
    private SurfaceHolder holder;

    @Mock
    private Surface surface;

    @Mock
    private DisplayMetrics displayMetrics;

    @Mock
    private Camera.Parameters parameters;

    @InjectMocks
    private DepositCheckCamera depositCheckCamera;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(Context.class);

        when(App.getApplicationInstance()).thenReturn(app);
        when(app.getBaseContext()).thenReturn(context);
        when(context.getResources()).thenReturn(resources);

    }

    @Test
    public void whenInitPreview() throws Exception {

        Size size = new Size(1600,1200);
        List<Size> listSize = new ArrayList<Size>();
        listSize.add(size);

        Camera.Size camerasize = camera.new Size(1600, 1200);
        List<Camera.Size> supportedSizes = new ArrayList<Camera.Size>();
        supportedSizes.add(camerasize);

        Whitebox.setInternalState(depositCheckCamera, "camera", camera);
        Whitebox.setInternalState(depositCheckCamera, "holder", holder);
        PowerMockito.when(holder.getSurface()).thenReturn(surface);
        when(app.getBaseContext().getResources().getDisplayMetrics()).thenReturn(displayMetrics);
        when(camera.getParameters()).thenReturn(parameters);
        when(parameters.getSupportedPreviewSizes()).thenReturn(supportedSizes);
        when(parameters.getSupportedPictureSizes()).thenReturn(supportedSizes);

        Whitebox.invokeMethod(depositCheckCamera, "initPreview",0, 0);

    }

    @Test
    public void whenInitPreviewWithException() throws Throwable {

        Size size = new Size(1600,1200);
        List<Size> listSize = new ArrayList<Size>();
        listSize.add(size);

        Camera.Size camerasize = camera.new Size(1600, 1200);
        List<Camera.Size> supportedSizes = new ArrayList<Camera.Size>();
        supportedSizes.add(camerasize);

        Whitebox.setInternalState(depositCheckCamera, "camera", camera);
        Whitebox.setInternalState(depositCheckCamera, "holder", holder);
        PowerMockito.when(holder.getSurface()).thenReturn(surface);

        doThrow(new NullPointerException()).when(camera).setPreviewDisplay(holder);

        Whitebox.invokeMethod(depositCheckCamera, "initPreview",0, 0);

    }
}
