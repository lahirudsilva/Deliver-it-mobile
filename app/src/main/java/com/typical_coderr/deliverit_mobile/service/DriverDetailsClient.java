
package com.typical_coderr.deliverit_mobile.service;

import com.typical_coderr.deliverit_mobile.model.DriverDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface DriverDetailsClient {

    @GET("driver/getDriverDetails")
    Call<DriverDetails> getDriverDetails(@Header("Authorization") String jwtToken);

}
