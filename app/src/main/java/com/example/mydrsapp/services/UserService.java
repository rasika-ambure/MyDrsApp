package com.example.mydrsapp.services;
import com.example.mydrsapp.model.TypeUpdate;
import com.example.mydrsapp.model.UserRequest;
import com.example.mydrsapp.model.UserResponse;
import com.example.mydrsapp.model.UserUpdate;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface UserService {
    @POST("/auth/login")
 Call<UserResponse> saveUser(@Body UserRequest userRequest);

    @PUT("/patient")
    Call<UserUpdate> putPost(@Query("id") String id, @Body UserUpdate userUpdate);

    @PUT("/patient")
    Call<TypeUpdate> putType(@Query("id") String id, @Body TypeUpdate typeUpdate);
}
