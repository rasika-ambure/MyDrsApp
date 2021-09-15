package com.example.mydrsapp.model;

public class RecordRequest {

    String patient_id;
    String provider_id;
    int duration_sec;
    String category;
    String name;

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public void setProvider_id(String provider_id) {
        this.provider_id = provider_id;
    }

    public void setDuration_sec(int duration_sec) {
        this.duration_sec = duration_sec;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }
}
