package com.bank.ui;

import com.bank.model.Transaction;
import com.bank.service.AccountService;
import com.bank.service.TransactionService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class TransactionMenu {

    private static final String DIV = "--------------------------------------------------------------------------------";
    private final Scanner scanner;
    private final TransactionService transactionService;
    private final AccountService accountService;

    public TransactionMenu(Scanner scanner, TransactionService transactionService, AccountService accountService) {
        this.scanner = scanner;
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + DIV);
            System.out.println("  TRANSACTIONS");
            System.out.println(DIV);
            System.out.println("  [1]  Deposit");
            System.out.println("  [2]  Withdraw");
            System.out.println("  [3]  Transfer");
            System.out.println("  [4]  Transaction history");
            System.out.println("  [5]  Reverse transaction");
            System.out.println("  [0]  Back");
            System.out.println(DIV);
            System.out.print("  Select: ");
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1": deposit();   break;
                case "2": withdraw();  break;
                case "3": transfer();  break;
                case "4": history();   break;
                case "5": reverse();   break;
                case "0": back = true; break;
                default:  System.out.println("  [!] Invalid option.");
            }
        }
    }

    private void deposit() {
        try {
            System.out.print("  Account ID: ");
            String id = scanner.nextLine().trim();
            System.out.print("  Amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            System.out.print("  Description: ");
            String desc = scanner.nextLine().trim();
            Transaction tx = transactionService.deposit(id, amount, desc);
            System.out.printf("  [OK] Deposited. Ref: %s  New balance: %.2f%n",
                    tx.getReferenceCode(), accountService.getBalance(id));
        } catch (Exception e) { System.out.println("  [!] " + e.getMessage()); }
    }

    private void withdraw() {
        try {
            System.out.print("  Account ID: ");
            String id = scanner.nextLine().trim();
            System.out.print("  Amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            System.out.print("  Description: ");
            String desc = scanner.nextLine().trim();
            Transaction tx = transactionService.withdraw(id, amount, desc);
            System.out.printf("  [OK] Withdrawn. Ref: %s  New balance: %.2f%n",
                    tx.getReferenceCode(), accountService.getBalance(id));
        } catch (Exception e) { System.out.println("  [!] " + e.getMessage()); }
    }

    private void transfer() {
        try {
            System.out.print("  From Account ID: ");
            String fromId = scanner.nextLine().trim();
            System.out.print("  To Account ID: ");
            String toId = scanner.nextLine().trim();
            System.out.print("  Amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            System.out.print("  Description: ");
            String desc = scanner.nextLine().trim();
            Transaction tx = transactionService.transfer(fromId, toId, amount, desc);
            System.out.println("  [OK] Transfer complete. Ref: " + tx.getReferenceCode());
        } catch (Exception e) { System.out.println("  [!] " + e.getMessage()); }
    }

    private void history() {
        System.out.print("  Account ID: ");
        String id = scanner.nextLine().trim();
        List<Transaction> list = transactionService.getHistory(id);
        System.out.println("\n  Transaction History");
        System.out.println("  " + DIV);
        System.out.printf("  %-20s %-12s %-14s %-10s %-20s%n", "Date/Time","Type","Amount","Status","Description");
        System.out.println("  " + DIV);
        if (list.isEmpty()) {
            System.out.println("  No transactions found.");
        } else {
            list.forEach(t -> {
                String d = t.getDescription().length() > 20 ? t.getDescription().substring(0, 20) : t.getDescription();
                System.out.printf("  %-20s %-12s %-14.2f %-10s %-20s%n",
                        t.getTimestamp().toString().substring(0, 19), t.getType(), t.getAmount(), t.getStatus(), d);
            });
        }
        System.out.println("  " + DIV);
    }

    private void reverse() {
        System.out.print("  Transaction ID: ");
        String id = scanner.nextLine().trim();
        try { transactionService.reverseTransaction(id); System.out.println("  [OK] Transaction reversed."); }
        catch (Exception e) { System.out.println("  [!] " + e.getMessage()); }
    }
}
