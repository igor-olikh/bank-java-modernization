package com.bank.model;

/**
 * Embedded address value object used across Customer and Branch entities.
 */
public class Address {

    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    public Address(String street, String city, String state, String postalCode, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
    }

    public String getStreet()     { return street; }
    public String getCity()       { return city; }
    public String getState()      { return state; }
    public String getPostalCode() { return postalCode; }
    public String getCountry()    { return country; }

    public void setStreet(String street)         { this.street = street; }
    public void setCity(String city)             { this.city = city; }
    public void setState(String state)           { this.state = state; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setCountry(String country)       { this.country = country; }

    @Override
    public String toString() {
        return street + ", " + city + (state != null && !state.isEmpty() ? ", " + state : "")
                + " " + postalCode + ", " + country;
    }
}
