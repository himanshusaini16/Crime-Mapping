package com.example.himanshu.crimemapping;


public class Crime {
    private String type, thumbnailUrl,descrip,date,time,lat2,lng2;

    public Crime() {
    }

    public Crime(String type1, String thumbnailUrl1,String descrip1, String date1, String time1, String latt, String lngg) {
        this.type = type1;
        this.thumbnailUrl = thumbnailUrl1;
        this.descrip = descrip1;
        this.date = date1;
        this.time = time1;
        this.lat2=latt;
        this.lng2=lngg;
    }

    public String getTitle() {
        return type;
    }

    public void setTitle(String type1) {
        this.type = type1;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl1) {
        this.thumbnailUrl = thumbnailUrl1;
    }

    public String getDes() {
        return descrip;
    }

    public void setDes(String descrip1) {
        this.descrip = descrip1;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date1) {
        this.date = date1;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time1) {
        this.time = time1;
    }

    public String getLat() {
        return lat2;
    }

    public void setLat(String latt) {
        this.lat2 = latt;
    }

    public String getLng() {
        return lng2;
    }

    public void setLng(String lngg) {
        this.lng2 = lngg;
    }



}