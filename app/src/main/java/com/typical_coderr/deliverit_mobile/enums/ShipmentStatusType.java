package com.typical_coderr.deliverit_mobile.enums;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Sun
 * Time: 1:43 PM
 */
public enum ShipmentStatusType {

    PENDING("pending"),ACCEPTED("accepted"),REJECTED("rejected");

    private final String type;

    ShipmentStatusType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }
}
