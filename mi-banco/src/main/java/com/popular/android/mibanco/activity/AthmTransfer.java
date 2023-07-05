package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoPreferences;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.AccountCard;
import com.popular.android.mibanco.model.PhonebookContact;
import com.popular.android.mibanco.task.AthmTasks;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.ContactsManagementUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.AlertDialogFragment;
import com.popular.android.mibanco.ws.response.AthmSendMoneyConfirmation;
import com.popular.android.mibanco.ws.response.AthmSendMoneyInfo;

/**
 * Activity to manage the money transfer in the ATHM send money process
 */
public class AthmTransfer extends AthmActivity implements AlertDialogFragment.AlertDialogListener {

    private AccountCard cardFrom;
    private AthmSendMoneyInfo serviceInfo;
    private PhonebookContact contactTo;
    private int transferAmount;

    private TextView tvTransferAmount;
    private TextView tvRecipientHint;
    private LinearLayout viewContact;
    private Button btnSendMoney;
    private EditText etMessage;

    private Context mContext = this;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(App.getApplicationInstance() != null && App.getApplicationInstance().getApiClient() != null) {
            setContentView(R.layout.athm_transfer);

            tvTransferAmount = (TextView) findViewById(R.id.tvTransferAmount);
            tvRecipientHint = (TextView) findViewById(R.id.tvRecipientHint);
            viewContact = (LinearLayout) findViewById(R.id.viewContact);
            btnSendMoney = (Button) findViewById(R.id.btnSendMoney);
            etMessage = (EditText) findViewById(R.id.etMessage);

            btnSendMoney.setOnClickListener(this);
            tvTransferAmount.setOnClickListener(this);
            findViewById(R.id.imgParticipants).setOnClickListener(this);
            findViewById(R.id.viewSelectRecipient).setOnClickListener(this);

            if (savedInstanceState != null) {
                transferAmount = savedInstanceState.getInt(MiBancoConstants.AMOUNT_PICKER_AMOUNT_KEY);
                contactTo = (PhonebookContact) savedInstanceState.getSerializable(MiBancoConstants.ATHM_CONTACT_KEY);
            }
            resetValues();
        }
    }

    private void resetValues() {

        if(App.getApplicationInstance() != null && App.getApplicationInstance().getApiClient() != null) {
            tvTransferAmount.setText(Utils.formatAmount(transferAmount));
            ContactsManagementUtils.setContactToView(this, contactTo,tvRecipientHint,viewContact);
            tvTransferAmount.setText(Utils.formatAmount(transferAmount));
            validateTransfer();
            etMessage.setText("");

            AthmTasks.getAthmSendMoneyInfo(this, new AthmTasks.AthmListener<AthmSendMoneyInfo>() {
                @Override
                public void onAthmApiResponse(AthmSendMoneyInfo result) {
                    if(result != null) {
                        serviceInfo = result;
                        cardFrom = result.getFromAccount();
                        setAthmCard();
                    }
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MiBancoConstants.AMOUNT_PICKER_AMOUNT_KEY, transferAmount);
        outState.putSerializable(MiBancoConstants.ATHM_CONTACT_KEY, contactTo);
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
                case MiBancoConstants.CONTACT_CHOOSER_REQUEST_CODE:
                    if (data != null) {
                        contactTo = (PhonebookContact) data.getSerializableExtra(MiBancoConstants.ATHM_CONTACT_KEY);
                        ContactsManagementUtils.setContactToView(this, contactTo,tvRecipientHint,viewContact);
                        validateTransfer();
                    }
                    break;
                case MiBancoConstants.ATHM_TRANSFER_SENT_REQUEST_CODE:
                    contactTo = null;
                    transferAmount = 0;
                    resetValues();
                    break;
                default:
                    break;
            }
        }
    }

    private void setAthmCard() {
        if(App.getApplicationInstance() != null && App.getApplicationInstance().getApiClient() != null) {
            TextView tvName = (TextView) findViewById(R.id.tvName);
            TextView tvBalance = (TextView) findViewById(R.id.tvBalance);
            TextView tvLast4Digits = (TextView) findViewById(R.id.tvLast4Digits);
            ImageView imgCard = (ImageView) findViewById(R.id.imgCard);

            if(tvName != null && tvBalance != null
                    && tvLast4Digits != null && cardFrom != null) {
                tvName.setText(cardFrom.getNickname());
                tvBalance.setText(cardFrom.getBalance());
                tvLast4Digits.setText(cardFrom.getAccountLast4Num());
                ImageLoader.getInstance().displayImage(cardFrom.getCardImageUri(), imgCard);
            }
        }
    }

    private void validateTransfer() {
        if (transferAmount > 0 && contactTo != null) {
            btnSendMoney.setEnabled(true);
        } else {
            btnSendMoney.setEnabled(false);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewSelectRecipient:
                startActivityForResult(new Intent(this, AthmContacts.class), MiBancoConstants.CONTACT_CHOOSER_REQUEST_CODE);
                break;
            case R.id.btnSendMoney:
                String serviceCharge = "0.00";
                if(serviceInfo != null && !Utils.isBlankOrNull(serviceInfo.getServiceCharge())){
                    serviceCharge = serviceInfo.getServiceCharge();
                }
                AlertDialogFragment.showAlertDialog(
                        this,
                        null,
                        getString(R.string.athm_transfer_message, Utils.formatAmount(transferAmount), contactTo.getName(), serviceCharge),
                        getString(R.string.confirm),
                        getString(R.string.cancel),
                        MiBancoConstants.MiBancoDialogId.ATHM_TRANSFER_CONFIRMATION,
                        null,
                        false);
                break;
            case R.id.tvTransferAmount:
                Intent amountPickerIntent = new Intent(this, EnterAmount.class);
                amountPickerIntent.putExtra(MiBancoConstants.AMOUNT_PICKER_AMOUNT_KEY, transferAmount);
                startActivityForResult(amountPickerIntent, MiBancoConstants.AMOUNT_PICKER_REQUEST_CODE);
                break;
            case R.id.imgParticipants:
                Intent webViewIntent = new Intent(this, WebViewActivity.class);
                webViewIntent.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, getString(R.string.athm_institutions_url));
                webViewIntent.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
                startActivity(webViewIntent);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private void sendTransfer() {
        if(App.getApplicationInstance() != null && App.getApplicationInstance().getApiClient() != null) {
            AthmTasks.postAthmSendMoney(this, Utils.formatAmount(transferAmount).replaceAll("[^0-9.]", ""), etMessage.getText().toString(), contactTo.getRawPhoneNumber(), new AthmTasks.AthmListener<AthmSendMoneyConfirmation>() {
                @Override
                public void onAthmApiResponse(AthmSendMoneyConfirmation result) {

                    if (result != null) {
                        if (result.getAlertMessage() == null || result.getAlertMessage().equals("")) {
                            Intent transferSentIntent = new Intent();
                            transferSentIntent.setClass(AthmTransfer.this, AthmTransferSent.class);
                            transferSentIntent.putExtra(MiBancoConstants.ATHM_TRANSFER_AMOUNT_KEY, result.getAmount());
                            transferSentIntent.putExtra(MiBancoConstants.ATHM_TRANSFER_PHONE_KEY, result.getPhone());
                            startActivityForResult(transferSentIntent, MiBancoConstants.ATHM_TRANSFER_SENT_REQUEST_CODE);
                            MiBancoPreferences.addAthmRecentPhoneNumber(MiBancoPreferences.getLoggedInUsername(), contactTo.getRawPhoneNumber(), MiBancoConstants.RECENT_CONTACTS_KEY_ATHM);
                            BPAnalytics.logEvent(BPAnalytics.EVENT_ATHM_SENDMONEY_SUCCESSFUL);
                        } else {
                            BPAnalytics.logEvent(BPAnalytics.EVENT_ATHM_SENDMONEY_FAILED);
                            showAlertDialog(android.text.Html.fromHtml(result.getAlertMessage()).toString(), getString(R.string.ok), null, MiBancoConstants.MiBancoDialogId.ATHM_TRANSFER_ERROR);
                        }
                    }
                }
            });
        }
    }

    private void showAlertDialog(String dialogMessage, String positiveButtonText, String negativeButtonText, int dialogId)
    {
        AlertDialogFragment.showAlertDialog(
                this,null,dialogMessage,positiveButtonText,negativeButtonText,dialogId,null,false);
    }

    @Override
    public void onPositiveClick(DialogFragment dialog, int dialogId, Bundle dataBundle) {
        if (dialogId == MiBancoConstants.MiBancoDialogId.ATHM_TRANSFER_CONFIRMATION) {
            sendTransfer();
        } else if(dialogId == MiBancoConstants.MiBancoDialogId.ATHM_TRANSFER_ERROR){
            contactTo = null;
            transferAmount = 0;
            resetValues();
        }else {
            super.onPositiveClick(dialog, dialogId, dataBundle);
        }
        if(dialog != null) {
            dialog.dismiss();
        }
    }
}