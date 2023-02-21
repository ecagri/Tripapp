package com.cagri.tripapp;

public class Message {
    private String text;
    private String sender;
    private String reciever;
    private String date;
    private String postId;
    private String image;
    private Boolean seen;
    private Boolean liked;

    public Message(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getSender() {
        return sender;
    }

    public String getReciever() {
        return reciever;
    }

    public String getDate() {
        return date;
    }

    public String getPostId() {
        return postId;
    }

    public String getImage() {
        return image;
    }

    public Boolean getSeen() {
        return seen;
    }

    public Boolean getLiked() {
        return liked;
    }
}
