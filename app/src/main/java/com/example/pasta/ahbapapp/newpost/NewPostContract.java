package com.example.pasta.ahbapapp.newpost;

import android.net.Uri;
import android.support.annotation.StringRes;

import com.example.pasta.ahbapapp.model.PostModel;

/**
 * Created by pasta on 28.03.2018.
 */

public interface NewPostContract {

    interface NewPostView{

        void showProgress();
        void hideProgress();
        void contentError(String err);
        void uploadError(String err);
        void sendBack();
    }

    interface NewPostPresenter{

        void addPost(String content, Uri imageUri, String city, String category);
        void uploadPost(String content, String imageUrl, String city, String category);
        void uploadImageAndPost(String content, Uri imageUri, String city, String category);
        void userData(String userID, String userName, String userImage);
    }
}
