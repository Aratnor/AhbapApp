package com.example.pasta.ahbapapp.interfaces;

import android.net.Uri;
import android.support.annotation.StringRes;

/**
 * Created by pasta on 28.03.2018.
 */

public interface NewPostContract {

    interface NewPostView{

        void showProgress();
        void hideProgress();
        void contentError(@StringRes int resId);
        void cityError(@StringRes int resId);
        void categoryError(@StringRes int resId);
        void postError(String err);
        void startMain();
    }

    interface NewPostPresenter{

        void addPost(String content, Uri imageUri, String city, String category);
        void uploadPost(String content, String imageUrl, String city, String category);
        void uploadImageAndPost(String content, Uri imageUri, String city, String category);
    }
}