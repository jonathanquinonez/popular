package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.PaymentHistoryEntry;
import com.popular.android.mibanco.model.PaymentHistoryEntry.Payee;
import com.popular.android.mibanco.util.FontChanger;
import com.popular.android.mibanco.util.Utils;

import java.util.List;

/**
 * Adapter class to manage payees data in list
 */
public class PayeesAdapter extends BaseAdapter {

    private final List<PaymentHistoryEntry> payees;

    private PaymentHistoryEntry selectedPaymentHistoryEntry;

    private final LayoutInflater inflater;

    private Context context;

    private String allAPayeesValue;

    public PayeesAdapter(final Context context, final List<PaymentHistoryEntry> payees, PaymentHistoryEntry selectedPayee, String allAPayeesValue) {
        this.payees = payees;
        this.selectedPaymentHistoryEntry = selectedPayee;
        this.context = context;
        this.allAPayeesValue = allAPayeesValue;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return payees.size();
    }

    @Override
    public PaymentHistoryEntry getItem(final int position) {
        return payees.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View myConvertView = convertView;
        if (myConvertView == null) {
            myConvertView = inflater.inflate(R.layout.list_item_payee_account, null);
        }

        final PaymentHistoryEntry paymentHistoryEntry = payees.get(position);

        if (paymentHistoryEntry != null) {
            PaymentHistoryEntry.Payee payee = paymentHistoryEntry.getPayee();
            if (payee != null) {
                TextView txtCardNumber = (TextView) myConvertView.findViewById(R.id.txt_number);
                txtCardNumber.setText(payee.getAccountLast4Num());
                txtCardNumber.setVisibility(View.VISIBLE);

                ((TextView) myConvertView.findViewById(R.id.txt_name)).setText(payee.getNickname());

                ImageView imgCard = (ImageView) myConvertView.findViewById(R.id.img_card);
                final int drawableId = Utils.getPayeeDrawableResource(Utils.getValidGlobalPayeeId(payee.getGlobalId()));
                imgCard.setImageResource(drawableId);

                if (payee.getNickname() != null && payee.getNickname().equalsIgnoreCase(context.getString(R.string.all_payees)) && payee.getFrontEndID().equalsIgnoreCase(allAPayeesValue)) {
                    txtCardNumber.setVisibility(View.GONE);
                }

                RadioButton radioPayee = (RadioButton) myConvertView.findViewById(R.id.radio_button);

                Payee selectedPayee = null;
                if (selectedPaymentHistoryEntry != null) {
                    selectedPayee = selectedPaymentHistoryEntry.getPayee();
                }

                if (selectedPayee != null && payee.getFrontEndID().equalsIgnoreCase(selectedPayee.getFrontEndID())) {
                    radioPayee.setChecked(true);
                } else {
                    radioPayee.setChecked(false);
                }

                if (position == payees.size() - 1) {
                    myConvertView.findViewById(R.id.bottom_line).setVisibility(View.GONE);
                } else {
                    myConvertView.findViewById(R.id.bottom_line).setVisibility(View.VISIBLE);
                }

                FontChanger.changeFonts(myConvertView);
            }
        }

        return myConvertView;
    }
}
