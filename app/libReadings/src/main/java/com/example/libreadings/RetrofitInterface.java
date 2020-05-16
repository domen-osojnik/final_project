package com.example.libreadings;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {
    @POST("/test")
    Call<Void> sendReadings(@Body HashMap<String, String> map);
}

