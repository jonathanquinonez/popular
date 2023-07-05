package com.popular.android.mibanco.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.listener.AsyncTaskListener;
import com.popular.android.mibanco.model.MarketPlaceEnum;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.KiuwanUtils;
import com.popular.android.mibanco.util.MarketplaceUtils;
import com.popular.android.mibanco.util.OtpUtils;
import com.popular.android.mibanco.util.PushUtils;
import com.popular.android.mibanco.util.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Activity that manages Webviews displayed
 */
@SuppressLint("SetJavaScriptEnabled")
public class WebViewActivity extends BaseActivity implements OtpUtils.ContinueButtonCallback,
        OtpUtils.ResendCodeButtonCallback, OtpUtils.CancelButtonCallback,MarketplaceUtils.ActionGetUnicaUrlCallback, AsyncTaskListener {

    private static final long COOKIE_PROCESSING_MILLIS = 1000;

    private WebView webView;
    private ProgressBar progressBar;
    private String requestedUrl;
    private boolean startOver;
    private boolean reLogin;
    private boolean hideNavigation;
    private boolean toolBarHide;
    private boolean backActionDisabled;
    private boolean closeFromCore;
    private String[] protectedUrlList;
    private String[] protectedUrlPatternList;
    private String[] urlBlacklist;
    private String[] externalUrls;
    private boolean syncCookies;
    private String[] pdfMbdp;
    private boolean progressBarHide;
    private boolean firstLoad;
    private boolean isAlerts;
    private boolean webViewCanBack; //Allow Web view to use back inside pages if available
    private boolean isMarketplace; // Identify if Web view is from Marketplace
    private boolean isRequestDocuments; // Identify if Web view is from Request Documents

    private Button btnAcceptTerms;
    private RelativeLayout termsButtonLayout;
    private Boolean isRedirectedToUnica = false;
    private Boolean disableBackButtonFromMarketplace = false;
    private ImageView btnBackFromActionBar;
    private MenuItem btnCloseFromActionBar;

    private boolean isCompleteFirstLoadingPage = false;

    private static final String EDIT_EMAIL_PATH = "mobileChangeEmail";//UriPath for Edit email webview
    private static final String COOKIES_PREFERENCE_SETTINGS_PATH = "cookiePreference";//UriPath for Cookies preference settings
    private boolean hideMenuOptions; //Hide settings menu options

    Context context;
    private boolean bottomNavigationBar;


    private boolean useDesktopUseAgent;
    private BottomNavigationView bottomNavigationView;

    private boolean isInterruptionPage = false;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_layout);
        getIntentExtras(getIntent());
        context = this;

        bottomNavigationView = KiuwanUtils.checkBeforeCast(BottomNavigationView.class, findViewById(R.id.bottom_navigation));

        termsButtonLayout = findViewById(R.id.termsButtonLayout);
        btnAcceptTerms = findViewById(R.id.btnAcceptTerms);

        if(!bottomNavigationBar){
            bottomNavigationView.setVisibility(View.GONE);
        } else {
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_goBack:
                            if (webView.canGoBack()) {
                                webView.goBack();
                            }
                            break;
                        case R.id.action_goForward:
                            if (webView.canGoForward()) {
                                webView.goForward();
                            }
                            break;
                        default:break;
                    }
                    return true;
                }
            });
        }
        setWebView();
        setUrlAction();
    }

    private void setUrlAction(){
        if (syncCookies) {
            new LoadUrlTask().execute();
        }else {
            if(!requestedUrl.contains("javascript") && Utils.isValidUrl(requestedUrl, getApplicationContext())) {
                webView.loadUrl(requestedUrl);
            }else{
                webView.goBack();
            }
        }
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        getIntentExtras(intent);
        if (syncCookies) {
            new LoadUrlTask().execute();
        } else {
            if(!requestedUrl.contains("javascript") && Utils.isValidUrl(requestedUrl, getApplicationContext())) {
                webView.loadUrl(requestedUrl);
            }else{
                webView.goBack();
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (disableBackButtonFromMarketplace) {
            return;
        }

        if (webViewCanBack) {
            if (webView.canGoBack()) {
                webView.goBack();
                return;
            } else {
                super.onBackPressed();
            }
        }

        if (!backActionDisabled) {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
        //Handle device back button in Alerts
        if (isAlerts && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(PushUtils.getAlertsBackButtonJS(), null);
        }

        validateInterruptionPageBackButton();
    }

    protected void validateInterruptionPageBackButton () {
        if(useDesktopUseAgent){
            final Intent intent = new Intent(this, Accounts.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(intent);
        } else {
            if (isInterruptionPage) {
                webView.loadUrl("javascript:backPressed()");
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Added manage when its needed a login after an activity
        //Code included for manage a bottom that launch a Dial activity but it required relogin when user resume on Mi Banco
        if (closeFromCore && reLogin) {
            setResult(RESULT_OK);
            finish();
            App.getApplicationInstance().reLogin(WebViewActivity.this);
        }
    }



    private void getIntentExtras(Intent intent) {
        requestedUrl = intent.getStringExtra(MiBancoConstants.WEB_VIEW_URL_KEY);
        boolean enrollment = intent.getBooleanExtra(MiBancoConstants.WEB_VIEW_ENROLLMENT_REQUEST_KEY, false);
        startOver = intent.hasExtra(MiBancoConstants.WEB_VIEW_START_OVER_KEY);
        hideNavigation = intent.hasExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY);
        protectedUrlList = intent.getStringArrayExtra(MiBancoConstants.WEB_VIEW_PROTECT_URL_LIST_KEY);
        syncCookies = intent.hasExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY);
        toolBarHide = intent.hasExtra(MiBancoConstants.WEB_VIEW_TOOLBAR_HIDE_KEY);
        backActionDisabled = intent.hasExtra(MiBancoConstants.WEB_VIEW_BACKACTION_DISABLED_KEY);
        closeFromCore = intent.hasExtra(MiBancoConstants.WEB_VIEW_CLOSEACTION_KEY);
        externalUrls = intent.getStringArrayExtra(MiBancoConstants.WEB_VIEW_URL_EXTERNAL_KEY);
        progressBarHide = intent.hasExtra(MiBancoConstants.WEB_VIEW_PROGRESSBAR_HIDE_KEY);
        isAlerts = intent.hasExtra(MiBancoConstants.WEB_VIEW_ONSEN_ALERTS);
        webViewCanBack = intent.hasExtra(MiBancoConstants.WEB_VIEW_CAN_BACK);
        isMarketplace = intent.hasExtra(MiBancoConstants.WEB_VIEW_MARKETPLACE);
        isRequestDocuments = intent.hasExtra(MiBancoConstants.WEB_VIEW_REQUEST_DOCUMENTS);
        hideMenuOptions = intent.hasExtra(MiBancoConstants.WEB_VIEW_HIDE_RIGHT_MENU);
        useDesktopUseAgent = intent.hasExtra(MiBancoConstants.DESKTOP_USER_AGENT);
        bottomNavigationBar = intent.hasExtra(MiBancoConstants.SHOW_BOTTOM_NAVIGATION_BAR);

        if (protectedUrlList != null) {
            for (int i = 0; i < protectedUrlList.length; ++i) {
                protectedUrlList[i] = Utils.stripUrlQueryParameters(protectedUrlList[i]);
            }
        }
        protectedUrlPatternList = intent.getStringArrayExtra(MiBancoConstants.WEB_VIEW_PROTECT_URL_PATTERN_LIST_KEY);
        urlBlacklist = intent.getStringArrayExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY);
        pdfMbdp = intent.getStringArrayExtra(MiBancoConstants.WEB_VIEW_URL_MBDP);
    }

    private boolean allowUrl(String url) {
        if (protectedUrlList == null && protectedUrlPatternList == null && urlBlacklist == null) {
            return true;
        }
        //The follow code is only happend when a functionality like outreach interruption is loading from a webview
        //Indeed to finish correctly the activity, we search for close word in url.
        if (closeFromCore && url.contains("close")) return false;

        if (url.contains(MiBancoConstants.MAKE_PAYMENT)) {
            App.getApplicationInstance().setUpdatingBalances(true);
            App.getApplicationInstance().setReloadPayments(true);
        }else
        if (url.contains(MiBancoConstants.MAKE_TRANSFER)) {
            App.getApplicationInstance().setUpdatingBalances(true);
            App.getApplicationInstance().setReloadTransfers(true);
        }

        if (FeatureFlags.MBSD_1876() && closeFromCore && url.contains("_reLogin_")) {
            reLogin = true;
        }


        final String strippedUrl = Utils.stripUrlQueryParameters(url);

        if (urlBlacklist != null) {
            if(!useDesktopUseAgent) {
                for (String blacklistedUrl : urlBlacklist) {
                    if (strippedUrl.matches(blacklistedUrl)) {
                        return false;
                    }
                }
            }

            return true;
        }

        if (protectedUrlList != null) {
            for (String protectedUrl : protectedUrlList) {
                if (strippedUrl.equalsIgnoreCase(protectedUrl)) {
                    return true;
                }
            }
        }

        if (protectedUrlPatternList != null) {
            for (String protectedUrlPattern : protectedUrlPatternList) {
                if (strippedUrl.matches(protectedUrlPattern)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_navigation_back:
                if (webView.canGoBack()) {
                    webView.goBack();
                }
                break;
            case R.id.menu_navigation_forward:
                if (webView.canGoForward()) {
                    webView.goForward();
                }
                break;
            case R.id.menu_navigation_refresh:
                webView.reload();
                break;
            case R.id.menu_close:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.menu_logout).setVisible(false);

        if (!hideNavigation) {
            menu.findItem(R.id.menu_navigation_back).setVisible(true);
            menu.findItem(R.id.menu_navigation_forward).setVisible(true);
            menu.findItem(R.id.menu_navigation_refresh).setVisible(true);
        }

        if (isMarketplace) {
            marketplaceMenu(menu);
            btnBackFromActionBar = findViewById(R.id.up);
            btnCloseFromActionBar = menu.findItem(R.id.menu_close);
        }

        if  (isRequestDocuments) {
            setRequestDocumentsMenu(menu);
            btnCloseFromActionBar = menu.findItem(R.id.menu_close);
        }

        if (hideMenuOptions){
            menu.findItem(R.id.menu_settings).setVisible(false);
            menu.findItem(R.id.menu_logout).setVisible(false);
            menu.findItem(R.id.menu_locator).setVisible(false);
            menu.findItem(R.id.menu_contact).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void setWebView() {
        progressBar = (ProgressBar) findViewById(R.id.progres_bar);
        webView = (WebView) findViewById(R.id.web_view);

        if(!useDesktopUseAgent) {
            webView.getSettings().setUserAgentString(MiBancoEnviromentConstants.USER_AGENT_WEBVIEW_STR + " WebView");
        }else {
            webView.getSettings().setUserAgentString(MiBancoEnviromentConstants.AGENT_USER_DESKTOP_STR);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            webView.getSettings().setSupportZoom(true);
        }

        if(toolBarHide) {this.getSupportActionBar().hide(); }
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(final WebView view, final int progress) {
                if(!progressBarHide) progressBar.setVisibility(progress == 100 ? View.GONE : View.VISIBLE);
                if(progressBarHide && firstLoad) progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCloseWindow(WebView w) {
                //Added event WindowClose.
                setResult(RESULT_OK);
                super.onCloseWindow(w);
                finish();
            }
        });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {

                isInterruptionPage = url.contains("actions/loginInterruptionPage");

                if (getString(R.string.log_network_calls).equals("true")) {
                    Log.d("WebViewActivity", url);
                }

                if (isMarketplace) {
                    handleMarketplaceUri(url);
                }
                
                if(useDesktopUseAgent){
                    if(url.contains(getString(R.string.actionsLogin)) || url.contains("endSession")){
                        setResult(RESULT_OK);
                        finish();
                        App.getApplicationInstance().reLogin(WebViewActivity.this);
                    }
                    bottomNavigationView.getMenu().findItem(R.id.action_goForward).setEnabled(webView.canGoForward());
                    bottomNavigationView.getMenu().findItem(R.id.action_goBack).setEnabled(webView.canGoBack());
                }

                if (!allowUrl(url)) {
                    setResult(RESULT_OK);
                    finish();
                    if (startOver) {
                        App.getApplicationInstance().reLogin(WebViewActivity.this);
                    }
                    return;
                }
                view.clearView();
                view.invalidate();
            }

            @Override
            public void onPageFinished (final WebView view, String url) {
                if(firstLoad) firstLoad = false;
                if(progressBarHide && !firstLoad) progressBar.setVisibility(View.GONE);
                if (useDesktopUseAgent && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript(String.format("document.querySelector('meta[name=viewport]').setAttribute('content', 'width=%d;', false);", webView.getWidth()), null);
                }




                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.cancel();
//                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                final Uri uri = Uri.parse(url);
                return webViewLinksInterceptor(uri) || handleUri(uri, view);
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                final Uri uri = request.getUrl();
                return webViewLinksInterceptor(uri) || handleUri(uri, view);
            }

            /**
             * Method intercepts if webview contains a link with mailto: or tel:
             * and Starts corresponding activity
             * @param uri
             * @return isIntercepted
             */
            private boolean webViewLinksInterceptor(Uri uri) {
                boolean isIntercepted = false;

                if (uri.toString().startsWith("mailto:")) {
                    isIntercepted = true;
                    startActivity(new Intent(Intent.ACTION_SENDTO, uri));
                } else if (uri.toString().startsWith("tel:")) {
                    isIntercepted = true;
                    startActivity(new Intent(Intent.ACTION_DIAL, uri));
                }

                return isIntercepted;
            }

            /*
                Method for manage the url loaded from webview if it have a protocol to handle
                or need to handle other action when a url is loaded into the webview.
             */
            private boolean handleUri(final Uri uri, WebView view) {

                final String scheme = uri.getScheme();
                final String url = uri.toString();
                // Based on some condition you need to determine if you are going to load the url
                // in your web view itself or in a browser.

                if (FeatureFlags.MBSD_1878() && scheme != null && scheme.equalsIgnoreCase("tel")) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, uri);
                    startActivity(dialIntent);
                    //view.reload();
                    return true;
                } else if (FeatureFlags.MBSD_1878() && isExternalUrl(url)) {
                    final Intent brwsIntent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(brwsIntent);
                    return true;
                    //verify if this ifs of webviews can put on Map with check function and path.
                } else if ((FeatureFlags.ADD_PAYEES() && uri.getPath().contains("mobileAddPayee")) ||
                        (FeatureFlags.ADD_PAYEES() && uri.getPath().contains("mobileEditPayee")) ||
                        uri.getPath().contains("mobileAlerts") ||
                        (FeatureFlags.RSA_ENROLLMENT() && uri.getPath().contains("rsaEnroll")) ||
                        (FeatureFlags.NOTIFICATION_CENTER() && uri.getPath().contains("notificationCenter")) ||
                        (uri.getPath().contains("rsaEditQuestions")) ||
                        (uri.getPath().contains(EDIT_EMAIL_PATH)) ||
                        (uri.getPath().contains(COOKIES_PREFERENCE_SETTINGS_PATH))) {
                    if(uri.getQuery() != null) {
                        if (uri.getQuery().equalsIgnoreCase("refresh&close")) {
                            setResult(RESULT_OK);
                            finish();
                            return true;
                        } else if (uri.getQuery().equalsIgnoreCase("back") || uri.getQuery().equalsIgnoreCase("close")) {
                            setResult(RESULT_CANCELED);
                            finish();
                            return true;
                        } else if (uri.getQuery().equalsIgnoreCase("block")) {
                            Intent rsa_response = new Intent();
                            rsa_response.putExtra(MiBancoConstants.RSA_BLOCKED, true);
                            setResult(RESULT_CANCELED, rsa_response);
                            finish();
                            return false;
                       /* } else if (uri.getQuery().equalsIgnoreCase(MiBancoConstants.RSA_OOB_ENROLLED)) {
                            Intent data = new Intent();
                            data.putExtra(MiBancoConstants.RSA_OOB_ENROLLED, true);
                            setResult(RESULT_OK, data);
                            finish();
                            return true;*/
                        } else if (uri.getQuery().equalsIgnoreCase(MiBancoConstants.RSA_QUESTIONS)) {
                            Intent data = new Intent();
                            data.putExtra(MiBancoConstants.RSA_OOB_ENROLLED, false);
                            setResult(RESULT_OK, data);
                            finish();
                            return true;
                        } else if (uri.getQuery().equalsIgnoreCase("endSession")) {
                            setResult(RESULT_OK);
                            App.getApplicationInstance().reLogin(WebViewActivity.this);
                            finish();
                            return true;
                        } else {
                            return false;
                        }
                    } else { return false;}
                } else if (url.contains("exitenrollsec=true")) {
                    App.getApplicationInstance().reLogin(WebViewActivity.this);
                }

                if (url.contains("_reLogin_")) {
                    setResult(RESULT_CANCELED);
                    App.getApplicationInstance().reLogin(WebViewActivity.this);
                    finish();
                }

                if (pdfMbdp != null) {
                    for (String utlToPlugin : pdfMbdp) {
                        if (url.contains(utlToPlugin)) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(browserIntent);
                            return true;
                        }
                    }
                }
                if(!url.contains("javascript") && Utils.isValidUrl(url, getApplicationContext())) {
                    view.loadUrl(url);
                }else{
                    view.goBack();
                }

                if (url.contains("login?firstTime=false")) {
                    final Intent intentForgotUsername = new Intent(view.getContext(), EnterUsername.class);
                    startActivity(intentForgotUsername);
                }

                return true;
            }

        });

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.clearHistory();
        webView.clearCache(true);
        clearCookies(this);
        webView.getSettings().setJavaScriptEnabled(true);
    }

    private void hideWebView() {
        webView.setVisibility(View.INVISIBLE);
    }

    @SuppressWarnings("deprecation")
    public void clearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            //Log.d(C.TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            //Log.d(C.TAG, "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    private void syncCookies() {
        List<Cookie> cookies = App.getApplicationInstance().getApiClient().getSyncRestClient().getCookieManager().loadForRequest(HttpUrl.get(requestedUrl));
        if (!cookies.isEmpty()) {
            CookieSyncManager.createInstance(WebViewActivity.this);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);

            for (Cookie cookie : cookies) {
                String cookieString = String.format(MiBancoEnviromentConstants.COOKIE_STRING, cookie.name(), cookie.value(), cookie.domain(), cookie.path());

                cookieManager.setCookie(cookie.domain(), cookieString);
                CookieSyncManager.getInstance().sync();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.flush();
            }
        }
    }

    private class LoadUrlTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            CookieSyncManager.createInstance(WebViewActivity.this);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeSessionCookie();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SystemClock.sleep(COOKIE_PROCESSING_MILLIS);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            syncCookies();
            if(!requestedUrl.contains("javascript") && Utils.isValidUrl(requestedUrl, getApplicationContext())) {
                webView.loadUrl(requestedUrl);
            }else{
                webView.goBack();
            }
        }
    }

    private boolean isExternalUrl(String url) {
        if (externalUrls != null) {
            for (String externalUrl : externalUrls) {
                if (url.contains(externalUrl)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Setups Toolbar for Marketplace SupportActionBar menu with no settings option
     *
     * @param menu to turn off some toolbar options
     */
    private void marketplaceMenu(Menu menu) {
        menu.findItem(R.id.menu_close).setVisible(true);
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(false);
        menu.findItem(R.id.menu_contact).setVisible(false);
        menu.findItem(R.id.menu_contact).setVisible(false);
    }

    /**
     * Setups Toolbar for Request Documents SupportActionBar menu with no settings option
     *
     * @param menu to turn off some toolbar options
     */
    private Menu setRequestDocumentsMenu(Menu menu) {
        menu.findItem(R.id.menu_close).setVisible(true);
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(false);
        menu.findItem(R.id.menu_contact).setVisible(false);

        return menu;
    }

    /**
     * handleMarketplaceUri
     * Handle Marketplace Activity
     * @param url to check Marketplace Stage
     */
    private void handleMarketplaceUri(String url) {
        Uri uri = Uri.parse(url);
        if (uri != null && uri.getQuery() != null) {
            if (uri.getQueryParameter("close") != null) {
                final Intent intent = new Intent(context, Accounts.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else if (uri.toString().contains("referrer=")) {
                String referrer = uri.getQueryParameter("referrer");
                if(StringUtils.isBlank(referrer) && uri.getQueryParameter("url") != null) {
                    Uri uril = Uri.parse(uri.getQueryParameter("url"));
                    referrer = uril.getQueryParameter("referrer");
                }

                if (!isRedirectedToUnica) {
                    btnAcceptTerms.setEnabled(false);
                    showTermsButtonLayout();
                    final Runnable runnableEnableTerms = new Runnable() {
                        public void run() {
                            enableTermsButton();
                        }
                    };
                    webViewAddOnScrollListener(0.8, runnableEnableTerms);
                    setTermsButtonOnClickListener(referrer);
                }
                else {
                    hideTermsButtonLayout();
                }
            }
            else {
                hideTermsButtonLayout();
            }
        } else {
            hideTermsButtonLayout();
        }
    }

    /**
     * webViewAddOnScrollListener
     * This method identifies how much Web View Page has Scrolled
     * @param pagePercent The Web view scrolled percent you want to run method on
     * @param func to run an optional Function
     */
    @SuppressLint("ClickableViewAccessibility")
    private void webViewAddOnScrollListener(final double pagePercent, final Runnable func) {

        webView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                double percent = pagePercent != 0.0 ? pagePercent : 0.9;//Page Scrolled Cut off Percent
                double viewSpace = Math.floor(((webView.getContentHeight() * getResources().getDisplayMetrics().density) - webView.getHeight()) * percent);//View space times percent
                int webViewScrolled = webView.getScrollY();
                if (viewSpace < webViewScrolled) {
                    //Bottom or Percent Reached
                    if(func != null) {
                        func.run();
                    }
                }
            }
        });
    }

    /**
     * setTermsButtonListener
     * This method sets AcceptTerms Button Listener
     * @param referrer
     */
    private void setTermsButtonOnClickListener(final String referrer) {
        btnAcceptTerms.setOnClickListener( new View.OnClickListener() {
            public void onClick(final View v) {
                MarketplaceUtils.sendAcceptTermsMarketplaceWithProduct(context, referrer);
            }
        });
    }

    /**
     * Method Enables Marketplace Terms and Condition Native Button
     */
    private void enableTermsButton() {
        if (termsButtonLayout.getVisibility() == View.VISIBLE) {
            btnAcceptTerms.setEnabled(true);
        }
    }

    /**
     * Method Shows Marketplace Terms and Condition Native Button
     */
    private void showTermsButtonLayout() {
        termsButtonLayout.setVisibility(View.VISIBLE);
        if(webView != null) {
            if (webView.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) webView.getLayoutParams(); //WebView Relative Layout

                params.addRule(RelativeLayout.ABOVE, R.id.termsButtonLayout);
                btnAcceptTerms.setText(getString(R.string.marketplace_terms_button));
            }
        }
    }

    /**
     * Method Hides Marketplace Terms and Condition Native Button
     */
    private void hideTermsButtonLayout() {
        termsButtonLayout.setVisibility(View.GONE);
        if(webView != null){
            if (webView.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) webView.getLayoutParams(); //WebView Relative Layout

                params.removeRule(RelativeLayout.ABOVE);
            }
        }
    }

    @Override
    public void continueButtonAction(String code) {
        MarketplaceUtils.validateOtpCode(context, code);
    }

    @Override
    public void resendCodeButtonAction() {
        MarketplaceUtils.generateOtpCode(context, MarketPlaceEnum.resendCode.RESEND_CODE_TRUE.toString());
    }

    @Override
    public void cancelButtonAction() {
        webViewBackToStart();
    }

    /**
     * OOB onSuccess
     * @param result
     */
    @Override
    public void onSuccess(Object result) {

    }

    /**
     * OOB onError
     * @param error The Throwable, which caused a task to fail. May be null.
     * @return
     */
    @Override
    public boolean onError(Throwable error) {
        return false;
    }

    /**
     * OOB onCancelled return to Webview First page
     */
    @Override
    public void onCancelled() {
        webViewBackToStart();
    }

    private void webViewBackToStart() {
        WebBackForwardList URL = getHistoricalList(webView);
        for(int i = 0;i < URL.getSize() - 1; i++){
            if (webView.canGoBack()) {
                webView.goBack();
            }
        }
    }

    public static WebBackForwardList getHistoricalList(WebView webView) {
        return webView.copyBackForwardList();
    }

    @Override
    public void getUnicaUrlAction(String url, String authCode) {

        btnBackFromActionBar.setVisibility(View.GONE);
        btnCloseFromActionBar.setVisible(false);
        disableBackButtonFromMarketplace = true;
        isRedirectedToUnica = true;
        Map<String, String> extraHeaders = new HashMap<>();
        extraHeaders.put("Authorization","Bearer " +authCode);
        webView.loadUrl(url,extraHeaders);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BPAnalytics.onEndSession(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (isMarketplace && !isCompleteFirstLoadingPage) {
            BPAnalytics.onStartSession(this);
            BPAnalytics.logEvent(BPAnalytics.EVENT_CREDIT_CARD_SECTION);
            isCompleteFirstLoadingPage = true;
        }
    }
}
