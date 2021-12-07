package com.typical_coderr.deliverit_mobile.enums;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Sun
 * Time: 1:42 PM
 */
public enum DriverStatusType {
    AVAILABLE("available"), ASSIGN_SLOTS_FULL("assign-slots full"), UNAVAILABLE("unavailable");

    private final String type;

    DriverStatusType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }
}
