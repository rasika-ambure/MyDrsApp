package com.example.mydrsapp.utils;

import com.example.mydrsapp.services.DeleteService;
import com.example.mydrsapp.services.ProviderService;
import com.example.mydrsapp.services.RecordService;
import com.example.mydrsapp.services.ServiceApi;
import com.example.mydrsapp.services.SpecialtyService;
import com.example.mydrsapp.services.UploadService;
import com.example.mydrsapp.services.UserService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit getRetrofit() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://65.2.3.41:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        return retrofit;
    }
    public static UserService getUserService(){
        UserService userService = getRetrofit().create(UserService.class);
        return userService;
    }
    public static ProviderService getProviderService(){
        ProviderService providerService = getRetrofit().create(ProviderService.class);
        return providerService;
    }
    public static SpecialtyService getSpecialtyService(){
        SpecialtyService specialtyService = getRetrofit().create(SpecialtyService.class);
        return specialtyService;
    }
    public static RecordService getRecordService(){
        RecordService recordService = getRetrofit().create(RecordService.class);
        return recordService;
    }
    public static UploadService getUploadService(){
        UploadService uploadService = getRetrofit().create(UploadService.class);
        return uploadService;
    }
    public static DeleteService getDeleteService(){
        DeleteService deleteService = getRetrofit().create(DeleteService.class);
        return deleteService;
    }
    public static ServiceApi getServiceApi(){
        ServiceApi serviceApi = getRetrofit().create(ServiceApi.class);
        return serviceApi;
    }
}
