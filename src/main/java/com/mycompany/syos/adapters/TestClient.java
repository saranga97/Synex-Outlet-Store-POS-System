
package com.mycompany.syos.adapters;

import com.mycompany.syos.domain.Item;
import com.mycompany.syos.domain.Transaction;
import com.mycompany.syos.frameworks.AppConfig;
import com.mycompany.syos.usecases.PurchaseService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestClient {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        PurchaseService purchaseService = context.getBean(PurchaseService.class);

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        Runnable task = () -> {
            List<Item> items = Arrays.asList(
                    new Item("001", "Apple", 1.0, 3),
                    new Item("002", "Banana", 0.5, 5)
            );
            double cashTendered = 10.0;

//            Transaction transaction = purchaseService.processPurchase(items, cashTendered);
//
//            System.out.println("Transaction Complete. Total Price: " + transaction.getTotalPrice());
        };

        for (int i = 0; i < 10; i++) {
            executorService.submit(task);
        }

        executorService.shutdown();
    }
}
