package com.example.pasta.ahbapapp.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.pasta.ahbapapp.MainActivity;
import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.chat.message.MessageActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by pasta on 3.05.2018.
 */

public class PostDialogFragment extends DialogFragment {

    private static final String TAG = "PostDialogFragment";
    public static final String USER_ID = "userID";
    public static final String POST_ID ="postID";
    public static final String ARRAY_ID = "arrayID" ;

    private String userID;
    private String postID;
    private int arrayID;
    private FirebaseFirestore db;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getArguments() != null){
            userID = getArguments().getString(USER_ID);
            postID = getArguments().getString(POST_ID);
            arrayID = getArguments().getInt(ARRAY_ID);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(arrayID, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Intent intent = new Intent(getActivity(), MessageActivity.class);
                        intent.putExtra("userID" ,userID );
                        Log.d(TAG , "case 0 " + userID);
                        startActivity(intent);
                        break;
                    case 1:
                        bookMarkAction();
                        break;
                    default: break;
                }
            }
        });
        return builder.create();
    }

    public void bookMarkAction() {
        final String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(currentUserID)
                .collection("bookmarks").whereEqualTo("post_id", postID)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()){
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("post_id",postID);
                    map.put("bookmarked_date",new Date());
                    db.collection("users").document(currentUserID).collection("bookmarks").add(map);
                }
                else {
                    queryDocumentSnapshots.getDocuments().get(0).getReference().delete();
                }
            }
        });
    }
}
