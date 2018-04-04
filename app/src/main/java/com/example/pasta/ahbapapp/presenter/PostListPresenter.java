package com.example.pasta.ahbapapp.presenter;

import com.example.pasta.ahbapapp.interfaces.PostListContract;
import com.example.pasta.ahbapapp.model.PostModel;
import com.example.pasta.ahbapapp.model.PostRecyclerAdapter;
import com.example.pasta.ahbapapp.view.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
