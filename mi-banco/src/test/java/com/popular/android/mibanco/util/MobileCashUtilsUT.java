package com.popular.android.mibanco.util;

import android.content.Context;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.ws.SyncRestClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MobileCashUtils.class, SyncRestClient.class, App.class, Context.class})
public class MobileCashUtilsUT {

    private MobileCashUtils mobileCashUtils;

    private App mockApp = mock(App.class);

    private Context mockContext = mock(Context.class);

    @Before
    public void setup() throws Exception {

        MockitoAnnotations.initMocks(this);

        mockStatic(App.class);

        when(App.getApplicationInstance()).thenReturn(mockApp);
        when(mockApp.getBaseContext()).thenReturn(mockContext);

        mobileCashUtils = spy(new MobileCashUtils());
    }

    @Test
    public void whenGetFormattedExpDate_Fail() {
        doThrow(new Exception("Error occurred"))
                .when(mobileCashUtils)
                .getFormattedExpDate(anyString(), mockContext);
    }
}
