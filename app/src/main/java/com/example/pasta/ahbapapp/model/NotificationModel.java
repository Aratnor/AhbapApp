package com.example.pasta.ahbapapp.model;

public class NotificationModel {
    private String from;
    private String message;
    private String post_id;

    public NotificationModel() {}

    public NotificationModel(String from, String message, String post_id) {
        this.from = from;
        this.message = message;
        this.post_id = post_id;
    }

    public String getFrom() { return from; }

    public String getMessage() { return message; }

    public String getPost_id() { return post_id; }
}
