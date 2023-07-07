package com.popular.android.mibanco.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.activity.CalendarView;
import com.popular.android.mibanco.activity.EBill;
import com.popular.android.mibanco.activity.EnterAmount;
import com.popular.android.mibanco.activity.PaymentReceipt;
import com.popular.android.mibanco.activity.Payments;
import com.popular.android.mibanco.activity.Receipts;
import com.popular.android.mibanco.activity.WebViewActivity;
import com.popular.android.mibanco.animation.FadeViewAnimation;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.listener.SimpleListener;
import com.popular.android.mibanco.model.FormField;
import com.popular.android.mibanco.model.Payment;
import com.popular.android.mibanco.model.PaymentActive;
import com.popular.android.mibanco.model.Transfer;
import com.popular.android.mibanco.model.TransferActive;
import com.popular.android.mibanco.model.TransferActiveAccount;
import com.popular.android.mibanco.model.TransferActiveTransfer;
import com.popular.android.mibanco.util.AmountEditor;
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

public class PaymentsTransfersFragment extends Fragment {

    private View rootView;

    private Context mContext;
    /**
     * Constant that define the duration animation for real time views indicatotr
     */
    public final static long RT_ANIMATION_DURATION = 300;

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
    private final static char VALID_REAL_TIME = 'Y';

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

    private Date selectedDate;
    private Date effectiveDate;
    private Date initialDateRealTime;
    private Date initialDate;

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
    private Button btnLastAmount;

    /**
     * Text for Expand Real Time Payee
     */
    private TextView seeMoreTextView;

    /**
     * View for Expand Real Time Payee
     */
    private LinearLayout seeMoreLinearLayout;

    /**
     * Detail Text for Expand Real Time Payee
     */
    private TextView detailtextView;

    /**
     * Value that indicate if Expand Real Time Payee
     */
    private Boolean isVisibleTextSeeMore = false;


    private LinearLayout lyAccountFrom;
    private LinearLayout lyAccountTo;
    private LinearLayout warningTransfers;

    private LinearLayout lyAmount;
    private Button btnChange;
    private BottomSheetDialog bottomSheetAccountFrom;
    private BottomSheetDialog bottomSheetAccountTo;

    AmountEditor amountEditor;

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        if (resultCode == Activity.RESULT_OK) {
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
        }else if (resultCode == Activity.RESULT_CANCELED) {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_payments, container, false);
        App.getApplicationInstance().setActivityContext(mContext);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(App.getApplicationInstance() != null && App.getApplicationInstance().getAsyncTasksManager() != null) {
            Utils.setupLanguage(getContext());

            initBottonSheetAccountFrom();

            initBottonSheetAccountTo();

            initBottonSheetAmountEditor();

            isTransfer = getArguments().getBoolean("transfers", false);
            amountTextView = view.findViewById(R.id.amount_text);
            dateTextView = view.findViewById(R.id.date_text);
            btnLastAmount = view.findViewById(R.id.last_ammount_btn);
            seeMoreTextView = view.findViewById(R.id.textViewSeeMore);
            detailtextView = view.findViewById(R.id.textViewDetail);
            seeMoreLinearLayout = view.findViewById(R.id.linearLayoutSeeMore);

            if (FeatureFlags.MBMT_477()) {
                btnLastAmount.setVisibility(View.GONE);
                btnLastAmount.setOnClickListener(lastPaymentListener);
            }

            final String selectedDateValue = "selectedDate";

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

            //cambios nuevos
            lyAccountFrom = view.findViewById(R.id.lyAccountFrom);
            lyAccountTo = view.findViewById(R.id.lyAccountTo);
            lyAmount = view.findViewById(R.id.lyAmount);

            warningTransfers = view.findViewById(R.id.warningTransfers);
            if (isTransfer) {
                warningTransfers.setVisibility(View.VISIBLE);
            } else {
                warningTransfers.setVisibility(View.GONE);
            }

            btnChange = view.findViewById(R.id.btnChange);

            setListeners();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    public void handleNewIntent(boolean isTransfer) {
        positionFrom = 0;
        positionTo = 0;
        amount = 0;
        selectedDate = null;
        setAmount(0);

        this.isTransfer = isTransfer;

        if (isTransfer) {
            setTransfersDestroyed(false);
            fetchTransfers(true, false, null);
        } else {
            setPaymentsDestroyed(false);
            fetchPayments(true, false, null);
        }

        setListeners();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(App.getApplicationInstance() != null && App.getApplicationInstance().getAsyncTasksManager() != null) {
            if (App.getApplicationInstance().isUpdatingBalances()) {
                Utils.dismissDialog(App.getApplicationInstance().getDialogCoverupUpdateBalances());
                App.getApplicationInstance().setDialogCoverupUpdateBalances(new DialogCoverup(getActivity()));
                App.getApplicationInstance().getDialogCoverupUpdateBalances().setProgressCaption(R.string.refreshing_balances);
                Utils.showDialog(App.getApplicationInstance().getDialogCoverupUpdateBalances(), getActivity());
            }

            if (isTransfer) {
                if (App.getApplicationInstance().isReloadTransfers()) {
                    fetchTransfers(true, true, null);
                    App.getApplicationInstance().setReloadTransfers(false);
                } else if (App.getApplicationInstance().isRefreshTransfersCardImages()) {
                    fetchTransfers(true, false, null);
                    App.getApplicationInstance().setRefreshTransfersCardImages(false);
                } else if (App.getApplicationInstance().isUpdatingBalances()) {
                    App.getApplicationInstance().setReloadPayments(true);
                    fetchTransfers(false, true, new SimpleListener() {

                        @Override
                        public void done() {
                            App.getApplicationInstance().setUpdatingBalances(false);
                            Utils.dismissDialog(App.getApplicationInstance().getDialogCoverupUpdateBalances());
                            App.getApplicationInstance().setDialogCoverupUpdateBalances(null);

                            final SharedPreferences sharedPreferences = Utils.getSecuredSharedPreferences(App.getApplicationInstance());
                            final SharedPreferences.Editor editor = sharedPreferences.edit();
                            App.getApplicationInstance().getAsyncTasksManager().updateWidgetBalances(getActivity(), editor);
                        }
                    });
                }
            } else {
                if (App.getApplicationInstance().isReloadPayments()) {
                    fetchPayments(true, true, null);
                    App.getApplicationInstance().setReloadPayments(false);
                } else if (App.getApplicationInstance().isRefreshPaymentsCardImages()) {
                    fetchPayments(true, false, null);
                    App.getApplicationInstance().setRefreshPaymentsCardImages(false);
                } else if (App.getApplicationInstance().isUpdatingBalances()) {
                    App.getApplicationInstance().setReloadTransfers(true);
                    fetchPayments(false, true, new SimpleListener() {

                        @Override
                        public void done() {
                            App.getApplicationInstance().setUpdatingBalances(false);
                            Utils.dismissDialog(App.getApplicationInstance().getDialogCoverupUpdateBalances());
                            App.getApplicationInstance().setDialogCoverupUpdateBalances(null);

                            if (FeatureFlags.MBMT_511()) {
                                final SharedPreferences sharedPreferences = Utils.getSecuredSharedPreferences(App.getApplicationInstance());
                                final SharedPreferences.Editor editor = sharedPreferences.edit();
                                App.getApplicationInstance().getAsyncTasksManager().updateWidgetBalances(getActivity(), editor);
                            }
                        }
                    });
                }
            }

            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(isTransfer){
            menu.findItem(R.id.menu_add_payee).setVisible(false);
            menu.findItem(R.id.menu_edit_payee).setVisible(false);
            menu.findItem(R.id.menu_logout).setVisible(false);
            menu.findItem(R.id.menu_transfers_history).setVisible(true);

        }else if (FeatureFlags.ADD_PAYEES() && Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            menu.findItem(R.id.menu_add_payee).setVisible(true);
            menu.findItem(R.id.menu_edit_payee).setVisible(true);
            menu.findItem(R.id.menu_payments_history).setVisible(true);
            menu.findItem(R.id.menu_logout).setVisible(false);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_ebills:
                Intent iEBills = new Intent(getActivity(), EBill.class);
                startActivityForResult(iEBills, Payments.EBILL_REQUEST_CODE);
                return true;
            case R.id.menu_add_payee:
                openAddPayeesWebView();
                return true;
            case R.id.menu_edit_payee:
                openEditPayeesWebView();
                return true;
            case R.id.menu_payments_history:
                Intent history = new Intent(getActivity(), Receipts.class);
                history.putExtra(Receipts.RECEIPT_TYPE, Receipts.HistoryType.PAYMENT);
                getContext().startActivity(history);
                return true;
            case R.id.menu_transfers_history:
                Intent transfersHistory = new Intent(getActivity(), Receipts.class);
                transfersHistory.putExtra(Receipts.RECEIPT_TYPE, Receipts.HistoryType.TRANSFER);
                getContext().startActivity(transfersHistory);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        BPAnalytics.onStartSession(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        BPAnalytics.onEndSession(getActivity());
    }

    @Override
    public void onDestroy() {
        if (isTransfer) {
            setTransfersDestroyed(true);
        } else {
            setPaymentsDestroyed(true);
        }
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        final String selectedDateValue = "selectedDate";

        if (selectedDate != null) {
            outState.putLong(selectedDateValue, selectedDate.getTime());
        }

        outState.putInt("positionFrom", positionFrom);
        outState.putInt("positionTo", positionTo);
        outState.putBoolean("isTransfer", isTransfer);
        outState.putInt("amount", amount);
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
        if(App.getApplicationInstance() != null && App.getApplicationInstance().getAsyncTasksManager() != null) {
            App.getApplicationInstance().getAsyncTasksManager().fetchPayments(getContext(), new ResponderListener() {

                @Override
                public void responder(final String responderName, final Object data) {
                    int message = R.string.no_payees;
                    if (FeatureFlags.ADD_PAYEES()) {
                        message = R.string.no_payees_add_new_payees;
                        toList.invalidateWheel(true);
                        toList.setViewAdapter(new ArrayBankWheelAdapter<>(getActivity(), new ArrayList<ArrayBankWheelItem>()));
                    }
                    if (Utils.showNoPayeesDialog(getActivity(), message, !FeatureFlags.ADD_PAYEES(), new SimpleListener() {
                        @Override
                        public void done() {
                            if (FeatureFlags.ADD_PAYEES()) {
                                //openAddPayeesWebView();
                                fromList.setEnabled(false);
                                toList.setEnabled(false);
                            }
                        }
                    })) { return; }

                    final List<ArrayBankWheelItem> outItemsFrom = new ArrayList<>();
                    final List<ArrayBankWheelItem> outItemsTo = new ArrayList<>();

                    if (App.getApplicationInstance().getValidEbills() != null && App.getApplicationInstance().getValidEbills().size() > 0) {
                        fromList.post(new Runnable() {

                            @Override
                            public void run() {
                                getActivity().invalidateOptionsMenu();
                            }
                        });
                    }

                    App.getApplicationInstance().getAsyncTasksManager().loadPaymentsCards(getActivity(), new SimpleListener() {

                        @Override
                        public void done() {
                            final int tempFromPosition = positionFrom;
                            final int tempToPosition = positionTo;

                            adapterFrom = new ArrayBankWheelAdapter<>(getActivity(), R.layout.wheel_item_fragment, outItemsFrom);
                            if (outItemsFrom.size() > 0 && positionFrom < outItemsFrom.size()) {
                                itemFrom = outItemsFrom.get(positionFrom);
                            }
                            fromList.setClickable(true);
                            fromList.setViewAdapter(adapterFrom);
                            fromList.setCD(false);
                            fromList.setCyclic(false);

                            adapterTo = new ArrayBankWheelAdapter<>(getActivity(),R.layout.wheel_item_fragment, outItemsTo);
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

                            final Payment paymentInstance = KiuwanUtils.checkBeforeCast(Payment.class, data);
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
                    App.getApplicationInstance().reLogin(getActivity());
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

        if(App.getApplicationInstance() != null && App.getApplicationInstance().getAsyncTasksManager() != null) {
            (App.getApplicationInstance()).getAsyncTasksManager().fetchTransfers(getContext(), new ResponderListener() {

                @Override
                public void responder(final String responderName, final Object data) {
                    final List<ArrayBankWheelItem> outItemsFrom = new ArrayList<ArrayBankWheelItem>();
                    final List<ArrayBankWheelItem> outItemsTo = new ArrayList<ArrayBankWheelItem>();

                    fromList.post(new Runnable() {

                        @Override
                        public void run() {
                            getActivity().invalidateOptionsMenu();
                        }
                    });

                    App.getApplicationInstance().getAsyncTasksManager().loadTransfersCards(getActivity(), new SimpleListener() {

                        @Override
                        public void done() {
                            adapterFrom = new ArrayBankWheelAdapter<>(getActivity(), R.layout.wheel_item_fragment, outItemsFrom);
                            if (outItemsFrom.size() > 0 && positionFrom < outItemsFrom.size()) {
                                itemFrom = outItemsFrom.get(positionFrom);
                            }
                            fromList.setClickable(true);
                            fromList.setViewAdapter(adapterFrom);
                            fromList.setCD(false);
                            fromList.setCyclic(false);

                            adapterTo = new ArrayBankWheelAdapter<>(getActivity(), R.layout.wheel_item_fragment, outItemsTo);
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
                    App.getApplicationInstance().reLogin(getActivity());
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

        Utils.showAlert(getActivity(), title, description, new SimpleListener() {

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
        if(App.getApplicationInstance() != null && App.getApplicationInstance().getAsyncTasksManager() != null) {
            final App app = (App) App.getApplicationInstance();
            final SimpleDateFormat sdf = new SimpleDateFormat(MiBancoConstants.WEBSERVICE_DATE_FORMAT);

            if (payment.getContent() == null) {
                onTransactionError(getString(R.string.transaction_failed_title), getString(R.string.transaction_failed_message), null);
                return;
            }

            if (payment.getResponderName().equals("quickpayment") && payment.getResponderMessage().equals("quickpayment_confirmation")) {
                if (!payment.isPaymentSent()) {
                    if (payment.isDowntime()) {
                        Utils.showMaintenanceError(getActivity(), getString(R.string.maintenance_personal_banking));
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

                        App.getApplicationInstance().getAsyncTasksManager().updateBalances(getActivity());

                        final Intent intent = new Intent(getActivity(), PaymentReceipt.class);
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
                        startActivityForResult(intent, RECEIPT_REQUEST_CODE);
                        goToReceipt = true;
                    } else {
                        onTransactionError(getString(R.string.payment_failed_title), getString(R.string.payment_failed_message), null);
                    }
                } else {
                    final SimpleDateFormat sdfLocal = new SimpleDateFormat(app.getDateFormat());
                    String dateLocal = payment.getEffectiveDate();
                    try {
                        dateLocal = sdfLocal.format(sdf.parse(payment.getEffectiveDate()));
                    } catch (final ParseException e) {

                        Log.e("Payments", "Error parsing effective date.");
                        onTransactionError(getString(R.string.transaction_failed_title), getString(R.string.transaction_failed_message), "Error parsing effective date.");
                        return;
                    }

                    App.getApplicationInstance().getAsyncTasksManager().updateBalances(getActivity());

                    final Intent intent = new Intent(getActivity(), PaymentReceipt.class);
                    intent.putExtra("transfers", isTransfer);
                    intent.putExtra("referenceNr", payment.getReferenceNumber());
                    intent.putExtra("fromName", payment.getSourceAccountNickname());
                    intent.putExtra("fromNr", payment.getSourceAccountNumber());
                    intent.putExtra("toName", payment.getPayeeNickname());
                    intent.putExtra("toNr", payment.getPayeeBillingAccount());
                    intent.putExtra("amount", payment.getAmount());
                    intent.putExtra("date", dateLocal);

                    BPAnalytics.logEvent(BPAnalytics.EVENT_PAYMENT_SUCCESSFUL);

                    startActivityForResult(intent, RECEIPT_REQUEST_CODE);
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
                    showPaymentConfirmation(MiBancoConstants.WEBSERVICE_DATE_FORMAT, sdf.format(selectedDate), Utils.formatAmountForWs(amount), itemFrom.getName(), itemFrom.getCode(), itemTo.getName(),
                            itemTo.getCode(), true);
                } else {
                    if (quickpaymentAmountError.contains(getString(R.string.transaction_error_larger_than_max))) {
                       onTransactionError(getString(R.string.payment_failed_title), getString(R.string.transaction_error_larger_than_max) + ".", quickpaymentAmountError);
                    } else {
                        onTransactionError(getString(R.string.payment_failed_title), getString(R.string.payment_failed_message), quickpaymentAmountError);
                    }
                }
            } else {
                final DialogHolo dialog = new DialogHolo(getActivity());
                dialog.setTitle(getString(R.string.payment_failed_title));
                dialog.setMessage(getString(R.string.transaction_unknown_error));
                dialog.setConfirmationButton("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        Utils.dismissDialog(dialog);
                        App.getApplicationInstance().reLogin(getActivity());
                    }
                });
                Utils.showDialog(dialog, getActivity());
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
        dateTextView.setText(DateFormat.getLongDateFormat(getContext()).format(date));
    }

    public void setGoToReceipt(final boolean receipt) {
        goToReceipt = receipt;
    }

    /**
     * Sets the listeners.
     */
    private void setListeners() {
        rootView.findViewById(R.id.amount_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Intent intent = new Intent(getActivity(), EnterAmount.class);
                intent.putExtra("amount", amount);
                startActivityForResult(intent, AMOUNT_PICKER_REQUEST_CODE);
            }
        });

        rootView.findViewById(R.id.calendar_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (effectiveDate != null) {
                    final Intent intent = new Intent(v.getContext(), CalendarView.class);
                    final String startTimeParam = "startTime";
                    final String initialDateParam = "initialDate";
                    final String rtNotificationParam = "rtNotification";
                    final long initialDateValue = KiuwanUtils.compareStringChar(itemTo.getRtNotification(), VALID_REAL_TIME)
                            ? initialDateRealTime.getTime() : initialDate.getTime();

                    intent.putExtra(startTimeParam, effectiveDate.getTime());
                    intent.putExtra(initialDateParam, initialDateValue);
                    intent.putExtra(rtNotificationParam, itemTo.getRtNotification());
                    startActivityForResult(intent, PICK_DATE_REQUEST_CODE);
                }
            }
        });

        final Button payButton = (Button) rootView.findViewById(R.id.pay_button);

        if (isTransfer) {
            ((TextView) rootView.findViewById(R.id.schedule_text)).setText(R.string.schedule_transfer);
            payButton.setText(R.string.transfer_verb_button);
        } else {
            ((TextView) rootView.findViewById(R.id.schedule_text)).setText(R.string.schedule_payment);
            payButton.setText(R.string.payment_verb);
        }

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final App application = App.getApplicationInstance();

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

                    final SimpleDateFormat sdf = new SimpleDateFormat(application.getTransfersInfo().getDateFormat());
                    application.getAsyncTasksManager().makeTransfer(getActivity(), itemFrom.getId(), itemTo.getId(), Utils.formatAmountForWs(amount), sdf.format(selectedDate), "variable", true,
                            new ResponderListener() {

                                @Override
                                public void responder(final String responderName, final Object data) {
                                    transferProcessor((TransferActive) data);
                                }

                                @Override
                                public void sessionHasExpired() {
                                    application.reLogin(getActivity());
                                }
                            });
                } else {
                    final SimpleDateFormat sdf = new SimpleDateFormat(MiBancoConstants.WEBSERVICE_DATE_FORMAT);
                    application.getAsyncTasksManager().quickPayment(getActivity(), itemFrom.getId(), Utils.formatAmountForWs(amount), sdf.format(selectedDate), itemTo.getId(), true,
                            new ResponderListener() {

                                @Override
                                public void responder(final String responderName, final Object data) {
                                    paymentProcessor((PaymentActive) data);
                                }

                                @Override
                                public void sessionHasExpired() {
                                    application.reLogin(getActivity());
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

        lyAccountFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialogAccountFrom();
            }
        });

        lyAccountTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialogAccountTo();
            }
        });

        lyAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialogAmountEditor();
            }
        });
    }

    /**
     * validateRealTimePayee
     */
    private void validateRealTimePayee(){
        Date resetCalendarToDate = null;
        final String validValue = "true";

        if (KiuwanUtils.compareStringChar(itemTo.getRtNotification(), VALID_REAL_TIME)) {
            resetCalendarToDate = initialDateRealTime;


            displayDetailRealTimeNotification(validValue.equals(itemTo.getRtHasPaymentHistory()));

        } else {
            resetCalendarToDate = initialDate;
        }

        if (resetCalendarToDate != null) {
            selectDate(resetCalendarToDate);
            effectiveDate = resetCalendarToDate;
        }
    }

    private void displayDetailRealTimeNotification(boolean show){
        seeMoreLinearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        seeMoreTextView.setText(show ? getContext().getString(R.string.pay_see_more) : StringUtils.EMPTY);
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
        final DialogHolo dialog = new DialogHolo(getActivity());
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
                App.getApplicationInstance().getAsyncTasksManager().quickPayment(getActivity(), itemFrom.getId(), Utils.formatAmountForWs(amount), effectiveDateString, itemTo.getId(), false, new ResponderListener() {

                    @Override
                    public void responder(final String responderName, final Object data) {
                        Utils.dismissDialog(dialog);
                        paymentProcessor((PaymentActive) data);
                    }

                    @Override
                    public void sessionHasExpired() {
                        App.getApplicationInstance().reLogin(getActivity());
                    }
                });
            }
        });

        ((TextView) dialogView.findViewById(R.id.pay_amount)).setText(amountString);

        Spannable.Factory factory = Spannable.Factory.getInstance();
        Spannable fromAccountNickname = factory.newSpannable(sourceAccountNickname + " ");
        Spannable fromAccountCode = factory.newSpannable(sourceAccountNumber);
        fromAccountNickname.setSpan(new StyleSpan(Typeface.BOLD), 0, fromAccountNickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        fromAccountNickname.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.black)), 0, fromAccountNickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        fromAccountCode.setSpan(new StyleSpan(Typeface.NORMAL), 0, fromAccountCode.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        fromAccountCode.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.grey_dark)), 0, fromAccountCode.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannedString fromAccountTitle = (SpannedString) TextUtils.concat(fromAccountNickname, fromAccountCode);
        ((TextView) dialogView.findViewById(R.id.from_nickname)).setText(fromAccountTitle);

        Spannable toAccountNickname = factory.newSpannable(payeeNickname + " ");
        Spannable toAccountCode = factory.newSpannable(payeeBillingAccount);
        toAccountNickname.setSpan(new StyleSpan(Typeface.BOLD), 0, toAccountNickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        toAccountNickname.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.black)), 0, toAccountNickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        toAccountCode.setSpan(new StyleSpan(Typeface.NORMAL), 0, toAccountCode.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        toAccountCode.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.grey_dark)), 0, toAccountCode.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannedString toAccountTitle = (SpannedString) TextUtils.concat(toAccountNickname, toAccountCode);
        ((TextView) dialogView.findViewById(R.id.to_nickname)).setText(toAccountTitle);

        final SimpleDateFormat sdf = new SimpleDateFormat(effectiveDateFormat);
        final String originalDate = sdf.format(selectedDate);
        String tempDateString = DateFormat.getLongDateFormat(getContext()).format(selectedDate);

        if (!originalDate.equals(effectiveDateString)) {
            Date tempDate;
            try {
                tempDate = sdf.parse(effectiveDateString);
                tempDateString = DateFormat.getLongDateFormat(getContext()).format(tempDate);
            } catch (final ParseException e) {
                Log.e("Payments", "Error parsing effective date.");
                onTransactionError(getString(R.string.transaction_failed_title), getString(R.string.transaction_failed_message), "Error parsing effective date.");
                return;
            }
            Utils.showAlert(getActivity(), getString(R.string.transaction_confirm_effective_date_title), String.format(getString(R.string.transaction_confirm_effective_date_message), tempDateString));
            selectDate(tempDate);
        }
        ((TextView) dialogView.findViewById(R.id.date)).setText(tempDateString);

        if (overpay) {
            ((TextView) dialogView.findViewById(R.id.pay_amount)).setTextColor(ContextCompat.getColor(getContext(), R.color.red));
            ((TextView) dialogView.findViewById(R.id.pay_amount)).setText(amountString.contains(MiBancoConstants.CURRENCY_SYMBOL) ? amountString : Utils.concatenateStrings(new String[]{MiBancoConstants.CURRENCY_SYMBOL,amountString}));
        }

        Utils.showDialog(dialog, getActivity());

        if (!originalDate.equals(effectiveDateString)) {
            Utils.showAlert(getActivity(), getString(R.string.transaction_confirm_effective_date_title), String.format(getString(R.string.transaction_confirm_effective_date_message), tempDateString));
        }

        if (overpay) {
            final DialogHolo dialogOverpay = new DialogHolo(getActivity());
            dialogOverpay.setTitle(R.string.transaction_overpay_title);
            dialogOverpay.setMessage(R.string.transaction_overpay_message);
            dialogOverpay.setCancelable(true);
            dialogOverpay.setConfirmationButton(getString(R.string.ok), new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    Utils.dismissDialog(dialogOverpay);
                }
            });
            Utils.showDialog(dialogOverpay, getActivity());

            BPAnalytics.logEvent(BPAnalytics.EVENT_PAYMENT_OVERPAY_PROMPT_PRESENTED);
        }
    }

    /**
     * Transfer processor.
     *
     * @param transfer the transfer to process
     */
    private void transferProcessor(final TransferActive transfer) {
        final App application = App.getApplicationInstance();

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

            if (transfer.isFailTransfers()) {
                if (transfer.isDowntime()) {
                    Utils.showMaintenanceError(getActivity(), getString(R.string.maintenance_personal_banking));
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

                        application.getAsyncTasksManager().updateBalances(getActivity());

                        final TransferActiveAccount fromAccountInformation = failedTransfer.getFromAccount();
                        final TransferActiveAccount toAccountInformation = failedTransfer.getToAccount();
                        final Intent intent = new Intent(getActivity(), PaymentReceipt.class);
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
                        startActivityForResult(intent, RECEIPT_REQUEST_CODE);
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

            final SimpleDateFormat sdfLocal = new SimpleDateFormat(application.getDateFormat());
            String dateLocal = confirmedTransfer.getEffectiveDate();
            try {
                dateLocal = sdfLocal.format(sdf.parse(confirmedTransfer.getEffectiveDate()));
            } catch (final ParseException e) {
                Log.e("Payments", "Error parsing effective date.");
                onTransactionError(getString(R.string.transaction_failed_title), getString(R.string.transaction_failed_message), "Error parsing effective date.");
                return;
            }

            application.getAsyncTasksManager().updateBalances(getActivity());

            final Intent intent = new Intent(getActivity(), PaymentReceipt.class);
            intent.putExtra("transfers", isTransfer);
            intent.putExtra("referenceNr", confirmedTransfer.getReferenceNumber());
            intent.putExtra("fromName", fromAccountInformation.getNickname());
            intent.putExtra("fromNr", fromAccountInformation.getAccountNumber());
            intent.putExtra("toName", toAccountInformation.getNickname());
            intent.putExtra("toNr", toAccountInformation.getAccountNumber());
            intent.putExtra("amount", confirmedTransfer.getAmount());
            intent.putExtra("date", dateLocal);

            BPAnalytics.logEvent(BPAnalytics.EVENT_TRANSFER_SUCCESSFUL);

            startActivityForResult(intent, RECEIPT_REQUEST_CODE);
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

            final DialogHolo dialog = new DialogHolo(getActivity());
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
                    application.getAsyncTasksManager().makeTransfer(getActivity(), itemFrom.getId(), itemTo.getId(), Utils.formatAmountForWs(amount), verifyTransfer.getEffectiveDate(), "variable",
                            false, new ResponderListener() {

                                @Override
                                public void responder(final String responderName, final Object data) {
                                    Utils.dismissDialog(dialog);
                                    transferProcessor((TransferActive) data);
                                }

                                @Override
                                public void sessionHasExpired() {
                                    application.reLogin(getActivity());
                                }
                            });
                }
            });
            Utils.showDialog(dialog, getActivity());

            ((TextView) dialogView.findViewById(R.id.pay_amount)).setText(verifyTransfer.getAmount());

            Spannable.Factory factory = Spannable.Factory.getInstance();
            Spannable fromAccountNickname = factory.newSpannable(fromAccountInformation.getNickname() + " ");
            Spannable fromAccountCode = factory.newSpannable(fromAccountInformation.getAccountNumber());
            fromAccountNickname.setSpan(new StyleSpan(Typeface.BOLD), 0, fromAccountNickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            fromAccountNickname.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.black)), 0, fromAccountNickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            fromAccountCode.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.grey_dark)), 0, fromAccountCode.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannedString fromAccountTitle = (SpannedString) TextUtils.concat(fromAccountNickname, fromAccountCode);
            ((TextView) dialogView.findViewById(R.id.from_nickname)).setText(fromAccountTitle);

            Spannable toAccountNickname = factory.newSpannable(toAccountInformation.getNickname() + " ");
            Spannable toAccountCode = factory.newSpannable(toAccountInformation.getAccountNumber());
            toAccountNickname.setSpan(new StyleSpan(Typeface.BOLD), 0, toAccountNickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            toAccountNickname.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.black)), 0, toAccountNickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            toAccountCode.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.grey_dark)), 0, toAccountCode.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannedString toAccountTitle = (SpannedString) TextUtils.concat(toAccountNickname, toAccountCode);
            ((TextView) dialogView.findViewById(R.id.to_nickname)).setText(toAccountTitle);

            sdf = new SimpleDateFormat(transfer.getDateFormat());
            final String originalDate = sdf.format(selectedDate);
            String tempDateString = DateFormat.getLongDateFormat(getContext()).format(selectedDate);

            if (!originalDate.equals(verifyTransfer.getEffectiveDate())) {
                Date tempDate;
                try {
                    tempDate = sdf.parse(verifyTransfer.getEffectiveDate());
                    tempDateString = DateFormat.getLongDateFormat(getContext()).format(tempDate);
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
        PaymentsTransfersFragment.isPaymentsDestroyed = isPaymentsDestroyed;
    }

    public static boolean isTransfersDestroyed() {
        return isTransfersDestroyed;
    }

    public static void setTransfersDestroyed(boolean isTransfersDestroyed) {
        PaymentsTransfersFragment.isTransfersDestroyed = isTransfersDestroyed;
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

    private void openAddPayeesWebView() {
        final Intent intentWebView = new Intent(getContext(), WebViewActivity.class);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.add_payee_url)));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, Utils.urlBlacklist(getActivity()));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_TOOLBAR_HIDE_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_PROGRESSBAR_HIDE_KEY, true);
        BPAnalytics.logEvent(BPAnalytics.EVENT_ADD_PAYEES_SECTION);
        startActivityForResult(intentWebView, MiBancoConstants.ADD_PAYEES_REQUEST_CODE);
    }

    private void openEditPayeesWebView (){
        final Intent intentWebView = new Intent(getContext(), WebViewActivity.class);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, Utils.getAbsoluteUrl(getString(R.string.edit_payee_url)));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, Utils.urlBlacklist(getContext()));
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_TOOLBAR_HIDE_KEY, true);
        intentWebView.putExtra(MiBancoConstants.WEB_VIEW_PROGRESSBAR_HIDE_KEY, true);
        BPAnalytics.logEvent(BPAnalytics.EVENT_EDIT_PAYEES_SECTION);
        startActivityForResult(intentWebView, MiBancoConstants.EDIT_PAYEES_REQUEST_CODE);
    }

    private void initBottonSheetAccountFrom(){
        bottomSheetAccountFrom = new BottomSheetDialog(getContext());
        bottomSheetAccountFrom.setContentView(R.layout.fragment_payment_account);
        fromList = bottomSheetAccountFrom.findViewById(R.id.wheel_from_fragment);

        bottomSheetAccountFrom.findViewById(R.id.closeBottonSheet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                bottomSheetAccountFrom.dismiss();
            }
        });
    }

    private void showBottomSheetDialogAccountFrom() {
        bottomSheetAccountFrom.show();
    }

    private void initBottonSheetAccountTo(){
        bottomSheetAccountTo = new BottomSheetDialog(getContext());
        bottomSheetAccountTo.setContentView(R.layout.fragment_payment_account);
        toList = bottomSheetAccountTo.findViewById(R.id.wheel_from_fragment);

        bottomSheetAccountTo.findViewById(R.id.closeBottonSheet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                bottomSheetAccountTo.dismiss();
            }
        });
    }

    private void showBottomSheetDialogAccountTo(){
        bottomSheetAccountTo.show();
    }

    private void initBottonSheetAmountEditor(){
        amountEditor = new AmountEditor(getContext());
    }

    private void showBottomSheetDialogAmountEditor(){
        amountEditor.show();
    }
}