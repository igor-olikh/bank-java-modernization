package com.bank.service;

import com.bank.model.Address;
import com.bank.model.Branch;
import com.bank.repository.BranchRepository;

import java.util.List;

/**
 * Business logic for branch information.
 */
public class BranchService {

    private final BranchRepository branchRepository;

    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    public Branch addBranch(String branchCode, String name, Address address,
                             String phone, String manager, String openingHours) {
        Branch branch = new Branch(branchCode, name, address, phone, manager, openingHours);
        branchRepository.save(branch);
        return branch;
    }

    public Branch getBranch(String branchId) {
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found: " + branchId));
    }

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }
}
