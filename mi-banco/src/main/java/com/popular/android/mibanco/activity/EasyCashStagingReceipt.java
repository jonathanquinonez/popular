package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BasePermissionsSessionActivity;
import com.popular.android.mibanco.model.PhonebookContact;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.ContactsManagementUtils;
import com.popular.android.mibanco.util.MobileCashUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.ws.response.MobileCashTrx;

import java.util.HashMap;


/**
 * Activity that manages the Easy cash staging receipt display
 */
public class EasyCashStagingReceipt extends BasePermissionsSessionActivity {


    private HashMap<String, PhonebookContact> contacts;
    private TextView txtRecipient;
    private Context mContext = this;
    private MobileCashTrx transaction;

    @Override
    public void onCreate(final Bundle savedInstanceState) {

        if (App.getApplicationInstance() != null && App.getApplicationInstance().getApiClient() != null) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.mobilecash_staging_receipt);

            Bundle bundle = this.getIntent().getExtras();
            transaction = (MobileCashTrx) bundle.getSerializable(MiBancoConstants.MOBILE_CASH_TRX_INFO_KEY);

            TextView txtReceiptTitle = (TextView) findViewById(R.id.txtReceiptTitle);
            TextView txtReceiptId = (TextView) findViewById(R.id.txtReceiptId);
            TextView txtAccountName = (TextView) findViewById(R.id.txtAccountName);
            TextView txtAmount = (TextView) findViewById(R.id.txtAmount);
            TextView txtToTitle = (TextView) findViewById(R.id.txtToTitle); //optional
            txtRecipient = (TextView) findViewById(R.id.txtRecipient); //optional
            TextView txtNoteTitle = (TextView) findViewById(R.id.txtNoteTitle); //optional
            TextView txtNote = (TextView) findViewById(R.id.txtNote); //optional


            TextView txtExpirationDate = (TextView) findViewById(R.id.txtExpirationDate);
            TextView tvStageReceiptInstruction1 = (TextView) findViewById(R.id.tvStageReceiptInstruction1);
            TextView tvStageReceiptInstruction2 = (TextView) findViewById(R.id.tvStageReceiptInstruction2);
            TextView tvStageReceiptInstruction3 = (TextView) findViewById(R.id.tvStageReceiptInstruction3);
            TextView tvStageReceiptInstruction4 = (TextView) findViewById(R.id.tvStageReceiptInstruction4);

            txtReceiptId.setText(transaction.getTrxReceiptId());
            txtAccountName.setText(Utils.concatenateStrings(new String[]{transaction.getNickname(), " ", transaction.getAccountLast4Num()}));
            txtAmount.setText(Utils.getFormattedDollarAmount(transaction.getAmount()));
            txtExpirationDate.setText(MobileCashUtils.getFormattedExpDate(transaction.getTrxExpDate(), this));

            txtToTitle.setVisibility(View.GONE);
            txtRecipient.setVisibility(View.GONE);
            txtNoteTitle.setVisibility(View.GONE);
            txtNote.setVisibility(View.GONE);

            Button scanBtn = (Button) findViewById(R.id.btnScan);

            if (!Utils.isBlankOrNull(transaction.getReceiverPhone())) { // TRANSACTION FROM ME TO OTHERS

                txtReceiptTitle.setText(getResources().getString(R.string.mc_stage_receipt_toother_title));
                tvStageReceiptInstruction1.setText(getString(R.string.ec_stage_receipt_instruction_1));
                tvStageReceiptInstruction2.setText(getString(R.string.ec_stage_receipt_instruction_2));
                tvStageReceiptInstruction3.setText(getString(R.string.ec_stage_receipt_instruction_3));

                ImageView image1 = (ImageView)findViewById(R.id.imageInstructions1);
                ImageView image2 = (ImageView)findViewById(R.id.imageInstructions2);
                ImageView image3 = (ImageView)findViewById(R.id.imageInstructions3);
                image1.setImageResource(R.drawable.ic_mc_location);
                image2.setImageResource(R.drawable.ic_mc_phone);
                image3.setImageResource(R.drawable.ic_mc_withdraw);

                (findViewById(R.id.withdraw_instuctions)).setVisibility(View.GONE);

                txtToTitle.setVisibility(View.VISIBLE);
                txtRecipient.setVisibility(View.VISIBLE);

                if (!Utils.isBlankOrNull(transaction.getMemo())) {
                    txtNoteTitle.setVisibility(View.VISIBLE);
                    txtNote.setVisibility(View.VISIBLE);
                    txtNote.setText(transaction.getMemo());
                    setUpReceiptHeight(400);
                }else{
                    setUpReceiptHeight(360);
                }

                scanBtn.setText(R.string.ec_stage_receipt_button);
                scanBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TEXT MESSAGE LOGIC
                        String message = getString(R.string.ec_forother_text_message, Utils.getFormattedDollarAmount(String.valueOf(transaction.getAmount())));
                        Utils.sendTextMessage(mContext,transaction.getReceiverPhone(),message);
                        finish();
                        BPAnalytics.logEvent(BPAnalytics.EVENT_MC_SEND_SMS_INSTRUCTIONS);
                    }
                });


            }else{ // TRANSACTION FROM ME TO ME

                setUpReceiptHeight(330);
                scanBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, EasyCashRedeem.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(MiBancoConstants.KEY_MOBILE_CASH_TRX, transaction);
                        intent.putExtras(bundle);
                        intent.putExtra(MiBancoConstants.KEY_ENROLL_LITE_IS_CUSTOMER, true);
                        mContext.startActivity(intent);
                        finish();
                    }
                });
            }

            TextView clickableLinkText = (TextView) findViewById(R.id.tvStageReceiptInstruction1);
            String linkString = "<a href=\""+getResources().getString(R.string.easycash_locator_url)+"\">"+tvStageReceiptInstruction1.getText().toString()+"</a>";
            clickableLinkText.setText(Html.fromHtml(linkString));
            clickableLinkText.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        }

    }

    public void onPermissionResult(boolean permissionGranted)
    {
        if(permissionGranted){
            contacts = ContactsManagementUtils.getContactsWithPhones(this);
        }else{
            contacts = null;
        }
        transactionsFromMeToOthersView();
    }

    private void transactionsFromMeToOthersView()
    {
        if(!Utils.isBlankOrNull(transaction.getReceiverPhone())) {
            String contactName = ContactsManagementUtils.getContactName(mContext, transaction.getReceiverPhone(), contacts);
            txtRecipient.setText(contactName);
        }
    }

    private void setUpReceiptHeight(int dp)
    {
        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (dp * scale + 0.5f);

        RelativeLayout receiptLayout = (RelativeLayout)findViewById(R.id.receipt_layout);
        receiptLayout.getLayoutParams().height = pixels;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}