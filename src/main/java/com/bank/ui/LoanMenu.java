package com.bank.ui;

import com.bank.model.Loan;
import com.bank.model.enums.LoanType;
import com.bank.service.AccountService;
import com.bank.service.CustomerService;
import com.bank.service.LoanService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class LoanMenu {

    private static final String DIV = "--------------------------------------------------------------------------------";
    private final Scanner scanner;
    private final LoanService loanService;
    private final AccountService accountService;
    private final CustomerService customerService;

    public LoanMenu(Scanner scanner, LoanService loanService, AccountService accountService, CustomerService customerService) {
        this.scanner = scanner;
        this.loanService = loanService;
        this.accountService = accountService;
        this.customerService = customerService;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + DIV);
            System.out.println("  LOANS");
            System.out.println(DIV);
            System.out.println("  [1]  View loans by customer");
            System.out.println("  [2]  Apply for loan");
            System.out.println("  [3]  Make repayment");
            System.out.println("  [0]  Back");
            System.out.println(DIV);
            System.out.print("  Select: ");
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1": viewLoans();   break;
                case "2": applyLoan();   break;
                case "3": repay();       break;
                case "0": back = true;   break;
                default:  System.out.println("  [!] Invalid option.");
            }
        }
    }

    private void viewLoans() {
        System.out.print("  Customer ID: ");
        String customerId = scanner.nextLine().trim();
        try {
            String name = customerService.getCustomerById(customerId).getFullName();
            List<Loan> loans = loanService.getLoansByCustomer(customerId);
            System.out.println("\n  Loans for: " + name);
            System.out.println("  " + DIV);
            System.out.printf("  %-8s %-12s %-14s %-14s %-6s %-8s%n",
                    "Type", "Principal", "Outstanding", "Monthly Pay", "Rate%", "Status");
            System.out.println("  " + DIV);
            if (loans.isEmpty()) { System.out.println("  No loans found."); return; }
            loans.forEach(l -> System.out.printf("  %-8s %-14.2f %-14.2f %-14.2f %-6.2f %-8s%n",
                    l.getLoanType(), l.getPrincipalAmount(), l.getOutstandingBalance(),
                    l.getMonthlyPayment(), l.getInterestRate(), l.getStatus()));
        } catch (Exception e) { System.out.println("  [!] " + e.getMessage()); }
    }

    private void applyLoan() {
        try {
            System.out.print("  Customer ID: ");        String customerId = scanner.nextLine().trim();
            System.out.print("  Account ID: ");         String accountId  = scanner.nextLine().trim();
            System.out.print("  Loan type (PERSONAL/MORTGAGE/AUTO/BUSINESS): ");
            LoanType type = LoanType.valueOf(scanner.nextLine().trim().toUpperCase());
            System.out.print("  Amount: ");             BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            System.out.print("  Annual interest rate (%): "); BigDecimal rate = new BigDecimal(scanner.nextLine().trim());
            System.out.print("  Term (months): ");      int term = Integer.parseInt(scanner.nextLine().trim());
            Loan loan = loanService.applyForLoan(customerId, accountId, type, amount, rate, term);
            System.out.printf("  [OK] Loan approved. Monthly payment: %.2f  End date: %s%n",
                    loan.getMonthlyPayment(), loan.getEndDate());
        } catch (Exception e) { System.out.println("  [!] " + e.getMessage()); }
    }

    private void repay() {
        try {
            System.out.print("  Loan ID: ");       String loanId    = scanner.nextLine().trim();
            System.out.print("  Account ID: ");    String accountId = scanner.nextLine().trim();
            System.out.print("  Amount: ");        BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            Loan loan = loanService.makeRepayment(loanId, accountId, amount);
            System.out.printf("  [OK] Repayment recorded. Outstanding balance: %.2f  Status: %s%n",
                    loan.getOutstandingBalance(), loan.getStatus());
        } catch (Exception e) { System.out.println("  [!] " + e.getMessage()); }
    }
}
