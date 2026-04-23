package com.bank.repository;

import com.bank.model.Transaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for Transaction entities.
 */
public class TransactionRepository {

    private final ConcurrentHashMap<String, Transaction> store = new ConcurrentHashMap<>();

    public synchronized void save(Transaction transaction) {
        store.put(transaction.getTransactionId(), transaction);
    }

    public Optional<Transaction> findById(String transactionId) {
        return Optional.ofNullable(store.get(transactionId));
    }

    public List<Transaction> findByAccountId(String accountId) {
        return store.values().stream()
                .filter(t -> accountId.equals(t.getFromAccountId()) || accountId.equals(t.getToAccountId()))
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public List<Transaction> findAll() {
        return new ArrayList<>(store.values());
    }
}
