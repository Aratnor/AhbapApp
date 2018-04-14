package com.example.pasta.ahbapapp.comment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewCommentFragment extends Fragment {

    @BindView(R.id.new_comment_edit_text)
    EditText commentEditText;

    private CommentModel comment = new CommentModel();
    private String postId;

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
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = mFirestore.collection("posts").document(postId)
                .collection("comments");

        collectionRef.add(comment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                afterSendAction();
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
}
