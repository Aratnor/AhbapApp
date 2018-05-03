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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import butterknife.BindView;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.message)
    EditText message;
    @BindView(R.id.messageButton)
    ImageButton messageButton;
    @BindView(R.id.message_list)
    RecyclerView messageList;

    private final String TAG = "Info";

    private String messageUserId;
    private String currentUserId;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        messageUserId = Objects.requireNonNull(getIntent().getExtras()).getString("userID");
        sharedPreferences = getSharedPreferences("com.example.pasta.ahbapapp"
                , Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getString(MainActivity.USER_ID,"");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.messageButton :
                if(!currentUserId.isEmpty()) {
                    db.collection("message").document(currentUserId).collection(messageUserId)
                }
                else {
                    Log.i(TAG,"userid is null from sharedPref");
                }
        }
    }
}
