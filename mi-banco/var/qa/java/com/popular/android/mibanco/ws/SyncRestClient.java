package com.popular.android.mibanco.ws;

import android.content.Context;
import android.util.Log;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Provides basic set of constants and methods for interacting with web services.
 */
public class SyncRestClient {

    /** Amount of seconds to keep connection alive. */
    public static final long KEEP_ALIVE_SECONDS = 300;

    /** Response created status code. */
    public static final int RESPONSE_CREATED_STATUS_CODE = 201;

    /** Response OK status code. */
    public static final int RESPONSE_OK_STATUS_CODE = 200;

    /** The timeout milliseconds. */
    public static final int TIMEOUT_MILIS = 60 * 1000;

    /** Application context. */
    private final Context context;

    /** The device id. */
    private String deviceID;

    /** The HTTP client. */
    private DefaultHttpClient httpClient;

    /** The language code. */
    private String language = MiBancoConstants.ENGLISH_LANGUAGE_CODE;

    /** The sync object. */
    private Object syncObject;

    /** The web service URL. */
    private String webServiceUrl;

    /** The persistent cookie store. */
    private PersistentCookieStore cookieStore;

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

        getNewHttpClient();
    }

    private void getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new AllAcceptingSSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            final HttpParams myParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(myParams, TIMEOUT_MILIS);
            HttpConnectionParams.setSoTimeout(myParams, TIMEOUT_MILIS);
            HttpConnectionParams.setStaleCheckingEnabled(myParams, true);

            syncObject = new Object();

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(myParams, registry);

            cookieStore = new PersistentCookieStore(context);
            cookieStore.excludeFromPersisting("sessionError");
            cookieStore.excludeFromPersisting("JSESSIONID");
            cookieStore.excludeFromPersisting("BIGipServeriphone_app_pool");
            // Two parameter constructor for quality
            httpClient = new DefaultHttpClient(ccm, myParams) {
                @Override
                protected CookieStore createCookieStore() {
                    return cookieStore;
                }
            };

            // set keep-alive strategy to avoid "SSL broken pipe error"
            httpClient.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {

                @Override
                public long getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
                    // seconds
                    return KEEP_ALIVE_SECONDS;
                }
            });

        } catch (Exception e) {
            httpClient = new DefaultHttpClient();
        }
    }

    public PersistentCookieStore getCookieStore() {
        return cookieStore;
    }

    /**
     * Do GET request.
     * 
     * @param url the web service method URL
     * @return the response body String
     * @throws Exception the exception
     */
    public String doGet(final String url) throws Exception {
        HttpResponse response;
        String ret = null;
        synchronized (syncObject) {
            final HttpContext localContext = new BasicHttpContext();
            final HttpGet httpGet = new HttpGet(webServiceUrl + url);

            httpGet.addHeader(new BasicHeader("User-Agent", MiBancoEnviromentConstants.USER_AGENT_STRING));
            httpGet.addHeader(new BasicHeader("Accept", "application/json"));
            httpGet.addHeader(new BasicHeader("Accept-Language", language));
            httpGet.addHeader(new BasicHeader("X-UDID", deviceID));
            httpGet.addHeader(new BasicHeader("Connection", "keep-alive"));

            response = httpClient.execute(httpGet, localContext);
            final HttpEntity entity = response.getEntity();

            if (entity != null) {
                ret = EntityUtils.toString(entity, "UTF-8");

                if (response.getStatusLine() != null) {
                    if (context.getString(R.string.log_network_calls).equals("true")) {
                        Log.d("SyncRestClient", url + " GET: " + response.getStatusLine().getStatusCode());
                    }

                    if (response.getStatusLine().getStatusCode() == RESPONSE_CREATED_STATUS_CODE || response.getStatusLine().getStatusCode() == RESPONSE_OK_STATUS_CODE) {
                        return preProcessResponse(ret);
                    } else {
                        throw new Exception("SyncRestClient GET request error, response status = " + response.getStatusLine().getStatusCode());
                    }
                } else {
                    throw new Exception("SyncRestClient GET request error - empty status line.");
                }
            } else {
                throw new Exception("SyncRestClient GET request error");
            }
        }
    }

    /**
     * Do GET request.
     * 
     * @param url the web service method URL
     * @param params the request parameters dictionary
     * @return the response body String
     * @throws Exception the exception
     */
    public String doGet(final String url, final HashMap<String, Object> params) throws Exception {
        final String p = parseParams(params);
        String tempUrl = url;
        if (p != null && p.length() > 0) {
            tempUrl += "?" + p;
        }
        return doGet(tempUrl);
    }

    /**
     * Do POST request.
     * 
     * @param url the web service method URL
     * @param params the request parameters dictionary
     * @return the response body String
     * @throws Exception the exception
     */
    public String doPost(final String url, final HashMap<String, Object> params) throws Exception {
        return doPost(url, parseParams(params));
    }

    /**
     * Do POST request.
     * 
     * @param url the web service method URL
     * @param body the request body
     * @return the response body String
     * @throws Exception the exception
     */
    private String doPost(final String url, final String body) throws Exception {
        HttpResponse response;
        String ret = null;
        synchronized (syncObject) {
            final HttpContext localContext = new BasicHttpContext();
            final HttpPost httpPost = new HttpPost(webServiceUrl + url);

            if (body != null) {
                final StringEntity se = new StringEntity(body, HTTP.UTF_8);
                se.setContentType("application/x-www-form-urlencoded; charset=utf-8");
                httpPost.setEntity(se);
            }

            httpPost.addHeader(new BasicHeader("User-Agent", "Android App; MiBanco.app; JSON Client; en-us"));
            httpPost.addHeader(new BasicHeader("Accept", "application/json"));
            httpPost.addHeader(new BasicHeader("Accept-Language", language));
            httpPost.addHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"));
            httpPost.addHeader(new BasicHeader("X-UDID", deviceID));
            httpPost.addHeader(new BasicHeader("Connection", "keep-alive"));

            response = httpClient.execute(httpPost, localContext);
            final HttpEntity entity = response.getEntity();

            if (entity != null) {
                ret = EntityUtils.toString(entity);

                if (response.getStatusLine() != null) {
                    if (context.getString(R.string.log_network_calls).equals("true")) {
                        Log.d("SyncRestClient", url + " POST: " + response.getStatusLine().getStatusCode());
                    }
                    if (response.getStatusLine().getStatusCode() == RESPONSE_CREATED_STATUS_CODE || response.getStatusLine().getStatusCode() == RESPONSE_OK_STATUS_CODE) {
                        return preProcessResponse(ret);
                    } else {
                        throw new Exception("SyncRestClient POST request error, response status = " + response.getStatusLine().getStatusCode());
                    }
                } else {
                    throw new Exception("SyncRestClient POST request error - empty status line.");
                }
            } else {
                throw new Exception("SyncRestClient POST request error");
            }
        }
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
