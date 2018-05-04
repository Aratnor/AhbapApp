package com.example.pasta.ahbapapp.chat;


import android.content.Intent;
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
import com.example.pasta.ahbapapp.adapter.ChatAdapter;
import com.example.pasta.ahbapapp.chat.message.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment implements ChatAdapter.OnChatSelectedListener {

    private static final String TAG = "HomeFragment";

    @BindView(R.id.chatListRecyclerView)
    RecyclerView mChatRecycler;

    private Query mQuery;
    private ChatAdapter mAdapter;
    private String currentUserID;
    private static final int LIMIT = 15;

    public ChatListFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        ButterKnife.bind(this,view);

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            initFirestore();
            initRecyclerView();
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mChatRecycler.setAdapter(null);
        Log.d(TAG,"onDestroy");
    }

    private void initFirestore() {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        mQuery = mFirestore.collection("chat").document(currentUserID).collection("chats")
                .orderBy("updated_at", Query.Direction.DESCENDING);
    }

    private void initRecyclerView(){
        if (mQuery == null){
            Log.d(TAG, "No query, not initializing RecyclerView");
        }
        Query firstQuery = mQuery.limit(LIMIT);
        mAdapter = new ChatAdapter(firstQuery,this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()
                , LinearLayoutManager.VERTICAL, false);
        mChatRecycler.setLayoutManager(layoutManager);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration
                (mChatRecycler.getContext(),layoutManager.getOrientation());
        mChatRecycler.addItemDecoration(mDividerItemDecoration);
        mChatRecycler.setAdapter(mAdapter);
        mChatRecycler.setHasFixedSize(true);

        mChatRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    public void scrollTop() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mChatRecycler
                .getLayoutManager();
        layoutManager.scrollToPositionWithOffset(0,0);
    }

    @Override
    public void onChatSelected(DocumentSnapshot chat) {
        Log.d("onChatSelected", chat.getId());

        Intent intent = new Intent(getActivity(), MessageActivity.class);
        intent.putExtra("userID", chat.getId());
        startActivity(intent);
    }
}
