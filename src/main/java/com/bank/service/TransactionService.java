package com.bank.service;

import com.bank.exception.AccountFrozenException;
import com.bank.exception.InsufficientFundsException;
import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.enums.AccountStatus;
import com.bank.model.enums.TransactionStatus;
import com.bank.model.enums.TransactionType;
import com.bank.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Business logic for all financial transactions.
 */
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    public Transaction deposit(String accountId, BigDecimal amount, String description) {
        validate(amount);
        Account account = accountService.getAccount(accountId);
        requireActive(account);

        account.credit(amount);
        accountService.saveAccount(account);

        Transaction tx = new Transaction(null, accountId, amount, account.getCurrency(),
                TransactionType.DEPOSIT, description);
        transactionRepository.save(tx);
        return tx;
    }

    public Transaction withdraw(String accountId, BigDecimal amount, String description) {
        validate(amount);
        Account account = accountService.getAccount(accountId);
        requireActive(account);

        if (account.getAvailableBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(account.getAvailableBalance(), amount);
        }
        account.debit(amount);
        accountService.saveAccount(account);

        Transaction tx = new Transaction(accountId, null, amount, account.getCurrency(),
                TransactionType.WITHDRAWAL, description);
        transactionRepository.save(tx);
        return tx;
    }

    public Transaction transfer(String fromAccountId, String toAccountId, BigDecimal amount, String description) {
        validate(amount);
        Account from = accountService.getAccount(fromAccountId);
        Account to   = accountService.getAccount(toAccountId);
        requireActive(from);
        requireActive(to);

        if (from.getAvailableBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(from.getAvailableBalance(), amount);
        }
        from.debit(amount);
        to.credit(amount);
        accountService.saveAccount(from);
        accountService.saveAccount(to);

        Transaction tx = new Transaction(fromAccountId, toAccountId, amount, from.getCurrency(),
                TransactionType.TRANSFER, description);
        transactionRepository.save(tx);
        return tx;
    }

    public List<Transaction> getHistory(String accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    public Transaction reverseTransaction(String transactionId) {
        Transaction original = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionId));

        if (original.getStatus() != TransactionStatus.COMPLETED) {
            throw new IllegalStateException("Only COMPLETED transactions can be reversed.");
        }

        // Reverse the money movement
        if (original.getToAccountId() != null) {
            Account to = accountService.getAccount(original.getToAccountId());
            to.debit(original.getAmount());
            accountService.saveAccount(to);
        }
        if (original.getFromAccountId() != null) {
            Account from = accountService.getAccount(original.getFromAccountId());
            from.credit(original.getAmount());
            accountService.saveAccount(from);
        }
        original.setStatus(TransactionStatus.REVERSED);
        transactionRepository.save(original);
        return original;
    }

    /** Save a pre-built transaction directly (used by DataSeeder). */
    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    private void validate(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
    }

    private void requireActive(Account account) {
        if (account.getStatus() == AccountStatus.FROZEN) {
            throw new AccountFrozenException(account.getAccountId());
        }
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new IllegalStateException("Account is closed: " + account.getAccountId());
        }
    }
}
