package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.adapter.MarketplaceAdapter;
import com.popular.android.mibanco.model.Customer;
import com.popular.android.mibanco.model.MarketplaceCard;
import com.popular.android.mibanco.task.AsyncTasks;
import com.popular.android.mibanco.util.Utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ReportFragment;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MarketplaceActivity.class, ReportFragment.class, ListView.class, App.class, Utils.class})
public class MarketplaceActivityUT {

    @Mock
    private Menu menu;

    @Mock
    private MenuItem menuItem;

    @Mock
    private MarketplaceAdapter marketplaceAdapter;

    @Mock
    private MarketplaceCard marketplaceCard;

    @Mock
    private AppCompatDelegate appDelegate;

    @Mock
    private Intent intent;

    @Mock
    private ListView listView;

    @Mock
    private App application;

    @Mock
    private Customer customer;

    @Mock
    private AsyncTasks asyncTasks;

    @Mock
    private Context context;

    @InjectMocks
    private MarketplaceActivity marketplaceActivity;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(ReportFragment.class);
        marketplaceActivity = PowerMockito.spy(new MarketplaceActivity());
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(App.getApplicationInstance()).thenReturn(application);
        PowerMockito.when(application.getAsyncTasksManager()).thenReturn(asyncTasks);
        PowerMockito.when(application.getBaseContext()).thenReturn(context);
        PowerMockito.doReturn(appDelegate).when(marketplaceActivity).getDelegate();
    }

    @Ignore
    @Test
    public void whenOnCreate_ThenCallGetIntentExtras() throws Exception {
        doNothing().when(marketplaceActivity).setContentView(R.layout.marketplace_layout);
        when(marketplaceActivity.getIntent()).thenReturn(intent);
        when(intent.getBooleanExtra(MiBancoConstants.WEB_VIEW_MARKETPLACE_CCA, Boolean.FALSE)).thenReturn(Boolean.TRUE);
        when(intent.getBooleanExtra(MiBancoConstants.WEB_VIEW_MARKETPLACE_EACCOUNT, Boolean.FALSE)).thenReturn(Boolean.FALSE);
        when(application.getLoggedInUser()).thenReturn(customer);
        when(customer.getHasVIAccount()).thenReturn(Boolean.FALSE);
        when(application.getImageCachePath()).thenReturn(new File(StringUtils.EMPTY));

        PowerMockito.doReturn(listView).when(marketplaceActivity).findViewById((R.id.marketplacelistViewCards));

        marketplaceActivity.onCreate(null);
        verifyPrivate(marketplaceActivity).invoke("getIntentExtras");
    }

    @Ignore
    @Test
    public void whenOnCreate_ThenCallSetMarketplaceProducts() throws Exception {
        doNothing().when(marketplaceActivity).setContentView(R.layout.marketplace_layout);
        when(marketplaceActivity.getIntent()).thenReturn(intent);
        when(intent.getBooleanExtra(MiBancoConstants.WEB_VIEW_MARKETPLACE_CCA, Boolean.FALSE)).thenReturn(Boolean.TRUE);
        when(intent.getBooleanExtra(MiBancoConstants.WEB_VIEW_MARKETPLACE_EACCOUNT, Boolean.FALSE)).thenReturn(Boolean.FALSE);
        when(application.getLoggedInUser()).thenReturn(customer);
        when(customer.getHasVIAccount()).thenReturn(Boolean.FALSE);
        when(application.getImageCachePath()).thenReturn(new File(StringUtils.EMPTY));

        PowerMockito.doReturn(listView).when(marketplaceActivity).findViewById((R.id.marketplacelistViewCards));

        marketplaceActivity.onCreate(null);
        verifyPrivate(marketplaceActivity).invoke("setMarketplaceProducts");
    }

    @Ignore
    @Test
    public void whenOnCreate_ThenCallSetProductImages() throws Exception {
        doNothing().when(marketplaceActivity).setContentView(R.layout.marketplace_layout);
        when(marketplaceActivity.getIntent()).thenReturn(intent);
        when(intent.getBooleanExtra(MiBancoConstants.WEB_VIEW_MARKETPLACE_CCA, Boolean.FALSE)).thenReturn(Boolean.TRUE);
        when(intent.getBooleanExtra(MiBancoConstants.WEB_VIEW_MARKETPLACE_EACCOUNT, Boolean.FALSE)).thenReturn(Boolean.FALSE);
        when(application.getLoggedInUser()).thenReturn(customer);
        when(customer.getHasVIAccount()).thenReturn(Boolean.FALSE);
        when(application.getImageCachePath()).thenReturn(new File(StringUtils.EMPTY));

        PowerMockito.doReturn(listView).when(marketplaceActivity).findViewById((R.id.marketplacelistViewCards));

        marketplaceActivity.onCreate(null);
        verifyPrivate(marketplaceActivity).invoke("setProductImages");
    }

    @Test
    public void whenOnItemClick_ThenCallStartProductActivity() throws Exception {

        MarketplaceCard item = new MarketplaceCard("TEST", "TEST", "TEST", "TEST", "TEST", null);
        List<MarketplaceCard> cards = new ArrayList<MarketplaceCard>();
        cards.add(item);
        listView.setAdapter(marketplaceAdapter);

        marketplaceCard = item;

        when(listView.getAdapter()).thenReturn(marketplaceAdapter);
        when(marketplaceAdapter.getItem(0)).thenReturn(marketplaceCard);

        marketplaceActivity.onItemClick(listView, listView.getSelectedView(), 0, 0);
        verifyPrivate(marketplaceActivity).invoke("startProductActivity", marketplaceCard.getType());
    }

    @Test
    public void whenOnPrepareOptionsMenu_ThenReturnTrue() {
        PowerMockito.when(menu.findItem(R.id.menu_settings)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_logout)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_locator)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_contact)).thenReturn(menuItem);

        boolean result = marketplaceActivity.onPrepareOptionsMenu(menu);//Response when WebView Activity(Succes/Cancel)
        assert(result);
    }

}
