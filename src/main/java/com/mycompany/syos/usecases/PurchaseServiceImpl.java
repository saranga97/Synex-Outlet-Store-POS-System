package com.mycompany.syos.usecases;

import com.mycompany.syos.domain.Item;
import com.mycompany.syos.domain.Transaction;
import com.mycompany.syos.domain.TransactionItem;
import com.mycompany.syos.frameworks.database.ItemRepository;
import com.mycompany.syos.frameworks.database.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final ItemRepository itemRepository;
    private final TransactionRepository transactionRepository;

    public PurchaseServiceImpl(ItemRepository itemRepository, TransactionRepository transactionRepository) {
        this.itemRepository = itemRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction processPurchase(List<Item> items, double cashTendered, BigDecimal discount, Date date) {
        Transaction transaction = new Transaction();
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<TransactionItem> transactionItems = new ArrayList<>();

        for (Item item : items) {
            Item dbItem = itemRepository.getItemByCode(item.getCode());
            if (dbItem == null || dbItem.getStock() < item.getStock()) {
                throw new IllegalArgumentException("Item not available or insufficient stock for code: " + item.getCode());
            }
            totalPrice = totalPrice.add(BigDecimal.valueOf(dbItem.getPrice()).multiply(BigDecimal.valueOf(item.getStock())));
            TransactionItem transactionItem = new TransactionItem();
            transactionItem.setItem(dbItem);
            transactionItem.setQuantity(item.getStock());
            transactionItems.add(transactionItem);
            itemRepository.reduceStock(dbItem.getCode(), item.getStock());
        }

        if (discount.compareTo(totalPrice) > 0) {
            throw new IllegalArgumentException("Discount cannot be greater than total price.");
        }

        totalPrice = totalPrice.subtract(discount);

        transaction.setTotalPrice(totalPrice);
        transaction.setCashTendered(BigDecimal.valueOf(cashTendered));
        transaction.setChangeAmount(BigDecimal.valueOf(cashTendered).subtract(totalPrice));
        transaction.setItems(transactionItems);
        transaction.setDiscount(discount);
        transaction.setDate(date);

        transactionRepository.save(transaction);
        return transaction;
    }

    @Override
    public Item getItemByCode(String code) {
        return itemRepository.getItemByCode(code);
    }

    @Override
    public List<Transaction> getTransactionsByDate(Date date) {
        return transactionRepository.getTransactionsByDate(date);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.getAllTransactions();
    }

    @Override
    public List<Item> getItemsToReshelve() {
        return itemRepository.getItemsToReshelve();
    }

    @Override
    public List<Item> getReorderLevels() {
        return itemRepository.getReorderLevels();
    }

    @Override
    public List<Item> getCurrentStock() {
        return itemRepository.getAllItems();
    }
}
