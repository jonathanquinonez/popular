package com.popular.android.mibanco.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.ContextCompat;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoPreferences;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BasePermissionsSessionActivity;
import com.popular.android.mibanco.model.AccountCard;
import com.popular.android.mibanco.model.PhonebookContact;
import com.popular.android.mibanco.model.User;
import com.popular.android.mibanco.task.LiteEnrollmentTasks;
import com.popular.android.mibanco.task.MobileCashTasks;
import com.popular.android.mibanco.util.AlertDialogParameters;
import com.popular.android.mibanco.util.AutoLoginUtils;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.ContactsManagementUtils;
import com.popular.android.mibanco.util.EnrollmentLiteStatus;
import com.popular.android.mibanco.util.FingerprintModule;
import com.popular.android.mibanco.util.MobileCashUtils;
import com.popular.android.mibanco.util.ProductType;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.AlertDialogFragment;
import com.popular.android.mibanco.ws.response.EasyCashTrx;
import com.popular.android.mibanco.ws.response.EnrollmentLiteResponse;
import com.popular.android.mibanco.ws.response.MobileCashAcctsResponse;
import com.popular.android.mibanco.ws.response.MobileCashTrx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Activity that manages the Easy Cash staging process
 */
public class EasyCashStaging extends BasePermissionsSessionActivity implements AlertDialogFragment.AlertDialogListener,DialogInterface.OnClickListener,MobileCashTasks.MobileCashListener<EasyCashTrx> {

    private int transferAmount;
    private TextView tvTransferAmount;
    private Button btnSendMoneyToMe;
    private Button btnSendMoneyToOthers;
    private Button btnConfirmSendToOther;
    private boolean isFormeTransfer;
    private int maximumAmountForme;
    private int maximumAmountForOther;
    private String[] amountOptions;
    private LinearLayout viewAthCard;
    private LinearLayout selectContact;
    private ArrayList<AccountCard> accounts;
    private AccountCard selectedAccount;
    private TextView tvAccountHint;
    private Context mContext;
    private MobileCashTrx mcTransaction;
    private PhonebookContact contactTo;
    private EditText optionalMessage;
    private int fingerprintStatus = 0;
    private HashMap<String, PhonebookContact> contacts;
    private TextView tvRecipientHint;
    private MobileCashTasks mobileCashTasks;

    public static final int ACTIVATE_FINGERPRINT = 1;
    public static final String ACTIVATE_FINGERPRINT_RESULT = "fp-result";
    public static final String ACTIVATE_FINGERPRINT_RESULT_YES = "Y";
    public static final String ACTIVATE_FINGERPRINT_RESULT_NO = "N";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.easycash_staging);
        App.getApplicationInstance().setFingerprintSectionId(1);

        viewAthCard = (LinearLayout) findViewById(R.id.viewAthCard);
        selectContact = (LinearLayout) findViewById(R.id.selectContact);
        selectContact.setOnClickListener(this);

        tvAccountHint = (TextView) findViewById(R.id.tvAccountHint);
        tvRecipientHint = (TextView) findViewById(R.id.tvRecipientHint);
        mContext = this;
        mcTransaction = new MobileCashTrx();

        tvTransferAmount = (TextView) findViewById(R.id.tvTransferAmount);
        btnSendMoneyToMe = (Button) findViewById(R.id.btnForMe);
        btnSendMoneyToOthers = (Button) findViewById(R.id.btnForOther);
        btnConfirmSendToOther = (Button) findViewById(R.id.btnConfirmForOther);
        optionalMessage = (EditText) findViewById(R.id.txt_optional_msg);

        btnSendMoneyToMe.setOnClickListener(this);
        btnSendMoneyToOthers.setOnClickListener(this);
        btnConfirmSendToOther.setOnClickListener(this);
        tvTransferAmount.setOnClickListener(this);

        maximumAmountForme = MiBancoConstants.MC_MAX_BALANCE_AMOUNT;
        maximumAmountForOther = MiBancoConstants.MC_MAX_BALANCE_AMOUNT;
        isFormeTransfer = true;

        accounts = new ArrayList<>();

        BPAnalytics.logEvent(BPAnalytics.EVENT_MC_VIEW_SCREEN);

        viewChangesManagement(2); //Initialize as send to myself view

        getEasyCashAccounts(true,this);
        validateTransfer();
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
            menu.findItem(R.id.menu_logout).setVisible(false);
            menu.findItem(R.id.easycash_pendingtrx_history).setVisible(true);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.easycash_pendingtrx_history:
                Intent transfersHistory = new Intent(this, EasyCashHistoryActivity.class);
                startActivityForResult(transfersHistory, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onPermissionResult(boolean permissionGranted)
    {
        if(permissionGranted){
            contacts = ContactsManagementUtils.getContactsWithPhones(this);
        }else{
            contacts = null;
        }

        Boolean userHasCashDrop = application.getCustomerEntitlements() != null
                && application.getCustomerEntitlements().hasCashDrop() != null
                && application.getCustomerEntitlements().hasCashDrop();

        boolean globalCashDropEntitlement = application.isGlobalCashdropEntitlementEnabled();
        /**
         * TODO: Why can hasCashDrop return null?
         */
        if((application.getCustomerEntitlements() != null && application.getCustomerEntitlements().hasCashDrop() == null) || !userHasCashDrop || !globalCashDropEntitlement){
            finish();
        }
    }

    private void getEasyCashAccounts(boolean refresh, final OnClickListener onClickListener)
    {
        MobileCashTasks.getEasyCashAccounts(this, refresh, new MobileCashTasks.MobileCashListener<MobileCashAcctsResponse>() {
            @Override
            public void onMobileCashApiResponse(MobileCashAcctsResponse result) {
                if (result != null) {

                    if(!Utils.isBlankOrNull(result.getMaxAmount())
                            && !result.getMaxAmount().equalsIgnoreCase("null"))
                        maximumAmountForme = Integer.parseInt(result.getMaxAmount());
                        maximumAmountForOther = Integer.parseInt(result.getMaxAmountCashDrop());

                    initScreenValues();

                    if(result.getMobileCashAccts()!= null && result.getMobileCashAccts().size()>0) {
                        accounts = result.getMobileCashAccts();
                        resetValues();

                        boolean onclickEnabled = true;
                        if(result.getPendingFromMeTransaction() != null){
                            mcTransaction = result.getPendingFromMeTransaction();

                            if (App.getApplicationInstance().getGlobalStatus().isMobileCashForOthers()) {
                                pendingForMeMessage();
                            } else {
                                showTransactionReciept(mcTransaction);
                                finish();
                            }
                        }
                        else if (accounts.size() == 1) {
                            if (accounts.get(0).getAtmCards() == null || accounts.get(0).getAtmCards().size() == 0) {
                                MobileCashUtils.informativeMessageWithoutTitle(mContext, R.string.mc_accounts_error_message);
                                return;
                            }

                            int balance = Utils.getAmountIntValue(accounts.get(0).getBalance());
                            if(balance < MiBancoConstants.MC_MIN_BALANCE_AMOUNT){
                                findViewById(R.id.viewSelectAccount).setOnClickListener(onClickListener);
                                MobileCashUtils.informativeMessageWithoutTitle(mContext, R.string.mc_error_minbalance);
                                return;
                            }

                            selectedAccount = accounts.get(0);

                            AccountCard card = selectedAccount.getAtmCards().get(0);
                            selectedAccount.setSelectedCardFromAccount(card);
                            setSelectedAccount();
                            ImageView dropdownView = (ImageView)findViewById(R.id.navigation_dropdown);
                            dropdownView.setVisibility(View.INVISIBLE);
                            validateTransfer();
                            onclickEnabled = false;
                        }

                        fingerprintStatus = result.getStatus();

                        if(onclickEnabled){
                            findViewById(R.id.viewSelectAccount).setOnClickListener(onClickListener);
                        }

                    }else{
                        MobileCashUtils.informativeMessageWithoutTitle(mContext, R.string.mc_accounts_error_message);
                    }

                } else {
                    AlertDialogParameters params = new AlertDialogParameters(mContext,R.string.mc_service_error_message,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    params.setPositiveButtonText(mContext.getResources().getString(R.string.ok));
                    Utils.showAlertDialog(params);
                }
            }
        });
    }

    private void pendingForMeMessage()
    {
        String message = getString(R.string.easycash_pendingtrx_alert, Utils.getFormattedDollarAmount(String.valueOf(mcTransaction.getAmount())), mcTransaction.getNickname()+" "+ mcTransaction.getAccountLast4Num());
        AlertDialogParameters params = new AlertDialogParameters(mContext,0,transactionForMe);
        params.setStrMessage(message);
        params.setPositiveButtonText(getString(R.string.easycash_pendingtrx_alert_yes));
        params.setNegativeButtonText(getString(R.string.easycash_pendingtrx_alert_no));
        Utils.showAlertDialog(params);
    }

    private void initScreenValues()
    {
        TextView disclaimerText = (TextView) findViewById(R.id.tvAmountDisclaimer);
        String amountDisclamer;
        amountDisclamer = getString(R.string.mc_amount_disclaimer);

        disclaimerText.setText(String.format(amountDisclamer, String.valueOf(MiBancoConstants.MC_MIN_BALANCE_AMOUNT)));
    }

    /**
     * Method that manages view changes from a self cashdrop to a "to others" cashdrop
     * @param viewId The view ID: 0 for me, 1 to others
     */
    private void viewChangesManagement(int viewId){
        switch (viewId){
            case 0: // FOR ME
                isFormeTransfer = true;
                selectContact.setVisibility(View.GONE);
                optionalMessage.setVisibility(View.GONE);
                btnConfirmSendToOther.setVisibility(View.GONE);

                break;
            case 1: // FOR OTHERS
                isFormeTransfer = false;
                selectContact.setVisibility(View.VISIBLE);
                optionalMessage.setVisibility(View.VISIBLE);
                btnConfirmSendToOther.setVisibility(View.VISIBLE);
                btnSendMoneyToOthers.setVisibility(View.GONE);
                btnSendMoneyToMe.setVisibility(View.GONE);
                break;
            case 2: // DEFAULT VALUES
                isFormeTransfer = true;
                selectContact.setVisibility(View.GONE);
                optionalMessage.setVisibility(View.GONE);
                btnConfirmSendToOther.setVisibility(View.GONE);
                btnSendMoneyToMe.setVisibility(View.VISIBLE);

                // Customize visibility btnSendMoneyToOthers
                // and text of btnSendMoneyToMe depending on the status of entitlement.
                if (App.getApplicationInstance().getGlobalStatus().isMobileCashForOthers()) {
                    btnSendMoneyToOthers.setVisibility(View.VISIBLE);
                    btnSendMoneyToMe.setText(R.string.ec_send_money_to_me);
                } else {
                    btnSendMoneyToOthers.setVisibility(View.GONE);
                    btnSendMoneyToMe.setText(R.string.ec_send_money_withdraw);
                }
        }
    }

    private void resetValues() {
        transferAmount = 0;
        tvTransferAmount.setText(Utils.getFormattedDollarAmount("0"));
        validateTransfer();
    }

    public void resetToInitialValues() {
        if (mcTransaction != null && Utils.isBlankOrNull(mcTransaction.getAmount())) {
            contactTo = null;
            selectContact.removeAllViews();
            viewChangesManagement(2);
        }
        validateTransfer();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MiBancoConstants.AMOUNT_PICKER_AMOUNT_KEY, transferAmount);
    }

    private void setSelectedAccount() {
        if (selectedAccount != null) {
            tvAccountHint.setVisibility(View.GONE);
            viewAthCard.removeAllViews();

            View cardLayout = LayoutInflater.from(this).inflate(R.layout.athm_card_layout, viewAthCard, false);

            TextView tvName = (TextView) cardLayout.findViewById(R.id.tvName);
            TextView tvBalance = (TextView) cardLayout.findViewById(R.id.tvBalance);
            TextView tvLast4Digits = (TextView) cardLayout.findViewById(R.id.tvLast4Digits);
            ImageView cardImage = (ImageView) cardLayout.findViewById(R.id.imgCard);
            ImageLoader.getInstance().displayImage(selectedAccount.getSelectedCardFromAccount().getCardImageUri(), cardImage);

            tvName.setText(selectedAccount.getNickname());
            tvBalance.setText(selectedAccount.getBalance());
            tvLast4Digits.setText(selectedAccount.getAccountLast4Num());



            viewAthCard.addView(cardLayout);
            viewAthCard.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MiBancoConstants.AMOUNT_PICKER_REQUEST_CODE:
                    if (data != null) {
                        transferAmount = data.getIntExtra(MiBancoConstants.AMOUNT_PICKER_AMOUNT_KEY, 0);
                        tvTransferAmount.setText(Utils.formatAmount(transferAmount));
                        validateTransfer();
                    }
                    break;

                case MiBancoConstants.MC_TRANSFER_SENT_REQUEST_CODE:
                    resetValues();
                    break;

                case MiBancoConstants.MC_SELECT_ACCOUNT_REQUEST_CODE:
                    AccountCard selectedAcc = (AccountCard) data.getSerializableExtra(MiBancoConstants.ATH_CARD_KEY);

                    if (selectedAcc.getAtmCards() != null && selectedAcc.getAtmCards().size() > 0) {
                        selectedAcc.setSelectedCardFromAccount(selectedAcc.getAtmCards().get(0));
                        selectedAccount = selectedAcc;
                        setSelectedAccount();
                        int balance = Utils.getAmountIntValue(selectedAccount.getBalance());
                        if(balance < Utils.getAmountIntValue(tvTransferAmount.getText().toString())){
                            resetValues();
                        }
                    }else {
                        MobileCashUtils.informativeMessageWithoutTitle(this, R.string.mc_accounts_error_message);
                    }
                    break;

                case MiBancoConstants.MC_SELECT_CARD_REQUEST_CODE:
                    AccountCard card = (AccountCard) data.getSerializableExtra(MiBancoConstants.ATH_CARD_KEY);
                    selectedAccount.setSelectedCardFromAccount(card);
                    setSelectedAccount();

                    break;

                case MiBancoConstants.CONTACT_CHOOSER_REQUEST_CODE:
                    if (data != null) {
                        contactTo = (PhonebookContact) data.getSerializableExtra(MiBancoConstants.ATHM_CONTACT_KEY);
                        ContactsManagementUtils.setContactToView(this, contactTo,tvRecipientHint,selectContact);
                    }
                    break;

                case ACTIVATE_FINGERPRINT:
                    if(data != null){
                        String result = (String)data.getSerializableExtra(ACTIVATE_FINGERPRINT_RESULT);
                        if(!Utils.isBlankOrNull(result)){
                            if(result.equals(ACTIVATE_FINGERPRINT_RESULT_YES)){
                                new FingerprintModule(this, getFragmentManager());
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        } else if (requestCode == MiBancoConstants.CONTACT_CHOOSER_REQUEST_CODE) {
            this.resetToInitialValues();
        }
        validateTransfer();
    }


    private void validateTransfer() {

        tvTransferAmount.setEnabled(selectedAccount != null);
        if(selectedAccount != null){
            if(selectContact.getVisibility() == View.VISIBLE){
                tvTransferAmount.setEnabled(contactTo != null);
            }
        }

        if(tvTransferAmount.isEnabled()){
            tvTransferAmount.setTextColor(ContextCompat.getColor(this, R.color.athm_header));
        }else{
            tvTransferAmount.setTextColor(ContextCompat.getColor(this, R.color.grey_light));
        }

        boolean enableButton = transferAmount >0;
        if(btnConfirmSendToOther.getVisibility() == View.VISIBLE){
            enableButton = enableButton && contactTo != null;
        }
        btnSendMoneyToMe.setEnabled(enableButton);
        btnSendMoneyToOthers.setEnabled(enableButton);
        btnConfirmSendToOther.setEnabled(enableButton);

    }

    DialogInterface.OnClickListener transactionForMe = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Intent intent = new Intent(mContext, EasyCashRedeem.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(MiBancoConstants.KEY_MOBILE_CASH_TRX, mcTransaction);
                    intent.putExtras(bundle);
                    intent.putExtra(MiBancoConstants.KEY_ENROLL_LITE_IS_CUSTOMER, true);
                    mContext.startActivity(intent);
                    finish();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    sendMoneyToOthersAction();
                    break;

                default:
                    break;
            }
            dialog.dismiss();
        }
    };



    DialogInterface.OnClickListener submitTrxForOtherOnClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    mcTransaction.setAmount(tvTransferAmount.getText().toString().replace("$",""));
                    mcTransaction.setAccountLast4Num(selectedAccount.getAccountLast4Num());
                    mcTransaction.setNickname(selectedAccount.getNickname());
                    mcTransaction.setAccountFrontEndId(selectedAccount.getFrontEndId());
                    mcTransaction.setAtmLast4Num(selectedAccount.getSelectedCardFromAccount().getAtmLast4Num());
                    mcTransaction.setAtmType(selectedAccount.getSelectedCardFromAccount().getAtmType());
                    mcTransaction.setTranType("DEPOSIT");
                    mcTransaction.setReceiverPhone(ContactsManagementUtils.getRawPhoneNumber(contactTo.getRawPhoneNumber(), mContext));
                    mcTransaction.setMemo(optionalMessage.getText().toString());
                    MobileCashTasks.postTransaction(mContext, mcTransaction, new MobileCashTasks.MobileCashListener<EasyCashTrx>() {
                        @Override
                        public void onMobileCashApiResponse(EasyCashTrx result) {
                            if (result != null && result.getContent().getStatus() != null) {
                                if(MiBancoConstants.MC_BLACKLIST_SENDER.equals(result.getContent().getBlackListStatus())){
                                    BPAnalytics.logEvent(BPAnalytics.EVENT_MC_PRESTAGE_FOR_OTHER_FAILED_SENDER_BLACKLIST);
                                    MobileCashUtils.informativeMessage(mContext, R.string.easycash_blacklist_sender);

                                }else if(MiBancoConstants.MC_BLACKLIST_RECEIVER.equals(result.getContent().getBlackListStatus())){
                                    BPAnalytics.logEvent(BPAnalytics.EVENT_MC_PRESTAGE_FOR_OTHER_FAILED_RECEIVER_BLACKLIST);
                                    MobileCashUtils.informativeMessage(mContext,R.string.easycash_blacklist_receiver);

                                }else if (result.getContent().getStatus().equals(MiBancoConstants.MOBILE_CASH_SUCCESS)) {

                                    List<MobileCashTrx> transactions = result.getContent().getTransactions();
                                    for (MobileCashTrx trx : transactions) {
                                        if (trx.getReceiverPhone().equals(mcTransaction.getReceiverPhone())) {

                                            mcTransaction.setTrxReceiptId(trx.getTrxReceiptId());
                                            mcTransaction.setTrxExpDate(trx.getTrxExpDate());
                                            mcTransaction.setMemo(trx.getMemo());
                                            break;
                                        }
                                    }
                                    if (!Utils.isBlankOrNull(mcTransaction.getTrxReceiptId())) { // Display Receipt
                                        showTransactionReciept(mcTransaction);

                                        MiBancoPreferences.addAthmRecentPhoneNumber(MiBancoPreferences.getLoggedInUsername(), contactTo.getRawPhoneNumber(), MiBancoConstants.RECENT_CONTACTS_KEY_EASYCASH);
                                        BPAnalytics.logEvent(BPAnalytics.EVENT_MC_PRESTAGE_FOR_OTHER_SUCCESSFULL);
                                        App.getApplicationInstance().setUpdateEasyCashHistory(true);

                                        finish();
                                    }else{
                                        MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);
                                        BPAnalytics.logEvent(BPAnalytics.EVENT_MC_PRESTAGE_FOR_OTHER_FAILED);
                                    }
                                } else {
                                    MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);
                                    BPAnalytics.logEvent(BPAnalytics.EVENT_MC_PRESTAGE_FOR_OTHER_FAILED);
                                }
                            } else {
                                MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);
                                BPAnalytics.logEvent(BPAnalytics.EVENT_MC_PRESTAGE_FOR_OTHER_FAILED);
                            }
                        }
                    });

                    break;
                default:
                    break;
            }
            dialog.dismiss();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewSelectAccount:

                if (accounts.size() == 1) {
                    int balance = Utils.getAmountIntValue(accounts.get(0).getBalance());
                    if(balance < MiBancoConstants.MC_MIN_BALANCE_AMOUNT){
                        MobileCashUtils.informativeMessageWithoutTitle(mContext, R.string.mc_error_minbalance);
                        return;
                    }
                }

                if (accounts == null || accounts.size() == 0) {
                    MobileCashUtils.informativeMessageWithoutTitle(mContext, R.string.mc_accounts_error_message);
                } else {
                    Intent selectAccountIntent = new Intent(this, SelectAccount.class);
                    Bundle b = new Bundle();
                    b.putSerializable("accounts", accounts);
                    selectAccountIntent.putExtra("accounts", b);
                    selectAccountIntent.putExtra("accountsOnly", true);
                    selectAccountIntent.putExtra("validateAccountValance", true);

                    startActivityForResult(selectAccountIntent, MiBancoConstants.MC_SELECT_ACCOUNT_REQUEST_CODE);
                }

                break;

            case R.id.selectContact:
                Intent contactIntent = new Intent(this, AthmContacts.class);
                contactIntent.putExtra(MiBancoConstants.RECENT_CONTACTS_KEY, MiBancoConstants.RECENT_CONTACTS_KEY_EASYCASH);
                startActivityForResult(contactIntent, MiBancoConstants.CONTACT_CHOOSER_REQUEST_CODE);
                break;

            case R.id.btnForMe:
                viewChangesManagement(0);
                String message = getString(R.string.mc_transfer_confirm, Utils.getFormattedDollarAmount(String.valueOf(transferAmount)), selectedAccount.getNickname()+" "+ selectedAccount.getAccountLast4Num());
                AlertDialogParameters params = new AlertDialogParameters(mContext,0,this);
                params.setStrMessage(message);
                params.setTitle(getResources().getString(R.string.mc_confirm_stage_title));
                params.setPositiveButtonText(getString(R.string.mc_confirm_stage));
                params.setNegativeButtonText(getString(R.string.close));
                Utils.showAlertDialog(params);

                break;
            case R.id.btnForOther:
                sendMoneyToOthersAction();
                break;

            case R.id.btnConfirmForOther:
                if(contactTo.getRawPhoneNumber().equals(App.getApplicationInstance().getCustomerPhone(mContext))
                        || ContactsManagementUtils.getRawPhoneNumber(contactTo.getRawPhoneNumber(),mContext).length()>10){
                    MobileCashUtils.informativeMessage(mContext,R.string.ec_alert_selfsend_not_allowed);
                }else {
                    String contact = ContactsManagementUtils.getContactName(mContext, contactTo.getRawPhoneNumber(), contacts);
                    String messageConfirmForOther = getString(R.string.mc_transfer_confirm_for_other, Utils.getFormattedDollarAmount(String.valueOf(transferAmount)), selectedAccount.getNickname() + " " + selectedAccount.getAccountLast4Num(), contact);

                    AlertDialogParameters paramsForOthers = new AlertDialogParameters(mContext, 0, submitTrxForOtherOnClick);
                    paramsForOthers.setStrMessage(messageConfirmForOther);
                    paramsForOthers.setTitle(getResources().getString(R.string.mc_confirm_stage_title));
                    paramsForOthers.setPositiveButtonText(getString(R.string.mc_confirm_stage));
                    paramsForOthers.setNegativeButtonText(getString(R.string.close));
                    Utils.showAlertDialog(paramsForOthers);
                }

                break;
            case R.id.tvTransferAmount:
                if(selectedAccount !=null) {

                    int maxWsAmount = maximumAmountForme;
                    if (!isFormeTransfer){
                        maxWsAmount = maximumAmountForOther;
                    }

                    if(Utils.isBlankOrNull(selectedAccount.getBalance()))
                        selectedAccount.setBalance("$0.00");

                    int balance = Utils.getAmountIntValue(selectedAccount.getBalance());
                    if(balance < MiBancoConstants.MC_MIN_BALANCE_AMOUNT){
                        MobileCashUtils.informativeMessageWithoutTitle(mContext, R.string.mc_error_minbalance);
                        return;
                    }

                    if(balance < maxWsAmount){
                        maxWsAmount = balance;
                    }

                    int arraySize = maxWsAmount / MiBancoConstants.MC_MIN_BALANCE_AMOUNT;
                    amountOptions = new String[arraySize];
                    for (int i = 1; i <= arraySize; i++) {
                        amountOptions[i - 1] = Utils.getFormattedDollarAmount(String.valueOf(i * MiBancoConstants.MC_MIN_BALANCE_AMOUNT));
                    }

                    amountPicker();
                }
                break;
            default:
                super.onClick(v);
                break;
        }
        validateTransfer();
    }

    private void sendMoneyToOthersAction()
    {
        if(FeatureFlags.CASH_DROP_FINGERPRINT_REQUIRED() ){
            if(AutoLoginUtils.osFingerprintRequirements(this,true)) {//Phone has fingerprint configured
                if (AutoLoginUtils.getFingerprintPreference(this)) {// Has fingerprint enabled in the app
                    if(App.getApplicationInstance().isAutoLogin()){ //Verify that the login was through fingerprint
                        fingerprintResultAnalizer(fingerprintStatus);
                    }else{
                        // Display Fingerprint auth
                        new FingerprintModule(this, getFragmentManager());
                    }
                } else {
                    // Display view to enable fingerprint in the app
                    final Intent campaignIntent = new Intent(mContext, CampaignActivity.class);
                    campaignIntent.putExtra(MiBancoConstants.KEY_ENROLL_LITE_FINGERPRINT, true);
                    startActivityForResult(campaignIntent, ACTIVATE_FINGERPRINT);
                }
            }else{
                MobileCashUtils.informativeMessage(mContext,R.string.enrollment_lite_fingerprint_req_title,R.string.enrollment_lite_fingerprint_required);//RM-Message32
            }
        }
    }

    private void fingerprintResultAnalizer(int result)
    {
        switch (result){
            case 209: //FINGERPRINT_VALIDATION_SUCCESS
                activateViewForOthers();
                break;

            case 412: //FINGERPRINT_DELAY_FAILED
                try {
                    SimpleDateFormat format = new SimpleDateFormat(MiBancoConstants.FINGERPRINT_STORAGE_DATE_FORMAT);
                    User currentUser = App.getApplicationInstance().getCurrentUser();
                    if(currentUser != null && !Utils.isBlankOrNull(currentUser.getFingerprintBindDate())) {
                        Date bindDate = format.parse(App.getApplicationInstance().getCurrentUser().getFingerprintBindDate());
                        long hoursBinded = Utils.dateDifferenceInHours(new Date(),bindDate);
                        long hoursLeft = MiBancoConstants.EASYCASH_FINGERPRINT_WAITHOURS - hoursBinded;

                        String title;
                        if(hoursLeft > 0){

                            title = String.format(getResources().getString(R.string.ec_fingerprint_wait_title),String.valueOf(hoursLeft));
                        }else{
                            title = getResources().getString(R.string.ec_fingerprint_wait_message);
                        }

                        MobileCashUtils.informativeMessage(mContext,title);

                    }else {
                        MobileCashUtils.informativeMessage(mContext, R.string.easycash_fingerprint_delay);
                    }

                }catch(ParseException e){
                    Log.e("EasyCashStaging", e.toString());
                }
                break;

            case 413: //FINGERPRINT_NOT_REGISTERED
                registerDevice(this, ProductType.FINGERPRINT.toString(),true);
                break;

            case 414: //FINGERPRINT_DISABLED
                break;
        }
    }

    public void onFingerprintAuthSuccess(boolean success)
    {
        if(success){
            App.getApplicationInstance().setAutoLogin(true);
            registerDevice(this,ProductType.FINGERPRINT.toString(),true);
        }
    }

    public void registerDevice(final Context mContext, final String productType, final boolean bind) {
        LiteEnrollmentTasks.bindCustomerDevice(mContext,productType,bind, new LiteEnrollmentTasks.LiteEnrollmentListener<EnrollmentLiteResponse>() {
            @Override
            public void onLiteEnrollmentApiResponse(EnrollmentLiteResponse result) {
                if(result != null){
                    fingerprintStatus = result.getStatus();
                    if(result.getStatus() == EnrollmentLiteStatus.FINGERPRINT_DELAY_FAILED.getCode()){
                        AutoLoginUtils.setUpCountdownFingerprint(mContext,bind);
                    }
                    fingerprintResultAnalizer(result.getStatus());
                }
            }
        });
    }

    public void onFingerprintAuthCanceled()
    {
        if(mcTransaction != null && !Utils.isBlankOrNull(mcTransaction.getAmount())) {
            pendingForMeMessage();
        }
    }

    private void activateViewForOthers(){
        Intent contactIntent = new Intent(this, AthmContacts.class);
        contactIntent.putExtra(MiBancoConstants.RECENT_CONTACTS_KEY, MiBancoConstants.RECENT_CONTACTS_KEY_EASYCASH);
        viewChangesManagement(1);
        startActivityForResult(contactIntent, MiBancoConstants.CONTACT_CHOOSER_REQUEST_CODE);
    }

    private void showTransactionReciept(MobileCashTrx transaction) {
        Intent intent = new Intent(mContext, EasyCashStagingReceipt.class);
        intent.putExtra(MiBancoConstants.MOBILE_CASH_TRX_INFO_KEY, transaction);
        startActivity(intent);
    }

    private void amountPicker()
    {
        final Dialog dialog = new AppCompatDialog(this, R.style.Dialog);
        dialog.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.single_list_view, null);
        dialog.setContentView(convertView);
        final ListView lv = (ListView) convertView.findViewById(R.id.list_view_elements);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,amountOptions);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item =  adapter.getItem(position);

                if(item != null) {
                    tvTransferAmount.setText(item);
                    transferAmount = Integer.parseInt(item.replace("$", "").replace(".00", "").trim());
                    dialog.dismiss();
                    validateTransfer();
                }
            }
        });

        dialog.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which){
            case DialogInterface.BUTTON_POSITIVE:
                mcTransaction.setAmount(tvTransferAmount.getText().toString().replace("$",""));
                mcTransaction.setAccountFrontEndId(selectedAccount.getFrontEndId());
                mcTransaction.setNickname(selectedAccount.getNickname());
                mcTransaction.setAccountLast4Num(selectedAccount.getAccountLast4Num());
                mcTransaction.setAtmLast4Num(selectedAccount.getSelectedCardFromAccount().getAtmLast4Num());
                mcTransaction.setAtmType(selectedAccount.getSelectedCardFromAccount().getAtmType());
                mcTransaction.setTranType("WITHDRAW");
                mobileCashTasks =  new MobileCashTasks();
                mobileCashTasks.postTransaction(mContext, mcTransaction, this);

                break;
            default:
                break;
        }
        dialog.dismiss();

    }

    @Override
    public void onMobileCashApiResponse(EasyCashTrx result) {

        if(MiBancoConstants.MC_BLACKLIST_PHONE.equals(result.getContent().getBlackListStatus())){
            BPAnalytics.logEvent(BPAnalytics.EVENT_MC_PRESTAGE_FOR_OTHER_FAILED_SENDER_BLACKLIST);
            MobileCashUtils.informativeMessage(mContext, R.string.easycash_blacklist_sender);

        }else if (result != null && result.getContent().getStatus() != null
                && result.getContent().getStatus().equals(MiBancoConstants.MOBILE_CASH_SUCCESS)) {

            Double sentAmount = Double.valueOf(mcTransaction.getAmount());
            for(MobileCashTrx trx: result.getContent().getTransactions()){

                Double responseAmount = Double.valueOf(trx.getAmount());
                if(trx.getAccountLast4Num().equals(mcTransaction.getAccountLast4Num())
                        && sentAmount.compareTo(responseAmount) == 0
                        && Utils.isBlankOrNull(trx.getReceiverPhone())){

                    mcTransaction.setTrxReceiptId(trx.getTrxReceiptId());
                    mcTransaction.setTrxExpDate(trx.getTrxExpDate());
                    break;
                }

            }
            if(!Utils.isBlankOrNull(mcTransaction.getTrxReceiptId())) {
                showTransactionReciept(mcTransaction);
                BPAnalytics.logEvent(BPAnalytics.EVENT_MC_PRESTAGE_SUCCESSFULL);
                App.getApplicationInstance().setUpdateEasyCashHistory(true);
                finish();
            }
        } else {
            MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);
            BPAnalytics.logEvent(BPAnalytics.EVENT_MC_PRESTAGE_FAILED);
        }

    }
}