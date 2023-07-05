package com.popular.android.mibanco.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.animation.Stamp;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.model.BaseResponse;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.DialogCoverup;
import com.popular.android.mibanco.view.DialogHolo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Displays animated transaction's receipt.
 */
public class PaymentReceipt extends BaseSessionActivity {

    public static final String TRANSACTION_DELETED = "transaction_deleted";

    private static final class MediaScannerClient implements MediaScannerConnectionClient {

        private MediaScannerConnection connection;

        private final Context context;

        private final String mimeType;

        private final String path;

        public MediaScannerClient(final String path, final String mimeType, final Context context) {
            this.path = path;
            this.mimeType = mimeType;
            this.context = context;
        }

        @Override
        public void onMediaScannerConnected() {
            connection.scanFile(path, mimeType);
        }

        @Override
        public void onScanCompleted(final String path, final Uri uri) {
            connection.disconnect();
        }
    }

    /**
     * The Constant ALPHA_ANIMATION_DURATION_MILIS.
     */
    private static final int ALPHA_ANIMATION_DURATION_MILIS = 1000;

    /**
     * The Constant SCALE_ANIMATION_DURATION_MILIS.
     */
    private static final int SCALE_ANIMATION_DURATION_MILIS = 325;

    /**
     * The layout object containing receipt view.
     */
    private RelativeLayout receiptLayout;

    /**
     * Is receipt for a transfer?
     */
    private boolean transfers;

    private String favId;
    private String frontEndId;

    private String receiptFilePath;
    private Bitmap receiptBitmap;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt);

        transfers = getIntent().getBooleanExtra("transfers", false);
        boolean historyReceipt = getIntent().getBooleanExtra("historyReceipt", false);
        String errorMessage = getIntent().getStringExtra(MiBancoConstants.ERROR_MESSAGE_KEY);

        TextView textViewReferenceNumber = (TextView) findViewById(R.id.reference_nr);
        if (getIntent().getStringExtra("referenceNr") != null && textViewReferenceNumber != null) {
            textViewReferenceNumber.setText(getIntent().getStringExtra("referenceNr").trim().replaceAll("\n", ""));
        }

        TextView textViewFromName = ((TextView) findViewById(R.id.from_name));
        TextView textViewFrom= ((TextView) findViewById(R.id.from_nr));
        TextView textViewToName = ((TextView) findViewById(R.id.to_name));
        TextView textViewTo = ((TextView) findViewById(R.id.to_nr));
        TextView textViewAmount = ((TextView) findViewById(R.id.amount));
        TextView textViewDate = ((TextView) findViewById(R.id.date));
        Button buttonClose = (Button)findViewById(R.id.button_close);

        if(textViewFromName!= null && textViewFrom != null
                && textViewToName != null && textViewTo != null
                && textViewAmount != null && textViewDate != null
                && buttonClose != null) {

            textViewFromName.setText(getIntent().getStringExtra("fromName").trim().replaceAll("\n", ""));
            textViewFrom.setText(getIntent().getStringExtra("fromNr").trim().replaceAll("\n", ""));
            textViewToName.setText(getIntent().getStringExtra("toName").trim().replaceAll("\n", ""));
            textViewTo.setText(getIntent().getStringExtra("toNr").trim().replaceAll("\n", ""));
            textViewAmount.setText(getIntent().getStringExtra("amount").trim().replaceAll("\n", ""));
            textViewDate.setText(getIntent().getStringExtra("date").trim().replaceAll("\n", ""));
            buttonClose.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    onBackPressed();
                }
            });
        }

        if (historyReceipt) {
            findViewById(R.id.status_title).setVisibility(View.VISIBLE);
            TextView textStatus = (TextView) findViewById(R.id.status);
            String statusCode = getIntent().getStringExtra("statusCode");
            textStatus.setText(getIntent().getStringExtra("status").trim().replaceAll("\n", ""));
            if (statusCode.equalsIgnoreCase(MiBancoConstants.TRANSACTION_STATUS_CODE_OK)
                    || statusCode.equalsIgnoreCase(MiBancoConstants.TRANSACTION_STATUS_CODE_IA)) {
                textStatus.setTextColor(ContextCompat.getColor(this, R.color.transaction_status_ok));
            } else if (statusCode.equalsIgnoreCase(MiBancoConstants.TRANSACTION_STATUS_CODE_IN_PROCESS)) {
                textStatus.setTextColor(ContextCompat.getColor(this, R.color.transaction_status_processing));
            } else {
                textStatus.setTextColor(ContextCompat.getColor(this, R.color.transaction_status_error));
            }
            textStatus.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(getIntent().getStringExtra("frequency"))) {
                findViewById(R.id.frequency_title).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.frequency)).setText(getIntent().getStringExtra("frequency").trim().replaceAll("\n", ""));
                findViewById(R.id.frequency).setVisibility(View.VISIBLE);
            }
        }

        final TextView title = (TextView) findViewById(R.id.alertTitle);
        final ImageView stampImage = (ImageView) findViewById(R.id.stamp);
        final ImageView alert = (ImageView) findViewById(R.id.alert);

        if(title != null && stampImage != null && alert != null) {
            if (transfers) {
                if (historyReceipt) {
                    setTitle(R.string.processed_transfer);
                    title.setText(getString(R.string.processed_transfer).toUpperCase());
                    stampImage.setImageResource(R.drawable.stamp_transfered);
                } else if (errorMessage != null) {
                    setTitle(R.string.transfer_failed_title);
                    title.setText(getString(R.string.transfer_failed_title).toUpperCase());
                    title.setTextColor(ContextCompat.getColor(this, R.color.payments_receipt_error));
                    alert.setVisibility(View.VISIBLE);
                    stampImage.setVisibility(View.GONE);
                    findViewById(R.id.receipt_nr).setVisibility(View.GONE);
                    findViewById(R.id.reference_nr).setVisibility(View.GONE);
                    findViewById(R.id.error_title).setVisibility(View.VISIBLE);
                    TextView errorMessageTextView = (TextView) findViewById(R.id.error_message);
                    errorMessageTextView.setVisibility(View.VISIBLE);
                    errorMessageTextView.setText(errorMessage);
                    errorMessageTextView.setTextColor(ContextCompat.getColor(this, R.color.payments_receipt_error));
                } else {
                    setTitle(R.string.transfer_processed);
                    title.setText(getString(R.string.transfer_processed).toUpperCase());
                }
                if(application.getLanguage().equalsIgnoreCase(MiBancoConstants.ENGLISH_LANGUAGE_CODE))
                {
                    stampImage.setImageResource(R.drawable.stamp_transfered_en);
                }else
                {
                    stampImage.setImageResource(R.drawable.stamp_transfered);
                }
            } else {
                if (historyReceipt) {
                    setTitle(R.string.processed_payment);
                    title.setText(getString(R.string.processed_payment).toUpperCase());
                } else if (errorMessage != null) {
                    setTitle(R.string.payment_failed_title);
                    title.setText(getString(R.string.payment_failed_title).toUpperCase());
                    title.setTextColor(ContextCompat.getColor(this, R.color.payments_receipt_error));
                    alert.setVisibility(View.VISIBLE);
                    stampImage.setVisibility(View.GONE);
                    findViewById(R.id.receipt_nr).setVisibility(View.GONE);
                    findViewById(R.id.reference_nr).setVisibility(View.GONE);
                    findViewById(R.id.error_title).setVisibility(View.VISIBLE);
                    TextView errorMessageTextView = (TextView) findViewById(R.id.error_message);
                    errorMessageTextView.setVisibility(View.VISIBLE);
                    errorMessageTextView.setText(errorMessage);
                    errorMessageTextView.setTextColor(ContextCompat.getColor(this, R.color.payments_receipt_error));
                } else {
                    setTitle(R.string.payment_processed);
                    title.setText(getString(R.string.payment_processed).toUpperCase());
                }
                if(application.getLanguage().equalsIgnoreCase(MiBancoConstants.ENGLISH_LANGUAGE_CODE))
                {
                    stampImage.setImageResource(R.drawable.stamp_paid_en);
                }else
                {
                    stampImage.setImageResource(R.drawable.stamp_paid);
                }

            }

            if (!historyReceipt && errorMessage == null) {
                final ScaleAnimation stampAnimation = new Stamp(2.0f, 0.8f, 2.0f, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, this);
                stampAnimation.setAnimationListener(new AnimationListener() {

                    @Override
                    public void onAnimationEnd(final Animation arg0) {
                        final ScaleAnimation sa = new ScaleAnimation(0.8f, 1, 0.8f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        sa.setDuration(SCALE_ANIMATION_DURATION_MILIS);
                        stampImage.startAnimation(sa);
                    }

                    @Override
                    public void onAnimationRepeat(final Animation arg0) {
                    }

                    @Override
                    public void onAnimationStart(final Animation arg0) {
                    }
                });
                final AlphaAnimation aa = new AlphaAnimation(1.0f, 0.0f);
                aa.setDuration(ALPHA_ANIMATION_DURATION_MILIS);
                stampImage.startAnimation(aa);
                stampImage.startAnimation(stampAnimation);
            }
        }

        receiptLayout = (RelativeLayout) findViewById(R.id.receipt_view);
        receiptLayout.setDrawingCacheEnabled(true);
        receiptLayout.setDrawingCacheBackgroundColor(Color.WHITE);
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

    @Override
    public void onBackPressed() {
        final Intent intent = getIntent();
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, intent);
        } else {
            getParent().setResult(Activity.RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       if (receiptFilePath != null) {
            scanFile(receiptFilePath);
        }

        if (receiptBitmap != null) {
            receiptBitmap.recycle();
            receiptBitmap = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete_transaction:
                deleteTransaction();
                break;
            case R.id.menu_save_receipt:
                saveReceipt();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem item = menu.findItem(R.id.menu_save_receipt);
        item.setVisible(true);

        favId = getIntent().getStringExtra("favId");
        frontEndId = getIntent().getStringExtra("frontEndId");
        if (favId != null && frontEndId != null) {
            item = menu.findItem(R.id.menu_delete_transaction);
            item.setVisible(true);
        }

        return true;
    }

    private void deleteTransaction() {
        final DialogHolo dialog = new DialogHolo(PaymentReceipt.this);
        dialog.setMessage(R.string.delete_transaction_question);
        dialog.setNoTitleMode();
        dialog.setPositiveButton(getString(R.string.yes), new OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.dismissDialog(dialog);
                if (transfers) {
                    onClickOverTransfers(application);
                } else {
                    onClickOverPayment(application);
                }
            }
        });
        dialog.setNegativeButton(getString(R.string.no), new OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.dismissDialog(dialog);
            }
        });
        dialog.setCancelable(true);
        Utils.showDialog(dialog, PaymentReceipt.this);

    }

    private void onClickOverTransfers(final App application) {
        BPAnalytics.logEvent(BPAnalytics.EVENT_TRANSFER_DELETED);
        application.setReloadTransfers(true);
        App.getApplicationInstance().getAsyncTasksManager().deleteInProcessTransfer(PaymentReceipt.this, new ResponderListener() {

            @Override
            public void sessionHasExpired() {
                application.reLogin(PaymentReceipt.this);
            }

            @Override
            public void responder(String responderName, Object data) {
                if (data instanceof BaseResponse) {
                    showStatusMessage((BaseResponse) data);
                } else {
                    throw new IllegalArgumentException("BaseResponse Expected: " + data.getClass().getName());
                }
            }
        }, true, favId, frontEndId);
    }

    private void onClickOverPayment(final App application) {
        BPAnalytics.logEvent(BPAnalytics.EVENT_PAYMENT_DELETED);
        application.setReloadPayments(true);
        App.getApplicationInstance().getAsyncTasksManager().deleteInProcessPayment(PaymentReceipt.this, new ResponderListener() {

            @Override
            public void sessionHasExpired() {
                application.reLogin(PaymentReceipt.this);
            }

            @Override
            public void responder(String responderName, Object data) {

                if (data instanceof BaseResponse) {
                    showStatusMessage((BaseResponse) data);
                } else {
                    throw new IllegalArgumentException("BaseResponse Expected: " + data.getClass().getName());
                }
            }
        }, true, favId, frontEndId);
        //MBIM-356
        application.getAsyncTasksManager().updateBalances(PaymentReceipt.this);
    }

    //MBIM-356 INCIDENT 3815327
    private void updateReceipts(final BaseResponse response){
        if(application != null && application.getAsyncTasksManager() != null) {
            // cover up the Activity till portal update is finished
            if (application.isUpdatingBalances()) {
                Utils.dismissDialog(application.getDialogCoverupUpdateBalances());
                application.setDialogCoverupUpdateBalances(new DialogCoverup(this));
                application.getDialogCoverupUpdateBalances().setProgressCaption(R.string.refreshing_balances);
                Utils.showDialog(application.getDialogCoverupUpdateBalances(), this);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        application.setPortalDelayInEffect(false);
                        application.setUpdatingBalances(false);
                        goBackToHistory(response);
                    }
                }, 7000);

            } else {
                goBackToHistory(response);
            }

            invalidateOptionsMenu();

        }
    }
    //ENDS MBIM-356
    private void showStatusMessage(final BaseResponse response) {

        if (!TextUtils.isEmpty(response.getStatusMessage())) {
            final DialogHolo dialog = new DialogHolo(PaymentReceipt.this);
            dialog.setNoTitleMode();
            dialog.setMessage(response.getStatusMessage());
            dialog.setConfirmationButton(getString(R.string.ok), new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    Utils.dismissDialog(dialog);
                    updateReceipts(response);
                }
            });
           Utils.showDialog(dialog, PaymentReceipt.this);
        } else {
            goBackToHistory(response);
        }
    }

    private void goBackToHistory(BaseResponse response) {
        if (transfers) {
            Intent intent = new Intent(PaymentReceipt.this, Receipts.class);
            intent.putExtra(Receipts.TRANSFER_HISTORY, response);
            intent.putExtra(Receipts.RECEIPT_TYPE, Receipts.HistoryType.TRANSFER);
            intent.putExtra(TRANSACTION_DELETED, true);
            startActivity(intent);
        } else {
            Intent intent = new Intent(PaymentReceipt.this, Receipts.class);
            intent.putExtra(Receipts.PAYMENT_HISTORY, response);
            intent.putExtra(Receipts.RECEIPT_TYPE, Receipts.HistoryType.PAYMENT);
            intent.putExtra(TRANSACTION_DELETED, true);
            startActivity(intent);
        }
        Utils.dismissDialog(application.getDialogCoverupUpdateBalances());
    }

    /**
     * Saves the displayed receipt to a file in the gallery.
     */
    private void saveReceipt() {
        final DialogHolo dialog = new DialogHolo(PaymentReceipt.this);
        dialog.setNoContentMode();
        dialog.setTitle(R.string.save_to_gallery);
        dialog.setPositiveButton(getString(R.string.yes), new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                try {
                    String dateFormat = ((App) getApplication()).getDateFormat();
                    if (dateFormat == null) {
                        dateFormat = MiBancoConstants.WEBSERVICE_DATE_FORMAT;
                    }
                    String date = new SimpleDateFormat(dateFormat).format(new Date());
                    date = date.replace("/", "_");
                    date = date.replace("-", "_");
                    date = date.replace(" ", "_");
                    date += new SimpleDateFormat("_HH_mm_ss").format(new Date());
                    receiptFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + File.separator + "popular_" + getString(R.string.receipt) + "_" + date
                            + ".png";

                    receiptBitmap = receiptLayout.getDrawingCache();
                    App.getApplicationInstance().getAsyncTasksManager().new SaveImageTask().execute(receiptBitmap, receiptFilePath);
                } catch (final Exception e) {
                    Log.w("TransferHistoryEntry", e);
                }
                Utils.dismissDialog(dialog);
            }
        });
        dialog.setNegativeButton(getString(R.string.no), new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                Utils.dismissDialog(dialog);
            }
        });
        dialog.setCancelable(true);
        Utils.showDialog(dialog, PaymentReceipt.this);
    }

    /**
     * Forces gallery to rescan its content.
     *
     * @param path path to a new file
     */
    private void scanFile(final String path) {
        final MediaScannerClient client = new MediaScannerClient(path, null, PaymentReceipt.this);
        MediaScannerConnection connection = new MediaScannerConnection(this, client);
        client.connection = connection;
        connection.connect();
    }
}
