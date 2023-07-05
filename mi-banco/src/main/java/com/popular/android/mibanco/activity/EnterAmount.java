package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.adapter.KeyPadAdapter;
import com.popular.android.mibanco.adapter.MyAmountsAdapter;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.listener.SimpleListener;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.object.ListItemSelectable;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.DialogHolo;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * The Class EnterAmount.
 */
public class EnterAmount extends BaseSessionActivity {

    private final static int KEY_0_POSITION = 10;

    private final static int KEY_00_POSITION = 9;

    private final static int KEY_1_POSITION = 0;

    private final static int KEY_2_POSITION = 1;

    private final static int KEY_3_POSITION = 2;

    private final static int KEY_4_POSITION = 3;

    private final static int KEY_5_POSITION = 4;

    private final static int KEY_6_POSITION = 5;

    private final static int KEY_7_POSITION = 6;

    private final static int KEY_8_POSITION = 7;

    private final static int KEY_9_POSITION = 8;

    private final static int KEY_DEL_POSITION = 11;

    /**
     * The amount integer value.
     */
    private int amount;

    /**
     * The amount EditText control.
     */
    private EditText amountET;

    /**
     * The amount TextView control.
     */
    private TextView amountTV;

    /**
     * The button for saving an entered amount.
     */
    private Button buttonRememberAmount;

    /**
     * The GridView control for the keypad.
     */
    private GridView gridview;

    /**
     * Should we hide both the remember and amounts buttons?
     */
    private boolean hideRemember;

    private CustomerAccount account;

    private boolean isCashRewardsRedemption;
    private TextView disclaimer;
    /**
     * Dispatches a key press event.
     *
     * @param keyCode the key code
     */
    private void dispatchKey(final int keyCode) {
        if (amountET.getText().toString().equals("0")) {
            amountET.setText("");
        }
        amountET.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
        amountET.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyCode));
    }


    @Override
    public boolean dispatchKeyEvent(final KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * Emulates a key press.
     *
     * @param position the pressed key position on the custom keypad
     */
    public void emulateKeyPress(final int position) {
        switch (position) {
            case KEY_1_POSITION:
                dispatchKey(KeyEvent.KEYCODE_1);
                break;
            case KEY_2_POSITION:
                dispatchKey(KeyEvent.KEYCODE_2);
                break;
            case KEY_3_POSITION:
                dispatchKey(KeyEvent.KEYCODE_3);
                break;
            case KEY_4_POSITION:
                dispatchKey(KeyEvent.KEYCODE_4);
                break;
            case KEY_5_POSITION:
                dispatchKey(KeyEvent.KEYCODE_5);
                break;
            case KEY_6_POSITION:
                dispatchKey(KeyEvent.KEYCODE_6);
                break;
            case KEY_7_POSITION:
                dispatchKey(KeyEvent.KEYCODE_7);
                break;
            case KEY_8_POSITION:
                dispatchKey(KeyEvent.KEYCODE_8);
                break;
            case KEY_9_POSITION:
                dispatchKey(KeyEvent.KEYCODE_9);
                break;
            case KEY_00_POSITION:
                // double 0
                dispatchKey(KeyEvent.KEYCODE_0);
                dispatchKey(KeyEvent.KEYCODE_0);
                break;
            case KEY_0_POSITION:
                dispatchKey(KeyEvent.KEYCODE_0);
                break;
            case KEY_DEL_POSITION:
                // remove last character
                amountET.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                break;
            default:
                break;
        }
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_amount);

        account = (CustomerAccount) getIntent().getSerializableExtra(MiBancoConstants.CUSTOMER_ACCOUNT_KEY);
        buttonRememberAmount = (Button) findViewById(R.id.remember_button);
        gridview = (GridView) findViewById(R.id.gridKeypad);
        amountET = (EditText) findViewById(R.id.amount);
        amountTV = (TextView) findViewById(R.id.amount_text);
        isCashRewardsRedemption = getIntent().getBooleanExtra("isCashRewardsRedemption", false);


        gridview.setAdapter(new KeyPadAdapter(this));
        amountET.requestFocus();
        buttonRememberAmount.setEnabled(false);

        if (savedInstanceState != null) {
            amount = savedInstanceState.getInt("amount");
        } else {
            amount = getIntent().getIntExtra("amount", 0);
        }

        hideRemember = getIntent().getBooleanExtra("hideRemember", false);

        if (hideRemember)
            buttonRememberAmount.setVisibility(View.GONE);

        amountET.setText(Integer.toString(amount));
        amountET.setSelection(amountET.length());
        setAmountTV();

        setListeners();
        setMyAmountsButtonVisibility();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("amount", amount);
    }


    @Override
    protected void onResume() {
        super.onResume();
        amountET.setSelection(amountET.length());
        setMyAmountsButtonVisibility();
    }

    @Override
    protected void onStart() {
        super.onStart();
        BPAnalytics.onStartSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BPAnalytics.onEndSession(this);
    }

    /**
     * Returns entered amount by intent's result.
     */
    private void returnAmount() {
        final Intent intent = new Intent();
        intent.putExtra("amount", amount);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Sets the amount TextView.
     */
    private void setAmountTV() {
        buttonRememberAmount.setEnabled(amount > 0);
        amountTV.setText(Utils.formatAmount(amount));
    }

    /**
     * Sets the listeners.
     */
    private void setListeners() {

        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View v, final int position, final long id) {
                emulateKeyPress(position);
            }
        });

        // block GridView scrolling
        gridview.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });

        amountET.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_CLEAR || keyCode == KeyEvent.KEYCODE_DEL || keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                    if (event.getAction() == KeyEvent.ACTION_UP || keyCode == KeyEvent.KEYCODE_CLEAR || keyCode == KeyEvent.KEYCODE_DEL) {
                        String amountStr = amountET.getText().toString();
                        if (amountStr.length() <= 0 || Integer.parseInt(amountStr) == 0) {
                            amountStr = "0";
                            amountET.setText("");
                        }

                        if (amountStr == null || amountStr.length() == 0 || keyCode == KeyEvent.KEYCODE_CLEAR || keyCode == KeyEvent.KEYCODE_DEL && amountStr.length() < 2) {
                            amountStr = "0";
                        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
                            amountStr = amountStr.substring(0, amountStr.length() - 1);
                        }
                        amount = Integer.parseInt(amountStr);
                        setAmountTV();
                    }
                } else {
                    amountET.setText(Integer.toString(amount));
                }
                return false;
            }
        });

        buttonRememberAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                BPAnalytics.logEvent(BPAnalytics.EVENT_REMEMBER_AMOUNT_BUTTON_TOUCHED);

                final DialogHolo dialog = new DialogHolo(EnterAmount.this);
                dialog.setCustomContentView(R.layout.dialog_remember_amount);
                dialog.setTitle(R.string.remember_amount);

                dialog.setNegativeButton(getString(R.string.cancel), new OnClickListener() {

                    @Override
                    public void onClick(final View v) {
                        Utils.dismissDialog(dialog);
                    }
                });

                dialog.setPositiveButton(getString(R.string.set), new OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final String name = ((EditText) dialog.findViewById(R.id.amount_name)).getText().toString();
                        if (!(name.length() > 0)) {
                            return;
                        }
                        if (Utils.addMyAmount(getApplicationContext(), name, amount)) {
                            Toast.makeText(getApplicationContext(), getString(R.string.amount_saved), Toast.LENGTH_LONG).show();
                            Utils.dismissDialog(dialog);
                            setMyAmountsButtonVisibility();
                            BPAnalytics.logEvent(BPAnalytics.EVENT_REMEMBER_AMOUNT_ENTERED);
                        } else {
                            final DialogHolo dialogUpdate = new DialogHolo(EnterAmount.this);
                            dialogUpdate.setTitle(null);
                            dialogUpdate.setMessage(getString(R.string.amount_exists));
                            dialogUpdate.setPositiveButton(getString(R.string.ok), new OnClickListener() {
                                @Override
                                public void onClick(final View v) {
                                    if (Utils.updateMyAmount(getApplicationContext(), name, amount)) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.amount_saved), Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), getString(R.string.amount_cant_be_saved), Toast.LENGTH_LONG).show();
                                    }
                                    Utils.dismissDialog(dialogUpdate);
                                    Utils.dismissDialog(dialog);
                                    setMyAmountsButtonVisibility();
                                    BPAnalytics.logEvent(BPAnalytics.EVENT_REMEMBER_AMOUNT_ENTERED);
                                }
                            });
                            dialogUpdate.setNegativeButton(getString(R.string.cancel), new OnClickListener() {
                                @Override
                                public void onClick(final View v) {
                                    Utils.dismissDialog(dialogUpdate);
                                }
                            });
                            Utils.showDialog(dialogUpdate, EnterAmount.this);
                        }
                    }
                });

                dialog.setCancelable(true);
                Utils.showDialog(dialog, EnterAmount.this);
            }
        });

        findViewById(R.id.set_button).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                if(isCashRewardsRedemption) {
                    validateRedemptionAmount(account);
                    return;
                }
                returnAmount();
            }
        });

        findViewById(R.id.my_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final DialogHolo dialog = new DialogHolo(EnterAmount.this);
                dialog.setTitle(getString(R.string.select_amount));
                final View customView = dialog.setCustomContentView(R.layout.my_amounts);

                final ListView list = (ListView) customView.findViewById(R.id.list_view);
                list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

                final ArrayList<ListItemSelectable> items = new ArrayList<ListItemSelectable>();
                final App utils = (App) getApplication();

                final HashMap<String, Integer> myAmounts = Utils.getMyAmounts(getApplicationContext());
                if (myAmounts != null) {
                    for (final String name : myAmounts.keySet()) {
                        items.add(new ListItemSelectable(myAmounts.get(name), name, Utils.formatAmount(myAmounts.get(name))));
                    }
                }
                final MyAmountsAdapter listAdapter = new MyAmountsAdapter(EnterAmount.this, items, utils, new SimpleListener() {

                    @Override
                    public void done() {
                        setMyAmountsButtonVisibility();
                        Utils.dismissDialog(dialog);
                    }
                });
                list.setAdapter(listAdapter);

                dialog.setNegativeButton(getString(R.string.cancel), new OnClickListener() {

                    @Override
                    public void onClick(final View paramView) {
                        Utils.dismissDialog(dialog);
                    }
                });

                dialog.setPositiveButton(getString(R.string.select), new OnClickListener() {

                    @Override
                    public void onClick(final View paramView) {
                        final int tempAmount = listAdapter.getCheckedAmount();
                        if (tempAmount != -1) {
                            amount = tempAmount;
                            amountET.setText(Integer.toString(amount));
                            amountET.setSelection(amountET.length());
                            setAmountTV();
                            Utils.dismissDialog(dialog);
                        }
                    }
                });

                Utils.showDialog(dialog, EnterAmount.this);
            }
        });
    }

    /**
     * Sets "My amounts" button visibility depending on saved amounts list items count.
     */
    private void setMyAmountsButtonVisibility() {
        if (Utils.getMyAmounts(this).size() == 0 || hideRemember) {
            findViewById(R.id.my_button).setVisibility(View.GONE);
        } else {
            findViewById(R.id.my_button).setVisibility(View.VISIBLE);
        }
         if(isCashRewardsRedemption) {
             disclaimer = findViewById(R.id.disclaimer);
            buttonRememberAmount.setVisibility(View.GONE);
            findViewById(R.id.my_button).setVisibility(View.GONE);
            findViewById(R.id.cash_rewards_disclaimer).setVisibility(View.VISIBLE);
            disclaimer.setText(getResources().getString(R.string.tsys_loyalty_rewards_amount_disclaimer)
                    +" " + account.getTsysLoyaltyRewardsInfo().getAvailableRewardsBalance() + ".");
        }
    }


    private void validateRedemptionAmount(CustomerAccount account) {
       final DialogHolo dialog = new DialogHolo(EnterAmount.this);

        if(isCashRewardsRedemption){
            //TODO: Find a more efficient way to convert amount as a decimal ex: 3500 to 35.00
            double redeemAmount = Double.parseDouble(Utils.formatAmountForWsWithoutCommas(amount));
            boolean showDialog = false;
            ((TextView)dialog.findViewById(R.id.alertTitle)).setSingleLine(false);

            if(redeemAmount  < account.getTsysLoyaltyRewardsInfo().getMinimumRewardsBalance()) {

                dialog.setTitleEnabled(false);
                dialog.setMessage(R.string.tsys_loyalty_rewards_min_amount_error_message);
                dialog.setMessageCenter();
                showDialog = true;

            } else if(redeemAmount > account.getTsysLoyaltyRewardsInfo()
                    .getAvailableBalanceDouble()) {

                dialog.setTitleEnabled(false);
                dialog.setMessage(R.string.tsys_loyalty_rewards_max_amount);
                dialog.setMessageCenter();
                showDialog = true;
            }

            if(showDialog){
                dialog.setConfirmationButton("OK", new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.dismissDialog(dialog);
                    }
                });
                Utils.showDialog(dialog, EnterAmount.this);
            } else{
                returnAmount();
            }
        }
    }

}
