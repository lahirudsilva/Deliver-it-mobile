package com.typical_coderr.deliverit_mobile.model;

import java.io.Serializable;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Thu
 * Time: 10:15 AM
 */
public class Inquiry implements Serializable {

    private Integer inquiryId;
    private String description;
    private String response;
    private String createdAt;
    private String inquiryStatus;
    private Integer shipmentId;
    private String userId;
    private Shipment shipment;
    private User user;

    public Inquiry(Integer inquiryId, String description, String response, String createdAt, String inquiryStatus, Integer shipmentId, String userId, Shipment shipment, User user) {
        this.inquiryId = inquiryId;
        this.description = description;
        this.response = response;
        this.createdAt = createdAt;
        this.inquiryStatus = inquiryStatus;
        this.shipmentId = shipmentId;
        this.userId = userId;
        this.shipment = shipment;
        this.user = user;
    }

    public Inquiry() {
    }

    public Integer getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(Integer inquiryId) {
        this.inquiryId = inquiryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getInquiryStatus() {
        return inquiryStatus;
    }

    public void setInquiryStatus(String inquiryStatus) {
        this.inquiryStatus = inquiryStatus;
    }

    public Integer getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Integer shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
