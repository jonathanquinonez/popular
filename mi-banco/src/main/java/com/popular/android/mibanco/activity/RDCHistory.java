package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.foound.widget.AmazingListView;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.adapter.RemoteDepositHistoryChecksAdapter;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.RemoteDepositHistory;
import com.popular.android.mibanco.object.RemoteDepositHistoryCheckItem;
import com.popular.android.mibanco.util.BPAnalytics;

import java.util.ArrayList;
import java.util.List;

/**
 * RDC History Activity class.
 */
public class RDCHistory extends BaseSessionActivity {
    /**
     * The tag
     */
    public static final String REMOTE_DEPOSIT_HISTORY = "remoteDepositHistory";

    /**
     * The amazing list for rdc receipts
     */
    private AmazingListView rdcReceiptsListView;

    /**
     * The raw Remote Deposit History object
     */
    RemoteDepositHistory remoteDepositHistory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(application != null && application.getAsyncTasksManager() != null) {
            setContentView(R.layout.remote_deposit_history);

            rdcReceiptsListView = (AmazingListView) findViewById(R.id.rdc_receipts_list);
            rdcReceiptsListView.setPinnedHeaderView(((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_header, rdcReceiptsListView, false));
            rdcReceiptsListView.setEmptyView(new View(this));
            rdcReceiptsListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                    RemoteDepositHistoryCheckItem rdcCheckItem = (RemoteDepositHistoryCheckItem) adapter.getItemAtPosition(position);
                    displayRDCCheckItem(rdcCheckItem.getReferenceNumber(), rdcCheckItem.getFrontendId(), rdcCheckItem.getTargetNickname(), rdcCheckItem.getTargetAccountLast4Num(), rdcCheckItem.getAmount(), rdcCheckItem.getSubmittedDate(), rdcCheckItem.getDepositStatus());
                }
            });

            boolean useExistingData = false;

            if (savedInstanceState != null) {
                remoteDepositHistory = (RemoteDepositHistory) savedInstanceState.getSerializable(REMOTE_DEPOSIT_HISTORY);
                useExistingData = true;
            }

            fetchAndSetRemoteDepositHistory(useExistingData);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        BPAnalytics.onStartSession(this);

        // Should we re-load the data?
        if (application != null && application.getAsyncTasksManager() != null
                && remoteDepositHistory != null && application.getRemoteDepositHistory() == null) {

            findViewById(R.id.rdc_history_no_results).setVisibility(View.GONE);
            fetchAndSetRemoteDepositHistory(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        BPAnalytics.onEndSession(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(REMOTE_DEPOSIT_HISTORY, remoteDepositHistory);
        super.onSaveInstanceState(outState);
    }

    /**
     * function: fetchAndSetRemoteDepositHistory
     * <p/>
     * Loads the history from the JSON and displays it.
     *
     * @param useExistingData
     * TRUE:  The data has already been stored (by the bundle) and does not need
     * to be looked up via JSON
     * FALSE: The data has not been loaded and should be looked up via JSON
     */
    private void fetchAndSetRemoteDepositHistory(boolean useExistingData) {

        if (useExistingData)
            loadRemoteDepositHistoryChecksList(remoteDepositHistory);
        else {
            App.getApplicationInstance().getAsyncTasksManager().fetchRemoteDepositHistory(RDCHistory.this, new ResponderListener() {

                @Override
                public void sessionHasExpired() {
                    application.reLogin(RDCHistory.this);
                }

                @Override
                public void responder(String responderName, Object data) {
                    if (application.getRemoteDepositHistory() != null) {
                        remoteDepositHistory = application.getRemoteDepositHistory();
                        loadRemoteDepositHistoryChecksList(remoteDepositHistory);
                    }
                }
            });
        }
    }

    /**
     * function: loadRemoteDepositHistoryChecksList
     * <p/>
     * Creates the two lists to display.
     *
     * @param remoteDepositHistory
     * The object to be parsed and displayed
     */
    private void loadRemoteDepositHistoryChecksList(RemoteDepositHistory remoteDepositHistory) {
        List<Pair<String, List<RemoteDepositHistoryCheckItem>>> rdcReceipts = new ArrayList<Pair<String, List<RemoteDepositHistoryCheckItem>>>();
        ArrayList<RemoteDepositHistoryCheckItem> inProcessCheckHistory = new ArrayList<>();
        ArrayList<RemoteDepositHistoryCheckItem> processedCheckHistory = new ArrayList<>();

        // At least one of the lists should display information
        if (remoteDepositHistory != null && ((remoteDepositHistory.getInProcessRemoteDepositHistoryChecks() != null && remoteDepositHistory.getInProcessRemoteDepositHistoryChecks().size() > 0)
                || (remoteDepositHistory.getProcessedRemoteDepositHistoryChecks() != null && remoteDepositHistory.getProcessedRemoteDepositHistoryChecks().size() > 0))) {

            // Set up the accounts
            ArrayList<CustomerAccount> accounts = new ArrayList<CustomerAccount>();
            accounts.addAll(application.getLoggedInUser().getDepositAccounts());

            // In Process
            if (remoteDepositHistory.getInProcessRemoteDepositHistoryChecks() != null && remoteDepositHistory.getInProcessRemoteDepositHistoryChecks().size() > 0) {
                for (int i = 0; i < remoteDepositHistory.getInProcessRemoteDepositHistoryChecks().size(); i++) {

                    RemoteDepositHistoryCheckItem rdcCheckItem = new RemoteDepositHistoryCheckItem();
                    rdcCheckItem.setTargetNickname(remoteDepositHistory.getInProcessRemoteDepositHistoryChecks().get(i).getTargetNickname());
                    rdcCheckItem.setTargetAccountLast4Num(remoteDepositHistory.getInProcessRemoteDepositHistoryChecks().get(i).getTargetAccountLast4Num());
                    rdcCheckItem.setFrontendId(remoteDepositHistory.getInProcessRemoteDepositHistoryChecks().get(i).getFrontendid());
                    rdcCheckItem.setReferenceNumber(remoteDepositHistory.getInProcessRemoteDepositHistoryChecks().get(i).getReferenceNumber());
                    rdcCheckItem.setAmount(remoteDepositHistory.getInProcessRemoteDepositHistoryChecks().get(i).getAmount());
                    rdcCheckItem.setSubmittedDate(remoteDepositHistory.getInProcessRemoteDepositHistoryChecks().get(i).getSubmittedDate());
                    rdcCheckItem.setDepositStatus(remoteDepositHistory.getInProcessRemoteDepositHistoryChecks().get(i).getStatus());
                    inProcessCheckHistory.add(rdcCheckItem);
                }

                rdcReceipts.add(new Pair<String, List<RemoteDepositHistoryCheckItem>>(getString(R.string.rdc_history_pending), inProcessCheckHistory));
            }

            // Processed
            if (remoteDepositHistory.getProcessedRemoteDepositHistoryChecks() != null && remoteDepositHistory.getProcessedRemoteDepositHistoryChecks().size() > 0) {
                for (int i = 0; i < remoteDepositHistory.getProcessedRemoteDepositHistoryChecks().size(); i++) {

                    RemoteDepositHistoryCheckItem rdcCheckItem = new RemoteDepositHistoryCheckItem();
                    rdcCheckItem.setTargetNickname(remoteDepositHistory.getProcessedRemoteDepositHistoryChecks().get(i).getTargetNickname());
                    rdcCheckItem.setTargetAccountLast4Num(remoteDepositHistory.getProcessedRemoteDepositHistoryChecks().get(i).getTargetAccountLast4Num());
                    rdcCheckItem.setFrontendId(remoteDepositHistory.getProcessedRemoteDepositHistoryChecks().get(i).getFrontendid());
                    rdcCheckItem.setReferenceNumber(remoteDepositHistory.getProcessedRemoteDepositHistoryChecks().get(i).getReferenceNumber());
                    rdcCheckItem.setAmount(remoteDepositHistory.getProcessedRemoteDepositHistoryChecks().get(i).getAmount());
                    rdcCheckItem.setSubmittedDate(remoteDepositHistory.getProcessedRemoteDepositHistoryChecks().get(i).getSubmittedDate());
                    rdcCheckItem.setDepositStatus(remoteDepositHistory.getProcessedRemoteDepositHistoryChecks().get(i).getStatus());
                    processedCheckHistory.add(rdcCheckItem);
                }

                rdcReceipts.add(new Pair<String, List<RemoteDepositHistoryCheckItem>>(getString(R.string.rdc_history_processed), processedCheckHistory));
            }

            rdcReceiptsListView.setAdapter(new RemoteDepositHistoryChecksAdapter(RDCHistory.this, rdcReceipts, application.getLoggedInUser().getRDCAccounts()));
        }
        // Neither list has any information - hide the lists and show the message
        else {
            findViewById(R.id.rdc_history_no_results).setVisibility(View.VISIBLE);
        }
    }

    /**
     * function: displayRDCCheckItem
     * <p/>
     * Handle the click when a user wants to see the details of a history item check.
     * Display the receipt activity.
     *
     * @param referenceNumber
     * The id for this deposit
     * @param frontendid
     * The front end id of the account the check was deposited to
     * @param targetNickname
     * The nickname of the account the check was deposited to
     * @param targetAccountLast4Num
     * The last 4 digits of the account the check was deposited to
     * @param amount
     * The amount the check
     * @param submittedDate
     * The date the deposit was made
     */
    private void displayRDCCheckItem(String referenceNumber, String frontendid, String targetNickname, String targetAccountLast4Num, String amount, String submittedDate, String depositStatus) {
        final Intent intent = new Intent(getApplicationContext(), RDCHistoryImages.class);
        intent.putExtra("referenceNumber", referenceNumber);
        intent.putExtra("frontendid", frontendid);
        intent.putExtra("targetNickname", targetNickname);
        intent.putExtra("targetAccountLast4Num", targetAccountLast4Num);
        intent.putExtra("amount", amount);
        intent.putExtra("submittedDate", submittedDate);
        intent.putExtra("status", depositStatus);
        startActivity(intent);
    }
}
