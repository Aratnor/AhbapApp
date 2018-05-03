package com.example.pasta.ahbapapp.postlist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.chat.message.MessageActivity;

/**
 * Created by pasta on 3.05.2018.
 */

public class PostDialogFragment extends DialogFragment {

    private static final String TAG ="PostDialogFragment" ;
    private String userID;

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
                        Log.d(TAG, "1 item clicked");
                        break;
                    default: break;
                }
            }
        });
        return builder.create();
    }

    public void setUserID(String userID){
        this.userID = userID;
    }
}
