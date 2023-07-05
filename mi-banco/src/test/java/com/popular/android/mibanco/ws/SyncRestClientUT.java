package com.popular.android.mibanco.ws;

import static org.junit.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import android.content.Context;

import com.popular.android.mibanco.App;
import com.shieldsquare.ss2_android_sdk.core.CookieManager;
import com.shieldsquare.ss2_android_sdk.core.ShieldSquare;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CookieManager.class,Retrofit.class, HttpURLConnection.class, OkHttpClient.class})
@PowerMockIgnore("javax.net.ssl.*")
public class SyncRestClientUT {

    private SyncRestClient syncRestClient;

    @Mock
    private ShieldSquare.Builder builder;
    @Mock
    private OkHttpClient okHttpClient;

    CookieManager cookieManager = mock(CookieManager.class);

    Retrofit retrofit = mock(Retrofit.class);

    HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);

    @Before
    public void setup() throws Exception {

        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(CookieManager.class);
        PowerMockito.mockStatic(Retrofit.class);
        PowerMockito.mockStatic(OkHttpClient.class);

        BDDMockito.given( CookieManager.provideCookieJar()).willReturn(cookieManager);


        Context context = new App();
        syncRestClient = spy(new SyncRestClient("http://test.popular.com", "test", "en", context));
    }

    @Test
    public void whenGetMiBancoServices_GivenGood_ThenReturnValue() throws IOException {
        MiBancoServices miBancoServices = syncRestClient.getMiBancoServices();
        assertNotNull(miBancoServices);
    }

    @Test
    public void whenGetCookieManager_GivenGood_ThenReturnValue() throws IOException {
        CookieManager cookieManager = syncRestClient.getCookieManager();
        assertNotNull(cookieManager);
    }

    @Test
    public void wheninitRadwareSDK_givenNotParams_thenReturnNotNullRadwareSDK () throws Exception {
        whenNew(ShieldSquare.Builder.class).withAnyArguments().thenReturn(builder);

        ShieldSquare shieldSquare = syncRestClient.initRadwareSDK();

        assertNotNull(shieldSquare);
    }

}
