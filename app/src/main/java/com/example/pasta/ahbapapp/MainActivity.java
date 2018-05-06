package com.example.pasta.ahbapapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.pasta.ahbapapp.account.AccountActivity;
import com.example.pasta.ahbapapp.chat.ChatListFragment;
import com.example.pasta.ahbapapp.login.LoginActivity;
import com.example.pasta.ahbapapp.model.PostModel;
import com.example.pasta.ahbapapp.newpost.NewPostActivity;
import com.example.pasta.ahbapapp.notificationlist.NotificationFragment;
import com.example.pasta.ahbapapp.postlist.HomeFragment;
import com.example.pasta.ahbapapp.util.DrawerLayoutHelper;
import com.example.pasta.ahbapapp.util.PostUtil;
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
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity{

    @BindView(R.id.mainBottomNav)
    BottomNavigationViewEx mainBottomNav;
    @BindView(R.id.spinnerCity)
    Spinner spinnerCity;
    @BindView(R.id.spinnerCat)
    Spinner spinnerCat;
    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.addFloatingBtn)
    FloatingActionButton fab;
    @BindView(R.id.randomPosts)
    Button randomBtn;

    private static final String TAG = "MainActivity";
    public static final String USER_ID = "userID";
    public static final String USER_NAME = "name";
    public static final String USER_IMAGE = "image_url";
    public static final String USER_EMAIL = "email";
    private HomeFragment mHomeFragment;
    private NotificationFragment mNotificationFragment;
    private ChatListFragment mChatListFragment;

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
            mChatListFragment = new ChatListFragment();
            initFragment();
            initBottomNav();
            initToolBar();
        }
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


    @Override protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            sendToLogin();
        }
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

    private void initToolBar() {
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
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
                        setVisibility(true);
                        replaceFragment(mHomeFragment);
                        return true;
                    case R.id.notificationNav:
                        setVisibility(false);
                        replaceFragment(mNotificationFragment);
                        return true;
                    case R.id.messageNav:
                        setVisibility(false);
                        replaceFragment(mChatListFragment);
                        return true;
                    default:return false;
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
                    case R.id.messageNav:
                        mChatListFragment.scrollTop();
                        break;
                    default:break;
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

                        String name = documentSnapshot.getString(USER_NAME);
                        String imageUrl = documentSnapshot.getString(USER_IMAGE);
                        String email = documentSnapshot.getString(USER_EMAIL);

                        SharedPreferences sharedPref = getSharedPreferences("com.example.pasta.ahbapapp"
                                ,Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(USER_ID, currentUserID);
                        editor.putString(USER_NAME, name);
                        editor.putString(USER_IMAGE, imageUrl);
                        editor.putString(USER_EMAIL,email);
                        editor.apply();
                        Log.d(TAG,"setUserDataSharedPref onSuccess");
                        initDrawer();
                    }
                });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_container,fragment);
        fragmentTransaction.commit();
    }

    private void setVisibility(boolean setVisible){
        if (setVisible){
            fab.setVisibility(View.VISIBLE);
            randomBtn.setVisibility(View.VISIBLE);
            spinnerCat.setVisibility(View.VISIBLE);
            spinnerCity.setVisibility(View.VISIBLE);
        }
        else {
            fab.setVisibility(View.GONE);
            randomBtn.setVisibility(View.GONE);
            spinnerCat.setVisibility(View.GONE);
            spinnerCity.setVisibility(View.GONE);
        }
    }

    @OnItemSelected(R.id.spinnerCity)
    public void spinnerCitySelected(){
        Log.d(TAG, "spinnerCitySelected" + spinnerCity.getSelectedItem().toString()
                + spinnerCat.getSelectedItem().toString());
        mHomeFragment.setQuery(spinnerCat.getSelectedItem().toString(),spinnerCity.getSelectedItem().toString());
    }

    @OnItemSelected(R.id.spinnerCat)
    public void spinnerCatSelected(){
        Log.d(TAG, "spinnerCatSelected" + spinnerCat.getSelectedItem().toString()
                + spinnerCity.getSelectedItem().toString());
        mHomeFragment.setQuery(spinnerCat.getSelectedItem().toString(),spinnerCity.getSelectedItem().toString());
    }

    @OnClick(R.id.addFloatingBtn)
    public void sendNewPostActivity(){
        Intent intent = new Intent(MainActivity.this, NewPostActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.randomPosts)
    public void setRandomPosts(){

        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        CollectionReference posts = mFirestore.collection("posts");

        for (int i = 0; i < 10; i++) {
            // Get a random Restaurant POJO
            PostModel post = PostUtil.getRandom(getApplicationContext());

            // Add a new document to the restaurants collection
            posts.add(post);
        }
    }


    private void initDrawer(){
        DrawerLayoutHelper drawerLayoutHelper = new DrawerLayoutHelper(navigationView,MainActivity.this);
        drawerLayoutHelper.setNavHeaderData();
        drawerLayoutHelper.setNavigationViewListener(mDrawerLayout);
    }
}
