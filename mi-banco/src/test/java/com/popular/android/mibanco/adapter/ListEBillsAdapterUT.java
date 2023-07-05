package com.popular.android.mibanco.adapter;

import static org.junit.Assert.assertNull;
import static org.powermock.api.mockito.PowerMockito.mock;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.object.ListItemEBills;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
@PrepareForTest({App.class,R.class, ListEBillsAdapter.class})
public class ListEBillsAdapterUT  {

    @Mock
    private ListEBillsAdapter listEBillsAdapterUT;

    @Mock
    private App app;
    int position = 1;

    @Mock
    private final View convertView = mock(View.class);

    @Mock
    private  final ViewGroup parent= mock(ViewGroup.class);

    @Mock
     private final Context ctx= mock(Context.class);

    @Mock
    private ListItemEBills listItemEBills;

    @Mock
    private final Activity act = mock(Activity.class);

    @Mock
    private View view= mock(View.class);

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(App.class);
        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);
        PowerMockito.when(app.getBaseContext()).thenReturn(ctx);
    }

    @Test
    public void whenGetView_Then_ReturnSucces(){

        ArrayList aItems = new ArrayList<ListItemEBills>();
        aItems.add(listItemEBills);
        listEBillsAdapterUT =  mock(ListEBillsAdapter.class);
        view =listEBillsAdapterUT.getView(position, convertView, parent);
        assertNull(view);
    }



}
