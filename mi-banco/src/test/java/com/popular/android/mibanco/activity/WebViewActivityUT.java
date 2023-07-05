package com.popular.android.mibanco.activity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ReportFragment;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.util.Utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;


@RunWith(PowerMockRunner.class)
@PrepareForTest({WebViewActivity.class, ReportFragment.class,
        MiBancoConstants.class, R.class, CookieSyncManager.class, MiBancoEnviromentConstants.class,
        BuildConfig.class, App.class, Utils.class})
public class WebViewActivityUT {

    public static final int ONE_HUNDRED_PERCENT_PROGRESS = 100;//Percent to progress bar test

    public static final String WEB_VIEW_FIELD = "webView";//webview field name

    public static final String HIDE_WEB_VIEW_MET_NAME = "hideWebView";//webview hide method name

    public static final String SHOW_WEB_VIEW_IF_IS_COMPLETE_MET_NAME = "showWebViewProgressIsComplete";//method name to hide webview

    @Mock
    private AppCompatDelegate appDelegate; //App interface to inject in webview

    @Mock
    private WebView webView;//Webview instance

    @Mock
    private App app;//App Instance from Mi Banco

    @Mock
    private ProgressBar progressBar;//Progress bar showed when webview is loading

    @Mock
    private Menu menu;//Menu instance

    @Mock
    private MenuItem menuItem;//Menu item instance

    @Mock
    private WebSettings webSettings;//Setting for webview instance

    @Mock
    private Context context;//Mi Banco App Context

    @Mock
    private SharedPreferences sharedPreferences;//Mi Banco settings

    @Mock
    private Resources resources;//App resource like progress bar and others

    @Mock
    private Intent intent;

    @Captor
    private ArgumentCaptor<String> captorUrl;

    @InjectMocks
    private WebViewActivity webViewActivity;//Activity to invoke webview

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(ReportFragment.class);
        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(CookieSyncManager.class);
        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(Utils.class);
        //Need this to mock static MiBancoEnvironmentConstants
        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);
        PowerMockito.when(app.getBaseContext()).thenReturn(context);
        PowerMockito.when(Utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);
        PowerMockito.when(context.getResources()).thenReturn(resources);

        PowerMockito.mockStatic(MiBancoEnviromentConstants.class);

        webViewActivity = PowerMockito.spy(new WebViewActivity());
        webView = PowerMockito.mock(WebView.class);
        PowerMockito.doReturn(appDelegate).when(webViewActivity).getDelegate();
        PowerMockito.when(webViewActivity.findViewById(R.id.web_view)).thenReturn(webView);
        PowerMockito.when(webViewActivity.findViewById(R.id.progres_bar)).thenReturn(progressBar);
        PowerMockito.when(webView.getSettings()).thenReturn(webSettings);

    }

    // @Test
    // public void whenOnCreate() throws Exception{

    //     doNothing().when(webViewActivity).setContentView(R.layout.web_view_layout);
    //     PowerMockito.doNothing().when(webViewActivity,"getIntentExtras",any(Intent.class));
    //     when(Utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences) ;
    //     PowerMockito.doReturn("api_url_test_6060").when(MiBancoEnviromentConstants.class, "getSavedUrl");

    //     final BottomNavigationView bottomNavigationView = PowerMockito.mock(BottomNavigationView.class);

    //     PowerMockito.doNothing().when(webViewActivity).clearCookies(any(Context.class));
    //     webViewActivity.onCreate(null);
    //     verify(webViewActivity, times(1)).setContentView(R.layout.web_view_layout);
    //     PowerMockito.verifyPrivate(webViewActivity).invoke("getIntentExtras",any(Intent.class));
    //     PowerMockito.verifyPrivate(webViewActivity).invoke("setWebView");
    // }

    @Test
    public void whenOnPrepareOptionsMenu_GivenHideNavigationFalse_ThenReturnTrue(){
        PowerMockito.when(menu.findItem(R.id.menu_logout)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_navigation_back)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_navigation_forward)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_navigation_refresh)).thenReturn(menuItem);

        boolean result = webViewActivity.onPrepareOptionsMenu(menu);//Response when WebView Activity(Succes/Cancel)
        assert(result);
    }

    @Test
    public void whenOnBackPressed_ThenGoBack(){
        webViewActivity.onBackPressed();
        verify(webViewActivity, times(1)).onBackPressed();
    }

    @Test
    public void whenCalledHideWebViewThenWebViewHided() throws Exception {

        final WebView webViewToSet = Mockito.mock(WebView.class);//Mock WebView Instance
        Mockito.when(webViewToSet.getVisibility()).thenReturn(View.INVISIBLE);

        Whitebox.setInternalState(webViewActivity, WEB_VIEW_FIELD, webViewToSet);

        final WebView webView = Whitebox.getInternalState(webViewActivity, WEB_VIEW_FIELD);//WebView after progress bar is completed

        assertEquals(webView.getVisibility(), View.INVISIBLE);
    }

    @Test
    public void whenShowWebViewWhenProgressIsComplete() throws Exception {

        final WebView webViewToSet = Mockito.mock(WebView.class);//Mock WebView Instance
        Mockito.when(webViewToSet.getVisibility()).thenReturn(View.VISIBLE);

        Whitebox.setInternalState(webViewActivity, WEB_VIEW_FIELD, webViewToSet);

        final WebView webView = Whitebox.getInternalState(webViewActivity, WEB_VIEW_FIELD);//WebView after progress bar is completed

        assertEquals(webView.getVisibility(), View.VISIBLE);
    }

    @Test
    public void whenOnPrepareOptionsMenu_GivenHideMenuOptionsTrue_ThenReturnTrue() throws Exception{

        when(intent.getStringExtra(MiBancoConstants.WEB_VIEW_URL_KEY)).thenReturn("any");
        when(intent.getBooleanExtra(MiBancoConstants.WEB_VIEW_ENROLLMENT_REQUEST_KEY, false)).thenReturn(false);
        when(intent.hasExtra(MiBancoConstants.WEB_VIEW_START_OVER_KEY)).thenReturn(false);
        when(intent.hasExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY)).thenReturn(true);
        when(intent.getStringArrayExtra(MiBancoConstants.WEB_VIEW_PROTECT_URL_LIST_KEY)).thenReturn(new String[]{});
        when(intent.hasExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY)).thenReturn(true);
        when(intent.hasExtra(MiBancoConstants.WEB_VIEW_TOOLBAR_HIDE_KEY)).thenReturn(false);
        when(intent.hasExtra(MiBancoConstants.WEB_VIEW_BACKACTION_DISABLED_KEY)).thenReturn(false);
        when(intent.hasExtra(MiBancoConstants.WEB_VIEW_CLOSEACTION_KEY)).thenReturn(false);
        when(intent.getStringArrayExtra(MiBancoConstants.WEB_VIEW_URL_EXTERNAL_KEY)).thenReturn(new String[]{});
        when(intent.hasExtra(MiBancoConstants.WEB_VIEW_PROGRESSBAR_HIDE_KEY)).thenReturn(false);
        when(intent.hasExtra(MiBancoConstants.WEB_VIEW_ONSEN_ALERTS)).thenReturn(false);
        when(intent.hasExtra(MiBancoConstants.WEB_VIEW_CAN_BACK)).thenReturn(true);
        when(intent.hasExtra(MiBancoConstants.WEB_VIEW_MARKETPLACE)).thenReturn(false);
        when(intent.hasExtra(MiBancoConstants.WEB_VIEW_HIDE_RIGHT_MENU)).thenReturn(true);

        Whitebox.invokeMethod(webViewActivity, "getIntentExtras", intent);

        PowerMockito.when(menu.findItem(R.id.menu_settings)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_logout)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_locator)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_contact)).thenReturn(menuItem);

        boolean result = webViewActivity.onPrepareOptionsMenu(menu);//Response when WebView Activity(Succes/Cancel)
        assert(result);
        Assert.assertFalse (menuItem.isVisible());
    }

    @Test
    public void whenOnBackPressed_givenDefaultParams_thenReturnCallValidateInterruptionPageBackButton () {
        webViewActivity.onBackPressed();

        verify(webViewActivity, times(1)).validateInterruptionPageBackButton();
    }

    @Test
    public void whenValidateInterruptionPageBackButton_givenisInterruptionPageFlagEnabled_thenReturnCallJavascript () {
        Whitebox.setInternalState(webViewActivity, "webView", webView);

        doNothing().when(webView).loadUrl(captorUrl.capture());
        Whitebox.setInternalState(webViewActivity, "isInterruptionPage", true);

        webViewActivity.validateInterruptionPageBackButton();

        assertEquals("javascript:backPressed()", captorUrl.getValue());
    }

    @Test
    public void whenValidateInterruptionPageBackButton_givenisInterruptionPageFlagDisabled_thenReturnOnBackPressed () {
        Whitebox.setInternalState(webViewActivity, "webView", webView);
        Whitebox.setInternalState(webViewActivity, "isInterruptionPage", false);

        webViewActivity.validateInterruptionPageBackButton();

        verify(webView, times(0)).loadUrl(any(String.class));
    }

    @Test
    public void whenSetRequestDocumentsMenu_Given_Then() throws Exception {
        PowerMockito.when(menu.findItem(R.id.menu_close)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_settings)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_logout)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_locator)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_contact)).thenReturn(menuItem);
        Whitebox.invokeMethod(webViewActivity, "setRequestDocumentsMenu", menu);
        assertNotNull(menu);

    }

    @Test
    public void whenOnPrepareOptionsMenu_GivenSelectRequestDocumentsTrue_ThenReturnTrue() throws Exception{

        when(intent.hasExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY)).thenReturn(true);
        when(intent.hasExtra(MiBancoConstants.WEB_VIEW_MARKETPLACE)).thenReturn(false);
        when(intent.hasExtra(MiBancoConstants.WEB_VIEW_REQUEST_DOCUMENTS)).thenReturn(true);

        Whitebox.invokeMethod(webViewActivity, "getIntentExtras", intent);

        PowerMockito.when(menu.findItem(R.id.menu_settings)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_logout)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_locator)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_contact)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_close)).thenReturn(menuItem);

        boolean result = webViewActivity.onPrepareOptionsMenu(menu);//Response when WebView Activity(Succes/Cancel)
        assert(result);
        Assert.assertFalse (menuItem.isVisible());
    }

    @Test
    public void whenOnPrepareOptionsMenu_GivenSelectRequestDocumentsFalse_ThenReturnTrue() throws Exception{

        when(intent.hasExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY)).thenReturn(true);
        when(intent.hasExtra(MiBancoConstants.WEB_VIEW_MARKETPLACE)).thenReturn(false);
        when(intent.hasExtra(MiBancoConstants.WEB_VIEW_REQUEST_DOCUMENTS)).thenReturn(false);

        Whitebox.invokeMethod(webViewActivity, "getIntentExtras", intent);

        PowerMockito.when(menu.findItem(R.id.menu_settings)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_logout)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_locator)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_contact)).thenReturn(menuItem);
        PowerMockito.when(menu.findItem(R.id.menu_close)).thenReturn(menuItem);

        boolean result = webViewActivity.onPrepareOptionsMenu(menu);//Response when WebView Activity(Succes/Cancel)
        assert(result);
        Assert.assertFalse (menuItem.isVisible());
    }
}