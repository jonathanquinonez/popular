package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.AccountTransaction;
import com.popular.android.mibanco.model.AccountTransactions;
import com.popular.android.mibanco.model.TransactionsCycle;
import com.popular.android.mibanco.util.AccountStatementsLoader;
import com.popular.android.mibanco.util.FontChanger;
import com.popular.android.mibanco.util.Utils;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({R.class, MiBancoConstants.class, MiBancoEnviromentConstants.class,
        App.class, BuildConfig.class, FeatureFlags.class, FontChanger.class, AccountStatementsLoader.class, Utils.class})

public class TransactionListAdapterIT {

    @Mock
    private AccountTransaction transaction;

    @Mock
    private List<AccountTransaction> transactions;

    @Mock
    private AccountTransactions accountTransactions;

    @Mock
    private Context context;

    @Mock
    private ListView listView;

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


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(FontChanger.class);
        PowerMockito.mockStatic(AccountStatementsLoader.class);
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
        accountTransactions = PowerMockito.mock(AccountTransactions.class);
        listView = PowerMockito.mock(ListView.class);

        PowerMockito.doReturn("http://192.168.37.107:").when(Utils.class, "changeHostIp", any(String.class));
        when(context.getSharedPreferences(MiBancoConstants.PREFS_KEY, Context.MODE_PRIVATE)).thenReturn(sharedPreferences);
        PowerMockito.doReturn("api_url_test_6565").when(MiBancoEnviromentConstants.class, "getSavedUrl");

    }

    @Test
    public void whenITUpstreamTransactionListAdapter_ExecutedFromLoginControllerClassUpdateListViewMethod_GivenTransactionsList_ThenValidateGetViewReached () throws Exception {
        PowerMockito.when(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(inflater);
        when(context.getString(R.string.debit)).thenReturn("debit");
        when(context.getString(R.string.all)).thenReturn("all");
        when(context.getString(R.string.credit)).thenReturn("credit");

        TransactionsCycle transactionsCycle = PowerMockito.mock(TransactionsCycle.class);

        when(AccountStatementsLoader.getStatementRangeString(context, transactionsCycle, transactions)).thenReturn("CURRENT STATEMENT");

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(context, accountTransactions, transactions, 1, "000199200", "IDA");
        viewPagerAdapter.updateListView(listView, transactions, view, 1);
        verify(listView, times(1)).setAdapter(any(TransactionListAdapter.class));

    }


}
