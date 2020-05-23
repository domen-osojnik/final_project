package com.example.libreadings;

import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitInterface {
    /*
    Hello world primer
    @POST("/test")
    Call<Void> sendReadings(@Body HashMap<String, String> map);

    @Headers("Content-Type: application/json")
    @POST("/recordings")
    Call<SensorReading> postReading(@Body SensorReading reading);
     */

    // @Headers("Content-Type: application/json")
    @POST("/recordings")
    @Multipart
    Call<ResponseBody> postReading(@Part("data") SensorReading reading, @Part List<MultipartBody.Part> file);

}

