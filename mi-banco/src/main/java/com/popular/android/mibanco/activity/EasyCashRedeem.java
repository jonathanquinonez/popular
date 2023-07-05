package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BasePermissionsActivity;
import com.popular.android.mibanco.model.PhonebookContact;
import com.popular.android.mibanco.task.MobileCashTasks;
import com.popular.android.mibanco.util.AlertDialogParameters;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.ContactsManagementUtils;
import com.popular.android.mibanco.util.MobileCashUtils;
import com.popular.android.mibanco.util.PermissionsManagerUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.AlertDialogFragment;
import com.popular.android.mibanco.view.CustomCompoundBarcodeView;
import com.popular.android.mibanco.ws.response.MobileCashTrx;
import com.popular.android.mibanco.ws.response.MobileCashTrxInfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.popular.android.mibanco.MiBancoConstants.KEY_ENROLL_LITE_IS_CUSTOMER;


/**
 * Activity that manages the Easy Cash money pickup process
 */
public class EasyCashRedeem extends BasePermissionsActivity implements AlertDialogFragment.AlertDialogListener {


    final Context mContext = this;
    private MobileCashTrx transaction;
    private CustomCompoundBarcodeView barcodeScannerView;
    private boolean isTransactionForMe;
    private HashMap<String, PhonebookContact> contacts;
    private boolean blockedScanner = false;
    private boolean isCustomer = false;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobilecash_pickup_scan);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        transaction = (MobileCashTrx)bundle.getSerializable(MiBancoConstants.KEY_MOBILE_CASH_TRX);
        isTransactionForMe = Utils.isBlankOrNull(transaction.getSenderPhone());
        isCustomer = getIntent().getBooleanExtra(KEY_ENROLL_LITE_IS_CUSTOMER, false);

        TextView txtAccountName = (TextView)findViewById(R.id.txtMcWithdrawalAccountName);
        TextView txtTrxAmount = (TextView)findViewById(R.id.txtTrxAmount);
        TextView txtTrxLastFourNum = (TextView)findViewById(R.id.txtLast4Num);
        TextView txtTrxExpirationDate = (TextView)findViewById(R.id.txtTrxExpirationDate);
        TextView txtAthType = (TextView)findViewById(R.id.txtAthType);
        ImageView img = (ImageView)findViewById(R.id.accountImg);
        Button btnDelete = (Button) findViewById(R.id.btnDelete);

        //TESTING PURPOSES: DO NOT UNCOMMENT

        /*
        txtAccountName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //242762392bd441f4bde92fa5fa1c3800b39d79eb648a0906b1c5ef75dcc252a1989c8d20160316133558
                processTransaction("242762392bd441f4bde92fa5fa1c3800b39d79eb648a0906b1c5ef75dcc252a1989c8d20160316133558");
            }
        });
        */

        if(transaction != null && txtAccountName != null && txtTrxAmount != null
                && txtTrxLastFourNum != null && txtTrxExpirationDate != null
                && txtAthType != null && img != null && btnDelete != null) {

            txtTrxAmount.setText(Utils.getFormattedDollarAmount(transaction.getAmount()));
            String dateText = MobileCashUtils.getFormattedExpDate(transaction.getTrxExpDate(), this);
            String expirationDateText = String.format(getResources().getString(R.string.mc_pickup_expiration),dateText);
            txtTrxExpirationDate.setText(expirationDateText);

            if(isTransactionForMe) {
                btnDelete.setOnClickListener(deleteOnClick);
                txtAccountName.setText(String.format("%s %s",transaction.getNickname(),transaction.getAccountLast4Num()));
                txtTrxLastFourNum.setText(txtTrxLastFourNum.getText().toString().replace("(?)", transaction.getAtmLast4Num()));

                if (transaction.getAtmType().equals("INT")) {
                    img.setImageResource(R.drawable.account_image_international);
                    txtAthType.setText(getResources().getString(R.string.mc_ath_international));
                }else
                if(transaction.getAtmType().equals("REG")){
                    if (FeatureFlags.MBMT_417()) {
                        img.setImageResource(R.drawable.account_image_regular);
                    }else {
                        img.setImageResource(R.drawable.account_image_default);
                    }
                }

            }else{

                contacts = null;
                img.setVisibility(View.GONE);
                btnDelete.setVisibility(View.INVISIBLE);

                if (!Utils.isBlankOrNull(transaction.getMemo())) {
                    txtAthType.setText(String.format(getResources().getString(R.string.ec_message_from), transaction.getMemo()));
                } else {
                    txtAthType.setVisibility(View.GONE);
                }

                txtTrxLastFourNum.setVisibility(View.GONE);


                List<String> missingPermissions = PermissionsManagerUtils.missingPermissions(this, Arrays.asList(MiBancoConstants.CONTACTS_PERMISSIONS));
                if(missingPermissions.size() == 0) {
                    contacts = ContactsManagementUtils.getContactsWithPhones(this);
                }

                String contact = ContactsManagementUtils.getContactName(mContext, transaction.getSenderPhone(), contacts);
                txtAccountName.setText(String.format(getResources().getString(R.string.ec_received_from), contact));
            }
        }
    }

    public void onPermissionResult(boolean permissionGranted)
    {
        RelativeLayout noPermissionLayout = (RelativeLayout)findViewById(R.id.rl_mc_nocamera_permission);
        if(permissionGranted){
            if(noPermissionLayout != null)
                noPermissionLayout.setVisibility(View.GONE);
            barcodeScannerView = (CustomCompoundBarcodeView)findViewById(R.id.zxing_barcode_scanner);
            if(barcodeScannerView != null) {
                barcodeScannerView.decodeContinuous(callback);
            }
        }else{
            if(noPermissionLayout != null)
                noPermissionLayout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(barcodeScannerView != null && !blockedScanner) {
            barcodeScannerView.resume();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(barcodeScannerView != null) {
            barcodeScannerView.pause();
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                processTransaction(result.getText());
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };


    private boolean processTransaction(String code)
    {
        barcodeScannerView.pause();
        blockedScanner = true;
        if(!code.equals("")
                && code.length()> MiBancoConstants.MOBILE_CASH_CODE_PREFIX.length()
                && code.trim().substring(0, MiBancoConstants.MOBILE_CASH_CODE_PREFIX.length()).equals(MiBancoConstants.MOBILE_CASH_CODE_PREFIX)) {

            MobileCashTasks.postTransactionCode(mContext, code,transaction.getTrxReceiptId(), new MobileCashTasks.MobileCashListener<MobileCashTrxInfo>() {
                @Override
                public void onMobileCashApiResponse(MobileCashTrxInfo result) {
                    if(result != null && result.getTransactionStatus() != null
                            && result.getTransactionStatus().equals(MiBancoConstants.MOBILE_CASH_CODE_SUCCESS)) {
                        Intent intent = new Intent(mContext, EasyCashRedeemReceipt.class);
                        intent.putExtra(MiBancoConstants.MC_REDEEM_SUCCESS_TRX,transaction);
                        startActivity(intent);
                        if (isTransactionForMe) {
                            BPAnalytics.logEvent(BPAnalytics.EVENT_MC_SCAN_SUCCESSFULL);
                        } else {
                            if (isCustomer) {
                                BPAnalytics.logEvent(BPAnalytics.EVENT_MC_SCAN_FROM_OTHER_SUCCESSFULL);
                            } else {
                                BPAnalytics.logEvent(BPAnalytics.EVENT_MC_SCAN_FROM_OTHER_NON_CUSTOMER_SUCCESSFUL);
                            }
                        }
                        finish();
                    }else{
                        if (isTransactionForMe) {
                            BPAnalytics.logEvent(BPAnalytics.EVENT_MC_SCAN_FAILED);
                        } else {
                            if (isCustomer) {
                                BPAnalytics.logEvent(BPAnalytics.EVENT_MC_SCAN_FROM_OTHER_FAILED);
                            } else {
                                BPAnalytics.logEvent(BPAnalytics.EVENT_MC_SCAN_FROM_OTHER_NON_CUSTOMER_FAILED);
                            }

                        }
                        AlertDialogParameters params = new AlertDialogParameters(mContext,R.string.mc_service_error_message,trxErrorOnClick);
                        params.setPositiveButtonText(mContext.getResources().getString(R.string.ok));
                        Utils.showAlertDialog(params);
                    }
                }
            });

            return true;
        }else{
            AlertDialogParameters params = new AlertDialogParameters(mContext,R.string.mc_error_wrong_qr,trxErrorOnClick);
            params.setPositiveButtonText(mContext.getResources().getString(R.string.ok));
            Utils.showAlertDialog(params);
        }

        return false;
    }

    DialogInterface.OnClickListener trxErrorOnClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(barcodeScannerView != null) {
                barcodeScannerView.resume();
                blockedScanner = false;
            }
        }
    };


    DialogInterface.OnClickListener deleteTrxOnClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:

                    MobileCashUtils.deleteTransaction(mContext, transaction.getTrxReceiptId(), isTransactionForMe);

                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    if(barcodeScannerView != null) {
                        barcodeScannerView.resume();
                    }
                    break;
                default:
                    break;
            }
            dialog.dismiss();
        }
    };

    View.OnClickListener deleteOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if(barcodeScannerView != null) {
                barcodeScannerView.pause();
            }
            AlertDialogParameters params = new AlertDialogParameters(mContext,R.string.mc_deletetrx_message,deleteTrxOnClick);
            params.setPositiveButtonText(getResources().getString(R.string.ok));
            params.setNegativeButtonText(getResources().getString(R.string.mc_deletetrx_back).toUpperCase());
            Utils.showAlertDialog(params);
        }
    };



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(barcodeScannerView == null) {
            return super.onKeyDown(keyCode, event);
        }
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
