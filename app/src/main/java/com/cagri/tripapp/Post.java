package com.cagri.tripapp;

public class Post implements Comparable{
    private String username;

    private String profile_picture;
    private String description;
    private String post_picture;
    private String date;
    public Post(String username, String profile_picture, String description, String post_picture, String date){
        this.username = username;
        this.profile_picture = profile_picture;
        this.description = description;
        this.post_picture = post_picture;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getPost_picture() {
        return post_picture;
    }

    @Override
    public int compareTo(Object o) {
        Post post = (Post) o;
        return this.date.compareTo(post.getDate());
    }
}
