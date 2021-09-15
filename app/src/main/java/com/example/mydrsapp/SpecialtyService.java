package com.example.mydrsapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface SpecialtyService {
    @POST("/speciality")
    Call<SpecialtyResponse> saveUser3(@Body SpecialtyRequest specialtyRequest);
}
