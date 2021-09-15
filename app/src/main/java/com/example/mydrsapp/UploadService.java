package com.example.mydrsapp;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface UploadService {
    @Multipart
    @POST("/recording/upload")
    Call<UploadResponse> uploadRecording( @Query("patient_id") String patient_id, @Query("file_name") String recording_id, @Part MultipartBody.Part audio);

}
