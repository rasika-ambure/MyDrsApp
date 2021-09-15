package com.example.mydrsapp.services;

import com.example.mydrsapp.model.RecordRequest;
import com.example.mydrsapp.model.RecordResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RecordService {
    @POST("/recording")
    Call<RecordResponse> saveRecording(@Body RecordRequest recordRequest);
}
