package com.popular.android.mibanco.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.google.android.gms.common.util.CollectionUtils;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.popular.android.mibanco.util.Utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.popular.android.mibanco.util.Utils.getSecuredSharedPreferences;
import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FeatureFlags.class, BuildConfig.class, MiBancoEnviromentConstants.class, App.class,
        MiBancoConstants.class, Utils.class})
public class CustomerUT {

    @Mock
    private App app;

    @Mock
    private Context context;

    @Mock
    private Resources resources;

    @Mock
    private SharedPreferences sharedPreferences;

    @InjectMocks
    private Customer cust;

    private CustomerContent content;
    private CustomerAccount account;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(Utils.class);

        //Need this to mock static MiBancoEnvironmentConstants
        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);
        PowerMockito.when(app.getBaseContext()).thenReturn(context);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.when(Utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);

        PowerMockito.mockStatic(MiBancoEnviromentConstants.class);
        PowerMockito.mockStatic(FeatureFlags.class);

        content = new CustomerContent();
        cust = spy(new Customer());
        cust.content =  content;
        account = new CustomerAccount();
    }

    @Test
    public void getRetirementPlanAccounts_without_returnsNull() {
        List<CustomerAccount> retirementPlanAccounts = cust.getRetirementPlanAccounts();
        assertTrue(CollectionUtils.isEmpty(retirementPlanAccounts));
    }

    @Test
    public void getRDCAccounts_returnListOfAccounts_thenAssertNotNull()
    {
        CustomerAccount account = new CustomerAccount();
        account.setApiAccountKey("any");
        account.setAccountNumberSuffix("1");
        account.setAccountSection("s");
        List<CustomerAccount> accounts = cust.getDepositAccounts();
        accounts.add(account);
        CustomerAccount rdcAccounts = new CustomerAccount();
        rdcAccounts.setApiAccountKey("any");
        rdcAccounts.setAccountNumberSuffix("1");
        rdcAccounts.setAccountSection("s");
        content.rdcAccounts = new LinkedList<>(Arrays.asList(rdcAccounts));
        assertNotNull(cust.getRDCAccounts());
    }

    @Test
    public void getRDCAccounts_whenAccountSectionDiffers_returnListOfAccounts()
    {
        CustomerAccount account = new CustomerAccount();
        account.setApiAccountKey("any");
        account.setAccountNumberSuffix("1");
        account.setAccountSection("s");
        List<CustomerAccount> accounts = cust.getDepositAccounts();
        accounts.add(account);
        CustomerAccount rdcAccounts = new CustomerAccount();
        rdcAccounts.setApiAccountKey("any");
        rdcAccounts.setAccountNumberSuffix("1");
        rdcAccounts.setAccountSection("n");
        content.rdcAccounts = new LinkedList<>(Arrays.asList(rdcAccounts));
        assertNotNull(cust.getRDCAccounts());
    }

    @Test
    public void getRDCAccounts__whenAccountNumberSuffixDiffers_returnListOfAccounts()
    {
        CustomerAccount account = new CustomerAccount();
        account.setApiAccountKey("any");
        account.setAccountNumberSuffix("1");
        account.setAccountSection("s");
        List<CustomerAccount> accounts = cust.getDepositAccounts();
        accounts.add(account);
        CustomerAccount rdcAccounts = new CustomerAccount();
        rdcAccounts.setApiAccountKey("any");
        rdcAccounts.setAccountNumberSuffix("0");
        rdcAccounts.setAccountSection("s");
        content.rdcAccounts = new LinkedList<>(Arrays.asList(rdcAccounts));
        assertNotNull(cust.getRDCAccounts());
    }

    @Test
    public void getRDCAccounts_whenApiAccountKeyDiffers_returnListOfAccounts()
    {
        CustomerAccount account = new CustomerAccount();
        account.setApiAccountKey("any");
        account.setAccountNumberSuffix("1");
        account.setAccountSection("s");
        List<CustomerAccount> accounts = cust.getDepositAccounts();
        accounts.add(account);
        CustomerAccount rdcAccounts = new CustomerAccount();
        rdcAccounts.setApiAccountKey("ani");
        rdcAccounts.setAccountNumberSuffix("1");
        rdcAccounts.setAccountSection("s");
        content.rdcAccounts = new LinkedList<>(Arrays.asList(rdcAccounts));
        assertNotNull(cust.getRDCAccounts());
    }

    @Test
    public void getRDCAccounts_whenAccountSectionMatch_returnListOfAccounts()
    {
        CustomerAccount account = new CustomerAccount();
        account.setApiAccountKey("any");
        account.setAccountNumberSuffix("1");
        account.setAccountSection("s");
        List<CustomerAccount> accounts = cust.getDepositAccounts();
        accounts.add(account);
        CustomerAccount rdcAccounts = new CustomerAccount();
        rdcAccounts.setApiAccountKey("ani");
        rdcAccounts.setAccountNumberSuffix("0");
        rdcAccounts.setAccountSection("s");
        content.rdcAccounts = new LinkedList<>(Arrays.asList(rdcAccounts));
        assertNotNull(cust.getRDCAccounts());
    }

    @Test
    public void getRDCAccounts_whenAccountNumberSuffixMatch_returnListOfAccounts()
    {
        CustomerAccount account = new CustomerAccount();
        account.setApiAccountKey("any");
        account.setAccountNumberSuffix("1");
        account.setAccountSection("s");
        List<CustomerAccount> accounts = cust.getDepositAccounts();
        accounts.add(account);
        CustomerAccount rdcAccounts = new CustomerAccount();
        rdcAccounts.setApiAccountKey("ani");
        rdcAccounts.setAccountNumberSuffix("1");
        rdcAccounts.setAccountSection("n");
        content.rdcAccounts = new LinkedList<>(Arrays.asList(rdcAccounts));
        assertNotNull(cust.getRDCAccounts());
    }

    @Test
    public void getRDCAccounts_whenApiAccountKeyMatch_returnListOfAccounts()
    {
        CustomerAccount account = new CustomerAccount();
        account.setApiAccountKey("any");
        account.setAccountNumberSuffix("1");
        account.setAccountSection("s");
        List<CustomerAccount> accounts = cust.getDepositAccounts();
        accounts.add(account);
        CustomerAccount rdcAccounts = new CustomerAccount();
        rdcAccounts.setApiAccountKey("any");
        rdcAccounts.setAccountNumberSuffix("0");
        rdcAccounts.setAccountSection("n");
        content.rdcAccounts = new LinkedList<>(Arrays.asList(rdcAccounts));
        assertNotNull(cust.getRDCAccounts());
    }

    @Test
    public void getRDCAccounts_whenNotMatch_returnListOfAccounts()
    {
        CustomerAccount account = new CustomerAccount();
        account.setApiAccountKey("any");
        account.setAccountNumberSuffix("1");
        account.setAccountSection("s");
        List<CustomerAccount> accounts = cust.getDepositAccounts();
        //accounts.add(account);
        CustomerAccount rdcAccounts = new CustomerAccount();
        rdcAccounts.setApiAccountKey("ani");
        rdcAccounts.setAccountNumberSuffix("0");
        rdcAccounts.setAccountSection("n");
        content.rdcAccounts = new LinkedList<>(Arrays.asList(rdcAccounts));
        assertNotNull(cust.getRDCAccounts());
    }

    @Test
    public void getRDCAccounts_accountsIsEmpty_returnListOfAccounts()
    {
        CustomerAccount account = new CustomerAccount();
        account.setApiAccountKey("any");
        account.setAccountNumberSuffix("1");
        account.setAccountSection("s");
        List<CustomerAccount> accounts = cust.getDepositAccounts();
        CustomerAccount rdcAccounts = new CustomerAccount();
        rdcAccounts.setApiAccountKey("any");
        rdcAccounts.setAccountNumberSuffix("1");
        rdcAccounts.setAccountSection("s");
        content.rdcAccounts = new LinkedList<>(Arrays.asList(rdcAccounts));
        assertNotNull(cust.getRDCAccounts());
    }


    @Test
    public void getRDCAccounts_rdcAccountsIsEmpty_returnListOfAccounts()
    {
        CustomerAccount account = new CustomerAccount();
        account.setApiAccountKey("any");
        account.setAccountNumberSuffix("1");
        account.setAccountSection("s");
        List<CustomerAccount> accounts = null;
        CustomerAccount rdcAccounts = new CustomerAccount();
        rdcAccounts.setApiAccountKey("any");
        rdcAccounts.setAccountNumberSuffix("a");
        rdcAccounts.setAccountSection("s");
        assertNotNull(cust.getRDCAccounts());
    }

    @Test
    public void getRDCAccounts_rdcAccountsIsNull_returnListOfAccounts()
    {
        CustomerAccount account = new CustomerAccount();
        account.setApiAccountKey("any");
        account.setAccountNumberSuffix("1");
        account.setAccountSection("s");
        List<CustomerAccount> accounts = cust.getDepositAccounts();
        assertNotNull(cust.getRDCAccounts());
    }


    @Test
    public void whenSortAcc_GivenHideMLATransactionsTrue() throws Exception {

        CustomerAccount account = new CustomerAccount();
        account.setSubtype("MLA");
        content.accounts = new LinkedList<>(Arrays.asList(account));
        content.hideMLATransactions = Boolean.TRUE;
        content.programs = null;
        content.secins = null;
        content.retplan = null;
        cust.content =  content;
        Whitebox.invokeMethod(cust, "sortAcc");
    }

    @Test
    public void whenSortAcc_GivenHideMLATransactionsFalseFeatureFlagTrue() throws Exception {

        CustomerAccount mlaAccount = new CustomerAccount();
        mlaAccount.setSubtype("MLA");
        content.accounts = new LinkedList<>(Arrays.asList(mlaAccount));
        content.hideMLATransactions = Boolean.FALSE;
        content.programs = null;
        content.secins = null;
        content.retplan = null;
        cust.content =  content;
        when(FeatureFlags.MBCA_104()).thenReturn(Boolean.TRUE);
        Whitebox.invokeMethod(cust, "sortAcc");
    }

    @Test
    public void whenSortAcc_GivenHideMLATransactionsFalseFeatureFlagFalse() throws Exception {

        CustomerAccount mlaAccount = new CustomerAccount();
        mlaAccount.setSubtype("MLA");
        content.accounts = new LinkedList<>(Arrays.asList(mlaAccount));
        content.hideMLATransactions = Boolean.FALSE;
        content.programs = null;
        content.secins = null;
        content.retplan = null;
        cust.content =  content;
        when(FeatureFlags.MBCA_104()).thenReturn(Boolean.FALSE);
        Whitebox.invokeMethod(cust, "sortAcc");
    }

    @Test
    public void whenSortAcc_GivenCCAAccount() throws Exception {

        CustomerAccount account = new CustomerAccount();
        account.setSubtype("CCA");
        content.accounts = new LinkedList<>(Arrays.asList(account));
        content.hideMLATransactions = Boolean.TRUE;
        content.programs = null;
        content.secins = null;
        content.retplan = null;
        cust.content =  content;
        Whitebox.invokeMethod(cust, "sortAcc");
    }

    @Test
    public void whenSortAcc_GivenIDAAccount() throws Exception {

        CustomerAccount account = new CustomerAccount();
        account.setSubtype("IDA");
        content.accounts = new LinkedList<>(Arrays.asList(account));
        content.hideMLATransactions = Boolean.TRUE;
        content.programs = null;
        content.secins = null;
        content.retplan = null;
        cust.content =  content;
        Whitebox.invokeMethod(cust, "sortAcc");
    }

    @Test
    public void whenSortAcc_GivenILAAccount() throws Exception {

        CustomerAccount account = new CustomerAccount();
        account.setSubtype("ILA");
        content.accounts = new LinkedList<>(Arrays.asList(account));
        content.hideMLATransactions = Boolean.TRUE;
        content.programs = null;
        content.secins = null;
        content.retplan = null;
        cust.content =  content;
        Whitebox.invokeMethod(cust, "sortAcc");
    }

    @Test
    public void whenSortAcc_GivenLEAAccount() throws Exception {

        CustomerAccount account = new CustomerAccount();
        account.setSubtype("LEA");
        content.accounts = new LinkedList<>(Arrays.asList(account));
        content.hideMLATransactions = Boolean.TRUE;
        content.programs = null;
        content.secins = null;
        content.retplan = null;
        cust.content =  content;
        Whitebox.invokeMethod(cust, "sortAcc");
    }

    @Test
    public void whenSortAcc_GivenCDAAccount() throws Exception {

        CustomerAccount account = new CustomerAccount();
        account.setSubtype("CDA");
        content.accounts = new LinkedList<>(Arrays.asList(account));
        content.hideMLATransactions = Boolean.TRUE;
        content.programs = null;
        content.secins = null;
        content.retplan = null;
        cust.content =  content;
        Whitebox.invokeMethod(cust, "sortAcc");
    }

    @Test
    public void whenSortAcc_GivenOTHERAccount() throws Exception {

        CustomerAccount account = new CustomerAccount();
        account.setSubtype("OTHER");
        content.accounts = new LinkedList<>(Arrays.asList(account));
        content.hideMLATransactions = Boolean.TRUE;
        content.programs = null;
        content.secins = null;
        content.retplan = null;
        cust.content =  content;
        Whitebox.invokeMethod(cust, "sortAcc");
    }

    @Test
    public void whenSortAcc_GivenContentNUll() throws Exception {
        cust.content =  null;
        Whitebox.invokeMethod(cust, "sortAcc");
    }

}
