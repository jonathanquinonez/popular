package com.popular.android.mibanco.activity;


import android.app.Activity;

import android.content.Context;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.task.AsyncTasks;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({App.class, Context.class, AsyncTasks.class, Activity.class})
public class FetchPaymentHistoryTaskUT {

    @InjectMocks
    private AsyncTasks mAsyncTasks ;

    private App mockApp = mock(App.class);

    private Context mockContext = mock(Activity.class);

    private Activity mockActivity = mock(Receipts.class);

    private ResponderListener mResponderListener = mock(ResponderListener.class);

    private Activity mReceipts;



    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        mockStatic(Context.class);


    }

    @Test
    public void whenFetchHistory_Payments_ByPayee_ReturnsTrue(){

        when(mockActivity.getApplication()).thenReturn(new App());

        mAsyncTasks.fetchPaymentHistory(mockActivity,mResponderListener,false,"123","");

        assertNotNull(mAsyncTasks);


    }

    @Test
    public void whenFetchHistory_Payments_ByPayee_ReturnsFalse(){

        when(mockActivity.getApplication()).thenReturn(new App());

        mAsyncTasks.fetchPaymentHistory(mockActivity,mResponderListener,false,null,"");

        assertNotNull(mAsyncTasks);

    }




}
