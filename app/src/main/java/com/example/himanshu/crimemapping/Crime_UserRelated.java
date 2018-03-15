package com.example.himanshu.crimemapping;

public class Crime_UserRelated {


    private String type, thumbnailUrl, descrip, address, reportdate;

    public Crime_UserRelated() {
    }

    public Crime_UserRelated(String type1, String thumbnailUrl1, String descrip1, String address1, String reportdate1) {
        this.type = type1;
        this.thumbnailUrl = thumbnailUrl1;
        this.descrip = descrip1;
        this.address = address1;
        this.reportdate = reportdate1;
    }

    public String getTitleUser() {
        return type;
    }

    public void setTitleUser(String type1) {
        this.type = type1;
    }

    String getThumbnailUrlUser() {
        return thumbnailUrl;
    }

    public void setThumbnailUrlUser(String thumbnailUrl1) {
        this.thumbnailUrl = thumbnailUrl1;
    }

    public String getDesUser() {
        return descrip;
    }

    public void setDesUser(String descrip1) {
        this.descrip = descrip1;
    }

    String getAddressUser() {
        return address;
    }

    public void setAddressUser(String address1) {
        this.address = address1;
    }

    public String getReportedDateUser() {
        return reportdate;
    }

    public void setReportedDateUser(String reportdate1) {
        this.reportdate = reportdate1;
    }


}
