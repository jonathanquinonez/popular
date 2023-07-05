package com.popular.android.mibanco.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.adapter.ViewPagerAdapter;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.listener.SimpleListener;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.OnOffPlastics;
import com.popular.android.mibanco.model.TsysLoyaltyRewardsInfo;
import com.popular.android.mibanco.model.TsysLoyaltyRewardsRecurringStatementCreditResponse;
import com.popular.android.mibanco.util.AccountStatementsLoader;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.MobileCashUtils;
import com.popular.android.mibanco.util.Utils;
import com.viewpagerindicator.TitlePageIndicator;

/**
 * Account details Activity class.
 * @see Accounts
 * @since 1.0
 * @version 1.0
 */
public class AccountDetails extends BaseSessionActivity {

    /**
     *  SELECT_STATEMENT_RESULT
     */
    private final static int SELECT_STATEMENT_RESULT = 2467; // SELECT_STATEMENT_RESULT

    /**
     * MAX_PAGER_PAGES
     */
    private final static int MAX_PAGER_PAGES = 3; // MAX_PAGER_PAGES

    /**
     * accFrontEndId
     */
    private String accFrontEndId; // accFrontEndId

    /**
     * helper
     */
    private AccountStatementsLoader helper; // helper

    /**
     * listener
     */
    private SimpleListener listener; // listener

    /**
     *pager
     */
    private ViewPager pager; // pager

    /**
     *pagerAdapter
     */
    private ViewPagerAdapter pagerAdapter; // pagerAdapter

    /**
     * account
     */
    private CustomerAccount account; // account

    /**
     * lastOnOffClickTime
     */
    private long lastOnOffClickTime = 0; // lastOnOffClickTime


    /**
     *  button_more_transactions
     */
    private Button btnMoreTransactions; // button_more_transactions

    /**
     *  TitlePageIndicator
     */
    private TitlePageIndicator indicator; // TitlePageIndicator

    /**
     *  TextView
     */
    private TextView onOffLabel; // TextView

    /**
     *  View
     */
    private View cashRewardsView; // View

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult (final int requestCode, final int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (App.getApplicationInstance() != null && App.getApplicationInstance().getAsyncTasksManager() != null) {

            if (resultCode == RESULT_OK && requestCode == SELECT_STATEMENT_RESULT) {

                final int tempCycle = data.getIntExtra("cycle", 1); // int value

                if (helper.getCycle() != tempCycle) {

                    btnMoreTransactions = findViewById(R.id.button_more_transactions); // button

                    helper = new AccountStatementsLoader(this, (App) getApplication(),
                            btnMoreTransactions, accFrontEndId, tempCycle, listener);
                    helper.refreshList();
                    BPAnalytics.logEvent(BPAnalytics.EVENT_ACCOUNT_STATEMENT_SELECTED_CYCLE,
                            "cycle", Integer.toString(helper.getCycle()));
                }
            }
        }
    }

    /**
     * OnResume Method
     */
    @Override
    protected void onResume () {

        super.onResume();

        if (FeatureFlags.ONOFF() && account.isOnOffElegible()) {

            if (account.getOnOffCount() > 0) {

                ((TextView) findViewById(R.id.onoff_plastic_count)).setText(Integer.toString(account.getOnOffCount()));
                findViewById(R.id.onoff_statement_frame).setVisibility(View.VISIBLE);
            } else {

                findViewById(R.id.onoff_statement_frame).setVisibility(View.GONE);
            }

            findViewById(R.id.onoff_statement_layout).setOnTouchListener(onTouchOnOffButton());
        } else {

            findViewById(R.id.onoff_statement_layout).setVisibility(View.GONE);
        }

        if (FeatureFlags.CASH_REWARDS() && account.isCashRewardsEligible()) {

            TsysLoyaltyRewardsInfo tsysLoyaltyRewardsInfo = account.getTsysLoyaltyRewardsInfo(); // tsysLoyaltyRewardsInfo
            findViewById(R.id.tsys_loyalty_rewards_balance_container).setVisibility(View.VISIBLE);

            if (tsysLoyaltyRewardsInfo != null) {

                initializeCashRewardsComponents(tsysLoyaltyRewardsInfo);
            } else {

                loadTsysLoyaltyRewardsInfo(account);
            }
        } else {

            findViewById(R.id.tsys_loyalty_rewards_balance_container).setVisibility(View.GONE);
        }
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate (final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_details);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        if (App.getApplicationInstance() != null && App.getApplicationInstance().getAsyncTasksManager() != null) {
            account = (CustomerAccount) getIntent().getSerializableExtra(MiBancoConstants.CUSTOMER_ACCOUNT_KEY);
            if (account == null) {
                finish();
                return;
            }
            accFrontEndId = account.getFrontEndId();

            Utils.displayAccountImage((ImageView) findViewById(R.id.item_image), account);

            ((TextView) findViewById(R.id.item_name)).setText(account.getNickname());
            ((TextView) findViewById(R.id.item_value)).setText(account.getPortalBalance());

            if (account.isBalanceColorRed()) {

                ((TextView) findViewById(R.id.item_value)).setTextColor(ContextCompat.getColor(this, R.color.account_details_header_debit_balance));
                ((TextView) findViewById(R.id.item_value)).setText(((TextView) findViewById(R.id.item_value)).getText());
            }

            ((TextView) findViewById(R.id.item_comment)).setText(account.getAccountLast4Num());

            pager = findViewById(R.id.view_pager);
            pager.setOffscreenPageLimit(MAX_PAGER_PAGES);

            listener = new SimpleListener() {

                @Override
                public void done () {

                    updateListView();
                }
            };

            btnMoreTransactions = findViewById(R.id.button_more_transactions);
            helper = new AccountStatementsLoader(this, (App) getApplication(), btnMoreTransactions, account.getFrontEndId(), 1, listener);
            helper.refreshList();

            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    new IntentFilter(MiBancoConstants.ONOFF_RELOAD_INDICATOR));
        }
    }

    /**
     *
     * @param v
     */
    @Override
    public void onClick (View v) {

        super.onClick(v);
    }

    /**
     *
     * @param item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected (final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_select_statement:
                selectStatement();
                break;
            case R.id.menu_automatic_redemption:
                activateAutomaticRedemption();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    /**
     *
     * @param menu
     * @return boolean
     */
    @SuppressLint("NewApi")
    @Override
    public boolean onPrepareOptionsMenu (final Menu menu) {

        super.onPrepareOptionsMenu(menu);

        if (account.isCashRewardsEligible()) {
            menu.findItem(R.id.menu_automatic_redemption).setVisible(true);
            menu.findItem(R.id.menu_logout).setVisible(false);
        }

        if (account != null && !App.isSelectedNonTransactioanlAcct(account.getSubtype()))
            menu.findItem(R.id.menu_select_statement).setVisible(true);

        return true;
    }

    /**
     *
     */
    @Override
    protected void onStart () {

        super.onStart();
        BPAnalytics.onStartSession(this);
        BPAnalytics.logEvent(BPAnalytics.EVENT_ACCOUNT_STATEMENT_REQUEST);
    }

    /**
     * onStop method
     */
    @Override
    protected void onStop () {

        super.onStop();
        BPAnalytics.onEndSession(this);
    }

    /**
     * onDestroy method
     */
    @Override
    protected void onDestroy () {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    /**
     * Show Automatic Redemption process
     */
    private void activateAutomaticRedemption () {

        final String frontEndIdName = "accFrontEndId"; // FrontEndId param name
        final String responseName = "tsysResponse"; // Api response param
        final String methodAction = "GET"; // Method param value

        if (App.getApplicationInstance() != null && App.getApplicationInstance().getAsyncTasksManager() != null) {
            App.getApplicationInstance().getAsyncTasksManager()
                    .getTsysLoyaltyRewardsRecurringStatementCreditInfoTask(AccountDetails.this,
                    accFrontEndId, methodAction, new ResponderListener() {

                        @Override
                        public void responder(String responderName, final Object data) {

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    TsysLoyaltyRewardsRecurringStatementCreditResponse
                                            response = null; //Api response data

                                    if (data instanceof TsysLoyaltyRewardsRecurringStatementCreditResponse) {
                                        response = (TsysLoyaltyRewardsRecurringStatementCreditResponse) data;
                                    }

                                    final Intent iAutomaticRedemption = new Intent(AccountDetails.this, AutomaticRedemptionActivity.class); // intent variable

                                    iAutomaticRedemption.putExtra(frontEndIdName, AccountDetails.this.accFrontEndId);
                                    iAutomaticRedemption.putExtra(responseName, response);

                                    startActivity(iAutomaticRedemption);
                                }
                            });
                        }

                        @Override
                        public void sessionHasExpired() {
                            application.reLogin(AccountDetails.this);
                        }
                    });
        }
    }

    /**
     * selectStatement
     */
    private void selectStatement () {

        final Intent iSelect = new Intent(AccountDetails.this, SelectStatement.class); // intent

        iSelect.putExtra("account", pagerAdapter.getAccNr());
        iSelect.putExtra("cycle", helper.getCurrentTransactions().getCurrentCycle());

        AccountDetails.this.startActivityForResult(iSelect, SELECT_STATEMENT_RESULT);
    }

    /**
     * updateListView
     */
    private void updateListView () {

        if (App.getApplicationInstance() != null && App.getApplicationInstance().getAsyncTasksManager() != null) {

            indicator = findViewById(R.id.titles_indicator); // indicator

            if (indicator == null) {
                return;
            }

            pagerAdapter = new ViewPagerAdapter(AccountDetails.this, helper.getCurrentTransactions(), helper.getAllSortedTransactions(), helper.getCycle(), accFrontEndId, account.getSubtype());
            pager.setAdapter(pagerAdapter);
            pager.setCurrentItem(1);
            indicator.setViewPager(pager);
            pager.invalidate();
        }
    }

    /**
     * onTouchOnOffButton
     * @return View
     */
    private View.OnTouchListener onTouchOnOffButton () {
        {
            return new View.OnTouchListener() {

                @Override
                public boolean onTouch(final View v, final MotionEvent event) {

                    onOffLabel = findViewById(R.id.onoff_label); // TextView

                    int action = event.getAction(); // action
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            onOffLabel.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_light));
                            break;
                        case MotionEvent.ACTION_UP:

                            onOffLabel.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));

                            // Prevent double clicks on button
                            if (SystemClock.elapsedRealtime() - lastOnOffClickTime < 1000) {
                                break;
                            }

                            lastOnOffClickTime = SystemClock.elapsedRealtime();

                            if (App.getApplicationInstance() != null && App.getApplicationInstance().getAsyncTasksManager() != null) {

                                final CustomerAccount account = (CustomerAccount) getIntent()
                                        .getSerializableExtra(MiBancoConstants.CUSTOMER_ACCOUNT_KEY); //customer account

                                if (account == null) {
                                    MobileCashUtils.informativeMessage(v.getContext(), R.string.on_off_error_plastics);
                                }

                                App.getApplicationInstance().getAsyncTasksManager().getMobileAthPlastics(AccountDetails.this, accFrontEndId, new ResponderListener() {

                                    @Override
                                    public void responder(String responderName, Object data) {

                                        if (data == null) {
                                            MobileCashUtils.informativeMessage(v.getContext(), R.string.on_off_error_plastics);
                                            return;
                                        }

                                        final Gson gson = new Gson(); // gson variable
                                        onOffLabel = findViewById(R.id.onoff_label); // textview
                                        onOffLabel.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));

                                        BPAnalytics.logEvent(BPAnalytics.EVENT_ONOFF_PLASTIC_INQUIRY_SUCCESS);
                                        OnOffPlastics plastics = (OnOffPlastics) data; //plastics

                                        Intent intent = new Intent(v.getContext(), OnOffAccountPlastics.class); // intent

                                        intent.putExtra("plastics", gson.toJson(plastics));
                                        intent.putExtra(MiBancoConstants.CUSTOMER_ACCOUNT_KEY, account);

                                        startActivity(intent);

                                    }

                                    @Override
                                    public void sessionHasExpired () {

                                        application.reLogin(AccountDetails.this);
                                    }
                                });
                            }
                    }
                    return true;
                }
            };
        }
    }

    /**
     * New Account Counter
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive (Context context, Intent intent) {

            // Get extra data included in the Intent
            int counter = intent.getIntExtra("newAccountCounter", 0); // counter

            account.setOnOffCount(account.getOnOffCount() + counter);
        }
    };

    /**
     * initializeCashRewardsComponents
     * @param tsysLoyaltyRewardsInfo
     */
    private void initializeCashRewardsComponents (TsysLoyaltyRewardsInfo tsysLoyaltyRewardsInfo) {

        //Get necessary widgets
        cashRewardsView = findViewById(R.id.tsys_loyalty_rewards_balance_container); // view
        cashRewardsView.setOnClickListener(onClickCashRewardsButton());

        TextView tsysLoyaltyRewardsTitle = findViewById(R.id.tsys_loyalty_rewards_title); // text view
        TextView tsysLoyaltyRewardsBalance = findViewById(R.id.tsys_loyalty_rewards_balance); // text view

        View cashBackErrorContainer = findViewById(R.id.tsys_loyalty_rewards_error_container); // view
        TextView errorText = findViewById(R.id.tsys_loyalty_rewards_error_message); // text view

        boolean isButtonClickable = false; // boolean value

        //If there was an error fetching the data null is received.
        if (tsysLoyaltyRewardsInfo == null) {

            cashRewardsView.setClickable(isButtonClickable);
            tsysLoyaltyRewardsBalance.setText(R.string.tsys_loyalty_rewards_no_balance_placeholder);
        } else {

            // Button should only be clickable when tsys canRedeemRewards flag is true and the
            // the available balance is greater or equal than the minimum required.
            isButtonClickable = tsysLoyaltyRewardsInfo.getCanRedeemRewards() &&
                    tsysLoyaltyRewardsInfo.getAvailableBalanceDouble() >=
                            tsysLoyaltyRewardsInfo.getMinimumRewardsBalance() &&
                    // "N/A is set if no balance information is available"
                    !tsysLoyaltyRewardsInfo.getAvailableRewardsBalance().equalsIgnoreCase(
                            getString(R.string.tsys_loyalty_rewards_no_balance_placeholder));

            cashRewardsView.setClickable(isButtonClickable);

            tsysLoyaltyRewardsBalance.setText(tsysLoyaltyRewardsInfo.getAvailableRewardsBalance());
        }

        //If the button is not clickable prepare error message
        if (!isButtonClickable) {

            cashBackErrorContainer.setVisibility(View.VISIBLE);
            cashRewardsView.setBackgroundResource(
                    R.drawable.tsys_loyalty_rewards_disabled_background);

            tsysLoyaltyRewardsTitle.setTextColor(getResources()
                    .getColor(R.color.cash_back_disabled_text));
            tsysLoyaltyRewardsBalance.setTextColor(getResources()
                    .getColor(R.color.cash_back_disabled_text));

            if (tsysLoyaltyRewardsInfo == null || !tsysLoyaltyRewardsInfo.getCanRedeemRewards()
                    || tsysLoyaltyRewardsInfo.getAvailableRewardsBalance().equalsIgnoreCase(
                    getString(R.string.tsys_loyalty_rewards_no_balance_placeholder))) {

                errorText.setText(R.string.tsys_loyalty_rewards_customer_service_error_message);
            } else {

                errorText.setText(R.string.tsys_loyalty_rewards_minimum_balance_error_message);
            }
        }
    }

    /**
     * onClickCashRewardsButton
     * @return View
     */
    private View.OnClickListener onClickCashRewardsButton () {

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //When MBCA-932 Activity is being created the code to open that activity goes here.
                final Intent intent = new Intent(AccountDetails.this, CashRewardsConfiguration.class); // intent
                intent.putExtra(MiBancoConstants.CUSTOMER_ACCOUNT_KEY, account);
                startActivity(intent);
            }
        };
    }

    /**
     * loadTsysLoyaltyRewardsInfo
     * @param account
     */
    private void loadTsysLoyaltyRewardsInfo (final CustomerAccount account) {

        final ImageView loading = findViewById(R.id.tsys_loyalty_rewards_loading); // image view

        //Set up loading animation
        Animation loadingAnimation = AnimationUtils
                .loadAnimation(this, R.anim.quick_spin_animation); // loading
        loadingAnimation.reset();

        //Start loading animation
        loading.clearAnimation();
        loading.setVisibility(View.VISIBLE);
        loading.startAnimation(loadingAnimation);

        //Make async call to get the tsys loyalty rewards info.
        application.getAsyncTasksManager().getTsysLoyaltyRewardsInfoTask(AccountDetails.this,
                account.getFrontEndId(), new ResponderListener() {

                    @Override
                    public void responder (String responderName, final Object data) {

                        //UI changes should run on ui thread
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                //Remove loading animation
                                loading.clearAnimation();
                                loading.setVisibility(View.GONE);

                                //If an error occurred show no balance placeholder
                                if (data == null) {
                                    initializeCashRewardsComponents(null);
                                    return;
                                }

                                TsysLoyaltyRewardsInfo tsysLoyaltyRewardsInfo = (TsysLoyaltyRewardsInfo) data; //api response

                                //The balance may be null in cases where the account is
                                //closed for example so N/A is set to be shown in the widget.
                                if (tsysLoyaltyRewardsInfo.getAvailableRewardsBalance() == null
                                        || tsysLoyaltyRewardsInfo.getAvailableRewardsBalance()
                                        .isEmpty()) {
                                    tsysLoyaltyRewardsInfo.setAvailableRewardsBalance(getString(
                                            R.string.tsys_loyalty_rewards_no_balance_placeholder));
                                }

                                //Set tsys loyalty rewards info in CustomerAccount object to avoid
                                //additional async calls.
                                account.setTsysLoyaltyRewardsInfo(tsysLoyaltyRewardsInfo);

                                initializeCashRewardsComponents(tsysLoyaltyRewardsInfo);
                            }
                        });
                    }

                    @Override
                    public void sessionHasExpired() {
                        application.reLogin(AccountDetails.this);
                    }
                });
    }

    /**
     * paintButtonsHeader
     */
    private void paintButtonsHeader () {

        RelativeLayout onOffView = findViewById(R.id.onoff_statement_layout); // layout
        cashRewardsView = findViewById(R.id.tsys_loyalty_rewards_balance_container); // view

        if (cashRewardsView.getVisibility() != View.GONE && onOffView.getVisibility() != View.GONE) {

            LinearLayout.LayoutParams onOffParams = (LinearLayout.LayoutParams) onOffView.getLayoutParams(); // layout
            onOffParams.setMarginStart(Utils.pixelsToDp(8, this));
            onOffView.setGravity(Gravity.CENTER);
            onOffView.setLayoutParams(onOffParams);
            LinearLayout.LayoutParams onCashRewardsParams = (LinearLayout.LayoutParams) cashRewardsView.getLayoutParams(); // layout
            onCashRewardsParams.setMarginEnd(Utils.pixelsToDp(8, this));
            cashRewardsView.setLayoutParams(onCashRewardsParams);

            LinearLayout counterView = findViewById(R.id.onoff_statement_frame); //layout
            RelativeLayout.LayoutParams counterViewParams = (RelativeLayout.LayoutParams) counterView.getLayoutParams(); // layout
            counterViewParams.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
            counterViewParams.addRule(RelativeLayout.END_OF, R.id.onoff_label);
            counterViewParams.setMarginStart(Utils.pixelsToDp(6, this));
            counterViewParams.setMarginEnd(Utils.pixelsToDp(0, this));
            counterView.setLayoutParams(counterViewParams);

            onOffLabel = findViewById(R.id.onoff_label); //text view
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) onOffLabel.getLayoutParams(); // layout
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
            layoutParams.setMarginStart(Utils.pixelsToDp(0, this));
            onOffLabel.setLayoutParams(layoutParams);

            if (counterView.getVisibility() != View.GONE) {

                onOffLabel.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
            else {

                onOffLabel.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.onoff_plastic_card_blue), null, null, null);
            }
        }
    }

}
