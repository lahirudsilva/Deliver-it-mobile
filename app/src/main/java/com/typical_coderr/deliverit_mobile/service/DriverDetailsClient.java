
package com.typical_coderr.deliverit_mobile.service;

import com.typical_coderr.deliverit_mobile.model.DriverDetails;
import com.typical_coderr.deliverit_mobile.model.Shipment;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface DriverDetailsClient {

    @GET("driver/getDriverDetails")
    Call<DriverDetails> getDriverDetails(@Header("Authorization") String jwtToken);

    @GET("getAvailableDrivers")
    Call<List<DriverDetails>> getAvailableDrivers(@Header("Authorization") String jwtToken);

    @POST("assignDriver")
    Call<ResponseBody> assignDriver(@Header("Authorization") String jwtToken, @Body Shipment shipment);

    @POST("registerDriver")
    Call<ResponseBody> addDriver(@Header("Authorization") String jwtToken, @Body DriverDetails driverDetails);

    @GET("getAllDrivers")
    Call<List<DriverDetails>> getAllDrivers(@Header("Authorization") String jwtToken);

    @GET("getAllDriversForWarehouse")
    Call<List<DriverDetails>> getAllDriversForWarehouse(@Header("Authorization") String jwtToken);



}
