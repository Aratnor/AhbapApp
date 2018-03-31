package com.example.pasta.ahbapapp.login;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

class LoginPresenter implements LoginContract.Presenter{

    private FirebaseAuth mAuth;
    private LoginContract.View mView;
    private FirebaseFirestore db;

    LoginPresenter(LoginContract.View mView){
        this.mView = mView;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }
    @Override public void logInWithFirebase(GoogleSignInAccount account) {
        Log.d("Error", "logInWithFirebase:" + account.getId());
        mView.showProgress();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener((Activity) mView, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        checkUserExists();

                    } else {

                        Log.w("logInWithFirebase", "signInWithCredential:failure", task.getException());
                        mView.updateUI();
                    }
                }
            });

    }

    @Override public void checkUserExists() {
        if (mAuth.getCurrentUser() != null){
            final String userId = mAuth.getCurrentUser().getUid();

            final DocumentReference userRef = db.collection("users").document(userId);
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            mView.updateUI();
                        }
                        else {
                            Log.d("Document", "null");
                            addUser();
                        }
                    }
                    else {
                        Log.d("checkUserExist", task.getException().toString());
                    }
                }
            });

        }
    }

    @Override public void addUser() {
        final String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", mAuth.getCurrentUser().getDisplayName());
        userInfo.put("email", mAuth.getCurrentUser().getEmail());
        userInfo.put("image_url", mAuth.getCurrentUser().getPhotoUrl().toString());

        db.collection("users").document(userId).set(userInfo)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override public void onSuccess(Void aVoid) {
                    Log.d("addUser", "User added");
                    mView.updateUI();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override public void onFailure(@NonNull Exception e) {
                    Log.d("addUser", "Something went wrong" + e.getMessage());

                }
            });
    }
}
