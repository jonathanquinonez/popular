package com.popular.android.mibanco.activity;


import com.popular.android.mibanco.R;
import android.content.Context;
import android.content.res.Resources;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.MiBancoConstants;
import static org.powermock.api.mockito.PowerMockito.spy;

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

@RunWith(PowerMockRunner.class)
@PrepareForTest({BuildConfig.class, App.class, MiBancoConstants.class, Context.class, R.class})
public class DepositCheckReceiptUT {

    @Mock
    private App app;//App Instance from Mi Banco

    @Mock
    private Context context;//Mi Banco App Context

    @Mock
    private Resources resources;//App resource

    @InjectMocks
    private DepositCheckReceipt depositCheckReceipt;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(App.class);

        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);
        PowerMockito.when(app.getBaseContext()).thenReturn(context);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        depositCheckReceipt = spy(new DepositCheckReceipt());

    }


    @Test
    public void whenScanFile_GivenPath_ThenExecuteMediaScannerClient() throws Exception {
        String path = "URL/url";
        Whitebox.invokeMethod(depositCheckReceipt, "scanFile", path);
    }


}