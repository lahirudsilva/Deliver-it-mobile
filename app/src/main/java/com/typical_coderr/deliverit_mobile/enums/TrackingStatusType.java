package com.typical_coderr.deliverit_mobile.enums;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Sun
 * Time: 1:43 PM
 */
public enum TrackingStatusType {

    PICKUP_IN_PROGRESS("Driver is on the way to pickup the package"),
    IN_WAREHOUSE("Your Package has been picked up by the driver"),
    OUT_FOR_DELIVERY("Driver out for Delivery"),
    DELIVERED("Package has been delivered");

    private final String type;

    TrackingStatusType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }
}
