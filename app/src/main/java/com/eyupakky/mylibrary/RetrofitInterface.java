package com.eyupakky.mylibrary;

import com.eyupakky.mylibrary.Pojo.BookPojo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by eyupakkaya on 24.12.2017.
 */

public interface RetrofitInterface {
    @GET("volumes?")
    Call<BookPojo>setData(@Query("q") String isbn);

}
