package com.bank.ui;

import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.enums.AccountType;
import com.bank.service.AccountService;
import com.bank.service.CustomerService;

import java.util.List;
import java.util.Scanner;

/**
 * Console sub-menu for account management operations.
 */
public class AccountMenu {

    private static final String DIV = "--------------------------------------------------------------------------------";

    private final Scanner scanner;
    private final AccountService  accountService;
    private final CustomerService customerService;

    public AccountMenu(Scanner scanner, AccountService accountService, CustomerService customerService) {
        this.scanner         = scanner;
        this.accountService  = accountService;
        this.customerService = customerService;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + DIV);
            System.out.println("  ACCOUNT MANAGEMENT");
            System.out.println(DIV);
            System.out.println("  [1]  View accounts by customer");
            System.out.println("  [2]  View account details");
            System.out.println("  [3]  Open new account");
            System.out.println("  [4]  Freeze account");
            System.out.println("  [5]  Unfreeze account");
            System.out.println("  [6]  Close account");
            System.out.println("  [0]  Back");
            System.out.println(DIV);
            System.out.print("  Select: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1": viewByCustomer();  break;
                case "2": viewAccount();     break;
                case "3": openAccount();     break;
                case "4": freezeAccount();   break;
                case "5": unfreezeAccount(); break;
                case "6": closeAccount();    break;
                case "0": back = true;       break;
                default:  System.out.println("  [!] Invalid option.");
            }
        }
    }

    private void viewByCustomer() {
        System.out.print("  Enter Customer ID: ");
        String customerId = scanner.nextLine().trim();
        try {
            Customer c = customerService.getCustomerById(customerId);
            List<Account> accounts = accountService.getAccountsByCustomer(customerId);
            System.out.println("\n  Accounts for: " + c.getFullName());
            System.out.println("  " + DIV);
            System.out.printf("  %-14s %-12s %-10s %-14s %-8s%n",
                    "Account No.", "Type", "Currency", "Balance", "Status");
            System.out.println("  " + DIV);
            accounts.forEach(a -> System.out.printf("  %-14s %-12s %-10s %-14.2f %-8s%n",
                    a.getAccountNumber(), a.getAccountType(), a.getCurrency(), a.getBalance(), a.getStatus()));
        } catch (Exception e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }

    private void viewAccount() {
        System.out.print("  Enter Account ID or Number: ");
        String input = scanner.nextLine().trim();
        try {
            Account a = input.startsWith("BNK") ? accountService.getAccountByNumber(input)
                                                 : accountService.getAccount(input);
            System.out.println("\n  " + DIV);
            System.out.println("  Account Number : " + a.getAccountNumber());
            System.out.println("  Type           : " + a.getAccountType());
            System.out.println("  Currency       : " + a.getCurrency());
            System.out.println("  Balance        : " + String.format("%.2f", a.getBalance()));
            System.out.println("  Available      : " + String.format("%.2f", a.getAvailableBalance()));
            System.out.println("  Interest Rate  : " + a.getInterestRate() + "%");
            System.out.println("  Status         : " + a.getStatus());
            System.out.println("  Opened         : " + a.getOpenedAt().toLocalDate());
            System.out.println("  " + DIV);
        } catch (Exception e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }

    private void openAccount() {
        try {
            System.out.print("  Customer ID: ");
            String customerId = scanner.nextLine().trim();
            System.out.print("  Account type (CHECKING/SAVINGS/BUSINESS/INVESTMENT): ");
            AccountType type = AccountType.valueOf(scanner.nextLine().trim().toUpperCase());
            System.out.print("  Currency (e.g. USD, EUR, GBP): ");
            String currency = scanner.nextLine().trim().toUpperCase();
            Account a = accountService.openAccount(customerId, type, currency);
            System.out.println("  [OK] Account opened. Number: " + a.getAccountNumber());
        } catch (Exception e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }

    private void freezeAccount() {
        System.out.print("  Enter Account ID: ");
        String id = scanner.nextLine().trim();
        try { accountService.freezeAccount(id); System.out.println("  [OK] Account frozen."); }
        catch (Exception e) { System.out.println("  [!] " + e.getMessage()); }
    }

    private void unfreezeAccount() {
        System.out.print("  Enter Account ID: ");
        String id = scanner.nextLine().trim();
        try { accountService.unfreezeAccount(id); System.out.println("  [OK] Account unfrozen."); }
        catch (Exception e) { System.out.println("  [!] " + e.getMessage()); }
    }

    private void closeAccount() {
        System.out.print("  Enter Account ID: ");
        String id = scanner.nextLine().trim();
        try { accountService.closeAccount(id); System.out.println("  [OK] Account closed."); }
        catch (Exception e) { System.out.println("  [!] " + e.getMessage()); }
    }
}
