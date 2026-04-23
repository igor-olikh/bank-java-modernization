package com.bank.model;

import java.util.UUID;

/**
 * Represents a physical bank branch.
 */
public class Branch {

    private final String branchId;
    private final String branchCode;
    private String name;
    private Address address;
    private String phone;
    private String manager;
    private String openingHours;

    public Branch(String branchCode, String name, Address address,
                  String phone, String manager, String openingHours) {
        this.branchId = UUID.randomUUID().toString();
        this.branchCode = branchCode;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.manager = manager;
        this.openingHours = openingHours;
    }

    public String getBranchId()      { return branchId; }
    public String getBranchCode()    { return branchCode; }
    public String getName()          { return name; }
    public Address getAddress()      { return address; }
    public String getPhone()         { return phone; }
    public String getManager()       { return manager; }
    public String getOpeningHours()  { return openingHours; }

    public void setName(String name)               { this.name = name; }
    public void setAddress(Address address)        { this.address = address; }
    public void setPhone(String phone)             { this.phone = phone; }
    public void setManager(String manager)         { this.manager = manager; }
    public void setOpeningHours(String hours)      { this.openingHours = hours; }

    @Override
    public String toString() {
        return String.format("Branch{code='%s', name='%s', manager='%s'}",
                branchCode, name, manager);
    }
}
