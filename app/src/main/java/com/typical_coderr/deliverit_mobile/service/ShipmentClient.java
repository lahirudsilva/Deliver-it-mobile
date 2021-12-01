package com.typical_coderr.deliverit_mobile.service;

import com.typical_coderr.deliverit_mobile.model.Shipment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ShipmentClient{

    //Get all shipments for driver
    @GET("driver/shipmentsDetails")
    Call<List<Shipment>> getAcceptedShipmentsForDriver(@Header("Authorization") String jwtToken);

}
