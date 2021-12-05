package com.typical_coderr.deliverit_mobile.service;

import com.typical_coderr.deliverit_mobile.model.ChangePasswordRequest;
import com.typical_coderr.deliverit_mobile.model.LoginCredentials;
import com.typical_coderr.deliverit_mobile.model.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserClient {

    //login as user
    @POST("auth/login")
    Call<User> login(@Body LoginCredentials loginCredentials);

    //get logged in user\
    @GET("getLoggedInUser")
    Call<User> loggedInUser(@Header("Authorization") String jwtToken);

    //get profile details
    @GET("getProfile")
    Call<User> getProfileDetails(@Header("Authorization") String jwtToken);

    //change password
    @POST("auth/change-password")
    Call<ResponseBody> changePassword(@Header("Authorization") String token, @Body ChangePasswordRequest request);


}
