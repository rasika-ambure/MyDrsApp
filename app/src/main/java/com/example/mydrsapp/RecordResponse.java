package com.example.mydrsapp;

public class RecordResponse {
    private Data data;

    public Data getData() {
        return data;
    }
    public class Data {

        private String id;
        private String provider_id;
        private String category;
        private String duration_sec;
        private String name;
        private String created;

        public String getId() {
            return id;
        }

        public String getProvider_id() {
            return provider_id;
        }

        public String getCategory() {
            return category;
        }

        public String getDuration_sec() {
            return duration_sec;
        }

        public String getName() {
            return name;
        }

        public String getCreated() {
            return created;
        }
    }
}
