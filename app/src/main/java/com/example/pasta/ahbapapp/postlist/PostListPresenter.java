package com.example.pasta.ahbapapp.postlist;

import com.example.pasta.ahbapapp.model.PostModel;

import java.util.List;

/**
 * Created by pasta on 3.04.2018.
 */

public class PostListPresenter implements PostListContract.PostListPresenter {
    PostListContract.PostListView mView;


    public PostListPresenter(PostListContract.PostListView mView) {
        this.mView = mView;
    }

    @Override
    public List<PostModel> getListFirst() {
        return null;
    }

    @Override
    public List<PostModel> getPostList() {
        return null;
    }
}
