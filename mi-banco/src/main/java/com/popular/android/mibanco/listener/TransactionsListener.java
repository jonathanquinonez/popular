package com.popular.android.mibanco.listener;

import com.popular.android.mibanco.model.AccountTransactions;

/**
 * Interface for transactions listener
 */
public interface TransactionsListener extends SessionListener {
    void onTransactionsUpdated(AccountTransactions transactions);
}
