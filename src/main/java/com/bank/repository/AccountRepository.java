package com.bank.repository;

import com.bank.model.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for Account entities.
 */
public class AccountRepository {

    private final ConcurrentHashMap<String, Account> store = new ConcurrentHashMap<>();

    public synchronized void save(Account account) {
        store.put(account.getAccountId(), account);
    }

    public Optional<Account> findById(String accountId) {
        return Optional.ofNullable(store.get(accountId));
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        return store.values().stream()
                .filter(a -> a.getAccountNumber().equals(accountNumber))
                .findFirst();
    }

    public List<Account> findByCustomerId(String customerId) {
        return store.values().stream()
                .filter(a -> a.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public List<Account> findAll() {
        return new ArrayList<>(store.values());
    }

    public synchronized void delete(String accountId) {
        store.remove(accountId);
    }
}
