package com.typical_coderr.deliverit_mobile.model;

import java.io.Serializable;

public class DriverDetails implements Serializable {

    private String driverFirstName;
    private String driverLastName;
    private String driverId;
    private String driverEmail;
    private String NIC;
    private String status;
    private Integer noOfRidesToGo;
    private String vehicleNumber;
    private String contactNumber;
    private String registeredOn;
    private String warehouseLocation;
    private User user;

    public DriverDetails(String driverFirstName, String driverLastName, String driverId, String driverEmail, String NIC, String status, Integer noOfRidesToGo, String vehicleNumber, String contactNumber, String registeredOn, String warehouseLocation, User user) {
        this.driverFirstName = driverFirstName;
        this.driverLastName = driverLastName;
        this.driverId = driverId;
        this.driverEmail = driverEmail;
        this.NIC = NIC;
        this.status = status;
        this.noOfRidesToGo = noOfRidesToGo;
        this.vehicleNumber = vehicleNumber;
        this.contactNumber = contactNumber;
        this.registeredOn = registeredOn;
        this.warehouseLocation = warehouseLocation;
        this.user = user;
    }

    public String getDriverFirstName() {
        return driverFirstName;
    }

    public void setDriverFirstName(String driverFirstName) {
        this.driverFirstName = driverFirstName;
    }

    public String getDriverLastName() {
        return driverLastName;
    }

    public void setDriverLastName(String driverLastName) {
        this.driverLastName = driverLastName;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public String getNIC() {
        return NIC;
    }

    public void setNIC(String NIC) {
        this.NIC = NIC;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getNoOfRidesToGo() {
        return noOfRidesToGo;
    }

    public void setNoOfRidesToGo(Integer noOfRidesToGo) {
        this.noOfRidesToGo = noOfRidesToGo;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getRegisteredOn() {
        return registeredOn;
    }

    public void setRegisteredOn(String registeredOn) {
        this.registeredOn = registeredOn;
    }

    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
