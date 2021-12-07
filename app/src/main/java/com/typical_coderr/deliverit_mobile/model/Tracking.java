package com.typical_coderr.deliverit_mobile.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Thu
 * Time: 9:54 PM
 */
public class Tracking implements Serializable {

    private Integer trackingId;
    private String shipmentStatus;
    private String driverId;
    private Integer shipmentId;
    private String updatedAt;
    private Shipment shipment;
    private String driverFirstName;
    private String driverLastName;
    private String driverVehicleNumber;
    private LocalDate dropOffDate;
    private String driverContactNumber;
    private DriverDetails driverDetails;
    private String pick;
    private String drop;

    public Tracking(Integer trackingId, String shipmentStatus, String driverId, Integer shipmentId, String updatedAt, Shipment shipment, String driverFirstName, String driverLastName, String driverVehicleNumber, LocalDate dropOffDate, String driverContactNumber, DriverDetails driverDetails, String pick, String drop) {
        this.trackingId = trackingId;
        this.shipmentStatus = shipmentStatus;
        this.driverId = driverId;
        this.shipmentId = shipmentId;
        this.updatedAt = updatedAt;
        this.shipment = shipment;
        this.driverFirstName = driverFirstName;
        this.driverLastName = driverLastName;
        this.driverVehicleNumber = driverVehicleNumber;
        this.dropOffDate = dropOffDate;
        this.driverContactNumber = driverContactNumber;
        this.driverDetails = driverDetails;
        this.pick = pick;
        this.drop = drop;
    }

    public Integer getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(Integer trackingId) {
        this.trackingId = trackingId;
    }

    public String getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(String shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public Integer getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Integer shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
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

    public String getDriverVehicleNumber() {
        return driverVehicleNumber;
    }

    public void setDriverVehicleNumber(String driverVehicleNumber) {
        this.driverVehicleNumber = driverVehicleNumber;
    }

    public LocalDate getDropOffDate() {
        return dropOffDate;
    }

    public void setDropOffDate(LocalDate dropOffDate) {
        this.dropOffDate = dropOffDate;
    }

    public String getDriverContactNumber() {
        return driverContactNumber;
    }

    public void setDriverContactNumber(String driverContactNumber) {
        this.driverContactNumber = driverContactNumber;
    }

    public DriverDetails getDriverDetails() {
        return driverDetails;
    }

    public void setDriverDetails(DriverDetails driverDetails) {
        this.driverDetails = driverDetails;
    }

    public String getPick() {
        return pick;
    }

    public void setPick(String pick) {
        this.pick = pick;
    }

    public String getDrop() {
        return drop;
    }

    public void setDrop(String drop) {
        this.drop = drop;
    }
}
