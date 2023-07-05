package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.model.CashRewardsRedemptionModel;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.RedemptionType;
import com.popular.android.mibanco.model.TsysLoyaltyRewardsInfo;
import com.popular.android.mibanco.model.TsysLoyaltyRewardsRecurringStatementCreditResponse;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.DialogHolo;
import com.popular.android.mibanco.ws.CustomGsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Mi Banco  - Mi Banco Credit Acquisition-MBCA.
 *
 * @author Stephanie Diaz <Stephanie.Diaz@evertecinc.com>
 * @version 1.1
 * @since 1.0
 * @see CashRewardsConfiguration {@link AutomaticRedemptionActivity}
 */
public class CashRewardsRedemptionResult extends BaseSessionActivity {

    /**
     * CashRewardsRedemptionModel
     */
    private CashRewardsRedemptionModel redemption; // CashRewardsRedemptionModel

    /**
     * ImageView
     */
    private ImageView success; // success image

    /**
     * TextView
     */
    private TextView title; // title text

    /**
     * TextView
     */
    private TextView message; // message text

    /**
     * Button
     */
    private Button backAccount; // success return button

    /**
     * TextView
     */
    private TextView confirmation; // confirmation text

    /**
     *
     */
    private LinearLayout confirmationLayout; // confirmation layout

    /**
     * FINISH
     */
    private static final String FINISH = "_finish"; // FINISH

    /**
     * REDEMPTION_SUCCESS
     */
    private static final String REDEMPTION_SUCCESS = "redemptionSuccess"; // REDEMPTION_SUCCESS

    /**
     * CONFIRMATION_BUTTON
     */
    private static final String CONFIRMATION_BUTTON = "OK"; // CONFIRMATION_BUTTON

    /**
     * AUTOMATIC_REDEMPTION_RESPONSE_NAME
     */
    private static final String AUTOMATIC_REDEMPTION_RESPONSE_NAME = "tsysAutomaticRedemptionResponse"; //Api response param

    /**
     * AUTOMATIC_REDEMPTION_PROCESS
     */
    private final static String AUTOMATIC_REDEMPTION_PROCESS = "AUTOMATIC_REDEMPTION"; // AUTOMATIC_REDEMPTION_PROCESS

    /**
     * CASH_REWARDS_CONFIRMATION_PROCESS
     */
    private final static String CASH_REWARDS_CONFIRMATION_PROCESS = "CASH_REWARDS_CONFIRMATION"; // CASH_REWARDS_CONFIRMATION_PROCESS

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate (Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_rewards_redemption);

        success = findViewById(R.id.successView);
        title = findViewById(R.id.redemption_title);
        message = findViewById(R.id.redemption_message);
        backAccount = findViewById(R.id.return_btn);
        confirmationLayout = findViewById(R.id.confirm_number_layout);
        confirmation = findViewById(R.id.confirmation_number);
        cashRewardsResultToolBar();

        if (App.getApplicationInstance() != null && App.getApplicationInstance()
                .getAsyncTasksManager() != null) {

            final String process = getIntent().getSerializableExtra("process").toString(); // previous process

            if (process.isEmpty()) {
                finish();
            }

            switch (process) {
                case AUTOMATIC_REDEMPTION_PROCESS:

                    callAutomaticRedemptionProcess();
                    break;

                case CASH_REWARDS_CONFIRMATION_PROCESS:

                    callRedemptionController();
                    break;

                default:

                    finish();
                    break;
            }
        }
    }

    /**
     * Show success or unsuccess on Automatic Redemption process
     */
    private void callAutomaticRedemptionProcess () {

        final String operationName = "operationName"; // Operation param
        final String activation = "ACTIVATION"; //activation process

        TsysLoyaltyRewardsRecurringStatementCreditResponse automaticRedemptionResponse = null; //api response

        final String operation = getIntent().getSerializableExtra(operationName).toString(); // previous process

        if (getIntent().getSerializableExtra(AUTOMATIC_REDEMPTION_RESPONSE_NAME)
                instanceof TsysLoyaltyRewardsRecurringStatementCreditResponse) {
            automaticRedemptionResponse = (TsysLoyaltyRewardsRecurringStatementCreditResponse)
                    getIntent().getSerializableExtra(AUTOMATIC_REDEMPTION_RESPONSE_NAME);
        }

        if (activation.equals(operation)) {
            showAutomaticRedemptionActivationResult(automaticRedemptionResponse);
        } else {
            showAutomaticRedemptionDeactivationResult(automaticRedemptionResponse);
        }
    }

    /**
     * Show success or unsuccess on Automatic Redemption activation
     * @param response
     */
    private void showAutomaticRedemptionActivationResult (TsysLoyaltyRewardsRecurringStatementCreditResponse response) {

        if (response != null && response.getRewardsBalance() != null) {
            success.setImageDrawable(getResources().getDrawable(R.drawable.success));
            title.setText(R.string.activation_automatic_redemption_success_title);
            message.setText(R.string.activation_automatic_redemption_success_message);
            backAccount.setText(R.string.activation_automatic_redemption_success_return);
            backAccount.setVisibility(View.VISIBLE);
            backAccount.setOnClickListener(onClickBack());
        } else {
            success.setImageDrawable(getResources().getDrawable(R.drawable.warning));
            title.setText(R.string.activation_automatic_redemption_unsuccess_title);
            message.setText(R.string.activation_automatic_redemption_unsuccess_message);
            backAccount.setText(R.string.activation_automatic_redemption_unsuccess_return);
            backAccount.setVisibility(View.VISIBLE);
            backAccount.setOnClickListener(onClickBack());
        }
    }

    /**
     * Show success or unsuccess on Automatic Redemption deactivation
     * @param response
     */
    private void showAutomaticRedemptionDeactivationResult (TsysLoyaltyRewardsRecurringStatementCreditResponse response) {

        if (response != null && response.getIdentifiers() != null) {
            success.setImageDrawable(getResources().getDrawable(R.drawable.success));
            title.setText(R.string.deactivation_automatic_redemption_success_title);
            message.setText(R.string.deactivation_automatic_redemption_success_message);
            backAccount.setText(R.string.deactivation_automatic_redemption_success_return);
            backAccount.setVisibility(View.VISIBLE);
            backAccount.setOnClickListener(onClickBack());
        } else {
            success.setImageDrawable(getResources().getDrawable(R.drawable.warning));
            title.setText(R.string.deactivation_automatic_redemption_unsuccess_title);
            message.setText(R.string.deactivation_automatic_redemption_unsuccess_message);
            backAccount.setText(R.string.activation_automatic_redemption_unsuccess_return);
            backAccount.setVisibility(View.VISIBLE);
            backAccount.setOnClickListener(onClickBack());
        }
    }

    /**
     * Show Success or Unsuccess on Cash Rewards Confirmation
     */
    private void callRedemptionController () {

        redemption = (CashRewardsRedemptionModel) getIntent()
                .getSerializableExtra(MiBancoConstants.CASH_REWARDS_REDEMPTION_MODEL);

        if (redemption == null) {
            finish();
        }

        App.getApplicationInstance().getAsyncTasksManager().postTsysLoyaltyRewardsInfoTask(
                CashRewardsRedemptionResult.this, redemption.getCashRewardsAccount()
                        .getFrontEndId(), getParamsLocal(), new ResponderListener() {
                    @Override
                    public void responder(String responderName, Object data) {

                        try {
                            if (data instanceof JSONObject) {
                                final JSONObject content = (JSONObject) data; // data response

                                if (content.getBoolean(REDEMPTION_SUCCESS) ) {

                                    final String confirmationNumber = content.getString("confirmationNumber"); // Confirmation Number

                                    success.setImageDrawable(getResources().getDrawable(R.drawable.success));
                                    title.setText(R.string.tsys_loyalty_rewards_confirmation_redemption_success_title);
                                    message.setText(R.string.tsys_loyalty_rewards_confirmation_redemption_success_message);
                                    confirmationLayout.setVisibility(View.VISIBLE);
                                    confirmation.setText(confirmationNumber);
                                    backAccount.setText(R.string.tsys_loyalty_rewards_confirmation_redemption_success_return);
                                    backAccount.setVisibility(View.VISIBLE);
                                    backAccount.setOnClickListener(onClickBack());

                                    if (redemption.getRedemptionType().equals(RedemptionType.statementCredit)) {
                                        BPAnalytics.logEvent(BPAnalytics.EVENT_CASH_REWARDS_REDEMPTION_STAT_CRED);
                                    }
                                    else if (redemption.getRedemptionType().equals(RedemptionType.directDeposit)) {
                                        BPAnalytics.logEvent(BPAnalytics.EVENT_CASH_REWARDS_REDEMPTION_ACCT_DEPOSIT);
                                    }

                                    final CustomGsonParser gson = new CustomGsonParser(); //Gson variable
                                    final JSONObject rewardsInfoJson = content.getJSONObject("tsysLoyaltyRewardsInfo"); //rewardsInfoJson
                                    final TsysLoyaltyRewardsInfo tsysLoyaltyRewardsInfo = gson.fromJson(
                                            rewardsInfoJson.toString(), TsysLoyaltyRewardsInfo.class); //tsysLoyaltyRewardsInfo
                                    final List<CustomerAccount> ccaAccounts = application.getLoggedInUser()
                                            .getCreditCards(); //ccaAccounts

                                    for (CustomerAccount ccaAccount : ccaAccounts) {
                                        if (ccaAccount.getFrontEndId().equalsIgnoreCase(
                                                redemption.getCashRewardsAccount().getFrontEndId())) {
                                            ccaAccount.setTsysLoyaltyRewardsInfo(tsysLoyaltyRewardsInfo);
                                            break;

                                        }
                                    }
                                } else {
                                    errorMessage();                                }
                            } else {
                                errorMessage();
                            }
                        } catch (JSONException e) {

                            final DialogHolo dialog = new DialogHolo(CashRewardsRedemptionResult.this); //dialog
                            dialog.setTitleEnabled(false);
                            dialog.setMessage(R.string.tsys_loyalty_rewards_confirmation_redemption_unsuccess_title);
                            dialog.setMessageCenter();
                            dialog.setConfirmationButton(CONFIRMATION_BUTTON, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Utils.dismissDialog(dialog);
                                }
                            });
                            Utils.showDialog(dialog, CashRewardsRedemptionResult.this);
                            BPAnalytics.logEvent(BPAnalytics.EVENT_CASH_REWARDS_REDEMPTION_ERROR);
                        }
                    }

                    @Override
                    public void sessionHasExpired () {
                        application.reLogin(CashRewardsRedemptionResult.this);
                    }
                });
    }

    /**
     * Return to Accounts
     * @return View.OnClickListener
     */
    private View.OnClickListener onClickBack () {

        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final Intent intent = new Intent(CashRewardsRedemptionResult.this,
                        Accounts.class); //intent
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        };
    }

    /**
     *  Return to CashRewardsConfiguration
     * @return View.OnClickListener
     */
    private View.OnClickListener onClickBackRedemption () {

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Intent intent = new Intent(CashRewardsRedemptionResult.this,
                        CashRewardsConfiguration.class); //intent
                intent.putExtra(MiBancoConstants.CUSTOMER_ACCOUNT_KEY,
                        redemption.getCashRewardsAccount());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        };
    }

    /**
     *  Method that creates the error messages
     */
    private void errorMessage(){
        success.setImageDrawable(getResources().getDrawable(R.drawable.warning));
        title.setText(R.string.tsys_loyalty_rewards_confirmation_redemption_unsuccess_title);
        message.setText(R.string.tsys_loyalty_rewards_confirmation_redemption_unsuccess_message);
        backAccount.setText(R.string.tsys_loyalty_rewards_confirmation_redemption_unsuccess_return);
        backAccount.setOnClickListener(onClickBackRedemption());
        backAccount.setVisibility(View.VISIBLE);
        BPAnalytics.logEvent(BPAnalytics.EVENT_CASH_REWARDS_REDEMPTION_ERROR);
    }

    /**
     *  Get params
     * @return HasMap
     */
    private HashMap<String, Object> getParamsLocal () {

        HashMap<String, Object> params = new HashMap<>(); //params map
        params.put(FINISH, "");
        return params;
    }
}
