package com.example.pasta.ahbapapp.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.interfaces.PostListContract;
import com.example.pasta.ahbapapp.model.PostModel;
import com.example.pasta.ahbapapp.model.PostRecyclerAdapter;
import com.example.pasta.ahbapapp.presenter.PostListPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment{

    private List<PostModel> mPostModelList;
    private PostRecyclerAdapter mPostRecyclerAdapter;
    private FirebaseFirestore mFirebaseFirestore;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mPostModelList = new ArrayList<>();
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        RecyclerView mPostRecyclerView = view.findViewById(R.id.postListRecyclerView);
        mPostRecyclerAdapter = new PostRecyclerAdapter(mPostModelList);

        mPostRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mPostRecyclerView.setAdapter(mPostRecyclerAdapter);
        mPostRecyclerView.setHasFixedSize(true);

        if(mFirebaseAuth.getCurrentUser() != null){
            mFirebaseFirestore = FirebaseFirestore.getInstance();
            
            mPostRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if(reachedBottom){
                        loadMorePost();
                    }
                }
            });

            Query firstQuery = mFirebaseFirestore.collection("posts")
                    .orderBy("created_at", Query.Direction.DESCENDING).limit(3);
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        if (isFirstPageFirstLoad) {
                            lastVisible = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);

                            mPostModelList.clear();
                        }
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String blogPostId = doc.getDocument().getId();
                                PostModel post = doc.getDocument()
                                        .toObject(PostModel.class).withId(blogPostId);

                                if (isFirstPageFirstLoad) {
                                    mPostModelList.add(post);
                                } else {
                                    mPostModelList.add(0, post);
                                }

                                mPostRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                        isFirstPageFirstLoad = false;
                    }
                }
            });

        }

        return view;
    }

    private void loadMorePost() {

        Query nextQuery = mFirebaseFirestore.collection("posts")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);
        nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()){
                    lastVisible = queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() - 1);
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blogPostId= doc.getDocument().getId();
                            PostModel post = doc.getDocument().toObject(PostModel.class).withId(blogPostId);
                            mPostModelList.add(post);

                            mPostRecyclerAdapter.notifyDataSetChanged();
                        }

                    }
                }
            }
        });
    }
}
