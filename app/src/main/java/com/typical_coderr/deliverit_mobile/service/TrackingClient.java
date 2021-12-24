package com.typical_coderr.deliverit_mobile.service;

import com.typical_coderr.deliverit_mobile.model.Shipment;
import com.typical_coderr.deliverit_mobile.model.Tracking;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Tue
 * Time: 2:11 AM
 */
public interface TrackingClient {

    @GET("getAllTracking")
    Call<List<Tracking>> getAllTracking(@Header("Authorization") String jwtToken);

    @GET("getAllOnGoingShipments")
    Call<List<Tracking>> getAllOnGoingShipments(@Header("Authorization") String jwtToken);

    @GET("getAllOnGoingShipmentsForAdmin")
    Call<List<Tracking>> getAllOnGoingShipmentsForAdmin(@Header("Authorization") String jwtToken);
}
