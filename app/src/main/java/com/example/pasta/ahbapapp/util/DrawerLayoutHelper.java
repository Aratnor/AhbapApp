package com.example.pasta.ahbapapp.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.pasta.ahbapapp.MainActivity;
import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.account.AccountActivity;
import com.example.pasta.ahbapapp.bookmark.BookMarkActivity;
import com.example.pasta.ahbapapp.login.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class DrawerLayoutHelper {
    private NavigationView navigationView;
    private Context context;
    private String user_id;

    private SharedPreferences sharedPref;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    public DrawerLayoutHelper(NavigationView navigationView,Context context){
        this.navigationView = navigationView;
        this.context = context;

        mAuth = FirebaseAuth.getInstance();

        initializeGoogleClient();

        sharedPref = context.getSharedPreferences("com.example.pasta.ahbapapp"
                , Context.MODE_PRIVATE);

        user_id = sharedPref.getString(MainActivity.USER_ID,"");
    }
    public void setNavHeaderData() {
        TextView nav_header_name;
        TextView nav_header_email;
        CircleImageView nav_header_image;

        View header = navigationView.getHeaderView(0);

        nav_header_name = header.findViewById(R.id.nav_header_name);
        nav_header_email = header.findViewById(R.id.nav_header_email);
        nav_header_image = header.findViewById(R.id.nav_header_image);

        String image_url;
        String name;


        image_url = sharedPref.getString(MainActivity.USER_IMAGE,"");

        if(!image_url.isEmpty())
            Glide.with(context)
                    .load(image_url)
                    .into(nav_header_image);

        name = sharedPref.getString(MainActivity.USER_NAME,"");

        nav_header_name.setText(name);

        nav_header_email.setText(sharedPref.getString(MainActivity.USER_EMAIL,""));
    }
    public void setNavigationViewListener(final DrawerLayout mDrawerLayout) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profileNavView :
                        startAccountActivity();
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.logOutNavView:
                        logOut();
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.bookmarkNavView:
                        startBookMarkActivity();
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.settingsNavView:
                        startSettingActivity();
                        return true;

                    default: return false;
                }
            }
        });
    }
    private void startSettingActivity() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");

//for Android 5-7
        intent.putExtra("app_package", context.getPackageName());
        intent.putExtra("app_uid", context.getApplicationInfo().uid);

// for Android O
        intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());

        context.startActivity(intent);
    }
    private void startAccountActivity() {
        Intent intent = new Intent(context, AccountActivity.class);
        intent.putExtra("user_id", user_id);
        context.startActivity(intent);
    }
    private void logOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut();
        context.startActivity(new Intent(context, LoginActivity.class));
    }
    private void initializeGoogleClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }
    private void startBookMarkActivity() {
        Intent intent = new Intent(context, BookMarkActivity.class);
        context.startActivity(intent);
    }
}
