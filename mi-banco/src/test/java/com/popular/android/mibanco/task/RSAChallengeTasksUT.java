package com.popular.android.mibanco.task;

import android.content.Context;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.listener.ResponderListener;

import org.junit.Before;
import org.junit.Ignore;
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
@PrepareForTest({App.class, RSAChallengeTasks.class, Context.class, ResponderListener.class})
public class RSAChallengeTasksUT {

    private App mockApp = mock(App.class);

    private Context mockContext = mock(Context.class);

    private ResponderListener mockResponderListener = mock(ResponderListener.class);

    private final String sdkRsaJson = "[{id:myid,device:fewt24,deviceOS:Android,macAddress:23-434-112-234}]";
    private final String rsaCookies = "myCookie";

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);
        mockStatic(App.class);
        mockStatic(RSAChallengeTasks.class);
        mockStatic(Thread.class);

        when(App.getApplicationInstance()).thenReturn(mockApp);
        when(mockApp.getBaseContext()).thenReturn(mockContext);

    }

    @Test
    public void whenCallingGetOOBChallenge_ThenSuccessStaticMethodCall() {
        RSAChallengeTasks.getOOBChallenge(mockContext, sdkRsaJson, rsaCookies, mockResponderListener);
        verifyStatic(atLeast(1));
    }

    @Test
    public void whenCallingGetOOBChallengeTwice_ThenSuccessStaticMethodCallTwoTimes() {
        RSAChallengeTasks.getOOBChallenge(mockContext, sdkRsaJson, rsaCookies, mockResponderListener);
        RSAChallengeTasks.getOOBChallenge(mockContext, sdkRsaJson, rsaCookies, mockResponderListener);
        verifyStatic(atLeast(2));
    }


    @Test
    public void whenCallingGetRSAChallenge_ThenSuccessStaticMethodCall() {
        RSAChallengeTasks.getRSAChallenge(mockContext, sdkRsaJson, rsaCookies, mockResponderListener);
        verifyStatic(atLeast(1));
    }

    @Test
    public void whenCallingGetRSAChallengeTwice_ThenSuccessStaticMethodCallTwoTimes() {
        RSAChallengeTasks.getRSAChallenge(mockContext, sdkRsaJson, rsaCookies, mockResponderListener);
        RSAChallengeTasks.getRSAChallenge(mockContext, sdkRsaJson, rsaCookies, mockResponderListener);
        verifyStatic(atLeast(2));
    }

    @Ignore
    @Test
    public void when_CallingGetRSAChallenge_thenThreadIsPutToSleepCorrectly() throws InterruptedException {
        final long beforeCall = System.currentTimeMillis();
        RSAChallengeTasks.getRSAChallenge(mockContext, sdkRsaJson, rsaCookies, mockResponderListener);
        Thread.sleep(4000);
        verifyStatic(atLeast(2));
        final long afterCall = System.currentTimeMillis();
        assertTrue(afterCall > beforeCall);
    }

}
