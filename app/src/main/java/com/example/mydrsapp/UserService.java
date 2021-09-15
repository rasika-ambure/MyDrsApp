package com.example.mydrsapp;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {
    @POST("/auth/login")
 Call<UserResponse> saveUser(@Body UserRequest userRequest);

    @PUT("/patient")
    Call<UserUpdate> putPost(@Query("id") String id, @Body UserUpdate userUpdate);
}
