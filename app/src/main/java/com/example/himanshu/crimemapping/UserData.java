package com.example.himanshu.crimemapping;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserData {
    private String eml;
    private String pass;

    public UserData() {

    }

    public UserData(String id, String eml, String pass) {
        this.eml = eml;
        this.pass = pass;
    }

    public String getEmail() {
        return eml;
    }

    public String getPass() {
        return pass;
    }
}