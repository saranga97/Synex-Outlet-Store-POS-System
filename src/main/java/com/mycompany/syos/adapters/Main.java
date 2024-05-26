package com.mycompany.syos.adapters;

import com.mycompany.syos.domain.Item;
import com.mycompany.syos.domain.Transaction;
import com.mycompany.syos.frameworks.AppConfig;
import com.mycompany.syos.usecases.PurchaseService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        PurchaseService purchaseService = context.getBean(PurchaseService.class);
        Scanner scanner = new Scanner(System.in);

        List<Item> items = new ArrayList<>();
        System.out.println("Enter item code and quantity (type 'done' to finish):");
        while (true) {
            System.out.print("Item code: ");
            String code = scanner.nextLine();
            if (code.equalsIgnoreCase("done")) break;

            System.out.print("Quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine();  // consume newline

            Item item = purchaseService.getItemByCode(code);
            if (item == null) {
                System.out.println("Item not found, please try again.");
                continue;
            }
            item.setStock(quantity);
            items.add(item);
        }

        System.out.print("Cash tendered: ");
        double cashTendered = scanner.nextDouble();

        //Transaction transaction = purchaseService.processPurchase(items, cashTendered);

//        System.out.println("Transaction Complete. Details:");
//        System.out.println("Total Price: " + transaction.getTotalPrice());
//        System.out.println("Cash Tendered: " + transaction.getCashTendered());
//        System.out.println("Change: " + transaction.getChangeAmount());
    }
}

