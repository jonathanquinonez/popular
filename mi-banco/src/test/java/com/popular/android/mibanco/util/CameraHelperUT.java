package com.popular.android.mibanco.util;

import android.content.Context;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.ws.ApiClient;
import com.popular.android.mibanco.ws.SyncRestClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CameraHelper.class, SyncRestClient.class, App.class, Context.class})
public class CameraHelperUT {

    private CameraHelper cameraHelper;

    private App mockApp = mock(App.class);

    private Context mockContext = mock(Context.class);

    private SyncRestClient mockSyncRestClient = mock(SyncRestClient.class);

    @Before
    public void setup() throws Exception {

        MockitoAnnotations.initMocks(this);

        mockStatic(App.class);

        when(App.getApplicationInstance()).thenReturn(mockApp);
        when(mockApp.getBaseContext()).thenReturn(mockContext);

        whenNew(SyncRestClient.class)
                .withAnyArguments()
                .thenReturn(mockSyncRestClient);

        cameraHelper = spy(new CameraHelper());

    }

    @Test
    public void whenGetOutputMediaFileUri_Fail() {
        doThrow(new Exception("Error occurred"))
                .when(cameraHelper)
                .getOutputMediaFileUri();
    }

    @Test
    public void whenGetOutputMediaFilePath_Fail() {
        doThrow(new Exception("Error occurred"))
                .when(cameraHelper)
                .getOutputMediaFilePath();
    }
}
