package com.example.pasta.ahbapapp.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.pasta.ahbapapp.MainActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static String TAG = "Token ID";
    FirebaseFirestore db;
    SharedPreferences sharedPref;
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sharedPref = getSharedPreferences("com.example.pasta.ahbapapp"
                , Context.MODE_PRIVATE);

        if (refreshedToken!=null) {
            sharedPref.edit().putString(TAG,refreshedToken).apply();
        }
        // TODO: Implement this method to send any registration to your app's servers.
        System.out.println("Registration.onTokenRefresh TOKEN: " + refreshedToken );
        try {
            sendRegistrationToServer(refreshedToken);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void sendRegistrationToServer(String refreshedToken) throws InterruptedException {
        sharedPref.edit().putString("token_id",refreshedToken).apply();
    }
}
