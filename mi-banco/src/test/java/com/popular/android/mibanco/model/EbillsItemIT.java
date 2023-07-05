package com.popular.android.mibanco.model;


import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.task.AsyncTasks;
import com.popular.android.mibanco.util.RSACollectUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.ws.ApiClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;


@RunWith(PowerMockRunner.class)
@PrepareForTest({App.class, AsyncTasks.class, Context.class, ResponderListener.class, RSACollectUtils.class, Utils.class, Activity.class })
public class EbillsItemIT {

    private App mockApp = mock(App.class);

    @InjectMocks
    private AsyncTasks asyncTasks;

    private  Resources resources = mock(Resources.class);

    private Activity mockContext = mock(Activity.class);

    private ResponderListener mockResponderListener = mock(ResponderListener.class);

    private Context mockContextc = mock(Context.class);

    private ApiClient apiClient = mock(ApiClient.class);

    @Mock
    private EBills ebills ;


    private EBillsItem ebill ;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);
        mockStatic(App.class);
        mockStatic(Thread.class);
        mockStatic(RSACollectUtils.class);
        mockStatic(Utils.class);

        when(App.getApplicationInstance()).thenReturn(mockApp);
        PowerMockito.when(mockApp.getDateFormat()).thenReturn("dd/mm/yyyy");
        when(RSACollectUtils.collectDeviceInfo(Mockito.any(Context.class))).thenReturn("[{id:myid,device:fewt24,deviceOS:Android,macAddress:23-434-112-234}]");
        when(Utils.getPrefsStringNotNull(MiBancoConstants.RSA_COOKIE, mockApp)).thenReturn("myCookie");
        when(mockApp.getBaseContext()).thenReturn(mockContext);

        asyncTasks = spy(new AsyncTasks(mockApp));
    }


    @Test
    public void whenfilterEbills_ThenEbills_ReturnSucces() {

        when(mockContext.getApplication()).thenReturn(mockApp);
        when(mockContext.getResources()).thenReturn(resources);

        try {
            Whitebox.invokeMethod(asyncTasks, "filterEbills", ebills);
            verify(ebill,times(1)).getDueDate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
