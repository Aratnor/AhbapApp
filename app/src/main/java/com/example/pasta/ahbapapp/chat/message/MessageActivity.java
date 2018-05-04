package com.example.pasta.ahbapapp.chat.message;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.pasta.ahbapapp.MainActivity;
import com.example.pasta.ahbapapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageActivity extends AppCompatActivity {
    @BindView(R.id.message)
    EditText message;
    @BindView(R.id.messageButton)
    ImageButton messageButton;

    private final String TAG = "MEssageActivity";

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
            db.collection("message").document(currentUserId).collection(messageUserId).add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.i(TAG,"Message successfully added to currentUser Database");
                }
            });
            map.put("sender",false);
            db.collection("message").document(messageUserId).collection(currentUserId).add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
