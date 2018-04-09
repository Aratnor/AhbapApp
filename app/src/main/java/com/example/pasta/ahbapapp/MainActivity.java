package com.example.pasta.ahbapapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.example.pasta.ahbapapp.login.LoginActivity;
import com.example.pasta.ahbapapp.model.PostModel;
import com.example.pasta.ahbapapp.util.PostUtil;
import com.example.pasta.ahbapapp.view.HomeFragment;
import com.example.pasta.ahbapapp.view.NewPostActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity{

    @BindView(R.id.mainToolbar)
    Toolbar mToolbar;
    @BindView(R.id.mainBottomNav)
    BottomNavigationViewEx mainBottomNav;
    @BindView(R.id.randomPosts)
    Button randomPosts;
    private GoogleSignInClient mGoogleSignInClient;
    private HomeFragment mHomeFragment;


    @SuppressLint("RestrictedApi")
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        randomPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
                CollectionReference posts = mFirestore.collection("posts");

                for (int i = 0; i < 10; i++) {
                    // Get a random Restaurant POJO
                    PostModel post = PostUtil.getRandom(getApplicationContext());

                    // Add a new document to the restaurants collection
                    posts.add(post);
                }
            }
        });
        if(mAuth.getCurrentUser() != null) {
            //Fragments
            mHomeFragment = new HomeFragment();
            initToolbar();
            initFragment();
            initBottomNav();
            initFloatingActionBtn();
        }
    }

    private void initToolbar() {
        mToolbar.setElevation(14f);
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
                default:
                    return false;
                }
            }
        });

        mainBottomNav.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                mHomeFragment.scrollTop();
        }});
    }

    private void initFloatingActionBtn(){
        FloatingActionButton mFloatingActionButton = findViewById(R.id.addFloatingBtn);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(intent);
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
