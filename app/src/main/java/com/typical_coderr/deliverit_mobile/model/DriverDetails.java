package com.typical_coderr.deliverit_mobile.model;

import java.io.Serializable;

public class DriverDetails implements Serializable {

    private String driverFirstName;
    private String driverLastName;
    private String driverId;
    private String driverEmail;
    private String NIC;
    private String idNumber;
    private String status;
    private Integer noOfRidesToGo;
    private String vehicleNumber;
    private String contactNumber;
    private String registeredOn;
    private String town;
    private String warehouseLocation;
    private String warehouseId;
    private User user;

    public DriverDetails() {
    }

    public DriverDetails(String driverFirstName, String driverLastName, String driverId, String driverEmail, String NIC, String idNumber, String status, Integer noOfRidesToGo, String vehicleNumber, String contactNumber, String registeredOn, String town, String warehouseLocation, String warehouseId, User user) {
        this.driverFirstName = driverFirstName;
        this.driverLastName = driverLastName;
        this.driverId = driverId;
        this.driverEmail = driverEmail;
        this.NIC = NIC;
        this.idNumber = idNumber;
        this.status = status;
        this.noOfRidesToGo = noOfRidesToGo;
        this.vehicleNumber = vehicleNumber;
        this.contactNumber = contactNumber;
        this.registeredOn = registeredOn;
        this.town = town;
        this.warehouseLocation = warehouseLocation;
        this.warehouseId = warehouseId;
        this.user = user;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
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
