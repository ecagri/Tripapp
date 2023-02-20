package com.cagri.tripapp;

public class User {
    private String profile_picture;
    private String username;

    private String last_message;

    public User(String profile_picture, String username,String last_message) {
        this.profile_picture = profile_picture;
        this.username = username;
        this.last_message=last_message;
    }
    public User(){

    }

    public String getProfile_picture() {
        return profile_picture;
    }



    public String getUsername() {
        return username;
    }

    public String getLast_message() {
        return last_message;
    }
}
