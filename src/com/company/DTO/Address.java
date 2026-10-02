package com.company.DTO;

import com.company.annotations.Alias;

public class Address {
    @Alias("ville de residence")
    private String city;
    private String postalCode;


    public Address() {}

    public Address(String city, String postalCode) {
        this.city = city;
        this.postalCode = postalCode;
    }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
}
