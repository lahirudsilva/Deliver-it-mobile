package com.typical_coderr.deliverit_mobile.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Shipment implements Serializable {

    private String pickupLocation, dropOffLocation, senderEmail, receiverEmail, receiverContactNumber, size, status, createdAt, description, warehouseLocation, warehouseNumber, arrival ,pickUp, senderContactNumber, senderFirstName, senderLastName, receiverName;
    private Integer shipmentId;
    private LocalDate pickUpDate, dropOffDate;
    private Double weight, estimatedPrice;
    private User user;

    public Shipment(String pickupLocation, String dropOffLocation, String senderEmail, String receiverEmail, String receiverContactNumber, String size, String status, String createdAt, String description, String warehouseLocation, String warehouseNumber, String arrival, String pickUp, String senderContactNumber, String senderFirstName, String senderLastName, String receiverName, Integer shipmentId, LocalDate pickUpDate, LocalDate dropOffDate, Double weight, Double estimatedPrice, User user) {
        this.pickupLocation = pickupLocation;
        this.dropOffLocation = dropOffLocation;
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.receiverContactNumber = receiverContactNumber;
        this.size = size;
        this.status = status;
        this.createdAt = createdAt;
        this.description = description;
        this.warehouseLocation = warehouseLocation;
        this.warehouseNumber = warehouseNumber;
        this.arrival = arrival;
        this.pickUp = pickUp;
        this.senderContactNumber = senderContactNumber;
        this.senderFirstName = senderFirstName;
        this.senderLastName = senderLastName;
        this.receiverName = receiverName;
        this.shipmentId = shipmentId;
        this.pickUpDate = pickUpDate;
        this.dropOffDate = dropOffDate;
        this.weight = weight;
        this.estimatedPrice = estimatedPrice;
        this.user = user;
    }

    public Shipment() {
    }

    public String getPickUp() {
        return pickUp;
    }

    public void setPickUp(String pickUp) {
        this.pickUp = pickUp;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropOffLocation() {
        return dropOffLocation;
    }

    public void setDropOffLocation(String dropOffLocation) {
        this.dropOffLocation = dropOffLocation;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getReceiverContactNumber() {
        return receiverContactNumber;
    }

    public void setReceiverContactNumber(String receiverContactNumber) {
        this.receiverContactNumber = receiverContactNumber;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }

    public String getWarehouseNumber() {
        return warehouseNumber;
    }

    public void setWarehouseNumber(String warehouseNumber) {
        this.warehouseNumber = warehouseNumber;
    }

    public Integer getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Integer shipmentId) {
        this.shipmentId = shipmentId;
    }

    public LocalDate getPickUpDate() {
        return pickUpDate;
    }

    public void setPickUpDate(LocalDate pickUpDate) {
        this.pickUpDate = pickUpDate;
    }

    public LocalDate getDropOffDate() {
        return dropOffDate;
    }

    public void setDropOffDate(LocalDate dropOffDate) {
        this.dropOffDate = dropOffDate;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(Double estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getSenderContactNumber() {
        return senderContactNumber;
    }

    public void setSenderContactNumber(String senderContactNumber) {
        this.senderContactNumber = senderContactNumber;
    }

    public String getSenderFirstName() {
        return senderFirstName;
    }

    public void setSenderFirstName(String senderFirstName) {
        this.senderFirstName = senderFirstName;
    }

    public String getSenderLastName() {
        return senderLastName;
    }

    public void setSenderLastName(String senderLastName) {
        this.senderLastName = senderLastName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
}
