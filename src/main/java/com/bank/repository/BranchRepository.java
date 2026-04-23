package com.bank.repository;

import com.bank.model.Branch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository for Branch entities.
 */
public class BranchRepository {

    private final ConcurrentHashMap<String, Branch> store = new ConcurrentHashMap<>();

    public synchronized void save(Branch branch) {
        store.put(branch.getBranchId(), branch);
    }

    public Optional<Branch> findById(String branchId) {
        return Optional.ofNullable(store.get(branchId));
    }

    public Optional<Branch> findByCode(String branchCode) {
        return store.values().stream()
                .filter(b -> b.getBranchCode().equals(branchCode))
                .findFirst();
    }

    public List<Branch> findAll() {
        return new ArrayList<>(store.values());
    }
}
