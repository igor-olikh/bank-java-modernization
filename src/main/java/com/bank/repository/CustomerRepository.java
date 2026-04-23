package com.bank.repository;

import com.bank.model.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for Customer entities.
 * Uses ConcurrentHashMap for thread-safe access.
 */
public class CustomerRepository {

    private final ConcurrentHashMap<String, Customer> store = new ConcurrentHashMap<>();

    public synchronized void save(Customer customer) {
        store.put(customer.getCustomerId(), customer);
    }

    public Optional<Customer> findById(String customerId) {
        return Optional.ofNullable(store.get(customerId));
    }

    public List<Customer> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Customer> findByName(String query) {
        String lower = query.toLowerCase();
        return store.values().stream()
                .filter(c -> c.getFullName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    public synchronized void delete(String customerId) {
        store.remove(customerId);
    }

    public int count() {
        return store.size();
    }
}
