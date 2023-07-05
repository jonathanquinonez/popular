package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.adapter.ListEBillsAdapter;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.model.EBillsItem;
import com.popular.android.mibanco.object.ListItemEBills;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.DialogHolo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Provides implementation of e-Bills screen.
 */
public class EBill extends BaseSessionActivity {

    /**
     * The Constant AMOUNT_PICKER_REQUEST_CODE.
     */
    private final static int AMOUNT_PICKER_REQUEST_CODE = 2;

    private final static String CURRENT_ITEM_KEY = "current_time";

    /**
     * The current item.
     */
    private ListItemEBills currentItem;

    /**
     * The ebills list.
     */
    private ListView ebillsList;

    /**
     * Fetch data.
     */
    private void fetchData() {
        ((App) getApplication()).getAsyncTasksManager().fetchPayments(this, new ResponderListener() {

            @Override
            public void responder(final String responderName, final Object data) {
                fillEbillsList();
            }

            @Override
            public void sessionHasExpired() {
                ((App) getApplication()).reLogin(EBill.this);
            }
        }, true, false);
    }

    /**
     * Fill Ebills list.
     */
    private void fillEbillsList() {
        if (App.getApplicationInstance().getValidEbills() == null || ebillsList == null) {
            return;
        }

        final ArrayList<ListItemEBills> items = new ArrayList<ListItemEBills>();
        for (int i = 0; i < App.getApplicationInstance().getValidEbills().size(); ++i) {
            final EBillsItem ebill = App.getApplicationInstance().getValidEbills().get(i);
            final int drawableId = Utils.getPayeeDrawableResource(ebill.getPayeeNumber());
            items.add(new ListItemEBills(i, drawableId, ebill.getAccountNickname(), ebill.getInvoiceDateString(EBill.this), ebill.getDueDateString(), ebill.getAmountDue(), ebill.getMinAmount(), ebill
                    .getPayeeNumber(), ebill.getLast4AcctNumber(), ebill.hasMinAmount(), MiBancoConstants.EBILL_DATE_FORMAT));
        }

        ebillsList.setAdapter(new ListEBillsAdapter(this, this, R.layout.list_item_ebills, items));
    }

    public ListItemEBills getCurrentItem() {
        return currentItem;
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AMOUNT_PICKER_REQUEST_CODE:
                    final int amount = data.getIntExtra("amount", 0);
                    prefillPaymentsScreen(currentItem, amount);
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ebill);

        ebillsList = (ListView) findViewById(R.id.list_view);
        ebillsList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> paramAdapterView, final View paramView, final int position, final long id) {
                final ListEBillsAdapter adapter = (ListEBillsAdapter) ((ListView) paramAdapterView).getAdapter();
                final ListItemEBills item = (ListItemEBills) adapter.getItem(position);

                if (item.hasMinDueAmount()) {
                    setCurrentItem(item);
                    showAmountDialog(item);
                } else {
                    prefillPaymentsScreen(item, false);
                }
            }
        });
        fetchData();

        if (savedInstanceState != null) {
            currentItem = (ListItemEBills) savedInstanceState.getSerializable(CURRENT_ITEM_KEY);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CURRENT_ITEM_KEY, currentItem);
        super.onSaveInstanceState(outState);
    }

    /**
     * Prefills payments screen with e-Bill details.
     *
     * @param item         the selected e-Bills list item
     * @param useMinAmount should we use the minimum amount provided by e-Bill?
     */
    public void prefillPaymentsScreen(final ListItemEBills item, final boolean useMinAmount) {
        if (useMinAmount) {
            prefillPaymentsScreen(item, App.getIntAmount(item.getMinAmount()));
        } else {
            prefillPaymentsScreen(item, App.getIntAmount(item.getAmount()));
        }
    }

    /**
     * Prefills payments screen with e-Bill details.
     *
     * @param item   the selected e-Bills list item
     * @param amount the amount due
     */
    public void prefillPaymentsScreen(final ListItemEBills item, final int amount) {
        final SimpleDateFormat sdf = new SimpleDateFormat(item.getEbillDateFormat());

        final Intent intent = new Intent(EBill.this, Payments.class);
        Bundle request = new Bundle();
        request.putInt("payeeId", item.getPayeeId());
        request.putString("payeeCode", item.getPayeeLast4Num());
        request.putInt("ebillAmount", amount);
        try {
            request.putSerializable("ebillDate", sdf.parse(item.getDueDate()));
        } catch (final ParseException e) {
            Log.e("Payments", "Error parsing effective date.");
            Utils.showAlert(EBill.this, getString(R.string.transaction_failed_title), getString(R.string.transaction_failed_message));
            return;
        }
        App.getApplicationInstance().setEbillRequest(request);
        startActivity(intent);
        finish();
    }

    public void setCurrentItem(final ListItemEBills currentItem) {
        this.currentItem = currentItem;
    }

    /**
     * Show amount dialog.
     *
     * @param item the item
     */
    public void showAmountDialog(final ListItemEBills item) {
        final DialogHolo dialog = new DialogHolo(EBill.this);
        final View customView = dialog.setCustomContentView(R.layout.ebill_dialog_amount);
        dialog.setTitle(item.getTitle());
        dialog.setCancelable(true);
        dialog.setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {

            @Override
            public void onClick(final View paramView) {
                Utils.dismissDialog(dialog);
            }
        });
        dialog.setPositiveButton(getString(R.string.select), new View.OnClickListener() {

            @Override
            public void onClick(final View paramView) {
                if (((RadioButton) customView.findViewById(R.id.radio_min)).isChecked()) {
                    Utils.dismissDialog(dialog);
                    prefillPaymentsScreen(item, true);
                } else if (((RadioButton) customView.findViewById(R.id.radio_due)).isChecked()) {
                    Utils.dismissDialog(dialog);
                    prefillPaymentsScreen(item, false);
                } else {
                    final Intent intent = new Intent(EBill.this, EnterAmount.class);
                    intent.putExtra("amount", 0);
                    EBill.this.startActivityForResult(intent, AMOUNT_PICKER_REQUEST_CODE);
                    Utils.dismissDialog(dialog);
                }
            }
        });

        ((TextView) customView.findViewById(R.id.title)).setText(String.format(getString(R.string.ebills_choose_amount_text), item.getTitle() + " " + item.getPayeeLast4Num(),
                Utils.addCurrencySign(item.getMinAmount()), Utils.addCurrencySign(item.getAmount())));
        ((Button) dialog.findViewById(R.id.radio_due)).setText(String.format(getString(R.string.ebills_pay), Utils.addCurrencySign(item.getAmount())));
        ((Button) dialog.findViewById(R.id.radio_min)).setText(String.format(getString(R.string.ebills_pay), Utils.addCurrencySign(item.getMinAmount())));

        Utils.showDialog(dialog, EBill.this);
    }
}
