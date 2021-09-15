package com.example.mydrsapp.services;

import com.example.mydrsapp.model.DeleteResponse;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Query;

public interface DeleteService {
    @DELETE("/recording")
    Call<DeleteResponse> DeleteRecording(@Query("patient_id") String patient_id, @Query("id") String id);
}
