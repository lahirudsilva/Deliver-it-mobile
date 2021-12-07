package com.typical_coderr.deliverit_mobile.service;

import com.typical_coderr.deliverit_mobile.model.Shipment;
import com.typical_coderr.deliverit_mobile.model.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ShipmentClient{

    //Get all shipments for driver
    @GET("driver/shipmentsDetails")
    Call<List<Shipment>> getAcceptedShipmentsForDriver(@Header("Authorization") String jwtToken);

    //Get all pickup deliveries for driver
    @GET("getPickupDeliveries")
    Call<List<Shipment>> getPickupDeliveries(@Header("Authorization") String jwtToken);

    //Confirm pickup delivery
    @POST("confirmPickupDelivery/{id}")
    Call<ResponseBody> confirmPickup (@Header("Authorization") String jwtToken, @Path(value = "id", encoded = true) int id);

    //Get all in Warehouse delivery
    @GET("getInWarehouseDeliveries")
    Call<List<Shipment>> getInWarehouseDeliveries(@Header("Authorization") String jwtToken );

    //Confirm out for delivery
    @POST("confirmOutForDelivery/{id}")
    Call<ResponseBody> confirmOutForDelivery(@Header("Authorization") String jwtToken, @Path(value = "id", encoded = true) int id);

    //Get all out for delivery
    @GET("getPackagesForDelivery")
    Call<List<Shipment>> getDeliveringPackages(@Header("Authorization") String jwtToken );

    //confirm package Delivered
    @POST("confirmPackageDelivered/{id}")
    Call<ResponseBody> confirmPackageDelivered(@Header("Authorization") String jwtToken, @Path(value = "id", encoded = true) int id);

    @GET("getAllPastRides")
    Call<List<Shipment>> getAllPastDeliveries(@Header("Authorization") String jwtToken);

    @GET("getAllCompletedShipment")
    Call<List<Shipment>> getCustomerShipmentHistory(@Header("Authorization") String jwtToken);


    @GET("getCustomerShipments")
    Call<List<Shipment>> getMyShipments(@Header("Authorization") String jwtToken);

    @POST("addPackage")
    Call<ResponseBody> createPackage(@Header("Authorization") String token, @Body Shipment shipment);

}
