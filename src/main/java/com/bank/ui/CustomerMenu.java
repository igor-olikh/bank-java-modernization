package com.bank.ui;

import com.bank.model.Customer;
import com.bank.model.enums.CustomerType;
import com.bank.service.CustomerService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Console sub-menu for customer management operations.
 */
public class CustomerMenu {

    private static final String DIV = "--------------------------------------------------------------------------------";

    private final Scanner scanner;
    private final CustomerService customerService;

    public CustomerMenu(Scanner scanner, CustomerService customerService) {
        this.scanner = scanner;
        this.customerService = customerService;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + DIV);
            System.out.println("  CUSTOMER MANAGEMENT");
            System.out.println(DIV);
            System.out.println("  [1]  List all customers");
            System.out.println("  [2]  View customer details");
            System.out.println("  [3]  Search by name");
            System.out.println("  [4]  Register new customer");
            System.out.println("  [5]  Block customer");
            System.out.println("  [6]  Activate customer");
            System.out.println("  [0]  Back");
            System.out.println(DIV);
            System.out.print("  Select: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1": listAll();           break;
                case "2": viewCustomer();      break;
                case "3": searchByName();      break;
                case "4": registerCustomer();  break;
                case "5": blockCustomer();     break;
                case "6": activateCustomer();  break;
                case "0": back = true;         break;
                default:  System.out.println("  [!] Invalid option.");
            }
        }
    }

    private void listAll() {
        List<Customer> customers = customerService.getAllCustomers();
        System.out.println("\n  " + DIV);
        System.out.printf("  %-4s %-30s %-12s %-12s %-15s%n",
                "#", "Name", "Type", "Status", "Nationality");
        System.out.println("  " + DIV);
        int i = 1;
        for (Customer c : customers) {
            System.out.printf("  %-4d %-30s %-12s %-12s %-15s%n",
                    i++, c.getFullName(), c.getCustomerType(), c.getStatus(), c.getNationality());
        }
        System.out.println("  " + DIV);
        System.out.println("  Total customers: " + customers.size());
    }

    private void viewCustomer() {
        System.out.print("  Enter Customer ID: ");
        String id = scanner.nextLine().trim();
        try {
            Customer c = customerService.getCustomerById(id);
            System.out.println("\n  " + DIV);
            System.out.println("  CUSTOMER PROFILE");
            System.out.println("  " + DIV);
            System.out.println("  ID          : " + c.getCustomerId());
            System.out.println("  Name        : " + c.getFullName());
            System.out.println("  Date of Birth: " + c.getDateOfBirth());
            System.out.println("  Nationality : " + c.getNationality());
            System.out.println("  Type        : " + c.getCustomerType());
            System.out.println("  Status      : " + c.getStatus());
            System.out.println("  Email       : " + c.getEmail());
            System.out.println("  Phone       : " + c.getPhone());
            System.out.println("  Address     : " + c.getAddress());
            System.out.println("  Member since: " + c.getCreatedAt().toLocalDate());
            System.out.println("  " + DIV);
        } catch (Exception e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }

    private void searchByName() {
        System.out.print("  Enter name to search: ");
        String query = scanner.nextLine().trim();
        List<Customer> results = customerService.searchByName(query);
        if (results.isEmpty()) {
            System.out.println("  No customers found matching: " + query);
        } else {
            results.forEach(c -> System.out.printf("  [%s] %s — %s%n",
                    c.getCustomerId(), c.getFullName(), c.getNationality()));
        }
    }

    private void registerCustomer() {
        System.out.println("\n  Register New Customer");
        System.out.println("  " + DIV);
        try {
            System.out.print("  First name: ");       String first = scanner.nextLine().trim();
            System.out.print("  Last name: ");        String last  = scanner.nextLine().trim();
            System.out.print("  Date of birth (YYYY-MM-DD): "); LocalDate dob = LocalDate.parse(scanner.nextLine().trim());
            System.out.print("  Email: ");            String email = scanner.nextLine().trim();
            System.out.print("  Phone: ");            String phone = scanner.nextLine().trim();
            System.out.print("  Street: ");           String street = scanner.nextLine().trim();
            System.out.print("  City: ");             String city  = scanner.nextLine().trim();
            System.out.print("  State/Province: ");   String state = scanner.nextLine().trim();
            System.out.print("  Postal code: ");      String postal = scanner.nextLine().trim();
            System.out.print("  Country: ");          String country = scanner.nextLine().trim();
            System.out.print("  Nationality: ");      String nationality = scanner.nextLine().trim();
            System.out.print("  Type (INDIVIDUAL/CORPORATE): ");
            CustomerType type = CustomerType.valueOf(scanner.nextLine().trim().toUpperCase());

            Customer c = customerService.registerCustomer(first, last, dob, email, phone,
                    new com.bank.model.Address(street, city, state, postal, country),
                    nationality, type);
            System.out.println("  [OK] Customer registered. ID: " + c.getCustomerId());
        } catch (Exception e) {
            System.out.println("  [!] Failed to register: " + e.getMessage());
        }
    }

    private void blockCustomer() {
        System.out.print("  Enter Customer ID to block: ");
        String id = scanner.nextLine().trim();
        try {
            customerService.blockCustomer(id);
            System.out.println("  [OK] Customer blocked.");
        } catch (Exception e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }

    private void activateCustomer() {
        System.out.print("  Enter Customer ID to activate: ");
        String id = scanner.nextLine().trim();
        try {
            customerService.activateCustomer(id);
            System.out.println("  [OK] Customer activated.");
        } catch (Exception e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }
}
