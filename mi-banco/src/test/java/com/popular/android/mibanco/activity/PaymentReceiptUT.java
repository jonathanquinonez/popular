package com.popular.android.mibanco.activity;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.task.AsyncTasks;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;


import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ReportFragment;


@RunWith(PowerMockRunner.class)
@PrepareForTest({BPAnalytics.class, Context.class, App.class, Bundle.class, ReportFragment.class, R.class, Utils.class, ContextCompat.class})
public class PaymentReceiptUT {

    @Mock
    private App app;

    @Mock
    private AsyncTasks asyncTasks;

    @Mock
    private Bundle savedInstanceState;
    @Mock
    private Context context;
    @Mock
    private Resources resources;
    @Mock
    private SharedPreferences sharedPreferences;
    @Mock
    private AppCompatDelegate appDelegate;
    @Mock
    private Intent intent;
    @Mock
    private RelativeLayout receiptLayout;
    @Mock
    private Button button;
    @Mock
    private TextView textView;
    @Mock
    private ImageView imageView;

    @InjectMocks
    private PaymentReceipt paymentReceipt;
    @Before
    public void setUp() throws Exception
    {

        MockitoAnnotations.initMocks(this);
        mockStatic(BPAnalytics.class);
        mockStatic(App.class);
        mockStatic(R.class);
        mockStatic(ReportFragment.class);
        mockStatic(Utils.class);
        mockStatic(ContextCompat.class);
        context = mock(Context.class);
        savedInstanceState = mock(Bundle.class);
        paymentReceipt = PowerMockito.spy(new PaymentReceipt());

        when(App.getApplicationInstance()).thenReturn(app);
        when(app.getAsyncTasksManager()).thenReturn(asyncTasks);
        when(app.getBaseContext()).thenReturn(context);
        PowerMockito.when(Utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.doReturn(appDelegate).when(paymentReceipt).getDelegate();
        when(paymentReceipt.getIntent()).thenReturn(intent);

    }

    @Test
    public void whenCallOnClickOverPayment_GivenAppNotNull_ThenVerifySetReloadPaymentTrue() throws Exception {

        Whitebox.invokeMethod(paymentReceipt, "onClickOverPayment", app);

        verify(app, times(1)).setReloadPayments(true);
    }

    @Test
    public void whenCallOnClickOverTransfers_GivenAppNotNull_ThenVerifySetReloadTransfersTrue() throws Exception {


        Whitebox.invokeMethod(paymentReceipt, "onClickOverTransfers",app);

        verify(app, times(1)).getAsyncTasksManager();

        verify(app, times(1)).setReloadTransfers(true);

    }

    @Ignore
    @Test
    public void whenCallOnCreate_GivenSavedInstanceStateNotNullAndAndHistoryReceiptTrueAndTransfersTrue_ThenCallOneTime() throws Exception
    {
        doNothing().when(paymentReceipt).setContentView(R.layout.receipt);
        when(paymentReceipt.findViewById(R.id.from_name)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.from_nr)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.to_name)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.to_nr)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.amount)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.date)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.button_close)).thenReturn(button);
        when(intent.getStringExtra(anyString())).thenReturn("pruebaUTejemplo");
        when(paymentReceipt.findViewById(R.id.receipt_view)).thenReturn(receiptLayout);
        when(intent.getBooleanExtra("historyReceipt", false)).thenReturn(true);
        when(appDelegate.findViewById(R.id.reference_nr)).thenReturn(textView);
        when(appDelegate.findViewById(R.id.status_title)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.status)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.frequency_title)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.frequency)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.alertTitle)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.stamp)).thenReturn(imageView);
        when(paymentReceipt.findViewById(R.id.alert)).thenReturn(imageView);
        when(paymentReceipt.getString(anyInt())).thenReturn("pruebaDEejemplo");
        when(app.getLanguage()).thenReturn("EN");
        when(intent.getBooleanExtra("transfers", false)).thenReturn(true);

        paymentReceipt.onCreate(savedInstanceState);
        verify(paymentReceipt, times(1)).onCreate(savedInstanceState);

    }

    @Ignore
    @Test
    public void whenCallOnCreate_GivenSavedInstanceStateNotNullAndAndHistoryReceiptTrueAndTransfersFalseAndStatusCodeNotNull_ThenCallOneTime() throws Exception
    {
        doNothing().when(paymentReceipt).setContentView(R.layout.receipt);
        when(paymentReceipt.findViewById(R.id.from_name)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.from_nr)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.to_name)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.to_nr)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.amount)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.date)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.button_close)).thenReturn(button);
        when(intent.getStringExtra(anyString())).thenReturn("pruebaUTejemplo");
        when(paymentReceipt.findViewById(R.id.receipt_view)).thenReturn(receiptLayout);
        when(intent.getBooleanExtra("historyReceipt", false)).thenReturn(true);
        when(appDelegate.findViewById(R.id.reference_nr)).thenReturn(textView);
        when(appDelegate.findViewById(R.id.status_title)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.status)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.frequency_title)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.frequency)).thenReturn(textView);
        when(intent.getStringExtra("statusCode")).thenReturn(MiBancoConstants.TRANSACTION_STATUS_CODE_OK);
        when(paymentReceipt.findViewById(R.id.alertTitle)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.stamp)).thenReturn(imageView);
        when(paymentReceipt.findViewById(R.id.alert)).thenReturn(imageView);
        when(paymentReceipt.getString(anyInt())).thenReturn("pruebaDEejemplo");
        when(app.getLanguage()).thenReturn("EN");
        when(intent.getBooleanExtra("transfers", false)).thenReturn(false);

        paymentReceipt.onCreate(savedInstanceState);
        verify(paymentReceipt, times(1)).onCreate(savedInstanceState);

    }

    @Ignore
    @Test
    public void whenCallOnCreate_GivenSavedInstanceStateNotNullAndHistoryReceiptTrueAndTransfersFalse_ThenCallOneTime() throws Exception
    {
        doNothing().when(paymentReceipt).setContentView(R.layout.receipt);
        when(paymentReceipt.findViewById(R.id.from_name)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.from_nr)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.to_name)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.to_nr)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.amount)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.date)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.button_close)).thenReturn(button);
        when(intent.getStringExtra(anyString())).thenReturn("pruebaUTejemplo");
        when(paymentReceipt.findViewById(R.id.receipt_view)).thenReturn(receiptLayout);
        when(intent.getBooleanExtra("historyReceipt", false)).thenReturn(true);
        when(appDelegate.findViewById(R.id.reference_nr)).thenReturn(textView);
        when(appDelegate.findViewById(R.id.status_title)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.status)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.frequency_title)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.frequency)).thenReturn(textView);
        when(intent.getStringExtra("statusCode")).thenReturn(MiBancoConstants.TRANSACTION_STATUS_CODE_IN_PROCESS);
        when(paymentReceipt.findViewById(R.id.alertTitle)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.stamp)).thenReturn(imageView);
        when(paymentReceipt.findViewById(R.id.alert)).thenReturn(imageView);
        when(paymentReceipt.getString(anyInt())).thenReturn("pruebaDEejemplo");
        when(app.getLanguage()).thenReturn("EN");
        when(intent.getBooleanExtra("transfers", false)).thenReturn(false);

        paymentReceipt.onCreate(savedInstanceState);
        verify(paymentReceipt, times(1)).onCreate(savedInstanceState);

    }

    @Ignore
    @Test
    public void whenCallOnCreate_GivenSavedInstanceStateNotNullAndAndAndHistoryReceiptFalseAndTransfersTrueAndErrorMessageNotNull_ThenCallOneTime() throws Exception//*******
    {
        doNothing().when(paymentReceipt).setContentView(R.layout.receipt);
        when(paymentReceipt.findViewById(R.id.from_name)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.from_nr)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.to_name)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.to_nr)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.amount)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.date)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.button_close)).thenReturn(button);
        when(intent.getStringExtra(anyString())).thenReturn("pruebaUTejemplo");
        when(paymentReceipt.findViewById(R.id.receipt_view)).thenReturn(receiptLayout);
        when(intent.getBooleanExtra("historyReceipt", false)).thenReturn(false);
        when(paymentReceipt.findViewById(R.id.alertTitle)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.stamp)).thenReturn(imageView);
        when(paymentReceipt.findViewById(R.id.alert)).thenReturn(imageView);
        when(paymentReceipt.getString(anyInt())).thenReturn("pruebaDEejemplo");
        when(app.getLanguage()).thenReturn("ES");
        when(intent.getBooleanExtra("transfers", false)).thenReturn(true);
        when(paymentReceipt.findViewById(R.id.receipt_nr)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.reference_nr)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.error_title)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.error_message)).thenReturn(textView);


        paymentReceipt.onCreate(savedInstanceState);
        verify(paymentReceipt, times(1)).onCreate(savedInstanceState);

    }

    @Ignore
    @Test
    public void whenCallOnCreate_GivenSavedInstanceStateNotNullAndAndAndAndHistoryReceiptFalseAndTransfersFalseAndErrorMessageNotNull_ThenCallOneTime() throws Exception//*******
    {
        doNothing().when(paymentReceipt).setContentView(R.layout.receipt);
        when(paymentReceipt.findViewById(R.id.from_name)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.from_nr)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.to_name)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.to_nr)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.amount)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.date)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.button_close)).thenReturn(button);
        when(intent.getStringExtra(anyString())).thenReturn("pruebaUTejemplo");
        when(paymentReceipt.findViewById(R.id.receipt_view)).thenReturn(receiptLayout);
        when(intent.getBooleanExtra("historyReceipt", false)).thenReturn(false);
        when(paymentReceipt.findViewById(R.id.alertTitle)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.stamp)).thenReturn(imageView);
        when(paymentReceipt.findViewById(R.id.alert)).thenReturn(imageView);
        when(paymentReceipt.getString(anyInt())).thenReturn("pruebaDEejemplo");
        when(app.getLanguage()).thenReturn("ES");
        when(intent.getBooleanExtra("transfers", false)).thenReturn(false);
        when(paymentReceipt.findViewById(R.id.receipt_nr)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.reference_nr)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.error_title)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.error_message)).thenReturn(textView);


        paymentReceipt.onCreate(savedInstanceState);
        verify(paymentReceipt, times(1)).onCreate(savedInstanceState);

    }

    @Ignore
    @Test
    public void whenCallOnCreate_GivenSavedInstanceStateNotNullAndAndAndAndHistoryReceiptFalseAndTransfersFalseAndErrorMessageNull_ThenCallOneTime() throws Exception
    {
        doNothing().when(paymentReceipt).setContentView(R.layout.receipt);
        when(paymentReceipt.findViewById(R.id.from_name)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.from_nr)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.to_name)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.to_nr)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.amount)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.date)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.button_close)).thenReturn(button);
        when(intent.getStringExtra(anyString())).thenReturn("pruebaUTejemplo");
        when(paymentReceipt.findViewById(R.id.receipt_view)).thenReturn(receiptLayout);
        when(intent.getBooleanExtra("historyReceipt", false)).thenReturn(false);
        when(paymentReceipt.findViewById(R.id.alertTitle)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.stamp)).thenReturn(imageView);
        when(paymentReceipt.findViewById(R.id.alert)).thenReturn(imageView);
        when(paymentReceipt.getString(anyInt())).thenReturn("pruebaDEejemplo");
        when(app.getLanguage()).thenReturn("EN");
        when(intent.getBooleanExtra("transfers", false)).thenReturn(false);
        when(intent.getStringExtra(MiBancoConstants.ERROR_MESSAGE_KEY)).thenReturn(null);

        paymentReceipt.onCreate(savedInstanceState);
        verify(paymentReceipt, times(1)).onCreate(savedInstanceState);
    }

    @Ignore
    @Test
    public void whenCallOnCreate_GivenSavedInstanceStateNotNullAndAndAndAndHistoryReceiptFalseAndTransfersTrueAndErrorMessageNull_ThenCallOneTime() throws Exception
    {
        doNothing().when(paymentReceipt).setContentView(R.layout.receipt);
        when(paymentReceipt.findViewById(R.id.from_name)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.from_nr)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.to_name)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.to_nr)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.amount)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.date)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.button_close)).thenReturn(button);
        when(intent.getStringExtra(anyString())).thenReturn("pruebaUTejemplo");
        when(paymentReceipt.findViewById(R.id.receipt_view)).thenReturn(receiptLayout);
        when(intent.getBooleanExtra("historyReceipt", false)).thenReturn(false);
        when(paymentReceipt.findViewById(R.id.alertTitle)).thenReturn(textView);
        when(paymentReceipt.findViewById(R.id.stamp)).thenReturn(imageView);
        when(paymentReceipt.findViewById(R.id.alert)).thenReturn(imageView);
        when(paymentReceipt.getString(anyInt())).thenReturn("pruebaDEejemplo");
        when(app.getLanguage()).thenReturn("EN");
        when(intent.getBooleanExtra("transfers", false)).thenReturn(true);
        when(intent.getStringExtra(MiBancoConstants.ERROR_MESSAGE_KEY)).thenReturn(null);

        paymentReceipt.onCreate(savedInstanceState);
        verify(paymentReceipt, times(1)).onCreate(savedInstanceState);

    }
}

