package com.bank.service;

import com.bank.model.Loan;
import com.bank.model.enums.LoanStatus;
import com.bank.model.enums.LoanType;
import com.bank.repository.LoanRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Business logic for loan application and repayment.
 */
public class LoanService {

    private final LoanRepository loanRepository;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public LoanService(LoanRepository loanRepository, AccountService accountService,
                       TransactionService transactionService) {
        this.loanRepository = loanRepository;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    public Loan applyForLoan(String customerId, String accountId, LoanType type,
                              BigDecimal amount, BigDecimal interestRate, int termMonths) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Loan amount must be positive.");
        if (termMonths <= 0) throw new IllegalArgumentException("Term must be at least 1 month.");

        Loan loan = new Loan(customerId, accountId, type, amount, interestRate, termMonths);
        loanRepository.save(loan);
        // Disburse funds to account
        transactionService.deposit(accountId, amount, "Loan disbursement – " + type);
        return loan;
    }

    public Loan getLoan(String loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found: " + loanId));
    }

    public List<Loan> getLoansByCustomer(String customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Loan makeRepayment(String loanId, String accountId, BigDecimal amount) {
        Loan loan = getLoan(loanId);
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new IllegalStateException("Loan is not active: " + loan.getStatus());
        }
        transactionService.withdraw(accountId, amount, "Loan repayment – " + loan.getLoanType());
        loan.applyRepayment(amount);
        loanRepository.save(loan);
        return loan;
    }
}
