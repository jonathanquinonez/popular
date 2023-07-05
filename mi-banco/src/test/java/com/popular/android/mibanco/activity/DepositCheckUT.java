package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;

import com.popular.android.mibanco.model.Customer;
import com.popular.android.mibanco.model.CustomerAccount;

import com.popular.android.mibanco.task.AsyncTasks;
import com.popular.android.mibanco.util.DFMUtils;
import com.popular.android.mibanco.util.Utils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ReportFragment;

@RunWith(PowerMockRunner.class)
@PrepareForTest({R.class, App.class, Utils.class, Context.class, Bundle.class, ReportFragment.class, DFMUtils.class})
public class DepositCheckUT {

    @Mock
    private App app;
    @Mock
    private Context context;
    @Mock
    private Resources resources;
    @Mock
    private Bundle savedInstanceState;
    @Mock
    private SharedPreferences sharedPreferences;
    @Mock
    private AppCompatDelegate appDelegate;
    @Mock
    private Intent intent;
    @Mock
    private AsyncTasks asyncTasks;
    @Mock
    private WebView popularWebView;
    @Mock
    private WebSettings webSettings;
    @Mock
    private TextView textView;
    @Mock
    private Button button;
    @Mock
    private LinearLayout linearLayout;
    @Mock
    private CustomerAccount selectedAccount;
    @Mock
    private Customer customer;

    @InjectMocks
    private DepositCheck depositCheck;
    @Before
    public void setUp() throws IllegalAccessException
    {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.mockStatic(ReportFragment.class);
        PowerMockito.mockStatic(DFMUtils.class);
        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);

        savedInstanceState = mock(Bundle.class);
        context = mock(Context.class);
        depositCheck = PowerMockito.spy(new DepositCheck());

        when(App.getApplicationInstance()).thenReturn(app);
        when(app.getBaseContext()).thenReturn(context);
        PowerMockito.when(Utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.doReturn(appDelegate).when(depositCheck).getDelegate();
        when(depositCheck.getIntent()).thenReturn(intent);
    }

    @Ignore
    @Test
    public void whenCallOnCreate_GivenSavedInstanceStateNotNull_ThenCallOneTime() throws Exception
    {
        when(app.getLanguage()).thenReturn("es");
        when(depositCheck.getBaseContext()).thenReturn(context);
        when(Utils.getLocaleStringResource(any(Locale.class),any(Integer.class),any(Context.class))).thenReturn("https://documents.popular.com/terms/mi_banco/mb_tc_alertas.html#app");
        when(depositCheck.getResources()).thenReturn(resources);
        when(app.getAsyncTasksManager()).thenReturn(asyncTasks);
        Mockito.doNothing().when(depositCheck).setContentView(R.layout.deposit_check);
        when(depositCheck.findViewById(R.id.deposit_check_popular_text)).thenReturn(popularWebView);
        when(popularWebView.getSettings()).thenReturn(webSettings);
        when(depositCheck.findViewById(R.id.amount_text)).thenReturn(textView);
        when(depositCheck.findViewById(R.id.deposit_check_account_button)).thenReturn(textView);
        when(depositCheck.findViewById(R.id.deposit_check_camera)).thenReturn(button);
        when(depositCheck.findViewById(R.id.deposit_check_send_deposit_button)).thenReturn(button);
        when(depositCheck.findViewById(R.id.deposit_check_back_side_camera)).thenReturn(button);
        when(depositCheck.findViewById(R.id.deposit_check_account_display)).thenReturn(linearLayout);
        when(depositCheck.findViewById(R.id.amount_button)).thenReturn(linearLayout);
        when(DFMUtils.isLimitsPerSegmentEnabled()).thenReturn(false);
        when(app.getDepositCheckInformationFromSession()).thenReturn(true);
        when(app.getDepositCheckSelectedAccount()).thenReturn(selectedAccount);
        when(app.getDepositCheckCurrentState()).thenReturn(1);//FRONT_PHOTO_DISPLAYING

        depositCheck.onCreate(savedInstanceState);
        verify(depositCheck, times(1)).onCreate(savedInstanceState);
    }

    @Ignore
    @Test
    public void whenCallOnCreate_GivenSavedInstanceStateNotNullAndDepositCheckCurrentStateIs2_ThenCallOneTime() throws Exception
    {
        when(app.getLanguage()).thenReturn("es");
        when(depositCheck.getBaseContext()).thenReturn(context);
        when(Utils.getLocaleStringResource(any(Locale.class),any(Integer.class),any(Context.class))).thenReturn("https://documents.popular.com/terms/mi_banco/mb_tc_alertas.html#app");
        when(depositCheck.getResources()).thenReturn(resources);
        when(app.getAsyncTasksManager()).thenReturn(asyncTasks);
        Mockito.doNothing().when(depositCheck).setContentView(R.layout.deposit_check);
        when(depositCheck.findViewById(R.id.deposit_check_popular_text)).thenReturn(popularWebView);
        when(popularWebView.getSettings()).thenReturn(webSettings);
        when(depositCheck.findViewById(R.id.amount_text)).thenReturn(textView);
        when(depositCheck.findViewById(R.id.deposit_check_account_button)).thenReturn(textView);
        when(depositCheck.findViewById(R.id.deposit_check_camera)).thenReturn(button);
        when(depositCheck.findViewById(R.id.deposit_check_send_deposit_button)).thenReturn(button);
        when(depositCheck.findViewById(R.id.deposit_check_back_side_camera)).thenReturn(button);
        when(depositCheck.findViewById(R.id.deposit_check_account_display)).thenReturn(linearLayout);
        when(depositCheck.findViewById(R.id.amount_button)).thenReturn(linearLayout);
        when(DFMUtils.isLimitsPerSegmentEnabled()).thenReturn(true);
        when(app.getLoggedInUser()).thenReturn(customer);
        when(app.getDepositCheckInformationFromSession()).thenReturn(true);
        when(app.getDepositCheckSelectedAccount()).thenReturn(selectedAccount);
        when(app.getDepositCheckCurrentState()).thenReturn(2);
        depositCheck.onCreate(savedInstanceState);
        verify(depositCheck, times(1)).onCreate(savedInstanceState);
    }

    @Ignore
    @Test
    public void whenCallOnCreate_GivenSavedInstanceStateNotNullAndLimitsPerSegmentEnabledIsTrue_ThenCallOneTime() throws Exception
    {
        when(app.getLanguage()).thenReturn("es");
        when(depositCheck.getBaseContext()).thenReturn(context);
        when(Utils.getLocaleStringResource(any(Locale.class),any(Integer.class),any(Context.class))).thenReturn("https://documents.popular.com/terms/mi_banco/mb_tc_alertas.html#app");
        when(depositCheck.getResources()).thenReturn(resources);
        when(app.getAsyncTasksManager()).thenReturn(asyncTasks);
        Mockito.doNothing().when(depositCheck).setContentView(R.layout.deposit_check);
        when(depositCheck.findViewById(R.id.deposit_check_popular_text)).thenReturn(popularWebView);
        when(popularWebView.getSettings()).thenReturn(webSettings);
        when(depositCheck.findViewById(R.id.amount_text)).thenReturn(textView);
        when(depositCheck.findViewById(R.id.deposit_check_account_button)).thenReturn(textView);
        when(depositCheck.findViewById(R.id.deposit_check_camera)).thenReturn(button);
        when(depositCheck.findViewById(R.id.deposit_check_send_deposit_button)).thenReturn(button);
        when(depositCheck.findViewById(R.id.deposit_check_back_side_camera)).thenReturn(button);
        when(depositCheck.findViewById(R.id.deposit_check_account_display)).thenReturn(linearLayout);
        when(depositCheck.findViewById(R.id.amount_button)).thenReturn(linearLayout);
        when(DFMUtils.isLimitsPerSegmentEnabled()).thenReturn(true);
        when(app.getLoggedInUser()).thenReturn(customer);
        when(Utils.isValidUrl(any(String.class), any(Context.class))).thenReturn(true);
        when(app.getDepositCheckInformationFromSession()).thenReturn(true);
        when(app.getDepositCheckSelectedAccount()).thenReturn(selectedAccount);
        depositCheck.onCreate(savedInstanceState);
        verify(depositCheck, times(1)).onCreate(savedInstanceState);
    }

    @Ignore
    @Test
    public void whenCallOnCreate_GivenSavedInstanceStateNotNullAndDepositCheckCurrentStateIs1_ThenCallOneTime() throws Exception
    {
        byte[] depositCheckImage = "string".getBytes();
        when(app.getLanguage()).thenReturn("es");
        when(depositCheck.getBaseContext()).thenReturn(context);
        when(Utils.getLocaleStringResource(any(Locale.class),any(Integer.class),any(Context.class))).thenReturn("https://documents.popular.com/terms/mi_banco/mb_tc_alertas.html#app");
        when(depositCheck.getResources()).thenReturn(resources);
        when(app.getAsyncTasksManager()).thenReturn(asyncTasks);
        Mockito.doNothing().when(depositCheck).setContentView(R.layout.deposit_check);
        when(depositCheck.findViewById(R.id.deposit_check_popular_text)).thenReturn(popularWebView);
        when(popularWebView.getSettings()).thenReturn(webSettings);
        when(depositCheck.findViewById(R.id.amount_text)).thenReturn(textView);
        when(depositCheck.findViewById(R.id.deposit_check_account_button)).thenReturn(textView);
        when(depositCheck.findViewById(R.id.deposit_check_camera)).thenReturn(button);
        when(depositCheck.findViewById(R.id.deposit_check_send_deposit_button)).thenReturn(button);
        when(depositCheck.findViewById(R.id.deposit_check_back_side_camera)).thenReturn(button);
        when(depositCheck.findViewById(R.id.deposit_check_account_display)).thenReturn(linearLayout);
        when(depositCheck.findViewById(R.id.amount_button)).thenReturn(linearLayout);
        when(DFMUtils.isLimitsPerSegmentEnabled()).thenReturn(false);
        when(app.getDepositCheckInformationFromSession()).thenReturn(true);
        when(app.getDepositCheckSelectedAccount()).thenReturn(selectedAccount);
        when(app.getDepositCheckCurrentState()).thenReturn(1);
        when(app.getDepositCheckFrontImage()).thenReturn(depositCheckImage);
        depositCheck.onCreate(savedInstanceState);
        verify(depositCheck, times(1)).onCreate(savedInstanceState);
    }

    @Ignore
    @Test
    public void whenCallOnCreate_GivenSavedInstanceStateNotNullAndDepositCheckBackImageNotNull_ThenCallOneTime() throws Exception
    {
        byte[] depositCheckImage = "string".getBytes();
        when(app.getLanguage()).thenReturn("es");
        when(depositCheck.getBaseContext()).thenReturn(context);
        when(Utils.getLocaleStringResource(any(Locale.class),any(Integer.class),any(Context.class))).thenReturn("https://documents.popular.com/terms/mi_banco/mb_tc_alertas.html#app");
        when(depositCheck.getResources()).thenReturn(resources);
        when(app.getAsyncTasksManager()).thenReturn(asyncTasks);
        Mockito.doNothing().when(depositCheck).setContentView(R.layout.deposit_check);
        when(depositCheck.findViewById(R.id.deposit_check_popular_text)).thenReturn(popularWebView);
        when(popularWebView.getSettings()).thenReturn(webSettings);
        when(depositCheck.findViewById(R.id.amount_text)).thenReturn(textView);
        when(depositCheck.findViewById(R.id.deposit_check_account_button)).thenReturn(textView);
        when(depositCheck.findViewById(R.id.deposit_check_camera)).thenReturn(button);
        when(depositCheck.findViewById(R.id.deposit_check_send_deposit_button)).thenReturn(button);
        when(depositCheck.findViewById(R.id.deposit_check_back_side_camera)).thenReturn(button);
        when(depositCheck.findViewById(R.id.deposit_check_account_display)).thenReturn(linearLayout);
        when(depositCheck.findViewById(R.id.amount_button)).thenReturn(linearLayout);
        when(DFMUtils.isLimitsPerSegmentEnabled()).thenReturn(true);
        when(app.getLoggedInUser()).thenReturn(customer);
        when(app.getDepositCheckInformationFromSession()).thenReturn(true);
        when(app.getDepositCheckSelectedAccount()).thenReturn(selectedAccount);
        when(app.getDepositCheckCurrentState()).thenReturn(2);
        when(app.getDepositCheckFrontImage()).thenReturn(depositCheckImage);
        when(app.getDepositCheckBackImage()).thenReturn(depositCheckImage);
        depositCheck.onCreate(savedInstanceState);
        verify(depositCheck, times(1)).onCreate(savedInstanceState);
    }

    @Ignore
    @Test
    public void whenCallOnCreate_GivenSavedInstanceStateNotNullAndLimitsPerSegmentEnabledIsFalse_ThenCallOneTime() throws Exception
    {
        when(app.getLanguage()).thenReturn("es");
        when(depositCheck.getBaseContext()).thenReturn(context);
        when(Utils.getLocaleStringResource(any(Locale.class),any(Integer.class),any(Context.class))).thenReturn("https://documents.popular.com/terms/mi_banco/mb_tc_alertas.html#app");
        when(depositCheck.getResources()).thenReturn(resources);
        when(app.getAsyncTasksManager()).thenReturn(asyncTasks);
        Mockito.doNothing().when(depositCheck).setContentView(R.layout.deposit_check);
        when(depositCheck.findViewById(R.id.deposit_check_popular_text)).thenReturn(popularWebView);
        when(popularWebView.getSettings()).thenReturn(webSettings);
        when(depositCheck.findViewById(R.id.amount_text)).thenReturn(textView);
        when(depositCheck.findViewById(R.id.deposit_check_account_button)).thenReturn(textView);
        when(depositCheck.findViewById(R.id.deposit_check_camera)).thenReturn(button);
        when(depositCheck.findViewById(R.id.deposit_check_send_deposit_button)).thenReturn(button);
        when(depositCheck.findViewById(R.id.deposit_check_back_side_camera)).thenReturn(button);
        when(depositCheck.findViewById(R.id.deposit_check_account_display)).thenReturn(linearLayout);
        when(depositCheck.findViewById(R.id.amount_button)).thenReturn(linearLayout);
        when(DFMUtils.isLimitsPerSegmentEnabled()).thenReturn(false);
        when(Utils.isValidUrl(any(String.class), any(Context.class))).thenReturn(true);
        depositCheck.onCreate(savedInstanceState);
        verify(depositCheck, times(1)).onCreate(savedInstanceState);
    }

}
