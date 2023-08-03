package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.foound.widget.AmazingListView;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.adapter.SelectStatementListAdapter;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.listener.TransactionsListener;
import com.popular.android.mibanco.model.AccountTransactions;
import com.popular.android.mibanco.model.TransactionsCycle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Activity that manages the selection of a statement
 */
public class SelectStatement extends BaseSessionActivity {

    private AmazingListView selectStatementListView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_statement);

        final int currentCycyle = getIntent().getIntExtra("cycle", 1);
        final String account = getIntent().getStringExtra("account");

        selectStatementListView = (AmazingListView) findViewById(R.id.statements_list);

        if(application != null && application.getAsyncTasksManager() != null) {
            application.getAsyncTasksManager().getAccountAvailableCycles(this, account, new TransactionsListener() {

                @Override
                public void onTransactionsUpdated(final AccountTransactions transactions) {
                    final HashMap<String, List<TransactionsCycle>> allCycles = new HashMap<>();

                    List<TransactionsCycle> cycles = transactions.getAvailibleCycles();
                    if (cycles == null) {
                        cycles = new ArrayList<>();
                    }
                    final SimpleDateFormat mdf = new SimpleDateFormat(transactions.getDateFormat());
                    final SimpleDateFormat mdf2 = new SimpleDateFormat("yyyy");
                    for (final TransactionsCycle cycle : cycles) {
                        Date myDate;
                        try {
                            myDate = mdf.parse(cycle.getEndDate());
                        } catch (final Exception ex) {
                            try {
                                myDate = mdf.parse(cycle.getStartDate());
                            } catch (final Exception e) {
                                myDate = Calendar.getInstance().getTime();
                            }
                        }
                        final String header = mdf2.format(myDate);
                        if (allCycles.containsKey(header)) {
                            allCycles.get(header).add(cycle);
                        } else {
                            final List<TransactionsCycle> list = new ArrayList<>();
                            list.add(cycle);
                            allCycles.put(header, list);
                        }

                        try {
                            cycle.setSelected((Integer.parseInt(cycle.getCycle()) == currentCycyle));
                        } catch (final Exception ex) {
                            Log.w("SelectStatement", ex);
                        }
                    }

                    final Comparator<String> comparator = new Comparator<String>() {
                        @Override
                        public int compare(final String lhs, final String rhs) {
                            final SimpleDateFormat mdf = new SimpleDateFormat("yyyy");
                            try {
                                return -mdf.parse(lhs).compareTo(mdf.parse(rhs));
                            } catch (final Exception ex) {
                                Log.e("SelectStatement", ex.toString());
                            }
                            return 0;
                        }
                    };

                    final Set<String> sortedSet = new TreeSet<>(comparator);
                    sortedSet.addAll(allCycles.keySet());

                    List<Pair<String, List<TransactionsCycle>>> statements = new ArrayList<>();
                    for (final String section : sortedSet) {
                        final List<TransactionsCycle> statementGroup = new ArrayList<>();
                        statementGroup.addAll(allCycles.get(section));
                        statements.add(new Pair<>(section, statementGroup));
                    }

                    SelectStatementListAdapter adapter = new SelectStatementListAdapter(SelectStatement.this, statements);
                    selectStatementListView.setPinnedHeaderView(((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_header, selectStatementListView, false));
                    selectStatementListView.setAdapter(adapter);
                    selectStatementListView.setEmptyView(findViewById(R.id.no_statements_text));

                    selectStatementListView.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(final AdapterView<?> arg0, final View arg1, final int arg2, final long arg3) {
                            try {
                                final TransactionsCycle item = (TransactionsCycle) arg0.getAdapter().getItem(arg2);
                                final Intent intent = new Intent();
                                intent.putExtra("cycle", Integer.parseInt(item.getCycle()));
                                intent.putExtra("descripcion", item.getDescription());
                                setResult(RESULT_OK, intent);
                                finish();
                            } catch (final Exception ex) {
                                Log.e("SelectStatement", ex.toString());
                            }
                        }
                    });
                }

                @Override
                public void sessionHasExpired() {
                    application.reLogin(SelectStatement.this);
                }
            });
        }else{
            Toast.makeText(this,R.string.error_occurred,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(false);
        menu.findItem(R.id.menu_contact).setVisible(false);

        return true;
    }
}
