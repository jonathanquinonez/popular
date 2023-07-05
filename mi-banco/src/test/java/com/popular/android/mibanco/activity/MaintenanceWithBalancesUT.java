package com.popular.android.mibanco.activity;


import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.model.Customer;
import com.popular.android.mibanco.widget.AccountNameAndBalanceItem;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MaintenanceWithBalancesUT {

    final String balances = "Checking><x5485><$220.53><B;Savings><x5485 SAV><$2.21><B;Reserve><x5485 RES><$0.00><R;";

    @Test
    public void givenEmptyDate_WhenShouldUpdateBalances_thenReturnTrue() {

         String date = StringUtils.EMPTY;

         boolean shouldUpdate = MaintenanceWithBalances.shouldUpdateBalances(date);

         assertTrue(shouldUpdate);
    }

    @Test
    public void givenNowDate_WhenShouldUpdateBalances_thenReturnFalse() {

        String date = Calendar.getInstance().getTime().toString();

        boolean shouldUpdate = MaintenanceWithBalances.shouldUpdateBalances(date);

        assertFalse(shouldUpdate);
    }

    @Test
    public void givenMinutesBeforeDate_WhenShouldUpdateBalances_thenReturnTrue() {

        Date date = Calendar.getInstance().getTime();
        long dateString = date.getTime() - MiBancoConstants.WIDGET_MAX_UPDATE_TIME - 100000;
        Date dateWithMoreMinutes = new Date(dateString);

        boolean shouldUpdate = MaintenanceWithBalances.shouldUpdateBalances(dateWithMoreMinutes.toString());

        assertTrue(shouldUpdate);
    }

    @Test
    public void givenNullDate_WhenShouldUpdateBalances_thenReturnTrue() {

        boolean shouldUpdate = MaintenanceWithBalances.shouldUpdateBalances(null);

        assertTrue(shouldUpdate);
    }

    @Test
    public void givenNotValidDate_WhenShouldUpdateBalances_thenReturnTrue() {

        boolean shouldUpdate = MaintenanceWithBalances.shouldUpdateBalances("ëqfdeqfefefefefe");

        assertTrue(shouldUpdate);
    }

    @Test
    public void givenEmptyBalances_WhenShowCurrentCachedBalances_ThenReturnEmptyList() {
         List<AccountNameAndBalanceItem> accounts = new ArrayList<>();
         String balances = StringUtils.EMPTY;

        accounts = MaintenanceWithBalances.showCurrentCachedBalance(accounts, balances);

        assert(accounts.isEmpty());
    }

    @Test
    public void givenValidBalances_WhenShowCurrentCachedBalances_ThenReturnNotEmptyList() {
        List<AccountNameAndBalanceItem> accounts = new ArrayList<>();;

        accounts = MaintenanceWithBalances.showCurrentCachedBalance(accounts, balances);

        assert(!accounts.isEmpty());
        assert(StringUtils.equals(accounts.get(0).getAccountName(), "Checking"));
    }

    @Test
    public void givenValidBalances_WhenShowCurrentCachedBalances_ThenReturnValidList() {
        List<AccountNameAndBalanceItem> accounts = new ArrayList<>();

        accounts = MaintenanceWithBalances.showCurrentCachedBalance(accounts, balances);

        // First account
        assert(StringUtils.equals(accounts.get(0).getAccountName(), "Checking"));
        assert(StringUtils.equals(accounts.get(0).getAccountSuffix(), "x5485"));
        assert(StringUtils.equals(accounts.get(0).getBalance(), "$220.53"));
        assertFalse(accounts.get(0).isRedBalance());

        // second account
        assert(StringUtils.equals(accounts.get(1).getAccountName(), "Savings"));
        assert(StringUtils.equals(accounts.get(1).getAccountSuffix(), "x5485 SAV"));
        assert(StringUtils.equals(accounts.get(1).getBalance(), "$2.21"));
        assertFalse(accounts.get(1).isRedBalance());

        // third account
        assert(StringUtils.equals(accounts.get(2).getAccountName(), "Reserve"));
        assert(StringUtils.equals(accounts.get(2).getAccountSuffix(), "x5485 RES"));
        assert(StringUtils.equals(accounts.get(2).getBalance(), "$0.00"));
        assertTrue(accounts.get(2).isRedBalance());

    }

    @Test
    public void givenNullCustomer_WhenIsValidCustomer_thenReturnFalse() {

        boolean isValidCustomer = MaintenanceWithBalances.isValidCustomer(null);

        assertFalse(isValidCustomer);
    }

    @Test
    public void givenValidCustomerWithEmptyAccounts_WhenIsValidCustomer_thenReturnFalse() {
        Customer customer = new Customer();

        boolean isValidCustomer = MaintenanceWithBalances.isValidCustomer(customer);

        assertFalse(isValidCustomer);
    }
}
