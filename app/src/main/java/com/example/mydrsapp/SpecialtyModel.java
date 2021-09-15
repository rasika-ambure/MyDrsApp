package com.example.mydrsapp;

public class SpecialtyModel {
    private String id;
    private String patient_id;
    private String name;
    private String code;

    public SpecialtyModel(String id, String patient_id, String name, String code) {
        this.id = id;
        this.patient_id = patient_id;
        this.name = name;
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
