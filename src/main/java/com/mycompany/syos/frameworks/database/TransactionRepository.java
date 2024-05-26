package com.mycompany.syos.frameworks.database;

import com.mycompany.syos.domain.Transaction;

import java.util.Date;
import java.util.List;

public interface TransactionRepository {
    void save(Transaction transaction);
    List<Transaction> getTransactionsByDate(Date date);
    List<Transaction> getAllTransactions();
}
