package com.bank.service;

import com.bank.exception.AccountNotFoundException;
import com.bank.exception.CustomerNotFoundException;
import com.bank.model.Account;
import com.bank.model.enums.AccountStatus;
import com.bank.model.enums.AccountType;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Business logic for account lifecycle management.
 */
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    public Account openAccount(String customerId, AccountType type, String currency) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        Account account = new Account(customerId, type, currency);
        accountRepository.save(account);
        return account;
    }

    public Account getAccount(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    public List<Account> getAccountsByCustomer(String customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public BigDecimal getBalance(String accountId) {
        return getAccount(accountId).getBalance();
    }

    public void freezeAccount(String accountId) {
        Account account = getAccount(accountId);
        account.setStatus(AccountStatus.FROZEN);
        accountRepository.save(account);
    }

    public void unfreezeAccount(String accountId) {
        Account account = getAccount(accountId);
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
    }

    public void closeAccount(String accountId) {
        Account account = getAccount(accountId);
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Cannot close account with non-zero balance: " + account.getBalance());
        }
        account.setStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
    }

    public void saveAccount(Account account) {
        accountRepository.save(account);
    }
}
