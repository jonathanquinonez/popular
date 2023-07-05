package com.popular.android.mibanco.activity;

import android.content.ClipData;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.PhonebookContact;
import com.popular.android.mibanco.object.ViewHolder;
import com.popular.android.mibanco.object.ViewHolderEasyCashPendForOther;
import com.popular.android.mibanco.util.ContactsManagementUtils;
import com.popular.android.mibanco.util.MobileCashUtils;
import com.popular.android.mibanco.ws.SyncRestClient;
import com.popular.android.mibanco.ws.response.MobileCashTrx;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.HashMap;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EasyCashNonCustHistoryActivity.class, SyncRestClient.class, App.class, Context.class,
        LayoutInflater.class, View.class, R.class})
public class EasyCashNonCustHistoryActivityUT {

    private EasyCashNonCustHistoryActivity easyCashNonCustHistoryActivity;

    private App mockApp = mock(App.class);

    private Context mockContext = mock(Context.class);

    private LayoutInflater mockInflater  = mock(LayoutInflater.class);;

    private View mockView = mock(View.class);

    private MenuItem menuItem = mock(MenuItem.class);

    private ImageView imageQR = mock(ImageView.class);

    private TextView textView = mock(TextView.class);

    private Resources resources = mock(Resources.class);

    private MobileCashTrx item = mock(MobileCashTrx.class);

    @Before
    public void setup() throws Exception {

        MockitoAnnotations.initMocks(this);

        mockStatic(App.class);
        mockStatic(LayoutInflater.class);

        when(App.getApplicationInstance()).thenReturn(mockApp);
        when(mockApp.getBaseContext()).thenReturn(mockContext);
        when(mockContext.getResources()).thenReturn(resources);

        easyCashNonCustHistoryActivity = spy(new EasyCashNonCustHistoryActivity());

    }

    @Ignore
    @Test
    public void whenGetPendingForOtherView_Fail() throws Exception {
        when(easyCashNonCustHistoryActivity.getResources()).thenReturn(mock(Resources.class));
        when(easyCashNonCustHistoryActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(mockInflater);
        View viewLocal = Mockito.spy(new View(mockContext));
        when(mockInflater.inflate(R.layout.easycash_transaction_list_item, null)).thenReturn(viewLocal);
        ViewHolderEasyCashPendForOther viewHolder = PowerMockito.spy(new ViewHolderEasyCashPendForOther(viewLocal));
        when(viewHolder.getImageQr()).thenReturn(imageQR);
        when(viewHolder.getTxtToOrFrom()).thenReturn(textView);

        item.setReceiverPhone("123123123");

        when(easyCashNonCustHistoryActivity, "getPendingForOtherView", new MobileCashTrx())
                .thenReturn(null);
    }

    @Ignore
    @Test
    public void whenOnOptionsItemSelected_Fail() throws Exception {
        when(easyCashNonCustHistoryActivity.onOptionsItemSelected(null)).thenReturn(null);
    }
}
