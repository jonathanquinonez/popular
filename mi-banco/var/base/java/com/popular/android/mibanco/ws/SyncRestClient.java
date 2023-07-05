package com.popular.android.mibanco.ws;

import android.content.Context;
import android.util.Log;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.shieldsquare.ss2_android_sdk.captcha.TextCaptcha;
import com.shieldsquare.ss2_android_sdk.core.CookieManager;
import com.shieldsquare.ss2_android_sdk.core.ShieldSquare;
import com.shieldsquare.ss2_android_sdk.core.ShieldSquareInterceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Provides basic set of constants and methods for interacting with web services.
 */
public class SyncRestClient {

    /** Application context. */
    private final Context context;

    /** The device id. */
    private String deviceID;

    /** The language code. */
    private String language;

    /** The web service URL. */
    private String webServiceUrl;

    private CookieManager cookieHandler;

    private static Retrofit retrofit = null;

    private static MiBancoServices miBancoServices;

    private static final boolean ENABLE_ATTESTATION = true;

    private static final long ATTESTATION_EXPIRY = 86400; //seconds

    private static final boolean ATTESTATION_ON_RELAUNCH = false;

    /**
     * Create web service connection.
     *
     * @param aWebServiceUrl web service URL
     * @param aDeviceID the device id
     * @param aLanguage the language used
     * @param aContext the context
     */
    public SyncRestClient(final String aWebServiceUrl, final String aDeviceID, final String aLanguage, final Context aContext) {
        context = aContext;
        webServiceUrl = aWebServiceUrl;
        deviceID = aDeviceID;
        language = aLanguage;

        initRadwareSDK();
        initRetrofit();
    }

    public CookieManager getCookieManager() {
        return cookieHandler;
    }

    public MiBancoServices getMiBancoServices() {
        return miBancoServices;
    }

    private void initRetrofit() {
        cookieHandler = CookieManager.provideCookieJar();

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.cookieJar(cookieHandler);
        builder.readTimeout(180, TimeUnit.SECONDS);
        builder.connectTimeout(180, TimeUnit.SECONDS);

        builder.addInterceptor(chain -> {
            Request.Builder builder1 = chain.request().newBuilder();
            builder1.headers(getJsonHeader());
            return chain.proceed(builder1.build());
        });

        builder.addInterceptor(new ShieldSquareInterceptor());

        retrofit = new Retrofit.Builder()
                .baseUrl(webServiceUrl)
                .client(builder.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        miBancoServices = retrofit.create(MiBancoServices.class);
    }

    protected ShieldSquare initRadwareSDK () {
        TextCaptcha textCaptcha = new TextCaptcha.Builder().build();

        return new ShieldSquare.Builder(App.getApplicationInstance())
                .setTrackingEnabled(true)
                .setSubscriberID(BuildConfig.radware_cid)
                .setShieldSecret(BuildConfig.radware_secret)
                .setCaptchaOption(textCaptcha)
                .setTrackingInterval(30,90)
                .setShieldSquareServiceUrl("https://cas.avalon.perfdrive.com/")
                .setAttestationConfig(ENABLE_ATTESTATION,ATTESTATION_ON_RELAUNCH,
                        ATTESTATION_EXPIRY).build();
    }

    private Headers getJsonHeader() {
        Headers.Builder builder = new Headers.Builder();
        builder.add("User-Agent", MiBancoEnviromentConstants.USER_AGENT_STRING);
        builder.add("Accept", "application/json");
        builder.add("Accept-Language", language);
        builder.add("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        builder.add("X-UDID", deviceID);
        builder.add("Connection", "keep-alive");

        return builder.build();
    }

    public static String downloadExternalContent(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = getStringFromInputStream(is);
            return contentAsString;
        }
        catch(Exception e){
            Log.e(App.getApplicationInstance().getAttributionTag(), e.getMessage());
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
        return "";
    }

    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            Log.e(App.getApplicationInstance().getAttributionTag(), e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Log.e(App.getApplicationInstance().getAttributionTag(), e.getMessage());
                }
            }
        }

        return sb.toString();

    }

    public String getLanguage() {
        return language;
    }

    /**
     * Returns the String representation of a parameters dictionary.
     *
     * @param params the parameters dictionary
     * @return the String representation of the parameters dictionary
     */
    private String parseParams(final HashMap<String, Object> params) {
        String ret = null;
        if (params != null && params.size() > 0) {
            try {
                ret = "";
                final Iterator<String> keys = params.keySet().iterator();
                String key = keys.next();
                ret += URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(params.get(key).toString(), "UTF-8");
                while (keys.hasNext()) {
                    key = keys.next();
                    ret += "&" + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(params.get(key).toString(), "UTF-8");
                }
            } catch (final Exception ex) {
                Log.w("WebService", ex);
            }
        }
        return ret;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    /**
     * Fixes invalid backend JSON. This should be temporary while the Bank releases a scheduled backend update.
     *
     * @param json A JSON string
     *
     * @return Cleaned up JSON string
     *
     */
    public String preProcessResponse(final String json) {

        // For Premia Account type invalid product id JSON
        String out = json.replaceAll("\"productId:", "\"productId\":");

        // For quotes inside JSON value strings
        out = out.replaceAll("'", "\u2019");

        // For error descriptions
        out = out.replaceAll("class=\"error\"", "");

        // For carriage returns inside JSON string values
        out = out.replaceAll("\\r\\n", "");

        return out;

    }
}
