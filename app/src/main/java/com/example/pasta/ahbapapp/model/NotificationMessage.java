package com.example.pasta.ahbapapp.model;

public class NotificationMessage {
    private String image;
    public static final String TEXT = "NE alaka";
    private int id;
    public NotificationMessage() { }
    public NotificationMessage(String image, int id, String title, String text, String sound) {
        this.image = image;
        this.id = id;
        this.title = title;
        this.text = text;
        this.sound = sound;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    private String title;
    private String text;
    private String sound;
}
