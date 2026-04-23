package com.bank;

import com.bank.data.DataSeeder;
import com.bank.repository.*;
import com.bank.service.*;
import com.bank.ui.ConsoleUI;

/**
 * Banking System — Java 8 Implementation
 * Entry point: wires all layers and launches the console UI.
 */
public class BankApplication {

    public static void main(String[] args) {
        // Repositories
        CustomerRepository    customerRepo    = new CustomerRepository();
        AccountRepository     accountRepo     = new AccountRepository();
        TransactionRepository transactionRepo = new TransactionRepository();
        CardRepository        cardRepo        = new CardRepository();
        LoanRepository        loanRepo        = new LoanRepository();
        BranchRepository      branchRepo      = new BranchRepository();

        // Services
        CustomerService    customerService    = new CustomerService(customerRepo);
        AccountService     accountService     = new AccountService(accountRepo, customerRepo);
        TransactionService transactionService = new TransactionService(transactionRepo, accountService);
        CardService        cardService        = new CardService(cardRepo, accountService);
        LoanService        loanService        = new LoanService(loanRepo, accountService, transactionService);
        BranchService      branchService      = new BranchService(branchRepo);

        // Seed initial data
        System.out.println("  Loading banking system data...");
        DataSeeder seeder = new DataSeeder(
                customerService, accountService, transactionService,
                cardService, loanService, branchService);
        seeder.seed();
        System.out.println("  Data loaded: " + customerService.getTotalCustomers() + " customers ready.\n");

        // Launch UI
        ConsoleUI ui = new ConsoleUI(
                customerService, accountService, transactionService,
                cardService, loanService, branchService);
        ui.start();
    }
}
