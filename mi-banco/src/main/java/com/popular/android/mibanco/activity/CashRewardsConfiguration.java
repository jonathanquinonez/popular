package com.popular.android.mibanco.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.model.CashRewardsRedemptionModel;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.RedemptionStep;
import com.popular.android.mibanco.model.RedemptionType;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.DialogHolo;
import com.popular.android.mibanco.ws.CustomGsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Mi Banco  - Mi Banco Credit Acquisition-MBCA.
 *
 * @author Stephanie Diaz <Stephanie.Diaz@evertecinc.com>
 * @version 1.0
 */
public class CashRewardsConfiguration extends BaseSessionActivity implements View.OnTouchListener {



    private final static int AMOUNT_PICKER_REQUEST_CODE = 1;

    private final static int ACCOUNT_SELECTION_CODE = 2;
    private final static String AMOUNT = "amount"; /*labels*/
    private final static String PAGE = "_page";/*labels*/
    private final static String IS_CASH_REWARDS_REDEMPTION = "isCashRewardsRedemption";/*labels*/
    private final static String LABEL_OK = "OK";/*labels*/
    private final static String STRING_FORMAT = "%s %s";/*labels*/
    private final static String CLICKABLE_VIEW_ACCESSIBILITY = "ClickableViewAccessibility";/*labels*/
    /**
     * Redemption redemption
     */
    private CashRewardsRedemptionModel redemption;

    /**
     * TextView amountTextView
     */
    private TextView amountTextView;

    /**
     * Statement Credit Linear Layout
     */
    private LinearLayout statementCredit;

    /**
     * Full name with the last 4 numbers TextView
     */
    private TextView cashLast4;

    /**
     * Redemption Button
     */
    private Button confirmBtn;

    /**
     * terms and conditions checkbox
     */
    private CheckBox acceptTerms;


    /**
     * Account Deposit TextView
     */
    private TextView directDepositAcct;
    /**
     * Last 4  Numbers of Account TextView
     */
    private TextView directDepositLast4;
    /**
     * Account Image Selected
     */
    private ImageView directDepositImage;

    /**
     *Direct Deposit Linear Layout
     */

    private LinearLayout directDepositLayout;

    /*
     *method default onstart
     */
    protected void onStart() {
        super.onStart();
        CashRewardsRedemptionModel redemption = this.redemption;
        if (redemption.getRedemptionStep() != null
                && (redemption.getRedemptionStep() == RedemptionStep.notStarted
                || redemption.getRedemptionStep() == RedemptionStep.redemptionConfirmation)) {
            callRedemptionController();
            redemption.setRedemptionStep(RedemptionStep.redemptionConfiguration);
            disableTerms();
            toggleConfirmBtn();
        }
        toggleRedemptionTypes(redemption.getRedemptionType());
        this.redemption = redemption;
    }

    @SuppressLint(CLICKABLE_VIEW_ACCESSIBILITY)
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_rewards_configuration);

        TextView cashRewardsBalance = findViewById(R.id.item_value);
        TextView nickName = findViewById(R.id.item_name);
        TextView last4 = findViewById(R.id.item_comment);
        RelativeLayout directDeposit =  findViewById(R.id.select_acct_deposit);
        TextView selectTxt = findViewById(R.id.txt_select);


        findViewById(R.id.onoff_statement_layout).setVisibility(View.GONE);
        findViewById(R.id.tsys_loyalty_rewards_balance_container).setVisibility(View.GONE);
        amountTextView = findViewById(R.id.amount_text);
        statementCredit = findViewById(R.id.statement_credit);
        directDepositAcct = findViewById(R.id.acct_deposit);
        directDepositLast4 = findViewById(R.id.acct_deposit_last4);
        directDepositLayout =findViewById(R.id.account_deposit);
        directDepositImage = findViewById(R.id.acct_deposit_image);
        cashLast4 = findViewById(R.id.cash_rewards_last4);
        acceptTerms = findViewById(R.id.check_tsys_terms);
        confirmBtn = findViewById(R.id.set_button);
        CashRewardsRedemptionModel redemption = new CashRewardsRedemptionModel();
        CheckBox acceptTerms = this.acceptTerms;
        acceptTerms.setMovementMethod(LinkMovementMethod.getInstance());

        if (App.getApplicationInstance() != null && App.getApplicationInstance()
                .getAsyncTasksManager() != null) {
            if (getIntent().getSerializableExtra(MiBancoConstants.CUSTOMER_ACCOUNT_KEY)
                    instanceof CustomerAccount){
                final CustomerAccount account = (CustomerAccount) getIntent()
                        .getSerializableExtra(MiBancoConstants.CUSTOMER_ACCOUNT_KEY);

                if (account == null) {
                    finish();
                    return;
                }

                redemption.setCashRewardsAccount(account);
                Utils.displayAccountImage((ImageView) findViewById(R.id.item_image), account);
                nickName.setText(account.getNickname());
                last4.setText(account.getAccountLast4Num());
                cashRewardsBalance.setText(account.getTsysLoyaltyRewardsInfo()
                        .getAvailableRewardsBalance());
                cashLast4.setText(String.format(STRING_FORMAT, account.getNickname(),
                        account.getAccountLast4Num()));
                amountTextView.setOnClickListener(onClickAmount());
                confirmBtn.setEnabled(false);
                confirmBtn.setOnClickListener(onClickConfirmButton());
                acceptTerms.setOnClickListener(onClickCheckTerms());
                selectTxt.setText(getString(
                        R.string.tsys_loyalty_rewards_account_deposit_redemption_select));
                selectTxt.setTextColor(getResources().getColor(R.color.blue));
                directDepositAcct.setPadding(0, 5, 0, 0);
                statementCredit.setOnTouchListener(this);
                directDepositLayout.setOnTouchListener(this);
                directDeposit.setOnTouchListener(this);
                directDepositImage.setVisibility(View.INVISIBLE);
                this.acceptTerms = acceptTerms;
                this.redemption = redemption;
                disableTerms();
                toggleConfirmBtn();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CashRewardsRedemptionModel redemption = this.redemption;
        super.onActivityResult(requestCode, resultCode, data);
        {
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK) {
                if (requestCode == AMOUNT_PICKER_REQUEST_CODE) {
                    disableTerms();
                    toggleConfirmBtn();
                    setAmount(data.getIntExtra(AMOUNT, 0));
                }if(requestCode == ACCOUNT_SELECTION_CODE){
                    disableTerms();

                    final CustomerAccount selectedAccount = (CustomerAccount)
                            data.getSerializableExtra(MiBancoConstants.ACCOUNT_DEPOSIT_INFO);
                    redemption.setAccountSelected(selectedAccount);
                    this.redemption = redemption;
                    toggleConfirmBtn();
                    setAccount(selectedAccount);
                }
            }
        }
    }


    private View.OnClickListener onClickAmount() {

        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final Intent intent = new Intent(CashRewardsConfiguration.this,
                        EnterAmount.class);
                intent.putExtra(AMOUNT, redemption.getRedemptionAmount());
                intent.putExtra(IS_CASH_REWARDS_REDEMPTION, true);
                intent.putExtra(MiBancoConstants.CUSTOMER_ACCOUNT_KEY,
                        redemption.getCashRewardsAccount());
                startActivityForResult(intent, AMOUNT_PICKER_REQUEST_CODE);
            }
        };
    }

    private View.OnClickListener onClickCheckTerms() {

        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final boolean checked = acceptTerms.isChecked();
                redemption.setAcceptedTerms(checked);
                toggleConfirmBtn();
            }
        };
    }

    private View.OnClickListener onClickConfirmButton() {

        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                redemption.setRedemptionStep(RedemptionStep.redemptionConfirmation);
                final Intent intent = new Intent(CashRewardsConfiguration.this,
                        CashRewardsConfirmation.class);
                intent.putExtra(MiBancoConstants.CASH_REWARDS_REDEMPTION_MODEL, redemption);
                startActivity(intent);
            }
        };
    }

    private void toggleConfirmBtn() {
        Button confirmBtn = this.confirmBtn;
        CashRewardsRedemptionModel redemption = this.redemption;
        if (redemption.isRedemptionInfoValid()) {
            confirmBtn.setEnabled(true);
            confirmBtn.setBackgroundResource(R.color.cash_back_btn);
        } else {
            confirmBtn.setEnabled(false);
            confirmBtn.setBackgroundResource(R.color.cash_back_disabled_btn);
        }
        this.confirmBtn = confirmBtn;
    }

    private void selectAccount(){
        CashRewardsRedemptionModel redemption = this.redemption;
        final Intent intent = new Intent(CashRewardsConfiguration.this,
                CashRewardsAccountDepositConfiguration.class);
        if(redemption.getDirectDepositAccounts() instanceof Serializable) {
            intent.putExtra(MiBancoConstants.CASH_REWARDS_REDEMPTION_MODEL
                    , (Serializable) redemption.getDirectDepositAccounts());
            intent.putExtra(IS_CASH_REWARDS_REDEMPTION, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, ACCOUNT_SELECTION_CODE);
        }
    }

    private void disableTerms() {
        CheckBox acceptTerms = this.acceptTerms;
        CashRewardsRedemptionModel redemption = this.redemption;
        redemption.setAcceptedTerms(false);
        acceptTerms.setChecked(false);
        this.acceptTerms = acceptTerms;
        this.redemption = redemption;
    }

    private void toggleRedemptionTypes(RedemptionType type) {
        LinearLayout directDepositLayout = this.directDepositLayout;
        CashRewardsRedemptionModel redemption = this.redemption;
        if(type == RedemptionType.statementCredit) {
            statementCredit.setPressed(true);
            directDepositLayout.setPressed(false);
            redemption.setRedemptionType(type);
            //redemption.setCardName(cashLast4.getText().toString());
        }else {
            directDepositLayout.setPressed(true);
            statementCredit.setPressed(false);
            redemption.setRedemptionType(type);
        }
        this.redemption = redemption;
        this.directDepositLayout = directDepositLayout;
    }

    @SuppressLint(CLICKABLE_VIEW_ACCESSIBILITY)
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId() == R.id.statement_credit) {
            toggleRedemptionTypes(RedemptionType.statementCredit);
            disableTerms();
            toggleConfirmBtn();

            return true;

        } else if (view.getId() == R.id.select_acct_deposit
                || view.getId() == R.id.account_deposit) {
            toggleRedemptionTypes(RedemptionType.directDeposit);
            disableTerms();
            toggleConfirmBtn();
            selectAccount();

            return true;
        }
        return false;
    }

    /**
     * Sets the amount TextView.
     */
    private void setAmount(int amount) {
        CashRewardsRedemptionModel redemption = this.redemption;
        redemption.setRedemptionAmount(amount);
        amountTextView.setText(Utils.formatAmount(amount));
        this.redemption = redemption;
    }

    private void setAccount(CustomerAccount selectedAccount) {
        TextView directDepositAcct = this.directDepositAcct;
        directDepositAcct.setVisibility(View.VISIBLE);
        directDepositImage.setVisibility(View.VISIBLE);
        directDepositLast4.setVisibility(View.VISIBLE);
        findViewById(R.id.txt_select).setVisibility(View.GONE);
        directDepositAcct.setTextColor(getResources().getColor(R.color.black));
        directDepositAcct.setText(selectedAccount.getNickname());
        directDepositLast4.setText(selectedAccount.getAccountLast4Num());
        directDepositImage.setImageResource(selectedAccount.getImgResource());
        this.directDepositAcct = directDepositAcct;
    }

    private void callRedemptionController() {
        final CashRewardsRedemptionModel redemption = this.redemption;
        App.getApplicationInstance().getAsyncTasksManager()
                .postTsysLoyaltyRewardsInfoTask(CashRewardsConfiguration.this,
                        redemption.getCashRewardsAccount().getFrontEndId(), getParamsLocal(),
                        new ResponderListener() {

                            @Override
                            public void responder(String responderName, Object data) {
                                try {
                                    if (data instanceof JSONObject){

                                        final JSONObject content = (JSONObject) data;
                                        final CustomGsonParser gson = new CustomGsonParser();
                                        final JSONArray accounts = content.getJSONArray("accounts");
                                        final Integer accountsSize = accounts.length();
                                        if (accountsSize == 0) {
                                            return;
                                        }

                                        List<CustomerAccount> accountsList = new ArrayList<>();
                                        for (int i = 0; i < accountsSize; ++i) {
                                            accountsList.add(gson.fromJson(accounts.get(i).toString(),
                                                    CustomerAccount.class));
                                        }
                                        CashRewardsRedemptionModel redemption = CashRewardsConfiguration.this.redemption;
                                        redemption.setDirectDepositAccounts(accountsList);
                                        CashRewardsConfiguration.this.redemption = redemption;
                                        directDepositLayout.setVisibility(View.VISIBLE);
                                    }
                                } catch (JSONException e) {
                                    final DialogHolo dialog = new DialogHolo(
                                            CashRewardsConfiguration.this);
                                    dialog.setTitleEnabled(false);
                                    dialog.setMessage(R.string
                                            .tsys_loyalty_rewards_confirmation_redemption_unsuccess_title);
                                    dialog.setMessageCenter();

                                    dialog.setConfirmationButton(LABEL_OK, new View.OnClickListener() {

                                        @Override
                                        public void onClick(View view) {
                                            Utils.navigateToPortal(CashRewardsConfiguration.this);
                                        }
                                    });
                                    Utils.showDialog(dialog,
                                            CashRewardsConfiguration.this);
                                    BPAnalytics.logEvent(BPAnalytics
                                            .EVENT_CASH_REWARDS_REDEMPTION_ERROR);
                                }
                            }

                            @Override
                            public void sessionHasExpired() {
                                application.reLogin(CashRewardsConfiguration.this);
                            }
                        });
    }

    private HashMap<String, Object> getParamsLocal() {

        HashMap<String, Object> params = new HashMap<>();
        final CashRewardsRedemptionModel redemption = this.redemption;

        if (redemption.getRedemptionStep() == RedemptionStep.redemptionConfirmation) {
            params.put(PAGE, 0);
        }
        return params;
    }
}



