package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.widget.AccountNameAndBalanceItem;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


/**
 * Adapter to fill a Maintenance Balances RecyclerView with balances
 */
public class MaintenanceBalancesAdapter extends RecyclerView.Adapter<MaintenanceBalancesAdapter.ViewHolder> {

    /**
     * Add balances object to fill RecyclerView
     */
    private final List<AccountNameAndBalanceItem> mBalances;
    private final Context context;


    public MaintenanceBalancesAdapter(List<AccountNameAndBalanceItem> mBalances, Context context) {

        this.mBalances = mBalances;
        this.context = context;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView accountNameTextView;
        private final TextView accountNumberTextView;
        private final TextView accountBalanceTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            accountBalanceTextView = itemView.findViewById(R.id.account_balance);
            accountNameTextView = itemView.findViewById(R.id.account_name);
            accountNumberTextView = itemView.findViewById(R.id.account_number);
        }

        public TextView getAccountNameTextView() {
            return accountNameTextView;
        }

        public TextView getAccountNumberTextView() {
            return accountNumberTextView;
        }

        public TextView getAccountBalanceTextView() {
            return accountBalanceTextView;
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context)
                .inflate(R.layout.list_item_maintenance_balances, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MaintenanceBalancesAdapter.ViewHolder holder, int position) {
        AccountNameAndBalanceItem balanceItem = mBalances.get(position);

        // Set account name
        holder.getAccountNameTextView().setText(balanceItem.getAccountName());

        // Set account number
        holder.getAccountNumberTextView().setText(balanceItem.getAccountSuffix());

        // Set account balance
        TextView balanceTextView = holder.getAccountBalanceTextView();
        balanceTextView.setText(balanceItem.getBalance());

        // Change color if negative
        if (balanceItem.isRedBalance()) {
            balanceTextView.setTextColor(ContextCompat.getColor(context,
                    R.color.maintenance_balance_negative_color));
        }
    }


    @Override
    public int getItemCount() {
        return mBalances.size();
    }
}
