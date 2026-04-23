package com.bank;

import com.bank.data.DataSeeder;
import com.bank.repository.*;
import com.bank.service.*;
import com.bank.ui.MainWindow;

import javax.swing.*;

/**
 * Banking System — Java 8 Implementation
 * Entry point: wires all layers, seeds data, and launches the Swing UI.
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
        System.out.println("Loading banking system data...");
        new DataSeeder(customerService, accountService, transactionService,
                cardService, loanService, branchService).seed();
        System.out.println("Data seeded: " + customerService.getTotalCustomers() + " customers.");

        // Use Nimbus Look & Feel for a modern appearance
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) { /* fallback to system default */ }

        // Launch Swing UI on the Event Dispatch Thread
        final CustomerService    cs  = customerService;
        final AccountService     as  = accountService;
        final TransactionService ts  = transactionService;
        final CardService        cds = cardService;
        final LoanService        ls  = loanService;
        final BranchService      bs  = branchService;

        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow(cs, as, ts, cds, ls, bs);
            window.setVisible(true);
        });
    }
}

