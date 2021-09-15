package com.example.mydrsapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ProviderService {
    @POST("/provider")
    Call<ProviderResponse> saveUser2(@Body ProviderRequest providerRequest);
}
