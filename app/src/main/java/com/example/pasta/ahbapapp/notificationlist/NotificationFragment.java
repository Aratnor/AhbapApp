package com.example.pasta.ahbapapp.notificationlist;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.adapter.NotificationAdapter;
import com.example.pasta.ahbapapp.adapter.PostAdapter;
import com.example.pasta.ahbapapp.postdetail.PostDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment implements NotificationAdapter.OnNotificationSelectedListener{

    private static final String TAG = "NotificationFragment";

    @BindView(R.id.notification_list_recycler)
    RecyclerView mNotificationRecycler;

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private NotificationAdapter mAdapter;
    private String currentUser_id;

    private final int LIMIT = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notification_list,container,false);
        ButterKnife.bind(this,root);

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            currentUser_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            initFirestore();
            initRecyclerView();
            Log.i("Current user is", FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        return root;
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();

        // Get the 50 highest rated restaurants
        mQuery = mFirestore.collection("users/" + currentUser_id + "/Notifications/");
    }

    public void scrollTop() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mNotificationRecycler
                .getLayoutManager();
        layoutManager.scrollToPositionWithOffset(0,0);
    }

    private void initRecyclerView(){
        if (mQuery == null){
            Log.d(TAG, "No query, not initializing RecyclerView");
        }
        Query firstQuery = mQuery.limit(LIMIT);
        mAdapter = new NotificationAdapter(mQuery.limit(LIMIT),this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()
                , LinearLayoutManager.VERTICAL, false);
        mNotificationRecycler.setLayoutManager(layoutManager);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration
                (mNotificationRecycler.getContext(),layoutManager.getOrientation());
        mNotificationRecycler.addItemDecoration(mDividerItemDecoration);
        mNotificationRecycler.setAdapter(mAdapter);
        mNotificationRecycler.setItemViewCacheSize(30);

        mNotificationRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Boolean bottomReached = !recyclerView.canScrollVertically(1);
                if (bottomReached){
                    Query moreQ = mQuery.startAfter(mAdapter.getLastVisible()).limit(LIMIT);
                    mAdapter.loadMore(moreQ);
                }
            }
        });
        mAdapter.startListening();
    }

    @Override
    public void onNotificationSelected(DocumentSnapshot documentSnapshot) {
        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
        intent.putExtra(PostDetailActivity.POST_ID, (String)documentSnapshot.get("post_id"));
        startActivity(intent);
    }
}
