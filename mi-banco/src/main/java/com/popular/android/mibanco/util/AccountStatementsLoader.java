package com.popular.android.mibanco.util;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.listener.SimpleListener;
import com.popular.android.mibanco.listener.TransactionsListener;
import com.popular.android.mibanco.model.AccountTransaction;
import com.popular.android.mibanco.model.AccountTransactions;
import com.popular.android.mibanco.model.TransactionsCycle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * TransactionHelper provides transactions fetching with support for paging.
 */
public class AccountStatementsLoader {

    /**
     * Filter.
     * 
     * @param token the token
     * @param transactions the transactions
     * @return the list
     */
    public static List<AccountTransaction> filter(final String token, final List<AccountTransaction> transactions) {
        final ArrayList<AccountTransaction> filteredTransactions = new ArrayList<AccountTransaction>();
        if (transactions != null) {
            if (token == null || token.length() == 0) {
                filteredTransactions.addAll(transactions);
            } else {
                if (token.length() == 1) {
                    for (final AccountTransaction item : transactions) {
                        if (item != null && token.equals(item.getSign())) {
                            filteredTransactions.add(item);
                        }
                    }
                } else {
                    for (final AccountTransaction item : transactions) {
                        if (item != null && item.getDescription() != null && item.getDescription().toLowerCase().contains(token.toLowerCase())) {
                            filteredTransactions.add(item);
                        }
                    }
                }
            }
        }

        return filteredTransactions;
    }

    /**
     * Gets the statement range string.
     * 
     * @param context the context
     * @param transactionsCycle the transactions cycle
     * @param transactions the transactions
     * @return the statement range string
     */
    public static String getStatementRangeString(final Context context, final TransactionsCycle transactionsCycle, final List<AccountTransaction> transactions) {
        final SimpleDateFormat sdf = new SimpleDateFormat(MiBancoConstants.WEBSERVICE_DATE_FORMAT);
        String rangeString = "";
        try {
            if (transactions.size() <= 0) {
                rangeString = context.getString(R.string.nothing_to_display);
            } else if (transactionsCycle == null || Utils.isBlankOrNull(transactionsCycle.getStartDate())) {
                rangeString = context.getString(R.string.current_statement).toUpperCase();
            } else {

                Date startDate = null;
                Date endDate = null;

                try {

                    if(transactionsCycle != null && !Utils.isBlankOrNull(transactionsCycle.getStartDate()) && !Utils.isBlankOrNull(transactionsCycle.getEndDate())) {
                        startDate = sdf.parse(transactionsCycle.getStartDate());
                        endDate = sdf.parse(transactionsCycle.getEndDate());

                        final String startDateString = DateFormat.getMediumDateFormat(context).format(startDate);
                        final String endDateString = DateFormat.getMediumDateFormat(context).format(endDate);

                        rangeString = String.format(context.getString(R.string.statement_date_range), startDateString, endDateString);
                    }

                } catch (final ParseException e) {
                    Log.w("AccountStatementsLoader", e);
                }
            }
        } catch (final Error e) {
            Log.w("AccountStatementsLoader", e);
        }

        return rangeString;
    }

    /**
     * Sorts a collection.
     * 
     * @param collection the collection to sort
     */
    private static void sortCollection(final List<AccountTransaction> collection) {
        final SimpleDateFormat sdf = new SimpleDateFormat(MiBancoConstants.WEBSERVICE_DATE_FORMAT);
        Collections.sort(collection, new Comparator<AccountTransaction>() {

            @Override
            public int compare(final AccountTransaction param1, final AccountTransaction param2) {
                try {
                    final Date date1 = sdf.parse(param1.getPostedDate());
                    final Date date2 = sdf.parse(param2.getPostedDate());

                    final long diff = date2.getTime() - date1.getTime();

                    if (diff > 0) {
                        return 1;
                    } else if (diff < 0) {
                        return -1;
                    }
                } catch (final ParseException e) {
                    Log.w("AccountStatementsLoader", e);
                }

                return 0;
            }
        });
    }

    /** The account number. */
    private final String accountNumber;

    /** The sorted transactions to display. */
    private List<AccountTransaction> allSortedTransactions;

    /** The application instance. */
    private final App application;

    /** Determines whether button for retrieving more transaction has been pressed yet. */
    private boolean buttonMorePressed;

    /** The button for getting more transactions. */
    private final Button buttonMoreTransactions;

    /** The corresponding Activity. */
    private final Context context;

    /** The sorted current transactions to display. */
    private List<AccountTransaction> currentSortedTransactions;

    /** The transactions fetched with the last request. */
    private AccountTransactions currentTransactions;

    /** The transactions current cycle. */
    private final int cycle;

    /** The sorted in process transactions to display. */
    private List<AccountTransaction> inProcessSortedTransactions;

    /** The listener. */
    private final SimpleListener listener;

    /** The number of loaded pages for the current request. */
    private int loadedPages;

    /** Should we show all transactions for the statement?. */
    private boolean showAllTransactions;

    /** The transactions current cycle's pages. */
    private int totalPages;

    /**
     * Instantiates a new transaction helper.
     * 
     * @param context the context
     * @param application the application
     * @param buttonMoreTransactions the button more transactions
     * @param accountNumber the account number
     * @param cycle the cycle
     * @param listener the listener
     */
    public AccountStatementsLoader(final Context context, final App application, final Button buttonMoreTransactions, final String accountNumber, final int cycle, final SimpleListener listener) {
        this.context = context;
        this.application = application;
        this.buttonMoreTransactions = buttonMoreTransactions;
        this.cycle = cycle;
        this.accountNumber = accountNumber;
        this.listener = listener;

        buttonMoreTransactions.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                showAllTransactions = true;
                buttonMorePressed = true;
                AccountStatementsLoader.this.buttonMoreTransactions.setVisibility(View.GONE);
                refreshList();
            }
        });

        showAllTransactions = false;
    }

    public List<AccountTransaction> getAllSortedTransactions() {
        return allSortedTransactions;
    }

    public AccountTransactions getCurrentTransactions() {
        return currentTransactions;
    }

    public int getCycle() {
        return cycle;
    }

    /**
     * Gets a page of transactions from web service.
     * 
     * @param page the page number
     * @param transactionsListener the listener
     */
    private void getPage(final int page, final TransactionsListener transactionsListener) {
        application.getAsyncTasksManager().getTransactions(context, accountNumber, cycle, page, transactionsListener);
    }

    /**
     * Loads a page retrieved from web service into memory.
     */
    private void loadPage() {
        if (currentTransactions != null) {
            final ArrayList<AccountTransaction> inProcessTransactions = currentTransactions.getAllInProcessTransactions();
            if (inProcessTransactions != null) {
                for (int j = inProcessTransactions.size() - 1; j >= 0; --j) {
                    final AccountTransaction transaction = inProcessTransactions.get(j);
                    transaction.SetDebit(true);
                    inProcessSortedTransactions.add(transaction);
                }
            }

            final ArrayList<AccountTransaction> currentCycleTransactions = currentTransactions.getAllCurrentTransactions();
            if (currentCycleTransactions != null) {
                currentSortedTransactions.addAll(currentCycleTransactions);
            }
        }
    }

    /**
     * Refreshes the transactions list.
     */
    public void refreshList() {
        getPage(1, new TransactionsListener() {

            @Override
            public void onTransactionsUpdated(final AccountTransactions transactions) {
                totalPages = transactions.getTotalPages();
                currentTransactions = transactions;
                if (!buttonMorePressed && totalPages > 2) {
                    buttonMoreTransactions.setVisibility(View.VISIBLE);
                }
                if (totalPages == 1) {
                    showRequestedPages(1, 1);
                } else if (showAllTransactions) {
                    showRequestedPages(1, totalPages);
                } else {
                    if (totalPages == 2) {
                        showRequestedPages(1, 2);
                    } else {
                        showRequestedPages(totalPages - 1, totalPages);
                    }
                }
            }

            @Override
            public void sessionHasExpired() {
                ((App) ((Activity) context).getApplication()).reLogin(context);
            }
        });
    }

    /**
     * Show requested pages.
     * 
     * @param firstPage the first page to display
     * @param lastPage the last page to display
     */
    private void showRequestedPages(final int firstPage, final int lastPage) {
        final int requestedPages = lastPage - firstPage + 1;
        loadedPages = 0;
        currentSortedTransactions = new LinkedList<AccountTransaction>();
        inProcessSortedTransactions = new ArrayList<AccountTransaction>();

        for (int i = firstPage; i <= lastPage; ++i) {
            getPage(i, new TransactionsListener() {

                @Override
                public void onTransactionsUpdated(final AccountTransactions transactions) {
                    currentTransactions = transactions;
                    loadPage();
                    ++loadedPages;
                    if (loadedPages >= requestedPages) {
                        allSortedTransactions = new ArrayList<AccountTransaction>();
                        allSortedTransactions.addAll(inProcessSortedTransactions);
                        AccountStatementsLoader.sortCollection(currentSortedTransactions);
                        allSortedTransactions.addAll(currentSortedTransactions);

                        if (listener != null) {
                            listener.done();
                        }
                    }
                }

                @Override
                public void sessionHasExpired() {
                    ((App) ((Activity) context).getApplication()).reLogin(context);
                }
            });
        }
    }
}
