package com.example.financetracker.model;

public class Data {

    private Double amount;
    private String type;
    private String note;
    private String date;
    private boolean isManual;
    private String id;
    private Long timeStamp;

    // Default constructor
    public Data() {
    }

    // Private constructor for builder
    private Data(Builder builder) {
        this.amount = builder.amount;
        this.type = builder.type;
        this.note = builder.note;
        this.isManual = builder.isManual;
        this.date = builder.date;
        this.timeStamp = builder.timeStamp;
        this.id = builder.id;
    }

    // Static nested Builder class
    public static class Builder {
        private Double amount;
        private String type;
        private String note;
        private String date;
        private boolean isManual;
        private Long timeStamp;
        private String id;

        public Builder setAmount(Double amount) {
            this.amount = amount;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setNote(String note) {
            this.note = note;
            return this;
        }

        public Builder setDate(String date) {
            this.date = date;
            return this;
        }

        public Builder settimeStamp(Long timeStamp){
            this.timeStamp = timeStamp;
            return this;
        }

        public Builder setIsManual(boolean isManual) {
            this.isManual = isManual;
            return this;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Data build() {
            return new Data(this);
        }
    }

    // Getters and Setters
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isManual() {
        return isManual;
    }

    public void setManual(boolean isManual) {
        this.isManual = isManual;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
