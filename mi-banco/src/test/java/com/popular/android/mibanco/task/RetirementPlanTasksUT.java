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

import static org.mockito.Mockito.atLeast;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({App.class, Context.class, ResponderListener.class, RetirementPlanTasks.class})
public class RetirementPlanTasksUT {

    private App mockApp = mock(App.class);

    private Context mockContext = mock(Context.class);

    private ResponderListener mockResponderListener = mock(ResponderListener.class);

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);

        mockStatic(App.class);
        mockStatic(RetirementPlanTasks.class);
        mockStatic(Thread.class);

        when(App.getApplicationInstance()).thenReturn(mockApp);
        when(mockApp.getBaseContext()).thenReturn(mockContext);

    }

    @Test
    public void whenCallingRetirementPlanInfo_ThenSuccessStaticMethodCall() {
        RetirementPlanTasks.retirementPlanInfo(mockContext, mockResponderListener);
        verifyStatic(atLeast(1));
    }


}
