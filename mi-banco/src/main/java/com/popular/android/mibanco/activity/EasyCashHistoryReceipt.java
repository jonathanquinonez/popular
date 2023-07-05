package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BasePermissionsActivity;
import com.popular.android.mibanco.model.PhonebookContact;
import com.popular.android.mibanco.util.AlertDialogParameters;
import com.popular.android.mibanco.util.ContactsManagementUtils;
import com.popular.android.mibanco.util.MobileCashUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.AlertDialogFragment;
import com.popular.android.mibanco.ws.response.MobileCashTrx;

import java.util.HashMap;

import static android.view.View.GONE;

/**
 * Activity that manages the Easy Cash money pickup receipt display process
 */
public class EasyCashHistoryReceipt extends BasePermissionsActivity implements AlertDialogFragment.AlertDialogListener {

    private MobileCashTrx transaction;
    private Context mContext = this;
    private TextView txtToLabel;
    private TextView txtRecipient;
    private TextView txtAccountName;
    private TextView txtNoteLabel;
    private TextView txtNote;
    private TextView txtLocationLabel;
    private TextView txtLocation;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobilecash_history_receipt);

        final Context mContext = this;

        TextView txtReceiptId = (TextView)findViewById(R.id.txtReceiptId);
        TextView txtAmount = (TextView)findViewById(R.id.txtAmount);
        TextView txtExpDateTitle = (TextView)findViewById(R.id.txtExpirationTitle);
        TextView txtExpDate = (TextView)findViewById(R.id.txtExpirationDate);
        TextView txtStatus = (TextView)findViewById(R.id.status);
        TextView txtDelete = (TextView)findViewById(R.id.deleteAction);

        txtToLabel = (TextView)findViewById(R.id.txtToTitle);
        txtRecipient = (TextView)findViewById(R.id.txtRecipient);
        txtAccountName = (TextView)findViewById(R.id.txtAccountName);
        txtNoteLabel = (TextView)findViewById(R.id.txtNoteTitle);
        txtNote = (TextView)findViewById(R.id.txtNote);
        txtLocationLabel = (TextView)findViewById(R.id.txtLocationTitle);
        txtLocation = (TextView)findViewById(R.id.txtLocation);

        Bundle extras = getIntent().getExtras();
        transaction = (MobileCashTrx)extras.get(MiBancoConstants.MC_REDEEM_SUCCESS_TRX);

        if(transaction == null) {
            finish();
        }else{
            if (!Utils.isBlankOrNull(transaction.getStatus())) {
                int statusMessage = getStatusMessage(transaction.getStatus(), ("true".equals(transaction.getReceived())));
                if (statusMessage != 0) {
                    txtStatus.setText(getResources().getString(statusMessage));
                    txtStatus.setTextColor(ContextCompat.getColor(mContext, getStatusColor(transaction.getStatus())));
                }

            } else {
                txtStatus.setVisibility(View.INVISIBLE);
            }

            txtReceiptId.setText(transaction.getTrxReceiptId());
            txtAmount.setText(Utils.getFormattedDollarAmount(transaction.getAmount()));

            if (!Utils.isBlankOrNull(transaction.getMemo())) {
                txtNote.setText(transaction.getMemo());
            } else {
                txtNoteLabel.setVisibility(GONE);
                txtNote.setVisibility(GONE);
            }

        if (!Utils.isBlankOrNull(transaction.getStatus()) && (!Utils.isBlankOrNull(transaction.getTrxDate()) || !Utils.isBlankOrNull(transaction.getTrxExpDate()))) {
            switch (transaction.getStatus()){
                case "EXPIRED": case "PENDING_ATM": case "PROCESSING":
                    txtExpDateTitle.setText(getString(R.string.mc_stage_receipt_expiration));
                    txtExpDate.setText(MobileCashUtils.getFormattedExpDate(transaction.getTrxExpDate(), this));
                    break;
                default:
                    txtExpDateTitle.setText(getString(R.string.mc_stage_receipt_completion));
                    txtExpDate.setText(MobileCashUtils.getFormattedExpDate(transaction.getTrxDate(), this));
                    break;
            }
        }else {
            txtExpDateTitle.setVisibility(GONE);
            txtExpDate.setVisibility(GONE);
        }

        if(!Utils.isBlankOrNull(transaction.getAtmLocation()))
        {
            txtLocationLabel.setVisibility(View.VISIBLE);
            txtLocation.setVisibility(View.VISIBLE);
            txtLocation.setText(transaction.getAtmLocation());
        }else{
            txtLocationLabel.setVisibility(GONE);
            txtLocation.setVisibility(GONE);
        }

            Button btnOk = (Button) findViewById(R.id.btnOk);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            boolean hasDeleteAction = getIntent().getBooleanExtra("deleteAction", false);
            if (hasDeleteAction) {
                txtDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialogParameters params = new AlertDialogParameters(mContext, R.string.mc_deletetrx_message, deleteTrxOnClick);
                        params.setPositiveButtonText(getResources().getString(R.string.ok));
                        params.setNegativeButtonText(getResources().getString(R.string.mc_deletetrx_back).toUpperCase());
                        Utils.showAlertDialog(params);
                    }
                });
            } else {
                txtDelete.setVisibility(View.INVISIBLE);
            }

            if (Utils.isBlankOrNull(transaction.getStatus()) && !hasDeleteAction) {
                findViewById(R.id.statusAndAction).setVisibility(GONE);
            }
        }
    }

    private void setContactName(boolean hasContactPermission)
    {
        HashMap<String, PhonebookContact> contacts;
        try {
            contacts = null;
            if(hasContactPermission){
                contacts = ContactsManagementUtils.getContactsWithPhones(this);
            }

            if("true".equalsIgnoreCase(transaction.getReceived())){
                txtToLabel.setVisibility(GONE);
                txtRecipient.setVisibility(GONE);

                txtAccountName.setText(ContactsManagementUtils.getContactName(mContext, transaction.getSenderPhone(),contacts));

            }else {
                txtAccountName.setText(Utils.concatenateStrings(new String[]{transaction.getNickname()," ",transaction.getAccountLast4Num()}));
                if (!Utils.isBlankOrNull(transaction.getReceiverPhone())) {
                    txtRecipient.setText(ContactsManagementUtils.getContactName(mContext, transaction.getReceiverPhone(),contacts));
                } else {
                    txtToLabel.setVisibility(GONE);
                    txtRecipient.setVisibility(GONE);
                }
            }
        }catch (Exception e){
            Log.e("EasyCashHistoryReceipt", e.toString());
        }
    }

    public void onPermissionResult(boolean permissionGranted)
    {
        setContactName(permissionGranted);
    }

    private int getStatusMessage(String status, boolean isReceiver)
    {
        switch (status){
            case "SUCCESS":
                return R.string.cashdrop_history_status_success;

            case "EXCEPTION":
                return R.string.cashdrop_history_status_exception;

            case "PROCESSING":
                return R.string.cashdrop_history_status_processing;

            case "PENDING_ATM":
                return (isReceiver?R.string.cashdrop_history_status_pending_atm_receiver:R.string.cashdrop_history_status_pending_atm_sender);

            case "EXPIRED":
                return R.string.cashdrop_history_status_expired;

            case "ERROR":
                return (isReceiver?R.string.cashdrop_history_status_error_receiver:R.string.cashdrop_history_status_error_sender);

        }
        return 0;
    }

    private int getStatusColor(String status)
    {
        switch (status){
            case "SUCCESS":
                return R.color.ec_status_green;

            case "EXCEPTION":
                return R.color.ec_status_orange;

            case "PROCESSING":
                return R.color.ec_status_orange;

            case "PENDING_ATM":
                return R.color.ec_status_orange;

            case "EXPIRED":
                return R.color.ec_status_red;

            case "ERROR":
                return R.color.ec_status_red;
        }
        return 0;
    }

    /*

    if(!Utils.isBlankOrNull(item.getStatus())){
            if(item.getStatus().equalsIgnoreCase("SUCCESS")){
                holder.getImageQr().setImageResource(R.drawable.easycash_trx_success);
            }else if(item.getStatus().equalsIgnoreCase("EXCEPTION") || item.getStatus().equalsIgnoreCase("PROCESSING")){
                holder.getImageQr().setImageResource(R.drawable.easycash_trx_warning);
            }else if(item.getStatus().equalsIgnoreCase("EXPIRED") || item.getStatus().equalsIgnoreCase("ERROR")){
                holder.getImageQr().setImageResource(R.drawable.easycash_trx_error);
            }
        }
     */


    DialogInterface.OnClickListener deleteTrxOnClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    MobileCashUtils.deleteTransactionnsAndFinish(mContext,transaction.getTrxReceiptId());
                    break;
                default:
                    break;
            }
            dialog.dismiss();
        }
    };



    public interface Callback {

        void onDelete();

    }

}
