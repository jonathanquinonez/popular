package com.popular.android.mibanco.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.animation.FadeViewAnimation;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.listener.SimpleListener;
import com.popular.android.mibanco.model.FormField;
import com.popular.android.mibanco.model.Payment;
import com.popular.android.mibanco.model.PaymentActive;
import com.popular.android.mibanco.model.Transfer;
import com.popular.android.mibanco.model.TransferActive;
import com.popular.android.mibanco.model.TransferActiveAccount;
import com.popular.android.mibanco.model.TransferActiveTransfer;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.KiuwanUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.DialogCoverup;
import com.popular.android.mibanco.view.DialogHolo;
import com.popular.android.mibanco.view.pickerview.ArrayBankWheelAdapter;
import com.popular.android.mibanco.view.pickerview.ArrayBankWheelItem;
import com.popular.android.mibanco.view.pickerview.OnWheelChangedListener;
import com.popular.android.mibanco.view.pickerview.OnWheelScrollListener;
import com.popular.android.mibanco.view.pickerview.WheelScrollListener;
import com.popular.android.mibanco.view.pickerview.WheelView;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Provides UI and methods for performing payments and transfers.
 */
public class Payments extends BaseSessionActivity {

    /**
     * Constant that define the duration animation for real time views indicatotr
     */
    public final static long RT_ANIMATION_DURATION = 300; // Real time Animation Duration

    /**
     * The Constant PICK_DATE_REQUEST_CODE.
     */
    public final static int PICK_DATE_REQUEST_CODE = 1;

    /**
     * The Constant RECEIPT_REQUEST_CODE.
     */
    public final static int RECEIPT_REQUEST_CODE = 3;

    /**
     * The Constant AMOUNT_PICKER_REQUEST_CODE.
     */
    public final static int AMOUNT_PICKER_REQUEST_CODE = 2;

    /**
     * The Constant EBILL_REQUEST_CODE.
     */
    public final static int EBILL_REQUEST_CODE = 4;

    /**
     * Value of payee Real Time
     */
    private final static char VALID_REAL_TIME = 'Y'; // value for real time payess

    private final static long WHEEL_VIEW_ROTATION_DELAY_MILLIS = 250;

    /**
     * Indicates whether Payments Activity has already been destroyed.
     */
    private static boolean isPaymentsDestroyed = false;

    /**
     * Indicates whether Transfers Activity has already been destroyed.
     */
    private static boolean isTransfersDestroyed= false;

    /**
     * The adapter "From".
     */
    private ArrayBankWheelAdapter<ArrayBankWheelItem> adapterFrom;

    /**
     * The adapter "To".
     */
    private ArrayBankWheelAdapter<ArrayBankWheelItem> adapterTo;

    /**
     * The amount integer value.
     */
    private int amount;

    /**
     * The amount TextView control.
     */
    private TextView amountTextView;

    /**
     * The date TextView control.
     */
    private TextView dateTextView;

    /**
     * A dialog for covering up the main Activity during balances update.
     */
    private DialogCoverup balanceUpdateDialog;

    private Date selectedDate; //The currently selected date
    private Date effectiveDate; //The effective date
    private Date initialDateRealTime; //The real time effective date.
    private Date initialDate; //Today date.

    /**
     * "From" accounts wheel list.
     */
    private WheelView fromList;

    /**
     * Are we going to receipt screen now?
     */
    private boolean goToReceipt;

    /**
     * Current "From" position.
     */
    private int positionFrom;

    /**
     * Current "To" position.
     */
    private int positionTo;

    /**
     * The selected item "From".
     */
    private ArrayBankWheelItem itemFrom;

    /**
     * The selected item "To".
     */
    private ArrayBankWheelItem itemTo;

    /**
     * "To" accounts wheel list.
     */
    private WheelView toList;

    /**
     * Is current screen Transfers?
     */
    private boolean isTransfer;

    /**
     * btnLastAmount
     */
    private Button btnLastAmount; // last amount

    /**
     * Text for Expand Real Time Payee
     */
    private TextView seeMoreTextView; // button see more text notification view

    /**
     * View for Expand Real Time Payee
     */
    private LinearLayout seeMoreLinearLayout; // see more notification layout

    /**
     * Detail Text for Expand Real Time Payee
     */
    private TextView detailtextView; // detail notification view

    /**
     * Value that indicate if Expand Real Time Payee
     */
    private Boolean isVisibleTextSeeMore = false; // flag visible Text See More

    /**
     * View that indicate if Expand Real Time Payee
     */
    private LinearLayout realTimelinearLayout; // real time notification

    /**
     * Icon of Real Time Payee
     */
    private ImageView iconRealTimeimageView; // icon view real notification

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_DATE_REQUEST_CODE:
                    Date newSelectedDate = new Date(data.getLongExtra("date", selectedDate.getTime()));
                    selectDate(newSelectedDate);
                    effectiveDate = newSelectedDate;
                    break;
                case AMOUNT_PICKER_REQUEST_CODE:
                    setAmount(data.getIntExtra("amount", 0));
                    break;
                case RECEIPT_REQUEST_CODE:
                    break;
                case MiBancoConstants.ADD_PAYEES_REQUEST_CODE:
                case MiBancoConstants.EDIT_PAYEES_REQUEST_CODE:
                    fetchPayments(true, true, null);
                    break;
                default:
                    break;
            }
        } if (resultCode == RESULT_CANCELED) {
            switch (requestCode) {
                case MiBancoConstants.ADD_PAYEES_REQUEST_CODE:
                case MiBancoConstants.EDIT_PAYEES_REQUEST_CODE:
                    fetchPayments(true, true, null);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(application != null && application.getAsyncTasksManager() != null) {
            Utils.setupLanguage(this);

            setContentView(R.layout.payments);

            fromList = findViewById(R.id.wheel_from);
            toList = findViewById(R.id.wheel_to);
            isTransfer = getIntent().getBooleanExtra("transfers", false);
            amountTextView = findViewById(R.id.amount_text);
            dateTextView = findViewById(R.id.date_text);
            btnLastAmount = findViewById(R.id.last_ammount_btn);
            seeMoreTextView = findViewById(R.id.textViewSeeMore);
            detailtextView = findViewById(R.id.textViewDetail);
            seeMoreLinearLayout = findViewById(R.id.linearLayoutSeeMore);
            realTimelinearLayout = findViewById(R.id.linearLayoutRealTime);
            iconRealTimeimageView = findViewById(R.id.imageViewIconRealTime);

            if (FeatureFlags.MBMT_477()) {
                btnLastAmount.setVisibility(View.GONE);
                btnLastAmount.setOnClickListener(lastPaymentListener);
            }

            final String selectedDateValue = "selectedDate"; // calendar selected date

            if (savedInstanceState != null) {
                if (savedInstanceState.getLong(selectedDateValue) != 0L) {
                    selectDate(new Date(savedInstanceState.getLong(selectedDateValue)));
                }
                positionFrom = savedInstanceState.getInt("positionFrom");
                positionTo = savedInstanceState.getInt("positionTo");
                isTransfer = savedInstanceState.getBoolean("isTransfer");
                setAmount(savedInstanceState.getInt("amount"));
            }

            if (isTransfer) {
                setTransfersDestroyed(false);
                fetchTransfers(true, false, null);
            } else {
                setPaymentsDestroyed(false);
                fetchPayments(true, false, null);
            }

            seeMoreLinearLayout.setVisibility(View.GONE);
            realTimelinearLayout.setVisibility(View.GONE);
            iconRealTimeimageView.setVisibility(View.INVISIBLE);
            realTimelinearLayout.setBackgroundColor(getResources().getColor(R.color.white));

            setListeners();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        positionFrom = 0;
        positionTo = 0;
        amount = 0;
        selectedDate = null;
        setAmount(0);

        isTransfer = intent.getBooleanExtra("transfers", false);

        if (isTransfer) {
            setTransfersDestroyed(false);
            fetchTransfers(true, false, null);
        } else {
            setPaymentsDestroyed(false);
            fetchPayments(true, false, null);
        }

        setListeners();
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();

        if(application != null && application.getAsyncTasksManager() != null) {
            // cover up the Activity till portal update is finished
            if (application.isUpdatingBalances()) {
                Utils.dismissDialog(application.getDialogCoverupUpdateBalances());
                application.setDialogCoverupUpdateBalances(new DialogCoverup(Payments.this));
                application.getDialogCoverupUpdateBalances().setProgressCaption(R.string.refreshing_balances);
                Utils.showDialog(application.getDialogCoverupUpdateBalances(), Payments.this);
            }

            if (isTransfer) {
                if (application.isReloadTransfers()) {
                    fetchTransfers(true, true, null);
                    application.setReloadTransfers(false);
                } else if (application.isRefreshTransfersCardImages()) {
                    fetchTransfers(true, false, null);
                    application.setRefreshTransfersCardImages(false);
                } else if (application.isUpdatingBalances()) {
                    application.setReloadPayments(true);
                    fetchTransfers(false, true, new SimpleListener() {

                        @Override
                        public void done() {
                            application.setUpdatingBalances(false);
                            Utils.dismissDialog(application.getDialogCoverupUpdateBalances());
                            application.setDialogCoverupUpdateBalances(null);

                            // Update the widget balances after a successful transfer
                            final SharedPreferences sharedPreferences = Utils.getSecuredSharedPreferences(application);
                            final SharedPreferences.Editor editor = sharedPreferences.edit();
                            application.getAsyncTasksManager().updateWidgetBalances(Payments.this, editor);
                        }
                    });
                }
            } else {
                if (application.isReloadPayments()) {
                    fetchPayments(true, true, null);
                    application.setReloadPayments(false);
                } else if (application.isRefreshPaymentsCardImages()) {
                    fetchPayments(true, false, null);
                    application.setRefreshPaymentsCardImages(false);
                } else if (application.isUpdatingBalances()) {
                    application.setReloadTransfers(true);
                    fetchPayments(false, true, new SimpleListener() {

                        @Override
                        public void done() {
                            application.setUpdatingBalances(false);
                            Utils.dismissDialog(application.getDialogCoverupUpdateBalances());
                            application.setDialogCoverupUpdateBalances(null);

                            if (FeatureFlags.MBMT_511()) {
                                // Update the widget balances after a successful payment
                                final SharedPreferences sharedPreferences = Utils.getSecuredSharedPreferences(application);
                                final SharedPreferences.Editor editor = sharedPreferences.edit();
                                application.getAsyncTasksManager().updateWidgetBalances(Payments.this, editor);
                            }
                        }
                    });
                }
            }

            invalidateOptionsMenu();
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
    protected void onDestroy() {
        if (isTransfer) {
            setTransfersDestroyed(true);
        } else {
            setPaymentsDestroyed(true);
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        final String selectedDateValue = "selectedDate"; // calendar selected date

        if (selectedDate != null) {
            outState.putLong(selectedDateValue, selectedDate.getTime());
        }

        outState.putInt("positionFrom", positionFrom);
        outState.putInt("positionTo", positionTo);
        outState.putBoolean("isTransfer", isTransfer);
        outState.putInt("amount", amount);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (isTransfer) {
            menu.findItem(R.id.menu_ebills).setVisible(false);
        } else {
            if (application!= null
                    && application.getAsyncTasksManager() != null
                    && application.getValidEbills() != null && application.getValidEbills().size() > 0) {
                menu.findItem(R.id.menu_ebills).setVisible(true);
            }
            if (FeatureFlags.ADD_PAYEES() && Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                menu.findItem(R.id.menu_add_payee).setVisible(true);
                menu.findItem(R.id.menu_edit_payee).setVisible(true);
                menu.findItem(R.id.menu_logout).setVisible(false);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_ebills:
                final Intent iEBills = new Intent(Payments.this, EBill.class);
                startActivityForResult(iEBills, Payments.EBILL_REQUEST_CODE);
                return true;
            case R.id.menu_add_payee:
                openAddPayeesWebView();
                return true;
            case R.id.menu_edit_payee:
                openEditPayeesWebView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Change wheel view item opacity.
     *
     * @param wheel       the wheel
     * @param currentItem the current item
     */
    private void changeWheelViewItemOpacity(final WheelView wheel, final int currentItem) {
        @SuppressWarnings("unchecked")
        final ArrayBankWheelAdapter<ArrayBankWheelItem> adapter = (ArrayBankWheelAdapter<ArrayBankWheelItem>) wheel.getViewAdapter();
        adapter.setCurrentItem(currentItem);
        wheel.invalidateWheel(true);
    }

    /**
     * Fetch payments.
     *
     * @param showProgress Show progress dialog?
     * @param forceReload  Should we force reload even when payments are already loaded.
     * @param listener     a listener to notify about task end
     */
    @SuppressLint("NewApi")
    private void fetchPayments(final boolean showProgress, final boolean forceReload, final SimpleListener listener) {
        if(application != null && application.getAsyncTasksManager() != null) {
            application.getAsyncTasksManager().fetchPayments(this, new ResponderListener() {

                @Override
                public void responder(final String responderName, final Object data) {
                    int message = R.string.no_payees;
                    if (FeatureFlags.ADD_PAYEES()) {
                        message = R.string.no_payees_add_new_payees;
                        toList.invalidateWheel(true);
                        toList.setViewAdapter(new ArrayBankWheelAdapter<>(Payments.this, new ArrayList<ArrayBankWheelItem>()));
                    }
                    if (Utils.showNoPayeesDialog(Payments.this, message, !FeatureFlags.ADD_PAYEES(), new SimpleListener() {
                        @Override
                        public void done() {
                            if (FeatureFlags.ADD_PAYEES()) {
                                /*This will be validated if is a dialog with options yes and cancel besides a simple dialog*/
                                //openAddPayeesWebView();
                                fromList.setEnabled(false);
                                toList.setEnabled(false);
                            }
                        }
                    })) { return; }

                    final List<ArrayBankWheelItem> outItemsFrom = new ArrayList<>();
                    final List<ArrayBankWheelItem> outItemsTo = new ArrayList<>();

                    if (application.getValidEbills() != null && application.getValidEbills().size() > 0) {
                        fromList.post(new Runnable() {

                            @Override
                            public void run() {
                                invalidateOptionsMenu();
                            }
                        });
                    }

                    application.getAsyncTasksManager().loadPaymentsCards(Payments.this, new SimpleListener() {

                        @Override
                        public void done() {
                            final int tempFromPosition = positionFrom;
                            final int tempToPosition = positionTo;

                            adapterFrom = new ArrayBankWheelAdapter<>(Payments.this, R.layout.wheel_item, outItemsFrom);
                            if (outItemsFrom.size() > 0 && positionFrom < outItemsFrom.size()) {
                                itemFrom = outItemsFrom.get(positionFrom);
                            }
                            fromList.setClickable(true);
                            fromList.setViewAdapter(adapterFrom);
                            fromList.setCD(false);
                            fromList.setCyclic(false);

                            adapterTo = new ArrayBankWheelAdapter<>(Payments.this, outItemsTo);
                            if (outItemsTo.size() > 0 && positionTo < outItemsTo.size()) {
                                itemTo = outItemsTo.get(positionTo);
                            }
                            toList.setClickable(true);
                            toList.setViewAdapter(adapterTo);
                            toList.setCD(false);
                            toList.setCyclic(false);

                            fromList.setCurrentItem(tempFromPosition);
                            toList.setCurrentItem(tempToPosition);

                            if (FeatureFlags.MBMT_477()) {
                                if (!Utils.isBlankOrNull(itemTo.getLastPayment()) && !Utils.isBlankOrNull(itemTo.getLastPaymentDate())) {
                                    btnLastAmount.setText(getString(R.string.last_payment, itemTo.getLastPayment(), itemTo.getLastPaymentDate()));
                                    btnLastAmount.setVisibility(View.VISIBLE);
                                    btnLastAmount.setClickable(true);
                                } else {
                                    btnLastAmount.setVisibility(View.INVISIBLE);
                                    btnLastAmount.setClickable(false);
                                }
                            }

                            levelLists();

                            final Payment paymentInstance = KiuwanUtils.checkBeforeCast(Payment.class, data); //Payment instance

                            if (paymentInstance != null){
                                initialDate = paymentInstance.getEffectiveDate();
                                initialDateRealTime = paymentInstance.getRealTimeEffectiveDate();
                            }

                            validateRealTimePayee();

                            if (forceReload) {
                                resetControls();
                            }

                            if (listener != null) {
                                listener.done();
                            }

                            if (App.getApplicationInstance().getEbillRequest() != null) {
                                checkEbillData();
                            }
                        }
                    }, (Payment) data, outItemsFrom, outItemsTo, showProgress);
                }

                @Override
                public void sessionHasExpired() {
                    Utils.dismissDialog(balanceUpdateDialog);
                    balanceUpdateDialog = null;
                    application.reLogin(Payments.this);
                }
            }, showProgress, forceReload);
        }
    }

    /**
     * Fetch transfers.
     *
     * @param showProgress Show progress dialog?
     * @param forceReload  Should we force reload even when transfers are already loaded.
     * @param listener     a listener to notify about task end
     */
    @SuppressLint("NewApi")
    private void fetchTransfers(final boolean showProgress, final boolean forceReload, final SimpleListener listener) {

        FadeViewAnimation.hideFadeViewAnimation(iconRealTimeimageView, RT_ANIMATION_DURATION, View.GONE);
        FadeViewAnimation.hideFadeViewAnimation(realTimelinearLayout, RT_ANIMATION_DURATION, View.INVISIBLE);

        if(application != null && application.getAsyncTasksManager() != null) {
            ((App) getApplication()).getAsyncTasksManager().fetchTransfers(this, new ResponderListener() {

                @Override
                public void responder(final String responderName, final Object data) {
                    final List<ArrayBankWheelItem> outItemsFrom = new ArrayList<ArrayBankWheelItem>();
                    final List<ArrayBankWheelItem> outItemsTo = new ArrayList<ArrayBankWheelItem>();

                    fromList.post(new Runnable() {

                        @Override
                        public void run() {
                            invalidateOptionsMenu();
                        }
                    });

                    application.getAsyncTasksManager().loadTransfersCards(Payments.this, new SimpleListener() {

                        @Override
                        public void done() {
                            adapterFrom = new ArrayBankWheelAdapter<>(Payments.this, R.layout.wheel_item, outItemsFrom);
                            if (outItemsFrom.size() > 0 && positionFrom < outItemsFrom.size()) {
                                itemFrom = outItemsFrom.get(positionFrom);
                            }
                            fromList.setClickable(true);
                            fromList.setViewAdapter(adapterFrom);
                            fromList.setCD(false);
                            fromList.setCyclic(false);

                            adapterTo = new ArrayBankWheelAdapter<>(Payments.this, outItemsTo);
                            if (outItemsTo.size() > 0 && positionTo < outItemsTo.size()) {
                                itemTo = outItemsTo.get(positionTo);
                            }
                            toList.setClickable(true);
                            toList.setViewAdapter(adapterTo);
                            toList.setCD(false);
                            toList.setCyclic(false);

                            fromList.setCurrentItem(positionFrom);
                            toList.setCurrentItem(positionTo);
                            levelLists();

                            effectiveDate = ((Transfer) data).getEfectiveDate();
                            initialDate = effectiveDate;

                            if (selectedDate == null) {
                                selectDate(effectiveDate);
                            }

                            if (forceReload) {
                                resetControls();
                            }

                            if (listener != null) {
                                listener.done();
                            }
                        }
                    }, (Transfer) data, outItemsFrom, outItemsTo, showProgress);
                }

                @Override
                public void sessionHasExpired() {
                    Utils.dismissDialog(balanceUpdateDialog);
                    balanceUpdateDialog = null;
                    application.reLogin(Payments.this);
                }
            }, showProgress, forceReload);
        }
    }

    private void checkEbillData() {
        Bundle request = App.getApplicationInstance().getEbillRequest();
        App.getApplicationInstance().setEbillRequest(null);

        int ebillPayeeId = request.getInt("payeeId", 0);
        if (ebillPayeeId == 0) {
            return;
        }

        String ebillPayeeCode = request.getString("payeeCode");

        Date ebillDate = (Date) request.getSerializable("ebillDate");
        int ebillAmount = request.getInt("ebillAmount", 0);

        if (adapterTo != null && toList != null) {
            for (int i = 0; i < adapterTo.getItemsCount(); ++i) {
                final ArrayBankWheelItem item = adapterTo.getItemAt(i);
                if (item.getPayeeId() != 0 && item.getPayeeId() == ebillPayeeId && item.getCode().equals(ebillPayeeCode)) {
                    final int index = i;
                    toList.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (toList != null) {
                                toList.setCurrentItem(index, true);
                            }
                        }
                    }, WHEEL_VIEW_ROTATION_DELAY_MILLIS);

                    break;
                }
            }

            selectDate(ebillDate);
            setAmount(ebillAmount);
        }
    }

    public boolean isGoToReceipt() {
        return goToReceipt;
    }

    /**
     * Level "From" and "To" lists so the bottom rulers are on the same level.
     */
    private void levelLists() {
        final boolean fromFiller = adapterFrom.getFiller();
        final boolean toFiller = adapterTo.getFiller();

        if (fromList.getCurrentItem() == 0 && toList.getCurrentItem() != 0) {
            adapterFrom.setFiller(true);
        } else {
            adapterFrom.setFiller(false);
        }

        if (fromList.getCurrentItem() != 0 && toList.getCurrentItem() == 0) {
            adapterTo.setFiller(true);
        } else {
            adapterTo.setFiller(false);
        }

        if (fromFiller != adapterFrom.getFiller()) {
            fromList.invalidateWheel(true);
        }

        if (toFiller != adapterTo.getFiller()) {
            toList.invalidateWheel(true);
        }
    }

    private void onTransactionError(final String title, final String description, final String detailedMessage) {
        String transactionNoun;
        if (isTransfer) {
            transactionNoun = getString(R.string.transfer_noun);
        } else {
            transactionNoun = getString(R.string.payment_noun);
        }

        String message = description;
        if (detailedMessage != null) {
            message = detailedMessage;
        }

        Utils.showAlert(Payments.this, title, description, new SimpleListener() {

            @Override
            public void done() {
                if (isTransfer) {
                    fetchTransfers(true, true, null);
                } else {
                    fetchPayments(true, true, null);
                }
            }
        });

        BPAnalytics.logEvent(BPAnalytics.EVENT_TRANSACTION_FAILED, "transactionType", transactionNoun, "error", title, "description", message);
    }

    /**
     * Payment processor.
     *
     * @param payment the payment to process
     */
    private void paymentProcessor(final PaymentActive payment) {
        if(application != null && application.getAsyncTasksManager() != null) {
            final App app = (App) getApplication();
            // looks like transactions always use "MM/dd/yyyy" date format with web services
            final SimpleDateFormat sdf = new SimpleDateFormat(MiBancoConstants.WEBSERVICE_DATE_FORMAT);

            if (payment.getContent() == null) {
                onTransactionError(getString(R.string.transaction_failed_title), getString(R.string.transaction_failed_message), null);
                return;
            }

            if (payment.getResponderName().equals("quickpayment") && payment.getResponderMessage().equals("quickpayment_confirmation")) {
                if (!payment.isPaymentSent()) {
                    if (payment.isDowntime()) {
                        Utils.showMaintenanceError(Payments.this, getString(R.string.maintenance_personal_banking));
                    } else if (!TextUtils.isEmpty(payment.getPaymentMessage())) {
                        final SimpleDateFormat sdfLocal = new SimpleDateFormat(app.getDateFormat());
                        String dateLocal = payment.getEffectiveDate();
                        try {
                            dateLocal = sdfLocal.format(sdf.parse(payment.getEffectiveDate()));
                        } catch (final ParseException e) {
                            Log.e("Payments", "Error parsing effective date.");
                            onTransactionError(getString(R.string.transaction_failed_title), getString(R.string.transaction_failed_message), "Error parsing effective date.");
                            return;
                        }

                        application.getAsyncTasksManager().updateBalances(Payments.this);

                        final Intent intent = new Intent(Payments.this, PaymentReceipt.class);
                        intent.putExtra(MiBancoConstants.ERROR_MESSAGE_KEY, payment.getPaymentMessage());
                        intent.putExtra("transfers", isTransfer);
                        intent.putExtra("referenceNr", payment.getReferenceNumber());
                        intent.putExtra("fromName", payment.getSourceAccountNickname());
                        intent.putExtra("fromNr", payment.getSourceAccountNumber());
                        intent.putExtra("toName", payment.getPayeeNickname());
                        intent.putExtra("toNr", payment.getPayeeBillingAccount());
                        intent.putExtra("amount", payment.getAmount());
                        intent.putExtra("date", dateLocal);

                        BPAnalytics.logEvent(BPAnalytics.EVENT_TRANSACTION_FAILED, "transactionType", getString(R.string.transfer_noun), "error", getString(R.string.transfer_failed_title),
                                "description", payment.getPaymentMessage());
                        Payments.this.startActivityForResult(intent, RECEIPT_REQUEST_CODE);
                        goToReceipt = true;
                    } else {
                        onTransactionError(getString(R.string.payment_failed_title), getString(R.string.payment_failed_message), null);
                    }
                } else {
                    // everything seems to be ok, go to receipt screen
                    final SimpleDateFormat sdfLocal = new SimpleDateFormat(app.getDateFormat());
                    String dateLocal = payment.getEffectiveDate();
                    try {
                        dateLocal = sdfLocal.format(sdf.parse(payment.getEffectiveDate()));
                    } catch (final ParseException e) {

                        Log.e("Payments", "Error parsing effective date.");
                        onTransactionError(getString(R.string.transaction_failed_title), getString(R.string.transaction_failed_message), "Error parsing effective date.");
                        return;
                    }

                    application.getAsyncTasksManager().updateBalances(Payments.this);

                    final Intent intent = new Intent(Payments.this, PaymentReceipt.class);
                    intent.putExtra("transfers", isTransfer);
                    intent.putExtra("referenceNr", payment.getReferenceNumber());
                    intent.putExtra("fromName", payment.getSourceAccountNickname());
                    intent.putExtra("fromNr", payment.getSourceAccountNumber());
                    intent.putExtra("toName", payment.getPayeeNickname());
                    intent.putExtra("toNr", payment.getPayeeBillingAccount());
                    intent.putExtra("amount", payment.getAmount());
                    intent.putExtra("date", dateLocal);

                    BPAnalytics.logEvent(BPAnalytics.EVENT_PAYMENT_SUCCESSFUL);

                    Payments.this.startActivityForResult(intent, RECEIPT_REQUEST_CODE);
                    goToReceipt = true;
                }
            } else if (payment.getResponderName().equals("quickpayment") && payment.getResponderMessage().equals("quickpayment_verification")) {
                if (payment.getEffectiveDate() == null || payment.getAmount() == null) {
                    onTransactionError(getString(R.string.payment_failed_title), getString(R.string.payment_failed_message), null);
                    return;
                }
                int accountFromBalance = App.getIntAmount(itemFrom.getAmount().trim());
                showPaymentConfirmation(payment, this.amount > accountFromBalance);

            } else if (payment.getResponderName().equals("quickpayment") && payment.getResponderMessage().equals("quickpayment_info")) {

                if (payment.getForm() == null) {
                    onTransactionError(getString(R.string.payment_failed_title), getString(R.string.payment_failed_message), null);
                    return;
                }

                if (payment.getForm().getFields() == null) {
                    onTransactionError(getString(R.string.payment_failed_title), getString(R.string.payment_failed_message), null);
                    return;
                }

                if (payment.getForm().getFields().get("quickpayment.amount") == null) {
                    onTransactionError(getString(R.string.payment_failed_title), getString(R.string.payment_failed_message), null);
                    return;
                }

                final FormField field = payment.getForm().getFields().get("quickpayment.amount");
                String quickpaymentAmountError = null;
                quickpaymentAmountError = field.getError();
                if (quickpaymentAmountError == null) {
                    onTransactionError(getString(R.string.payment_failed_title), getString(R.string.payment_failed_message), null);
                    return;
                }

                if (quickpaymentAmountError.contains(getString(R.string.transaction_error_code_overpay_en)) || quickpaymentAmountError.contains(getString(R.string.transaction_error_code_overpay_es))) {
                    // Overpay error - show an alert before the confirmation dialog
                    showPaymentConfirmation(MiBancoConstants.WEBSERVICE_DATE_FORMAT, sdf.format(selectedDate), Utils.formatAmountForWs(amount), itemFrom.getName(), itemFrom.getCode(), itemTo.getName(),
                            itemTo.getCode(), true);
                } else {
                    if (quickpaymentAmountError.contains(getString(R.string.transaction_error_larger_than_max))) {
                        // Not enough balance alert
                        onTransactionError(getString(R.string.payment_failed_title), getString(R.string.transaction_error_larger_than_max) + ".", quickpaymentAmountError);
                    } else {
                        onTransactionError(getString(R.string.payment_failed_title), getString(R.string.payment_failed_message), quickpaymentAmountError);
                    }
                }
            } else {
                final DialogHolo dialog = new DialogHolo(Payments.this);
                dialog.setTitle(getString(R.string.payment_failed_title));
                dialog.setMessage(getString(R.string.transaction_unknown_error));
                dialog.setConfirmationButton("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        Utils.dismissDialog(dialog);
                        application.reLogin(Payments.this);
                    }
                });
                Utils.showDialog(dialog, Payments.this);
            }
        }
    }

    /**
     * Reset controls.
     */
    private void resetControls() {
        fromList.setCurrentItem(0);
        fromList.scrollTo(0, 0);
        toList.setCurrentItem(0);
        toList.scrollTo(0, 0);

        setAmount(0);
        selectDate(effectiveDate);
        validateRealTimePayee();
    }

    /**
     * Sets the amount TextView.
     */
    private void setAmount(int amount) {
        this.amount = amount;
        amountTextView.setText(Utils.formatAmount(amount));
    }

    private void selectDate(final Date date) {
        this.selectedDate = date;
        dateTextView.setText(DateFormat.getLongDateFormat(getApplicationContext()).format(date));
    }

    public void setGoToReceipt(final boolean receipt) {
        goToReceipt = receipt;
    }

    /**
     * Sets the listeners.
     */
    private void setListeners() {
        findViewById(R.id.amount_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Intent intent = new Intent(Payments.this, EnterAmount.class);
                intent.putExtra("amount", amount);
                startActivityForResult(intent, AMOUNT_PICKER_REQUEST_CODE);
            }
        });

        findViewById(R.id.calendar_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // if webservice haven't returned effective date yet
                if (effectiveDate != null) {
                    final Intent intent = new Intent(v.getContext(), CalendarView.class);
                    final String startTimeParam = "startTime"; // start time intent param
                    final String initialDateParam = "initialDate"; // start time intent param
                    final String rtNotificationParam = "rtNotification"; // rtNotification intent param
                    final long initialDateValue = KiuwanUtils.compareStringChar(itemTo.getRtNotification(), VALID_REAL_TIME)
                            ? initialDateRealTime.getTime() : initialDate.getTime(); // inital date

                    intent.putExtra(startTimeParam, effectiveDate.getTime());
                    intent.putExtra(initialDateParam, initialDateValue);
                    intent.putExtra(rtNotificationParam, itemTo.getRtNotification());
                    startActivityForResult(intent, PICK_DATE_REQUEST_CODE);
                }
            }
        });

        final Button payButton = (Button) findViewById(R.id.pay_button);

        if (isTransfer) {
            ((TextView) findViewById(R.id.schedule_text)).setText(R.string.schedule_transfer);
            payButton.setText(R.string.transfer_verb_button);
        } else {
            ((TextView) findViewById(R.id.schedule_text)).setText(R.string.schedule_payment);
            payButton.setText(R.string.payment_verb);
        }

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final App application = (App) getApplication();

                if (!validateTransactionAmount()) {
                    onTransactionError(getString(R.string.transaction_invalid_data_title), getString(R.string.transaction_invalid_data_amount), null);
                    return;
                }

                if (!validateTransactionAccounts()) {
                    onTransactionError(getString(R.string.transaction_invalid_data_title), getString(R.string.transaction_invalid_data_account), null);
                    return;
                }

                if (isTransfer) {
                    if (application.getTransfersInfo() == null) {
                        onTransactionError(getString(R.string.transaction_failed_title), getString(R.string.transaction_failed_message), null);
                        return;
                    }

                    int accountFromBalance = App.getIntAmount(itemFrom.getAmount().trim());
                    if (!validateTransactionFromAccountBalance() || amount > accountFromBalance) {
                        onTransactionError(getString(R.string.transaction_invalid_data_title), getString(R.string.transfer_failed_zero_or_negative_balance), null);
                        return;
                    }

                    // commence a transfer
                    final SimpleDateFormat sdf = new SimpleDateFormat(application.getTransfersInfo().getDateFormat());
                    application.getAsyncTasksManager().makeTransfer(Payments.this, itemFrom.getId(), itemTo.getId(), Utils.formatAmountForWs(amount), sdf.format(selectedDate), "variable", true,
                            new ResponderListener() {

                                @Override
                                public void responder(final String responderName, final Object data) {
                                    transferProcessor((TransferActive) data);
                                }

                                @Override
                                public void sessionHasExpired() {
                                    application.reLogin(Payments.this);
                                }
                            });
                } else {
                    // commence a payment
                    final SimpleDateFormat sdf = new SimpleDateFormat(MiBancoConstants.WEBSERVICE_DATE_FORMAT);
                    application.getAsyncTasksManager().quickPayment(Payments.this, itemFrom.getId(), Utils.formatAmountForWs(amount), sdf.format(selectedDate), itemTo.getId(), true,
                            new ResponderListener() {

                                @Override
                                public void responder(final String responderName, final Object data) {
                                    paymentProcessor((PaymentActive) data);
                                }

                                @Override
                                public void sessionHasExpired() {
                                    application.reLogin(Payments.this);
                                }
                            });
                }
            }
        });

        fromList.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(final WheelView wheel, final int lastItem, final int currentItem) {
                @SuppressWarnings("unchecked")
                final ArrayBankWheelAdapter<ArrayBankWheelItem> adapter = (ArrayBankWheelAdapter<ArrayBankWheelItem>) wheel.getViewAdapter();
                itemFrom = adapter.getItemAt(currentItem);
                levelLists();
                changeWheelViewItemOpacity(wheel, currentItem);
                positionFrom = currentItem;
            }
        });
        fromList.invalidateWheel(true);

        toList.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(final WheelView wheel, final int lastItem, final int currentItem) {
                @SuppressWarnings("unchecked")
                final ArrayBankWheelAdapter<ArrayBankWheelItem> adapter = (ArrayBankWheelAdapter<ArrayBankWheelItem>) wheel.getViewAdapter();
                itemTo = adapter.getItemAt(currentItem);
                levelLists();
                changeWheelViewItemOpacity(wheel, currentItem);
                positionTo = currentItem;

                if(FeatureFlags.MBMT_477()) {
                    setAmount(0);
                    if (!Utils.isBlankOrNull(itemTo.getLastPayment())) {
                        btnLastAmount.setVisibility(View.INVISIBLE);
                        btnLastAmount.setText(getString(R.string.last_payment, itemTo.getLastPayment(), itemTo.getLastPaymentDate()));
                        fadeLastPayment(true);
                        btnLastAmount.setVisibility(View.VISIBLE);
                        btnLastAmount.setClickable(true);
                    } else {
                        fadeLastPayment(false);
                        btnLastAmount.setVisibility(View.INVISIBLE);
                        btnLastAmount.setClickable(false);
                    }
                }
            }
        });

        toList.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingFinished(WheelView wheel) {

            }
            @Override
            public void onScrollingStarted(WheelView wheel) {
                FadeViewAnimation.hideFadeViewAnimation(iconRealTimeimageView,(long)10.0,View.GONE);
                FadeViewAnimation.hideFadeViewAnimation(realTimelinearLayout,(long)10.0,View.INVISIBLE);
            }
        });

        toList.setScrollEventListener(new WheelScrollListener() {
            @Override
            public void stopScroll() {
                validateRealTimePayee();
            }
        });

        toList.invalidateWheel(true);

        seeMoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDetailRealTimeNotification(isVisibleTextSeeMore);
            }
        });

        detailtextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDetailRealTimeNotification(isVisibleTextSeeMore);
            }
        });

    }

    /**
     * validateRealTimePayee
     */
    private void validateRealTimePayee(){
        Date resetCalendarToDate = null; // date to reset calendar
        final String validValue = "true"; // true string value

        if (KiuwanUtils.compareStringChar(itemTo.getRtNotification(), VALID_REAL_TIME)) {
            resetCalendarToDate = initialDateRealTime;
            realTimelinearLayout.setVisibility(View.VISIBLE);
            iconRealTimeimageView.setVisibility(View.VISIBLE);

            FadeViewAnimation.showFadeViewAnimation(iconRealTimeimageView, RT_ANIMATION_DURATION);
            FadeViewAnimation.showFadeViewAnimation(realTimelinearLayout, RT_ANIMATION_DURATION);

            displayDetailRealTimeNotification(validValue.equals(itemTo.getRtHasPaymentHistory()));

        } else {
            resetCalendarToDate = initialDate;
            FadeViewAnimation.hideFadeViewAnimation(iconRealTimeimageView,RT_ANIMATION_DURATION,View.GONE);
            FadeViewAnimation.hideFadeViewAnimation(realTimelinearLayout,RT_ANIMATION_DURATION,View.INVISIBLE);
        }

        if (resetCalendarToDate != null) {
            selectDate(resetCalendarToDate);
            effectiveDate = resetCalendarToDate;
        }
    }

    private void displayDetailRealTimeNotification(boolean show){
        seeMoreLinearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        seeMoreTextView.setText(show ? getApplicationContext().getString(R.string.pay_see_more) : StringUtils.EMPTY);
        realTimelinearLayout.setBackgroundColor(getResources().getColor(show ? R.color.white : R.color.payments_yellow));
        isVisibleTextSeeMore = !show;
    }

    /**
     * Show payment confirmation.
     *
     * @param payment the payment
     * @param overpay overpay?
     */
    private void showPaymentConfirmation(final PaymentActive payment, final boolean overpay) {
        showPaymentConfirmation(payment.getEffectiveDateFormat(), payment.getEffectiveDate(), payment.getAmount(), payment.getSourceAccountNickname(), payment.getSourceAccountNumber(),
                payment.getPayeeNickname(), payment.getPayeeBillingAccount(), overpay);
    }

    /**
     * Show payment confirmation.
     *
     * @param effectiveDateFormat   the effective date format
     * @param effectiveDateString   the effective date string
     * @param amountString          the amount string
     * @param sourceAccountNickname the source account nickname
     * @param sourceAccountNumber   the source account number
     * @param payeeNickname         the payee nickname
     * @param payeeBillingAccount   the payee billing account
     * @param overpay               overpay?
     */
    private void showPaymentConfirmation(final String effectiveDateFormat, final String effectiveDateString, final String amountString, final String sourceAccountNickname,
                                         final String sourceAccountNumber, final String payeeNickname, final String payeeBillingAccount, final boolean overpay) {
        final DialogHolo dialog = new DialogHolo(Payments.this);
        dialog.setTitle(getString(R.string.confirm_your_payment));
        final View dialogView = dialog.setCustomContentView(R.layout.dialog_payment);
        dialog.setCancelable(true);
        dialog.setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                Utils.dismissDialog(dialog);
            }
        });

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MiBancoConstants.ENGLISH_DATE_FORMAT);
        String currentDateString = simpleDateFormat.format(new Date());
        String confirmButtonTitle;
        if (currentDateString.equals(effectiveDateString)) {
            confirmButtonTitle = getString(R.string.pay_now);
        } else {
            confirmButtonTitle = getString(R.string.payment_future) + effectiveDateString;
        }

        dialog.setPositiveButton(confirmButtonTitle, new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                application.getAsyncTasksManager().quickPayment(Payments.this, itemFrom.getId(), Utils.formatAmountForWs(amount), effectiveDateString, itemTo.getId(), false, new ResponderListener() {

                    @Override
                    public void responder(final String responderName, final Object data) {
                        Utils.dismissDialog(dialog);
                        paymentProcessor((PaymentActive) data);
                    }

                    @Override
                    public void sessionHasExpired() {
                        application.reLogin(Payments.this);
                    }
                });
            }
        });

        // set dialog's text boxes
        ((TextView) dialogView.findViewById(R.id.pay_amount)).setText(amountString);

        Factory factory = Spannable.Factory.getInstance();
        Spannable fromAccountNickname = factory.newSpannable(sourceAccountNickname + " ");
        Spannable fromAccountCode = factory.newSpannable(sourceAccountNumber);
        fromAccountNickname.setSpan(new StyleSpan(Typeface.BOLD), 0, fromAccountNickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        fromAccountNickname.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)), 0, fromAccountNickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        fromAccountCode.setSpan(new StyleSpan(Typeface.NORMAL), 0, fromAccountCode.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        fromAccountCode.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.grey_dark)), 0, fromAccountCode.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannedString fromAccountTitle = (SpannedString) TextUtils.concat(fromAccountNickname, fromAccountCode);
        ((TextView) dialogView.findViewById(R.id.from_nickname)).setText(fromAccountTitle);

        Spannable toAccountNickname = factory.newSpannable(payeeNickname + " ");
        Spannable toAccountCode = factory.newSpannable(payeeBillingAccount);
        toAccountNickname.setSpan(new StyleSpan(Typeface.BOLD), 0, toAccountNickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        toAccountNickname.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)), 0, toAccountNickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        toAccountCode.setSpan(new StyleSpan(Typeface.NORMAL), 0, toAccountCode.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        toAccountCode.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.grey_dark)), 0, toAccountCode.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannedString toAccountTitle = (SpannedString) TextUtils.concat(toAccountNickname, toAccountCode);
        ((TextView) dialogView.findViewById(R.id.to_nickname)).setText(toAccountTitle);

        final SimpleDateFormat sdf = new SimpleDateFormat(effectiveDateFormat);
        final String originalDate = sdf.format(selectedDate);
        String tempDateString = DateFormat.getLongDateFormat(getApplicationContext()).format(selectedDate);

        if (!originalDate.equals(effectiveDateString)) {
            Date tempDate;
            try {
                tempDate = sdf.parse(effectiveDateString);
                tempDateString = DateFormat.getLongDateFormat(getApplicationContext()).format(tempDate);
            } catch (final ParseException e) {
                Log.e("Payments", "Error parsing effective date.");
                onTransactionError(getString(R.string.transaction_failed_title), getString(R.string.transaction_failed_message), "Error parsing effective date.");
                return;
            }
            // show alert on the top
            Utils.showAlert(Payments.this, getString(R.string.transaction_confirm_effective_date_title), String.format(getString(R.string.transaction_confirm_effective_date_message), tempDateString));
            selectDate(tempDate);
        }
        ((TextView) dialogView.findViewById(R.id.date)).setText(tempDateString);

        // highlight the amount - overpay
        if (overpay) {
            ((TextView) dialogView.findViewById(R.id.pay_amount)).setTextColor(ContextCompat.getColor(this, R.color.red));
             ((TextView) dialogView.findViewById(R.id.pay_amount)).setText(amountString.contains(MiBancoConstants.CURRENCY_SYMBOL) ? amountString : Utils.concatenateStrings(new String[]{MiBancoConstants.CURRENCY_SYMBOL,amountString}));
        }

        Utils.showDialog(dialog, Payments.this);

        // show alert on the top
        if (!originalDate.equals(effectiveDateString)) {
            Utils.showAlert(Payments.this, getString(R.string.transaction_confirm_effective_date_title), String.format(getString(R.string.transaction_confirm_effective_date_message), tempDateString));
        }

        if (overpay) {
            final DialogHolo dialogOverpay = new DialogHolo(Payments.this);
            dialogOverpay.setTitle(R.string.transaction_overpay_title);
            dialogOverpay.setMessage(R.string.transaction_overpay_message);
            dialogOverpay.setCancelable(true);
            dialogOverpay.setConfirmationButton(getString(R.string.ok), new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    Utils.dismissDialog(dialogOverpay);
                }
            });
            Utils.showDialog(dialogOverpay, Payments.this);

            BPAnalytics.logEvent(BPAnalytics.EVENT_PAYMENT_OVERPAY_PROMPT_PRESENTED);
        }
    }

    /**
     * Transfer processor.
     *
     * @param transfer the transfer to process
     */
    private void transferProcessor(final TransferActive transfer) {
        final App application = (App) getApplication();

        if (application.getTransfersInfo() == null || transfer.getContent() == null) {
            onTransactionError(getString(R.string.transaction_failed_title), getString(R.string.transaction_failed_message), null);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(application.getTransfersInfo().getDateFormat());
        if (transfer.getResponderName().equals("makeTransfer") && transfer.getResponderMessage().equals("transfer_confirmation")) {

            if (transfer.getFlags() == null) {
                onTransactionError(getString(R.string.transaction_failed_title), getString(R.string.transaction_failed_message), null);
                return;
            }

            // transfer has failed
            if (transfer.isFailTransfers()) {
                if (transfer.isDowntime()) {
                    Utils.showMaintenanceError(Payments.this, getString(R.string.maintenance_personal_banking));
                    return;
                }
                if (transfer.getFailTransfers().size() > 0) {
                    final TransferActiveTransfer failedTransfer = transfer.getFailTransfers().get(0);
                    if (failedTransfer != null && !TextUtils.isEmpty(failedTransfer.getError())) {
                        final SimpleDateFormat sdfLocal = new SimpleDateFormat(application.getDateFormat());
                        String dateLocal = failedTransfer.getEffectiveDate();
                        try {
                            dateLocal = sdfLocal.format(sdf.parse(failedTransfer.getEffectiveDate()));
                        } catch (final ParseException e) {
                            Log.e("Payments", "Error parsing effective date.");
                            onTransactionError(getString(R.string.transaction_failed_title), getString(R.string.transaction_failed_message), "Error parsing effective date.");
                            return;
                        }

                        application.getAsyncTasksManager().updateBalances(Payments.this);

                        final TransferActiveAccount fromAccountInformation = failedTransfer.getFromAccount();
                        final TransferActiveAccount toAccountInformation = failedTransfer.getToAccount();
                        final Intent intent = new Intent(Payments.this, PaymentReceipt.class);
                        intent.putExtra(MiBancoConstants.ERROR_MESSAGE_KEY, failedTransfer.getError());
                        intent.putExtra("transfers", isTransfer);
                        intent.putExtra("referenceNr", failedTransfer.getReferenceNumber());
                        intent.putExtra("fromName", fromAccountInformation.getNickname());
                        intent.putExtra("fromNr", fromAccountInformation.getAccountNumber());
                        intent.putExtra("toName", toAccountInformation.getNickname());
                        intent.putExtra("toNr", toAccountInformation.getAccountNumber());
                        intent.putExtra("amount", failedTransfer.getAmount());
                        intent.putExtra("date", dateLocal);

                        BPAnalytics.logEvent(BPAnalytics.EVENT_TRANSACTION_FAILED, "transactionType", getString(R.string.transfer_noun), "error", getString(R.string.transfer_failed_title),
                                "description", failedTransfer.getError());
                        Payments.this.startActivityForResult(intent, RECEIPT_REQUEST_CODE);
                        goToReceipt = true;
                        return;
                    } else {
                        onTransactionError(getString(R.string.transfer_failed_title), getString(R.string.transfer_failed_message), null);
                        return;
                    }
                } else {
                    onTransactionError(getString(R.string.transfer_failed_title), getString(R.string.transfer_failed_message), null);
                    return;
                }
            }

            if (transfer.getConfirmedTransfers() == null || transfer.getConfirmedTransfers().size() == 0) {
                onTransactionError(getString(R.string.transfer_failed_title), getString(R.string.transfer_failed_message), null);
                return;
            }

            final TransferActiveTransfer confirmedTransfer = transfer.getConfirmedTransfers().get(0);
            final TransferActiveAccount fromAccountInformation = confirmedTransfer.getFromAccount();
            final TransferActiveAccount toAccountInformation = confirmedTransfer.getToAccount();

            if (fromAccountInformation == null || toAccountInformation == null) {
                onTransactionError(getString(R.string.transfer_failed_title), getString(R.string.transfer_failed_message), null);
                return;
            }

            // everything seems to be ok, go to receipt screen
            final SimpleDateFormat sdfLocal = new SimpleDateFormat(application.getDateFormat());
            String dateLocal = confirmedTransfer.getEffectiveDate();
            try {
                dateLocal = sdfLocal.format(sdf.parse(confirmedTransfer.getEffectiveDate()));
            } catch (final ParseException e) {
                Log.e("Payments", "Error parsing effective date.");
                onTransactionError(getString(R.string.transaction_failed_title), getString(R.string.transaction_failed_message), "Error parsing effective date.");
                return;
            }

            application.getAsyncTasksManager().updateBalances(Payments.this);

            final Intent intent = new Intent(Payments.this, PaymentReceipt.class);
            intent.putExtra("transfers", isTransfer);
            intent.putExtra("referenceNr", confirmedTransfer.getReferenceNumber());
            intent.putExtra("fromName", fromAccountInformation.getNickname());
            intent.putExtra("fromNr", fromAccountInformation.getAccountNumber());
            intent.putExtra("toName", toAccountInformation.getNickname());
            intent.putExtra("toNr", toAccountInformation.getAccountNumber());
            intent.putExtra("amount", confirmedTransfer.getAmount());
            intent.putExtra("date", dateLocal);

            BPAnalytics.logEvent(BPAnalytics.EVENT_TRANSFER_SUCCESSFUL);

            Payments.this.startActivityForResult(intent, RECEIPT_REQUEST_CODE);
            goToReceipt = true;
        } else if (transfer.getResponderName().equals("makeTransfer") && transfer.getResponderMessage().equals("transfer_verification")) {
            if (transfer.getTransfers() == null || transfer.getTransfers().size() == 0) {
                onTransactionError(getString(R.string.transfer_failed_title), getString(R.string.transfer_failed_message), null);
                return;
            }

            final TransferActiveTransfer verifyTransfer = transfer.getTransfers().get(0);
            final TransferActiveAccount fromAccountInformation = verifyTransfer.getFromAccount();
            final TransferActiveAccount toAccountInformation = verifyTransfer.getToAccount();

            if (fromAccountInformation == null || toAccountInformation == null || verifyTransfer.getEffectiveDate() == null || verifyTransfer.getAmount() == null) {
                onTransactionError(getString(R.string.transfer_failed_title), getString(R.string.transfer_failed_message), null);
                return;
            }

            final DialogHolo dialog = new DialogHolo(Payments.this);
            dialog.setTitle(getString(R.string.confirm_your_transfer));
            final View dialogView = dialog.setCustomContentView(R.layout.dialog_transfer);
            dialog.setCancelable(true);
            dialog.setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    Utils.dismissDialog(dialog);
                }
            });

            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MiBancoConstants.ENGLISH_DATE_FORMAT);
            String currentDateString = simpleDateFormat.format(new Date());
            String confirmButtonTitle;
            if (currentDateString.equals(verifyTransfer.getEffectiveDate())) {
                confirmButtonTitle = getString(R.string.transfer_now);
            } else {
                confirmButtonTitle = getString(R.string.transfer_future) + verifyTransfer.getEffectiveDate();
            }

            dialog.setPositiveButton(confirmButtonTitle, new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    // confirm the transfer
                    application.getAsyncTasksManager().makeTransfer(Payments.this, itemFrom.getId(), itemTo.getId(), Utils.formatAmountForWs(amount), verifyTransfer.getEffectiveDate(), "variable",
                            false, new ResponderListener() {

                                @Override
                                public void responder(final String responderName, final Object data) {
                                    Utils.dismissDialog(dialog);
                                    transferProcessor((TransferActive) data);
                                }

                                @Override
                                public void sessionHasExpired() {
                                    application.reLogin(Payments.this);
                                }
                            });
                }
            });
            Utils.showDialog(dialog, Payments.this);

            // set dialog's text boxes
            ((TextView) dialogView.findViewById(R.id.pay_amount)).setText(verifyTransfer.getAmount());

            Factory factory = Spannable.Factory.getInstance();
            Spannable fromAccountNickname = factory.newSpannable(fromAccountInformation.getNickname() + " ");
            Spannable fromAccountCode = factory.newSpannable(fromAccountInformation.getAccountNumber());
            fromAccountNickname.setSpan(new StyleSpan(Typeface.BOLD), 0, fromAccountNickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            fromAccountNickname.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)), 0, fromAccountNickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            fromAccountCode.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.grey_dark)), 0, fromAccountCode.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannedString fromAccountTitle = (SpannedString) TextUtils.concat(fromAccountNickname, fromAccountCode);
            ((TextView) dialogView.findViewById(R.id.from_nickname)).setText(fromAccountTitle);

            Spannable toAccountNickname = factory.newSpannable(toAccountInformation.getNickname() + " ");
            Spannable toAccountCode = factory.newSpannable(toAccountInformation.getAccountNumber());
            toAccountNickname.setSpan(new StyleSpan(Typeface.BOLD), 0, toAccountNickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            toAccountNickname.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)), 0, toAccountNickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            toAccountCode.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.grey_dark)), 0, toAccountCode.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannedString toAccountTitle = (SpannedString) TextUtils.concat(toAccountNickname, toAccountCode);
            ((TextView) dialogView.findViewById(R.id.to_nickname)).setText(toAccountTitle);

            sdf = new SimpleDateFormat(transfer.getDateFormat());
            final String originalDate = sdf.format(selectedDate);
            String tempDateString = DateFormat.getLongDateFormat(getApplicationContext()).format(selectedDate);

            if (!originalDate.equals(verifyTransfer.getEffectiveDate())) {
                Date tempDate;
                try {
                    tempDate = sdf.parse(verifyTransfer.getEffectiveDate());
                    tempDateString = DateFormat.getLongDateFormat(getApplicationContext()).format(tempDate);
                } catch (final ParseException e) {
                    Log.e("Payments", "Error parsing effective date.");
                    onTransactionError(getString(R.string.transaction_failed_title), getString(R.string.transaction_failed_message), "Error parsing effective date.");
                    return;
                }
                selectDate(tempDate);
            }
            ((TextView) dialogView.findViewById(R.id.date)).setText(tempDateString);
        } else if (transfer.getResponderName().equals("makeTransfer") && transfer.getResponderMessage().equals("transfer_information")) {
            onTransactionError(getString(R.string.transfer_failed_title), getString(R.string.transfer_failed_message), null);
        } else {
            onTransactionError(getString(R.string.transfer_failed_title), getString(R.string.transfer_failed_message), null);
        }
    }

    /**
     * Validates transaction accounts.
     *
     * @return true, if accounts are valid
     */
    private boolean validateTransactionAccounts() {
        if (itemFrom == null || itemTo == null) {
            return false;
        }

        if (itemFrom.getId().equals(itemTo.getId())) {
            return false;
        }

        return !itemFrom.getTitle().equals(itemTo.getTitle());

    }

    /**
     * Validates transaction amount.
     *
     * @return true, if amount is valid
     */
    private boolean validateTransactionAmount() {
        return amount > 0;

    }

    /**
     * Validates transaction "from" account for a zero or negative balance.
     *
     * @return true, if "from" account's balance is positive
     */
    private boolean validateTransactionFromAccountBalance() {
        /*
         * This validation is only for "real time" transfers. For transfers in the future, negative or zero amounts are ok. Since they person may have
         * prefunded the money.
         */
        if (effectiveDate != null && selectedDate != null) {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.setTimeInMillis(selectedDate.getTime());

            Calendar effectiveCalendar = Calendar.getInstance();
            effectiveCalendar.setTimeInMillis(effectiveDate.getTime());

            if (selectedCalendar.get(Calendar.YEAR) > effectiveCalendar.get(Calendar.YEAR)) {
                return true;
            }

            if (selectedCalendar.get(Calendar.YEAR) == effectiveCalendar.get(Calendar.YEAR)) {
                if (selectedCalendar.get(Calendar.DAY_OF_YEAR) > effectiveCalendar.get(Calendar.DAY_OF_YEAR)) {
                    return true;
                }
            }
        }

        String accountFromBalanceString = itemFrom.getAmount().trim();
        if (accountFromBalanceString.startsWith("(") && accountFromBalanceString.endsWith(")")) {
            return false;
        }

        String cleanBalanceString = App.cleanBalanceString(accountFromBalanceString.replace(MiBancoConstants.CURRENCY_SYMBOL, ""));

        /* If empty string this must be a PIF account which the system does not provide a balance. */
        if (TextUtils.isEmpty(cleanBalanceString)) {
            return true;
        }

        int accountFromBalance = App.getIntAmount(cleanBalanceString);
        return accountFromBalance > 0;

    }

    public static boolean isPaymentsDestroyed() {
        return isPaymentsDestroyed;
    }

    public static void setPaymentsDestroyed(boolean isPaymentsDestroyed) {
        Payments.isPaymentsDestroyed = isPaymentsDestroyed;
    }

    public static boolean isTransfersDestroyed() {
        return isTransfersDestroyed;
    }

    public static void setTransfersDestroyed(boolean isTransfersDestroyed) {
        Payments.isTransfersDestroyed = isTransfersDestroyed;
    }

    private View.OnClickListener lastPaymentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String amount = itemTo.getLastPayment();
            amount = amount.replace(".", "")
                    .replace(MiBancoConstants.CURRENCY_SYMBOL, "");
            setAmount(Integer.parseInt(amount));
        }
    };

    private void fadeLastPayment(boolean fadeIn) {
        Animation fade;
        if (fadeIn) {
            fade = new AlphaAnimation(0, 1);
            fade.setInterpolator(new DecelerateInterpolator());
            fade.setDuration(250);
            fade.setStartOffset(850);
        } else {
            fade = new AlphaAnimation(1,0);
            if(btnLastAmount.getVisibility() == View.VISIBLE) {
                fade.setDuration(250);
                fade.setStartOffset(0);
            } else
                return;
        }
        fade.setInterpolator(new DecelerateInterpolator());
        AnimationSet anim = new AnimationSet(false);
        anim.addAnimation(fade);
        btnLastAmount.setAnimation(anim);
    }

    private void openAddPayeesWebView (){
        final Intent intentWebView = new Intent(Payments.this, WebViewActivity.class);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.add_payee_url)));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, Utils.urlBlacklist(Payments.this));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_TOOLBAR_HIDE_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_PROGRESSBAR_HIDE_KEY, true);
        BPAnalytics.logEvent(BPAnalytics.EVENT_ADD_PAYEES_SECTION);
        startActivityForResult(intentWebView, MiBancoConstants.ADD_PAYEES_REQUEST_CODE);
    }

    private void openEditPayeesWebView (){
        final Intent intentWebView = new Intent(Payments.this, WebViewActivity.class);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.edit_payee_url)));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, Utils.urlBlacklist(Payments.this));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_TOOLBAR_HIDE_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_PROGRESSBAR_HIDE_KEY, true);
        BPAnalytics.logEvent(BPAnalytics.EVENT_EDIT_PAYEES_SECTION);
        startActivityForResult(intentWebView, MiBancoConstants.EDIT_PAYEES_REQUEST_CODE);
    }
}