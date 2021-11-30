package com.typical_coderr.deliverit_mobile.model;

import java.time.Instant;

public class User {
    private String email,firstName,lastName,password,userRole,contactNumber,city,jwtToken,warehouseId;
    private Instant joinedOn;

    public User() {

    }

    public User(String email, String firstName, String lastName, String password, String userRole, String contactNumber, String city, String jwtToken, String warehouseId, Instant joinedOn) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.userRole = userRole;
        this.contactNumber = contactNumber;
        this.city = city;
        this.jwtToken = jwtToken;
        this.warehouseId = warehouseId;
        this.joinedOn = joinedOn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Instant getJoinedOn() {
        return joinedOn;
    }

    public void setJoinedOn(Instant joinedOn) {
        this.joinedOn = joinedOn;
    }
}
