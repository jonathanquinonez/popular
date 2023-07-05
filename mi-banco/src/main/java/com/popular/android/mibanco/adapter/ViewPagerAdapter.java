package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.FeatureFlags;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.AccountTransaction;
import com.popular.android.mibanco.model.AccountTransactions;
import com.popular.android.mibanco.model.TransactionsCycle;
import com.popular.android.mibanco.util.AccountStatementsLoader;

import java.util.List;

/**
 * Adapter class to manage data in the view pager
 */
public class ViewPagerAdapter extends PagerAdapter {

    private final static int MAX_TAB_TITLES = 3;

    private final static int MAX_TAB_TITLES_NONTRANS = 1;

    private static String[] tabTitles;

    private String accountNumber;

    private String accountSubtype;

    private final Context context;

    private int currentCycle;

    private final LayoutInflater inflater;

    private final List<AccountTransaction> transactionsList;

    private final AccountTransactions transactionsObject;

    public ViewPagerAdapter(final Context context, final AccountTransactions transactionsObject, final List<AccountTransaction> transactionsList, final int cycle, final String accNr, final String accSubtype) {
        this.context = context;
        this.transactionsList = transactionsList;
        this.transactionsObject = transactionsObject;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (App.isSelectedNonTransactioanlAcct(accSubtype)) {
            tabTitles = new String[MAX_TAB_TITLES_NONTRANS];
        }
        else {
            tabTitles = new String[MAX_TAB_TITLES];
            tabTitles[0] = context.getString(R.string.debit).toUpperCase();
            tabTitles[1] = context.getString(R.string.all).toUpperCase();
            tabTitles[2] = context.getString(R.string.credit).toUpperCase();
        }

        currentCycle = cycle;
        accountNumber = accNr;
        accountSubtype = accSubtype;
    }

    @Override
    public void destroyItem(final View pager, final int position, final Object view) {
        ((ViewPager) pager).removeView((View) view);
    }

    @Override
    public void finishUpdate(final View view) {
    }

    public String getAccNr() {
        return accountNumber;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public String getPageTitle(final int position) {
        return tabTitles[position];
    }

    @Override
    public Object instantiateItem(final View pager, final int position) {
        final View v = inflater.inflate(R.layout.account_details_page, null);
        final ListView list = (ListView) v.findViewById(R.id.transaction_list);

        updateListView(list, transactionsList, v, position);
        ((ViewPager) pager).addView(v, 0);

        return v;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(final Parcelable p, final ClassLoader c) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(final View view) {
    }

    public void updateListView(final ListView listView, final List<AccountTransaction> transactions, final View v, final int position) {
        final TransactionsCycle transactionsCycle = transactionsObject.getCycle(currentCycle);

        TransactionListAdapter listAdapter;
        List<AccountTransaction> filteredTransactions;

        if (FeatureFlags.MBCA_104() &&  App.isSelectedNonTransactioanlAcct(accountSubtype)) {
            filteredTransactions = AccountStatementsLoader.filter("", transactions);
        }
        else {
            switch (position) {
                case 0:
                    filteredTransactions = AccountStatementsLoader.filter("-", transactions);
                    break;
                case 1:
                    filteredTransactions = AccountStatementsLoader.filter("", transactions);
                    break;
                case 2:
                    filteredTransactions = AccountStatementsLoader.filter("+", transactions);
                    break;
                default:
                    filteredTransactions = AccountStatementsLoader.filter("", transactions);
                    break;
            }
        }

        listAdapter = new TransactionListAdapter(context, filteredTransactions, AccountStatementsLoader.getStatementRangeString(context, transactionsCycle, filteredTransactions), accountSubtype, accountNumber);
        listView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
    }
}
