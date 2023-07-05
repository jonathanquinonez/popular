package com.popular.android.mibanco.activity;

        import static org.mockito.Matchers.any;
        import static org.mockito.Mockito.doNothing;
        import static org.mockito.Mockito.verify;
        import static org.powermock.api.mockito.PowerMockito.mock;
        import static org.powermock.api.mockito.PowerMockito.mockStatic;
        import static org.powermock.api.mockito.PowerMockito.when;
        import static org.mockito.Mockito.times;

        import android.content.Context;
        import android.content.SharedPreferences;
        import android.content.res.Resources;
        import android.os.Bundle;

        import androidx.appcompat.app.AppCompatDelegate;
        import androidx.lifecycle.ReportFragment;

        import com.popular.android.mibanco.App;
        import com.popular.android.mibanco.util.Utils;
        import com.popular.android.mibanco.R;
        import android.content.Intent;


        import android.webkit.WebSettings;
        import android.webkit.WebView;
        import android.widget.Button;


        import org.junit.Before;
        import org.junit.Ignore;
        import org.junit.Test;
        import org.junit.runner.RunWith;

        import org.mockito.InjectMocks;
        import org.mockito.Mock;

        import org.mockito.MockitoAnnotations;

        import org.powermock.api.mockito.PowerMockito;
        import org.powermock.core.classloader.annotations.PrepareForTest;
        import org.powermock.modules.junit4.PowerMockRunner;

        import java.util.Locale;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class, App.class, Bundle.class, ReportFragment.class, R.class, Utils.class})
public class EnrollmentLiteTermsAndConditionsUT
{
    @Mock
    private App mockApp;
    @Mock
    private Context context;
    @Mock
    private Bundle savedInstanceState;
    @Mock
    private Resources resources;
    @Mock
    private SharedPreferences sharedPreferences;
    @Mock
    private AppCompatDelegate appDelegate;
    @Mock
    private Intent intent;
    @Mock
    private WebView webViewTerms;
    @Mock
    private WebSettings webSettings;
    @Mock
    private Button button;

    @InjectMocks
    private EnrollmentLiteTermsAndConditions controller;
    @Before
    public void setup() throws Exception
    {

        MockitoAnnotations.initMocks(this);
        mockStatic(App.class);
        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(ReportFragment.class);
        PowerMockito.mockStatic(Utils.class);
        context = mock(Context.class);
        savedInstanceState = mock(Bundle.class);

        controller = PowerMockito.spy(new EnrollmentLiteTermsAndConditions());

        when(App.getApplicationInstance()).thenReturn(mockApp);
        when(mockApp.getBaseContext()).thenReturn(context);
        PowerMockito.when(Utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.doReturn(appDelegate).when(controller).getDelegate();
        when(controller.getIntent()).thenReturn(intent);

    }

    @Ignore
    @Test
    public void WhenCallOnCreate_GivenSavedInstanceStateNotNullAndLanguageIsEs_ThenCallOneTime() throws Exception
    {
        String[] urlAllowList = new String[1];
        urlAllowList[0] = "documents.popular.com";


        when(mockApp.getLanguage()).thenReturn("es");
        when(controller.findViewById(R.id.webViewTerms)).thenReturn(webViewTerms);
        when(webViewTerms.getSettings()).thenReturn(webSettings);
        when(controller.findViewById(R.id.btnAgree)).thenReturn(button);
        when(controller.findViewById(R.id.btnDisagree)).thenReturn(button);
        when(controller.getApplicationContext()).thenReturn(context);
        when(context.getResources().getStringArray(R.array.allowed_host)).thenReturn(urlAllowList);
        when(Utils.getLocaleStringResource(any(Locale.class), any(Integer.class), any(Context.class))).thenReturn("https://documents.popular.com/terms/mi_banco/mb_tc_alertas.html#app");
        doNothing().when(controller).setContentView(R.layout.activity_enrollment_lite_terms_and_conditions);

        controller.onCreate(savedInstanceState);
        verify(controller, times(1)).onCreate(savedInstanceState);
    }

    @Ignore
    @Test
    public void WhenCallOnCreate_GivenSavedInstanceStateAdnUrlAllowListEmpty_ThenCallOneTime() throws Exception
    {
        String[] urlAllowList = new String[1];

        when(mockApp.getLanguage()).thenReturn("es");
        when(controller.findViewById(R.id.webViewTerms)).thenReturn(webViewTerms);
        when(webViewTerms.getSettings()).thenReturn(webSettings);
        when(controller.findViewById(R.id.btnAgree)).thenReturn(button);
        when(controller.findViewById(R.id.btnDisagree)).thenReturn(button);
        when(controller.getApplicationContext()).thenReturn(context);
        when(context.getResources().getStringArray(R.array.allowed_host)).thenReturn(urlAllowList);
        doNothing().when(controller).setContentView(R.layout.activity_enrollment_lite_terms_and_conditions);
        when(Utils.getLocaleStringResource(any(Locale.class), any(Integer.class), any(Context.class))).thenReturn("https://documents.popular.com/terms/mi_banco/mb_tc_alerts.html#app");

        controller.onCreate(savedInstanceState);
        verify(controller, times(1)).onCreate(savedInstanceState);
    }

    @Ignore
    @Test
    public void WhenCallOnCreate_GivenSavedInstanceStateNotNullAndLanguageIsEn_ThenCallOneTime() throws Exception
    {
        String[] urlAllowList = new String[1];


        when(mockApp.getLanguage()).thenReturn("en");
        when(controller.findViewById(R.id.webViewTerms)).thenReturn(webViewTerms);
        when(webViewTerms.getSettings()).thenReturn(webSettings);
        when(controller.findViewById(R.id.btnAgree)).thenReturn(button);
        when(controller.findViewById(R.id.btnDisagree)).thenReturn(button);
        when(controller.getApplicationContext()).thenReturn(context);
        when(context.getResources().getStringArray(R.array.allowed_host)).thenReturn(urlAllowList);
        doNothing().when(controller).setContentView(R.layout.activity_enrollment_lite_terms_and_conditions);
        when(Utils.getLocaleStringResource(any(Locale.class), any(Integer.class), any(Context.class))).thenReturn("https://documents.popular.com/terms/mi_banco/mb_tc_alerts.html#app");

        controller.onCreate(savedInstanceState);
        verify(controller, times(1)).onCreate(savedInstanceState);
    }

}

