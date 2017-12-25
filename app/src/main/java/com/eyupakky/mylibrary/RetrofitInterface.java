package com.eyupakky.mylibrary;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by eyupakkaya on 24.12.2017.
 */

public interface RetrofitInterface {

    @GET("data/{userid}/")
    Call<List<String>>getData();

    @POST("setdata/")
    Call<List<String>>setData(@Body SetPojo setPojo);

}
