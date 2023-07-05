package com.popular.android.mibanco.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.animation.Stamp;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.util.MediaScannerClient;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.DialogHolo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Displays animated deposit check receipt.
 */
public class DepositCheckReceipt extends BaseSessionActivity {

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

    private String receiptFilePath;
    private Bitmap receiptBitmap;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deposit_check_receipt);

        if(App.getApplicationInstance() != null && App.getApplicationInstance().getApiClient() != null) {

            String errorMessage = getIntent().getStringExtra(MiBancoConstants.ERROR_MESSAGE_KEY);
            TextView alertTitle = (TextView) findViewById(R.id.alertTitle);
            alertTitle.setText(R.string.deposit_check_deposit_sent);

            ((TextView) findViewById(R.id.reference_nr)).setText(getIntent().getStringExtra("referenceNr").trim().replaceAll("\n", ""));
            ((TextView) findViewById(R.id.to_name)).setText(getIntent().getStringExtra("toName").trim().replaceAll("\n", ""));
            ((TextView) findViewById(R.id.to_nr)).setText(getIntent().getStringExtra("toNr").trim().replaceAll("\n", ""));
            ((TextView) findViewById(R.id.amount)).setText(getIntent().getStringExtra("amount").trim().replaceAll("\n", ""));
            ((TextView) findViewById(R.id.date)).setText(getIntent().getStringExtra("date").trim().replaceAll("\n", ""));
            findViewById(R.id.button_close).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    onBackPressed();
                }
            });

            final ImageView stampImage = (ImageView) findViewById(R.id.stamp);
            if (application.getLanguage().equalsIgnoreCase(MiBancoConstants.ENGLISH_LANGUAGE_CODE)) {
                stampImage.setImageResource(R.drawable.stamp_received_en);
            }

            final ImageView alert = (ImageView) findViewById(R.id.alert);

            if (errorMessage != null) {
                setTitle(R.string.deposit_check_error);
                alertTitle.setText(R.string.deposit_check_error);
                alertTitle.setTextColor(ContextCompat.getColor(this, R.color.payments_receipt_error));
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

            receiptLayout = (RelativeLayout) findViewById(R.id.receipt_view);
            receiptLayout.setDrawingCacheEnabled(true);
            receiptLayout.setDrawingCacheBackgroundColor(Color.WHITE);
        }
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

        return true;
    }

    /**
     * function: saveReceipt
     * <p/>
     * Saves an image of the receipt to the device.
     */
    private void saveReceipt() {
        final DialogHolo dialog = new DialogHolo(DepositCheckReceipt.this);
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
                    Log.w("DepositCheckReceipt", e);
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
        Utils.showDialog(dialog, DepositCheckReceipt.this);
    }

    /**
     * Forces gallery to rescan its content.
     *
     * @param path path to a new file
     */
    private void scanFile(final String path) {
        final MediaScannerClient client = new MediaScannerClient(path, null, DepositCheckReceipt.this);
        MediaScannerConnection connection = new MediaScannerConnection(this, client);
        client.setConnection(connection);
        connection.connect();
    }
}
