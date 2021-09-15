package com.example.mydrsapp.services;

import com.example.mydrsapp.model.UploadResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ServiceApi {
    @GET
    Call<UploadResponse> download(@Url String fileUrl);
}
