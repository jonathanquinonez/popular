package com.popular.android.mibanco.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.model.PhonebookContact;
import com.popular.android.mibanco.util.ContactsManagementUtils;
import com.popular.android.mibanco.util.MobileCashUtils;
import com.popular.android.mibanco.util.PermissionsManagerUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.AlertDialogFragment;
import com.popular.android.mibanco.ws.response.MobileCashTrx;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;

/**
 * Activity that manages the Easy Cash money pickup receipt display process
 */
public class EasyCashRedeemReceipt extends BaseActivity implements AlertDialogFragment.AlertDialogListener {

    private HashMap<String, PhonebookContact> contacts;
    private Date completionDate;
    private static final long SECONDS_FOR_UPDATE = 15;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobilecash_pickup_processed);

        final Context mContext = this;
        completionDate = new Date();

        List<String> missingPermissions = PermissionsManagerUtils.missingPermissions(this, Arrays.asList(MiBancoConstants.CONTACTS_PERMISSIONS));
        if(missingPermissions.size() == 0) {
            contacts = ContactsManagementUtils.getContactsWithPhones(this);
        }
        TextView txtReceiptId = (TextView) findViewById(R.id.txtReceiptId);
        TextView txtAccountName = (TextView) findViewById(R.id.txtAccountName);
        TextView txtAmount = (TextView) findViewById(R.id.txtAmount);
        TextView txtToTitle = (TextView) findViewById(R.id.txtToTitle); //optional
        TextView txtRecipient = (TextView) findViewById(R.id.txtRecipient); //optional
        TextView txtNoteTitle = (TextView) findViewById(R.id.txtNoteTitle); //optional
        TextView txtNote = (TextView) findViewById(R.id.txtNote); //optional
        TextView txtExpirationDate = (TextView) findViewById(R.id.txtExpirationDate);



        Bundle extras = getIntent().getExtras();
        MobileCashTrx transaction = (MobileCashTrx)extras.get(MiBancoConstants.MC_REDEEM_SUCCESS_TRX);

        Button btnOk = (Button)findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long seconds = Utils.dateDifferenceInSeconds(new Date(),completionDate);
                if(seconds >= SECONDS_FOR_UPDATE){
                    MobileCashUtils.easyCashRedeemReceiptNextScreen(mContext, App.getApplicationInstance().getCurrentUser() != null);
                    finish();
                }else{
                    long secondsLeft = SECONDS_FOR_UPDATE - seconds;
                    countdownUntilShowHistory(secondsLeft, mContext);
                }
            }
        });

        txtReceiptId.setText(transaction.getTrxReceiptId());
        txtAmount.setText(Utils.getFormattedDollarAmount(transaction.getAmount()));
        txtExpirationDate.setText(MobileCashUtils.getFormattedExpDate(transaction.getTrxExpDate(), this));

        try {
            if("true".equalsIgnoreCase(transaction.getReceived())){
                txtToTitle.setVisibility(GONE);
                txtRecipient.setVisibility(GONE);

                txtAccountName.setText(ContactsManagementUtils.getContactName(mContext, transaction.getSenderPhone(),contacts));

            }else {
                txtAccountName.setText(Utils.concatenateStrings(new String[]{transaction.getNickname()," ",transaction.getAccountLast4Num()}));
                if (!Utils.isBlankOrNull(transaction.getReceiverPhone())) {
                    txtRecipient.setText(ContactsManagementUtils.getContactName(mContext, transaction.getReceiverPhone(),contacts));
                } else {
                    txtToTitle.setVisibility(GONE);
                    txtRecipient.setVisibility(GONE);
                }
            }
        }catch (Exception e){
            Log.e("EasyCashRedeemReceipt", e.toString());
        }

        if(!Utils.isBlankOrNull(transaction.getMemo()))
        {
            txtNote.setText(transaction.getMemo());
        }else{
            txtNoteTitle.setVisibility(GONE);
            txtNote.setVisibility(GONE);
        }

    }

    /**
     *
     * @param secondsLeft The amount of seconds left after the user clicked OK (substracted from SECONDS_FOR_UPDATE)
     * @param mContext The activity context
     */
    private void countdownUntilShowHistory(long secondsLeft, final Context mContext)
    {

        final ProgressDialog dialog = new ProgressDialog(mContext, ProgressDialog.THEME_HOLO_LIGHT);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getResources().getString(R.string.processing_transaction));
        dialog.show();

        final long secondsLeftInMillis = (secondsLeft*1000);
        new CountDownTimer(secondsLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //this will be done every 1000 milliseconds ( 1 seconds )
                long progress = (secondsLeftInMillis - millisUntilFinished) / 1000;
                dialog.setProgress((int)progress);
            }

            @Override
            public void onFinish() {
                MobileCashUtils.easyCashRedeemReceiptNextScreen(mContext,App.getApplicationInstance().getCurrentUser() != null);
                dialog.dismiss();
                finish();
            }

        }.start();
    }

}