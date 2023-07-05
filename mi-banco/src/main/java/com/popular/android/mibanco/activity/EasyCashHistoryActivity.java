package com.popular.android.mibanco.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BasePermissionsSessionActivity;
import com.popular.android.mibanco.model.PhonebookContact;
import com.popular.android.mibanco.object.ViewHolderEasyCashPendForMe;
import com.popular.android.mibanco.object.ViewHolderEasyCashPendForOther;
import com.popular.android.mibanco.task.MobileCashTasks;
import com.popular.android.mibanco.util.AlertDialogParameters;
import com.popular.android.mibanco.util.AutoLoginUtils;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.ContactsManagementUtils;
import com.popular.android.mibanco.util.MobileCashUtils;
import com.popular.android.mibanco.util.ProductType;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.ws.response.EasyCashTrx;
import com.popular.android.mibanco.ws.response.MobileCashTrx;
import com.popular.android.mibanco.ws.response.MobileCashTrxInfo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static android.view.View.GONE;

public class EasyCashHistoryActivity extends BasePermissionsSessionActivity implements View.OnClickListener, EasyCashHistoryReceipt.Callback {

    private Context mContext = this;
    private HashMap<String,String> idReferenceNumberMapping;
    private String trxToDeleteReceipt;
    private String trxforMeReceipt;

    private List<MobileCashTrx> pendingForMeLst= new LinkedList<>();
    private List<MobileCashTrx> pendingForOthersLst= new LinkedList<>();
    private List<MobileCashTrx> historyLst= new LinkedList<>();
    private List<MobileCashTrx> filteredHistoryLst= new LinkedList<>();
    private boolean isPhoneInBlacklist = false;
    private boolean isOfacOffensor = false;
    private boolean refreshedDataFirstTime = false;

    private HashMap<String, PhonebookContact> contacts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_cash_history);
        findViewById(R.id.trxsScrollView).setVisibility(View.INVISIBLE);

        TextView deleteWithdrawalTrx = (TextView)findViewById(R.id.deleteWithdrawalTrx);
        TextView filterTrxs = (TextView)findViewById(R.id.filterTrxs);

        deleteWithdrawalTrx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trxToDeleteReceipt = trxforMeReceipt;
                deleteWithdrawal();
            }
        });

        filterTrxs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterOptionsPicker();
            }
        });


        Button buttonSendMoneyNonCust = (Button)findViewById(R.id.btnSendMoneyFromNonCust);
        buttonSendMoneyNonCust.setVisibility(View.INVISIBLE);
        buttonSendMoneyNonCust.setVisibility(GONE);

        BPAnalytics.logEvent(BPAnalytics.EVENT_MC_VIEW_HISTORY_CUSTOMER);

    }

    public void onPermissionResult(boolean permissionGranted)
    {
        if(permissionGranted){
            contacts = ContactsManagementUtils.getContactsWithPhones(this);
        }else{
            contacts = null;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(App.getApplicationInstance().isUpdateEasyCashHistory()){
            refreshAllData();
            App.getApplicationInstance().setUpdateEasyCashHistory(false);

        }else if(!refreshedDataFirstTime) {
            refreshAllData();
            refreshedDataFirstTime = true;

        }

        Boolean userHasCashDrop = application.getCustomerEntitlements() != null
                && application.getCustomerEntitlements().hasCashDrop()!= null
                && application.getCustomerEntitlements().hasCashDrop();
        boolean globalCashDropEntitlement = application.isGlobalCashdropEntitlementEnabled();

        if ((application != null && application.getCustomerEntitlements()!= null && application.getCustomerEntitlements().hasCashDrop() == null)
                || !userHasCashDrop || !globalCashDropEntitlement) {
            finish();
        }

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private void getHistory(Context context, boolean refresh){
        MobileCashTasks.getEasyCashPrestageTrxInfo(context,refresh, new MobileCashTasks.MobileCashListener<EasyCashTrx>() {
            @Override
            public void onMobileCashApiResponse(EasyCashTrx result) {

                if(result != null && result.getContent() != null) {
                    isPhoneInBlacklist = (MiBancoConstants.MC_BLACKLIST_PHONE.equals(result.getContent().getBlackListStatus()));
                    isOfacOffensor = (!Utils.isBlankOrNull(result.getContent().getEasyOfacStatus()) && "true".equalsIgnoreCase(result.getContent().getEasyOfacStatus()));

                    if (result.getContent().getStatus() != null
                            && result.getContent().getStatus().equals(MiBancoConstants.MOBILE_CASH_SUCCESS)) {

                        if(result.getContent().getTransactions() == null || result.getContent().getHistory() == null
                                || (result.getContent().getTransactions().size() == 0 && result.getContent().getHistory().size() == 0)) {
                            // DISPLAY NO TRANSACTIONS VIEW

                            setTransactionVisibility(false);
                        }else{
                            // DISPLAY TRANSACTIONS VIEW
                            setTransactionVisibility(true);
                            initializeTransactionsList(result.getContent().getTransactions(), result.getContent().getHistory());
                        }

                    } else {
                        setTransactionVisibility(false);

                    }
                }else{
                    // Error in service
                    MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);
                }
            }
        });
    }

    private void setTransactionVisibility(boolean trxVisible)
    {
        RelativeLayout noTrxsLayout = (RelativeLayout)findViewById(R.id.noTransactionsLayout);
        ScrollView transactionsView = (ScrollView) findViewById(R.id.trxsScrollView);

        if(noTrxsLayout != null) {
            if(!trxVisible) {
                noTrxsLayout.setVisibility(View.VISIBLE);
            }else{
                noTrxsLayout.setVisibility(View.GONE);
            }
        }

        if(transactionsView != null) {
            if(!trxVisible) {
                transactionsView.setVisibility(GONE);
            }else{
                transactionsView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDelete()
    {
        refreshAllData();
    }

    private void initializeTransactionsList(List<MobileCashTrx> pendingTransactions, List<MobileCashTrx> historyTransactions)
    {
        idReferenceNumberMapping = new HashMap<>();

        int counter = 1;
        for(MobileCashTrx trx: pendingTransactions){
            trx.setId(counter);
            idReferenceNumberMapping.put(String.valueOf(trx.getId()),trx.getTrxReceiptId());
            if(Utils.isBlankOrNull(trx.getReceiverPhone())){
                pendingForMeLst.add(trx);
                trxforMeReceipt = trx.getTrxReceiptId();
            }else{
                pendingForOthersLst.add(trx);
            }
            counter++;
        }
        historyLst = historyTransactions;
        filteredHistoryLst = historyTransactions;
        loadAccounts(pendingForMeLst, R.id.ec_withdrawal_view, R.id.withdrawals_list,0);
        pendingTrxLayoutManagement();
        historyLayoutManagement();

    }

    private void historyLayoutManagement()
    {
        final LinearLayout notInfoAvailableLayout = (LinearLayout) findViewById(R.id.history_notavailable);
        final LinearLayout listLayout = (LinearLayout) findViewById(R.id.history_list);
        if(filteredHistoryLst.size() > 0){
            notInfoAvailableLayout.setVisibility(View.GONE);
            listLayout.setVisibility(View.VISIBLE);
            loadAccounts(filteredHistoryLst, R.id.ec_history_view, R.id.history_list,2);
        }else{
            notInfoAvailableLayout.setVisibility(View.VISIBLE);
            listLayout.setVisibility(View.GONE);
        }
    }

    private void pendingTrxLayoutManagement()
    {
        final LinearLayout notInfoAvailableLayout = (LinearLayout) findViewById(R.id.pending_notavailable);
        final LinearLayout listLayout = (LinearLayout) findViewById(R.id.inprocess_list);
        if(pendingForOthersLst.size() > 0){
            notInfoAvailableLayout.setVisibility(View.GONE);
            listLayout.setVisibility(View.VISIBLE);
            loadAccounts(pendingForOthersLst, R.id.ec_inprocess_view, R.id.inprocess_list,1);

        }else{
            notInfoAvailableLayout.setVisibility(View.VISIBLE);
            listLayout.setVisibility(View.GONE);
        }
    }

    private void loadAccounts(final List<MobileCashTrx> listItems, final int sectionResId, final int listResId, int id) {
        final LinearLayout listLayout = (LinearLayout) findViewById(listResId);
        listLayout.removeAllViews();
        if (listItems.size() == 0) { // Do not hide for history
            findViewById(sectionResId).setVisibility(View.GONE);
        } else {
            for (final MobileCashTrx transaction : listItems) {
                if(id == 0)
                    listLayout.addView(getPendingFromMeView(transaction));
                else if(id == 1)
                    listLayout.addView(getPendingForOtherView(transaction));
                else {
                    listLayout.addView(getHistoryView(transaction));
                }
            }
        }
    }


    /**
     * Method that manages the row view for the From Me use case
     * @param item A MobileCashTrx object
     * @return The view
     */
    private View getPendingFromMeView(final MobileCashTrx item) {
        ViewHolderEasyCashPendForMe holder = null;
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View viewItem = inflater.inflate(R.layout.easycash_mywithdrawals_list_item, null);
        viewItem.setTag(holder);
        viewItem.setId(item.getId());

        viewItem.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(isPhoneInBlacklist){
                    BPAnalytics.logEvent(BPAnalytics.EVENT_MC_SCAN_BLACKLIST_FAILED);
                    AlertDialogParameters params = new AlertDialogParameters(mContext, R.string.enrollment_lite_phone_blacklist, blackListAction);
                    params.setPositiveButtonText(getResources().getString(R.string.ok));
                    Utils.showAlertDialog(params);

                }else if(isOfacOffensor) {
                    AlertDialogParameters params = new AlertDialogParameters(mContext, R.string.easycash_ofac_hit, blackListAction);
                    params.setPositiveButtonText(getResources().getString(R.string.ok));
                    Utils.showAlertDialog(params);

                }else {
                    MobileCashUtils.goToMobileCashFromMeToMeRedeem(mContext, item, true);
                }
            }
        });

        viewItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                trxToDeleteReceipt = idReferenceNumberMapping.get(String.valueOf(v.getId()));
                deleteWithdrawal();
                return false;
            }
        });

        holder = new ViewHolderEasyCashPendForMe(viewItem);
        holder.getTxtAccountName().setText(String.format("%s %s",item.getNickname(),item.getAccountLast4Num()));
        holder.getTxtTrxAmount().setText(Utils.getFormattedDollarAmount(item.getAmount()));
        holder.getTxtTrxLastFourNum().setText(holder.getTxtTrxLastFourNum().getText().toString().replace("(?)", item.getAtmLast4Num()));

        String dateText = MobileCashUtils.getFormattedExpDate(item.getTrxExpDate(), this);
        String expirationDateText = String.format(getResources().getString(R.string.mc_pickup_expiration),dateText);
        holder.getTxtTrxExpirationDate().setText(expirationDateText);

        if (item.getAtmType().equals("INT")) {
            holder.getImg().setImageResource(R.drawable.account_image_international);
            holder.getTxtAthType().setText(getResources().getString(R.string.mc_ath_international));
        }else
        if(item.getAtmType().equals("REG")){
            if (FeatureFlags.MBMT_417()) {
                holder.getImg().setImageResource(R.drawable.account_image_regular);
            }else {
                holder.getImg().setImageResource(R.drawable.account_image_default);
            }
        }

        return viewItem;
    }

    DialogInterface.OnClickListener blackListAction = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
            dialog.dismiss();
        }
    };

    /**
     * Method that manages the row view for the Pending for Others use case
     * @param item A MobileCashTrx object
     * @return The view
     */
    private View getPendingForOtherView(final MobileCashTrx item) {

        ViewHolderEasyCashPendForOther holder = null;
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View viewItem = inflater.inflate(R.layout.easycash_transaction_list_item, null);
        viewItem.setTag(holder);
        viewItem.setId(item.getId());

        holder = new ViewHolderEasyCashPendForOther(viewItem);

        try {
            if ("true".equals(item.getReceived())) {
                holder.getTxtToOrFrom().setText(String.format(getResources().getString(R.string.easycash_received_from), ContactsManagementUtils.getContactName(mContext, item.getSenderPhone(), contacts).toUpperCase()));

                viewItem.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if(isPhoneInBlacklist){
                            BPAnalytics.logEvent(BPAnalytics.EVENT_MC_SCAN_BLACKLIST_FAILED);
                            AlertDialogParameters params = new AlertDialogParameters(mContext, R.string.enrollment_lite_phone_blacklist, blackListAction);
                            params.setPositiveButtonText(getResources().getString(R.string.ok));
                            Utils.showAlertDialog(params);

                        }else if(isOfacOffensor) {
                            AlertDialogParameters params = new AlertDialogParameters(mContext, R.string.easycash_ofac_hit, blackListAction);
                            params.setPositiveButtonText(getResources().getString(R.string.ok));
                            Utils.showAlertDialog(params);

                        }else {
                            MobileCashUtils.goToMobileCashFromMeToMeRedeem(mContext, item, true);
                        }
                    }
                });
            } else {
                holder.getImageQr().setVisibility(View.INVISIBLE);
                holder.getTxtToOrFrom().setText(String.format(getResources().getString(R.string.easycash_sent_to), ContactsManagementUtils.getContactName(mContext, item.getReceiverPhone(), contacts).toUpperCase()));

                viewItem.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        trxToDeleteReceipt = idReferenceNumberMapping.get(String.valueOf(v.getId()));
                        deleteWithdrawal();
                        return false;
                    }
                });

                viewItem.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        MobileCashUtils.goToMobileTrxReceipt(mContext, true,true, item);
                    }
                });

            }
        }catch (Exception e){
            Log.e("EasyCashHistoryActivity", e.toString());
        }

        holder.getTxtTrxAmount().setText(Utils.getFormattedDollarAmount(item.getAmount()));
        String dateText = MobileCashUtils.getFormattedExpDate(item.getTrxExpDate(), this);
        String expirationDateText = String.format(getResources().getString(R.string.mc_pickup_expiration),dateText);
        holder.getTxtExpirationDate().setText(expirationDateText);
        return viewItem;
    }



    private View getHistoryView(final MobileCashTrx item) {

        ViewHolderEasyCashPendForOther holder = null;
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View viewItem = inflater.inflate(R.layout.easycash_transaction_list_item, null);
        viewItem.setTag(holder);

        viewItem.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                MobileCashUtils.goToMobileTrxReceipt(mContext, false,false, item);
            }
        });

        holder = new ViewHolderEasyCashPendForOther(viewItem);
        holder.getImageQr().setVisibility(View.VISIBLE);

        if(!Utils.isBlankOrNull(item.getStatus())){
            if(item.getStatus().equalsIgnoreCase("SUCCESS")){
                holder.getImageQr().setImageResource(R.drawable.easycash_trx_success);
            }else if(item.getStatus().equalsIgnoreCase("EXCEPTION") || item.getStatus().equalsIgnoreCase("PROCESSING") || item.getStatus().equalsIgnoreCase("PENDING_ATM")){
                holder.getImageQr().setImageResource(R.drawable.easycash_trx_warning);
            }else if(item.getStatus().equalsIgnoreCase("EXPIRED") || item.getStatus().equalsIgnoreCase("ERROR")){
                holder.getImageQr().setImageResource(R.drawable.easycash_trx_error);
            }
        }


        if(Utils.isBlankOrNull(item.getReceiverPhone())){
            String acct = String.format("%s %s",item.getNickname(),item.getAccountLast4Num()).toUpperCase();
            holder.getTxtToOrFrom().setText(acct);
        }
        else if("true".equals(item.getReceived())){
            holder.getTxtToOrFrom().setText(String.format(getResources().getString(R.string.easycash_received_from),ContactsManagementUtils.getContactName(mContext, item.getSenderPhone(), contacts).toUpperCase()));
        }else{
            holder.getTxtToOrFrom().setText(String.format(getResources().getString(R.string.easycash_sent_to),ContactsManagementUtils.getContactName(mContext, item.getReceiverPhone(), contacts).toUpperCase()));
        }

        holder.getTxtTrxAmount().setText(Utils.getFormattedDollarAmount(item.getAmount()));

        switch (item.getStatus()){
            case "EXPIRED": case "PENDING_ATM": case "PROCESSING":
                String expDateText = MobileCashUtils.getFormattedExpDate(item.getTrxExpDate(), this);
                String expirationDateText = String.format(getResources().getString(R.string.mc_pickup_past_expiration),expDateText);
                holder.getTxtExpirationDate().setText(expirationDateText);
                break;

            default:
                String compDateText = MobileCashUtils.getFormattedExpDate(item.getTrxDate(), this);
                String completedDateText = String.format(getResources().getString(R.string.mc_pickup_past_completed),compDateText);
                holder.getTxtExpirationDate().setText(completedDateText);
                break;
        }

        return viewItem;
    }

    DialogInterface.OnClickListener deletePendingFromMeTrxOnClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    deleteTransaction(mContext,trxToDeleteReceipt);
                    break;
                default:
                    break;
            }
            dialog.dismiss();
        }
    };


    DialogInterface.OnClickListener unboundUserOnClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    AutoLoginUtils.registerDevice(mContext, ProductType.CASHDROP.toString(),false, true);
                    break;
                default:
                    break;
            }
            dialog.dismiss();
        }
    };

    private void deleteWithdrawal()
    {
        AlertDialogParameters params = new AlertDialogParameters(mContext,R.string.mc_deletetrx_message,deletePendingFromMeTrxOnClick);
        params.setPositiveButtonText(getResources().getString(R.string.ok));
        params.setNegativeButtonText(getResources().getString(R.string.mc_deletetrx_back).toUpperCase());
        Utils.showAlertDialog(params);
    }

    public void deleteTransaction(final Context mContext,String receiptId)
    {
        MobileCashTasks.cancelTransaction(mContext,receiptId, new MobileCashTasks.MobileCashListener<MobileCashTrxInfo>() {
            @Override
            public void onMobileCashApiResponse(MobileCashTrxInfo result) {
                if(result != null && result.getTransactionStatus() != null
                        && result.getTransactionStatus().equals(MiBancoConstants.MOBILE_CASH_DELETE_SUCCESS)) {
                    BPAnalytics.logEvent(BPAnalytics.EVENT_MC_CANCEL_PRESTAGE_SUCCESSFULL);

                    refreshAllData();

                }else{
                    BPAnalytics.logEvent(BPAnalytics.EVENT_MC_CANCEL_PRESTAGE_FAILED);
                    MobileCashUtils.informativeMessage(mContext, R.string.mc_service_error_message);
                }
            }
        });
    }

    private void refreshAllData()
    {
        pendingForMeLst.clear();
        pendingForOthersLst.clear();
        getHistory(mContext, true);
    }

    private void filterOptionsPicker()
    {
        String[] filteringOptions = {
                getResources().getString(R.string.ec_filter_all),
                getResources().getString(R.string.ec_filter_received),
                getResources().getString(R.string.ec_filter_sent),
                getResources().getString(R.string.ec_filter_forme)
        };


        final Dialog dialog = new AppCompatDialog(this, R.style.Dialog);
        dialog.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.single_list_view, null);
        dialog.setContentView(convertView);
        final ListView lv = (ListView) convertView.findViewById(R.id.list_view_elements);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,filteringOptions);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                filterHistory(position);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void filterHistory(int position){
        switch (position){
            case 0: // ALL
                filteredHistoryLst = historyLst;
                break;

            case 1: // RECEIVED
                filteredHistoryLst = MobileCashUtils.filterReceivedTransactionList(historyLst);
                break;

            case 2: // SENT
                filteredHistoryLst = MobileCashUtils.filterSentTransactionList(historyLst);
                break;

            case 3: // FOR ME
                filteredHistoryLst = MobileCashUtils.filterForMeTransactionList(historyLst);
                break;
        }

        historyLayoutManagement();
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(false);
        menu.findItem(R.id.menu_contact).setVisible(false);

//        menu.findItem(R.id.menu_easycash_faq).setVisible(true);
//        menu.findItem(R.id.menu_easycash_locator).setVisible(true);

        if(!Utils.isBlankOrNull(App.getApplicationInstance().getCustomerPhone(mContext))) {
            menu.findItem(R.id.menu_easycash_unbound).setVisible(true);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_easycash_faq:

                final Intent faqs = new Intent(mContext, EasyCashFaqs.class);
                faqs.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(faqs);

                break;
            case R.id.menu_easycash_locator:
                final Intent locator = new Intent(mContext, EasyCashLocator.class);
                locator.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(locator);

                break;
            case R.id.menu_easycash_unbound:

                AlertDialogParameters params = new AlertDialogParameters(mContext,R.string.easycash_unbound_message,unboundUserOnClick);

                String phoneNumber = App.getApplicationInstance().getCustomerPhone(mContext);
                if(!Utils.isBlankOrNull(phoneNumber)){
                    try {
                        phoneNumber = ContactsManagementUtils.formatPhoneNumber(mContext, phoneNumber, false);
                        String title = String.format(getResources().getString(R.string.easycash_unbound_title), phoneNumber);
                        params.setTitle(title);
                    }catch (Exception e){
                        Log.e("EasyCashHistoryActivity", e.toString());
                    }
                }

                params.setPositiveButtonText(getResources().getString(R.string.yes).toUpperCase());
                params.setNegativeButtonText(getResources().getString(R.string.no).toUpperCase());
                Utils.showAlertDialog(params);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
