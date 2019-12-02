package com.example.opensourceproject.Utils;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface APIInterface {
    @Headers("Content-Type: application/json")
    @POST("opensource/login")
    Call<ResponseBody> loginUser(@Body HashMap<String, String> body);

    @Headers("Content-Type: application/json")
    @POST("opensource/register")
    Call<ResponseBody> registerUser(@Body HashMap<String, String> body);

    @Headers("Content-Type: application/json")
    @POST("opensource/upload")
    Call<ResponseBody> uploadFile(@Body HashMap<String, String> body);

    @Headers("Content-Type: application/json")
    @GET("opensource/browse")
    Call<ResponseBody> browseFile();
}
