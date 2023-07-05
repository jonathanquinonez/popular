package com.popular.android.mibanco.task;

import android.content.Context;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.listener.ResponderListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({App.class, Context.class, ResponderListener.class, PremiaTasks.class})
public class PremiaTasksUT {

    private App mockApp = mock(App.class);

    private Context mockContext = mock(Context.class);

    private ResponderListener mockResponderListener = mock(ResponderListener.class);

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);

        mockStatic(App.class);
        mockStatic(PremiaTasks.class);
        mockStatic(Thread.class);

        when(App.getApplicationInstance()).thenReturn(mockApp);
        when(mockApp.getBaseContext()).thenReturn(mockContext);

    }

    @Test
    public void whenCallingPremiaCatalogRedirect_ThenSuccessStaticMethodCall() {
        PremiaTasks.premiaCatalogRedirect(mockContext, "123456789", mockResponderListener);
        verifyStatic(atLeast(1));
    }

    @Test
    public void whenCallingPremiaCatalogRedirectTwice_ThenSuccessStaticMethodCallTwoTimes() {
        PremiaTasks.premiaCatalogRedirect(mockContext, "123456789", mockResponderListener);
        PremiaTasks.premiaCatalogRedirect(mockContext, "123456790", mockResponderListener);
        verifyStatic(atLeast(2));
    }

    // @Test
    // public void when_CallingPremiaCatalogRedirect_thenThreadIsPutToSleepCorrectly() throws InterruptedException {
    //     final long beforeCall = System.currentTimeMillis();
    //     PremiaTasks.premiaCatalogRedirect(mockContext, "123456789", mockResponderListener);
    //     Thread.sleep(4000);
    //     verifyStatic(atLeast(2));
    //     final long afterCall = System.currentTimeMillis();
    //     assertTrue(afterCall > beforeCall);
    // }

    @Test
    public void whenCallingPremiaBalanceInfo_ThenSuccessStaticMethodCall() {
        PremiaTasks.premiaAccountBalance(mockContext, mockResponderListener);
        verifyStatic(atLeast(1));
    }

}
