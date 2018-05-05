package com.example.pasta.ahbapapp.postlist;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by pasta on 3.05.2018.
 */

public class PostDialogFragment extends DialogFragment {

    private static final String TAG ="PostDialogFragment" ;
    private String userID;
    private String postID = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.post_actions, new DialogInterface.OnClickListener() {
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
                        addBookMark();
                        break;
                    default: break;
                }
            }
        });
        return builder.create();
    }

    public void addBookMark() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String,Object> map = new HashMap<>();
        map.put("post_id",postID);
        map.put("bookmarked_date",new Date());
        db.collection("users").document(userID).collection("bookmarks").add(map);
    }

    public void setPostID(String postID) {this.postID = postID;}
    public void setUserID(String userID){
        this.userID = userID;
    }
}
