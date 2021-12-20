package com.typical_coderr.deliverit_mobile.util;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.typical_coderr.deliverit_mobile.CreatePackageActivity;
import com.typical_coderr.deliverit_mobile.CustomerDeliveryHistoryActivity;
import com.typical_coderr.deliverit_mobile.CustomerHomeActivity;
import com.typical_coderr.deliverit_mobile.DriverHomeActivity;
import com.typical_coderr.deliverit_mobile.InquiriesActivity;
import com.typical_coderr.deliverit_mobile.ManageDeliveryRidesActivity;
import com.typical_coderr.deliverit_mobile.OnGoingShipmentsActivity;
import com.typical_coderr.deliverit_mobile.PackageRequestsActivity;
import com.typical_coderr.deliverit_mobile.R;
import com.typical_coderr.deliverit_mobile.SupervisorHomeActivity;
import com.typical_coderr.deliverit_mobile.UserProfileActivity;

public class NavHandler {

    public static void handleDriverNav(MenuItem item, Context context) {
        switch (item.getItemId()) {
            case R.id.nav_logout: {
                AuthHandler.logout(context);
                break;
            }
            case R.id.nav_home: {
                Intent intent = new Intent(context, DriverHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.manage_deliveries:{
                Intent intent = new Intent(context, ManageDeliveryRidesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.profile:{
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
        }
    }

    public static void handleCustomerNav(MenuItem item, Context context){
        switch (item.getItemId()){
            case R.id.nav_home: {
                Intent intent = new Intent(context, CustomerHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_create_package:{
                Intent intent = new Intent(context, CreatePackageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_shipment_history:{
                Intent intent = new Intent(context, CustomerDeliveryHistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_customer_profile:{
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_logout: {
                AuthHandler.logout(context);
                break;
            }
        }


    }

    public static void handleSupervisorNav(MenuItem item, Context context){
        switch (item.getItemId()){
            case R.id.nav_home: {
                Intent intent = new Intent(context, SupervisorHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_package_requests: {
                Intent intent = new Intent(context, PackageRequestsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_inquires: {
                Intent intent = new Intent(context, InquiriesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_on_going_shipment: {
                Intent intent = new Intent(context, OnGoingShipmentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_logout: {
                AuthHandler.logout(context);
                break;
            }
        }

    }
}
