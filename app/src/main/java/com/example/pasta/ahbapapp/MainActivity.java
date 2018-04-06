package com.example.pasta.ahbapapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
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
import com.google.firebase.storage.FirebaseStorage;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity{

    @BindView(R.id.randomPosts)
    Button randomPosts;
    private GoogleSignInClient mGoogleSignInClient;
    private DrawerLayout mDrawerLayout;
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
            mDrawerLayout = findViewById(R.id.drawer_layout);
            //Fragments
            mHomeFragment = new HomeFragment();
            initializeFragment();
            initializeBottomNav();
            initializeFloatingActionBtn();
            initializeToolbar();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void initializeFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_frame_container, mHomeFragment);
        fragmentTransaction.commit();
    }

    private void initializeBottomNav(){
        BottomNavigationViewEx mainBottomNav = findViewById(R.id.mainBottomNav);
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
    private void initializeFloatingActionBtn(){
        FloatingActionButton mFloatingActionButton = findViewById(R.id.addFloatingBtn);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initializeToolbar(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // set item as selected to persist highlight
                menuItem.setChecked(true);
                // close drawer when item is tapped
                mDrawerLayout.closeDrawers();

                // Add code here to update the UI based on the item selected
                // For example, swap UI fragments here

                return true;
            }
        });

        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
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
