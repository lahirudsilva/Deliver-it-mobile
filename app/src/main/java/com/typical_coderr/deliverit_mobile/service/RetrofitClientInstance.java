package com.typical_coderr.deliverit_mobile.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**Singleton class to initialize retrofit instance**/
public class RetrofitClientInstance {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://34a6-2402-4000-11c9-7281-e122-3a5c-393a-7d15.ngrok.io/api/";

    public static Retrofit getRetrofitInstance(){
        if(retrofit == null){
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
