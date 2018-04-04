package com.example.pasta.ahbapapp.interfaces;

import com.example.pasta.ahbapapp.model.PostModel;

import java.util.List;

/**
 * Created by pasta on 3.04.2018.
 */

public interface PostListContract {

    interface PostListView{
        void recylerNotifyDataSetChanged();

    }

    interface PostListPresenter{
        List<PostModel> getListFirst();
        List<PostModel> getPostList();
    }
}
