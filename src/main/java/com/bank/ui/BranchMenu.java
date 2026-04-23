package com.bank.ui;

import com.bank.model.Branch;
import com.bank.service.BranchService;

import java.util.List;
import java.util.Scanner;

public class BranchMenu {

    private static final String DIV = "--------------------------------------------------------------------------------";
    private final Scanner scanner;
    private final BranchService branchService;

    public BranchMenu(Scanner scanner, BranchService branchService) {
        this.scanner = scanner;
        this.branchService = branchService;
    }

    public void show() {
        System.out.println("\n" + DIV);
        System.out.println("  BRANCH INFORMATION");
        System.out.println(DIV);
        List<Branch> branches = branchService.getAllBranches();
        if (branches.isEmpty()) {
            System.out.println("  No branches available.");
        } else {
            branches.forEach(b -> {
                System.out.println("\n  Code    : " + b.getBranchCode());
                System.out.println("  Name    : " + b.getName());
                System.out.println("  Address : " + b.getAddress());
                System.out.println("  Phone   : " + b.getPhone());
                System.out.println("  Manager : " + b.getManager());
                System.out.println("  Hours   : " + b.getOpeningHours());
                System.out.println("  " + DIV);
            });
        }
        System.out.print("  Press ENTER to return...");
        scanner.nextLine();
    }
}
