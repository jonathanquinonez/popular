package com.popular.android.mibanco.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.foound.widget.AmazingListView;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.adapter.AccountsAdapter;
import com.popular.android.mibanco.adapter.HistoryMonthAdapter;
import com.popular.android.mibanco.adapter.PayeesAdapter;
import com.popular.android.mibanco.adapter.PaymentHistoryAdapter;
import com.popular.android.mibanco.adapter.TransferHistoryAdapter;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.Month;
import com.popular.android.mibanco.model.PaymentHistory;
import com.popular.android.mibanco.model.PaymentHistoryEntry;
import com.popular.android.mibanco.model.TransferHistory;
import com.popular.android.mibanco.model.TransferHistoryEntry;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.DialogHolo;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that manages receipts
 */
public class Receipts extends BaseSessionActivity implements OnClickListener, OnItemClickListener {

    public static final String TRANSFER_HISTORY = "transferHistory";
    public static final String PAYMENT_HISTORY = "paymentHistory";
    public static final String RECEIPT_TYPE = "receiptType";

    private static final String SELECTED_MONTH = "selectedMonth";
    private static final String SELECTED_PAYEE = "selectedPayee";
    private static final String SELECTED_ACCOUNT = "selectedAccount";

    public enum HistoryType {
        PAYMENT, TRANSFER
    }

    public enum EPaymentReceipt {
        FULLLIST, BYPAYEE
    }

    public enum ETransferReceipt {
        FULLLIST, BYACCOUNT
    }

    private AmazingListView selectStatementListView;

    private Button btnByMonth;
    private Button btnByPayee;
    private Button btnByAccount;

    private Month selectedMonth;

    private PaymentHistory paymentHistory;
    private PaymentHistoryEntry selectedPayee;

    private TransferHistory transferHistory;
    private TransferHistoryEntry selectedAccount;

    private HistoryType selectedHistoryType = HistoryType.PAYMENT;

    private DialogHolo dialog;

    private LinearLayout layoutTransferPayment;

    private TextView txtName;
    private TextView txtNumber;
    private ImageView imgLogo;

    private TextView txtNoDataOnList;

    private View viewEmptyList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipts);

        txtNoDataOnList = (TextView) findViewById(R.id.txt_no_data);
        viewEmptyList = findViewById(R.id.layout_no_data);
        viewEmptyList.setVisibility(View.GONE);

        selectStatementListView = (AmazingListView) findViewById(R.id.receipts_list);
        selectStatementListView.setPinnedHeaderView(((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_header, selectStatementListView, false));
        selectStatementListView.setEmptyView(new View(this));
        selectStatementListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

                Object selectedItem = adapter.getItemAtPosition(position);
                openReceiptView(selectedItem);
            }
        });

        btnByMonth = (Button) findViewById(R.id.btn_by_month);
        btnByMonth.setOnClickListener(this);

        btnByPayee = (Button) findViewById(R.id.btn_by_payee);
        btnByPayee.setOnClickListener(this);

        btnByAccount = (Button) findViewById(R.id.btn_by_account);
        btnByAccount.setOnClickListener(this);

        layoutTransferPayment = (LinearLayout) findViewById(R.id.layout_transfer_payment);
        txtName = (TextView) findViewById(R.id.txt_receipts_to);
        txtNumber = (TextView) findViewById(R.id.txt_to_receipts_number);
        imgLogo = (ImageView) findViewById(R.id.img_receipts_card);

        if (savedInstanceState != null) {
            selectedHistoryType = HistoryType.values()[savedInstanceState.getInt(RECEIPT_TYPE)];
            selectedMonth = (Month) savedInstanceState.getSerializable(SELECTED_MONTH);
            if (selectedMonth != null && selectedMonth.getYear() == null) {
                selectedMonth = null;
            }

            paymentHistory = (PaymentHistory) savedInstanceState.getSerializable(PAYMENT_HISTORY);
            selectedPayee = (PaymentHistoryEntry) savedInstanceState.getSerializable(SELECTED_PAYEE);
            if (selectedPayee != null && selectedPayee.getFrontEndId() == null) {
                selectedPayee = null;
            }
            if (selectedPayee != null && paymentHistory != null && selectedPayee.getFrontEndId().equals(paymentHistory.getAvailablePayees().getDefaultValue())) {
                selectedPayee = null;
            }

            transferHistory = (TransferHistory) savedInstanceState.getSerializable(TRANSFER_HISTORY);
            selectedAccount = (TransferHistoryEntry) savedInstanceState.getSerializable(SELECTED_ACCOUNT);
            if (selectedAccount != null && selectedAccount.getTargetApiAccountKey() == null) {
                selectedAccount = null;
            }
            if (selectedAccount != null && transferHistory != null && selectedAccount.getTargetApiAccountKey().equals(transferHistory.getAvailableAccounts().getDefaultValue())) {
                selectedAccount = null;
            }
        }

        loadReceipts(getIntent(), savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        loadReceipts(intent, null);
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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(RECEIPT_TYPE, selectedHistoryType.ordinal());
        outState.putSerializable(SELECTED_MONTH, selectedMonth);
        outState.putSerializable(PAYMENT_HISTORY, paymentHistory);
        outState.putSerializable(SELECTED_PAYEE, selectedPayee);
        outState.putSerializable(TRANSFER_HISTORY, transferHistory);
        outState.putSerializable(SELECTED_ACCOUNT, selectedAccount);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        if (v == btnByMonth) {
            showMonthDialog();
        } else if (v == btnByPayee) {
            showSelectPayeeDialog();
        } else if (v == btnByAccount) {
            showSelectAccountDialog();
        } else {
            super.onClick(v);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

        Object selectedItem = adapter.getItemAtPosition(position);

        if (selectedItem != null) {
            if (selectedItem instanceof Month) {
                selectedMonth = (Month) selectedItem;
                selectedPayee = null;
                selectedAccount = null;
                Utils.dismissDialog(dialog);

                if (selectedHistoryType == HistoryType.TRANSFER) {
                    if (transferHistory != null && selectedMonth.getValue().equals(transferHistory.getAvailablePeriods().getDefaultValue())) {
                        fetchAndSetTransferHistory(false, ETransferReceipt.FULLLIST);
                    } else {
                        filterTransferHistoryByMonth(false, ETransferReceipt.FULLLIST);
                    }
                } else if (selectedHistoryType == HistoryType.PAYMENT) {
                    if (paymentHistory != null && selectedMonth.getValue().equals(paymentHistory.getAvailablePeriods().getDefaultValue())) {
                        fetchAndSetPaymentHistory(false, EPaymentReceipt.FULLLIST);
                    } else {
                        filterPaymentHistoryByMonth(false, EPaymentReceipt.FULLLIST);
                    }
                }
            } else if (selectedItem instanceof PaymentHistoryEntry) {
                selectedPayee = (PaymentHistoryEntry) selectedItem;
                selectedMonth = null;
                selectedAccount = null;
                Utils.dismissDialog(dialog);
                PaymentHistoryEntry.Payee selPayee = selectedPayee.getPayee();
                if (paymentHistory != null && selPayee.getFrontEndID().equals(paymentHistory.getAvailablePayees().getDefaultValue())) {
                    fetchAndSetPaymentHistory(false, EPaymentReceipt.FULLLIST);
                } else {
                    fetchFilteredReceiptsByPayee(false, EPaymentReceipt.BYPAYEE);
                }
            } else if (selectedItem instanceof TransferHistoryEntry) {
                selectedAccount = (TransferHistoryEntry) selectedItem;
                selectedMonth = null;
                selectedPayee = null;
                Utils.dismissDialog(dialog);
                TransferHistoryEntry.Account selAccount = selectedAccount.getAccount();
                if (transferHistory != null && selAccount.getApiAccountKey().equals(transferHistory.getAvailableAccounts().getDefaultValue())) {
                    fetchAndSetTransferHistory(false, ETransferReceipt.FULLLIST);
                } else {
                    fetchFilteredReceiptsByAccount(false, ETransferReceipt.BYACCOUNT);
                }
            }
        }
    }

    private void showSelectPayeeDialog() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new DialogHolo(Receipts.this);
        View layout = dialog.setCustomContentView(R.layout.single_list_view);
        ListView listView = (ListView) layout.findViewById(R.id.list_view_elements);
        listView.setOnItemClickListener(this);

        String allPayeesDefaultValue = null;
        List<PaymentHistoryEntry> payees = new ArrayList<>();
        if (paymentHistory != null && paymentHistory.getAvailablePayees() != null) {
            allPayeesDefaultValue = paymentHistory.getAvailablePayees().getDefaultValue();
            payees.add(new PaymentHistoryEntry(getString(R.string.all_payees), allPayeesDefaultValue));
            if (paymentHistory.getAvailablePayees().getPayees() != null) {
                payees.addAll(paymentHistory.getAvailablePayees().getPayees());
            }
        }

        listView.setAdapter(new PayeesAdapter(Receipts.this, payees, selectedPayee, allPayeesDefaultValue));

        dialog.setTitle(getString(R.string.select_payee));
        dialog.setConfirmationButton(getString(R.string.cancel), new OnClickListener() {

            @Override
            public void onClick(final View v) {
                Utils.dismissDialog(dialog);
            }
        });

        dialog.setCancelable(true);
        Utils.showDialog(dialog, Receipts.this);
    }

    private void showSelectAccountDialog() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new DialogHolo(Receipts.this, false);
        View layout = dialog.setCustomContentView(R.layout.single_list_view);
        ListView listView = (ListView) layout.findViewById(R.id.list_view_elements);
        listView.setOnItemClickListener(this);

        String defaultAllAccountsValue = null;
        List<TransferHistoryEntry> accounts = new ArrayList<>();
        if (transferHistory != null && transferHistory.getAvailableAccounts() != null) {
            defaultAllAccountsValue = transferHistory.getAvailableAccounts().getDefaultValue();
            accounts.add(new TransferHistoryEntry(getString(R.string.all_accounts), defaultAllAccountsValue, "", ""));
            if (transferHistory.getAvailableAccounts().getAccounts() != null) {
                accounts.addAll(transferHistory.getAvailableAccounts().getAccounts());
            }
        }

        listView.setAdapter(new AccountsAdapter(Receipts.this, accounts, selectedAccount, defaultAllAccountsValue));

        dialog.setTitle(getString(R.string.select_account));

        dialog.setConfirmationButton(getString(R.string.cancel), new OnClickListener() {

            @Override
            public void onClick(final View v) {
                Utils.dismissDialog(dialog);
            }
        });

        dialog.setCancelable(true);
        Utils.showDialog(dialog, Receipts.this);
    }

    private void showMonthDialog() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new DialogHolo(Receipts.this);
        View layout = dialog.setCustomContentView(R.layout.single_amazing_list_view);
        AmazingListView listView = (AmazingListView) layout.findViewById(R.id.list_view_elements);
        listView.setPinnedHeaderView(((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.gray_header, listView, false));
        listView.setOnItemClickListener(this);

        List<Month> monthsArray = new ArrayList<>();
        Month recent = null;
        if (selectedHistoryType == HistoryType.TRANSFER) {
            if (transferHistory != null && transferHistory.getAvailablePeriods() != null) {
                recent = new Month(getString(R.string.recent_transfers), null, transferHistory.getAvailablePeriods().getDefaultValue());
                monthsArray.addAll(transferHistory.getAvailablePeriods().getMonths());
            }
        } else if (selectedHistoryType == HistoryType.PAYMENT) {
            if (paymentHistory != null && paymentHistory.getAvailablePeriods() != null) {
                recent = new Month(getString(R.string.recent_payments), null, paymentHistory.getAvailablePeriods().getDefaultValue());
                monthsArray.addAll(paymentHistory.getAvailablePeriods().getMonths());
            }
        }

        List<Pair<String, List<Month>>> months = new ArrayList<Pair<String, List<Month>>>();
        for (Month month : monthsArray) {
            boolean addList = true;
            String year = month.getYear();
            for (int i = 0; i < months.size(); i++) {
                if (months.get(i).first.equalsIgnoreCase(year)) {
                    months.get(i).second.add(month);
                    addList = false;
                }
            }
            if (addList) {
                List<Month> yearSection = new ArrayList<>();
                yearSection.add(month);
                months.add(new Pair<String, List<Month>>(year, yearSection));
            }
        }

        if (recent != null) {
            if (months != null && months.size() > 0) {
                months.get(0).second.add(0, recent);
            } else {
                months = new ArrayList<>();
                List<Month> recentMonths = new ArrayList<>();
                recentMonths.add(recent);
                months.add(new Pair<>(recent.getYear(), recentMonths));
            }
        }

        listView.setAdapter(new HistoryMonthAdapter(Receipts.this, months, selectedMonth));
        dialog.setTitle(getString(R.string.select_month));
        dialog.setConfirmationButton(getString(R.string.cancel), new OnClickListener() {

            @Override
            public void onClick(final View v) {
                Utils.dismissDialog(dialog);
            }
        });

        dialog.setCancelable(true);
        Utils.showDialog(dialog, Receipts.this);
    }

    /*
     * PAYMENT HISTORY PART
     */
    @SuppressLint("DefaultLocale")
    private void loadPaymentHistoryList(boolean byMonth, EPaymentReceipt paymentReceipt) {
        List<Pair<String, List<PaymentHistoryEntry>>> transfers = new ArrayList<Pair<String, List<PaymentHistoryEntry>>>();
        final List<PaymentHistoryEntry> inProcessPayments = paymentHistory.getInProcess();
        final List<PaymentHistoryEntry> sentPayments = paymentHistory.getHistory();

        if (inProcessPayments != null && inProcessPayments.size() > 0) {
            transfers.add(new Pair<>(getString(R.string.statement_in_process_caps), inProcessPayments));
        }

        if (sentPayments != null && sentPayments.size() > 0) {
            String header = paymentReceipt == EPaymentReceipt.BYPAYEE ? getString(R.string.when) : getString(R.string.sent_payment_caps);
            if (byMonth && selectedMonth != null) {
                header = selectedMonth.getMonth().toUpperCase() + " " + selectedMonth.getYear();
            }
            transfers.add(new Pair<>(header, sentPayments));
        }

        if (transfers.size() == 0) {
            txtNoDataOnList.setText(getString(R.string.no_payments_receipts_found));
            viewEmptyList.setVisibility(View.VISIBLE);
        } else {
            viewEmptyList.setVisibility(View.GONE);
        }

        selectStatementListView.setAdapter(new PaymentHistoryAdapter(Receipts.this, transfers, paymentReceipt));
    }

    private void fetchAndSetPaymentHistory(boolean useExistingData, final EPaymentReceipt paymentReceipt) {
        layoutTransferPayment.setVisibility(View.GONE);
        if (useExistingData) {
            loadPaymentHistoryList(false, paymentReceipt);
        } else {
            if(App.getApplicationInstance() != null && App.getApplicationInstance().getAsyncTasksManager() != null) {
                App.getApplicationInstance().getAsyncTasksManager().fetchPaymentHistory(Receipts.this, new ResponderListener() {

                    @Override
                    public void sessionHasExpired() {
                        application.reLogin(Receipts.this);
                    }

                    @Override
                    public void responder(String responderName, Object data) {
                        if (data != null) {
                            btnByMonth.setEnabled(true);
                            btnByPayee.setEnabled(true);
                            paymentHistory = (PaymentHistory) data;
                            if (paymentHistory != null && paymentHistory.getAvailablePeriods() != null) {
                                selectedMonth = new Month(paymentHistory.getAvailablePeriods().getDefaultLabel(), null, paymentHistory.getAvailablePeriods().getDefaultValue());
                            }
                            if (paymentHistory != null && paymentHistory.getAvailablePayees() != null) {
                                selectedPayee = new PaymentHistoryEntry(getString(R.string.all_payees), paymentHistory.getAvailablePayees().getDefaultValue());
                            }
                            loadPaymentHistoryList(false, paymentReceipt);
                        } else {
                            txtNoDataOnList.setText(getString(R.string.no_payments_receipts_found));
                            viewEmptyList.setVisibility(View.VISIBLE);
                            btnByMonth.setEnabled(false);
                            btnByPayee.setEnabled(false);
                        }
                    }
                }, true, null, null);
            }else{
                Toast.makeText(getApplicationContext(),R.string.error_occurred,Toast.LENGTH_LONG).show();
            }
        }
    }

    private void filterPaymentHistoryByMonth(boolean useExistingData, final EPaymentReceipt paymentReceipt) {
        BPAnalytics.logEvent(BPAnalytics.EVENT_PAYMENT_HISTORY_FILTERED_BY_DATE);
        selectedAccount = null;
        selectedPayee = null;
        layoutTransferPayment.setVisibility(View.GONE);
        if (useExistingData) {
            loadPaymentHistoryList(true, paymentReceipt);
        } else {
            App.getApplicationInstance().getAsyncTasksManager().fetchPaymentHistory(Receipts.this, new ResponderListener() {

                @Override
                public void sessionHasExpired() {
                    application.reLogin(Receipts.this);
                }

                @Override
                public void responder(String responderName, Object data) {
                    if (data != null) {
                        paymentHistory = (PaymentHistory) data;
                        loadPaymentHistoryList(true, paymentReceipt);
                    }
                }
            }, true, null, selectedMonth != null ? selectedMonth.getValue() : "");
        }
    }

    /*
     * TRANSFER HISTORY PART
     */

    @SuppressLint("DefaultLocale")
    private void loadTransferHistoryList(boolean byMonth, ETransferReceipt transferReceipt) {
        List<Pair<String, List<TransferHistoryEntry>>> transfers = new ArrayList<Pair<String, List<TransferHistoryEntry>>>();
        final List<TransferHistoryEntry> inProcessPayments = transferHistory.getInProcess();
        final List<TransferHistoryEntry> sentPayments = transferHistory.getHistory();

        if (inProcessPayments != null && inProcessPayments.size() > 0) {
            transfers.add(new Pair<>(getString(R.string.statement_in_process_caps), inProcessPayments));
        }

        if (sentPayments != null && sentPayments.size() > 0) {
            String header = transferReceipt == ETransferReceipt.BYACCOUNT ? getString(R.string.from_caps) : getString(R.string.sent_transfers_caps);
            if (byMonth && selectedMonth != null) {
                header = selectedMonth.getMonth().toUpperCase() + " " + selectedMonth.getYear();
            }
            transfers.add(new Pair<>(header, sentPayments));
        }

        if (transfers.size() == 0) {
            txtNoDataOnList.setText(getString(R.string.no_transfers_receipts_found));
            viewEmptyList.setVisibility(View.VISIBLE);
        } else {
            viewEmptyList.setVisibility(View.GONE);
        }

        selectStatementListView.setAdapter(new TransferHistoryAdapter(Receipts.this, transfers, transferReceipt));
    }

    private void fetchFilteredReceiptsByPayee(boolean useExistingData, final EPaymentReceipt paymentReceipt) {
        BPAnalytics.logEvent(BPAnalytics.EVENT_PAYMENT_HISTORY_FILTERED_BY_PAYEE);
        selectedMonth = null;
        selectedAccount = null;
        if (selectedPayee != null) {
            layoutTransferPayment.setVisibility(View.VISIBLE);
            txtName.setText(selectedPayee.getPayeeNickname());
            txtNumber.setText(selectedPayee.getPayeeAccountLast4Num());

            final int drawableId = Utils.getPayeeDrawableResource(Utils.getValidGlobalPayeeId(selectedPayee.getGlobalPayeeId()));
            imgLogo.setImageResource(drawableId);

            if (useExistingData) {
                loadPaymentHistoryList(false, paymentReceipt);
            } else {
                App.getApplicationInstance().getAsyncTasksManager().fetchPaymentHistory(Receipts.this, new ResponderListener() {

                    @Override
                    public void sessionHasExpired() {
                        application.reLogin(Receipts.this);
                    }

                    @Override
                    public void responder(String responderName, Object data) {
                        if (data != null) {
                            paymentHistory = (PaymentHistory) data;
                            loadPaymentHistoryList(false, paymentReceipt);
                        }
                    }
                }, true, selectedPayee.getFrontEndId(), null);
            }
        }
    }

    private void fetchAndSetTransferHistory(boolean useExistingData, final ETransferReceipt transferReceipt) {
        layoutTransferPayment.setVisibility(View.GONE);
        if (useExistingData) {
            loadTransferHistoryList(false, transferReceipt);
        } else {
            App.getApplicationInstance().getAsyncTasksManager().fetchTransferHistory(Receipts.this, new ResponderListener() {

                @Override
                public void sessionHasExpired() {
                    application.reLogin(Receipts.this);
                }

                @Override
                public void responder(String responderName, Object data) {
                    if (data != null) {
                        btnByMonth.setEnabled(true);
                        btnByAccount.setEnabled(true);
                        transferHistory = (TransferHistory) data;
                        if (transferHistory != null && transferHistory.getAvailablePeriods() != null) {
                            selectedMonth = new Month(transferHistory.getAvailablePeriods().getDefaultLabel(), null, transferHistory.getAvailablePeriods().getDefaultValue());
                        }
                        if (transferHistory != null && transferHistory.getAvailableAccounts() != null) {
                            selectedAccount = new TransferHistoryEntry(getString(R.string.all_accounts), transferHistory.getAvailableAccounts().getDefaultValue(), "", "");
                        }
                        loadTransferHistoryList(false, transferReceipt);
                    } else {
                        txtNoDataOnList.setText(getString(R.string.no_transfers_receipts_found));
                        viewEmptyList.setVisibility(View.VISIBLE);
                        btnByMonth.setEnabled(false);
                        btnByAccount.setEnabled(false);
                    }
                }
            }, true, null, null);
        }
    }

    private void fetchFilteredReceiptsByAccount(boolean useExistingData, final ETransferReceipt transferReceipt) {
        BPAnalytics.logEvent(BPAnalytics.EVENT_TRANSFER_HISTORY_FILTERED_BY_ACCOUNT);
        selectedMonth = null;
        selectedPayee = null;
        if (selectedAccount != null) {
            layoutTransferPayment.setVisibility(View.VISIBLE);
            txtName.setText(selectedAccount.getTargetNickname());
            txtNumber.setText(selectedAccount.getTargetAccountLast4Num());

            if (App.getApplicationInstance() != null && App.getApplicationInstance().getCustomerAccountsMap() != null) {
                CustomerAccount account = App.getApplicationInstance().getCustomerAccountsMap().get(selectedAccount.getTargetApiAccountKey() + selectedAccount.getTargetAccountNumberSuffix());
                if (account != null) {
                    Utils.displayAccountImage(imgLogo, account);
                } else {
                    imgLogo.setImageResource(R.drawable.account_image_default);
                }
            } else {
                imgLogo.setImageResource(R.drawable.account_image_default);
            }

            if (useExistingData) {
                loadTransferHistoryList(false, transferReceipt);
            } else {
                App.getApplicationInstance().getAsyncTasksManager().fetchTransferHistory(Receipts.this, new ResponderListener() {

                    @Override
                    public void sessionHasExpired() {
                        application.reLogin(Receipts.this);
                    }

                    @Override
                    public void responder(String responderName, Object data) {
                        if (data != null) {
                            transferHistory = (TransferHistory) data;
                            loadTransferHistoryList(false, transferReceipt);
                        }
                    }
                }, true, selectedAccount.getValue(), null);
            }
        }
    }

    private void filterTransferHistoryByMonth(boolean useExistingData, final ETransferReceipt transferReceipt) {
        BPAnalytics.logEvent(BPAnalytics.EVENT_TRANSFER_HISTORY_FILTERED_BY_DATE);
        selectedAccount = null;
        selectedPayee = null;
        layoutTransferPayment.setVisibility(View.GONE);
        if (useExistingData) {
            loadTransferHistoryList(true, transferReceipt);
        } else {
            App.getApplicationInstance().getAsyncTasksManager().fetchTransferHistory(Receipts.this, new ResponderListener() {

                @Override
                public void sessionHasExpired() {
                    application.reLogin(Receipts.this);
                }

                @Override
                public void responder(String responderName, Object data) {
                    if (data != null) {
                        transferHistory = (TransferHistory) data;
                        loadTransferHistoryList(true, transferReceipt);
                    }
                }
            }, true, null, selectedMonth != null ? selectedMonth.getValue() : "");
        }
    }

    private void loadReceipts(Intent intent, Bundle savedInstanceState) {
        layoutTransferPayment.setVisibility(View.GONE);
        if (savedInstanceState == null) {
            selectedHistoryType = (HistoryType) intent.getSerializableExtra(RECEIPT_TYPE);
            selectedMonth = null;
            paymentHistory = null;
            selectedPayee = null;
            transferHistory = null;
            selectedAccount = null;
        }

        if (intent.hasExtra(PaymentReceipt.TRANSACTION_DELETED)) {
            paymentHistory = intent.hasExtra(PAYMENT_HISTORY) ? (PaymentHistory) intent.getSerializableExtra(PAYMENT_HISTORY) : null;
            transferHistory = intent.hasExtra(TRANSFER_HISTORY) ? (TransferHistory) intent.getSerializableExtra(TRANSFER_HISTORY) : null;
            selectedHistoryType = (HistoryType) intent.getSerializableExtra(RECEIPT_TYPE);
            if (paymentHistory != null) {
                fetchAndSetPaymentHistory(true, EPaymentReceipt.FULLLIST);
                return;
            } else if (transferHistory != null) {
                fetchAndSetTransferHistory(true, ETransferReceipt.FULLLIST);
                return;
            }
        }

        if (selectedHistoryType == HistoryType.TRANSFER) {
            btnByPayee.setVisibility(View.GONE);
            btnByAccount.setVisibility(View.VISIBLE);
            if (savedInstanceState != null && selectedAccount != null && transferHistory != null) {
                // load existing data
                fetchFilteredReceiptsByAccount(true, ETransferReceipt.BYACCOUNT);
            } else if (savedInstanceState != null && selectedMonth != null) {
                // load existing month data
                filterTransferHistoryByMonth(true, ETransferReceipt.FULLLIST);
            } else if (savedInstanceState != null && transferHistory != null) {
                // load existing data
                fetchAndSetTransferHistory(true, ETransferReceipt.FULLLIST);
            } else {
                // Fetch data from WS
                fetchAndSetTransferHistory(false, ETransferReceipt.FULLLIST);
            }
        } else if (selectedHistoryType == HistoryType.PAYMENT) {
            btnByPayee.setVisibility(View.VISIBLE);
            btnByAccount.setVisibility(View.GONE);
            if (savedInstanceState != null && selectedPayee != null && paymentHistory != null) {
                // load existing data
                fetchFilteredReceiptsByPayee(true, EPaymentReceipt.BYPAYEE);
            } else if (savedInstanceState != null && selectedMonth != null) {
                // load existing month data
                filterPaymentHistoryByMonth(true, EPaymentReceipt.FULLLIST);
            } else if (savedInstanceState != null && paymentHistory != null) {
                // load existing data
                fetchAndSetPaymentHistory(true, EPaymentReceipt.FULLLIST);
            } else {
                // Fetch data from WS
                fetchAndSetPaymentHistory(false, EPaymentReceipt.FULLLIST);
            }
        }

        if (selectedHistoryType == HistoryType.PAYMENT) {
            BPAnalytics.logEvent(BPAnalytics.EVENT_PAYMENT_HISTORY_VIEWED);
        } else if (selectedHistoryType == HistoryType.TRANSFER) {
            BPAnalytics.logEvent(BPAnalytics.EVENT_TRANSFER_HISTORY_VIEWED);
        }
    }

    private void openReceiptView(Object item) {
        final Intent intent = new Intent(Receipts.this, PaymentReceipt.class);
        intent.putExtra("historyReceipt", true);
        if (item instanceof PaymentHistoryEntry) {
            intent.putExtra("transfers", false);
            PaymentHistoryEntry paymentHistoryEntry = (PaymentHistoryEntry) item;
            intent.putExtra("referenceNr", paymentHistoryEntry.getReferenceNumber());
            intent.putExtra("fromName", paymentHistoryEntry.getSourceNickname());
            intent.putExtra("fromNr", paymentHistoryEntry.getSourceAccountLast4Num());
            intent.putExtra("toName", paymentHistoryEntry.getPayeeNickname());
            intent.putExtra("toNr", paymentHistoryEntry.getPayeeAccountLast4Num());
            intent.putExtra("amount", paymentHistoryEntry.getAmount());
            intent.putExtra("date", paymentHistoryEntry.getEffectiveDate());
            intent.putExtra("favId", paymentHistoryEntry.getFavId());
            intent.putExtra("frontEndId", paymentHistoryEntry.getFrontEndId());
            intent.putExtra("status", paymentHistoryEntry.getStatus());
            intent.putExtra("statusCode", paymentHistoryEntry.getStatusCode());
            intent.putExtra("frequency", paymentHistoryEntry.getFrequency());
        } else if (item instanceof TransferHistoryEntry) {
            intent.putExtra("transfers", true);
            TransferHistoryEntry transferHistoryEntry = (TransferHistoryEntry) item;
            intent.putExtra("referenceNr", transferHistoryEntry.getReferenceNumber());
            intent.putExtra("fromName", transferHistoryEntry.getSourceNickname());
            intent.putExtra("fromNr", transferHistoryEntry.getSourceAccountLast4Num());
            intent.putExtra("toName", transferHistoryEntry.getTargetNickname());
            intent.putExtra("toNr", transferHistoryEntry.getTargetAccountLast4Num());
            intent.putExtra("amount", transferHistoryEntry.getAmount());
            intent.putExtra("date", transferHistoryEntry.getEffectiveDate());
            intent.putExtra("favId", transferHistoryEntry.getFavId());
            intent.putExtra("frontEndId", transferHistoryEntry.getFrontEndId());
            intent.putExtra("status", transferHistoryEntry.getStatus());
            intent.putExtra("statusCode", transferHistoryEntry.getStatusCode());
            intent.putExtra("frequency", transferHistoryEntry.getFrequency());
        }
        startActivity(intent);
    }
}
