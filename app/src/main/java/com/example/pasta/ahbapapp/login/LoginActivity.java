package com.example.pasta.ahbapapp.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.pasta.ahbapapp.MainActivity;
import com.example.pasta.ahbapapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("RestrictedApi")
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SignInButton googleSignInBtn = findViewById(R.id.google_sign_in_btn);
        googleSignInBtn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

                Log.w("Failed Sign In", "signInResult:failed code=" + e.getStatusCode());
                updateUI(null);
            }

        }

    }

    private void googleSignIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Error", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        checkUserExists();

                    } else {

                        Log.w("Error", "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                }
            });
    }

    private void updateUI(FirebaseUser account) {

        if(account != null){

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void checkUserExists(){

        if (mAuth.getCurrentUser() != null){
            final String userId = mAuth.getCurrentUser().getUid();

            final DocumentReference userRef = db.collection("users").document(userId);
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            updateUI(mAuth.getCurrentUser());
                        }
                        else {
                            Log.d("Document", "null");
                            addUser();
                            updateUI(mAuth.getCurrentUser());
                        }
                    }
                    else {
                        Log.d("checkUserExist", task.getException().toString());
                    }
                }
            });

        }
    }

    private void addUser() {
        final String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", mAuth.getCurrentUser().getDisplayName());
        userInfo.put("email", mAuth.getCurrentUser().getEmail());
        userInfo.put("image_url", mAuth.getCurrentUser().getPhotoUrl().toString());

        db.collection("users").document(userId).set(userInfo)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override public void onSuccess(Void aVoid) {
                Log.d("addUser", "User added");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception e) {
                Log.d("addUser", "Something went wrong" + e.getMessage());

            }
        });
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_btn:
                googleSignIn();
                break;
        }
    }

}
