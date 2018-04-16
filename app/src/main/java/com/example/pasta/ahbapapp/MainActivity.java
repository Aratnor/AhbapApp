package com.example.pasta.ahbapapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Spinner;
import com.example.pasta.ahbapapp.login.LoginActivity;
import com.example.pasta.ahbapapp.model.PostModel;
import com.example.pasta.ahbapapp.notificationlist.NotificationFragment;
import com.example.pasta.ahbapapp.util.PostUtil;
import com.example.pasta.ahbapapp.postlist.HomeFragment;
import com.example.pasta.ahbapapp.newpost.NewPostActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class MainActivity extends AppCompatActivity{

    @BindView(R.id.mainBottomNav)
    BottomNavigationViewEx mainBottomNav;


    private static final String TAG = "MainActivity";
    public static final String USER_ID = "userID";
    public static final String USER_NAME = "userName";
    public static final String USER_IMAGE = "userImage";
    private GoogleSignInClient mGoogleSignInClient;
    private HomeFragment mHomeFragment;
    private NotificationFragment mNotificationFragment;
    private FirebaseAuth mAuth;


    @SuppressLint("RestrictedApi")
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null) {
            setUserDataSharedPref();
            //Fragments
            mHomeFragment = new HomeFragment();
            mNotificationFragment = new NotificationFragment();
            initFragment();
            initBottomNav();
        }
    }

    @Override protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            sendToLogin();
        }
        initializeGoogleClient();
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void initFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_frame_container, mHomeFragment);
        fragmentTransaction.commit();
    }

    private void initBottomNav(){
        mainBottomNav.enableAnimation(false);
        mainBottomNav.enableItemShiftingMode(false);
        mainBottomNav.enableShiftingMode(false);
        mainBottomNav.setTextVisibility(false);
        mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                case R.id.homeNav:
                    replaceFragment(mHomeFragment);
                    return true;
                    case R.id.notificationNav:
                        replaceFragment(mNotificationFragment);
                        return true;
                default:
                    return false;
                }
            }
        });

        mainBottomNav.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.homeNav:
                        mHomeFragment.scrollTop();
                        break;
                    case R.id.notificationNav:
                        mNotificationFragment.scrollTop();
                        break;
                }
        }});

    }

    private void setUserDataSharedPref() {
        Log.d(TAG,"setUserDataSharedPref");
        final String currentUserID = mAuth.getCurrentUser().getUid();
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("users").document(currentUserID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String name = documentSnapshot.getString("name");
                        String imageUrl = documentSnapshot.getString("image_url");

                        SharedPreferences sharedPref = getSharedPreferences("com.example.pasta.ahbapapp"
                                ,Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(USER_ID, currentUserID);
                        editor.putString(USER_NAME, name);
                        editor.putString(USER_IMAGE, imageUrl);
                        editor.apply();
                        Log.d(TAG,"setUserDataSharedPref onSuccess");
                    }
                });
    }

    private void initializeGoogleClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_container,fragment);
        fragmentTransaction.commit();
    }
}
