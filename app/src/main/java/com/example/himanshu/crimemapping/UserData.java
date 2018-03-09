package com.example.himanshu.crimemapping;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserData {
    private String id;
    private String eml;
    private String pass;

    public UserData() {

    }

    public UserData(String id, String eml, String pass) {
        this.eml = eml;
        this.pass = pass;
        this.id = id;
    }

    public String getEmail() {
        return eml;
    }

    public String getPass() {
        return pass;
    }
}