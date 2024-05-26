package com.mycompany.syos.adapters;

import com.mycompany.syos.domain.Item;
import com.mycompany.syos.domain.Transaction;
import com.mycompany.syos.usecases.PurchaseService;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<?> processPurchase(@RequestBody PurchaseRequest request) {
        try {
            List<Item> items = new ArrayList<>();
            for (PurchaseRequestItem reqItem : request.getItems()) {
                Item item = purchaseService.getItemByCode(reqItem.getCode());
                if (item == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Item not found: " + reqItem.getCode());
                }
                item.setStock(reqItem.getQuantity());
                items.add(item);
            }
            double cashTendered = request.getCashTendered();
            BigDecimal discount = BigDecimal.valueOf(request.getDiscount());
            Date date;
            if (request.getDate() == null || request.getDate().isEmpty()) {
                date = new Date(); // Default to current date if not provided
            } else {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(request.getDate());
            }

            Transaction transaction = purchaseService.processPurchase(items, cashTendered, discount, date);
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging purposes
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @GetMapping("/totalSale")
    public ResponseEntity<?> getTotalSale(@RequestParam String date) {
        try {
            Date parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            List<Transaction> transactions = purchaseService.getTransactionsByDate(parsedDate);
            BigDecimal totalSale = transactions.stream()
                    .map(Transaction::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return ResponseEntity.ok(totalSale);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactionsByDate() {
        try {
            List<Transaction> transactions = purchaseService.getAllTransactions();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/todayTransactions")
    public ResponseEntity<List<Transaction>> getTodayTransactions() {
        try {
            Date today = new Date(); // Get today's date
            List<Transaction> transactions = purchaseService.getTransactionsByDate(today);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    static class PurchaseRequest {

        private List<PurchaseRequestItem> items;
        private double cashTendered;
        private double discount;
        private String date;

        // Getters and setters for all fields
        public List<PurchaseRequestItem> getItems() {
            return items;
        }

        public void setItems(List<PurchaseRequestItem> items) {
            this.items = items;
        }

        public double getCashTendered() {
            return cashTendered;
        }

        public void setCashTendered(double cashTendered) {
            this.cashTendered = cashTendered;
        }

        public double getDiscount() {
            return discount;
        }

        public void setDiscount(double discount) {
            this.discount = discount;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    static class PurchaseRequestItem {

        private String code;
        private int quantity;

        // Getters and setters for all fields
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
