package com.bank.repository;

import com.bank.model.Loan;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for Loan entities.
 */
public class LoanRepository {

    private final ConcurrentHashMap<String, Loan> store = new ConcurrentHashMap<>();

    public synchronized void save(Loan loan) {
        store.put(loan.getLoanId(), loan);
    }

    public Optional<Loan> findById(String loanId) {
        return Optional.ofNullable(store.get(loanId));
    }

    public List<Loan> findByCustomerId(String customerId) {
        return store.values().stream()
                .filter(l -> l.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public List<Loan> findAll() {
        return new ArrayList<>(store.values());
    }
}
