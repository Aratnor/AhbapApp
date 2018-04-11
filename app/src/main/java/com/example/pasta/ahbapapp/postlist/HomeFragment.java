package com.example.pasta.ahbapapp.postlist;

import android.content.Intent;
import android.nfc.Tag;
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

import com.example.pasta.ahbapapp.MainActivity;
import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.adapter.PostAdapter;
import com.example.pasta.ahbapapp.postdetail.PostDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import butterknife.BindView;
import butterknife.ButterKnife;


public class HomeFragment extends Fragment implements PostAdapter.OnPostSelectedListener{

    private static final String TAG = "HomeFragment";

    @BindView(R.id.postListRecyclerView)
    RecyclerView mPostRecycler;

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private PostAdapter mAdapter;

    private static final int LIMIT = 15;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this,view);

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            initFirestore();
            initRecyclerView();
        }

        return view;
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();

        // Get the 50 highest rated restaurants
        mQuery = mFirestore.collection("posts")
                .orderBy("created_at", Query.Direction.DESCENDING);
    }

    private void initRecyclerView(){
        if (mQuery == null){
            Log.d(TAG, "No query, not initializing RecyclerView");
        }
        Query firstQuery = mQuery.limit(LIMIT);
        mAdapter = new PostAdapter(firstQuery,getActivity(),this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()
                , LinearLayoutManager.VERTICAL, false);
        mPostRecycler.setLayoutManager(layoutManager);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration
                (mPostRecycler.getContext(),layoutManager.getOrientation());
        mPostRecycler.addItemDecoration(mDividerItemDecoration);
        mPostRecycler.setAdapter(mAdapter);
        mPostRecycler.setHasFixedSize(true);
        mPostRecycler.setItemViewCacheSize(30);

        mPostRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
    }

    public void scrollTop() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mPostRecycler
                .getLayoutManager();
        layoutManager.scrollToPositionWithOffset(0,0);
    }

    public void setQuery(String cat, String city) {
        if(cat.equals("Kategori") && city.equals("İl Seçiniz")) {
            mQuery = mFirestore.collection("posts").orderBy("created_at",Query.Direction.DESCENDING);
            mAdapter.setQuery(mQuery.limit(LIMIT));
        }
        else if(cat.equals("Kategori") && !city.equals("İl Seçiniz")) {
            mQuery = mFirestore.collection("posts").whereEqualTo("city",city).orderBy("created_at",Query.Direction.DESCENDING);
            mAdapter.setQuery(mQuery.limit(LIMIT));
        }
        else if(!cat.equals("Kategori") && city.equals("İl Seçiniz")) {
            mQuery =mFirestore.collection("posts").whereEqualTo("category",cat).orderBy("created_at",Query.Direction.DESCENDING);
            mAdapter.setQuery(mQuery.limit(LIMIT));
        }
        else {
            mQuery = mFirestore.collection("posts").whereEqualTo("category", cat)
                    .whereEqualTo("city", city)
                    .orderBy("created_at", Query.Direction.DESCENDING);
            mAdapter.setQuery(mQuery.limit(LIMIT));
        }
    }

    @Override
    public void onPostSelected(DocumentSnapshot post) {
        Log.d("onPostSelected", "go");
        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
        intent.putExtra(PostDetailActivity.POST_ID, post.getId());
        startActivity(intent);
    }
}
