package com.popular.android.mibanco.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.adapter.DepositCheckAccountsAdapter;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.listener.SimpleListener;
import com.popular.android.mibanco.model.Customer;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.DepositCheckEnrollment;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.DFMUtils;
import com.popular.android.mibanco.util.PermissionsManagerUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.DialogHolo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Deposit Check Activity class.
 */
public class DepositCheck extends BaseSessionActivity implements OnClickListener, OnItemClickListener {

    public static final int AMOUNT_PICKER_REQUEST_CODE = 1;
    public static final int CAMERA_REQUEST_CODE = 2;
    public static final int VERIFY_DEPOSIT_CODE = 3;
    public static final int VERIFY_DEPOSIT_ERROR_CODE = 4;

    public static final int NO_PHOTOS_DISPLAYING = 0;
    public static final int FRONT_PHOTO_DISPLAYING = 1;
    public static final int FRONT_AND_BACK_PHOTOS_DISPLAYING = 2;

    private static final String TAKE_FRONT_PICTURE = "take_front_picture";

    private int currentState = NO_PHOTOS_DISPLAYING;
    private int amount = 0;

    private TextView amountTextView;
    private TextView btnSelectAccount;

    private Button btnSendDeposit;
    private Button btnTakeFrontPicture;
    private Button btnTakeBackPicture;

    private LinearLayout layoutSelectAccount;
    private RelativeLayout depositCheckAccountLayout;
    private WebView popularWebView;
    private ImageView frontCheckImage;

    private DialogHolo dialog;
    private CustomerAccount selectedAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(application != null && application.getAsyncTasksManager() != null) {
            // Enroll the user in RDC if he has not been enrolled yet...
            if (!application.getRdcClientEnrolled()) {
                App.getApplicationInstance().getAsyncTasksManager().enrollInRDC(DepositCheck.this, new ResponderListener() {

                    @Override
                    public void sessionHasExpired() {
                        application.reLogin(DepositCheck.this);
                    }

                    @Override
                    public void responder(String responderName, Object data) {

                        DepositCheckEnrollment depositCheckEnrollment = (DepositCheckEnrollment) data;

                        // If we were not able to enroll, show the maintenance window
                        if (!depositCheckEnrollment.getStatus().equals("SUCCESS")) {
                            final Intent iDowntime = new Intent(getBaseContext(), Downtime.class);
                            iDowntime.putExtra("downtimeMessage", getResources().getString(R.string.maintenance_rdc));
                            startActivity(iDowntime);
                            finish();
                        } else {
                            application.setRdcClientEnrolled(true);
                        }
                    }
                });
            }

            setContentView(R.layout.deposit_check);

            amountTextView = (TextView) findViewById(R.id.amount_text);
            btnTakeFrontPicture = (Button) findViewById(R.id.deposit_check_camera);
            btnSelectAccount = (TextView) findViewById(R.id.deposit_check_account_button);
            btnSendDeposit = (Button) findViewById(R.id.deposit_check_send_deposit_button);
            btnTakeBackPicture = (Button) findViewById(R.id.deposit_check_back_side_camera);
            layoutSelectAccount = (LinearLayout) findViewById(R.id.deposit_check_account_display);
            depositCheckAccountLayout = (RelativeLayout) findViewById(R.id.deposit_check_account_rl);
            popularWebView = (WebView) findViewById(R.id.deposit_check_popular_text);
            frontCheckImage = (ImageView) findViewById(R.id.front_check_image);

            LinearLayout amountButton = (LinearLayout) findViewById(R.id.amount_button);

            popularWebView.getSettings().setJavaScriptEnabled(true);

            if (amountTextView != null && btnTakeFrontPicture != null
                    && btnSelectAccount != null && btnSendDeposit != null
                    && btnTakeBackPicture != null && layoutSelectAccount != null
                    && amountButton != null && popularWebView != null) {

                btnSelectAccount.setOnClickListener(this);
                layoutSelectAccount.setOnClickListener(this);
                btnSendDeposit.setOnClickListener(this);
                btnTakeFrontPicture.setOnClickListener(this);
                btnTakeBackPicture.setOnClickListener(this);

                // Handle the amount button presses
                amountButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final Intent intent = new Intent(DepositCheck.this, EnterAmount.class);
                        intent.putExtra("amount", amount);
                        intent.putExtra("hideRemember", true);
                        startActivityForResult(intent, AMOUNT_PICKER_REQUEST_CODE);
                    }
                });

                // Load Popular's RDC instructions (HTML)
                String requestedUrl = "";
                if (DFMUtils.isLimitsPerSegmentEnabled()){
                    requestedUrl = getRdcInstructionsUrl(application.getLoggedInUser());
                    if(!requestedUrl.contains("javascript") && Utils.isValidUrl(requestedUrl, getApplicationContext())) {
                        popularWebView.loadUrl(requestedUrl);
                    }else{
                        popularWebView.goBack();
                    }
                    //popularWebView.loadUrl(getRdcInstructionsUrl(application.getLoggedInUser()));
                } else {
                    requestedUrl = Utils.getLocaleStringResource(new Locale(application.getLanguage()), R.string.remote_deposit_instructions_url, getBaseContext());
                    if(!requestedUrl.contains("javascript") && Utils.isValidUrl(requestedUrl, getApplicationContext())) {
                        popularWebView.loadUrl(requestedUrl);
                    }else{
                        popularWebView.goBack();
                    }
                    //popularWebView.loadUrl(getString(R.string.remote_deposit_instructions_url));
                }
            }

            if (application != null && application.getDepositCheckInformationFromSession()) {
                currentState = application.getDepositCheckCurrentState();
                if (currentState == FRONT_PHOTO_DISPLAYING && application.getDepositCheckFrontImage() == null) {
                    resetLayout();
                } else if (currentState == FRONT_AND_BACK_PHOTOS_DISPLAYING && (application.getDepositCheckFrontImage() == null || application.getDepositCheckBackImage() == null)) {
                    resetLayout();
                } else {
                    amount = application.getDepositCheckAmount();
                    setAmount(amount);

                    // If we have an account, look it up
                    if (application.getDepositCheckSelectedAccount() != null) {
                        selectedAccount = application.getDepositCheckSelectedAccount();
                        showSelectedAccount();
                    }

                    // Set up the current state
                    if (currentState == FRONT_PHOTO_DISPLAYING)
                        setupBackPicture();
                    else if (currentState == FRONT_AND_BACK_PHOTOS_DISPLAYING) {
                        setupBackPicture();
                        setupSendDeposit();
                    }
                }
            }
        }
    }

    // If we are destroyed and there is information pending, save it to session
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(application != null) {
            if (application.getLoggedInUser() != null && (currentState != NO_PHOTOS_DISPLAYING || amount > 0 || selectedAccount != null)) {
                application.setDepositCheckAmount(amount);
                application.setDepositCheckCurrentState(currentState);
                application.setDepositCheckSelectedAccount(selectedAccount);
                application.setDepositCheckInformationFromSession(true);
            } else {
                application.setDepositCheckInformationFromSession(false);
            }
        }
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
    public void onClick(View v) {
        if (v == btnSelectAccount || v == layoutSelectAccount) {
            showSelectAccountDialog();
        } else if (v == btnSendDeposit) {
            showConfirmDepositDialog();

        } else if(v == btnTakeFrontPicture){
            showCamera(true);

        } else if (v == btnTakeBackPicture) {
            showCamera(false);

        } else {
            super.onClick(v);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MiBancoConstants.REQUEST_CODE_ASK_PERMISSIONS:
                boolean permissionsAccepted = true;
                if(grantResults.length >0){
                    for(int res: grantResults){
                        if(res != PackageManager.PERMISSION_GRANTED){
                            permissionsAccepted = false;
                            break;
                        }
                    }
                }
                if (permissionsAccepted) {
                    if(btnTakeFrontPicture.getVisibility() == View.VISIBLE){
                        showCamera(true);
                    }else if(btnTakeBackPicture.getVisibility() == View.VISIBLE){
                        showCamera(false);
                    }

                } else {
                    PermissionsManagerUtils.displayRequiredPermissionsDialog(this, R.string.permission_rdc_mandatory,
                            Utils.openPermissionSettings(this));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

        Object selectedItem = adapter.getItemAtPosition(position);

        if (selectedItem != null) {

            // An account is selected - set is as the target account
            if (selectedItem instanceof CustomerAccount) {
                selectedAccount = (CustomerAccount) selectedItem;
                Utils.dismissDialog(dialog);
                showSelectedAccount();
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AMOUNT_PICKER_REQUEST_CODE:
                    setAmount(data.getIntExtra("amount", 0));
                    break;
                case CAMERA_REQUEST_CODE:
                    if (data.getBooleanExtra(TAKE_FRONT_PICTURE, true)) {
                        if (currentState != FRONT_AND_BACK_PHOTOS_DISPLAYING) {
                            currentState = FRONT_PHOTO_DISPLAYING;
                            setupBackPicture();
                        } else {
                            swapFrontPicture();
                        }
                    } else {
                        currentState = FRONT_AND_BACK_PHOTOS_DISPLAYING;
                        setupSendDeposit();
                    }
                    break;
                case VERIFY_DEPOSIT_CODE:
                    resetLayout();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.easy_deposit_history).setVisible(true);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.easy_deposit_history:
                Intent easyDepositHistory = new Intent(this, RDCHistory.class);
                startActivityForResult(easyDepositHistory, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * function: showSelectAccountDialog
     * <p/>
     * Shows the selected account dialog and allows the user to select the account to deposit to.
     */
    private void showSelectAccountDialog() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        Utils.setupLanguage(this);
        dialog = new DialogHolo(DepositCheck.this);
        View layout = dialog.setCustomContentView(R.layout.single_list_view);
        ListView listView = (ListView) layout.findViewById(R.id.list_view_elements);
        listView.setOnItemClickListener(this);

        // Load the deposit accounts
        ArrayList<CustomerAccount> accounts = new ArrayList<>();
        accounts.addAll(application.getLoggedInUser().getRDCAccounts());

        listView.setAdapter(new DepositCheckAccountsAdapter(DepositCheck.this, accounts, selectedAccount));

        dialog.setTitle(R.string.select_account);
        dialog.setConfirmationButton(getString(R.string.cancel), new OnClickListener() {

            @Override
            public void onClick(final View v) {
                Utils.dismissDialog(dialog);
            }
        });

        dialog.setCancelable(true);
        Utils.showDialog(dialog, DepositCheck.this);
    }

    /**
     * function: showConfirmDepositDialog
     * <p/>
     * Shows the confirm deposit dialog to confirm/cancel the deposit.
     */
    private void showConfirmDepositDialog() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        Utils.setupLanguage(this);
        dialog = new DialogHolo(DepositCheck.this);
        dialog.setTitle(R.string.deposit_check_confirm_deposit);
        final View dialogView = dialog.setCustomContentView(R.layout.dialog_deposit_check);

        dialog.setCancelable(true);
        dialog.setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                Utils.dismissDialog(dialog);
            }
        });

        dialog.setPositiveButton(getString(R.string.deposit_check_deposit_now), new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Utils.dismissDialog(dialog);

                // Send the deposit asynchronously...
                App.getApplicationInstance().getAsyncTasksManager().depositCheck(DepositCheck.this, selectedAccount.getFrontEndId(), Utils.formatAmountForWs(amount), application.getDepositCheckFrontImage(), application.getDepositCheckBackImage(), new ResponderListener() {

                    @Override
                    public void sessionHasExpired() {
                        if(application != null) {
                            application.reLogin(DepositCheck.this);
                        }
                    }

                    @Override
                    public void responder(String responderName, Object data) {
                        if (application.getDepositCheckReceipt() != null) {

                            final SimpleDateFormat sdfLocal = new SimpleDateFormat(application.getDateFormat(), Locale.US);
                            String dateLocal = sdfLocal.format(new Date());

                            // Show the confirmation page
                            final Intent intent = new Intent(DepositCheck.this, DepositCheckReceipt.class);
                            intent.putExtra("referenceNr", application.getDepositCheckReceipt().getDepositId());
                            intent.putExtra("toName", selectedAccount.getNickname());
                            intent.putExtra("toNr", selectedAccount.getAccountLast4Num());
                            intent.putExtra("amount", Utils.formatAmount(amount));
                            intent.putExtra("date", dateLocal);

                            if (application.getDepositCheckReceipt().getStatus().equals("SUCCESS")
                                    && application.getDepositCheckReceipt().getDepositId() != null
                                    && !"".equals(application.getDepositCheckReceipt().getDepositId())) {

                                BPAnalytics.logEvent(BPAnalytics.EVENT_REMOTE_DEPOSIT_SUCCESSFUL);
                                startActivityForResult(intent, VERIFY_DEPOSIT_CODE);
                            } else {

                                // Show the error receipt box
                                if (application.getDepositCheckReceipt().getError() != null || !"".equals(application.getDepositCheckReceipt().getError()))
                                    intent.putExtra(MiBancoConstants.ERROR_MESSAGE_KEY, application.getDepositCheckReceipt().getError());
                                else
                                    intent.putExtra(MiBancoConstants.ERROR_MESSAGE_KEY, getString(R.string.deposit_check_failed_message));

                                BPAnalytics.logEvent(BPAnalytics.EVENT_REMOTE_DEPOSIT_FAILED);
                                startActivityForResult(intent, VERIFY_DEPOSIT_ERROR_CODE);
                            }
                        } else {

                            // Show the error dialog box
                            onDepositCheckError(getString(R.string.deposit_check_failed_message), null);
                        }
                    }
                });
            }
        });

        // Set dialog's text boxes
        ((TextView) dialogView.findViewById(R.id.pay_amount)).setText(Utils.formatAmount(amount));

        Factory factory = Spannable.Factory.getInstance();
        Spannable toAccountNickname = factory.newSpannable(selectedAccount.getNickname() + " ");
        Spannable toAccountCode = factory.newSpannable(selectedAccount.getAccountLast4Num());
        toAccountNickname.setSpan(new StyleSpan(Typeface.BOLD), 0, toAccountNickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        toAccountNickname.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)), 0, toAccountNickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        toAccountCode.setSpan(new StyleSpan(Typeface.NORMAL), 0, toAccountCode.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        toAccountCode.setSpan(new ForegroundColorSpan((ContextCompat.getColor(this, R.color.grey_dark))), 0, toAccountCode.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannedString toAccountTitle = (SpannedString) TextUtils.concat(toAccountNickname, toAccountCode);
        ((TextView) dialogView.findViewById(R.id.to_nickname)).setText(toAccountTitle);

        Utils.showDialog(dialog, DepositCheck.this);
    }

    /**
     * function: showSelectedAccount
     * <p/>
     * Looks up the account selected in the dialog and display it, along with its picture.
     */
    private void showSelectedAccount() {

        final TextView accountNameView = (TextView) findViewById(R.id.item_name);
        final TextView accountLast4DigitsView = (TextView) findViewById(R.id.item_comment);
        final ImageView cardImageView = (ImageView) findViewById(R.id.item_image);

        if(depositCheckAccountLayout != null && accountNameView != null
                && accountLast4DigitsView != null && cardImageView != null){

            depositCheckAccountLayout.setVisibility(View.GONE);
            accountNameView.setText(selectedAccount.getNickname());
            accountLast4DigitsView.setText(selectedAccount.getAccountLast4Num());

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
        layoutSelectAccount.setVisibility(View.VISIBLE);
        if (btnSendDeposit.getVisibility() == View.VISIBLE)
            btnSendDeposit.setEnabled(allValuesEntered());
    }

    /**
     * function: setupBackPicture
     * <p/>
     * Prepares the layout for displaying the front picture and allowing the user to take the back picture.
     */
    private void setupBackPicture() {

        // Display the front image
        Bitmap bm = BitmapFactory.decodeByteArray(application.getDepositCheckFrontImage(), 0, application.getDepositCheckFrontImage().length);


        // Show and hide the proper controls
        TextView depositCheckInfoTitle = (TextView) findViewById(R.id.deposit_check_info_title);
        LinearLayout depositCheckInfoLine = (LinearLayout) findViewById(R.id.deposit_check_info_line);
        LinearLayout depositCheckPopularBorder = (LinearLayout) findViewById(R.id.deposit_check_popular_text_border);
        RelativeLayout takePictureRelativeLayout = (RelativeLayout)findViewById(R.id.take_picture_rl_step2);

        btnTakeFrontPicture.setVisibility(View.GONE);
        btnTakeBackPicture.setVisibility(View.VISIBLE);
        popularWebView.setVisibility(View.GONE);

        if(frontCheckImage != null && takePictureRelativeLayout!= null && depositCheckInfoTitle!= null
                && depositCheckInfoLine != null && depositCheckPopularBorder != null){

            frontCheckImage.setImageBitmap(bm);
            takePictureRelativeLayout.setVisibility(View.VISIBLE);
            depositCheckInfoTitle.setVisibility(View.VISIBLE);
            depositCheckInfoLine.setVisibility(View.VISIBLE);
            depositCheckPopularBorder.setVisibility(View.GONE);

            // Set up the click listener in case the picture is clicked
            frontCheckImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showCamera(true);
                }
            });
        }

    }

    /**
     * function: swapFrontPicture
     * <p/>
     * Swaps the current front picture with the new one just taken.
     */
    private void swapFrontPicture() {

        Bitmap bm = BitmapFactory.decodeByteArray(application.getDepositCheckFrontImage(), 0, application.getDepositCheckFrontImage().length);

        if(frontCheckImage != null) {
            frontCheckImage.setMinimumHeight(bm.getHeight());
            frontCheckImage.setMinimumWidth(bm.getWidth());
            frontCheckImage.setImageBitmap(bm);
        }
    }

    /**
     * function: setupSendDeposit
     * <p/>
     * Prepares the layout for sending the deposit since both pictures have been taken.
     */
    private void setupSendDeposit() {

        // Display the back image
        ImageView backCheckImage = (ImageView) findViewById(R.id.back_check_image);
        Bitmap bm = BitmapFactory.decodeByteArray(application.getDepositCheckBackImage(), 0, application.getDepositCheckBackImage().length);

        // Show and hide the proper controls
        LinearLayout takePictureLinearLayout = (LinearLayout) findViewById(R.id.take_picture_rl_step_2_ll_2);
        LinearLayout takePictureLinearLayout2 = (LinearLayout) findViewById(R.id.take_picture_rl_step_2_ll_3);

        if(backCheckImage != null && takePictureLinearLayout != null && takePictureLinearLayout2 != null){

            backCheckImage.setMinimumHeight(bm.getHeight());
            backCheckImage.setMinimumWidth(bm.getWidth());
            backCheckImage.setImageBitmap(bm);

            // Set up the click listener in case the picture is clicked
            backCheckImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showCamera(false);
                }
            });

            takePictureLinearLayout.setVisibility(View.VISIBLE);
            takePictureLinearLayout2.setVisibility(View.VISIBLE);
        }

        btnTakeBackPicture.setVisibility(View.GONE);
        btnSendDeposit.setVisibility(View.VISIBLE);
        btnSendDeposit.setEnabled(allValuesEntered());


    }

    /**
     * function: showCamera
     * <p/>
     * Determines which picture to take.
     *
     * @param frontPictureToBeTaken True: the front picture will be taken False: the back picture will be taken
     */
    private void showCamera(boolean frontPictureToBeTaken) {
        List<String> missingPermissions = PermissionsManagerUtils.missingPermissions(this);

        if(missingPermissions.size() == 0){
            final Intent intent = new Intent(DepositCheck.this, DepositCheckCamera.class);
            intent.putExtra(TAKE_FRONT_PICTURE, frontPictureToBeTaken);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);

        }else{
            PermissionsManagerUtils.askForPermission(this, missingPermissions,MiBancoConstants.REQUEST_CODE_ASK_PERMISSIONS);
        }

    }

    /**
     * function: setAmount
     * <p/>
     * Sets the amount TextView.
     *
     * @param amount The value to display in the amount field
     */
    private void setAmount(int amount) {
        this.amount = amount;
        amountTextView.setText(Utils.formatAmount(amount));

        if (btnSendDeposit.getVisibility() == View.VISIBLE)
            btnSendDeposit.setEnabled(allValuesEntered());
    }

    /**
     * function: allValuesEntered
     * <p/>
     * Determines if all the deposit values have been entered.
     *
     * @return value: true - all the values have been entered false - not all the values have been entered
     */
    private boolean allValuesEntered() {
        return amount > 0 && selectedAccount != null && currentState == FRONT_AND_BACK_PHOTOS_DISPLAYING;

    }

    /**
     * function: resetLayout
     * <p/>
     * Resets the layout to allow entry of another check.
     */
    private void resetLayout() {

        // Reset the values
        amount = 0;
        setAmount(amount);
        selectedAccount = null;
        currentState = NO_PHOTOS_DISPLAYING;
        application.setDepositCheckInformationFromSession(false);

        application.setDepositCheckFrontImage(null);
        application.setDepositCheckBackImage(null);
        application.setRemoteDepositHistory(null);

        btnSendDeposit.setVisibility(View.GONE);
        layoutSelectAccount.setVisibility(View.GONE);
        btnTakeFrontPicture.setVisibility(View.VISIBLE);
        popularWebView.setVisibility(View.VISIBLE);

        LinearLayout takePictureLinearLayout2 = (LinearLayout) findViewById(R.id.take_picture_rl_step_2_ll_3);
        LinearLayout takePictureLinearLayout = (LinearLayout) findViewById(R.id.take_picture_rl_step_2_ll_2);
        LinearLayout depositCheckPopularBorder = (LinearLayout) findViewById(R.id.deposit_check_popular_text_border);
        RelativeLayout takePictureRelativeLayout = (RelativeLayout)findViewById(R.id.take_picture_rl_step2);
        TextView depositCheckInfoTitle = (TextView) findViewById(R.id.deposit_check_info_title);
        LinearLayout depositCheckInfoLine = (LinearLayout) findViewById(R.id.deposit_check_info_line);

        if(takePictureLinearLayout != null && depositCheckAccountLayout != null && takePictureLinearLayout2 != null
                && depositCheckPopularBorder != null && takePictureRelativeLayout!= null && depositCheckInfoTitle != null
                && depositCheckInfoLine != null) {

            takePictureLinearLayout.setVisibility(View.GONE);
            depositCheckAccountLayout.setVisibility(View.VISIBLE);
            takePictureLinearLayout2.setVisibility(View.GONE);
            depositCheckPopularBorder.setVisibility(View.VISIBLE);
            takePictureRelativeLayout.setVisibility(View.GONE);
            depositCheckInfoTitle.setVisibility(View.GONE);
            depositCheckInfoLine.setVisibility(View.GONE);

        }

    }

    /**
     * function: onDepositCheckError
     * <p/>
     * Displays the error message when a deposit check fails.
     *
     * @param description The error description
     * @param detailedMessage A more detailed error message to display
     */
    private void onDepositCheckError(final String description, final String detailedMessage) {
        String title = getString(R.string.deposit_check_noun);
        String message = description;
        if (detailedMessage != null) {
            message = detailedMessage;
        }

        Utils.showAlert(DepositCheck.this, title, message, new SimpleListener() {

            @Override
            public void done() {
            }
        });

        BPAnalytics.logEvent(BPAnalytics.EVENT_REMOTE_DEPOSIT_FAILED);
    }

    private String getRdcInstructionsUrl(Customer customer) {
        String segmentType;
        if (customer.getIsComercialCustomer()){
            segmentType = "comercial";
        } else if (customer.getIsPremiumBanking()){
            segmentType = "pbs";
        } else if (customer.getIsWealth()) {
            segmentType = "wealth";
        } else if (!customer.getIsTransactional()){
            segmentType = "nonretail";
        } else {
            segmentType = "retail";
        }

        String requestedUrl = Utils.getLocaleStringResource(new Locale(application.getLanguage()), R.string.base_remote_deposit_instructions_url, getBaseContext());

        return requestedUrl.replace("%1$s",segmentType);
    }
}
