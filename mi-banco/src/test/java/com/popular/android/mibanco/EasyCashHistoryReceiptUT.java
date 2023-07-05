package com.popular.android.mibanco;

import android.content.Context;

import com.popular.android.mibanco.activity.EasyCashHistoryReceipt;
import com.popular.android.mibanco.ws.SyncRestClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

import java.lang.reflect.Method;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EasyCashHistoryReceipt.class, SyncRestClient.class, App.class, Context.class})
public class EasyCashHistoryReceiptUT {

    private EasyCashHistoryReceipt easyCashHistoryReceipt;

    private App mockApp = mock(App.class);

    private Context mockContext = mock(Context.class);

    @Before
    public void setup() throws Exception {

        MockitoAnnotations.initMocks(this);

        mockStatic(App.class);

        when(App.getApplicationInstance()).thenReturn(mockApp);
        when(mockApp.getBaseContext()).thenReturn(mockContext);

        easyCashHistoryReceipt = spy(new EasyCashHistoryReceipt());
    }

    @Test
    public void whenSetContactName_Fail() throws Exception {
        when(easyCashHistoryReceipt, method(EasyCashHistoryReceipt.class, "setContactName", boolean.class))
                .withArguments(anyBoolean())
                .thenThrow();
    }
}
