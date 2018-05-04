package com.example.pasta.ahbapapp.model;

import java.util.Date;

public class Message {
    private String message;
    private Date timeStamp;
    private boolean sender;

    public Message(String message, Date timeStamp, boolean isSender) {
        this.message = message;
        this.timeStamp = timeStamp;
        this.sender = isSender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean getSender() {
        return sender;
    }

    public void setSender(boolean sender) {
        sender = sender;
    }
}
