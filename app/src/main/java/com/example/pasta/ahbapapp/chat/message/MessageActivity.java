package com.example.pasta.ahbapapp.chat.message;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pasta.ahbapapp.MainActivity;
import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.adapter.MessageAdapter;
import com.example.pasta.ahbapapp.adapter.PostAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    @BindView(R.id.message)
    EditText message;
    @BindView(R.id.messageButton)
    ImageButton messageButton;
    @BindView(R.id.messageList)
    RecyclerView mMessageRecycler;
    @BindView(R.id.message_toolbar_image)
    CircleImageView message_toolbar_image;
    @BindView(R.id.message_toolbar_name)
    TextView message_toolbar_name;
    @BindView(R.id.message_toolbar)
    Toolbar message_toolbar;

    private final String TAG = "MEssageActivity";
    private final int LIMIT = 15;

    private MessageAdapter mAdapter;
    private Query mQuery;

    private String messageUserId;
    private String currentUserId;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);

        db = FirebaseFirestore.getInstance();

        messageUserId = getIntent().getExtras().getString("userID");
        sharedPreferences = getSharedPreferences("com.example.pasta.ahbapapp"
                , Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getString(MainActivity.USER_ID,"");
        initQuery();
        initRecyclerView();
        setToolBar();
    }

    private void setToolBar() {
        setSupportActionBar(message_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        String imageUrl = sharedPreferences.getString(MainActivity.USER_IMAGE,"");
        String name = sharedPreferences.getString(MainActivity.USER_NAME,"");
        if(!imageUrl.isEmpty())
        Glide.with(this)
                .load(imageUrl)
                .into(message_toolbar_image);
        else {
            Log.i(TAG,"image url empty");
        }
        if(!name.isEmpty()) {
            message_toolbar_name.setText(name);
        }
        else {
            Log.i(TAG,"name text empty");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initRecyclerView(){
        if (mQuery == null){
            Log.d(TAG, "No query, not initializing RecyclerView");
        }
        Query firstQuery = mQuery.limit(LIMIT);
        mAdapter = new MessageAdapter(firstQuery){
            @Override
            protected void onDocumentAdded(DocumentChange change) {
                super.onDocumentAdded(change);
                if ((boolean)change.getDocument().get("sender"))
                    mMessageRecycler.scrollToPosition(0);
            }
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(this
                , LinearLayoutManager.VERTICAL, true);
        mMessageRecycler.setLayoutManager(layoutManager);
        mMessageRecycler.setAdapter(mAdapter);
        mMessageRecycler.setHasFixedSize(true);
        mMessageRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Boolean topReached = !recyclerView.canScrollVertically(-1);
                if (topReached){
                    Query moreQ = mQuery.startAfter(mAdapter.getLastVisible()).limit(LIMIT);
                    mAdapter.loadMore(moreQ);
                }
            }
        });
        mAdapter.startListening();
    }

    public void initQuery() {
        mQuery = db.collection("message").document(currentUserId).collection(messageUserId)
                .orderBy("timeStamp",Query.Direction.DESCENDING);
    }

    @OnClick(R.id.messageButton)
    public void sendMessage() {
        Log.i(TAG,"Button Clicked");
        if(!currentUserId.isEmpty()) {
            final String context = message.getText().toString();
            final Date timeStamp = new Date();
            HashMap<String,Object> map = new HashMap<>();
            map.put("message",context);
            map.put("timeStamp",timeStamp);
            map.put("sender",true);
            db.collection("message").document(currentUserId).collection(messageUserId)
                    .add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.i(TAG,"Message successfully added to currentUser Database");
                }
            });
            map.put("sender",false);
            db.collection("message").document(messageUserId).collection(currentUserId)
                    .add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.i(TAG,"Message successfully added to messageUser Database");
                }
            });
            db.collection("chat").document(currentUserId).collection("chats").document(messageUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("last_message", context);
                    map.put("updated_at",timeStamp);
                    DocumentReference chat = documentSnapshot.getReference();
                    if(documentSnapshot.exists()){
                        chat.update(map);
                    }
                    else {
                        chat.set(map);
                    }
                }
            });
            db.collection("chat").document(messageUserId).collection("chats").document(currentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("last_message", context);
                    map.put("updated_at",timeStamp);
                    DocumentReference chat = documentSnapshot.getReference();
                    if(documentSnapshot.exists()){
                        chat.update(map);
                    }
                    else {
                        chat.set(map);
                    }
                }
            });
        }
        else {
            Log.i(TAG,"UserID is null from sharedPref");
        }

    }

}
