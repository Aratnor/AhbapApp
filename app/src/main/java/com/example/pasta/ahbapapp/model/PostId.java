package com.example.pasta.ahbapapp.model;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

/**
 * Created by pasta on 3.04.2018.
 */

public class PostId {
    @Exclude
    private String postId;

    public  <T extends PostId> T withId(@NonNull final String postId) {
        this.postId = postId;
        return (T) this;
    }

    public String getPostId(){
        return postId;
    }
}
