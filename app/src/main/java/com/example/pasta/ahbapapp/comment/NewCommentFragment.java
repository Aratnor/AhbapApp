package com.example.pasta.ahbapapp.comment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.pasta.ahbapapp.MainActivity;
import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.model.CommentModel;
import com.example.pasta.ahbapapp.postdetail.PostDetailActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewCommentFragment extends Fragment {

    private static final String TAG = "NewCommentFragment";
    @BindView(R.id.new_comment_edit_text)
    EditText commentEditText;

    private CommentModel comment = new CommentModel();
    private FirebaseFirestore mFirestore;
    private String postId;
    private String currentID;
    private String message;

    public NewCommentFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_comment, container, false);
        ButterKnife.bind(this,view);
        mFirestore = FirebaseFirestore.getInstance();
        getPostId();
        return view;
    }

    private void getPostId() {
        if (getArguments() == null){
            throw new IllegalArgumentException("Must pass extra " + PostDetailActivity.POST_ID);
        }
        postId = getArguments().getString(PostDetailActivity.POST_ID);
    }

    @OnClick(R.id.send_comment_btn)
    public void sendBtnClicked(){

        String commentBody = commentEditText.getText().toString().trim();
        getUserData();

        if (comment.getAuthor_id() != null || comment.getAuthor_id().equals("")){
            comment.setComment_body(commentBody);
            addCommentFirestore();
        }
        else {
            Toast.makeText(getActivity(), "Beklenmeyen bir problem oluştu."
                    , Toast.LENGTH_SHORT).show();
        }
    }

    public void getUserData(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        SharedPreferences sharedPref = getActivity()
                .getSharedPreferences("com.example.pasta.ahbapapp", Context.MODE_PRIVATE);
        String userID = sharedPref.getString(MainActivity.USER_ID, "");
        String userName = sharedPref.getString(MainActivity.USER_NAME, "");
        String userImage = sharedPref.getString(MainActivity.USER_IMAGE, "");

        if (mAuth.getCurrentUser().getUid().equals(userID)){
            comment.setAuthor_id(userID);
            comment.setAuthor_name(userName);
            comment.setAuthor_image_url(userImage);
            comment.setCreated_at(new Date());
        }
    }


    private void addCommentFirestore() {
        CollectionReference collectionRef = mFirestore.collection("posts").document(postId)
                .collection("comments");

        collectionRef.add(comment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                afterSendAction();
                sendNotification();
                updateCommentCount();
            }
        });
    }

    private void sendNotification() {
        final Map<String,Object> notification = new HashMap<>();
        SharedPreferences sharedPref = getActivity()
                .getSharedPreferences("com.example.pasta.ahbapapp", Context.MODE_PRIVATE);
        currentID = sharedPref.getString(MainActivity.USER_ID, "");
        message = sharedPref.getString(MainActivity.USER_NAME, "") + " içeriğine yorum ekledi.";
        notification.put("message", message);
        notification.put("post_id", postId);
        notification.put("created_at",new Date());
        notification.put("from",currentID);
        final HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("currentID",currentID);
        mFirestore.collection("posts").document(postId).collection("user_ids").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()) {
                    mFirestore.collection("posts").document(postId).collection("user_ids").add(hashMap);
                }
                else {
                    boolean isAccepted = false;
                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                        if(currentID.equals(documentSnapshot.get("currentID"))){
                            isAccepted = true;
                        }
                        else {

                            mFirestore.collection("users")
                                    .document((String) documentSnapshot.get("currentID"))
                                    .collection("Notifications").add(notification);
                        }
                    }
                    if (!isAccepted) {
                        mFirestore.collection("posts").document(postId).collection("user_ids").add(hashMap);
                    }
                }
            }
        });
    }

    private void afterSendAction() {
        Toast.makeText(getActivity(), "Yorum gönderildi.", Toast.LENGTH_SHORT).show();
        commentEditText.setText("");
        InputMethodManager inputManager =
                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(commentEditText.getWindowToken(),0);
    }

    private void updateCommentCount() {
        final DocumentReference postRef = mFirestore.collection("posts").document(postId);
        postRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                Long commentCount = (Long) snapshot.get("comment_count");
                postRef.update("comment_count", ++commentCount).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "updateComment successful");
                    }
                });
            }
        });

    }
}
