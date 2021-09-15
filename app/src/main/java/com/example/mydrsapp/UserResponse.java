package com.example.mydrsapp;

import java.util.ArrayList;

public class UserResponse {

    String message;
    public String getMessage() {
        return message;
    }


    private Data data;

    public Data getData() {
        return data;
    }

    public class Data {

        private String id;
        private String first_name;
        private Boolean setup_done;

        public Data(String id, String first_name, Boolean setup_done) {
            this.id = id;
            this.first_name = first_name;
            this.setup_done = setup_done;
        }

        public Boolean getSetup_done() {
            return setup_done;
        }

        public String getFirst_name() {
            return first_name;
        }

        public String getId() {
            return id;
        }

    }
}
