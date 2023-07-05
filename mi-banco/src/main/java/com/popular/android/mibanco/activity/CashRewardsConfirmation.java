package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.model.CashRewardsRedemptionModel;
import com.popular.android.mibanco.model.RedemptionStep;
import com.popular.android.mibanco.model.RedemptionType;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.DialogHolo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Mi Banco  - Mi Banco Credit Acquisition-MBCA.
 *
 * @author Stephanie Diaz <Stephanie.Diaz@evertecinc.com>
 * @since 1.0
 * @version 1.1
 */
public class CashRewardsConfirmation extends BaseSessionActivity {


    private CashRewardsRedemptionModel redemption;
    private TextView redeemDate;
    private TextView cashLast4;


    private static final String TARGET1 = "_target1";
    private static final String REDEMPTION_TYPE = "redemptionType";
    private static final String REDEEM_AMOUNT = "redeemAmount";
    private static final String TERMS_AND_COND = "termsAndCond";
    private static final String ACCT_NUMBER = "acctNumber";
    private static final String STRING_FORMAT = "%s %s";
    private static final String BUTTON_LABEL_CONFIRMATION = "OK";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_rewards_confirmation);

        cashRewardsConfirmationToolBar();
        cashLast4 = findViewById(R.id.redeem_acct);
        TextView amountTextView = findViewById(R.id.amount_text);
        TextView redeemType = findViewById(R.id.redeem_type);
        redeemDate = findViewById(R.id.redeem_date);
        Button redeemBtn = findViewById(R.id.redeem_btn);

        if (App.getApplicationInstance() != null && App.getApplicationInstance().getAsyncTasksManager() != null) {
            if (getIntent().getSerializableExtra(MiBancoConstants.CASH_REWARDS_REDEMPTION_MODEL) instanceof CashRewardsRedemptionModel){
                redemption = (CashRewardsRedemptionModel) getIntent().getSerializableExtra(MiBancoConstants.CASH_REWARDS_REDEMPTION_MODEL);
                CashRewardsRedemptionModel redemption = this.redemption;
                if (redemption == null || !redemption.isAcceptedTerms()) {
                    finish();
                    return;
                }

                redemption.setRedemptionStep(RedemptionStep.redemptionConfirmation);

                if (redemption.getRedemptionType().equals(RedemptionType.statementCredit)) {
                    redeemType.setText(R.string.tsys_loyalty_rewards_confirmation_redemption_type_statement_credit);
                } else if (redemption.getRedemptionType().equals(RedemptionType.directDeposit)) {

                    cashLast4.setText(String.format(STRING_FORMAT, redemption.getAccountSelected().getNickname()
                            , redemption.getAccountSelected().getAccountLast4Num()));
                    redeemType.setText(getResources()
                            .getString(R.string
                                    .tsys_loyalty_rewards_confirmation_redemption_type_account_deposit));

                }

                callRedemptionController();
                amountTextView.setText(Utils.formatAmount(redemption.getRedemptionAmount()));
                redeemBtn.setOnClickListener(onClickRedemptionButton());
            }
        } else {
            finish();
        }
    }

    private View.OnClickListener onClickRedemptionButton(){

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String processName = "process"; //variable name
                final String processValue = "CASH_REWARDS_CONFIRMATION"; //process name

                final Intent intent = new Intent(CashRewardsConfirmation.this,
                        CashRewardsRedemptionResult.class);
                intent.putExtra(MiBancoConstants.CASH_REWARDS_REDEMPTION_MODEL, redemption);
                intent.putExtra(processName, processValue);

                startActivity(intent);
            }
        };
    }

    private void callRedemptionController(){
        final CashRewardsRedemptionModel redemption = this.redemption;
        App.getApplicationInstance().getAsyncTasksManager()
                .postTsysLoyaltyRewardsInfoTask(CashRewardsConfirmation.this,
                        redemption.getCashRewardsAccount().getFrontEndId(), getParamsLocal()
                        , new ResponderListener() {
            @Override
            public void responder(String responderName, Object data) {
                try {
                    if(data instanceof JSONObject) {
                        final JSONObject content = (JSONObject) data;
                        final String date = content.getString("date");
                        final String card = content.getString("accountTo");
                        redeemDate.setText(date);
                        cashLast4.setText(card);
                        if(redemption.getRedemptionType().equals(RedemptionType.statementCredit))
                        BPAnalytics.logEvent(BPAnalytics.EVENT_CASH_REWARDS_REDEMPTION_STAT_CRED);
                        else if(redemption.getRedemptionType().equals(RedemptionType.directDeposit))
                            BPAnalytics.logEvent(BPAnalytics.EVENT_CASH_REWARDS_REDEMPTION_ACCT_DEPOSIT);
                    }
                } catch (JSONException e) {
                    final DialogHolo dialog = new DialogHolo(CashRewardsConfirmation.this);
                    dialog.setTitleEnabled(false);
                    dialog.setMessage(R.string.tsys_loyalty_rewards_confirmation_redemption_unsuccess_title);
                    dialog.setMessageCenter();

                    dialog.setConfirmationButton(BUTTON_LABEL_CONFIRMATION, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Utils.navigateToPortal(CashRewardsConfirmation.this);
                        }
                    });
                    Utils.showDialog(dialog, CashRewardsConfirmation.this);
                    BPAnalytics.logEvent(BPAnalytics.EVENT_CASH_REWARDS_REDEMPTION_ERROR);
                }
            }

            @Override
            public void sessionHasExpired() {
                application.reLogin(CashRewardsConfirmation.this);
            }
        });
    }

    private HashMap<String, Object> getParamsLocal(){
        final CashRewardsRedemptionModel redemption = this.redemption;
        HashMap<String, Object> params = new HashMap<>();
        params.put(TARGET1, "");
        params.put(REDEMPTION_TYPE, redemption.getRedemptionType().getValue());
        params.put(REDEEM_AMOUNT, Double.parseDouble(
                Utils.formatAmountForWsWithoutCommas(redemption.getRedemptionAmount())));
        params.put(TERMS_AND_COND, redemption.isAcceptedTerms());
        if(redemption.getAccountSelected() != null) {
            if (redemption.getAccountSelected().getFrontEndId() != null
                    && !redemption.getAccountSelected().getFrontEndId().isEmpty()) {
                params.put(ACCT_NUMBER, redemption.getAccountSelected().getFrontEndId());
            }
        }
        return params;
    }

}
