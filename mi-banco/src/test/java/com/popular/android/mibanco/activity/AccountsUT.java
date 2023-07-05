package com.popular.android.mibanco.activity;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.when;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentController;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ReportFragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.fragment.AccountsFragment;
import com.popular.android.mibanco.model.Customer;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.CustomerEntitlements;
import com.popular.android.mibanco.model.GlobalStatus;
import com.popular.android.mibanco.model.PremiaInfo;
import com.popular.android.mibanco.object.ViewHolder;
import com.popular.android.mibanco.task.AsyncTasks;
import com.popular.android.mibanco.task.BaseAsyncTask;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.ws.response.RetirementPlanDetail;
import com.popular.android.mibanco.ws.response.RetirementPlanInfoResponse;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ReportFragment.class,
        MiBancoConstants.class, R.class, CookieSyncManager.class, MiBancoEnviromentConstants.class,
        BuildConfig.class, App.class, Utils.class, FeatureFlags.class, ContextCompat.class,AnimationUtils.class,
        BPAnalytics.class})
public class AccountsUT {

    @Mock
    private Customer cust;

    @Mock
    private LayoutInflater inflater;

    @Mock
    private App app;

    @Mock
    private AppCompatDelegate appDelegate;

    @Mock
    private Context context;

    @Mock
    private Resources resources;

    @Mock
    private SharedPreferences sharedPreferences;

    @Mock
    private View view;

    @Mock
    private Button retPlanNewAlertView;

    @Mock
    protected ImageView imageView;

    @Mock
    private Utils utils;

    @Mock
    private BottomSheetDialog bottomSheetDialog;

    @Mock
    private CustomerEntitlements customerEntitlements;

    @Mock
    private Configuration configuration;

    @Mock
    private FloatingActionButton floatingActionButton;

    @Mock
    private BaseAsyncTask baseAsyncTaskMock = PowerMockito.mock(BaseAsyncTask.class);

    @InjectMocks
    private AccountsFragment accounts;

    @Mock
    private Intent intent;

    @Mock
    private PremiaInfo premiaInfo;

    @Mock
    private AsyncTasks asyncTasks;

    @Mock
    private LinearLayout accountsContainer;

    @Mock
    private RelativeLayout premiaViewLayout;

    @Mock
    private Animation animation;

    @Mock
    private AnimationUtils animationUtils;

    @Mock
    private FragmentController fragmentController;

    @Mock
    private ConstraintLayout marketplaceBanner;

    @Mock
    private Context mockContext = PowerMockito.mock(Context.class);

    private final String SSOBOOL = "0"; // literal
    private final String NOUSER = "nouser";
    
    @Mock
    private  RetirementPlanInfoResponse newRetirementPlanInfoResponse;

    private LinkedList<RetirementPlanDetail> newRetirementPlanDetail = new LinkedList<RetirementPlanDetail>();

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
        PowerMockito.mockStatic(ContextCompat.class);
        PowerMockito.mockStatic(BPAnalytics.class);

        accounts = PowerMockito.spy(new AccountsFragment());

        //Need this to mock static MiBancoEnvironmentConstants
        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);
        PowerMockito.when(app.getBaseContext()).thenReturn(context);
        PowerMockito.when(Utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);
        PowerMockito.when(context.getResources()).thenReturn(resources);


        PowerMockito.mockStatic(MiBancoEnviromentConstants.class);
        PowerMockito.mockStatic(FeatureFlags.class);

        PowerMockito.doReturn(appDelegate).when(accounts).getDelegateCompat();

        cust = Mockito.mock(Customer.class);
        bottomSheetDialog = Mockito.mock(BottomSheetDialog.class);

        // Mock Accounts activity
        Accounts mockAccounts = mock(Accounts.class);
        when(mockAccounts.hasMarketplaceProducts()).thenReturn(false);

        // When getActivity() is called on the fragment, return the mock Accounts activity
        when(accounts.getActivity()).thenReturn(mockAccounts);

        when(app.isSessionNeeded()).thenReturn(false);
        Whitebox.setInternalState(accounts, "mFragments", fragmentController);
        when(app.isUpdatingBalances()).thenReturn(false);
        doNothing().when(accounts).updateDataSource();

        // Mock FragmentManager and FragmentTransaction
        FragmentManager mockFragmentManager = mock(FragmentManager.class);
        FragmentTransaction mockFragmentTransaction = mock(FragmentTransaction.class);

        when(mockAccounts.getSupportFragmentManager()).thenReturn(mockFragmentManager);
        when(mockFragmentManager.beginTransaction()).thenReturn(mockFragmentTransaction);

        // Add fragment to the activity
        mockFragmentTransaction.add(accounts, null);
        mockFragmentTransaction.commit();

        // Now you can call onResume() method
        accounts.onResume();

        // Verify that addView() was never called on accountsContainer
        verify(accountsContainer, never()).addView(any(), anyInt());
    }

    @Ignore
    @Test
    public void whenOnCreate_GivenNoMarketplaceProducts_thenAccountsAddViewNotInvoked() throws Exception {
        when(app.isSessionNeeded()).thenReturn(false);
        Whitebox.setInternalState(accounts, "mFragments", fragmentController);
        when(app.isUpdatingBalances()).thenReturn(false);
//        doNothing().when(accounts).updateDataSource();
        doReturn(false).when(accounts, "hasMarketplaceProducts");

//        accounts.onResume();

        verify(accountsContainer, never()).addView(marketplaceBanner, 0);
    }
    @Ignore
    @Test
    public void whenOnCreate_GivenMarketplaceProductsAndMarketplaceBannerIsNotNull_thenAccountsAddViewNotInvoked() throws Exception {
        when(app.isSessionNeeded()).thenReturn(false);
        Whitebox.setInternalState(accounts, "mFragments", fragmentController);
        when(app.isUpdatingBalances()).thenReturn(false);
//        doNothing().when(accounts).updateDataSource();
        doReturn(true).when(accounts, "hasMarketplaceProducts");
        when(accounts.getViewFromFragment(R.id.marketplace_banner)).thenReturn(marketplaceBanner);

//        accounts.onResume();

        verify(accountsContainer, never()).addView(marketplaceBanner, 0);
    }
    @Ignore
    @Test
    public void whenOnResume_GivenMarketplaceProductsAndFirstChildInAccountsContainerIsVisible_thenAccountsAddViewInvokedAtIndexOne() throws Exception {
        when(app.isSessionNeeded()).thenReturn(false);
        Whitebox.setInternalState(accounts, "mFragments", fragmentController);
        when(app.isUpdatingBalances()).thenReturn(false);
        doNothing().when(accounts).updateDataSource();
        doReturn(true).when(accounts, "hasMarketplaceProducts");
        when(accounts.getViewFromFragment(R.id.accounts_container)).thenReturn(accountsContainer);
        when(accountsContainer.getChildCount()).thenReturn(1);
        when(accountsContainer.getChildAt(0)).thenReturn(view);
        when(view.getVisibility()).thenReturn(View.VISIBLE);
        when(inflater.inflate(R.layout.marketplace_banner, null, false)).thenReturn(marketplaceBanner);

        accounts.onResume();

        verify(accountsContainer).addView(marketplaceBanner, 1);
    }
    @Ignore
    @Test
    public void whenOnResume_GivenMarketplaceProductsAndFirstChildInAccountsContainerIsGone_thenAccountsAddViewInvokedAtIndexZero() throws Exception {
        when(app.isSessionNeeded()).thenReturn(false);
        Whitebox.setInternalState(accounts, "mFragments", fragmentController);
        when(app.isUpdatingBalances()).thenReturn(false);
        doNothing().when(accounts).updateDataSource();
        doReturn(true).when(accounts, "hasMarketplaceProducts");
        when(accounts.getViewFromFragment(R.id.accounts_container)).thenReturn(accountsContainer);
        when(accountsContainer.getChildCount()).thenReturn(1);
        when(accountsContainer.getChildAt(0)).thenReturn(view);
        when(view.getVisibility()).thenReturn(View.GONE);
        when(inflater.inflate(R.layout.marketplace_banner, null, false)).thenReturn(marketplaceBanner);

        accounts.onResume();

        verify(accountsContainer).addView(marketplaceBanner, 0);
    }


    @Test
    @Ignore
    public void whenUpdateDataSource_ThenLoadAccounts() throws Exception {

        List<CustomerAccount> list = new ArrayList<CustomerAccount>();
        PowerMockito.doReturn(cust).when(app).getLoggedInUser();

        when(FeatureFlags.MBDP_MARKETPLACE()).thenReturn(Boolean.TRUE);

        when(cust.getInsuranceAndSecurities()).thenReturn(list);
        when(cust.getRetirementPlanAccounts()).thenReturn(list);

        when(cust.isRetPlanEnabled()).thenReturn(true);

        PowerMockito.doNothing().when(accounts, "loadAccounts", list, R.id.secins_view, R.id.secins_list);

        PowerMockito.doNothing().when(accounts, "loadAccounts", list, R.id.retplan_view, R.id.retplan_list);

        PowerMockito.doNothing().when(accounts, "showOrHideRetirementPlanAccount",cust);

        accounts.updateDataSource();

        verify(accounts, times(1)).updateDataSource();

    }

    @Test
    @Ignore
    public void whenUpdateDataSourceAndFlagIsDisabled_ThenDontLoadAccounts() throws Exception {

        List<CustomerAccount> list = new ArrayList<CustomerAccount>();
        PowerMockito.doReturn(cust).when(app).getLoggedInUser();

        when(FeatureFlags.MBDP_MARKETPLACE()).thenReturn(Boolean.TRUE);

        when(cust.getInsuranceAndSecurities()).thenReturn(list);
        when(cust.getRetirementPlanAccounts()).thenReturn(list);

        when(cust.isRetPlanEnabled()).thenReturn(false);

        PowerMockito.doNothing().when(accounts, "loadAccounts", list, R.id.secins_view, R.id.secins_list);

        when(accounts.getViewFromFragment(R.id.retplan_view)).thenReturn(view);

        accounts.updateDataSource();

        verify(accounts, times(1)).updateDataSource();

    }

    @Test
    @Ignore
    public void retPlanEnableBudgeIsFalseViewIsHidden() throws Exception {
        //conditions
        when(this.cust.hasRpBadge()).thenReturn(false);
        when(this.cust.isRetPlanEnabled()).thenReturn(true);

        //spying
        when(FeatureFlags.MBDP_MARKETPLACE()).thenReturn(Boolean.TRUE);
        PowerMockito.doReturn(retPlanNewAlertView).when(accounts).getViewFromFragment((R.id.retplan_view_new_alert));
        PowerMockito.doNothing().when(accounts, "loadAccounts", new ArrayList<CustomerAccount>(), R.id.secins_view, R.id.secins_list);
        PowerMockito.doNothing().when(accounts, "loadAccounts", new ArrayList<CustomerAccount>(), R.id.retplan_view, R.id.retplan_list);

        PowerMockito.doReturn(view).when(accounts).getViewFromFragment((R.id.retplan_view));
        PowerMockito.doReturn(cust).when(app).getLoggedInUser();
        PowerMockito.doReturn(true).when(cust).isRetPlanEnabled();
        when( App.getApplicationInstance().getGlobalStatus().isReterimentPlanEnabled()).thenReturn(Boolean.TRUE);

        PowerMockito.doReturn(true).when(cust).getRetPlanCallback();
        PowerMockito.doReturn(true).when(cust).customerHasRetPlan();

        PowerMockito.doNothing().when(animation).reset();
        PowerMockito.doNothing().when(animation).setInterpolator(new LinearInterpolator());
        PowerMockito.doNothing().when(animation).setDuration(anyInt());

        PowerMockito.doReturn(imageView).when(accounts).getViewFromFragment(R.id.ret_plan_loading);

        accounts.updateDataSource();

        verify(utils, atLeastOnce()).hideOrShowView(View.GONE, retPlanNewAlertView);
    }

    @Test
    @Ignore
    public void retPlanEnablebudgeIsTrueViewIsShown() throws Exception {
        //conditions
        when(this.cust.hasRpBadge()).thenReturn(true);
        when(this.cust.isRetPlanEnabled()).thenReturn(true);

        //spying
        when(FeatureFlags.MBDP_MARKETPLACE()).thenReturn(Boolean.TRUE);
        PowerMockito.doReturn(retPlanNewAlertView).when(accounts).getViewFromFragment((R.id.retplan_view_new_alert));
        PowerMockito.doNothing().when(accounts, "loadAccounts", new ArrayList<CustomerAccount>(), R.id.secins_view, R.id.secins_list);
        PowerMockito.doNothing().when(accounts, "loadAccounts", new ArrayList<CustomerAccount>(), R.id.retplan_view, R.id.retplan_list);

        PowerMockito.doReturn(view).when(accounts).getViewFromFragment((R.id.retplan_view));
        PowerMockito.doReturn(cust).when(app).getLoggedInUser();
        PowerMockito.doReturn(true).when(cust).isRetPlanEnabled();
        when( App.getApplicationInstance().getGlobalStatus().isReterimentPlanEnabled()).thenReturn(Boolean.TRUE);

        PowerMockito.doReturn(true).when(cust).getRetPlanCallback();
        PowerMockito.doReturn(true).when(cust).customerHasRetPlan();

        PowerMockito.doNothing().when(animation).reset();
        PowerMockito.doNothing().when(animation).setInterpolator(new LinearInterpolator());
        PowerMockito.doNothing().when(animation).setDuration(anyInt());

        PowerMockito.doReturn(imageView).when(accounts).getViewFromFragment(R.id.ret_plan_loading);

        accounts.updateDataSource();

        verify(utils, times(1)).hideOrShowView(View.VISIBLE, retPlanNewAlertView);
    }

    @Test
    @Ignore
    public void notRetPlanBudgeViewIsHidden() throws Exception {
        //conditions
        when(this.cust.hasRpBadge()).thenReturn(false);
        when(this.cust.isRetPlanEnabled()).thenReturn(true);

        //spying
        when(FeatureFlags.MBDP_MARKETPLACE()).thenReturn(Boolean.TRUE);
        PowerMockito.doReturn(retPlanNewAlertView).when(accounts).getViewFromFragment((R.id.retplan_view_new_alert));
        PowerMockito.doNothing().when(accounts, "loadAccounts", new ArrayList<CustomerAccount>(), R.id.secins_view, R.id.secins_list);
        PowerMockito.doNothing().when(accounts, "loadAccounts", new ArrayList<CustomerAccount>(), R.id.retplan_view, R.id.retplan_list);

        PowerMockito.doReturn(view).when(accounts).getViewFromFragment((R.id.retplan_view));
        PowerMockito.doReturn(cust).when(app).getLoggedInUser();
        PowerMockito.doReturn(true).when(cust).isRetPlanEnabled();
        when( App.getApplicationInstance().getGlobalStatus().isReterimentPlanEnabled()).thenReturn(Boolean.TRUE);

        PowerMockito.doReturn(true).when(cust).getRetPlanCallback();
        PowerMockito.doReturn(true).when(cust).customerHasRetPlan();

        PowerMockito.doNothing().when(animation).reset();
        PowerMockito.doNothing().when(animation).setInterpolator(new LinearInterpolator());
        PowerMockito.doNothing().when(animation).setDuration(anyInt());

        PowerMockito.doReturn(imageView).when(accounts).getViewFromFragment(R.id.ret_plan_loading);

        accounts.updateDataSource();

        verify(utils, times(1)).hideOrShowView(View.GONE, retPlanNewAlertView);
    }
    @Ignore
    public void whenGetViewWithRetirementPlan_withoutFullText_thenSetFooterTextOnce() throws Exception {
        //Prepare
        when(accounts.getViewFromFragment(anyInt())).thenReturn(view);
        when(accounts.getString(R.string.accounts_item_footer_more)).thenReturn("Mock String");
        when(accounts.getResources()).thenReturn(mock(Resources.class));
        View viewLocal = spy(new View(context));
        when(inflater.inflate(R.layout.list_item_accounts, null)).thenReturn(viewLocal);
        when(viewLocal.findViewById(R.id.main_layout)).thenReturn(spy(new LinearLayout(context)));
        when(viewLocal.findViewById(R.id.onoff_plastic_layout)).thenReturn(new LinearLayout(context));
        when(viewLocal.findViewById(R.id.footer_layout)).thenReturn(new LinearLayout(context));
        when(viewLocal.findViewById(R.id.item_name)).thenReturn(new TextView(context));
        when(viewLocal.findViewById(R.id.item_comment)).thenReturn(new TextView(context));
        when(viewLocal.findViewById(R.id.item_value)).thenReturn(new TextView(context));
        TextView footerTextView = spy(new TextView(context));
        LinearLayout mainLayout = viewLocal.findViewById(R.id.main_layout);
        when(viewLocal.findViewById(R.id.item_footer)).thenReturn(footerTextView);
        when(viewLocal.findViewById(R.id.item_image)).thenReturn(new ImageView(context));
        when(ContextCompat.getColor(any(Context.class), anyInt())).thenReturn(1);


        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setFooterPreviewTextId(1);
        final String footerText = "footerText";
        when(accounts.getString(1)).thenReturn(footerText);
        customerAccount.setFooterFullTextId(null);
        LinearLayout retPlanLayout = new LinearLayout(context);
        when(accounts.getViewFromFragment(R.id.retplan_list)).thenReturn(retPlanLayout);
        PowerMockito.doReturn(cust).when(app).getLoggedInUser();
        List<CustomerAccount> list = new ArrayList<>();
        list.add(customerAccount);
        when(cust.getRetirementPlanAccounts()).thenReturn(list);
        when(cust.getInsuranceAndSecurities()).thenReturn(list);
        PowerMockito.doNothing().when(accounts, "loadAccounts", list, R.id.secins_view, R.id.secins_list);
        when(cust.isRetPlanEnabled()).thenReturn(true);

        //Test
        accounts.updateDataSource();

        //Verify
        verify(mainLayout, times(1)).setOnClickListener(any(View.OnClickListener.class));
        verify(footerTextView, times(1)).setText(any(SpannableString.class));
    }

    @Test
    @Ignore
    public void whenGetViewWithRetirementPlan_withFullText_thenSetFooterTextTwice() throws Exception {
        //
        //AccountStub accounts = PowerMockito.spy(new AccountStub(app));

        PowerMockito.doReturn(appDelegate).when(accounts).getDelegateCompat();
        when(App.getApplicationInstance()).thenReturn(app);
        when(accounts.getViewFromFragment(R.layout.accounts)).thenReturn(view);
        when(accounts.getString(R.string.accounts_item_footer_more)).thenReturn("Mock String");
        when(accounts.getResources()).thenReturn(mock(Resources.class));
        View viewLocal = spy(new View(context));
        LinearLayout mainLayout = viewLocal.findViewById(R.id.main_layout);
        when(inflater.inflate(R.layout.list_item_accounts, null)).thenReturn(viewLocal);
        when(viewLocal.findViewById(R.id.main_layout)).thenReturn(spy(new LinearLayout(context)));
        when(viewLocal.findViewById(R.id.onoff_plastic_layout)).thenReturn(new LinearLayout(context));
        when(viewLocal.findViewById(R.id.footer_layout)).thenReturn(new LinearLayout(context));
        when(viewLocal.findViewById(R.id.item_name)).thenReturn(new TextView(context));
        when(viewLocal.findViewById(R.id.item_comment)).thenReturn(new TextView(context));
        when(viewLocal.findViewById(R.id.item_value)).thenReturn(new TextView(context));
        TextView footerTextView = spy(new TextView(context));
        when(viewLocal.findViewById(R.id.item_footer)).thenReturn(footerTextView);
        when(viewLocal.findViewById(R.id.item_image)).thenReturn(new ImageView(context));
        when(ContextCompat.getColor(any(Context.class), anyInt())).thenReturn(1);


        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setFooterPreviewTextId(1);
        customerAccount.setSubtype("");
        final String footerTextPreview = "footerTextPreview";
        final String footerTextFull = "footerTextFull";
        when(accounts.getString(1)).thenReturn(footerTextPreview);
        customerAccount.setFooterFullTextId(2);

        when(accounts.getString(2)).thenReturn(footerTextFull);
        LinearLayout retPlanLayout = new LinearLayout(context);
        when(accounts.getViewFromFragment(R.id.retplan_list)).thenReturn(retPlanLayout);
        PowerMockito.doReturn(cust).when(app).getLoggedInUser();
        List<CustomerAccount> list = new ArrayList<>();
        list.add(customerAccount);
        when(cust.getRetirementPlanAccounts()).thenReturn(list);
        when(cust.getInsuranceAndSecurities()).thenReturn(list);
        PowerMockito.doNothing().when(accounts, "loadAccounts", list, R.id.secins_view, R.id.secins_list);
        when(cust.isRetPlanEnabled()).thenReturn(true);
        when(App.getApplicationInstance().getPremiaInfo()).thenReturn(premiaInfo);
        when(App.getApplicationInstance().getPremiaInfo().isPremiaEnabled()).thenReturn(false);

        PowerMockito.doReturn(view).when(accounts).getViewFromFragment((R.id.retplan_view));

        PowerMockito.doReturn(true).when(cust).isRetPlanEnabled();
        when( App.getApplicationInstance().getGlobalStatus().isReterimentPlanEnabled()).thenReturn(Boolean.TRUE);

        PowerMockito.doReturn(true).when(cust).getRetPlanCallback();
        PowerMockito.doReturn(true).when(cust).customerHasRetPlan();

        PowerMockito.doNothing().when(animation).reset();
        PowerMockito.doNothing().when(animation).setInterpolator(new LinearInterpolator());
        PowerMockito.doNothing().when(animation).setDuration(anyInt());

        PowerMockito.doReturn(imageView).when(accounts).getViewFromFragment(R.id.ret_plan_loading);

        accounts.updateDataSource();

        //Verify
        verify(mainLayout, times(1)).setOnClickListener(any(View.OnClickListener.class));
        verify(footerTextView, times(2)).setText(any(SpannableString.class));
    }

    @Test
    @Ignore
    public void disabledRetPlanGlobalStatusShowDisabledTextRetPlanView() throws Exception {
        //mocks
        View disabledTextRetPlan = PowerMockito.mock(View.class);
        PowerMockito.doReturn(disabledTextRetPlan).when(accounts).getViewFromFragment(R.id.retplan_view_csr_downtimes_text);

        //conditions
        when(this.app.getGlobalStatus().isReterimentPlanEnabled()).thenReturn(false);
        when(cust.customerHasRetPlan()).thenReturn(true);

        when (cust.isRetPlanEnabled()).thenReturn(true);

        //spying
        when(FeatureFlags.MBDP_MARKETPLACE()).thenReturn(Boolean.TRUE);
        PowerMockito.doNothing().when(accounts, "loadAccounts", new ArrayList<CustomerAccount>(), R.id.secins_view, R.id.secins_list);

        PowerMockito.doReturn(view).when(accounts).getViewFromFragment((R.id.retplan_view));
        when(App.getApplicationInstance().getCustomerEntitlements().hasRetirementPlan()).thenReturn(true);
        // under test
        accounts.updateDataSource();

        // asserts
        verify(utils, times(1)).hideOrShowView(View.GONE, view);
    }

    @Test
    @Ignore
    public void enabledRetPlanGlobalStatusShowView() throws Exception {
        //spying
        when(FeatureFlags.MBDP_MARKETPLACE()).thenReturn(Boolean.TRUE);
        PowerMockito.doReturn(view).when(accounts).getViewFromFragment((R.id.retplan_view));
        PowerMockito.doNothing().when(accounts, "loadAccounts", new ArrayList<CustomerAccount>(), R.id.secins_view, R.id.secins_list);
        PowerMockito.doNothing().when(accounts, "loadAccounts", new ArrayList<CustomerAccount>(), R.id.retplan_view, R.id.retplan_list);

        accounts.updateDataSource();

        verify(utils, times(1)).hideOrShowView(View.VISIBLE, view);
    }

    @Test
    @Ignore
    public void whenUpdateDataSource_given_disableRetPlanGlobalStatusShowView_and_custumerHasRetPlanFalse() throws Exception {
        //spying
        when(FeatureFlags.MBDP_MARKETPLACE()).thenReturn(Boolean.TRUE);
        PowerMockito.doReturn(view).when(accounts).getViewFromFragment((R.id.retplan_view));
        PowerMockito.doNothing().when(accounts, "loadAccounts", new ArrayList<CustomerAccount>(), R.id.secins_view, R.id.secins_list);
        PowerMockito.doNothing().when(accounts, "loadAccounts", new ArrayList<CustomerAccount>(), R.id.retplan_view, R.id.retplan_list);

        when(this.app.getGlobalStatus().isReterimentPlanEnabled()).thenReturn(false);

        when(cust.customerHasRetPlan()).thenReturn(false);

        when (cust.isRetPlanEnabled()).thenReturn(true);

        when(App.getApplicationInstance().getCustomerEntitlements().hasRetirementPlan()).thenReturn(true);

//        accounts.updateDataSource();

        verify(utils, times(1)).hideOrShowView(View.VISIBLE, view);
    }


    @Test
    @Ignore
    public void whenShowBottomSheetDialog_Given_ThenShowAdvisorNAme() throws Exception {
        //mocks
        PowerMockito.doReturn(cust).when(app).getLoggedInUser();
        PowerMockito.whenNew(BottomSheetDialog.class).withAnyArguments().thenReturn(bottomSheetDialog);
        //spying
        when (cust.getIsWealth()).thenReturn(true);

        //spying
        View viewLocal = spy(new View(context));
        PowerMockito.doNothing().when(bottomSheetDialog).setContentView(any(View.class));
        TextView textView = spy(new TextView(context));
        ImageView btnClose = mock(ImageView.class);
        Button btnCall = mock(Button.class);
        Button btnEmail = mock(Button.class);
        PowerMockito.doNothing().when(btnClose).setOnClickListener(any(View.OnClickListener.class));
        PowerMockito.doNothing().when(btnCall).setOnClickListener(any(View.OnClickListener.class));
        PowerMockito.doNothing().when(btnEmail).setOnClickListener(any(View.OnClickListener.class));
        verify(textView, times(1)).setText("Test Test");

    }

    @Test
    @Ignore
    public void whenShowBottomSheetDialog_Given_ThenShowAdviserTitle() throws Exception {
        //mocks
        PowerMockito.doReturn(cust).when(app).getLoggedInUser();
        PowerMockito.whenNew(BottomSheetDialog.class).withAnyArguments().thenReturn(bottomSheetDialog);

        //spying
        when (cust.getIsWealth()).thenReturn(true);

        //spying
        View viewLocal = spy(new View(context));

        PowerMockito.doNothing().when(bottomSheetDialog).setContentView(any(View.class));
        TextView textView = spy(new TextView(context));
        ImageView btnClose = mock(ImageView.class);
        Button btnCall = mock(Button.class);
        Button btnEmail = mock(Button.class);

        PowerMockito.doNothing().when(btnClose).setOnClickListener(any(View.OnClickListener.class));
        PowerMockito.doNothing().when(btnCall).setOnClickListener(any(View.OnClickListener.class));
        PowerMockito.doNothing().when(btnEmail).setOnClickListener(any(View.OnClickListener.class));
        verify(textView, times(1)).setText("Entitlement");

    }

    @Test
    @Ignore
    public void whenShowBottomSheetDialog_Given_ThenShowAdviserPhoto() throws Exception {
        
        //mocks
        PowerMockito.doReturn(cust).when(app).getLoggedInUser();
        PowerMockito.whenNew(BottomSheetDialog.class).withAnyArguments().thenReturn(bottomSheetDialog);
        
        //spying
        View viewLocal = spy(new View(context));
        
        PowerMockito.doNothing().when(bottomSheetDialog).setContentView(any(View.class));
        ImageView advisorPhoto = spy(new ImageView(context));
        String glideUrl = "URL";
        Drawable drawablePhoto  = mock(Drawable.class);
        advisorPhoto.setImageDrawable(drawablePhoto);
        advisorPhoto.onDrawForeground(new Canvas());
        
        ImageView btnClose = mock(ImageView.class);
        Button btnCall = mock(Button.class);
        Button btnEmail = mock(Button.class);

        PowerMockito.doNothing().when(btnClose).setOnClickListener(any(View.OnClickListener.class));
        PowerMockito.doNothing().when(btnCall).setOnClickListener(any(View.OnClickListener.class));
        PowerMockito.doNothing().when(btnEmail).setOnClickListener(any(View.OnClickListener.class));
        
        verify(advisorPhoto, times(1)).setImageDrawable(drawablePhoto);
    }
    
    @Test
    @Ignore
    public void whenGetViewWithCCAAccount_withPositiveBalance_thenTextGreen() throws Exception {
        //Prepare
        AccountStub accounts = PowerMockito.spy(new AccountStub(app));
        
        PowerMockito.doReturn(appDelegate).when(accounts).getDelegate();
        when(App.getApplicationInstance()).thenReturn(app);
        when(accounts.findViewById(R.layout.accounts)).thenReturn(view);
        when(accounts.findViewById(anyInt())).thenReturn(view);
        when(accounts.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(inflater);
        when(accounts.getString(R.string.accounts_item_footer_more)).thenReturn("Mock String");
        when(accounts.getResources()).thenReturn(mock(Resources.class));
        View viewLocal = spy(new View(context));
        LinearLayout mainLayout = viewLocal.findViewById(R.id.main_layout);
        when(inflater.inflate(R.layout.list_item_accounts, null)).thenReturn(viewLocal);
        when(viewLocal.findViewById(R.id.main_layout)).thenReturn(spy(new LinearLayout(context)));
        when(viewLocal.findViewById(R.id.onoff_plastic_layout)).thenReturn(new LinearLayout(context));
        when(viewLocal.findViewById(R.id.footer_layout)).thenReturn(new LinearLayout(context));
        when(viewLocal.findViewById(R.id.item_name)).thenReturn(new TextView(context));
        when(viewLocal.findViewById(R.id.item_comment)).thenReturn(new TextView(context));
        when(viewLocal.findViewById(R.id.item_value)).thenReturn(new TextView(context));
        TextView footerTextView = spy(new TextView(context));
        when(viewLocal.findViewById(R.id.item_footer)).thenReturn(footerTextView);
        when(viewLocal.findViewById(R.id.item_image)).thenReturn(new ImageView(context));
        when(ContextCompat.getColor(any(Context.class), anyInt())).thenReturn(1);
        doNothing().when(accounts).setContentView(R.layout.accounts);
        
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setFooterPreviewTextId(1);
        customerAccount.setSubtype("CCA");
        customerAccount.setPortalBalance("($100.00)");
        final String footerTextPreview = "footerTextPreview";
        final String footerTextFull = "footerTextFull";
        when(accounts.getString(1)).thenReturn(footerTextPreview);
        customerAccount.setFooterFullTextId(2);
        when(accounts.getString(2)).thenReturn(footerTextFull);
        when(accounts.getApplication()).thenReturn(app);
        LinearLayout retPlanLayout = new LinearLayout(context);
        LinearLayout ccaLayout = new LinearLayout(context);
        when(accounts.findViewById(R.id.cards_list)).thenReturn(ccaLayout);
        when(accounts.findViewById(R.id.retplan_list)).thenReturn(retPlanLayout);
        PowerMockito.doReturn(cust).when(app).getLoggedInUser();
        List<CustomerAccount> list = new ArrayList<>();
        list.add(customerAccount);
        when(cust.getRetirementPlanAccounts()).thenReturn(list);
        when(cust.getInsuranceAndSecurities()).thenReturn(list);
        when(cust.getCreditCards()).thenReturn(list);
        PowerMockito.doNothing().when(accounts, "loadAccounts", list, R.id.cards_view, R.id.cards_list);
        PowerMockito.doNothing().when(accounts, "loadAccounts", list, R.id.secins_view, R.id.secins_list);
        when(cust.isRetPlanEnabled()).thenReturn(true);
        when(app.getPremiaInfo()).thenReturn(premiaInfo);
        when(premiaInfo.isPremiaEnabled()).thenReturn(false);

        PowerMockito.doNothing().when(accounts, "showOrHideRetirementPlanAccount",cust);

        //Test
//        accounts.updateDataSource();
        
        //Verify
        verify(mainLayout, times(1)).setOnClickListener(any(View.OnClickListener.class));
        verify(footerTextView, times(2)).setText(any(SpannableString.class));
    }

    public void whenLoadAccounts_thenLoadPremiaBalance() throws Exception {
        //
        AccountStub accounts = PowerMockito.spy(new AccountStub(app));
        
        PowerMockito.doReturn(appDelegate).when(accounts).getDelegate();
        when(App.getApplicationInstance()).thenReturn(app);
        when(accounts.findViewById(R.layout.accounts)).thenReturn(view);
        when(accounts.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(inflater);
        when(accounts.getString(R.string.accounts_item_footer_more)).thenReturn("Mock String");
        when(accounts.getResources()).thenReturn(mock(Resources.class));
        View viewLocal = spy(new View(context));
        LinearLayout mainLayout = viewLocal.findViewById(R.id.main_layout);
        when(inflater.inflate(R.layout.list_item_accounts, null)).thenReturn(viewLocal);
        when(viewLocal.findViewById(R.id.main_layout)).thenReturn(spy(new LinearLayout(context)));
        when(viewLocal.findViewById(R.id.onoff_plastic_layout)).thenReturn(new LinearLayout(context));
        when(viewLocal.findViewById(R.id.footer_layout)).thenReturn(new LinearLayout(context));
        when(viewLocal.findViewById(R.id.item_name)).thenReturn(new TextView(context));
        when(viewLocal.findViewById(R.id.item_comment)).thenReturn(new TextView(context));
        when(viewLocal.findViewById(R.id.item_value)).thenReturn(new TextView(context));
        TextView footerTextView = spy(new TextView(context));
        when(viewLocal.findViewById(R.id.item_footer)).thenReturn(footerTextView);
        when(viewLocal.findViewById(R.id.item_image)).thenReturn(new ImageView(context));
        when(ContextCompat.getColor(any(Context.class), anyInt())).thenReturn(1);
        
        doNothing().when(accounts).setContentView(R.layout.accounts);
        when(accounts.getApplicationContext()).thenReturn(context);
        when(resources.getString(R.string.premia_account_subtype)).thenReturn("PRM");
        ViewHolder viewHolder = PowerMockito.spy(new ViewHolder(viewLocal));
        when(viewHolder.getPremiaBalanceView()).thenReturn(premiaViewLayout);
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setFooterPreviewTextId(1);
        final String footerTextPreview = "footerTextPreview";
        final String footerTextFull = "footerTextFull";
        when(accounts.getString(1)).thenReturn(footerTextPreview);
        customerAccount.setFooterFullTextId(2);
        customerAccount.setSubtype("PRM");
        customerAccount.setPremiaBalance("100");
        when(accounts.getString(2)).thenReturn(footerTextFull);
        when(accounts.getApplication()).thenReturn(app);
        LinearLayout retPlanLayout = new LinearLayout(context);
        when(accounts.findViewById(R.id.retplan_list)).thenReturn(retPlanLayout);
        PowerMockito.doReturn(cust).when(app).getLoggedInUser();
        TextView premiaTextView = spy(new TextView(context));
        when(viewHolder.getValue()).thenReturn(premiaTextView);
        
        List<CustomerAccount> list = new ArrayList<>();
        list.add(customerAccount);
        when(cust.getRetirementPlanAccounts()).thenReturn(list);
        when(cust.getInsuranceAndSecurities()).thenReturn(list);
        when(cust.getRewards()).thenReturn(list);
        PowerMockito.doNothing().when(accounts, "loadAccounts", list, R.id.secins_view, R.id.secins_list);
        PowerMockito.doNothing().when(accounts, "loadAccounts", list, R.id.rewards_view, R.id.rewards_list);
        when(cust.isRetPlanEnabled()).thenReturn(true);
        when(app.getPremiaInfo()).thenReturn(premiaInfo);
        when(premiaInfo.isPremiaEnabled()).thenReturn(true);
        
        accounts.setContentView(R.layout.accounts);
        
//        accounts.updateDataSource();
        
        //Verify
        verify(mainLayout, times(1)).setOnClickListener(any(View.OnClickListener.class));
        verify(footerTextView, times(2)).setText(any(SpannableString.class));
    }

    @Test
    @Ignore
    public void whenOnCreate_givenShowBankerInfoButton_thenLoadBankerInformationInvoked() throws Exception {

        PowerMockito.doReturn(appDelegate).when(accounts).getDelegateCompat();
        when(App.getApplicationInstance()).thenReturn(app);
        when(app.getLoggedInUser()).thenReturn(cust);
        ImageView btnClose = mock(ImageView.class);
        View msgView = mock(View.class);
        View whiteSpaceView = mock(View.class);
        View notificationCenterView = mock(View.class);
        when(accounts.getViewFromFragment(R.id.close_button)).thenReturn(btnClose);
        when(accounts.getViewFromFragment(R.id.accounts_notice_layout)).thenReturn(msgView);
        when(accounts.getViewFromFragment(R.id.notification_center_notice)).thenReturn(notificationCenterView);
        when(msgView.getVisibility()).thenReturn(View.GONE);
        doNothing().when(btnClose).setOnClickListener(any(View.OnClickListener.class));
        PowerMockito.when(Utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);
        when(sharedPreferences.getBoolean(MiBancoConstants.DISMISS_PERSONALIZATION_NOTICE_PREFS_KEY, false)).thenReturn(false);
        when(accounts.getResources()).thenReturn(resources);
        when(resources.getConfiguration()).thenReturn(configuration);
        when(app.getLanguage()).thenReturn("es");
        doNothing().when(floatingActionButton).setOnClickListener(any(View.OnClickListener.class));
        accounts.onCreate(null);
        PowerMockito.verifyPrivate(accounts).invoke("loadBankerInformation");
    }

    @Test
    @Ignore
    public void whenActionSecurityAccount_GivenGetSsoAcceptedSSOBOOL() throws Exception {
        onCreate();
        PowerMockito.doReturn(cust).when(app).getLoggedInUser();
        PowerMockito.doNothing().when(accounts).startActivityForResult(any(Intent.class), any(Integer.class));

        Whitebox.invokeMethod(accounts, "actionSecurityAccount");
    }

    @Test
    @Ignore
    public void whenActionSecurityAccount_GivenGetSsoAcceptedNOUSER() throws Exception {

        onCreate();
        PowerMockito.doReturn(cust).when(app).getLoggedInUser();
        Whitebox.invokeMethod(accounts, "actionSecurityAccount");
    }

    @Test
    @Ignore
    public void whenActionSecurityAccount_GivenGetSsoAcceptedOTHER() throws Exception {

        onCreate();
        PowerMockito.doReturn(cust).when(app).getLoggedInUser();

        Whitebox.invokeMethod(accounts, "actionSecurityAccount");
    }

    private void onCreate() {
        PowerMockito.doReturn(appDelegate).when(accounts).getDelegateCompat();
        when(App.getApplicationInstance()).thenReturn(app);
        when(app.getLoggedInUser()).thenReturn(cust);
        ImageView btnClose = mock(ImageView.class);
        View msgView = mock(View.class);
        View whiteSpaceView = mock(View.class);
        View notificationCenterView = mock(View.class);
        when(accounts.getViewFromFragment(R.id.close_button)).thenReturn(btnClose);
        when(accounts.getViewFromFragment(R.id.accounts_notice_layout)).thenReturn(msgView);
        when(accounts.getViewFromFragment(R.id.notification_center_notice)).thenReturn(notificationCenterView);
        when(msgView.getVisibility()).thenReturn(View.GONE);
        doNothing().when(btnClose).setOnClickListener(any(View.OnClickListener.class));
        PowerMockito.when(Utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);
        when(sharedPreferences.getBoolean(MiBancoConstants.DISMISS_PERSONALIZATION_NOTICE_PREFS_KEY, false)).thenReturn(false);
        when(accounts.getResources()).thenReturn(resources);
        when(resources.getConfiguration()).thenReturn(configuration);
        when(app.getLanguage()).thenReturn("es");
        doNothing().when(floatingActionButton).setOnClickListener(any(View.OnClickListener.class));
        accounts.onCreate(null);
    }

/**
    public void whenLoadRetirementPlanSection_GivenRetirementPlanInfoResponseAndCustomerLogin_ThenLoadRetirementPlan() throws Exception {
        when(accounts.getApplication()).thenReturn(app);

        when(newRetirementPlanInfoResponse.getHasRetPlan()).thenReturn(true);

        RetirementPlanDetail detalle = new RetirementPlanDetail();

        detalle.setPlanTotalBalance("123");
        detalle.setPlannam("test");
        detalle.setPlanLast4Num("x9090");

        newRetirementPlanDetail.add(detalle);

        when(newRetirementPlanInfoResponse.getRetirementPlans()).thenReturn(newRetirementPlanDetail);

        PowerMockito.doReturn(view).when(accounts).findViewById((R.id.retplan_view));
        PowerMockito.doNothing().when(accounts, "loadAccounts", new ArrayList<CustomerAccount>(), R.id.retplan_view, R.id.retplan_list);
        Whitebox.invokeMethod(accounts, "loadRetirementPlanSection",newRetirementPlanInfoResponse,cust);

        PowerMockito.verifyPrivate(accounts).invoke("loadRetirementPlanSection",newRetirementPlanInfoResponse,cust);
    }

    /**
     * Class to set a private Mock variable when it is not posible mock on regular flow
     */
    private static class AccountStub extends Accounts {
        public AccountStub (App appMock) {
            this.application = appMock;
        }
    }

    @Test
    @Ignore
    public void whenUpdateDataSource_GivenMbdp_MarketplaceIsTrue_ThenShowOpenAccountView() throws Exception {
        
        List<CustomerAccount> list = new ArrayList<CustomerAccount>();
        PowerMockito.doReturn(cust).when(app).getLoggedInUser();
        
        when(FeatureFlags.MBDP_MARKETPLACE()).thenReturn(Boolean.TRUE);
        
        when(cust.getInsuranceAndSecurities()).thenReturn(list);
        when(cust.getRetirementPlanAccounts()).thenReturn(list);
        
        when(cust.isRetPlanEnabled()).thenReturn(true);
        
        PowerMockito.doNothing().when(accounts, "loadAccounts", list, R.id.secins_view, R.id.secins_list);
        PowerMockito.doNothing().when(accounts, "loadAccounts", list, R.id.retplan_view, R.id.retplan_list);
        
//        accounts.updateDataSource();
        
//        verify(accounts, times(1)).updateDataSource();
    }

    @Test
    @Ignore
    public void whenUpdateDataSource_GivenMbdp_MarketplaceIsFalse_ThenHideMarketplaceBanner() throws Exception {
        
        List<CustomerAccount> list = new ArrayList<CustomerAccount>();
        PowerMockito.doReturn(cust).when(app).getLoggedInUser();
        
        when(FeatureFlags.MBDP_MARKETPLACE()).thenReturn(Boolean.FALSE);
        
        when(cust.getInsuranceAndSecurities()).thenReturn(list);
        when(cust.getRetirementPlanAccounts()).thenReturn(list);
        
        when(cust.isRetPlanEnabled()).thenReturn(true);
        
        PowerMockito.doNothing().when(accounts, "loadAccounts", list, R.id.secins_view, R.id.secins_list);
        PowerMockito.doNothing().when(accounts, "loadAccounts", list, R.id.retplan_view, R.id.retplan_list);
        
//        accounts.updateDataSource();
        
//        verify(accounts, times(1)).updateDataSource();
        verify(utils, times(1)).hideOrShowView(View.GONE, view);
    }
    @Ignore
    @Test
    public void whenClickingMobileWithdrawalButton_StartsEnrollmentLiteWelcomeActivity() {
        intent = new Intent();
        intent.setClass(mockContext, EnrollmentLiteWelcomeActivity.class);

        try {
            PowerMockito.whenNew(Intent.class)
                    .withArguments(String.class).thenReturn(intent);
            verify(intent).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            verify(mockContext).startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Ignore
    @Test
    public void whenClickingEasyDepositButton_StartsEasyDepositActivity() {

        intent = new Intent();
        intent.setClass(mockContext, DepositCheck.class);

        try {
            PowerMockito.whenNew(Intent.class)
                    .withArguments(String.class).thenReturn(intent);
            verify(intent).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            verify(mockContext).startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}