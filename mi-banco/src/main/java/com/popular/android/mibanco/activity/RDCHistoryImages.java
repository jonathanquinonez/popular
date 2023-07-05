package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.RDCCheckItem;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Review Checks Activity class.
 */
public class RDCHistoryImages extends BaseSessionActivity {

    /**
     * The string constants
     */
    public static final String IMAGE_PATH = "imagePath";
    public static final String REFERENCE_NUMBER = "referenceNumber";
    public static final String FRONT_END_ID = "frontendid";
    public static final String TARGET_NICKNAME = "targetNickname";
    public static final String TARGET_ACCOUNT_LAST_4_NUM = "targetAccountLast4Num";
    public static final String AMOUNT = "amount";
    public static final String SUBMITTED_DATE = "submittedDate";
    public static final String STATUS = "status";
    public static final String FRONT_CHECK_URI = "frontCheckUri";
    public static final String BACK_CHECK_URI = "backCheckUri";
    public static final String FRONT_CHECK_FILENAME = "rdcFrontCheck";
    public static final String BACK_CHECK_FILENAME = "rdcBackCheck";

    /**
     * The reference number to obtain
     */
    private String referenceNumber;

    /**
     * The account front end id
     */
    private String frontendid;

    /**
     * The account nickname
     */
    private String accountNickname;

    /**
     * The account las 4 number
     */
    private String accountLast4Num;

    /**
     * The check amount
     */
    private String amount;

    /**
     * The check submitted date
     */
    private String submittedDate;

    /**
     * The check deposit status
     */
    private String status;

    /**
     * The account deposited to
     */
    private CustomerAccount selectedAccount;

    /**
     * The check images
     */
    private ImageView frontCheckImage;
    private ImageView backCheckImage;

    /**
     * The check images Uri's
     */
    private Uri frontCheckUri;
    private Uri backCheckUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rdc_history_check_images);

        boolean useExistingData = false;

        if (savedInstanceState != null) {
            frontendid = savedInstanceState.getString(FRONT_END_ID);
            accountNickname = savedInstanceState.getString(TARGET_NICKNAME);
            accountLast4Num = savedInstanceState.getString(TARGET_ACCOUNT_LAST_4_NUM);
            amount = savedInstanceState.getString(AMOUNT);
            submittedDate = savedInstanceState.getString(SUBMITTED_DATE);
            status = savedInstanceState.getString(STATUS);
            frontCheckUri = Uri.parse(savedInstanceState.getString(FRONT_CHECK_URI));
            backCheckUri = Uri.parse(savedInstanceState.getString(BACK_CHECK_URI));

            if (frontCheckUri != null && backCheckUri != null && frontendid != null)
                useExistingData = true;
        } else {

            final Intent intent = getIntent();

            if (intent.getStringExtra(REFERENCE_NUMBER) != null) {
                referenceNumber = intent.getStringExtra(REFERENCE_NUMBER);
            }
            if (intent.getStringExtra(FRONT_END_ID) != null) {
                frontendid = intent.getStringExtra(FRONT_END_ID);
            }
            if (intent.getStringExtra(TARGET_NICKNAME) != null) {
                accountNickname = intent.getStringExtra(TARGET_NICKNAME);
            }
            if (intent.getStringExtra(TARGET_ACCOUNT_LAST_4_NUM) != null) {
                accountLast4Num = intent.getStringExtra(TARGET_ACCOUNT_LAST_4_NUM);
            }
            if (intent.getStringExtra(AMOUNT) != null) {
                amount = intent.getStringExtra(AMOUNT);
            }
            if (intent.getStringExtra(SUBMITTED_DATE) != null) {
                submittedDate = intent.getStringExtra(SUBMITTED_DATE);
            }
            if (intent.getStringExtra(STATUS) != null) {
                status = intent.getStringExtra(STATUS);
            }
        }

        // Set the check images
        frontCheckImage = (ImageView) findViewById(R.id.front_check_image);
        backCheckImage = (ImageView) findViewById(R.id.back_check_image);

        fetchAndSetReviewCheck(useExistingData);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(FRONT_END_ID, frontendid);
        outState.putString(TARGET_NICKNAME, accountNickname);
        outState.putString(TARGET_ACCOUNT_LAST_4_NUM, accountLast4Num);
        outState.putString(AMOUNT, amount);
        outState.putString(SUBMITTED_DATE, submittedDate);
        outState.putString(STATUS, status);
        outState.putString(FRONT_CHECK_URI, frontCheckUri.toString());
        outState.putString(BACK_CHECK_URI, backCheckUri.toString());
        super.onSaveInstanceState(outState);
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
     * function: fetchAndSetReviewCheck
     * <p/>
     * Get the information for this check (images and all).
     *
     * @param: boolean useExistingData TRUE: The information was already looked up and should be loaded from the bundle. FALSE: The information has
     * not been looked up and should be loaded from the JSON.
     */
    private void fetchAndSetReviewCheck(boolean useExistingData) {

        if (useExistingData)
            loadReviewCheck(null);
        else {
            App.getApplicationInstance().getAsyncTasksManager().FetchRDCHistoryImagesTask(RDCHistoryImages.this, referenceNumber, new ResponderListener() {

                @Override
                public void sessionHasExpired() {
                    application.reLogin(RDCHistoryImages.this);
                }

                @Override
                public void responder(String responderName, Object data) {
                    loadReviewCheck(application.getReviewCheck());
                }
            });
        }
    }

    /**
     * function: loadReviewCheck
     * <p/>
     * Display the information for the check.
     *
     * @param: RDCCheckItem reviewCheck The check item to display
     */
    private void loadReviewCheck(RDCCheckItem reviewCheck) {
        String depositStatus = "";
        if (status.equalsIgnoreCase("submitted"))
            depositStatus = getString(R.string.rdc_history_deposit_status_submitted);
        else if (status.equalsIgnoreCase("approved"))
            depositStatus = getString(R.string.rdc_history_deposit_status_approved);
        else if (status.equalsIgnoreCase("declined"))
            depositStatus = getString(R.string.rdc_history_deposit_status_declined);

        ((TextView) findViewById(R.id.rdc_history_check_item_amount)).setText(amount);
        ((TextView) findViewById(R.id.rdc_history_check_item_date)).setText(submittedDate);
        ((TextView) findViewById(R.id.rdc_history_reference_num)).setText(referenceNumber);
        ((TextView) findViewById(R.id.rdc_history_deposit_status)).setText(depositStatus);
        ((TextView) findViewById(R.id.item_name)).setText(accountNickname);
        ((TextView) findViewById(R.id.item_comment)).setText(accountLast4Num);

        // Get all the possible accounts
        ArrayList<CustomerAccount> rdcAccounts = new ArrayList<CustomerAccount>();
        rdcAccounts.addAll(application.getLoggedInUser().getRDCAccounts());

        for (CustomerAccount rdcAccount : rdcAccounts) {
            if (rdcAccount.getFrontEndId().equals(frontendid)) {
                selectedAccount = App.getApplicationInstance().getCustomerAccountsMap().get(rdcAccount.getApiAccountKey() + rdcAccount.getAccountNumberSuffix());
                showSelectedAccount();
                break;
            }
        }

        // Store check images locally
        if (reviewCheck != null && reviewCheck.getFrontImage() != null && reviewCheck.getBackImage() != null) {
            frontCheckUri = saveCheckImage(reviewCheck.getFrontImage(), FRONT_CHECK_FILENAME);
            backCheckUri = saveCheckImage(reviewCheck.getBackImage(), BACK_CHECK_FILENAME);
        }

        // Set check ImageViews
        if (frontCheckUri != null && backCheckUri != null) {
            frontCheckImage.setImageURI(frontCheckUri);
            frontCheckImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RDCHistoryImages.this, RDCHistoryImageZoom.class);
                    intent.putExtra(IMAGE_PATH, frontCheckUri.toString());
                    startActivity(intent);
                }
            });

            backCheckImage.setImageURI(backCheckUri);
            backCheckImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RDCHistoryImages.this, RDCHistoryImageZoom.class);
                    intent.putExtra(IMAGE_PATH, backCheckUri.toString());
                    startActivity(intent);
                }
            });
        } else {
            frontCheckImage.setVisibility(View.GONE);
            backCheckImage.setVisibility(View.GONE);
            findViewById(R.id.front_check_text).setVisibility(View.GONE);
            findViewById(R.id.back_check_text).setVisibility(View.GONE);
            findViewById(R.id.rdc_history_check_images_title).setVisibility(View.GONE);
        }

        // Display the images
        findViewById(R.id.rdc_history_ll_front).setVisibility(View.VISIBLE);
        findViewById(R.id.rdc_history_ll_back).setVisibility(View.VISIBLE);
        findViewById(R.id.rootView).setVisibility(View.VISIBLE);
    }

    /**
     * function: showSelectedAccount
     * <p/>
     * Looks up the account selected in the dialog and display it, along with its picture.
     */
    private void showSelectedAccount() {

        final ImageView cardImageView = (ImageView) findViewById(R.id.item_image);
        final String path = Utils.getAccountImagePath(selectedAccount, getApplicationContext());
        if (path == null) {
            cardImageView.setImageResource(selectedAccount.getImgResource());
        } else {
            Bitmap accountImage = BitmapFactory.decodeFile(path);
            if (accountImage != null) {
                cardImageView.setImageBitmap(accountImage);
            } else {
                cardImageView.setImageResource(selectedAccount.getImgResource());
            }
        }
    }

    private Uri saveCheckImage(String imageString, String imageName) {
        Uri imageUri = null;

        final byte[] frontImage = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(frontImage, 0, frontImage.length);

        final File frontCheckFile = new File(getFilesDir(), imageName);

        try {
            FileOutputStream fos = new FileOutputStream(frontCheckFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();
            imageUri = Uri.fromFile(frontCheckFile);
        } catch (Exception e) {
            Log.e("RDC Check Image", e.getMessage(), e);
        }

        return imageUri;
    }
}
