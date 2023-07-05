package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.TransferHistoryEntry;
import com.popular.android.mibanco.util.FontChanger;
import com.popular.android.mibanco.util.Utils;

import java.util.List;

/**
 * Adapter for the accounts list
 */
public class AccountsAdapter extends BaseAdapter {

    private final List<TransferHistoryEntry> accounts;

    private TransferHistoryEntry selectedTransferHistoryEntry;

    private final LayoutInflater inflater;

    private Context context;

    private String allAccountsValue;

    public AccountsAdapter(final Context context, final List<TransferHistoryEntry> accounts, TransferHistoryEntry selectedAccount, String defaultValue) {
        this.accounts = accounts;
        this.selectedTransferHistoryEntry = selectedAccount;
        this.context = context;
        this.allAccountsValue = defaultValue;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return accounts.size();
    }

    @Override
    public TransferHistoryEntry getItem(final int position) {
        return accounts.get(position);
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

        final TransferHistoryEntry transferHistoryEntry = accounts.get(position);

        if (transferHistoryEntry != null) {
            TransferHistoryEntry.Account account = transferHistoryEntry.getAccount();
            if (account != null) {
                ((TextView) myConvertView.findViewById(R.id.txt_name)).setText(account.getNickname());

                ImageView imgLogo = (ImageView) myConvertView.findViewById(R.id.img_card);
                if (App.getApplicationInstance() != null && App.getApplicationInstance().getCustomerAccountsMap() != null) {
                    CustomerAccount customerAccount = App.getApplicationInstance().getCustomerAccountsMap().get(account.getApiAccountKey() + account.getAccountNumberSuffix());
                    if (customerAccount != null) {
                        Utils.displayAccountImage(imgLogo, customerAccount);
                    } else {
                        imgLogo.setImageResource(R.drawable.account_image_default);
                    }
                } else {
                    imgLogo.setImageResource(R.drawable.account_image_default);
                }

                TextView txtCardNumber = (TextView) myConvertView.findViewById(R.id.txt_number);
                txtCardNumber.setText(account.getAccountLast4Num());
                txtCardNumber.setVisibility(View.VISIBLE);

                if (account.getNickname().equalsIgnoreCase(context.getString(R.string.all_accounts)) && account.getApiAccountKey().equalsIgnoreCase(allAccountsValue)) {
                    txtCardNumber.setVisibility(View.GONE);
                }

                RadioButton radioAccount = (RadioButton) myConvertView.findViewById(R.id.radio_button);

                TransferHistoryEntry.Account selectedAccount = null;
                if (selectedTransferHistoryEntry != null) {
                    selectedAccount = selectedTransferHistoryEntry.getAccount();
                }

                if (selectedAccount != null && account.getApiAccountKey().equalsIgnoreCase(selectedAccount.getApiAccountKey())
                        && account.getAccountNumberSuffix().equalsIgnoreCase(selectedAccount.getAccountNumberSuffix())
                        && account.getAccountSection().equalsIgnoreCase(selectedAccount.getAccountSection())) {
                    radioAccount.setChecked(true);
                } else {
                    radioAccount.setChecked(false);
                }

                if (position == accounts.size() - 1) {
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
