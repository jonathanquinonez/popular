package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.popular.android.mibanco.util.Utils;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.AccountTransaction;
import com.popular.android.mibanco.util.FontChanger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({R.class, MiBancoConstants.class, MiBancoEnviromentConstants.class,
        App.class, BuildConfig.class, FeatureFlags.class, FontChanger.class, TextUtils.class,Utils.class})
public class TransactionListAdapterUT {

    @Mock
    private AccountTransaction transaction;

    @Mock
    private List<AccountTransaction> transactions;

    @Mock
    private Context context;

    @Mock
    private App app;

    @Mock
    private Resources resources;

    @Mock
    private SharedPreferences sharedPreferences;

    @Mock
    private LayoutInflater inflater;

    @Mock
    private View view;

    @Mock
    protected ImageView imageView;

    @Mock
    private TextView textView;

    @Mock
    private ViewGroup viewGroup;

    private TransactionListAdapter transactionListAdapter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(FontChanger.class);
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);
        PowerMockito.when(app.getBaseContext()).thenReturn(context);
        PowerMockito.when(Utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);
        PowerMockito.when(context.getResources()).thenReturn(resources);

        PowerMockito.mockStatic(MiBancoEnviromentConstants.class);
        PowerMockito.mockStatic(FeatureFlags.class);

        PowerMockito.when(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(inflater);

        transactions = PowerMockito.spy(new ArrayList<AccountTransaction>());
        transaction = PowerMockito.mock(AccountTransaction.class);
        transactions.add(transaction);
        transactionListAdapter = new TransactionListAdapter(context, transactions, "CURRENT STATEMENT", "IDA", "1234");

        when(transaction.getDescription()).thenReturn("TRANSFER ON 04/19/21");
        when(transaction.getPostedDate()).thenReturn("");
        when(transaction.getAmount()).thenReturn("10000");
        when(transaction.getSign()).thenReturn("+");
        when(transaction.getShowDetailEnabled()).thenReturn("true");
        PowerMockito.doReturn("http://192.168.37.107:").when(Utils.class, "changeHostIp", any(String.class));
        when(context.getSharedPreferences(MiBancoConstants.PREFS_KEY, Context.MODE_PRIVATE)).thenReturn(sharedPreferences);
        PowerMockito.doReturn("api_url_test_6565").when(MiBancoEnviromentConstants.class, "getSavedUrl");

    }

    @Test
    public void whenGetView_GivenAccountIDA_transactionIsDebitFalse_ThenArrowVisible () throws Exception{
        TransactionListAdapter.ViewHolder viewHolder = PowerMockito.mock(TransactionListAdapter.ViewHolder.class);
        view = PowerMockito.mock(View.class);
        textView = PowerMockito.mock(TextView.class);
        viewHolder.description =  textView;
        viewHolder.date =  textView;
        viewHolder.amount =  textView;
        viewHolder.amountSign =  textView;
        viewHolder.accountDetailArrow =  imageView;

        when(transaction.getIsDebit()).thenReturn(false);
        when(FeatureFlags.MBCA_104()).thenReturn(Boolean.TRUE);

        when(App.isSelectedNonTransactioanlAcct(anyString())).thenReturn(false);
        when(inflater.inflate(anyInt(), any(ViewGroup.class), any(Boolean.class))).thenReturn(view);
        when(view.findViewById(R.id.text_header)).thenReturn(textView);
        when(view.getTag()).thenReturn(viewHolder);

        PowerMockito.doNothing().when(FontChanger.class, "changeFonts", any(View.class));
        transactionListAdapter.getView(1, view, viewGroup);
        verify(viewHolder.accountDetailArrow, times(1)).setVisibility(View.VISIBLE);
    }

    @Test
    public void whenGetView_GivenAccountIDA_transactionIsDebitTrue_ThenArrowGone () throws Exception{
        TransactionListAdapter.ViewHolder viewHolder = PowerMockito.mock(TransactionListAdapter.ViewHolder.class);
        view = PowerMockito.mock(View.class);
        textView = PowerMockito.mock(TextView.class);
        viewHolder.description =  textView;
        viewHolder.date =  textView;
        viewHolder.amount =  textView;
        viewHolder.amountSign =  textView;
        viewHolder.accountDetailArrow =  imageView;

        when(transaction.getIsDebit()).thenReturn(true);
        when(FeatureFlags.MBCA_104()).thenReturn(Boolean.TRUE);
        when(App.isSelectedNonTransactioanlAcct(anyString())).thenReturn(false);
        when(inflater.inflate(anyInt(), any(ViewGroup.class), any(Boolean.class))).thenReturn(view);
        when(view.findViewById(R.id.text_header)).thenReturn(textView);
        when(view.getTag()).thenReturn(viewHolder);

        PowerMockito.doNothing().when(FontChanger.class, "changeFonts", any(View.class));
        final View firstItem = transactionListAdapter.getView(1, view, viewGroup);

        verify(firstItem, times(1)).setOnClickListener(null);
        verify(viewHolder.accountDetailArrow, times(1)).setVisibility(View.GONE);
    }

    @Test
    public void whenGetView_GivenAccountIDA_ThenArrowVisible () throws Exception{
        TransactionListAdapter.ViewHolder viewHolder = PowerMockito.mock(TransactionListAdapter.ViewHolder.class);
        view = PowerMockito.mock(View.class);
        textView = PowerMockito.mock(TextView.class);
        viewHolder.description =  textView;
        viewHolder.date =  textView;
        viewHolder.amount =  textView;
        viewHolder.amountSign =  textView;
        viewHolder.accountDetailArrow =  imageView;

        when(transaction.getIsDebit()).thenReturn(false);
        when(FeatureFlags.MBCA_104()).thenReturn(Boolean.TRUE);
        when(App.isSelectedNonTransactioanlAcct(anyString())).thenReturn(false);
        when(inflater.inflate(anyInt(), any(ViewGroup.class), any(Boolean.class))).thenReturn(view);
        when(view.findViewById(R.id.text_header)).thenReturn(textView);
        when(view.findViewById(R.id.statement_arrow)).thenReturn(imageView);
        when(view.getTag()).thenReturn(viewHolder);

        PowerMockito.doNothing().when(FontChanger.class, "changeFonts", any(View.class));
        final View firstItem = transactionListAdapter.getView(1, view, viewGroup);

        verify(firstItem, times(1)).setOnClickListener(any(View.OnClickListener.class));
        verify(viewHolder.accountDetailArrow, times(1)).setVisibility(View.VISIBLE);
    }

    @Test
    public void whenReferenceData_givenCustomerWealthFalse_ThenReturnIsWealthInModel () throws Exception{
        TransactionListAdapter.ViewHolder viewHolder = PowerMockito.mock(TransactionListAdapter.ViewHolder.class);
        view = PowerMockito.mock(View.class);
        textView = PowerMockito.mock(TextView.class);
        viewHolder.description =  textView;
        viewHolder.date =  textView;
        viewHolder.amount =  textView;
        viewHolder.amountSign =  textView;
        viewHolder.accountDetailArrow =  imageView;

        when(transaction.getIsDebit()).thenReturn(false);
        when(FeatureFlags.MBCA_104()).thenReturn(Boolean.TRUE);
        when(App.isSelectedNonTransactioanlAcct(anyString())).thenReturn(false);
        when(inflater.inflate(anyInt(), any(ViewGroup.class), any(Boolean.class))).thenReturn(view);
        when(view.findViewById(R.id.text_header)).thenReturn(textView);
        when(view.findViewById(R.id.statement_arrow)).thenReturn(imageView);
        when(view.getTag()).thenReturn(viewHolder);
        when(transaction.getShowDetailEnabled()).thenReturn("false");
        PowerMockito.doNothing().when(FontChanger.class, "changeFonts", any(View.class));
        final View firstItem = transactionListAdapter.getView(1, view, viewGroup);

        verify(firstItem, times(1)).setOnClickListener(null);
        verify(viewHolder.accountDetailArrow, times(1)).setVisibility(View.INVISIBLE);

    }
}
