package com.example.mydrsapp.model;

public class UserUpdate {

    private String id;
    private String first_name;

    private Boolean setup_done;

    public UserUpdate(String id, String first_name, Boolean setup_done) {
        this.id = id;
        this.first_name = first_name;
        this.setup_done = setup_done;

    }

    public Boolean getSetup_done() {
        return setup_done;
    }

    public void setSetup_done(Boolean setup_done) {
        this.setup_done = setup_done;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
