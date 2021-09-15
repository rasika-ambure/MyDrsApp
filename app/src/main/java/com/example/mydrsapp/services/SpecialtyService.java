package com.example.mydrsapp.services;

import com.example.mydrsapp.model.SpecialtyRequest;
import com.example.mydrsapp.model.SpecialtyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SpecialtyService {
    @POST("/speciality")
    Call<SpecialtyResponse> saveUser3(@Body SpecialtyRequest specialtyRequest);
}
