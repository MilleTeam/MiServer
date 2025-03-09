package com.github.mille.team.inventory;

import com.github.mille.team.utils.queue.Queue;

import java.util.Set;

/**
 * author: MagicDroidX Nukkit Project
 */
public interface TransactionGroup {

    int DEFAULT_ALLOWED_RETRIES = 5;

    Set<Inventory> getInventories();

    Queue<Transaction> getTransactions();

    int getTransactionCount();

    void addTransaction(Transaction transaction);

    boolean execute();

}
