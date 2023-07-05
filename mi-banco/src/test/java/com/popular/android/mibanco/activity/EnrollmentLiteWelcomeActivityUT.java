package com.popular.android.mibanco.activity;

import android.content.Context;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.util.MobileCashUtils;
import com.popular.android.mibanco.ws.SyncRestClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EnrollmentLiteWelcomeActivity.class, SyncRestClient.class, App.class, Context.class})
public class EnrollmentLiteWelcomeActivityUT {

    private EnrollmentLiteWelcomeActivity enrollmentLiteWelcomeActivity;

    private App mockApp = mock(App.class);

    private Context mockContext = mock(Context.class);

    @Before
    public void setup() throws Exception {

        MockitoAnnotations.initMocks(this);

        mockStatic(App.class);

        when(App.getApplicationInstance()).thenReturn(mockApp);
        when(mockApp.getBaseContext()).thenReturn(mockContext);

        enrollmentLiteWelcomeActivity = spy(new EnrollmentLiteWelcomeActivity());
    }

    @Test
    public void whenGetFormattedExpDate_Fail() throws Exception {
        when(enrollmentLiteWelcomeActivity, method(EnrollmentLiteWelcomeActivity.class, "displayPhoneAlertsMessage"))
                .withNoArguments()
                .thenThrow();
    }
}
