package com.example.pasta.ahbapapp.comment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.adapter.CommentAdapter;
import com.example.pasta.ahbapapp.adapter.PostAdapter;
import com.example.pasta.ahbapapp.postdetail.PostDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CommentListFragment extends Fragment {

    private static final String TAG = "CommentListFragment";

    @BindView(R.id.comment_list_recycler)
    RecyclerView mCommentRecycler;

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private CommentAdapter mAdapter;

    private static final int LIMIT = 15;
    private String postId;

    public CommentListFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_list, container, false);
        ButterKnife.bind(this,view);

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            getPostId();
            initFirestore();
            initRecyclerView();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCommentRecycler.setAdapter(null);
    }
    private void getPostId() {
        if (getArguments() == null){
            throw new IllegalArgumentException("Must pass extra " + PostDetailActivity.POST_ID);
        }
        postId = getArguments().getString(PostDetailActivity.POST_ID);
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();

        // Get the 50 highest rated restaurants
        mQuery = mFirestore.collection("posts").document(postId).collection("comments")
                .orderBy("created_at", Query.Direction.DESCENDING);
    }

    private void initRecyclerView() {
        if (mQuery == null){
            Log.d(TAG, "No query, not initializing RecyclerView");
        }
        Query firstQuery = mQuery.limit(LIMIT);
        mAdapter = new CommentAdapter(firstQuery);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()
                , LinearLayoutManager.VERTICAL, false);
        mCommentRecycler.setLayoutManager(layoutManager);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration
                (mCommentRecycler.getContext(),layoutManager.getOrientation());
        mCommentRecycler.addItemDecoration(mDividerItemDecoration);
        mCommentRecycler.setAdapter(mAdapter);
        mCommentRecycler.setItemViewCacheSize(30);

        mCommentRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Boolean bottomReached = !recyclerView.canScrollVertically(1);
                if (bottomReached){
                    if (mAdapter.getLastVisible() != null){
                        Query moreQ = mQuery.startAfter(mAdapter.getLastVisible()).limit(LIMIT);
                        mAdapter.loadMore(moreQ);
                    }
                }
            }
        });
    }
}
