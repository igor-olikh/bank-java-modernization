package com.bank.service;

import com.bank.exception.CustomerNotFoundException;
import com.bank.model.Address;
import com.bank.model.Customer;
import com.bank.model.enums.CustomerStatus;
import com.bank.model.enums.CustomerType;
import com.bank.repository.CustomerRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Business logic for customer lifecycle management.
 */
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public Customer registerCustomer(String firstName, String lastName, LocalDate dateOfBirth,
                                     String email, String phone, Address address,
                                     String nationality, CustomerType customerType) {
        if (firstName == null || firstName.trim().isEmpty()) throw new IllegalArgumentException("First name is required");
        if (lastName == null || lastName.trim().isEmpty())   throw new IllegalArgumentException("Last name is required");
        if (email == null || email.trim().isEmpty())         throw new IllegalArgumentException("Email is required");

        Customer customer = new Customer(firstName.trim(), lastName.trim(), dateOfBirth,
                email.trim(), phone, address, nationality, customerType);
        repository.save(customer);
        return customer;
    }

    public Customer getCustomerById(String customerId) {
        return repository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    public List<Customer> getAllCustomers() {
        return repository.findAll();
    }

    public List<Customer> searchByName(String query) {
        return repository.findByName(query);
    }

    public Customer updateCustomer(String customerId, String email, String phone, Address address) {
        Customer customer = getCustomerById(customerId);
        if (email != null && !email.trim().isEmpty()) customer.setEmail(email.trim());
        if (phone != null)  customer.setPhone(phone);
        if (address != null) customer.setAddress(address);
        repository.save(customer);
        return customer;
    }

    public void blockCustomer(String customerId) {
        Customer customer = getCustomerById(customerId);
        customer.setStatus(CustomerStatus.BLOCKED);
        repository.save(customer);
    }

    public void activateCustomer(String customerId) {
        Customer customer = getCustomerById(customerId);
        customer.setStatus(CustomerStatus.ACTIVE);
        repository.save(customer);
    }

    public int getTotalCustomers() {
        return repository.count();
    }
}
