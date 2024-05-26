package com.mycompany.syos.usecases;

import com.mycompany.syos.domain.Item;
import com.mycompany.syos.domain.Transaction;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface PurchaseService {
    Transaction processPurchase(List<Item> items, double cashTendered, BigDecimal discount, Date date);
    Item getItemByCode(String code);
    List<Transaction> getTransactionsByDate(Date date);
    List<Transaction> getAllTransactions();
    List<Item> getItemsToReshelve();
    List<Item> getReorderLevels();
    List<Item> getCurrentStock();
}
