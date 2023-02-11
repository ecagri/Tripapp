package com.cagri.tripapp;

import androidx.annotation.Nullable;

public class Post implements Comparable{
    private String username;
    private String profile_picture;
    private String description;
    private String post_picture;
    private String date;
    private String id;
    public Post(String username, String profile_picture, String description, String post_picture, String date, String id){
        this.username = username;
        this.profile_picture = profile_picture;
        this.description = description;
        this.post_picture = post_picture;
        this.date = date;
        this.id = id;
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

    public String getId() {
        return id;
    }
    @Override
    public int compareTo(Object o) {
        Post post = (Post) o;
        return this.date.compareTo(post.getDate());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Post post = (Post) obj;
        return this.id.equals(((Post) obj).getId());
    }
}
