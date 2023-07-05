package com.popular.android.mibanco.activity;

import android.content.Context;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.ws.ApiClient;
import com.popular.android.mibanco.ws.MiBancoServices;
import com.popular.android.mibanco.ws.SyncRestClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import retrofit2.Call;
import retrofit2.Response;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApiClient.class, SyncRestClient.class, App.class, Context.class,
        MiBancoServices.class, Response.class})
public class FetchPaymentHistoryTaskIT {

    private ApiClient apiClient;

    private App mockApp = mock(App.class);

    private Context mockContext = mock(Context.class);

    private SyncRestClient mockSyncRestClient = mock(SyncRestClient.class);

    Call<String> call = mock(Call.class);

    Response<String> response = mock(Response.class);

    MiBancoServices mockMiBancoService = mock(MiBancoServices.class);


    @Before
    public void setup() throws Exception {

        MockitoAnnotations.initMocks(this);

        mockStatic(App.class);

        when(App.getApplicationInstance()).thenReturn(mockApp);
        when(mockApp.getBaseContext()).thenReturn(mockContext);

        Context context = new App();
        whenNew(SyncRestClient.class).withArguments("","","",context).thenReturn(mockSyncRestClient);
        apiClient = spy(new ApiClient("","","",context));

    }

    @Test
    public void whenCallingfetchPaymentHistoryByPayee_ThenReturnJsonResponseEmpty() throws Exception {
        final HashMap<String, Object> dummyParams = new HashMap<>(); // Parameter map.
        dummyParams.put("action", 3);
        dummyParams.put("payeeId","123");

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.paymentHistory(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn("{}");
    }

    @Test
    public void whenCallingfetchPaymentHistoryByPayee_ThenReturnJsonResponseNotEmpty() throws Exception {
        final HashMap<String, Object> dummyParams = new HashMap<>(); // Parameter map.
        dummyParams.put("action", 3);
        dummyParams.put("payeeId","123");

        when(apiClient.getSyncRestClient()).thenReturn(mockSyncRestClient);
        when(mockSyncRestClient.getMiBancoServices()).thenReturn(mockMiBancoService);
        when(mockMiBancoService.paymentHistory(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(dummyJsonResponseString());
    }

    private String dummyJsonResponseString() {
        StringBuffer sb = new StringBuffer(); // StringBuffer instance.
        sb.append("{\"responder_name\": \"payments\", ");
        sb.append("\"responder_message\": \"history\", ");
        sb.append("\"error_message\": \"\", ");
        sb.append("\"content\": \"\"totalInProcess\": \"$0.00\",    \"in_process\": [    ]\"}");
        return sb.toString();
    }


}
