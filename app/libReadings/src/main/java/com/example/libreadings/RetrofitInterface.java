package com.example.libreadings;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {
    /*
    Hello world primer
    @POST("/test")
    Call<Void> sendReadings(@Body HashMap<String, String> map);
     */

    @Headers("Content-Type: application/json")
    @POST("/recordings")
    Call<SensorReading> postReading(@Body SensorReading reading);
}

