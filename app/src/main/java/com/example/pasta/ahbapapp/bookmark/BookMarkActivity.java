package com.example.pasta.ahbapapp.bookmark;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.pasta.ahbapapp.MainActivity;
import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.adapter.BookMarkAdapter;
import com.example.pasta.ahbapapp.adapter.PostAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookMarkActivity extends AppCompatActivity {
    private static final String TAG =  "BookmarkActivity";
    private static final long LIMIT = 5;
    @BindView(R.id.bookmark_recycler)
    RecyclerView mBookMarkRecycler;
    @BindView(R.id.bookmark_toolbar)
    Toolbar bookmark_toolbar;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private BookMarkAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_mark);
        ButterKnife.bind(this);

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            initToolbar();
            initFirestore();
            initRecyclerView();
        }
    }

    private void initToolbar(){
        setSupportActionBar(bookmark_toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initFirestore() {
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.pasta.ahbapapp"
                , Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString(MainActivity.USER_ID,"");

        mFirestore = FirebaseFirestore.getInstance();

        mQuery = mFirestore.collection("users").document(userId).collection("bookmarks")
                .orderBy("bookmarked_date", Query.Direction.DESCENDING);
    }

    private void initRecyclerView(){
        if (mQuery == null){
            Log.d(TAG, "No query, not initializing RecyclerView");
        }
        Query firstQuery = mQuery.limit(LIMIT);
        mAdapter = new BookMarkAdapter(firstQuery,this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext()
                , LinearLayoutManager.VERTICAL, false);
        mBookMarkRecycler.setLayoutManager(layoutManager);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration
                (mBookMarkRecycler.getContext(),layoutManager.getOrientation());
        mBookMarkRecycler.addItemDecoration(mDividerItemDecoration);
        mBookMarkRecycler.setAdapter(mAdapter);
        mBookMarkRecycler.setItemViewCacheSize(30);

        mBookMarkRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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


}
