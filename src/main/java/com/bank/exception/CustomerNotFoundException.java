package com.bank.exception;

public class CustomerNotFoundException extends BankException {
    public CustomerNotFoundException(String customerId) {
        super("Customer not found: " + customerId);
    }
}
