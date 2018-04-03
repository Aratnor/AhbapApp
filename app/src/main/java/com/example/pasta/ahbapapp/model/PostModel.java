package com.example.pasta.ahbapapp.model;

import java.util.Date;

/**
 * Created by pasta on 28.03.2018.
 */

public class PostModel extends PostId {

    private String user_id;
    private String city;
    private String category;
    private String content;
    private String image_url;
    private Date created_at;
    private Date updated_at;

    public PostModel() {
    }

    public PostModel(String user_id, String city, String category, String content, String image_url,
        Date created_at, Date updated_at) {
        this.user_id = user_id;
        this.city = city;
        this.category = category;
        this.content = content;
        this.image_url = image_url;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
}
