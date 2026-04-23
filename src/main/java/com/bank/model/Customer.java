package com.bank.model;

import com.bank.model.enums.CustomerStatus;
import com.bank.model.enums.CustomerType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents an individual or corporate bank customer.
 */
public class Customer {

    private final String customerId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String email;
    private String phone;
    private Address address;
    private String nationality;
    private CustomerType customerType;
    private CustomerStatus status;
    private final LocalDateTime createdAt;

    public Customer(String firstName, String lastName, LocalDate dateOfBirth,
                    String email, String phone, Address address,
                    String nationality, CustomerType customerType) {
        this.customerId = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.nationality = nationality;
        this.customerType = customerType;
        this.status = CustomerStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public String getCustomerId()       { return customerId; }
    public String getFirstName()        { return firstName; }
    public String getLastName()         { return lastName; }
    public String getFullName()         { return firstName + " " + lastName; }
    public LocalDate getDateOfBirth()   { return dateOfBirth; }
    public String getEmail()            { return email; }
    public String getPhone()            { return phone; }
    public Address getAddress()         { return address; }
    public String getNationality()      { return nationality; }
    public CustomerType getCustomerType() { return customerType; }
    public CustomerStatus getStatus()   { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setFirstName(String firstName)   { this.firstName = firstName; }
    public void setLastName(String lastName)     { this.lastName = lastName; }
    public void setDateOfBirth(LocalDate dob)    { this.dateOfBirth = dob; }
    public void setEmail(String email)           { this.email = email; }
    public void setPhone(String phone)           { this.phone = phone; }
    public void setAddress(Address address)      { this.address = address; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public void setCustomerType(CustomerType t)  { this.customerType = t; }
    public void setStatus(CustomerStatus status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Customer{id='%s', name='%s', nationality='%s', type=%s, status=%s}",
                customerId, getFullName(), nationality, customerType, status);
    }
}
