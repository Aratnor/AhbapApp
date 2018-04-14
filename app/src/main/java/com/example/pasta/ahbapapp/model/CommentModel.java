package com.example.pasta.ahbapapp.model;

/**
 * Created by pasta on 14.04.2018.
 */

public class CommentModel {

    private String author_id;
    private String author_name;
    private String author_image_url;
    private String comment_body;

    public CommentModel() {
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_image_url() {
        return author_image_url;
    }

    public void setAuthor_image_url(String author_image_url) {
        this.author_image_url = author_image_url;
    }

    public String getComment_body() {
        return comment_body;
    }

    public void setComment_body(String comment_body) {
        this.comment_body = comment_body;
    }
}
