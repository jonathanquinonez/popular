package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.AccountTransaction;
import com.popular.android.mibanco.model.AccountTransactions;

import java.util.List;

/**
 * Adapter class to manage data in the view pager
 */
public class ViewPagerEasyCashAdapter extends PagerAdapter {

    private final static int MAX_TAB_TITLES = 3;

    private static String[] tabTitles;
    private final Context context;


    private final LayoutInflater inflater;

    private final AccountTransactions transactionsObject;

    public ViewPagerEasyCashAdapter(final Context context, final AccountTransactions transactionsObject) {
        this.context = context;
        this.transactionsObject = transactionsObject;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        tabTitles = new String[MAX_TAB_TITLES];
        tabTitles[0] = context.getString(R.string.received).toUpperCase();
        tabTitles[1] = context.getString(R.string.all).toUpperCase();
        tabTitles[2] = context.getString(R.string.sent).toUpperCase();
    }

    @Override
    public void destroyItem(final View pager, final int position, final Object view) {
        ((ViewPager) pager).removeView((View) view);
    }

    @Override
    public void finishUpdate(final View view) {
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

        updateListView(list, transactionsObject, v, position);
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

    public void updateListView(final ListView listView, final AccountTransactions transactionsObject, final View v, final int position) {
//        final TransactionsCycle transactionsCycle = transactionsObject.getCycle(currentCycle);

        TransactionListAdapter listAdapter;
        List<AccountTransaction> filteredTransactions;
        switch (position) {
        case 0:
//            filteredTransactions = AccountStatementsLoader.filter("-", transactions);
//            listAdapter = new TransactionListAdapter(context, filteredTransactions, AccountStatementsLoader.getStatementRangeString(context, transactionsCycle, filteredTransactions));
            break;
        case 1:
//            filteredTransactions = AccountStatementsLoader.filter("", transactions);
//            listAdapter = new TransactionListAdapter(context, filteredTransactions, AccountStatementsLoader.getStatementRangeString(context, transactionsCycle, filteredTransactions));
            break;
        case 2:
//            filteredTransactions = AccountStatementsLoader.filter("+", transactions);
//            listAdapter = new TransactionListAdapter(context, filteredTransactions, AccountStatementsLoader.getStatementRangeString(context, transactionsCycle, filteredTransactions));
            break;
        default:
//            filteredTransactions = AccountStatementsLoader.filter("", transactions);
//            listAdapter = new TransactionListAdapter(context, filteredTransactions, AccountStatementsLoader.getStatementRangeString(context, transactionsCycle, filteredTransactions));
            break;
        }

//        listView.setAdapter(listAdapter);
//        listAdapter.notifyDataSetChanged();
    }
}
