package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.model.TsysLoyaltyRewardsRecurringStatementCreditResponse;

/**
 * Automatic Redemption Activation Activity class.
 * @author leandro.baleriani
 * @version 1.0
 * @since 1.0
 * @see BaseSessionActivity
 */
public class AutomaticRedemptionActivity extends BaseSessionActivity {

    /**
     * TSYS_RESPONSE_FIELD
     */
    private final static String TSYS_RESPONSE_FIELD = "tsysResponse"; // TSYS_RESPONSE_FIELD

    /**
     * FRONT_END_ID_FIELD
     */
    private final static String FRONT_END_ID_FIELD = "accFrontEndId"; // FRONT_END_ID_FIELD

    /**
     * FRONT END ID FROM ACCOUNT
     */
    private String accFrontEndId; //frontEndId value

    /**
     * PROCESS_FIELD
     */
    private final static String PROCESS_FIELD = "process"; // PROCESS_FIELD

    /**
     * OPERATION_FIELD
     */
    private final static String OPERATION_FIELD = "operationName"; // OPERATION_FIELD

    /**
     * PROCESS_FIELD_VALUE
     */
    private final static String PROCESS_FIELD_VALUE = "AUTOMATIC_REDEMPTION"; // PROCESS_FIELD_VALUE

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation_deactivation_automatic_redemption);

        if (App.getApplicationInstance() != null && App.getApplicationInstance().getAsyncTasksManager() != null) {
            accFrontEndId = getIntent().getSerializableExtra(FRONT_END_ID_FIELD).toString(); //FrontEndId value

            TsysLoyaltyRewardsRecurringStatementCreditResponse response = null; //Tsys Api response

            if (getIntent().getSerializableExtra(TSYS_RESPONSE_FIELD)
                    instanceof TsysLoyaltyRewardsRecurringStatementCreditResponse) {
                response = (TsysLoyaltyRewardsRecurringStatementCreditResponse)
                        getIntent().getSerializableExtra(TSYS_RESPONSE_FIELD);
            }

            if (accFrontEndId.isEmpty()) {
                finish();
            } else {
                cashRewardsAutomaticRedemptionToolBar();
                initializeComponents(response);
            }
        } else {
            Toast.makeText(this, R.string.error_occurred, Toast.LENGTH_LONG).show();
        }
    }

    /**
     *
     * @param mnuMenu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu (final Menu mnuMenu) {
        mnuMenu.findItem(R.id.menu_settings).setVisible(false);
        mnuMenu.findItem(R.id.menu_logout).setVisible(false);
        mnuMenu.findItem(R.id.menu_locator).setVisible(false);
        mnuMenu.findItem(R.id.menu_contact).setVisible(false);

        return true;
    }

    /**
     * Return to previous page
     *
     * @return
     */
    private View.OnClickListener onClickBack () {

        return new View.OnClickListener() {

            @Override
            public void onClick (View view) {

                onBackPressed();

            }

        };
    }

    /**
     * Pause the Automatic Redemption
     *
     * @return
     */
    private View.OnClickListener onClickPause () {

        return new View.OnClickListener() {

            @Override
            public void onClick (View view) {

                final String responseName = "tsysAutomaticRedemptionResponse"; //Api response param
                final String methodAction = "DELETE"; //Method param value
                final String operation = "DEACTIVATION"; // Operation value

                if (App.getApplicationInstance() != null && App.getApplicationInstance().getAsyncTasksManager() != null) {
                    App.getApplicationInstance().getAsyncTasksManager()
                            .getTsysLoyaltyRewardsRecurringStatementCreditInfoTask(AutomaticRedemptionActivity.this,
                                    accFrontEndId, methodAction, new ResponderListener() {

                                        @Override
                                        public void responder (String strResponderName, final Object data) {

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    TsysLoyaltyRewardsRecurringStatementCreditResponse
                                                            response = null; //Api response data

                                                    if (data instanceof TsysLoyaltyRewardsRecurringStatementCreditResponse) {
                                                        response = (TsysLoyaltyRewardsRecurringStatementCreditResponse) data;
                                                    }

                                                    final Intent iAutomaticRedemption = new Intent(AutomaticRedemptionActivity.this, CashRewardsRedemptionResult.class); // intent variable

                                                    iAutomaticRedemption.putExtra(responseName, response);
                                                    iAutomaticRedemption.putExtra(PROCESS_FIELD, PROCESS_FIELD_VALUE);
                                                    iAutomaticRedemption.putExtra(OPERATION_FIELD, operation);
                                                    iAutomaticRedemption.putExtra(FRONT_END_ID_FIELD, accFrontEndId);

                                                    startActivity(iAutomaticRedemption);
                                                }
                                            });
                                        }

                                        @Override
                                        public void sessionHasExpired() {
                                            application.reLogin(AutomaticRedemptionActivity.this);
                                        }
                                    });
                }
            }

        };
    }

    /**
     * Activate the Automatic Redemption
     *
     * @return
     */
    private View.OnClickListener onClickActivate () {

        return new View.OnClickListener() {

            @Override
            public void onClick (View view) {

                final String responseName = "tsysAutomaticRedemptionResponse"; //Api response param
                final String methodAction = "POST"; //Method param value
                final String operation = "ACTIVATION"; // Operation value

                if (App.getApplicationInstance() != null && App.getApplicationInstance().getAsyncTasksManager() != null) {
                    App.getApplicationInstance().getAsyncTasksManager()
                            .getTsysLoyaltyRewardsRecurringStatementCreditInfoTask(AutomaticRedemptionActivity.this,
                                    accFrontEndId, methodAction, new ResponderListener() {

                                        @Override
                                        public void responder (String strResponderName, final Object data) {


                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    TsysLoyaltyRewardsRecurringStatementCreditResponse
                                                            response = null; //Api response data

                                                    if (data instanceof TsysLoyaltyRewardsRecurringStatementCreditResponse) {
                                                        response = (TsysLoyaltyRewardsRecurringStatementCreditResponse) data;
                                                    }

                                                    final Intent iAutomaticRedemption = new Intent(AutomaticRedemptionActivity.this, CashRewardsRedemptionResult.class); // intent variable

                                                    iAutomaticRedemption.putExtra(responseName, response);
                                                    iAutomaticRedemption.putExtra(PROCESS_FIELD, PROCESS_FIELD_VALUE);
                                                    iAutomaticRedemption.putExtra(OPERATION_FIELD, operation);
                                                    iAutomaticRedemption.putExtra(FRONT_END_ID_FIELD, accFrontEndId);

                                                    startActivity(iAutomaticRedemption);
                                                }
                                            });
                                        }

                                        @Override
                                        public void sessionHasExpired () {
                                            application.reLogin(AutomaticRedemptionActivity.this);
                                        }
                                    });
                }
            }

        };
    }

    /**
     * Initialize UI components
     * @param response
     */
    private void initializeComponents (TsysLoyaltyRewardsRecurringStatementCreditResponse response) {

        Button activateBtn = findViewById(R.id.ar_activate_btn); // Button activate
        Button pauseBtn = findViewById(R.id.ar_pause_btn); // Button pause
        TextView arText = findViewById(R.id.ar_text); // Principal Text
        TextView arQuestion = findViewById(R.id.ar_question); // Question Text
        TextView arTitle = findViewById(R.id.ar_title); // Title of Page

        if (response != null) {
            arTitle.setText(getString(R.string.activation_automatic_redemption_title));
            arText.setText(getString(R.string.activation_automatic_redemption_text));
            arQuestion.setText(getString(R.string.activation_automatic_redemption_question));
            activateBtn.setText(getString(R.string.activation_automatic_redemption_button_continue));
            pauseBtn.setText(getString(R.string.activation_automatic_redemption_button_pause));
            activateBtn.setOnClickListener(onClickBack());
            pauseBtn.setOnClickListener(onClickPause());
        } else {
            arTitle.setText(getString(R.string.deactivation_automatic_redemption_title));
            arText.setText(getBullets());
            arQuestion.setVisibility(View.INVISIBLE);
            activateBtn.setText(getString(R.string.deactivation_automatic_redemption_button_activate));
            activateBtn.setOnClickListener(onClickActivate());
            pauseBtn.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Generate the text with bullets for deactivate state
     * @return string with text of bullets
     */
    private String getBullets () {

        final int sbCapacity = 4; //StringBuilder Capacity
        StringBuilder sbBullets = new StringBuilder(sbCapacity); //Bullets text builder

        sbBullets.append(getString(R.string.deactivation_automatic_redemption_bullet_one));
        sbBullets.append(getString(R.string.deactivation_automatic_redemption_bullet_two));
        sbBullets.append(getString(R.string.deactivation_automatic_redemption_bullet_three));
        sbBullets.append(getString(R.string.deactivation_automatic_redemption_bullet_four));

        return  sbBullets.toString();
    }

}