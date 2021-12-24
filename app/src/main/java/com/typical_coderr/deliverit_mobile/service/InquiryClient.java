package com.typical_coderr.deliverit_mobile.service;

import com.typical_coderr.deliverit_mobile.model.Inquiry;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Thu
 * Time: 10:19 AM
 */
public interface InquiryClient {

    @GET("getAllInquiriesForWarehouse")
    
    Call<List<Inquiry>> getAllInquiriesForWarehouse(@Header("Authorization") String jwtToken);


    @GET("getAllMyInquiries")
    Call<List<Inquiry>> getAllMyInquiries(@Header("Authorization") String jwtToken);

    @GET("getAllInquiries")
    Call<List<Inquiry>> getAllInquiries(@Header("Authorization") String jwtToken);

    @POST("sendInquiryResponse")
    Call<ResponseBody> sendInquiryResponse(@Header("Authorization") String jwtToken, @Body Inquiry inquiry);
}
