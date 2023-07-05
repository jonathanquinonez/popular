package com.popular.android.mibanco.activity;


import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.adapter.MaintenanceBalancesAdapter;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.model.Customer;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.widget.AccountNameAndBalanceItem;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


/**
 * Maintenance Screen with the option to show balances if available
 *
 * @author armandojimenez
 */
public class MaintenanceWithBalances extends BaseActivity {

    private List<AccountNameAndBalanceItem> mBalances;
    private Button getBalancesBtn;
    private LinearLayout balancesSection;
    private boolean isShowingBalances = false;
    private SharedPreferences mSharedPreferences;
    private TextView lastUpdatedBalance;
    private RecyclerView rvBalances;
    private NestedScrollView scrollView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_with_balances);
        mSharedPreferences = Utils.getSecuredSharedPreferences(this);


        String maintenanceType = getIntent().getStringExtra(MiBancoConstants.MAINTENANCE_TYPE);

        TextView titleTextView = findViewById(R.id.balances_warning_title);

        if (MiBancoConstants.MAINTENANCE.equals(maintenanceType)) {
            titleTextView.setText(getString(R.string.maintenance_balances_maintenance_title));
        } else if (MiBancoConstants.HIGH_VOLUME.equals(maintenanceType)) {
            titleTextView.setText(getString(R.string.maintenance_balances_high_volume_title));
        }

        String widgetUsername = mSharedPreferences.getString(MiBancoConstants.SHARED_WIDGET_USERNAME, StringUtils.EMPTY);
        String accountsAndBalances = mSharedPreferences.getString(MiBancoConstants.SHARED_WIDGET_BALANCE, StringUtils.EMPTY);

        // User does not have a widget username, do not show button
        getBalancesBtn = findViewById(R.id.get_balances_btn);
        if (StringUtils.isBlank(widgetUsername) && StringUtils.isBlank(accountsAndBalances)) {
            getBalancesBtn.setVisibility(View.GONE);
        } else {
            /// get Cached balance and show button
            getBalancesBtn.setOnClickListener(getBalancesBtnListener());
            getBalancesBtn.setVisibility(View.VISIBLE);

            lastUpdatedBalance = findViewById(R.id.last_updated_balances);
            balancesSection = findViewById(R.id.balances_group);
            scrollView = findViewById(R.id.maintenance_balances_scroll);

            // Manage button visibility depending on balance section
            balancesSection.setTag(balancesSection.getVisibility());
            balancesSection.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if ((int) balancesSection.getTag() != balancesSection.getVisibility()) {

                        int visibility = balancesSection.getVisibility();

                        balancesSection.setTag(visibility);

                        isShowingBalances = visibility == View.VISIBLE;

                        if (isShowingBalances) {
                            // Balance section is now visible, smooth scroll to the section header
                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Scroll to the content for a better UX experience
                                    scrollView.smoothScrollBy(0, balancesSection.getTop());
                                }
                            }, 200);
                        }
                    }
                }
            });

            // Mask User
            TextView maskedUsername = findViewById(R.id.masked_username);
            maskedUsername.setText(Utils.maskUsername(widgetUsername, MiBancoConstants.MASK_PATTERN_LENGTH));


            mBalances = new ArrayList<>();
            showCurrentCachedBalance(mBalances, accountsAndBalances);
            String lastUpdatedOn = mSharedPreferences.getString(MiBancoConstants.SHARED_WIDGET_LAST_UPDATED, StringUtils.EMPTY);
            lastUpdatedBalance.setText(getLastUpdatedDate(lastUpdatedOn));

            // Configure Recycler View
            rvBalances = findViewById(R.id.rv_balances);
            rvBalances.setLayoutManager(new LinearLayoutManager(this));
            rvBalances.setNestedScrollingEnabled(false);
            rvBalances.setHasFixedSize(true);
            scrollView.setFillViewport(true);

            // Add divider between balances
            DividerItemDecoration verticalDecoration = new DividerItemDecoration(rvBalances.getContext(),
                    DividerItemDecoration.VERTICAL);
            Drawable verticalDivider = ContextCompat.getDrawable(this, R.drawable.maintenance_balances_divider);
            if (verticalDivider != null) {
                verticalDecoration.setDrawable(verticalDivider);
                rvBalances.addItemDecoration(verticalDecoration);
            }
            // Set RecyclerView adapter
            rvBalances.setAdapter(new MaintenanceBalancesAdapter(mBalances, this));
        }
    }

    /**
     * Verify if the given name is more than MiBancoConstants.WIDGET_MAX_UPDATE_TIME
     *
     * @return boolean if should update the balance
     */
    @VisibleForTesting
    public static boolean shouldUpdateBalances(String date) {
        if (StringUtils.isNotBlank(date)) {
            Date today = new Date();
            Date lastUpdate;
            try {
                lastUpdate = new SimpleDateFormat(MiBancoConstants.WIDGET_LAST_UPDATE_FORMAT,
                        Locale.US).parse(date);

                long parsedLastUpdate = 0;

                if (lastUpdate != null) {
                    parsedLastUpdate = lastUpdate.getTime();
                }

                Date afterAddingTenMinutes = new Date(parsedLastUpdate + MiBancoConstants.WIDGET_MAX_UPDATE_TIME);

                return afterAddingTenMinutes.before(today);
            } catch (ParseException e) {
                return true;
            }
        }
        return true;
    }

    /**
     * Parse the saved string into a list of AccountNameAndBalanceItem
     *
     * @return List<AccountNameAndBalanceItem>
     */
    @VisibleForTesting
    public static List<AccountNameAndBalanceItem> showCurrentCachedBalance(List<AccountNameAndBalanceItem> itemList, String balances) {
        itemList.clear();

        if (StringUtils.isNotBlank((balances))) {
            int indexAccountName = 0;
            int indexAccountSuffix = 1;
            int indexAccountBalance = 2;
            int indexBalanceColor = 3;
            String[] accountInfo = balances.split(MiBancoConstants.WIDGET_CONTENT_SPLIT);
            for (String information : accountInfo) {
                String[] parsedAccountInformation = information.split(MiBancoConstants.WIDGET_CONTENT_DIVIDER);
                if (parsedAccountInformation.length == 4) {
                    AccountNameAndBalanceItem acctInfo = new AccountNameAndBalanceItem();
                    acctInfo.setAccountName(parsedAccountInformation[indexAccountName].trim());
                    acctInfo.setAccountSuffix(parsedAccountInformation[indexAccountSuffix].trim());
                    acctInfo.setBalance(parsedAccountInformation[indexAccountBalance].trim());
                    acctInfo.setRedBalance(MiBancoConstants.WIDGET_RED_BALANCE
                            .equalsIgnoreCase(parsedAccountInformation[indexBalanceColor]));
                    itemList.add(acctInfo);
                }
            }
            return itemList;
        }
        return itemList;
    }

    /**
     * Get and format the last updated date for the widget
     *
     * @return the formatted date
     */
    public String getLastUpdatedDate(String lastUpdatedOn) {
        if (StringUtils.isNotBlank(lastUpdatedOn)) {
            // Parse the date format
            SimpleDateFormat englishFormatter = new SimpleDateFormat(MiBancoConstants.WIDGET_EN_DATE_FORMAT, Locale.US);
            SimpleDateFormat spanishFormatter = new SimpleDateFormat(MiBancoConstants.WIDGET_ES_DATE_FORMAT,
                    new Locale(MiBancoConstants.SPANISH_LANGUAGE_CODE, MiBancoConstants.SPANISH_LANGUAGE_CODE.toUpperCase()));
            Date todayDate = new Date();
            Date date;
            try {
                date = new SimpleDateFormat(MiBancoConstants.WIDGET_LAST_UPDATE_FORMAT, Locale.US).parse(lastUpdatedOn);
                if (date != null) {
                    String formattedDate = getString(R.string.last_updated_on);
                    String lang = application.getLanguage();
                    if (Utils.dateResetTime(date).equals(Utils.dateResetTime(todayDate))) {
                        if (lang.equals(MiBancoConstants.SPANISH_LANGUAGE_CODE)) {
                            formattedDate += getString(R.string.maintenance_balances_today_at)
                                    + (new SimpleDateFormat(MiBancoConstants.MOBILE_CASH_TIME_FORMAT,
                                    new Locale(MiBancoConstants.SPANISH_LANGUAGE_CODE,
                                            MiBancoConstants.SPANISH_LANGUAGE_CODE.toUpperCase()))).format(date);
                        } else {
                            formattedDate += getString(R.string.maintenance_balances_today_at)
                                    + (new SimpleDateFormat(MiBancoConstants.MOBILE_CASH_TIME_FORMAT,
                                    Locale.US)).format(date);
                        }
                    } else {
                        if (lang.equals(MiBancoConstants.SPANISH_LANGUAGE_CODE)) {
                            formattedDate += StringUtils.SPACE + spanishFormatter.format(date);
                        } else {
                            formattedDate += StringUtils.SPACE + englishFormatter.format(date);
                        }

                    }

                    return formattedDate;
                }
                return StringUtils.EMPTY;
            } catch (ParseException e) {
                return StringUtils.EMPTY;
            }
        }
        return StringUtils.EMPTY;
    }


    /**
     * Get and format the last updated date for the widget
     *
     * @return accounts and balances in string format
     */
    @VisibleForTesting
    public static String updateBalances(Customer customer) {

        if (isValidCustomer(customer)) {

            LinkedList<CustomerAccount> accounts = populateAccounts(customer);


            StringBuilder accountsAndBalances = new StringBuilder();
            for (CustomerAccount account : accounts) {
                accountsAndBalances.append(account.getNickname())
                        .append(MiBancoConstants.WIDGET_CONTENT_DIVIDER);

                String accountSuffix = StringUtils.EMPTY;

                if (StringUtils.equals(account.getAccountNumberSuffix(), StringUtils.EMPTY)) {
                    accountSuffix = StringUtils.SPACE + account.getAccountNumberSuffix();
                }

                accountsAndBalances.append(account.getAccountLast4Num()).append(accountSuffix)
                        .append(MiBancoConstants.WIDGET_CONTENT_DIVIDER);
                accountsAndBalances.append(account.getPortalBalance())
                        .append(MiBancoConstants.WIDGET_CONTENT_DIVIDER);

                String isRedBalance = MiBancoConstants.WIDGET_BLUE_BALANCE_WITH_DELIMITER;

                if (account.isBalanceColorRed()) {
                    isRedBalance = MiBancoConstants.WIDGET_RED_BALANCE_WITH_DELIMITER;
                }

                accountsAndBalances.append(isRedBalance);
            }

            return String.valueOf(accountsAndBalances);
        }

        return StringUtils.EMPTY;
    }

    /**
     * Validate the passed Customer and check if has accounts
     *
     * @return if its a valid customer
     */
    @VisibleForTesting
    public static boolean isValidCustomer(Customer customer) {
        return customer != null &&
                ((customer.getCreditCards() != null && !customer.getCreditCards().isEmpty())
                        || (customer.getDepositAccounts() != null && !customer.getDepositAccounts().isEmpty()));
    }

    /**
     * Populate the Customer list with the provide accounts
     *
     * @return LinkedList<CustomerAccount>
     */
    @VisibleForTesting
    public static LinkedList<CustomerAccount> populateAccounts(Customer customer) {
        LinkedList<CustomerAccount> accounts = new LinkedList<>();
        if (customer.getDepositAccounts() != null && !customer.getDepositAccounts().isEmpty())
            accounts.addAll(customer.getDepositAccounts());

        if (customer.getCreditCards() != null && !customer.getCreditCards().isEmpty())
            accounts.addAll(customer.getCreditCards());

        return accounts;
    }

    /**
     * OnClickListener for the balance button
     *
     * @return a View.OnClickListener
     */
    private View.OnClickListener getBalancesBtnListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isShowingBalances) {
                    // Hide section
                    balancesSection.setVisibility(View.GONE);
                    getBalancesBtn.setText(getString(R.string.maintenance_balances_view_balances));

                } else {
                    balancesSection.setVisibility(View.VISIBLE);
                    getBalancesBtn.setText(getString(R.string.maintenance_balances_hide_balances));
                    BPAnalytics.logEvent(BPAnalytics.EVENT_MAINTENANCE_BALANCE_REQUESTED);

                    // Get last updated time for balances
                    String lastUpdatedOn = mSharedPreferences.getString(MiBancoConstants.SHARED_WIDGET_LAST_UPDATED, StringUtils.EMPTY);
                    // Get last time the call failed, so we avoid spamming
                    String lastFailOn = mSharedPreferences.getString(MiBancoConstants.SHARED_WIDGET_BALANCE_FAIL, StringUtils.EMPTY);

                    // To be able to fetch, both dates should be more than ten minutes
                    if (shouldUpdateBalances(lastUpdatedOn) && shouldUpdateBalances(lastFailOn)) {
                        // update with a call
                        final String customerToken = mSharedPreferences.getString(MiBancoConstants.SHARED_WIDGET_CUSTOMER_TOKEN, StringUtils.EMPTY);
                        final String deviceId = mSharedPreferences.getString(MiBancoConstants.SHARED_WIDGET_DEVICE, StringUtils.EMPTY);

                        // Customer has a token and device ID, lets fetch (Android save the widget_device_id on second login
                        if (canUpdate(customerToken, deviceId)) {
                            application.getAsyncTasksManager().getBalances(MaintenanceWithBalances.this,
                                    customerToken + deviceId, new ResponderListener<Object>() {
                                        @Override
                                        public void responder(String responderName, Object data) {

                                            if (StringUtils.equals(responderName, "error")) {
                                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                                editor.putString(MiBancoConstants.SHARED_WIDGET_BALANCE_FAIL,
                                                        Calendar.getInstance().getTime().toString());
                                                editor.apply();
                                                return;
                                            }

                                            Customer customer = data instanceof Customer ? (Customer) data : null;

                                            String accounts = updateBalances(customer);


                                            if (StringUtils.isNotBlank(accounts)) {
                                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                                editor.putString(MiBancoConstants.SHARED_WIDGET_LAST_UPDATED, Calendar.getInstance().getTime().toString());
                                                editor.putString(MiBancoConstants.SHARED_WIDGET_BALANCE, accounts);
                                                editor.apply();
                                                // Now, update refresh RecyclerView

                                                showCurrentCachedBalance(mBalances, accounts);

                                                String lastUpdatedOn = mSharedPreferences.getString(MiBancoConstants.SHARED_WIDGET_LAST_UPDATED,
                                                        StringUtils.EMPTY);
                                                // Update TextView last updated text
                                                lastUpdatedBalance.setText(getLastUpdatedDate(lastUpdatedOn));

                                                // invalidate the RecyclerView
                                                rvBalances.setAdapter(new MaintenanceBalancesAdapter(mBalances,
                                                        MaintenanceWithBalances.this));

                                                rvBalances.invalidate();
                                            }
                                        }

                                        @Override
                                        public void sessionHasExpired() {
                                        }
                                    });
                        }
                    }


                }
            }
        };
    }

    /**
     * Verify if the customer has the required data to make a fetch
     *
     * @return boolean if can update
     */
    @VisibleForTesting
    public static boolean canUpdate(String customerToken, String deviceId) {
        return StringUtils.isNotBlank(customerToken) && StringUtils.isNotBlank(deviceId);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        // Hide the toolbar options
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(false);
        menu.findItem(R.id.menu_contact).setVisible(false);
        return true;
    }

}