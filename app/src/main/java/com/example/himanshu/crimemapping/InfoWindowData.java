package com.example.himanshu.crimemapping;



public class InfoWindowData {
    private String heading_info;
    private String description_info;
    private String date_info;
    private String time_info;
    private String date_info_reported;
    private String time_info_reported;


    public String getHeading() {
        return heading_info;
    }

    public void setHeading(String image) {
        this.heading_info = image;
    }

    public String getDescription() {
        return description_info;
    }

    public void setDescription(String hotel) {
        this.description_info = hotel;
    }

    public String getDate() {
        return date_info;
    }

    public void setDate(String food) {
        this.date_info = food;
    }

    public String getTime() {
        return time_info;
    }

    public void setTime(String transport) {
        this.time_info = transport;
    }

    public String getDateReported() {
        return date_info_reported;
    }

    public void setDateReported(String foodey) {
        this.date_info_reported = foodey;
    }

    public String getTimeReported() {
        return time_info_reported;
    }

    public void setTimeReported(String transporty) {
        this.time_info_reported = transporty;
    }

}
